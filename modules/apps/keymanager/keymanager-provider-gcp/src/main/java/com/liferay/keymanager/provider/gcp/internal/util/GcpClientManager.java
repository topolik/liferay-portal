/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.keymanager.provider.gcp.internal.util;

import com.google.api.gax.core.FixedCredentialsProvider;
import com.google.api.gax.rpc.ApiException;
import com.google.api.gax.rpc.StatusCode;
import com.google.auth.oauth2.AccessToken;
import com.google.auth.oauth2.GoogleCredentials;

import com.liferay.keymanager.KeyReference;
import com.liferay.keymanager.secret.SecretManager;
import com.liferay.keymanager.secret.SecureSecret;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.Validator;

import java.io.ByteArrayInputStream;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author Tomas Polesovsky
 */
public class GcpClientManager<T extends AutoCloseable> {

	public GcpClientManager(
		SecretManager secretManager, String gcpAuthKeyReference,
		ClientFactory<T> clientFactory) {

		_secretManager = secretManager;
		_gcpAuthKeyReference = gcpAuthKeyReference;
		_clientFactory = clientFactory;
	}

	public synchronized void close() {
		_closeClient();

		_tokenExpirationTime = 0;
	}

	public <R> R execute(long companyId, GcpOperation<T, R> gcpOperation)
		throws Exception {

		T client = _getClient(companyId);

		try {
			return gcpOperation.execute(client);
		}
		catch (ApiException apiException) {
			StatusCode.Code statusCodeCode = apiException.getStatusCode(
			).getCode();

			if (statusCodeCode == StatusCode.Code.UNAUTHENTICATED) {
				close();

				client = _getClient(companyId);

				return gcpOperation.execute(client);
			}

			throw apiException;
		}
	}

	public void setGcpAuthKeyReference(String gcpAuthKeyReference) {
		if (Objects.equals(_gcpAuthKeyReference, gcpAuthKeyReference)) {
			return;
		}

		_gcpAuthKeyReference = gcpAuthKeyReference;

		close();
	}

	@FunctionalInterface
	public interface ClientFactory<T> {

		public T create(FixedCredentialsProvider fixedCredentialsProvider)
			throws Exception;

	}

	@FunctionalInterface
	public interface GcpOperation<T, R> {

		public R execute(T client) throws Exception;

	}

	private void _closeClient() {
		if (_client != null) {
			try {
				_client.close();
			}
			catch (Exception exception) {
				if (_log.isDebugEnabled()) {
					_log.debug(exception);
				}
			}

			_client = null;
		}
	}

	private T _getClient(long companyId) throws Exception {
		long currentTime = System.currentTimeMillis();

		T client = _client;

		if ((client != null) &&
			(currentTime < (_tokenExpirationTime - 60000))) {

			return client;
		}

		synchronized (this) {
			if ((_client != null) &&
				(currentTime < (_tokenExpirationTime - 60000))) {

				return _client;
			}

			GoogleCredentials credentials = _getGoogleCredentials(companyId);

			_closeClient();

			_client = _clientFactory.create(
				FixedCredentialsProvider.create(credentials));

			return _client;
		}
	}

	private GoogleCredentials _getGoogleCredentials(long companyId)
		throws Exception {

		String gcpAuthKeyReference = _gcpAuthKeyReference;

		if (Validator.isNull(gcpAuthKeyReference)) {
			return GoogleCredentials.getApplicationDefault();
		}

		try (SecureSecret secureSecret = _secretManager.getSecret(
				companyId, KeyReference.fromString(gcpAuthKeyReference))) {

			byte[] bytes = secureSecret.getBytes();

			if (!_isJson(bytes)) {
				throw new IllegalArgumentException(
					"GCP Auth material must be a JSON blob");
			}

			Map<String, String> map = _parseJson(bytes);

			String accessTokenString = map.get("access_token");

			if (Validator.isNull(accessTokenString)) {
				accessTokenString = map.get("accessToken");
			}

			String expiresAtStr = map.get("expires_at");

			if (Validator.isNotNull(accessTokenString) &&
				Validator.isNotNull(expiresAtStr)) {

				AccessToken accessToken = new AccessToken(
					accessTokenString,
					new Date(GetterUtil.getLong(expiresAtStr)));

				_tokenExpirationTime = GetterUtil.getLong(expiresAtStr);

				return GoogleCredentials.create(accessToken);
			}

			String expiresInStr = map.get("expires_in");

			if (Validator.isNotNull(accessTokenString) &&
				Validator.isNotNull(expiresInStr)) {

				long expiresIn = GetterUtil.getLong(expiresInStr);

				long issuedAt = GetterUtil.getLong(
					map.get("issued_at"), System.currentTimeMillis());

				long expiresAt = issuedAt + (expiresIn * 1000);

				AccessToken accessToken = new AccessToken(
					accessTokenString, new Date(expiresAt));

				_tokenExpirationTime = expiresAt;

				return GoogleCredentials.create(accessToken);
			}

			_tokenExpirationTime = Long.MAX_VALUE;

			return GoogleCredentials.fromStream(
				new ByteArrayInputStream(bytes));
		}
	}

	private boolean _isJson(byte[] bytes) {
		for (byte b : bytes) {
			if (Character.isWhitespace(b)) {
				continue;
			}

			if (b == '{') {
				return true;
			}

			return false;
		}

		return false;
	}

	private Map<String, String> _parseJson(byte[] bytes) {
		Map<String, String> map = new HashMap<>();

		String json = new String(bytes);

		Matcher matcher = _jsonPattern.matcher(json);

		while (matcher.find()) {
			map.put(matcher.group(1), matcher.group(2));
		}

		return map;
	}

	private static final Log _log = LogFactoryUtil.getLog(
		GcpClientManager.class);

	private static final Pattern _jsonPattern = Pattern.compile(
		"\"(\\w+)\"\\s*:\\s*\"?([^\",}]+)\"?");

	private T _client;
	private final ClientFactory<T> _clientFactory;
	private volatile String _gcpAuthKeyReference;
	private final SecretManager _secretManager;
	private volatile long _tokenExpirationTime;

}