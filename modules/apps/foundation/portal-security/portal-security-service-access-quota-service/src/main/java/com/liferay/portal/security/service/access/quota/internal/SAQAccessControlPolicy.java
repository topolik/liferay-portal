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

package com.liferay.portal.security.service.access.quota.internal;

import com.liferay.portal.configuration.metatype.bnd.util.ConfigurableUtil;
import com.liferay.portal.security.service.access.quota.configuration.SAQConfiguration;

import java.util.Map;

import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.ConfigurationPolicy;
import org.osgi.service.component.annotations.Modified;

/**
 * @author Stian Sigvartsen
 */
@Component(
	configurationPid = "com.liferay.portal.security.service.access.quota.configuration.SAQConfiguration",
	configurationPolicy = ConfigurationPolicy.REQUIRE,
	service = SAQAccessControlPolicy.class
)
public class SAQAccessControlPolicy {

	public long getIntervalMillis() {
		return _configuration.intervalMillis();
	}

	public int getMax() {
		return _configuration.max();
	}

	public String[] getPolicyMetric() {
		return _configuration.metric();
	}

	public String[] getServiceSignature() {
		return _configuration.serviceSignature();
	}

	@Activate
	@Modified
	protected void activate(Map<String, Object> properties) {
		_configuration = ConfigurableUtil.createConfigurable(
			SAQConfiguration.class, properties);
	}

	private volatile SAQConfiguration _configuration;

}