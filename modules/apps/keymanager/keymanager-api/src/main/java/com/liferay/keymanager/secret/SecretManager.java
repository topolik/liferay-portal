/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.keymanager.secret;

import com.liferay.keymanager.KeyReference;

import java.util.List;

import org.osgi.annotation.versioning.ProviderType;

/**
 * @author Tomas Polesovsky
 */
@ProviderType
public interface SecretManager {

	public void deleteSecret(KeyReference keyReference)
		throws SecretManagerException;

	public List<String> getProviders() throws SecretManagerException;

	public SecureSecret getSecret(KeyReference keyReference)
		throws SecretManagerException;

	public List<KeyReference> getSecretIdentifiers(String providerId)
		throws SecretManagerException;

	public void putSecret(SecureSecret secureSecret)
		throws SecretManagerException;

}