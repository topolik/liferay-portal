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

package com.liferay.login.authentication.opensso.web.internal.portlet.action;

import com.liferay.portal.kernel.exception.UserEmailAddressException;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.module.configuration.ConfigurationProvider;
import com.liferay.portal.kernel.portlet.bridges.mvc.MVCRenderCommand;
import com.liferay.portal.kernel.portlet.bridges.mvc.MVCRenderConstants;
import com.liferay.portal.kernel.security.auth.PrincipalException;
import com.liferay.portal.kernel.security.sso.OpenSSO;
import com.liferay.portal.kernel.servlet.SessionErrors;
import com.liferay.portal.kernel.servlet.SessionMessages;
import com.liferay.portal.kernel.settings.CompanyServiceSettingsLocator;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.ArrayUtil;
import com.liferay.portal.kernel.util.Portal;
import com.liferay.portal.kernel.util.PortletKeys;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.portal.security.sso.opensso.configuration.OpenSSOConfiguration;
import com.liferay.portal.security.sso.opensso.constants.OpenSSOConstants;

import java.io.IOException;

import javax.portlet.PortletException;
import javax.portlet.RenderRequest;
import javax.portlet.RenderResponse;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Stian Sigvartsen
 */
@Component(
	immediate = true,
	property = {
		"javax.portlet.name=" + PortletKeys.FAST_LOGIN,
		"javax.portlet.name=" + PortletKeys.LOGIN, "mvc.command.name=/",
		"mvc.command.name=/login/login", "service.ranking:Integer=1"
	},
	service = MVCRenderCommand.class
)
public class OpenSSOMVCRenderCommand implements MVCRenderCommand {

	@Override
	public String render(
			RenderRequest renderRequest, RenderResponse renderResponse)
		throws PortletException {

		HttpServletRequest httpServletRequest = _portal.getHttpServletRequest(
			renderRequest);

		ThemeDisplay themeDisplay =
			(ThemeDisplay)httpServletRequest.getAttribute(
				WebKeys.THEME_DISPLAY);

		long companyId = themeDisplay.getCompanyId();

		OpenSSOConfiguration openSSOConfiguration;

		try {
			openSSOConfiguration = getOpenSSOConfiguration(companyId);

			if (!openSSOConfiguration.enabled() ||
				Validator.isNull(openSSOConfiguration.loginURL()) ||
				Validator.isNull(openSSOConfiguration.logoutURL()) |
				Validator.isNull(openSSOConfiguration.serviceURL())) {

				return "/login.jsp";
			}
		}
		catch (Exception e) {
			_log.error(e, e);

			return "/login.jsp";
		}

		HttpServletRequest originalHttpServletRequest =
			_portal.getOriginalServletRequest(httpServletRequest);

		String error = (String)originalHttpServletRequest.getAttribute(
			"open.sso.error");

		if (Validator.isNotNull(error)) {
			originalHttpServletRequest.removeAttribute("open.sso.error");

			if (ArrayUtil.contains(_ERRORS, error)) {
				SessionErrors.add(renderRequest, error);
			}
			else {
				SessionErrors.add(renderRequest, "unknownError");
			}
		}
		else {
			try {
				if (!_openSSO.isAuthenticated(
						originalHttpServletRequest,
						openSSOConfiguration.serviceURL())) {

					SessionErrors.add(
						renderRequest,
						PrincipalException.MustBeAuthenticated.class.
							getSimpleName());
				}
			}
			catch (IOException ioe) {
				throw new PortletException(ioe);
			}
		}

		SessionMessages.add(
			renderRequest,
			_portal.getPortletId(renderRequest) +
				SessionMessages.KEY_SUFFIX_HIDE_DEFAULT_ERROR_MESSAGE);

		RequestDispatcher requestDispatcher =
			_servletContext.getRequestDispatcher(_JSP_PATH);

		try {
			requestDispatcher.include(
				httpServletRequest,
				_portal.getHttpServletResponse(renderResponse));
		}
		catch (Exception e) {
			_log.error("Unable to include JSP " + _JSP_PATH, e);

			throw new PortletException("Unable to include JSP " + _JSP_PATH, e);
		}

		return MVCRenderConstants.MVC_PATH_VALUE_SKIP_DISPATCH;
	}

	protected OpenSSOConfiguration getOpenSSOConfiguration(long companyId)
		throws Exception {

		return _configurationProvider.getConfiguration(
			OpenSSOConfiguration.class,
			new CompanyServiceSettingsLocator(
				companyId, OpenSSOConstants.SERVICE_NAME));
	}

	private static final String[] _ERRORS = {
		PrincipalException.MustBeAuthenticated.class.getSimpleName(),
		"StrangersNotAllowedException",
		UserEmailAddressException.MustNotUseCompanyMx.class.getSimpleName()
	};

	private static final String _JSP_PATH =
		"/com.liferay.login.web/opensso.jsp";

	private static final Log _log = LogFactoryUtil.getLog(
		OpenSSOMVCRenderCommand.class);

	@Reference
	private ConfigurationProvider _configurationProvider;

	@Reference
	private OpenSSO _openSSO;

	@Reference
	private Portal _portal;

	@Reference(
		target = "(osgi.web.symbolicname=com.liferay.login.authentication.opensso.web)"
	)
	private ServletContext _servletContext;

}