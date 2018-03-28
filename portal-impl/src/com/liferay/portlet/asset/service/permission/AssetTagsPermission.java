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

import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.security.permission.BaseResourcePermissionChecker;
import com.liferay.portal.kernel.security.permission.PermissionChecker;
import com.liferay.portal.kernel.security.permission.resource.PortletResourcePermission;
import com.liferay.portal.kernel.security.permission.resource.PortletResourcePermissionFactory;
import com.liferay.portal.kernel.spring.osgi.OSGiBeanProperties;
import com.liferay.portlet.asset.constants.AssetConstants;

/**
 * @author Pavel Savinov
 * @deprecated As of 7.0.0, with no direct replacement
 */
@Deprecated
@OSGiBeanProperties(
	property = {"resource.name=" + AssetConstants.RESOURCE_NAME_TAGS}
)
public class AssetTagsPermission extends BaseResourcePermissionChecker {

	public static final String RESOURCE_NAME =
		AssetConstants.RESOURCE_NAME_TAGS;

	public static void check(
			PermissionChecker permissionChecker, long groupId, String actionId)
		throws PortalException {

		_portletResourcePermission.check(permissionChecker, groupId, actionId);
	}

	public static boolean contains(
		PermissionChecker permissionChecker, long groupId, String actionId) {

		return _portletResourcePermission.contains(
			permissionChecker, groupId, actionId);
	}

	@Override
	public Boolean checkResource(
		PermissionChecker permissionChecker, long classPK, String actionId) {

		return _portletResourcePermission.contains(
			permissionChecker, classPK, actionId);
	}

	private static volatile PortletResourcePermission
		_portletResourcePermission =
			PortletResourcePermissionFactory.getInstance(
				AssetTagsPermission.class, "_portletResourcePermission",
				AssetConstants.RESOURCE_NAME_TAGS);

}