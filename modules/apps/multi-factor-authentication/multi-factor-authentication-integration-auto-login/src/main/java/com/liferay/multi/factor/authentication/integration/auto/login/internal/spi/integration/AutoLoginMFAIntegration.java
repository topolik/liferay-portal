/**
 * Copyright (c) 2000-present Liferay, Inc. All rights reserved.
 *
 * This library is free software; you can redistribute it and/or modify it under
 * the terms of the GNU Lesser General Public License as published by the Free
 * Software Foundation; either version 2.1 of the License, or (at your option)
 * any later version.
 *
 * This library is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more
 * details.
 */

package com.liferay.multi.factor.authentication.integration.auto.login.internal.spi.integration;

import com.liferay.multi.factor.authentication.integration.auto.login.internal.configuration.AutoLoginMFAIntegrationConfiguration;
import com.liferay.multi.factor.authentication.spi.integration.MFAIntegration;
import com.liferay.multi.factor.authentication.spi.verifier.BrowserMFAVerifier;
import com.liferay.multi.factor.authentication.spi.verifier.HeadlessMFAVerifier;
import com.liferay.portal.configuration.metatype.bnd.util.ConfigurableUtil;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.ConfigurationPolicy;

import java.util.Map;

/**
 * @author Tomas Polesovsky
 */
@Component(
	configurationPid = "com.liferay.multi.factor.authentication.integration.auto.login.internal.configuration.AutoLoginMFAIntegrationConfiguration",
	configurationPolicy = ConfigurationPolicy.OPTIONAL,
	service = {MFAIntegration.class, AutoLoginMFAIntegration.class}
)
public class AutoLoginMFAIntegration implements MFAIntegration {
	private String _name;
	private boolean _enabled;

	@Activate
	protected void activate(Map<String, Object> properties) {
		AutoLoginMFAIntegrationConfiguration
			autoLoginMFAIntegrationConfiguration =
				ConfigurableUtil.createConfigurable(
					AutoLoginMFAIntegrationConfiguration.class, properties);

		_enabled = autoLoginMFAIntegrationConfiguration.enabled();
		_name = autoLoginMFAIntegrationConfiguration.name();
	}

	@Override
	public String getName() {
		return _name;
	}

	@Override
	public boolean isEnabled() {
		return _enabled;
	}

	@Override
	public boolean supportsHeadless() {
		return true;
	}

	@Override
	public boolean supportsBrowser() {
		return true;
	}

}
