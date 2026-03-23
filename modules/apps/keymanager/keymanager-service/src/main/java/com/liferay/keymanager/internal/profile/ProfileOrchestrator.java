/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.keymanager.internal.profile;

import com.liferay.keymanager.internal.profile.configuration.KeyManagerGlobalConfiguration;
import com.liferay.keymanager.spi.profile.KeyManagerProfile;
import com.liferay.osgi.service.tracker.collections.map.PropertyServiceReferenceMapper;
import com.liferay.osgi.service.tracker.collections.map.ServiceTrackerMap;
import com.liferay.osgi.service.tracker.collections.map.ServiceTrackerMapFactory;
import com.liferay.portal.configuration.metatype.bnd.util.ConfigurableUtil;

import java.util.Map;

import org.osgi.framework.BundleContext;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Deactivate;
import org.osgi.service.component.annotations.Modified;

/**
 * @author Tomas Polesovsky
 */
@Component(
	configurationPid = "com.liferay.keymanager.internal.profile.configuration.KeyManagerGlobalConfiguration",
	service = ProfileOrchestrator.class
)
public class ProfileOrchestrator {

	public KeyManagerProfile getActiveProfile() {
		String activeProfileId = _configuration.activeProfileId();

		KeyManagerProfile keyManagerProfile = _serviceTrackerMap.getService(
			activeProfileId);

		if (keyManagerProfile == null) {
			keyManagerProfile = _serviceTrackerMap.getService("custom");
		}

		return keyManagerProfile;
	}

	@Activate
	protected void activate(
		BundleContext bundleContext, Map<String, Object> properties) {

		_configuration = ConfigurableUtil.createConfigurable(
			KeyManagerGlobalConfiguration.class, properties);

		_serviceTrackerMap = ServiceTrackerMapFactory.openSingleValueMap(
			bundleContext, KeyManagerProfile.class,
			"(keymanager.profile.id=*)",
			new PropertyServiceReferenceMapper<>("keymanager.profile.id"));
	}

	@Deactivate
	protected void deactivate() {
		_serviceTrackerMap.close();
	}

	@Modified
	protected void modified(Map<String, Object> properties) {
		_configuration = ConfigurableUtil.createConfigurable(
			KeyManagerGlobalConfiguration.class, properties);
	}

	private volatile KeyManagerGlobalConfiguration _configuration;
	private ServiceTrackerMap<String, KeyManagerProfile> _serviceTrackerMap;

}