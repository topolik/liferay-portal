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

package com.liferay.oauth2.provider.rest.internal.jaxrs.feature;

import com.liferay.oauth2.provider.scope.ScopeChecker;
import com.liferay.oauth2.provider.scope.spi.scope.finder.ScopeFinder;

import java.io.IOException;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Dictionary;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;
import java.util.function.Predicate;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import javax.annotation.Priority;

import javax.ws.rs.Priorities;
import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerRequestFilter;
import javax.ws.rs.core.Configuration;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Feature;
import javax.ws.rs.core.FeatureContext;
import javax.ws.rs.core.Request;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;
import javax.ws.rs.ext.Provider;

import org.osgi.framework.BundleContext;
import org.osgi.framework.Constants;
import org.osgi.framework.ServiceRegistration;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Deactivate;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.component.annotations.ServiceScope;

/**
 * @author Carlos Sierra Andrés
 */
@Component(
	property = {
		"osgi.jaxrs.extension=true",
		"osgi.jaxrs.extension.select=(osgi.jaxrs.name=Liferay.OAuth2)",
		"osgi.jaxrs.name=Liferay.OAuth2.HTTP.configurable.request.checker"
	},
	scope = ServiceScope.PROTOTYPE
)
@Priority(Priorities.AUTHORIZATION - 8)
@Provider
public class ConfigurableCheckerFeature implements Feature {

	@Override
	public boolean configure(FeatureContext context) {
		if (_patterns.isEmpty()) {
			return false;
		}

		Map<Class<?>, Integer> contracts = new HashMap<>();

		contracts.put(
			ContainerRequestFilter.class, Priorities.AUTHORIZATION - 8);

		context.register(
			new ConfigurableCheckerContainerRequestFilter(), contracts);

		Configuration configuration = context.getConfiguration();

		Stream<CheckPattern> stream = _patterns.stream();

		_serviceRegistration = _bundleContext.registerService(
			ScopeFinder.class,
			new CollectionScopeFinder(
				stream.flatMap(
					c -> Arrays.stream(c.getScopes())
				).collect(
					Collectors.toSet()
				)
			),
			buildProperties(configuration));

		return true;
	}

	protected void abortRequest(
		ContainerRequestContext containerRequestContext) {

		containerRequestContext.abortWith(
			Response.status(
				403
			).build());
	}

	@Activate
	protected void activate(
		BundleContext bundleContext, Map<String, Object> properties) {

		_bundleContext = bundleContext;

		Object patternsObject = properties.get("patterns");

		if (patternsObject == null) {
			return;
		}

		String[] patternStrings = (String[])patternsObject;

		for (String patternString : patternStrings) {
			String[] split = patternString.split("::");

			if (split.length != 3) {
				return;
			}

			String methodPatternString = split[0];
			String urlPatternString = split[1];

			String scopesString = split[2];

			String[] scopes = scopesString.split(",");

			try {
				_patterns.add(
					new CheckPattern(
						Pattern.compile(methodPatternString),
						Pattern.compile(urlPatternString), scopes));
			}
			catch (PatternSyntaxException pse) {
				//TODO: log

				continue;
			}
		}
	}

	protected Dictionary<String, Object> buildProperties(
		Configuration configuration) {

		Dictionary<String, Object> properties = new Hashtable<>(
			(Map<String, Object>)configuration.getProperty(
				"osgi.jaxrs.application.serviceProperties"));

		properties.put(Constants.SERVICE_RANKING, Integer.MIN_VALUE);

		return properties;
	}

	@Deactivate
	protected void deactivate() {
		if (_serviceRegistration != null) {
			_serviceRegistration.unregister();
		}
	}

	private BundleContext _bundleContext;
	private final List<CheckPattern> _patterns = new ArrayList<>();

	@Reference
	private ScopeChecker _scopeChecker;

	private ServiceRegistration<ScopeFinder> _serviceRegistration;

	private static class CheckPattern {

		public CheckPattern(
			Pattern methodPattern, Pattern urlPattern, String[] scopes) {

			_methodPatternPredicate = methodPattern.asPredicate();
			_urlPatternPredicate = urlPattern.asPredicate();
			_scopes = scopes;
		}

		public Predicate<String> getMethodPatternPredicate() {
			return _methodPatternPredicate;
		}

		public String[] getScopes() {
			return _scopes;
		}

		public Predicate<String> getUrlPatternPredicate() {
			return _urlPatternPredicate;
		}

		private final Predicate<String> _methodPatternPredicate;
		private final String[] _scopes;
		private final Predicate<String> _urlPatternPredicate;

	}

	private class ConfigurableCheckerContainerRequestFilter
		implements ContainerRequestFilter {

		@Override
		public void filter(ContainerRequestContext containerRequestContext)
			throws IOException {

			Request request = containerRequestContext.getRequest();

			String path = _uriInfo.getPath();

			for (CheckPattern pattern : _patterns) {
				Predicate<String> urlPatternPredicate =
					pattern.getUrlPatternPredicate();

				if (urlPatternPredicate.test(path)) {
					Predicate<String> methodPatternPredicate =
						pattern.getMethodPatternPredicate();

					if (methodPatternPredicate.test(request.getMethod())) {
						if (_scopeChecker.checkAnyScope(pattern.getScopes())) {
							return;
						}
					}
				}
			}

			abortRequest(containerRequestContext);
		}

		@Context
		private UriInfo _uriInfo;

	}

}