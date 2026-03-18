/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.keymanager.provider.gcp.internal.configuration.listener;

import com.liferay.keymanager.provider.gcp.internal.configuration.GcpSecretManagerSystemSecretVaultProviderConfiguration;
import com.liferay.keymanager.spi.configuration.listener.BaseConfigurationModelListener;
import com.liferay.portal.configuration.persistence.listener.ConfigurationModelListener;

import org.osgi.service.component.annotations.Component;

/**
 * @author Tomas Polesovsky
 */
@Component(service = ConfigurationModelListener.class)
public class GcpSecretManagerSystemSecretVaultProviderConfigurationModelListener
	extends BaseConfigurationModelListener<GcpSecretManagerSystemSecretVaultProviderConfiguration> {

	public GcpSecretManagerSystemSecretVaultProviderConfigurationModelListener() {
		super(GcpSecretManagerSystemSecretVaultProviderConfiguration.class);
	}

}