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

package com.liferay.portal.security.sso.facebook.connect.portlet.action;

import java.io.IOException;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

import javax.portlet.PortletMode;
import javax.portlet.PortletRequest;
import javax.portlet.PortletURL;
import javax.portlet.ResourceRequest;
import javax.servlet.Servlet;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

import com.liferay.portal.kernel.facebook.FacebookConnect;
import com.liferay.portal.kernel.json.JSONObject;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.portlet.LiferayWindowState;
import com.liferay.portal.kernel.util.CalendarFactoryUtil;
import com.liferay.portal.kernel.util.CookieKeys;
import com.liferay.portal.kernel.util.LocaleUtil;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.kernel.util.StringPool;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.portal.kernel.workflow.WorkflowConstants;
import com.liferay.portal.model.Contact;
import com.liferay.portal.model.User;
import com.liferay.portal.model.UserGroupRole;
import com.liferay.portal.security.auth.PrincipalException;
import com.liferay.portal.security.sso.facebook.connect.constants.FacebookConnectWebKeys;
import com.liferay.portal.service.ServiceContext;
import com.liferay.portal.service.UserLocalService;
import com.liferay.portal.theme.ThemeDisplay;
import com.liferay.portal.util.PortalUtil;
import com.liferay.portal.util.PortletKeys;
import com.liferay.portlet.PortletURLFactoryUtil;

/**
 * @author Stian Sigvartsen
 */
@Component(
	immediate = true,
	property = {
		"osgi.http.whiteboard.context.path=/login/facebook_connect_oauth",
		"osgi.http.whiteboard.servlet.name=Facebook Connect Servlet",
		"osgi.http.whiteboard.servlet.pattern=/login/facebook_connect_oauth/*"
	},
	service = Servlet.class
)
public class FacebookConnectRedirectURIServlet extends HttpServlet {

	protected User addUser(
			HttpSession session, long companyId, JSONObject jsonObject)
		throws Exception {

		long creatorUserId = 0;
		boolean autoPassword = true;
		String password1 = StringPool.BLANK;
		String password2 = StringPool.BLANK;
		boolean autoScreenName = true;
		String screenName = StringPool.BLANK;
		String emailAddress = jsonObject.getString("email");
		long facebookId = jsonObject.getLong("id");
		String openId = StringPool.BLANK;
		Locale locale = LocaleUtil.getDefault();
		String firstName = jsonObject.getString("first_name");
		String middleName = StringPool.BLANK;
		String lastName = jsonObject.getString("last_name");
		long prefixId = 0;
		long suffixId = 0;
		boolean male = Validator.equals(jsonObject.getString("gender"), "male");
		int birthdayMonth = Calendar.JANUARY;
		int birthdayDay = 1;
		int birthdayYear = 1970;
		String jobTitle = StringPool.BLANK;
		long[] groupIds = null;
		long[] organizationIds = null;
		long[] roleIds = null;
		long[] userGroupIds = null;
		boolean sendEmail = true;

		ServiceContext serviceContext = new ServiceContext();

		User user = _userLocalService.addUser(
			creatorUserId, companyId, autoPassword, password1, password2,
			autoScreenName, screenName, emailAddress, facebookId, openId,
			locale, firstName, middleName, lastName, prefixId, suffixId, male,
			birthdayMonth, birthdayDay, birthdayYear, jobTitle, groupIds,
			organizationIds, roleIds, userGroupIds, sendEmail, serviceContext);

		user = _userLocalService.updateLastLogin(
			user.getUserId(), user.getLoginIP());

		user = _userLocalService.updatePasswordReset(user.getUserId(), false);

		user = _userLocalService.updateEmailAddressVerified(
			user.getUserId(), true);

		session.setAttribute(WebKeys.FACEBOOK_USER_EMAIL_ADDRESS, emailAddress);

		return user;
	}

	@Override
	protected void doGet(
			HttpServletRequest request, HttpServletResponse response)
		throws IOException, ServletException {

		request = getOriginalServletRequest(request);

		long companyId = PortalUtil.getCompanyId(request);

		if (!_facebookConnect.isEnabled(companyId)) {
			throw new ServletException(
				new PrincipalException.MustBeEnabled(
					companyId, FacebookConnect.class.getName()));
		}

		HttpSession session = request.getSession();
		
		String sessionCSRFToken = (String)getCurrentCSRFToken(request);
		String requestCSRFToken = request.getParameter("state");
		
		// Consume the CSRF token so it cannot be used multiple times
		request.getSession().removeAttribute(FacebookConnectWebKeys.FACEBOOK_CSRF_TOKEN);
		
		if (Validator.isNull(sessionCSRFToken) || Validator.isNull(requestCSRFToken) || !sessionCSRFToken.equals(requestCSRFToken)) {	
			return;
		}
		

		String redirect = ParamUtil.getString(request, "postAuthRedirect");

		String code = ParamUtil.getString(request, "code");

		if (Validator.isNull(redirect) || Validator.isNull(code)) {
			response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
			return;
		}

		String token = _facebookConnect.getAccessToken(
			companyId, redirect, code);

		if (Validator.isNotNull(token)) {
			try {
				User user = setFacebookCredentials(session, companyId, token);
			}
			catch (Exception e) {
				throw new ServletException(e);
			}
		}
		else {
			response.sendRedirect(getRefererURL(request, null));
			return;
		}

		response.sendRedirect(PortalUtil.escapeRedirect(redirect));
	}

	@Reference(unbind = "-")
	protected void setFacebookConnect(FacebookConnect facebookConnect) {
		_facebookConnect = facebookConnect;
	}

	protected User setFacebookCredentials(
			HttpSession session, long companyId, String token)
		throws Exception {

		JSONObject jsonObject = _facebookConnect.getGraphResources(
			companyId, "/me", token,
			"id,email,first_name,last_name,gender");

		if ((jsonObject == null) ||
			(jsonObject.getJSONObject("error") != null)) {

			return null;
		}

		if (_facebookConnect.isVerifiedAccountRequired(companyId) &&
			!jsonObject.getBoolean("verified")) {

			return null;
		}

		User user = null;

		long facebookId = jsonObject.getLong("id");

		if (facebookId > 0) {
			session.setAttribute(
				FacebookConnectWebKeys.FACEBOOK_ACCESS_TOKEN, token);

			user = _userLocalService.fetchUserByFacebookId(
				companyId, facebookId);

			if ((user != null) &&
				(user.getStatus() != WorkflowConstants.STATUS_INCOMPLETE)) {

				session.setAttribute(
					FacebookConnectWebKeys.FACEBOOK_USER_ID,
					String.valueOf(facebookId));
			}
		}

		String emailAddress = jsonObject.getString("email");

		if ((user == null) && Validator.isNotNull(emailAddress)) {
			user = _userLocalService.fetchUserByEmailAddress(
				companyId, emailAddress);

			if ((user != null) &&
				(user.getStatus() != WorkflowConstants.STATUS_INCOMPLETE)) {

				session.setAttribute(
					WebKeys.FACEBOOK_USER_EMAIL_ADDRESS, emailAddress);
			}
		}

		if (user != null) {
			if (user.getStatus() == WorkflowConstants.STATUS_INCOMPLETE) {
				session.setAttribute(
					WebKeys.FACEBOOK_INCOMPLETE_USER_ID, facebookId);
				
				session.setAttribute(
						FacebookConnectWebKeys.FACEBOOK_INCOMPLETE_MATCHED_USER_ID, user.getUserId());

				user.setEmailAddress(jsonObject.getString("email"));
				user.setFirstName(jsonObject.getString("first_name"));
				user.setLastName(jsonObject.getString("last_name"));

				return user;
			}

			user = updateUser(user, jsonObject);
		}
		else {
			user = addUser(session, companyId, jsonObject);
		}

		return user;
	}

	@Reference(unbind = "-")
	protected void setUserLocalService(UserLocalService userLocalService) {
		_userLocalService = userLocalService;
	}

	protected User updateUser(User user, JSONObject jsonObject)
		throws Exception {

		long facebookId = jsonObject.getLong("id");
		String emailAddress = jsonObject.getString("email");
		String firstName = jsonObject.getString("first_name");
		String lastName = jsonObject.getString("last_name");
		boolean male = Validator.equals(jsonObject.getString("gender"), "male");

		if ((facebookId == user.getFacebookId()) &&
			emailAddress.equals(user.getEmailAddress()) &&
			firstName.equals(user.getFirstName()) &&
			lastName.equals(user.getLastName()) && (male == user.isMale())) {

			return user;
		}

		Contact contact = user.getContact();

		Calendar birthdayCal = CalendarFactoryUtil.getCalendar();

		birthdayCal.setTime(contact.getBirthday());

		int birthdayMonth = birthdayCal.get(Calendar.MONTH);
		int birthdayDay = birthdayCal.get(Calendar.DAY_OF_MONTH);
		int birthdayYear = birthdayCal.get(Calendar.YEAR);

		long[] groupIds = null;
		long[] organizationIds = null;
		long[] roleIds = null;
		List<UserGroupRole> userGroupRoles = null;
		long[] userGroupIds = null;

		ServiceContext serviceContext = new ServiceContext();

		if (!StringUtil.equalsIgnoreCase(
				emailAddress, user.getEmailAddress())) {

			_userLocalService.updateEmailAddress(
				user.getUserId(), StringPool.BLANK, emailAddress, emailAddress);
		}

		_userLocalService.updateEmailAddressVerified(user.getUserId(), true);

		return _userLocalService.updateUser(
			user.getUserId(), StringPool.BLANK, StringPool.BLANK,
			StringPool.BLANK, false, user.getReminderQueryQuestion(),
			user.getReminderQueryAnswer(), user.getScreenName(), emailAddress,
			facebookId, user.getOpenId(), true, null, user.getLanguageId(),
			user.getTimeZoneId(), user.getGreeting(), user.getComments(),
			firstName, user.getMiddleName(), lastName, contact.getPrefixId(),
			contact.getSuffixId(), male, birthdayMonth, birthdayDay,
			birthdayYear, contact.getSmsSn(), contact.getFacebookSn(),
			contact.getJabberSn(), contact.getSkypeSn(), contact.getTwitterSn(),
			contact.getJobTitle(), groupIds, organizationIds, roleIds,
			userGroupRoles, userGroupIds, serviceContext);
	}

	private String getRefererURL(
		HttpServletRequest request, ThemeDisplay themeDisplay) {

		String referer = null;
		String refererParam = PortalUtil.escapeRedirect(
			request.getParameter(WebKeys.REFERER));
		String refererRequest = (String)request.getAttribute(WebKeys.REFERER);

		String refererSession = null;

		HttpSession session = request.getSession(false);

		if (session != null) {
			refererSession = (String)session.getAttribute(WebKeys.REFERER);
		}

		if (Validator.isNotNull(refererParam)) {
			referer = refererParam;
		}
		else if (Validator.isNotNull(refererRequest)) {
			referer = refererRequest;
		}
		else if (Validator.isNotNull(refererSession)) {
			referer = refererSession;
		}
		else if (themeDisplay != null) {
			referer = themeDisplay.getPathMain();
		}
		else {
			referer = PortalUtil.getPathMain();
		}

		if ((session != null) && !CookieKeys.hasSessionId(request) &&
			Validator.isNotNull(referer)) {

			referer = PortalUtil.getURLWithSessionId(referer, session.getId());
		}

		return referer;
	}

	private HttpServletRequest getOriginalServletRequest(HttpServletRequest request) {
		return PortalUtil.getOriginalServletRequest(request);
	}
	
	public String getCurrentCSRFToken(HttpServletRequest request) {
		return (String)getOriginalServletRequest(request).getSession().getAttribute(FacebookConnectWebKeys.FACEBOOK_CSRF_TOKEN);
	}
	
	private static final Log _log = LogFactoryUtil.getLog(
		FacebookConnectRedirectURIServlet.class);

	private static final long serialVersionUID = 1L;

	private FacebookConnect _facebookConnect;
	private UserLocalService _userLocalService;

}