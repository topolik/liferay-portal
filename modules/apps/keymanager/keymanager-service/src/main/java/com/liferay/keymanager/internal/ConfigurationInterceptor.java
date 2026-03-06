/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.keymanager.internal;

import com.liferay.keymanager.KeyResolverService;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;

import java.util.Dictionary;
import java.util.Enumeration;

import org.osgi.framework.ServiceReference;
import org.osgi.service.cm.ConfigurationPlugin;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Tomas Polesovsky
 */
@Component(
	property = {
		ConfigurationPlugin.CM_RANKING + ":Integer=1000", "cm.target=*"
	},
	service = ConfigurationPlugin.class
)
public class ConfigurationInterceptor implements ConfigurationPlugin {

	@Override
	public void modifyConfiguration(
		ServiceReference<?> serviceReference,
		Dictionary<String, Object> properties) {

		if (properties == null) {
			return;
		}

		Enumeration<String> enumeration = properties.keys();

		while (enumeration.hasMoreElements()) {
			String key = enumeration.nextElement();

			Object value = properties.get(key);

			if (value instanceof String) {
				String stringValue = (String)value;

				if (_keyResolverService.isKeyReference(stringValue)) {
					try {
						String resolved = _keyResolverService.resolve(
							stringValue);

						properties.put(key, resolved);
					}
					catch (Exception exception) {
						_log.error(
							"Failed to resolve key reference for property: " +
								key,
							exception);
					}
				}
			}
			else if (value instanceof String[]) {
				String[] arrayValue = (String[])value;
				boolean modified = false;

				for (int i = 0; i < arrayValue.length; i++) {
					if (_keyResolverService.isKeyReference(arrayValue[i])) {
						try {
							arrayValue[i] = _keyResolverService.resolve(
								arrayValue[i]);

							modified = true;
						}
						catch (Exception exception) {
							_log.error(
								"Failed to resolve key reference in array " +
									"property: " + key,
								exception);
						}
					}
				}

				if (modified) {
					properties.put(key, arrayValue);
				}
			}
		}
	}

	private static final Log _log = LogFactoryUtil.getLog(
		ConfigurationInterceptor.class);

	@Reference
	private KeyResolverService _keyResolverService;

}