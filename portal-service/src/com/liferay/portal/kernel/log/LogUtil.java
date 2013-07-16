/**
 * Copyright (c) 2000-2013 Liferay, Inc. All rights reserved.
 *
 * This library is free software; you can redistribute it and/or modify it under
 * the terms of the GNU Lesser General Public License as published by the Free
 * Software Foundation; either version 2.1 of the License, or (at your option)
 * any later version.
 *
 * This library is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more
 * details.
 */

package com.liferay.portal.kernel.log;

import com.liferay.portal.kernel.io.unsync.UnsyncStringWriter;
import com.liferay.portal.kernel.sanitizer.SanitizerUtil;
import com.liferay.portal.kernel.util.CharPool;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.Html;
import com.liferay.portal.kernel.util.HtmlUtil;
import com.liferay.portal.kernel.util.KMPSearch;
import com.liferay.portal.kernel.util.Props;
import com.liferay.portal.kernel.util.PropsKeys;
import com.liferay.portal.kernel.util.PropsUtil;
import com.liferay.portal.kernel.util.StackTraceUtil;
import com.liferay.portal.kernel.util.StringPool;
import com.liferay.portal.kernel.util.UnsyncPrintWriterPool;
import com.liferay.portal.kernel.util.Validator;

import java.nio.CharBuffer;

import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import javax.servlet.ServletException;
import javax.servlet.jsp.JspException;

/**
 * @author Brian Wing Shun Chan
 * @author László Csontos
 */
public class LogUtil {

	public static final boolean REMOVE_UNKNOWN_SOURCE = true;

	public static final int STACK_TRACE_LENGTH = 20;

	public static void debug(Log log, Properties props) {
		if (log.isDebugEnabled()) {
			UnsyncStringWriter unsyncStringWriter = new UnsyncStringWriter(
				props.size() + 1);

			props.list(UnsyncPrintWriterPool.borrow(unsyncStringWriter));

			log.debug(unsyncStringWriter.toString());
		}
	}

	public static void log(Log log, JspException jspe) {
		Throwable cause = jspe.getCause();

		if (cause == null) {
			cause = jspe;
		}

		if ((cause != jspe) && (cause instanceof JspException)) {
			log(log, (JspException)cause);
		}
		else if (cause instanceof ServletException) {
			log(log, (ServletException)cause);
		}
		else {
			_log(log, cause);
		}
	}

	public static void log(Log log, ServletException se) {
		Throwable cause = se.getRootCause();

		if (cause == null) {
			cause = se;
		}

		if (cause instanceof JspException) {
			log(log, (JspException)cause);
		}
		else if ((cause != se) && (cause instanceof ServletException)) {
			log(log, (ServletException)cause);
		}
		else {
			_log(log, cause);
		}
	}

	public static void log(Log log, Throwable t) {
		if (t instanceof JspException) {
			log(log, (JspException)t);
		}
		else if (t instanceof ServletException) {
			log(log, (ServletException)t);
		}
		else {
			Throwable cause = t.getCause();

			if (cause != null) {
				log(log, cause);
			}
			else {
				_log(log, t);
			}
		}
	}

	public static String sanitize(Object msg) {
		boolean sanitizeCrlf = _isSanitizeCrlfEnabled();
		boolean sanitizeHtml = _isSanitizeHtmlEnabled();

		return _sanitize(msg, sanitizeCrlf, sanitizeHtml);
	}

	private static boolean _isSanitizeCrlfEnabled() {
		Props props = PropsUtil.getProps();

		if (props != null) {
			return GetterUtil.getBoolean(
				props.get(PropsKeys.SECURE_LOGGING_SANITIZE_CRLF_ENABLED));
		}

		return _DEFAULT_SANITIZE_CRLF_ENABLED;
	}

	private static boolean _isSanitizeHtmlEnabled() {
		Props props = PropsUtil.getProps();

		if (props != null) {
			return GetterUtil.getBoolean(
				props.get(PropsKeys.SECURE_LOGGING_SANITIZE_HTML_ENABLED));
		}

		return _DEFAULT_SANITIZE_HTML_ENABLED;
	}

	private static void _log(Log log, Throwable cause) {
		StackTraceElement[] steArray = cause.getStackTrace();

		// Make the stack trace more readable by limiting the number of
		// elements.

		if (steArray.length <= STACK_TRACE_LENGTH) {
			log.error(StackTraceUtil.getStackTrace(cause, true));

			return;
		}

		int count = 0;

		List<StackTraceElement> steList = new ArrayList<StackTraceElement>();

		for (int i = 0; i < steArray.length; i++) {
			StackTraceElement ste = steArray[i];

			// Make the stack trace more readable by removing elements that
			// refer to classes with no packages, or starts with a $, or are
			// Spring classes, or are standard reflection classes.

			String className = ste.getClassName();

			boolean addElement = true;

			if (REMOVE_UNKNOWN_SOURCE && (ste.getLineNumber() < 0)) {
				addElement = false;
			}

			if (className.startsWith("$") ||
				className.startsWith("java.lang.reflect.") ||
				className.startsWith("org.springframework.") ||
				className.startsWith("sun.reflect.")) {

				addElement = false;
			}

			if (addElement) {
				steList.add(ste);

				count++;
			}

			if (count >= STACK_TRACE_LENGTH) {
				break;
			}
		}

		steArray = steList.toArray(new StackTraceElement[steList.size()]);

		cause.setStackTrace(steArray);

		log.error(StackTraceUtil.getStackTrace(cause, true));
	}

	private static String _sanitize(
		Object msg, boolean sanitizeCrlf, boolean sanitizeHtml) {

		if (msg == null) {
			return StringPool.BLANK;
		}

		String originalMessage = msg.toString();

		if (Validator.isBlank(originalMessage)) {
			return originalMessage;
		}

		String sanitizedMessage = originalMessage;

		if (sanitizeCrlf) {
			sanitizedMessage = _sanitizeCrlf(sanitizedMessage);
		}

		if (sanitizeHtml) {
			Html html = HtmlUtil.getHtml();

			if (html != null) {
				sanitizedMessage = html.escape(sanitizedMessage);
			}
		}

		if (!sanitizedMessage.equals(originalMessage)) {
			sanitizedMessage = sanitizedMessage.concat(" [Encoded]");
		}

		return sanitizedMessage;
	}

	private static String _sanitizeCrlf(String msg) {
		CharBuffer charBuffer = CharBuffer.wrap(msg);

		UnsyncStringWriter unsyncStringWriter = new UnsyncStringWriter(
			charBuffer.capacity());

		while (charBuffer.hasRemaining()) {
			int eolPos = KMPSearch.search(
				charBuffer, 0, StringPool.OS_EOL, _OS_EOL_NEXTS);

			boolean steFound = false;

			if (eolPos == -1) {
				eolPos = charBuffer.limit() - charBuffer.position();
			}
			else {
				for (int i = 0; i < _STACKTRACE_ELEMENT_TOKENS.length; i++) {
					int[] nexts = _STACKTRACE_ELEMENT_TOKENS_NEXTS[i];
					String token = _STACKTRACE_ELEMENT_TOKENS[i];

					int stePos = KMPSearch.search(
						charBuffer, eolPos, token, nexts);

					if (eolPos == (stePos - _OS_EOL_LENGTH)) {
						steFound = true;

						break;
					}
				}
			}

			SanitizerUtil.sanitizeCrlf(
				charBuffer, unsyncStringWriter, eolPos, CharPool.UNDERLINE);

			int nextPos = charBuffer.position() + eolPos + _OS_EOL_LENGTH;

			if (nextPos > charBuffer.limit()) {
				nextPos = charBuffer.limit();
			}

			if (nextPos < charBuffer.limit()) {
				String sanitizedEol = StringPool.UNDERLINE;

				if (steFound) {
					sanitizedEol = StringPool.OS_EOL;
				}

				unsyncStringWriter.write(sanitizedEol);
			}

			charBuffer.position(nextPos);
		}

		return unsyncStringWriter.toString();
	}

	private static final boolean _DEFAULT_SANITIZE_CRLF_ENABLED = true;

	private static final boolean _DEFAULT_SANITIZE_HTML_ENABLED = false;

	private static final int _OS_EOL_LENGTH = StringPool.OS_EOL.length();

	private static final int[] _OS_EOL_NEXTS = KMPSearch.generateNexts(
		StringPool.OS_EOL);

	private static final String[] _STACKTRACE_ELEMENT_TOKENS = {
		"\tat ", "Caused by: ", "\t... "
	};

	private static final int[][] _STACKTRACE_ELEMENT_TOKENS_NEXTS = {
		KMPSearch.generateNexts(_STACKTRACE_ELEMENT_TOKENS[0]),
		KMPSearch.generateNexts(_STACKTRACE_ELEMENT_TOKENS[1]),
		KMPSearch.generateNexts(_STACKTRACE_ELEMENT_TOKENS[2])
	};

}