/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.keymanager;

import org.osgi.annotation.versioning.ProviderType;

import java.util.List;
import java.util.Optional;

/**
 * Registry for managing and retrieving key providers.
 *
 * @author Tomas Polesovsky
 */
@ProviderType
public interface KeyProviderRegistry {

	public List<KeyProvider> getAllProviders();

	public List<KeyProvider> getAvailableProviders();

	public Optional<KeyProvider> getProvider(String providerId);

	public void registerProvider(KeyProvider provider);

	public void unregisterProvider(String providerId);

}