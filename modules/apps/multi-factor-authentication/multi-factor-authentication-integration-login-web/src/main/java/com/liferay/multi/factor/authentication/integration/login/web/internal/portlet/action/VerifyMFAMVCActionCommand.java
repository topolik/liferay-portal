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
import com.liferay.petra.string.StringBundler;
import com.liferay.portal.kernel.json.JSONFactory;
import com.liferay.portal.kernel.portlet.bridges.mvc.BaseMVCActionCommand;
import com.liferay.portal.kernel.portlet.bridges.mvc.MVCActionCommand;
import com.liferay.portal.kernel.security.auth.PrincipalException;
import com.liferay.portal.kernel.servlet.SessionErrors;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.DigesterUtil;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.kernel.util.Portal;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.kernel.util.WebKeys;

import java.io.IOException;

import java.security.Key;

import java.util.Map;

import javax.portlet.ActionRequest;
import javax.portlet.ActionResponse;
import javax.portlet.PortletException;
import javax.portlet.filter.ActionRequestWrapper;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Tomas Polesovsky
 */
@Component(
	property = {
		"javax.portlet.name=" + MFAPortletKeys.FAST_LOGIN,
		"javax.portlet.name=" + MFAPortletKeys.LOGIN,
		"mvc.command.name=/login/verify_mfa"
	},
	service = MVCActionCommand.class
)
public class VerifyMFAMVCActionCommand extends BaseMVCActionCommand {

	@Override
	protected void doProcessAction(
			ActionRequest actionRequest, ActionResponse actionResponse)
		throws Exception {

		LoginWebMFAVerifier loginWebMFAVerifier =
			_mfaVerifierRegistry.getMFAVerifier(LoginWebMFAVerifier.class);

		if (loginWebMFAVerifier == null) {
			return;
		}

		ThemeDisplay themeDisplay = (ThemeDisplay)actionRequest.getAttribute(
			WebKeys.THEME_DISPLAY);

		if (themeDisplay.isSignedIn()) {
			_verifyAuthenticatedUser(
				actionRequest, actionResponse, loginWebMFAVerifier,
				themeDisplay);
		}
		else {
			_verifyGuestUser(
				actionRequest, actionResponse, loginWebMFAVerifier);
		}
	}

	private void _verifyAuthenticatedUser(
			ActionRequest actionRequest, ActionResponse actionResponse,
			LoginWebMFAVerifier loginWebMFAVerifier, ThemeDisplay themeDisplay)
		throws IOException {

		if (loginWebMFAVerifier.verify(
				themeDisplay.getUserId(), actionRequest, actionResponse)) {

			loginWebMFAVerifier.setupSessionAfterVerification(actionRequest);

			String redirect = ParamUtil.getString(actionRequest, "redirect");

			if (!Validator.isBlank(redirect)) {
				actionResponse.sendRedirect(_portal.escapeRedirect(redirect));
			}

			return;
		}

		SessionErrors.add(actionRequest, "mfaFailed");
	}

	private void _verifyGuestUser(
			ActionRequest actionRequest, ActionResponse actionResponse,
			LoginWebMFAVerifier loginWebMFAVerifier)
		throws EncryptorException, PortletException, PrincipalException {

		HttpServletRequest httpServletRequest =
			_portal.getOriginalServletRequest(
				_portal.getHttpServletRequest(actionRequest));

		HttpSession session = httpServletRequest.getSession();

		long userId = (Long)session.getAttribute("userid");
		String digest = (String)session.getAttribute("digest");

		String encryptedParameterMapJSON = ParamUtil.getString(
			actionRequest, "encryptedParameterMapJSON");

		if (!StringUtil.equals(
				DigesterUtil.digest(encryptedParameterMapJSON), digest)) {

			throw new PrincipalException(
				StringBundler.concat("User ", userId, " sent unverified data"));
		}

		if (loginWebMFAVerifier.verify(userId, actionRequest, actionResponse)) {
			Key key = (Key)session.getAttribute("key");

			String parameterMapJSON = Encryptor.decrypt(
				key, encryptedParameterMapJSON);

			Map<String, String> parameterMap = _jsonFactory.looseDeserialize(
				parameterMapJSON, Map.class);

			ActionRequestWrapper actionRequestWrapper =
				new ActionRequestWrapper(actionRequest) {

					public String getParameter(String name) {
						return parameterMap.get(name);
					}

				};

			_loginMVCActionCommand.processAction(
				actionRequestWrapper, actionResponse);

			loginWebMFAVerifier.setupSessionAfterVerification(actionRequest);

			return;
		}

		SessionErrors.add(actionRequest, "mfaFailed");
	}

	@Reference
	private JSONFactory _jsonFactory;

	@Reference(
		target = "(component.name=com.liferay.login.web.internal.portlet.action.LoginMVCActionCommand)"
	)
	private MVCActionCommand _loginMVCActionCommand;

	@Reference
	private MFAVerifierRegistry _mfaVerifierRegistry;

	@Reference
	private Portal _portal;

}