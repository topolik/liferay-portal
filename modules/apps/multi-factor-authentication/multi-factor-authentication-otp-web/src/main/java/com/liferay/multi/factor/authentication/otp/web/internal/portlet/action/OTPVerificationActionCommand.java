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

package com.liferay.multi.factor.authentication.otp.web.internal.portlet.action;

import com.liferay.multi.factor.authentication.otp.model.TOTP;
import com.liferay.multi.factor.authentication.otp.service.TOTPLocalService;
import com.liferay.multi.factor.authentication.otp.web.internal.util.OTPUtil;
import com.liferay.portal.kernel.portlet.bridges.mvc.BaseMVCActionCommand;
import com.liferay.portal.kernel.portlet.bridges.mvc.MVCActionCommand;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.kernel.util.Portal;

import javax.portlet.ActionRequest;
import javax.portlet.ActionResponse;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author arthurchan35
 */
@Component(
	property = {
		"javax.portlet.name=OTPPortlet", "mvc.command.name=/otp/verify"
	},
	service = MVCActionCommand.class
)
public class OTPVerificationActionCommand extends BaseMVCActionCommand {

	@Override
	protected void doProcessAction(
			ActionRequest actionRequest, ActionResponse actionResponse)
		throws Exception {

		long userId = _portal.getUserId(actionRequest);

		TOTP totp = _totpLocalService.fetchTOTPByUserId(userId);

		String userInput = ParamUtil.getString(actionRequest, "otp");

		String generated = OTPUtil.generateTOTP(
			totp.getSharedSecret(), 30, 6, "HmacSHA1");

		if (userInput.equals(generated)) {
			_totpLocalService.updateVerified(totp.getTotpId(), true);
		}
	}

	@Reference
	private Portal _portal;

	@Reference
	private TOTPLocalService _totpLocalService;

}