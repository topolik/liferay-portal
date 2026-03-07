/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.keymanager.provider.db.internal;

import com.liferay.document.library.kernel.exception.NoSuchFileException;
import com.liferay.document.library.kernel.store.Store;
import com.liferay.keymanager.crypto.CryptoManagerException;
import com.liferay.keymanager.provider.db.internal.configuration.DLKeyStoreCryptoVaultProviderConfiguration;
import com.liferay.keymanager.spi.crypto.CryptoVaultProvider;
import com.liferay.petra.string.StringBundler;
import com.liferay.portal.configuration.metatype.bnd.util.ConfigurableUtil;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.model.CompanyConstants;
import com.liferay.portal.kernel.security.auth.CompanyThreadLocal;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;

import java.nio.file.Files;
import java.nio.file.Path;

import java.security.Key;
import java.security.KeyStore;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.cert.Certificate;

import java.util.Map;
import java.util.Objects;

import javax.crypto.SecretKey;

import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.ConfigurationPolicy;
import org.osgi.service.component.annotations.Modified;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Tomas Polesovsky
 */
@Component(
	configurationPid = "com.liferay.keymanager.provider.db.internal.configuration.DLKeyStoreCryptoVaultProviderConfiguration",
	configurationPolicy = ConfigurationPolicy.REQUIRE,
	service = CryptoVaultProvider.class
)
public class DLKeyStoreCryptoVaultProvider implements CryptoVaultProvider {

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
		DLKeyStoreCryptoVaultProviderConfiguration
			dlKeyStoreCryptoVaultProviderConfiguration =
				ConfigurableUtil.createConfigurable(
					DLKeyStoreCryptoVaultProviderConfiguration.class,
					properties);

		_providerId = dlKeyStoreCryptoVaultProviderConfiguration.providerId();
		_keystoreType = dlKeyStoreCryptoVaultProviderConfiguration.keystoreType();

		String extension = "p12";

		if (Objects.equals(_keystoreType, "JKS")) {
			extension = "jks";
		}

		_keystorePath = StringBundler.concat(
			"keymanager/keystore.", extension);

		String password =
			dlKeyStoreCryptoVaultProviderConfiguration.keystorePassword();

		_keystorePassword = password.toCharArray();

		try {
			_loadKeyStore();

			if (_log.isInfoEnabled()) {
				_log.info(
					StringBundler.concat(
						"DL KeyStore Crypto Vault initialized: providerId=",
						_providerId, " at ", _keystorePath));
			}
		}
		catch (Exception exception) {
			_log.error(
				"Failed to initialize DL KeyStore crypto provider", exception);
		}
	}

	private long _getCompanyId() {
		return CompanyThreadLocal.getCompanyId();
	}

	private void _loadKeyStore() throws Exception {
		_keyStore = KeyStore.getInstance(_keystoreType);

		try (InputStream inputStream = _store.getFileAsStream(
				_getCompanyId(), CompanyConstants.SYSTEM, _keystorePath,
				Store.VERSION_DEFAULT)) {

			_keyStore.load(inputStream, _keystorePassword);
		}
		catch (NoSuchFileException noSuchFileException) {
			_keyStore.load(null, _keystorePassword);
		}
	}

	private void _saveKeyStore() throws Exception {
		Path tempPath = Files.createTempFile("keymanager-", "-keystore");

		File tempFile = tempPath.toFile();

		try {
			try (FileOutputStream fileOutputStream = new FileOutputStream(
					tempFile)) {

				_keyStore.store(fileOutputStream, _keystorePassword);
			}

			if (_store.hasFile(
					_getCompanyId(), CompanyConstants.SYSTEM, _keystorePath,
					Store.VERSION_DEFAULT)) {

				_store.deleteDirectory(
					_getCompanyId(), CompanyConstants.SYSTEM, _keystorePath);
			}

			try (FileInputStream fileInputStream = new FileInputStream(
					tempFile)) {

				_store.addFile(
					_getCompanyId(), CompanyConstants.SYSTEM, _keystorePath,
					Store.VERSION_DEFAULT, fileInputStream);
			}
		}
		finally {
			Files.deleteIfExists(tempPath);
		}
	}

	private static final Log _log = LogFactoryUtil.getLog(
		DLKeyStoreCryptoVaultProvider.class);

	private KeyStore _keyStore;
	private char[] _keystorePassword;
	private String _keystorePath;
	private String _keystoreType;
	private String _providerId;

	@Reference(target = "(default=true)")
	private Store _store;

}
