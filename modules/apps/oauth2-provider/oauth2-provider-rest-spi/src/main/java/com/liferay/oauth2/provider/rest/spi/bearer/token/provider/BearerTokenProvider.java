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

package com.liferay.oauth2.provider.rest.spi.bearer.token.provider;

/**
 * For manipulating and additional validation of access tokens and refresh
 * tokens to meet specific implementation detail.
 *
 * @author Tomas Polesovsky
 * @review
 */
public interface BearerTokenProvider {

	public default void onBeforeCreate(AccessToken accessToken) {
	}

	public default void onBeforeCreate(RefreshToken refreshToken) {
	}

	public boolean isValid(AccessToken accessToken);

	public default boolean isValid(RefreshToken refreshToken) {
		return true;
	}

}