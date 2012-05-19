/**
 * Copyright (c) 2000-2012 Liferay, Inc. All rights reserved.
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

package com.liferay.portal.servlet.filters.secure;

import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.servlet.ProtectedServletRequest;
import com.liferay.portal.kernel.util.Http;
import com.liferay.portal.kernel.util.HttpUtil;
import com.liferay.portal.kernel.util.StringBundler;
import com.liferay.portal.kernel.util.StringPool;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.security.PortalAuthenticationManager;
import com.liferay.portal.security.auth.AuthenticationConfig;
import com.liferay.portal.security.auth.AuthenticationContext;
import com.liferay.portal.security.auth.AuthenticationResult;
import com.liferay.portal.servlet.filters.BasePortalFilter;
import com.liferay.portal.util.PropsValues;

import java.io.IOException;

import javax.servlet.FilterChain;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Portal Authentication filter with 2 phase authentication.<br />
 * <ul><li>1st phase is triggered before underlying API (pre-API call) and is
 * responsible to invoke required authenticators. In this phase all
 * required {@link com.liferay.portal.security.auth.PortalAuthenticator}s
 * must be successful.</li>
 * <li>2nd phase is called only on SecureMethodInvocationException (post-API
 * call), when guest is trying to access method requiring authentication.
 * First successful {@link com.liferay.portal.security.auth.PortalAuthenticator}
 * initiates another call of the underlying API</li>
 * </ul>
 * @author Tomas Polesovsky
 */
public class PortalAuthenticationFilter extends BasePortalFilter {

	protected boolean applyHttps(
			HttpServletRequest httpServletRequest,
			HttpServletResponse httpServletResponse)
		throws IOException {

		PortalAuthenticationManager portalAuthenticationManager =
			PortalAuthenticationManager.getInstance();

		AuthenticationContext authenticationContext =
			portalAuthenticationManager.getAuthenticationContext();

		AuthenticationConfig authenticationConfig =
			authenticationContext.getAuthenticationConfig();

		if (authenticationConfig.isSecure() || httpServletRequest.isSecure()) {
			return false;
		}

		if (_log.isDebugEnabled()) {
			String completeURL = HttpUtil.getCompleteURL(httpServletRequest);

			_log.debug("Securing " + completeURL);
		}

		StringBundler redirectURL = new StringBundler(5);

		redirectURL.append(Http.HTTPS_WITH_SLASH);
		redirectURL.append(httpServletRequest.getServerName());
		redirectURL.append(httpServletRequest.getServletPath());

		String queryString = httpServletRequest.getQueryString();

		if (Validator.isNotNull(queryString)) {
			redirectURL.append(StringPool.QUESTION);
			redirectURL.append(httpServletRequest.getQueryString());
		}

		if (_log.isDebugEnabled()) {
			_log.debug("Redirect to " + redirectURL);
		}

		httpServletResponse.sendRedirect(redirectURL.toString());

		return true;
	}

	protected void authenticate(
			HttpServletRequest httpServletRequest,
			HttpServletResponse httpServletResponse, FilterChain filterChain,
			boolean applyRequiredAuthenticators)
		throws Exception {

		PortalAuthenticationManager portalAuthenticationManager =
			PortalAuthenticationManager.getInstance();

		AuthenticationResult authenticationResult =
			portalAuthenticationManager.authenticate(
				httpServletRequest, httpServletResponse,
				applyRequiredAuthenticators);

		// there was an authenticator in the pipeline
		if (authenticationResult != null) {

			AuthenticationResult.State resultState =
				authenticationResult.getState();

			if (resultState == AuthenticationResult.State.IN_PROGRESS) {
				return;
			}

			if (applyRequiredAuthenticators &&
				(resultState ==
					AuthenticationResult.State.INVALID_CREDENTIALS)) {

				httpServletResponse.setStatus(
					HttpServletResponse.SC_UNAUTHORIZED);

				return;
			}

			if (resultState == AuthenticationResult.State.SUCCESS) {
				httpServletRequest = new ProtectedServletRequest(
					httpServletRequest,
					String.valueOf(authenticationResult.getUserId()));
			}
		}

		// we don't need to continue if optional authentication didn't
		// changed current user
		if ((authenticationResult == null) && !applyRequiredAuthenticators) {
			httpServletResponse.setStatus(HttpServletResponse.SC_UNAUTHORIZED);

			return;
		}

		// call inner API
		processFilter(
			getClass(), httpServletRequest, httpServletResponse, filterChain);
	}

	protected AuthenticationConfig initAuthenticationConfig(
			HttpServletRequest request) {

		return new ServletFilterAuthenticationLoader().load(request);
	}

	protected void initAuthenticationContext(AuthenticationConfig config,
			HttpServletRequest request, HttpServletResponse response) {

		PortalAuthenticationManager pam =
			PortalAuthenticationManager.getInstance();

		AuthenticationContext context = new AuthenticationContext();

		context.setAuthenticationConfig(config);
		context.setRequest(request);
		context.setResponse(response);

		pam.setAuthenticationContext(context);
	}

	@Override
	protected void processFilter(
			HttpServletRequest request, HttpServletResponse response,
			FilterChain filterChain)
		throws Exception {

		AuthenticationConfig config = initAuthenticationConfig(request);

		initAuthenticationContext(config, request, response);

		if (applyHttps(request, response)) {
			return;
		}

		if (PropsValues.PORTAL_JAAS_ENABLE) {
			processFilter(getClass(), request, response, filterChain);

			return;
		}

		try {
			// 1st authentication phase
			authenticate(request, response, filterChain, true);
		}
		catch (Throwable ex) {
			Throwable cause = ex;

			while (!(cause instanceof SecurityException) &&
					(cause.getCause() != null)) {

				cause = cause.getCause();
			}

			if (!(cause instanceof SecurityException)) {
				throw new RuntimeException(ex);
			}

			// 2nd authentication phase
			authenticate(request, response, filterChain, false);
		}

	}

	private static Log _log = LogFactoryUtil.getLog(
		PortalAuthenticationFilter.class.getName());

}