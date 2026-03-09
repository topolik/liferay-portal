/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.keymanager.internal.crypto;

import com.liferay.keymanager.KeyReference;
import com.liferay.keymanager.crypto.CryptoKeyMetadata;
import com.liferay.keymanager.crypto.CryptoManagerException;
import com.liferay.keymanager.spi.crypto.CryptoVaultProvider;
import com.liferay.osgi.service.tracker.collections.map.ServiceTrackerMap;
import com.liferay.portal.test.rule.LiferayUnitTestRule;

import java.lang.reflect.Field;

import java.security.Key;

import java.util.Collections;
import java.util.List;

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

		Mockito.when(
			_serviceTrackerMap.keySet()
		).thenReturn(
			Collections.singleton("test-crypto-provider")
		);
	}

	@Test
	public void testDecrypt() throws Exception {
		KeyReference keyRef = KeyReference.fromString(
			"${keyRef:test-crypto-provider:my-key}");

		byte[] ciphertext = "ciphertext".getBytes();
		byte[] plaintext = "plaintext".getBytes();

		Mockito.when(
			_cryptoVaultProvider.decrypt("my-key", ciphertext)
		).thenReturn(
			plaintext
		);

		byte[] result = _cryptoManagerImpl.decrypt(keyRef, ciphertext);

		Assert.assertArrayEquals(plaintext, result);
	}

	@Test
	public void testDeleteKey() throws Exception {
		KeyReference keyRef = KeyReference.fromString(
			"${keyRef:test-crypto-provider:my-key}");

		_cryptoManagerImpl.deleteKey(keyRef);

		Mockito.verify(
			_cryptoVaultProvider
		).deleteKey(
			"my-key"
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
	public void testGenerateAsymmetricKeyPair() throws Exception {
		Mockito.when(
			_cryptoVaultProvider.generateAsymmetricKeyPair("my-key", "RSA")
		).thenReturn(
			"my-key"
		);

		KeyReference result = _cryptoManagerImpl.generateAsymmetricKeyPair(
			"test-crypto-provider", "my-key", "RSA");

		Assert.assertEquals("test-crypto-provider", result.getProviderId());
		Assert.assertEquals("my-key", result.getIdentifier());
	}

	@Test
	public void testGenerateSecretKey() throws Exception {
		Mockito.when(
			_cryptoVaultProvider.generateSecretKey("my-key", "AES")
		).thenReturn(
			"my-key"
		);

		KeyReference result = _cryptoManagerImpl.generateSecretKey(
			"test-crypto-provider", "my-key", "AES");

		Assert.assertEquals("test-crypto-provider", result.getProviderId());
		Assert.assertEquals("my-key", result.getIdentifier());
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

	@Test
	public void testGetKeyMetadata() throws Exception {
		KeyReference keyRef = KeyReference.fromString(
			"${keyRef:test-crypto-provider:my-key}");

		CryptoKeyMetadata metadata = new CryptoKeyMetadata(
			keyRef, "AES", "AES/GCM/NoPadding", 123456789L);

		Mockito.when(
			_cryptoVaultProvider.getKeyMetadata("my-key")
		).thenReturn(
			metadata
		);

		CryptoKeyMetadata result = _cryptoManagerImpl.getKeyMetadata(keyRef);

		Assert.assertEquals(metadata, result);
	}

	@Test
	public void testGetProviders() throws Exception {
		List<String> result = _cryptoManagerImpl.getProviders();

		Assert.assertEquals(result.toString(), 1, result.size());
		Assert.assertEquals("test-crypto-provider", result.get(0));
	}

	@Test
	public void testImportSecretKey() throws Exception {
		byte[] rawKeyMaterial = "secret".getBytes();

		Mockito.when(
			_cryptoVaultProvider.importSecretKey(
				"my-key", rawKeyMaterial, "AES")
		).thenReturn(
			"my-key"
		);

		KeyReference result = _cryptoManagerImpl.importSecretKey(
			"test-crypto-provider", "my-key", rawKeyMaterial, "AES");

		Assert.assertEquals("test-crypto-provider", result.getProviderId());
		Assert.assertEquals("my-key", result.getIdentifier());
	}

	@Test
	public void testUnwrap() throws Exception {
		KeyReference keyRef = KeyReference.fromString(
			"${keyRef:test-crypto-provider:my-key}");

		byte[] wrappedKeyBytes = "wrapped".getBytes();
		Key unwrappedKey = Mockito.mock(Key.class);

		Mockito.when(
			_cryptoVaultProvider.unwrap("my-key", wrappedKeyBytes, "AES", 1)
		).thenReturn(
			unwrappedKey
		);

		Key result = _cryptoManagerImpl.unwrap(
			keyRef, wrappedKeyBytes, "AES", 1);

		Assert.assertEquals(unwrappedKey, result);
	}

	@Test
	public void testWrap() throws Exception {
		KeyReference keyRef = KeyReference.fromString(
			"${keyRef:test-crypto-provider:my-key}");

		Key keyToWrap = Mockito.mock(Key.class);
		byte[] wrappedKeyBytes = "wrapped".getBytes();

		Mockito.when(
			_cryptoVaultProvider.wrap("my-key", keyToWrap)
		).thenReturn(
			wrappedKeyBytes
		);

		byte[] result = _cryptoManagerImpl.wrap(keyRef, keyToWrap);

		Assert.assertArrayEquals(wrappedKeyBytes, result);
	}

	private CryptoManagerImpl _cryptoManagerImpl;

	@Mock
	private CryptoVaultProvider _cryptoVaultProvider;

	@Mock
	private ServiceTrackerMap<String, CryptoVaultProvider> _serviceTrackerMap;

}