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

package com.liferay.portal.security.auth;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * @author Tomas Polesovsky
 */
public class AuthenticationContext {

	public AuthenticationConfig getAuthenticationConfig() {
		return _authenticationConfig;
	}

	public AuthenticationPhase getAuthenticationPhase() {
		return _authenticationPhase;
	}

	public AuthenticationResult getAuthenticationResult() {
		return _authenticationResult;
	}

	public HttpServletRequest getHttpServletRequest() {
		return _httpServletRequest;
	}

	public HttpServletResponse getHttpServletResponse() {
		return _httpServletResponse;
	}

	public boolean isAccessDenied() {
		return _accessDenied;
	}

	public void setAccessDenied(boolean accessDenied) {
		_accessDenied = accessDenied;
	}

	public void setAuthenticationConfig(
		AuthenticationConfig authenticationConfig) {

		_authenticationConfig = authenticationConfig;
	}

	public void setAuthenticationPhase(
		AuthenticationPhase authenticationPhase) {

		_authenticationPhase = authenticationPhase;
	}

	public void setAuthenticationResult(
		AuthenticationResult authenticationResult) {

		this._authenticationResult = authenticationResult;
	}

	public void setRequest(HttpServletRequest request) {
		_httpServletRequest = request;
	}

	public void setResponse(HttpServletResponse response) {
		_httpServletResponse = response;
	}

	private boolean _accessDenied;
	private AuthenticationConfig _authenticationConfig;
	private AuthenticationPhase _authenticationPhase;
	private AuthenticationResult _authenticationResult;
	private HttpServletRequest _httpServletRequest;
	private HttpServletResponse _httpServletResponse;

}