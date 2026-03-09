/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.keymanager.crypto;

import com.liferay.keymanager.KeyReference;

import java.security.Key;

import java.util.List;

import org.osgi.annotation.versioning.ProviderType;

/**
 * @author Tomas Polesovsky
 */
@ProviderType
public interface CryptoManager {

	public void addPrivateKey(
			KeyReference keyReference, CryptoKey privateKey,
			CryptoKey publicKey)
		throws CryptoManagerException;

	public void addPublicKey(KeyReference keyReference, CryptoKey publicKey)
		throws CryptoManagerException;

	public void addSecretKey(KeyReference keyReference, CryptoKey secretKey)
		throws CryptoManagerException;

	public byte[] decrypt(KeyReference keyReference, byte[] ciphertext)
		throws CryptoManagerException;

	public void deleteKey(KeyReference keyReference)
		throws CryptoManagerException;

	public byte[] encrypt(KeyReference keyReference, byte[] plaintext)
		throws CryptoManagerException;

	public List<KeyReference> getKeyIdentifiers(String providerId)
		throws CryptoManagerException;

	public List<String> getProviders() throws CryptoManagerException;

	public Key unwrap(
			KeyReference masterKeyReference, byte[] wrappedKeyBytes,
			String wrappedKeyAlgorithm, int wrappedKeyCipherType)
		throws CryptoManagerException;

	public byte[] wrap(KeyReference masterKeyReference, Key keyToWrap)
		throws CryptoManagerException;

}