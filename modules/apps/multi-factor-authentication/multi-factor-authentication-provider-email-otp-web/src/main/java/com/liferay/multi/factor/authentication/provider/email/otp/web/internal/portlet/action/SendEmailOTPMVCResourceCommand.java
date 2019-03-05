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
import com.liferay.mail.kernel.template.MailTemplate;
import com.liferay.mail.kernel.template.MailTemplateContext;
import com.liferay.mail.kernel.template.MailTemplateContextBuilder;
import com.liferay.mail.kernel.template.MailTemplateFactoryUtil;
import com.liferay.multi.factor.authentication.api.MFARegistry;
import com.liferay.multi.factor.authentication.portlet.api.constants.MFAPortletKeys;
import com.liferay.multi.factor.authentication.provider.email.otp.model.EmailOTPEntry;
import com.liferay.multi.factor.authentication.provider.email.otp.service.EmailOTPEntryLocalService;
import com.liferay.multi.factor.authentication.provider.email.otp.web.internal.configuration.EmailOTPConfiguration;
import com.liferay.multi.factor.authentication.provider.email.otp.web.internal.spi.verifier.EmailOTPMFAVerifier;
import com.liferay.multi.factor.authentication.spi.verifier.MFAVerifier;
import com.liferay.petra.string.StringBundler;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.model.Company;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.module.configuration.ConfigurationProvider;
import com.liferay.portal.kernel.portlet.bridges.mvc.MVCResourceCommand;
import com.liferay.portal.kernel.resource.manager.ClassLoaderResourceManager;
import com.liferay.portal.kernel.security.auth.AuthToken;
import com.liferay.portal.kernel.security.auth.PrincipalException;
import com.liferay.portal.kernel.service.CompanyLocalService;
import com.liferay.portal.kernel.service.UserLocalService;
import com.liferay.portal.kernel.settings.LocationVariableResolver;
import com.liferay.portal.kernel.settings.SettingsLocatorHelper;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.HtmlUtil;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.kernel.util.Portal;
import com.liferay.portal.kernel.util.PwdGenerator;
import com.liferay.portal.kernel.util.WebKeys;

import java.io.IOException;

import javax.mail.internet.InternetAddress;

import javax.portlet.PortletException;
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
			ResourceRequest request, ResourceResponse response)
		throws PortletException {

		HttpServletRequest originalRequest = _portal.getOriginalServletRequest(
			_portal.getHttpServletRequest(request));

		try {
			_authToken.checkCSRFToken(
				originalRequest,
				SendEmailOTPMVCResourceCommand.class.getName());
		}
		catch (PrincipalException pe) {
			throw new PortletException(pe);
		}

		EmailOTPConfiguration emailOTPConfiguration = getEmailOTPConfiguration(
			request);

		if (emailOTPConfiguration == null) {
			return false;
		}

		HttpSession session = originalRequest.getSession();

		if (!_verifyEmailResendTimedOut(emailOTPConfiguration, session)) {
			return false;
		}

		try {
			User user = null;
			String email = null;
			String otpPhase = (String)session.getAttribute("otpPhase");

			if (otpPhase.equals("verify")) {
				long userId = (Long)session.getAttribute("userId");

				user = _userLocalService.fetchUserById(userId);

				EmailOTPEntry entry =
					_emailOTPEntryLocalService.fetchEntryByUserId(userId);

				email = entry.getEmailAddress();

				session.setAttribute("otpEmail", email);
			}
			else if (otpPhase.equals("setup")) {
				ThemeDisplay themeDisplay = (ThemeDisplay)request.getAttribute(
					WebKeys.THEME_DISPLAY);

				user = themeDisplay.getUser();

				email = user.getEmailAddress();

				if (emailOTPConfiguration.allowCustomEmail()) {
					email = ParamUtil.getString(originalRequest, "setupEmail");
				}
			}
			else {
				return false;
			}

			String generatedOTP = PwdGenerator.getPassword(
				emailOTPConfiguration.otpSize());

			session.setAttribute("otp", generatedOTP);

			session.setAttribute("otpSetAt", System.currentTimeMillis());

			MailTemplateContextBuilder mailTemplateContextBuilder =
				MailTemplateFactoryUtil.createMailTemplateContextBuilder();

			mailTemplateContextBuilder.put(
				"[$FROM_ADDRESS$]", emailOTPConfiguration.emailTemplateFrom());
			mailTemplateContextBuilder.put(
				"[$FROM_NAME$]",
				HtmlUtil.escape(emailOTPConfiguration.emailTemplateFromName()));
			mailTemplateContextBuilder.put(
				"[$ONE_TIME_PASSWORD$]", HtmlUtil.escape(generatedOTP));
			mailTemplateContextBuilder.put(
				"[$PORTAL_URL$]", _portal.getPortalURL(originalRequest));
			mailTemplateContextBuilder.put(
				"[$REMOTE_ADDRESS$]", originalRequest.getRemoteAddr());
			mailTemplateContextBuilder.put(
				"[$REMOTE_HOST$]",
				HtmlUtil.escape(originalRequest.getRemoteHost()));
			mailTemplateContextBuilder.put(
				"[$TO_NAME$]", HtmlUtil.escape(user.getFullName()));

			MailTemplateContext mailTemplateContext =
				mailTemplateContextBuilder.build();

			String emailTemplateSubject = _locationVariableResolver.resolve(
				emailOTPConfiguration.emailTemplateSubject());

			String emailTemplateBody = _locationVariableResolver.resolve(
				emailOTPConfiguration.emailTemplateBody());

			return _sendNotificationEmail(
				emailOTPConfiguration.emailTemplateFrom(),
				emailOTPConfiguration.emailTemplateFromName(), email, user,
				emailTemplateSubject, emailTemplateBody, mailTemplateContext);
		}
		catch (Exception e) {
			return false;
		}
	}

	protected EmailOTPConfiguration getEmailOTPConfiguration(
		ResourceRequest request) {

		String mfaVerifierName = ParamUtil.getString(
			request, "mfaVerifierName");

		MFAVerifier mfaVerifier = _mfaRegistry.getMFAVerifier(mfaVerifierName);

		if (mfaVerifier == null) {
			_log.error("Unable to find MFAVerifier " + mfaVerifierName);

			return null;
		}

		if (!(mfaVerifier instanceof EmailOTPMFAVerifier)) {
			_log.error(
				StringBundler.concat(
					"MFAVerifier", mfaVerifierName,
					" is not EmailOTPMFAVerifier!"));

			return null;
		}

		EmailOTPMFAVerifier emailOTPMFAVerifier =
			(EmailOTPMFAVerifier)mfaVerifier;

		return emailOTPMFAVerifier.getEmailOTPConfiguration();
	}

	private boolean _sendNotificationEmail(
			String fromAddress, String fromName, String toAddress, User toUser,
			String subject, String body,
			MailTemplateContext mailTemplateContext)
		throws IOException, PortalException {

		MailTemplate subjectTemplate =
			MailTemplateFactoryUtil.createMailTemplate(subject, false);

		MailTemplate bodyTemplate = MailTemplateFactoryUtil.createMailTemplate(
			body, true);

		MailMessage mailMessage = new MailMessage(
			new InternetAddress(fromAddress, fromName),
			new InternetAddress(toAddress, toUser.getFullName()),
			subjectTemplate.renderAsString(
				toUser.getLocale(), mailTemplateContext),
			bodyTemplate.renderAsString(
				toUser.getLocale(), mailTemplateContext),
			true);

		Company company = _companyLocalService.getCompany(
			toUser.getCompanyId());

		mailMessage.setMessageId(
			_portal.getMailId(company.getMx(), "user", toUser.getUserId()));

		_mailService.sendEmail(mailMessage);

		return true;
	}

	private boolean _verifyEmailResendTimedOut(
		EmailOTPConfiguration emailOTPConfiguration, HttpSession session) {

		Object otpSetAtObj = session.getAttribute("otpSetAt");

		if (otpSetAtObj == null) {
			return true;
		}

		long otpSetAt = (Long)otpSetAtObj;

		long timedOut =
			otpSetAt + emailOTPConfiguration.resendEmailTimeout() * 1000;

		if (System.currentTimeMillis() > timedOut) {
			return true;
		}

		return false;
	}

	private static final Log _log = LogFactoryUtil.getLog(
		SendEmailOTPMVCResourceCommand.class);

	@Reference
	private AuthToken _authToken;

	@Reference
	private CompanyLocalService _companyLocalService;

	@Reference
	private ConfigurationProvider _configurationProvider;

	@Reference
	private EmailOTPEntryLocalService _emailOTPEntryLocalService;

	private final LocationVariableResolver _locationVariableResolver =
		new LocationVariableResolver(
			new ClassLoaderResourceManager(
				SendEmailOTPMVCResourceCommand.class.getClassLoader()),
			(SettingsLocatorHelper)null);

	@Reference
	private MailService _mailService;

	@Reference
	private MFARegistry _mfaRegistry;

	@Reference
	private Portal _portal;

	@Reference
	private UserLocalService _userLocalService;

}