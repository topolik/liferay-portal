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

package com.liferay.oauth2.provider.jsonws.internal.security.auth.verifier;

import com.liferay.portal.kernel.util.MapUtil;
import com.liferay.portal.servlet.filters.authverifier.AuthVerifierFilter;

import java.util.Hashtable;
import java.util.Map;

import javax.servlet.Filter;

import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceRegistration;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Deactivate;

/**
 * @author Stian Sigvartsen
 */
@Component(
	configurationPid = "com.liferay.oauth2.provider.jsonws.internal.configuration.OAuth2JSONWSConfiguration"
)
public class OAuth2DocumentsAuthVerifierFilterRegistrator {

	@Activate
	protected void activate(
		BundleContext bundleContext, Map<String, Object> properties) {

		boolean enabled = MapUtil.getBoolean(
			properties, "oauth2.documents.scope.enabled", true);

		if (enabled) {
			Hashtable<String, Object> authVerifierFilterProperties =
				new Hashtable<>();

			authVerifierFilterProperties.put(
				"before-filter", "Auto Login Filter");
			authVerifierFilterProperties.put("dispatcher", "REQUEST");
			authVerifierFilterProperties.put("servlet-context-name", "");
			authVerifierFilterProperties.put(
				"servlet-filter-name", "OAuth2 Documents Auth Verifier Filter");
			authVerifierFilterProperties.put("url-pattern", "/documents/*");
			authVerifierFilterProperties.put(
				"init.param.auth.verifier.OAuth2DocumentsAuthVerifier.urls." +
					"includes",
				"/*");

			_serviceRegistration = bundleContext.registerService(
				Filter.class, new AuthVerifierFilter(),
				authVerifierFilterProperties);
		}
	}

	@Deactivate
	protected void deactivate() {
		if (_serviceRegistration != null) {
			_serviceRegistration.unregister();
		}
	}

	private ServiceRegistration<Filter> _serviceRegistration;

}