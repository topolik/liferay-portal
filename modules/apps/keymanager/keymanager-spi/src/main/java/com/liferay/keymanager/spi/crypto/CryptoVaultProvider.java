/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.keymanager.spi.crypto;

import com.liferay.keymanager.crypto.CryptoKeyMetadata;
import com.liferay.keymanager.crypto.CryptoManagerException;

import java.security.Key;

import java.util.List;

/**
 * @author Tomas Polesovsky
 */
public interface CryptoVaultProvider {

	public byte[] decrypt(String identifier, byte[] ciphertext)
		throws CryptoManagerException;

	public void deleteKey(String identifier) throws CryptoManagerException;

	public byte[] encrypt(String identifier, byte[] plaintext)
		throws CryptoManagerException;

	public String generateAsymmetricKeyPair(
			String identifier, String algorithmSpec)
		throws CryptoManagerException;

	public String generateSecretKey(String identifier, String algorithmSpec)
		throws CryptoManagerException;

	public List<String> getKeyIdentifiers() throws CryptoManagerException;

	public CryptoKeyMetadata getKeyMetadata(String identifier)
		throws CryptoManagerException;

	public String importSecretKey(
			String identifier, byte[] rawKeyMaterial, String algorithmSpec)
		throws CryptoManagerException;

	public Key unwrap(
			String identifier, byte[] wrappedKeyBytes,
			String wrappedKeyAlgorithm, int wrappedKeyCipherType)
		throws CryptoManagerException;

	public byte[] wrap(String identifier, Key keyToWrap)
		throws CryptoManagerException;

}