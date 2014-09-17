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

package com.liferay.token.auth.persistence.impl;

import com.liferay.token.auth.model.TokenClient;
import com.liferay.token.auth.persistence.TokenClientPersistence;
import org.osgi.service.component.annotations.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.Vector;

/**
 * @author Tomas Polesovsky
 */
@Component(
	immediate = true,
	service = TokenClientPersistence.class
)
public class TokenClientPersistenceImpl implements TokenClientPersistence {
	@Override
	public TokenClient update(TokenClient tokenClient) {
		TokenClient result = tokenClient;

		String primaryKey = tokenClient.getClientId();

		if(primaryKey == null) {
			result = tokenClient.clone();
			result.setClientId(UUID.randomUUID().toString());
		}
		else {
			_db.remove(findById(primaryKey));
		}

		_db.add(result);

		return result;
	}

	@Override
	public boolean remove(String tokenClientId) {
		TokenClient tokenClient = findById(tokenClientId);

		if(tokenClient != null) {
			_db.remove(tokenClient);
			return true;
		}

		return false;
	}

	@Override
	public TokenClient findById(String clientId) {
		for (TokenClient tokenClient : _db) {
			if (tokenClient.getClientId().equals(clientId)) {
				return tokenClient;
			}
		}

		return null;
	}

	@Override
	public List<TokenClient> findAll() {
		List<TokenClient> result = new ArrayList<TokenClient>(_db.size());
		result.addAll(_db);
		return result;
	}

	@Override
	public List<TokenClient> findByOwnerId(long ownerId) {
		List<TokenClient> result = new ArrayList<TokenClient>();
		for (TokenClient tokenClient : _db) {
			if (tokenClient.getOwnerId() == ownerId) {
				result.add(tokenClient);
			}
		}

		return result;
	}

	private static List<TokenClient> _db = new Vector<TokenClient>();
}
