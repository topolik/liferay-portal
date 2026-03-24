/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.keymanager.provider.db.internal.secret;

import com.liferay.keymanager.spi.secret.SecretVaultProvider;
import com.liferay.keymanager.spi.secret.SecretVaultReader;
import com.liferay.keymanager.spi.secret.SecretVaultWriter;
import com.liferay.portal.kernel.util.HashMapBuilder;

import java.util.Map;

import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Modified;

/**
 * @author Tomas Polesovsky
 */
@Component(
	configurationPid = "com.liferay.keymanager.provider.db.internal.configuration.DBSystemSecretVaultProviderConfiguration",
	property = "keymanager.provider.id=db-system-secret",
	service = {
		SecretVaultProvider.class, SecretVaultReader.class,
		SecretVaultWriter.class
	}
)
public class DBSystemSecretVaultProvider extends BaseDBSecretVaultProvider {

	@Activate
	@Modified
	protected void activate(Map<String, Object> properties) {
		super.activate(
			HashMapBuilder.<String, Object>putAll(
				properties
			).put(
				"systemScope", true
			).build());
	}

}