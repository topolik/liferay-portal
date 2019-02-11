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

package com.liferay.multi.factor.authentication.portlet.web.internal.portlet;

import com.liferay.multi.factor.authentication.api.MFARegistry;
import com.liferay.multi.factor.authentication.portlet.api.constants.MFAPortletKeys;
import com.liferay.multi.factor.authentication.spi.integration.MFAIntegration;
import com.liferay.multi.factor.authentication.spi.verifier.BrowserMFAVerifier;
import com.liferay.multi.factor.authentication.spi.verifier.MFAVerifier;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.portlet.bridges.mvc.MVCPortlet;
import com.liferay.portal.kernel.servlet.SessionErrors;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.kernel.util.StringBundler;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

import javax.portlet.ActionRequest;
import javax.portlet.ActionResponse;
import javax.portlet.Portlet;
import javax.portlet.PortletContext;
import javax.portlet.PortletException;
import javax.portlet.PortletRequestDispatcher;
import javax.portlet.RenderRequest;
import javax.portlet.RenderResponse;
import java.io.IOException;

/**
 * @author Tomas Polesovsky
 */
@Component(
	immediate = true,
	property = {
		"com.liferay.portlet.add-default-resource=true",
		"com.liferay.portlet.application-type=full-page-application",
		"com.liferay.portlet.css-class-wrapper=portlet-mfa-verify",
		"com.liferay.portlet.display-category=category.hidden",
		"com.liferay.portlet.header-portlet-css=/css/main.css",
		"com.liferay.portlet.preferences-company-wide=true",
		"javax.portlet.display-name=Multi Factor Authentication Verify Portlet",
		"javax.portlet.init-param.mvc-command-names-default-views=/mfa_verify/verify",
		"javax.portlet.init-param.portlet-title-based-navigation=true",
		"javax.portlet.init-param.template-path=/",
		"javax.portlet.name=" + MFAPortletKeys.MFA_VERIFY,
		"javax.portlet.resource-bundle=content.Language",
		"portlet.add.default.resource.check.whitelist=" + MFAPortletKeys.MFA_VERIFY
	},
	service = Portlet.class
)
public class MFAVerifyPortlet extends MVCPortlet {

	@Override
	public void processAction(
		ActionRequest actionRequest, ActionResponse actionResponse)
		throws IOException, PortletException {

		String integrationName = ParamUtil.getString(
			actionRequest, "integrationName");

		MFAIntegration mfaIntegration =
			_mfaRegistry.getMFAIntegration(integrationName);

		if (mfaIntegration == null) {
			SessionErrors.add(
				actionRequest, "unknownMFAIntegrationName", integrationName);

			actionResponse.setRenderParameter("mvcRenderCommandName", "/");
			actionResponse.setRenderParameter("mvcPath", "/error.jsp");

			return;
		}

		MFAVerifier mfaVerifier = _mfaRegistry.getMFAVerifier(integrationName);

		if (mfaVerifier == null) {
			SessionErrors.add(
				actionRequest, "noVerifierConfigured", integrationName);

			actionResponse.setRenderParameter("mvcRenderCommandName", "/");
			actionResponse.setRenderParameter("mvcPath", "/error.jsp");

			return;
		}

		if (!mfaVerifier.supportsBrowser() ||
			!(mfaVerifier instanceof BrowserMFAVerifier)) {

			_log.error(
				StringBundler.concat(
				"Unsupported MFAVerifier: ",
					mfaVerifier.getClass().getName(), " for integration ",
					integrationName));

			SessionErrors.add(actionRequest, "unsupportedIntegrationVerifier");

			actionResponse.setRenderParameter("mvcRenderCommandName", "/");
			actionResponse.setRenderParameter("mvcPath", "/error.jsp");

			return;
		}

		super.processAction(actionRequest, actionResponse);
	}

	@Override
	public void render(
		RenderRequest renderRequest, RenderResponse renderResponse)
		throws IOException, PortletException {

		String integrationName = ParamUtil.getString(
			renderRequest, "integrationName");

		MFAIntegration mfaIntegration =
			_mfaRegistry.getMFAIntegration(integrationName);

		if (mfaIntegration == null) {
			SessionErrors.add(
				renderRequest, "unknownMFAIntegrationName", integrationName);

			PortletContext portletContext = getPortletContext();

			PortletRequestDispatcher portletRequestDispatcher =
				portletContext.getRequestDispatcher("/error.jsp");

			portletRequestDispatcher.include(renderRequest, renderResponse);

			return;
		}

		MFAVerifier mfaVerifier = _mfaRegistry.getMFAVerifier(integrationName);

		if (mfaVerifier == null) {
			SessionErrors.add(
				renderRequest, "noVerifierConfigured", integrationName);

			PortletContext portletContext = getPortletContext();

			PortletRequestDispatcher portletRequestDispatcher =
				portletContext.getRequestDispatcher("/error.jsp");

			portletRequestDispatcher.include(renderRequest, renderResponse);

			return;
		}



		if (!mfaVerifier.supportsBrowser() ||
			!(mfaVerifier instanceof BrowserMFAVerifier)) {

			_log.error(
				StringBundler.concat(
					"Unsupported MFAVerifier: ",
					mfaVerifier.getClass().getName(), " for integration ",
					integrationName));

			SessionErrors.add(renderRequest, "unsupportedIntegrationVerifier");

			PortletContext portletContext = getPortletContext();

			PortletRequestDispatcher portletRequestDispatcher =
				portletContext.getRequestDispatcher("/error.jsp");

			portletRequestDispatcher.include(renderRequest, renderResponse);

			return;
		}

		super.render(renderRequest, renderResponse);
	}

	private static final Log _log = LogFactoryUtil.getLog(
		MFAVerifyPortlet.class);

	@Reference
	private MFARegistry _mfaRegistry;
}
