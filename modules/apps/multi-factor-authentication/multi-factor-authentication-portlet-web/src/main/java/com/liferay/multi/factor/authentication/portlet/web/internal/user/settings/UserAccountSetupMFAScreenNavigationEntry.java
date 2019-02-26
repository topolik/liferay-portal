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

package com.liferay.multi.factor.authentication.portlet.web.internal.user.settings;

import com.liferay.frontend.taglib.servlet.taglib.ScreenNavigationEntry;
import com.liferay.multi.factor.authentication.portlet.api.constants.MFAPortletKeys;
import com.liferay.multi.factor.authentication.spi.verifier.UserAccountSetupMFAVerifier;
import com.liferay.portal.kernel.language.LanguageUtil;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.util.ResourceBundleUtil;
import com.liferay.users.admin.constants.UserFormConstants;
import org.osgi.service.component.annotations.Reference;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Locale;
import java.util.ResourceBundle;

/**
 * @author Marta Medio
 **/
public class UserAccountSetupMFAScreenNavigationEntry
	implements ScreenNavigationEntry<User> {

	private final UserAccountSetupMFAVerifier _userAccountSetupMFAVerifier;

	public UserAccountSetupMFAScreenNavigationEntry(
		UserAccountSetupMFAVerifier userAccountSetupMFAVerifier) {
		_userAccountSetupMFAVerifier = userAccountSetupMFAVerifier;
	}

	@Override
	public String getCategoryKey() {
		return MFAPortletKeys.CATEGORY_KEY_MFA;
	}

	@Override
	public String getEntryKey() {
		return _userAccountSetupMFAVerifier.getProviderName();
	}

	@Override
	public String getLabel(Locale locale) {
		ResourceBundle resourceBundle = ResourceBundleUtil.getBundle(
			"content.Language", locale, getClass());

		return LanguageUtil.get(resourceBundle, getEntryKey());
	}

	@Override
	public String getScreenNavigationKey() {
		return UserFormConstants.SCREEN_NAVIGATION_KEY_USERS;
	}

	@Override
	public void render(HttpServletRequest request, HttpServletResponse response)
		throws IOException {

		request.setAttribute(UserAccountSetupMFAVerifier.class.getName(), _userAccountSetupMFAVerifier);
		request.setAttribute("label", getLabel(request.getLocale()));
		request.setAttribute("screenNavigationCategoryKey", getCategoryKey());
		request.setAttribute("screenNavigationEntryKey", getEntryKey());


		RequestDispatcher requestDispatcher =
			_servletContext.getRequestDispatcher("/user_account_setup.jsp");

		try {
			requestDispatcher.include(request, response);
		}
		catch (ServletException se) {
			_log.error("Unable to render JSP " + "/user_account_setup.jsp", se);

			throw new IOException("Unable to render " + "/user_account_setup.jsp", se);
		}

	}

	public void setServletContext(ServletContext servletContext) {
		_servletContext = servletContext;
	}

	private ServletContext _servletContext;

	private static Log _log = LogFactoryUtil.getLog(
		UserAccountSetupMFAScreenNavigationEntry.class);

}