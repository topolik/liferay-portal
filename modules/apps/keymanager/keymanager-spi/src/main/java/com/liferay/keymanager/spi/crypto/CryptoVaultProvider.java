/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.keymanager.spi.crypto;

import com.liferay.keymanager.crypto.CryptoKey;
import com.liferay.keymanager.crypto.CryptoManagerException;

import java.security.Key;
import java.security.PublicKey;
import java.security.cert.Certificate;

import java.util.List;

/**
 * @author Tomas Polesovsky
 */
public interface CryptoVaultProvider {

	public void addCertificate(String identifier, Certificate certificate)
		throws CryptoManagerException;

	public void addPrivateKey(String identifier, CryptoKey privateKey)
		throws CryptoManagerException;

	public void addPublicKey(String identifier, CryptoKey publicKey)
		throws CryptoManagerException;

	public void addSecretKey(String identifier, CryptoKey secretKey)
		throws CryptoManagerException;

	public byte[] decrypt(String identifier, byte[] ciphertext)
		throws CryptoManagerException;

	public void deleteKey(String identifier) throws CryptoManagerException;

	public byte[] encrypt(String identifier, byte[] plaintext)
		throws CryptoManagerException;

	public List<String> getKeyIdentifiers() throws CryptoManagerException;

	public PublicKey getPublicKey(String identifier)
		throws CryptoManagerException;

	public Key unwrap(
			String identifier, byte[] wrappedKeyBytes,
			String wrappedKeyAlgorithm, int wrappedKeyCipherType)
		throws CryptoManagerException;

	public byte[] wrap(String identifier, Key keyToWrap)
		throws CryptoManagerException;

}