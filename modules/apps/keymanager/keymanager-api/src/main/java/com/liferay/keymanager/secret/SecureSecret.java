/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.keymanager.secret;

import com.liferay.keymanager.KeyReference;

import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;

import java.util.Arrays;

import javax.security.auth.Destroyable;

/**
 * @author Tomas Polesovsky
 */
public final class SecureSecret implements AutoCloseable, Destroyable {

	public SecureSecret(KeyReference keyReference, byte[] bytes) {
		_keyReference = keyReference;

		if (bytes == null) {
			_bytes = new byte[0];
		}
		else {
			_bytes = Arrays.copyOf(bytes, bytes.length);
		}
	}

	public SecureSecret(KeyReference keyReference, char[] chars) {
		_keyReference = keyReference;

		if (chars == null) {
			_bytes = new byte[0];
		}
		else {
			_chars = Arrays.copyOf(chars, chars.length);

			ByteBuffer byteBuffer = StandardCharsets.UTF_8.encode(
				CharBuffer.wrap(chars));

			_bytes = new byte[byteBuffer.remaining()];

			byteBuffer.get(_bytes);
		}
	}

	public SecureSecret(KeyReference keyReference, String value) {
		this(keyReference, (value != null) ? value.toCharArray() : null);
	}

	@Override
	public void close() {
		destroy();
	}

	@Override
	public synchronized void destroy() {
		_destroyed = true;

		if (_bytes != null) {
			Arrays.fill(_bytes, (byte)0);
		}

		if (_chars != null) {
			Arrays.fill(_chars, '\0');
		}
	}

	public synchronized byte[] getBytes() {
		if (_destroyed) {
			throw new IllegalArgumentException("Secret is destroyed");
		}

		return _bytes;
	}

	public char[] getChars() {
		return _getChars(StandardCharsets.UTF_8);
	}

	public KeyReference getKeyReference() {
		return _keyReference;
	}

	@Override
	public synchronized boolean isDestroyed() {
		return _destroyed;
	}

	private synchronized char[] _getChars(Charset charset) {
		if (_destroyed) {
			throw new IllegalArgumentException("Secret is destroyed");
		}

		if (_chars != null) {
			return _chars;
		}

		CharBuffer charBuffer = charset.decode(ByteBuffer.wrap(_bytes));

		_chars = new char[charBuffer.remaining()];

		charBuffer.get(_chars);

		return _chars;
	}

	private volatile byte[] _bytes;
	private volatile char[] _chars;
	private volatile boolean _destroyed;
	private final KeyReference _keyReference;

}