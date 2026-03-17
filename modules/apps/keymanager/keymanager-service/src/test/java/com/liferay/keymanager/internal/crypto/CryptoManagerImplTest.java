/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.keymanager.internal.crypto;

import com.liferay.keymanager.KeyReference;
import com.liferay.keymanager.crypto.CryptoKey;
import com.liferay.keymanager.crypto.CryptoManagerException;
import com.liferay.keymanager.spi.crypto.CryptoVaultProvider;
import com.liferay.portal.test.rule.LiferayUnitTestRule;

import java.lang.reflect.Field;

import java.security.Key;

import java.util.Collections;
import java.util.List;
import java.util.SortedMap;
import java.util.TreeMap;

import org.junit.Assert;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;

import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import org.osgi.framework.ServiceReference;
import org.osgi.util.tracker.ServiceTracker;

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

		// Manually inject mock ServiceTracker

		Field field = CryptoManagerImpl.class.getDeclaredField("_serviceTracker");

		field.setAccessible(true);

		field.set(_cryptoManagerImpl, _serviceTracker);

		ServiceReference<CryptoVaultProvider> serviceReference =
			Mockito.mock(ServiceReference.class);

		Mockito.when(
			serviceReference.getProperty("providerId")
		).thenReturn(
			"test-crypto-provider"
		);

		SortedMap<ServiceReference<CryptoVaultProvider>, CryptoVaultProvider>
			tracked = new TreeMap<>();

		tracked.put(serviceReference, _cryptoVaultProvider);

		Mockito.when(
			_serviceTracker.getTracked()
		).thenReturn(
			tracked
		);

		Mockito.when(
			_cryptoVaultProvider.isAllowedCompany(Mockito.anyLong())
		).thenReturn(
			true
		);
	}

	@Test
	public void testDecrypt() throws Exception {
		KeyReference keyRef = KeyReference.fromString(
			"${keyRef:test-crypto-provider:my-key}");

		byte[] ciphertext = "ciphertext".getBytes();
		byte[] plaintext = "plaintext".getBytes();

		Mockito.when(
			_cryptoVaultProvider.decrypt(0L, "my-key", ciphertext)
		).thenReturn(
			plaintext
		);

		byte[] result = _cryptoManagerImpl.decrypt(0L, keyRef, ciphertext);

		Assert.assertArrayEquals(plaintext, result);
	}

	@Test
	public void testDeleteKey() throws Exception {
		KeyReference keyRef = KeyReference.fromString(
			"${keyRef:test-crypto-provider:my-key}");

		_cryptoManagerImpl.deleteKey(0L, keyRef);

		Mockito.verify(
			_cryptoVaultProvider
		).deleteKey(
			0L, "my-key"
		);
	}

	@Test
	public void testEncrypt() throws Exception {
		KeyReference keyRef = KeyReference.fromString(
			"${keyRef:test-crypto-provider:my-key}");

		byte[] plaintext = "hello".getBytes();
		byte[] ciphertext = "encrypted".getBytes();

		Mockito.when(
			_cryptoVaultProvider.encrypt(0L, "my-key", plaintext)
		).thenReturn(
			ciphertext
		);

		byte[] result = _cryptoManagerImpl.encrypt(0L, keyRef, plaintext);

		Assert.assertArrayEquals(ciphertext, result);
	}

	@Test(expected = CryptoManagerException.class)
	public void testEncryptWrongProvider() throws Exception {
		KeyReference keyRef = KeyReference.fromString(
			"${keyRef:wrong-provider:my-key}");

		_cryptoManagerImpl.encrypt(0L, keyRef, "data".getBytes());
	}

	@Test
	public void testGenerateAsymmetricKeyPair() throws Exception {
		Mockito.when(
			_cryptoVaultProvider.generateAsymmetricKeyPair(0L, "my-key", "RSA")
		).thenReturn(
			"my-key"
		);

		KeyReference result = _cryptoManagerImpl.generateAsymmetricKeyPair(
			0L, "test-crypto-provider", "my-key", "RSA");

		Assert.assertEquals("test-crypto-provider", result.getProviderId());
		Assert.assertEquals("my-key", result.getIdentifier());
	}

	@Test
	public void testGenerateSecretKey() throws Exception {
		Mockito.when(
			_cryptoVaultProvider.generateSecretKey(0L, "my-key", "AES")
		).thenReturn(
			"my-key"
		);

		KeyReference result = _cryptoManagerImpl.generateSecretKey(
			0L, "test-crypto-provider", "my-key", "AES");

		Assert.assertEquals("test-crypto-provider", result.getProviderId());
		Assert.assertEquals("my-key", result.getIdentifier());
	}

	@Test
	public void testGetKeyIdentifiers() throws Exception {
		List<String> identifiers = Collections.singletonList("key-1");

		Mockito.when(
			_cryptoVaultProvider.getKeyIdentifiers(0L)
		).thenReturn(
			identifiers
		);

		List<KeyReference> result = _cryptoManagerImpl.getKeyIdentifiers(
			0L, "test-crypto-provider");

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

		CryptoKey metadata = new CryptoKey(
			keyRef, "AES", "AES/GCM/NoPadding", 123456789L);

		Mockito.when(
			_cryptoVaultProvider.getKeyMetadata(0L, "my-key")
		).thenReturn(
			metadata
		);

		CryptoKey result = _cryptoManagerImpl.getKeyMetadata(0L, keyRef);

		Assert.assertEquals(metadata, result);
	}

	@Test
	public void testGetProviders() throws Exception {
		List<String> result = _cryptoManagerImpl.getProviders(0L);

		Assert.assertEquals(result.toString(), 1, result.size());
		Assert.assertEquals("test-crypto-provider", result.get(0));
	}

	@Test
	public void testImportSecretKey() throws Exception {
		byte[] rawKeyMaterial = "secret".getBytes();

		Mockito.when(
			_cryptoVaultProvider.importSecretKey(
				0L, "my-key", rawKeyMaterial, "AES")
		).thenReturn(
			"my-key"
		);

		KeyReference result = _cryptoManagerImpl.importSecretKey(
			0L, "test-crypto-provider", "my-key", rawKeyMaterial, "AES");

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
			_cryptoVaultProvider.unwrap(0L, "my-key", wrappedKeyBytes, "AES", 1)
		).thenReturn(
			unwrappedKey
		);

		Key result = _cryptoManagerImpl.unwrap(
			0L, keyRef, wrappedKeyBytes, "AES", 1);

		Assert.assertEquals(unwrappedKey, result);
	}

	@Test
	public void testWrap() throws Exception {
		KeyReference keyRef = KeyReference.fromString(
			"${keyRef:test-crypto-provider:my-key}");

		Key keyToWrap = Mockito.mock(Key.class);
		byte[] wrappedKeyBytes = "wrapped".getBytes();

		Mockito.when(
			_cryptoVaultProvider.wrap(0L, "my-key", keyToWrap)
		).thenReturn(
			wrappedKeyBytes
		);

		byte[] result = _cryptoManagerImpl.wrap(0L, keyRef, keyToWrap);

		Assert.assertArrayEquals(wrappedKeyBytes, result);
	}

	@Test
	public void testDecryptAnyProvider() throws Exception {
		KeyReference keyRef = new KeyReference(
			KeyReference.Type.CRYPTO, KeyReference.ANY_PROVIDER, "my-key");

		byte[] ciphertext = "ciphertext".getBytes();
		byte[] plaintext = "plaintext".getBytes();

		Mockito.when(
			_cryptoVaultProvider.decrypt(0L, "my-key", ciphertext)
		).thenReturn(
			plaintext
		);

		byte[] result = _cryptoManagerImpl.decrypt(0L, keyRef, ciphertext);

		Assert.assertArrayEquals(plaintext, result);
	}

	@Test
	public void testEncryptAnyProvider() throws Exception {
		KeyReference keyRef = new KeyReference(
			KeyReference.Type.CRYPTO, KeyReference.ANY_PROVIDER, "my-key");

		byte[] plaintext = "hello".getBytes();
		byte[] ciphertext = "encrypted".getBytes();

		Mockito.when(
			_cryptoVaultProvider.encrypt(0L, "my-key", plaintext)
		).thenReturn(
			ciphertext
		);

		byte[] result = _cryptoManagerImpl.encrypt(0L, keyRef, plaintext);

		Assert.assertArrayEquals(ciphertext, result);
	}

	@Test
	public void testGetKeyMetadataAnyProvider() throws Exception {
		KeyReference keyRef = new KeyReference(
			KeyReference.Type.CRYPTO, KeyReference.ANY_PROVIDER, "my-key");

		CryptoKey metadata = new CryptoKey(
			keyRef, "AES", "AES/GCM/NoPadding", 123456789L);

		Mockito.when(
			_cryptoVaultProvider.getKeyMetadata(0L, "my-key")
		).thenReturn(
			metadata
		);

		CryptoKey result = _cryptoManagerImpl.getKeyMetadata(0L, keyRef);

		Assert.assertEquals(metadata, result);
	}

	@Test
	public void testUnwrapAnyProvider() throws Exception {
		KeyReference keyRef = new KeyReference(
			KeyReference.Type.CRYPTO, KeyReference.ANY_PROVIDER, "my-key");

		byte[] wrappedKeyBytes = "wrapped".getBytes();
		Key unwrappedKey = Mockito.mock(Key.class);

		Mockito.when(
			_cryptoVaultProvider.unwrap(0L, "my-key", wrappedKeyBytes, "AES", 1)
		).thenReturn(
			unwrappedKey
		);

		Key result = _cryptoManagerImpl.unwrap(
			0L, keyRef, wrappedKeyBytes, "AES", 1);

		Assert.assertEquals(unwrappedKey, result);
	}

	@Test
	public void testWrapAnyProvider() throws Exception {
		KeyReference keyRef = new KeyReference(
			KeyReference.Type.CRYPTO, KeyReference.ANY_PROVIDER, "my-key");

		Key keyToWrap = Mockito.mock(Key.class);
		byte[] wrappedKeyBytes = "wrapped".getBytes();

		Mockito.when(
			_cryptoVaultProvider.wrap(0L, "my-key", keyToWrap)
		).thenReturn(
			wrappedKeyBytes
		);

		byte[] result = _cryptoManagerImpl.wrap(0L, keyRef, keyToWrap);

		Assert.assertArrayEquals(wrappedKeyBytes, result);
	}

	private CryptoManagerImpl _cryptoManagerImpl;

	@Mock
	private CryptoVaultProvider _cryptoVaultProvider;

	@Mock
	private ServiceTracker<CryptoVaultProvider, CryptoVaultProvider>
		_serviceTracker;

}