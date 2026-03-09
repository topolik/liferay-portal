package com.liferay.keymanager.crypto;

import com.liferay.keymanager.KeyReference;

import java.security.Key;

/**
 * @author Tomas Polesovsky
 */
public class CryptoKey {
	public CryptoKey(KeyReference keyReference, Key key, String cipherSpec) {
		this._keyReference = keyReference;
		this._key = key;
		this._cipherSpec = cipherSpec;
	}

	public KeyReference getKeyReference() {
		return _keyReference;
	}

	public Key getKey() {
		return _key;
	}

	public String getCipherSpec() {
		return _cipherSpec;
	}

	private final KeyReference _keyReference;
	private final Key _key;
	private final String _cipherSpec;
}
