/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.keymanager.internal.crypto;

import com.liferay.keymanager.KeyReference;
import com.liferay.keymanager.spi.crypto.CryptoVaultProvider;
import com.liferay.osgi.service.tracker.collections.map.ServiceTrackerMap;
import com.liferay.portal.test.rule.LiferayUnitTestRule;

import java.lang.reflect.Field;

import javax.crypto.SecretKey;

import org.junit.Assert;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.mockito.Mockito;

/**
 * @author Tomas Polesovsky
 */
public class CryptoManagerImplTest {

	@ClassRule
	@Rule
	public static final LiferayUnitTestRule liferayUnitTestRule =
		LiferayUnitTestRule.INSTANCE;

	@Before
	public void setUp() throws Exception {
		_cryptoManagerImpl = new CryptoManagerImpl();

		_mockProvider = Mockito.mock(CryptoVaultProvider.class);
		_mockServiceTrackerMap = Mockito.mock(ServiceTrackerMap.class);

		Mockito.when(
			_mockServiceTrackerMap.getService("test-provider")
		).thenReturn(
			_mockProvider
		);

		Field field = CryptoManagerImpl.class.getDeclaredField(
			"_serviceTrackerMap");

		field.setAccessible(true);

		field.set(_cryptoManagerImpl, _mockServiceTrackerMap);
	}

	@Test
	public void testGetSecretKey() throws Exception {
		KeyReference keyReference = KeyReference.fromString(
			"${keyRef:test-provider:my-key}");

		SecretKey mockKey = Mockito.mock(SecretKey.class);

		Mockito.when(
			_mockProvider.getSecretKey("my-key")
		).thenReturn(
			mockKey
		);

		SecretKey result = _cryptoManagerImpl.getSecretKey(keyReference);

		Assert.assertSame(mockKey, result);
	}

	@Test(expected = Exception.class)
	public void testGetSecretKeyWrongType() throws Exception {
		KeyReference keyReference = KeyReference.fromString(
			"${secretRef:test-provider:my-secret}");

		_cryptoManagerImpl.getSecretKey(keyReference);
	}

	@Test(expected = Exception.class)
	public void testGetSecretKeyUnknownProvider() throws Exception {
		KeyReference keyReference = KeyReference.fromString(
			"${keyRef:unknown:my-key}");

		_cryptoManagerImpl.getSecretKey(keyReference);
	}

	private CryptoManagerImpl _cryptoManagerImpl;
	private CryptoVaultProvider _mockProvider;
	private ServiceTrackerMap<String, CryptoVaultProvider> _mockServiceTrackerMap;

}
