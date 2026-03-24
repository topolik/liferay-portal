/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.keymanager.provider.db.internal;

import com.liferay.keymanager.KeyReference;
import com.liferay.keymanager.crypto.CryptoKey;
import com.liferay.keymanager.crypto.CryptoManager;
import com.liferay.keymanager.provider.db.internal.crypto.DBCompanyCryptoVaultProvider;
import com.liferay.keymanager.provider.db.internal.crypto.DBSystemCryptoVaultProvider;
import com.liferay.keymanager.provider.db.model.KeyEntry;
import com.liferay.keymanager.provider.db.service.KeyEntryLocalService;
import com.liferay.portal.kernel.service.CompanyLocalService;
import com.liferay.portal.kernel.util.HashMapBuilder;
import com.liferay.portal.test.rule.LiferayUnitTestRule;

import java.io.ByteArrayInputStream;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

import java.security.Key;

import java.sql.Blob;

import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Map;

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
public class DBCryptoVaultProviderTest {

	@ClassRule
	@Rule
	public static final LiferayUnitTestRule liferayUnitTestRule =
		new LiferayUnitTestRule();

	@Before
	public void setUp() throws Exception {
		MockitoAnnotations.openMocks(this);

		_dbCryptoVaultProvider = new DBCompanyCryptoVaultProvider();

		_injectField(
			_dbCryptoVaultProvider, "companyLocalService",
			_companyLocalService);

		_injectField(_dbCryptoVaultProvider, "cryptoManager", _cryptoManager);

		_injectField(
			_dbCryptoVaultProvider, "keyEntryLocalService",
			_keyEntryLocalService);

		_activate(
			_dbCryptoVaultProvider,
			HashMapBuilder.<String, Object>put(
				"companyId", _COMPANY_ID
			).put(
				"masterKeyReference", "${keyRef:*:db-vault-provider-master-kek}"
			).put(
				"providerId", "db"
			).build());
	}

	@Test
	public void testDecrypt() throws Exception {
		String identifier = "test-key";

		byte[] ciphertext = "encrypted-data".getBytes();

		byte[] plaintext = "decrypted-data".getBytes();

		KeyEntry keyEntry = Mockito.mock(KeyEntry.class);

		Mockito.when(
			keyEntry.getAlgorithm()
		).thenReturn(
			"AES"
		);

		Mockito.when(
			keyEntry.getCipherSpec()
		).thenReturn(
			"AES/GCM/NoPadding"
		);

		Blob ciphertextBlob = Mockito.mock(Blob.class);

		Mockito.when(
			ciphertextBlob.getBinaryStream()
		).thenReturn(
			new ByteArrayInputStream(ciphertext)
		);

		Mockito.when(
			keyEntry.getWrappedKeyBlob()
		).thenReturn(
			ciphertextBlob
		);

		Mockito.when(
			_keyEntryLocalService.fetchKeyEntry(_COMPANY_ID, identifier)
		).thenReturn(
			keyEntry
		);

		Mockito.when(
			_cryptoManager.decrypt(
				Mockito.eq(_COMPANY_ID), Mockito.any(KeyReference.class),
				Mockito.any(byte[].class))
		).thenReturn(
			plaintext
		);

		byte[] result = _dbCryptoVaultProvider.decrypt(
			_COMPANY_ID, identifier, ciphertext);

		Assert.assertArrayEquals(plaintext, result);
	}

	@Test
	public void testDeleteKey() throws Exception {
		String identifier = "test-key";

		KeyEntry keyEntry = Mockito.mock(KeyEntry.class);

		Mockito.when(
			_keyEntryLocalService.fetchKeyEntry(_COMPANY_ID, identifier)
		).thenReturn(
			keyEntry
		);

		_dbCryptoVaultProvider.deleteKey(_COMPANY_ID, identifier);

		Mockito.verify(
			_keyEntryLocalService
		).deleteKeyEntry(
			keyEntry
		);
	}

	@Test
	public void testEncrypt() throws Exception {
		String identifier = "test-key";

		byte[] plaintext = "decrypted-data".getBytes();

		byte[] ciphertext = "encrypted-data".getBytes();

		KeyEntry keyEntry = Mockito.mock(KeyEntry.class);

		Mockito.when(
			keyEntry.getAlgorithm()
		).thenReturn(
			"AES"
		);

		Mockito.when(
			keyEntry.getCipherSpec()
		).thenReturn(
			"AES/GCM/NoPadding"
		);

		Blob ciphertextBlob = Mockito.mock(Blob.class);

		Mockito.when(
			ciphertextBlob.getBinaryStream()
		).thenReturn(
			new ByteArrayInputStream(ciphertext)
		);

		Mockito.when(
			keyEntry.getWrappedKeyBlob()
		).thenReturn(
			ciphertextBlob
		);

		Mockito.when(
			_keyEntryLocalService.fetchKeyEntry(_COMPANY_ID, identifier)
		).thenReturn(
			keyEntry
		);

		Mockito.when(
			_cryptoManager.encrypt(
				Mockito.eq(_COMPANY_ID), Mockito.any(KeyReference.class),
				Mockito.any(byte[].class))
		).thenReturn(
			ciphertext
		);

		byte[] result = _dbCryptoVaultProvider.encrypt(
			_COMPANY_ID, identifier, plaintext);

		Assert.assertArrayEquals(ciphertext, result);
	}

	@Test
	public void testGenerateAsymmetricKeyPair() throws Exception {
		String identifier = "test-key";

		String algorithmSpec = "RSA/2048";

		Mockito.when(
			_cryptoManager.wrap(
				Mockito.anyLong(), Mockito.any(KeyReference.class),
				Mockito.any(Key.class))
		).thenReturn(
			"wrapped-key".getBytes()
		);

		Mockito.when(
			_keyEntryLocalService.createKeyEntry(0)
		).thenReturn(
			Mockito.mock(KeyEntry.class)
		);

		String resultIdentifier =
			_dbCryptoVaultProvider.generateAsymmetricKeyPair(
				_COMPANY_ID, identifier, algorithmSpec);

		Assert.assertEquals(identifier, resultIdentifier);

		Mockito.verify(
			_keyEntryLocalService, Mockito.atLeastOnce()
		).updateKeyEntry(
			Mockito.any(KeyEntry.class)
		);
	}

	@Test
	public void testGenerateSecretKey() throws Exception {
		String identifier = "test-key";

		String algorithmSpec = "AES/256";

		Mockito.when(
			_cryptoManager.wrap(
				Mockito.anyLong(), Mockito.any(KeyReference.class),
				Mockito.any(Key.class))
		).thenReturn(
			"wrapped-key".getBytes()
		);

		Mockito.when(
			_keyEntryLocalService.createKeyEntry(0)
		).thenReturn(
			Mockito.mock(KeyEntry.class)
		);

		String resultIdentifier = _dbCryptoVaultProvider.generateSecretKey(
			_COMPANY_ID, identifier, algorithmSpec);

		Assert.assertEquals(identifier, resultIdentifier);

		Mockito.verify(
			_keyEntryLocalService
		).updateKeyEntry(
			Mockito.any(KeyEntry.class)
		);
	}

	@Test
	public void testGetKeyIdentifiers() throws Exception {
		Mockito.when(
			_keyEntryLocalService.getKeyIdentifiers(_COMPANY_ID)
		).thenReturn(
			Collections.singletonList("key-1")
		);

		List<String> identifiers = _dbCryptoVaultProvider.getKeyIdentifiers(
			_COMPANY_ID);

		Assert.assertEquals(identifiers.toString(), 1, identifiers.size());

		Assert.assertEquals("key-1", identifiers.get(0));
	}

	@Test
	public void testGetKeyMetadata() throws Exception {
		String identifier = "test-key";

		KeyEntry keyEntry = Mockito.mock(KeyEntry.class);

		Mockito.when(
			keyEntry.getAlgorithm()
		).thenReturn(
			"AES"
		);

		Mockito.when(
			keyEntry.getCipherSpec()
		).thenReturn(
			"AES/GCM/NoPadding"
		);

		Mockito.when(
			keyEntry.getCreateDate()
		).thenReturn(
			new Date()
		);

		Mockito.when(
			_keyEntryLocalService.fetchKeyEntry(_COMPANY_ID, identifier)
		).thenReturn(
			keyEntry
		);

		CryptoKey result = _dbCryptoVaultProvider.getKeyMetadata(
			_COMPANY_ID, identifier);

		Assert.assertEquals("AES", result.getAlgorithm());

		Assert.assertEquals(
			identifier,
			result.getKeyReference(
			).getIdentifier());

		Assert.assertEquals(
			KeyReference.ANY_PROVIDER,
			result.getKeyReference(
			).getProviderId());
	}

	@Test
	public void testImportSecretKey() throws Exception {
		String identifier = "new-key";

		byte[] rawKeyMaterial = new byte[32];

		String algorithmSpec = "AES";

		Mockito.when(
			_cryptoManager.wrap(
				Mockito.anyLong(), Mockito.any(KeyReference.class),
				Mockito.any(Key.class))
		).thenReturn(
			"wrapped-key".getBytes()
		);

		Mockito.when(
			_keyEntryLocalService.createKeyEntry(0)
		).thenReturn(
			Mockito.mock(KeyEntry.class)
		);

		String resultIdentifier = _dbCryptoVaultProvider.importSecretKey(
			_COMPANY_ID, identifier, rawKeyMaterial, algorithmSpec);

		Assert.assertEquals(identifier, resultIdentifier);

		Mockito.verify(
			_keyEntryLocalService
		).updateKeyEntry(
			Mockito.any(KeyEntry.class)
		);
	}

	@Test
	public void testIsAllowedCompany() {
		Assert.assertTrue(_dbCryptoVaultProvider.isAllowedCompany(_COMPANY_ID));

		Assert.assertFalse(_dbCryptoVaultProvider.isAllowedCompany(54321L));
	}

	@Test
	public void testSystemProvider() throws Exception {
		DBSystemCryptoVaultProvider systemProvider =
			new DBSystemCryptoVaultProvider();

		_injectField(
			systemProvider, "companyLocalService", _companyLocalService);

		_injectField(systemProvider, "cryptoManager", _cryptoManager);

		_injectField(
			systemProvider, "keyEntryLocalService", _keyEntryLocalService);

		_activate(
			systemProvider,
			HashMapBuilder.<String, Object>put(
				"masterKeyReference", "system-master-kek"
			).put(
				"providerId", "db-system"
			).build());

		Assert.assertTrue(systemProvider.isAllowedCompany(0L));

		Assert.assertTrue(systemProvider.isAllowedCompany(123L));
	}

	private void _activate(Object target, Map<String, Object> properties)
		throws Exception {

		Method method = null;

		Class<?> clazz = target.getClass();

		while (clazz != null) {
			try {
				method = clazz.getDeclaredMethod("activate", Map.class);

				break;
			}
			catch (NoSuchMethodException noSuchMethodException) {
				clazz = clazz.getSuperclass();
			}
		}

		if (method == null) {
			throw new RuntimeException("Method activate not found");
		}

		method.setAccessible(true);

		method.invoke(target, properties);
	}

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

	private static final long _COMPANY_ID = 12345L;

	@Mock
	private CompanyLocalService _companyLocalService;

	@Mock
	private CryptoManager _cryptoManager;

	private DBCompanyCryptoVaultProvider _dbCryptoVaultProvider;

	@Mock
	private KeyEntryLocalService _keyEntryLocalService;

}