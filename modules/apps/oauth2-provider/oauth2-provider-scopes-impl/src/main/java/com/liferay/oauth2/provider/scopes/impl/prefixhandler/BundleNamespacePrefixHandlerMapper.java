/**
 * Copyright (c) 2000-present Liferay, Inc. All rights reserved.
 * <p>
 * This library is free software; you can redistribute it and/or modify it under
 * the terms of the GNU Lesser General Public License as published by the Free
 * Software Foundation; either version 2.1 of the License, or (at your option)
 * any later version.
 * <p>
 * This library is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more
 * details.
 */

package com.liferay.oauth2.provider.scopes.impl.prefixhandler;

import com.liferay.oauth2.provider.scopes.spi.PrefixHandler;
import com.liferay.oauth2.provider.scopes.spi.PrefixHandlerFactory;
import com.liferay.oauth2.provider.scopes.spi.PrefixHandlerMapper;
import com.liferay.oauth2.provider.scopes.spi.PropertyGetter;
import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.osgi.framework.Version;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

@Component(immediate = true)
public class BundleNamespacePrefixHandlerMapper implements PrefixHandlerMapper {

	@Override
	public PrefixHandler mapFrom(PropertyGetter propertyGetter) {
		long bundleId = Long.parseLong(
			propertyGetter.get("service.bundleid").toString());

		Bundle bundle = _bundleContext.getBundle(bundleId);

		Version bundleVersion = bundle.getVersion();

		Object applicationNameObject = propertyGetter.get("osgi.jaxrs.name");

		String applicationName = applicationNameObject.toString();

		return _namespaceAdderFactory.create(
			bundle.getSymbolicName(), bundleVersion.toString(),
			applicationName);
	}

	@Activate
	protected void activate(BundleContext bundleContext) {
		_bundleContext = bundleContext;
	}

	private BundleContext _bundleContext;

	@Reference
	PrefixHandlerFactory _namespaceAdderFactory;

}