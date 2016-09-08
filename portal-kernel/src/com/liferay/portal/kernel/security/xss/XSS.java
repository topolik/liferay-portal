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

import com.liferay.portal.kernel.util.HtmlUtil;

/**
 * @author Tomas Polesovsky
 */
public class XSS {

	/**
	 * Escapes the text so that it is safe to use in an HTML context.
	 *
	 * @param  text the text to escape
	 * @return the escaped HTML text, or <code>null</code> if the text is
	 *         <code>null</code>
	 */
	public static String htmlBody(CharSequence text) {
		HtmlBody htmlBody = null;

		if (text instanceof XSS.HtmlBody) {
			htmlBody = (HtmlBody) text;
		}
		else {
			htmlBody = (HtmlBody) HtmlUtil.escape(text);
		}

		return htmlBody.escape();
	}

	/**
	 * Escapes the attribute value so that it is safe to use as an attribute
	 * value.
	 *
	 * @param  text the attribute to escape
	 * @return the escaped attribute value, or <code>null</code> if the
	 *         attribute value is <code>null</code>
	 */
	public static String attribute(CharSequence text) {
		HtmlAttribute htmlAttribute = null;

		if (text instanceof HtmlAttribute) {
			htmlAttribute = (HtmlAttribute) text;
		}
		else {
			htmlAttribute = (HtmlAttribute) HtmlUtil.escapeAttribute(text);
		}

		return htmlAttribute.escape();
	}

	/**
	 * Escapes the CSS value so that it is safe to use in a CSS context.
	 *
	 * @param  css the CSS value to escape
	 * @return the escaped CSS value, or <code>null</code> if the CSS value is
	 *         <code>null</code>
	 */
	public static CSS css(CharSequence css) {
		return new CSS(css);
	}

	/**
	 * Escapes the HREF attribute so that it is safe to use as an HREF
	 * attribute.
	 *
	 * @param  href the HREF attribute to escape
	 * @return the escaped HREF attribute, or <code>null</code> if the HREF
	 *         attribute is <code>null</code>
	 */
	public static HREF href(CharSequence href) {
		return new HREF(href);
	}

	/**
	 * Escapes the SRC attribute so that it is safe to use as an SRC
	 * attribute.
	 *
	 * @param  src the SRC attribute to escape
	 * @return the escaped SRC attribute, or <code>null</code> if the SRC
	 *         attribute is <code>null</code>
	 */
	public static SRC src(CharSequence src) {
		return new SRC(src);
	}

	/**
	 * Escapes the JavaScript value so that it is safe to use in a JavaScript
	 * context.
	 *
	 * @param  js the JavaScript value to escape
	 * @return the escaped JavaScript value, or <code>null</code> if the
	 *         JavaScript value is <code>null</code>
	 */
	public static JavaScript js(CharSequence js) {
		return new JavaScript(js);
	}


	public interface XSSEscapedString extends CharSequence {
		public String getRawString();
		public String escape();
	}

	public interface HtmlAttribute extends XSSEscapedString {}
	public interface HtmlBody extends XSSEscapedString {}
	public interface JavaScript extends XSSEscapedString {}
	public interface CSS extends XSSEscapedString {}
	public interface SRC extends XSSEscapedString {}
	public interface HREF extends XSSEscapedString {}

	public interface SafeContent
		extends HtmlAttribute, HtmlBody, JavaScript, CSS, SRC, HREF {}

}
