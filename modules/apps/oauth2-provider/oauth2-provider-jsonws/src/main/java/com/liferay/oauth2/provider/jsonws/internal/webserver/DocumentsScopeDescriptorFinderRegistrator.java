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

import java.util.Dictionary;
import java.util.Hashtable;
import java.util.Map;

import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceRegistration;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Deactivate;
import org.osgi.service.component.annotations.Reference;

import com.liferay.oauth2.provider.jsonws.internal.constants.OAuth2JSONWSConstants;
import com.liferay.oauth2.provider.scope.spi.scope.descriptor.ScopeDescriptor;
import com.liferay.oauth2.provider.scope.spi.scope.finder.ScopeFinder;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.util.MapUtil;
import com.liferay.portal.kernel.util.ResourceBundleLoader;

/**
 * @author Stian Sigvartsen
 */
@Component(
	configurationPid = "com.liferay.oauth2.provider.jsonws.internal.configuration.OAuth2DocumentsConfiguration",
	immediate = true, service = DocumentsScopeDescriptorFinderRegistrator.class
)
public class DocumentsScopeDescriptorFinderRegistrator {

	@Activate
	protected void activate(
		BundleContext bundleContext, Map<String, Object> properties) {

		DocumentsScopeDescriptorFinder documentScopeDescriptorFinder =
			new DocumentsScopeDescriptorFinder(
				//_documentsScopeDescription, 
				_resourceBundleLoader);

		Dictionary<String, Object> documentScopeDescriptorFinderProperties =
			new Hashtable<>();

		documentScopeDescriptorFinderProperties.put(
			"osgi.jaxrs.name", OAuth2JSONWSConstants.APPLICATION_NAME_DOCUMENTS);
				
		_serviceRegistration = bundleContext.registerService(
			new String[] {
				ScopeDescriptor.class.getName(),
				ScopeFinder.class.getName()
			},
			documentScopeDescriptorFinder,
			documentScopeDescriptorFinderProperties);			
	}
	
	@Deactivate
	protected void deactivate() {
		if (_serviceRegistration != null) {
			_serviceRegistration.unregister();
		}
	}
	
	private static final Log _log = LogFactoryUtil.getLog(
		DocumentsScopeDescriptorFinderRegistrator.class);

	//private String _documentsScopeDescription;
	
	private ServiceRegistration<?> _serviceRegistration;
	
	@Reference(
		target = "(bundle.symbolic.name=com.liferay.oauth2.provider.jsonws)"
	)
	private ResourceBundleLoader _resourceBundleLoader;
}