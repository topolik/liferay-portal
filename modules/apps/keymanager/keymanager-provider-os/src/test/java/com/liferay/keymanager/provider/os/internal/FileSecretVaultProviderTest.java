/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.keymanager.provider.os.internal;

import com.liferay.keymanager.secret.SecretManagerException;
import com.liferay.keymanager.secret.SecureSecret;
import com.liferay.portal.kernel.util.HashMapBuilder;
import com.liferay.portal.test.rule.LiferayUnitTestRule;

import java.io.File;

import java.nio.file.Files;

import java.util.List;

import org.junit.Assert;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

/**
 * @author Tomas Polesovsky
 */
public class FileSecretVaultProviderTest {

	@ClassRule
	@Rule
	public static final LiferayUnitTestRule liferayUnitTestRule =
		LiferayUnitTestRule.INSTANCE;

	@Before
	public void setUp() throws Exception {
		_provider = new FileSecretVaultProvider();

		_secretsDir = temporaryFolder.newFolder("secrets");

		_provider.activate(
			HashMapBuilder.<String, Object>put(
				"providerId", "k8s"
			).put(
				"secretsDirectory", _secretsDir.getAbsolutePath()
			).build());
	}

	@Test
	public void testGetSecret() throws Exception {
		File secretFile = new File(_secretsDir, "my-password");

		Files.write(secretFile.toPath(), "password123".getBytes());

		try (SecureSecret secret = _provider.getSecret("my-password")) {
			Assert.assertArrayEquals(
				"password123".getBytes(), secret.getBytes());
		}
	}

	@Test
	public void testGetSecretIdentifiers() throws Exception {
		Files.write(
			new File(
				_secretsDir, "s1"
			).toPath(),
			"d1".getBytes());
		Files.write(
			new File(
				_secretsDir, "s2"
			).toPath(),
			"d2".getBytes());

		List<String> identifiers = _provider.getSecretIdentifiers();

		Assert.assertEquals(identifiers.toString(), 2, identifiers.size());
		Assert.assertTrue(identifiers.contains("s1"));
		Assert.assertTrue(identifiers.contains("s2"));
	}

	@Test(expected = SecretManagerException.class)
	public void testGetSecretNotFound() throws Exception {
		_provider.getSecret("non-existent-secret");
	}

	@Test(expected = SecretManagerException.class)
	public void testGetSecretPathTraversal() throws Exception {

		// Attempt to read a file outside the secrets directory (security check)

		_provider.getSecret("../outside-secret");
	}

	@Rule
	public TemporaryFolder temporaryFolder = new TemporaryFolder();

	private FileSecretVaultProvider _provider;
	private File _secretsDir;

}