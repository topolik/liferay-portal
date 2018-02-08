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

package com.liferay.oauth2.provider.scopes.impl;

import com.liferay.oauth2.provider.scopes.spi.ScopeMapper;
import com.liferay.portal.kernel.util.MapUtil;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;

@Component(
	immediate = true,
	property = "osgi.jaxrs.name=com.liferay.oauth2.provider.sample.oauth.Test"
)
public class DefaultHTTPVerbsScopeMapper implements ScopeMapper {

	private boolean _passtrough;

	@Override
	public Set<String> map(String scope) {
		Set<String> result = new HashSet<>();

		switch (scope) {
			case "GET":
			case "HEAD":
			case "OPTIONS":
				result.add("everything.readonly");
				break;
			default:
				result.add("everything");
				break;
		}

		if (_passtrough) {
			result.add(scope);
		}

		return result;
	}

	@Activate
	protected void activate(Map<String, Object> properties) {
		_passtrough = MapUtil.getBoolean(properties, "passtrough", false);
	}

}
