/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.keymanager.provider.os.internal;

import com.liferay.keymanager.crypto.CryptoKeyMetadata;
import com.liferay.keymanager.crypto.CryptoManagerException;
import com.liferay.keymanager.provider.os.internal.configuration.FileKeyStoreCryptoVaultProviderConfiguration;
import com.liferay.keymanager.spi.crypto.CryptoVaultProvider;
import com.liferay.portal.configuration.metatype.bnd.util.ConfigurableUtil;
import com.liferay.portal.kernel.util.FileUtil;
import com.liferay.portal.kernel.util.StringUtil;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;

import java.security.Key;
import java.security.KeyStore;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Enumeration;
import java.util.List;
import java.util.Map;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.ConfigurationPolicy;
import org.osgi.service.component.annotations.Modified;

/**
 * @author Tomas Polesovsky
 */
@Component(
	configurationPid = "com.liferay.keymanager.provider.os.internal.configuration.FileKeyStoreCryptoVaultProviderConfiguration",
	configurationPolicy = ConfigurationPolicy.REQUIRE,
	service = CryptoVaultProvider.class
)
public class FileKeyStoreCryptoVaultProvider implements CryptoVaultProvider {

	@Override
	public byte[] decrypt(String identifier, byte[] ciphertext)
		throws CryptoManagerException {

		throw new CryptoManagerException("Operation not supported");
	}

	@Override
	public void deleteKey(String identifier) throws CryptoManagerException {
		try {
			KeyStore keyStore = _getKeyStore();

			keyStore.deleteEntry(identifier);

			_saveKeyStore(keyStore);
		}
		catch (Exception exception) {
			throw new CryptoManagerException(
				"Unable to delete key: " + identifier, exception);
		}
	}

	@Override
	public byte[] encrypt(String identifier, byte[] plaintext)
		throws CryptoManagerException {

		throw new CryptoManagerException("Operation not supported");
	}

	@Override
	public String generateAsymmetricKeyPair(
			String identifier, String algorithmSpec)
		throws CryptoManagerException {

		throw new CryptoManagerException("Operation not supported");
	}

	@Override
	public String generateSecretKey(String identifier, String algorithmSpec)
		throws CryptoManagerException {

		throw new CryptoManagerException("Operation not supported");
	}

	@Override
	public List<String> getKeyIdentifiers() throws CryptoManagerException {
		try {
			KeyStore keyStore = _getKeyStore();

			List<String> identifiers = new ArrayList<>();

			Enumeration<String> enumeration = keyStore.aliases();

			while (enumeration.hasMoreElements()) {
				identifiers.add(enumeration.nextElement());
			}

			return identifiers;
		}
		catch (Exception exception) {
			throw new CryptoManagerException(
				"Unable to list key identifiers", exception);
		}
	}

	@Override
	public CryptoKeyMetadata getKeyMetadata(String identifier)
		throws CryptoManagerException {

		throw new CryptoManagerException("Operation not supported");
	}

	@Override
	public String importSecretKey(
			String identifier, byte[] rawKeyMaterial, String algorithmSpec)
		throws CryptoManagerException {

		try {
			KeyStore keyStore = _getKeyStore();

			SecretKey secretKey = new SecretKeySpec(
				rawKeyMaterial, _parseAlgorithm(algorithmSpec));

			keyStore.setKeyEntry(
				identifier, secretKey, _password.toCharArray(), null);

			_saveKeyStore(keyStore);

			return identifier;
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
	public Key unwrap(
			String identifier, byte[] wrappedKeyBytes,
			String wrappedKeyAlgorithm, int wrappedKeyCipherType)
		throws CryptoManagerException {

		try {
			KeyStore keyStore = _getKeyStore();

			Key kek = keyStore.getKey(identifier, _password.toCharArray());

			Cipher cipher = Cipher.getInstance("AESWrap");

			cipher.init(Cipher.UNWRAP_MODE, kek);

			return cipher.unwrap(
				wrappedKeyBytes, wrappedKeyAlgorithm, wrappedKeyCipherType);
		}
		catch (Exception exception) {
			throw new CryptoManagerException(
				"Unable to unwrap with key: " + identifier, exception);
		}
	}

	@Override
	public byte[] wrap(String identifier, Key keyToWrap)
		throws CryptoManagerException {

		try {
			KeyStore keyStore = _getKeyStore();

			Key kek = keyStore.getKey(identifier, _password.toCharArray());

			Cipher cipher = Cipher.getInstance("AESWrap");

			cipher.init(Cipher.WRAP_MODE, kek);

			return cipher.wrap(keyToWrap);
		}
		catch (Exception exception) {
			throw new CryptoManagerException(
				"Unable to wrap with key: " + identifier, exception);
		}
	}

	@Activate
	@Modified
	protected void activate(Map<String, Object> properties) {
		FileKeyStoreCryptoVaultProviderConfiguration
			fileKeyStoreCryptoVaultProviderConfiguration =
				ConfigurableUtil.createConfigurable(
					FileKeyStoreCryptoVaultProviderConfiguration.class,
					properties);

		_autoCreate = fileKeyStoreCryptoVaultProviderConfiguration.autoCreate();
		_password =
			fileKeyStoreCryptoVaultProviderConfiguration.keystorePassword();
		_path = fileKeyStoreCryptoVaultProviderConfiguration.keystorePath();
		_type = fileKeyStoreCryptoVaultProviderConfiguration.keystoreType();
	}

	private KeyStore _getKeyStore() throws Exception {
		KeyStore keyStore = KeyStore.getInstance(_type);

		File file = new File(_path);

		if (file.exists()) {
			try (FileInputStream fileInputStream = new FileInputStream(file)) {
				keyStore.load(fileInputStream, _password.toCharArray());
			}
		}
		else if (_autoCreate) {
			keyStore.load(null, _password.toCharArray());
		}
		else {
			throw new CryptoManagerException(
				"KeyStore file not found: " + _path);
		}

		return keyStore;
	}

	private String _parseAlgorithm(String algorithmSpec) {
		String[] parts = StringUtil.split(algorithmSpec, ";");

		return parts[0];
	}

	private void _saveKeyStore(KeyStore keyStore) throws Exception {
		File file = new File(_path);

		FileUtil.mkdirs(file.getParentFile());

		try (FileOutputStream fileOutputStream = new FileOutputStream(file)) {
			keyStore.store(fileOutputStream, _password.toCharArray());
		}
	}

	private volatile boolean _autoCreate;
	private volatile String _password;
	private volatile String _path;
	private volatile String _type;

}