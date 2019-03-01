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
import java.util.Collections;
import java.util.List;

/**
 * @author Tomas Polesovsky
 */
public class MandatoryCompositeMFAVerifier
	extends CompositeMFAVerifierImpl {

	public MandatoryCompositeMFAVerifier(List<MFAVerifier> mfaVerifiers) {
		super(mfaVerifiers);
	}

	@Override
	public List<MFAVerifier> getOptionalMFAVerifiers() {
		return Collections.singletonList(this);
	}

	@Override
	public boolean isBrowserVerified(HttpServletRequest request, long userId) {
		if (mfaVerifiers.size() == 0) {
			return false;
		}

		for (MFAVerifier mfaVerifier : mfaVerifiers) {
			if (!mfaVerifier.supportsBrowser()) {
				return false;
			}

			BrowserMFAVerifier browserMFAVerifier =
				(BrowserMFAVerifier)mfaVerifier;

			if (!browserMFAVerifier.isBrowserVerified(request, userId)) {
				return false;
			}
		}

		return true;
	}

	@Override
	public boolean isHeadlessVerified(HttpServletRequest request, long userId) {
		if (mfaVerifiers.size() == 0) {
			return false;
		}

		for (MFAVerifier mfaVerifier : mfaVerifiers) {
			if (!mfaVerifier.supportsHeadless()) {
				return false;
			}

			HeadlessMFAVerifier headlessMFAVerifier =
				(HeadlessMFAVerifier) mfaVerifier;

			if (!headlessMFAVerifier.isHeadlessVerified(request, userId)) {
				return false;
			}
		}

		return true;
	}

	@Override
	public boolean canVerifyHeadless(
		HttpServletRequest request, long userId) {

		if (mfaVerifiers.size() == 0) {
			return false;
		}

		for (MFAVerifier mfaVerifier : mfaVerifiers) {
			if (!mfaVerifier.supportsHeadless()) {
				return false;
			}

			HeadlessMFAVerifier headlessMFAVerifier =
				(HeadlessMFAVerifier) mfaVerifier;

			if (!headlessMFAVerifier.canVerifyHeadless(request, userId)) {
				return false;
			}
		}

		return true;
	}

	@Override
	public boolean canVerifyBrowser(
		HttpServletRequest request, long userId) {

		if (mfaVerifiers.size() == 0) {
			return false;
		}

		for (MFAVerifier mfaVerifier : mfaVerifiers) {
			if (!mfaVerifier.supportsBrowser()) {
				return false;
			}

			BrowserMFAVerifier browserMFAVerifier =
				(BrowserMFAVerifier)mfaVerifier;

			if (!browserMFAVerifier.canVerifyBrowser(request, userId)) {
				return false;
			}
		}

		return true;
	}

	@Override
	public boolean verifyBrowserRequest(
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
				return false;
			}

			BrowserMFAVerifier browserMFAVerifier =
				(BrowserMFAVerifier)mfaVerifier;

			if(!browserMFAVerifier.canVerifyBrowser(
				originalServletRequest, userId)) {

				return false;
			}

			if (browserMFAVerifier.isBrowserVerified(
					originalServletRequest, userId)) {

				continue;
			}

			verified &= browserMFAVerifier.verifyBrowserRequest(
				actionRequest, actionResponse, userId);
		}

		return verified;
	}

	@Override
	public boolean verifyHeadlessRequest(
		HttpServletRequest request, long userId) {

		if (mfaVerifiers.size() == 0) {
			return false;
		}

		boolean verified = true;

		for (MFAVerifier mfaVerifier : mfaVerifiers) {
			if (!mfaVerifier.supportsHeadless()) {
				return false;
			}

			HeadlessMFAVerifier headlessMFAVerifier =
				(HeadlessMFAVerifier)mfaVerifier;


			if(!headlessMFAVerifier.canVerifyHeadless(
				request, userId)) {

				return false;
			}

			if (headlessMFAVerifier.isHeadlessVerified(request, userId)) {
				continue;
			}

			verified &= headlessMFAVerifier.verifyHeadlessRequest(
				request, userId);
		}

		return verified;
	}

}
