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

package com.liferay.multi.factor.authentication.integration.login.web.internal.security.auth.http;

import com.liferay.multi.factor.authentication.integration.spi.verifier.StringMFAVerifier;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.security.auth.http.HttpAuthManager;
import com.liferay.portal.kernel.security.auth.http.HttpAuthorizationHeader;

import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.component.annotations.ReferencePolicy;
import org.osgi.service.component.annotations.ReferencePolicyOption;

/**
 * @author Tomas Polesovsky
 */
@Component(
	enabled = false,
	property = {"request.header.name=X-2FA-Token", "service.ranking:Integer=1"},
	service = HttpAuthManager.class
)
public class MFAHttpAuthManagerImpl implements HttpAuthManager {

	@Activate
	public void activate(Map<String, Object> properties) {
		_requestHeaderName = (String)properties.get("request.header.name");
	}

	@Override
	public void generateChallenge(
		HttpServletRequest httpServletRequest,
		HttpServletResponse httpServletResponse,
		HttpAuthorizationHeader httpAuthorizationHeader) {

		_httpAuthManager.generateChallenge(
			httpServletRequest, httpServletResponse, httpAuthorizationHeader);
	}

	@Override
	public long getBasicUserId(HttpServletRequest httpServletRequest)
		throws PortalException {

		long userId = _httpAuthManager.getBasicUserId(httpServletRequest);

		if (!_stringMFAVerifier.isVerified(
				HttpAuthManager.class.getName(),
				httpServletRequest.getHeader(_requestHeaderName), userId)) {

			return 0;
		}

		return userId;
	}

	@Override
	public long getDigestUserId(HttpServletRequest httpServletRequest)
		throws PortalException {

		long userId = _httpAuthManager.getDigestUserId(httpServletRequest);

		if (!_stringMFAVerifier.isVerified(
				HttpAuthManager.class.getName(),
				httpServletRequest.getHeader(_requestHeaderName), userId)) {

			return 0;
		}

		return userId;
	}

	@Override
	public long getUserId(
			HttpServletRequest httpServletRequest,
			HttpAuthorizationHeader httpAuthorizationHeader)
		throws PortalException {

		long userId = _httpAuthManager.getUserId(
			httpServletRequest, httpAuthorizationHeader);

		if (!_stringMFAVerifier.isVerified(
				HttpAuthManager.class.getName(),
				httpServletRequest.getHeader(_requestHeaderName), userId)) {

			return 0;
		}

		return userId;
	}

	@Override
	public HttpAuthorizationHeader parse(
		HttpServletRequest httpServletRequest) {

		return _httpAuthManager.parse(httpServletRequest);
	}

	@Reference(
		target = "(!(component.name=com.liferay.multi.factor.authentication.integration.login.web.internal.security.auth.http.MFAHttpAuthManagerImpl))"
	)
	private HttpAuthManager _httpAuthManager;

	private String _requestHeaderName;

	@Reference(
		policy = ReferencePolicy.DYNAMIC,
		policyOption = ReferencePolicyOption.GREEDY
	)
	private volatile StringMFAVerifier _stringMFAVerifier;

}