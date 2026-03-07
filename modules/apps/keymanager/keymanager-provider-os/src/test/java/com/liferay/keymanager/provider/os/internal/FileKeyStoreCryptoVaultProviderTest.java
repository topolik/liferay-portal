/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.keymanager.provider.os.internal;

import com.liferay.portal.kernel.util.HashMapBuilder;
import com.liferay.portal.test.rule.LiferayUnitTestRule;

import java.io.File;

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

	@Rule
	public TemporaryFolder temporaryFolder = new TemporaryFolder();

	@Before
	public void setUp() throws Exception {
		_provider = new FileKeyStoreCryptoVaultProvider();

		// Ensure the file doesn't exist to trigger auto-create

		_keystoreFile = new File(
			temporaryFolder.getRoot(), "data/keystore.p12");

		_provider.activate(
			HashMapBuilder.<String, Object>put(
				"autoCreate", true
			).put(
				"providerId", "keystore"
			).put(
				"keystorePassword", "password"
			).put(
				"keystorePath", _keystoreFile.getAbsolutePath()
			).put(
				"keystoreType", "PKCS12"
			).build());
	}

	@Test
	public void testAutoCreateDirectory() {
		// Verify that 'data/' directory was created during activation

		Assert.assertTrue(_keystoreFile.getParentFile().exists());
	}

	@Test
	public void testDeleteKey() throws Exception {
		KeyGenerator keyGenerator = KeyGenerator.getInstance("AES");

		keyGenerator.init(256);

		SecretKey secretKey = keyGenerator.generateKey();

		_provider.putSecretKey("delete-me", secretKey);

		Assert.assertNotNull(_provider.getSecretKey("delete-me"));

		_provider.deleteKey("delete-me");

		Assert.assertNull(_provider.getSecretKey("delete-me"));
	}

	@Test
	public void testPutAndGetSecretKey() throws Exception {
		KeyGenerator keyGenerator = KeyGenerator.getInstance("AES");

		keyGenerator.init(256);

		SecretKey secretKey = keyGenerator.generateKey();

		_provider.putSecretKey("my-key", secretKey);

		// 1. Verify in-memory retrieval

		SecretKey result = _provider.getSecretKey("my-key");

		Assert.assertNotNull(result);
		Assert.assertArrayEquals(secretKey.getEncoded(), result.getEncoded());

		// 2. Verify persistence (reload KeyStore in a new provider instance)

		FileKeyStoreCryptoVaultProvider newProvider =
			new FileKeyStoreCryptoVaultProvider();

		newProvider.activate(
			HashMapBuilder.<String, Object>put(
				"autoCreate", false
			).put(
				"providerId", "keystore-reloaded"
			).put(
				"keystorePassword", "password"
			).put(
				"keystorePath", _keystoreFile.getAbsolutePath()
			).put(
				"keystoreType", "PKCS12"
			).build());

		SecretKey reloadedKey = newProvider.getSecretKey("my-key");

		Assert.assertNotNull(reloadedKey);
		Assert.assertArrayEquals(
			secretKey.getEncoded(), reloadedKey.getEncoded());
	}

	private File _keystoreFile;
	private FileKeyStoreCryptoVaultProvider _provider;

}
