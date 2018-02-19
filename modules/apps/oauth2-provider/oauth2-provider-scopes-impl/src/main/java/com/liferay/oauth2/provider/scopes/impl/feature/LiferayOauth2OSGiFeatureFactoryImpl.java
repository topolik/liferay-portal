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

package com.liferay.oauth2.provider.scopes.impl.feature;

import com.liferay.oauth2.provider.scopes.api.LiferayOauth2OSGiFeatureFactory;
import com.liferay.oauth2.provider.scopes.liferay.api.ScopeContext;
import com.liferay.oauth2.provider.scopes.api.ScopeChecker;
import com.liferay.oauth2.provider.scopes.spi.ScopeMatcherFactory;
import org.osgi.framework.Bundle;
import org.osgi.service.component.ComponentContext;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Deactivate;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.component.annotations.ServiceScope;

import javax.ws.rs.core.Feature;
import java.util.concurrent.ConcurrentHashMap;

@Component(scope = ServiceScope.BUNDLE)
public class LiferayOauth2OSGiFeatureFactoryImpl implements
	LiferayOauth2OSGiFeatureFactory {

	private Bundle _bundle;
	private ConcurrentHashMap<String, LiferayOauth2OSGiFeature> _map;

	@Activate
	protected void activate(ComponentContext componentContext) {
		_map = new ConcurrentHashMap<>();
		_bundle = componentContext.getUsingBundle();
	}

	@Deactivate
	protected void deactivate() {
		_map.forEach((__, feature) -> feature.close());
	}

	@Override
	public Feature create(String applicationName) {
		return _map.computeIfAbsent(applicationName, __ -> new LiferayOauth2OSGiFeature(
			_scopeStack, _scopeChecker, _scopeMatcherFactory, _bundle,
			applicationName));
	}

	@Reference
	ScopeContext _scopeStack;

	@Reference
	ScopeChecker _scopeChecker;

	@Reference
	ScopeMatcherFactory _scopeMatcherFactory;

}
