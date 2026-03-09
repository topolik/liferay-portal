/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.keymanager.provider.os.internal;

import com.liferay.keymanager.crypto.CryptoManagerException;
import com.liferay.portal.kernel.util.HashMapBuilder;
import com.liferay.portal.test.rule.LiferayUnitTestRule;

import java.io.File;

import java.security.Key;

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
public class FileKeyStoreCryptoVaultProviderTest {

	@ClassRule
	@Rule
	public static final LiferayUnitTestRule liferayUnitTestRule =
		LiferayUnitTestRule.INSTANCE;

	@Before
	public void setUp() throws Exception {
		_provider = new FileKeyStoreCryptoVaultProvider();

		_keystoreFile = new File(temporaryFolder.getRoot(), "test.jks");

		_provider.activate(
			HashMapBuilder.<String, Object>put(
				"autoCreate", true
			).put(
				"keystorePassword", "test123456"
			).put(
				"keystorePath", _keystoreFile.getAbsolutePath()
			).put(
				"keystoreType", "JCEKS"
			).put(
				"providerId", "keystore"
			).build());
	}

	@Test
	public void testAutoCreateDirectory() throws Exception {
		File deepDir = new File(
			temporaryFolder.getRoot(), "deep/path/test.jks");

		FileKeyStoreCryptoVaultProvider provider =
			new FileKeyStoreCryptoVaultProvider();

		provider.activate(
			HashMapBuilder.<String, Object>put(
				"autoCreate", true
			).put(
				"keystorePassword", "test123456"
			).put(
				"keystorePath", deepDir.getAbsolutePath()
			).put(
				"keystoreType", "JCEKS"
			).put(
				"providerId", "keystore"
			).build());

		KeyGenerator keyGenerator = KeyGenerator.getInstance("AES");

		keyGenerator.init(256);

		SecretKey secretKey = keyGenerator.generateKey();

		// Writing a key should trigger directory creation and keystore save

		provider.importSecretKey("test-key", secretKey.getEncoded(), "AES");

		Assert.assertTrue(deepDir.exists());
	}

	@Test(expected = CryptoManagerException.class)
	public void testDecrypt() throws Exception {
		_provider.decrypt("identifier", "ciphertext".getBytes());
	}

	@Test
	public void testDeleteKey() throws Exception {
		KeyGenerator keyGenerator = KeyGenerator.getInstance("AES");

		keyGenerator.init(256);

		SecretKey secretKey = keyGenerator.generateKey();

		_provider.importSecretKey("delete-me", secretKey.getEncoded(), "AES");

		Assert.assertTrue(
			_provider.getKeyIdentifiers(
			).contains(
				"delete-me"
			));

		_provider.deleteKey("delete-me");

		Assert.assertFalse(
			_provider.getKeyIdentifiers(
			).contains(
				"delete-me"
			));
	}

	@Test(expected = CryptoManagerException.class)
	public void testEncrypt() throws Exception {
		_provider.encrypt("identifier", "plaintext".getBytes());
	}

	@Test(expected = CryptoManagerException.class)
	public void testGenerateAsymmetricKeyPair() throws Exception {
		_provider.generateAsymmetricKeyPair("test-key", "RSA");
	}

	@Test(expected = CryptoManagerException.class)
	public void testGenerateSecretKey() throws Exception {
		_provider.generateSecretKey("test-key", "AES");
	}

	@Test(expected = CryptoManagerException.class)
	public void testGetKeyMetadata() throws Exception {
		_provider.getKeyMetadata("test-key");
	}

	@Test
	public void testImportSecretKey() throws Exception {
		String identifier = "my-key";

		KeyGenerator keyGenerator = KeyGenerator.getInstance("AES");

		keyGenerator.init(256);

		SecretKey secretKey = keyGenerator.generateKey();

		_provider.importSecretKey(identifier, secretKey.getEncoded(), "AES");

		// 1. Verify it's in the list

		Assert.assertTrue(
			_provider.getKeyIdentifiers(
			).contains(
				identifier
			));

		// 2. Verify Wrap/Unwrap (Operational use for KEK)

		byte[] data = new byte[32];

		for (int i = 0; i < 32; i++) {
			data[i] = (byte)i;
		}

		byte[] wrapped = _provider.wrap(identifier, secretKey);

		Assert.assertNotNull(wrapped);

		Key unwrapped = _provider.unwrap(
			identifier, wrapped, "AES", Cipher.SECRET_KEY);

		Assert.assertArrayEquals(
			secretKey.getEncoded(), unwrapped.getEncoded());
	}

	@Rule
	public TemporaryFolder temporaryFolder = new TemporaryFolder();

	private File _keystoreFile;
	private FileKeyStoreCryptoVaultProvider _provider;

}