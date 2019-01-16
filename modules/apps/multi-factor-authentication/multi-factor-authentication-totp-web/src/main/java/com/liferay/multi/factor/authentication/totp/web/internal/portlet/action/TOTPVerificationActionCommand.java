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

import com.liferay.multi.factor.authentication.totp.web.internal.util.OTPUtil;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.portlet.bridges.mvc.BaseMVCActionCommand;
import com.liferay.portal.kernel.portlet.bridges.mvc.MVCActionCommand;
import com.liferay.portal.kernel.service.UserLocalService;
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

		String generated = OTPUtil.generateTOTP(user.getTotpSecret(), 30, 6, "HmacSHA1");

		if (userInput.equals(generated)) {
			_userLocalService.updateTOTPVerified(user.getUserId(), true);
		}
	}

	@Reference
	private Portal _portal;

	@Reference
	private UserLocalService _userLocalService;

}