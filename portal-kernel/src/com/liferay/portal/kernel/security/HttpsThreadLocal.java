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
import com.liferay.portal.kernel.util.PropertiesUtil;
import com.liferay.portal.kernel.util.PropsKeys;
import com.liferay.portal.kernel.util.PropsUtil;
import com.liferay.portal.kernel.util.StringPool;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.kernel.util.Validator;

import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

/**
 * @author Carlos Sierra Andrés
 */
public class HttpsThreadLocal {

	public static void clear() {
		_secure.remove();

		_serverName.remove();
	}

	public static boolean isHttpsSupported() {
		return _HTTPS_SUPPORTED;
	}

	public static boolean isHttpsSupported(String serverName) {
		if (Validator.isNotNull(serverName)) {
			Boolean httpsSupported = _httpsPerServerName.get(serverName);

			if (httpsSupported != null) {
				return httpsSupported;
			}
		}

		return isHttpsSupported();
	}

	public static boolean isSecure() {
		return _secure.get();
	}

	public static boolean isSecure(String serverName, boolean secureRequested) {
		if (secureRequested) {
			return true;
		}

		if (Validator.isNotNull(serverName)) {
			if (serverName.equals(_serverName.get())) {
				return isSecure();
			}
		}

		return isHttpsSupported(serverName);
	}

	public static void setSecure(boolean secure) {
		_secure.set(secure);
	}

	public static void setServerName(String serverName) {
		_serverName.set(serverName);
	}

	private static boolean _isHttps(String protocol) {
		return StringUtil.equalsIgnoreCase(Http.HTTPS, protocol);
	}

	private static final boolean _HTTPS_SUPPORTED = _isHttps(
		PropsUtil.get(PropsKeys.WEB_SERVER_PROTOCOL)) ||
													GetterUtil.getBoolean(
		PropsUtil.get(PropsKeys.WEBDAV_SERVLET_HTTPS_REQUIRED)) ||
													GetterUtil.getBoolean(
		PropsUtil.get(PropsKeys.COMPANY_SECURITY_AUTH_REQUIRES_HTTPS));

	private static final Map<String, Boolean> _httpsPerServerName;
	private static final AutoResetThreadLocal<Boolean> _secure =
		new AutoResetThreadLocal<>(
			HttpsThreadLocal.class.getName() + "_https_supported",
			_HTTPS_SUPPORTED);
	private static final AutoResetThreadLocal<String> _serverName =
		new AutoResetThreadLocal<>(
			HttpsThreadLocal.class.getName() + "_serverName");

	static {
		_httpsPerServerName = new HashMap<>();

		Properties properties = PropertiesUtil.getProperties(
			PropsUtil.getProperties(),
			PropsKeys.WEB_SERVER_PROTOCOL + StringPool.PERIOD, true);

		for (Map.Entry<Object, Object> entry : properties.entrySet()) {
			String serverName = (String)entry.getKey();
			String serverProtocol = (String)entry.getValue();

			_httpsPerServerName.put(serverName, _isHttps(serverProtocol));
		}
	}

}