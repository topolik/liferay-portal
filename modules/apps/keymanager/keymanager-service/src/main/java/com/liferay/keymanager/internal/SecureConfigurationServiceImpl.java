/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.keymanager.internal;

import com.liferay.keymanager.KeyResolutionException;
import com.liferay.keymanager.KeyResolverService;
import com.liferay.keymanager.SecureConfigurationService;
import com.liferay.keymanager.SecureSecret;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;

import java.util.Dictionary;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Tomas Polesovsky
 */
@Component(service = SecureConfigurationService.class)
public class SecureConfigurationServiceImpl
	implements SecureConfigurationService {

	@Override
	public SecureConfiguration wrap(Dictionary<String, Object> properties) {
		return new SecureConfigurationImpl(properties);
	}

	private static final Log _log = LogFactoryUtil.getLog(
		SecureConfigurationServiceImpl.class);

	@Reference
	private KeyResolverService _keyResolverService;

	private class SecureConfigurationImpl implements SecureConfiguration {

		public SecureConfigurationImpl(Dictionary<String, Object> properties) {
			_properties = properties;
		}

		@Override
		public SecureSecret getSecret(String key) throws Exception {
			Object value = _properties.get(key);

			if (value instanceof String) {
				String stringValue = (String)value;

				if (_keyResolverService.isKeyReference(stringValue)) {
					return _keyResolverService.resolveSecure(stringValue);
				}

				return new SecureSecret(stringValue.toCharArray());
			}

			throw new Exception(
				"Property '" + key + "' is not a String/Reference");
		}

		@Override
		public String getString(String key) {
			Object value = _properties.get(key);

			if (value instanceof String) {
				String stringValue = (String)value;

				if (_keyResolverService.isKeyReference(stringValue)) {
					try {
						return _keyResolverService.resolve(stringValue);
					}
					catch (KeyResolutionException keyResolutionException) {
						if (_log.isWarnEnabled()) {
							_log.warn(
								"Unable to resolve key reference: " +
									stringValue,
								keyResolutionException);
						}

						return stringValue;
					}
				}

				return stringValue;
			}

			if (value != null) {
				return String.valueOf(value);
			}

			return null;
		}

		private final Dictionary<String, Object> _properties;

	}

}