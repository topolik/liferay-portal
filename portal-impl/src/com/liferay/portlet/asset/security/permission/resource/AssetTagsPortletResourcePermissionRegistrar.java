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

package com.liferay.portlet.asset.security.permission.resource;

import com.liferay.asset.kernel.constants.AssetConstants;
import com.liferay.exportimport.kernel.staging.permission.StagingPermission;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.security.permission.PermissionChecker;
import com.liferay.portal.kernel.security.permission.resource.PortletResourcePermission;
import com.liferay.portal.kernel.security.permission.resource.PortletResourcePermissionFactory;
import com.liferay.portal.kernel.security.permission.resource.PortletResourcePermissionLogic;
import com.liferay.registry.Registry;
import com.liferay.registry.RegistryUtil;
import com.liferay.registry.ServiceRegistration;
import com.liferay.registry.ServiceTracker;

import java.util.HashMap;
import java.util.Map;

/**
 * @author Tomas Polesovsky
 */
public class AssetTagsPortletResourcePermissionRegistrar {

	public void afterPropertiesSet() {
		Registry registry = RegistryUtil.getRegistry();

		_serviceTracker = registry.trackServices(StagingPermission.class);

		_serviceTracker.open();

		Map<String, Object> properties = new HashMap<>();

		properties.put("resource.name", AssetConstants.RESOURCE_NAME_TAGS);

		_serviceRegistration = registry.registerService(
			PortletResourcePermission.class,
			PortletResourcePermissionFactory.create(
				AssetConstants.RESOURCE_NAME_TAGS,
				new AssetTagsStagedPortletPermissionLogic()),
			properties);
	}

	public void destroy() {
		_serviceRegistration.unregister();

		_serviceTracker.close();
	}

	private static final String _ASSET_TAGS_ADMIN_PORTLET =
		"com_liferay_asset_tags_admin_web_portlet_AssetTagsAdminPortlet";

	private ServiceRegistration<PortletResourcePermission> _serviceRegistration;
	private ServiceTracker<StagingPermission, StagingPermission>
		_serviceTracker;

	private class AssetTagsStagedPortletPermissionLogic
		implements PortletResourcePermissionLogic {

		@Override
		public Boolean contains(
			PermissionChecker permissionChecker, String name, Group group,
			String actionId) {

			StagingPermission stagingPermission = _serviceTracker.getService();

			if (stagingPermission == null) {
				return true;
			}

			long groupId = 0;

			if (group != null) {
				groupId = group.getGroupId();
			}

			return stagingPermission.hasPermission(
				permissionChecker, group, name, groupId,
				_ASSET_TAGS_ADMIN_PORTLET, actionId);
		}

	}

}