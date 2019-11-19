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

package com.liferay.multi.factor.authentication.checker.email.otp.web.internal.action;

import com.liferay.mail.kernel.model.MailMessage;
import com.liferay.mail.kernel.service.MailService;
import com.liferay.mail.kernel.template.MailTemplate;
import com.liferay.mail.kernel.template.MailTemplateContext;
import com.liferay.mail.kernel.template.MailTemplateContextBuilder;
import com.liferay.mail.kernel.template.MailTemplateFactoryUtil;
import com.liferay.multi.factor.authentication.checker.email.otp.web.internal.checker.EmailOTPMFAChecker;
import com.liferay.multi.factor.authentication.checker.email.otp.web.internal.configuration.EmailOTPConfiguration;
import com.liferay.multi.factor.authentication.checker.email.otp.web.internal.constants.MFAPortletKeys;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.model.Company;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.module.configuration.ConfigurationProvider;
import com.liferay.portal.kernel.module.configuration.ConfigurationProviderUtil;
import com.liferay.portal.kernel.portlet.bridges.mvc.MVCResourceCommand;
import com.liferay.portal.kernel.security.auth.AuthToken;
import com.liferay.portal.kernel.security.auth.PrincipalException;
import com.liferay.portal.kernel.service.CompanyLocalService;
import com.liferay.portal.kernel.service.UserLocalService;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.HtmlUtil;
import com.liferay.portal.kernel.util.Portal;
import com.liferay.portal.kernel.util.PwdGenerator;
import com.liferay.portal.kernel.util.StringBundler;

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
 * @author Arthur Chan
 */
@Component(
	property = {
		"javax.portlet.name=" + MFAPortletKeys.MFA_SETUP_PORTLET,
		"javax.portlet.name=" + MFAPortletKeys.MFA_VERIFY_PORTLET,
		"mvc.command.name=/mfa/sendemailotp"
	},
	service = MVCResourceCommand.class
)
public class SendEmailOTPMVCResourceCommand implements MVCResourceCommand {

	@Override
	public boolean serveResource(
			ResourceRequest resourceRequest, ResourceResponse resourceResponse)
		throws PortletException {

		HttpServletRequest httpServletRequest =
			_portal.getOriginalServletRequest(
				_portal.getHttpServletRequest(resourceRequest));

		try {
			_authToken.checkCSRFToken(
				httpServletRequest,
				SendEmailOTPMVCResourceCommand.class.getName());
		}
		catch (PrincipalException pe) {
			throw new PortletException(pe);
		}

		HttpSession session = httpServletRequest.getSession();

		try {
			User user = _userLocalService.getUserById(
				GetterUtil.getLong(session.getAttribute("userId")));

			EmailOTPConfiguration emailOTPConfiguration =
				ConfigurationProviderUtil.getCompanyConfiguration(
					EmailOTPConfiguration.class, user.getCompanyId());

			if (emailOTPConfiguration == null) {
				return false;
			}

			if (!_isResendTimedOut(emailOTPConfiguration, session)) {
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
				"[$PORTAL_URL$]", _portal.getPortalURL(httpServletRequest));
			mailTemplateContextBuilder.put(
				"[$REMOTE_ADDRESS$]", httpServletRequest.getRemoteAddr());
			mailTemplateContextBuilder.put(
				"[$REMOTE_HOST$]",
				HtmlUtil.escape(httpServletRequest.getRemoteHost()));
			mailTemplateContextBuilder.put(
				"[$TO_NAME$]", HtmlUtil.escape(user.getFullName()));

			MailTemplateContext mailTemplateContext =
				mailTemplateContextBuilder.build();

			String emailTemplateSubject = "";

			//	emailOTPConfiguration.emailTemplateSubject();

			String emailTemplateBody = "";

			// emailOTPConfiguration.emailTemplateBody();

			return _sendNotificationEmail(
				emailOTPConfiguration.emailTemplateFrom(),
				emailOTPConfiguration.emailTemplateFromName(),
				user.getEmailAddress(), user, emailTemplateSubject,
				emailTemplateBody, mailTemplateContext);
		}
		catch (Exception e) {
			throw new PortletException(e);
		}
	}

	private boolean _isResendTimedOut(
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

		if (_log.isDebugEnabled()) {
			_log.debug(
				StringBundler.concat(
					"OTP email sent to user id ",
					String.valueOf(toUser.getUserId()), " to address ",
					toAddress));
		}

		return true;
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
	private EmailOTPMFAChecker _emailOTPMFAChecker;

	@Reference
	private MailService _mailService;

	@Reference
	private Portal _portal;

	@Reference
	private UserLocalService _userLocalService;

}