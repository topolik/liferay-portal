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

package com.liferay.portal.kernel.security;

import com.liferay.portal.kernel.util.AutoResetThreadLocal;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.Http;
import com.liferay.portal.kernel.util.PropsKeys;
import com.liferay.portal.kernel.util.PropsUtil;
import com.liferay.portal.kernel.util.StringUtil;

/**
 * @author Carlos Sierra Andrés
 */
public class HttpsThreadLocal {

	public static boolean isHttpsSupported() {
		return _HTTPS_SUPPORTED;
	}

	public static void setSecure(boolean secure) {
		_secure.set(secure);
	}

	public static boolean isSecure() {
		return _secure.get();
	}

	private static final boolean _HTTPS_SUPPORTED = StringUtil.equalsIgnoreCase(
		Http.HTTPS, PropsUtil.get(PropsKeys.WEB_SERVER_PROTOCOL)) ||
	GetterUtil.getBoolean(
		PropsUtil.get(PropsKeys.WEBDAV_SERVLET_HTTPS_REQUIRED)) ||
	GetterUtil.getBoolean(
		PropsUtil.get(PropsKeys.COMPANY_SECURITY_AUTH_REQUIRES_HTTPS));

	private static final AutoResetThreadLocal<Boolean> _secure =
		new AutoResetThreadLocal<>(
			HttpsThreadLocal.class.getName(), _HTTPS_SUPPORTED);

}