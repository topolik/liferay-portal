/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.keymanager.spi;

import com.liferay.keymanager.SecureSecret;

import java.util.List;
import java.util.Map;

/**
 * @author Tomas Polesovsky
 */
public interface KeyProvider {

	public enum Capability {
		DELETE, LIST, READ, VERSIONING, WRITE
	}

	public boolean containsKey(String alias) throws Exception;

	public void deleteKey(String alias) throws Exception;

	public Capability[] getCapabilities();

	/**
	 * Determines initialization phase.
	 * 1 = Ambient Identity
	 * 2 = Remote Vaults
	 * 3 = Encrypted Local Storage
	 */
	public int getInitializationPhase();

	public String getProviderId();

	public boolean isAvailable();

	public List<String> listAliases() throws Exception;

	public SecureSecret resolveKey(String alias, Map<String, Object> context)
		throws Exception;

	public void storeKey(String alias, SecureSecret secret) throws Exception;

}
