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

package com.liferay.oauth2.provider.jsonws.internal.webserver;

import com.liferay.oauth2.provider.jsonws.internal.constants.OAuth2JSONWSConstants;
import com.liferay.oauth2.provider.scope.spi.scope.descriptor.ScopeDescriptor;
import com.liferay.oauth2.provider.scope.spi.scope.finder.ScopeFinder;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.util.ResourceBundleLoader;
import com.liferay.portal.kernel.util.ResourceBundleUtil;

import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

import org.osgi.service.component.annotations.Reference;

/**
 * @author Stian Sigvartsen
 */
public class DocumentsScopeDescriptorFinder
	implements ScopeDescriptor, ScopeFinder {

	
	public DocumentsScopeDescriptorFinder(
		//String documentsScopeDescription,
		ResourceBundleLoader resourceBundleLoader) {
		
		//_documentsScopeDescription = documentsScopeDescription;
		_resourceBundleLoader = resourceBundleLoader;
	}
	
	@Override
	public String describeScope(String scope, Locale locale) {
		String scopeDescription = ResourceBundleUtil.getString(
			_resourceBundleLoader.loadResourceBundle(locale),
			scope);

		if (scopeDescription == null) {
			return scope;
		}

		return scopeDescription;
	}

	@Override
	public Collection<String> findScopes() {
		return new HashSet<>(_scopes);
	}

	private static final Log _log = LogFactoryUtil.getLog(
		DocumentsScopeDescriptorFinder.class);

	private final List<String> _scopes =
		Arrays.asList(
			new String[] {
				OAuth2JSONWSConstants.SCOPE_DOCUMENTS});	
	
	//private final String _documentsScopeDescription;
	private ResourceBundleLoader _resourceBundleLoader;
}