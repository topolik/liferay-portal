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
import com.liferay.portal.kernel.util.Validator;
import com.liferay.token.auth.model.TokenClient;
import com.liferay.token.auth.model.TokenSession;
import com.liferay.token.auth.service.TokenClientService;
import com.liferay.token.auth.service.TokenSessionService;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Tomas Polesovsky
 */
@Component(
	immediate = true,
	property = {
		"json.web.service.path=TokenSessionService"
	},
	service = TokenSessionService.class
)
@JSONWebService
public class TokenSessionServiceImpl implements TokenSessionService {

	@Override
	public boolean isValid(TokenSession tokenSession) throws PortalException {
		if (tokenSession == null) {
			return false;
		}

		// TODO: load tokenClient from DB - don't trust input
		String tokenClientId = tokenSession.getTokenClientId();
		if (Validator.isNull(tokenClientId)) {
			return false;
		}

		if (_tokenClientService.isRevoked(tokenClientId)) {
			return false;
		}

		return true;
	}

	@Override
	public void create(TokenSession tokenSession) {
		_sessions.add(tokenSession);
	}

	@Reference(unbind = "-")
	protected void setTokenClientService(TokenClientService tokenClientService) {
		this._tokenClientService = tokenClientService;
	}

	private static List<TokenSession> _sessions = new ArrayList<TokenSession>();
	private TokenClientService _tokenClientService;
}
