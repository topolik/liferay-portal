/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.keymanager.secret;

import com.liferay.keymanager.KeyReference;

import java.util.Arrays;

/**
 * @author Tomas Polesovsky
 */
public final class SecureSecret implements AutoCloseable {

	public SecureSecret(KeyReference keyReference, byte[] bytes) {
		_keyReference = keyReference;

		if (bytes == null) {
			_bytes = new byte[0];
		}
		else {
			_bytes = Arrays.copyOf(bytes, bytes.length);
		}
	}

	@Override
	public void close() {
		if (_bytes != null) {
			Arrays.fill(_bytes, (byte)0);
		}
	}

	public byte[] getBytes() {
		return _bytes;
	}

	public KeyReference getKeyReference() {
		return _keyReference;
	}

	private final byte[] _bytes;
	private final KeyReference _keyReference;

}