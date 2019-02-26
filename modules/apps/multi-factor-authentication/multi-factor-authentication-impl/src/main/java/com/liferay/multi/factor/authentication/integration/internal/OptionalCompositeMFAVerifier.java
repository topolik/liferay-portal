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

package com.liferay.multi.factor.authentication.integration.internal;

import com.liferay.multi.factor.authentication.spi.verifier.BrowserMFAVerifier;
import com.liferay.multi.factor.authentication.spi.verifier.HeadlessMFAVerifier;
import com.liferay.multi.factor.authentication.spi.verifier.MFAVerifier;
import com.liferay.portal.kernel.util.PortalUtil;

import javax.portlet.ActionRequest;
import javax.portlet.ActionResponse;
import javax.servlet.http.HttpServletRequest;
import java.util.Collection;

/**
 * @author Tomas Polesovsky
 */
public class OptionalCompositeMFAVerifier extends CompositeMFAVerifier {

	public OptionalCompositeMFAVerifier(Collection<MFAVerifier> mfaVerifiers) {
		super(mfaVerifiers);
	}

	@Override
	public boolean requiresHeadlessVerification(
		HttpServletRequest request, long userId) {

		if (mfaVerifiers.isEmpty()) {
			return false;
		}

		boolean requiresVerification = true;

		for (MFAVerifier mfaVerifier : mfaVerifiers) {
			if (!mfaVerifier.supportsHeadless()) {
				continue;
			}

			HeadlessMFAVerifier headlessMFAVerifier =
				(HeadlessMFAVerifier)mfaVerifier;

			requiresVerification &=
				headlessMFAVerifier.requiresHeadlessVerification(
					request, userId);
		}

		return requiresVerification;
	}

	@Override
	public boolean requiresBrowserVerification(
		HttpServletRequest request, long userId) {

		if (mfaVerifiers.isEmpty()) {
			return false;
		}

		boolean requiresVerification = true;

		for (MFAVerifier mfaVerifier : mfaVerifiers) {
			if (!mfaVerifier.supportsBrowser()) {
				continue;
			}

			BrowserMFAVerifier browserMFAVerifier =
				(BrowserMFAVerifier)mfaVerifier;

			requiresVerification &=
				browserMFAVerifier.requiresBrowserVerification(
					request, userId);
		}

		return requiresVerification;
	}

	@Override
	public boolean requiresSetup(long userId) {
		if (mfaVerifiers.isEmpty()) {
			return false;
		}

		boolean requiresSetup = true;

		for (MFAVerifier mfaVerifier : mfaVerifiers) {
			if (!mfaVerifier.supportsBrowser()) {
				continue;
			}

			BrowserMFAVerifier browserMFAVerifier =
				(BrowserMFAVerifier)mfaVerifier;

			requiresSetup &= browserMFAVerifier.requiresSetup(userId);

		}

		return requiresSetup;
	}

	@Override
	public boolean setup(ActionRequest actionRequest, long userId) {
		boolean setup = false;

		for (MFAVerifier mfaVerifier : mfaVerifiers) {
			if (!mfaVerifier.supportsBrowser()) {
				continue;
			}

			BrowserMFAVerifier browserMFAVerifier =
				(BrowserMFAVerifier)mfaVerifier;

			if(!browserMFAVerifier.requiresSetup(userId)) {
				continue;
			}

			setup |= browserMFAVerifier.setup(actionRequest, userId);
		}

		return setup;
	}

	@Override
	public boolean verifyBrowserRequest(
		ActionRequest actionRequest, ActionResponse actionResponse,
		long userId) {

		HttpServletRequest originalServletRequest =
			PortalUtil.getOriginalServletRequest(
				PortalUtil.getHttpServletRequest(actionRequest));

		for (MFAVerifier mfaVerifier : mfaVerifiers) {
			if (!mfaVerifier.supportsBrowser()) {
				continue;
			}

			BrowserMFAVerifier browserMFAVerifier =
				(BrowserMFAVerifier)mfaVerifier;

			if(!browserMFAVerifier.requiresBrowserVerification(
					originalServletRequest, userId)) {

				continue;
			}

			if(browserMFAVerifier.verifyBrowserRequest(
				actionRequest, actionResponse, userId)) {

				return true;
			}
		}

		return false;
	}

	@Override
	public boolean verifyHeadlessRequest(HttpServletRequest request, long userId) {
		for (MFAVerifier mfaVerifier : mfaVerifiers) {
			if (!mfaVerifier.supportsHeadless()) {
				continue;
			}

			HeadlessMFAVerifier headlessMFAVerifier =
				(HeadlessMFAVerifier)mfaVerifier;

			if(!headlessMFAVerifier.requiresHeadlessVerification(
				request, userId)) {

				return true;
			}

			if (headlessMFAVerifier.verifyHeadlessRequest(request, userId)) {
				return true;
			}
		}

		return false;
	}
}
