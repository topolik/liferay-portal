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

package com.liferay.token.auth.jwt;

import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.token.auth.jwt.service.JWTTokenService;
import com.liferay.token.auth.model.TokenSession;
import com.liferay.token.auth.verifier.TokenVerificationException;
import com.liferay.token.auth.verifier.TokenVerifier;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.StringTokenizer;

/**
 * @author Tomas Polesovsky
 */
@Component(
	immediate = true,
	service = TokenVerifier.class
)
public class JWTTokenVerifier implements TokenVerifier {

	@Override
	public TokenSession verify(
			HttpServletRequest request, HttpServletResponse response)
		throws TokenVerificationException {

		TokenSession tokenSession = parseBearerToken(request);

		if (tokenSession == null) {
			tokenSession = parseXApplicationHeaderToken(request);
		}

		if (tokenSession == null) {
			tokenSession = parseTokenFromParameter(request);
		}

		return tokenSession;
	}

	protected TokenSession parseBearerToken(HttpServletRequest request)
		throws TokenVerificationException {

		String authorizationHeader = request.getHeader(_AUTHORIZATION);

		String[] values = StringUtil.split(authorizationHeader);

		for (String value : values) {
			StringTokenizer stringTokenizer = new StringTokenizer(value);

			if (!stringTokenizer.hasMoreTokens()) {
				continue;
			}

			String authorization = stringTokenizer.nextToken().trim();

			if (!authorization.equalsIgnoreCase(_BEARER)) {
				continue;
			}

			if (!stringTokenizer.hasMoreTokens()) {
				continue;
			}

			String token = stringTokenizer.nextToken().trim();

			TokenSession result = _jwtTokenService.verify(token);

			if (result != null) {
				return result;
			}
		}

		return null;
	}

	protected TokenSession parseTokenFromParameter(HttpServletRequest request)
		throws TokenVerificationException {

		String token = ParamUtil.getString(request, _APPLICATION_TOKEN);

		if (Validator.isNotNull(token)) {
			return _jwtTokenService.verify(token);
		}

		return null;
	}


	protected TokenSession parseXApplicationHeaderToken(
			HttpServletRequest request)
		throws TokenVerificationException {

		String applicationToken = request.getHeader(_X_APPLICATION_TOKEN);

		if (Validator.isNotNull(applicationToken)) {
			return _jwtTokenService.verify(applicationToken);
		}

		return null;
	}

	@Reference(unbind = "-")
	protected void setJwtTokenService(JWTTokenService jwtTokenService) {
		this._jwtTokenService = jwtTokenService;
	}

	private static final String _APPLICATION_TOKEN = "applicationToken";
	private static final String _AUTHORIZATION = "Authorization";
	private static final String _BEARER = "Bearer";
	private static final String _X_APPLICATION_TOKEN = "x-application-token";

	private JWTTokenService _jwtTokenService;

}
