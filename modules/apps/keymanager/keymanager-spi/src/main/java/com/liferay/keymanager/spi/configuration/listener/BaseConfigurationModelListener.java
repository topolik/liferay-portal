/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.keymanager.spi.configuration.listener;

import com.liferay.portal.configuration.persistence.listener.ConfigurationModelListener;
import com.liferay.portal.configuration.persistence.listener.ConfigurationModelListenerException;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.Validator;

import java.util.Dictionary;
import java.util.Objects;

import org.osgi.service.cm.Configuration;
import org.osgi.service.cm.ConfigurationAdmin;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Tomas Polesovsky
 */
public abstract class BaseConfigurationModelListener<T>
	implements ConfigurationModelListener {

	public BaseConfigurationModelListener(Class<T> configurationClass) {
		_configurationClass = configurationClass;
	}

	@Override
	public void onBeforeSave(String pid, Dictionary<String, Object> properties)
		throws ConfigurationModelListenerException {

		String providerId = GetterUtil.getString(properties.get("providerId"));

		if (Validator.isNull(providerId)) {
			providerId = GetterUtil.getString(properties.get("provider-id"));
		}

		if (Validator.isNull(providerId)) {
			return;
		}

		long companyId = GetterUtil.getLong(properties.get("companyId"), 0);

		try {
			Configuration[] configurations =
				_configurationAdmin.listConfigurations(
					"(&(providerId=" + providerId + ")(service.pid=*" +
						_configurationClass.getSimpleName() + "*))");

			if (configurations == null) {
				configurations = _configurationAdmin.listConfigurations(
					"(&(provider-id=" + providerId + ")(service.pid=*" +
						_configurationClass.getSimpleName() + "*))");
			}

			if (configurations != null) {
				for (Configuration configuration : configurations) {
					String existingPid = configuration.getPid();

					if (Objects.equals(pid, existingPid)) {
						continue;
					}

					Dictionary<String, Object> existingProperties =
						configuration.getProperties();

					long existingCompanyId = GetterUtil.getLong(
						existingProperties.get("companyId"), 0);

					if (companyId == existingCompanyId) {
						throw new ConfigurationModelListenerException(
							"A provider with ID '" + providerId +
								"' already exists for this scope.",
							_configurationClass, getClass(), properties);
					}
				}
			}
		}
		catch (ConfigurationModelListenerException cmle) {
			throw cmle;
		}
		catch (Exception e) {
			throw new ConfigurationModelListenerException(
				e, _configurationClass, getClass(), properties);
		}
	}

	@Reference
	private ConfigurationAdmin _configurationAdmin;

	private final Class<T> _configurationClass;

}