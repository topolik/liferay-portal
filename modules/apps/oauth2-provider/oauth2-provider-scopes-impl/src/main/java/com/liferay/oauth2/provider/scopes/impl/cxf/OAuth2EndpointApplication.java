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

package com.liferay.oauth2.provider.scopes.impl.cxf;

import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.servlet.ProtectedServletRequest;
import com.liferay.portal.kernel.util.HttpUtil;
import com.liferay.portal.kernel.util.MapUtil;
import com.liferay.portal.kernel.util.Portal;
import com.liferay.portal.kernel.util.StringBundler;
import com.liferay.portal.kernel.util.Validator;
import org.apache.cxf.rs.security.oauth2.provider.AccessTokenGrantHandler;
import org.apache.cxf.rs.security.oauth2.provider.OAuthJSONProvider;
import org.apache.cxf.rs.security.oauth2.provider.SubjectCreator;
import org.apache.cxf.rs.security.oauth2.services.AccessTokenService;
import org.apache.cxf.rs.security.oauth2.services.AuthorizationCodeGrantService;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceReference;
import org.osgi.framework.ServiceRegistration;
import org.osgi.service.cm.Configuration;
import org.osgi.service.cm.ConfigurationAdmin;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Deactivate;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.component.annotations.ReferenceCardinality;
import org.osgi.service.component.annotations.ReferencePolicyOption;
import org.osgi.service.http.whiteboard.HttpWhiteboardConstants;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.ApplicationPath;
import javax.ws.rs.core.Application;
import java.io.IOException;
import java.util.Arrays;
import java.util.Collections;
import java.util.Dictionary;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;
import java.util.Set;

@Component(
	immediate = true,
	service = Application.class
)
@ApplicationPath("/")
public class OAuth2EndpointApplication extends Application {

	@Activate
	public void activate(
			BundleContext bundleContext, final Map<String, Object> properties)
		throws IOException {

		ServiceReference<ConfigurationAdmin> serviceReference =
			bundleContext.getServiceReference(ConfigurationAdmin.class);

		try {
			ConfigurationAdmin configurationAdmin = bundleContext.getService(
				serviceReference);

			String contextPath = MapUtil.getString(properties, "contextPath", "/oauth2");

			_cxfConfiguration = configurationAdmin.createFactoryConfiguration(
				"com.liferay.portal.remote.cxf.common.configuration." +
				"CXFEndpointPublisherConfiguration",
				"?");

			Dictionary<String, Object> dictionary = new Hashtable<>();

			dictionary.put("contextPath", contextPath);

			_cxfConfiguration.update(dictionary);

			_restConfiguration = configurationAdmin.createFactoryConfiguration(
				"com.liferay.portal.remote.rest.extender.configuration." +
				"RestExtenderConfiguration",
				"?");

			dictionary = new Hashtable<>();

			dictionary.put("contextPaths", new String[]{contextPath});
			dictionary.put(
				"jaxRsApplicationFilterStrings",
				new String[]{"(component.name=" + getClass().getName() + ")"});

			_restConfiguration.update(dictionary);

			dictionary = new Hashtable<>();

			String contextName = contextPath.substring(1);

			contextName = contextName.replace("/", ".");

			dictionary.put(
				HttpWhiteboardConstants.HTTP_WHITEBOARD_CONTEXT_SELECT,
				contextName);
			dictionary.put(
				HttpWhiteboardConstants.HTTP_WHITEBOARD_FILTER_PATTERN,
				new String[]{"/authorize", "/authorize/*"});

			_authorizeEndpointFilterServiceRegistration =
				bundleContext.registerService(
					Filter.class, new Filter() {
						@Override
						public void init(FilterConfig filterConfig)
							throws ServletException {

						}

						@Override
						public void doFilter(
							ServletRequest servletRequest,
							ServletResponse servletResponse,
							FilterChain chain)
							throws IOException, ServletException {

							HttpServletRequest request =
								(HttpServletRequest) servletRequest;

							try {
								User user = _portal.getUser(request);

								if ((user != null) && !user.isDefaultUser()) {
									request = new ProtectedServletRequest(
										request,
										String.valueOf(user.getUserId()),
										null);

									chain.doFilter(request, servletResponse);

									return;
								}

								String loginPage = MapUtil.getString(
									properties, "loginPage");

								if (Validator.isBlank(loginPage)) {
									StringBundler sb = new StringBundler();
									sb.append(_portal.getPortalURL(request));
									sb.append(_portal.getPathContext());
									sb.append(_portal.getPathMain());
									sb.append("/portal/login");
									loginPage = sb.toString();
								}

								StringBundler redirect = new StringBundler();
								redirect.append(request.getRequestURI());
								redirect.append("?");
								redirect.append(request.getQueryString());

								loginPage = HttpUtil.addParameter(
									loginPage, "redirect", redirect.toString());

								HttpServletResponse response =
									(HttpServletResponse) servletResponse;

								response.sendRedirect(loginPage);
							}
							catch (Exception e) {
								_log.error(
									"Unable to resolve authenticated user", e);
							}
						}

						@Override
						public void destroy() {

						}
					}, dictionary);


		}
		finally {
			bundleContext.ungetService(serviceReference);
		}
	}

	@Deactivate
	public void deactivate() {
		_authorizeEndpointFilterServiceRegistration.unregister();

		try {
			_cxfConfiguration.delete();
		}
		catch (IOException e) {
			_log.error(e);
		}
		try {
			_restConfiguration.delete();
		}
		catch (IOException e) {
			_log.error(e);
		}
	}

	@Override
	public Set<Object> getSingletons() {
		AuthorizationCodeGrantService authorizationCodeGrantService =
			new AuthorizationCodeGrantService();

		authorizationCodeGrantService.setDataProvider(
			_liferayOAuthDataProvider);

		authorizationCodeGrantService.setSubjectCreator(_subjectCreator);

		AccessTokenService accessTokenService = new AccessTokenService();

		accessTokenService.setBlockUnsecureRequests(true);
		accessTokenService.setDataProvider(_liferayOAuthDataProvider);

		accessTokenService.setGrantHandlers(_accessTokenGrantHandlers);

		return new HashSet<>(
			Arrays.asList(
				authorizationCodeGrantService, accessTokenService,
				_authorizationMessageBodyWriter));
	}

	@Override
	public Set<Class<?>> getClasses() {
		return Collections.singleton(OAuthJSONProvider.class);
	}

	private static final Log _log = LogFactoryUtil.getLog(
		OAuth2EndpointApplication.class);
	@Reference(
		cardinality = ReferenceCardinality.AT_LEAST_ONE,
		policyOption = ReferencePolicyOption.GREEDY
	)
	private List<AccessTokenGrantHandler> _accessTokenGrantHandlers;

	@Reference(policyOption = ReferencePolicyOption.GREEDY)
	private LiferayOAuthDataProvider _liferayOAuthDataProvider;

	@Reference
	private AuthorizationMessageBodyWriter _authorizationMessageBodyWriter;

	@Reference
	private Portal _portal;

	@Reference(policyOption = ReferencePolicyOption.GREEDY)
	private SubjectCreator _subjectCreator;

	private ServiceRegistration _authorizeEndpointFilterServiceRegistration;
	private Configuration _cxfConfiguration;
	private Configuration _restConfiguration;

}