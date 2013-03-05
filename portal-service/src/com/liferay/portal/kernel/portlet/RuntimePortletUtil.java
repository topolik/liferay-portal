/**
 * Copyright (c) 2000-2012 Liferay, Inc. All rights reserved.
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

package com.liferay.portal.kernel.portlet;

import com.liferay.portal.kernel.security.pacl.permission.PortalRuntimePermission;
import com.liferay.portal.model.Portlet;
import com.liferay.portal.theme.ThemeDisplay;

import javax.servlet.http.HttpServletRequest;

/**
 * @author Raymond Augé
 */
public class RuntimePortletUtil {

	public static Portlet getPortlet(
			HttpServletRequest request, ThemeDisplay themeDisplay,
			String portletId, String defaultPreferences)
		throws Exception {

		return getRuntimePortlet().getPortlet(
			request, themeDisplay, portletId, defaultPreferences);
	}

	public static RuntimePortlet getRuntimePortlet() {
		PortalRuntimePermission.checkGetBeanProperty(RuntimePortletUtil.class);

		return _runtimePortlet;
	}

	public void setRuntimePortlet(RuntimePortlet runtimePortlet) {
		PortalRuntimePermission.checkSetBeanProperty(getClass());

		_runtimePortlet = runtimePortlet;
	}

	private static RuntimePortlet _runtimePortlet;

}