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

package com.liferay.oauth2.provider.rest.internal.endpoint.filter;

import com.liferay.oauth2.provider.model.OAuth2Application;
import com.liferay.oauth2.provider.rest.spi.bearer.token.provider.BearerTokenProvider;
import com.liferay.petra.string.StringBundler;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.security.access.control.AccessControlUtil;
import com.liferay.portal.kernel.security.auth.AccessControlContext;
import com.liferay.portal.kernel.security.auth.verifier.AuthVerifierResult;
import com.liferay.portal.kernel.servlet.HttpMethods;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.kernel.util.Validator;

import java.io.IOException;

import java.net.URI;
import java.net.URISyntaxException;

import java.util.List;
import java.util.Map;

import javax.annotation.Priority;

import javax.ws.rs.ForbiddenException;
import javax.ws.rs.Priorities;
import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerRequestFilter;
import javax.ws.rs.container.ContainerResponseContext;
import javax.ws.rs.container.ContainerResponseFilter;
import javax.ws.rs.container.PreMatching;
import javax.ws.rs.core.MultivaluedMap;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.ServiceScope;

/**
 * @author Marta Medio
 */
@Component(
	property = {
		"osgi.jaxrs.extension=true",
		"osgi.jaxrs.extension.select=(osgi.jaxrs.name=Liferay.OAuth2)",
		"osgi.jaxrs.name=OAuth2CORSFilter"
	},
	scope = ServiceScope.PROTOTYPE,
	service = {ContainerRequestFilter.class, ContainerResponseFilter.class}
)
@PreMatching
@Priority(Priorities.HEADER_DECORATOR - 8)
public class OAuth2CORSRequestResponseFilter
	implements ContainerRequestFilter, ContainerResponseFilter {

	@Override
	public void filter(ContainerRequestContext requestContext)
		throws IOException {

		String origin = requestContext.getHeaderString("Origin");

		if (Validator.isBlank(origin)) {
			return;
		}

		String method = requestContext.getMethod();

		if (StringUtil.equals(method, HttpMethods.OPTIONS)) {
			String accessControlRequestMethod = requestContext.getHeaderString(
				"Access-Control-Request-Method");

			if (Validator.isBlank(accessControlRequestMethod)) {
				if (_log.isDebugEnabled()) {
					_log.debug("Invalid preflight sent by browser");
				}

				throw new ForbiddenException();
			}
		}
		else {
			OAuth2Application oAuth2Application = getOAuth2Application();

			if (oAuth2Application == null) {
				if (_log.isDebugEnabled()) {
					_log.debug("Unable to find OAuth2 application");
				}

				throw new ForbiddenException();
			}

			List<String> redirectURIsList =
				oAuth2Application.getRedirectURIsList();

			if (!isOriginAllowed(origin, oAuth2Application, redirectURIsList)) {
				if (_log.isDebugEnabled()) {
					_log.debug(
						StringBundler.concat(
							"CORS was disallowed for client ",
							oAuth2Application.getClientId(), " and origin: ",
							origin));
				}

				throw new ForbiddenException();
			}
		}
	}

	@Override
	public void filter(
		ContainerRequestContext requestContext,
		ContainerResponseContext responseContext) {

		String origin = requestContext.getHeaderString("Origin");

		if (Validator.isBlank(origin)) {
			return;
		}

		MultivaluedMap<String, Object> headers = responseContext.getHeaders();

		headers.putSingle("Access-Control-Allow-Headers", "*");
		headers.putSingle("Access-Control-Allow-Origin", origin);
	}

	protected OAuth2Application getOAuth2Application() {
		AccessControlContext accessControlContext =
			AccessControlUtil.getAccessControlContext();

		AuthVerifierResult authVerifierResult =
			accessControlContext.getAuthVerifierResult();

		Map<String, Object> settings = authVerifierResult.getSettings();

		Object accessTokenObject = settings.get(
			BearerTokenProvider.AccessToken.class.getName());

		if (accessTokenObject == null) {
			return null;
		}

		BearerTokenProvider.AccessToken accessToken =
			(BearerTokenProvider.AccessToken)accessTokenObject;

		return accessToken.getOAuth2Application();
	}

	protected boolean isOriginAllowed(
		String origin, OAuth2Application oAuth2Application,
		List<String> redirectURIsList) {

		for (String redirect : redirectURIsList) {
			try {
				URI originURI = new URI(origin);
				URI redirectURI = new URI(redirect);

				String originHost = originURI.getHost();
				String redirectHost = redirectURI.getHost();

				if (originHost.equals(redirectHost)) {
					return true;
				}
			}
			catch (URISyntaxException urise) {
				if (_log.isDebugEnabled()) {
					_log.debug(
						StringBundler.concat(
							"Invalid callback uri ", redirect,
							" for client id: ",
							oAuth2Application.getClientId()),
						urise);
				}
			}
		}

		return false;
	}

	private static final Log _log = LogFactoryUtil.getLog(
		OAuth2CORSRequestResponseFilter.class);

}