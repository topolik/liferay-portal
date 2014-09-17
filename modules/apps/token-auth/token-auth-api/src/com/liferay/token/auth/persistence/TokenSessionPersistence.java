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

import com.liferay.token.auth.model.TokenSession;

import java.util.List;

/**
 * @author Tomas Polesovsky
 */
public interface TokenSessionPersistence {
	public TokenSession add(TokenSession tokenSession);

	public TokenSession update(TokenSession tokenSession);

	public boolean remove(String tokenSessionId);

	public TokenSession findByToken(String token);

	public List<TokenSession> findAll();

	public List<TokenSession> findByOwnerId(long ownerId);

	public List<TokenSession> findByTokenClientId(long tokenClientId);

}
