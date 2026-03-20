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
import com.liferay.keymanager.provider.db.model.impl.SecretEntryImpl;
import com.liferay.keymanager.provider.db.service.SecretEntryLocalService;
import com.liferay.keymanager.secret.SecureSecret;
import com.liferay.portal.kernel.instance.PortalInstancePool;
import com.liferay.portal.kernel.service.CompanyLocalService;
import com.liferay.portal.kernel.util.HashMapBuilder;
import com.liferay.portal.test.rule.LiferayUnitTestRule;

import java.lang.reflect.Field;

import java.security.Key;

import java.util.Collections;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;

import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

/**
 * @author Tomas Polesovsky
 */
public class DBSecretVaultProviderTest {

	@ClassRule
	@Rule
	public static final LiferayUnitTestRule liferayUnitTestRule =
		LiferayUnitTestRule.INSTANCE;

	@Before
	public void setUp() throws Exception {
		MockitoAnnotations.openMocks(this);

		_portalInstancePoolMockedStatic = Mockito.mockStatic(
			PortalInstancePool.class);

		_portalInstancePoolMockedStatic.when(
			PortalInstancePool::getDefaultCompanyId
		).thenReturn(
			_COMPANY_ID
		);

		_dbSecretVaultProvider = new DBCompanySecretVaultProvider();

		_injectField(_dbSecretVaultProvider, "_cryptoManager", _cryptoManager);
		_injectField(
			_dbSecretVaultProvider, "_secretEntryLocalService",
			_secretEntryLocalService);

		_dbSecretVaultProvider.activate(
			HashMapBuilder.<String, Object>put(
				"companyId", _COMPANY_ID
			).put(
				"dekCipherSpec",
				"AES/GCM/NoPadding;keySize=256;ivSize=12;gcmTag=128"
			).put(
				"masterKeyReference", "${keyRef:*:db-vault-provider-master-kek}"
			).put(
				"providerId", "db"
			).build());
	}

	@After
	public void tearDown() {
		_portalInstancePoolMockedStatic.close();
	}

	@Test
	public void testDeleteSecret() throws Exception {
		String identifier = "to-delete";

		SecretEntry secretEntry = new SecretEntryImpl();

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
	public void testGetSecretIdentifiers() throws Exception {
		Mockito.when(
			_secretEntryLocalService.getSecretIdentifiers(_COMPANY_ID)
		).thenReturn(
			Collections.singletonList("secret-1")
		);

		List<String> identifiers =
			_dbSecretVaultProvider.getSecretIdentifiers(_COMPANY_ID);

		Assert.assertEquals(identifiers.toString(), 1, identifiers.size());
		Assert.assertEquals("secret-1", identifiers.get(0));
	}

	@Test
	public void testIsAllowedCompany() throws Exception {
		Assert.assertTrue(_dbSecretVaultProvider.isAllowedCompany(0));
		Assert.assertTrue(_dbSecretVaultProvider.isAllowedCompany(_COMPANY_ID));
		Assert.assertFalse(_dbSecretVaultProvider.isAllowedCompany(1L));

		DBSystemSecretVaultProvider systemProvider =
			new DBSystemSecretVaultProvider();

		_injectField(systemProvider, "_companyLocalService", _companyLocalService);

		systemProvider.activate(
			HashMapBuilder.<String, Object>put(
				"dekCipherSpec",
				"AES/GCM/NoPadding;keySize=256;ivSize=12;gcmTag=128"
			).put(
				"enabled", true
			).put(
				"masterKeyReference", "${keyRef:*:db-vault-provider-master-kek}"
			).put(
				"providerId", "db-secret-manager-system"
			).build());

		Assert.assertTrue(systemProvider.isAllowedCompany(0));
		Assert.assertTrue(systemProvider.isAllowedCompany(_COMPANY_ID));
		Assert.assertFalse(systemProvider.isAllowedCompany(1L));
	}

	@Test
	public void testPutAndGetSecretRoundtrip() throws Exception {
		String identifier = "my-secret";
		byte[] originalPlaintext = "super-secret-password".getBytes();

		KeyReference keyRef = KeyReference.fromString(
			"${secretRef:db:" + identifier + "}");

		SecureSecret secureSecret = new SecureSecret(keyRef, originalPlaintext);

		// Use a real SecretEntryImpl to act as a data carrier

		SecretEntry secretEntry = new SecretEntryImpl();

		// 1. Mock CryptoManager statefully to capture the dynamically
		// generated DEK

		AtomicReference<Key> capturedDekReference = new AtomicReference<>();

		Mockito.when(
			_cryptoManager.wrap(
				Mockito.anyLong(), Mockito.any(KeyReference.class),
				Mockito.any(Key.class))
		).thenAnswer(
			invocation -> {
				capturedDekReference.set(invocation.getArgument(2));

				return "wrapped-dek-material".getBytes();
			}
		);

		Mockito.when(
			_cryptoManager.unwrap(
				Mockito.anyLong(), Mockito.any(KeyReference.class),
				Mockito.any(byte[].class), Mockito.anyString(),
				Mockito.anyInt())
		).thenAnswer(
			invocation -> capturedDekReference.get()
		);

		// 2. Mock Service interaction

		Mockito.when(
			_secretEntryLocalService.fetchSecretEntry(_COMPANY_ID, identifier)
		).thenReturn(
			null
		).thenReturn(
			secretEntry
		);

		Mockito.when(
			_secretEntryLocalService.createSecretEntry(0)
		).thenReturn(
			secretEntry
		);

		Mockito.when(
			_secretEntryLocalService.getSecretEntry(_COMPANY_ID, identifier)
		).thenReturn(
			secretEntry
		);

		// 3. Perform Put (this will generate a random DEK and IV)

		_dbSecretVaultProvider.putSecret(_COMPANY_ID, secureSecret);

		// 4. Perform Get (this will use the captured DEK and stored IV)

		SecureSecret retrievedSecret = _dbSecretVaultProvider.getSecret(
			_COMPANY_ID, identifier);

		// 5. Verify

		Assert.assertArrayEquals(originalPlaintext, retrievedSecret.getBytes());

		Assert.assertEquals(_COMPANY_ID, secretEntry.getCompanyId());
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

	private MockedStatic<PortalInstancePool> _portalInstancePoolMockedStatic;

}