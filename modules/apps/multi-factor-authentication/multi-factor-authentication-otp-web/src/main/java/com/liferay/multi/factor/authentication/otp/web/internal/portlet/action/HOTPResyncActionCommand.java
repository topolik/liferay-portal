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

import com.liferay.multi.factor.authentication.otp.model.HOTP;
import com.liferay.multi.factor.authentication.otp.service.HOTPLocalService;
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
		"javax.portlet.name=OTPPortlet", "mvc.command.name=/hotp/resync"
	},
	service = MVCActionCommand.class
)
public class HOTPResyncActionCommand extends BaseMVCActionCommand {

	@Override
	protected void doProcessAction(
			ActionRequest actionRequest, ActionResponse actionResponse)
		throws Exception {

		String hotps = ParamUtil.getString(actionRequest, "hotps");

		String[] hotpsArr = hotps.split(",");

		long userId = _portal.getUserId(actionRequest);

		HOTP hotp = _hotpLocalService.fetchHOTPByUserId(userId);

		int increment = OTPUtil.resyncHOTPWithConsecHOTPs(
			hotp.getSharedSecret(), hotp.getCount(), 6, "HmacSHA1", hotpsArr,
			10);

		if (increment > 0) {
			_hotpLocalService.resync(hotp.getHotpId(), increment);
		}

		actionResponse.setRenderParameter("mvcPath", "/otp/verify");
	}

	@Reference
	private HOTPLocalService _hotpLocalService;

	@Reference
	private Portal _portal;

}