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

package com.liferay.oauth2.provider.rest.internal.endpoint.access.token;

import com.liferay.oauth2.provider.rest.internal.endpoint.constants.OAuth2ProviderRestEndpointConstants;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.util.Validator;

import java.net.InetAddress;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.UnknownHostException;

import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.Response;

import org.apache.cxf.jaxrs.ext.MessageContext;
import org.apache.cxf.rs.security.oauth2.common.Client;
import org.apache.cxf.rs.security.oauth2.services.AccessTokenService;

/**
 * @author Tomas Polesovsky
 */
@Path("/token")
public class LiferayAccessTokenService extends AccessTokenService {

	@Consumes("application/x-www-form-urlencoded")
	@Override
	@POST
	@Produces("application/json")
	public Response handleTokenRequest(MultivaluedMap<String, String> params) {
		Response response = super.handleTokenRequest(params);

		Client client = authenticateClientIfNeeded(params);

		MultivaluedMap<String, Object> headers = response.getHeaders();

		String origin = _httpHeaders.getHeaderString("Origin");

		if (Validator.isBlank(origin)) {
			return response;
		}

		for (String redirect : client.getRedirectUris()) {
			try {
				URI originURI = new URI(origin);
				URI redirectURI = new URI(redirect);

				String originHost = originURI.getHost();
				String redirectHost = redirectURI.getHost();

				if (originHost.equals(redirectHost)) {
					headers.putSingle(
						"Accept", "application/x-www-form-urlencoded");
					headers.putSingle("Access-Control-Allow-Methods", "POST");
					headers.putSingle("Access-Control-Allow-Origin", origin);
					headers.putSingle("Content-Type", "application/json");
				}
			}
			catch (URISyntaxException urise) {
				if (_log.isWarnEnabled()) {
					_log.warn("Invalid callback uri", urise);
				}
			}
		}

		return response;
	}

	@Override
	protected Client authenticateClientIfNeeded(
		MultivaluedMap<String, String> params) {

		Client client = super.authenticateClientIfNeeded(params);

		Map<String, String> properties = client.getProperties();

		MessageContext messageContext = getMessageContext();

		HttpServletRequest httpServletRequest =
			messageContext.getHttpServletRequest();

		String remoteAddr = httpServletRequest.getRemoteAddr();

		String remoteHost = httpServletRequest.getRemoteHost();

		try {
			InetAddress inetAddress = InetAddress.getByName(remoteAddr);

			remoteHost = inetAddress.getCanonicalHostName();
		}
		catch (UnknownHostException uhe) {
		}

		properties.put(
			OAuth2ProviderRestEndpointConstants.PROPERTY_KEY_CLIENT_REMOTE_ADDR,
			remoteAddr);
		properties.put(
			OAuth2ProviderRestEndpointConstants.PROPERTY_KEY_CLIENT_REMOTE_HOST,
			remoteHost);

		return client;
	}

	private static final Log _log = LogFactoryUtil.getLog(
		LiferayAccessTokenService.class);

	@Context
	private HttpHeaders _httpHeaders;

}