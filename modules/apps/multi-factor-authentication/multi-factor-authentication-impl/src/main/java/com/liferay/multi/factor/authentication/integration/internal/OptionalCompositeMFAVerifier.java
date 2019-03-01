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
public class OptionalCompositeMFAVerifier extends CompositeMFAVerifierImpl {

	public OptionalCompositeMFAVerifier(List<MFAVerifier> mfaVerifiers) {
		super(mfaVerifiers);
	}


	@Override
	public List<MFAVerifier> getOptionalMFAVerifiers() {
		return mfaVerifiers;
	}

	@Override
	public boolean isBrowserVerified(HttpServletRequest request, long userId) {
		for (MFAVerifier mfaVerifier : mfaVerifiers) {
			if (!mfaVerifier.supportsBrowser()) {
				continue;
			}

			BrowserMFAVerifier browserMFAVerifier =
				(BrowserMFAVerifier)mfaVerifier;

			if(browserMFAVerifier.isBrowserVerified(request, userId)) {
				return true;
			}
		}

		return false;
	}

	@Override
	public boolean isHeadlessVerified(HttpServletRequest request, long userId) {
		for (MFAVerifier mfaVerifier : mfaVerifiers) {
			if (!mfaVerifier.supportsHeadless()) {
				continue;
			}

			HeadlessMFAVerifier headlessMFAVerifier =
				(HeadlessMFAVerifier)mfaVerifier;

			if(headlessMFAVerifier.isHeadlessVerified(request, userId)) {
				return true;
			}
		}

		return false;
	}

	@Override
	public boolean canVerifyHeadless(HttpServletRequest request, long userId) {
		for (MFAVerifier mfaVerifier : mfaVerifiers) {
			if (!mfaVerifier.supportsHeadless()) {
				continue;
			}

			HeadlessMFAVerifier headlessMFAVerifier =
				(HeadlessMFAVerifier)mfaVerifier;

			if(headlessMFAVerifier.canVerifyHeadless(request, userId)) {
				return true;
			}
		}

		return false;
	}

	@Override
	public boolean canVerifyBrowser(HttpServletRequest request, long userId) {
		for (MFAVerifier mfaVerifier : mfaVerifiers) {
			if (!mfaVerifier.supportsBrowser()) {
				continue;
			}

			BrowserMFAVerifier browserMFAVerifier =
				(BrowserMFAVerifier)mfaVerifier;

			if(browserMFAVerifier.canVerifyBrowser(request, userId)) {
				return true;
			}
		}

		return false;
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

			if(!browserMFAVerifier.canVerifyBrowser(
					originalServletRequest, userId)) {

				continue;
			}

			if (browserMFAVerifier.isBrowserVerified(
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
	public boolean verifyHeadlessRequest(
		HttpServletRequest request, long userId) {

		for (MFAVerifier mfaVerifier : mfaVerifiers) {
			if (!mfaVerifier.supportsHeadless()) {
				continue;
			}

			HeadlessMFAVerifier headlessMFAVerifier =
				(HeadlessMFAVerifier)mfaVerifier;

			if(!headlessMFAVerifier.canVerifyHeadless(
				request, userId)) {

				continue;
			}

			if (headlessMFAVerifier.isHeadlessVerified(request, userId)) {
				continue;
			}

			if (headlessMFAVerifier.verifyHeadlessRequest(request, userId)) {
				return true;
			}
		}

		return false;
	}
}
