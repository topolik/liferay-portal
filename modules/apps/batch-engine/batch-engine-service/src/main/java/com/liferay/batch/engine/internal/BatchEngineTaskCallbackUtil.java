/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.batch.engine.internal;

import com.fasterxml.jackson.databind.ObjectMapper;

import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.util.InetAddressUtil;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.kernel.util.Validator;

import java.net.InetAddress;
import java.net.URI;
import java.net.UnknownHostException;

import java.util.Collections;

import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;

/**
 * @author Ivica Cardic
 */
public class BatchEngineTaskCallbackUtil {

	public static void sendCallback(
		String callbackURL, String executeStatus, long id) {

		if (Validator.isBlank(callbackURL)) {
			return;
		}

		if (!_isValidCallbackURL(callbackURL)) {
			return;
		}

		HttpClientBuilder httpClientBuilder = HttpClientBuilder.create();

		try (CloseableHttpClient closeableHttpClient =
				httpClientBuilder.disableRedirectHandling(
				).useSystemProperties(
				).build()) {

			HttpPost httpPost = new HttpPost(callbackURL);

			httpPost.setEntity(
				new StringEntity(
					_objectMapper.writeValueAsString(
						Collections.singletonMap(id, executeStatus)),
					ContentType.APPLICATION_JSON));

			closeableHttpClient.execute(httpPost);
		}
		catch (Exception exception) {
			_log.error(exception);
		}
	}

	private static boolean _isValidCallbackURL(String callbackURL) {
		URI uri = null;

		try {
			uri = new URI(callbackURL);
		}
		catch (Exception exception) {
			if (_log.isWarnEnabled()) {
				_log.warn("Rejected malformed callback URL: " + callbackURL);
			}

			return false;
		}

		String scheme = uri.getScheme();

		if ((scheme == null) ||
			(!StringUtil.equalsIgnoreCase(scheme, "http") &&
			 !StringUtil.equalsIgnoreCase(scheme, "https"))) {

			if (_log.isWarnEnabled()) {
				_log.warn(
					"Rejected callback URL with disallowed scheme: " +
						callbackURL);
			}

			return false;
		}

		String host = uri.getHost();

		if (Validator.isBlank(host)) {
			if (_log.isWarnEnabled()) {
				_log.warn("Rejected callback URL without host: " + callbackURL);
			}

			return false;
		}

		try {
			for (InetAddress inetAddress : InetAddress.getAllByName(host)) {
				if (InetAddressUtil.isLocalInetAddress(inetAddress)) {
					if (_log.isWarnEnabled()) {
						_log.warn(
							"Rejected callback URL resolving to a restricted " +
								"address: " + callbackURL);
					}

					return false;
				}
			}
		}
		catch (UnknownHostException unknownHostException) {
			if (_log.isWarnEnabled()) {
				_log.warn(
					"Rejected callback URL with unresolvable host: " +
						callbackURL);
			}

			return false;
		}

		return true;
	}

	private static final Log _log = LogFactoryUtil.getLog(
		BatchEngineTaskCallbackUtil.class);

	private static final ObjectMapper _objectMapper = new ObjectMapper();

}
