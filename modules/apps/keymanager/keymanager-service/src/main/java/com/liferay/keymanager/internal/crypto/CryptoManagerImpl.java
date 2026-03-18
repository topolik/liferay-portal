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
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;

import java.security.Key;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

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
	public byte[] decrypt(
			long companyId, KeyReference keyReference, byte[] ciphertext)
		throws CryptoManagerException {

		for (CryptoVaultProvider provider : _getCryptoVaultProviders(
				companyId, keyReference.getProviderId())) {

			try {
				return provider.decrypt(
					companyId, keyReference.getIdentifier(), ciphertext);
			}
			catch (CryptoManagerException cryptoManagerException) {
				if (_log.isDebugEnabled()) {
					_log.debug(
						"Unable to decrypt with provider",
						cryptoManagerException);
				}
			}
		}

		throw new CryptoManagerException(
			"No key found for decryption: " + keyReference.getIdentifier());
	}

	@Override
	public void deleteKey(long companyId, KeyReference keyReference)
		throws CryptoManagerException {

		CryptoVaultProvider cryptoVaultProvider = _getCryptoVaultProvider(
			companyId, keyReference.getProviderId());

		cryptoVaultProvider.deleteKey(companyId, keyReference.getIdentifier());
	}

	@Override
	public byte[] encrypt(
			long companyId, KeyReference keyReference, byte[] plaintext)
		throws CryptoManagerException {

		for (CryptoVaultProvider provider : _getCryptoVaultProviders(
				companyId, keyReference.getProviderId())) {

			try {
				return provider.encrypt(
					companyId, keyReference.getIdentifier(), plaintext);
			}
			catch (CryptoManagerException cryptoManagerException) {
				if (_log.isDebugEnabled()) {
					_log.debug(
						"Unable to encrypt with provider",
						cryptoManagerException);
				}
			}
		}

		throw new CryptoManagerException(
			"No key found for encryption: " + keyReference.getIdentifier());
	}

	@Override
	public KeyReference generateAsymmetricKeyPair(
			long companyId, String providerId, String identifier,
			String algorithmSpec)
		throws CryptoManagerException {

		String resolvedProviderId = _getCryptoVaultProviderId(
			companyId, providerId);

		CryptoVaultProvider cryptoVaultProvider = _getCryptoVaultProvider(
			companyId, resolvedProviderId);

		String finalIdentifier = cryptoVaultProvider.generateAsymmetricKeyPair(
			companyId, identifier, algorithmSpec);

		return KeyReference.fromString(
			StringBundler.concat(
				"${keyRef:", resolvedProviderId, ":", finalIdentifier, "}"));
	}

	@Override
	public KeyReference generateSecretKey(
			long companyId, String providerId, String identifier,
			String algorithmSpec)
		throws CryptoManagerException {

		String resolvedProviderId = _getCryptoVaultProviderId(
			companyId, providerId);

		CryptoVaultProvider cryptoVaultProvider = _getCryptoVaultProvider(
			companyId, resolvedProviderId);

		String finalIdentifier = cryptoVaultProvider.generateSecretKey(
			companyId, identifier, algorithmSpec);

		return KeyReference.fromString(
			StringBundler.concat(
				"${keyRef:", resolvedProviderId, ":", finalIdentifier, "}"));
	}

	@Override
	public List<KeyReference> getKeyIdentifiers(
			long companyId, String providerId)
		throws CryptoManagerException {

		List<KeyReference> keyReferences = new ArrayList<>();

		for (String trackedProviderId : _getCryptoVaultProviderIds(
				companyId, providerId)) {

			CryptoVaultProvider provider = _serviceTrackerMap.getService(
				trackedProviderId);

			if (provider == null) {
				continue;
			}

			List<String> identifiers = provider.getKeyIdentifiers(companyId);

			for (String identifier : identifiers) {
				keyReferences.add(
					KeyReference.fromString(
						StringBundler.concat(
							"${keyRef:", trackedProviderId, ":", identifier,
							"}")));
			}
		}

		return keyReferences;
	}

	@Override
	public CryptoKey getKeyMetadata(
			long companyId, KeyReference keyReference)
		throws CryptoManagerException {

		for (CryptoVaultProvider provider : _getCryptoVaultProviders(
				companyId, keyReference.getProviderId())) {

			try {
				return provider.getKeyMetadata(
					companyId, keyReference.getIdentifier());
			}
			catch (CryptoManagerException cryptoManagerException) {
				if (_log.isDebugEnabled()) {
					_log.debug(
						"Unable to fetch key metadata from provider",
						cryptoManagerException);
				}
			}
		}

		throw new CryptoManagerException(
			"No key metadata found for identifier: " +
				keyReference.getIdentifier());
	}

	@Override
	public List<String> getProviders(long companyId)
		throws CryptoManagerException {

		return _getCryptoVaultProviderIds(companyId, KeyReference.ANY_PROVIDER);
	}

	@Override
	public KeyReference importSecretKey(
			long companyId, String providerId, String identifier,
			byte[] rawKeyMaterial, String algorithmSpec)
		throws CryptoManagerException {

		String resolvedProviderId = _getCryptoVaultProviderId(
			companyId, providerId);

		CryptoVaultProvider cryptoVaultProvider = _getCryptoVaultProvider(
			companyId, resolvedProviderId);

		String finalIdentifier = cryptoVaultProvider.importSecretKey(
			companyId, identifier, rawKeyMaterial, algorithmSpec);

		return KeyReference.fromString(
			StringBundler.concat(
				"${keyRef:", resolvedProviderId, ":", finalIdentifier, "}"));
	}

	@Override
	public Key unwrap(
			long companyId, KeyReference masterKeyReference,
			byte[] wrappedKeyBytes, String wrappedKeyAlgorithm,
			int wrappedKeyCipherType)
		throws CryptoManagerException {

		for (CryptoVaultProvider provider : _getCryptoVaultProviders(
				companyId, masterKeyReference.getProviderId())) {

			try {
				return provider.unwrap(
					companyId, masterKeyReference.getIdentifier(),
					wrappedKeyBytes, wrappedKeyAlgorithm, wrappedKeyCipherType);
			}
			catch (CryptoManagerException cryptoManagerException) {
				if (_log.isDebugEnabled()) {
					_log.debug(
						"Unable to unwrap key with provider",
						cryptoManagerException);
				}
			}
		}

		throw new CryptoManagerException(
			"No master key found for unwrapping: " +
				masterKeyReference.getIdentifier());
	}

	@Override
	public byte[] wrap(
			long companyId, KeyReference masterKeyReference, Key keyToWrap)
		throws CryptoManagerException {

		for (CryptoVaultProvider provider : _getCryptoVaultProviders(
				companyId, masterKeyReference.getProviderId())) {

			try {
				return provider.wrap(
					companyId, masterKeyReference.getIdentifier(), keyToWrap);
			}
			catch (CryptoManagerException cryptoManagerException) {
				if (_log.isDebugEnabled()) {
					_log.debug(
						"Unable to wrap key with provider",
						cryptoManagerException);
				}
			}
		}

		throw new CryptoManagerException(
			"No master key found for wrapping: " +
				masterKeyReference.getIdentifier());
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

	private CryptoVaultProvider _getCryptoVaultProvider(
			long companyId, String providerId)
		throws CryptoManagerException {

		CryptoVaultProvider provider = _serviceTrackerMap.getService(
			providerId);

		if ((provider != null) && provider.isAllowedCompany(companyId)) {
			return provider;
		}

		throw new CryptoManagerException(
			StringBundler.concat(
				"No crypto vault provider found for ID: ", providerId,
				" and company ID: ", companyId));
	}

	private String _getCryptoVaultProviderId(long companyId, String providerId)
		throws CryptoManagerException {

		if (Objects.equals(providerId, KeyReference.ANY_PROVIDER)) {
			for (String trackedProviderId : _serviceTrackerMap.keySet()) {
				CryptoVaultProvider provider = _serviceTrackerMap.getService(
					trackedProviderId);

				if (provider.isAllowedCompany(companyId)) {
					return trackedProviderId;
				}
			}

			throw new CryptoManagerException(
				StringBundler.concat(
					"No crypto vault provider found for ANY provider",
					" and company ID: ", companyId));
		}

		_getCryptoVaultProvider(companyId, providerId);

		return providerId;
	}

	private List<String> _getCryptoVaultProviderIds(
			long companyId, String providerId)
		throws CryptoManagerException {

		if (Objects.equals(providerId, KeyReference.ANY_PROVIDER)) {
			List<String> providerIds = new ArrayList<>();

			for (String trackedProviderId : _serviceTrackerMap.keySet()) {
				CryptoVaultProvider provider = _serviceTrackerMap.getService(
					trackedProviderId);

				if (provider.isAllowedCompany(companyId)) {
					providerIds.add(trackedProviderId);
				}
			}

			return providerIds;
		}

		_getCryptoVaultProvider(companyId, providerId);

		return Collections.singletonList(providerId);
	}

	private List<CryptoVaultProvider> _getCryptoVaultProviders(
			long companyId, String providerId)
		throws CryptoManagerException {

		List<CryptoVaultProvider> providers = new ArrayList<>();

		for (String id : _getCryptoVaultProviderIds(
				companyId, providerId)) {

			CryptoVaultProvider provider = _serviceTrackerMap.getService(id);

			if (provider != null) {
				providers.add(provider);
			}
		}

		return providers;
	}

	private static final Log _log = LogFactoryUtil.getLog(
		CryptoManagerImpl.class);

	private ServiceTrackerMap<String, CryptoVaultProvider> _serviceTrackerMap;

}