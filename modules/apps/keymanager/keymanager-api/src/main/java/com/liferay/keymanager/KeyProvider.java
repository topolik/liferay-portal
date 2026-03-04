/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.keymanager;

import com.liferay.keymanager.exception.KeyProviderException;

import java.util.List;

/**
 * SPI interface for key storage backends. Each provider (KeyStore, GCP KMS, etc.)
 * implements this interface and registers as an OSGi service.
 *
 * @author Tomas Polesovsky
 */
public interface KeyProvider {

	public boolean containsKey(String alias) throws KeyProviderException;

	public void deleteKey(String alias) throws KeyProviderException;

	public String getDisplayName();

	public KeyMetadata getKeyMetadata(String alias) throws KeyProviderException;

	default public int getPriority() {
		return 100;
	}

	public String getProviderId();

	public boolean isAvailable();

	public List<String> listAliases() throws KeyProviderException;

	public char[] resolveKey(String alias) throws KeyProviderException;

	public byte[] resolveKeyBytes(String alias) throws KeyProviderException;

	public void storeKey(String alias, char[] value)
		throws KeyProviderException;

}