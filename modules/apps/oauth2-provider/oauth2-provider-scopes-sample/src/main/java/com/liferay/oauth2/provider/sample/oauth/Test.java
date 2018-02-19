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

package com.liferay.oauth2.provider.sample.oauth;

import com.liferay.oauth2.provider.scopes.api.LiferayOauth2OSGiFeatureFactory;
import com.liferay.oauth2.provider.scopes.api.RequiresScope;
import com.liferay.oauth2.provider.scopes.api.ScopesDescriptionBundle;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import javax.ws.rs.GET;
import javax.ws.rs.core.Application;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

@Component(
	immediate = true,
	service = Application.class
)
@ScopesDescriptionBundle("content.Language")
public class Test extends Application {

	@Override
	public Set<Object> getSingletons() {
		return new HashSet<>(
			Arrays.asList(
				this, _liferayOauth2OSGiFeatureFactory.create("Sample")));
	}

	@GET
	@RequiresScope("everything.readonly")
	public String method() {
		return "hello";
	}

	@Reference
	LiferayOauth2OSGiFeatureFactory _liferayOauth2OSGiFeatureFactory;

}