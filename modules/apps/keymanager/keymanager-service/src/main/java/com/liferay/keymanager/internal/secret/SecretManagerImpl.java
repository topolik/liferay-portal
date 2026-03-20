/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.keymanager.internal.secret;

import com.liferay.keymanager.KeyReference;
import com.liferay.keymanager.secret.SecretManager;
import com.liferay.keymanager.secret.SecretManagerException;
import com.liferay.keymanager.secret.SecureSecret;
import com.liferay.keymanager.spi.secret.SecretVaultReader;
import com.liferay.keymanager.spi.secret.SecretVaultWriter;
import com.liferay.osgi.service.tracker.collections.map.ServiceTrackerMap;
import com.liferay.osgi.service.tracker.collections.map.ServiceTrackerMapFactory;
import com.liferay.petra.string.StringBundler;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;

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
@Component(service = SecretManager.class)
public class SecretManagerImpl implements SecretManager {

	@Override
	public void deleteSecret(long companyId, KeyReference keyReference)
		throws SecretManagerException {

		Objects.requireNonNull(keyReference, "No KeyReference provided!");

		String providerId = keyReference.getProviderId();
		
		try {

			if (Objects.equals(providerId, KeyReference.ANY_PROVIDER)) {
				providerId = _getDefaultSecretVaultWriterProviderId(companyId);
			}

			SecretVaultWriter secretVaultWriter = _getSecretVaultWriter(
				companyId, providerId);

			secretVaultWriter.deleteSecret(companyId, keyReference.getIdentifier());
		}
		catch (SecretManagerException e) {
			if (_log.isWarnEnabled()) {
				_log.warn("Unable to delete secret: " + e.getMessage(), e);
			}

			throw e;
		}

	}

	@Override
	public List<String> getProviders(long companyId)
		throws SecretManagerException {
		
		try {
			return _getSecretVaultReaderProviderIds(
				companyId, KeyReference.ANY_PROVIDER);
		}
		catch (SecretManagerException e) {
			if (_log.isWarnEnabled()) {
				_log.warn("Unable to get providers: " + e.getMessage(), e);
			}

			throw e;
		}
	}

	@Override
	public SecureSecret getSecret(long companyId, KeyReference keyReference)
		throws SecretManagerException {

		Objects.requireNonNull(keyReference, "No KeyReference provided!");
		
		try {
			for (SecretVaultReader reader : _getSecretVaultReaders(
				companyId, keyReference.getProviderId())) {

				try {
					SecureSecret secureSecret = reader.getSecret(
						companyId, keyReference.getIdentifier());

					if (secureSecret != null) {
						return secureSecret;
					}
				}
				catch (SecretManagerException secretManagerException) {
					if (_log.isDebugEnabled()) {
						_log.debug(
							"Unable to fetch secret from reader",
							secretManagerException);
					}
				}
			}
		}
		catch (SecretManagerException e) {
			if (_log.isWarnEnabled()) {
				_log.warn("Unable to get secret: " + e.getMessage(), e);
			}
	
			throw e;
		}

		return null;
	}

	@Override
	public List<KeyReference> getSecretIdentifiers(
			long companyId, String providerId)
		throws SecretManagerException {

		List<KeyReference> keyReferences = new ArrayList<>();

		try {
			for (String trackedProviderId : _getSecretVaultReaderProviderIds(
				companyId, providerId)) {

				SecretVaultReader reader = _readerServiceTrackerMap.getService(
					trackedProviderId);

				if (reader == null) {
					continue;
				}

				List<String> identifiers = reader.getSecretIdentifiers(companyId);

				for (String identifier : identifiers) {
					keyReferences.add(
						new KeyReference(
							KeyReference.Type.SECRET, trackedProviderId,
							identifier));
				}
			}
		}
		catch (SecretManagerException e) {
			if (_log.isWarnEnabled()) {
				_log.warn(
					"Unable to get secret identifiers: " + e.getMessage(), e);
			}

			throw e;
		}

		return keyReferences;
	}

	@Override
	public KeyReference putSecret(long companyId, SecureSecret secureSecret)
		throws SecretManagerException {

		Objects.requireNonNull(secureSecret, "No SecureSecret provided!");
		
		KeyReference keyReference = secureSecret.getKeyReference();

		Objects.requireNonNull(keyReference, "No KeyReference provided!");
		
		String providerId = keyReference.getProviderId();
		
		try {
			if (Objects.equals(providerId, KeyReference.ANY_PROVIDER)) {
				providerId = _getDefaultSecretVaultWriterProviderId(companyId);
			}

			SecretVaultWriter secretVaultWriter = _getSecretVaultWriter(
				companyId, providerId);

			secretVaultWriter.putSecret(companyId, secureSecret);

			return new KeyReference(
				KeyReference.Type.SECRET, providerId, keyReference.getIdentifier());
		}
		catch (SecretManagerException e) {
			if (_log.isWarnEnabled()) {
				_log.warn("Unable to save secret: " + e.getMessage(), e);
			}
			
			throw e;
		}
	}

	@Activate
	protected void activate(BundleContext bundleContext) {
		_readerServiceTrackerMap = ServiceTrackerMapFactory.openSingleValueMap(
			bundleContext, SecretVaultReader.class, "providerId");

		_writerServiceTrackerMap = ServiceTrackerMapFactory.openSingleValueMap(
			bundleContext, SecretVaultWriter.class, "providerId");
	}

	@Deactivate
	protected void deactivate() {
		_readerServiceTrackerMap.close();
		_writerServiceTrackerMap.close();
	}

	private List<String> _getSecretVaultReaderProviderIds(
			long companyId, String providerId)
		throws SecretManagerException {

		if (Objects.equals(providerId, KeyReference.ANY_PROVIDER)) {
			List<String> providerIds = new ArrayList<>();

			for (String trackedProviderId : _readerServiceTrackerMap.keySet()) {
				SecretVaultReader reader = _readerServiceTrackerMap.getService(
					trackedProviderId);

				if (reader.isAllowedCompany(companyId)) {
					providerIds.add(trackedProviderId);
				}
			}

			return providerIds;
		}

		_getSecretVaultReader(companyId, providerId);

		return Collections.singletonList(providerId);
	}

	private SecretVaultReader _getSecretVaultReader(
			long companyId, String providerId)
		throws SecretManagerException {

		if (Objects.equals(providerId, KeyReference.ANY_PROVIDER)) {
			for (String trackedProviderId : _readerServiceTrackerMap.keySet()) {
				SecretVaultReader reader = _readerServiceTrackerMap.getService(
					trackedProviderId);

				if (reader.isAllowedCompany(companyId)) {
					return reader;
				}
			}
		}

		SecretVaultReader reader = _readerServiceTrackerMap.getService(
			providerId);

		if ((reader != null) && reader.isAllowedCompany(companyId)) {
			return reader;
		}

		throw new SecretManagerException(
			StringBundler.concat(
				"No secret vault reader found for ID: ", providerId,
				" and company ID: ", companyId));
	}

	private List<SecretVaultReader> _getSecretVaultReaders(
			long companyId, String providerId)
		throws SecretManagerException {

		List<SecretVaultReader> readers = new ArrayList<>();

		for (String id : _getSecretVaultReaderProviderIds(
				companyId, providerId)) {

			SecretVaultReader reader = _readerServiceTrackerMap.getService(id);

			if (reader != null) {
				readers.add(reader);
			}
		}

		return readers;
	}

	private SecretVaultWriter _getSecretVaultWriter(
			long companyId, String providerId)
		throws SecretManagerException {

		SecretVaultWriter writer = _writerServiceTrackerMap.getService(
			providerId);

		if (writer != null && writer.isAllowedCompany(companyId)) {
			return writer;
		}

		throw new SecretManagerException(
			StringBundler.concat(
				"No secret vault writer found for ID: ", providerId,
				" and company ID: ", companyId));
	}

	private String _getDefaultSecretVaultWriterProviderId(long companyId)
		throws SecretManagerException {

		for (String trackedProviderId : _writerServiceTrackerMap.keySet()) {
			SecretVaultWriter writer = _writerServiceTrackerMap.getService(
				trackedProviderId);

			if (writer.isAllowedCompany(companyId)) {
				return trackedProviderId;
			}
		}

		throw new SecretManagerException(
			StringBundler.concat(
				"No secret vault writer found for ANY provider",
				" and company ID: ", companyId));
	}

	private static final Log _log = LogFactoryUtil.getLog(
		SecretManagerImpl.class);

	private ServiceTrackerMap<String, SecretVaultReader>
		_readerServiceTrackerMap;
	private ServiceTrackerMap<String, SecretVaultWriter>
		_writerServiceTrackerMap;

}