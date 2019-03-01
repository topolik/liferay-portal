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
import com.liferay.multi.factor.authentication.provider.email.otp.web.internal.configuration.EmailOTPConfiguration;
import com.liferay.multi.factor.authentication.spi.verifier.BrowserMFAVerifier;
import com.liferay.multi.factor.authentication.spi.verifier.MFAVerifier;
import com.liferay.multi.factor.authentication.spi.verifier.UserAccountSetupMFAVerifier;
import com.liferay.portal.configuration.metatype.bnd.util.ConfigurableUtil;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.service.UserLocalService;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.kernel.util.Portal;
import com.liferay.portal.util.PropsValues;

import java.io.IOException;

import java.util.ArrayList;
import java.util.Arrays;
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

import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.ConfigurationPolicy;
import org.osgi.service.component.annotations.Deactivate;
import org.osgi.service.component.annotations.Reference;

/**
 * @author arthurchan35
 */
@Component(
	configurationPid = "com.liferay.multi.factor.authentication.provider.email.otp.web.internal.configuration.EmailOTPConfiguration",
	configurationPolicy = ConfigurationPolicy.OPTIONAL,
	service = MFAVerifier.class
)
public class EmailOTPMFAVerifier
	implements BrowserMFAVerifier, MFAVerifier, UserAccountSetupMFAVerifier {

	private boolean _enabled;
	private String _name;
	private boolean _forceUserSetup;
	private long _resendEmailTimeout;
	private long _validationExpirationTime;
	private boolean _allowCustomEmail;
	private EmailOTPConfiguration _emailOTPConfiguration;

	@Activate
	protected void activate(Map<String, Object> properties) {
		_emailOTPConfiguration =
			ConfigurableUtil.createConfigurable(
				EmailOTPConfiguration.class, properties);

		_allowCustomEmail = _emailOTPConfiguration.allowCustomEmail();
		_enabled = _emailOTPConfiguration.enabled();
		_forceUserSetup = _emailOTPConfiguration.forceUserSetup();
		_name = _emailOTPConfiguration.name();
		_resendEmailTimeout = _emailOTPConfiguration.resendEmailTimeout();
		_validationExpirationTime = _emailOTPConfiguration.validationExpirationTime();

		if (PropsValues.SESSION_ENABLE_PHISHING_PROTECTION) {
			List<String> sessionPhishingProtectedAttributesList = new ArrayList(
				Arrays.asList(
					PropsValues.SESSION_PHISHING_PROTECTED_ATTRIBUTES));

			sessionPhishingProtectedAttributesList.add(_VALIDATED_AT);

			PropsValues.SESSION_PHISHING_PROTECTED_ATTRIBUTES =
				sessionPhishingProtectedAttributesList.toArray(
					new String[sessionPhishingProtectedAttributesList.size()]);
		}
	}

	@Deactivate
	protected void deactivate() {
		if (PropsValues.SESSION_ENABLE_PHISHING_PROTECTION) {
			List<String> sessionPhishingProtectedAttributesList = new ArrayList(
				Arrays.asList(
					PropsValues.SESSION_PHISHING_PROTECTED_ATTRIBUTES));

			sessionPhishingProtectedAttributesList.remove(_VALIDATED_AT);

			PropsValues.SESSION_PHISHING_PROTECTED_ATTRIBUTES =
				sessionPhishingProtectedAttributesList.toArray(
					new String[sessionPhishingProtectedAttributesList.size()]);
		}
	}

	public EmailOTPConfiguration getEmailOTPConfiguration() {
		return _emailOTPConfiguration;
	}

	@Override
	public String getProviderName() {
		return "email-one-time-password";
	}

	@Override
	public void includeUserAccountSetup(
		long userId, HttpServletRequest request, HttpServletResponse response)
		throws IOException {

		includeSetup(userId, request, response);
	}

	@Override
	public boolean setupUserAccount(ActionRequest actionRequest, long userId) {
		return setup(actionRequest, userId);
	}

	public boolean isEnabled() {
		return _enabled;
	}

	@Override
	public String getName() {
		return _name;
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
			request.setAttribute(
				"emailOTPConfiguration", _emailOTPConfiguration);

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
	public void includeBrowserVerification(
			long userId, HttpServletRequest request,
			HttpServletResponse response)
		throws IOException {

		EmailOTP emailOTP = _emailOTPLocalService.fetchEmailOTPByUserId(userId);

		request.setAttribute("sendToEmail", emailOTP.getEmailAddress());

		RequestDispatcher requestDispatcher =
			_servletContext.getRequestDispatcher("/verify_otp.jsp");

		try {
			request.setAttribute(
				"emailOTPConfiguration", _emailOTPConfiguration);

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
	public boolean requiresSetup(long userId) {
		if (isUserSetUp(userId)) {
			return false;
		}

		return _forceUserSetup;
	}

	private boolean isUserSetUp(long userId) {
		EmailOTP emailOTP = _emailOTPLocalService.fetchEmailOTPByUserId(userId);

		if (emailOTP != null) {
			return true;
		}

		return false;
	}

	@Override
	public boolean requiresBrowserVerification(
		HttpServletRequest request, long userId) {

		if (!isUserSetUp(userId)) {
			return false;
		}

		HttpServletRequest originalServletRequest =
			_portal.getOriginalServletRequest(request);

		HttpSession session = originalServletRequest.getSession(false);

		if (isValid(session)) {
			return false;
		}

		return true;
	}

	@Override
	public boolean setup(ActionRequest request, long userId) {
		String userInput = ParamUtil.getString(request, "otp");

		HttpServletRequest originalRequest = _portal.getOriginalServletRequest(
			_portal.getHttpServletRequest(request));

		HttpSession session = originalRequest.getSession();

		try {
			String email = (String)session.getAttribute("otpEmail");

			if (!_allowCustomEmail) {
				User user = _userLocalService.getUserById(userId);

				email = user.getEmailAddress();
			}

			String userIP = originalRequest.getRemoteAddr();

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
	public boolean verifyBrowserRequest(
		ActionRequest request, ActionResponse response, long userId) {

		String userInput = ParamUtil.getString(request, "otp");

		HttpServletRequest originalRequest = _portal.getOriginalServletRequest(
			_portal.getHttpServletRequest(request));

		HttpSession session = originalRequest.getSession();

		try {
			boolean verified = _verify(session, userInput);

			String userIP = originalRequest.getRemoteAddr();

			if (verified) {
				long validatedAt = System.currentTimeMillis();

				session.setAttribute(_VALIDATED_AT, validatedAt);

				_emailOTPLocalService.updateAttempts(userId, true, userIP);

				return true;
			}

			_emailOTPLocalService.updateAttempts(userId, false, userIP);
		}
		catch (Exception e) {
			_log.error(e.getMessage(), e);
		}

		return false;
	}

	private boolean _verify(HttpSession session, String userInput)
		throws Exception {

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

	protected boolean isValid(HttpSession httpSession) {
		if (httpSession == null) {
			return false;
		}

		Object validatedAtObject = httpSession.getAttribute(_VALIDATED_AT);

		if (validatedAtObject != null) {
			if (_validationExpirationTime < 0) {
				return true;
			}

			long validatedAt = (Long)validatedAtObject;

			if (validatedAt + _validationExpirationTime * 1000 >
				System.currentTimeMillis()) {

				return true;
			}
		}

		return false;
	}

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

	private static final String _VALIDATED_AT =
		EmailOTPMFAVerifier.class.getName() + "#VALIDATED_AT";


}