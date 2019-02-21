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

package com.liferay.multi.factor.authentication.provider.totp.web.internal.spi.verifier;

import com.liferay.multi.factor.authentication.spi.verifier.BrowserMFAVerifier;
import com.liferay.multi.factor.authentication.spi.verifier.HeadlessMFAVerifier;
import com.liferay.multi.factor.authentication.spi.verifier.MFAVerifier;
import com.liferay.multi.factor.authentication.provider.totp.model.TOTP;
import com.liferay.multi.factor.authentication.provider.totp.service.TOTPLocalService;
import com.liferay.multi.factor.authentication.provider.totp.web.internal.util.TOTPUtil;
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

import javax.portlet.ActionRequest;
import javax.portlet.ActionResponse;
import javax.servlet.RequestDispatcher;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import com.liferay.portal.util.PropsValues;
import jodd.util.Base32;

import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Deactivate;
import org.osgi.service.component.annotations.Reference;

import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.WeakHashMap;

/**
 * @author Tomas Polesovsky
 */
@Component(
	immediate = true,
	service = MFAVerifier.class
)
public class TOTPMFAVerifier
	implements BrowserMFAVerifier, HeadlessMFAVerifier, MFAVerifier {

	@Activate
	protected void activate() {
		if (PropsValues.SESSION_ENABLE_PHISHING_PROTECTION) {
			List<String> sessionPhishingProtectedAttributesList = new ArrayList(
				Arrays.asList(
					PropsValues.SESSION_PHISHING_PROTECTED_ATTRIBUTES));

			sessionPhishingProtectedAttributesList.add(
				MFAContext.class.getName());

			PropsValues.SESSION_PHISHING_PROTECTED_ATTRIBUTES =
				sessionPhishingProtectedAttributesList.toArray(
					new String[sessionPhishingProtectedAttributesList.size()]);
		}
	}


	@Override
	public boolean supportsBrowser(){
		return true;
	}

	@Override
	public boolean supportsHeadless() {
		return true;
	}

	@Override
	public boolean verify(HttpServletRequest request, long userId) {
		String totpValue = request.getHeader("X-2FA-Token");

		if (Validator.isBlank(totpValue)) {
			return false;
		}

		return verify(totpValue, userId);
	}

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

		String sharedSecret = generateSharedSecret();

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

	private String generateSharedSecret() {
		int count = (int)Math.ceil((double)_algorithmKeySize / 8);

		byte[] buffer = new byte[count * 8];

		for (int i = 0; i < count; i++) {
			BigEndianCodec.putLong(buffer, i * 8, SecureRandomUtil.nextLong());
		}

		byte[] secret = new byte[_algorithmKeySize];

		System.arraycopy(buffer, 0, secret, 0, _algorithmKeySize);

		return Base32.encode(secret);
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
	public String getName() {
		return "time-based-one-time-password";
	}

	@Override
	public boolean needsVerification(HttpServletRequest request, long userId) {
		if (needsSetup(userId)) {
			return false;
		}

		HttpServletRequest originalServletRequest =
			_portal.getOriginalServletRequest(request);

		HttpSession session = originalServletRequest.getSession(false);

		if (session != null) {
			MFAContext mfaContext = (MFAContext)session.getAttribute(
				MFAContext.class.getName());

			if (mfaContext != null) {
				if (mfaContext.isValid()) {
					return false;
				}
				else {
					session.removeAttribute(MFAContext.class.getName());
				}
			}
		}

		TOTP totp = _totpLocalService.fetchTOTPByUserId(userId);

		if ((totp != null) && totp.isVerified()) {
			return true;
		}

		return false;
	}

	@Override
	public boolean setup(ActionRequest actionRequest, long userId) {
		HttpServletRequest originalServletRequest =
			_portal.getOriginalServletRequest(
				_portal.getHttpServletRequest(actionRequest));

		HttpSession session = originalServletRequest.getSession();

		String sharedSecret = (String)session.getAttribute("sharedSecret");

		String totpValue = ParamUtil.getString(actionRequest, "totp");

		try {
			if (TOTPUtil.verifyTOTP(
				Base32.decode(sharedSecret), totpValue, _clockSkew, _timeWindow,
				_digitsCount, _algorithm)) {

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
	public boolean verify(
		ActionRequest actionRequest, ActionResponse actionResponse,
		long userId) {

		if (needsSetup(userId)) {
			return false;
		}

		String totpValue = ParamUtil.getString(actionRequest, "totp");

		if (Validator.isBlank(totpValue)) {
			return false;
		}

		boolean verified = verify(totpValue, userId);

		if (verified) {
			MFAContext mfaContext = new MFAContext();

			long oneDay = 24 * 60 * 60 * 1000;

			mfaContext._expiresAt = System.currentTimeMillis() + oneDay;

			HttpServletRequest request =
				_portal.getOriginalServletRequest(
					_portal.getHttpServletRequest(actionRequest));

			HttpSession session = request.getSession();

			session.setAttribute(MFAContext.class.getName(), mfaContext);

			_httpSessionsSet.add(session);
		}

		return verified;
	}

	protected boolean verify(String totpValue, long userId) {
		TOTP totp = _totpLocalService.fetchTOTPByUserId(userId);

		if ((totp != null) && totp.isVerified()) {
			try {
				return TOTPUtil.verifyTOTP(
					Base32.decode(totp.getSharedSecret()), totpValue,
					_clockSkew,
					_timeWindow, _digitsCount, _algorithm);
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

	@Deactivate
	protected void deactivate() {
		if (PropsValues.SESSION_ENABLE_PHISHING_PROTECTION) {
			List<String> sessionPhishingProtectedAttributesList = new ArrayList(
				Arrays.asList(
					PropsValues.SESSION_PHISHING_PROTECTED_ATTRIBUTES));

			sessionPhishingProtectedAttributesList.remove(
				MFAContext.class.getName());

			PropsValues.SESSION_PHISHING_PROTECTED_ATTRIBUTES =
				sessionPhishingProtectedAttributesList.toArray(
					new String[sessionPhishingProtectedAttributesList.size()]);
		}


		for (HttpSession httpSession : _httpSessionsSet) {
			httpSession.removeAttribute(MFAContext.class.getName());
		}

		_httpSessionsSet.clear();;
	}

	private static final Log _log = LogFactoryUtil.getLog(
		TOTPMFAVerifier.class);

	@Reference
	private TOTPLocalService _totpLocalService;

	@Reference
	private Portal _portal;

	@Reference(
		target = "(osgi.web.symbolicname=com.liferay.multi.factor.authentication.provider.totp.web)"
	)
	private ServletContext _servletContext;

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

	private long _clockSkew = 3 * 1000;
	private long _timeWindow = 30 * 1000;
	private int _digitsCount = 6;
	private String _algorithm = "HmacSHA1";
	private int _algorithmKeySize = 20;

	private Set<HttpSession> _httpSessionsSet =
		Collections.newSetFromMap(
			Collections.synchronizedMap(new WeakHashMap<>()));

}