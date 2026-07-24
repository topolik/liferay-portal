/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.object.internal.action.executor;

import com.liferay.object.action.executor.ObjectActionExecutor;
import com.liferay.object.constants.ObjectActionExecutorConstants;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.json.JSONObject;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.servlet.HttpHeaders;
import com.liferay.portal.kernel.util.ContentTypes;
import com.liferay.portal.kernel.util.Http;
import com.liferay.portal.kernel.util.InetAddressUtil;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.kernel.util.UnicodeProperties;
import com.liferay.portal.kernel.util.Validator;

import java.net.InetAddress;
import java.net.URI;
import java.net.UnknownHostException;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Brian Wing Shun Chan
 */
@Component(service = ObjectActionExecutor.class)
public class WebhookObjectActionExecutorImpl implements ObjectActionExecutor {

	@Override
	public void execute(
			long companyId, long objectActionId,
			UnicodeProperties parametersUnicodeProperties,
			JSONObject payloadJSONObject, long userId)
		throws Exception {

		String url = parametersUnicodeProperties.get("url");

		if (!_isValidWebhookURL(url)) {
			return;
		}

		Http.Options options = new Http.Options();

		options.addHeader(
			HttpHeaders.CONTENT_TYPE, ContentTypes.APPLICATION_JSON);
		options.addHeader(
			"x-api-key", parametersUnicodeProperties.get("secret"));
		options.setBody(
			payloadJSONObject.toString(), ContentTypes.APPLICATION_JSON,
			StringPool.UTF8);
		options.setFollowRedirects(false);
		options.setLocation(url);
		options.setPost(true);

		_http.URLtoString(options);
	}

	@Override
	public String getKey() {
		return ObjectActionExecutorConstants.KEY_WEBHOOK;
	}

	private boolean _isValidWebhookURL(String url) {
		if (Validator.isBlank(url)) {
			if (_log.isWarnEnabled()) {
				_log.warn("Rejected webhook URL that is blank");
			}

			return false;
		}

		URI uri = null;

		try {
			uri = new URI(url);
		}
		catch (Exception exception) {
			if (_log.isWarnEnabled()) {
				_log.warn("Rejected malformed webhook URL: " + url);
			}

			return false;
		}

		String scheme = uri.getScheme();

		if ((scheme == null) ||
			(!StringUtil.equalsIgnoreCase(scheme, "http") &&
			 !StringUtil.equalsIgnoreCase(scheme, "https"))) {

			if (_log.isWarnEnabled()) {
				_log.warn(
					"Rejected webhook URL with disallowed scheme: " + url);
			}

			return false;
		}

		String host = uri.getHost();

		if (Validator.isBlank(host)) {
			if (_log.isWarnEnabled()) {
				_log.warn("Rejected webhook URL without host: " + url);
			}

			return false;
		}

		try {
			for (InetAddress inetAddress : InetAddress.getAllByName(host)) {
				if (InetAddressUtil.isLocalInetAddress(inetAddress)) {
					if (_log.isWarnEnabled()) {
						_log.warn(
							"Rejected webhook URL resolving to a restricted " +
								"address: " + url);
					}

					return false;
				}
			}
		}
		catch (UnknownHostException unknownHostException) {
			if (_log.isWarnEnabled()) {
				_log.warn(
					"Rejected webhook URL with unresolvable host: " + url);
			}

			return false;
		}

		return true;
	}

	@Reference
	private Http _http;

	private static final Log _log = LogFactoryUtil.getLog(
		WebhookObjectActionExecutorImpl.class);

}
