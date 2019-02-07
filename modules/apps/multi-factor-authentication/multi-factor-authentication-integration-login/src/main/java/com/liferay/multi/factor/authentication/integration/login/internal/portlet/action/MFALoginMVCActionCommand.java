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

package com.liferay.multi.factor.authentication.integration.login.internal.portlet.action;

import com.liferay.multi.factor.authentication.api.MFARegistry;
import com.liferay.multi.factor.authentication.integration.login.internal.constants.LoginPortletKeys;
import com.liferay.multi.factor.authentication.integration.login.internal.spi.integration.LoginMFAIntegration;
import com.liferay.multi.factor.authentication.portlet.api.MFAPortletURLFactory;
import com.liferay.multi.factor.authentication.spi.verifier.BrowserMFAVerifier;
import com.liferay.petra.encryptor.Encryptor;
import com.liferay.portal.kernel.json.JSONFactory;
import com.liferay.portal.kernel.portlet.LiferayPortletResponse;
import com.liferay.portal.kernel.portlet.LiferayPortletURL;
import com.liferay.portal.kernel.portlet.bridges.mvc.BaseMVCActionCommand;
import com.liferay.portal.kernel.portlet.bridges.mvc.MVCActionCommand;
import com.liferay.portal.kernel.security.auth.PrincipalException;
import com.liferay.portal.kernel.security.auth.session.AuthenticatedSessionManagerUtil;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.Accessor;
import com.liferay.portal.kernel.util.DigesterUtil;
import com.liferay.portal.kernel.util.ListUtil;
import com.liferay.portal.kernel.util.MapUtil;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.kernel.util.Portal;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.kernel.util.WebKeys;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

import javax.portlet.ActionRequest;
import javax.portlet.ActionResponse;
import javax.portlet.ActionURL;
import javax.portlet.PortletException;
import javax.portlet.PortletURL;
import javax.portlet.filter.ActionRequestWrapper;
import javax.portlet.filter.ActionResponseWrapper;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.io.IOException;
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
	property = {
		"javax.portlet.name=" + LoginPortletKeys.FAST_LOGIN,
		"javax.portlet.name=" + LoginPortletKeys.LOGIN,
		"mvc.command.name=/login/login", "service.ranking:Integer=1"
	},
	service = MVCActionCommand.class
)
public class MFALoginMVCActionCommand extends BaseMVCActionCommand {

	@Override
	protected void doProcessAction(
			ActionRequest actionRequest, ActionResponse actionResponse)
		throws Exception {

		BrowserMFAVerifier browserMFAVerifier =
			_mfaVerifierRegistry.getMFAVerifier(_loginMFAIntegration);

		if (browserMFAVerifier == null) {
			_loginMVCActionCommand.processAction(actionRequest, actionResponse);

			return;
		}

		String state = ParamUtil.getString(actionRequest, "state");

		if (!Validator.isBlank(state)) {
			actionRequest = _loadRequestFromState(
				actionRequest, actionResponse, state);
		}

		ThemeDisplay themeDisplay = (ThemeDisplay)actionRequest.getAttribute(
			WebKeys.THEME_DISPLAY);

		if (!themeDisplay.isSignedIn()) {
			String login = ParamUtil.getString(actionRequest, "login");
			String password = actionRequest.getParameter("password");

			HttpServletRequest request = _portal.getOriginalServletRequest(
				_portal.getHttpServletRequest(actionRequest));

			long userId =
				AuthenticatedSessionManagerUtil.getAuthenticatedUserId(
					request, login, password, null);

			if (userId > 0) {
				if (browserMFAVerifier.needsSetup(userId)) {
					_setupMFA(actionRequest, actionResponse);

					return;
				}

				if (browserMFAVerifier.needsVerification(request, userId)) {
					_verifyMFA(userId, actionRequest, actionResponse);

					return;
				}

				if (!Validator.isBlank(state)) {
					browserMFAVerifier.setupSessionAfterVerification(
						request, userId);
				}
			}
		}

		_loginMVCActionCommand.processAction(actionRequest, actionResponse);
	}

	private ActionRequest _loadRequestFromState(
			ActionRequest actionRequest, ActionResponse actionResponse,
			String encryptedStateMap)
		throws Exception {

		HttpServletRequest httpServletRequest =
			_portal.getOriginalServletRequest(
				_portal.getHttpServletRequest(actionRequest));

		Map<String, Object> stateMap = _decrypt(
			httpServletRequest.getSession(), encryptedStateMap);

		Map<String, String[]> requestParameters =
			(Map<String, String[]>) stateMap.get(
				"requestParameters");

		ActionRequestWrapper actionRequestWrapper =
			new ActionRequestWrapper(actionRequest) {
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

		return actionRequestWrapper;
	}

	private void _setupMFA(
			ActionRequest actionRequest, ActionResponse actionResponse)
		throws PortletException {

		final String[] responseRedirect = {null};

		ActionResponseWrapper actionResponseWrapper =
			new ActionResponseWrapper(actionResponse) {

				@Override
				public void sendRedirect(String location) throws IOException {
					responseRedirect[0] = location;
				}

			};

		_loginMVCActionCommand.processAction(
			actionRequest, actionResponseWrapper);

		String redirect = (String)actionRequest.getAttribute(WebKeys.REDIRECT);

		if (Validator.isNotNull(redirect)) {
			responseRedirect[0] = redirect;
		}

		PortletURL renderURL = _mfaPortletURLFactory.createSetupURL(
			_portal.getHttpServletRequest(actionRequest),
			_loginMFAIntegration.getName(), responseRedirect[0]);

//		renderURL.setWindowState(WindowState.MAXIMIZED);

		actionRequest.setAttribute(WebKeys.REDIRECT, renderURL.toString());

		return;
	}

	private void _verifyMFA(
			long userId, ActionRequest actionRequest,
			ActionResponse actionResponse)
		throws Exception {

		Map<String, Object> stateMap = new HashMap<>();

		stateMap.put("requestParameters", actionRequest.getParameterMap());

		HttpServletRequest httpServletRequest =
			_portal.getOriginalServletRequest(
				_portal.getHttpServletRequest(actionRequest));

		String state = _encrypt(httpServletRequest.getSession(), stateMap);

		LiferayPortletResponse liferayPortletResponse =
			_portal.getLiferayPortletResponse(actionResponse);

		ActionURL actionURL = liferayPortletResponse.createActionURL();

		actionURL.setParameter("state", state);

		LiferayPortletURL verifyURL =
			_mfaPortletURLFactory.createVerifyURL(
				httpServletRequest, _loginMFAIntegration.getName(),
				actionURL.toString(), userId);

		actionRequest.setAttribute(WebKeys.REDIRECT, verifyURL.toString());

		return;
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

	@Reference
	private JSONFactory _jsonFactory;

	@Reference(
		target = "(component.name=com.liferay.login.web.internal.portlet.action.LoginMVCActionCommand)"
	)
	private MVCActionCommand _loginMVCActionCommand;

	@Reference
	private MFARegistry _mfaVerifierRegistry;

	@Reference
	private LoginMFAIntegration _loginMFAIntegration;

	@Reference
	private MFAPortletURLFactory _mfaPortletURLFactory;

	@Reference
	private Portal _portal;

}