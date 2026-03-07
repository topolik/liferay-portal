/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.keymanager.spi.crypto;

import com.liferay.keymanager.crypto.CryptoManagerException;

import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.cert.Certificate;

import javax.crypto.SecretKey;

/**
 * @author Tomas Polesovsky
 */
public interface CryptoVaultProvider {

	public void deleteKey(String alias) throws CryptoManagerException;

	public Certificate getCertificate(String alias)
		throws CryptoManagerException;

	public PrivateKey getPrivateKey(String alias) throws CryptoManagerException;

	public PublicKey getPublicKey(String alias) throws CryptoManagerException;

	public SecretKey getSecretKey(String alias) throws CryptoManagerException;

	public void putCertificate(String alias, Certificate certificate)
		throws CryptoManagerException;

	public void putPrivateKey(
			String alias, PrivateKey privateKey, Certificate[] certificateChain)
		throws CryptoManagerException;

	public void putSecretKey(String alias, SecretKey secretKey)
		throws CryptoManagerException;

}