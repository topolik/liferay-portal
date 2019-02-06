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

package com.liferay.multi.factor.authentication.integration.login.web.internal.events;

import com.liferay.multi.factor.authentication.integration.login.web.spi.LoginWebMFAVerifier;
import com.liferay.multi.factor.authentication.integration.spi.verifier.MFAVerifierRegistry;
import com.liferay.portal.kernel.events.Action;
import com.liferay.portal.kernel.events.ActionException;
import com.liferay.portal.kernel.events.LifecycleAction;
import com.liferay.portal.kernel.portlet.LiferayWindowState;
import com.liferay.portal.kernel.portlet.PortletURLFactoryUtil;
import com.liferay.portal.kernel.security.auth.session.AuthenticatedSessionManager;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.Portal;
import com.liferay.portal.kernel.util.PortletKeys;
import com.liferay.portal.kernel.util.Props;
import com.liferay.portal.kernel.util.WebKeys;

import javax.portlet.PortletRequest;
import javax.portlet.PortletURL;
import javax.portlet.WindowState;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Tomas Polesovsky
 */
@Component(
	property = "key=servlet.service.events.pre", service = LifecycleAction.class
)
public class MFALoginServicePreAction extends Action {

	@Override
	public void run(HttpServletRequest request, HttpServletResponse response)
		throws ActionException {

		LoginWebMFAVerifier loginWebMFAVerifier =
			_mfaVerifierRegistry.getMFAVerifier(LoginWebMFAVerifier.class);

		if (loginWebMFAVerifier == null) {
			return;
		}

		ThemeDisplay themeDisplay = (ThemeDisplay)request.getAttribute(
			WebKeys.THEME_DISPLAY);

		if ((themeDisplay == null) || !themeDisplay.isSignedIn() ||
			themeDisplay.isImpersonated()) {

			return;
		}

		long userId = themeDisplay.getUserId();

		if (loginWebMFAVerifier.needsSetup(userId)) {
			if (PortletKeys.LOGIN.equals(themeDisplay.getPpid())) {
				return;
			}

			try {
				PortletURL portletURL = PortletURLFactoryUtil.create(
					request, PortletKeys.LOGIN, PortletRequest.RENDER_PHASE);

				portletURL.setParameter(
					"mvcRenderCommandName", "/login/setup_mfa");
				portletURL.setParameter(
					"redirect", themeDisplay.getURLCurrent());
				portletURL.setWindowState(WindowState.MAXIMIZED);

				response.sendRedirect(portletURL.toString());
			}
			catch (Exception e) {
				throw new ActionException(
					"Unable to send login redirect: " + e.getMessage(), e);
			}

			return;
		}

		if (loginWebMFAVerifier.needsVerification(request, userId)) {
			if (PortletKeys.LOGIN.equals(themeDisplay.getPpid()) &&
				LiferayWindowState.isExclusive(request)) {

				return;
			}

			try {
				PortletURL portletURL = PortletURLFactoryUtil.create(
					request, PortletKeys.LOGIN, PortletRequest.RENDER_PHASE);

				portletURL.setParameter(
					"mvcRenderCommandName", "/login/verify_mfa");
				portletURL.setParameter(
					"redirect", themeDisplay.getURLCurrent());
				portletURL.setWindowState(LiferayWindowState.EXCLUSIVE);

				response.sendRedirect(portletURL.toString());
			}
			catch (Exception e) {
				throw new ActionException(
					"Unable to send login redirect: " + e.getMessage(), e);
			}

			return;
		}
	}

	@Reference
	private AuthenticatedSessionManager _authenticatedSessionManager;

	@Reference
	private MFAVerifierRegistry _mfaVerifierRegistry;

	@Reference
	private Portal _portal;

	@Reference
	private Props _props;

}