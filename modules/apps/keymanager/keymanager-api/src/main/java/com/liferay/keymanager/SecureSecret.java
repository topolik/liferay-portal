/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.keymanager;

import java.util.Arrays;

/**
 * @author Tomas Polesovsky
 */
public class SecureSecret implements AutoCloseable {

	public SecureSecret(byte[] bytes) {
		if (bytes == null) {
			_bytes = new byte[0];
		}
		else {
			_bytes = Arrays.copyOf(bytes, bytes.length);
		}

		_chars = null;
	}

	public SecureSecret(char[] chars) {
		if (chars == null) {
			_chars = new char[0];
		}
		else {
			_chars = Arrays.copyOf(chars, chars.length);
		}

		_bytes = null;
	}

	@Override
	public void close() {
		if (_bytes != null) {
			Arrays.fill(_bytes, (byte)0);
		}

		if (_chars != null) {
			Arrays.fill(_chars, '\0');
		}
	}

	public byte[] getBytes() {
		if (_bytes != null) {
			return _bytes;
		}

		if (_chars == null) {
			return new byte[0];
		}

		byte[] bytes = new byte[_chars.length];

		for (int i = 0; i < _chars.length; i++) {
			bytes[i] = (byte)_chars[i];
		}

		return bytes;
	}

	public char[] getChars() {
		if (_chars != null) {
			return _chars;
		}

		if (_bytes == null) {
			return new char[0];
		}

		char[] chars = new char[_bytes.length];

		for (int i = 0; i < _bytes.length; i++) {
			chars[i] = (char)(_bytes[i] & 0xFF);
		}

		return chars;
	}

	private final byte[] _bytes;
	private final char[] _chars;

}
