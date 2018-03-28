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
import com.liferay.asset.kernel.model.AssetVocabulary;
import com.liferay.asset.kernel.service.AssetVocabularyLocalService;
import com.liferay.portal.kernel.bean.BeanReference;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.security.auth.PrincipalException;
import com.liferay.portal.kernel.security.permission.PermissionChecker;
import com.liferay.portal.kernel.security.permission.resource.ModelResourcePermission;
import com.liferay.portal.kernel.security.permission.resource.PortletResourcePermission;
import com.liferay.portal.kernel.security.permission.resource.PortletResourcePermissionFactory;
import com.liferay.portal.kernel.spring.osgi.OSGiBeanProperties;

/**
 * @author Tomas Polesovsky
 */
@OSGiBeanProperties(
	property = "model.class.name=com.liferay.asset.kernel.model.AssetVocabulary"
)
public class AssetVocabularyModelResourcePermission
	implements ModelResourcePermission<AssetVocabulary> {

	@Override
	public void check(
			PermissionChecker permissionChecker,
			AssetVocabulary assetVocabulary, String actionId)
		throws PortalException {

		if (!contains(permissionChecker, assetVocabulary, actionId)) {
			throw new PrincipalException.MustHavePermission(
				permissionChecker, AssetVocabulary.class.getName(),
				assetVocabulary.getVocabularyId(), actionId);
		}
	}

	@Override
	public void check(
			PermissionChecker permissionChecker, long vocabularyId,
			String actionId)
		throws PortalException {

		AssetVocabulary assetVocabulary =
			_assetVocabularyLocalService.getAssetVocabulary(vocabularyId);

		check(permissionChecker, assetVocabulary, actionId);
	}

	@Override
	public boolean contains(
			PermissionChecker permissionChecker,
			AssetVocabulary assetVocabulary, String actionId)
		throws PortalException {

		if (permissionChecker.hasOwnerPermission(
				assetVocabulary.getCompanyId(), AssetVocabulary.class.getName(),
				assetVocabulary.getVocabularyId(), assetVocabulary.getUserId(),
				actionId)) {

			return true;
		}

		return permissionChecker.hasPermission(
			assetVocabulary.getGroupId(), AssetVocabulary.class.getName(),
			assetVocabulary.getVocabularyId(), actionId);
	}

	@Override
	public boolean contains(
			PermissionChecker permissionChecker, long vocabularyId,
			String actionId)
		throws PortalException {

		AssetVocabulary assetVocabulary =
			_assetVocabularyLocalService.getAssetVocabulary(vocabularyId);

		return contains(permissionChecker, assetVocabulary, actionId);
	}

	@Override
	public String getModelName() {
		return AssetVocabulary.class.getName();
	}

	@Override
	public PortletResourcePermission getPortletResourcePermission() {
		return _portletResourcePermission;
	}

	private static volatile PortletResourcePermission
		_portletResourcePermission =
			PortletResourcePermissionFactory.getInstance(
				AssetVocabularyModelResourcePermission.class,
				"_portletResourcePermission",
				AssetConstants.RESOURCE_NAME_CATEGORIES);

	@BeanReference(type = AssetVocabularyLocalService.class)
	private AssetVocabularyLocalService _assetVocabularyLocalService;

}