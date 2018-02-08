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

package com.liferay.oauth2.provider.scopes.impl;

import com.liferay.oauth2.provider.scopes.spi.ScopeDescriptor;
import com.liferay.portal.kernel.util.ResourceBundleLoader;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.component.annotations.ReferencePolicy;
import org.osgi.service.component.annotations.ReferencePolicyOption;

import java.util.Locale;
import java.util.ResourceBundle;

/**
 * @author Tomas Polesovsky
 */
@Component(
	immediate=true,
	property = "osgi.jaxrs.name=com.liferay.oauth2.provider.sample.oauth.Test"
)
public class TestScopeDescriptor implements ScopeDescriptor {
	@Override
	public String describe(String scope, Locale locale) {

		scope = "oauth2." + scope;

		ResourceBundle resourceBundle =
			_resourceBundleLoader.loadResourceBundle(locale.toString());

		if (!resourceBundle.containsKey(scope)) {
			return scope;
		}

		return resourceBundle.getString(scope);
	}

	@Reference(
		policy = ReferencePolicy.DYNAMIC,
		policyOption = ReferencePolicyOption.GREEDY,
		target = "(bundle.symbolic.name=com.liferay.oauth2.provider.sample)"
	)
	private volatile ResourceBundleLoader _resourceBundleLoader;
}