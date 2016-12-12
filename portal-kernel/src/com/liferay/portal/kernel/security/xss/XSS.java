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

import java.util.function.Function;

/**
 * @author Tomas Polesovsky
 */
public class XSS {

	public static SafeHtml safeHtmlOnly(SafeHtml safeHtml) {
		return safeHtml;
	}

	public static VerifiedJavaScript verifiedJavaScriptOnly(
			VerifiedJavaScript verifiedJavaScript) {

		return verifiedJavaScript;
	}
	public static SafeHtml safeHtml(CharSequence input) {
		return _createOrApply(input, EscapeOperations.HTML);
	}

	public static SafeHtml htmlBody(CharSequence input) {
		return _createOrApply(input, EscapeOperations.HTML);
	}

	public static SafeHtml attribute(CharSequence input) {
		return _createOrApply(input, EscapeOperations.ATTRIBUTE);
	}

	public static SafeHtml css(CharSequence input) {
		return _createOrApply(input, EscapeOperations.CSS);
	}

	public static SafeHtml href(CharSequence input) {
		return _createOrApply(input, EscapeOperations.HREF);
	}

	public static SafeHtml src(CharSequence input) {
		return _createOrApply(input, EscapeOperations.URL);
	}

	public static VerifiedJavaScript js(CharSequence input) {
		return _createOrApply(input, EscapeOperations.JS);
	}

	protected static EscapedString getEscapedString(CharSequence input) {
		if (input instanceof EscapedString) {
			return (EscapedString) input;
		}
		else {
			return new EscapedStringImpl(input.toString());
		}
	}

	public interface EscapedString extends CharSequence {

		public EscapedString map(Function<String, String> f);
		public EscapedString apply(EscapeOperation ... escapeOperation);

	}

	public interface SafeHtml extends EscapedString {}

	public interface VerifiedJavaScript extends EscapedString {}
	public interface TranslationContent extends EscapedString {}

	private static EscapedStringImpl _createOrApply(
		CharSequence cs, EscapeOperation ... escapeOperation) {

		if (cs instanceof EscapedStringImpl) {
			EscapedStringImpl escapedString = (EscapedStringImpl) cs;

			return escapedString.apply(escapeOperation);
		}

		return new EscapedStringImpl(cs.toString()).apply(escapeOperation);
	}

}
