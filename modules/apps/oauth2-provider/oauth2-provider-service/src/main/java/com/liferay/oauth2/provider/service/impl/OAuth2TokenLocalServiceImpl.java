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

package com.liferay.oauth2.provider.service.impl;

import com.liferay.oauth2.provider.exception.NoSuchOAuth2TokenException;
import com.liferay.oauth2.provider.model.OAuth2Token;
import com.liferay.oauth2.provider.service.base.OAuth2TokenLocalServiceBaseImpl;

import java.util.Collection;

/**
 * The implementation of the o auth2 token local service.
 *
 * <p>
 * All custom service methods should be put in this class. Whenever methods are added, rerun ServiceBuilder to copy their definitions into the {@link com.liferay.oauth2.provider.service.OAuth2TokenLocalService} interface.
 *
 * <p>
 * This is a local service. Methods of this service will not have security checks based on the propagated JAAS credentials because this service can only be accessed from within the same VM.
 * </p>
 *
 * @author Brian Wing Shun Chan
 * @see OAuth2TokenLocalServiceBaseImpl
 * @see com.liferay.oauth2.provider.service.OAuth2TokenLocalServiceUtil
 */
public class OAuth2TokenLocalServiceImpl extends OAuth2TokenLocalServiceBaseImpl {
	/*
	 * NOTE FOR DEVELOPERS:
	 *
	 * Never reference this class directly. Always use {@link com.liferay.oauth2.provider.service.OAuth2TokenLocalServiceUtil} to access the o auth2 token local service.
	 */

	public Collection<OAuth2Token> findByApplicationAndUserName(
		long applicationId, String username) {

		return oAuth2TokenPersistence.findByA_U(applicationId, username);
	}

	@Override
	public Collection<OAuth2Token> findByRefreshToken(String refreshToken) {
		return oAuth2TokenPersistence.findByRefreshToken(refreshToken);
	}

}