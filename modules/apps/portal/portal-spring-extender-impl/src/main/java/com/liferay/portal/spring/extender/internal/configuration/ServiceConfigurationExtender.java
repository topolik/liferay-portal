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

import aQute.bnd.version.Version;
import aQute.bnd.version.VersionRange;

import com.liferay.osgi.felix.util.AbstractExtender;
import com.liferay.petra.string.StringBundler;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.configuration.Configuration;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.model.Release;
import com.liferay.portal.kernel.module.framework.ModuleServiceLifecycle;
import com.liferay.portal.kernel.security.permission.ResourceActions;
import com.liferay.portal.kernel.service.ServiceComponentLocalService;
import com.liferay.portal.kernel.util.StringUtil;

import java.util.Dictionary;

import org.apache.felix.dm.DependencyManager;
import org.apache.felix.dm.ServiceDependency;
import org.apache.felix.utils.extender.Extension;

import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.osgi.framework.wiring.BundleWiring;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Deactivate;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Preston Crary
 */
@Component(immediate = true, service = {})
public class ServiceConfigurationExtender extends AbstractExtender {

	@Activate
	protected void activate(BundleContext bundleContext) throws Exception {
		start(bundleContext);
	}

	@Deactivate
	protected void deactivate(BundleContext bundleContext) throws Exception {
		stop(bundleContext);
	}

	@Override
	protected void debug(Bundle bundle, String s) {
		if (_log.isDebugEnabled()) {
			_log.debug(s);
		}
	}

	@Override
	protected Extension doCreateExtension(Bundle bundle) {
		Dictionary<String, String> headers = bundle.getHeaders(
			StringPool.BLANK);

		if (headers.get("Liferay-Service") == null) {
			return null;
		}

		BundleWiring bundleWiring = bundle.adapt(BundleWiring.class);

		ClassLoader classLoader = bundleWiring.getClassLoader();

		Configuration portletConfiguration = ConfigurationUtil.getConfiguration(
			classLoader, "portlet");
		Configuration serviceConfiguration = ConfigurationUtil.getConfiguration(
			classLoader, "service");

		if ((portletConfiguration == null) && (serviceConfiguration == null)) {
			return null;
		}

		String requireSchemaVersion = headers.get(
			"Liferay-Require-SchemaVersion");

		PortletConfigurationInitializer portletConfigurationInitializer =
			new PortletConfigurationInitializer(
				bundle, classLoader, portletConfiguration, _resourceActions);

		ServiceConfigurationInitializer serviceConfigurationInitializer =
			new ServiceConfigurationInitializer(
				bundle, classLoader, serviceConfiguration,
				_serviceComponentLocalService);

		return new ServiceConfigurationExtension(
			bundle, requireSchemaVersion, portletConfigurationInitializer,
			serviceConfigurationInitializer);
	}

	@Override
	protected void error(String s, Throwable throwable) {
		_log.error(s, throwable);
	}

	@Override
	protected void warn(Bundle bundle, String s, Throwable throwable) {
		if (_log.isWarnEnabled()) {
			_log.warn(s, throwable);
		}
	}

	private static final Log _log = LogFactoryUtil.getLog(
		ServiceConfigurationExtender.class);

	@Reference(target = ModuleServiceLifecycle.DATABASE_INITIALIZED)
	private ModuleServiceLifecycle _moduleServiceLifecycle;

	@Reference
	private ResourceActions _resourceActions;

	@Reference
	private ServiceComponentLocalService _serviceComponentLocalService;

	private static class ServiceConfigurationExtension implements Extension {

		@Override
		public void destroy() {
			_dependencyManager.remove(_portletComponent);
			_dependencyManager.remove(_serviceComponent);
		}

		@Override
		public void start() {
			_dependencyManager.add(_portletComponent);
			_dependencyManager.add(_serviceComponent);
		}

		private ServiceConfigurationExtension(
			Bundle bundle, String requireSchemaVersion,
			PortletConfigurationInitializer portletConfigurationInitializer,
			ServiceConfigurationInitializer serviceConfigurationInitializer) {

			_dependencyManager = new DependencyManager(
				bundle.getBundleContext());

			_portletComponent = _dependencyManager.createComponent();

			_portletComponent.setImplementation(
				portletConfigurationInitializer);

			_serviceComponent = _dependencyManager.createComponent();

			_serviceComponent.setImplementation(
				serviceConfigurationInitializer);

			if (requireSchemaVersion == null) {
				return;
			}

			String versionRangeFilter = null;

			// See LPS-76926

			if (Version.isVersion(requireSchemaVersion)) {
				Version version = Version.parseVersion(requireSchemaVersion);

				versionRangeFilter = _getVersionRangerFilter(version);
			}
			else if (VersionRange.isVersionRange(requireSchemaVersion)) {
				VersionRange versionRange = VersionRange.parseVersionRange(
					requireSchemaVersion);

				versionRangeFilter = StringUtil.replace(
					versionRange.toFilter(), "version",
					"release.schema.version");
			}

			if (versionRangeFilter == null) {
				return;
			}

			ServiceDependency serviceDependency =
				_dependencyManager.createServiceDependency();

			serviceDependency.setRequired(true);

			serviceDependency.setService(
				Release.class,
				StringBundler.concat(
					"(&(release.bundle.symbolic.name=",
					bundle.getSymbolicName(), ")", versionRangeFilter,
					"(|(!(release.state=*))(release.state=0)))"));

			_portletComponent.add(serviceDependency);

			_serviceComponent.add(serviceDependency);
		}

		private String _getVersionRangerFilter(Version version) {
			StringBundler sb = new StringBundler(9);

			sb.append("(&(release.schema.version>=");
			sb.append(version.getMajor());
			sb.append(".");
			sb.append(version.getMinor());
			sb.append(".0)(!(release.schema.version>=");
			sb.append(version.getMajor());
			sb.append(".");
			sb.append(version.getMinor() + 1);
			sb.append(".0)))");

			return sb.toString();
		}

		private final DependencyManager _dependencyManager;
		private final org.apache.felix.dm.Component _portletComponent;
		private final org.apache.felix.dm.Component _serviceComponent;

	}

}