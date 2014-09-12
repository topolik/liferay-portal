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

package com.liferay.token.auth.service;

import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.token.auth.model.TokenClient;

import java.util.List;
import java.util.Properties;

/**
 * @author Tomas Polesovsky
 */
public interface TokenClientService {
	TokenClient add(String name, Properties configuration)
		throws PortalException;

	TokenClient findById(String clientId) throws PortalException;

	List<TokenClient> getAll() throws PortalException;

	List<TokenClient> findByOwnerId(long ownerId)
			throws PortalException;

	boolean isRevoked(String clientId) throws PortalException;

	void remove(String clientId) throws PortalException;

	void revoke(String clientId) throws PortalException;

	void update(TokenClient tokenClient) throws PortalException;
}
