/**
 * Copyright (c) 2000-present Liferay, Inc. All rights reserved.
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

package com.liferay.portal.kernel.security.xss;

import com.liferay.portal.kernel.language.LanguageUtil;
import com.liferay.registry.RegistryUtil;

import java.util.ResourceBundle;

/**
 * @author Tomas Polesovsky
 */
public class XSS {

	public static SafeHTML safeHTMLOnly(SafeHTML html) {
		return html;
	}

	public static VerifiedJS verifiedJSOnly(VerifiedJS verifiedJS) {
		return verifiedJS;
	}

	public static SafeHTML safeHTML(String input) {
		return getXSSEscaper().safeHTML(input);
	}

	public static VerifiedJS verifiedJS(String input) {
		return getXSSEscaper().verifiedJS(input);
	}

	public static EscapedString htmlBody(CharSequence input) {
		return getXSSEscaper().htmlBody(input);
	}

	public static EscapedString attribute(CharSequence input) {
		return getXSSEscaper().attribute(input);
	}

	public static EscapedString css(CharSequence input) {
		return getXSSEscaper().css(input);
	}

	public static EscapedString href(CharSequence input) {
		return getXSSEscaper().href(input);
	}

	public static EscapedString src(CharSequence input) {
		return getXSSEscaper().src(input);
	}

	public static EscapedString js(CharSequence input) {
		return getXSSEscaper().js(input);
	}


	public static CharSequence get(ResourceBundle resourceBundle, String key) {
		String value = LanguageUtil.get(resourceBundle, key, null);

		if (value != null) {
			return XSS.safeHTML(value);
		}

		return key;
	}


	/**
	 * String that is escaped using one of {@link XSS} static methods and is
	 * safe to be used in that particular context (html body, attribute, etc.).
	 * <br />
	 * <br />
	 * Chain of escape operations can be {@link #reset()} and added using
	 * {@link #chain(EscapeOperation...)} method.<br />
	 * <br />
	 * Escaped output is printed using {@link #toString()} method.
	 */
	public interface EscapedString extends CharSequence {
		/**
		 * Add {@code escapeOperation} to the chain of transformations to be
		 * applied on the content.
		 *
		 * @param escapeOperation to be used as the last one
		 * @return this object
		 */
		EscapedString chain(EscapeOperation... escapeOperation);

		/**
		 * Joins this escaped string with a new String that will be escaped
		 * using the same escape operations.
		 *
		 * @param input to be escaped
		 * @return this object
		 */
		EscapedString concat(String input);

		/**
		 * Joins this escaped string with another EscapedString instance that
		 * will be escaped using the same escape operations.
		 *
		 * @param escapedString to be escaped
		 * @return this object
		 */
		EscapedString concat(EscapedString escapedString);

		/**
		 * Resets chain of escape operations.
		 *
		 * @return this object
		 */
		EscapedString reset();

		/**
		 * Returns escaped representation of String.<br />
		 * <br />
		 * When there is no EscapeOperation set, it must log an exception and
		 * return empty String.
		 *
		 * @return content escaped using all chained {@link EscapeOperation}s.
		 */
		@Override
		String toString();

		/**
		 * Returns original content
		 *
		 * @return original input wrapped by this class
		 */
		String original();
	}

	/**
	 * Contains JavaScript that does not originate from user input or is safe to
	 * execute. The content should be still escaped for appropriate context.
	 * <br />
	 * <br />
	 * Please note the output of {@link XSS#js(CharSequence)} is generally not
	 * safe to execute, it is only safe to be printed into HTML page. <br />
	 *<br />
	 * Following example illustrates the difference between VerifiedJavaScript
	 * and {@link EscapedString} string. The {@code input} variable must contain only
	 * JavaScript that is safe to execute to avoid XSS. Therefore we require
	 * {@link VerifiedJS} type:<br />
	 * {@code <a onclick=<%= XSS.attribute(XSS.verifiedJSOnly(input)) %>}
	 */
	public interface VerifiedJS extends EscapedString {
		static VerifiedJS instance(String input) {
			return XSS.verifiedJS(input);
		}
	}

	/**
	 * Contains content to be used directly in HTML output without any
	 * escape operation. Developer must make sure the content of this class is
	 * safe to be printed into HTML output.
	 * <br />
	 * When used in escape operation it stays immune to the operation.
	 */
	public interface SafeHTML extends EscapedString {
		static SafeHTML instance(String input) {
			return XSS.safeHTML(input);
		}
	}

	protected static XSSEscaper getXSSEscaper(){
		XSSEscaper escaper = RegistryUtil.getRegistry().getService(
			XSSEscaper.class);

		if (escaper == null) {
			throw new RuntimeException(
				"Unable to find any XSSEscaper implementation! " +
					"Portal is vulnerable to XSS!");
		}

		return escaper;
	}
}