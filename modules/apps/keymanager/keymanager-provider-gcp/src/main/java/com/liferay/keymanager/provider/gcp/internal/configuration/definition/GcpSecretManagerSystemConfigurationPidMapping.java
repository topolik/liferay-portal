/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.keymanager.provider.gcp.internal.configuration.definition;

import com.liferay.keymanager.provider.gcp.internal.configuration.GcpSecretManagerSystemSecretVaultProviderConfiguration;
import com.liferay.portal.kernel.settings.definition.ConfigurationPidMapping;

import org.osgi.service.component.annotations.Component;

/**
 * @author Tomas Polesovsky
 */
@Component(service = ConfigurationPidMapping.class)
public class GcpSecretManagerSystemConfigurationPidMapping
	extends BaseGcpConfigurationPidMapping {

	public GcpSecretManagerSystemConfigurationPidMapping() {
		super(
			GcpSecretManagerSystemSecretVaultProviderConfiguration.class,
			GcpSecretManagerSystemSecretVaultProviderConfiguration.class.getName());
	}

}