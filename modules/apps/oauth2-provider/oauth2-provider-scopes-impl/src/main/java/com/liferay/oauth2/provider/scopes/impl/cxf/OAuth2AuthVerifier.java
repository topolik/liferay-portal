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

import com.liferay.oauth2.provider.scopes.liferay.api.ScopeContext;
import com.liferay.portal.kernel.security.auth.AccessControlContext;
import com.liferay.portal.kernel.security.auth.AuthException;
import com.liferay.portal.kernel.security.auth.verifier.AuthVerifier;
import com.liferay.portal.kernel.security.auth.verifier.AuthVerifierResult;
import com.liferay.portal.kernel.security.service.access.policy.ServiceAccessPolicyThreadLocal;
import com.liferay.portal.kernel.servlet.HttpHeaders;

import org.apache.cxf.rs.security.oauth2.common.OAuthPermission;
import org.apache.cxf.rs.security.oauth2.common.ServerAccessToken;
import org.apache.cxf.rs.security.oauth2.utils.OAuthUtils;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.Properties;

/**
 * @author Carlos Sierra Andrés
 */
@Component(
	immediate = true,
	property = {
		"auth.verifier.OAuth2AuthVerifier.urls.includes=#N/A#"
	}
)
public class OAuth2AuthVerifier implements AuthVerifier {

	@Override
	public String getAuthType() {
		return "OAuth2";
	}

	@Override
	public AuthVerifierResult verify(
		AccessControlContext accessControlContext, Properties properties)
		throws AuthException {

		HttpServletRequest request = accessControlContext.getRequest();

		String authorization = request.getHeader(HttpHeaders.AUTHORIZATION);

		AuthVerifierResult authVerifierResult = new AuthVerifierResult();

		try {
			String[] basicAuthParts = authorization.split(" ");

			String basicAuthPart = basicAuthParts[0];

			if (!"Bearer".equalsIgnoreCase(basicAuthPart)) {
				return new AuthVerifierResult();
			}

			String token = basicAuthParts[1];

			ServerAccessToken accessToken =
				_liferayOAuthDataProvider.getAccessToken(token);

			if (accessToken == null) {
				return new AuthVerifierResult();
			}

			_scopeContext.setTokenString(token);
			
			for (OAuthPermission scope : accessToken.getScopes()) {
				ServiceAccessPolicyThreadLocal.addActiveServiceAccessPolicyName(
					scope.getPermission());

				/*
				Do not prefix the permission, for the sake of transparency send what's granted,
				this expects the scope.permissions to be initialized with real SAP name
				 */

			}

			authVerifierResult.setState(AuthVerifierResult.State.SUCCESS);
			authVerifierResult.setUserId(
				Long.parseLong(accessToken.getSubject().getId()));

			return authVerifierResult;
		}
		catch (Exception e) {
			return new AuthVerifierResult();
		}
	}

	@Reference
	LiferayOAuthDataProvider _liferayOAuthDataProvider;

	@Reference
	ScopeContext _scopeContext;

}
