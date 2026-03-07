/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.keymanager.spi.secret;

import com.liferay.keymanager.secret.SecretManagerException;
import com.liferay.keymanager.secret.SecureSecret;

/**
 * @author Tomas Polesovsky
 */
public interface SecretVaultProvider {

	public void deleteSecret(String alias) throws SecretManagerException;

	public SecureSecret getSecret(String alias) throws SecretManagerException;

	public SecureSecret putSecret(SecureSecret secureSecret)
		throws SecretManagerException;

}