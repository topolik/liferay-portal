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

package com.liferay.multi.factor.authentication.portlet.web.internal.portlet.action;

import com.liferay.multi.factor.authentication.api.MFARegistry;
import com.liferay.multi.factor.authentication.api.verifier.CompositeMFAVerifier;
import com.liferay.multi.factor.authentication.portlet.api.MFAPortletURLFactory;
import com.liferay.multi.factor.authentication.portlet.api.constants.MFAPortletKeys;
import com.liferay.multi.factor.authentication.spi.verifier.BrowserMFAVerifier;
import com.liferay.multi.factor.authentication.spi.verifier.MFAVerifier;
import com.liferay.portal.kernel.portlet.bridges.mvc.BaseMVCActionCommand;
import com.liferay.portal.kernel.portlet.bridges.mvc.MVCActionCommand;
import com.liferay.portal.kernel.servlet.SessionErrors;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.kernel.util.Portal;
import com.liferay.portal.kernel.util.WebKeys;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

import javax.portlet.ActionRequest;
import javax.portlet.ActionResponse;
import javax.portlet.PortletRequest;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * @author Tomas Polesovsky
 */
@Component(
	property = {
		"javax.portlet.name=" + MFAPortletKeys.MFA_VERIFY,
		"mvc.command.name=/mfa_verify/verify"
	},
	service = MVCActionCommand.class
)
public class MFAVerifyMVCActionCommand extends BaseMVCActionCommand {

	@Override
	protected void doProcessAction(
			ActionRequest actionRequest, ActionResponse actionResponse)
		throws Exception {

		ThemeDisplay themeDisplay = (ThemeDisplay)actionRequest.getAttribute(
			WebKeys.THEME_DISPLAY);

		long userId = getMFAUserId(actionRequest);

		if (userId == 0) {
			SessionErrors.add(actionRequest, "sessionExpired");

			actionResponse.setRenderParameter("mvcRenderCommandName", "/");
			actionResponse.setRenderParameter("mvcPath", "/error.jsp");

			return;
		}

		String integrationName = ParamUtil.getString(
			actionRequest, "integrationName");

		BrowserMFAVerifier browserMFAVerifier =
			(BrowserMFAVerifier)_mfaRegistry.getIntegrationVerifier(integrationName);

		int verifyMFAVerifierIndex =
			ParamUtil.getInteger(actionRequest, "verifyMFAVerifierIndex", -1);

		if (verifyMFAVerifierIndex > 1) {
			List<BrowserMFAVerifier> verifyMFAVerifiers =
				_getVerifyMFAVerifiers(
					browserMFAVerifier, actionRequest, userId);

			if (verifyMFAVerifierIndex < verifyMFAVerifiers.size()) {
				browserMFAVerifier = verifyMFAVerifiers.get(
					verifyMFAVerifierIndex);
			}
		}

		if (browserMFAVerifier.verifyBrowserRequest(
			actionRequest, actionResponse, userId)) {

			sendRedirect(actionRequest, actionResponse);

			return;
		}
	}

	private long getMFAUserId(PortletRequest portletRequest){
		ThemeDisplay themeDisplay = (ThemeDisplay)portletRequest.getAttribute(
			WebKeys.THEME_DISPLAY);

		if (themeDisplay.isSignedIn()) {
			return themeDisplay.getUserId();
		}
		else {
			String integrationName = ParamUtil.getString(
				portletRequest, "integrationName");

			HttpServletRequest httpServletRequest =
				_portal.getOriginalServletRequest(
					_portal.getHttpServletRequest(portletRequest));

			HttpSession session = httpServletRequest.getSession();

			Object mfaUserId = session.getAttribute(
				MFAPortletURLFactory.MFA_USER_ID + integrationName);

			if (mfaUserId == null) {
				return 0;
			}

			return (Long)mfaUserId;
		}
	}

	private List<BrowserMFAVerifier> _getVerifyMFAVerifiers(
		BrowserMFAVerifier mfaVerifier, PortletRequest portletRequest,
		long userId) {

		if (!(mfaVerifier instanceof CompositeMFAVerifier)) {
			return Collections.singletonList(mfaVerifier);
		}

		HttpServletRequest httpServletRequest =
			_portal.getOriginalServletRequest(
				_portal.getHttpServletRequest(portletRequest));

		CompositeMFAVerifier compositeMFAVerifier =
			(CompositeMFAVerifier)mfaVerifier;

		return compositeMFAVerifier.getMFAVerifiersAvailableForVerify(
			httpServletRequest, userId);
	}


	@Reference
	private MFARegistry _mfaRegistry;

	@Reference
	private Portal _portal;

}