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

package com.liferay.multi.factor.authentication.integration.auth.verifier.internal.servlet.filter;

import com.liferay.multi.factor.authentication.integration.auth.verifier.spi.AuthVerifierMFAVerifier;
import com.liferay.multi.factor.authentication.integration.spi.verifier.MFAVerifierRegistry;
import com.liferay.petra.string.StringBundler;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.security.access.control.AccessControlUtil;
import com.liferay.portal.kernel.security.auth.AccessControlContext;
import com.liferay.portal.kernel.security.auth.verifier.AuthVerifierResult;
import com.liferay.portal.kernel.servlet.BaseFilter;

import java.util.Objects;

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
	immediate = true,
	property = {
		"after-filter=Upload Servlet Request Filter", "servlet-context-name=",
		"servlet-filter-name=MFA Auth Verifier Filter", "url-pattern=/",
		"url-pattern=/*"
	},
	service = Filter.class
)
public class MFAAuthVerifierFilter extends BaseFilter {

	@Reference(unbind = "-")
	public void setMfaVerifierRegistry(
		MFAVerifierRegistry mfaVerifierRegistry) {

		_mfaVerifierRegistry = mfaVerifierRegistry;
	}

	@Override
	protected Log getLog() {
		return _log;
	}

	@Override
	protected void processFilter(
			HttpServletRequest request, HttpServletResponse response,
			FilterChain filterChain)
		throws Exception {

		AccessControlContext accessControlContext =
			AccessControlUtil.getAccessControlContext();

		if (accessControlContext == null) {
			super.processFilter(request, response, filterChain);

			return;
		}

		AuthVerifierResult authVerifierResult =
			accessControlContext.getAuthVerifierResult();

		if ((authVerifierResult == null) ||
			!authVerifierResult.isPasswordBasedAuthentication()) {

			super.processFilter(request, response, filterChain);

			return;
		}

		if (Objects.equals(
				HttpServletRequest.FORM_AUTH, request.getAuthType())) {

			super.processFilter(request, response, filterChain);

			return;
		}

		AuthVerifierMFAVerifier apiClientMFAVerifier =
			_mfaVerifierRegistry.getMFAVerifier(AuthVerifierMFAVerifier.class);

		if (apiClientMFAVerifier == null) {
			super.processFilter(request, response, filterChain);

			return;
		}

		long userId = authVerifierResult.getUserId();

		if (apiClientMFAVerifier.needsVerification(request, userId)) {
			if (!apiClientMFAVerifier.verify(userId, request, response)) {
				if (_log.isWarnEnabled()) {
					_log.warn(
						StringBundler.concat(
							"Unable to verify Multi Factor Authentication ",
							"token for ", request.getPathInfo()));
				}

				response.sendError(
					HttpServletResponse.SC_FORBIDDEN, "Two Factor Required");

				return;
			}
		}

		super.processFilter(request, response, filterChain);
	}

	private static final Log _log = LogFactoryUtil.getLog(
		MFAAuthVerifierFilter.class);

	private MFAVerifierRegistry _mfaVerifierRegistry;

}