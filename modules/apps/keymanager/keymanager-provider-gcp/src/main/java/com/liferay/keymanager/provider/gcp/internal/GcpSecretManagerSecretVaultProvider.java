/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.keymanager.provider.gcp.internal;

import com.google.api.gax.rpc.NotFoundException;
import com.google.cloud.secretmanager.v1.AccessSecretVersionResponse;
import com.google.cloud.secretmanager.v1.AddSecretVersionRequest;
import com.google.cloud.secretmanager.v1.CreateSecretRequest;
import com.google.cloud.secretmanager.v1.CustomerManagedEncryption;
import com.google.cloud.secretmanager.v1.DeleteSecretRequest;
import com.google.cloud.secretmanager.v1.GetSecretRequest;
import com.google.cloud.secretmanager.v1.ListSecretsRequest;
import com.google.cloud.secretmanager.v1.Replication;
import com.google.cloud.secretmanager.v1.Secret;
import com.google.cloud.secretmanager.v1.SecretManagerServiceClient;
import com.google.cloud.secretmanager.v1.SecretPayload;
import com.google.protobuf.ByteString;

import com.liferay.keymanager.KeyReference;
import com.liferay.keymanager.provider.gcp.internal.configuration.GcpSecretManagerSecretVaultProviderConfiguration;
import com.liferay.keymanager.secret.SecretManagerException;
import com.liferay.keymanager.secret.SecureSecret;
import com.liferay.keymanager.spi.secret.SecretVaultProvider;
import com.liferay.petra.string.StringBundler;
import com.liferay.portal.configuration.metatype.bnd.util.ConfigurableUtil;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.util.ArrayUtil;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.kernel.util.Validator;

import java.io.IOException;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.ConfigurationPolicy;
import org.osgi.service.component.annotations.Deactivate;
import org.osgi.service.component.annotations.Modified;

/**
 * @author Tomas Polesovsky
 */
@Component(
	configurationPid = "com.liferay.keymanager.provider.gcp.internal.configuration.GcpSecretManagerSecretVaultProviderConfiguration",
	configurationPolicy = ConfigurationPolicy.REQUIRE,
	service = SecretVaultProvider.class
)
public class GcpSecretManagerSecretVaultProvider
	implements SecretVaultProvider {

	@Override
	public void deleteSecret(String identifier) throws SecretManagerException {
		try {
			DeleteSecretRequest request = DeleteSecretRequest.newBuilder(
			).setName(
				StringBundler.concat(
					"projects/", _projectId, "/secrets/",
					_getSecretId(identifier))
			).build();

			_client.deleteSecret(request);
		}
		catch (Exception exception) {
			throw new SecretManagerException(
				"Unable to delete GCP secret: " + identifier, exception);
		}
	}

	@Override
	public SecureSecret getSecret(String identifier)
		throws SecretManagerException {

		try {
			String name = StringBundler.concat(
				"projects/", _projectId, "/secrets/", _getSecretId(identifier),
				"/versions/", _getVersionId(identifier));

			AccessSecretVersionResponse response = _client.accessSecretVersion(
				name);

			byte[] bytes = response.getPayload(
			).getData(
			).toByteArray();

			try {
				return new SecureSecret(
					new KeyReference(
						KeyReference.Type.SECRET, _providerId, identifier),
					bytes);
			}
			finally {
				Arrays.fill(bytes, (byte)0);
			}
		}
		catch (Exception exception) {
			throw new SecretManagerException(
				"Unable to get GCP secret: " + identifier, exception);
		}
	}

	@Override
	public List<String> getSecretIdentifiers() throws SecretManagerException {
		try {
			List<String> identifiers = new ArrayList<>();

			ListSecretsRequest request = ListSecretsRequest.newBuilder(
			).setParent(
				"projects/" + _projectId
			).build();

			SecretManagerServiceClient.ListSecretsPagedResponse response =
				_client.listSecrets(request);

			for (Secret secret : response.iterateAll()) {
				String name = secret.getName();

				// Strip the prefix "projects/.../secrets/"

				identifiers.add(name.substring(name.lastIndexOf('/') + 1));
			}

			return identifiers;
		}
		catch (Exception exception) {
			throw new SecretManagerException(
				"Unable to list GCP secrets", exception);
		}
	}

	@Override
	public void putSecret(SecureSecret secureSecret)
		throws SecretManagerException {

		try {
			String secretId = _getSecretId(
				secureSecret.getKeyReference(
				).getIdentifier());

			String secretPath = StringBundler.concat(
				"projects/", _projectId, "/secrets/", secretId);

			// 1. Ensure secret exists

			try {
				GetSecretRequest getSecretRequest = GetSecretRequest.newBuilder(
				).setName(
					secretPath
				).build();

				_client.getSecret(getSecretRequest);
			}
			catch (NotFoundException notFoundException) {
				if (_log.isDebugEnabled()) {
					_log.debug(
						"Secret not found, creating a new one: " + secretPath,
						notFoundException);
				}

				Secret.Builder secretBuilder = Secret.newBuilder();

				// Configure Replication

				Replication.Builder replicationBuilder =
					Replication.newBuilder();

				if (ArrayUtil.isEmpty(_locations)) {
					replicationBuilder.setAutomatic(
						Replication.Automatic.newBuilder(
						).build());
				}
				else {
					Replication.UserManaged.Builder userManagedBuilder =
						Replication.UserManaged.newBuilder();

					for (String location : _locations) {
						userManagedBuilder.addReplicas(
							Replication.UserManaged.Replica.newBuilder(
							).setLocation(
								location
							).build());
					}

					replicationBuilder.setUserManaged(
						userManagedBuilder.build());
				}

				secretBuilder.setReplication(replicationBuilder.build());

				// Configure CMEK

				if (Validator.isNotNull(_kmsKeyName)) {
					secretBuilder.setCustomerManagedEncryption(
						CustomerManagedEncryption.newBuilder(
						).setKmsKeyName(
							_kmsKeyName
						).build());
				}

				CreateSecretRequest createSecretRequest =
					CreateSecretRequest.newBuilder(
					).setParent(
						"projects/" + _projectId
					).setSecretId(
						secretId
					).setSecret(
						secretBuilder.build()
					).build();

				_client.createSecret(createSecretRequest);
			}

			// 2. Add new version

			AddSecretVersionRequest addSecretVersionRequest =
				AddSecretVersionRequest.newBuilder(
				).setParent(
					secretPath
				).setPayload(
					SecretPayload.newBuilder(
					).setData(
						ByteString.copyFrom(secureSecret.getBytes())
					).build()
				).build();

			_client.addSecretVersion(addSecretVersionRequest);
		}
		catch (Exception exception) {
			KeyReference keyReference = secureSecret.getKeyReference();

			String msg =
				"Unable to put GCP secret: " + keyReference.getIdentifier();

			throw new SecretManagerException(msg, exception);
		}
	}

	@Activate
	@Modified
	protected void activate(Map<String, Object> properties) throws IOException {
		GcpSecretManagerSecretVaultProviderConfiguration
			gcpSecretManagerSecretVaultProviderConfiguration =
				ConfigurableUtil.createConfigurable(
					GcpSecretManagerSecretVaultProviderConfiguration.class,
					properties);

		_providerId =
			gcpSecretManagerSecretVaultProviderConfiguration.providerId();
		_projectId =
			gcpSecretManagerSecretVaultProviderConfiguration.projectId();
		_kmsKeyName =
			gcpSecretManagerSecretVaultProviderConfiguration.kmsKeyName();
		_locations =
			gcpSecretManagerSecretVaultProviderConfiguration.locations();

		_client = SecretManagerServiceClient.create();
	}

	@Deactivate
	protected void deactivate() {
		if (_client != null) {
			_client.close();
		}
	}

	private String _getSecretId(String identifier) {
		if (identifier.contains(":")) {
			String[] parts = StringUtil.split(identifier, ":");

			return parts[0];
		}

		return identifier;
	}

	private String _getVersionId(String identifier) {
		if (identifier.contains(":")) {
			String[] parts = StringUtil.split(identifier, ":");

			return parts[1];
		}

		return "latest";
	}

	private static final Log _log = LogFactoryUtil.getLog(
		GcpSecretManagerSecretVaultProvider.class);

	private volatile SecretManagerServiceClient _client;
	private volatile String _kmsKeyName;
	private volatile String[] _locations;
	private volatile String _projectId;
	private volatile String _providerId;

}