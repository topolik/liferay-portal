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

package com.liferay.portal.security.xss;

import com.liferay.portal.kernel.security.xss.EscapeOperation;
import com.liferay.portal.kernel.security.xss.XSS;
import com.liferay.portal.kernel.security.xss.XSSEscaper;

import org.osgi.service.component.annotations.Component;

/**
 * @author Tomas Polesovsky
 */
@Component(immediate = true, service = XSSEscaper.class)
public class XSSEscaperImpl implements XSSEscaper {

	@Override
	public XSS.EscapedString attribute(CharSequence input) {
		return _resetAndChain(input, EscapeOperations.ATTRIBUTE);
	}

	@Override
	public XSS.EscapedString css(CharSequence input) {
		return _resetAndChain(input, EscapeOperations.CSS);
	}

	@Override
	public XSS.EscapedString href(CharSequence input) {
		return _resetAndChain(input, EscapeOperations.HREF);
	}

	@Override
	public XSS.EscapedString htmlBody(CharSequence input) {
		return _resetAndChain(input, EscapeOperations.HTML);
	}

	@Override
	public XSS.EscapedString js(CharSequence input) {
		return _resetAndChain(input, EscapeOperations.JS);
	}

	@Override
	public XSS.SafeHTML safeHTML(String input) {
		return new SafeHTMLImpl(input);
	}

	@Override
	public XSS.EscapedString src(CharSequence input) {
		return _resetAndChain(input, EscapeOperations.URL);
	}

	@Override
	public XSS.VerifiedJS verifiedJS(String input) {
		return new VerifiedJSImpl(input);
	}

	private static XSS.EscapedString _resetAndChain(
		CharSequence cs, EscapeOperation... escapeOperation) {

		if (cs instanceof XSS.EscapedString) {
			XSS.EscapedString escapedString = (XSS.EscapedString)cs;

			return escapedString.reset().chain(escapeOperation);
		}

		return new EscapedStringImpl(cs.toString()).chain(escapeOperation);
	}

}