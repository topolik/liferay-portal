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
import com.liferay.osgi.service.tracker.collections.map.ServiceTrackerMap;
import com.liferay.osgi.service.tracker.collections.map.ServiceTrackerMapFactory;
import com.liferay.osgi.util.StringPlus;

import java.util.List;
import java.util.Objects;
import java.util.stream.Stream;

import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;

/**
 * @author Tomas Polesovsky
 */
@Component(immediate = true, service = MFAVerifierRegistry.class)
public class MFAVerifierRegistryImpl implements MFAVerifierRegistry {

	@Override
	public <T extends MFAVerifier> T getMFAVerifier(Class<T> mfaVerifierClass) {
		return (T)_serviceTrackerMap.getService(mfaVerifierClass);
	}

	@Activate
	protected void activate(BundleContext bundleContext) {
		_serviceTrackerMap = ServiceTrackerMapFactory.openSingleValueMap(
			bundleContext, MFAVerifier.class, null,
			(serviceReference, emitter) -> {
				Bundle bundle = serviceReference.getBundle();

				List<String> objectClassList = StringPlus.asList(
					serviceReference.getProperty("objectClass"));

				Stream<String> stream = objectClassList.stream();

				stream.map(
					s -> {
						try {
							return (Class<? extends MFAVerifier>)
								bundle.loadClass(s);
						}
						catch (ClassNotFoundException cnfe) {
							return null;
						}
					}
				).filter(
					Objects::nonNull
				).filter(
					c -> !c.equals(MFAVerifier.class)
				).filter(
					c -> MFAVerifier.class.isAssignableFrom(c)
				).forEach(
					emitter::emit
				);
			});
	}

	private ServiceTrackerMap<Class<? extends MFAVerifier>, MFAVerifier>
		_serviceTrackerMap;

}