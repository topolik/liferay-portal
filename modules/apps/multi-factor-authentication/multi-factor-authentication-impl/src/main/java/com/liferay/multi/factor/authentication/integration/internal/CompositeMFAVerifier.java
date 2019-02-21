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
import com.liferay.petra.string.StringBundler;
import com.liferay.petra.string.StringPool;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.util.Collection;
import java.util.List;

/**
 * @author Tomas Polesovsky
 */
public abstract class CompositeMFAVerifier
	implements MFAVerifier, BrowserMFAVerifier, HeadlessMFAVerifier {

	@Override
	public String getName() {
		if (mfaVerifiers.isEmpty()) {
			return StringPool.BLANK;
		}

		StringBundler sb = new StringBundler(mfaVerifiers.size() * 2 - 1);
		for (MFAVerifier mfaVerifier : mfaVerifiers) {
			if (sb.length() > 0) {
				sb.append(StringPool.COMMA);
			}
			sb.append(mfaVerifier.getName());
		}

		return sb.toString();
	}

	public CompositeMFAVerifier(Collection<MFAVerifier> mfaVerifiers) {
		this.mfaVerifiers = mfaVerifiers;
	}

	@Override
	public boolean supportsHeadless() {
		for (MFAVerifier mfaVerifier : mfaVerifiers) {
			if (mfaVerifier.supportsHeadless()) {
				return true;
			}
		}

		return false;
	}

	@Override
	public boolean supportsBrowser() {
		for (MFAVerifier mfaVerifier : mfaVerifiers) {
			if (mfaVerifier.supportsBrowser()) {
				return true;
			}
		}

		return false;
	}

	@Override
	public void includeSetup(
		long userId, HttpServletRequest request, HttpServletResponse response)
		throws IOException {

		for (MFAVerifier mfaVerifier : mfaVerifiers) {
			if (!mfaVerifier.supportsBrowser()) {
				continue;
			}

			if (!mfaVerifier.needsSetup(userId)) {
				continue;
			}

			BrowserMFAVerifier browserMFAVerifier =
				(BrowserMFAVerifier) mfaVerifier;

			browserMFAVerifier.includeSetup(userId, request, response);
		}
	}

	@Override
	public void includeVerification(
		long userId, HttpServletRequest request, HttpServletResponse response)
		throws IOException {

		for (MFAVerifier mfaVerifier : mfaVerifiers) {
			if (!mfaVerifier.supportsBrowser()) {
				continue;
			}

			if (!mfaVerifier.needsVerification(request, userId)) {
				continue;
			}

			BrowserMFAVerifier browserMFAVerifier =
				(BrowserMFAVerifier)mfaVerifier;

			browserMFAVerifier.includeVerification(userId, request, response);
		}
	}

	@Override
	public boolean needsVerification(HttpServletRequest request, long userId) {
		for (MFAVerifier mfaVerifier : mfaVerifiers) {
			if (mfaVerifier.needsVerification(request, userId)) {
				return true;
			}
		}

		return false;
	}

	@Override
	public boolean needsSetup(long userId) {
		for (MFAVerifier mfaVerifier : mfaVerifiers) {
			if (mfaVerifier.needsSetup(userId)) {
				return true;
			}
		}

		return false;
	}

	protected Collection<MFAVerifier> mfaVerifiers;

}
