/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.keymanager.provider.gcp.internal;

import com.google.cloud.secretmanager.v1.AccessSecretVersionResponse;
import com.google.cloud.secretmanager.v1.ProjectName;
import com.google.cloud.secretmanager.v1.Replication;
import com.google.cloud.secretmanager.v1.Secret;
import com.google.cloud.secretmanager.v1.SecretManagerServiceClient;
import com.google.cloud.secretmanager.v1.SecretName;
import com.google.cloud.secretmanager.v1.SecretPayload;
import com.google.cloud.secretmanager.v1.SecretVersionName;
import com.google.protobuf.ByteString;

import com.liferay.keymanager.SecureSecret;
import com.liferay.keymanager.provider.gcp.internal.configuration.GoogleSecretManagerProviderConfiguration;
import com.liferay.keymanager.spi.KeyProvider;
import com.liferay.petra.string.StringBundler;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

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
	configurationPolicy = ConfigurationPolicy.REQUIRE,
	service = KeyProvider.class, property = "service.ranking:Integer=100"
)
@Designate(ocd = GoogleSecretManagerProviderConfiguration.class)
public class GoogleSecretManagerProvider extends BaseGCPKeyProvider {

	@Deactivate
	protected void deactivate() {
		_closeClient();
	}

	@Override
	public boolean containsKey(String alias) throws Exception {
		if (!_available) {
			throw new Exception("Provider is not available");
		}

		String secretId = _extractSecretId(alias);

		try {
			_client.getSecret(SecretName.of(_projectId, secretId));

			return true;
		}
		catch (Exception exception) {
			_log.error("Failed to check if secret exists", exception);

			return false;
		}
	}

	@Override
	public void deleteKey(String alias) throws Exception {
		String secretId = _extractSecretId(alias);

		_client.deleteSecret(SecretName.of(_projectId, secretId));
	}

	@Override
	public Capability[] getCapabilities() {
		return new Capability[] {
			Capability.READ, Capability.WRITE, Capability.DELETE,
			Capability.LIST, Capability.VERSIONING
		};
	}

	@Override
	public int getInitializationPhase() {
		return 2;
	}

	@Override
	public List<String> listAliases() throws Exception {
		if (!_available) {
			throw new Exception("Provider is not available");
		}

		List<String> aliases = new ArrayList<>();

		SecretManagerServiceClient.ListSecretsPagedResponse response =
			_client.listSecrets(ProjectName.of(_projectId));

		for (Secret secret : response.iterateAll()) {
			String name = secret.getName();

			aliases.add(name.substring(name.lastIndexOf('/') + 1));
		}

		return aliases;
	}

	@Override
	public SecureSecret resolveKey(String alias, Map<String, Object> context)
		throws Exception {

		String secretId = _extractSecretId(alias);
		String versionId = _extractVersionId(alias);

		SecretVersionName secretVersionName = SecretVersionName.of(
			_projectId, secretId, versionId);

		AccessSecretVersionResponse response = _client.accessSecretVersion(
			secretVersionName);

		SecretPayload secretPayload = response.getPayload();

		ByteString byteString = secretPayload.getData();

		return new SecureSecret(byteString.toByteArray());
	}

	@Override
	public void storeKey(String alias, SecureSecret secret) throws Exception {
		String secretId = _extractSecretId(alias);

		SecretName secretName = SecretName.of(_projectId, secretId);

		boolean exists = true;

		try {
			_client.getSecret(secretName);
		}
		catch (Exception exception) {
			_log.error("Failed to get secret", exception);

			exists = false;
		}

		if (!exists) {
			Secret gcpSecret = Secret.newBuilder(
			).setReplication(
				Replication.newBuilder(
				).setAutomatic(
					Replication.Automatic.newBuilder(
					).build(
					)
				).build()
			).build();

			_client.createSecret(ProjectName.of(_projectId), secretId, gcpSecret);
		}

		SecretPayload payload = SecretPayload.newBuilder(
		).setData(
			ByteString.copyFrom(secret.getBytes())
		).build();

		_client.addSecretVersion(secretName, payload);
	}

	@Activate
	@Modified
	protected void activate(
		GoogleSecretManagerProviderConfiguration googleSecretManagerProviderConfiguration) {

		_providerId = googleSecretManagerProviderConfiguration.providerId();
		_projectId = googleSecretManagerProviderConfiguration.projectId();
		_enabled = googleSecretManagerProviderConfiguration.enabled();

		if (_enabled && (_projectId != null) && !_projectId.isEmpty()) {
			try {
				// Assumes ADC provider has seeded ambient identity into the
				// environment

				_client = SecretManagerServiceClient.create();

				_available = true;

				if (_log.isInfoEnabled()) {
					_log.info(
						StringBundler.concat(
							"Google Secret Manager initialized: id=",
							_providerId, ", project=", _projectId));
				}
			}
			catch (Exception exception) {
				_available = false;

				if (_log.isErrorEnabled()) {
					_log.error(
						"Failed to initialize Google Secret Manager client",
						exception);
				}
			}
		}
		else {
			_available = false;

			_closeClient();
		}
	}

	private void _closeClient() {
		if (_client != null) {
			_client.close();

			_client = null;
		}
	}

	private String _extractSecretId(String alias) {
		int index = alias.indexOf(':');

		if (index > 0) {
			return alias.substring(0, index);
		}

		return alias;
	}

	private String _extractVersionId(String alias) {
		int index = alias.indexOf(':');

		if (index > 0) {
			return alias.substring(index + 1);
		}

		return "latest";
	}

	private static final Log _log = LogFactoryUtil.getLog(
		GoogleSecretManagerProvider.class);

	private volatile SecretManagerServiceClient _client;

}
