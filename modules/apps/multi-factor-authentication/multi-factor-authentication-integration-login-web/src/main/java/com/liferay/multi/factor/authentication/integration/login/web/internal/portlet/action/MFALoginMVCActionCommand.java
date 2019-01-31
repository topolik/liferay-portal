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

package com.liferay.multi.factor.authentication.integration.login.web.internal.portlet.action;

import com.liferay.multi.factor.authentication.integration.login.web.internal.constants.MFAPortletKeys;
import com.liferay.multi.factor.authentication.integration.login.web.spi.LoginWebMFAVerifier;
import com.liferay.multi.factor.authentication.integration.spi.verifier.MFAVerifierRegistry;
import com.liferay.petra.encryptor.Encryptor;
import com.liferay.petra.encryptor.EncryptorException;
import com.liferay.portal.kernel.json.JSONFactory;
import com.liferay.portal.kernel.model.UserConstants;
import com.liferay.portal.kernel.portlet.LiferayPortletResponse;
import com.liferay.portal.kernel.portlet.bridges.mvc.BaseMVCActionCommand;
import com.liferay.portal.kernel.portlet.bridges.mvc.MVCActionCommand;
import com.liferay.portal.kernel.security.auth.session.AuthenticatedSessionManagerUtil;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.Base64;
import com.liferay.portal.kernel.util.DigesterUtil;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.kernel.util.Portal;
import com.liferay.portal.kernel.util.PortalUtil;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.kernel.util.WebKeys;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.component.annotations.ReferencePolicy;
import org.osgi.service.component.annotations.ReferencePolicyOption;

import javax.portlet.ActionRequest;
import javax.portlet.ActionResponse;
import javax.portlet.PortletException;
import javax.portlet.PortletSession;
import javax.portlet.PortletURL;
import javax.portlet.filter.ActionResponseWrapper;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.security.Key;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;

/**
 * @author Tomas Polesovsky
 */
@Component(
	property = {
		"javax.portlet.name=" + MFAPortletKeys.FAST_LOGIN,
		"javax.portlet.name=" + MFAPortletKeys.LOGIN,
		"mvc.command.name=/login/login",
		"service.ranking:Integer=1"
	},
	service = MVCActionCommand.class
)
public class MFALoginMVCActionCommand extends BaseMVCActionCommand {

	@Override
	protected void doProcessAction(
			ActionRequest actionRequest, ActionResponse actionResponse)
		throws Exception {

		LoginWebMFAVerifier loginWebMFAVerifier =
			_mfaVerifierRegistry.getMFAVerifier(
				LoginWebMFAVerifier.class);

		if (loginWebMFAVerifier == null) {
			_loginMVCActionCommand.processAction(actionRequest, actionResponse);

			return;
		}

		ThemeDisplay themeDisplay = (ThemeDisplay)actionRequest.getAttribute(
			WebKeys.THEME_DISPLAY);

		if (!themeDisplay.isSignedIn()) {
			String login = ParamUtil.getString(actionRequest, "login");
			String password = actionRequest.getParameter("password");

			HttpServletRequest request = PortalUtil.getOriginalServletRequest(
				PortalUtil.getHttpServletRequest(actionRequest));

			long userId =
				AuthenticatedSessionManagerUtil.getAuthenticatedUserId(
					request, login, password, null);

			if (userId > 0) {
				if(loginWebMFAVerifier.needsSetup(userId)) {
					_setupMFA(userId, actionRequest, actionResponse);

					return;
				}

				if (loginWebMFAVerifier.needsVerify(userId)) {
					_verifyMFA(userId, actionRequest, actionResponse);

					return;
				}
			}
		}

		_loginMVCActionCommand.processAction(actionRequest, actionResponse);
	}

	private void _setupMFA(
			long userId, ActionRequest actionRequest,
			ActionResponse actionResponse)
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

		LiferayPortletResponse liferayPortletResponse =
			_portal.getLiferayPortletResponse(actionResponse);

		PortletURL renderURL = liferayPortletResponse.createRenderURL();

		renderURL.setParameter(
			"mvcRenderCommandName", "/login/setup_mfa");

		if (Validator.isNotNull(responseRedirect[0])) {
			renderURL.setParameter("redirect", responseRedirect[0]);
		}

		actionRequest.setAttribute(
			WebKeys.REDIRECT, renderURL.toString());

		return;
	}

	private void _verifyMFA(
			long userId, ActionRequest actionRequest,
			ActionResponse actionResponse)
		throws Exception {

		Enumeration<String> parameterNames =
			actionRequest.getParameterNames();

		Map<String, String> parameterMap = new HashMap<>();

		for (
			String name = parameterNames.nextElement();
			parameterNames.hasMoreElements();) {

			parameterMap.put(
				name, ParamUtil.getString(actionRequest, name));
		}

		String parameterMapJSON = _jsonFactory.looseSerialize(
			parameterMap);

		PortletSession portletSession =
			actionRequest.getPortletSession();

		portletSession.setAttribute("userid", userId);

		Key key = Encryptor.generateKey();

		portletSession.setAttribute(
			"encryptedParameterMapJSON",
			Encryptor.encrypt(key, parameterMapJSON));

		String encodedKey = Base64.encode(key.getEncoded());

		portletSession.setAttribute(
			"encodedKeyDigest", DigesterUtil.digest(encodedKey));

		LiferayPortletResponse liferayPortletResponse =
			_portal.getLiferayPortletResponse(actionResponse);

		PortletURL renderURL = liferayPortletResponse.createRenderURL();

		renderURL.setParameter(
			"mvcRenderCommandName", "/login/verify_mfa");

		renderURL.setParameter("encodedKey", encodedKey);

		actionRequest.setAttribute(
			WebKeys.REDIRECT, renderURL.toString());

		return;
	}

	@Reference
	private MFAVerifierRegistry _mfaVerifierRegistry;

	@Reference(
		target = "(component.name=com.liferay.login.web.internal.portlet.action.LoginMVCActionCommand)"
	)
	private MVCActionCommand _loginMVCActionCommand;

	@Reference
	private Portal _portal;

	@Reference
	private JSONFactory _jsonFactory;
}
