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

package com.liferay.multi.factor.authentication.provider.time.otp.web.internal.spi.verifier;

import com.liferay.multi.factor.authentication.provider.time.otp.model.TimeOTPEntry;
import com.liferay.multi.factor.authentication.provider.time.otp.service.TimeOTPEntryLocalService;
import com.liferay.multi.factor.authentication.provider.time.otp.web.internal.configuration.TimeOTPConfiguration;
import com.liferay.multi.factor.authentication.provider.time.otp.web.internal.util.TimeOTPUtil;
import com.liferay.multi.factor.authentication.spi.verifier.BrowserMFAVerifier;
import com.liferay.multi.factor.authentication.spi.verifier.HeadlessMFAVerifier;
import com.liferay.multi.factor.authentication.spi.verifier.MFAVerifier;
import com.liferay.multi.factor.authentication.spi.verifier.UserAccountSetupMFAVerifier;
import com.liferay.petra.string.StringBundler;
import com.liferay.portal.configuration.metatype.bnd.util.ConfigurableUtil;
import com.liferay.portal.kernel.io.BigEndianCodec;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.security.SecureRandomUtil;
import com.liferay.portal.kernel.service.UserLocalService;
import com.liferay.portal.kernel.util.MapUtil;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.kernel.util.Portal;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.util.PropsValues;

import java.io.IOException;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.portlet.ActionRequest;
import javax.portlet.ActionResponse;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import jodd.util.Base32;

import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.ConfigurationPolicy;
import org.osgi.service.component.annotations.Deactivate;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Tomas Polesovsky
 */
@Component(
	configurationPid = "com.liferay.multi.factor.authentication.provider.time.otp.web.internal.configuration.TimeOTPConfiguration",
	configurationPolicy = ConfigurationPolicy.REQUIRE, immediate = true,
	service = MFAVerifier.class
)
public class TimeOTPMFAVerifier
	implements BrowserMFAVerifier, HeadlessMFAVerifier, MFAVerifier,
			   UserAccountSetupMFAVerifier {

	@Override
	public boolean forceUserSetup(long userId) {
		if (isUserSetUp(userId)) {
			return false;
		}

		if (_forceUserSetup) {
			return true;
		}

		return false;
	}

	@Override
	public String getName() {
		return _name;
	}

	@Override
	public String getProviderName() {
		return "time-based-one-time-password";
	}

	@Override
	public void includeBrowserVerification(
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
	public void includeSetup(
			long userId, HttpServletRequest request,
			HttpServletResponse response)
		throws IOException {

		TimeOTPEntry entry = _timeOTPEntryLocalService.fetchEntryByUserId(
			userId);

		if (entry != null) {
			_log.error("Setup is already finished for user: " + userId);

			return;
		}

		String sharedSecret = _generateSharedSecret();

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
	public void includeUserAccountSetup(
			long userId, HttpServletRequest request,
			HttpServletResponse response)
		throws IOException {

		includeSetup(userId, request, response);
	}

	@Override
	public boolean isBrowserSetupComplete(
		HttpServletRequest request, long userId) {

		return canVerify(request, userId);
	}

	@Override
	public boolean isBrowserVerified(HttpServletRequest request, long userId) {
		HttpServletRequest originalServletRequest =
			_portal.getOriginalServletRequest(request);

		HttpSession session = originalServletRequest.getSession(false);

		if (isValid(session, userId)) {
			return true;
		}

		return false;
	}

	@Override
	public boolean isEnabled() {
		return _enabled;
	}

	@Override
	public boolean isHeadlessSetupComplete(
		HttpServletRequest request, long userId) {

		return canVerify(request, userId);
	}

	@Override
	public boolean isHeadlessVerified(HttpServletRequest request, long userId) {
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
			if (TimeOTPUtil.verifyTOTP(
					Base32.decode(sharedSecret), totpValue, _clockSkew,
					_timeWindow, _digitsCount, _algorithm)) {

				String userIP = originalServletRequest.getRemoteAddr();

				_timeOTPEntryLocalService.addEntry(
					userId, sharedSecret, userIP);

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
	public boolean setupUserAccount(ActionRequest actionRequest, long userId) {
		return setup(actionRequest, userId);
	}

	@Override
	public boolean verifyBrowserRequest(
		ActionRequest actionRequest, ActionResponse actionResponse,
		long userId) {

		if (!isUserSetUp(userId)) {
			return false;
		}

		String totpValue = ParamUtil.getString(actionRequest, "totp");

		if (Validator.isBlank(totpValue)) {
			return false;
		}

		HttpServletRequest request = _portal.getOriginalServletRequest(
			_portal.getHttpServletRequest(actionRequest));

		boolean verified = verify(totpValue, userId, request.getRemoteAddr());

		if (verified) {
			long validatedAt = System.currentTimeMillis();

			HttpSession session = request.getSession();

			Map<String, Object> validatedMap = new HashMap(2);

			validatedMap.put("validatedAt", validatedAt);
			validatedMap.put("userId", userId);

			session.setAttribute(_VALIDATED, validatedMap);
		}

		return verified;
	}

	@Override
	public boolean verifyHeadlessRequest(
		HttpServletRequest request, long userId) {

		if (!isUserSetUp(userId)) {
			return false;
		}

		String totpValue = request.getHeader(_headlessHeaderName);

		if (Validator.isBlank(totpValue)) {
			return false;
		}

		HttpServletRequest originalRequest = _portal.getOriginalServletRequest(
			request);

		return verify(totpValue, userId, originalRequest.getRemoteAddr());
	}

	@Activate
	protected void activate(Map<String, Object> properties) {
		TimeOTPConfiguration totpConfiguration =
			ConfigurableUtil.createConfigurable(
				TimeOTPConfiguration.class, properties);

		_algorithm = totpConfiguration.algorithm();
		_algorithmKeySize = totpConfiguration.algorithmKeySize();
		_clockSkew = totpConfiguration.clockSkew();
		_digitsCount = totpConfiguration.digitsCount();
		_enabled = totpConfiguration.enabled();
		_forceUserSetup = totpConfiguration.forceUserSetup();
		_headlessHeaderName = totpConfiguration.headlessHeaderName();
		_name = totpConfiguration.name();
		_timeWindow = totpConfiguration.timeWindow();
		_validationExpirationTime =
			totpConfiguration.validationExpirationTime();

		if (PropsValues.SESSION_ENABLE_PHISHING_PROTECTION) {
			List<String> sessionPhishingProtectedAttributesList = new ArrayList(
				Arrays.asList(
					PropsValues.SESSION_PHISHING_PROTECTED_ATTRIBUTES));

			sessionPhishingProtectedAttributesList.add(_VALIDATED);

			PropsValues.SESSION_PHISHING_PROTECTED_ATTRIBUTES =
				sessionPhishingProtectedAttributesList.toArray(
					new String[sessionPhishingProtectedAttributesList.size()]);
		}
	}

	protected boolean canVerify(HttpServletRequest request, long userId) {
		if (!isUserSetUp(userId)) {
			return false;
		}

		return true;
	}

	@Deactivate
	protected void deactivate() {
		if (PropsValues.SESSION_ENABLE_PHISHING_PROTECTION) {
			List<String> sessionPhishingProtectedAttributesList = new ArrayList(
				Arrays.asList(
					PropsValues.SESSION_PHISHING_PROTECTED_ATTRIBUTES));

			sessionPhishingProtectedAttributesList.remove(_VALIDATED);

			PropsValues.SESSION_PHISHING_PROTECTED_ATTRIBUTES =
				sessionPhishingProtectedAttributesList.toArray(
					new String[sessionPhishingProtectedAttributesList.size()]);
		}
	}

	protected boolean isUserSetUp(long userId) {
		TimeOTPEntry entry = _timeOTPEntryLocalService.fetchEntryByUserId(
			userId);

		if (entry != null) {
			return true;
		}

		return false;
	}

	protected boolean isValid(HttpSession httpSession, long userId) {
		if (httpSession == null) {
			return false;
		}

		Map<String, Object> validatedMap = (Map)httpSession.getAttribute(
			_VALIDATED);

		if (validatedMap != null) {
			if (userId != MapUtil.getLong(validatedMap, "userId")) {
				return false;
			}

			if (_validationExpirationTime < 0) {
				return true;
			}

			long validatedAt = MapUtil.getLong(validatedMap, "validatedAt");

			if (validatedAt + _validationExpirationTime * 1000 >
					System.currentTimeMillis()) {

				return true;
			}
		}

		return false;
	}

	protected boolean verify(String totpValue, long userId, String userIP) {
		TimeOTPEntry entry = _timeOTPEntryLocalService.fetchEntryByUserId(
			userId);

		if (entry != null) {
			try {
				if (TimeOTPUtil.verifyTOTP(
						Base32.decode(entry.getSharedSecret()), totpValue,
						_clockSkew, _timeWindow, _digitsCount, _algorithm)) {

					_timeOTPEntryLocalService.updateAttempts(
						userId, true, userIP);

					return true;
				}

				_timeOTPEntryLocalService.updateAttempts(userId, false, userIP);
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

	private String _generateSharedSecret() {
		int count = (int)Math.ceil((double)_algorithmKeySize / 8);

		byte[] buffer = new byte[count * 8];

		for (int i = 0; i < count; i++) {
			BigEndianCodec.putLong(buffer, i * 8, SecureRandomUtil.nextLong());
		}

		byte[] secret = new byte[_algorithmKeySize];

		System.arraycopy(buffer, 0, secret, 0, _algorithmKeySize);

		return Base32.encode(secret);
	}

	private static final String _VALIDATED =
		TimeOTPMFAVerifier.class.getName() + "#VALIDATED";

	private static final Log _log = LogFactoryUtil.getLog(
		TimeOTPMFAVerifier.class);

	private String _algorithm = "HmacSHA1";
	private int _algorithmKeySize = 20;
	private long _clockSkew = 3 * 1000;
	private int _digitsCount = 6;
	private boolean _enabled;
	private boolean _forceUserSetup;
	private String _headlessHeaderName;
	private String _name;

	@Reference
	private Portal _portal;

	@Reference(
		target = "(osgi.web.symbolicname=com.liferay.multi.factor.authentication.provider.time.otp.web)"
	)
	private ServletContext _servletContext;

	@Reference
	private TimeOTPEntryLocalService _timeOTPEntryLocalService;

	private long _timeWindow = 30 * 1000;

	@Reference
	private UserLocalService _userLocalService;

	private long _validationExpirationTime;

}