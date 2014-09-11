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

package com.liferay.token.auth.verifier;

import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.util.StringPool;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.security.auth.AccessControlContext;
import com.liferay.portal.security.auth.AuthException;
import com.liferay.portal.security.auth.AuthToken;
import com.liferay.portal.security.auth.AuthVerifier;
import com.liferay.portal.security.auth.AuthVerifierResult;
import com.liferay.registry.Registry;
import com.liferay.registry.RegistryUtil;
import com.liferay.registry.ServiceTracker;
import com.liferay.token.auth.model.TokenSession;
import com.liferay.token.auth.service.TokenSessionService;
import org.osgi.service.component.annotations.Reference;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Properties;
import java.util.StringTokenizer;

/**
 * @author Tomas Polesovsky
 */
public class TokenAuthVerifier implements AuthVerifier {

	public TokenAuthVerifier() {
		Registry registry = RegistryUtil.getRegistry();

		_serviceTracker = registry.trackServices(TokenVerifier.class);

		_serviceTracker.open();
	}

	@Override
	public String getAuthType() {
		return getClass().getName();
	}

	@Override
	public AuthVerifierResult verify(
			AccessControlContext accessControlContext, Properties properties)
		throws AuthException {

		AuthVerifierResult authVerifierResult = new AuthVerifierResult();

		try {
			TokenSession tokenSession = getTokenSession(
				accessControlContext.getRequest(),
				accessControlContext.getResponse());

			if (tokenSession == null) {
				return authVerifierResult;
			}



			authVerifierResult.setPassword("");
			authVerifierResult.setState(AuthVerifierResult.State.SUCCESS);
			authVerifierResult.setUserId(tokenSession.getUserId());

		}
		catch (TokenVerificationException e) {
			if (_log.isInfoEnabled() && Validator.isNotNull(e.getMessage())) {
				_log.info(e.getMessage());
			}

			authVerifierResult.setState(
				AuthVerifierResult.State.INVALID_CREDENTIALS);
		}
		catch (Exception e) {
			throw new AuthException(e);
		}

		return authVerifierResult;
	}

	@Reference
	public void setTokenSessionService(
		TokenSessionService tokenSessionService) {
		this._tokenSessionService = tokenSessionService;
	}

	protected TokenSession getTokenSession(
			HttpServletRequest request, HttpServletResponse response)
		throws TokenVerificationException {

		TokenVerifier[] tokenVerifiers = _serviceTracker.getServices(
			new TokenVerifier[0]);

		for (TokenVerifier tokenVerifier : tokenVerifiers) {
			TokenSession tokenSession = tokenVerifier.verify(request, response);
			if (tokenSession != null) {
				return tokenSession;
			}
		}

		return null;
	}

	protected String getValidToken(HttpServletRequest request) {
		String authorizationHeader = request.getHeader(_AUTHORIZATION);

		String[] values = StringUtil.split(authorizationHeader);

		for (String value : values) {
			StringTokenizer stringTokenizer = new StringTokenizer(value);

			String authorization = stringTokenizer.nextToken();

			if (!authorization.equalsIgnoreCase(_BEARER)) {
				continue;
			}

			String token = stringTokenizer.nextToken();

			if (verifyToken(token)) {
				return token;
			}
		}

		String applicationToken = request.getHeader(_X_APPLICATION_TOKEN);
		if (verifyToken(applicationToken)) {
			return applicationToken;
		}

		return null;
	}

	protected boolean verifyToken(String token) {
		return false;
	}

	private ServiceTracker<?, TokenVerifier> _serviceTracker;
	private TokenSessionService _tokenSessionService;

	private static final String _APPLICATION_TOKEN = "applicationToken";
	private static final String _AUTHORIZATION = "Authorization";
	private static final String _BEARER = "Bearer";
	private static final String _X_APPLICATION_TOKEN = "x-application-token";

	private static Log _log = LogFactoryUtil.getLog(
		TokenAuthVerifier.class);

}
