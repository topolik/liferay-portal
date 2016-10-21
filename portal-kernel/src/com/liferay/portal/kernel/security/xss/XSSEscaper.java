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

/**
 * @author Tomas Polesovsky
 */
public interface XSSEscaper {

	XSS.SafeHTML safeHTML(String input);

	XSS.VerifiedJS verifiedJS(String input);

	XSS.EscapedString htmlBody(CharSequence input);

	XSS.EscapedString attribute(CharSequence input);

	XSS.EscapedString css(CharSequence input);

	XSS.EscapedString href(CharSequence input);

	XSS.EscapedString src(CharSequence input);

	XSS.EscapedString js(CharSequence input);

}