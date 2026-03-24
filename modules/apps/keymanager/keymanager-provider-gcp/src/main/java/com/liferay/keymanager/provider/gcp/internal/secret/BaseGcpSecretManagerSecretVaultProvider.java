/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.keymanager.provider.gcp.internal.secret;

import com.google.api.gax.rpc.NotFoundException;
import com.google.cloud.secretmanager.v1.AccessSecretVersionResponse;
import com.google.cloud.secretmanager.v1.AddSecretVersionRequest;
import com.google.cloud.secretmanager.v1.CreateSecretRequest;
import com.google.cloud.secretmanager.v1.CustomerManagedEncryption;
import com.google.cloud.secretmanager.v1.DeleteSecretRequest;
import com.google.cloud.secretmanager.v1.GetSecretRequest;
import com.google.cloud.secretmanager.v1.ListSecretsRequest;
import com.google.cloud.secretmanager.v1.ProjectName;
import com.google.cloud.secretmanager.v1.Replication;
import com.google.cloud.secretmanager.v1.Secret;
import com.google.cloud.secretmanager.v1.SecretManagerServiceClient;
import com.google.cloud.secretmanager.v1.SecretManagerServiceSettings;
import com.google.cloud.secretmanager.v1.SecretName;
import com.google.cloud.secretmanager.v1.SecretPayload;
import com.google.cloud.secretmanager.v1.SecretVersionName;
import com.google.protobuf.ByteString;

import com.liferay.keymanager.KeyReference;
import com.liferay.keymanager.provider.gcp.internal.configuration.GcpSecretManagerCompanySecretVaultProviderConfiguration;
import com.liferay.keymanager.provider.gcp.internal.configuration.GcpSecretManagerSystemSecretVaultProviderConfiguration;
import com.liferay.keymanager.provider.gcp.internal.util.GcpClientManager;
import com.liferay.keymanager.secret.SecretManager;
import com.liferay.keymanager.secret.SecretManagerException;
import com.liferay.keymanager.secret.SecureSecret;
import com.liferay.keymanager.spi.secret.SecretVaultProvider;
import com.liferay.keymanager.spi.secret.SecretVaultReader;
import com.liferay.keymanager.spi.secret.SecretVaultWriter;
import com.liferay.keymanager.util.GcpAliasUtil;
import com.liferay.osgi.util.configuration.ConfigurationFactoryUtil;
import com.liferay.portal.configuration.metatype.bnd.util.ConfigurableUtil;
import com.liferay.portal.kernel.instance.PortalInstancePool;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.service.CompanyLocalService;
import com.liferay.portal.kernel.util.ArrayUtil;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.kernel.util.Validator;

import java.io.IOException;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import org.osgi.service.component.annotations.Deactivate;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Tomas Polesovsky
 */
public abstract class BaseGcpSecretManagerSecretVaultProvider
	implements SecretVaultProvider, SecretVaultReader, SecretVaultWriter {

	@Override
	public void deleteSecret(long companyId, String identifier)
		throws SecretManagerException {

		_checkPermission(companyId);

		try {
			gcpClientManager.execute(
				companyId,
				client -> {
					DeleteSecretRequest request =
						DeleteSecretRequest.newBuilder(
						).setName(
							SecretName.of(
								_projectId, _getSecretId(identifier)
							).toString()
						).build();

					client.deleteSecret(request);

					return null;
				});
		}
		catch (Exception exception) {
			throw new SecretManagerException(
				"Unable to delete GCP secret: " + identifier, exception);
		}
	}

	@Override
	public SecureSecret getSecret(long companyId, String identifier)
		throws SecretManagerException {

		_checkPermission(companyId);

		try {
			return gcpClientManager.execute(
				companyId,
				client -> {
					String name = SecretVersionName.of(
						_projectId, _getSecretId(identifier),
						_getVersionId(identifier)
					).toString();

					AccessSecretVersionResponse response =
						client.accessSecretVersion(name);

					byte[] bytes = response.getPayload(
					).getData(
					).toByteArray();

					try {
						return new SecureSecret(
							new KeyReference(
								KeyReference.Type.SECRET,
								KeyReference.ANY_PROVIDER, identifier),
							bytes);
					}
					finally {
						if (bytes != null) {
							Arrays.fill(bytes, (byte)0);
						}
					}
				});
		}
		catch (Exception exception) {
			throw new SecretManagerException(
				"Unable to fetch GCP secret: " + identifier, exception);
		}
	}

	@Override
	public List<String> getSecretIdentifiers(long companyId)
		throws SecretManagerException {

		_checkPermission(companyId);

		try {
			return gcpClientManager.execute(
				companyId,
				client -> {
					List<String> identifiers = new ArrayList<>();

					ListSecretsRequest request = ListSecretsRequest.newBuilder(
					).setParent(
						ProjectName.of(
							_projectId
						).toString()
					).build();

					SecretManagerServiceClient.ListSecretsPagedResponse
						response = client.listSecrets(request);

					for (Secret secret : response.iterateAll()) {
						String name = secret.getName();

						identifiers.add(
							name.substring(name.lastIndexOf('/') + 1));
					}

					return identifiers;
				});
		}
		catch (Exception exception) {
			throw new SecretManagerException(
				"Unable to list GCP secrets", exception);
		}
	}

	@Override
	public boolean isAllowedCompany(long companyId) {
		if (!_enabled) {
			return false;
		}

		if ((companyId == 0) || (_companyId == companyId)) {
			return true;
		}

		return false;
	}

	@Override
	public void putSecret(long companyId, SecureSecret secureSecret)
		throws SecretManagerException {

		_checkPermission(companyId);

		try {
			gcpClientManager.execute(
				companyId,
				client -> {
					KeyReference keyReference = secureSecret.getKeyReference();

					String secretId = _getSecretId(
						keyReference.getIdentifier());

					String secretPath = SecretName.of(
						_projectId, secretId
					).toString();

					// 1. Ensure secret exists

					try {
						GetSecretRequest getSecretRequest =
							GetSecretRequest.newBuilder(
							).setName(
								secretPath
							).build();

						client.getSecret(getSecretRequest);
					}
					catch (NotFoundException notFoundException) {
						if (_log.isDebugEnabled()) {
							_log.debug(
								"Secret not found, creating a new one: " +
									secretPath,
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

						secretBuilder.setReplication(
							replicationBuilder.build());

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
								ProjectName.of(
									_projectId
								).toString()
							).setSecretId(
								secretId
							).setSecret(
								secretBuilder.build()
							).build();

						client.createSecret(createSecretRequest);
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

					client.addSecretVersion(addSecretVersionRequest);

					return null;
				});
		}
		catch (Exception exception) {
			KeyReference keyReference = secureSecret.getKeyReference();

			String msg =
				"Unable to put GCP secret: " + keyReference.getIdentifier();

			throw new SecretManagerException(msg, exception);
		}
	}

	protected void activate(Map<String, Object> properties) throws IOException {
		String gcpAuthKeyReference = null;
		GcpClientManager.AuthType authType = null;
		String impersonatedServiceAccount = null;

		if (GetterUtil.getBoolean(properties.get("systemScope"))) {
			GcpSecretManagerSystemSecretVaultProviderConfiguration
				gcpSecretManagerSystemSecretVaultProviderConfiguration =
					ConfigurableUtil.createConfigurable(
						GcpSecretManagerSystemSecretVaultProviderConfiguration.
							class,
						properties);

			_enabled =
				gcpSecretManagerSystemSecretVaultProviderConfiguration.
					enabled();
			_projectId =
				gcpSecretManagerSystemSecretVaultProviderConfiguration.
					projectId();
			_kmsKeyName =
				gcpSecretManagerSystemSecretVaultProviderConfiguration.
					kmsKeyName();
			_locations =
				gcpSecretManagerSystemSecretVaultProviderConfiguration.
					locations();

			gcpAuthKeyReference =
				gcpSecretManagerSystemSecretVaultProviderConfiguration.
					gcpServiceAccountKey();
			authType = GcpClientManager.AuthType.create(
				gcpSecretManagerSystemSecretVaultProviderConfiguration.
					gcpAuthType());
			impersonatedServiceAccount =
				gcpSecretManagerSystemSecretVaultProviderConfiguration.
					gcpImpersonatedServiceAccount();

			_companyId = PortalInstancePool.getDefaultCompanyId();
		}
		else {
			GcpSecretManagerCompanySecretVaultProviderConfiguration
				gcpSecretManagerCompanySecretVaultProviderConfiguration =
					ConfigurableUtil.createConfigurable(
						GcpSecretManagerCompanySecretVaultProviderConfiguration.
							class,
						properties);

			_enabled = true;
			_companyId = ConfigurationFactoryUtil.getCompanyId(
				companyLocalService, properties);
			_projectId =
				gcpSecretManagerCompanySecretVaultProviderConfiguration.
					projectId();
			_kmsKeyName =
				gcpSecretManagerCompanySecretVaultProviderConfiguration.
					kmsKeyName();
			_locations =
				gcpSecretManagerCompanySecretVaultProviderConfiguration.
					locations();

			gcpAuthKeyReference =
				gcpSecretManagerCompanySecretVaultProviderConfiguration.
					gcpServiceAccountKey();
			authType = GcpClientManager.AuthType.SA_KEY;
		}

		if (gcpClientManager == null) {
			gcpClientManager = new GcpClientManager<>(
				secretManager, gcpAuthKeyReference, authType,
				impersonatedServiceAccount,
				fixedCredentialsProvider -> SecretManagerServiceClient.create(
					SecretManagerServiceSettings.newBuilder(
					).setCredentialsProvider(
						fixedCredentialsProvider
					).build()));
		}
		else {
			gcpClientManager.updateConfiguration(
				gcpAuthKeyReference, authType, impersonatedServiceAccount);
		}

		try {
			gcpClientManager.execute(
				_companyId,
				client -> {
					client.listSecrets(ProjectName.of(_projectId));

					return null;
				});
		}
		catch (Exception exception) {
			if (GetterUtil.getBoolean(
					System.getenv("LIFERAY_KEYMANAGER_FIPS_ENFORCED"))) {

				throw new RuntimeException(
					"Remediation hint: ensure " +
						"roles/secretmanager.secretAccessor is configured",
					exception);
			}
		}
	}

	@Deactivate
	protected void deactivate() {
		if (gcpClientManager != null) {
			gcpClientManager.close();
		}
	}

	@Reference
	protected CompanyLocalService companyLocalService;

	protected GcpClientManager<SecretManagerServiceClient> gcpClientManager;

	@Reference
	protected SecretManager secretManager;

	private void _checkPermission(long companyId)
		throws SecretManagerException {

		if (!isAllowedCompany(companyId)) {
			throw new SecretManagerException(
				"Company " + companyId +
					" is not allowed to use this provider");
		}
	}

	private String _getSecretId(String identifier) {
		String alias = identifier;

		if (identifier.contains(":")) {
			String[] parts = StringUtil.split(identifier, ":");

			alias = parts[0];
		}

		return GcpAliasUtil.normalize(alias);
	}

	private String _getVersionId(String identifier) {
		if (identifier.contains(":")) {
			String[] parts = StringUtil.split(identifier, ":");

			return parts[1];
		}

		return "latest";
	}

	private static final Log _log = LogFactoryUtil.getLog(
		BaseGcpSecretManagerSecretVaultProvider.class);

	private volatile long _companyId;
	private volatile boolean _enabled;
	private volatile String _kmsKeyName;
	private volatile String[] _locations;
	private volatile String _projectId;

}