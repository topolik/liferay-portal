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

	public byte[] decrypt(KeyReference keyReference, byte[] ciphertext)
		throws CryptoManagerException;

	public void deleteKey(KeyReference keyReference)
		throws CryptoManagerException;

	public byte[] encrypt(KeyReference keyReference, byte[] plaintext)
		throws CryptoManagerException;

	public KeyReference generateAsymmetricKeyPair(
			String providerId, String identifier, String algorithmSpec)
		throws CryptoManagerException;

	public KeyReference generateSecretKey(
			String providerId, String identifier, String algorithmSpec)
		throws CryptoManagerException;

	public List<KeyReference> getKeyIdentifiers(String providerId)
		throws CryptoManagerException;

	public CryptoKeyMetadata getKeyMetadata(KeyReference keyReference)
		throws CryptoManagerException;

	public List<String> getProviders() throws CryptoManagerException;

	public KeyReference importSecretKey(
			String providerId, String identifier, byte[] rawKeyMaterial,
			String algorithmSpec)
		throws CryptoManagerException;

	public Key unwrap(
			KeyReference masterKeyReference, byte[] wrappedKeyBytes,
			String wrappedKeyAlgorithm, int wrappedKeyCipherType)
		throws CryptoManagerException;

	public byte[] wrap(KeyReference masterKeyReference, Key keyToWrap)
		throws CryptoManagerException;

}