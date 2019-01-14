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

package com.liferay.multi.factor.authentication.totp.web.internal.portlet.action;

import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.portlet.bridges.mvc.BaseMVCActionCommand;
import com.liferay.portal.kernel.portlet.bridges.mvc.MVCActionCommand;
import com.liferay.portal.kernel.service.UserLocalService;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.kernel.util.Portal;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;

import javax.portlet.ActionRequest;
import javax.portlet.ActionResponse;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author arthurchan35
 */
@Component(
	property = {
		"javax.portlet.name=TOTPPortlet", "mvc.command.name=/totp/verify"
	},
	service = MVCActionCommand.class
)
public class TOTPVerificationActionCommand extends BaseMVCActionCommand {

	@Override
	protected void doProcessAction(
			ActionRequest actionRequest, ActionResponse actionResponse)
		throws Exception {

		long userId = _portal.getUserId(actionRequest);

		User user = _userLocalService.fetchUser(userId);

		String userInput = ParamUtil.getString(actionRequest, "totp");

		String generated = _generateTOTP(user.getTotpSecret());

		if (userInput.equals(generated)) {
			_userLocalService.updateTOTPVerified(user.getUserId(), true);
		}
	}

	private byte[] _generateHMAC(String secret, String message, String hashAlg)
		throws Exception {

		SecretKeySpec keySpec = new SecretKeySpec(secret.getBytes(), hashAlg);
		Mac mac = Mac.getInstance(hashAlg);

		mac.init(keySpec);

		return mac.doFinal(message.getBytes());
	}

	private String _generateTOTP(String totpSecret) throws Exception {

		//start, these values should be configured according to authenticator
		int timeWindow = 30;
		int totpSize = 6; //cant be longer than 10,otherwise we need more bytes in binary, see below
		String shaAlgor = "HmacSHA1";
		//end

		long intervals = System.currentTimeMillis() / (1000 * (long)timeWindow);

		String message = String.valueOf(intervals);

		byte[] hmac = _generateHMAC(totpSecret, message, shaAlgor);

		int offset = hmac[hmac.length - 1] & 0xf;

		int binary = hmac[offset + 0x3] & 0xff;

		binary |= (hmac[offset + 0x2] & 0xff) << 0x08;
		binary |= (hmac[offset + 0x1] & 0xff) << 0x10;
		binary |= (hmac[offset + 0x0] & 0x7f) << 0x18;

		int modulo = binary % (int)Math.pow(10, totpSize);

		return String.valueOf(modulo);
	}

	@Reference
	private Portal _portal;

	@Reference
	private UserLocalService _userLocalService;

}