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

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

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
	service = ServiceAccessQuota.class
)
public class ServiceAccessQuota {

	public long getIntervalMillis() {
		return _configuration.intervalMillis();
	}

	public int getMax() {
		return _configuration.max();
	}

	public List<String> getMetric() {
		return Arrays.asList(_configuration.metric());
	}

	public Set<String> getServiceSignature() {
		String[] signatureArray = _configuration.serviceSignature();
		HashSet<String> serviceSignature = new HashSet<>(signatureArray.length);

		for (String signature : signatureArray) {
			serviceSignature.add(signature);
		}

		return serviceSignature;
	}

	@Activate
	@Modified
	protected void activate(Map<String, Object> properties) {
		_configuration = ConfigurableUtil.createConfigurable(
			SAQConfiguration.class, properties);
	}

	private volatile SAQConfiguration _configuration;

}