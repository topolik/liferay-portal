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

import java.util.Collections;
import java.util.List;

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
public class JavaKeyStoreProviderTest {

	@ClassRule
	@Rule
	public static final LiferayUnitTestRule liferayUnitTestRule =
		LiferayUnitTestRule.INSTANCE;

	@Before
	public void setUp() throws Exception {
		MockitoAnnotations.openMocks(this);

		File keystoreFolder = temporaryFolder.newFolder("keystore");

		File keystoreFile = new File(keystoreFolder, "test.p12");

		Mockito.when(
			_javaKeyStoreProviderConfiguration.keystorePath()
		).thenReturn(
			keystoreFile.getAbsolutePath()
		);

		Mockito.when(
			_javaKeyStoreProviderConfiguration.keystoreType()
		).thenReturn(
			"PKCS12"
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
			"keystore"
		);

		_javaKeyStoreProvider.activate(_javaKeyStoreProviderConfiguration);
	}

	@Test
	public void testDeleteKey() throws Exception {
		String alias = "delete-me";

		try (SecureSecret secret = new SecureSecret("val".toCharArray())) {
			_javaKeyStoreProvider.storeKey(alias, secret);
		}

		Assert.assertTrue(_javaKeyStoreProvider.containsKey(alias));

		_javaKeyStoreProvider.deleteKey(alias);

		Assert.assertFalse(_javaKeyStoreProvider.containsKey(alias));
	}

	@Test
	public void testListAliases() throws Exception {
		try (SecureSecret s1 = new SecureSecret("v1".toCharArray());
			SecureSecret s2 = new SecureSecret("v2".toCharArray())) {

			_javaKeyStoreProvider.storeKey("key1", s1);
			_javaKeyStoreProvider.storeKey("key2", s2);
		}

		List<String> aliases = _javaKeyStoreProvider.listAliases();

		Assert.assertTrue(aliases.contains("key1"));
		Assert.assertTrue(aliases.contains("key2"));
		Assert.assertEquals(aliases.toString(), 2, aliases.size());
	}

	@Test
	public void testStoreAndResolveKey() throws Exception {
		String alias = "my-key";
		char[] value = "my-secret-value".toCharArray();

		try (SecureSecret secret = new SecureSecret(value)) {
			_javaKeyStoreProvider.storeKey(alias, secret);
		}

		Assert.assertTrue(_javaKeyStoreProvider.containsKey(alias));

		try (SecureSecret secret = _javaKeyStoreProvider.resolveKey(
				alias, Collections.emptyMap())) {

			Assert.assertArrayEquals(value, secret.getChars());
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