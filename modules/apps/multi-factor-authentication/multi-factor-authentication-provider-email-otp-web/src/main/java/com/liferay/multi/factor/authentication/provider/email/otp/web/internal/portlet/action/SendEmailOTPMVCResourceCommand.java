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

package com.liferay.multi.factor.authentication.provider.email.otp.web.internal.portlet.action;

import com.liferay.mail.kernel.model.MailMessage;
import com.liferay.mail.kernel.service.MailService;
import com.liferay.multi.factor.authentication.portlet.api.constants.MFAPortletKeys;
import com.liferay.multi.factor.authentication.provider.email.otp.model.EmailOTP;
import com.liferay.multi.factor.authentication.provider.email.otp.service.EmailOTPLocalService;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.portlet.bridges.mvc.MVCResourceCommand;
import com.liferay.portal.kernel.service.UserLocalService;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.kernel.util.Portal;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.kernel.util.WebKeys;

import javax.mail.internet.InternetAddress;

import javax.portlet.ResourceRequest;
import javax.portlet.ResourceResponse;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author arthurchan35
 */
@Component(
	property = {
		"javax.portlet.name=" + MFAPortletKeys.MFA_VERIFY,
		"mvc.command.name=/mfa_verify/sendemailotp"
	},
	service = MVCResourceCommand.class
)
public class SendEmailOTPMVCResourceCommand implements MVCResourceCommand {

	@Override
	public boolean serveResource(
		ResourceRequest request, ResourceResponse response) {

		HttpServletRequest originalRequest = _portal.getOriginalServletRequest(
			_portal.getHttpServletRequest(request));

		HttpSession session = originalRequest.getSession();

		Object otpSetAtObj = session.getAttribute("otpSetAt");

		long currentTime = System.currentTimeMillis();

		if (otpSetAtObj != null) {
			long otpSetAt = (Long)otpSetAtObj;

			if (otpSetAt + _DURATION > currentTime) {
				return false;
			}
		}

		try {
			User user = null;
			String email = null;
			String otpPhase = (String)session.getAttribute("otpPhase");

			if (otpPhase.equals("verify")) {
				long userId = (Long)session.getAttribute("userId");

				user = _userLocalService.fetchUserById(userId);
				EmailOTP emailOTP = _emailOTPLocalService.fetchEmailOTPByUserId(
					userId);

				email = emailOTP.getEmailAddress();
			}
			else if (otpPhase.equals("setup")) {
				ThemeDisplay themeDisplay = (ThemeDisplay)request.getAttribute(
					WebKeys.THEME_DISPLAY);

				user = themeDisplay.getUser();

				email = ParamUtil.getString(request, "email");
			}
			else {
				return false;
			}

			String generatedOTP = StringUtil.randomString(_LENGTH);

			session.setAttribute("otp", generatedOTP);

			session.setAttribute("otpSetAt", currentTime);

			MailMessage mailMessage = new MailMessage(
				new InternetAddress("test@liferay.com", "admin"),
				new InternetAddress(email, user.getFullName()),
				"Your One Time Password",
				"Your One Time Password is" + generatedOTP, true);

			_mailService.sendEmail(mailMessage);

			return true;
		}
		catch (Exception e) {
			return false;
		}
	}

	//this should be configured by admin

	private static final long _DURATION = 60 * 1000;

	private static final int _LENGTH = 6;

	@Reference
	private EmailOTPLocalService _emailOTPLocalService;

	@Reference
	private MailService _mailService;

	@Reference
	private Portal _portal;

	@Reference
	private UserLocalService _userLocalService;

}