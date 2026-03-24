/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.keymanager.internal.crypto;

import com.liferay.keymanager.KeyReference;
import com.liferay.keymanager.crypto.CryptoManagerException;
import com.liferay.keymanager.spi.crypto.CryptoVaultProvider;
import com.liferay.keymanager.spi.fips.FipsComplianceChecker;
import com.liferay.keymanager.spi.profile.KeyManagerProfile;
import com.liferay.keymanager.spi.profile.ProfileOrchestrator;
import com.liferay.osgi.service.tracker.collections.map.ServiceTrackerMap;
import com.liferay.portal.test.rule.LiferayUnitTestRule;

import java.lang.reflect.Field;

import java.util.Collections;
import java.util.HashSet;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

/**
 * @author Tomas Polesovsky
 */
public class CryptoManagerImplTest {

	@Before
	public void setUp() throws Exception {
		MockitoAnnotations.openMocks(this);

		_cryptoManagerImpl = new CryptoManagerImpl();

		_injectField(
			_cryptoManagerImpl, "_serviceTrackerMap", _serviceTrackerMap);

		_injectField(
			_cryptoManagerImpl, "_fipsComplianceChecker",
			_fipsComplianceChecker);

		_injectField(
			_cryptoManagerImpl, "_profileOrchestrator", _profileOrchestrator);

		Mockito.when(
			_serviceTrackerMap.getService("test-crypto-provider")
		).thenReturn(
			_cryptoVaultProvider
		);

		Mockito.when(
			_serviceTrackerMap.keySet()
		).thenReturn(
			new HashSet<>(Collections.singletonList("test-crypto-provider"))
		);

		Mockito.when(
			_cryptoVaultProvider.isAllowedCompany(Mockito.anyLong())
		).thenReturn(
			true
		);

		Mockito.when(
			_profileOrchestrator.getActiveProfile()
		).thenReturn(
			_keyManagerProfile
		);
	}

	@Test
	public void testDecrypt() throws Exception {
		KeyReference keyRef = new KeyReference(
			KeyReference.Type.CRYPTO, "test-crypto-provider", "my-key");

		byte[] ciphertext = "encrypted".getBytes();

		byte[] plaintext = "hello".getBytes();

		Mockito.when(
			_cryptoVaultProvider.decrypt(0L, "my-key", ciphertext)
		).thenReturn(
			plaintext
		);

		byte[] result = _cryptoManagerImpl.decrypt(0L, keyRef, ciphertext);

		Assert.assertArrayEquals(plaintext, result);

		Mockito.verify(
			_cryptoVaultProvider
		).decrypt(
			0L, "my-key", ciphertext
		);
	}

	@Test
	public void testEncrypt() throws Exception {
		KeyReference keyRef = new KeyReference(
			KeyReference.Type.CRYPTO, "test-crypto-provider", "my-key");

		byte[] plaintext = "hello".getBytes();

		byte[] ciphertext = "encrypted".getBytes();

		Mockito.when(
			_cryptoVaultProvider.encrypt(0L, "my-key", plaintext)
		).thenReturn(
			ciphertext
		);

		byte[] result = _cryptoManagerImpl.encrypt(0L, keyRef, plaintext);

		Assert.assertArrayEquals(ciphertext, result);

		Mockito.verify(
			_cryptoVaultProvider
		).encrypt(
			0L, "my-key", plaintext
		);
	}

	@Test(expected = CryptoManagerException.class)
	public void testEncryptNoProvider() throws Exception {
		KeyReference keyRef = new KeyReference(
			KeyReference.Type.CRYPTO, "non-existent", "my-key");

		_cryptoManagerImpl.encrypt(0L, keyRef, "hello".getBytes());
	}

	@Test
	public void testNetworkBlackoutAntiCaching() throws Exception {
		KeyReference keyRef = new KeyReference(
			KeyReference.Type.CRYPTO, "test-crypto-provider", "my-key");

		byte[] ciphertext = "encrypted".getBytes();

		byte[] plaintext1 = "hello".getBytes();

		// Mock the provider to succeed the first time, but throw an exception
		// the second time

		Mockito.when(
			_cryptoVaultProvider.decrypt(0L, "my-key", ciphertext)
		).thenReturn(
			plaintext1
		).thenThrow(
			new CryptoManagerException("Network failure")
		);

		// First call should succeed

		byte[] result1 = _cryptoManagerImpl.decrypt(0L, keyRef, ciphertext);

		Assert.assertArrayEquals(plaintext1, result1);

		// Second call should NOT return a cached value, it should hit the
		// provider and fail

		try {
			_cryptoManagerImpl.decrypt(0L, keyRef, ciphertext);

			Assert.fail(
				"Expected CryptoManagerException due to network blackout and " +
					"anti-caching mandate.");
		}
		catch (CryptoManagerException cryptoManagerException) {
			String message = cryptoManagerException.getMessage();

			Assert.assertTrue(
				message.contains("No key found for decryption: my-key"));
		}

		// Verify that the provider was indeed called twice

		Mockito.verify(
			_cryptoVaultProvider, Mockito.times(2)
		).decrypt(
			0L, "my-key", ciphertext
		);
	}

	@Test
	public void testProfileRouting() throws Exception {
		CryptoVaultProvider systemDekProvider = Mockito.mock(
			CryptoVaultProvider.class, "systemDekProvider");

		CryptoVaultProvider companyDekProvider = Mockito.mock(
			CryptoVaultProvider.class, "companyDekProvider");

		Mockito.when(
			_keyManagerProfile.getSystemDekProviderId()
		).thenReturn(
			"system-dek"
		);

		Mockito.when(
			_keyManagerProfile.getCompanyDekProviderId()
		).thenReturn(
			"company-dek"
		);

		Mockito.when(
			_serviceTrackerMap.getService("system-dek")
		).thenReturn(
			systemDekProvider
		);

		Mockito.when(
			_serviceTrackerMap.getService("company-dek")
		).thenReturn(
			companyDekProvider
		);

		Mockito.when(
			systemDekProvider.isAllowedCompany(0L)
		).thenReturn(
			true
		);

		Mockito.when(
			companyDekProvider.isAllowedCompany(1L)
		).thenReturn(
			true
		);

		KeyReference keyRef = new KeyReference(
			KeyReference.Type.CRYPTO, KeyReference.ANY_PROVIDER, "my-key");

		byte[] plaintext = "hello".getBytes();

		byte[] ciphertext = "encrypted".getBytes();

		// 1. System routing

		Mockito.when(
			systemDekProvider.encrypt(0L, "my-key", plaintext)
		).thenReturn(
			ciphertext
		);

		_cryptoManagerImpl.encrypt(0L, keyRef, plaintext);

		Mockito.verify(
			systemDekProvider
		).encrypt(
			0L, "my-key", plaintext
		);

		// 2. Company routing

		Mockito.when(
			companyDekProvider.encrypt(1L, "my-key", plaintext)
		).thenReturn(
			ciphertext
		);

		_cryptoManagerImpl.encrypt(1L, keyRef, plaintext);

		Mockito.verify(
			companyDekProvider
		).encrypt(
			1L, "my-key", plaintext
		);
	}

	@Rule
	public final LiferayUnitTestRule liferayUnitTestRule =
		new LiferayUnitTestRule();

	private void _injectField(Object target, String fieldName, Object value) {
		try {
			Field field = null;

			Class<?> clazz = target.getClass();

			while (clazz != null) {
				try {
					field = clazz.getDeclaredField(fieldName);

					break;
				}
				catch (NoSuchFieldException noSuchFieldException) {
					clazz = clazz.getSuperclass();
				}
			}

			if (field == null) {
				throw new RuntimeException("Field not found: " + fieldName);
			}

			field.setAccessible(true);

			field.set(target, value);
		}
		catch (Exception exception) {
			throw new RuntimeException(exception);
		}
	}

	private CryptoManagerImpl _cryptoManagerImpl;

	@Mock
	private CryptoVaultProvider _cryptoVaultProvider;

	@Mock
	private FipsComplianceChecker _fipsComplianceChecker;

	@Mock
	private KeyManagerProfile _keyManagerProfile;

	@Mock
	private ProfileOrchestrator _profileOrchestrator;

	@Mock
	private ServiceTrackerMap<String, CryptoVaultProvider> _serviceTrackerMap;

}