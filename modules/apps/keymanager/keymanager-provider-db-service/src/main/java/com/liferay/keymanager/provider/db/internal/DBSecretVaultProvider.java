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
import com.liferay.keymanager.spi.secret.SecretVaultReader;
import com.liferay.keymanager.spi.secret.SecretVaultWriter;
import com.liferay.osgi.util.configuration.ConfigurationFactoryUtil;
import com.liferay.portal.configuration.metatype.bnd.util.ConfigurableUtil;
import com.liferay.portal.kernel.dao.jdbc.OutputBlob;
import com.liferay.portal.kernel.service.CompanyLocalService;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.StreamUtil;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.kernel.util.Validator;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;

import java.security.Key;
import java.security.SecureRandom;
import java.security.spec.AlgorithmParameterSpec;

import java.sql.Blob;

import java.util.Arrays;
import java.util.Base64;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
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
	factory = "com.liferay.keymanager.provider.db.internal.DBSecretVaultProvider",
	service = {
		SecretVaultProvider.class, SecretVaultReader.class,
		SecretVaultWriter.class
	}
)
public class DBSecretVaultProvider
	implements SecretVaultProvider, SecretVaultReader, SecretVaultWriter {

	@Override
	public void deleteSecret(long companyId, String identifier)
		throws SecretManagerException {

		try {
			SecretEntry secretEntry = _secretEntryLocalService.fetchSecretEntry(
				companyId, identifier);

			if (secretEntry != null) {
				_secretEntryLocalService.deleteSecretEntry(secretEntry);
			}
		}
		catch (Exception exception) {
			throw new SecretManagerException(
				"Unable to delete secret: " + identifier, exception);
		}
	}

	@Override
	public SecureSecret getSecret(long companyId, String identifier)
		throws SecretManagerException {

		try {
			SecretEntry secretEntry = _secretEntryLocalService.fetchSecretEntry(
				companyId, identifier);

			if (secretEntry == null) {
				return null;
			}

			// 1. Unwrap DEK

			KeyReference masterKeyRef = KeyReference.fromString(
				secretEntry.getKekReference());

			byte[] dekBytes = _blobToBytes(secretEntry.getEncryptedDEKBlob());

			Key dek = _cryptoManager.unwrap(
				companyId, masterKeyRef, dekBytes, _keyAlgorithm,
				Cipher.SECRET_KEY);

			try {

				// 2. Decrypt Secret

				byte[] ciphertext = _blobToBytes(
					secretEntry.getCiphertextBlob());

				byte[] plaintext = null;

				try {
					plaintext = _decrypt(
						dek, ciphertext, secretEntry.getIv(),
						_encryptionAlgorithm);

					return new SecureSecret(
						new KeyReference(
							KeyReference.Type.SECRET, _providerId, identifier),
						plaintext);
				}
				finally {
					if (plaintext != null) {
						Arrays.fill(plaintext, (byte)0);
					}
				}
			}
			finally {
				if ((dek != null) && (dek.getEncoded() != null)) {
					byte[] encoded = dek.getEncoded();

					Arrays.fill(encoded, (byte)0);
				}
			}
		}
		catch (SecretManagerException secretManagerException) {
			throw secretManagerException;
		}
		catch (Exception exception) {
			throw new SecretManagerException(
				"Unable to get secret: " + identifier, exception);
		}
	}

	@Override
	public List<String> getSecretIdentifiers(long companyId)
		throws SecretManagerException {

		try {
			return _secretEntryLocalService.getSecretIdentifiers(companyId);
		}
		catch (Exception exception) {
			throw new SecretManagerException(
				"Unable to list secret identifiers", exception);
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

		byte[] dekBytes = null;

		try {

			// 1. Generate DEK

			KeyGenerator keyGenerator = KeyGenerator.getInstance(_keyAlgorithm);

			keyGenerator.init(_keySize, _secureRandom);

			Key dek = keyGenerator.generateKey();

			dekBytes = dek.getEncoded();

			// 2. Encrypt Secret

			byte[] iv = new byte[_ivLength];

			_secureRandom.nextBytes(iv);

			byte[] ciphertext = _encrypt(
				dek, secureSecret.getBytes(), iv, _encryptionAlgorithm);

			// 3. Wrap DEK

			KeyReference masterKeyRef = KeyReference.fromString(
				_masterKeyReference);

			byte[] encryptedDek = _cryptoManager.wrap(
				companyId, masterKeyRef, dek);

			// 4. Persist

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
			secretEntry.setKekReference(_masterKeyReference);

			_secretEntryLocalService.updateSecretEntry(secretEntry);
		}
		catch (Exception exception) {
			KeyReference keyReference = secureSecret.getKeyReference();

			throw new SecretManagerException(
				"Unable to put secret: " + keyReference.getIdentifier(),
				exception);
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

		_companyId = ConfigurationFactoryUtil.getCompanyId(
			_companyLocalService, properties);
		_providerId = dbSecretVaultProviderConfiguration.providerId();
		_masterKeyReference =
			dbSecretVaultProviderConfiguration.masterKeyReference();

		String dekCipherSpec =
			dbSecretVaultProviderConfiguration.dekCipherSpec();

		_encryptionAlgorithm = _parseAlgorithm(dekCipherSpec);

		Map<String, String> configurationMap = _parseConfiguration(
			dekCipherSpec);

		_keySize = GetterUtil.getInteger(configurationMap.get("keySize"));
		_ivLength = GetterUtil.getInteger(configurationMap.get("ivSize"));
		_gcmTagLength = GetterUtil.getInteger(configurationMap.get("gcmTag"));

		if (Validator.isNull(_encryptionAlgorithm) ||
			(!_encryptionAlgorithm.contains("/GCM/") &&
			 !_encryptionAlgorithm.contains("/CBC/"))) {

			throw new IllegalArgumentException(
				"Only GCM and CBC modes are supported: " +
					_encryptionAlgorithm);
		}

		// Key algorithm is the first part of the transformation (e.g. AES)

		_keyAlgorithm = StringUtil.split(_encryptionAlgorithm, "/")[0];
	}

	private byte[] _blobToBytes(Blob blob) throws Exception {
		try (InputStream inputStream = blob.getBinaryStream()) {
			ByteArrayOutputStream byteArrayOutputStream =
				new ByteArrayOutputStream();

			StreamUtil.transfer(inputStream, byteArrayOutputStream);

			return byteArrayOutputStream.toByteArray();
		}
	}

	private byte[] _decrypt(
			Key key, byte[] ciphertext, String ivBase64, String algorithm)
		throws Exception {

		Cipher cipher = Cipher.getInstance(algorithm);

		byte[] iv = Base64.getDecoder().decode(ivBase64);

		cipher.init(Cipher.DECRYPT_MODE, key, _getParameterSpec(iv, algorithm));

		return cipher.doFinal(ciphertext);
	}

	private byte[] _encrypt(
			Key key, byte[] plaintext, byte[] iv, String algorithm)
		throws Exception {

		Cipher cipher = Cipher.getInstance(algorithm);

		cipher.init(Cipher.ENCRYPT_MODE, key, _getParameterSpec(iv, algorithm));

		return cipher.doFinal(plaintext);
	}

	private AlgorithmParameterSpec _getParameterSpec(
		byte[] iv, String algorithm) {

		if (algorithm.contains("/GCM/")) {
			return new GCMParameterSpec(_gcmTagLength, iv);
		}

		return new IvParameterSpec(iv);
	}

	private String _parseAlgorithm(String configuration) {
		String[] parts = StringUtil.split(configuration, ";");

		if (parts.length > 0) {
			return parts[0].trim();
		}

		return null;
	}

	private Map<String, String> _parseConfiguration(String configuration) {
		Map<String, String> map = new HashMap<>();

		String[] parts = StringUtil.split(configuration, ";");

		// Skip the first part (the algorithm)

		for (int i = 1; i < parts.length; i++) {
			String[] keyValue = StringUtil.split(parts[i], "=");

			if (keyValue.length == 2) {
				map.put(keyValue[0].trim(), keyValue[1].trim());
			}
		}

		return map;
	}

	@Reference
	private CryptoManager _cryptoManager;

	private volatile long _companyId;

	@Reference
	private CompanyLocalService _companyLocalService;

	private volatile String _encryptionAlgorithm;
	private volatile int _gcmTagLength;
	private volatile int _ivLength;
	private volatile String _keyAlgorithm;
	private volatile int _keySize;
	private volatile String _masterKeyReference;
	private volatile String _providerId;
	private final SecureRandom _secureRandom = new SecureRandom();

	@Reference
	private SecretEntryLocalService _secretEntryLocalService;

}