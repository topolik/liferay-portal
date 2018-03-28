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

package com.liferay.portlet.asset.service.permission;

import com.liferay.asset.kernel.model.AssetCategory;
import com.liferay.asset.kernel.model.AssetCategoryConstants;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.security.permission.PermissionChecker;
import com.liferay.portal.kernel.security.permission.resource.ModelResourcePermission;
import com.liferay.portal.kernel.security.permission.resource.ModelResourcePermissionFactory;
import com.liferay.portal.kernel.security.permission.resource.PortletResourcePermission;
import com.liferay.portal.kernel.security.permission.resource.PortletResourcePermissionFactory;
import com.liferay.portlet.asset.constants.AssetConstants;

/**
 * @author Eduardo Lundgren
 * @deprecated As of 7.1.0, with no direct replacement
 */
@Deprecated
public class AssetCategoryPermission {

	public static void check(
			PermissionChecker permissionChecker, AssetCategory category,
			String actionId)
		throws PortalException {

		_modelResourcePermission.check(permissionChecker, category, actionId);
	}

	public static void check(
			PermissionChecker permissionChecker, long groupId, long categoryId,
			String actionId)
		throws PortalException {

		if (categoryId == AssetCategoryConstants.DEFAULT_PARENT_CATEGORY_ID) {
			_portletResourcePermission.check(
				permissionChecker, groupId, actionId);
		}
		else {
			_modelResourcePermission.check(
				permissionChecker, categoryId, actionId);
		}
	}

	public static void check(
			PermissionChecker permissionChecker, long categoryId,
			String actionId)
		throws PortalException {

		_modelResourcePermission.check(
			permissionChecker, categoryId, actionId);
	}

	public static boolean contains(
			PermissionChecker permissionChecker, AssetCategory category,
			String actionId)
		throws PortalException {

		return _modelResourcePermission.contains(
			permissionChecker, category, actionId);
	}

	public static boolean contains(
			PermissionChecker permissionChecker, long groupId, long categoryId,
			String actionId)
		throws PortalException {

		if (categoryId == AssetCategoryConstants.DEFAULT_PARENT_CATEGORY_ID) {
			return _portletResourcePermission.contains(
				permissionChecker, groupId, actionId);
		}
		else {
			return _modelResourcePermission.contains(
				permissionChecker, categoryId, actionId);
		}
	}

	public static boolean contains(
			PermissionChecker permissionChecker, long categoryId,
			String actionId)
		throws PortalException {

		return _modelResourcePermission.contains(
			permissionChecker, categoryId, actionId);
	}

	private static volatile ModelResourcePermission<AssetCategory>
		_modelResourcePermission =
			ModelResourcePermissionFactory.getInstance(
				AssetCategoryPermission.class, "_modelResourcePermission",
				AssetCategory.class);

	private static volatile PortletResourcePermission
		_portletResourcePermission =
			PortletResourcePermissionFactory.getInstance(
				AssetCategoryPermission.class,
				"_portletResourcePermission",
				AssetConstants.RESOURCE_NAME_CATEGORIES);
}