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

package com.liferay.oauth2.provider.rest.internal.cors;

import com.liferay.oauth2.provider.rest.internal.cors.servlet.filters.OAuth2CORSServletFilter;
import com.liferay.petra.string.StringBundler;
import com.liferay.portal.kernel.util.MapUtil;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Dictionary;
import java.util.Hashtable;
import java.util.Map;

import javax.servlet.Filter;

import javax.ws.rs.core.Application;
import javax.ws.rs.core.Configuration;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Feature;
import javax.ws.rs.core.FeatureContext;
import javax.ws.rs.ext.Provider;

import org.osgi.framework.BundleContext;
import org.osgi.framework.Constants;
import org.osgi.framework.ServiceRegistration;
import org.osgi.service.component.ComponentContext;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Deactivate;
import org.osgi.service.component.annotations.ServiceScope;
import org.osgi.service.http.whiteboard.HttpWhiteboardConstants;

/**
 * @author Carlos Sierra Andrés
 * @author Marta Medio
 */
@Component(
	property = {
		"liferay.extension=CORS",
		"osgi.jaxrs.application.select=(!(liferay.cors=false))",
		"osgi.jaxrs.extension=true", "osgi.jaxrs.name=Liferay.CORS"
	},
	scope = ServiceScope.PROTOTYPE, service = Feature.class
)
@Provider
public class CORSFeature implements Feature {

	@Override
	public boolean configure(FeatureContext featureContext) {
		Configuration configuration = featureContext.getConfiguration();

		Map<String, Object> applicationProperties =
			(Map<String, Object>)configuration.getProperty(
				"osgi.jaxrs.application.serviceProperties");

		Class<? extends Application> applicationClass = _application.getClass();

		String osgiJAXRSName = MapUtil.getString(
			applicationProperties, "osgi.jaxrs.name",
			applicationClass.getName());

		String contextSelect = MapUtil.getString(
			applicationProperties,
			HttpWhiteboardConstants.HTTP_WHITEBOARD_CONTEXT_SELECT,
			StringBundler.concat(
				"(", HttpWhiteboardConstants.HTTP_WHITEBOARD_CONTEXT_NAME,
				"=context.for", osgiJAXRSName, ")"));

		Dictionary<String, Object> properties = new Hashtable<>();

		properties.put(
			HttpWhiteboardConstants.HTTP_WHITEBOARD_CONTEXT_SELECT,
			contextSelect);

		properties.put(
			HttpWhiteboardConstants.HTTP_WHITEBOARD_FILTER_NAME,
			OAuth2CORSServletFilter.class.getName());

		properties.put(
			HttpWhiteboardConstants.HTTP_WHITEBOARD_FILTER_SERVLET,
			"cxf-servlet");

		properties.put(Constants.SERVICE_RANKING, -1);

		_serviceRegistrations.add(
			_bundleContext.registerService(
				Filter.class, new OAuth2CORSServletFilter(), properties));

		return true;
	}

	@Activate
	protected void activate(
		ComponentContext componentContext, Map<String, Object> properties) {

		_bundleContext = componentContext.getBundleContext();
	}

	@Deactivate
	protected void deactivate() {
		for (ServiceRegistration<?> serviceRegistration :
				_serviceRegistrations) {

			try {
				serviceRegistration.unregister();
			}
			catch (Exception e) {
			}
		}
	}

	@Context
	private Application _application;

	private BundleContext _bundleContext;
	private final Collection<ServiceRegistration<?>> _serviceRegistrations =
		new ArrayList<>();

}