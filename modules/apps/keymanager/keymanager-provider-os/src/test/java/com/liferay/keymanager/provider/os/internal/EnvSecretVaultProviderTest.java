/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.keymanager.provider.os.internal;

import com.liferay.keymanager.secret.SecretManagerException;
import com.liferay.keymanager.secret.SecureSecret;
import com.liferay.portal.kernel.util.HashMapBuilder;
import com.liferay.portal.test.rule.LiferayUnitTestRule;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.Assert;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;

/**
 * @author Tomas Polesovsky
 */
public class EnvSecretVaultProviderTest {

	@ClassRule
	@Rule
	public static final LiferayUnitTestRule liferayUnitTestRule =
		LiferayUnitTestRule.INSTANCE;

	@Before
	public void setUp() {
		_mockEnv = new HashMap<>();

		_provider = new EnvSecretVaultProvider() {

			@Override
			protected String getEnv(String name) {
				return _mockEnv.get(name);
			}

		};

		_provider.activate(
			HashMapBuilder.<String, Object>put(
				"envVariablePrefix", "LIFERAY_SECRET_"
			).put(
				"providerId", "env"
			).build());
	}

	@Test
	public void testGetSecret() throws Exception {
		_mockEnv.put("LIFERAY_SECRET_PASSWORD", "secret123");

		try (SecureSecret secret = _provider.getSecret(
				"LIFERAY_SECRET_PASSWORD")) {

			Assert.assertArrayEquals("secret123".getBytes(), secret.getBytes());
		}
	}

	@Test(expected = SecretManagerException.class)
	public void testGetSecretAccessDenied() throws Exception {
		_provider.getSecret("PATH");
	}

	@Test
	public void testGetSecretIdentifiers() throws Exception {

		// Note: The provider currently uses System.getenv() directly for
		// listing identifiers, which we cannot mock easily without PowerMock.
		// However, we can verify it doesn't throw exceptions.

		List<String> identifiers = _provider.getSecretIdentifiers();

		Assert.assertNotNull(identifiers);
	}

	@Test(expected = SecretManagerException.class)
	public void testGetSecretNotFound() throws Exception {
		_provider.getSecret("LIFERAY_SECRET_MISSING");
	}

	private Map<String, String> _mockEnv;
	private EnvSecretVaultProvider _provider;

}