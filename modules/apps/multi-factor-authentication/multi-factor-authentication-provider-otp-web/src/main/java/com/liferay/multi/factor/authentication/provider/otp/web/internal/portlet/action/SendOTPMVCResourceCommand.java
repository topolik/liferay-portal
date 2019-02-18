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

package com.liferay.multi.factor.authentication.provider.otp.web.internal.portlet.action;

import com.liferay.mail.kernel.model.MailMessage;
import com.liferay.mail.kernel.service.MailService;
import com.liferay.multi.factor.authentication.portlet.api.constants.MFAPortletKeys;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.portlet.bridges.mvc.MVCResourceCommand;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.kernel.util.Portal;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.kernel.util.WebKeys;

import java.io.UnsupportedEncodingException;

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
		"mvc.command.name=/mfa_verify/sendotp"
	},
	service = MVCResourceCommand.class
)
public class SendOTPMVCResourceCommand implements MVCResourceCommand {

	@Override
	public boolean serveResource(
		ResourceRequest resourceRequest, ResourceResponse resourceResponse) {

		ThemeDisplay themeDisplay = (ThemeDisplay)resourceRequest.getAttribute(
			WebKeys.THEME_DISPLAY);

		User user = themeDisplay.getUser();

		HttpServletRequest originalServletRequest =
			_portal.getOriginalServletRequest(
				_portal.getHttpServletRequest(resourceRequest));

		HttpSession session = originalServletRequest.getSession();

		String email = ParamUtil.getString(resourceRequest, "email");

		String generatedOTP = StringUtil.randomString(_LENGTH);

		session.setAttribute("otp", generatedOTP);

		session.setAttribute("otpSetAt", System.currentTimeMillis());

		try {
			MailMessage mailMessage = new MailMessage(
				new InternetAddress("test@liferay.com", "admin"),
				new InternetAddress(email, user.getFullName()),
				"Your One Time Password",
				"Your One Time Password is" + generatedOTP, true);

			_mailService.sendEmail(mailMessage);

			return true;
		}
		catch (UnsupportedEncodingException uee) {
			return false;
		}
	}

	private static final int _LENGTH = 6;

	@Reference
	private MailService _mailService;

	@Reference
	private Portal _portal;

}