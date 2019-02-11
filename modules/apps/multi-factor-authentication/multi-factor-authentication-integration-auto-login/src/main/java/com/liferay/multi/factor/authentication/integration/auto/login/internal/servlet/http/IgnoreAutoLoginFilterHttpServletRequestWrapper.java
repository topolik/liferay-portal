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

package com.liferay.multi.factor.authentication.integration.auto.login.internal.servlet.http;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;

/**
 * @author Tomas Polesovsky
 */
public class IgnoreAutoLoginFilterHttpServletRequestWrapper
	extends HttpServletRequestWrapper {

	public IgnoreAutoLoginFilterHttpServletRequestWrapper(
		HttpServletRequest request) {

		super(request);
	}

	@Override
	public String getRemoteUser() {
		String remoteUser = super.getRemoteUser();

		if (remoteUser == null) {
			return _TEMP_REMOTE_USER;
		}

		return remoteUser;
	}

	private static final String _TEMP_REMOTE_USER = "TEMP_REMOTE_USER";

}