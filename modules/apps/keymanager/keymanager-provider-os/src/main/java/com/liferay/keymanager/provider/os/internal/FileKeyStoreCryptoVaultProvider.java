/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.keymanager.provider.os.internal;

import com.liferay.keymanager.crypto.CryptoManagerException;
import com.liferay.keymanager.provider.os.internal.configuration.FileKeyStoreCryptoVaultProviderConfiguration;
import com.liferay.keymanager.spi.crypto.CryptoVaultProvider;
import com.liferay.petra.string.StringBundler;
import com.liferay.portal.configuration.metatype.bnd.util.ConfigurableUtil;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.util.FileUtil;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

import java.security.Key;
import java.security.KeyStore;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.cert.Certificate;

import java.util.Map;

import javax.crypto.SecretKey;

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
	public void deleteKey(String alias) throws CryptoManagerException {
		try {
			_keyStore.deleteEntry(alias);

			_saveKeyStore();
		}
		catch (Exception exception) {
			throw new CryptoManagerException(exception);
		}
	}

	@Override
	public Certificate getCertificate(String alias)
		throws CryptoManagerException {

		try {
			return _keyStore.getCertificate(alias);
		}
		catch (Exception exception) {
			throw new CryptoManagerException(exception);
		}
	}

	@Override
	public PrivateKey getPrivateKey(String alias)
		throws CryptoManagerException {

		try {
			Key key = _keyStore.getKey(alias, _keystorePassword);

			if (key instanceof PrivateKey) {
				return (PrivateKey)key;
			}

			return null;
		}
		catch (Exception exception) {
			throw new CryptoManagerException(exception);
		}
	}

	@Override
	public PublicKey getPublicKey(String alias) throws CryptoManagerException {
		try {
			Certificate certificate = _keyStore.getCertificate(alias);

			if (certificate != null) {
				return certificate.getPublicKey();
			}

			return null;
		}
		catch (Exception exception) {
			throw new CryptoManagerException(exception);
		}
	}

	@Override
	public SecretKey getSecretKey(String alias) throws CryptoManagerException {
		try {
			Key key = _keyStore.getKey(alias, _keystorePassword);

			if (key instanceof SecretKey) {
				return (SecretKey)key;
			}

			return null;
		}
		catch (Exception exception) {
			throw new CryptoManagerException(exception);
		}
	}

	@Override
	public void putCertificate(String alias, Certificate certificate)
		throws CryptoManagerException {

		try {
			_keyStore.setCertificateEntry(alias, certificate);

			_saveKeyStore();
		}
		catch (Exception exception) {
			throw new CryptoManagerException(exception);
		}
	}

	@Override
	public void putPrivateKey(
			String alias, PrivateKey privateKey, Certificate[] certificateChain)
		throws CryptoManagerException {

		try {
			_keyStore.setKeyEntry(
				alias, privateKey, _keystorePassword, certificateChain);

			_saveKeyStore();
		}
		catch (Exception exception) {
			throw new CryptoManagerException(exception);
		}
	}

	@Override
	public void putSecretKey(String alias, SecretKey secretKey)
		throws CryptoManagerException {

		try {
			KeyStore.SecretKeyEntry entry = new KeyStore.SecretKeyEntry(
				secretKey);

			_keyStore.setEntry(
				alias, entry, new KeyStore.PasswordProtection(_keystorePassword));

			_saveKeyStore();
		}
		catch (Exception exception) {
			throw new CryptoManagerException(exception);
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

		_providerId = fileKeyStoreCryptoVaultProviderConfiguration.providerId();
		_keystorePath = _resolveLiferayHome(
			fileKeyStoreCryptoVaultProviderConfiguration.keystorePath());
		_keystoreType =
			fileKeyStoreCryptoVaultProviderConfiguration.keystoreType();
		_autoCreate =
			fileKeyStoreCryptoVaultProviderConfiguration.autoCreate();

		String password =
			fileKeyStoreCryptoVaultProviderConfiguration.keystorePassword();

		_keystorePassword = password.toCharArray();

		try {
			_loadOrCreateKeyStore();

			if (_log.isInfoEnabled()) {
				_log.info(
					StringBundler.concat(
						"File KeyStore Crypto Vault initialized: providerId=",
						_providerId, " at ", _keystorePath));
			}
		}
		catch (Exception exception) {
			_log.error(
				"Failed to initialize File KeyStore crypto provider",
				exception);
		}
	}

	private void _loadOrCreateKeyStore() throws Exception {
		_keyStore = KeyStore.getInstance(_keystoreType);

		File file = new File(_keystorePath);

		if (file.exists()) {
			try (FileInputStream fileInputStream = new FileInputStream(file)) {
				_keyStore.load(fileInputStream, _keystorePassword);
			}
		}
		else if (_autoCreate) {
			_keyStore.load(null, _keystorePassword);

			FileUtil.mkdirs(file.getParentFile());

			_saveKeyStore();
		}
		else {
			throw new IOException("KeyStore file not found: " + _keystorePath);
		}
	}

	private String _resolveLiferayHome(String path) {
		if (path.contains("${liferay.home}")) {
			String liferayHome = System.getProperty(
				"liferay.home", "/opt/liferay");

			return path.replace("${liferay.home}", liferayHome);
		}

		return path;
	}

	private void _saveKeyStore() throws Exception {
		try (FileOutputStream fileOutputStream = new FileOutputStream(
				_keystorePath)) {

			_keyStore.store(fileOutputStream, _keystorePassword);
		}
	}

	private static final Log _log = LogFactoryUtil.getLog(
		FileKeyStoreCryptoVaultProvider.class);

	private boolean _autoCreate;
	private KeyStore _keyStore;
	private char[] _keystorePassword;
	private String _keystorePath;
	private String _keystoreType;
	private String _providerId;

}
