/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.keymanager.crypto;

import com.liferay.keymanager.KeyReference;

import java.security.Key;

/**
 * @author Tomas Polesovsky
 */
public class CryptoKey {

	public CryptoKey(KeyReference keyReference, Key key, String cipherSpec) {
		_keyReference = keyReference;
		_key = key;
		_cipherSpec = cipherSpec;
	}

	public String getCipherSpec() {
		return _cipherSpec;
	}

	public Key getKey() {
		return _key;
	}

	public KeyReference getKeyReference() {
		return _keyReference;
	}

	private final String _cipherSpec;
	private final Key _key;
	private final KeyReference _keyReference;

}