/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.keymanager.provider.db.internal.crypto;

import com.liferay.keymanager.spi.crypto.CryptoVaultProvider;
import com.liferay.portal.kernel.util.HashMapBuilder;

import java.util.Map;

import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.ConfigurationPolicy;
import org.osgi.service.component.annotations.Modified;

/**
 * @author Tomas Polesovsky
 */
@Component(
	configurationPid = "com.liferay.keymanager.provider.db.internal.configuration.DBCompanyCryptoVaultProviderConfiguration.scoped",
	configurationPolicy = ConfigurationPolicy.REQUIRE,
	property = "keymanager.provider.id=db-company-crypto",
	service = CryptoVaultProvider.class
)
public class DBCompanyCryptoVaultProvider extends BaseDBCryptoVaultProvider {

	@Activate
	@Modified
	protected void activate(Map<String, Object> properties) {
		super.activate(
			HashMapBuilder.<String, Object>putAll(
				properties
			).put(
				"systemScope", false
			).build());
	}

}