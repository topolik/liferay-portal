/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.keymanager.provider.gcp.internal;

import java.util.Map;

import org.osgi.service.component.ComponentFactory;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.ConfigurationPolicy;
import org.osgi.service.component.annotations.Deactivate;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Tomas Polesovsky
 */
@Component(
	configurationPid = "com.liferay.keymanager.provider.gcp.internal.configuration.GcpKmsSystemCryptoVaultProviderConfiguration",
	configurationPolicy = ConfigurationPolicy.REQUIRE, service = {}
)
public class GcpKmsSystemCryptoVaultProviderFactory
	extends BaseGcpFactory<GcpKmsCryptoVaultProvider> {

	@Activate
	protected void activate(Map<String, Object> properties) {
		super.activate(properties, _componentFactory, true);
	}

	@Deactivate
	@Override
	protected void deactivate() {
		super.deactivate();
	}

	@Reference(
		target = "(component.factory=com.liferay.keymanager.provider.gcp.internal.GcpKmsCryptoVaultProvider)"
	)
	private ComponentFactory<GcpKmsCryptoVaultProvider> _componentFactory;

}