/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.keymanager.internal.secret;

import com.liferay.keymanager.KeyReference;
import com.liferay.keymanager.secret.SecretManager;
import com.liferay.keymanager.secret.SecretManagerException;
import com.liferay.keymanager.secret.SecureSecret;
import com.liferay.keymanager.spi.secret.SecretVaultProvider;
import com.liferay.petra.string.StringBundler;
import com.liferay.portal.kernel.util.GetterUtil;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

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

		SecretVaultProvider secretVaultProvider = _getSecretVaultProvider(
			companyId, keyReference.getProviderId());

		secretVaultProvider.deleteSecret(companyId, keyReference.getIdentifier());
	}

	@Override
	public List<String> getProviders(long companyId)
		throws SecretManagerException {

		List<String> providerIds = new ArrayList<>();

		Map<ServiceReference<SecretVaultProvider>, SecretVaultProvider>
			tracked = _serviceTracker.getTracked();

		for (Map.Entry<ServiceReference<SecretVaultProvider>, SecretVaultProvider>
				entry : tracked.entrySet()) {

			ServiceReference<SecretVaultProvider> serviceReference =
				entry.getKey();
			SecretVaultProvider provider = entry.getValue();

			if (provider.isAllowedCompany(companyId)) {
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

		SecretVaultProvider secretVaultProvider = _getSecretVaultProvider(
			companyId, keyReference.getProviderId());

		return secretVaultProvider.getSecret(
			companyId, keyReference.getIdentifier());
	}

	@Override
	public List<KeyReference> getSecretIdentifiers(
			long companyId, String providerId)
		throws SecretManagerException {

		SecretVaultProvider secretVaultProvider = _getSecretVaultProvider(
			companyId, providerId);

		List<String> identifiers = secretVaultProvider.getSecretIdentifiers(
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

		SecretVaultProvider secretVaultProvider = _getSecretVaultProvider(
			companyId, keyReference.getProviderId());

		secretVaultProvider.putSecret(companyId, secureSecret);
	}

	@Activate
	protected void activate(BundleContext bundleContext) {
		_serviceTracker = new ServiceTracker<>(
			bundleContext, SecretVaultProvider.class, null);

		_serviceTracker.open();
	}

	@Deactivate
	protected void deactivate() {
		_serviceTracker.close();
	}

	private SecretVaultProvider _getSecretVaultProvider(
			long companyId, String providerId)
		throws SecretManagerException {

		Map<ServiceReference<SecretVaultProvider>, SecretVaultProvider>
			tracked = _serviceTracker.getTracked();

		for (Map.Entry<ServiceReference<SecretVaultProvider>, SecretVaultProvider>
				entry : tracked.entrySet()) {

			ServiceReference<SecretVaultProvider> serviceReference =
				entry.getKey();

			String trackedProviderId = GetterUtil.getString(
				serviceReference.getProperty("providerId"));

			if (providerId.equals(trackedProviderId)) {
				SecretVaultProvider provider = entry.getValue();

				if (provider.isAllowedCompany(companyId)) {
					return provider;
				}
			}
		}

		throw new SecretManagerException(
			StringBundler.concat(
				"No secret vault provider found for ID: ", providerId,
				" and company ID: ", companyId));
	}

	@Reference(
		cardinality = ReferenceCardinality.MULTIPLE,
		policy = ReferencePolicy.DYNAMIC, service = SecretVaultProvider.class
	)
	protected void addSecretVaultProvider(
		SecretVaultProvider secretVaultProvider) {
	}

	protected void removeSecretVaultProvider(
		SecretVaultProvider secretVaultProvider) {
	}

	private ServiceTracker<SecretVaultProvider, SecretVaultProvider>
		_serviceTracker;

}