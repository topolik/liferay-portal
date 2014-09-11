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

package com.liferay.token.auth.persistence;

import com.liferay.token.auth.model.TokenClient;
import com.liferay.token.auth.service.TokenClientService;
import org.osgi.service.component.annotations.Component;

import java.util.List;
import java.util.UUID;

/**
 * @author Tomas Polesovsky
 */
@Component(
	immediate = true,
	service = TokenClientPersistence.class
)
public class TokenClientPersistence {
	public TokenClient update(TokenClient tokenClient) {
		TokenClient result = tokenClient;

		String primaryKey = tokenClient.getClientId();

		if(primaryKey == null) {
			result = tokenClient.clone();
			result.setClientId(UUID.randomUUID().toString());
		}

		return result;
	}

	public boolean remove(TokenClient tokenClient) {
		String primaryKey = tokenClient.getClientId();

		return false;
	}

	public TokenClient findById(String clientId) {
		return null;
	}

	public List<TokenClient> findAll() {
		return null;
	}

	public List<TokenClient> findByOwnerId(long ownerId) {
		return null;
	}
}
