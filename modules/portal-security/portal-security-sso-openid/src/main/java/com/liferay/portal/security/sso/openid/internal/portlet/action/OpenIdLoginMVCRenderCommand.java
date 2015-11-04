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

package com.liferay.portal.security.sso.openid.internal.portlet.action;

import com.liferay.portal.kernel.portlet.bridges.mvc.MVCRenderCommand;
import com.liferay.portal.util.PortalUtil;
import com.liferay.portal.util.PortletKeys;

import javax.portlet.PortletException;
import javax.portlet.RenderRequest;
import javax.portlet.RenderResponse;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Stian Sigvartsen
 */
@Component(
		immediate = true,
		property = {
			"javax.portlet.name=" + PortletKeys.FAST_LOGIN,
			"javax.portlet.name=" + PortletKeys.LOGIN,
			"mvc.command.name=/login/openid"
		},
		service = MVCRenderCommand.class
	)
public class OpenIdLoginMVCRenderCommand implements MVCRenderCommand {

	@Override
	public String render(
			RenderRequest renderRequest, RenderResponse renderResponse)
		throws PortletException {

		HttpServletRequest httpServletRequest =
			PortalUtil.getHttpServletRequest(renderRequest);

		HttpServletResponse httpServletResponse =
			PortalUtil.getHttpServletResponse(renderResponse);

		RequestDispatcher requestDispatcher =
			_servletContext.getRequestDispatcher(_JSP_OPENID_LOGIN_PATH);

		try {
			requestDispatcher.include(httpServletRequest, httpServletResponse);
		}
		catch (Exception e) {
			throw new PortletException(
				"Unable to include " + _JSP_OPENID_LOGIN_PATH, e);
		}

		return _JSP_OPENID_NAVIGATION_PATH;
	}

	@Reference(
			target = "(osgi.web.symbolicname=com.liferay.portal.security.sso.openid)"
		)
	protected void setServletContext(ServletContext servletContext) {
		_servletContext = servletContext;
	}

	private static final String _JSP_OPENID_LOGIN_PATH =
		"/html/portlet/login/open_id.jsp";

	private static final String _JSP_OPENID_NAVIGATION_PATH =
		"/html/portlet/login/navigation.jsp";

	private ServletContext _servletContext;

}