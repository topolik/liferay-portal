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

import com.liferay.asset.kernel.model.AssetVocabulary;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.security.permission.PermissionChecker;
import com.liferay.portal.kernel.security.permission.resource.ModelResourcePermission;
import com.liferay.portal.kernel.security.permission.resource.ModelResourcePermissionFactory;

/**
 * @author Eduardo Lundgren
 * @author JorgeFerrer
 * @deprecated As of 7.1.0, with no direct replacement
 */
@Deprecated
public class AssetVocabularyPermission {

	public static void check(
			PermissionChecker permissionChecker, AssetVocabulary vocabulary,
			String actionId)
		throws PortalException {

		_modelResourcePermission.check(permissionChecker, vocabulary, actionId);
	}

	public static void check(
			PermissionChecker permissionChecker, long vocabularyId,
			String actionId)
		throws PortalException {

		_modelResourcePermission.check(
			permissionChecker, vocabularyId, actionId);
	}

	public static boolean contains(
			PermissionChecker permissionChecker, AssetVocabulary vocabulary,
			String actionId)
		throws PortalException {

		return _modelResourcePermission.contains(
			permissionChecker, vocabulary, actionId);
	}

	public static boolean contains(
			PermissionChecker permissionChecker, long vocabularyId,
			String actionId)
		throws PortalException {

		return _modelResourcePermission.contains(
			permissionChecker, vocabularyId, actionId);
	}

	private static volatile ModelResourcePermission<AssetVocabulary>
		_modelResourcePermission =
			ModelResourcePermissionFactory.getInstance(
				AssetVocabularyPermission.class, "_modelResourcePermission",
				AssetVocabulary.class);

}