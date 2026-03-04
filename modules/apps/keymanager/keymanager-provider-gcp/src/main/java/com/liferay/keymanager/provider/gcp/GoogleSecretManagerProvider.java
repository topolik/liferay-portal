/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.keymanager.provider.gcp;

import aQute.bnd.annotation.metatype.Meta;

import com.google.cloud.secretmanager.v1.AccessSecretVersionResponse;
import com.google.cloud.secretmanager.v1.ProjectName;
import com.google.cloud.secretmanager.v1.Replication;
import com.google.cloud.secretmanager.v1.Secret;
import com.google.cloud.secretmanager.v1.SecretManagerServiceClient;
import com.google.cloud.secretmanager.v1.SecretName;
import com.google.cloud.secretmanager.v1.SecretPayload;
import com.google.cloud.secretmanager.v1.SecretVersionName;
import com.google.protobuf.ByteString;

import com.liferay.keymanager.KeyMetadata;
import com.liferay.keymanager.KeyProvider;
import com.liferay.keymanager.exception.KeyProviderException;
import com.liferay.keymanager.provider.gcp.internal.configuration.GoogleSecretManagerProviderConfiguration;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;

import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.ConfigurationPolicy;
import org.osgi.service.component.annotations.Deactivate;
import org.osgi.service.component.annotations.Modified;
import org.osgi.service.metatype.annotations.Designate;

/**
 * @author Tomas Polesovsky
 */
@Component(
	configurationPid = "com.liferay.keymanager.provider.gcp.internal.configuration.GoogleSecretManagerProviderConfiguration",
	configurationPolicy = ConfigurationPolicy.REQUIRE, immediate = true,
	service = KeyProvider.class
)
@Designate(ocd = GoogleSecretManagerProviderConfiguration.class)
public class GoogleSecretManagerProvider implements KeyProvider {

	@Activate
	@Modified
	protected void activate(
		GoogleSecretManagerProviderConfiguration configuration) {

		_providerId = configuration.providerId();
		_displayName = configuration.displayName();
		_projectId = configuration.projectId();
		_enabled = configuration.enabled();

		if (_enabled && !_projectId.isEmpty()) {
			try {
				_client = SecretManagerServiceClient.create();
				_available = true;

				if (_log.isInfoEnabled()) {
					_log.info(
						"Google Secret Manager provider initialized: id=" +
							_providerId + ", project=" + _projectId);
				}
			}
			catch (Exception e) {
				_available = false;

				_log.error(
					"Failed to initialize Google Secret Manager client", e);
			}
		}
		else {
			_available = false;

			_closeClient();
		}
	}

	@Deactivate
	protected void deactivate() {
		_closeClient();
	}

	@Override
	public boolean containsKey(String alias) throws KeyProviderException {
		if (!_available) {
			throw new KeyProviderException("Provider is not available");
		}

		try {
			String secretId = alias;

			int index = alias.indexOf(':');

			if (index > 0) {
				secretId = alias.substring(0, index);
			}

			_client.getSecret(SecretName.of(_projectId, secretId));

			return true;
		}
		catch (Exception e) {
			return false;
		}
	}

	@Override
	public void deleteKey(String alias) throws KeyProviderException {
		if (!_available) {
			throw new KeyProviderException("Provider is not available");
		}

		try {
			String secretId = alias;

			int index = alias.indexOf(':');

			if (index > 0) {
				secretId = alias.substring(0, index);
			}

			_client.deleteSecret(SecretName.of(_projectId, secretId));
		}
		catch (Exception e) {
			throw new KeyProviderException(
				"Failed to delete secret: " + alias, e);
		}
	}

	@Override
	public String getDisplayName() {
		return _displayName;
	}

	@Override
	public KeyMetadata getKeyMetadata(String alias)
		throws KeyProviderException {

		if (!containsKey(alias)) {
			return null;
		}

		return new KeyMetadata.Builder(
		).alias(
			alias
		).provider(
			getProviderId()
		).keyType(
			"SECRET"
		).rotatable(
			true
		).build();
	}

	@Override
	public String getProviderId() {
		return _providerId;
	}

	@Override
	public boolean isAvailable() {
		return _available;
	}

	@Override
	public List<String> listAliases() throws KeyProviderException {
		if (!_available) {
			throw new KeyProviderException("Provider is not available");
		}

		List<String> aliases = new ArrayList<>();

		try {
			SecretManagerServiceClient.ListSecretsPagedResponse response =
				_client.listSecrets(ProjectName.of(_projectId));

			for (Secret secret : response.iterateAll()) {
				String name = secret.getName();

				aliases.add(name.substring(name.lastIndexOf('/') + 1));
			}
		}
		catch (Exception e) {
			throw new KeyProviderException("Failed to list secrets", e);
		}

		return aliases;
	}

	@Override
	public char[] resolveKey(String alias) throws KeyProviderException {
		byte[] bytes = resolveKeyBytes(alias);

		try {
			ByteBuffer byteBuffer = ByteBuffer.wrap(bytes);

			CharBuffer charBuffer = StandardCharsets.UTF_8.decode(byteBuffer);

			char[] result = new char[charBuffer.remaining()];

			charBuffer.get(result);

			return result;
		}
		finally {
			Arrays.fill(bytes, (byte)0);
		}
	}

	@Override
	public byte[] resolveKeyBytes(String alias) throws KeyProviderException {
		if (!_available) {
			throw new KeyProviderException("Provider is not available");
		}

		try {
			String secretId = alias;
			String versionId = "latest";

			int index = alias.indexOf(':');

			if (index > 0) {
				secretId = alias.substring(0, index);
				versionId = alias.substring(index + 1);
			}

			SecretVersionName secretVersionName = SecretVersionName.of(
				_projectId, secretId, versionId);

			AccessSecretVersionResponse response = _client.accessSecretVersion(
				secretVersionName);

			return response.getPayload(
			).getData(
			).toByteArray();
		}
		catch (Exception e) {
			throw new KeyProviderException(
				"Failed to resolve secret: " + alias, e);
		}
	}

	@Override
	public void storeKey(String alias, char[] value)
		throws KeyProviderException {

		if (!_available) {
			throw new KeyProviderException("Provider is not available");
		}

		try {
			String secretId = alias;

			int index = alias.indexOf(':');

			if (index > 0) {
				secretId = alias.substring(0, index);
			}

			SecretName secretName = SecretName.of(_projectId, secretId);

			boolean exists = true;

			try {
				_client.getSecret(secretName);
			}
			catch (Exception e) {
				exists = false;
			}

			if (!exists) {
				Secret secret = Secret.newBuilder(
				).setReplication(
					Replication.newBuilder(
					).setAutomatic(
						Replication.Automatic.newBuilder(
						).build()
					).build()
				).build();

				_client.createSecret(
					ProjectName.of(_projectId), secretId, secret);
			}

			ByteBuffer byteBuffer = StandardCharsets.UTF_8.encode(
				CharBuffer.wrap(value));

			SecretPayload payload = SecretPayload.newBuilder(
			).setData(
				ByteString.copyFrom(byteBuffer)
			).build();

			_client.addSecretVersion(secretName, payload);
		}
		catch (Exception e) {
			throw new KeyProviderException(
				"Failed to store secret: " + alias, e);
		}
	}

	private void _closeClient() {
		if (_client != null) {
			_client.close();

			_client = null;
		}
	}

	private volatile boolean _available = false;
	private SecretManagerServiceClient _client;
	private String _displayName;
	private boolean _enabled;
	private String _projectId;
	private String _providerId;

	private static final Log _log = LogFactoryUtil.getLog(
		GoogleSecretManagerProvider.class);

}
