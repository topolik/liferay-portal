/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.keymanager.provider.gcp.internal;

import com.google.cloud.kms.v1.CryptoKey;
import com.google.cloud.kms.v1.DecryptResponse;
import com.google.cloud.kms.v1.EncryptResponse;
import com.google.cloud.kms.v1.KeyManagementServiceClient;
import com.google.cloud.kms.v1.ListCryptoKeysRequest;
import com.google.protobuf.ByteString;

import com.liferay.keymanager.crypto.CryptoManagerException;
import com.liferay.keymanager.provider.gcp.internal.configuration.GcpKmsCryptoVaultProviderConfiguration;
import com.liferay.keymanager.spi.crypto.CryptoVaultProvider;
import com.liferay.petra.string.StringBundler;
import com.liferay.portal.configuration.metatype.bnd.util.ConfigurableUtil;

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
import org.osgi.service.component.annotations.ConfigurationPolicy;
import org.osgi.service.component.annotations.Deactivate;
import org.osgi.service.component.annotations.Modified;

/**
 * @author Tomas Polesovsky
 */
@Component(
	configurationPid = "com.liferay.keymanager.provider.gcp.internal.configuration.GcpKmsCryptoVaultProviderConfiguration",
	configurationPolicy = ConfigurationPolicy.REQUIRE,
	service = CryptoVaultProvider.class
)
public class GcpKmsCryptoVaultProvider implements CryptoVaultProvider {

	@Override
	public void addPrivateKey(
			String identifier,
			com.liferay.keymanager.crypto.CryptoKey privateKey)
		throws CryptoManagerException {

		throw new CryptoManagerException("Operation not supported");
	}

	@Override
	public void addPublicKey(
			String identifier,
			com.liferay.keymanager.crypto.CryptoKey publicKey)
		throws CryptoManagerException {

		throw new CryptoManagerException("Operation not supported");
	}

	@Override
	public void addSecretKey(
			String identifier,
			com.liferay.keymanager.crypto.CryptoKey secretKey)
		throws CryptoManagerException {

		throw new CryptoManagerException("Operation not supported");
	}

	@Override
	public byte[] decrypt(String identifier, byte[] ciphertext)
		throws CryptoManagerException {

		try {
			String name = _getGcpKeyName(identifier);

			DecryptResponse response = _client.decrypt(
				name, ByteString.copyFrom(ciphertext));

			return response.getPlaintext(
			).toByteArray();
		}
		catch (Exception exception) {
			throw new CryptoManagerException(
				"Unable to decrypt with GCP KMS key: " + identifier, exception);
		}
	}

	@Override
	public void deleteKey(String identifier) throws CryptoManagerException {
		throw new CryptoManagerException("Operation not supported");
	}

	@Override
	public byte[] encrypt(String identifier, byte[] plaintext)
		throws CryptoManagerException {

		try {
			String name = _getGcpKeyName(identifier);

			EncryptResponse response = _client.encrypt(
				name, ByteString.copyFrom(plaintext));

			return response.getCiphertext(
			).toByteArray();
		}
		catch (Exception exception) {
			throw new CryptoManagerException(
				"Unable to encrypt with GCP KMS key: " + identifier, exception);
		}
	}

	@Override
	public List<String> getKeyIdentifiers() throws CryptoManagerException {
		try {
			List<String> identifiers = new ArrayList<>();

			ListCryptoKeysRequest request = ListCryptoKeysRequest.newBuilder(
			).setParent(
				StringBundler.concat(
					"projects/", _projectId, "/locations/", _locationId,
					"/keyRings/", _keyRingId)
			).build();

			KeyManagementServiceClient.ListCryptoKeysPagedResponse response =
				_client.listCryptoKeys(request);

			for (CryptoKey cryptoKey : response.iterateAll()) {
				String name = cryptoKey.getName();

				identifiers.add(name.substring(name.lastIndexOf('/') + 1));
			}

			return identifiers;
		}
		catch (Exception exception) {
			throw new CryptoManagerException(
				"Unable to list GCP KMS keys", exception);
		}
	}

	@Override
	public Key unwrap(
			String identifier, byte[] wrappedKeyBytes,
			String wrappedKeyAlgorithm, int wrappedKeyCipherType)
		throws CryptoManagerException {

		byte[] plaintext = null;

		try {
			plaintext = decrypt(identifier, wrappedKeyBytes);

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
	public byte[] wrap(String identifier, Key keyToWrap)
		throws CryptoManagerException {

		return encrypt(identifier, keyToWrap.getEncoded());
	}

	@Activate
	@Modified
	protected void activate(Map<String, Object> properties) throws IOException {
		GcpKmsCryptoVaultProviderConfiguration
			gcpKmsCryptoVaultProviderConfiguration =
				ConfigurableUtil.createConfigurable(
					GcpKmsCryptoVaultProviderConfiguration.class, properties);

		_providerId = gcpKmsCryptoVaultProviderConfiguration.providerId();
		_projectId = gcpKmsCryptoVaultProviderConfiguration.projectId();
		_locationId = gcpKmsCryptoVaultProviderConfiguration.locationId();
		_keyRingId = gcpKmsCryptoVaultProviderConfiguration.keyRingId();

		_client = KeyManagementServiceClient.create();
	}

	@Deactivate
	protected void deactivate() {
		if (_client != null) {
			_client.close();
		}
	}

	private String _getGcpKeyName(String alias) {
		return StringBundler.concat(
			"projects/", _projectId, "/locations/", _locationId, "/keyRings/",
			_keyRingId, "/cryptoKeys/", alias);
	}

	private volatile KeyManagementServiceClient _client;
	private volatile String _keyRingId;
	private volatile String _locationId;
	private volatile String _projectId;
	private volatile String _providerId;

}