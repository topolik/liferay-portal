/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.keymanager.spi.crypto;

import com.liferay.keymanager.crypto.CryptoKey;
import com.liferay.keymanager.crypto.CryptoManagerException;

import java.security.Key;

import java.util.List;

/**
 * @author Tomas Polesovsky
 */
public interface CryptoVaultProvider {

	public byte[] decrypt(long companyId, String identifier, byte[] ciphertext)
		throws CryptoManagerException;

	public void deleteKey(long companyId, String identifier)
		throws CryptoManagerException;

	public byte[] encrypt(long companyId, String identifier, byte[] plaintext)
		throws CryptoManagerException;

	public String generateAsymmetricKeyPair(
			long companyId, String identifier, String algorithmSpec)
		throws CryptoManagerException;

	public String generateSecretKey(
			long companyId, String identifier, String algorithmSpec)
		throws CryptoManagerException;

	public List<String> getKeyIdentifiers(long companyId)
		throws CryptoManagerException;

	public CryptoKey getKeyMetadata(long companyId, String identifier)
		throws CryptoManagerException;

	public String importSecretKey(
			long companyId, String identifier, byte[] rawKeyMaterial,
			String algorithmSpec)
		throws CryptoManagerException;

	public boolean isAllowedCompany(long companyId);

	public Key unwrap(
			long companyId, String identifier, byte[] wrappedKeyBytes,
			String wrappedKeyAlgorithm, int wrappedKeyCipherType)
		throws CryptoManagerException;

	public byte[] wrap(long companyId, String identifier, Key keyToWrap)
		throws CryptoManagerException;

}