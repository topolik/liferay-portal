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
import com.liferay.keymanager.provider.gcp.internal.configuration.GcpSecretManagerSecretVaultProviderConfiguration;
import com.liferay.keymanager.provider.gcp.internal.configuration.GcpSecretManagerSystemSecretVaultProviderConfiguration;
import com.liferay.keymanager.provider.gcp.internal.util.GcpClientManager;
import com.liferay.keymanager.secret.SecretManager;
import com.liferay.keymanager.secret.SecretManagerException;
import com.liferay.keymanager.secret.SecureSecret;
import com.liferay.keymanager.spi.secret.SecretVaultProvider;
import com.liferay.keymanager.spi.secret.SecretVaultReader;
import com.liferay.keymanager.spi.secret.SecretVaultWriter;
import com.liferay.osgi.util.configuration.ConfigurationFactoryUtil;
import com.liferay.petra.string.StringBundler;
import com.liferay.portal.configuration.metatype.bnd.util.ConfigurableUtil;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.model.Company;
import com.liferay.portal.kernel.service.CompanyLocalService;
import com.liferay.portal.kernel.util.ArrayUtil;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.kernel.util.Validator;

import java.io.IOException;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Deactivate;
import org.osgi.service.component.annotations.Modified;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Tomas Polesovsky
 */
@Component(
	factory = "com.liferay.keymanager.provider.gcp.internal.GcpSecretManagerSecretVaultProvider",
	property = "providerId=gcp-secret-manager",
	service = {
		SecretVaultProvider.class, SecretVaultReader.class,
		SecretVaultWriter.class
	}
)
public class GcpSecretManagerSecretVaultProvider
	implements SecretVaultProvider, SecretVaultReader, SecretVaultWriter {

	@Override
	public void deleteSecret(long companyId, String identifier)
		throws SecretManagerException {

		try {
			_gcpClientManager.execute(
				companyId,
				client -> {
					DeleteSecretRequest request =
						DeleteSecretRequest.newBuilder(
						).setName(
							SecretName.of(
								_projectId, _getSecretId(identifier)).toString()
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

		try {
			return _gcpClientManager.execute(
				companyId,
				client -> {
					String name = SecretVersionName.of(
						_projectId, _getSecretId(identifier),
						_getVersionId(identifier)).toString();

					AccessSecretVersionResponse response =
						client.accessSecretVersion(name);

					byte[] bytes = response.getPayload(
					).getData(
					).toByteArray();

					return new SecureSecret(
						new KeyReference(
							KeyReference.Type.SECRET, _providerId, identifier),
						bytes);
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

		try {
			return _gcpClientManager.execute(
				companyId,
				client -> {
					List<String> identifiers = new ArrayList<>();

					ListSecretsRequest request = ListSecretsRequest.newBuilder(
					).setParent(ProjectName.of(_projectId).toString()).build();

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
		if (_companyId == companyId) {
			return true;
		}

		return false;
	}

	@Override
	public void putSecret(long companyId, SecureSecret secureSecret)
		throws SecretManagerException {

		try {
			_gcpClientManager.execute(
				companyId,
				client -> {
					KeyReference keyReference = secureSecret.getKeyReference();

					String secretId = _getSecretId(
						keyReference.getIdentifier());

					String secretPath = SecretName.of(
						_projectId, secretId).toString();

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
								ProjectName.of(_projectId).toString()
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

	@Activate
	@Modified
	protected void activate(Map<String, Object> properties) throws IOException {
		String gcpAuthKeyReference = null;
		String authType = null;
		String impersonatedServiceAccount = null;

		if (GetterUtil.getBoolean(properties.get("systemScope"))) {
			GcpSecretManagerSystemSecretVaultProviderConfiguration
				configuration = ConfigurableUtil.createConfigurable(
					GcpSecretManagerSystemSecretVaultProviderConfiguration.class,
					properties);

			_companyId = _getDefaultCompanyId();
			_providerId = configuration.providerId();
			_projectId = configuration.projectId();
			_kmsKeyName = configuration.kmsKeyName();
			_locations = configuration.locations();

			gcpAuthKeyReference = configuration.gcpServiceAccountKey();
			authType = configuration.gcpAuthType();
			impersonatedServiceAccount =
				configuration.gcpImpersonatedServiceAccount();
		}
		else {
			GcpSecretManagerSecretVaultProviderConfiguration configuration =
				ConfigurableUtil.createConfigurable(
					GcpSecretManagerSecretVaultProviderConfiguration.class,
					properties);

			_companyId = ConfigurationFactoryUtil.getCompanyId(
				_companyLocalService, properties);
			_providerId = configuration.providerId();
			_projectId = configuration.projectId();
			_kmsKeyName = configuration.kmsKeyName();
			_locations = configuration.locations();

			gcpAuthKeyReference = configuration.gcpServiceAccountKey();
			authType = "sa-key";
		}

		if (_gcpClientManager == null) {
			_gcpClientManager = new GcpClientManager<>(
				_secretManager, gcpAuthKeyReference, authType,
				impersonatedServiceAccount,
				fixedCredentialsProvider -> SecretManagerServiceClient.create(
					SecretManagerServiceSettings.newBuilder(
					).setCredentialsProvider(
						fixedCredentialsProvider
					).build()));
		}
		else {
			_gcpClientManager.updateConfiguration(
				gcpAuthKeyReference, authType, impersonatedServiceAccount);
		}
	}

	@Deactivate
	protected void deactivate() {
		if (_gcpClientManager != null) {
			_gcpClientManager.close();
		}
	}

	private long _getDefaultCompanyId() {
		List<Company> companies = _companyLocalService.getCompanies();

		if (companies.isEmpty()) {
			return 0;
		}

		return companies.get(0).getCompanyId();
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

	@Reference
	private CompanyLocalService _companyLocalService;

	@Reference
	private SecretManager _secretManager;

	private volatile long _companyId;
	private GcpClientManager<SecretManagerServiceClient> _gcpClientManager;
	private volatile String _kmsKeyName;
	private volatile String[] _locations;
	private volatile String _projectId;
	private volatile String _providerId;

}