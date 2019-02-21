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
import java.util.List;

/**
 * @author Tomas Polesovsky
 */
public class MandatoryCompositeMFAVerifier
	extends CompositeMFAVerifier {

	public MandatoryCompositeMFAVerifier(List<MFAVerifier> mfaVerifiers) {
		super(mfaVerifiers);
	}

	@Override
	public boolean setup(ActionRequest actionRequest, long userId) {
		if (mfaVerifiers.size() == 0) {
			return false;
		}

		boolean setup = true;

		for (MFAVerifier mfaVerifier : mfaVerifiers) {
			if (!mfaVerifier.supportsBrowser()) {
				continue;
			}

			if(!mfaVerifier.needsSetup(userId)) {
				continue;
			}

			BrowserMFAVerifier browserMFAVerifier =
				(BrowserMFAVerifier) mfaVerifier;

			setup &= browserMFAVerifier.setup(actionRequest, userId);
		}

		return setup;
	}

	@Override
	public boolean verify(
		ActionRequest actionRequest, ActionResponse actionResponse,
		long userId) {

		if (mfaVerifiers.size() == 0) {
			return false;
		}

		boolean verified = true;

		HttpServletRequest originalServletRequest =
			PortalUtil.getOriginalServletRequest(
				PortalUtil.getHttpServletRequest(actionRequest));


		for (MFAVerifier mfaVerifier : mfaVerifiers) {
			if (!mfaVerifier.supportsBrowser()) {
				continue;
			}

			if(!mfaVerifier.needsVerification(originalServletRequest, userId)) {
				continue;
			}

			BrowserMFAVerifier browserMFAVerifier =
				(BrowserMFAVerifier) mfaVerifier;

			verified &= browserMFAVerifier.verify(
				actionRequest, actionResponse, userId);
		}

		return verified;
	}

	@Override
	public boolean verify(HttpServletRequest request, long userId) {
		if (mfaVerifiers.size() == 0) {
			return false;
		}

		boolean verified = true;

		for (MFAVerifier mfaVerifier : mfaVerifiers) {
			if (!mfaVerifier.supportsHeadless()) {
				continue;
			}

			if(!mfaVerifier.needsVerification(request, userId)) {
				continue;
			}

			HeadlessMFAVerifier headlessMFAVerifier =
				(HeadlessMFAVerifier) mfaVerifier;

			verified &= headlessMFAVerifier.verify(request, userId);
		}

		return verified;
	}

}
