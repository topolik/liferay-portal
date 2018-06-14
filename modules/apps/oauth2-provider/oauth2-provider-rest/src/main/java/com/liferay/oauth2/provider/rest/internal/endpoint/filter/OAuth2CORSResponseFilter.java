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
import com.liferay.portal.kernel.security.access.control.AccessControlUtil;
import com.liferay.portal.kernel.security.auth.AccessControlContext;
import com.liferay.portal.kernel.security.auth.verifier.AuthVerifierResult;

import java.net.URI;
import java.net.URISyntaxException;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerResponseContext;
import javax.ws.rs.container.ContainerResponseFilter;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.ext.Provider;

import org.osgi.service.component.annotations.Component;

/**
 * @author Marta Medio
 */
@Component(
	immediate = true,
	property = {
		"osgi.jaxrs.extension=true",
		"osgi.jaxrs.extension.select=(osgi.jaxrs.name=Liferay.OAuth2)",
		"osgi.jaxrs.name=OAuth2CORSResponseFilter"
	}
)
@Provider
public class OAuth2CORSResponseFilter implements ContainerResponseFilter {

	@Override
	public void filter(
		ContainerRequestContext requestContext,
		ContainerResponseContext responseContext) {

		AccessControlContext accessControlContext =
			AccessControlUtil.getAccessControlContext();

		AuthVerifierResult authVerifierResult =
			accessControlContext.getAuthVerifierResult();

		Map<String, Object> settings = authVerifierResult.getSettings();

		Object accessTokenObject = settings.get(
			BearerTokenProvider.AccessToken.class.getName());

		if (accessTokenObject == null) {
			return;
		}

		BearerTokenProvider.AccessToken accessToken =
			(BearerTokenProvider.AccessToken)accessTokenObject;

		OAuth2Application oAuth2Application =
			accessToken.getOAuth2Application();

		List<String> redirectURIsList = oAuth2Application.getRedirectURIsList();

		List hostUris = new ArrayList<>();

		for (String redirectUri : redirectURIsList) {
			try {
				URI uri = new URI(redirectUri);

				String host = uri.getScheme() + "://" + uri.getHost();

				hostUris.add(host);
			}
			catch (URISyntaxException urise) {
			}
		}

		MultivaluedMap<String, Object> headers = responseContext.getHeaders();

		headers.put("Access-Control-Allow-Origin", Arrays.asList(hostUris));
		headers.put("Access-Control-Allow-Headers", Arrays.asList("*"));
	}

}