/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.keymanager.provider.gcp.internal;

import com.google.cloud.kms.v1.CryptoKeyName;
import com.google.cloud.kms.v1.CryptoKeyVersion;
import com.google.cloud.kms.v1.CryptoKeyVersionTemplate;
import com.google.cloud.kms.v1.DecryptResponse;
import com.google.cloud.kms.v1.EncryptResponse;
import com.google.cloud.kms.v1.KeyManagementServiceClient;
import com.google.cloud.kms.v1.KeyManagementServiceSettings;
import com.google.cloud.kms.v1.KeyRingName;
import com.google.cloud.kms.v1.ListCryptoKeysRequest;
import com.google.cloud.kms.v1.ProtectionLevel;
import com.google.protobuf.ByteString;
import com.google.protobuf.Duration;
import com.google.protobuf.Timestamp;

import com.liferay.keymanager.KeyReference;
import com.liferay.keymanager.crypto.CryptoKey;
import com.liferay.keymanager.crypto.CryptoManagerException;
import com.liferay.keymanager.provider.gcp.internal.configuration.GcpKmsCryptoVaultProviderConfiguration;
import com.liferay.keymanager.provider.gcp.internal.configuration.GcpKmsSystemCryptoVaultProviderConfiguration;
import com.liferay.keymanager.provider.gcp.internal.util.GcpClientManager;
import com.liferay.keymanager.secret.SecretManager;
import com.liferay.keymanager.spi.crypto.CryptoVaultProvider;
import com.liferay.osgi.util.configuration.ConfigurationFactoryUtil;
import com.liferay.petra.string.StringBundler;
import com.liferay.portal.configuration.metatype.bnd.util.ConfigurableUtil;
import com.liferay.portal.kernel.model.Company;
import com.liferay.portal.kernel.service.CompanyLocalService;
import com.liferay.portal.kernel.util.GetterUtil;

import java.io.IOException;

import java.security.Key;
import java.security.KeyFactory;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;

import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Deactivate;
import org.osgi.service.component.annotations.Modified;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Tomas Polesovsky
 */
@Component(
	factory = "com.liferay.keymanager.provider.gcp.internal.GcpKmsCryptoVaultProvider",
	property = "providerId=gcp-kms", service = CryptoVaultProvider.class
)
public class GcpKmsCryptoVaultProvider implements CryptoVaultProvider {

	@Override
	public byte[] decrypt(long companyId, String identifier, byte[] ciphertext)
		throws CryptoManagerException {

		try {
			return _gcpClientManager.execute(
				companyId,
				client -> {
					String name = _getGcpKeyName(identifier);

					DecryptResponse response = client.decrypt(
						name, ByteString.copyFrom(ciphertext));

					return response.getPlaintext(
					).toByteArray();
				});
		}
		catch (Exception exception) {
			throw new CryptoManagerException(
				"Unable to decrypt with GCP KMS key: " + identifier, exception);
		}
	}

	@Override
	public void deleteKey(long companyId, String identifier)
		throws CryptoManagerException {

		throw new CryptoManagerException("Operation not supported");
	}

	@Override
	public byte[] encrypt(long companyId, String identifier, byte[] plaintext)
		throws CryptoManagerException {

		try {
			return _gcpClientManager.execute(
				companyId,
				client -> {
					String name = _getGcpKeyName(identifier);

					EncryptResponse response = client.encrypt(
						name, ByteString.copyFrom(plaintext));

					return response.getCiphertext(
					).toByteArray();
				});
		}
		catch (Exception exception) {
			throw new CryptoManagerException(
				"Unable to encrypt with GCP KMS key: " + identifier, exception);
		}
	}

	@Override
	public String generateAsymmetricKeyPair(
			long companyId, String identifier, String algorithmSpec)
		throws CryptoManagerException {

		try {
			return _gcpClientManager.execute(
				companyId,
				client -> {
					com.google.cloud.kms.v1.CryptoKey.Builder cryptoKeyBuilder = com.google.cloud.kms.v1.CryptoKey.newBuilder();

					cryptoKeyBuilder.setPurpose(
						com.google.cloud.kms.v1.CryptoKey.CryptoKeyPurpose.ASYMMETRIC_DECRYPT);

					CryptoKeyVersionTemplate.Builder
						cryptoKeyVersionTemplateBuilder =
							CryptoKeyVersionTemplate.newBuilder();

					cryptoKeyVersionTemplateBuilder.setAlgorithm(
						_getAsymmetricAlgorithm(algorithmSpec));

					cryptoKeyVersionTemplateBuilder.setProtectionLevel(
						_protectionLevel);

					cryptoKeyBuilder.setVersionTemplate(
						cryptoKeyVersionTemplateBuilder.build());

					client.createCryptoKey(
						_keyRingName, identifier, cryptoKeyBuilder.build());

					return identifier;
				});
		}
		catch (Exception exception) {
			throw new CryptoManagerException(
				"Unable to generate asymmetric key pair: " + identifier,
				exception);
		}
	}

	@Override
	public String generateSecretKey(
			long companyId, String identifier, String algorithmSpec)
		throws CryptoManagerException {

		try {
			return _gcpClientManager.execute(
				companyId,
				client -> {
					if (algorithmSpec == null) {
						return null;
					}

					com.google.cloud.kms.v1.CryptoKey.Builder cryptoKeyBuilder = com.google.cloud.kms.v1.CryptoKey.newBuilder();

					cryptoKeyBuilder.setPurpose(
						com.google.cloud.kms.v1.CryptoKey.CryptoKeyPurpose.ENCRYPT_DECRYPT);

					CryptoKeyVersionTemplate.Builder
						cryptoKeyVersionTemplateBuilder =
							CryptoKeyVersionTemplate.newBuilder();

					cryptoKeyVersionTemplateBuilder.setAlgorithm(
						CryptoKeyVersion.CryptoKeyVersionAlgorithm.
							GOOGLE_SYMMETRIC_ENCRYPTION);

					cryptoKeyVersionTemplateBuilder.setProtectionLevel(
						_protectionLevel);

					cryptoKeyBuilder.setVersionTemplate(
						cryptoKeyVersionTemplateBuilder.build());

					if (_rotationPeriodSeconds > 0) {
						cryptoKeyBuilder.setRotationPeriod(
							Duration.newBuilder(
							).setSeconds(
								_rotationPeriodSeconds
							).build());

						cryptoKeyBuilder.setNextRotationTime(
							Timestamp.newBuilder(
							).setSeconds(
								(System.currentTimeMillis() / 1000) +
									_rotationPeriodSeconds
							).build());
					}

					client.createCryptoKey(
						_keyRingName, identifier, cryptoKeyBuilder.build());

					return identifier;
				});
		}
		catch (Exception exception) {
			throw new CryptoManagerException(
				"Unable to generate secret key: " + identifier, exception);
		}
	}

	@Override
	public List<String> getKeyIdentifiers(long companyId)
		throws CryptoManagerException {

		try {
			return _gcpClientManager.execute(
				companyId,
				client -> {
					List<String> identifiers = new ArrayList<>();

					ListCryptoKeysRequest request =
						ListCryptoKeysRequest.newBuilder(
						).setParent(_keyRingName.toString()).build();

					KeyManagementServiceClient.ListCryptoKeysPagedResponse
						response = client.listCryptoKeys(request);

					for (com.google.cloud.kms.v1.CryptoKey cryptoKey : response.iterateAll()) {
						String name = cryptoKey.getName();

						identifiers.add(
							name.substring(name.lastIndexOf('/') + 1));
					}

					return identifiers;
				});
		}
		catch (Exception exception) {
			throw new CryptoManagerException(
				"Unable to list GCP KMS keys", exception);
		}
	}

	@Override
	public CryptoKey getKeyMetadata(long companyId, String identifier)
		throws CryptoManagerException {

		try {
			return _gcpClientManager.execute(
				companyId,
				client -> {
					String name = _getGcpKeyName(identifier);

					com.google.cloud.kms.v1.CryptoKey cryptoKey = client.getCryptoKey(name);

					Timestamp createTime = cryptoKey.getCreateTime();

					long seconds = createTime.getSeconds();

					return new CryptoKey(
						KeyReference.fromString(
							StringBundler.concat(
								"${keyRef:", _providerId, ":", identifier,
								"}")),
						cryptoKey.getPurpose(
						).name(),
						cryptoKey.getVersionTemplate(
						).getAlgorithm(
						).name(),
						seconds * 1000);
				});
		}
		catch (Exception exception) {
			throw new CryptoManagerException(
				"Unable to get GCP KMS key metadata: " + identifier, exception);
		}
	}

	@Override
	public String importSecretKey(
			long companyId, String identifier, byte[] rawKeyMaterial,
			String algorithmSpec)
		throws CryptoManagerException {

		if ((rawKeyMaterial == null) || (algorithmSpec == null)) {
			throw new CryptoManagerException("Operation not supported");
		}

		throw new CryptoManagerException("Operation not supported");
	}

	@Override
	public boolean isAllowedCompany(long companyId) {
		if (_companyId == companyId) {
			return true;
		}

		return false;
	}

	@Override
	public Key unwrap(
			long companyId, String identifier, byte[] wrappedKeyBytes,
			String wrappedKeyAlgorithm, int wrappedKeyCipherType)
		throws CryptoManagerException {

		byte[] plaintext = null;

		try {
			plaintext = decrypt(companyId, identifier, wrappedKeyBytes);

			if (wrappedKeyCipherType == Cipher.SECRET_KEY) {
				return new SecretKeySpec(plaintext, wrappedKeyAlgorithm);
			}

			KeyFactory keyFactory = KeyFactory.getInstance(wrappedKeyAlgorithm);

			if (wrappedKeyCipherType == Cipher.PRIVATE_KEY) {
				return keyFactory.generatePrivate(
					new PKCS8EncodedKeySpec(plaintext));
			}

			if (wrappedKeyCipherType == Cipher.PUBLIC_KEY) {
				return keyFactory.generatePublic(
					new X509EncodedKeySpec(plaintext));
			}

			throw new CryptoManagerException(
				"Unsupported wrapped key cipher type: " + wrappedKeyCipherType);
		}
		catch (CryptoManagerException cryptoManagerException) {
			throw cryptoManagerException;
		}
		catch (Exception exception) {
			throw new CryptoManagerException(
				"Unable to unwrap GCP KMS key: " + identifier, exception);
		}
		finally {
			if (plaintext != null) {
				Arrays.fill(plaintext, (byte)0);
			}
		}
	}

	@Override
	public byte[] wrap(long companyId, String identifier, Key keyToWrap)
		throws CryptoManagerException {

		return encrypt(companyId, identifier, keyToWrap.getEncoded());
	}

	@Activate
	@Modified
	protected void activate(Map<String, Object> properties) throws IOException {
		String gcpAuthKeyReference = null;
		String authType = null;
		String impersonatedServiceAccount = null;
		String keyRingPath = null;
		String newKeyProtectionLevel = null;
		long newKeyRotationPeriodSeconds = 0;

		if (GetterUtil.getBoolean(properties.get("systemScope"))) {
			GcpKmsSystemCryptoVaultProviderConfiguration
				configuration = ConfigurableUtil.createConfigurable(
					GcpKmsSystemCryptoVaultProviderConfiguration.class,
					properties);

			_companyId = _getDefaultCompanyId();
			_providerId = configuration.providerId();

			gcpAuthKeyReference = configuration.gcpServiceAccountKey();
			authType = configuration.gcpAuthType();
			impersonatedServiceAccount =
				configuration.gcpImpersonatedServiceAccount();
			keyRingPath = configuration.keyRingPath();
			newKeyProtectionLevel = configuration.newKeyProtectionLevel();
			newKeyRotationPeriodSeconds =
				configuration.newKeyRotationPeriodSeconds();
		}
		else {
			GcpKmsCryptoVaultProviderConfiguration configuration =
				ConfigurableUtil.createConfigurable(
					GcpKmsCryptoVaultProviderConfiguration.class, properties);

			_companyId = ConfigurationFactoryUtil.getCompanyId(
				_companyLocalService, properties);
			_providerId = configuration.providerId();

			gcpAuthKeyReference = configuration.gcpServiceAccountKey();
			authType = "sa-key";
			keyRingPath = configuration.keyRingPath();
			newKeyProtectionLevel = configuration.newKeyProtectionLevel();
			newKeyRotationPeriodSeconds =
				configuration.newKeyRotationPeriodSeconds();
		}

		_keyRingName = KeyRingName.parse(keyRingPath);

		_protectionLevel = ProtectionLevel.valueOf(newKeyProtectionLevel);
		_rotationPeriodSeconds = newKeyRotationPeriodSeconds;

		if (_gcpClientManager == null) {
			_gcpClientManager = new GcpClientManager<>(
				_secretManager, gcpAuthKeyReference, authType,
				impersonatedServiceAccount,
				fixedCredentialsProvider -> KeyManagementServiceClient.create(
					KeyManagementServiceSettings.newBuilder(
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

	private CryptoKeyVersion.CryptoKeyVersionAlgorithm _getAsymmetricAlgorithm(
		String algorithmSpec) {

		if (algorithmSpec.contains("RSA")) {
			return CryptoKeyVersion.CryptoKeyVersionAlgorithm.
				RSA_DECRYPT_OAEP_2048_SHA256;
		}

		if (algorithmSpec.contains("EC")) {
			return CryptoKeyVersion.CryptoKeyVersionAlgorithm.
				EC_SIGN_P256_SHA256;
		}

		return CryptoKeyVersion.CryptoKeyVersionAlgorithm.
			CRYPTO_KEY_VERSION_ALGORITHM_UNSPECIFIED;
	}

	private long _getDefaultCompanyId() {
		List<Company> companies = _companyLocalService.getCompanies();

		if (companies.isEmpty()) {
			return 0;
		}

		return companies.get(0).getCompanyId();
	}

	private String _getGcpKeyName(String alias) {
		return CryptoKeyName.of(
			_keyRingName.getProject(), _keyRingName.getLocation(),
			_keyRingName.getKeyRing(), alias).toString();
	}

	private volatile long _companyId;

	@Reference
	private CompanyLocalService _companyLocalService;

	private GcpClientManager<KeyManagementServiceClient> _gcpClientManager;
	private volatile KeyRingName _keyRingName;
	private volatile ProtectionLevel _protectionLevel;
	private volatile String _providerId;
	private volatile long _rotationPeriodSeconds;

	@Reference
	private SecretManager _secretManager;

}