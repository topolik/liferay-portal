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

import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.security.auth.PrincipalException;
import com.liferay.portal.security.permission.PermissionChecker;
import com.liferay.portal.security.permission.PermissionThreadLocal;
import com.liferay.portal.security.permission.ResourceActionsUtil;
import com.liferay.portal.service.ResourceActionLocalServiceUtil;
import com.liferay.token.auth.model.TokenClient;
import org.osgi.service.component.annotations.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * @author Tomas Polesovsky
 */
@Component(
	immediate = true,
	service = TokenClientPermission.class
)
public class TokenClientPermission {

	public void check(TokenClient tokenClient, String actionId)
		throws PortalException {

		if (!contains(tokenClient, actionId)) {
			throw new PrincipalException();
		}
	}

	public boolean contains(TokenClient tokenClient, String actionId) {
		PermissionChecker permissionChecker =
			PermissionThreadLocal.getPermissionChecker();

		String primaryKey = "0";

		if (tokenClient != null) {
			primaryKey = tokenClient.getClientId();
		}

		return permissionChecker.hasPermission(
			0, _MODEL_NAME, primaryKey, actionId);
	}

	public List<TokenClient> filter(
		List<TokenClient> tokenClients, String action) {

		List<TokenClient> result = new ArrayList(tokenClients.size());

		for (TokenClient tokenClient : tokenClients) {
			if (contains(tokenClient, action)) {
				result.add(tokenClient);
			}
		}

		return result;
	}

// TODO: bug in PortletTracker, _resourceActions.getPortletModelResources
// cannot find any model resources because it's originaly registered with
// WAR separator
//	@Activate
	protected void activate(Map<String, Object> properties) {
		List<String> modelActions =
			ResourceActionsUtil.getModelResourceActions(_MODEL_NAME);

		ResourceActionLocalServiceUtil.checkResourceActions(
			_MODEL_NAME, modelActions);
	}

	private static final String _MODEL_NAME = TokenClient.class.getName();

}
