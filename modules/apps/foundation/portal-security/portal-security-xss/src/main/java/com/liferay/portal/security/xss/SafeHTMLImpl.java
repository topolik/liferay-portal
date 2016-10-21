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

import com.liferay.portal.kernel.security.xss.XSS;

/**
 * @author Tomas Polesovsky
 */
public class SafeHTMLImpl extends EscapedStringImpl implements XSS.SafeHTML {

	public SafeHTMLImpl(String safeHTML) {
		super(safeHTML);

		_safeHTML = safeHTML;
	}

	@Override
	public char charAt(int index) {
		return _safeHTML.charAt(index);
	}

	@Override
	public int length() {
		return _safeHTML.length();
	}

	@Override
	public CharSequence subSequence(int start, int end) {
		return _safeHTML.subSequence(start, end);
	}

	@Override
	public String toString() {
		return _safeHTML;
	}

	private final String _safeHTML;

}