/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.keymanager.provider.db.internal;

import com.liferay.document.library.kernel.exception.NoSuchFileException;
import com.liferay.document.library.kernel.store.Store;
import com.liferay.portal.kernel.model.CompanyConstants;
import com.liferay.portal.kernel.security.auth.CompanyThreadLocal;
import com.liferay.portal.kernel.util.HashMapBuilder;
import com.liferay.portal.test.rule.LiferayUnitTestRule;

import java.io.ByteArrayInputStream;
import java.io.InputStream;

import java.lang.reflect.Field;

import java.util.HashMap;
import java.util.Map;

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
public class DLKeyStoreCryptoVaultProviderTest {

	@ClassRule
	@Rule
	public static final LiferayUnitTestRule liferayUnitTestRule =
		LiferayUnitTestRule.INSTANCE;

	@Rule
	public TemporaryFolder temporaryFolder = new TemporaryFolder();

	@Before
	public void setUp() throws Exception {
		MockitoAnnotations.openMocks(this);

		_dlKeyStoreCryptoVaultProvider = new DLKeyStoreCryptoVaultProvider();

		// Manually inject mock since we are not using full OSGi container

		_injectField("_store", _store);

		_keystorePath = "keymanager/keystore.p12";

		// Mock Document Library Storage

		_mockStorage = new HashMap<>();

		Mockito.when(
			_store.getFileAsStream(
				Mockito.eq(_COMPANY_ID), Mockito.eq(CompanyConstants.SYSTEM),
				Mockito.eq(_keystorePath), Mockito.anyString())
		).thenAnswer(
			invocation -> {
				byte[] data = _mockStorage.get(_keystorePath);

				if (data == null) {
					throw new NoSuchFileException(_keystorePath);
				}

				return new ByteArrayInputStream(data);
			}
		);

		Mockito.doAnswer(
			invocation -> {
				InputStream inputStream = invocation.getArgument(4);

				byte[] data = new byte[inputStream.available()];

				inputStream.read(data);

				_mockStorage.put(_keystorePath, data);

				return null;
			}
		).when(_store).addFile(
			Mockito.eq(_COMPANY_ID), Mockito.eq(CompanyConstants.SYSTEM),
			Mockito.eq(_keystorePath), Mockito.anyString(),
			Mockito.any(InputStream.class));

		Mockito.when(
			_store.hasFile(
				Mockito.eq(_COMPANY_ID), Mockito.eq(CompanyConstants.SYSTEM),
				Mockito.eq(_keystorePath), Mockito.anyString())
		).thenAnswer(
			invocation -> _mockStorage.containsKey(_keystorePath)
		);

		CompanyThreadLocal.setCompanyId(_COMPANY_ID);

		_dlKeyStoreCryptoVaultProvider.activate(
			HashMapBuilder.<String, Object>put(
				"keystorePassword", "password"
			).put(
				"keystoreType", "PKCS12"
			).put(
				"providerId", "dl-keystore"
			).build());
	}

	@Test
	public void testPutAndGetSecretKeyRoundtrip() throws Exception {
		// 1. Verify lazy save: Storage should be empty after activation

		Assert.assertTrue(_mockStorage.isEmpty());

		// 2. Prepare SecretKey

		KeyGenerator keyGenerator = KeyGenerator.getInstance("AES");

		keyGenerator.init(256);

		SecretKey originalKey = keyGenerator.generateKey();

		// 3. Put SecretKey

		_dlKeyStoreCryptoVaultProvider.putSecretKey("my-test-key", originalKey);

		// 4. Verify persistence: Storage should no longer be empty

		Assert.assertFalse(_mockStorage.isEmpty());
		Assert.assertTrue(_mockStorage.containsKey(_keystorePath));

		// 5. Get SecretKey (this exercises loading from the mock storage)

		SecretKey retrievedKey = _dlKeyStoreCryptoVaultProvider.getSecretKey(
			"my-test-key");

		// 6. Verify integrity

		Assert.assertNotNull(retrievedKey);
		Assert.assertArrayEquals(
			originalKey.getEncoded(), retrievedKey.getEncoded());
	}

	private void _injectField(String fieldName, Object value) {
		try {
			Field field = DLKeyStoreCryptoVaultProvider.class.getDeclaredField(
				fieldName);

			field.setAccessible(true);

			field.set(_dlKeyStoreCryptoVaultProvider, value);
		}
		catch (Exception exception) {
			throw new RuntimeException(exception);
		}
	}

	private static final long _COMPANY_ID = 54321L;

	@Mock
	private Store _store;

	private DLKeyStoreCryptoVaultProvider _dlKeyStoreCryptoVaultProvider;
	private String _keystorePath;
	private Map<String, byte[]> _mockStorage;

}
