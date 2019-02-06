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

package com.liferay.multi.factor.authentication.integration.auth.verifier.internal.tracker;

import com.liferay.multi.factor.authentication.integration.auth.verifier.internal.servlet.filter.MFAAuthVerifierFilter;
import com.liferay.multi.factor.authentication.integration.spi.verifier.MFAVerifierRegistry;
import com.liferay.osgi.util.ServiceTrackerFactory;
import com.liferay.petra.string.StringBundler;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.HashMapDictionary;
import com.liferay.portal.kernel.util.MapUtil;

import java.util.Dictionary;
import java.util.Map;

import javax.servlet.Filter;

import org.osgi.framework.BundleContext;
import org.osgi.framework.Constants;
import org.osgi.framework.ServiceReference;
import org.osgi.framework.ServiceRegistration;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Deactivate;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.http.context.ServletContextHelper;
import org.osgi.service.http.whiteboard.HttpWhiteboardConstants;
import org.osgi.util.tracker.ServiceTracker;
import org.osgi.util.tracker.ServiceTrackerCustomizer;

/**
 * @author Tomas Polesovsky
 */
@Component(
	immediate = true,
	property = "servlet.context.helper.select.filter=(!(liferay.mfa.verifier=false))",
	service = {}
)
public class MFAuthVerifierFilterTracker {

	@Activate
	protected void activate(
		BundleContext bundleContext, Map<String, Object> properties) {

		_bundleContext = bundleContext;

		String servletContextHelperSelectFilterString = MapUtil.getString(
			properties, "servlet.context.helper.select.filter");

		String filterString = StringBundler.concat(
			"(&" + servletContextHelperSelectFilterString + "(",
			HttpWhiteboardConstants.HTTP_WHITEBOARD_CONTEXT_NAME, "=*)",
			"(objectClass=", ServletContextHelper.class.getName(), "))");

		_serviceTracker = ServiceTrackerFactory.open(
			bundleContext, filterString,
			new ServletContextHelperServiceTrackerCustomizer());
	}

	@Deactivate
	protected void deactivate() {
		_serviceTracker.close();
	}

	private BundleContext _bundleContext;

	@Reference
	private MFAVerifierRegistry _mfaVerifierRegistry;

	private ServiceTracker<ServletContextHelper, ServiceRegistration<?>>
		_serviceTracker;

	private class ServletContextHelperServiceTrackerCustomizer
		implements ServiceTrackerCustomizer
			<ServletContextHelper, ServiceRegistration<?>> {

		@Override
		public ServiceRegistration<?> addingService(
			ServiceReference<ServletContextHelper> serviceReference) {

			MFAAuthVerifierFilter mfaAuthVerifierFilter =
				new MFAAuthVerifierFilter();

			mfaAuthVerifierFilter.setMfaVerifierRegistry(_mfaVerifierRegistry);

			return _bundleContext.registerService(
				Filter.class, mfaAuthVerifierFilter,
				_buildProperties(serviceReference));
		}

		@Override
		public void modifiedService(
			ServiceReference<ServletContextHelper> serviceReference,
			ServiceRegistration<?> serviceRegistration) {

			serviceRegistration.setProperties(
				_buildProperties(serviceReference));
		}

		@Override
		public void removedService(
			ServiceReference<ServletContextHelper> serviceReference,
			ServiceRegistration<?> serviceRegistration) {

			serviceRegistration.unregister();
		}

		private Dictionary<String, ?> _buildProperties(
			ServiceReference<ServletContextHelper> serviceReference) {

			Dictionary<String, Object> properties = new HashMapDictionary<>();

			properties.put(Constants.SERVICE_RANKING, Integer.MIN_VALUE);

			String contextName = GetterUtil.getString(
				serviceReference.getProperty(
					HttpWhiteboardConstants.HTTP_WHITEBOARD_CONTEXT_NAME));

			properties.put(
				HttpWhiteboardConstants.HTTP_WHITEBOARD_CONTEXT_SELECT,
				StringBundler.concat(
					"(", HttpWhiteboardConstants.HTTP_WHITEBOARD_CONTEXT_NAME,
					"=", contextName, ")"));

			properties.put(
				HttpWhiteboardConstants.HTTP_WHITEBOARD_FILTER_PATTERN,
				new String[] {"/", "/*"});

			return properties;
		}

	}

}