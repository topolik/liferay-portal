/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.keymanager.crypto;

import com.liferay.keymanager.KeyReference;

import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.cert.Certificate;

import javax.crypto.SecretKey;

/**
 * @author Tomas Polesovsky
 */
public interface CryptoManager {

	public void deleteKey(KeyReference keyReference)
		throws CryptoManagerException;

	public Certificate getCertificate(KeyReference keyReference)
		throws CryptoManagerException;

	public PrivateKey getPrivateKey(KeyReference keyReference)
		throws CryptoManagerException;

	public PublicKey getPublicKey(KeyReference keyReference)
		throws CryptoManagerException;

	public SecretKey getSecretKey(KeyReference keyReference)
		throws CryptoManagerException;

	public void putCertificate(
			KeyReference keyReference, Certificate certificate)
		throws CryptoManagerException;

	public void putPrivateKey(
			KeyReference keyReference, PrivateKey privateKey,
			Certificate[] certificateChain)
		throws CryptoManagerException;

	public void putSecretKey(KeyReference keyReference, SecretKey secretKey)
		throws CryptoManagerException;

}