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

import com.liferay.portal.kernel.util.StringPool;

/**
 * @author Tomas Polesovsky
 */
public abstract class XSSEscapedStringImpl implements XSS.XSSEscapedString {
	private String _rawString;

	public XSSEscapedStringImpl(CharSequence string) {
		if (string == null) {
			_rawString = StringPool.BLANK;
		}
		else if (string instanceof XSS.XSSEscapedString) {
			_rawString = ((XSS.XSSEscapedString) string).getRawString();
		}
		else if(string instanceof String) {
			_rawString = (String)string;
		}
		else {
			throw new IllegalArgumentException(
				"Unsupported argument " + string.getClass().getName());
		}
	}

	@Override
	public String getRawString() {
		return _rawString;
	}

	@Override
	public int length() {
		return toString().length();
	}

	@Override
	public char charAt(int index) {
		return toString().charAt(index);
	}

	@Override
	public CharSequence subSequence(int start, int end) {
		return toString().subSequence(start, end);
	}

	@Override
	public String toString() {
		return escape();
	}

}
