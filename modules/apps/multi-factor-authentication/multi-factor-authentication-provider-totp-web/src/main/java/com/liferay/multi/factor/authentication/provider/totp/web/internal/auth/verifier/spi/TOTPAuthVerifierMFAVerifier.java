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

package com.liferay.multi.factor.authentication.provider.totp.web.internal.auth.verifier.spi;

import com.liferay.multi.factor.authentication.integration.auth.verifier.spi.AuthVerifierMFAVerifier;
import com.liferay.multi.factor.authentication.integration.spi.verifier.MFAVerifier;
import com.liferay.multi.factor.authentication.provider.totp.model.TOTP;
import com.liferay.multi.factor.authentication.provider.totp.service.TOTPLocalService;
import com.liferay.multi.factor.authentication.provider.totp.web.internal.util.OTPUtil;
import com.liferay.petra.string.StringBundler;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.util.Validator;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Tomas Polesovsky
 */
@Component(
	immediate = true,
	service = {AuthVerifierMFAVerifier.class, MFAVerifier.class}
)
public class TOTPAuthVerifierMFAVerifier implements AuthVerifierMFAVerifier {

	@Override
	public boolean needsVerification(HttpServletRequest request, long userId) {
		TOTP totp = _totpLocalService.fetchTOTPByUserId(userId);

		if ((totp != null) && totp.isVerified()) {
			return true;
		}

		return false;
	}

	@Override
	public boolean verify(
		long userId, HttpServletRequest request, HttpServletResponse response) {

		String totpValue = request.getHeader("X-2FA-Token");

		if (Validator.isBlank(totpValue)) {
			return false;
		}

		TOTP totp = _totpLocalService.fetchTOTPByUserId(userId);

		if ((totp != null) && totp.isVerified()) {
			try {
				String generatedTotpValue = OTPUtil.generateTOTP(
					totp.getSharedSecret(), 30, 6, "HmacSHA1");

				if (generatedTotpValue.equals(totpValue)) {
					return true;
				}

				return false;
			}
			catch (Exception e) {
				_log.error(
					StringBundler.concat(
						"Unable to generate TOTP value for user ", userId, ": ",
						e.getMessage()),
					e);

				return false;
			}
		}

		return false;
	}

	private static final Log _log = LogFactoryUtil.getLog(
		TOTPAuthVerifierMFAVerifier.class);

	@Reference
	private TOTPLocalService _totpLocalService;

}