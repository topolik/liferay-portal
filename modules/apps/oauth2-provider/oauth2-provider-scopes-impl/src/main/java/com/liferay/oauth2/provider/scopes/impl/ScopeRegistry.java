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

import com.liferay.oauth2.provider.model.LiferayOAuth2Scope;
import com.liferay.oauth2.provider.scopes.impl.model.LiferayOAuth2ScopeImpl;
import com.liferay.oauth2.provider.scopes.liferay.api.ScopeFinderLocator;
import com.liferay.oauth2.provider.scopes.spi.ScopeFinder;
import com.liferay.oauth2.provider.scopes.spi.ScopeMapper;
import com.liferay.oauth2.provider.scopes.spi.ScopeMatcher;
import com.liferay.oauth2.provider.scopes.spi.PrefixHandler;
import com.liferay.oauth2.provider.scopes.spi.PrefixHandlerMapper;
import com.liferay.oauth2.provider.scopes.spi.ScopeMatcherFactory;
import com.liferay.oauth2.provider.service.OAuth2ScopeGrantLocalService;
import com.liferay.osgi.service.tracker.collections.ServiceReferenceServiceTuple;
import com.liferay.osgi.service.tracker.collections.map.ServiceTrackerMap;
import com.liferay.osgi.service.tracker.collections.map.ServiceTrackerMapFactory;
import com.liferay.portal.kernel.model.Company;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Set;

import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceReference;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Deactivate;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.component.annotations.ReferencePolicyOption;
import org.osgi.util.tracker.ServiceTrackerCustomizer;

@Component(immediate = true, service = ScopeFinderLocator.class)
public class ScopeRegistry implements ScopeFinderLocator {

	@Override
	public Collection<LiferayOAuth2Scope> locateScopes(
		long companyId, String scope) {

		Collection<LiferayOAuth2Scope> grants = new ArrayList<>();

		Set<String> names = _scopeFinderByNameServiceTrackerMap.keySet();

		for (String name : names) {
			ServiceReferenceServiceTuple<?, ScopeFinder> tuple =
				_scopeFinderByNameServiceTrackerMap.getService(name);

			ServiceReference<?> serviceReference = tuple.getServiceReference();

			PrefixHandlerMapper prefixHandlerMapper =
				_scopedPrefixHandlerMappers.getService(companyId, name);

			PrefixHandler prefixHandler = prefixHandlerMapper.mapFrom(
				serviceReference::getProperty);

			ScopeFinder scopeFinder = tuple.getService();

			ScopeMatcherFactory scopeMatcherFactory =
				_scopedScopeMatcherFactories.getService(companyId, name);

			if (scopeMatcherFactory == null) {
				scopeMatcherFactory =
					scopeFinder.getDefaultScopeMatcherFactory();
			}

			ScopeMatcher scopeMatcher = scopeMatcherFactory.create(scope);

			scopeMatcher = scopeMatcher.prepend(prefixHandler);

			scopeMatcher = scopeMatcher.withMapper(
				_scopedScopeMapper.getService(companyId, name));

			Collection<String> grantedScopes = scopeFinder.findScopes(
				scopeMatcher);

			for (String grantedScope : grantedScopes) {
				Bundle bundle = serviceReference.getBundle();

				grants.add(
					new LiferayOAuth2ScopeImpl(name, bundle, grantedScope));
			}
		}

		return grants;
	}

	private ScopedServiceTrackerMap<PrefixHandlerMapper>
		_scopedPrefixHandlerMappers;
	private ScopedServiceTrackerMap<ScopeMatcherFactory>
		_scopedScopeMatcherFactories;
	private ScopedServiceTrackerMap<ScopeMapper>
		_scopedScopeMapper;

	@Override
	public Collection<LiferayOAuth2Scope> listScopes(Company company) {
		Collection<LiferayOAuth2Scope> scopes = new ArrayList<>();

		Set<String> names = _scopeFinderByNameServiceTrackerMap.keySet();

		for (String name : names) {
			ServiceReferenceServiceTuple<?, ScopeFinder> tuple =
				_scopeFinderByNameServiceTrackerMap.getService(name);

			ServiceReference<?> serviceReference =
				tuple.getServiceReference();

			ScopeFinder scopeFinder = tuple.getService();

			Collection<String> availableScopes = scopeFinder.findScopes(
				__ -> true);

			long companyId = company.getCompanyId();

			PrefixHandlerMapper prefixHandlerMapper =
				_scopedPrefixHandlerMappers.getService(companyId, name);

			PrefixHandler prefixHandler = prefixHandlerMapper.mapFrom(
				serviceReference::getProperty);

			ScopeMapper scopeMapper =
				_scopedScopeMapper.getService(companyId, name);

			for (String availableScope : availableScopes) {
				Bundle bundle = serviceReference.getBundle();

				availableScope = prefixHandler.addPrefix(
					scopeMapper.map(availableScope));

				scopes.add(
					new LiferayOAuth2ScopeImpl(name, bundle, availableScope));
			}
		}

		return scopes;
	}

	@Activate
	protected void activate(BundleContext bundleContext) {
		_scopeFinderByNameServiceTrackerMap =
			ServiceTrackerMapFactory.openSingleValueMap(
				bundleContext, ScopeFinder.class, "osgi.jaxrs.name",
				new ScopeFinderServiceTupleServiceTrackerCustomizer(
					bundleContext));

		_scopedPrefixHandlerMappers = new ScopedServiceTrackerMap<>(
			bundleContext, PrefixHandlerMapper.class, "osgi.jaxrs.name",
			() -> _defaultPrefixHandlerMapper);

		_scopedScopeMatcherFactories = new ScopedServiceTrackerMap<>(
			bundleContext, ScopeMatcherFactory.class, "osgi.jaxrs.name",
			() -> null);

		_scopedScopeMapper = new ScopedServiceTrackerMap<>(
			bundleContext, ScopeMapper.class, "osgi.jaxrs.name",
			() -> ScopeMapper.NULL);
	}

	@Deactivate
	protected void deactivate() {
		_scopeFinderByNameServiceTrackerMap.close();
		_scopedPrefixHandlerMappers.close();
		_scopedScopeMatcherFactories.close();
		_scopedScopeMapper.close();
	}

	@Reference(
		target = "(default=true)",
		policyOption = ReferencePolicyOption.GREEDY
	)
	private PrefixHandlerMapper _defaultPrefixHandlerMapper;

	@Reference
	private OAuth2ScopeGrantLocalService _oAuth2ScopeGrantLocalService;

	private ServiceTrackerMap<
		String, ServiceReferenceServiceTuple<?, ScopeFinder>>
			_scopeFinderByNameServiceTrackerMap;

	private static class ScopeFinderServiceTupleServiceTrackerCustomizer
		implements
		ServiceTrackerCustomizer
			<ScopeFinder, ServiceReferenceServiceTuple<?, ScopeFinder>> {

		private BundleContext _bundleContext;

		public ScopeFinderServiceTupleServiceTrackerCustomizer(
			BundleContext bundleContext) {

			_bundleContext = bundleContext;
		}

		@Override
		public ServiceReferenceServiceTuple<?, ScopeFinder> addingService(
			ServiceReference<ScopeFinder> reference) {

			ScopeFinder scopeFinder = _bundleContext.getService(reference);

			return new ServiceReferenceServiceTuple<>(reference, scopeFinder);
		}

		@Override
		public void modifiedService(
			ServiceReference<ScopeFinder> reference,
			ServiceReferenceServiceTuple<?, ScopeFinder> tuple) {
		}

		@Override
		public void removedService(
			ServiceReference<ScopeFinder> reference,
			ServiceReferenceServiceTuple<?, ScopeFinder> tuple) {

			_bundleContext.ungetService(reference);
		}

	}

}