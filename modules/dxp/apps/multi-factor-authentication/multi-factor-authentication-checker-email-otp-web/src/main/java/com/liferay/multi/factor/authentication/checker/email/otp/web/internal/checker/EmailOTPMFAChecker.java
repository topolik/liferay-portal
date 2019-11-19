/**
 * Copyright (c) 2000-present Liferay, Inc. All rights reserved.
 *
 * The contents of this file are subject to the terms of the Liferay Enterprise
 * Subscription License ("License"). You may not use this file except in
 * compliance with the License. You can obtain a copy of the License by
 * contacting Liferay, Inc. See the License for the specific language governing
 * permissions and limitations under the License, including but not limited to
 * distribution rights of the Software.
 *
 *
 *
 */

package com.liferay.multi.factor.authentication.checker.email.otp.web.internal.checker;

import com.liferay.multi.factor.authentication.checker.email.otp.model.MFAEmailOTPEntry;
import com.liferay.multi.factor.authentication.checker.email.otp.service.MFAEmailOTPEntryLocalService;
import com.liferay.multi.factor.authentication.checker.email.otp.web.internal.configuration.EmailOTPConfiguration;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.module.configuration.ConfigurationProviderUtil;
import com.liferay.portal.kernel.service.UserLocalService;
import com.liferay.portal.kernel.util.MapUtil;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.kernel.util.Portal;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.util.PropsValues;

import java.io.IOException;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
 * @author Arthur Chan
 */
@Component(service = EmailOTPMFAChecker.class)
public class EmailOTPMFAChecker {

	public void includeBrowserVerification(
			HttpServletRequest httpServletRequest,
			HttpServletResponse httpServletResponse, long userId)
		throws IOException {

		User user = _userLocalService.fetchUser(userId);

		httpServletRequest.setAttribute("sendToEmail", user.getEmailAddress());

		RequestDispatcher requestDispatcher =
			_servletContext.getRequestDispatcher("/verify_otp.jsp");

		EmailOTPConfiguration emailOTPConfiguration = _getEmailOTPConfiguration(
			userId);

		try {
			httpServletRequest.setAttribute(
				"emailOTPConfiguration", emailOTPConfiguration);

			requestDispatcher.include(httpServletRequest, httpServletResponse);

			HttpServletRequest originalHttpServletRequest =
				_portal.getOriginalServletRequest(httpServletRequest);

			HttpSession session = originalHttpServletRequest.getSession();

			session.setAttribute("otpPhase", "verify");
			session.setAttribute("userId", userId);
		}
		catch (ServletException se) {
			throw new IOException(
				"Unable to include /verify_otp.jsp: " + se, se);
		}
	}

	public boolean isBrowserVerified(
		HttpServletRequest httpServletRequest, long userId) {

		HttpServletRequest originalHttpServletRequest =
			_portal.getOriginalServletRequest(httpServletRequest);

		HttpSession session = originalHttpServletRequest.getSession(false);

		if (isVerified(session, userId)) {
			return true;
		}

		return false;
	}

	public boolean verifyBrowserRequest(
		HttpServletRequest httpServletRequest,
		HttpServletResponse httpServletResponse, long userId) {

		String userInput = ParamUtil.getString(httpServletRequest, "otp");

		if (Validator.isBlank(userInput)) {
			return false;
		}

		HttpServletRequest originalHttpServletRequest =
			_portal.getOriginalServletRequest(httpServletRequest);

		HttpSession session = originalHttpServletRequest.getSession();

		try {
			MFAEmailOTPEntry mfaEmailOTP =
				_mfaEmailOTPEntryLocalService.fetchMFAEmailOTPEntryByUserId(
					userId);

			if (mfaEmailOTP == null) {
				_mfaEmailOTPEntryLocalService.addMFAEmailOTPEntry(userId);
			}

			if (isThrottlingEnabled(userId) &&
				_reachedFailedAttemptsAllowed(userId)) {

				if (_isRetryTimedout(userId)) {
					_mfaEmailOTPEntryLocalService.resetFailedAttempts(userId);
				}
				else {
					return false;
				}
			}

			boolean verified = _verify(session, userInput);
			String userIP = originalHttpServletRequest.getRemoteAddr();

			if (verified) {
				long validatedAt = System.currentTimeMillis();

				Map<String, Object> validatedMap = _getValidatedMap(session);

				if (validatedMap == null) {
					validatedMap = new HashMap<>(2);

					session.setAttribute(_VALIDATED, validatedMap);
				}

				validatedMap.put("userId", userId);
				validatedMap.put("validatedAt", validatedAt);

				_mfaEmailOTPEntryLocalService.updateAttempts(
					userId, userIP, true);

				return true;
			}

			_mfaEmailOTPEntryLocalService.updateAttempts(userId, userIP, false);
		}
		catch (Exception e) {
			_log.error(e.getMessage(), e);
		}

		return false;
	}

	@Activate
	protected void activate(Map<String, Object> properties) {
		if (PropsValues.SESSION_ENABLE_PHISHING_PROTECTION) {
			List<String> sessionPhishingProtectedAttributesList =
				new ArrayList<>(
					Arrays.asList(
						PropsValues.SESSION_PHISHING_PROTECTED_ATTRIBUTES));

			sessionPhishingProtectedAttributesList.add(_VALIDATED);

			PropsValues.SESSION_PHISHING_PROTECTED_ATTRIBUTES =
				sessionPhishingProtectedAttributesList.toArray(new String[0]);
		}
	}

	@Deactivate
	protected void deactivate() {
		if (PropsValues.SESSION_ENABLE_PHISHING_PROTECTION) {
			List<String> sessionPhishingProtectedAttributesList =
				new ArrayList<>(
					Arrays.asList(
						PropsValues.SESSION_PHISHING_PROTECTED_ATTRIBUTES));

			sessionPhishingProtectedAttributesList.remove(_VALIDATED);

			PropsValues.SESSION_PHISHING_PROTECTED_ATTRIBUTES =
				sessionPhishingProtectedAttributesList.toArray(new String[0]);
		}
	}

	protected boolean isThrottlingEnabled(long userId) {
		EmailOTPConfiguration emailOTPConfiguration = _getEmailOTPConfiguration(
			userId);

		long retryTimeout = emailOTPConfiguration.retryTimeout();

		int failedAttemptsAllowed =
			emailOTPConfiguration.failedAttemptsAllowed();

		if ((retryTimeout < 0) || (failedAttemptsAllowed < 0)) {
			return false;
		}

		return true;
	}

	protected boolean isVerified(HttpSession httpSession, long userId) {
		if (httpSession == null) {
			return false;
		}

		Map<String, Object> validatedMap = _getValidatedMap(httpSession);

		if (validatedMap != null) {
			if (userId != MapUtil.getLong(validatedMap, "userId")) {
				return false;
			}

			EmailOTPConfiguration emailOTPConfiguration =
				_getEmailOTPConfiguration(userId);

			long validationExpirationTime =
				emailOTPConfiguration.validationExpirationTime();

			if (validationExpirationTime < 0) {
				return true;
			}

			long validatedAt = MapUtil.getLong(validatedMap, "validatedAt");

			if ((validatedAt + validationExpirationTime * 1000) >
					System.currentTimeMillis()) {

				return true;
			}
		}

		return false;
	}

	private EmailOTPConfiguration _getEmailOTPConfiguration(long userId) {
		try {
			User user = _userLocalService.getUser(userId);

			return ConfigurationProviderUtil.getCompanyConfiguration(
				EmailOTPConfiguration.class, user.getCompanyId());
		}
		catch (Exception e) {
			return null;
		}
	}

	@SuppressWarnings("unchecked")
	private Map<String, Object> _getValidatedMap(HttpSession session) {
		return (Map<String, Object>)session.getAttribute(_VALIDATED);
	}

	private boolean _isRetryTimedout(long userId) {
		EmailOTPConfiguration emailOTPConfiguration = _getEmailOTPConfiguration(
			userId);

		long retryTimeout = emailOTPConfiguration.retryTimeout();

		MFAEmailOTPEntry mfaEmailOTP =
			_mfaEmailOTPEntryLocalService.fetchMFAEmailOTPEntryByUserId(userId);

		Date lastFailedDate = mfaEmailOTP.getLastFailDate();

		if ((lastFailedDate.getTime() + retryTimeout) >
				System.currentTimeMillis()) {

			return false;
		}

		return true;
	}

	private boolean _reachedFailedAttemptsAllowed(long userId) {
		EmailOTPConfiguration emailOTPConfiguration = _getEmailOTPConfiguration(
			userId);

		int failedAttemptsAllowed =
			emailOTPConfiguration.failedAttemptsAllowed();

		MFAEmailOTPEntry mfaEmailOTP =
			_mfaEmailOTPEntryLocalService.fetchMFAEmailOTPEntryByUserId(userId);

		if (mfaEmailOTP.getFailedAttempts() >= failedAttemptsAllowed) {
			return true;
		}

		return false;
	}

	private boolean _verify(HttpSession session, String userInput)
		throws Exception {

		String expected = (String)session.getAttribute("otp");

		if ((expected == null) || !expected.equals(userInput)) {
			return false;
		}

		session.removeAttribute("otp");
		session.removeAttribute("otpSetAt");
		session.removeAttribute("otpPhase");
		session.removeAttribute("userId");

		return true;
	}

	private static final String _VALIDATED =
		EmailOTPMFAChecker.class.getName() + "#VALIDATED";

	private static final Log _log = LogFactoryUtil.getLog(
		EmailOTPMFAChecker.class);

	@Reference
	private MFAEmailOTPEntryLocalService _mfaEmailOTPEntryLocalService;

	@Reference
	private Portal _portal;

	@Reference(
		target = "(osgi.web.symbolicname=com.liferay.multi.factor.authentication.checker.email.otp.web)"
	)
	private ServletContext _servletContext;

	@Reference
	private UserLocalService _userLocalService;

}