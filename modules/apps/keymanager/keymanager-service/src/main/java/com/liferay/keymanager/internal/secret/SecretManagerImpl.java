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
import com.liferay.osgi.service.tracker.collections.map.ServiceTrackerMap;
import com.liferay.osgi.service.tracker.collections.map.ServiceTrackerMapFactory;

import java.util.List;

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
	public void deleteSecret(KeyReference keyReference)
		throws SecretManagerException {

		_getSecretVaultProvider(keyReference).deleteSecret(
			keyReference.getIdentifier());
	}

	@Override
	public SecureSecret getSecret(KeyReference keyReference)
		throws SecretManagerException {

		return _getSecretVaultProvider(keyReference).getSecret(
			keyReference.getIdentifier());
	}

	@Override
	public List<KeyReference> getSecretIdentifiers(String providerId)
		throws SecretManagerException {

		return _getSecretVaultProvider(providerId).getSecretIdentifiers();
	}

	@Override
	public void putSecret(SecureSecret secureSecret)
		throws SecretManagerException {

		_getSecretVaultProvider(secureSecret.getKeyReference()).putSecret(
			secureSecret);
	}

	@Activate
	protected void activate(BundleContext bundleContext) {
		_serviceTrackerMap = ServiceTrackerMapFactory.openSingleValueMap(
			bundleContext, SecretVaultProvider.class, "providerId");
	}

	@Deactivate
	protected void deactivate() {
		_serviceTrackerMap.close();
	}

	private SecretVaultProvider _getSecretVaultProvider(
			KeyReference keyReference)
		throws SecretManagerException {

		return _getSecretVaultProvider(keyReference.getProviderId());
	}

	private SecretVaultProvider _getSecretVaultProvider(String providerId)
		throws SecretManagerException {

		SecretVaultProvider secretVaultProvider = _serviceTrackerMap.getService(
			providerId);

		if (secretVaultProvider == null) {
			throw new SecretManagerException(
				"No secret vault provider found for ID: " + providerId);
		}

		return secretVaultProvider;
	}

	private ServiceTrackerMap<String, SecretVaultProvider> _serviceTrackerMap;

}
