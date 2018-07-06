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

package com.liferay.portal.servlet.filters.authverifier;

import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.security.access.control.AccessControlUtil;
import com.liferay.portal.kernel.security.auth.AccessControlContext;
import com.liferay.portal.kernel.security.auth.verifier.AuthVerifierResult;
import com.liferay.portal.kernel.servlet.HttpMethods;
import com.liferay.portal.kernel.servlet.ProtectedServletRequest;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.Http;
import com.liferay.portal.kernel.util.HttpUtil;
import com.liferay.portal.kernel.util.MapUtil;
import com.liferay.portal.kernel.util.StringBundler;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.security.auth.AuthVerifierPipeline;
import com.liferay.portal.servlet.filters.BasePortalFilter;
import com.liferay.portal.util.PropsUtil;
import com.liferay.util.servlet.NullServletOutputStream;

import java.io.IOException;

import java.util.Enumeration;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Properties;
import java.util.Set;

import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpServletResponseWrapper;

/**
 * <p>
 * See https://issues.liferay.com/browse/LPS-27888.
 * </p>
 *
 * @author Tomas Polesovsky
 * @author Raymond Augé
 */
public class AuthVerifierFilter extends BasePortalFilter {

	@Override
	public void init(FilterConfig filterConfig) {
		super.init(filterConfig);

		Enumeration<String> enu = filterConfig.getInitParameterNames();

		while (enu.hasMoreElements()) {
			String name = enu.nextElement();

			String value = filterConfig.getInitParameter(name);

			_initParametersMap.put(name, value);
		}

		String portalPropertyPrefix = GetterUtil.getString(
			_initParametersMap.get("portal_property_prefix"));

		if (Validator.isNotNull(portalPropertyPrefix)) {
			Properties properties = PropsUtil.getProperties(
				portalPropertyPrefix, true);

			for (Map.Entry<Object, Object> entry : properties.entrySet()) {
				_initParametersMap.put(
					(String)entry.getKey(), entry.getValue());
			}
		}

		if (_initParametersMap.containsKey("cors.allowed")) {
			_corsAllowed = GetterUtil.getBoolean(
				_initParametersMap.get("cors.allowed"), true);

			_initParametersMap.remove("cors.allowed");
		}

		if (_initParametersMap.containsKey("guest.allowed")) {
			_guestAllowed = GetterUtil.getBoolean(
				_initParametersMap.get("guest.allowed"), true);

			_initParametersMap.remove("guest.allowed");
		}

		if (_initParametersMap.containsKey("hosts.allowed")) {
			String hostsAllowedString = (String)_initParametersMap.get(
				"hosts.allowed");

			String[] hostsAllowed = StringUtil.split(hostsAllowedString);

			for (String hostAllowed : hostsAllowed) {
				_hostsAllowed.add(hostAllowed);
			}

			_initParametersMap.remove("hosts.allowed");
		}

		if (_initParametersMap.containsKey("https.required")) {
			_httpsRequired = GetterUtil.getBoolean(
				_initParametersMap.get("https.required"));

			_initParametersMap.remove("https.required");
		}

		if (_initParametersMap.containsKey("use_permission_checker")) {
			_initParametersMap.remove("use_permission_checker");

			if (_log.isWarnEnabled()) {
				_log.warn("use_permission_checker is deprecated");
			}
		}
	}

	@Override
	protected void processFilter(
			HttpServletRequest request, HttpServletResponse response,
			FilterChain filterChain)
		throws Exception {

		if (!_isAccessAllowed(request, response)) {
			return;
		}

		if (_isApplySSL(request, response)) {
			return;
		}

		if (_isApplyCORSPreflight(request)) {
			Class<?> clazz = getClass();

			processFilter(
				clazz.getName(), request, new CORSServletResponse(response),
				filterChain);

			return;
		}

		AccessControlUtil.initAccessControlContext(
			request, response, _initParametersMap);

		AuthVerifierResult.State state = AccessControlUtil.verifyRequest();

		AccessControlContext accessControlContext =
			AccessControlUtil.getAccessControlContext();

		AuthVerifierResult authVerifierResult =
			accessControlContext.getAuthVerifierResult();

		if (_log.isDebugEnabled()) {
			_log.debug("Auth verifier result " + authVerifierResult);
		}

		if (state == AuthVerifierResult.State.INVALID_CREDENTIALS) {
			if (_log.isDebugEnabled()) {
				_log.debug("Result state doesn't allow us to continue");
			}
		}
		else if (state == AuthVerifierResult.State.NOT_APPLICABLE) {
			_log.error("Invalid state " + state);
		}
		else if (!_guestAllowed &&
				 (state == AuthVerifierResult.State.UNSUCCESSFUL)) {

			if (_log.isDebugEnabled()) {
				_log.debug(
					"Guest is not allowed to access " +
						request.getRequestURI());
			}

			response.sendError(
				HttpServletResponse.SC_FORBIDDEN, "Authorization required");
		}
		else if (_guestAllowed || (state == AuthVerifierResult.State.SUCCESS)) {
			long userId = authVerifierResult.getUserId();

			AccessControlUtil.initContextUser(userId);

			String authType = MapUtil.getString(
				accessControlContext.getSettings(),
				AuthVerifierPipeline.AUTH_TYPE);

			ProtectedServletRequest protectedServletRequest =
				new ProtectedServletRequest(
					request, String.valueOf(userId), authType);

			accessControlContext.setRequest(protectedServletRequest);

			Class<?> clazz = getClass();

			processFilter(
				clazz.getName(), protectedServletRequest, response,
				filterChain);
		}
		else {
			_log.error("Unimplemented state " + state);
		}
	}

	private boolean _isAccessAllowed(
			HttpServletRequest request, HttpServletResponse response)
		throws IOException {

		String remoteAddr = request.getRemoteAddr();

		if (AccessControlUtil.isAccessAllowed(request, _hostsAllowed)) {
			if (_log.isDebugEnabled()) {
				_log.debug("Access allowed for " + remoteAddr);
			}

			return true;
		}

		if (_log.isWarnEnabled()) {
			_log.warn("Access denied for " + remoteAddr);
		}

		response.sendError(
			HttpServletResponse.SC_FORBIDDEN,
			"Access denied for " + remoteAddr);

		return false;
	}

	private boolean _isApplyCORSPreflight(HttpServletRequest request) {
		if (!_corsAllowed) {
			return false;
		}

		String method = request.getMethod();

		if (!StringUtil.equals(method, HttpMethods.OPTIONS)) {
			return false;
		}

		String origin = request.getHeader("Origin");

		if (Validator.isBlank(origin)) {
			return false;
		}

		String accessControlRequestMethod = request.getHeader(
			"Access-Control-Request-Method");

		if (Validator.isBlank(accessControlRequestMethod)) {
			return false;
		}

		return true;
	}

	private boolean _isApplySSL(
			HttpServletRequest request, HttpServletResponse response)
		throws IOException {

		if (!_httpsRequired || request.isSecure()) {
			return false;
		}

		if (_log.isDebugEnabled()) {
			String completeURL = HttpUtil.getCompleteURL(request);

			_log.debug("Securing " + completeURL);
		}

		StringBundler sb = new StringBundler(5);

		sb.append(Http.HTTPS_WITH_SLASH);
		sb.append(request.getServerName());
		sb.append(request.getServletPath());

		String queryString = request.getQueryString();

		if (Validator.isNotNull(queryString)) {
			sb.append(StringPool.QUESTION);
			sb.append(request.getQueryString());
		}

		if (_log.isDebugEnabled()) {
			_log.debug("Redirect to " + sb.toString());
		}

		response.sendRedirect(sb.toString());

		return true;
	}

	private static final Log _log = LogFactoryUtil.getLog(
		AuthVerifierFilter.class.getName());

	private boolean _corsAllowed = false;
	private boolean _guestAllowed = true;
	private final Set<String> _hostsAllowed = new HashSet<>();
	private boolean _httpsRequired;
	private final Map<String, Object> _initParametersMap = new HashMap<>();

	private static class CORSServletResponse
		extends HttpServletResponseWrapper {

		public static final String ACCESS_CONTROL_ALLOW_HEADERS =
			"Access-Control-Allow-Headers";

		public static final String ACCESS_CONTROL_ALLOW_ORIGIN =
			"Access-Control-Allow-Origin";

		public CORSServletResponse(HttpServletResponse response) {
			super(response);
		}

		@Override
		public ServletOutputStream getOutputStream() throws IOException {
			return new NullServletOutputStream();
		}

		@Override
		public void setHeader(String name, String value) {
			if (name.equals(ACCESS_CONTROL_ALLOW_ORIGIN) ||
				name.equals(ACCESS_CONTROL_ALLOW_HEADERS)) {

				super.setHeader(name, value);
			}
		}

	}

}