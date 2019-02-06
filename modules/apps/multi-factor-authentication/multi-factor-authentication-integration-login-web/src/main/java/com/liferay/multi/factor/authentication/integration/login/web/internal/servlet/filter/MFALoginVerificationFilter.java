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

package com.liferay.multi.factor.authentication.integration.login.web.internal.servlet.filter;

import com.liferay.multi.factor.authentication.integration.login.web.spi.LoginWebMFAVerifier;
import com.liferay.multi.factor.authentication.integration.spi.verifier.MFAVerifierRegistry;
import com.liferay.petra.string.StringBundler;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.security.auth.session.AuthenticatedSessionManager;
import com.liferay.portal.kernel.servlet.BaseFilter;
import com.liferay.portal.kernel.util.Http;
import com.liferay.portal.kernel.util.Portal;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Tomas Polesovsky
 */
@Component(
	enabled = false, immediate = true,
	property = {
		"after-filter=Auto Login Filter", "servlet-context-name=",
		"servlet-filter-name=MFA Login Verification Filter", "url-pattern=/",
		"url-pattern=/*"
	},
	service = Filter.class
)
public class MFALoginVerificationFilter extends BaseFilter {

	@Override
	protected Log getLog() {
		return _log;
	}

	@Override
	protected void processFilter(
			HttpServletRequest request, HttpServletResponse response,
			FilterChain filterChain)
		throws Exception {

		LoginWebMFAVerifier loginWebMFAVerifier =
			_mfaVerifierRegistry.getMFAVerifier(LoginWebMFAVerifier.class);

		if (loginWebMFAVerifier == null) {
			super.processFilter(request, response, filterChain);

			return;
		}

		long userId = _portal.getUserId(request);

		if (userId == 0) {
			super.processFilter(request, response, filterChain);

			return;
		}

		if (loginWebMFAVerifier.needsVerification(request, userId)) {
			_authenticatedSessionManager.logout(request, response);

			String redirect = _portal.getPathMain() + "/portal/login";

			String currentURL = _portal.getCurrentURL(request);

			redirect = _http.addParameter(redirect, "redirect", currentURL);

			response.sendRedirect(redirect);

			if (_log.isWarnEnabled()) {
				_log.warn(
					StringBundler.concat(
						"User ", userId, " must be verified using Multi ",
						"Factor Authentication!"));
			}

			return;
		}

		super.processFilter(request, response, filterChain);
	}

	private static final Log _log = LogFactoryUtil.getLog(
		MFALoginVerificationFilter.class);

	@Reference
	private AuthenticatedSessionManager _authenticatedSessionManager;

	@Reference
	private Http _http;

	@Reference
	private MFAVerifierRegistry _mfaVerifierRegistry;

	@Reference
	private Portal _portal;

}