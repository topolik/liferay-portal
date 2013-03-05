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

package com.liferay.portlet;

import com.liferay.portal.kernel.portlet.PortletLayoutListener;
import com.liferay.portal.kernel.portlet.RuntimePortlet;
import com.liferay.portal.model.Portlet;
import com.liferay.portal.service.PortletLocalServiceUtil;
import com.liferay.portal.theme.ThemeDisplay;

import javax.portlet.PortletPreferences;

import javax.servlet.http.HttpServletRequest;

/**
 * @author Raymond Augé
 */
public class RuntimePortletImpl implements RuntimePortlet {

	public Portlet getPortlet(
			HttpServletRequest request, ThemeDisplay themeDisplay,
			String portletId, String defaultPreferences)
		throws Exception {

		Portlet portlet = PortletLocalServiceUtil.getPortletById(
			themeDisplay.getCompanyId(), portletId);

		// See LayoutTypePortletImpl#getStaticPortlets for why we only clone
		// non-instanceable portlets

		PortletPreferences portletSetup =
			PortletPreferencesFactoryUtil.fetchPortletSetup(
				request, portletId);

		if (portletSetup == null) {
			PortletPreferencesFactoryUtil.addPortletSetup(
				request, portletId, defaultPreferences);

			PortletLayoutListener portletLayoutListener =
				portlet.getPortletLayoutListenerInstance();

			if (portletLayoutListener != null) {
				portletLayoutListener.onAddToLayout(
					portletId, themeDisplay.getPlid());
			}
		}

		if (!portlet.isInstanceable()) {
			portlet = (Portlet)portlet.clone();
		}

		portlet.setStatic(true);

		return portlet;
	}

}