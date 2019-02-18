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

package com.liferay.multi.factor.authentication.provider.otp.web.internal.spi.verifier;

import com.liferay.multi.factor.authentication.provider.otp.model.OTP;
import com.liferay.multi.factor.authentication.provider.otp.service.OTPLocalService;
import com.liferay.multi.factor.authentication.spi.verifier.BrowserMFAVerifier;
import com.liferay.multi.factor.authentication.spi.verifier.MFAVerifier;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.security.auth.PrincipalException;
import com.liferay.portal.kernel.service.UserLocalService;
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
public class OTPMFAVerifier implements BrowserMFAVerifier, MFAVerifier {

	@Override
	public String getName() {
		return "time-based-one-time-password";
	}

	@Override
	public void includeSetup(
			long userId, HttpServletRequest request,
			HttpServletResponse response)
		throws IOException {

		try {
			OTP otp = _otpLocalService.fetchOTPByUserId(userId);

			if (otp != null) {
				if (otp.isVerified()) {
					throw new PrincipalException("Setup is already finished!");
				}

				_otpLocalService.deleteOTP(userId);
			}
		}
		catch (PortalException pe) {
			_log.error("Unable to delete otp: " + pe.getMessage(), pe);
		}

		RequestDispatcher requestDispatcher =
			_servletContext.getRequestDispatcher("/setup_otp.jsp");

		try {
			requestDispatcher.include(request, response);
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

		OTP otp = _otpLocalService.fetchOTPByUserId(userId);

		request.setAttribute("sendToEmail", otp.getEmailAddress());

		RequestDispatcher requestDispatcher =
			_servletContext.getRequestDispatcher("/verify_otp.jsp");

		try {
			requestDispatcher.include(request, response);
		}
		catch (ServletException se) {
			throw new IOException(
				"Unable to include /verify_otp.jsp: " + se, se);
		}
	}

	@Override
	public boolean needsSetup(long userId) {
		OTP otp = _otpLocalService.fetchOTPByUserId(userId);

		if (otp != null) {
			return false;
		}

		return true;
	}

	@Override
	public boolean needsVerification(HttpServletRequest request, long userId) {
		if (needsSetup(userId)) {
			return true;
		}

		OTP otp = _otpLocalService.fetchOTPByUserId(userId);

		if ((otp != null) && otp.isVerified()) {
			return false;
		}

		return true;
	}

	@Override
	public boolean setup(ActionRequest actionRequest, long userId) {
		String email = ParamUtil.getString(actionRequest, "setupEmail");

		try {
			if (_verify(actionRequest)) {
				_otpLocalService.addOTP(userId, email);

				_otpLocalService.updateVerified(userId, true);

				return true;
			}
		}
		catch (PortalException pe) {
			_log.error("Unable to update otp: " + pe.getMessage(), pe);

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
		ActionRequest actionRequest, ActionResponse actionResponse,
		long userId) {

		if (needsSetup(userId)) {
			return false;
		}

		try {
			if (_verify(actionRequest)) {
				_otpLocalService.updateVerified(userId, true);

				return true;
			}

			_otpLocalService.updateVerified(userId, false);

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

	private boolean _verify(ActionRequest request) throws PortalException {
		HttpServletRequest originalServletRequest =
			_portal.getOriginalServletRequest(
				_portal.getHttpServletRequest(request));

		HttpSession session = originalServletRequest.getSession();

		long otpSetAt = (Long)session.getAttribute("otpSetAt");

		if (otpSetAt + _DURATION > System.currentTimeMillis()) {
			String expected = (String)session.getAttribute("otp");

			String userInput = ParamUtil.getString(request, "otp");

			if (expected.equals(userInput)) {
				session.removeAttribute("otp");
				session.removeAttribute("otpSetAt");

				return true;
			}
		}

		return false;
	}

	private static final long _DURATION = 60 * 1000;

	private static final Log _log = LogFactoryUtil.getLog(OTPMFAVerifier.class);

	@Reference
	private OTPLocalService _otpLocalService;

	@Reference
	private Portal _portal;

	@Reference(
		target = "(osgi.web.symbolicname=com.liferay.multi.factor.authentication.provider.otp.web)"
	)
	private ServletContext _servletContext;

	@Reference
	private UserLocalService _userLocalService;

}