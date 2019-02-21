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

package com.liferay.multi.factor.authentication.integration.login.internal.events;

import com.liferay.multi.factor.authentication.api.MFARegistry;
import com.liferay.multi.factor.authentication.integration.login.internal.spi.integration.LoginMFAIntegration;
import com.liferay.multi.factor.authentication.portlet.api.MFAPortletURLFactory;
import com.liferay.multi.factor.authentication.spi.verifier.BrowserMFAVerifier;
import com.liferay.multi.factor.authentication.spi.verifier.HeadlessMFAVerifier;
import com.liferay.multi.factor.authentication.spi.verifier.MFAVerifier;
import com.liferay.portal.kernel.events.Action;
import com.liferay.portal.kernel.events.ActionException;
import com.liferay.portal.kernel.events.LifecycleAction;
import com.liferay.portal.kernel.portlet.LiferayPortletURL;
import com.liferay.portal.kernel.portlet.LiferayWindowState;
import com.liferay.portal.kernel.security.auth.PrincipalException;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.WebKeys;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Objects;

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

		MFAVerifier mfaVerifier =
			_mfaRegistry.getMFAVerifier(LoginMFAIntegration.NAME);

		if (mfaVerifier == null) {
			return;
		}

		ThemeDisplay themeDisplay = (ThemeDisplay)request.getAttribute(
			WebKeys.THEME_DISPLAY);

		if ((themeDisplay == null) || !themeDisplay.isSignedIn() ||
			themeDisplay.isImpersonated()) {

			return;
		}

		long userId = themeDisplay.getUserId();

		if (mfaVerifier.needsSetup(userId)) {
			LiferayPortletURL liferayPortletURL =
				_mfaPortletURLFactory.createSetupURL(
					request, LoginMFAIntegration.NAME,
					themeDisplay.getURLCurrent());

			if (themeDisplay.isStateMaximized() && Objects.equals(
				liferayPortletURL.getPortletId(), themeDisplay.getPpid())) {

				return;
			}

			try {
				response.sendRedirect(liferayPortletURL.toString());
			}
			catch (Exception e) {
				throw new ActionException(
					"Unable to send login redirect: " + e.getMessage(), e);
			}

			return;
		}

		if (mfaVerifier.needsVerification(request, userId)) {
			if (mfaVerifier.supportsHeadless()) {
				HeadlessMFAVerifier headlessMFAVerifier =
					(HeadlessMFAVerifier)mfaVerifier;

				if (headlessMFAVerifier.verify(request, userId)) {
					return;
				}
			}

			if (!mfaVerifier.supportsBrowser()) {
				throw new ActionException(
					new PrincipalException.MustBeAuthenticated(
						"Unable to verify MFA"));
			}

			LiferayPortletURL liferayPortletURL =
				_mfaPortletURLFactory.createVerifyURL(
					request, LoginMFAIntegration.NAME,
					themeDisplay.getURLCurrent(), userId);

			if (Objects.equals(
				liferayPortletURL.getPortletId(), themeDisplay.getPpid()) &&
				LiferayWindowState.isExclusive(request)) {

				return;
			}

			try {
				liferayPortletURL.setWindowState(LiferayWindowState.EXCLUSIVE);

				response.sendRedirect(liferayPortletURL.toString());
			}
			catch (Exception e) {
				throw new ActionException(
					"Unable to send login redirect: " + e.getMessage(), e);
			}

			return;
		}
	}

	@Reference
	private MFARegistry _mfaRegistry;

	@Reference
	private MFAPortletURLFactory _mfaPortletURLFactory;

}