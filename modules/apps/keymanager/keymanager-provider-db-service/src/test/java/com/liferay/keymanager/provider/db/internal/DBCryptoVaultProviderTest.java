/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.keymanager.provider.db.internal;

import com.liferay.keymanager.KeyReference;
import com.liferay.keymanager.crypto.CryptoKey;
import com.liferay.keymanager.crypto.CryptoManager;
import com.liferay.keymanager.crypto.CryptoManagerException;
import com.liferay.keymanager.provider.db.model.KeyEntry;
import com.liferay.keymanager.provider.db.model.impl.KeyEntryImpl;
import com.liferay.keymanager.provider.db.service.KeyEntryLocalService;
import com.liferay.portal.kernel.dao.jdbc.OutputBlob;
import com.liferay.portal.kernel.util.HashMapBuilder;
import com.liferay.portal.test.rule.LiferayUnitTestRule;

import java.io.ByteArrayInputStream;

import java.lang.reflect.Field;

import java.security.Key;

import java.util.Collections;
import java.util.Date;
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
public class DBCryptoVaultProviderTest {

	@ClassRule
	@Rule
	public static final LiferayUnitTestRule liferayUnitTestRule =
		LiferayUnitTestRule.INSTANCE;

	@Before
	public void setUp() throws Exception {
		MockitoAnnotations.openMocks(this);

		_dbCryptoVaultProvider = new DBCryptoVaultProvider();

		_injectField("_cryptoManager", _cryptoManager);
		_injectField("_keyEntryLocalService", _keyEntryLocalService);

		_dbCryptoVaultProvider.activate(
			HashMapBuilder.<String, Object>put(
				"companyId", _COMPANY_ID
			).put(
				"masterKeyReference", "${keyRef:keystore:master}"
			).put(
				"providerId", "db"
			).build());
	}

	@Test
	public void testDeleteKey() throws Exception {
		String identifier = "to-delete";

		KeyEntry keyEntry = new KeyEntryImpl();

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
	public void testEncryptAndDecryptRoundtrip() throws Exception {
		String identifier = "test-key";
		byte[] plaintext = "hello-world".getBytes();

		KeyGenerator keyGenerator = KeyGenerator.getInstance("AES");

		keyGenerator.init(256);

		SecretKey keyMaterial = keyGenerator.generateKey();

		KeyEntry keyEntry = new KeyEntryImpl();

		keyEntry.setAlias(identifier);
		keyEntry.setAlgorithm("AES");
		keyEntry.setKeyType(DBCryptoVaultProvider.KeyType.SECRET.name());
		keyEntry.setCipherSpec(
			"AES/GCM/NoPadding;keySize=256;ivSize=12;gcmTag=128");
		keyEntry.setKekReference("${keyRef:keystore:master}");
		keyEntry.setWrappedKeyBlob(
			new OutputBlob(new ByteArrayInputStream(new byte[32]), 32));

		Mockito.when(
			_cryptoManager.unwrap(
				Mockito.anyLong(), Mockito.any(KeyReference.class),
				Mockito.any(byte[].class), Mockito.anyString(),
				Mockito.anyInt())
		).thenReturn(
			keyMaterial
		);

		Mockito.when(
			_keyEntryLocalService.getKeyEntry(_COMPANY_ID, identifier)
		).thenReturn(
			keyEntry
		);

		byte[] ciphertext = _dbCryptoVaultProvider.encrypt(
			_COMPANY_ID, identifier, plaintext);

		Assert.assertNotNull(ciphertext);

		byte[] recoveredPlaintext = _dbCryptoVaultProvider.decrypt(
			_COMPANY_ID, identifier, ciphertext);

		Assert.assertArrayEquals(plaintext, recoveredPlaintext);
	}

	@Test
	public void testGenerateAsymmetricKeyPair() throws Exception {
		String identifier = "new-key";

		Mockito.when(
			_keyEntryLocalService.createKeyEntry(Mockito.anyLong())
		).thenReturn(
			new KeyEntryImpl()
		);

		Mockito.when(
			_cryptoManager.wrap(
				Mockito.anyLong(), Mockito.any(KeyReference.class),
				Mockito.any(Key.class))
		).thenReturn(
			"wrapped".getBytes()
		);

		String cipherSpec = "RSA";

		String result = _dbCryptoVaultProvider.generateAsymmetricKeyPair(
			_COMPANY_ID, identifier, cipherSpec);

		Assert.assertEquals(identifier, result);

		Mockito.verify(
			_keyEntryLocalService, Mockito.times(2)
		).updateKeyEntry(
			Mockito.any(KeyEntry.class)
		);
	}

	@Test
	public void testGenerateSecretKey() throws Exception {
		String identifier = "new-key";

		Mockito.when(
			_keyEntryLocalService.createKeyEntry(Mockito.anyLong())
		).thenReturn(
			new KeyEntryImpl()
		);

		Mockito.when(
			_cryptoManager.wrap(
				Mockito.anyLong(), Mockito.any(KeyReference.class),
				Mockito.any(Key.class))
		).thenReturn(
			"wrapped".getBytes()
		);

		String cipherSpec =
			"AES/GCM/NoPadding;keySize=256;ivSize=12;gcmTag=128";

		String result = _dbCryptoVaultProvider.generateSecretKey(
			_COMPANY_ID, identifier, cipherSpec);

		Assert.assertEquals(identifier, result);

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

		KeyEntry keyEntry = new KeyEntryImpl();

		keyEntry.setAlgorithm("AES");
		keyEntry.setCipherSpec("AES/GCM/NoPadding");
		keyEntry.setCreateDate(new Date(123456789L));

		Mockito.when(
			_keyEntryLocalService.getKeyEntry(_COMPANY_ID, identifier)
		).thenReturn(
			keyEntry
		);

		CryptoKey result = _dbCryptoVaultProvider.getKeyMetadata(
			_COMPANY_ID, identifier);

		Assert.assertEquals("AES", result.getAlgorithm());
		Assert.assertEquals("AES/GCM/NoPadding", result.getCipherSpec());
		Assert.assertEquals(123456789L, result.getCreationDate());
		Assert.assertEquals(
			"test-key",
			result.getKeyReference(
			).getIdentifier());
		Assert.assertEquals(
			"db",
			result.getKeyReference(
			).getProviderId());
	}

	@Test
	public void testImportSecretKey() throws Exception {
		String identifier = "new-key";

		Mockito.when(
			_keyEntryLocalService.createKeyEntry(Mockito.anyLong())
		).thenReturn(
			new KeyEntryImpl()
		);

		Mockito.when(
			_cryptoManager.wrap(
				Mockito.anyLong(), Mockito.any(KeyReference.class),
				Mockito.any(Key.class))
		).thenReturn(
			"wrapped".getBytes()
		);

		String cipherSpec =
			"AES/GCM/NoPadding;keySize=256;ivSize=12;gcmTag=128";
		byte[] rawKeyMaterial = new byte[32];

		String result = _dbCryptoVaultProvider.importSecretKey(
			_COMPANY_ID, identifier, rawKeyMaterial, cipherSpec);

		Assert.assertEquals(identifier, result);

		Mockito.verify(
			_keyEntryLocalService
		).updateKeyEntry(
			Mockito.any(KeyEntry.class)
		);
	}

	@Test(expected = CryptoManagerException.class)
	public void testWrap() throws Exception {
		String identifier = "test-key";
		Key keyToWrap = Mockito.mock(Key.class);

		_dbCryptoVaultProvider.wrap(_COMPANY_ID, identifier, keyToWrap);
	}

	private void _injectField(String fieldName, Object value) {
		try {
			Field field = DBCryptoVaultProvider.class.getDeclaredField(
				fieldName);

			field.setAccessible(true);
			field.set(_dbCryptoVaultProvider, value);
		}
		catch (Exception exception) {
			throw new RuntimeException(exception);
		}
	}

	private static final long _COMPANY_ID = 999L;

	@Mock
	private CryptoManager _cryptoManager;

	private DBCryptoVaultProvider _dbCryptoVaultProvider;

	@Mock
	private KeyEntryLocalService _keyEntryLocalService;

}