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

package com.liferay.multi.factor.authentication.integration.login.web.internal.security.auth.session;

import com.liferay.multi.factor.authentication.integration.spi.verifier.StringMFAVerifier;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.security.auth.AuthException;
import com.liferay.portal.kernel.security.auth.session.AuthenticatedSessionManager;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.component.annotations.ReferencePolicy;
import org.osgi.service.component.annotations.ReferencePolicyOption;

/**
 * @author Tomas Polesovsky
 */
@Component(
	enabled = false, property = "service.ranking:Integer=1",
	service = AuthenticatedSessionManager.class
)
public class MFAAuthenticatedSessionManager
	implements AuthenticatedSessionManager {

	@Override
	public long getAuthenticatedUserId(
			HttpServletRequest request, String login, String password,
			String authType)
		throws PortalException {

		return _authenticatedSessionManager.getAuthenticatedUserId(
			request, login, password, authType);
	}

	@Override
	public void login(
			HttpServletRequest request, HttpServletResponse response,
			String login, String password, boolean rememberMe, String authType)
		throws Exception {

		long userId = _authenticatedSessionManager.getAuthenticatedUserId(
			request, login, password, authType);

		if (!_stringMFAVerifier.isVerified(
				AuthenticatedSessionManager.class.getName(),
				request.getParameter("2FAToken"), userId)) {

			throw new AuthException();
		}

		_authenticatedSessionManager.login(
			request, response, login, password, rememberMe, authType);
	}

	@Override
	public void logout(HttpServletRequest request, HttpServletResponse response)
		throws Exception {

		_authenticatedSessionManager.logout(request, response);
	}

	@Override
	public HttpSession renewSession(
			HttpServletRequest request, HttpSession session)
		throws Exception {

		return _authenticatedSessionManager.renewSession(request, session);
	}

	@Override
	public void signOutSimultaneousLogins(long userId) throws Exception {
		_authenticatedSessionManager.signOutSimultaneousLogins(userId);
	}

	@Reference(
		target = "(!(component.name=com.liferay.multi.factor.authentication.integration.login.web.internal.security.auth.session.MFAAuthenticatedSessionManager))"
	)
	private AuthenticatedSessionManager _authenticatedSessionManager;

	@Reference(
		policy = ReferencePolicy.DYNAMIC,
		policyOption = ReferencePolicyOption.GREEDY
	)
	private volatile StringMFAVerifier _stringMFAVerifier;

}