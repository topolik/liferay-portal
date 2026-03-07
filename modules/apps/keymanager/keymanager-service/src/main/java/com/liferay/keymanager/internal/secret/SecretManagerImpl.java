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

		_getProvider(keyReference).deleteSecret(keyReference.getIdentifier());
	}

	@Override
	public SecureSecret getSecret(KeyReference keyReference)
		throws SecretManagerException {

		return _getProvider(keyReference).getSecret(keyReference.getIdentifier());
	}

	@Override
	public SecureSecret putSecret(SecureSecret secureSecret)
		throws SecretManagerException {

		return _getProvider(secureSecret.getKeyReference()).putSecret(
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

	private SecretVaultProvider _getProvider(KeyReference keyReference)
		throws SecretManagerException {

		if (keyReference.getType() != KeyReference.Type.SECRET) {
			throw new SecretManagerException(
				"Reference is not of type SECRET: " +
					keyReference.getRawReference());
		}

		String providerId = keyReference.getProviderId();

		SecretVaultProvider secretVaultProvider =
			_serviceTrackerMap.getService(providerId);

		if (secretVaultProvider == null) {
			throw new SecretManagerException(
				"Secret provider not found: " + providerId);
		}

		return secretVaultProvider;
	}

	private ServiceTrackerMap<String, SecretVaultProvider> _serviceTrackerMap;

}
