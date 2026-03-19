/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.keymanager.provider.gcp.internal.util;

import com.google.api.gax.core.FixedCredentialsProvider;
import com.google.api.gax.rpc.ApiException;
import com.google.api.gax.rpc.StatusCode;
import com.google.auth.oauth2.GoogleCredentials;
import com.google.auth.oauth2.ImpersonatedCredentials;

import com.liferay.keymanager.KeyReference;
import com.liferay.keymanager.secret.SecretManager;
import com.liferay.keymanager.secret.SecureSecret;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.util.Validator;

import java.io.ByteArrayInputStream;

import java.util.Arrays;
import java.util.Collections;
import java.util.Objects;

/**
 * @author Tomas Polesovsky
 */
public class GcpClientManager<T extends AutoCloseable> {

	public enum AuthType {

		ADC("adc"), IMPERSONATION("impersonation"), SA_KEY("sa-key");

		@Override
		public String toString() {
			return _value;
		}

		private AuthType(String value) {
			_value = value;
		}

		private final String _value;

	}

	public GcpClientManager(
		SecretManager secretManager, String gcpAuthKeyReference,
		AuthType authType, String impersonatedServiceAccount,
		ClientFactory<T> clientFactory) {

		_secretManager = secretManager;
		_gcpAuthKeyReference = gcpAuthKeyReference;
		_authType = authType;
		_impersonatedServiceAccount = impersonatedServiceAccount;
		_clientFactory = clientFactory;
	}

	public synchronized void close() {
		_closeClient();
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

	public void updateConfiguration(
		String gcpAuthKeyReference, AuthType authType,
		String impersonatedServiceAccount) {

		if (Objects.equals(_gcpAuthKeyReference, gcpAuthKeyReference) &&
			Objects.equals(_authType, authType) &&
			Objects.equals(
				_impersonatedServiceAccount, impersonatedServiceAccount)) {

			return;
		}

		_gcpAuthKeyReference = gcpAuthKeyReference;
		_authType = authType;
		_impersonatedServiceAccount = impersonatedServiceAccount;

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
		T client = _client;

		if (client != null) {
			return client;
		}

		synchronized (this) {
			if (_client != null) {
				return _client;
			}

			GoogleCredentials credentials = _getGoogleCredentials(companyId);

			_client = _clientFactory.create(
				FixedCredentialsProvider.create(credentials));

			return _client;
		}
	}

	private GoogleCredentials _getGoogleCredentials(long companyId)
		throws Exception {

		if ((_authType == AuthType.IMPERSONATION) &&
			Validator.isNotNull(_impersonatedServiceAccount)) {

			return ImpersonatedCredentials.create(
				GoogleCredentials.getApplicationDefault(),
				_impersonatedServiceAccount, Collections.emptyList(),
				Collections.singletonList(
					"https://www.googleapis.com/auth/cloud-platform"),
				3600);
		}

		if (Validator.isNull(_gcpAuthKeyReference)) {
			return GoogleCredentials.getApplicationDefault();
		}

		try (SecureSecret secureSecret = _secretManager.getSecret(
				companyId, KeyReference.fromString(_gcpAuthKeyReference))) {

			byte[] bytes = secureSecret.getBytes();

			try {
				return GoogleCredentials.fromStream(
					new ByteArrayInputStream(bytes));
			}
			finally {
				Arrays.fill(bytes, (byte)0);
			}
		}
	}

	private static final Log _log = LogFactoryUtil.getLog(
		GcpClientManager.class);

	private volatile T _client;
	private final ClientFactory<T> _clientFactory;
	private volatile String _gcpAuthKeyReference;
	private volatile AuthType _authType;
	private volatile String _impersonatedServiceAccount;
	private final SecretManager _secretManager;

}