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
import com.liferay.petra.string.StringBundler;
import com.liferay.portal.kernel.util.GetterUtil;

import java.security.Key;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceReference;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Deactivate;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.component.annotations.ReferenceCardinality;
import org.osgi.service.component.annotations.ReferencePolicy;
import org.osgi.util.tracker.ServiceTracker;

/**
 * @author Tomas Polesovsky
 */
@Component(service = CryptoManager.class)
public class CryptoManagerImpl implements CryptoManager {

	@Override
	public byte[] decrypt(
			long companyId, KeyReference keyReference, byte[] ciphertext)
		throws CryptoManagerException {

		String providerId = keyReference.getProviderId();

		if (Objects.equals(providerId, KeyReference.ANY_PROVIDER)) {
			Map<ServiceReference<CryptoVaultProvider>, CryptoVaultProvider>
				tracked = _serviceTracker.getTracked();

			List<ServiceReference<CryptoVaultProvider>> serviceReferences =
				new ArrayList<>(tracked.keySet());

			Collections.sort(serviceReferences);
			Collections.reverse(serviceReferences);

			for (ServiceReference<CryptoVaultProvider> serviceReference :
					serviceReferences) {

				CryptoVaultProvider provider = tracked.get(serviceReference);

				if (!provider.isAllowedCompany(companyId)) {
					continue;
				}

				try {
					return provider.decrypt(
						companyId, keyReference.getIdentifier(), ciphertext);
				}
				catch (CryptoManagerException cryptoManagerException) {
				}
			}

			throw new CryptoManagerException(
				"No key found for decryption: " + keyReference.getIdentifier());
		}

		CryptoVaultProvider cryptoVaultProvider = _getCryptoVaultProvider(
			companyId, providerId);

		return cryptoVaultProvider.decrypt(
			companyId, keyReference.getIdentifier(), ciphertext);
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

		String providerId = keyReference.getProviderId();

		if (Objects.equals(providerId, KeyReference.ANY_PROVIDER)) {
			Map<ServiceReference<CryptoVaultProvider>, CryptoVaultProvider>
				tracked = _serviceTracker.getTracked();

			List<ServiceReference<CryptoVaultProvider>> serviceReferences =
				new ArrayList<>(tracked.keySet());

			Collections.sort(serviceReferences);
			Collections.reverse(serviceReferences);

			for (ServiceReference<CryptoVaultProvider> serviceReference :
					serviceReferences) {

				CryptoVaultProvider provider = tracked.get(serviceReference);

				if (!provider.isAllowedCompany(companyId)) {
					continue;
				}

				try {
					return provider.encrypt(
						companyId, keyReference.getIdentifier(), plaintext);
				}
				catch (CryptoManagerException cryptoManagerException) {
				}
			}

			throw new CryptoManagerException(
				"No key found for encryption: " + keyReference.getIdentifier());
		}

		CryptoVaultProvider cryptoVaultProvider = _getCryptoVaultProvider(
			companyId, providerId);

		return cryptoVaultProvider.encrypt(
			companyId, keyReference.getIdentifier(), plaintext);
	}

	@Override
	public KeyReference generateAsymmetricKeyPair(
			long companyId, String providerId, String identifier,
			String algorithmSpec)
		throws CryptoManagerException {

		CryptoVaultProvider cryptoVaultProvider = _getCryptoVaultProvider(
			companyId, providerId);

		String finalIdentifier = cryptoVaultProvider.generateAsymmetricKeyPair(
			companyId, identifier, algorithmSpec);

		return KeyReference.fromString(
			StringBundler.concat(
				"${keyRef:", providerId, ":", finalIdentifier, "}"));
	}

	@Override
	public KeyReference generateSecretKey(
			long companyId, String providerId, String identifier,
			String algorithmSpec)
		throws CryptoManagerException {

		CryptoVaultProvider cryptoVaultProvider = _getCryptoVaultProvider(
			companyId, providerId);

		String finalIdentifier = cryptoVaultProvider.generateSecretKey(
			companyId, identifier, algorithmSpec);

		return KeyReference.fromString(
			StringBundler.concat(
				"${keyRef:", providerId, ":", finalIdentifier, "}"));
	}

	@Override
	public List<KeyReference> getKeyIdentifiers(
			long companyId, String providerId)
		throws CryptoManagerException {

		CryptoVaultProvider cryptoVaultProvider = _getCryptoVaultProvider(
			companyId, providerId);

		List<String> identifiers = cryptoVaultProvider.getKeyIdentifiers(
			companyId);

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
	public CryptoKey getKeyMetadata(
			long companyId, KeyReference keyReference)
		throws CryptoManagerException {

		String providerId = keyReference.getProviderId();

		if (Objects.equals(providerId, KeyReference.ANY_PROVIDER)) {
			Map<ServiceReference<CryptoVaultProvider>, CryptoVaultProvider>
				tracked = _serviceTracker.getTracked();

			List<ServiceReference<CryptoVaultProvider>> serviceReferences =
				new ArrayList<>(tracked.keySet());

			Collections.sort(serviceReferences);
			Collections.reverse(serviceReferences);

			for (ServiceReference<CryptoVaultProvider> serviceReference :
					serviceReferences) {

				CryptoVaultProvider provider = tracked.get(serviceReference);

				if (!provider.isAllowedCompany(companyId)) {
					continue;
				}

				try {
					return provider.getKeyMetadata(
						companyId, keyReference.getIdentifier());
				}
				catch (CryptoManagerException cryptoManagerException) {
				}
			}

			throw new CryptoManagerException(
				"No key metadata found for identifier: " +
					keyReference.getIdentifier());
		}

		CryptoVaultProvider cryptoVaultProvider = _getCryptoVaultProvider(
			companyId, providerId);

		return cryptoVaultProvider.getKeyMetadata(
			companyId, keyReference.getIdentifier());
	}

	@Override
	public List<String> getProviders(long companyId)
		throws CryptoManagerException {

		List<String> providerIds = new ArrayList<>();

		Map<ServiceReference<CryptoVaultProvider>, CryptoVaultProvider>
			tracked = _serviceTracker.getTracked();

		for (Map.Entry<ServiceReference<CryptoVaultProvider>, CryptoVaultProvider>
				entry : tracked.entrySet()) {

			ServiceReference<CryptoVaultProvider> serviceReference =
				entry.getKey();
			CryptoVaultProvider provider = entry.getValue();

			if (provider.isAllowedCompany(companyId)) {
				providerIds.add(
					GetterUtil.getString(
						serviceReference.getProperty("providerId")));
			}
		}

		return providerIds;
	}

	@Override
	public KeyReference importSecretKey(
			long companyId, String providerId, String identifier,
			byte[] rawKeyMaterial, String algorithmSpec)
		throws CryptoManagerException {

		CryptoVaultProvider cryptoVaultProvider = _getCryptoVaultProvider(
			companyId, providerId);

		String finalIdentifier = cryptoVaultProvider.importSecretKey(
			companyId, identifier, rawKeyMaterial, algorithmSpec);

		return KeyReference.fromString(
			StringBundler.concat(
				"${keyRef:", providerId, ":", finalIdentifier, "}"));
	}

	@Override
	public Key unwrap(
			long companyId, KeyReference masterKeyReference,
			byte[] wrappedKeyBytes, String wrappedKeyAlgorithm,
			int wrappedKeyCipherType)
		throws CryptoManagerException {

		String providerId = masterKeyReference.getProviderId();

		if (Objects.equals(providerId, KeyReference.ANY_PROVIDER)) {
			Map<ServiceReference<CryptoVaultProvider>, CryptoVaultProvider>
				tracked = _serviceTracker.getTracked();

			List<ServiceReference<CryptoVaultProvider>> serviceReferences =
				new ArrayList<>(tracked.keySet());

			Collections.sort(serviceReferences);
			Collections.reverse(serviceReferences);

			for (ServiceReference<CryptoVaultProvider> serviceReference :
					serviceReferences) {

				CryptoVaultProvider provider = tracked.get(serviceReference);

				if (!provider.isAllowedCompany(companyId)) {
					continue;
				}

				try {
					return provider.unwrap(
						companyId, masterKeyReference.getIdentifier(),
						wrappedKeyBytes, wrappedKeyAlgorithm,
						wrappedKeyCipherType);
				}
				catch (CryptoManagerException cryptoManagerException) {
				}
			}

			throw new CryptoManagerException(
				"No master key found for unwrapping: " +
					masterKeyReference.getIdentifier());
		}

		CryptoVaultProvider cryptoVaultProvider = _getCryptoVaultProvider(
			companyId, providerId);

		return cryptoVaultProvider.unwrap(
			companyId, masterKeyReference.getIdentifier(), wrappedKeyBytes,
			wrappedKeyAlgorithm, wrappedKeyCipherType);
	}

	@Override
	public byte[] wrap(
			long companyId, KeyReference masterKeyReference, Key keyToWrap)
		throws CryptoManagerException {

		String providerId = masterKeyReference.getProviderId();

		if (Objects.equals(providerId, KeyReference.ANY_PROVIDER)) {
			Map<ServiceReference<CryptoVaultProvider>, CryptoVaultProvider>
				tracked = _serviceTracker.getTracked();

			List<ServiceReference<CryptoVaultProvider>> serviceReferences =
				new ArrayList<>(tracked.keySet());

			Collections.sort(serviceReferences);
			Collections.reverse(serviceReferences);

			for (ServiceReference<CryptoVaultProvider> serviceReference :
					serviceReferences) {

				CryptoVaultProvider provider = tracked.get(serviceReference);

				if (!provider.isAllowedCompany(companyId)) {
					continue;
				}

				try {
					return provider.wrap(
						companyId, masterKeyReference.getIdentifier(),
						keyToWrap);
				}
				catch (CryptoManagerException cryptoManagerException) {
				}
			}

			throw new CryptoManagerException(
				"No master key found for wrapping: " +
					masterKeyReference.getIdentifier());
		}

		CryptoVaultProvider cryptoVaultProvider = _getCryptoVaultProvider(
			companyId, providerId);

		return cryptoVaultProvider.wrap(
			companyId, masterKeyReference.getIdentifier(), keyToWrap);
	}

	@Activate
	protected void activate(BundleContext bundleContext) {
		_serviceTracker = new ServiceTracker<>(
			bundleContext, CryptoVaultProvider.class, null);

		_serviceTracker.open();
	}

	@Deactivate
	protected void deactivate() {
		_serviceTracker.close();
	}

	private CryptoVaultProvider _getCryptoVaultProvider(
			long companyId, String providerId)
		throws CryptoManagerException {

		Map<ServiceReference<CryptoVaultProvider>, CryptoVaultProvider>
			tracked = _serviceTracker.getTracked();

		for (Map.Entry<ServiceReference<CryptoVaultProvider>, CryptoVaultProvider>
				entry : tracked.entrySet()) {

			ServiceReference<CryptoVaultProvider> serviceReference =
				entry.getKey();

			String trackedProviderId = GetterUtil.getString(
				serviceReference.getProperty("providerId"));

			if (providerId.equals(trackedProviderId)) {
				CryptoVaultProvider provider = entry.getValue();

				if (provider.isAllowedCompany(companyId)) {
					return provider;
				}
			}
		}

		throw new CryptoManagerException(
			StringBundler.concat(
				"No crypto vault provider found for ID: ", providerId,
				" and company ID: ", companyId));
	}

	@Reference(
		cardinality = ReferenceCardinality.MULTIPLE,
		policy = ReferencePolicy.DYNAMIC, service = CryptoVaultProvider.class
	)
	protected void addCryptoVaultProvider(
		CryptoVaultProvider cryptoVaultProvider) {
	}

	protected void removeCryptoVaultProvider(
		CryptoVaultProvider cryptoVaultProvider) {
	}

	private ServiceTracker<CryptoVaultProvider, CryptoVaultProvider>
		_serviceTracker;

}