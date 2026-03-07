/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.keymanager.internal.secret;

import com.liferay.keymanager.KeyReference;
import com.liferay.keymanager.secret.SecureSecret;
import com.liferay.keymanager.spi.secret.SecretVaultProvider;
import com.liferay.osgi.service.tracker.collections.map.ServiceTrackerMap;
import com.liferay.portal.test.rule.LiferayUnitTestRule;

import java.lang.reflect.Field;

import org.junit.Assert;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.mockito.Mockito;

/**
 * @author Tomas Polesovsky
 */
public class SecretManagerImplTest {

	@ClassRule
	@Rule
	public static final LiferayUnitTestRule liferayUnitTestRule =
		LiferayUnitTestRule.INSTANCE;

	@Before
	public void setUp() throws Exception {
		_secretManagerImpl = new SecretManagerImpl();

		_mockProvider = Mockito.mock(SecretVaultProvider.class);
		_mockServiceTrackerMap = Mockito.mock(ServiceTrackerMap.class);

		Mockito.when(
			_mockServiceTrackerMap.getService("test-vault")
		).thenReturn(
			_mockProvider
		);

		Field field = SecretManagerImpl.class.getDeclaredField(
			"_serviceTrackerMap");

		field.setAccessible(true);

		field.set(_secretManagerImpl, _mockServiceTrackerMap);
	}

	@Test
	public void testGetSecret() throws Exception {
		KeyReference keyReference = KeyReference.fromString(
			"${secretRef:test-vault:my-secret}");

		SecureSecret mockSecret = Mockito.mock(SecureSecret.class);

		Mockito.when(
			_mockProvider.getSecret("my-secret")
		).thenReturn(
			mockSecret
		);

		SecureSecret result = _secretManagerImpl.getSecret(keyReference);

		Assert.assertSame(mockSecret, result);
	}

	@Test(expected = Exception.class)
	public void testGetSecretWrongType() throws Exception {
		KeyReference keyReference = KeyReference.fromString(
			"${keyRef:test-vault:my-key}");

		_secretManagerImpl.getSecret(keyReference);
	}

	private SecretManagerImpl _secretManagerImpl;
	private SecretVaultProvider _mockProvider;
	private ServiceTrackerMap<String, SecretVaultProvider> _mockServiceTrackerMap;

}
