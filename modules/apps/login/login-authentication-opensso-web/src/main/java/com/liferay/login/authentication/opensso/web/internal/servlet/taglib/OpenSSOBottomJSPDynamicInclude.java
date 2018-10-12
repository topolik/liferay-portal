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

package com.liferay.login.authentication.opensso.web.internal.servlet.taglib;

import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.model.Layout;
import com.liferay.portal.kernel.module.configuration.ConfigurationProvider;
import com.liferay.portal.kernel.portlet.LiferayPortletURL;
import com.liferay.portal.kernel.portlet.LiferayWindowState;
import com.liferay.portal.kernel.portlet.PortletURLFactoryUtil;
import com.liferay.portal.kernel.security.sso.OpenSSO;
import com.liferay.portal.kernel.servlet.taglib.BaseJSPDynamicInclude;
import com.liferay.portal.kernel.servlet.taglib.DynamicInclude;
import com.liferay.portal.kernel.settings.CompanyServiceSettingsLocator;
import com.liferay.portal.kernel.util.Portal;
import com.liferay.portal.kernel.util.PortletKeys;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.portal.security.sso.opensso.configuration.OpenSSOConfiguration;
import com.liferay.portal.security.sso.opensso.constants.OpenSSOConstants;

import java.io.IOException;

import javax.portlet.PortletRequest;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Stian Sigvartsen
 */
@Component(immediate = true, service = DynamicInclude.class)
public class OpenSSOBottomJSPDynamicInclude extends BaseJSPDynamicInclude {

	@Override
	public void include(
			HttpServletRequest request, HttpServletResponse response,
			String key)
		throws IOException {

		long companyId = _portal.getCompanyId(request);

		try {
			OpenSSOConfiguration openSSOConfiguration = getOpenSSOConfiguration(
				companyId);

			if (!openSSOConfiguration.enabled()) {
				return;
			}
		}
		catch (Exception e) {
			_log.error(e, e);

			return;
		}

		HttpServletRequest originalHttpServletRequest =
			_portal.getOriginalServletRequest(request);

		String error = (String)originalHttpServletRequest.getAttribute(
			"open.sso.error");

		if (Validator.isBlank(error)) {
			return;
		}

		try {
			request.setAttribute("open.sso.error.link", getErrorURL(request));
		}
		catch (Exception e) {
			_log.error(e, e);

			return;
		}

		super.include(request, response, key);
	}

	@Override
	public void register(
		DynamicInclude.DynamicIncludeRegistry dynamicIncludeRegistry) {

		dynamicIncludeRegistry.register("/html/common/themes/bottom.jsp#post");
	}

	protected String getErrorURL(HttpServletRequest request) throws Exception {
		Layout layout = (Layout)request.getAttribute(WebKeys.LAYOUT);

		LiferayPortletURL liferayPortletURL = PortletURLFactoryUtil.create(
			request, PortletKeys.LOGIN, layout, PortletRequest.RENDER_PHASE);

		liferayPortletURL.setWindowState(LiferayWindowState.MAXIMIZED);

		liferayPortletURL.setParameter("mvcRenderCommandName", "/login/login");
		liferayPortletURL.setParameter(
			"redirect", (String)request.getAttribute(WebKeys.CURRENT_URL));

		return liferayPortletURL.toString();
	}

	@Override
	protected String getJspPath() {
		return "/dynamic_include/com.liferay.portal/opensso_error.jsp";
	}

	@Override
	protected Log getLog() {
		return _log;
	}

	protected OpenSSOConfiguration getOpenSSOConfiguration(long companyId)
		throws Exception {

		return _configurationProvider.getConfiguration(
			OpenSSOConfiguration.class,
			new CompanyServiceSettingsLocator(
				companyId, OpenSSOConstants.SERVICE_NAME));
	}

	@Reference(
		target = "(osgi.web.symbolicname=com.liferay.login.authentication.opensso.web)",
		unbind = "-"
	)
	protected void setServletContext(ServletContext servletContext) {
		super.setServletContext(servletContext);
	}

	private static final Log _log = LogFactoryUtil.getLog(
		OpenSSOBottomJSPDynamicInclude.class);

	@Reference
	private ConfigurationProvider _configurationProvider;

	@Reference
	private OpenSSO _openSSO;

	@Reference
	private Portal _portal;

}