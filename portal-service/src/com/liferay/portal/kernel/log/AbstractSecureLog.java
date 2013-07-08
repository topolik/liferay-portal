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

import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.Html;
import com.liferay.portal.kernel.util.HtmlUtil;
import com.liferay.portal.kernel.util.Props;
import com.liferay.portal.kernel.util.PropsKeys;
import com.liferay.portal.kernel.util.PropsUtil;
import com.liferay.portal.kernel.util.StackTraceUtil;
import com.liferay.portal.kernel.util.StringBundler;
import com.liferay.portal.kernel.util.StringPool;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.kernel.util.Validator;

/**
 * @author László Csontos
 */
public abstract class AbstractSecureLog<L> implements Log {

	@Override
	public void debug(Object msg) {
		log(getDebugLevel(), msg);
	}

	@Override
	public void debug(Object msg, Throwable t) {
		log(getDebugLevel(), msg, t);
	}

	@Override
	public void debug(Throwable t) {
		log(getDebugLevel(), t.getMessage(), t);
	}

	@Override
	public void error(Object msg) {
		log(getErrorLevel(), msg);
	}

	@Override
	public void error(Object msg, Throwable t) {
		log(getErrorLevel(), msg, t);
	}

	@Override
	public void error(Throwable t) {
		log(getErrorLevel(), t.getMessage(), t);
	}

	@Override
	public void fatal(Object msg) {
		log(getFatalLevel(), msg);
	}

	@Override
	public void fatal(Object msg, Throwable t) {
		log(getFatalLevel(), msg, t);
	}

	@Override
	public void fatal(Throwable t) {
		log(getFatalLevel(), t.getMessage(), t);
	}

	@Override
	public void info(Object msg) {
		log(getInfoLevel(), msg);
	}

	@Override
	public void info(Object msg, Throwable t) {
		log(getInfoLevel(), msg, t);
	}

	@Override
	public void info(Throwable t) {
		log(getInfoLevel(), t.getMessage(), t);
	}

	@Override
	public boolean isDebugEnabled() {
		return isLevelEnabled(getDebugLevel());
	}

	@Override
	public boolean isErrorEnabled() {
		return isLevelEnabled(getErrorLevel());
	}

	@Override
	public boolean isFatalEnabled() {
		return isLevelEnabled(getFatalLevel());
	}

	@Override
	public boolean isInfoEnabled() {
		return isLevelEnabled(getInfoLevel());
	}

	@Override
	public boolean isTraceEnabled() {
		return isLevelEnabled(getTraceLevel());
	}

	@Override
	public boolean isWarnEnabled() {
		return isLevelEnabled(getWarnLevel());
	}

	@Override
	public void trace(Object msg) {
		log(getTraceLevel(), msg);
	}

	@Override
	public void trace(Object msg, Throwable t) {
		log(getTraceLevel(), msg, t);
	}

	@Override
	public void trace(Throwable t) {
		log(getTraceLevel(), t.getMessage(), t);
	}

	@Override
	public void warn(Object msg) {
		log(getWarnLevel(), msg);
	}

	@Override
	public void warn(Object msg, Throwable t) {
		log(getWarnLevel(), msg, t);
	}

	@Override
	public void warn(Throwable t) {
		log(getWarnLevel(), t.getMessage(), t);
	}

	protected abstract void doLog(L level, String msg);

	protected abstract L getDebugLevel();

	protected abstract L getErrorLevel();

	protected abstract L getFatalLevel();

	protected abstract L getInfoLevel();

	protected abstract L getTraceLevel();

	protected abstract L getWarnLevel();

	protected abstract boolean isLevelEnabled(L level);

	protected void log(L level, Object msg) {
		log(level, msg, null);
	}

	protected void log(L level, Object msg, Throwable t) {
		String message = _sanitize(msg);

		if (t == null) {
			doLog(level, message);

			return;
		}

		if (Validator.isBlank(message)) {
			message = _sanitize(t.getMessage());
		}

		if (Validator.isBlank(message)) {
			message = t.getClass().getName();
		}

		StringBundler sb = new StringBundler(3);

		sb.append(message);
		sb.append(StringPool.OS_EOL);

		String stackTrace = _sanitize(
			StackTraceUtil.getStackTrace(t), false, _isSanitizeHtmlEnabled());

		sb.append(stackTrace);

		doLog(level, sb.toString());
	}

	protected void log(L level, Throwable t) {
		log(level, null, t);
	}

	private boolean _isSanitizeCrlfEnabled() {
		Props props = PropsUtil.getProps();

		if (props != null) {
			return GetterUtil.getBoolean(
				props.get(PropsKeys.SECURE_LOGGING_SANITIZE_CRLF_ENABLED));
		}

		return _DEFAULT_SANITIZE_CRLF_ENABLED;
	}

	private boolean _isSanitizeHtmlEnabled() {
		Props props = PropsUtil.getProps();

		if (props != null) {
			return GetterUtil.getBoolean(
				props.get(PropsKeys.SECURE_LOGGING_SANITIZE_HTML_ENABLED));
		}

		return _DEFAULT_SANITIZE_HTML_ENABLED;
	}

	private String _sanitize(Object msg) {
		boolean sanitizeCrlf = _isSanitizeCrlfEnabled();
		boolean sanitizeHtml = _isSanitizeHtmlEnabled();

		return _sanitize(msg, sanitizeCrlf, sanitizeHtml);
	}

	private String _sanitize(
		Object msg, boolean sanitizeCrlf, boolean sanitizeHtml) {

		String originalMessage = GetterUtil.getString(msg, StringPool.BLANK);

		if (Validator.isBlank(originalMessage)) {
			return originalMessage;
		}

		String sanitizedMessage = originalMessage;

		if (sanitizeCrlf) {
			sanitizedMessage = StringUtil.replace(
				sanitizedMessage, _CRLF, _UNDERLINES);
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

	private static final String[] _CRLF = new String[] {
		StringPool.NEW_LINE, StringPool.RETURN
	};

	private static final String[] _UNDERLINES = new String[] {
		StringPool.UNDERLINE, StringPool.UNDERLINE
	};

	private static boolean _DEFAULT_SANITIZE_CRLF_ENABLED = true;

	private static boolean _DEFAULT_SANITIZE_HTML_ENABLED = false;

}