/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.keymanager.provider.db.internal;

import com.liferay.keymanager.KeyReference;
import com.liferay.keymanager.crypto.CryptoManager;
import com.liferay.keymanager.provider.db.internal.configuration.DBSecretVaultProviderConfiguration;
import com.liferay.keymanager.provider.db.model.SecretEntry;
import com.liferay.keymanager.provider.db.service.SecretEntryLocalService;
import com.liferay.keymanager.secret.SecretManagerException;
import com.liferay.keymanager.secret.SecureSecret;
import com.liferay.keymanager.spi.secret.SecretVaultProvider;
import com.liferay.portal.configuration.metatype.bnd.util.ConfigurableUtil;
import com.liferay.portal.kernel.dao.jdbc.OutputBlob;
import com.liferay.portal.kernel.security.auth.CompanyThreadLocal;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.StreamUtil;
import com.liferay.portal.kernel.util.StringUtil;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;

import java.security.SecureRandom;
import java.security.spec.AlgorithmParameterSpec;

import java.sql.Blob;

import java.util.Arrays;
import java.util.Base64;
import java.util.Map;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.spec.GCMParameterSpec;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.ConfigurationPolicy;
import org.osgi.service.component.annotations.Modified;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Tomas Polesovsky
 */
@Component(
	configurationPid = "com.liferay.keymanager.provider.db.internal.configuration.DBSecretVaultProviderConfiguration",
	configurationPolicy = ConfigurationPolicy.REQUIRE,
	service = SecretVaultProvider.class
)
public class DBSecretVaultProvider implements SecretVaultProvider {

	@Override
	public void deleteSecret(String identifier) throws SecretManagerException {
		try {
			SecretEntry secretEntry = _secretEntryLocalService.fetchSecretEntry(
				_getCompanyId(), identifier);

			if (secretEntry != null) {
				_secretEntryLocalService.deleteSecretEntry(secretEntry);
			}
		}
		catch (Exception exception) {
			throw new SecretManagerException(exception);
		}
	}

	@Override
	public SecureSecret getSecret(String identifier)
		throws SecretManagerException {

		try {
			SecretEntry secretEntry = _secretEntryLocalService.getSecretEntry(
				_getCompanyId(), identifier);

			// 1. Fetch Master Key (KEK)

			KeyReference masterKeyRef = KeyReference.fromString(
				secretEntry.getKekReference());

			SecretKey masterKey = _cryptoManager.getSecretKey(masterKeyRef);

			// 2. Unwrap DEK

			byte[] dekBytes = _blobToBytes(secretEntry.getEncryptedDEKBlob());

			byte[] unwrappedDek = _decrypt(
				masterKey, dekBytes, secretEntry.getDekIv(),
				secretEntry.getAlgorithm());

			try {
				SecretKey dek = new SecretKeySpec(unwrappedDek, _keyAlgorithm);

				// 3. Decrypt Secret

				byte[] ciphertext = _blobToBytes(secretEntry.getCiphertextBlob());

				byte[] plaintext = _decrypt(
					dek, ciphertext, secretEntry.getIv(),
					secretEntry.getAlgorithm());

				return new SecureSecret(
					new KeyReference(
						KeyReference.Type.SECRET, _providerId, identifier, ""),
					plaintext);
			}
			finally {
				Arrays.fill(unwrappedDek, (byte)0);
			}
		}
		catch (Exception exception) {
			throw new SecretManagerException(exception);
		}
	}

	@Override
	public SecureSecret putSecret(SecureSecret secureSecret)
		throws SecretManagerException {

		byte[] dekBytes = null;

		try {
			// 1. Generate DEK

			KeyGenerator keyGenerator = KeyGenerator.getInstance(_keyAlgorithm);

			keyGenerator.init(_keySize, _secureRandom);

			SecretKey dek = keyGenerator.generateKey();

			dekBytes = dek.getEncoded();

			// 2. Encrypt Secret

			byte[] iv = new byte[_ivLength];

			_secureRandom.nextBytes(iv);

			byte[] ciphertext = _encrypt(
				dek, secureSecret.getBytes(), iv, _encryptionAlgorithm);

			// 3. Fetch Master Key (KEK)

			KeyReference masterKeyRef = KeyReference.fromString(
				_masterKeyReference);

			SecretKey masterKey = _cryptoManager.getSecretKey(masterKeyRef);

			// 4. Wrap DEK

			byte[] dekIv = new byte[_ivLength];

			_secureRandom.nextBytes(dekIv);

			byte[] encryptedDek = _encrypt(
				masterKey, dekBytes, dekIv, _encryptionAlgorithm);

			// 5. Persist

			long companyId = _getCompanyId();

			SecretEntry secretEntry = _secretEntryLocalService.fetchSecretEntry(
				companyId, secureSecret.getKeyReference().getIdentifier());

			if (secretEntry == null) {
				secretEntry = _secretEntryLocalService.createSecretEntry(0);

				secretEntry.setCompanyId(companyId);
				secretEntry.setAlias(
					secureSecret.getKeyReference().getIdentifier());
			}

			secretEntry.setCiphertextBlob(
				new OutputBlob(
					new ByteArrayInputStream(ciphertext), ciphertext.length));
			secretEntry.setIv(Base64.getEncoder().encodeToString(iv));
			secretEntry.setEncryptedDEKBlob(
				new OutputBlob(
					new ByteArrayInputStream(encryptedDek),
					encryptedDek.length));
			secretEntry.setDekIv(Base64.getEncoder().encodeToString(dekIv));
			secretEntry.setKekReference(_masterKeyReference);
			secretEntry.setAlgorithm(_encryptionAlgorithm);

			_secretEntryLocalService.updateSecretEntry(secretEntry);

			return secureSecret;
		}
		catch (Exception exception) {
			throw new SecretManagerException(exception);
		}
		finally {
			if (dekBytes != null) {
				Arrays.fill(dekBytes, (byte)0);
			}
		}
	}

	@Activate
	@Modified
	protected void activate(Map<String, Object> properties) {
		DBSecretVaultProviderConfiguration dbSecretVaultProviderConfiguration =
			ConfigurableUtil.createConfigurable(
				DBSecretVaultProviderConfiguration.class, properties);

		_providerId = dbSecretVaultProviderConfiguration.providerId();
		_masterKeyReference =
			dbSecretVaultProviderConfiguration.masterKeyReference();

		String cipherConfiguration =
			dbSecretVaultProviderConfiguration.cipherConfiguration();

		String[] parts = StringUtil.split(cipherConfiguration, ";");

		if (parts.length != 4) {
			throw new IllegalArgumentException(
				"Invalid cipher configuration: " + cipherConfiguration);
		}

		_encryptionAlgorithm = parts[0];
		_gcmTagLength = GetterUtil.getInteger(parts[1]);
		_ivLength = GetterUtil.getInteger(parts[2]);
		_keySize = GetterUtil.getInteger(parts[3]);

		// Key algorithm is the first part of the transformation (e.g. AES)

		_keyAlgorithm = StringUtil.split(_encryptionAlgorithm, "/")[0];
	}

	private byte[] _blobToBytes(Blob blob) throws Exception {
		ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();

		StreamUtil.transfer(blob.getBinaryStream(), byteArrayOutputStream);

		return byteArrayOutputStream.toByteArray();
	}

	private byte[] _decrypt(
			SecretKey key, byte[] ciphertext, String ivBase64, String algorithm)
		throws Exception {

		Cipher cipher = Cipher.getInstance(algorithm);

		byte[] iv = Base64.getDecoder().decode(ivBase64);

		cipher.init(Cipher.DECRYPT_MODE, key, _getParameterSpec(iv, algorithm));

		return cipher.doFinal(ciphertext);
	}

	private byte[] _encrypt(
			SecretKey key, byte[] plaintext, byte[] iv, String algorithm)
		throws Exception {

		Cipher cipher = Cipher.getInstance(algorithm);

		cipher.init(Cipher.ENCRYPT_MODE, key, _getParameterSpec(iv, algorithm));

		return cipher.doFinal(plaintext);
	}

	private long _getCompanyId() {
		return CompanyThreadLocal.getCompanyId();
	}

	private AlgorithmParameterSpec _getParameterSpec(
		byte[] iv, String algorithm) {

		if (algorithm.contains("/GCM/")) {
			return new GCMParameterSpec(_gcmTagLength, iv);
		}

		return new IvParameterSpec(iv);
	}

	@Reference
	private CryptoManager _cryptoManager;

	@Reference
	private SecretEntryLocalService _secretEntryLocalService;

	private String _encryptionAlgorithm;
	private int _gcmTagLength;
	private int _ivLength;
	private String _keyAlgorithm;
	private int _keySize;
	private String _masterKeyReference;
	private String _providerId;
	private final SecureRandom _secureRandom = new SecureRandom();

}
