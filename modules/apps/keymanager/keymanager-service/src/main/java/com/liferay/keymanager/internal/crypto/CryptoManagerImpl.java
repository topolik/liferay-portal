/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.keymanager.internal.crypto;

import com.liferay.keymanager.KeyReference;
import com.liferay.keymanager.crypto.CryptoKey;
import com.liferay.keymanager.crypto.CryptoManager;
import com.liferay.keymanager.crypto.CryptoManagerException;
import com.liferay.keymanager.spi.crypto.CryptoVaultProvider;
import com.liferay.osgi.service.tracker.collections.map.ServiceTrackerMap;
import com.liferay.osgi.service.tracker.collections.map.ServiceTrackerMapFactory;
import com.liferay.petra.string.StringBundler;

import java.security.Key;

import java.util.ArrayList;
import java.util.List;

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
	public void addPrivateKey(KeyReference keyReference, CryptoKey privateKey)
		throws CryptoManagerException {

		CryptoVaultProvider cryptoVaultProvider = _getCryptoVaultProvider(
			keyReference.getProviderId());

		cryptoVaultProvider.addPrivateKey(
			keyReference.getIdentifier(), privateKey);
	}

	@Override
	public void addPublicKey(KeyReference keyReference, CryptoKey publicKey)
		throws CryptoManagerException {

		CryptoVaultProvider cryptoVaultProvider = _getCryptoVaultProvider(
			keyReference.getProviderId());

		cryptoVaultProvider.addPublicKey(
			keyReference.getIdentifier(), publicKey);
	}

	@Override
	public void addSecretKey(KeyReference keyReference, CryptoKey secretKey)
		throws CryptoManagerException {

		CryptoVaultProvider cryptoVaultProvider = _getCryptoVaultProvider(
			keyReference.getProviderId());

		cryptoVaultProvider.addSecretKey(
			keyReference.getIdentifier(), secretKey);
	}

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
	public List<KeyReference> getKeyIdentifiers(String providerId)
		throws CryptoManagerException {

		CryptoVaultProvider cryptoVaultProvider = _getCryptoVaultProvider(
			providerId);

		List<String> identifiers = cryptoVaultProvider.getKeyIdentifiers();

		List<KeyReference> keyReferences = new ArrayList<>(identifiers.size());

		for (String identifier : identifiers) {
			keyReferences.add(
				KeyReference.fromString(
					StringBundler.concat(
						"${keyRef:", providerId, ":", identifier, "}")));
		}

		return keyReferences;
	}

	@Override
	public List<String> getProviders() throws CryptoManagerException {
		return new ArrayList<>(_serviceTrackerMap.keySet());
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