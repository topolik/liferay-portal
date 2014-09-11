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

import com.liferay.portal.kernel.jsonwebservice.JSONWebService;
import com.liferay.portal.security.ac.AccessControlled;
import com.liferay.token.auth.model.TokenClient;
import com.liferay.token.auth.model.TokenSession;
import org.osgi.service.component.annotations.Component;

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
public class TokenSessionService {

	public boolean isValid(TokenSession tokenSession) {
		if (tokenSession == null) {
			return false;
		}

		TokenClient tokenClient = tokenSession.getTokenClient();

		if (tokenClient == null) {
			return false;
		}

		if(tokenClient.getState().equals(TokenClient.State.REVOKED)) {
			return false;
		}

		return true;
	}
}
