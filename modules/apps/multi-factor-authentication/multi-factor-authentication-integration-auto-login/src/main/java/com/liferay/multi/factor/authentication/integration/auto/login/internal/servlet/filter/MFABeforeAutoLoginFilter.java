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

package com.liferay.multi.factor.authentication.integration.auto.login.internal.servlet.filter;

import com.liferay.multi.factor.authentication.api.MFARegistry;
import com.liferay.multi.factor.authentication.integration.auto.login.internal.servlet.http.IgnoreAutoLoginFilterHttpServletRequestWrapper;
import com.liferay.multi.factor.authentication.integration.auto.login.internal.spi.integration.AutoLoginMFAIntegration;
import com.liferay.multi.factor.authentication.portlet.api.MFAPortletURLFactory;
import com.liferay.multi.factor.authentication.spi.verifier.BrowserMFAVerifier;
import com.liferay.petra.encryptor.Encryptor;
import com.liferay.petra.string.CharPool;
import com.liferay.portal.kernel.json.JSONFactory;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.portlet.LiferayPortletURL;
import com.liferay.portal.kernel.security.auth.PrincipalException;
import com.liferay.portal.kernel.security.auto.login.AutoLogin;
import com.liferay.portal.kernel.service.UserLocalServiceUtil;
import com.liferay.portal.kernel.servlet.ProtectedServletRequest;
import com.liferay.portal.kernel.util.Accessor;
import com.liferay.portal.kernel.util.DigesterUtil;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.Http;
import com.liferay.portal.kernel.util.ListUtil;
import com.liferay.portal.kernel.util.MapUtil;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.kernel.util.Portal;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.servlet.filters.autologin.AutoLoginFilter;
import com.liferay.portal.util.PropsValues;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.security.Key;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Vector;

/**
 * @author Tomas Polesovsky
 */
@Component(
	immediate = true,
	property = {
		"before-filter=Auto Login Filter", "servlet-context-name=",
		"servlet-filter-name=MFA Before Auto Login Filter", "url-pattern=/",
		"url-pattern=/*"
	},
	service = Filter.class
)
public class MFABeforeAutoLoginFilter extends AutoLoginFilter {

	@Override
	public boolean isFilterEnabled() {
		BrowserMFAVerifier browserMFAVerifier =
			_mfaVerifierRegistry.getMFAVerifier(_autoLoginMFAIntegration);

		if (browserMFAVerifier == null) {
			return false;
		}

		return super.isFilterEnabled();
	}

	@Override
	protected Log getLog() {
		return _log;
	}

	@Override
	protected String getLoginRemoteUser(
			HttpServletRequest request, HttpServletResponse response,
			HttpSession session, String[] credentials)
		throws Exception {

		if ((credentials == null) || (credentials.length != 3)) {
			return null;
		}

		String jUsername = credentials[0];
		String jPassword = credentials[1];

		if (Validator.isNull(jUsername) || Validator.isNull(jPassword)) {
			return null;
		}

		long userId = GetterUtil.getLong(jUsername);

		if (userId <= 0) {
			return null;
		}

		User user = UserLocalServiceUtil.fetchUserById(userId);

		if ((user == null) || user.isLockout()) {
			return null;
		}

		BrowserMFAVerifier browserMFAVerifier =
			_mfaVerifierRegistry.getMFAVerifier(_autoLoginMFAIntegration);

		if (!browserMFAVerifier.needsVerification(request, userId)){
			return super.getLoginRemoteUser(
				request, response, session, credentials);
		}

		Map<String, Object> stateMap = new HashMap<>();

		stateMap.put("credentials", credentials);

		stateMap.put(
			AutoLogin.AUTO_LOGIN_REDIRECT_AND_CONTINUE,
			request.getAttribute(AutoLogin.AUTO_LOGIN_REDIRECT_AND_CONTINUE));

		stateMap.put("requestParameters", request.getParameterMap());

		String encryptedStateMap = _encrypt(session, stateMap);

		String callbackURL = _portal.getCurrentURL(request);

		int pos = callbackURL.indexOf(CharPool.QUESTION);

		if (pos > 0) {
			callbackURL = callbackURL.substring(0, pos);
		}

		_http.setParameter(callbackURL, "state", encryptedStateMap);

		LiferayPortletURL verificationURL =
			_mfaPortletURLFactory.createVerifyURL(
				request, _autoLoginMFAIntegration.getName(), callbackURL,
				userId);

		request.setAttribute(
			AutoLogin.AUTO_LOGIN_REDIRECT_AND_CONTINUE,
			verificationURL.toString());

		return jUsername;
	}

	@Override
	protected void processFilter(
			HttpServletRequest request, HttpServletResponse response,
			FilterChain filterChain)
		throws Exception {

		String state = ParamUtil.getString(request, "state");

		if (!Validator.isBlank(state)) {
			Map<String, Object> stateMap = _decrypt(
				request.getSession(), state);

			Map<String, String[]> requestParameters =
				(Map<String, String[]>) stateMap.get(
					"requestParameters");

			request = new HttpServletRequestWrapper(request) {
				@Override
				public String getParameter(String name) {
					return MapUtil.getString(requestParameters, name, null);
				}

				@Override
				public Map<String, String[]> getParameterMap() {
					return new HashMap<>(requestParameters);
				}

				@Override
				public Enumeration<String> getParameterNames() {
					return new Vector(requestParameters.keySet()).elements();
				}

				@Override
				public String[] getParameterValues(String name) {
					return requestParameters.get(name);
				}
			};

			request = _finishAutoLogin(request, response, stateMap);
		}
		else {
			super.processFilter(request, response, filterChain);
		}

		request = new IgnoreAutoLoginFilterHttpServletRequestWrapper(request);

		super.processFilter(
			MFABeforeAutoLoginFilter.class.getName(), request, response,
			filterChain);
	}

	private String _encrypt(
		HttpSession session, Map<String, Object> stateMap)
		throws Exception {

		String stateMapJSON = _jsonFactory.looseSerialize(stateMap);

		Key key = Encryptor.generateKey();

		String encryptedStateMapJSON = Encryptor.encrypt(
			key, stateMapJSON);

		session.setAttribute(
			"digest", DigesterUtil.digest(encryptedStateMapJSON));

		session.setAttribute("key", key);

		return encryptedStateMapJSON;
	}

	private Map<String, Object> _decrypt(
		HttpSession session, String encryptedStateMapJSON)
		throws Exception {

		String digest = (String)session.getAttribute("digest");

		if (!StringUtil.equals(
			DigesterUtil.digest(encryptedStateMapJSON), digest)) {

			throw new PrincipalException("User sent unverified data");
		}

		Key key = (Key)session.getAttribute("key");

		String stateMapJSON = Encryptor.decrypt(key, encryptedStateMapJSON);

		Map<String, Object> stateMap = _jsonFactory.looseDeserialize(
			stateMapJSON, Map.class);

		Map<String, Object> requestParameters =
			(Map<String, Object>) stateMap.get("requestParameters");

		for (Map.Entry<String, Object> entry : requestParameters.entrySet()) {
			Object value = entry.getValue();

			if (value instanceof List) {
				entry.setValue(
					ListUtil.toArray((List<Object>)value, _STRING_ACCESSOR));
			}
		}

		return stateMap;
	}

	private static final Accessor<Object, String> _STRING_ACCESSOR =
		new Accessor<Object, String>() {
			@Override
			public String get(Object object) {
				return String.valueOf(object);
			}

			@Override
			public Class<String> getAttributeClass() {
				return String.class;
			}

			@Override
			public Class<Object> getTypeClass() {
				return Object.class;
			}
		};

	private HttpServletRequest _finishAutoLogin(
			HttpServletRequest request, HttpServletResponse response,
			Map<String, Object> autoLoginStateMap)
		throws Exception {

		List<String> credentialsList = (List<String>)autoLoginStateMap.get(
			"credentials");

		String[] credentials = credentialsList.toArray(new String[3]);

		String loginRemoteUser = super.getLoginRemoteUser(
			request, response, request.getSession(), credentials);

		if (loginRemoteUser != null) {
			request = new ProtectedServletRequest(
				request, loginRemoteUser);

			long userId = GetterUtil.getLong(loginRemoteUser);

			BrowserMFAVerifier browserMFAVerifier =
				_mfaVerifierRegistry.getMFAVerifier(_autoLoginMFAIntegration);

			browserMFAVerifier.setupSessionAfterVerification(request, userId);

			if (PropsValues.PORTAL_JAAS_ENABLE) {
				return request;
			}

			String redirect = null;

			if (!PropsValues.AUTH_FORWARD_BY_LAST_PATH) {
				redirect = Portal.PATH_MAIN;
			}
			else {
				redirect = (String)autoLoginStateMap.get(
					AutoLogin.AUTO_LOGIN_REDIRECT_AND_CONTINUE);
			}

			if (Validator.isNotNull(redirect)) {
				response.sendRedirect(redirect);
			}
		}

		return request;
	}

	private static final Log _log = LogFactoryUtil.getLog(
		MFABeforeAutoLoginFilter.class);

	@Reference
	private MFARegistry _mfaVerifierRegistry;

	@Reference
	private AutoLoginMFAIntegration _autoLoginMFAIntegration;

	@Reference
	private MFAPortletURLFactory _mfaPortletURLFactory;

	@Reference
	private Portal _portal;

	@Reference
	private JSONFactory _jsonFactory;

	@Reference
	private Http _http;
}