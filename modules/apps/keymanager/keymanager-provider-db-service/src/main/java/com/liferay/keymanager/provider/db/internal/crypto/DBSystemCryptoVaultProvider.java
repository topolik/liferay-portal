/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.keymanager.provider.db.internal.crypto;

import com.liferay.keymanager.crypto.CryptoManager;
import com.liferay.keymanager.provider.db.service.KeyEntryLocalService;
import com.liferay.keymanager.spi.crypto.CryptoVaultProvider;
import com.liferay.portal.kernel.service.CompanyLocalService;
import com.liferay.portal.kernel.util.HashMapBuilder;

import java.util.Map;

import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.ConfigurationPolicy;
import org.osgi.service.component.annotations.Modified;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Tomas Polesovsky
 */
@Component(
	configurationPid = "com.liferay.keymanager.provider.db.internal.configuration.DBSystemCryptoVaultProviderConfiguration",
	configurationPolicy = ConfigurationPolicy.OPTIONAL,
	property = "keymanager.provider.id=db-system-crypto",
	service = CryptoVaultProvider.class
)
public class DBSystemCryptoVaultProvider extends BaseDBCryptoVaultProvider {

	@Activate
	@Modified
	public void activate(Map<String, Object> properties) {
		super.activate(
			HashMapBuilder.<String, Object>putAll(
				properties
			).put(
				"systemScope", true
			).build());
	}

	@Reference
	protected void setCompanyLocalService(
		CompanyLocalService companyLocalService) {

		_companyLocalService = companyLocalService;
	}

	@Reference
	protected void setCryptoManager(CryptoManager cryptoManager) {
		_cryptoManager = cryptoManager;
	}

	@Reference
	protected void setKeyEntryLocalService(
		KeyEntryLocalService keyEntryLocalService) {

		_keyEntryLocalService = keyEntryLocalService;
	}

}