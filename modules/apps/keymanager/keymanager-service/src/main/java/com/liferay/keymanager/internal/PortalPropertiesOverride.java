/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.keymanager.internal;

import com.liferay.keymanager.KeyResolverService;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.module.framework.ModuleServiceLifecycle;
import com.liferay.portal.kernel.util.PropsUtil;

import java.util.Properties;

import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Tomas Polesovsky
 */
@Component(service = {})
public class PortalPropertiesOverride {

	@Activate
	protected void activate() {
		if (_log.isInfoEnabled()) {
			_log.info("Scanning portal properties for key references...");
		}

		Properties properties = PropsUtil.getProperties();

		int count = 0;

		for (String key : properties.stringPropertyNames()) {
			String value = properties.getProperty(key);

			if ((value != null) && _keyResolverService.isKeyReference(value)) {
				try {
					String resolved = _keyResolverService.resolve(value);

					PropsUtil.set(key, resolved);

					count++;
				}
				catch (Exception exception) {
					_log.error(
						"Failed to resolve key reference for portal " +
							"property: " + key,
						exception);
				}
			}
		}

		if (_log.isInfoEnabled()) {
			_log.info(
				"Resolved " + count + " key references in portal properties.");
		}
	}

	private static final Log _log = LogFactoryUtil.getLog(
		PortalPropertiesOverride.class);

	@Reference
	private KeyResolverService _keyResolverService;

	@Reference(target = ModuleServiceLifecycle.PORTAL_INITIALIZED)
	private ModuleServiceLifecycle _portalInitialized;

}