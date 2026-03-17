/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.keymanager.provider.os.internal;

import com.liferay.keymanager.KeyReference;
import com.liferay.keymanager.crypto.CryptoKey;
import com.liferay.keymanager.crypto.CryptoManagerException;
import com.liferay.keymanager.provider.os.internal.configuration.KeyStoreCryptoVaultProviderConfiguration;
import com.liferay.keymanager.secret.SecretManager;
import com.liferay.keymanager.secret.SecureSecret;
import com.liferay.keymanager.spi.crypto.CryptoVaultProvider;
import com.liferay.petra.string.StringBundler;
import com.liferay.portal.configuration.metatype.bnd.util.ConfigurableUtil;
import com.liferay.portal.kernel.util.Validator;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;

import java.security.Key;
import java.security.KeyStore;
import java.security.cert.Certificate;

import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Map;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
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
	configurationPid = "com.liferay.keymanager.provider.os.internal.configuration.KeyStoreCryptoVaultProviderConfiguration",
	configurationPolicy = ConfigurationPolicy.REQUIRE,
	property = "providerId=keystore", service = CryptoVaultProvider.class
)
public class KeyStoreCryptoVaultProvider implements CryptoVaultProvider {

	@Override
	public byte[] decrypt(long companyId, String identifier, byte[] ciphertext)
		throws CryptoManagerException {

		try {
			try (SecureSecret secureSecret = _getKeystorePasswordSecret(
					companyId)) {

				char[] password = null;

				if (secureSecret != null) {
					password = secureSecret.getChars();
				}

				KeyStore keyStore = _getKeyStore(password);

				Key key = keyStore.getKey(identifier, password);

				if (key == null) {
					throw new CryptoManagerException(
						"Key not found: " + identifier);
				}

				Cipher cipher = Cipher.getInstance(key.getAlgorithm());

				cipher.init(Cipher.DECRYPT_MODE, key);

				return cipher.doFinal(ciphertext);
			}
		}
		catch (Exception exception) {
			throw new CryptoManagerException(
				"Unable to decrypt with key: " + identifier, exception);
		}
	}

	@Override
	public void deleteKey(long companyId, String identifier)
		throws CryptoManagerException {

		try {
			try (SecureSecret secureSecret = _getKeystorePasswordSecret(
					companyId)) {

				char[] password = null;

				if (secureSecret != null) {
					password = secureSecret.getChars();
				}

				KeyStore keyStore = _getKeyStore(password);

				if (keyStore.containsAlias(identifier)) {
					keyStore.deleteEntry(identifier);

					_saveKeyStore(keyStore, password);
				}
			}
		}
		catch (Exception exception) {
			throw new CryptoManagerException(
				"Unable to delete key: " + identifier, exception);
		}
	}

	@Override
	public byte[] encrypt(long companyId, String identifier, byte[] plaintext)
		throws CryptoManagerException {

		try {
			try (SecureSecret secureSecret = _getKeystorePasswordSecret(
					companyId)) {

				char[] password = null;

				if (secureSecret != null) {
					password = secureSecret.getChars();
				}

				KeyStore keyStore = _getKeyStore(password);

				Key key = null;

				if (keyStore.isKeyEntry(identifier)) {
					key = keyStore.getKey(identifier, password);
				}
				else {
					Certificate certificate = keyStore.getCertificate(
						identifier);

					if (certificate != null) {
						key = certificate.getPublicKey();
					}
				}

				if (key == null) {
					throw new CryptoManagerException(
						"Key not found: " + identifier);
				}

				Cipher cipher = Cipher.getInstance(key.getAlgorithm());

				cipher.init(Cipher.ENCRYPT_MODE, key);

				return cipher.doFinal(plaintext);
			}
		}
		catch (Exception exception) {
			throw new CryptoManagerException(
				"Unable to encrypt with key: " + identifier, exception);
		}
	}

	@Override
	public String generateAsymmetricKeyPair(
			long companyId, String identifier, String algorithmSpec)
		throws CryptoManagerException {

		throw new CryptoManagerException(
			"Asymmetric key pair generation not supported for KeyStore");
	}

	@Override
	public String generateSecretKey(
			long companyId, String identifier, String algorithmSpec)
		throws CryptoManagerException {

		try {
			try (SecureSecret secureSecret = _getKeystorePasswordSecret(
					companyId)) {

				char[] password = null;

				if (secureSecret != null) {
					password = secureSecret.getChars();
				}

				KeyGenerator keyGenerator = KeyGenerator.getInstance(
					_parseAlgorithm(algorithmSpec));

				int keySize = _parseKeySize(algorithmSpec, 256);

				keyGenerator.init(keySize);

				SecretKey secretKey = keyGenerator.generateKey();

				KeyStore keyStore = _getKeyStore(password);

				keyStore.setKeyEntry(identifier, secretKey, password, null);

				_saveKeyStore(keyStore, password);

				return identifier;
			}
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
			try (SecureSecret secureSecret = _getKeystorePasswordSecret(
					companyId)) {

				char[] password = null;

				if (secureSecret != null) {
					password = secureSecret.getChars();
				}

				KeyStore keyStore = _getKeyStore(password);

				return Collections.list(keyStore.aliases());
			}
		}
		catch (Exception exception) {
			throw new CryptoManagerException(
				"Unable to list key identifiers", exception);
		}
	}

	@Override
	public CryptoKey getKeyMetadata(long companyId, String identifier)
		throws CryptoManagerException {

		try {
			try (SecureSecret secureSecret = _getKeystorePasswordSecret(
					companyId)) {

				char[] password = null;

				if (secureSecret != null) {
					password = secureSecret.getChars();
				}

				KeyStore keyStore = _getKeyStore(password);

				if (!keyStore.containsAlias(identifier)) {
					throw new CryptoManagerException(
						"Key not found: " + identifier);
				}

				Date createDate = keyStore.getCreationDate(identifier);

				String algorithm = "Unknown";

				if (keyStore.isKeyEntry(identifier)) {
					Key key = keyStore.getKey(identifier, password);

					algorithm = key.getAlgorithm();
				}
				else {
					Certificate certificate = keyStore.getCertificate(
						identifier);

					algorithm = certificate.getPublicKey(
					).getAlgorithm();
				}

				return new CryptoKey(
					KeyReference.fromString(
						StringBundler.concat(
							"${keyRef:", _providerId, ":", identifier, "}")),
					algorithm, algorithm, createDate.getTime());
			}
		}
		catch (Exception exception) {
			throw new CryptoManagerException(
				"Unable to get key metadata for: " + identifier, exception);
		}
	}

	@Override
	public String importSecretKey(
			long companyId, String identifier, byte[] rawKeyMaterial,
			String algorithmSpec)
		throws CryptoManagerException {

		try {
			try (SecureSecret secureSecret = _getKeystorePasswordSecret(
					companyId)) {

				char[] password = null;

				if (secureSecret != null) {
					password = secureSecret.getChars();
				}

				SecretKey secretKey = new SecretKeySpec(
					rawKeyMaterial, _parseAlgorithm(algorithmSpec));

				KeyStore keyStore = _getKeyStore(password);

				keyStore.setKeyEntry(identifier, secretKey, password, null);

				_saveKeyStore(keyStore, password);

				return identifier;
			}
		}
		catch (Exception exception) {
			throw new CryptoManagerException(
				"Unable to import secret key: " + identifier, exception);
		}
		finally {
			if (rawKeyMaterial != null) {
				Arrays.fill(rawKeyMaterial, (byte)0);
			}
		}
	}

	@Override
	public boolean isAllowedCompany(long companyId) {
		return true;
	}

	@Override
	public Key unwrap(
			long companyId, String identifier, byte[] wrappedKeyBytes,
			String wrappedKeyAlgorithm, int wrappedKeyCipherType)
		throws CryptoManagerException {

		throw new CryptoManagerException("Operation not supported");
	}

	@Override
	public byte[] wrap(long companyId, String identifier, Key keyToWrap)
		throws CryptoManagerException {

		throw new CryptoManagerException("Operation not supported");
	}

	@Activate
	@Modified
	protected void activate(Map<String, Object> properties) {
		KeyStoreCryptoVaultProviderConfiguration
			keyStoreCryptoVaultProviderConfiguration =
				ConfigurableUtil.createConfigurable(
					KeyStoreCryptoVaultProviderConfiguration.class,
					properties);

		_keyStorePath =
			keyStoreCryptoVaultProviderConfiguration.keystorePath();
		_keyStoreType =
			keyStoreCryptoVaultProviderConfiguration.keystoreType();
		_keystorePasswordReference =
			keyStoreCryptoVaultProviderConfiguration.keystorePassword();
		_providerId =
			keyStoreCryptoVaultProviderConfiguration.providerId();
	}

	private KeyStore _getKeyStore(char[] password) throws Exception {
		KeyStore keyStore = KeyStore.getInstance(_keyStoreType);

		File file = new File(_keyStorePath);

		if (file.exists()) {
			try (FileInputStream fis = new FileInputStream(file)) {
				keyStore.load(fis, password); // here
			}
		}
		else {
			keyStore.load(null, password); // here
		}

		return keyStore;
	}

	private SecureSecret _getKeystorePasswordSecret(long companyId)
		throws Exception {

		if (Validator.isNull(_keystorePasswordReference)) {
			return null;
		}

		return _secretManager.getSecret(
			companyId, KeyReference.fromString(_keystorePasswordReference));
	}

	private String _parseAlgorithm(String spec) {
		if (Validator.isNull(spec)) {
			return null;
		}

		int pos = spec.indexOf(';');

		if (pos > 0) {
			return spec.substring(0, pos).trim();
		}

		return spec.trim();
	}

	private int _parseKeySize(String spec, int defaultSize) {
		if (Validator.isNull(spec)) {
			return defaultSize;
		}

		int pos = spec.indexOf("keySize=");

		if (pos >= 0) {
			int end = spec.indexOf(';', pos);

			if (end < 0) {
				end = spec.length();
			}

			return Integer.parseInt(spec.substring(pos + 8, end).trim());
		}

		return defaultSize;
	}

	private void _saveKeyStore(KeyStore keyStore, char[] password)
		throws Exception {

		File file = new File(_keyStorePath);

		File parentFile = file.getParentFile();

		if ((parentFile != null) && !parentFile.exists()) {
			parentFile.mkdirs();
		}

		try (FileOutputStream fos = new FileOutputStream(file)) {
			keyStore.store(fos, password);  // here
		}
	}

	private String _keyStorePath;
	private String _keyStoreType;
	private String _keystorePasswordReference;
	private String _providerId;

	protected void setSecretManager(SecretManager secretManager) {
		_secretManager = secretManager;
	}

	@Reference
	private SecretManager _secretManager;

}