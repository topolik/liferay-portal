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

package com.liferay.oauth2.provider.web.internal.portlet;

import com.liferay.oauth2.provider.service.OAuth2AuthorizationService;
import com.liferay.oauth2.provider.web.constants.OAuth2ProviderPortletKeys;
import com.liferay.oauth2.provider.web.constants.OAuth2ProviderWebKeys;
import com.liferay.oauth2.provider.web.internal.display.context.OAuth2ConnectedApplicationsPortletDisplayContext;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.portlet.bridges.mvc.MVCPortlet;
import com.liferay.portal.kernel.servlet.SessionErrors;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.kernel.util.StringUtil;

import java.io.IOException;

import javax.portlet.ActionRequest;
import javax.portlet.ActionResponse;
import javax.portlet.Portlet;
import javax.portlet.PortletException;
import javax.portlet.RenderRequest;
import javax.portlet.RenderResponse;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Tomas Polesovsky
 */
@Component(
	immediate = true,
	property = {
		"com.liferay.portlet.css-class-wrapper=portlet-oauth2-provider-connected-applications",
		"com.liferay.portlet.display-category=category.hidden",
		"com.liferay.portlet.header-portlet-css=/css/main.css",
		"com.liferay.portlet.preferences-owned-by-group=true",
		"com.liferay.portlet.preferences-unique-per-layout=false",
		"javax.portlet.display-name=OAuth2 Connected Applications",
		"javax.portlet.init-param.portlet-title-based-navigation=true",
		"javax.portlet.init-param.template-path=/connected_applications/",
		"javax.portlet.init-param.view-template=/connected_applications/view.jsp",
		"javax.portlet.name=" + OAuth2ProviderPortletKeys.OAUTH2_CONNECTED_APPLICATIONS,
		"javax.portlet.resource-bundle=content.Language"
	},
	service = Portlet.class
)
public class OAuth2ConnectedApplicationsPortlet extends MVCPortlet {

	@Override
	public void render(
			RenderRequest renderRequest, RenderResponse renderResponse)
		throws IOException, PortletException {

		OAuth2ConnectedApplicationsPortletDisplayContext
			oAuth2ConnectedApplicationsPortletDisplayContext =
				new OAuth2ConnectedApplicationsPortletDisplayContext(
					renderRequest);

		renderRequest.setAttribute(
			OAuth2ProviderWebKeys.
				OAUTH2_CONNECTED_APPLICATIONS_PORTLET_DISPLAY_CONTEXT,
			oAuth2ConnectedApplicationsPortletDisplayContext);

		super.render(renderRequest, renderResponse);
	}

	public void revokeOAuth2Authorization(
			ActionRequest request, ActionResponse response)
		throws PortalException {

		long oAuth2AuthorizationId = ParamUtil.getLong(
			request, "oAuth2AuthorizationId");

		try {
			_oAuth2AuthorizationService.revokeOAuth2Authorization(
				oAuth2AuthorizationId);
		}
		catch (PortalException pe) {
			if (_log.isDebugEnabled()) {
				_log.debug(pe);
			}

			SessionErrors.add(request, pe.getClass());
		}
	}

	public void revokeOAuth2Authorizations(
		ActionRequest request, ActionResponse response) {

		long[] oAuth2AuthorizationIds = StringUtil.split(
			ParamUtil.getString(request, "oAuth2AuthorizationIds"), 0L);

		try {
			for (long oAuth2AuthorizationId : oAuth2AuthorizationIds) {
				_oAuth2AuthorizationService.revokeOAuth2Authorization(
					oAuth2AuthorizationId);
			}
		}
		catch (PortalException pe) {
			if (_log.isDebugEnabled()) {
				_log.debug(pe);
			}

			SessionErrors.add(request, pe.getClass());
		}
	}

	private static final Log _log = LogFactoryUtil.getLog(
		OAuth2ConnectedApplicationsPortlet.class);

	@Reference
	private OAuth2AuthorizationService _oAuth2AuthorizationService;

}