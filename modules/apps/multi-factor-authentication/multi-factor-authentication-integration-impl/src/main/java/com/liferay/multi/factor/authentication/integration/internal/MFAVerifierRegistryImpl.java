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

package com.liferay.multi.factor.authentication.integration.internal;

import com.liferay.multi.factor.authentication.integration.spi.verifier.MFAVerifier;
import com.liferay.multi.factor.authentication.integration.spi.verifier.MFAVerifierRegistry;
import com.liferay.osgi.service.tracker.collections.map.ServiceReferenceMapperFactory;
import com.liferay.osgi.service.tracker.collections.map.ServiceTrackerMap;
import com.liferay.osgi.service.tracker.collections.map.ServiceTrackerMapFactory;
import com.liferay.osgi.util.StringPlus;
import com.liferay.portal.kernel.util.GetterUtil;
import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;

import java.util.List;
import java.util.Objects;
import java.util.Set;

/**
 * @author Tomas Polesovsky
 */
@Component(
	immediate = true,
	service = MFAVerifierRegistry.class
)
public class MFAVerifierRegistryImpl implements MFAVerifierRegistry {

	@Activate
	protected void activate(BundleContext bundleContext) {
		_serviceTrackerMap = ServiceTrackerMapFactory.openSingleValueMap(
			bundleContext, MFAVerifier.class, null, (serviceReference, emitter) -> {
				List<String> objectClass = StringPlus.asList(
					serviceReference.getProperty("objectClass"));

				Bundle bundle = serviceReference.getBundle();

				objectClass.stream().map( s -> {
					try {
						return (Class<? extends MFAVerifier>)bundle.loadClass(s);
					}
					catch (ClassNotFoundException e) {
						return null;
					}
				}).filter(
					Objects::nonNull
				).filter(
					c -> !c.equals(MFAVerifier.class)
				).filter(
					c -> MFAVerifier.class.isAssignableFrom(c)
				)
				.forEach(emitter::emit);
			});

	}

	@Override
	public <T extends MFAVerifier> T getMFAVerifier(Class<T> mfaVerifierClass) {
		return (T)_serviceTrackerMap.getService(mfaVerifierClass);
	}


	private ServiceTrackerMap<Class<? extends MFAVerifier>, MFAVerifier> _serviceTrackerMap;

}
