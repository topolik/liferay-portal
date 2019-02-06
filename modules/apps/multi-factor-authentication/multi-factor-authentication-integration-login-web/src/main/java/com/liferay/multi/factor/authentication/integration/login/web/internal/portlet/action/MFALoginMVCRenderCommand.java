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
import com.liferay.portal.kernel.portlet.bridges.mvc.MVCRenderCommand;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.WebKeys;

import javax.portlet.PortletException;
import javax.portlet.RenderRequest;
import javax.portlet.RenderResponse;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Peter Fellwock
 */
@Component(
	property = {
		"javax.portlet.name=" + MFAPortletKeys.FAST_LOGIN,
		"javax.portlet.name=" + MFAPortletKeys.LOGIN,
		"mvc.command.name=/login/login", "service.ranking:Integer=1"
	},
	service = MVCRenderCommand.class
)
public class MFALoginMVCRenderCommand implements MVCRenderCommand {

	@Override
	public String render(
			RenderRequest renderRequest, RenderResponse renderResponse)
		throws PortletException {

		LoginWebMFAVerifier loginWebMFAVerifier =
			_mfaVerifierRegistry.getMFAVerifier(LoginWebMFAVerifier.class);

		if (loginWebMFAVerifier != null) {
			ThemeDisplay themeDisplay =
				(ThemeDisplay)renderRequest.getAttribute(WebKeys.THEME_DISPLAY);

			if (themeDisplay.isSignedIn() &&
				loginWebMFAVerifier.needsSetup(themeDisplay.getUserId())) {

				return _setupMFAMVCRenderCommand.render(
					renderRequest, renderResponse);
			}
		}

		return _loginMVCRenderCommand.render(renderRequest, renderResponse);
	}

	@Reference(
		target = "(component.name=com.liferay.login.web.internal.portlet.action.LoginMVCRenderCommand)"
	)
	private MVCRenderCommand _loginMVCRenderCommand;

	@Reference
	private MFAVerifierRegistry _mfaVerifierRegistry;

	@Reference
	private SetupMFAMVCRenderCommand _setupMFAMVCRenderCommand;

}