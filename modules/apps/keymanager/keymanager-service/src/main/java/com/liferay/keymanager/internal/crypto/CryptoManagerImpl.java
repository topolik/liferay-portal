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

import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.cert.Certificate;

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
	public void deleteKey(KeyReference keyReference)
		throws CryptoManagerException {

		_getProvider(keyReference).deleteKey(keyReference.getIdentifier());
	}

	@Override
	public Certificate getCertificate(KeyReference keyReference)
		throws CryptoManagerException {

		return _getProvider(keyReference).getCertificate(
			keyReference.getIdentifier());
	}

	@Override
	public PrivateKey getPrivateKey(KeyReference keyReference)
		throws CryptoManagerException {

		return _getProvider(keyReference).getPrivateKey(
			keyReference.getIdentifier());
	}

	@Override
	public PublicKey getPublicKey(KeyReference keyReference)
		throws CryptoManagerException {

		return _getProvider(keyReference).getPublicKey(
			keyReference.getIdentifier());
	}

	@Override
	public SecretKey getSecretKey(KeyReference keyReference)
		throws CryptoManagerException {

		return _getProvider(keyReference).getSecretKey(
			keyReference.getIdentifier());
	}

	@Override
	public void putCertificate(
			KeyReference keyReference, Certificate certificate)
		throws CryptoManagerException {

		_getProvider(keyReference).putCertificate(
			keyReference.getIdentifier(), certificate);
	}

	@Override
	public void putPrivateKey(
			KeyReference keyReference, PrivateKey privateKey,
			Certificate[] certificateChain)
		throws CryptoManagerException {

		_getProvider(keyReference).putPrivateKey(
			keyReference.getIdentifier(), privateKey, certificateChain);
	}

	@Override
	public void putSecretKey(KeyReference keyReference, SecretKey secretKey)
		throws CryptoManagerException {

		_getProvider(keyReference).putSecretKey(
			keyReference.getIdentifier(), secretKey);
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

	private CryptoVaultProvider _getProvider(KeyReference keyReference)
		throws CryptoManagerException {

		if (keyReference.getType() != KeyReference.Type.CRYPTO) {
			throw new CryptoManagerException(
				"Reference is not of type CRYPTO: " +
					keyReference.getRawReference());
		}

		String providerId = keyReference.getProviderId();

		CryptoVaultProvider cryptoVaultProvider = _serviceTrackerMap.getService(
			providerId);

		if (cryptoVaultProvider == null) {
			throw new CryptoManagerException(
				"Crypto provider not found: " + providerId);
		}

		return cryptoVaultProvider;
	}

	private ServiceTrackerMap<String, CryptoVaultProvider> _serviceTrackerMap;

}
