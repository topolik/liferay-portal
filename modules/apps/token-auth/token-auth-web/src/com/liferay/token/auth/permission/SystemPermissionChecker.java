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

package com.liferay.token.auth.permission;

import com.liferay.portal.model.User;
import com.liferay.portal.security.permission.PermissionChecker;
import com.liferay.portal.security.permission.PermissionThreadLocal;

import javax.portlet.PortletRequest;
import java.security.PrivilegedAction;
import java.security.PrivilegedExceptionAction;
import java.util.List;

/**
 * @author Tomas Polesovsky
 */
public class SystemPermissionChecker implements PermissionChecker {
	private PermissionChecker _permissionChecker;

	public static <T> T runAsSystem(
		PrivilegedExceptionAction<T> privilegedExceptionAction)
		throws Exception {

		PermissionChecker oldInstance =
			PermissionThreadLocal.getPermissionChecker();

		PermissionThreadLocal.setPermissionChecker(
			new SystemPermissionChecker(oldInstance));

		try {
			return privilegedExceptionAction.run();
		}
		finally {
			PermissionThreadLocal.setPermissionChecker(oldInstance);
		}
	}

	public static <T> T runAsSystem(PrivilegedAction<T> privilegedAction) {
		PermissionChecker oldInstance =
			PermissionThreadLocal.getPermissionChecker();

		PermissionThreadLocal.setPermissionChecker(
			new SystemPermissionChecker(oldInstance));

		try {
			return privilegedAction.run();
		}
		finally {
			PermissionThreadLocal.setPermissionChecker(oldInstance);
		}
	}

	protected SystemPermissionChecker(PermissionChecker permissionChecker) {
		this._permissionChecker = permissionChecker;
	}

	@Override
	public PermissionChecker clone() {
		return new SystemPermissionChecker(_permissionChecker);
	}

	@Override
	public long getCompanyId() {
		return _permissionChecker.getCompanyId();
	}

	@Override
	public List<Long> getGuestResourceBlockIds(long companyId, long groupId, String name, String actionId) {
		return _permissionChecker.getGuestResourceBlockIds(companyId, groupId, name, actionId);
	}

	@Override
	public List<Long> getOwnerResourceBlockIds(long companyId, long groupId, String name, String actionId) {
		return _permissionChecker.getOwnerResourceBlockIds(companyId, groupId, name, actionId);
	}

	@Override
	public long getOwnerRoleId() {
		return _permissionChecker.getOwnerRoleId();
	}

	@Override
	public List<Long> getResourceBlockIds(long companyId, long groupId, long userId, String name, String actionId) {
		return _permissionChecker.getResourceBlockIds(companyId, groupId, userId, name, actionId);
	}

	@Override
	public long[] getRoleIds(long userId, long groupId) {
		return _permissionChecker.getRoleIds(userId, groupId);
	}

	@Override
	public User getUser() {
		return _permissionChecker.getUser();
	}

	@Override
	public long getUserId() {
		return _permissionChecker.getUserId();
	}

	@Override
	public boolean hasOwnerPermission(long companyId, String name, long primKey, long ownerId, String actionId) {
		return true;
	}

	@Override
	public boolean hasOwnerPermission(long companyId, String name, String primKey, long ownerId, String actionId) {
		return true;
	}

	@Override
	public boolean hasPermission(long groupId, String name, long primKey, String actionId) {
		return true;
	}

	@Override
	public boolean hasPermission(long groupId, String name, String primKey, String actionId) {
		return true;
	}

	@Override
	public boolean hasUserPermission(long groupId, String name, String primKey, String actionId, boolean checkAdmin) {
		return true;
	}

	@Override
	public void init(User user) {
	}

	@Override
	public boolean isCheckGuest() {
		return false;
	}

	@Override
	public boolean isCommunityAdmin(long groupId) {
		return true;
	}

	@Override
	public boolean isCommunityOwner(long groupId) {
		return true;
	}

	@Override
	public boolean isCompanyAdmin() {
		return true;
	}

	@Override
	public boolean isCompanyAdmin(long companyId) {
		return true;
	}

	@Override
	public boolean isContentReviewer(long companyId, long groupId) {
		return true;
	}

	@Override
	public boolean isGroupAdmin(long groupId) {
		return true;
	}

	@Override
	public boolean isGroupMember(long groupId) {
		return true;
	}

	@Override
	public boolean isGroupOwner(long groupId) {
		return true;
	}

	@Override
	public boolean isOmniadmin() {
		return true;
	}

	@Override
	public boolean isOrganizationAdmin(long organizationId) {
		return true;
	}

	@Override
	public boolean isOrganizationOwner(long organizationId) {
		return true;
	}

	@Override
	public boolean isSignedIn() {
		return true;
	}

	@Override
	public void resetValues() {
	}

	@Override
	public void setValues(PortletRequest portletRequest) {
	}
}
