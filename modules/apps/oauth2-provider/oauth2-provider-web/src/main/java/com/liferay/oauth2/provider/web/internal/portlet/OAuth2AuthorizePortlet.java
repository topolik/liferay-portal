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

import com.liferay.oauth2.provider.model.OAuth2Application;
import com.liferay.oauth2.provider.model.OAuth2ApplicationScopeAliases;
import com.liferay.oauth2.provider.scope.liferay.ApplicationDescriptorLocator;
import com.liferay.oauth2.provider.scope.liferay.LiferayOAuth2Scope;
import com.liferay.oauth2.provider.scope.liferay.ScopeDescriptorLocator;
import com.liferay.oauth2.provider.scope.liferay.ScopeLocator;
import com.liferay.oauth2.provider.scope.spi.application.descriptor.ApplicationDescriptor;
import com.liferay.oauth2.provider.service.OAuth2ApplicationScopeAliasesLocalService;
import com.liferay.oauth2.provider.service.OAuth2ApplicationService;
import com.liferay.oauth2.provider.web.constants.OAuth2ProviderPortletKeys;
import com.liferay.oauth2.provider.web.constants.OAuth2ProviderWebKeys;
import com.liferay.oauth2.provider.web.internal.display.context.AuthorizationModel;
import com.liferay.oauth2.provider.web.internal.display.context.OAuth2AuthorizePortletDisplayContext;
import com.liferay.osgi.service.tracker.collections.map.ServiceTrackerMap;
import com.liferay.osgi.service.tracker.collections.map.ServiceTrackerMapFactory;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.portlet.bridges.mvc.MVCPortlet;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.kernel.util.Portal;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.kernel.util.WebKeys;

import java.io.IOException;

import java.util.Collection;
import java.util.Collections;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import javax.portlet.Portlet;
import javax.portlet.PortletException;
import javax.portlet.RenderRequest;
import javax.portlet.RenderResponse;

import javax.servlet.http.HttpServletRequest;

import org.osgi.framework.BundleContext;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Deactivate;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Tomas Polesovsky
 */
@Component(
	immediate = true,
	property = {
		"com.liferay.portlet.application-type=full-page-application",
		"com.liferay.portlet.css-class-wrapper=portlet-oauth2-provider-authorize",
		"com.liferay.portlet.display-category=category.hidden",
		"com.liferay.portlet.header-portlet-css=/css/main.css",
		"com.liferay.portlet.preferences-owned-by-group=true",
		"com.liferay.portlet.preferences-unique-per-layout=false",
		"javax.portlet.display-name=OAuth2 Authorize Portlet",
		"javax.portlet.init-param.portlet-title-based-navigation=true",
		"javax.portlet.init-param.template-path=/authorize/",
		"javax.portlet.init-param.view-template=/authorize/authorize.jsp",
		"javax.portlet.name=" + OAuth2ProviderPortletKeys.OAUTH2_AUTHORIZE,
		"javax.portlet.resource-bundle=content.Language"
	},
	service = Portlet.class
)
public class OAuth2AuthorizePortlet extends MVCPortlet {

	// It would be better if this method was moved to MVCRenderCommand classes
	@Override
	public void doView(
			RenderRequest renderRequest, RenderResponse renderResponse)
		throws IOException, PortletException {

		ThemeDisplay themeDisplay = (ThemeDisplay)renderRequest.getAttribute(
			WebKeys.THEME_DISPLAY);

		long companyId = themeDisplay.getCompanyId();

		HttpServletRequest request = _portal.getOriginalServletRequest(
			_portal.getHttpServletRequest(renderRequest));

		OAuth2AuthorizePortletDisplayContext
			oAuth2AuthorizePortletDisplayContext =
				new OAuth2AuthorizePortletDisplayContext(themeDisplay);

		Map<String, String> oAuth2Parameters = getOAuth2Parameters(request);

		oAuth2AuthorizePortletDisplayContext.setOAuth2Parameters(
			oAuth2Parameters);

		String clientId = oAuth2Parameters.get("client_id");

		try {
			OAuth2Application oAuth2Application =
				_oAuth2ApplicationService.fetchOAuth2Application(
					companyId, clientId);

			oAuth2AuthorizePortletDisplayContext.setOAuth2Application(
				oAuth2Application);

			List<String> allowedScopeAliases = Collections.emptyList();

			if (oAuth2Application.getOAuth2ApplicationScopeAliasesId() > 0) {
				OAuth2ApplicationScopeAliases oAuth2ApplicationScopeAliases =
					_oAuth2ApplicationScopeAliasesLocalService.
						getOAuth2ApplicationScopeAliases(
							oAuth2Application.
								getOAuth2ApplicationScopeAliasesId());

				allowedScopeAliases =
					oAuth2ApplicationScopeAliases.getScopeAliasesList();
			}

			String[] requestedScopeAliases = StringUtil.split(
				oAuth2Parameters.get("scope"), StringPool.SPACE);

			Locale locale = themeDisplay.getLocale();

			AuthorizationModel authorizationModel = new AuthorizationModel(
				_applicationDescriptorLocator, locale, _scopeDescriptorLocator);

			locateLiferayOAuth2Scopes(
				companyId, allowedScopeAliases, authorizationModel,
				requestedScopeAliases);

			oAuth2AuthorizePortletDisplayContext.setAuthorizationModel(
				authorizationModel);

			renderRequest.setAttribute(
				OAuth2ProviderWebKeys.OAUTH2_AUTHORIZE_PORTLET_DISPLAY_CONTEXT,
				oAuth2AuthorizePortletDisplayContext);
		}
		catch (PortalException pe) {
			throw new PortletException(pe);
		}

		super.doView(renderRequest, renderResponse);
	}

	@Activate
	protected void activate(BundleContext bundleContext) {
		_applicationDescriptors = ServiceTrackerMapFactory.openSingleValueMap(
			bundleContext, ApplicationDescriptor.class, "osgi.jaxrs.name");
	}

	@Deactivate
	protected void deactivate() {
		_applicationDescriptors.close();
	}

	protected Map<String, String> getOAuth2Parameters(
		HttpServletRequest request) {

		HashMap<String, String> result = new HashMap<>();

		for (Enumeration<String> names = request.getParameterNames();
			names.hasMoreElements();) {

			String name = names.nextElement();

			if (name.startsWith("oauth2_")) {
				result.put(
					name.substring("oauth2_".length()),
					ParamUtil.getString(request, name));
			}
		}

		return result;
	}

	protected void locateLiferayOAuth2Scopes(
		long companyId, List<String> allowedScopeAliases,
		AuthorizationModel authorizationModel, String[] requestedScopeAliases) {

		for (String requestedScopeAlias : requestedScopeAliases) {
			if (!allowedScopeAliases.contains(requestedScopeAlias)) {
				continue;
			}

			Collection<LiferayOAuth2Scope> liferayOAuth2Scopes =
				_scopeFinderLocator.getLiferayOAuth2Scopes(
					companyId, requestedScopeAlias);

			authorizationModel.addLiferayOAuth2Scopes(liferayOAuth2Scopes);
		}
	}

	@Reference
	private ApplicationDescriptorLocator _applicationDescriptorLocator;

	private ServiceTrackerMap<String, ApplicationDescriptor>
		_applicationDescriptors;

	@Reference
	private OAuth2ApplicationScopeAliasesLocalService
		_oAuth2ApplicationScopeAliasesLocalService;

	@Reference
	private OAuth2ApplicationService _oAuth2ApplicationService;

	@Reference
	private Portal _portal;

	@Reference
	private ScopeDescriptorLocator _scopeDescriptorLocator;

	@Reference
	private ScopeLocator _scopeFinderLocator;

}