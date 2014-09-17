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

package com.liferay.token.auth.service.impl;

import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.jsonwebservice.JSONWebService;
import com.liferay.portal.model.ResourceConstants;
import com.liferay.portal.security.auth.PrincipalException;
import com.liferay.portal.security.permission.PermissionChecker;
import com.liferay.portal.security.permission.PermissionThreadLocal;
import com.liferay.portal.service.ResourceLocalService;
import com.liferay.token.auth.model.TokenClient;
import com.liferay.token.auth.persistence.TokenClientPersistence;
import com.liferay.token.auth.service.TokenClientService;
import com.liferay.token.auth.permission.TokenClientPermission;
import com.liferay.token.auth.util.ActionKeys;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

import java.util.List;
import java.util.Properties;

/**
 * @author Tomas Polesovsky
 */
@Component(
	immediate = true,
	property = {
		"json.web.service.path=TokenClientService"
	},
	service = TokenClientService.class
)
@JSONWebService
public class TokenClientServiceImpl implements TokenClientService {

	@Override
	public TokenClient add(String name, Properties configuration)
		throws PortalException {

		_tokenClientPermission.check(null, ActionKeys.ADD_CLIENT);

		PermissionChecker permissionChecker = getPermissionChecker();

		TokenClient tokenClient = new TokenClient();
		tokenClient.setClientName(name);
		tokenClient.setConfiguration(configuration);
		tokenClient.setCompanyId(permissionChecker.getCompanyId());
		tokenClient.setOwnerId(permissionChecker.getUserId());

		tokenClient = _tokenClientPersistence.update(tokenClient);

		_resourceLocalService.addResources(
			tokenClient.getCompanyId(), 0, tokenClient.getOwnerId(),
			TokenClient.class.getName(), tokenClient.getClientId(), false,
			false, false);

		return tokenClient;
	}

	@Override
	public TokenClient findById(String clientId) throws PortalException {
		TokenClient tokenClient = _tokenClientPersistence.findById(clientId);

		_tokenClientPermission.check(tokenClient, ActionKeys.VIEW);

		return tokenClient;
	}

	@Override
	public List<TokenClient> getAll() throws PortalException {
		List<TokenClient> tokenClients = _tokenClientPersistence.findAll();

		return _tokenClientPermission.filter(tokenClients, ActionKeys.VIEW);
	}

	@Override
	public List<TokenClient> findByOwnerId(long ownerId)
		throws PortalException {

		List<TokenClient> tokenClients = _tokenClientPersistence.findByOwnerId(
			ownerId);

		return _tokenClientPermission.filter(tokenClients, ActionKeys.VIEW);
	}

	@Override
	public boolean isRevoked(String clientId) throws PortalException {
		TokenClient tokenClient = _tokenClientPersistence.findById(clientId);

		if(tokenClient == null) {
			return true;
		}

		_tokenClientPermission.check(tokenClient, ActionKeys.VIEW);

		if (tokenClient.getState().equals(TokenClient.State.REVOKED)) {
			return true;
		}

		return false;
	}

	@Override
	public void remove(String clientId) throws PortalException {
		TokenClient tokenClient = _tokenClientPersistence.findById(clientId);

		_tokenClientPermission.check(tokenClient, ActionKeys.UPDATE);

		_tokenClientPersistence.remove(clientId);

		_resourceLocalService.deleteResource(
			tokenClient.getCompanyId(), TokenClient.class.getName(),
			ResourceConstants.SCOPE_INDIVIDUAL, clientId);
	}

	@Override
	public void revoke(String clientId) throws PortalException {
		TokenClient tokenClient = _tokenClientPersistence.findById(clientId);

		_tokenClientPermission.check(tokenClient, ActionKeys.REVOKE);

		tokenClient.setState(TokenClient.State.REVOKED);

		_tokenClientPersistence.update(tokenClient);
	}

	@Override
	public void update(TokenClient tokenClient) throws PortalException {
		_tokenClientPermission.check(tokenClient, ActionKeys.UPDATE);

		_tokenClientPersistence.update(tokenClient);
	}

	protected PermissionChecker getPermissionChecker() throws PrincipalException {
		PermissionChecker permissionChecker =
			PermissionThreadLocal.getPermissionChecker();

		if (permissionChecker == null) {
			throw new PrincipalException("PermissionChecker not initialized");
		}

		return permissionChecker;
	}

	@Reference
	protected void setTokenClientPersistence(
		TokenClientPersistence tokenClientPersistence) {

		this._tokenClientPersistence = tokenClientPersistence;
	}

	@Reference
	protected void setTokenClientPermission(
		TokenClientPermission tokenClientPermission) {

		this._tokenClientPermission = tokenClientPermission;
	}

	@Reference
	protected void setResourceLocalService(
		ResourceLocalService resourceLocalService) {

		this._resourceLocalService = resourceLocalService;
	}

	private ResourceLocalService _resourceLocalService;
	private TokenClientPersistence _tokenClientPersistence;
	private TokenClientPermission _tokenClientPermission;

}