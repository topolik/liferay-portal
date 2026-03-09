/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.keymanager.internal.crypto;

import com.liferay.keymanager.KeyReference;
import com.liferay.keymanager.crypto.CryptoKey;
import com.liferay.keymanager.crypto.CryptoManagerException;
import com.liferay.keymanager.spi.crypto.CryptoVaultProvider;
import com.liferay.osgi.service.tracker.collections.map.ServiceTrackerMap;
import com.liferay.portal.test.rule.LiferayUnitTestRule;

import java.lang.reflect.Field;

import java.util.Collections;
import java.util.List;

import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;

import org.junit.Assert;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;

import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

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
		MockitoAnnotations.openMocks(this);

		_cryptoManagerImpl = new CryptoManagerImpl();

		// Manually inject mock ServiceTrackerMap

		Field field = CryptoManagerImpl.class.getDeclaredField(
			"_serviceTrackerMap");

		field.setAccessible(true);

		field.set(_cryptoManagerImpl, _serviceTrackerMap);

		Mockito.when(
			_serviceTrackerMap.getService("test-crypto-provider")
		).thenReturn(
			_cryptoVaultProvider
		);
	}

	@Test
	public void testAddSecretKey() throws Exception {
		KeyReference keyRef = KeyReference.fromString(
			"${keyRef:test-crypto-provider:my-key}");

		KeyGenerator keyGenerator = KeyGenerator.getInstance("AES");

		keyGenerator.init(256);

		SecretKey secretKey = keyGenerator.generateKey();

		String cipherSpec =
			"cipher=AES/GCM/NoPadding;keySize=256;ivSize=12;gcmTag=128";

		CryptoKey cryptoKey = new CryptoKey(keyRef, secretKey, cipherSpec);

		_cryptoManagerImpl.addSecretKey(keyRef, cryptoKey);

		Mockito.verify(
			_cryptoVaultProvider
		).addSecretKey(
			"my-key", cryptoKey
		);
	}

	@Test
	public void testEncrypt() throws Exception {
		KeyReference keyRef = KeyReference.fromString(
			"${keyRef:test-crypto-provider:my-key}");

		byte[] plaintext = "hello".getBytes();
		byte[] ciphertext = "encrypted".getBytes();

		Mockito.when(
			_cryptoVaultProvider.encrypt("my-key", plaintext)
		).thenReturn(
			ciphertext
		);

		byte[] result = _cryptoManagerImpl.encrypt(keyRef, plaintext);

		Assert.assertArrayEquals(ciphertext, result);
	}

	@Test(expected = CryptoManagerException.class)
	public void testEncryptWrongProvider() throws Exception {
		KeyReference keyRef = KeyReference.fromString(
			"${keyRef:wrong-provider:my-key}");

		_cryptoManagerImpl.encrypt(keyRef, "data".getBytes());
	}

	@Test
	public void testGetKeyIdentifiers() throws Exception {
		List<String> identifiers = Collections.singletonList("key-1");

		Mockito.when(
			_cryptoVaultProvider.getKeyIdentifiers()
		).thenReturn(
			identifiers
		);

		List<KeyReference> result = _cryptoManagerImpl.getKeyIdentifiers(
			"test-crypto-provider");

		Assert.assertEquals(result.toString(), 1, result.size());
		Assert.assertEquals(
			"key-1",
			result.get(
				0
			).getIdentifier());
		Assert.assertEquals(
			"test-crypto-provider",
			result.get(
				0
			).getProviderId());
	}

	private CryptoManagerImpl _cryptoManagerImpl;

	@Mock
	private CryptoVaultProvider _cryptoVaultProvider;

	@Mock
	private ServiceTrackerMap<String, CryptoVaultProvider> _serviceTrackerMap;

}