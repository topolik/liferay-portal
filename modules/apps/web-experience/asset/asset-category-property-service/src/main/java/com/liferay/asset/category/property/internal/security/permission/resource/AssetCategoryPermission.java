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

package com.liferay.asset.category.property.internal.security.permission.resource;

import com.liferay.asset.kernel.model.AssetCategory;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.security.permission.PermissionChecker;
import com.liferay.portal.kernel.security.permission.resource.ModelResourcePermission;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Tomas Polesovsky
 */
@Component(immediate = true)
public class AssetCategoryPermission {

	public static void check(
			PermissionChecker permissionChecker, long categoryId,
			String actionId)
		throws PortalException {

		_modelResourcePermission.check(permissionChecker, categoryId, actionId);
	}

	public static boolean contains(
			PermissionChecker permissionChecker, long categoryId,
			String actionId)
		throws PortalException {

		return _modelResourcePermission.contains(
			permissionChecker, categoryId, actionId);
	}

	@Reference(
		target = "(model.class.name=com.liferay.asset.kernel.model.AssetCategory)",
		unbind = "-"
	)
	protected void setModelResourcePermission(
		ModelResourcePermission<AssetCategory> modelResourcePermission) {

		_modelResourcePermission = modelResourcePermission;
	}

	private static ModelResourcePermission<AssetCategory>
		_modelResourcePermission;

}