/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.keymanager.provider.db.internal;

import com.liferay.keymanager.KeyReference;
import com.liferay.keymanager.crypto.CryptoManager;
import com.liferay.keymanager.provider.db.internal.secret.DBCompanySecretVaultProvider;
import com.liferay.keymanager.provider.db.internal.secret.DBSystemSecretVaultProvider;
import com.liferay.keymanager.provider.db.model.SecretEntry;
import com.liferay.keymanager.provider.db.service.SecretEntryLocalService;
import com.liferay.keymanager.secret.SecureSecret;
import com.liferay.portal.kernel.service.CompanyLocalService;
import com.liferay.portal.kernel.util.HashMapBuilder;
import com.liferay.portal.test.rule.LiferayUnitTestRule;

import java.io.ByteArrayInputStream;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

import java.security.Key;

import java.sql.Blob;

import java.util.Collections;
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
public class DBSecretVaultProviderTest {

	@ClassRule
	@Rule
	public static final LiferayUnitTestRule liferayUnitTestRule =
		new LiferayUnitTestRule();

	@Before
	public void setUp() throws Exception {
		MockitoAnnotations.openMocks(this);

		_dbSecretVaultProvider = new DBCompanySecretVaultProvider();

		_injectField(
			_dbSecretVaultProvider, "companyLocalService",
			_companyLocalService);

		_injectField(_dbSecretVaultProvider, "cryptoManager", _cryptoManager);

		_injectField(
			_dbSecretVaultProvider, "secretEntryLocalService",
			_secretEntryLocalService);

		_activate(
			_dbSecretVaultProvider,
			HashMapBuilder.<String, Object>put(
				"companyId", _COMPANY_ID
			).put(
				"masterKeyReference", "local-master-kek"
			).put(
				"providerId", "db"
			).build());
	}

	@Test
	public void testDeleteSecret() throws Exception {
		String identifier = "test-secret";

		SecretEntry secretEntry = Mockito.mock(SecretEntry.class);

		Mockito.when(
			_secretEntryLocalService.fetchSecretEntry(_COMPANY_ID, identifier)
		).thenReturn(
			secretEntry
		);

		_dbSecretVaultProvider.deleteSecret(_COMPANY_ID, identifier);

		Mockito.verify(
			_secretEntryLocalService
		).deleteSecretEntry(
			secretEntry
		);
	}

	@Test
	public void testGetSecret() throws Exception {
		String identifier = "test-secret";

		byte[] ciphertext = "encrypted-data".getBytes();

		byte[] plaintext = "decrypted-data".getBytes();

		SecretEntry secretEntry = Mockito.mock(SecretEntry.class);

		Blob ciphertextBlob = Mockito.mock(Blob.class);

		Mockito.when(
			ciphertextBlob.getBinaryStream()
		).thenReturn(
			new ByteArrayInputStream(ciphertext)
		);

		Mockito.when(
			secretEntry.getCiphertextBlob()
		).thenReturn(
			ciphertextBlob
		);

		Mockito.when(
			secretEntry.getKekReference()
		).thenReturn(
			"local-master-kek"
		);

		Blob encryptedDEKBlob = Mockito.mock(Blob.class);

		Mockito.when(
			encryptedDEKBlob.getBinaryStream()
		).thenReturn(
			new ByteArrayInputStream("wrapped-dek".getBytes())
		);

		Mockito.when(
			secretEntry.getEncryptedDEKBlob()
		).thenReturn(
			encryptedDEKBlob
		);

		Mockito.when(
			secretEntry.getIv()
		).thenReturn(
			"iv-data"
		);

		Mockito.when(
			_secretEntryLocalService.fetchSecretEntry(_COMPANY_ID, identifier)
		).thenReturn(
			secretEntry
		);

		Mockito.when(
			_cryptoManager.unwrap(
				Mockito.eq(_COMPANY_ID), Mockito.any(KeyReference.class),
				Mockito.any(byte[].class), Mockito.anyString(),
				Mockito.anyInt())
		).thenReturn(
			Mockito.mock(Key.class)
		);

		Mockito.when(
			_cryptoManager.decrypt(
				Mockito.eq(_COMPANY_ID), Mockito.any(KeyReference.class),
				Mockito.any(byte[].class))
		).thenReturn(
			plaintext
		);

		SecureSecret secureSecret = _dbSecretVaultProvider.getSecret(
			_COMPANY_ID, identifier);

		Assert.assertArrayEquals(plaintext, secureSecret.getBytes());
	}

	@Test
	public void testGetSecretIdentifiers() throws Exception {
		Mockito.when(
			_secretEntryLocalService.getSecretIdentifiers(_COMPANY_ID)
		).thenReturn(
			Collections.singletonList("secret-1")
		);

		List<String> identifiers = _dbSecretVaultProvider.getSecretIdentifiers(
			_COMPANY_ID);

		Assert.assertEquals(identifiers.toString(), 1, identifiers.size());

		Assert.assertEquals("secret-1", identifiers.get(0));
	}

	@Test
	public void testIsAllowedCompany() {
		Assert.assertTrue(_dbSecretVaultProvider.isAllowedCompany(_COMPANY_ID));

		Assert.assertFalse(_dbSecretVaultProvider.isAllowedCompany(54321L));
	}

	@Test
	public void testPutSecret() throws Exception {
		String identifier = "new-secret";

		byte[] plaintext = "secret-data".getBytes();

		KeyReference keyRef = new KeyReference(
			KeyReference.Type.SECRET, "db", identifier);

		SecureSecret secureSecret = new SecureSecret(keyRef, plaintext);

		Mockito.when(
			_cryptoManager.wrap(
				Mockito.anyLong(), Mockito.any(KeyReference.class),
				Mockito.any(Key.class))
		).thenReturn(
			"wrapped-dek".getBytes()
		);

		Mockito.when(
			_secretEntryLocalService.createSecretEntry(0)
		).thenReturn(
			Mockito.mock(SecretEntry.class)
		);

		_dbSecretVaultProvider.putSecret(_COMPANY_ID, secureSecret);

		Mockito.verify(
			_secretEntryLocalService
		).updateSecretEntry(
			Mockito.any(SecretEntry.class)
		);
	}

	@Test
	public void testSystemProvider() throws Exception {
		DBSystemSecretVaultProvider systemProvider =
			new DBSystemSecretVaultProvider();

		_injectField(
			systemProvider, "companyLocalService", _companyLocalService);

		_injectField(systemProvider, "cryptoManager", _cryptoManager);

		_injectField(
			systemProvider, "secretEntryLocalService",
			_secretEntryLocalService);

		_activate(
			systemProvider,
			HashMapBuilder.<String, Object>put(
				"masterKeyReference", "system-master-kek"
			).put(
				"providerId", "db-system"
			).build());

		Assert.assertTrue(systemProvider.isAllowedCompany(0L));

		Assert.assertTrue(systemProvider.isAllowedCompany(_COMPANY_ID));
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

	private DBCompanySecretVaultProvider _dbSecretVaultProvider;

	@Mock
	private SecretEntryLocalService _secretEntryLocalService;

}