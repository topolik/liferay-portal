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

package com.liferay.multi.factor.authentication.provider.email.otp.web.internal.spi.verifier;

import com.liferay.multi.factor.authentication.provider.email.otp.model.EmailOTP;
import com.liferay.multi.factor.authentication.provider.email.otp.service.EmailOTPLocalService;
import com.liferay.multi.factor.authentication.spi.verifier.BrowserMFAVerifier;
import com.liferay.multi.factor.authentication.spi.verifier.MFAVerifier;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.service.UserLocalService;
import com.liferay.portal.kernel.servlet.HttpHeaders;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.kernel.util.Portal;

import java.io.IOException;

import javax.portlet.ActionRequest;
import javax.portlet.ActionResponse;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Deactivate;
import org.osgi.service.component.annotations.Reference;

/**
 * @author arthurchan35
 */
@Component(immediate = true, service = MFAVerifier.class)
public class EmailOTPMFAVerifier implements BrowserMFAVerifier, MFAVerifier {

	@Override
	public String getName() {
		return "email-one-time-password";
	}

	@Override
	public void includeSetup(
			long userId, HttpServletRequest request,
			HttpServletResponse response)
		throws IOException {

		EmailOTP emailOTP = _emailOTPLocalService.fetchEmailOTPByUserId(userId);

		//todo: include some parameter so we can allow user to re-setup

		if (emailOTP != null) {
			_log.error("Setup is already finished for user: " + userId);

			return;
		}

		RequestDispatcher requestDispatcher =
			_servletContext.getRequestDispatcher("/setup_otp.jsp");

		try {
			request.setAttribute("resendDuration", _DURATION / 1000);

			requestDispatcher.include(request, response);

			HttpServletRequest originalRequest =
				_portal.getOriginalServletRequest(request);

			HttpSession session = originalRequest.getSession();

			session.setAttribute("otpPhase", "setup");
		}
		catch (ServletException se) {
			throw new IOException(
				"Unable to include /setup_otp.jsp: " + se, se);
		}
	}

	@Override
	public void includeVerification(
			long userId, HttpServletRequest request,
			HttpServletResponse response)
		throws IOException {

		EmailOTP emailOTP = _emailOTPLocalService.fetchEmailOTPByUserId(userId);

		request.setAttribute("sendToEmail", emailOTP.getEmailAddress());

		RequestDispatcher requestDispatcher =
			_servletContext.getRequestDispatcher("/verify_otp.jsp");

		try {
			request.setAttribute("resendDuration", _DURATION / 1000);

			requestDispatcher.include(request, response);

			HttpServletRequest originalRequest =
				_portal.getOriginalServletRequest(request);

			HttpSession session = originalRequest.getSession();

			session.setAttribute("otpPhase", "verify");
			session.setAttribute("userId", userId);
		}
		catch (ServletException se) {
			throw new IOException(
				"Unable to include /verify_otp.jsp: " + se, se);
		}
	}

	@Override
	public boolean needsSetup(long userId) {
		EmailOTP emailOTP = _emailOTPLocalService.fetchEmailOTPByUserId(userId);

		if (emailOTP != null) {
			return false;
		}

		return true;
	}

	@Override
	public boolean needsVerification(HttpServletRequest request, long userId) {
		if (needsSetup(userId)) {
			return false;
		}

		return true;
	}

	@Override
	public boolean setup(ActionRequest request, long userId) {
		String email = ParamUtil.getString(request, "setupEmail");

		String userInput = ParamUtil.getString(request, "otp");

		HttpServletRequest originalRequest = _portal.getOriginalServletRequest(
			_portal.getHttpServletRequest(request));

		HttpSession session = originalRequest.getSession();

		String userIP = originalRequest.getHeader(HttpHeaders.X_FORWARDED_FOR);

		try {
			if (_verify(session, userInput)) {
				_emailOTPLocalService.addEmailOTP(userId, email, userIP);

				_emailOTPLocalService.updateAttempts(userId, true, userIP);

				return true;
			}
		}
		catch (Exception e) {
			_log.error("Unable to update emailOTP: " + e.getMessage(), e);

			return false;
		}

		return false;
	}

	@Override
	public boolean supportsBrowser() {
		return true;
	}

	@Override
	public boolean supportsHeadless() {
		return false;
	}

	@Override
	public boolean verify(
		ActionRequest request, ActionResponse response, long userId) {

		String userInput = ParamUtil.getString(request, "otp");

		HttpServletRequest originalRequest = _portal.getOriginalServletRequest(
			_portal.getHttpServletRequest(request));

		HttpSession session = originalRequest.getSession();

		String userIP = originalRequest.getHeader(HttpHeaders.X_FORWARDED_FOR);

		try {
			if (_verify(session, userInput)) {
				_emailOTPLocalService.updateAttempts(userId, true, userIP);

				return true;
			}

			_emailOTPLocalService.updateAttempts(userId, false, userIP);

			return false;
		}
		catch (Exception e) {
			return false;
		}
	}

	@Activate
	protected void activate() {
	}

	@Deactivate
	protected void deactivate() {
	}

	private boolean _verify(HttpSession session, String userInput)
		throws Exception {

		long otpSetAt = (Long)session.getAttribute("otpSetAt");

		if (otpSetAt + _DURATION < System.currentTimeMillis()) {
			session.removeAttribute("otp");
			session.removeAttribute("otpSetAt");

			return false;
		}

		String expected = (String)session.getAttribute("otp");

		// user may make typo, not removing attributes to allow retry

		if (!expected.equals(userInput)) {
			return false;
		}

		session.removeAttribute("otp");
		session.removeAttribute("otpSetAt");
		session.removeAttribute("otpPhase");
		session.removeAttribute("userId");

		return true;
	}

	private static final long _DURATION = 60 * 1000;

	private static final Log _log = LogFactoryUtil.getLog(
		EmailOTPMFAVerifier.class);

	@Reference
	private EmailOTPLocalService _emailOTPLocalService;

	@Reference
	private Portal _portal;

	@Reference(
		target = "(osgi.web.symbolicname=com.liferay.multi.factor.authentication.provider.email.otp.web)"
	)
	private ServletContext _servletContext;

	@Reference
	private UserLocalService _userLocalService;

}