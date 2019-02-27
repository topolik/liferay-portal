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

package com.liferay.multi.factor.authentication.portlet.web.internal;

import com.liferay.multi.factor.authentication.portlet.api.MFAPortletURLFactory;
import com.liferay.multi.factor.authentication.portlet.api.constants.MFAPortletKeys;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.portlet.LiferayPortletURL;
import com.liferay.portal.kernel.portlet.PortletURLFactory;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.Portal;
import com.liferay.portal.kernel.util.WebKeys;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

import javax.portlet.PortletRequest;
import javax.portlet.WindowState;
import javax.portlet.WindowStateException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

/**
 * @author Tomas Polesovsky
 */
@Component(immediate = true, service = MFAPortletURLFactory.class)
public class MFAPortletURLFactoryImpl implements MFAPortletURLFactory {

	@Override
	public LiferayPortletURL createVerifyURL(
		HttpServletRequest request, String integrationName,
		String redirectURL, long userId) {

		request = _portal.getOriginalServletRequest(request);

		HttpSession session = request.getSession();

		session.setAttribute(MFA_USER_ID + integrationName, userId);

		long plid = 0;

		ThemeDisplay themeDisplay =
			(ThemeDisplay) request.getAttribute(WebKeys.THEME_DISPLAY);

		if (themeDisplay != null) {
			plid = themeDisplay.getPlid();
		}

		LiferayPortletURL liferayPortletURL =
			_portletURLFactory.create(
				request, MFAPortletKeys.MFA_VERIFY, plid,
				PortletRequest.RENDER_PHASE);

		liferayPortletURL.setParameter("integrationName", integrationName);
		liferayPortletURL.setParameter(
			"saveLastPath", Boolean.FALSE.toString());
		liferayPortletURL.setParameter(
			"mvcRenderCommandName", "/mfa_verify/verify");
		liferayPortletURL.setParameter("redirect", redirectURL);

		try {
			liferayPortletURL.setWindowState(WindowState.MAXIMIZED);
		}
		catch (WindowStateException e) {
			if (_log.isDebugEnabled()) {
				_log.debug(e, e);
			}
		}

		return liferayPortletURL;
	}

	@Override
	public LiferayPortletURL createSetupURL(
		HttpServletRequest request, String integrationName,
		String redirectURL) {

		LiferayPortletURL liferayPortletURL =
			_portletURLFactory.create(
				request, MFAPortletKeys.MFA_VERIFY,
				PortletRequest.RENDER_PHASE);

		liferayPortletURL.setParameter("integrationName", integrationName);
		liferayPortletURL.setParameter(
			"mvcRenderCommandName", "/mfa_verify/setup");
		liferayPortletURL.setParameter("redirect", redirectURL);

		try {
			liferayPortletURL.setWindowState(WindowState.MAXIMIZED);
		}
		catch (WindowStateException e) {
			if (_log.isDebugEnabled()) {
				_log.debug(e, e);
			}
		}

		return liferayPortletURL;
	}

	private static final Log _log = LogFactoryUtil.getLog(
		MFAPortletURLFactoryImpl.class);

	@Reference
	private PortletURLFactory _portletURLFactory;

	@Reference
	private Portal _portal;

}
