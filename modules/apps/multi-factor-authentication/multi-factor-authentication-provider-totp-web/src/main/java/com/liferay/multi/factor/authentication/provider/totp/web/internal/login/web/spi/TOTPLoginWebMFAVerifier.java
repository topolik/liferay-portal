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

package com.liferay.multi.factor.authentication.provider.totp.web.internal.login.web.spi;

import com.liferay.multi.factor.authentication.integration.login.web.spi.LoginWebMFAVerifier;
import com.liferay.multi.factor.authentication.integration.spi.verifier.MFAVerifier;
import com.liferay.multi.factor.authentication.provider.totp.model.TOTP;
import com.liferay.multi.factor.authentication.provider.totp.service.TOTPLocalService;
import com.liferay.multi.factor.authentication.provider.totp.web.internal.util.OTPUtil;
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

import java.io.IOException;
import java.io.Serializable;

import javax.portlet.ActionRequest;
import javax.portlet.ActionResponse;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import jodd.util.Base32;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Tomas Polesovsky
 */
@Component(
	immediate = true, service = {MFAVerifier.class, LoginWebMFAVerifier.class}
)
public class TOTPLoginWebMFAVerifier implements LoginWebMFAVerifier {

	@Override
	public void includeSetup(
			long userId, HttpServletRequest request,
			HttpServletResponse response)
		throws IOException {

		try {
			TOTP totp = _totpLocalService.fetchTOTPByUserId(userId);

			if (totp != null) {
				if (totp.isVerified()) {
					throw new PrincipalException("Setup is already finished!");
				}

				_totpLocalService.deleteTOTP(userId);
			}
		}
		catch (PortalException pe) {
			_log.error("Unable to delete totp: " + pe.getMessage(), pe);
		}

		int keySize = 20;

		int count = (int)Math.ceil((double)keySize / 8);

		byte[] buffer = new byte[count * 8];

		for (int i = 0; i < count; i++) {
			BigEndianCodec.putLong(buffer, i * 8, SecureRandomUtil.nextLong());
		}

		byte[] secret = new byte[keySize];

		System.arraycopy(buffer, 0, secret, 0, keySize);

		String sharedSecret = Base32.encode(secret);

		request.setAttribute(
			"mfaUser", _userLocalService.fetchUserById(userId));

		request.setAttribute("sharedSecret", sharedSecret);

		HttpServletRequest originalServletRequest =
			_portal.getOriginalServletRequest(request);

		HttpSession session = originalServletRequest.getSession();

		session.setAttribute("sharedSecret", sharedSecret);

		RequestDispatcher requestDispatcher =
			_servletContext.getRequestDispatcher("/setup_totp.jsp");

		try {
			requestDispatcher.include(request, response);
		}
		catch (ServletException se) {
			throw new IOException(
				"Unable to include /setup_totp.jsp: " + se, se);
		}
	}

	@Override
	public void includeVerification(
			long userId, HttpServletRequest request,
			HttpServletResponse response)
		throws IOException {

		RequestDispatcher requestDispatcher =
			_servletContext.getRequestDispatcher("/verify_totp.jsp");

		try {
			requestDispatcher.include(request, response);
		}
		catch (ServletException se) {
			throw new IOException(
				"Unable to include /verify_totp.jsp: " + se, se);
		}
	}

	@Override
	public boolean needsSetup(long userId) {
		TOTP totp = _totpLocalService.fetchTOTPByUserId(userId);

		if ((totp != null) && totp.isVerified()) {
			return false;
		}

		return true;
	}

	@Override
	public boolean needsVerification(HttpServletRequest request, long userId) {
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
	public boolean setup(
		long userId, ActionRequest actionRequest,
		ActionResponse actionResponse) {

		HttpServletRequest originalServletRequest =
			_portal.getOriginalServletRequest(
				_portal.getHttpServletRequest(actionRequest));

		HttpSession session = originalServletRequest.getSession();

		String sharedSecret = (String)session.getAttribute("sharedSecret");

		String totpValue = ParamUtil.getString(actionRequest, "totp");

		try {
			String generatedTotpValue = OTPUtil.generateTOTP(
				sharedSecret, 30, 6, "HmacSHA1");

			if (generatedTotpValue.equals(totpValue)) {
				TOTP totp = _totpLocalService.addTOTP(userId, sharedSecret);

				_totpLocalService.updateVerified(totp.getTotpId(), true);

				return true;
			}
		}
		catch (Exception e) {
			_log.error(
				StringBundler.concat(
					"Unable to generate TOTP value for user ", userId, ": ",
					e.getMessage()),
				e);
		}

		return false;
	}

	@Override
	public void setupSessionAfterVerification(ActionRequest actionRequest) {
		HttpServletRequest originalServletRequest =
			_portal.getOriginalServletRequest(
				_portal.getHttpServletRequest(actionRequest));

		HttpSession session = originalServletRequest.getSession();

		MFAContext mfaContext = new MFAContext();

		long oneDay = 24 * 60 * 60 * 1000;

		mfaContext._expiresAt = System.currentTimeMillis() + oneDay;

		session.setAttribute(MFAContext.class.getName(), mfaContext);
	}

	@Override
	public boolean verify(
		long userId, ActionRequest actionRequest,
		ActionResponse actionResponse) {

		if (needsSetup(userId)) {
			return false;
		}

		String totpValue = ParamUtil.getString(actionRequest, "totp");

		if (Validator.isBlank(totpValue)) {
			return false;
		}

		TOTP totp = _totpLocalService.fetchTOTPByUserId(userId);

		if ((totp != null) && totp.isVerified()) {
			try {
				String generatedTotpValue = OTPUtil.generateTOTP(
					totp.getSharedSecret(), 30, 6, "HmacSHA1");

				if (generatedTotpValue.equals(totpValue)) {
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
		TOTPLoginWebMFAVerifier.class);

	@Reference
	private Portal _portal;

	@Reference(
		target = "(osgi.web.symbolicname=com.liferay.multi.factor.authentication.provider.totp.web)"
	)
	private ServletContext _servletContext;

	@Reference
	private TOTPLocalService _totpLocalService;

	@Reference
	private UserLocalService _userLocalService;

	private class MFAContext implements Serializable {

		public boolean isValid() {
			if (System.currentTimeMillis() < _expiresAt) {
				return true;
			}

			return false;
		}

		private static final long serialVersionUID = 1L;

		private long _expiresAt;

	}

}