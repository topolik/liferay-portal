/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.keymanager;

import org.osgi.annotation.versioning.ProviderType;

import com.liferay.keymanager.exception.KeyResolutionException;

import java.util.List;
import java.util.Map;

/**
 * Service responsible for resolving key references into their actual values.
 *
 * @author Tomas Polesovsky
 */
@ProviderType
public interface KeyResolverService {

	public String createReference(String providerId, String alias);

	public List<KeyProvider> getAvailableProviders();

	public void invalidateAllCaches();

	public void invalidateCache(String referenceString);

	public boolean isKeyReference(String value);

	public KeyReference parseReference(String referenceString)
		throws KeyResolutionException;

	public String resolve(String value) throws KeyResolutionException;

	public Map<String, String> resolveAll(Map<String, String> properties)
		throws KeyResolutionException;

	public char[] resolveSecure(KeyReference reference)
		throws KeyResolutionException;

	public String storeAndReference(
			String providerId, String alias, char[] value)
		throws KeyResolutionException;

}