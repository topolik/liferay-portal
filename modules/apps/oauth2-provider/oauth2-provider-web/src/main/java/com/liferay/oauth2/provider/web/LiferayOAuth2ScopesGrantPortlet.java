/**
 * Copyright (c) 2000-present Liferay, Inc. All rights reserved.
 * <p>
 * This library is free software; you can redistribute it and/or modify it under
 * the terms of the GNU Lesser General Public License as published by the Free
 * Software Foundation; either version 2.1 of the License, or (at your option)
 * any later version.
 * <p>
 * This library is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more
 * details.
 */

package com.liferay.oauth2.provider.web;

import com.liferay.oauth2.provider.scopes.liferay.api.ScopeFinderLocator;
import com.liferay.portal.kernel.portlet.bridges.mvc.MVCPortlet;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.kernel.util.PortalUtil;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

import javax.portlet.Portlet;
import javax.portlet.PortletException;
import javax.portlet.RenderRequest;
import javax.portlet.RenderResponse;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintWriter;

@Component(
	immediate = true,
	property = {
		"com.liferay.portlet.application-type=full-page-application",
		"javax.portlet.name=com.liferay.oauth2.provider.web.LiferayOAuth2ScopesGrantPortlet",
		"javax.portlet.display-name=OAuth2 Grant Scopes Portlet"

	},
	service = Portlet.class
)
public class LiferayOAuth2ScopesGrantPortlet extends MVCPortlet {

	@Override
	public void doView(
			RenderRequest renderRequest, RenderResponse renderResponse)
		throws IOException, PortletException {

		OutputStream portletOutputStream =
			renderResponse.getPortletOutputStream();

		HttpServletRequest httpServletRequest =
			PortalUtil.getHttpServletRequest(renderRequest);

		httpServletRequest = PortalUtil.getOriginalServletRequest(
			httpServletRequest);

		PrintWriter printWriter = new PrintWriter(portletOutputStream, true);

		printWriter.append(
			"<form method=\"GET\" " +
				"enctype=\"application/x-www-form-urlencoded\" action=\"");
		printWriter.append(
			ParamUtil.get(httpServletRequest, "reply_to", ""));
		printWriter.append("\" >");
		printWriter.append(
			"<input type=\"hidden\" name=\"client_id\" value=\"");
		printWriter.append(
			ParamUtil.get(httpServletRequest, "client_id", ""));
		printWriter.append("\">");
		printWriter.append(
			"<input type=\"hidden\" name=\"redirect_uri\" value=\"");
		printWriter.append(
			ParamUtil.get(httpServletRequest, "redirect_uri", ""));
		printWriter.append("\">");
		printWriter.append(
			"<input type=\"hidden\" name=\"scope\" value=\"");
		printWriter.append(ParamUtil.get(httpServletRequest, "scope", ""));

		/*
			show scopes from scope finder, but filtered against available scopes that are assigned to application

			example: application requests everything, oauth2 admin selected blogs.everything for this app
				=> show blogs.everything description

			In this version we approve all scopes requested, don't further sub-select
		*/

		printWriter.append("\">");
		printWriter.append(
			"<input type=\"hidden\" name=\"session_authenticity_token\" " +
			"value=\"");
		printWriter.append(
			ParamUtil.get(httpServletRequest, "session_authenticity_token", ""));
		printWriter.append("\">");
		printWriter.append(
			"<button type=\"submit\" name=\"oauthDecision\" value=\"allow\">" +
				"Allow</button>");
		printWriter.append(
			"<button type=\"submit\" name=\"oauthDecision\" value=\"deny\">" +
				"Deny</button>");
		printWriter.append("</form>");

		printWriter.println("Hello world!<br>");

		printWriter.flush();
	}

	@Reference
	private ScopeFinderLocator _scopeFinderLocator;
}
