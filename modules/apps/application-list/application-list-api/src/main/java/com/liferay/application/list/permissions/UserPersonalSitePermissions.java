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

package com.liferay.application.list.permissions;

import com.liferay.application.list.constants.PanelCategoryKeys;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.security.permission.ResourceActionsUtil;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.model.Company;
import com.liferay.portal.model.Group;
import com.liferay.portal.model.Portlet;
import com.liferay.portal.model.ResourceConstants;
import com.liferay.portal.model.Role;
import com.liferay.portal.model.RoleConstants;
import com.liferay.portal.service.GroupLocalService;
import com.liferay.portal.service.ResourcePermissionLocalService;
import com.liferay.portal.service.RoleLocalService;

import java.util.List;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Tomas Polesovsky
 */
@Component(immediate = true, service = UserPersonalSitePermissions.class)
public class UserPersonalSitePermissions {

	public void initPermissions(List<Company> companies, Portlet portlet) {
		String controlPanelEntryCategory =
			portlet.getControlPanelEntryCategory();

		if (!controlPanelEntryCategory.startsWith(
				PanelCategoryKeys.SITE_ADMINISTRATION)) {

			return;
		}

		String rootPortletId = portlet.getRootPortletId();

		for (Company company : companies) {
			long companyId = company.getCompanyId();

			Role powerUserRole = null;

			try {
				powerUserRole = roleLocalService.getRole(
					companyId, RoleConstants.POWER_USER);
			}
			catch (PortalException e) {
				_log.error(
					"Unable to obtain power user role in company " + companyId,
					e);

				continue;
			}

			Group userPersonalSite = null;

			try {
				userPersonalSite = groupLocalService.getUserPersonalSiteGroup(
					companyId);
			}
			catch (PortalException e) {
				_log.error(
					"Unable to obtain personal site in company " + companyId,
					e);

				continue;
			}

			try {
				initPermissions(
					companyId, powerUserRole.getRoleId(), rootPortletId,
					userPersonalSite.getGroupId());
			}
			catch (PortalException e) {
				_log.error(
					"Unable to initialize user personal site permissions" +
						" for portlet " + portlet.getPortletId() +
						" in company " + companyId
					, e);
			}
		}
	}

	public void initPermissions(long companyId, List<Portlet> portlets) {
		Role powerUserRole = null;

		try {
			powerUserRole = roleLocalService.getRole(
				companyId, RoleConstants.POWER_USER);
		}
		catch (PortalException e) {
			_log.error(
				"Unable to obtain power user role in company " + companyId, e);

			return;
		}

		Group userPersonalSite = null;

		try {
			userPersonalSite = groupLocalService.getUserPersonalSiteGroup(
				companyId);
		}
		catch (PortalException e) {
			_log.error(
				"Unable to obtain personal site in company " + companyId, e);

			return;
		}

		for (Portlet portlet : portlets) {
			String controlPanelEntryCategory =
				portlet.getControlPanelEntryCategory();

			if (!controlPanelEntryCategory.equals(
					PanelCategoryKeys.SITE_ADMINISTRATION_CONTENT)) {

				continue;
			}

			try {
				initPermissions(
					companyId, powerUserRole.getRoleId(),
					portlet.getRootPortletId(), userPersonalSite.getGroupId());
			}
			catch (PortalException e) {
				_log.error(
					"Unable to initialize user personal site permissions" +
						" for portlet " + portlet.getPortletId() +
						" in company " + companyId
					, e);
			}
		}
	}

	protected void initPermissions(
			long companyId, long powerUserRoleId, String rootPortletId,
			long userPersonalSiteGroupId)
		throws PortalException {

		List<String> portletActionIds =
			ResourceActionsUtil.getPortletResourceActions(rootPortletId);

		resourcePermissionLocalService.setResourcePermissions(
			companyId, rootPortletId, ResourceConstants.SCOPE_GROUP,
			String.valueOf(userPersonalSiteGroupId), powerUserRoleId,
			portletActionIds.toArray(new String[0]));

		String rootModelName = ResourceActionsUtil.getPortletRootModelResource(
			rootPortletId);

		if (Validator.isBlank(rootModelName)) {
			return;
		}

		List<String> modelActionIds =
			ResourceActionsUtil.getModelResourceActions(rootModelName);

		resourcePermissionLocalService.setResourcePermissions(
			companyId, rootModelName, ResourceConstants.SCOPE_GROUP,
			String.valueOf(userPersonalSiteGroupId), powerUserRoleId,
			modelActionIds.toArray(new String[0]));
	}

	@Reference(unbind = "-")
	protected void setGroupLocalService(GroupLocalService groupLocalService) {
		this.groupLocalService = groupLocalService;
	}

	@Reference(unbind = "-")
	protected void setResourcePermissionLocalService(
		ResourcePermissionLocalService resourcePermissionLocalService) {

		this.resourcePermissionLocalService = resourcePermissionLocalService;
	}

	@Reference(unbind = "-")
	protected void setRoleLocalService(RoleLocalService roleLocalService) {
		this.roleLocalService = roleLocalService;
	}

	protected GroupLocalService groupLocalService;
	protected ResourcePermissionLocalService resourcePermissionLocalService;
	protected RoleLocalService roleLocalService;

	private static final Log _log = LogFactoryUtil.getLog(
		UserPersonalSitePermissions.class);

}