/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.keymanager.provider.keystore.internal;

import com.liferay.keymanager.KeyResolverService;
import com.liferay.keymanager.SecureSecret;
import com.liferay.keymanager.provider.keystore.internal.configuration.JavaKeyStoreProviderConfiguration;
import com.liferay.keymanager.spi.BaseKeyProvider;
import com.liferay.keymanager.spi.KeyProvider;
import com.liferay.petra.string.StringBundler;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import java.security.KeyStore;

import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.Map;

import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.ConfigurationPolicy;
import org.osgi.service.component.annotations.Modified;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.metatype.annotations.Designate;

/**
 * @author Tomas Polesovsky
 */
@Component(
	configurationPid = "com.liferay.keymanager.provider.keystore.internal.configuration.JavaKeyStoreProviderConfiguration",
	configurationPolicy = ConfigurationPolicy.REQUIRE,
	property = "service.ranking:Integer=10", service = KeyProvider.class
)
@Designate(ocd = JavaKeyStoreProviderConfiguration.class)
public class JavaKeyStoreProvider extends BaseKeyProvider {

	@Override
	public boolean containsKey(String alias) throws Exception {
		return _keyStore.containsAlias(alias);
	}

	@Override
	public void deleteKey(String alias) throws Exception {
		_keyStore.deleteEntry(alias);

		try (SecureSecret secret = _resolvePassword()) {
			_saveKeyStore(secret.getChars());
		}
	}

	@Override
	public Capability[] getCapabilities() {
		return new Capability[] {
			Capability.READ, Capability.WRITE, Capability.DELETE,
			Capability.LIST
		};
	}

	@Override
	public int getInitializationPhase() {
		return 3;
	}

	@Override
	public List<String> listAliases() throws Exception {
		Enumeration<String> enumeration = _keyStore.aliases();

		List<String> result = new ArrayList<>();

		while (enumeration.hasMoreElements()) {
			result.add(enumeration.nextElement());
		}

		return result;
	}

	@Override
	public SecureSecret resolveKey(String alias, Map<String, Object> context)
		throws Exception {

		if (!_keyStore.containsAlias(alias)) {
			throw new Exception("Key not found in keystore: " + alias);
		}

		try (SecureSecret password = _resolvePassword()) {
			KeyStore.PasswordProtection passwordProtection =
				new KeyStore.PasswordProtection(password.getChars());

			KeyStore.Entry entry = _keyStore.getEntry(
				alias, passwordProtection);

			if (entry instanceof KeyStore.SecretKeyEntry) {
				KeyStore.SecretKeyEntry secretKeyEntry =
					(KeyStore.SecretKeyEntry)entry;

				SecretKey secretKey = secretKeyEntry.getSecretKey();

				return new SecureSecret(secretKey.getEncoded());
			}

			throw new Exception("Unsupported entry type for alias: " + alias);
		}
	}

	@Override
	public synchronized void storeKey(String alias, SecureSecret secret)
		throws Exception {

		// We store the secret material using SecretKeySpec with AES as a standard
		// carrier algorithm.

		SecretKey secretKey = new SecretKeySpec(secret.getBytes(), "AES");

		KeyStore.SecretKeyEntry entry = new KeyStore.SecretKeyEntry(secretKey);

		try (SecureSecret password = _resolvePassword()) {
			KeyStore.PasswordProtection passwordProtection =
				new KeyStore.PasswordProtection(password.getChars());

			_keyStore.setEntry(alias, entry, passwordProtection);

			_saveKeyStore(password.getChars());
		}
	}

	@Activate
	@Modified
	protected void activate(
		JavaKeyStoreProviderConfiguration javaKeyStoreProviderConfiguration) {

		_providerId = javaKeyStoreProviderConfiguration.providerId();
		_keystorePath = _resolveLiferayHome(
			javaKeyStoreProviderConfiguration.keystorePath());
		_keystoreType = javaKeyStoreProviderConfiguration.keystoreType();
		_autoCreate = javaKeyStoreProviderConfiguration.autoCreate();
		_passwordRef = javaKeyStoreProviderConfiguration.keystorePassword();

		try {

			// Test resolution at startup, but don't store the chars in a field

			try (SecureSecret secret = _resolvePassword()) {
				_loadOrCreateKeyStore(secret.getChars());
			}

			_available = true;

			if (_log.isInfoEnabled()) {
				_log.info(
					StringBundler.concat(
						"Java KeyStore initialized: id=", _providerId, " at ",
						_keystorePath));
			}
		}
		catch (Exception exception) {
			_available = false;

			_log.error(
				"Failed to initialize Java KeyStore provider", exception);
		}
	}

	private void _loadOrCreateKeyStore(char[] password) throws Exception {
		_keyStore = KeyStore.getInstance(_keystoreType);

		Path path = Paths.get(_keystorePath);

		if (Files.exists(path)) {
			try (FileInputStream fileInputStream = new FileInputStream(
					path.toFile())) {

				_keyStore.load(fileInputStream, password);
			}
		}
		else if (_autoCreate) {
			_keyStore.load(null, password);

			Files.createDirectories(path.getParent());

			_saveKeyStore(password);
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

	private SecureSecret _resolvePassword() throws Exception {
		if (_keyResolverService.isKeyReference(_passwordRef)) {
			return _keyResolverService.resolveSecure(_passwordRef);
		}

		return new SecureSecret(_passwordRef.toCharArray());
	}

	private void _saveKeyStore(char[] password) throws Exception {
		Path path = Paths.get(_keystorePath);

		try (FileOutputStream fileOutputStream = new FileOutputStream(
				path.toFile())) {

			_keyStore.store(fileOutputStream, password);
		}
	}

	private static final Log _log = LogFactoryUtil.getLog(
		JavaKeyStoreProvider.class);

	private volatile boolean _autoCreate;

	@Reference
	private KeyResolverService _keyResolverService;

	private KeyStore _keyStore;
	private volatile String _keystorePath;
	private volatile String _keystoreType;
	private volatile String _passwordRef;

}