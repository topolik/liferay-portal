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

import com.liferay.multi.factor.authentication.spi.verifier.HeadlessMFAVerifier;
import com.liferay.multi.factor.authentication.spi.verifier.MFAVerifier;
import com.liferay.multi.factor.authentication.provider.totp.model.TOTP;
import com.liferay.multi.factor.authentication.provider.totp.service.TOTPLocalService;
import com.liferay.multi.factor.authentication.provider.totp.web.internal.util.TOTPUtil;
import com.liferay.petra.string.StringBundler;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.util.Validator;

import javax.servlet.http.HttpServletRequest;

import jodd.util.Base32;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Tomas Polesovsky
 */
@Component(
	immediate = true,
	service = {HeadlessMFAVerifier.class, MFAVerifier.class}
)
public class TOTPHeadlessMFAVerifier implements HeadlessMFAVerifier {

	@Override
	public boolean needsVerification(HttpServletRequest request, long userId) {
		TOTP totp = _totpLocalService.fetchTOTPByUserId(userId);

		if ((totp != null) && totp.isVerified()) {
			return true;
		}

		return false;
	}

	@Override
	public boolean verify(HttpServletRequest request, long userId) {
		String totpValue = request.getHeader("X-2FA-Token");

		if (Validator.isBlank(totpValue)) {
			return false;
		}

		TOTP totp = _totpLocalService.fetchTOTPByUserId(userId);

		if ((totp != null) && totp.isVerified()) {
			try {
				long clockSkew = 3 * 1000;
				long timeWindow = 30 * 1000;
				int digitsCount = 6;
				String algorithm = "HmacSHA1";

				if (TOTPUtil.checkTOTP(
						Base32.decode(totp.getSharedSecret()), totpValue,
						clockSkew, timeWindow, digitsCount, algorithm)) {

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
		TOTPHeadlessMFAVerifier.class);

	@Reference
	private TOTPLocalService _totpLocalService;

}