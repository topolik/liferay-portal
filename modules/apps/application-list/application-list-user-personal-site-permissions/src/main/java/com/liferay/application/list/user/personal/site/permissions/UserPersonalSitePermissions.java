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

package com.liferay.application.list.user.personal.site.permissions;

import com.liferay.application.list.PanelApp;
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
import com.liferay.portal.service.CompanyLocalService;
import com.liferay.portal.service.GroupLocalService;
import com.liferay.portal.service.PortletLocalService;
import com.liferay.portal.service.ResourcePermissionLocalService;
import com.liferay.portal.service.RoleLocalService;

import java.util.List;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.component.annotations.ReferenceCardinality;
import org.osgi.service.component.annotations.ReferencePolicy;
import org.osgi.service.component.annotations.ReferencePolicyOption;

/**
 * @author Tomas Polesovsky
 */
@Component(immediate = true)
public class UserPersonalSitePermissions {

	public void initPermissions(List<Company> companies, Portlet portlet) {
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

	@Reference(
		cardinality = ReferenceCardinality.MULTIPLE,
		policy = ReferencePolicy.DYNAMIC,
		policyOption = ReferencePolicyOption.GREEDY,
		target = "(panel.category.key="+ PanelCategoryKeys.SITE_ADMINISTRATION_CONTENT +")",
		unbind = "-"
	)
	protected synchronized void addPanelApp(PanelApp panelApp) {
		Portlet portlet = portletLocalService.getPortletById(
			panelApp.getPortletId());

		initPermissions(companyLocalService.getCompanies(), portlet);
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
	protected void setCompanyLocalService(
		CompanyLocalService companyLocalService) {

		this.companyLocalService = companyLocalService;
	}

	@Reference(unbind = "-")
	protected void setGroupLocalService(GroupLocalService groupLocalService) {
		this.groupLocalService = groupLocalService;
	}

	@Reference(unbind = "-")
	protected void setPortletLocalService(
		PortletLocalService portletLocalService) {

		this.portletLocalService = portletLocalService;
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

	protected CompanyLocalService companyLocalService;
	protected GroupLocalService groupLocalService;
	protected PortletLocalService portletLocalService;
	protected ResourcePermissionLocalService resourcePermissionLocalService;
	protected RoleLocalService roleLocalService;

	private static final Log _log = LogFactoryUtil.getLog(
		UserPersonalSitePermissions.class);

}