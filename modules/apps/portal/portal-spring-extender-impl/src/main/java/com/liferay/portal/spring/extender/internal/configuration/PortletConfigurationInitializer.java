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

package com.liferay.portal.spring.extender.internal.configuration;

import com.liferay.portal.kernel.cache.PortalCacheManagerNames;
import com.liferay.portal.kernel.cache.configurator.PortalCacheConfiguratorSettings;
import com.liferay.portal.kernel.configuration.Configuration;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.model.Company;
import com.liferay.portal.kernel.security.permission.ResourceActions;
import com.liferay.portal.kernel.service.CompanyLocalService;
import com.liferay.portal.kernel.service.ResourcePermissionLocalService;
import com.liferay.portal.kernel.util.HashMapDictionary;
import com.liferay.portal.kernel.util.PropsKeys;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.kernel.util.Validator;

import java.util.ArrayList;
import java.util.Dictionary;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceRegistration;

/**
 * @author Tomas Polesovsky
 */
public class PortletConfigurationInitializer {

	public PortletConfigurationInitializer(
		Bundle bundle, ClassLoader classLoader,
		CompanyLocalService companyLocalService,
		Configuration portletConfiguration, ResourceActions resourceActions,
		ResourcePermissionLocalService resourcePermissionLocalService) {

		_bundle = bundle;
		_classLoader = classLoader;
		_companyLocalService = companyLocalService;
		_portletConfiguration = portletConfiguration;
		_resourceActions = resourceActions;
		_resourcePermissionLocalService = resourcePermissionLocalService;
	}

	public void stop() {
		for (ServiceRegistration<?> serviceRegistration :
				_serviceRegistrations) {

			serviceRegistration.unregister();
		}

		_serviceRegistrations.clear();
	}

	protected void start() {
		BundleContext bundleContext = _bundle.getBundleContext();

		if (_portletConfiguration != null) {
			_reconfigureCaches();

			_readResourceActions();

			_registerConfiguration(
				bundleContext, _portletConfiguration, "portlet");
		}
	}

	private void _readResourceActions() {
		try {
			String portlets = _portletConfiguration.get(
				"service.configurator.portlet.ids");

			String[] resourceActionConfigs = StringUtil.split(
				_portletConfiguration.get(PropsKeys.RESOURCE_ACTIONS_CONFIGS));

			Set<String> portletResources = new HashSet<>();
			Set<String> modelResources = new HashSet<>();

			for (String resourceActionConfig : resourceActionConfigs) {
				_resourceActions.read(
					null, _classLoader, resourceActionConfig, portletResources,
					modelResources);
			}

			if (Validator.isNull(portlets)) {
				for (String portletResourceName : portletResources) {
					_resourceActions.check(portletResourceName);
				}
			}
			else {
				for (String portletId : StringUtil.split(portlets)) {
					_resourceActions.check(portletId);
				}
			}

			List<Company> companies = _companyLocalService.getCompanies();

			for (Company company : companies) {
				_resourcePermissionLocalService.initModelDefaultPermissions(
					company.getCompanyId(), modelResources);
			}
		}
		catch (Exception e) {
			_log.error(
				"Unable to read resource actions config in " +
					PropsKeys.RESOURCE_ACTIONS_CONFIGS,
				e);
		}
	}

	private void _reconfigureCaches() {
		String singleVMConfigurationLocation = _portletConfiguration.get(
			PropsKeys.EHCACHE_SINGLE_VM_CONFIG_LOCATION);
		String multiVMConfigurationLocation = _portletConfiguration.get(
			PropsKeys.EHCACHE_MULTI_VM_CONFIG_LOCATION);

		if (Validator.isNull(singleVMConfigurationLocation) &&
			Validator.isNull(multiVMConfigurationLocation)) {

			return;
		}

		BundleContext bundleContext = _bundle.getBundleContext();

		if (Validator.isNotNull(singleVMConfigurationLocation)) {
			Dictionary<String, Object> properties = new HashMapDictionary<>();

			properties.put(
				"portal.cache.manager.name", PortalCacheManagerNames.SINGLE_VM);

			_serviceRegistrations.add(
				bundleContext.registerService(
					PortalCacheConfiguratorSettings.class,
					new PortalCacheConfiguratorSettings(
						_classLoader, singleVMConfigurationLocation),
					properties));
		}

		if (Validator.isNotNull(multiVMConfigurationLocation)) {
			Dictionary<String, Object> properties = new HashMapDictionary<>();

			properties.put(
				"portal.cache.manager.name", PortalCacheManagerNames.MULTI_VM);

			_serviceRegistrations.add(
				bundleContext.registerService(
					PortalCacheConfiguratorSettings.class,
					new PortalCacheConfiguratorSettings(
						_classLoader, multiVMConfigurationLocation),
					properties));
		}
	}

	private void _registerConfiguration(
		BundleContext bundleContext, Configuration configuration, String name) {

		Dictionary<String, Object> properties = new HashMapDictionary<>();

		properties.put(
			"configuration.bundle.symbolic.name", _bundle.getSymbolicName());
		properties.put("name", name);

		_serviceRegistrations.add(
			bundleContext.registerService(
				Configuration.class, configuration, properties));
	}

	private static final Log _log = LogFactoryUtil.getLog(
		PortletConfigurationInitializer.class);

	private final Bundle _bundle;
	private final ClassLoader _classLoader;
	private final CompanyLocalService _companyLocalService;
	private final Configuration _portletConfiguration;
	private final ResourceActions _resourceActions;
	private final ResourcePermissionLocalService
		_resourcePermissionLocalService;
	private final List<ServiceRegistration<?>> _serviceRegistrations =
		new ArrayList<>();

}