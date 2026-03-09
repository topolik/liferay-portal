/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.keymanager.internal.crypto;

import com.liferay.keymanager.KeyReference;
import com.liferay.keymanager.crypto.CryptoManager;
import com.liferay.keymanager.crypto.CryptoManagerException;
import com.liferay.keymanager.spi.crypto.CryptoVaultProvider;
import com.liferay.osgi.service.tracker.collections.map.ServiceTrackerMap;
import com.liferay.osgi.service.tracker.collections.map.ServiceTrackerMapFactory;

import java.security.Key;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.cert.Certificate;

import java.util.List;

import javax.crypto.SecretKey;

import org.osgi.framework.BundleContext;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Deactivate;

/**
 * @author Tomas Polesovsky
 */
@Component(service = CryptoManager.class)
public class CryptoManagerImpl implements CryptoManager {

	@Override
	public byte[] decrypt(KeyReference keyReference, byte[] ciphertext)
		throws CryptoManagerException {

		CryptoVaultProvider cryptoVaultProvider = _getCryptoVaultProvider(
			keyReference.getProviderId());

		return cryptoVaultProvider.decrypt(
			keyReference.getIdentifier(), ciphertext);
	}

	@Override
	public void deleteKey(KeyReference keyReference)
		throws CryptoManagerException {

		CryptoVaultProvider cryptoVaultProvider = _getCryptoVaultProvider(
			keyReference.getProviderId());

		cryptoVaultProvider.deleteKey(keyReference.getIdentifier());
	}

	@Override
	public byte[] encrypt(KeyReference keyReference, byte[] plaintext)
		throws CryptoManagerException {

		CryptoVaultProvider cryptoVaultProvider = _getCryptoVaultProvider(
			keyReference.getProviderId());

		return cryptoVaultProvider.encrypt(
			keyReference.getIdentifier(), plaintext);
	}

	@Override
	public List<String> getKeyIdentifiers(String providerId)
		throws CryptoManagerException {

		CryptoVaultProvider cryptoVaultProvider = _getCryptoVaultProvider(
			providerId);

		return cryptoVaultProvider.getKeyIdentifiers();
	}

	@Override
	public PublicKey getPublicKey(KeyReference keyReference)
		throws CryptoManagerException {

		CryptoVaultProvider cryptoVaultProvider = _getCryptoVaultProvider(
			keyReference.getProviderId());

		return cryptoVaultProvider.getPublicKey(keyReference.getIdentifier());
	}

	@Override
	public void addCertificate(
			KeyReference keyReference, Certificate certificate)
		throws CryptoManagerException {

		CryptoVaultProvider cryptoVaultProvider = _getCryptoVaultProvider(
			keyReference.getProviderId());

		cryptoVaultProvider.addCertificate(
			keyReference.getIdentifier(), certificate);
	}

	@Override
	public void addPrivateKey(
			KeyReference keyReference, PrivateKey privateKey,
			Certificate[] certificateChain, String cipherSpec)
		throws CryptoManagerException {

		CryptoVaultProvider cryptoVaultProvider = _getCryptoVaultProvider(
			keyReference.getProviderId());

		cryptoVaultProvider.addPrivateKey(
			keyReference.getIdentifier(), privateKey, certificateChain,
			cipherSpec);
	}

	@Override
	public void addPublicKey(
			KeyReference keyReference, PublicKey publicKey, String cipherSpec)
		throws CryptoManagerException {

		CryptoVaultProvider cryptoVaultProvider = _getCryptoVaultProvider(
			keyReference.getProviderId());

		cryptoVaultProvider.addPublicKey(
			keyReference.getIdentifier(), publicKey, cipherSpec);
	}

	@Override
	public void addSecretKey(
			KeyReference keyReference, SecretKey secretKey, String cipherSpec)
		throws CryptoManagerException {

		CryptoVaultProvider cryptoVaultProvider = _getCryptoVaultProvider(
			keyReference.getProviderId());

		cryptoVaultProvider.addSecretKey(
			keyReference.getIdentifier(), secretKey, cipherSpec);
	}

	@Override
	public Key unwrap(
			KeyReference masterKeyReference, byte[] wrappedKeyBytes,
			String wrappedKeyAlgorithm, int wrappedKeyCipherType)
		throws CryptoManagerException {

		CryptoVaultProvider cryptoVaultProvider = _getCryptoVaultProvider(
			masterKeyReference.getProviderId());

		return cryptoVaultProvider.unwrap(
			masterKeyReference.getIdentifier(), wrappedKeyBytes,
			wrappedKeyAlgorithm, wrappedKeyCipherType);
	}

	@Override
	public byte[] wrap(KeyReference masterKeyReference, Key keyToWrap)
		throws CryptoManagerException {

		CryptoVaultProvider cryptoVaultProvider = _getCryptoVaultProvider(
			masterKeyReference.getProviderId());

		return cryptoVaultProvider.wrap(
			masterKeyReference.getIdentifier(), keyToWrap);
	}

	@Activate
	protected void activate(BundleContext bundleContext) {
		_serviceTrackerMap = ServiceTrackerMapFactory.openSingleValueMap(
			bundleContext, CryptoVaultProvider.class, "providerId");
	}

	@Deactivate
	protected void deactivate() {
		_serviceTrackerMap.close();
	}

	private CryptoVaultProvider _getCryptoVaultProvider(String providerId)
		throws CryptoManagerException {

		CryptoVaultProvider cryptoVaultProvider = _serviceTrackerMap.getService(
			providerId);

		if (cryptoVaultProvider == null) {
			throw new CryptoManagerException(
				"No crypto vault provider found for ID: " + providerId);
		}

		return cryptoVaultProvider;
	}

	private ServiceTrackerMap<String, CryptoVaultProvider> _serviceTrackerMap;

}
