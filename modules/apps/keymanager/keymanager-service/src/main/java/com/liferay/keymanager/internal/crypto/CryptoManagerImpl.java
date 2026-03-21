/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.keymanager.internal.crypto;

import com.liferay.keymanager.KeyReference;
import com.liferay.keymanager.crypto.CryptoKey;
import com.liferay.keymanager.crypto.CryptoKey;
import com.liferay.keymanager.crypto.CryptoManager;
import com.liferay.keymanager.crypto.CryptoManagerException;
import com.liferay.keymanager.spi.crypto.CryptoVaultProvider;
import java.util.ArrayList;
import com.liferay.osgi.service.tracker.collections.map.PropertyServiceReferenceComparator;
import com.liferay.osgi.service.tracker.collections.map.PropertyServiceReferenceMapper;
import com.liferay.osgi.service.tracker.collections.map.ServiceTrackerMap;
import com.liferay.osgi.service.tracker.collections.map.ServiceTrackerMapFactory;
import com.liferay.petra.string.StringBundler;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;

import java.security.Key;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Objects;
import java.util.TreeMap;

import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceReference;
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

		Objects.requireNonNull(keyReference, "No KeyReference provided!");
		Objects.requireNonNull(ciphertext, "No ciphertext provided!");
		
		try {
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
		}
		catch (CryptoManagerException e) {
			if (_log.isWarnEnabled()) {
				_log.warn("Unable to decrypt cipherText: " + e.getMessage(), e);
			}

			throw e;
		}

		throw new CryptoManagerException(
			"No key found for decryption: " + keyReference.getIdentifier());
	}

	@Override
	public void deleteKey(long companyId, KeyReference keyReference)
		throws CryptoManagerException {

		Objects.requireNonNull(keyReference, "No KeyReference provided!");
		
		try {
			CryptoVaultProvider cryptoVaultProvider = _getCryptoVaultProvider(
				companyId, keyReference.getProviderId());

			cryptoVaultProvider.deleteKey(companyId, keyReference.getIdentifier());
		}
		catch (CryptoManagerException e) {
			if (_log.isWarnEnabled()) {
				_log.warn("Unable to delete key: " + e.getMessage(), e);
			}

			throw e;
		}
	}

	@Override
	public byte[] encrypt(
			long companyId, KeyReference keyReference, byte[] plaintext)
		throws CryptoManagerException {

		Objects.requireNonNull(keyReference, "No KeyReference provided!");
		Objects.requireNonNull(plaintext, "No plaintext provided!");

		try {
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
		}
		catch (CryptoManagerException e) {
			if (_log.isWarnEnabled()) {
				_log.warn("Unable to encrypt cipherText: " + e.getMessage(), e);
			}

			throw e;
		}

		throw new CryptoManagerException(
			"No key found for encryption: " + keyReference.getIdentifier());
	}

	@Override
	public KeyReference generateAsymmetricKeyPair(
			long companyId, String providerId, String identifier,
			String algorithmSpec)
		throws CryptoManagerException {

		try {
			String resolvedProviderId = _getCryptoVaultProviderId(
				companyId, providerId);

			CryptoVaultProvider cryptoVaultProvider = _getCryptoVaultProvider(
				companyId, resolvedProviderId);

			String finalIdentifier = cryptoVaultProvider.generateAsymmetricKeyPair(
				companyId, identifier, algorithmSpec);

			return new KeyReference(
				KeyReference.Type.CRYPTO, resolvedProviderId, finalIdentifier);
		}
		catch (CryptoManagerException e) {
			if (_log.isWarnEnabled()) {
				_log.warn("Unable to get key pair: " + e.getMessage(), e);
			}

			throw e;
		}
		
	}

	@Override
	public KeyReference generateSecretKey(
			long companyId, String providerId, String identifier,
			String algorithmSpec)
		throws CryptoManagerException {

		try {
			String resolvedProviderId = _getCryptoVaultProviderId(
				companyId, providerId);

			CryptoVaultProvider cryptoVaultProvider = _getCryptoVaultProvider(
				companyId, resolvedProviderId);

			String finalIdentifier = cryptoVaultProvider.generateSecretKey(
				companyId, identifier, algorithmSpec);

			return new KeyReference(
				KeyReference.Type.CRYPTO, resolvedProviderId, finalIdentifier);
		}
		catch (CryptoManagerException e) {
			if (_log.isWarnEnabled()) {
				_log.warn("Unable to generate secret key: " + e.getMessage(), e);
			}

			throw e;
		}
	}

	@Override
	public List<KeyReference> getKeyIdentifiers(
			long companyId, String providerId)
		throws CryptoManagerException {

		List<KeyReference> keyReferences = new ArrayList<>();

		try {
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
						new KeyReference(
							KeyReference.Type.CRYPTO, trackedProviderId,
							identifier));
				}
			}
		}
		catch (CryptoManagerException e) {
			if (_log.isWarnEnabled()) {
				_log.warn("Unable to get key identifiers: " + e.getMessage(), e);
			}

			throw e;
		}

		return keyReferences;
	}

	@Override
	public CryptoKey getKeyMetadata(
			long companyId, KeyReference keyReference)
		throws CryptoManagerException {

		Objects.requireNonNull(keyReference, "No KeyReference provided!");

		try {
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
		}
		catch (CryptoManagerException e) {
			if (_log.isWarnEnabled()) {
				_log.warn("Unable to get key metadata: " + e.getMessage(), e);
			}

			throw e;
		}

		throw new CryptoManagerException(
			"No key metadata found for identifier: " +
				keyReference.getIdentifier());
	}

	@Override
	public List<String> getProviders(long companyId)
		throws CryptoManagerException {
		
		try {
			return _getCryptoVaultProviderIds(companyId, KeyReference.ANY_PROVIDER);
		}
		catch (CryptoManagerException e) {
			if (_log.isWarnEnabled()) {
				_log.warn("Unable to get providers: " + e.getMessage(), e);
			}

			throw e;
		}
	}

	@Override
	public KeyReference importSecretKey(
			long companyId, String providerId, String identifier,
			byte[] rawKeyMaterial, String algorithmSpec)
		throws CryptoManagerException {

		Objects.requireNonNull(providerId, "No providerId provided!");
		Objects.requireNonNull(identifier, "No identifier provided!");
		Objects.requireNonNull(rawKeyMaterial, "No rawKeyMaterial provided!");
		Objects.requireNonNull(algorithmSpec, "No algorithmSpec provided!");

		try {
			String resolvedProviderId = _getCryptoVaultProviderId(
				companyId, providerId);

			CryptoVaultProvider cryptoVaultProvider = _getCryptoVaultProvider(
				companyId, resolvedProviderId);

			String finalIdentifier = cryptoVaultProvider.importSecretKey(
				companyId, identifier, rawKeyMaterial, algorithmSpec);

			return new KeyReference(
				KeyReference.Type.CRYPTO, resolvedProviderId, finalIdentifier);
		}
		catch (CryptoManagerException e) {
			if (_log.isWarnEnabled()) {
				_log.warn("Unable to import secret key: " + e.getMessage(), e);
			}

			throw e;
		}
		
	}

	@Override
	public Key unwrap(
			long companyId, KeyReference masterKeyReference,
			byte[] wrappedKeyBytes, String wrappedKeyAlgorithm,
			int wrappedKeyCipherType)
		throws CryptoManagerException {

		Objects.requireNonNull(masterKeyReference, "No KeyReference provided!");
		Objects.requireNonNull(wrappedKeyBytes, "No wrappedKeyBytes provided!");
		Objects.requireNonNull(wrappedKeyAlgorithm, "No wrappedKeyAlgorithm provided!");

		try {
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
		}
		catch (CryptoManagerException e) {
			if (_log.isWarnEnabled()) {
				_log.warn("Unable to unwrap key: " + e.getMessage(), e);
			}

			throw e;
		}

		throw new CryptoManagerException(
			"No master key found for unwrapping: " +
				masterKeyReference.getIdentifier());
	}

	@Override
	public byte[] wrap(
			long companyId, KeyReference masterKeyReference, Key keyToWrap)
		throws CryptoManagerException {

		Objects.requireNonNull(masterKeyReference, "No KeyReference provided!");
		Objects.requireNonNull(keyToWrap, "No keyToWrap provided!");
		
		try {
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
		}
		catch (CryptoManagerException e) {
			if (_log.isWarnEnabled()) {
				_log.warn("Unable to wrap key: " + e.getMessage(), e);
			}

			throw e;
		}

		throw new CryptoManagerException(
			"No master key found for wrapping: " +
				masterKeyReference.getIdentifier());
	}

	@Activate
	protected void activate(BundleContext bundleContext) {
		_serviceTrackerMap = ServiceTrackerMapFactory.openSingleValueMap(
			bundleContext, CryptoVaultProvider.class, "(providerId=*)",
			new PropertyServiceReferenceMapper<>("providerId"),
			new PropertyServiceReferenceComparator<>("priority"));
	}

	@Deactivate
	protected void deactivate() {
		_serviceTrackerMap.close();
	}

	private Collection<String> _getProviderIds() {
		TreeMap<Integer, List<String>> providersTreeMap = new TreeMap<>(
			Collections.reverseOrder());

		for (String providerId : _serviceTrackerMap.keySet()) {
			CryptoVaultProvider cryptoVaultProvider =
				_serviceTrackerMap.getService(providerId);

			if (cryptoVaultProvider == null) {
				continue;
			}

			int priority = cryptoVaultProvider.getPriority();

			List<String> providerIds = providersTreeMap.computeIfAbsent(
				priority, k -> new ArrayList<>());

			providerIds.add(providerId);
		}

		List<String> sortedProviderIds = new ArrayList<>();

		for (List<String> providerIds : providersTreeMap.values()) {
			sortedProviderIds.addAll(providerIds);
		}

		return sortedProviderIds;
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
			for (String trackedProviderId : _getProviderIds()) {
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

			for (String trackedProviderId : _getProviderIds()) {
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