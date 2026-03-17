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
import com.liferay.petra.string.StringBundler;
import com.liferay.portal.kernel.util.GetterUtil;

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
@Component(service = SecretManager.class)
public class SecretManagerImpl implements SecretManager {

	@Override
	public void deleteSecret(long companyId, KeyReference keyReference)
		throws SecretManagerException {

		SecretVaultWriter secretVaultWriter = _getSecretVaultWriter(
			companyId, keyReference.getProviderId());

		secretVaultWriter.deleteSecret(companyId, keyReference.getIdentifier());
	}

	@Override
	public List<String> getProviders(long companyId)
		throws SecretManagerException {

		List<String> providerIds = new ArrayList<>();

		Map<ServiceReference<SecretVaultReader>, SecretVaultReader>
			tracked = _readerServiceTracker.getTracked();

		for (Map.Entry<ServiceReference<SecretVaultReader>, SecretVaultReader>
				entry : tracked.entrySet()) {

			ServiceReference<SecretVaultReader> serviceReference =
				entry.getKey();
			SecretVaultReader reader = entry.getValue();

			if (reader.isAllowedCompany(companyId)) {
				providerIds.add(
					GetterUtil.getString(
						serviceReference.getProperty("providerId")));
			}
		}

		return providerIds;
	}

	@Override
	public SecureSecret getSecret(long companyId, KeyReference keyReference)
		throws SecretManagerException {

		String providerId = keyReference.getProviderId();

		if (Objects.equals(providerId, KeyReference.ANY_PROVIDER)) {
			Map<ServiceReference<SecretVaultReader>, SecretVaultReader>
				tracked = _readerServiceTracker.getTracked();

			List<ServiceReference<SecretVaultReader>> serviceReferences =
				new ArrayList<>(tracked.keySet());

			Collections.sort(serviceReferences);
			Collections.reverse(serviceReferences);

			for (ServiceReference<SecretVaultReader> serviceReference :
					serviceReferences) {

				SecretVaultReader reader = tracked.get(serviceReference);

				if (!reader.isAllowedCompany(companyId)) {
					continue;
				}

				try {
					return reader.getSecret(
						companyId, keyReference.getIdentifier());
				}
				catch (SecretManagerException secretManagerException) {
				}
			}

			throw new SecretManagerException(
				"No secret found for identifier: " +
					keyReference.getIdentifier());
		}

		SecretVaultReader secretVaultReader = _getSecretVaultReader(
			companyId, providerId);

		return secretVaultReader.getSecret(
			companyId, keyReference.getIdentifier());
	}

	@Override
	public List<KeyReference> getSecretIdentifiers(
			long companyId, String providerId)
		throws SecretManagerException {

		SecretVaultReader secretVaultReader = _getSecretVaultReader(
			companyId, providerId);

		List<String> identifiers = secretVaultReader.getSecretIdentifiers(
			companyId);

		List<KeyReference> keyReferences = new ArrayList<>(identifiers.size());

		for (String identifier : identifiers) {
			keyReferences.add(
				new KeyReference(
					KeyReference.Type.SECRET, providerId, identifier));
		}

		return keyReferences;
	}

	@Override
	public void putSecret(long companyId, SecureSecret secureSecret)
		throws SecretManagerException {

		KeyReference keyReference = secureSecret.getKeyReference();

		SecretVaultWriter secretVaultWriter = _getSecretVaultWriter(
			companyId, keyReference.getProviderId());

		secretVaultWriter.putSecret(companyId, secureSecret);
	}

	@Activate
	protected void activate(BundleContext bundleContext) {
		_readerServiceTracker = new ServiceTracker<>(
			bundleContext, SecretVaultReader.class, null);

		_readerServiceTracker.open();

		_writerServiceTracker = new ServiceTracker<>(
			bundleContext, SecretVaultWriter.class, null);

		_writerServiceTracker.open();
	}

	@Deactivate
	protected void deactivate() {
		_readerServiceTracker.close();
		_writerServiceTracker.close();
	}

	private SecretVaultReader _getSecretVaultReader(
			long companyId, String providerId)
		throws SecretManagerException {

		Map<ServiceReference<SecretVaultReader>, SecretVaultReader>
			tracked = _readerServiceTracker.getTracked();

		for (Map.Entry<ServiceReference<SecretVaultReader>, SecretVaultReader>
				entry : tracked.entrySet()) {

			ServiceReference<SecretVaultReader> serviceReference =
				entry.getKey();

			String trackedProviderId = GetterUtil.getString(
				serviceReference.getProperty("providerId"));

			if (providerId.equals(trackedProviderId)) {
				SecretVaultReader reader = entry.getValue();

				if (reader.isAllowedCompany(companyId)) {
					return reader;
				}
			}
		}

		throw new SecretManagerException(
			StringBundler.concat(
				"No secret vault reader found for ID: ", providerId,
				" and company ID: ", companyId));
	}

	private SecretVaultWriter _getSecretVaultWriter(
			long companyId, String providerId)
		throws SecretManagerException {

		Map<ServiceReference<SecretVaultWriter>, SecretVaultWriter>
			tracked = _writerServiceTracker.getTracked();

		for (Map.Entry<ServiceReference<SecretVaultWriter>, SecretVaultWriter>
				entry : tracked.entrySet()) {

			ServiceReference<SecretVaultWriter> serviceReference =
				entry.getKey();

			String trackedProviderId = GetterUtil.getString(
				serviceReference.getProperty("providerId"));

			if (providerId.equals(trackedProviderId)) {
				SecretVaultWriter writer = entry.getValue();

				if (writer instanceof SecretVaultReader) {
					SecretVaultReader reader = (SecretVaultReader)writer;

					if (reader.isAllowedCompany(companyId)) {
						return writer;
					}
				}
				else {
					// Fallback if writer doesn't implement reader (unlikely)
					return writer;
				}
			}
		}

		throw new SecretManagerException(
			StringBundler.concat(
				"No secret vault writer found for ID: ", providerId,
				" and company ID: ", companyId));
	}

	@Reference(
		cardinality = ReferenceCardinality.MULTIPLE,
		policy = ReferencePolicy.DYNAMIC, service = SecretVaultReader.class
	)
	protected void addSecretVaultReader(SecretVaultReader secretVaultReader) {
	}

	protected void removeSecretVaultReader(SecretVaultReader secretVaultReader) {
	}

	@Reference(
		cardinality = ReferenceCardinality.MULTIPLE,
		policy = ReferencePolicy.DYNAMIC, service = SecretVaultWriter.class
	)
	protected void addSecretVaultWriter(SecretVaultWriter secretVaultWriter) {
	}

	protected void removeSecretVaultWriter(SecretVaultWriter secretVaultWriter) {
	}

	private ServiceTracker<SecretVaultReader, SecretVaultReader>
		_readerServiceTracker;
	private ServiceTracker<SecretVaultWriter, SecretVaultWriter>
		_writerServiceTracker;

}