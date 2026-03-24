/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.keymanager.internal.secret;

import com.liferay.keymanager.KeyReference;
import com.liferay.keymanager.secret.SecretManagerException;
import com.liferay.keymanager.secret.SecureSecret;
import com.liferay.keymanager.spi.fips.FipsComplianceChecker;
import com.liferay.keymanager.spi.profile.KeyManagerProfile;
import com.liferay.keymanager.spi.profile.ProfileOrchestrator;
import com.liferay.keymanager.spi.secret.SecretVaultReader;
import com.liferay.keymanager.spi.secret.SecretVaultWriter;
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
public class SecretManagerImplTest {

	@Before
	public void setUp() throws Exception {
		MockitoAnnotations.openMocks(this);

		_secretManagerImpl = new SecretManagerImpl();

		_injectField(
			_secretManagerImpl, "_readerServiceTrackerMap",
			_readerServiceTrackerMap);

		_injectField(
			_secretManagerImpl, "_writerServiceTrackerMap",
			_writerServiceTrackerMap);

		_injectField(
			_secretManagerImpl, "_fipsComplianceChecker",
			_fipsComplianceChecker);

		_injectField(
			_secretManagerImpl, "_profileOrchestrator", _profileOrchestrator);

		Mockito.when(
			_readerServiceTrackerMap.getService("test-secret-provider")
		).thenReturn(
			_secretVaultReader
		);

		Mockito.when(
			_readerServiceTrackerMap.keySet()
		).thenReturn(
			new HashSet<>(Collections.singletonList("test-secret-provider"))
		);

		Mockito.when(
			_writerServiceTrackerMap.getService("test-secret-provider")
		).thenReturn(
			_secretVaultWriter
		);

		Mockito.when(
			_writerServiceTrackerMap.keySet()
		).thenReturn(
			new HashSet<>(Collections.singletonList("test-secret-provider"))
		);

		Mockito.when(
			_secretVaultReader.isAllowedCompany(Mockito.anyLong())
		).thenReturn(
			true
		);

		Mockito.when(
			_secretVaultWriter.isAllowedCompany(Mockito.anyLong())
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
	public void testGetSecret() throws Exception {
		KeyReference keyRef = new KeyReference(
			KeyReference.Type.SECRET, "test-secret-provider", "my-secret");

		byte[] secretData = "password123".getBytes();

		SecureSecret secureSecret = new SecureSecret(keyRef, secretData);

		Mockito.when(
			_secretVaultReader.getSecret(0L, "my-secret")
		).thenReturn(
			secureSecret
		);

		SecureSecret result = _secretManagerImpl.getSecret(0L, keyRef);

		Assert.assertEquals(secureSecret, result);

		Mockito.verify(
			_secretVaultReader
		).getSecret(
			0L, "my-secret"
		);
	}

	@Test(expected = SecretManagerException.class)
	public void testGetSecretNoProvider() throws Exception {
		KeyReference keyRef = new KeyReference(
			KeyReference.Type.SECRET, "non-existent", "my-secret");

		_secretManagerImpl.getSecret(0L, keyRef);
	}

	@Test
	public void testProfileRouting() throws Exception {
		SecretVaultReader systemReader = Mockito.mock(
			SecretVaultReader.class, "systemReader");

		SecretVaultWriter systemWriter = Mockito.mock(
			SecretVaultWriter.class, "systemWriter");

		Mockito.when(
			_keyManagerProfile.getSystemSecretProviderId()
		).thenReturn(
			"system-secret"
		);

		Mockito.when(
			_readerServiceTrackerMap.getService("system-secret")
		).thenReturn(
			systemReader
		);

		Mockito.when(
			_writerServiceTrackerMap.getService("system-secret")
		).thenReturn(
			systemWriter
		);

		Mockito.when(
			systemReader.isAllowedCompany(0L)
		).thenReturn(
			true
		);

		Mockito.when(
			systemWriter.isAllowedCompany(0L)
		).thenReturn(
			true
		);

		KeyReference keyRef = new KeyReference(
			KeyReference.Type.SECRET, KeyReference.ANY_PROVIDER, "my-secret");

		byte[] secretData = "password123".getBytes();

		SecureSecret secureSecret = new SecureSecret(keyRef, secretData);

		// 1. Get secret routing

		Mockito.when(
			systemReader.getSecret(0L, "my-secret")
		).thenReturn(
			secureSecret
		);

		SecureSecret result = _secretManagerImpl.getSecret(0L, keyRef);

		Assert.assertEquals(secureSecret, result);

		Mockito.verify(
			systemReader
		).getSecret(
			0L, "my-secret"
		);

		// 2. Put secret routing

		_secretManagerImpl.putSecret(0L, secureSecret);

		Mockito.verify(
			systemWriter
		).putSecret(
			0L, secureSecret
		);
	}

	@Test
	public void testPutSecret() throws Exception {
		KeyReference keyRef = new KeyReference(
			KeyReference.Type.SECRET, "test-secret-provider", "my-secret");

		byte[] secretData = "password123".getBytes();

		SecureSecret secureSecret = new SecureSecret(keyRef, secretData);

		_secretManagerImpl.putSecret(0L, secureSecret);

		Mockito.verify(
			_secretVaultWriter
		).putSecret(
			0L, secureSecret
		);
	}

	@Test
	public void testRogueTenantIsolation() throws Exception {
		KeyReference keyRef = new KeyReference(
			KeyReference.Type.SECRET, "test-secret-provider",
			"tenant-a-secret");

		// Mock the provider to only allow Tenant A (companyId = 1)

		Mockito.when(
			_secretVaultReader.isAllowedCompany(1L)
		).thenReturn(
			true
		);

		// Explicitly deny Tenant B (companyId = 2) and System (companyId = 0)

		Mockito.when(
			_secretVaultReader.isAllowedCompany(2L)
		).thenReturn(
			false
		);

		Mockito.when(
			_secretVaultReader.isAllowedCompany(0L)
		).thenReturn(
			false
		);

		// Tenant A should succeed

		SecureSecret tenantASecret = new SecureSecret(
			keyRef, "tenantA".getBytes());

		Mockito.when(
			_secretVaultReader.getSecret(1L, "tenant-a-secret")
		).thenReturn(
			tenantASecret
		);

		SecureSecret result = _secretManagerImpl.getSecret(1L, keyRef);

		Assert.assertEquals(tenantASecret, result);

		// Tenant B should fail with an exception due to not being allowed

		try {
			_secretManagerImpl.getSecret(2L, keyRef);

			Assert.fail(
				"Expected SecretManagerException due to rogue tenant " +
					"cross-access.");
		}
		catch (SecretManagerException secretManagerException) {
			Assert.assertTrue(
				secretManagerException.getMessage(
				).contains(
					"No secret vault reader found"
				));
		}

		// System should also fail

		try {
			_secretManagerImpl.getSecret(0L, keyRef);

			Assert.fail(
				"Expected SecretManagerException due to system accessing " +
					"tenant secret.");
		}
		catch (SecretManagerException secretManagerException) {
			Assert.assertTrue(
				secretManagerException.getMessage(
				).contains(
					"No secret vault reader found"
				));
		}
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

	@Mock
	private FipsComplianceChecker _fipsComplianceChecker;

	@Mock
	private KeyManagerProfile _keyManagerProfile;

	@Mock
	private ProfileOrchestrator _profileOrchestrator;

	@Mock
	private ServiceTrackerMap<String, SecretVaultReader>
		_readerServiceTrackerMap;

	private SecretManagerImpl _secretManagerImpl;

	@Mock
	private SecretVaultReader _secretVaultReader;

	@Mock
	private SecretVaultWriter _secretVaultWriter;

	@Mock
	private ServiceTrackerMap<String, SecretVaultWriter>
		_writerServiceTrackerMap;

}