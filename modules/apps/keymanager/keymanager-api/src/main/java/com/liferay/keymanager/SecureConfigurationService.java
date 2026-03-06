/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.keymanager;

import java.util.Dictionary;

import org.osgi.annotation.versioning.ProviderType;

/**
 * @author Tomas Polesovsky
 */
@ProviderType
public interface SecureConfigurationService {

	/**
	 * Creates a secure wrapper around a configuration dictionary.
	 */
	public SecureConfiguration wrap(Dictionary<String, Object> properties);

	public interface SecureConfiguration {

		/**
		 * Returns a value as a SecureSecret (Heap-safe resolution).
		 * If the value is not a key reference, it wraps the plaintext in a SecureSecret.
		 */
		public SecureSecret getSecret(String key) throws Exception;

		/**
		 * Returns a value as a String (standard resolution).
		 */
		public String getString(String key);

	}

}