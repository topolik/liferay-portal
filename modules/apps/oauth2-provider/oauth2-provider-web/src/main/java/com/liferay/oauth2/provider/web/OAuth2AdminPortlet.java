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

import com.liferay.oauth2.provider.model.OAuth2Application;
import com.liferay.oauth2.provider.service.OAuth2ApplicationLocalService;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.portlet.bridges.mvc.MVCPortlet;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.service.ServiceContextFactory;
import com.liferay.portal.kernel.servlet.SessionErrors;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.kernel.util.StringPool;

import javax.portlet.ActionRequest;
import javax.portlet.ActionResponse;
import javax.portlet.Portlet;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Stian Sigvartsen
 */
@Component(
	immediate = true,
	property = {
		"com.liferay.portlet.display-category=category.hidden",
		"com.liferay.portlet.preferences-owned-by-group=true",
		"com.liferay.portlet.preferences-unique-per-layout=false",
		"javax.portlet.display-name=OAuth2 Admin",
		"javax.portlet.init-param.portlet-title-based-navigation=true",
		"javax.portlet.init-param.template-path=/",
		"javax.portlet.init-param.view-template=/admin/view.jsp",
		"javax.portlet.name=" + OAuth2AdminPortletKeys.OAUTH2_ADMIN,
		"javax.portlet.resource-bundle=content.Language"
	},
	service = Portlet.class
)
public class OAuth2AdminPortlet extends MVCPortlet {

	public void updateOAuth2Application(
			ActionRequest request, ActionResponse response)
		throws PortalException {

		ServiceContext serviceContext = ServiceContextFactory.getInstance(
			OAuth2Application.class.getName(), request);

		long userId = serviceContext.getUserId();

		long oAuth2ApplicationId = ParamUtil.getLong(
			request, "oAuth2ApplicationId", -1);

		boolean clientConfidential = ParamUtil.get(request, "clientConfidential", false);
				
		String oAuth2ClientId = ParamUtil.get(
			request, "clientId", StringPool.BLANK);

		String oAuth2ClientSecret = ParamUtil.get(
			request, "clientSecret", StringPool.BLANK);

		String oAuth2RedirectURI = ParamUtil.get(request, "redirectUri", StringPool.BLANK);
		
		String name = ParamUtil.get(request, "name", StringPool.BLANK);

		String description = ParamUtil.get(
			request, "description", StringPool.BLANK);

		String webURL = ParamUtil.get(request, "webUrl", StringPool.BLANK);
				
		try {
			if (oAuth2ApplicationId <= 0) {
				_oAuth2ApplicationLocalService.addOAuth2Application(
					userId, name, description, webURL, clientConfidential,
					oAuth2ClientId, oAuth2ClientSecret, oAuth2RedirectURI, 
					serviceContext);
			}
			else {
				_oAuth2ApplicationLocalService.updateOAuth2Application(
					userId, oAuth2ApplicationId, name, description, webURL, 
					clientConfidential, oAuth2ClientId, oAuth2ClientSecret, 
					oAuth2RedirectURI, serviceContext);
			}
		}
		catch (PortalException pe) {
			response.setRenderParameter(
				"mvcPath", "/admin/edit_oauth2application.jsp");

			Class<? extends PortalException> peClass = pe.getClass();

			SessionErrors.add(request, peClass.getName());
		}
	}

	@Reference(unbind = "-")
	protected void setOAuth2ApplicationService(
		OAuth2ApplicationLocalService oAuth2ApplicationLocalService) {

		_oAuth2ApplicationLocalService = oAuth2ApplicationLocalService;
	}

	private OAuth2ApplicationLocalService _oAuth2ApplicationLocalService;

}