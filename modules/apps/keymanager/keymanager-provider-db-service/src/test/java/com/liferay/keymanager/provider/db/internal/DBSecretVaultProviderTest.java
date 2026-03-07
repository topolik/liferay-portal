/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.keymanager.provider.db.internal;

import com.liferay.keymanager.KeyReference;
import com.liferay.keymanager.crypto.CryptoManager;
import com.liferay.keymanager.provider.db.model.SecretEntry;
import com.liferay.keymanager.provider.db.model.impl.SecretEntryImpl;
import com.liferay.keymanager.provider.db.service.SecretEntryLocalService;
import com.liferay.keymanager.secret.SecureSecret;
import com.liferay.portal.kernel.security.auth.CompanyThreadLocal;
import com.liferay.portal.kernel.util.HashMapBuilder;
import com.liferay.portal.test.rule.LiferayUnitTestRule;

import java.lang.reflect.Field;

import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

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
		LiferayUnitTestRule.INSTANCE;

	@Before
	public void setUp() throws Exception {
		MockitoAnnotations.openMocks(this);

		_dbSecretVaultProvider = new DBSecretVaultProvider();

		// Manually inject mocks since we are not using full OSGi container

		_injectField("_cryptoManager", _cryptoManager);
		_injectField("_secretEntryLocalService", _secretEntryLocalService);

		_dbSecretVaultProvider.activate(
			HashMapBuilder.<String, Object>put(
				"cipherConfiguration", "AES/GCM/NoPadding;128;12;256"
			).put(
				"masterKeyReference", "${keyRef:keystore:master}"
			).put(
				"providerId", "db"
			).build());

		// Mock Master Key (KEK)

		byte[] masterKeyBytes = new byte[32];

		for (int i = 0; i < masterKeyBytes.length; i++) {
			masterKeyBytes[i] = (byte)i;
		}

		SecretKey masterKey = new SecretKeySpec(masterKeyBytes, "AES");

		Mockito.when(
			_cryptoManager.getSecretKey(Mockito.any(KeyReference.class))
		).thenReturn(
			masterKey
		);

		CompanyThreadLocal.setCompanyId(_COMPANY_ID);
	}

	@Test
	public void testPutAndGetSecretRoundtrip() throws Exception {
		String identifier = "my-secret";

		byte[] originalPlaintext = "super-secret-password".getBytes();

		KeyReference keyRef = KeyReference.fromString(
			"${secretRef:db:" + identifier + "}");

		SecureSecret secureSecret = new SecureSecret(keyRef, originalPlaintext);

		// 1. Use real SecretEntryImpl instead of mock

		SecretEntry secretEntry = new SecretEntryImpl();

		// 2. Mock Service interaction to return our real object

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

		// 3. Perform Put

		_dbSecretVaultProvider.putSecret(secureSecret);

		// 4. Verify companyId was set

		Assert.assertEquals(_COMPANY_ID, secretEntry.getCompanyId());

		// 5. Perform Get

		SecureSecret retrievedSecret = _dbSecretVaultProvider.getSecret(
			identifier);

		// 6. Verify

		Assert.assertArrayEquals(originalPlaintext, retrievedSecret.getBytes());

		Assert.assertEquals(
			keyRef.toString(), retrievedSecret.getKeyReference().toString());
	}

	private void _injectField(String fieldName, Object value) {
		try {
			Field field = DBSecretVaultProvider.class.getDeclaredField(
				fieldName);

			field.setAccessible(true);

			field.set(_dbSecretVaultProvider, value);
		}
		catch (Exception exception) {
			throw new RuntimeException(exception);
		}
	}

	private static final long _COMPANY_ID = 12345L;

	@Mock
	private CryptoManager _cryptoManager;

	@Mock
	private SecretEntryLocalService _secretEntryLocalService;

	private DBSecretVaultProvider _dbSecretVaultProvider;

}
