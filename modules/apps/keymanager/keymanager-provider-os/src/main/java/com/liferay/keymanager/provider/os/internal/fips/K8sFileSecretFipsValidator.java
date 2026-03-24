/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.keymanager.provider.os.internal.fips;

import com.liferay.keymanager.spi.fips.FipsValidator;

import org.osgi.service.component.annotations.Component;

/**
 * @author Tomas Polesovsky
 */
@Component(service = FipsValidator.class)
public class K8sFileSecretFipsValidator extends BaseOsFipsValidator {

	@Override
	public String getConfigurationPid() {
		return "com.liferay.keymanager.provider.os.internal.configuration." +
			"K8sFileSecretVaultProviderConfiguration";
	}

}