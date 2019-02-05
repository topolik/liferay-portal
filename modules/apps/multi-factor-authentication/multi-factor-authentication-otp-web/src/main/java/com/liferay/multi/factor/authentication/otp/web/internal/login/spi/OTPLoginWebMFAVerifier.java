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

package com.liferay.multi.factor.authentication.otp.web.internal.login.spi;

import com.liferay.multi.factor.authentication.integration.login.web.spi.LoginWebMFAVerifier;
import com.liferay.multi.factor.authentication.integration.spi.verifier.MFAVerifier;
import com.liferay.multi.factor.authentication.integration.spi.verifier.MFAVerifierRegistry;
import com.liferay.multi.factor.authentication.otp.model.HOTP;
import com.liferay.multi.factor.authentication.otp.model.TOTP;
import com.liferay.multi.factor.authentication.otp.service.HOTPLocalService;
import com.liferay.multi.factor.authentication.otp.service.TOTPLocalService;
import com.liferay.multi.factor.authentication.otp.web.internal.util.OTPUtil;
import com.liferay.petra.string.StringBundler;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.io.BigEndianCodec;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.security.SecureRandomUtil;
import com.liferay.portal.kernel.security.auth.PrincipalException;
import com.liferay.portal.kernel.service.UserLocalService;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.kernel.util.Portal;
import com.liferay.portal.kernel.util.Validator;
import jodd.util.Base32;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

import javax.portlet.ActionRequest;
import javax.portlet.ActionResponse;
import javax.servlet.RequestDispatcher;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.Serializable;

/**
 * @author Tomas Polesovsky
 */
@Component(
	immediate = true,
	service = {MFAVerifier.class, LoginWebMFAVerifier.class}
)
public class OTPLoginWebMFAVerifier implements LoginWebMFAVerifier {

	@Override
	public void includeUserChallenge(
		long userId, HttpServletRequest request, HttpServletResponse response)
		throws IOException {

		RequestDispatcher requestDispatcher =
			_servletContext.getRequestDispatcher("/challenge_otp.jsp");

		try {
			requestDispatcher.include(request, response);
		}
		catch (ServletException e) {
			throw new IOException(
				"Unable to include /challenge_otp.jsp: " + e.getMessage(), e);
		}
	}

	@Override
	public void includeSetup(
		long userId, HttpServletRequest request, HttpServletResponse response)
		throws IOException {

		try {
			TOTP totp = _totpLocalService.fetchTOTPByUserId(userId);

			if (totp != null) {
				if (totp.isVerified()) {
					throw new PrincipalException("Setup is already finished!");
				}

				_totpLocalService.deleteTOTP(userId);
			}

			HOTP hotp = _hotpLocalService.fetchHOTPByUserId(userId);

			if (hotp != null) {
				if (hotp.isVerified()) {
					throw new PrincipalException("Setup is already finished!");
				}

				_hotpLocalService.deleteHOTP(userId);
			}
		}
		catch (PortalException e) {
			_log.error("Unable to delete totp: " + e.getMessage(), e);
		}

		int keySize = 20;

		int count = (int) Math.ceil((double) keySize / 8);

		byte[] buffer = new byte[count * 8];

		for (int i = 0; i < count; i++) {
			BigEndianCodec.putLong(
				buffer, i * 8, SecureRandomUtil.nextLong());
		}

		byte[] secret = new byte[keySize];

		System.arraycopy(buffer, 0, secret, 0, keySize);

		String sharedSecret = Base32.encode(secret);

		HttpServletRequest originalServletRequest =
			_portal.getOriginalServletRequest(request);

		HttpSession session = originalServletRequest.getSession();

		session.setAttribute("sharedSecret", sharedSecret);
		request.setAttribute("sharedSecret", sharedSecret);
		request.setAttribute(
			"mfaUser", _userLocalService.fetchUserById(userId));

		RequestDispatcher requestDispatcher =
			_servletContext.getRequestDispatcher("/setup_otp.jsp");

		try {
			requestDispatcher.include(request, response);
		}
		catch (ServletException e) {
			throw new IOException(
				"Unable to include /setup_otp.jsp: " + e.getMessage(), e);
		}
	}

	@Override
	public boolean verifyChallenge(
		long userId, ActionRequest actionRequest,
		ActionResponse actionResponse) {

		if (needsSetup(userId)) {
			return false;
		}

		String otpValue = ParamUtil.getString(actionRequest, "otp");

		if (Validator.isBlank(otpValue)) {
			return false;
		}

		HOTP hotp = _hotpLocalService.fetchHOTPByUserId(userId);

		if ((hotp != null) && hotp.isVerified()) {
			try {
				String generatedOtpValue = OTPUtil.generateHOTP(
					hotp.getSharedSecret(), hotp.getCount(), 6, "HmacSHA1");

				if (generatedOtpValue.equals(otpValue)) {
					return true;
				}
				else {
					return false;
				}
			}
			catch (Exception e) {
				_log.error(
					StringBundler.concat(
						"Unable to generate HOTP value for user " , userId,
						": ", e.getMessage()),
					e);

				return false;
			}
		}

		TOTP totp = _totpLocalService.fetchTOTPByUserId(userId);

		if ((totp != null) && totp.isVerified()) {
			try {
				String generatedOtpValue = OTPUtil.generateTOTP(
					totp.getSharedSecret(), 30, 6, "HmacSHA1");

				if (generatedOtpValue.equals(otpValue)) {
					return true;
				}
				else {
					return false;
				}
			}
			catch (Exception e) {
				_log.error(
					StringBundler.concat(
						"Unable to generate TOTP value for user " , userId,
						": ", e.getMessage()),
					e);

				return false;
			}
		}

		return true;
	}

	@Override
	public boolean verifySetup(
		long userId, ActionRequest actionRequest,
		ActionResponse actionResponse) {

		HttpServletRequest originalServletRequest =
			_portal.getOriginalServletRequest(
				_portal.getHttpServletRequest(actionRequest));

		HttpSession session = originalServletRequest.getSession();

		String sharedSecret = (String)session.getAttribute("sharedSecret");
		String otpType = ParamUtil.getString(actionRequest, "otpType");
		String otpValue = ParamUtil.getString(actionRequest, "otp");

		if ("TOTP".equals(otpType)) {
			try {
				String generatedOtpValue = OTPUtil.generateTOTP(
					sharedSecret, 30, 6, "HmacSHA1");

				if (generatedOtpValue.equals(otpValue)) {
					TOTP totp = _totpLocalService.addTOTP(userId, sharedSecret);
					_totpLocalService.updateVerified(totp.getTotpId(), true);
					return true;
				}
				else {
					_log.error("TOTP mismatch");
				}
			}
			catch (Exception e) {
				_log.error(
					StringBundler.concat(
						"Unable to generate TOTP value for user " , userId,
						": ", e.getMessage()),
					e);
			}

		}
		else if ("HOTP".equals(otpType)) {
			try {
				int count = 1;
				int digits = 6;

				String generatedOtpValue = OTPUtil.generateHOTP(
					sharedSecret, count, digits, "HmacSHA1");

				if (generatedOtpValue.equals(otpValue)) {
					HOTP hotp = _hotpLocalService.addHOTP(userId, sharedSecret);
					_hotpLocalService.updateVerified(hotp.getHotpId(), true);
					return true;
				}
				else {
					_log.error("HOTP mismatch");
				}
			}
			catch (Exception e) {
				_log.error(
					StringBundler.concat(
						"Unable to generate HOTP value for user " , userId,
						": ", e.getMessage()),
					e);

			}
		}

		return false;
	}

	@Override
	public boolean needsSetup(long userId) {
		HOTP hotp = _hotpLocalService.fetchHOTPByUserId(userId);

		if ((hotp != null) && hotp.isVerified()) {
			return false;
		}

		TOTP totp = _totpLocalService.fetchTOTPByUserId(userId);

		if ((totp != null) && totp.isVerified()) {
			return false;
		}

		return true;
	}

	@Override
	public boolean needsVerify(HttpServletRequest request, long userId) {
		if (needsSetup(userId)) {
			return false;
		}

		HttpServletRequest originalServletRequest =
			_portal.getOriginalServletRequest(request);

		HttpSession session = originalServletRequest.getSession(false);

		if (session == null) {
			return true;
		}

		MFAContext mfaContext = (MFAContext)session.getAttribute(
			MFAContext.class.getName());

		if (mfaContext == null) {
			return true;
		}

		if (!mfaContext.isValid()) {
			session.removeAttribute(MFAContext.class.getName());

			return true;
		}

		return false;
	}

	@Override
	public void setupSessionAfterVerify(ActionRequest actionRequest) {
		HttpServletRequest originalServletRequest =
			_portal.getOriginalServletRequest(
				_portal.getHttpServletRequest(actionRequest));

		HttpSession session = originalServletRequest.getSession();

		MFAContext mfaContext = new MFAContext();
		mfaContext.expiresAt = System.currentTimeMillis() + 10*60*1000;

		session.setAttribute(MFAContext.class.getName(), mfaContext);
	}

	@Reference
	private Portal _portal;

	@Reference
	private HOTPLocalService _hotpLocalService;

	@Reference
	private TOTPLocalService _totpLocalService;


	@Reference(
		target = "(osgi.web.symbolicname=com.liferay.multi.factor.authentication.otp.web)"
	)
	private ServletContext _servletContext;


	@Reference
	private UserLocalService _userLocalService;

	private class MFAContext implements Serializable {
		private long expiresAt;
		private static final long serialVersionUID = 1L;

		public boolean isValid() {
			if (System.currentTimeMillis() < expiresAt) {
				return true;
			}

			return false;
		}
	}

	private static final Log _log = LogFactoryUtil.getLog(
		OTPLoginWebMFAVerifier.class);
}
