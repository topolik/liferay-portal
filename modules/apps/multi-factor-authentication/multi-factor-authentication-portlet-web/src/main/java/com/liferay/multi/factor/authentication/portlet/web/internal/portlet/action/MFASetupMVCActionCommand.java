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
import com.liferay.multi.factor.authentication.portlet.api.constants.MFAPortletKeys;
import com.liferay.multi.factor.authentication.spi.integration.MFAIntegration;
import com.liferay.multi.factor.authentication.spi.verifier.BrowserMFAVerifier;
import com.liferay.portal.kernel.portlet.bridges.mvc.BaseMVCActionCommand;
import com.liferay.portal.kernel.portlet.bridges.mvc.MVCActionCommand;
import com.liferay.portal.kernel.servlet.SessionErrors;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.kernel.util.Portal;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.kernel.util.WebKeys;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

import javax.portlet.ActionRequest;
import javax.portlet.ActionResponse;

/**
 * @author Tomas Polesovsky
 */
@Component(
	property = {
		"javax.portlet.name=" + MFAPortletKeys.MFA_VERIFY,
		"mvc.command.name=/mfa_verify/setup"
	},
	service = MVCActionCommand.class
)
public class MFASetupMVCActionCommand extends BaseMVCActionCommand {

	@Override
	protected void doProcessAction(
			ActionRequest actionRequest, ActionResponse actionResponse)
		throws Exception {

		String integrationName = ParamUtil.getString(
			actionRequest, "integrationName");

		MFAIntegration mfaIntegration =
			_mfaRegistry.getMFAIntegration(integrationName);

		BrowserMFAVerifier browserMFAVerifier = _mfaRegistry.getMFAVerifier(
			(MFAIntegration<BrowserMFAVerifier>)mfaIntegration);

		ThemeDisplay themeDisplay = (ThemeDisplay)actionRequest.getAttribute(
			WebKeys.THEME_DISPLAY);

		if (browserMFAVerifier.setup(actionRequest, themeDisplay.getUserId())) {
			browserMFAVerifier.setupSessionAfterVerification(
				_portal.getOriginalServletRequest(
					_portal.getHttpServletRequest(actionRequest)),
				themeDisplay.getUserId());

			String redirect = _portal.escapeRedirect(
				ParamUtil.getString(actionRequest, "redirect"));

			if (Validator.isBlank(redirect)) {
				redirect = themeDisplay.getPortalURL();
			}

			actionResponse.sendRedirect(redirect);

			return;
		}

		SessionErrors.add(actionRequest, "mfaFailed");
	}

	@Reference
	private MFARegistry _mfaRegistry;

	@Reference
	private Portal _portal;
}