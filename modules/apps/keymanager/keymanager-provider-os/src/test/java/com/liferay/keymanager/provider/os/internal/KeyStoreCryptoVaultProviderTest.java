/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.keymanager.provider.os.internal;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import com.liferay.keymanager.KeyReference;
import com.liferay.keymanager.crypto.CryptoKey;
import com.liferay.keymanager.crypto.CryptoManagerException;
import com.liferay.keymanager.secret.SecretManager;
import com.liferay.keymanager.secret.SecureSecret;
import com.liferay.portal.kernel.util.HashMapBuilder;
import com.liferay.portal.test.rule.LiferayUnitTestRule;

import java.io.File;

import java.nio.charset.StandardCharsets;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;

import org.junit.Assert;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

/**
 * @author Tomas Polesovsky
 */
public class KeyStoreCryptoVaultProviderTest {

	@ClassRule
	@Rule
	public static final LiferayUnitTestRule liferayUnitTestRule =
		LiferayUnitTestRule.INSTANCE;

	@Before
	public void setUp() throws Exception {
		_provider = new KeyStoreCryptoVaultProvider();

		_secretManager = mock(SecretManager.class);

		_provider.setSecretManager(_secretManager);

		when(
			_secretManager.getSecret(anyLong(), any(KeyReference.class))
		).thenAnswer(
			invocation -> new SecureSecret(
				(KeyReference)invocation.getArgument(1),
				"test123456".getBytes(StandardCharsets.UTF_8))
		);

		_keystoreFile = new File(temporaryFolder.getRoot(), "test.jks");

		_provider.activate(
			HashMapBuilder.<String, Object>put(
				"autoCreate", true
			).put(
				"enabled", true
			).put(
				"keystorePassword", "${secretRef:env:KEYSTORE_PASSWORD}"
			).put(
				"keystorePath", _keystoreFile.getAbsolutePath()
			).put(
				"keystoreType", "PKCS12"
			).put(
				"priority", 100
			).put(
				"providerId", "keystore"
			).build());
	}

	@Test
	public void testAutoCreateDirectory() throws Exception {
		File deepDir = new File(
			temporaryFolder.getRoot(), "deep/path/test.jks");

		KeyStoreCryptoVaultProvider provider =
			new KeyStoreCryptoVaultProvider();

		provider.setSecretManager(_secretManager);

		provider.activate(
			HashMapBuilder.<String, Object>put(
				"autoCreate", true
			).put(
				"enabled", true
			).put(
				"keystorePassword", "${secretRef:env:KEYSTORE_PASSWORD}"
			).put(
				"keystorePath", deepDir.getAbsolutePath()
			).put(
				"keystoreType", "PKCS12"
			).put(
				"priority", 100
			).put(
				"providerId", "keystore"
			).build());

		KeyGenerator keyGenerator = KeyGenerator.getInstance("AES");

		keyGenerator.init(256);

		SecretKey secretKey = keyGenerator.generateKey();

		// Writing a key should trigger directory creation and keystore save

		provider.importSecretKey(0L, "test-key", secretKey.getEncoded(), "AES");

		Assert.assertTrue(deepDir.exists());
	}

	@Test(expected = CryptoManagerException.class)
	public void testDecryptNotFound() throws Exception {
		_provider.decrypt(0L, "non-existent", "ciphertext".getBytes());
	}

	@Test
	public void testDeleteKey() throws Exception {
		KeyGenerator keyGenerator = KeyGenerator.getInstance("AES");

		keyGenerator.init(256);

		SecretKey secretKey = keyGenerator.generateKey();

		_provider.importSecretKey(0L, "delete-me", secretKey.getEncoded(), "AES");

		Assert.assertTrue(
			_provider.getKeyIdentifiers(0L).contains("delete-me"));

		_provider.deleteKey(0L, "delete-me");

		Assert.assertFalse(
			_provider.getKeyIdentifiers(0L).contains("delete-me"));
	}

	@Test(expected = CryptoManagerException.class)
	public void testEncryptNotFound() throws Exception {
		_provider.encrypt(0L, "non-existent", "plaintext".getBytes());
	}

	@Test(expected = CryptoManagerException.class)
	public void testGenerateAsymmetricKeyPair() throws Exception {
		_provider.generateAsymmetricKeyPair(0L, "test-key", "RSA");
	}

	@Test
	public void testGenerateSecretKey() throws Exception {
		_provider.generateSecretKey(0L, "test-key-gen", "AES");

		Assert.assertTrue(
			_provider.getKeyIdentifiers(0L).contains("test-key-gen"));
	}

	@Test
	public void testGetKeyMetadata() throws Exception {
		KeyGenerator keyGenerator = KeyGenerator.getInstance("AES");

		keyGenerator.init(256);

		SecretKey secretKey = keyGenerator.generateKey();

		_provider.importSecretKey(0L, "meta-key", secretKey.getEncoded(), "AES");

		CryptoKey metadata = _provider.getKeyMetadata(0L, "meta-key");

		Assert.assertNotNull(metadata);
		Assert.assertEquals("AES", metadata.getAlgorithm());
	}

	@Test
	public void testImportSecretKey() throws Exception {
		String identifier = "my-key";

		KeyGenerator keyGenerator = KeyGenerator.getInstance("AES");

		keyGenerator.init(256);

		SecretKey secretKey = keyGenerator.generateKey();

		_provider.importSecretKey(
			0L, identifier, secretKey.getEncoded(), "AES");

		// 1. Verify it's in the list

		Assert.assertTrue(
			_provider.getKeyIdentifiers(0L).contains(identifier));
	}

	@Test(expected = CryptoManagerException.class)
	public void testWrapNotSupported() throws Exception {
		KeyGenerator keyGenerator = KeyGenerator.getInstance("AES");

		keyGenerator.init(256);

		SecretKey secretKey = keyGenerator.generateKey();

		_provider.wrap(0L, "any", secretKey);
	}

	@Test(expected = CryptoManagerException.class)
	public void testUnwrapNotSupported() throws Exception {
		_provider.unwrap(0L, "any", new byte[0], "AES", Cipher.SECRET_KEY);
	}

	@Rule
	public TemporaryFolder temporaryFolder = new TemporaryFolder();

	private File _keystoreFile;
	private KeyStoreCryptoVaultProvider _provider;
	private SecretManager _secretManager;

}