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

import java.io.IOException;

import javax.portlet.PortletException;
import javax.portlet.RenderRequest;
import javax.portlet.RenderResponse;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
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
	public String render(RenderRequest renderReq, RenderResponse renderResp)
		throws PortletException {

		includeLocalResource(
			"/html/portlet/login/open_id.jsp",
			PortalUtil.getHttpServletRequest(renderReq),
			PortalUtil.getHttpServletResponse(renderResp));

		return "/html/portlet/login/navigation.jsp";
	}

	@Reference(
			target = "(osgi.web.symbolicname=com.liferay.portal.security.sso.openid)"
		)
	protected void setServletContext(ServletContext servletContext) {
		_servletContext = servletContext;
	}

	private RequestDispatcher getRequestDispatcher(String path) {
		return _servletContext.getRequestDispatcher(path);
	}

	private void includeLocalResource(
		String path, HttpServletRequest servletReq,
		HttpServletResponse servletResp) {

		try {
			getRequestDispatcher(path).include(servletReq, servletResp);
		}
		catch (ServletException | IOException e) {
			e.printStackTrace();
		}
	}

	private ServletContext _servletContext;

}