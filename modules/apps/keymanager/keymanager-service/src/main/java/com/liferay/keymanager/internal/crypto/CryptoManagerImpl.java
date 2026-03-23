/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.keymanager.internal.crypto;

import com.liferay.keymanager.KeyReference;
import com.liferay.keymanager.crypto.CryptoKey;
import com.liferay.keymanager.crypto.CryptoManager;
import com.liferay.keymanager.crypto.CryptoManagerException;
import com.liferay.keymanager.internal.fips.FipsComplianceChecker;
import com.liferay.keymanager.internal.profile.ProfileOrchestrator;
import com.liferay.keymanager.spi.crypto.CryptoVaultProvider;
import com.liferay.keymanager.spi.profile.KeyManagerProfile;
import com.liferay.osgi.service.tracker.collections.map.PropertyServiceReferenceMapper;
import com.liferay.osgi.service.tracker.collections.map.ServiceTrackerMap;
import com.liferay.osgi.service.tracker.collections.map.ServiceTrackerMapFactory;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.util.StringBundler;

import java.security.Key;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

import org.osgi.framework.BundleContext;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Deactivate;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Tomas Polesovsky
 */
@Component(service = CryptoManager.class)
public class CryptoManagerImpl implements CryptoManager {

	@Override
	public byte[] decrypt(
			long companyId, KeyReference keyReference, byte[] ciphertext)
		throws CryptoManagerException {

		_fipsComplianceChecker.check();

		Objects.requireNonNull(keyReference, "No KeyReference provided!");
		Objects.requireNonNull(ciphertext, "No ciphertext provided!");

		try {
			for (CryptoVaultProvider provider : _getCryptoVaultProviders(
				companyId, keyReference.getProviderId(), ProviderRole.DEK)) {

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
				companyId, keyReference.getProviderId(), ProviderRole.DEK);

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

		_fipsComplianceChecker.check();

		Objects.requireNonNull(keyReference, "No KeyReference provided!");
		Objects.requireNonNull(plaintext, "No plaintext provided!");

		try {
			for (CryptoVaultProvider provider : _getCryptoVaultProviders(
				companyId, keyReference.getProviderId(), ProviderRole.DEK)) {

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

		_fipsComplianceChecker.check();

		try {
			String resolvedProviderId = _getCryptoVaultProviderId(
				companyId, providerId, ProviderRole.DEK);

			CryptoVaultProvider cryptoVaultProvider = _getCryptoVaultProvider(
				companyId, resolvedProviderId, ProviderRole.DEK);

			String resultIdentifier =
				cryptoVaultProvider.generateAsymmetricKeyPair(
					companyId, identifier, algorithmSpec);

			return KeyReference.fromString(
				StringBundler.concat(
					"${keyRef:", resolvedProviderId, ":", resultIdentifier,
					"}"));
		}
		catch (CryptoManagerException e) {
			if (_log.isWarnEnabled()) {
				_log.warn(
					"Unable to generate asymmetric key pair: " + e.getMessage(),
					e);
			}

			throw e;
		}
	}

	@Override
	public KeyReference generateSecretKey(
			long companyId, String providerId, String identifier,
			String algorithmSpec)
		throws CryptoManagerException {

		_fipsComplianceChecker.check();

		try {
			String resolvedProviderId = _getCryptoVaultProviderId(
				companyId, providerId, ProviderRole.DEK);

			CryptoVaultProvider cryptoVaultProvider = _getCryptoVaultProvider(
				companyId, resolvedProviderId, ProviderRole.DEK);

			String resultIdentifier = cryptoVaultProvider.generateSecretKey(
				companyId, identifier, algorithmSpec);

			return KeyReference.fromString(
				StringBundler.concat(
					"${keyRef:", resolvedProviderId, ":", resultIdentifier,
					"}"));
		}
		catch (CryptoManagerException e) {
			if (_log.isWarnEnabled()) {
				_log.warn(
					"Unable to generate secret key: " + e.getMessage(), e);
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
			for (String resolvedProviderId : _getCryptoVaultProviderIds(
					companyId, providerId, ProviderRole.DEK)) {

				CryptoVaultProvider provider = _serviceTrackerMap.getService(
					resolvedProviderId);

				if (provider != null) {
					List<String> identifiers = provider.getKeyIdentifiers(
						companyId);

					for (String identifier : identifiers) {
						keyReferences.add(
							KeyReference.fromString(
								StringBundler.concat(
									"${keyRef:", resolvedProviderId, ":",
									identifier, "}")));
					}
				}
			}
		}
		catch (CryptoManagerException e) {
			if (_log.isWarnEnabled()) {
				_log.warn(
					"Unable to list key identifiers: " + e.getMessage(), e);
			}

			throw e;
		}

		return keyReferences;
	}

	@Override
	public CryptoKey getKeyMetadata(long companyId, KeyReference keyReference)
		throws CryptoManagerException {

		Objects.requireNonNull(keyReference, "No KeyReference provided!");

		try {
			for (CryptoVaultProvider provider : _getCryptoVaultProviders(
				companyId, keyReference.getProviderId(), ProviderRole.DEK)) {

				try {
					return provider.getKeyMetadata(
						companyId, keyReference.getIdentifier());
				}
				catch (CryptoManagerException cryptoManagerException) {
					if (_log.isDebugEnabled()) {
						_log.debug(
							"Unable to get key metadata with provider",
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
			"No key found for metadata: " + keyReference.getIdentifier());
	}

	@Override
	public List<String> getProviders(long companyId) {
		return new ArrayList<>(_getProviderIds());
	}

	@Override
	public KeyReference importSecretKey(
			long companyId, String providerId, String identifier,
			byte[] rawKeyMaterial, String algorithmSpec)
		throws CryptoManagerException {

		try {
			String resolvedProviderId = _getCryptoVaultProviderId(
				companyId, providerId, ProviderRole.DEK);

			CryptoVaultProvider cryptoVaultProvider = _getCryptoVaultProvider(
				companyId, resolvedProviderId, ProviderRole.DEK);

			String resultIdentifier = cryptoVaultProvider.importSecretKey(
				companyId, identifier, rawKeyMaterial, algorithmSpec);

			return KeyReference.fromString(
				StringBundler.concat(
					"${keyRef:", resolvedProviderId, ":", resultIdentifier,
					"}"));
		}
		catch (CryptoManagerException e) {
			if (_log.isWarnEnabled()) {
				_log.warn("Unable to import secret key: " + e.getMessage(), e);
			}

			throw e;
		}
		finally {
			if (rawKeyMaterial != null) {
				java.util.Arrays.fill(rawKeyMaterial, (byte)0);
			}
		}
	}

	@Override
	public Key unwrap(
			long companyId, KeyReference masterKeyReference,
			byte[] wrappedKeyBytes, String wrappedKeyAlgorithm,
			int wrappedKeyCipherType)
		throws CryptoManagerException {

		Objects.requireNonNull(
			masterKeyReference, "No master KeyReference provided!");
		Objects.requireNonNull(wrappedKeyBytes, "No wrappedKeyBytes provided!");

		try {
			for (CryptoVaultProvider provider : _getCryptoVaultProviders(
				companyId, masterKeyReference.getProviderId(),
				ProviderRole.KEK)) {

				try {
					return provider.unwrap(
						companyId, masterKeyReference.getIdentifier(),
						wrappedKeyBytes, wrappedKeyAlgorithm,
						wrappedKeyCipherType);
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

		Objects.requireNonNull(
			masterKeyReference, "No master KeyReference provided!");
		Objects.requireNonNull(keyToWrap, "No keyToWrap provided!");

		try {
			for (CryptoVaultProvider provider : _getCryptoVaultProviders(
				companyId, masterKeyReference.getProviderId(),
				ProviderRole.KEK)) {

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
			bundleContext, CryptoVaultProvider.class,
			"(keymanager.provider.id=*)",
			new PropertyServiceReferenceMapper<>("keymanager.provider.id"));
	}

	@Deactivate
	protected void deactivate() {
		_serviceTrackerMap.close();
	}

	private Collection<String> _getProviderIds() {
		return _serviceTrackerMap.keySet();
	}

	private CryptoVaultProvider _getCryptoVaultProvider(
			long companyId, String providerId, ProviderRole providerRole)
		throws CryptoManagerException {

		String resolvedProviderId = _getCryptoVaultProviderId(
			companyId, providerId, providerRole);

		CryptoVaultProvider provider = _serviceTrackerMap.getService(
			resolvedProviderId);

		if ((provider != null) && provider.isAllowedCompany(companyId)) {
			return provider;
		}

		throw new CryptoManagerException(
			StringBundler.concat(
				"No crypto vault provider found for ID: ", resolvedProviderId,
				" and company ID: ", String.valueOf(companyId)));
	}

	private String _getCryptoVaultProviderId(
			long companyId, String providerId, ProviderRole providerRole)
		throws CryptoManagerException {

		if (Objects.equals(providerId, KeyReference.ANY_PROVIDER)) {
			KeyManagerProfile activeProfile =
				_profileOrchestrator.getActiveProfile();

			if (activeProfile != null) {
				if (companyId == 0L) {
					if (providerRole == ProviderRole.KEK) {
						return activeProfile.getSystemKekProviderId();
					}

					return activeProfile.getSystemDekProviderId();
				}

				if (providerRole == ProviderRole.KEK) {
					return activeProfile.getCompanyKekProviderId();
				}

				return activeProfile.getCompanyDekProviderId();
			}

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
					" and company ID: ", String.valueOf(companyId)));
		}

		return providerId;
	}

	private List<String> _getCryptoVaultProviderIds(
			long companyId, String providerId, ProviderRole providerRole)
		throws CryptoManagerException {

		if (Objects.equals(providerId, KeyReference.ANY_PROVIDER)) {
			return Collections.singletonList(
				_getCryptoVaultProviderId(companyId, providerId, providerRole));
		}

		return Collections.singletonList(providerId);
	}

	private List<CryptoVaultProvider> _getCryptoVaultProviders(
			long companyId, String providerId, ProviderRole providerRole)
		throws CryptoManagerException {

		List<CryptoVaultProvider> providers = new ArrayList<>();

		for (String id : _getCryptoVaultProviderIds(
				companyId, providerId, providerRole)) {

			CryptoVaultProvider provider = _serviceTrackerMap.getService(id);

			if ((provider != null) && provider.isAllowedCompany(companyId)) {
				providers.add(provider);
			}
			else if (!Objects.equals(providerId, KeyReference.ANY_PROVIDER)) {
				throw new CryptoManagerException(
					StringBundler.concat(
						"No crypto vault provider found for ID: ", id,
						" and company ID: ", String.valueOf(companyId)));
			}
		}

		if (providers.isEmpty() &&
			Objects.equals(providerId, KeyReference.ANY_PROVIDER)) {

			throw new CryptoManagerException(
				StringBundler.concat(
					"No crypto vault provider found for ANY provider",
					" and company ID: ", String.valueOf(companyId)));
		}

		return providers;
	}

	private enum ProviderRole {

		DEK, KEK

	}

	private static final Log _log = LogFactoryUtil.getLog(
		CryptoManagerImpl.class);

	@Reference
	private FipsComplianceChecker _fipsComplianceChecker;

	@Reference
	private ProfileOrchestrator _profileOrchestrator;

	private ServiceTrackerMap<String, CryptoVaultProvider> _serviceTrackerMap;

}