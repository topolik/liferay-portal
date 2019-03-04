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
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.portlet.bridges.mvc.MVCRenderCommand;
import com.liferay.portal.kernel.servlet.SessionErrors;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.kernel.util.Portal;
import com.liferay.portal.kernel.util.WebKeys;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

import javax.portlet.PortletException;
import javax.portlet.PortletRequest;
import javax.portlet.RenderRequest;
import javax.portlet.RenderResponse;
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
	service = MVCRenderCommand.class
)
public class MFAVerifyMVCRenderCommand implements MVCRenderCommand {
	@Override
	public String render(
			RenderRequest renderRequest, RenderResponse renderResponse)
		throws PortletException {


		long mfaUserId = getMFAUserId(renderRequest);

		if (mfaUserId == 0) {
			SessionErrors.add(renderRequest, "sessionExpired");

			return "/error.jsp";
		}

		renderRequest.setAttribute("mfaUserId", mfaUserId);

		String integrationName = ParamUtil.getString(
			renderRequest, "integrationName");

		BrowserMFAVerifier browserMFAVerifier =
			(BrowserMFAVerifier) _mfaRegistry.getIntegrationVerifier(
				integrationName);

		renderRequest.setAttribute(
			BrowserMFAVerifier.class.getName(), browserMFAVerifier);

		List<BrowserMFAVerifier> verifyMFAVerifiers = _getVerifyMFAVerifiers(
			browserMFAVerifier, renderRequest, mfaUserId);

		renderRequest.setAttribute("verifyMFAVerifiers", verifyMFAVerifiers);

		return "/verify.jsp";
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

	private static final Log _log = LogFactoryUtil.getLog(
		MFAVerifyMVCRenderCommand.class);
}
