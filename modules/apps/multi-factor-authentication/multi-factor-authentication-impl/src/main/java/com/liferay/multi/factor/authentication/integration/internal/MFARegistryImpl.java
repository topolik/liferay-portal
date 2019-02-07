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

import com.liferay.multi.factor.authentication.api.MFARegistry;
import com.liferay.multi.factor.authentication.spi.integration.MFAIntegration;
import com.liferay.multi.factor.authentication.spi.verifier.MFAVerifier;
import com.liferay.osgi.service.tracker.collections.map.ServiceReferenceMapperFactory;
import com.liferay.osgi.service.tracker.collections.map.ServiceTrackerMap;
import com.liferay.osgi.service.tracker.collections.map.ServiceTrackerMapFactory;
import com.liferay.osgi.util.StringPlus;

import java.util.ArrayList;
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
@Component(immediate = true, service = MFARegistry.class)
public class MFARegistryImpl implements MFARegistry {

	private ServiceTrackerMap<String, MFAIntegration>
		_mfaIntegrationServiceTrackerMap;

	private ServiceTrackerMap<Class<? extends MFAVerifier>, List<MFAVerifier>>
		_mfaVerifiersServiceTrackerMap;

	@Override
	public <T extends MFAVerifier> List<T> getMFAVerifier(Class<T> mfaVerifierClass){
		return (List<T>) _mfaVerifiersServiceTrackerMap.getService(mfaVerifierClass);
	}

	@Override
	public List<MFAVerifier> getMFAVerifiers() {
		return new ArrayList(_mfaVerifiersServiceTrackerMap.values());
	}

	@Override
	public MFAVerifier getMFAVerifier(String mfaIntegrationName) {
		MFAIntegration<?> mfaIntegration =
			getMFAIntegration(mfaIntegrationName);

		return getMFAVerifier(mfaIntegration);
	}

	@Override
	public <T extends MFAVerifier> T getMFAVerifier(
		MFAIntegration<T> mfaIntegration) {

		Class<T> supportedMFAVerifierClass =
			mfaIntegration.getSupportedMFAVerifierClass();

		List<T> mfaVerifiers = getMFAVerifier(supportedMFAVerifierClass);

		// TODO: now, based on the integration point configuration, we could
		// return selected verifier implementation
		// Or we can return all with AND/OR logic.
		// AND logic = chain of verifiers, all must setup+verify
		// OR logic = just one can succeed

		if ((mfaVerifiers == null) || mfaVerifiers.size() == 0) {
			return null;
		}

		return mfaVerifiers.get(0);
	}

	@Activate
	protected void activate(BundleContext bundleContext) {
		_mfaIntegrationServiceTrackerMap =
			ServiceTrackerMapFactory.openSingleValueMap(
				bundleContext, MFAIntegration.class, null,
				ServiceReferenceMapperFactory.create(
					bundleContext,
					(service, emitter) -> emitter.emit(service.getName())));


		_mfaVerifiersServiceTrackerMap =
			ServiceTrackerMapFactory.openMultiValueMap(
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

	@Override
	public MFAIntegration<?> getMFAIntegration(String name) {
		return _mfaIntegrationServiceTrackerMap.getService(name);
	}

	@Override
	public List<MFAIntegration<?>> getMFAIntegrations() {
		return new ArrayList(_mfaIntegrationServiceTrackerMap.values());
	}

}