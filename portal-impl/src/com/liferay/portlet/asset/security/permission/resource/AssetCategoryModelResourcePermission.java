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
import com.liferay.asset.kernel.model.AssetCategory;
import com.liferay.asset.kernel.model.AssetCategoryConstants;
import com.liferay.asset.kernel.service.AssetCategoryLocalService;
import com.liferay.asset.kernel.service.AssetCategoryLocalServiceUtil;
import com.liferay.portal.kernel.bean.BeanReference;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.security.auth.PrincipalException;
import com.liferay.portal.kernel.security.permission.ActionKeys;
import com.liferay.portal.kernel.security.permission.PermissionChecker;
import com.liferay.portal.kernel.security.permission.resource.ModelResourcePermission;
import com.liferay.portal.kernel.security.permission.resource.PortletResourcePermission;
import com.liferay.portal.kernel.security.permission.resource.PortletResourcePermissionFactory;
import com.liferay.portal.kernel.spring.osgi.OSGiBeanProperties;
import com.liferay.portal.util.PropsValues;

/**
 * @author Tomas Polesovsky
 */
@OSGiBeanProperties(
	property = "model.class.name=com.liferay.asset.kernel.model.AssetCategory"
)
public class AssetCategoryModelResourcePermission
	implements ModelResourcePermission<AssetCategory> {

	@Override
	public void check(
			PermissionChecker permissionChecker, AssetCategory assetCategory,
			String actionId)
		throws PortalException {

		if (!contains(permissionChecker, assetCategory, actionId)) {
			throw new PrincipalException.MustHavePermission(
				permissionChecker, AssetCategory.class.getName(),
				assetCategory.getCategoryId(), actionId);
		}
	}

	@Override
	public void check(
			PermissionChecker permissionChecker, long categoryId,
			String actionId)
		throws PortalException {

		AssetCategory assetCategory = _assetCategoryLocalService.getCategory(
			categoryId);

		check(permissionChecker, assetCategory, actionId);
	}

	@Override
	public boolean contains(
			PermissionChecker permissionChecker, AssetCategory assetCategory,
			String actionId)
		throws PortalException {

		if (actionId.equals(ActionKeys.VIEW) &&
			!_assetVocabularyModelResourcePermission.contains(
				permissionChecker, assetCategory.getVocabularyId(),
				ActionKeys.VIEW)) {

			return false;
		}

		if (actionId.equals(ActionKeys.VIEW) &&
			PropsValues.PERMISSIONS_VIEW_DYNAMIC_INHERITANCE) {

			while (true) {
				if (!hasPermission(
						permissionChecker, assetCategory, actionId)) {

					return false;
				}

				long parentCategoryId = assetCategory.getParentCategoryId();

				if (parentCategoryId ==
						AssetCategoryConstants.DEFAULT_PARENT_CATEGORY_ID) {

					break;
				}

				assetCategory = AssetCategoryLocalServiceUtil.getCategory(
					parentCategoryId);
			}

			return _assetVocabularyModelResourcePermission.contains(
				permissionChecker, assetCategory.getVocabularyId(), actionId);
		}

		return hasPermission(permissionChecker, assetCategory, actionId);
	}

	@Override
	public boolean contains(
			PermissionChecker permissionChecker, long categoryId,
			String actionId)
		throws PortalException {

		AssetCategory assetCategory = _assetCategoryLocalService.getCategory(
			categoryId);

		return contains(permissionChecker, assetCategory, actionId);
	}

	@Override
	public String getModelName() {
		return AssetCategory.class.getName();
	}

	@Override
	public PortletResourcePermission getPortletResourcePermission() {
		return _portletResourcePermission;
	}

	protected boolean hasPermission(
		PermissionChecker permissionChecker, AssetCategory category,
		String actionId) {

		if (permissionChecker.hasOwnerPermission(
				category.getCompanyId(), AssetCategory.class.getName(),
				category.getCategoryId(), category.getUserId(), actionId) ||
			permissionChecker.hasPermission(
				category.getGroupId(), AssetCategory.class.getName(),
				category.getCategoryId(), actionId)) {

			return true;
		}

		return false;
	}

	private static volatile PortletResourcePermission
		_portletResourcePermission =
			PortletResourcePermissionFactory.getInstance(
				AssetCategoryModelResourcePermission.class,
				"_portletResourcePermission",
				AssetConstants.RESOURCE_NAME_CATEGORIES);

	@BeanReference(type = AssetCategoryLocalService.class)
	private AssetCategoryLocalService _assetCategoryLocalService;

	@BeanReference(type = AssetVocabularyModelResourcePermission.class)
	private AssetVocabularyModelResourcePermission
		_assetVocabularyModelResourcePermission;

}