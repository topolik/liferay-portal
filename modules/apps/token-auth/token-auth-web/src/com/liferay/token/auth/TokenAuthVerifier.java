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

package com.liferay.token.auth;

import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.security.auth.AccessControlContext;
import com.liferay.portal.security.auth.AuthException;
import com.liferay.portal.security.auth.AuthVerifier;
import com.liferay.portal.security.auth.AuthVerifierResult;
import com.liferay.registry.Registry;
import com.liferay.registry.RegistryUtil;
import com.liferay.registry.ServiceTracker;
import com.liferay.token.auth.model.TokenClient;
import com.liferay.token.auth.model.TokenSession;
import com.liferay.token.auth.permission.SystemPermissionChecker;
import com.liferay.token.auth.service.TokenClientService;
import com.liferay.token.auth.verifier.TokenVerificationException;
import com.liferay.token.auth.verifier.TokenVerifier;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.security.PrivilegedExceptionAction;
import java.util.Map;
import java.util.Properties;

/**
 * @author Tomas Polesovsky
 */
@Component(
	immediate = true,
	property = {
		"hosts.allowed=",
		"urls.includes=/api/*,/xmlrpc/*",
		"urls.excludes=/api/liferay/do"
	},
	service = AuthVerifier.class
)
public class TokenAuthVerifier implements AuthVerifier {

	public TokenAuthVerifier() {
		Registry registry = RegistryUtil.getRegistry();

		_tokenVerifierServiceTracker = registry.trackServices(
			TokenVerifier.class);
		_tokenVerifierServiceTracker.open();
	}

	@Override
	public String getAuthType() {
		return AUTH_TYPE;
	}

	@Override
	public AuthVerifierResult verify(
			AccessControlContext accessControlContext, Properties properties)
		throws AuthException {

		AuthVerifierResult authVerifierResult = new AuthVerifierResult();

		try {
			final TokenSession tokenSession = getTokenSession(
				accessControlContext.getRequest(),
				accessControlContext.getResponse());

			if (tokenSession == null) {
				return authVerifierResult;
			}

			TokenClient tokenClient = SystemPermissionChecker.runAsSystem(
				new PrivilegedExceptionAction<TokenClient>(){
				@Override
				public TokenClient run() throws Exception {
					return _tokenClientService.findById(
						tokenSession.getTokenClientId());
				}
			});

			if (!tokenClient.getState().equals(TokenClient.State.VALID)) {
				if (_log.isWarnEnabled()) {
					_log.warn(
						"Attempt to access API using revoked token! " +
							"Remote IP: " +
							accessControlContext.getRequest().getRemoteAddr() +
							" TokenSession: " + tokenSession);
				}

				throw new AuthException("The application has been revoked!");
			}

			Map<String, Object> settings = authVerifierResult.getSettings();
			settings.put(TOKEN_CLIENT, tokenClient);
			settings.put(TOKEN_SESSION, tokenSession);

			authVerifierResult.setPassword("");
			authVerifierResult.setState(AuthVerifierResult.State.SUCCESS);
			authVerifierResult.setUserId(tokenSession.getUserId());

		}
		catch (TokenVerificationException e) {
			if (_log.isDebugEnabled()) {
				_log.debug(e);
			}

			authVerifierResult.setState(
				AuthVerifierResult.State.INVALID_CREDENTIALS);
		}
		catch (Exception e) {
			throw new AuthException(e);
		}

		return authVerifierResult;
	}

	protected TokenSession getTokenSession(
			HttpServletRequest request, HttpServletResponse response)
		throws TokenVerificationException {

		TokenVerifier[] tokenVerifiers =
			_tokenVerifierServiceTracker.getServices(new TokenVerifier[0]);

		for (TokenVerifier tokenVerifier : tokenVerifiers) {
			TokenSession tokenSession = tokenVerifier.verify(request, response);
			if (tokenSession != null) {
				return tokenSession;
			}
		}

		return null;
	}

	@Reference
	protected void setTokenClientService(
		TokenClientService tokenClientService) {

		this._tokenClientService = tokenClientService;
	}

	public static final String AUTH_TYPE = TokenAuthVerifier.class.getName();

	public static final String TOKEN_CLIENT =
		TokenAuthVerifier.class.getName() + "_TOKEN_CLIENT";

	public static final String TOKEN_SESSION =
		TokenAuthVerifier.class.getName() + "_TOKEN_SESSION";

	private ServiceTracker<?, TokenVerifier>
		_tokenVerifierServiceTracker;

	private TokenClientService _tokenClientService;

	private static Log _log = LogFactoryUtil.getLog(
		TokenAuthVerifier.class);

}

