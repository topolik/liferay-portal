/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.keymanager.provider.keystore.internal;

import com.liferay.keymanager.KeyResolverService;
import com.liferay.keymanager.SecureSecret;
import com.liferay.keymanager.provider.keystore.internal.configuration.JavaKeyStoreProviderConfiguration;
import com.liferay.portal.test.rule.LiferayUnitTestRule;

import java.io.File;

import org.junit.Assert;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

/**
 * @author Tomas Polesovsky
 */
public class JavaJKSKeyStoreProviderTest {

	@ClassRule
	@Rule
	public static final LiferayUnitTestRule liferayUnitTestRule =
		LiferayUnitTestRule.INSTANCE;

	@Before
	public void setUp() throws Exception {
		MockitoAnnotations.openMocks(this);

		File keystoreFolder = temporaryFolder.newFolder("keystore-jks");

		File keystoreFile = new File(keystoreFolder, "test.jks");

		Mockito.when(
			_javaKeyStoreProviderConfiguration.keystorePath()
		).thenReturn(
			keystoreFile.getAbsolutePath()
		);

		Mockito.when(
			_javaKeyStoreProviderConfiguration.keystoreType()
		).thenReturn(
			"JKS"
		);

		Mockito.when(
			_javaKeyStoreProviderConfiguration.keystorePassword()
		).thenReturn(
			"password"
		);

		Mockito.when(
			_javaKeyStoreProviderConfiguration.autoCreate()
		).thenReturn(
			true
		);

		Mockito.when(
			_javaKeyStoreProviderConfiguration.providerId()
		).thenReturn(
			"keystore-jks"
		);

		_javaKeyStoreProvider.activate(_javaKeyStoreProviderConfiguration);
	}

	@Test
	public void testJKSAvailable() {
		Assert.assertTrue(_javaKeyStoreProvider.isAvailable());
	}

	@Test
	public void testStoreKeyFailsForJKS() {

		// JKS format cannot store SecretKey (AES/RAW) material.

		String alias = "my-key";

		try (SecureSecret secret = new SecureSecret("val".toCharArray())) {
			_javaKeyStoreProvider.storeKey(alias, secret);

			if (!_javaKeyStoreProvider.containsKey(alias)) {
				Assert.assertTrue("Should have thrown exception", false);
			}
		}
		catch (Exception exception) {
			String message = exception.getMessage();

			if ((message != null) && !message.contains("JKS") &&
				!message.contains("SecretKey") &&
				!message.contains("private keys")) {

				Assert.assertTrue(
					"Unexpected error message: " + message, false);
			}
		}
	}

	@Rule
	public TemporaryFolder temporaryFolder = new TemporaryFolder();

	@InjectMocks
	private JavaKeyStoreProvider _javaKeyStoreProvider;

	@Mock
	private JavaKeyStoreProviderConfiguration
		_javaKeyStoreProviderConfiguration;

	@Mock
	private KeyResolverService _keyResolverService;

}