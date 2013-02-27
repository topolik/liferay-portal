/**
 * Copyright (c) 2000-2013 Liferay, Inc. All rights reserved.
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

package com.liferay.portlet;

import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.exception.SystemException;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.portlet.PortletLayoutListener;
import com.liferay.portal.kernel.portlet.PortletModeFactory;
import com.liferay.portal.kernel.util.ArrayUtil;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.model.Group;
import com.liferay.portal.model.Layout;
import com.liferay.portal.model.LayoutTypePortlet;
import com.liferay.portal.model.Portlet;
import com.liferay.portal.security.auth.AuthTokenUtil;
import com.liferay.portal.security.permission.ActionKeys;
import com.liferay.portal.security.permission.PermissionChecker;
import com.liferay.portal.service.PortletPreferencesLocalServiceUtil;
import com.liferay.portal.service.permission.GroupPermissionUtil;
import com.liferay.portal.service.permission.LayoutPermissionUtil;
import com.liferay.portal.service.permission.LayoutPrototypePermissionUtil;
import com.liferay.portal.service.permission.LayoutSetPrototypePermissionUtil;
import com.liferay.portal.service.permission.OrganizationPermissionUtil;
import com.liferay.portal.service.permission.PortletPermissionUtil;
import com.liferay.portal.theme.ThemeDisplay;
import com.liferay.portal.util.PortalUtil;
import com.liferay.portal.util.PortletKeys;
import com.liferay.portal.util.PropsValues;
import com.liferay.portal.util.WebKeys;

import javax.portlet.PortletMode;
import javax.servlet.http.HttpServletRequest;

/**
 * @author Tomas Polesovsky
 */
public class PortletContainerPermissionImpl {

	public boolean isAuthorized(HttpServletRequest request, Portlet portlet)
		throws PortalException, SystemException {

		if(_log.isDebugEnabled()){
			_log.debug("Checking authorization for " + portlet.getPortletId());
		}

		boolean isPortletAlive = isPortletAlive(portlet);

		if(_log.isDebugEnabled()){
			if (isPortletAlive) {
				_log.debug("Portlet " + portlet.getPortletId() + " is alive");
			}
			else {
				_log.debug("Portlet " + portlet.getPortletId() + " is not alive!");
			}
		}

		if(isPortletAlive && isPortletOnPage(request, portlet)){
			if(_log.isDebugEnabled()){
				_log.debug("Portlet " + portlet.getPortletId() + " is on the page");
			}

			if(isFirstTimeExecuted(request, portlet)){
				if(_log.isDebugEnabled()){
					_log.debug("Portlet " + portlet.getPortletId() + " is executed for the first time");
				}

				PortalUtil.addPortletDefaultResource(request, portlet);
			}

			if(hasAccessPermission(request, portlet)) {
				if(_log.isDebugEnabled()){
					_log.debug("User has access to portlet " + portlet.getPortletId());
				}
				return true;
			}

			if(_log.isDebugEnabled()){
				_log.debug("User doesn't have access to portlet " + portlet.getPortletId());
			}
		}


		if(isPortletAlive && grantedByPPAuth(request, portlet)){
			if(_log.isDebugEnabled()){
				_log.debug("Portlet " + portlet.getPortletId() + " was authorized based on p_p_auth check");
			}

			// for the sake of backward compatibility, add-default-resource must
			// bypass all portlet container checks

			if(true) {
//			if(hasAccessPermission(request, portlet)) {
				if(_log.isDebugEnabled()){
					_log.debug("User has access to portlet " + portlet.getPortletId());
				}
				return true;
			}

			if(_log.isDebugEnabled()){
				_log.debug("User doesn't have access to portlet " + portlet.getPortletId());
			}
		}

		if(_log.isDebugEnabled()){
			_log.debug("Portlet " + portlet.getPortletId() + " is not alive or is not placed on the page");
		}

		return false;
	}

	/*
	 * Copied from PortalImpl.isAllowAddPortletDefaultResource()
	 */
	protected boolean grantedByPPAuth(
		HttpServletRequest request, Portlet portlet) {

		ThemeDisplay themeDisplay = (ThemeDisplay)request.getAttribute(
			WebKeys.THEME_DISPLAY);

		Layout layout = themeDisplay.getLayout();

		String portletId = portlet.getPortletId();

		if (!portlet.isAddDefaultResource()) {
			return false;
		}

		if (!PropsValues.PORTLET_ADD_DEFAULT_RESOURCE_CHECK_ENABLED) {
			return true;
		}

		if (PortalUtil.getPortletAddDefaultResourceCheckWhitelist().contains(portletId)) {
			return true;
		}

		String namespace = PortalUtil.getPortletNamespace(portletId);

		String strutsAction = ParamUtil.getString(
			request, namespace + "struts_action");

		if (Validator.isNull(strutsAction)) {
			strutsAction = ParamUtil.getString(request, "struts_action");
		}

		if (PortalUtil.getPortletAddDefaultResourceCheckWhitelistActions().contains(
			strutsAction)) {

			return true;
		}

		String requestPortletAuthenticationToken = ParamUtil.getString(
			request, "p_p_auth");

		if (Validator.isNull(requestPortletAuthenticationToken)) {
			HttpServletRequest originalRequest = PortalUtil.getOriginalServletRequest(
				request);

			requestPortletAuthenticationToken = ParamUtil.getString(
				originalRequest, "p_p_auth");
		}

		if (Validator.isNotNull(requestPortletAuthenticationToken)) {
			String actualPortletAuthenticationToken = AuthTokenUtil.getToken(
				request, layout.getPlid(), portletId);

			if (requestPortletAuthenticationToken.equals(
				actualPortletAuthenticationToken)) {

				return true;
			}
		}

		return false;
	}

	/*
	 * Copied from PortletContainerImpl
	 */
	protected boolean hasAccessPermission(
			HttpServletRequest request, Portlet portlet)
		throws SystemException, PortalException {

		ThemeDisplay themeDisplay = (ThemeDisplay)request.getAttribute(
			WebKeys.THEME_DISPLAY);

		Layout layout = themeDisplay.getLayout();

		PermissionChecker permissionChecker =
			themeDisplay.getPermissionChecker();

		long scopeGroupId = themeDisplay.getScopeGroupId();

		PortletMode portletMode = PortletModeFactory.getPortletMode(
			ParamUtil.getString(request, "p_p_mode"));

		return PortletPermissionUtil.hasAccessPermission(
			permissionChecker, scopeGroupId, layout, portlet,
			portletMode);

	}

	/*
	 * Copied from LayoutTypePortletImpl.hasPortletId()
	 */
	protected boolean isFirstTimeExecuted(
			HttpServletRequest request, Portlet portlet)
		throws SystemException, PortalException {

		ThemeDisplay themeDisplay = (ThemeDisplay)request.getAttribute(
			WebKeys.THEME_DISPLAY);

		Layout layout = themeDisplay.getLayout();

		String portletId = portlet.getPortletId();
		if (((PortletPreferencesLocalServiceUtil.getPortletPreferencesCount(
				PortletKeys.PREFS_OWNER_TYPE_LAYOUT, layout.getPlid(),
				portletId) < 1) &&
				(PortletPreferencesLocalServiceUtil.getPortletPreferencesCount(
					PortletKeys.PREFS_OWNER_TYPE_USER, layout.getPlid(),
					portletId) < 1))) {

			return true;
		}

		return false;
	}

	/*
	 * Copied from PortalImpl.isAllowAddPortletDefaultResource()
	 */
	protected boolean isLayoutConfigurationPortletAllowedOnPage(
			ThemeDisplay themeDisplay, Layout layout, String portletId)
		throws SystemException, PortalException {

		if (themeDisplay.isSignedIn() &&
			(portletId.equals(PortletKeys.LAYOUT_CONFIGURATION) ||
			 portletId.equals(PortletKeys.LAYOUTS_ADMIN))) {

			PermissionChecker permissionChecker =
				themeDisplay.getPermissionChecker();

			Group group = layout.getGroup();

			if (group.isSite()) {
				if (LayoutPermissionUtil.contains(
					permissionChecker, layout, ActionKeys.CUSTOMIZE) ||
					LayoutPermissionUtil.contains(
						permissionChecker, layout, ActionKeys.UPDATE)) {

					return true;
				}
			}

			if (group.isCompany()) {
				if (permissionChecker.isCompanyAdmin()) {
					return true;
				}
			}
			else if (group.isLayoutPrototype()) {
				long layoutPrototypeId = group.getClassPK();

				if (LayoutPrototypePermissionUtil.contains(
					permissionChecker, layoutPrototypeId,
					ActionKeys.UPDATE)) {

					return true;
				}
			}
			else if (group.isLayoutSetPrototype()) {
				long layoutSetPrototypeId = group.getClassPK();

				if (LayoutSetPrototypePermissionUtil.contains(
					permissionChecker, layoutSetPrototypeId,
					ActionKeys.UPDATE)) {

					return true;
				}
			}
			else if (group.isOrganization()) {
				long organizationId = group.getOrganizationId();

				if (OrganizationPermissionUtil.contains(
					permissionChecker, organizationId, ActionKeys.UPDATE)) {

					return true;
				}
			}
			else if (group.isUserGroup()) {
				long scopeGroupId = themeDisplay.getScopeGroupId();

				if (GroupPermissionUtil.contains(
					permissionChecker, scopeGroupId, ActionKeys.UPDATE)) {

					return true;
				}
			}
			else if (group.isUser()) {
				return true;
			}
		}

		return false;
	}

	protected boolean isPortletAlive(Portlet portlet) {
		return portlet.isActive() && !portlet.isUndeployedPortlet();
	}

	/*
	 * Copied from PortalImpl.isAllowAddPortletDefaultResource()
	 */
	protected boolean isPortletOnControlPanelLayout(
			ThemeDisplay themeDisplay, Layout layout, String portletId)
		throws SystemException {

		if (layout.isTypeControlPanel() &&
			PortalUtil.isControlPanelPortlet(portletId, themeDisplay)) {

			return true;
		}

		return false;
	}

	/*
	 * Copied from PortalImpl.isAllowAddPortletDefaultResource()
	 */
	protected boolean isPortletOnPage(HttpServletRequest request, Portlet portlet)
		throws SystemException, PortalException {

		ThemeDisplay themeDisplay = (ThemeDisplay)request.getAttribute(
			WebKeys.THEME_DISPLAY);

		Layout layout = themeDisplay.getLayout();

		String portletId = portlet.getPortletId();
		
		Boolean renderPortletResource = (Boolean)request.getAttribute(
			WebKeys.RENDER_PORTLET_RESOURCE);

		if (renderPortletResource != null) {
			boolean runtimePortlet = renderPortletResource.booleanValue();

			if (runtimePortlet) {
				return true;
			}
		}

		if(isPortletOnPanelLayout(themeDisplay, layout, portletId)){
			return true;
		}

		if(isPortletOnControlPanelLayout(themeDisplay, layout, portletId)){
			return true;
		}

		if(isPortletOnPortletLayout(themeDisplay, layout, portletId)){
			return true;
		}
		
		if(isLayoutConfigurationPortletAllowedOnPage(themeDisplay, layout, portletId)){
			return true;
		}

		return false;
	}

	/*
	 * Copied from PortalImpl.isAllowAddPortletDefaultResource()
	 */
	protected boolean isPortletOnPanelLayout(
		ThemeDisplay themeDisplay, Layout layout, String portletId) {

		if (layout.isTypePanel()){
			String panelSelectedPortlets = layout.getTypeSettingsProperty(
				"panelSelectedPortlets");

			if (Validator.isNotNull(panelSelectedPortlets)) {
				String[] panelSelectedPortletsArray = StringUtil.split(
					panelSelectedPortlets);

				if (ArrayUtil.contains(panelSelectedPortletsArray, portletId)) {
					return true;
				}
			}
		}

		return false;
	}

	/*
	 * Copied from PortalImpl.isAllowAddPortletDefaultResource()
	 */
	protected boolean isPortletOnPortletLayout(
			ThemeDisplay themeDisplay, Layout layout, String portletId)
		throws SystemException, PortalException {

		LayoutTypePortlet layoutTypePortlet =
			themeDisplay.getLayoutTypePortlet();

		if ((layoutTypePortlet != null) &&
			layoutTypePortlet.hasPortletId(portletId)) {

			return true;
		}

		return false;
	}

	private static Log _log = LogFactoryUtil.getLog(PortletContainerPermissionImpl.class);
}
