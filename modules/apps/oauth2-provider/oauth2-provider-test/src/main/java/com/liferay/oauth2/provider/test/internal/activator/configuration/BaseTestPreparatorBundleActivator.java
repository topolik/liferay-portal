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

package com.liferay.oauth2.provider.test.internal.activator.configuration;

import com.liferay.oauth2.provider.constants.GrantType;
import com.liferay.oauth2.provider.model.OAuth2Application;
import com.liferay.oauth2.provider.scope.spi.prefix.handler.PrefixHandler;
import com.liferay.oauth2.provider.scope.spi.prefix.handler.PrefixHandlerFactory;
import com.liferay.oauth2.provider.scope.spi.scope.mapper.ScopeMapper;
import com.liferay.oauth2.provider.service.OAuth2ApplicationLocalService;
import com.liferay.oauth2.provider.test.internal.TestUtils;
import com.liferay.osgi.util.ServiceTrackerFactory;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.model.Company;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.service.CompanyLocalServiceUtil;
import com.liferay.portal.kernel.service.ServiceContext;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Dictionary;
import java.util.Hashtable;
import java.util.List;
import java.util.ListIterator;
import java.util.Objects;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import javax.ws.rs.core.Application;

import com.liferay.portal.kernel.service.UserLocalServiceUtil;
import com.liferay.portal.kernel.test.util.UserTestUtil;
import org.apache.cxf.Bus;
import org.apache.cxf.endpoint.Server;
import org.apache.cxf.endpoint.ServerRegistry;

import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.osgi.framework.InvalidSyntaxException;
import org.osgi.framework.ServiceReference;
import org.osgi.framework.ServiceRegistration;
import org.osgi.service.cm.Configuration;
import org.osgi.service.cm.ConfigurationAdmin;
import org.osgi.service.http.context.ServletContextHelper;
import org.osgi.service.http.whiteboard.HttpWhiteboardConstants;
import org.osgi.util.tracker.ServiceTracker;
import org.osgi.util.tracker.ServiceTrackerCustomizer;

/**
 * @author Carlos Sierra Andrés
 */
public abstract class BaseTestPreparatorBundleActivator implements BundleActivator {

	protected BundleContext _bundleContext;

	public OAuth2Application createOauth2Application(
		final long companyId, User user, String clientId)
		throws PortalException {

		return createOauth2Application(
			companyId, user, clientId,
			Arrays.asList(
				GrantType.CLIENT_CREDENTIALS,
				GrantType.RESOURCE_OWNER_PASSWORD),
			Arrays.asList("everything", "everything.readonly"));
	}

	public User addUser(Company company) throws Exception {
		User user = UserTestUtil.addUser(company);

		_autoCloseables.add(
			() -> UserLocalServiceUtil.deleteUser(user.getUserId()));

		return user;
	}

	public User addAdminUser(Company company) throws Exception {
		User user = UserTestUtil.addCompanyAdminUser(company);

		_autoCloseables.add(
			() -> UserLocalServiceUtil.deleteUser(user.getUserId()));

		return user;
	}

	public OAuth2Application createOauth2Application(
		final long companyId, User user, String clientId,
		List<GrantType> availableGrants, List<String> availableScopes)
		throws PortalException {

		return createOauth2Application(
			companyId, user, clientId, "oauthTestApplicationSecret",
			availableGrants, availableScopes);
	}

	public OAuth2Application createOauth2Application(
		final long companyId, User user, String clientId, String clientSecret,
		List<GrantType> availableGrants, List<String> availableScopes)
		throws PortalException {

		ServiceReference<OAuth2ApplicationLocalService> serviceReference =
			_bundleContext.getServiceReference(
				OAuth2ApplicationLocalService.class);

		_oAuth2ApplicationLocalService = _bundleContext.getService(
			serviceReference);

		final OAuth2Application oAuth2Application;

		try {
			/**long companyId, long userId,
			 String userName, List<GrantType> allowedGrantTypesList,
			 String clientId, int clientProfile, String clientSecret,
			 String description, List<String> featuresList, String homePageURL,
			 long iconFileEntryId, String name, String privacyPolicyURL,
			 List<String> redirectURIsList, List<String> scopeAliasesList,
			 ServiceContext serviceContext*/
			oAuth2Application =
				_oAuth2ApplicationLocalService.addOAuth2Application(
					companyId, user.getUserId(), user.getLogin(),
					availableGrants, clientId, 0, clientSecret, 
					"test oauth application", 
					Collections.singletonList("token-introspection"),
					"http://localhost:8080", 0, "test application",
					"http://localhost:8080",
					Collections.singletonList("http://localhost:8080"),
					availableScopes, new ServiceContext()
					);

			_autoCloseables.add(
				() -> _oAuth2ApplicationLocalService.deleteOAuth2Application(
					oAuth2Application.getOAuth2ApplicationId()));

			return oAuth2Application;
		}
		finally {
			_bundleContext.ungetService(serviceReference);
		}

	}

	public Company createCompany(String hostName) throws PortalException {
		Company company = CompanyLocalServiceUtil.addCompany(
			hostName, hostName + ".xyz", hostName + ".xyz", false, 0, true);

		_autoCloseables.add(
			() -> CompanyLocalServiceUtil.deleteCompany(
				company.getCompanyId()));

		return company;
	}

	public ServiceRegistration<Application> registerJaxRsApplication(
		Application application, String path,
		Dictionary<String, Object> properties)

		throws InterruptedException {

		if (properties == null || properties.isEmpty()) {
			properties = new Hashtable<>();
		}

		properties.put("oauth2.test.application", "true");
		properties.put(
			"osgi.jaxrs.extension.select", "(liferay.extension=OAuth2)");
		properties.put("osgi.jaxrs.application.base", "/oauth2-test/" + path);

		ServiceRegistration<Application> serviceRegistration =
			_bundleContext.registerService(
				Application.class, application, properties);

		_autoCloseables.add(serviceRegistration::unregister);

		return serviceRegistration;
	}

	public ServiceRegistration<Object> registerJsonWebService(
		String name, String path, Object service,
		Dictionary<String, Object> properties) {

		if (properties == null || properties.isEmpty()) {
			properties = new Hashtable<>();
		}

		properties.put("json.web.service.context.name", name);
		properties.put("json.web.service.context.path", path);

		ServiceRegistration<Object> serviceRegistration =
			_bundleContext.registerService(
				Object.class, service, properties);

		_autoCloseables.add(serviceRegistration::unregister);

		return serviceRegistration;
	}

	protected OAuth2Application createOauth2Application(
		long companyId, User user, String clientId,
		List<String> availableScopes) throws PortalException {

		return createOauth2Application(
			companyId, user, clientId,
			Arrays.asList(
				GrantType.CLIENT_CREDENTIALS,
				GrantType.RESOURCE_OWNER_PASSWORD),
			availableScopes);
	}

	protected ServiceRegistration<PrefixHandlerFactory> registerPrefixHandler(
		PrefixHandler prefixHandler,
		Dictionary<String, Object> prefixHandlerProperties) {

		ServiceRegistration<PrefixHandlerFactory> serviceRegistration =
			_bundleContext.registerService(
				PrefixHandlerFactory.class, __ -> prefixHandler,
				prefixHandlerProperties);

		_autoCloseables.add(serviceRegistration::unregister);

		return serviceRegistration;
	}

	protected ServiceRegistration<ScopeMapper> registerScopeMapper(
		ScopeMapper scopeMapper,
		Hashtable<String, Object> scopeMapperProperties) {

		ServiceRegistration<ScopeMapper> serviceRegistration =
			_bundleContext.registerService(
				ScopeMapper.class, scopeMapper, scopeMapperProperties);

		_autoCloseables.add(serviceRegistration::unregister);

		return serviceRegistration;
	}

	protected Configuration createConfigurationFactory(
		String factoryPid, Dictionary<String, Object> properties) {

		Configuration factoryConfiguration =
			TestUtils.createFactoryConfiguration(
				_bundleContext, factoryPid, properties);

		_autoCloseables.add(factoryConfiguration::delete);

		return factoryConfiguration;
	}

	@Override
	public void start(BundleContext bundleContext) throws Exception {
		_bundleContext = bundleContext;

		_autoCloseables = new ArrayList<>();

		ServiceReference<ConfigurationAdmin> serviceReference =
			bundleContext.getServiceReference(ConfigurationAdmin.class);

		try {
			prepareTest();
		}
		catch (Exception e) {
			e.printStackTrace();

			bundleContext.ungetService(serviceReference);
		}
	}

	protected abstract void prepareTest() throws Exception;

	@Override
	public void stop(BundleContext bundleContext) throws Exception {
		ListIterator<AutoCloseable> listIterator = _autoCloseables.listIterator(
			_autoCloseables.size());

		while (listIterator.hasPrevious()) {
			AutoCloseable previous = listIterator.previous();

			try {
				previous.close();
			}
			catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	protected ArrayList<AutoCloseable> _autoCloseables;
	private OAuth2ApplicationLocalService _oAuth2ApplicationLocalService;

	protected Runnable updateOrCreateConfiguration(
			String pid, Dictionary<String, ?> properties) {

		Configuration configuration;

		try {
			configuration = TestUtils.configurationExists(
				_bundleContext, pid);
		}
		catch (Exception e) {
			return () -> {};
		}

		if (configuration == null) {
			TestUtils.updateConfiguration(_bundleContext, pid, properties);

			return () -> TestUtils.deleteConfiguration(_bundleContext, pid);
		}
		else {
			Dictionary<String, Object> oldProperties =
				configuration.getProperties();

			TestUtils.updateConfiguration(_bundleContext, pid, properties);

			return () -> TestUtils.updateConfiguration(
				_bundleContext, pid, oldProperties);
		}
	}

	protected void waitForFramework(Runnable runnable)
		throws InvalidSyntaxException {
		CountDownLatch countDownLatch = new CountDownLatch(52);

		ServiceTracker<?, ?> tracker =
			new ServiceTracker<>(
				_bundleContext,
				_bundleContext.createFilter(
					("(&(component.name=com.liferay.oauth2.provider.rest." +
						 "internal.endpoint.OAuth2EndpointApplication)(objectClass=org.apache.aries.jax.rs.whiteboard.internal.cxf.CxfJaxrsServiceRegistrator))")),
				new ServiceTrackerCustomizer<Object, Object>() {
					@Override
					public Object addingService(
						ServiceReference<Object> reference) {

						countDownLatch.countDown();

						return reference;
					}

					@Override
					public void modifiedService(
						ServiceReference<Object> reference,
						Object service) {

					}

					@Override
					public void removedService(
						ServiceReference<Object> reference,
						Object service) {

						countDownLatch.countDown();
					}
				});

		tracker.open();

		runnable.run();

		try {
			countDownLatch.await(30, TimeUnit.SECONDS);
		}
		catch (InterruptedException e) {
			e.printStackTrace();
		}
		finally {
			tracker.close();
		}
	}
}
