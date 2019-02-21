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

package com.liferay.multi.factor.authentication.portlet.api;

import com.liferay.portal.kernel.portlet.LiferayPortletURL;

import javax.servlet.http.HttpServletRequest;

/**
 * @author Tomas Polesovsky
 */
public interface MFAPortletURLFactory {

	public static final String MFA_USER_ID =
		MFAPortletURLFactory.class.getName() + "#MFA_USER_ID";

	public LiferayPortletURL createVerifyURL(
		HttpServletRequest request, String integrationName, String redirectURL, long userId);

	public LiferayPortletURL createSetupURL(
		HttpServletRequest request, String integrationName, String redirectURL);

}
