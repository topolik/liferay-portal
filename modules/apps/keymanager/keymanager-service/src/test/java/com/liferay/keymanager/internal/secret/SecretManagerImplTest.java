/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.keymanager.internal.secret;

import com.liferay.keymanager.KeyReference;
import com.liferay.keymanager.secret.SecretManagerException;
import com.liferay.keymanager.secret.SecureSecret;
import com.liferay.keymanager.spi.secret.SecretVaultReader;
import com.liferay.keymanager.spi.secret.SecretVaultWriter;
import com.liferay.osgi.service.tracker.collections.map.ServiceTrackerMap;
import com.liferay.portal.test.rule.LiferayUnitTestRule;

import java.lang.reflect.Field;

import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedHashSet;
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
public class SecretManagerImplTest {

	@ClassRule
	@Rule
	public static final LiferayUnitTestRule liferayUnitTestRule =
		LiferayUnitTestRule.INSTANCE;

	@Before
	public void setUp() throws Exception {
		MockitoAnnotations.openMocks(this);

		_secretManagerImpl = new SecretManagerImpl();

		// Manually inject mock ServiceTrackerMaps

		_injectField(
			_secretManagerImpl, "_readerServiceTrackerMap",
			_readerServiceTrackerMap);
		_injectField(
			_secretManagerImpl, "_writerServiceTrackerMap",
			_writerServiceTrackerMap);

		Mockito.when(
			_readerServiceTrackerMap.getService("test-provider")
		).thenReturn(
			_secretVaultReader
		);

		Mockito.when(
			_readerServiceTrackerMap.keySet()
		).thenReturn(
			new HashSet<>(Collections.singletonList("test-provider"))
		);

		Mockito.when(
			_writerServiceTrackerMap.getService("test-provider")
		).thenReturn(
			_secretVaultWriter
		);

		Mockito.when(
			_writerServiceTrackerMap.keySet()
		).thenReturn(
			new HashSet<>(Collections.singletonList("test-provider"))
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
			_secretVaultReader.getPriority()
		).thenReturn(
			100
		);

		Mockito.when(
			_secretVaultWriter.getPriority()
		).thenReturn(
			100
		);
	}

	@Test
	public void testPriorityOrdering() throws Exception {
		SecretVaultReader lowPriorityReader = Mockito.mock(
			SecretVaultReader.class, "lowPriorityReader");

		Mockito.when(
			lowPriorityReader.getPriority()
		).thenReturn(
			10
		);
		Mockito.when(
			lowPriorityReader.isAllowedCompany(Mockito.anyLong())
		).thenReturn(
			true
		);

		SecretVaultReader specialPrioritizedReader = Mockito.mock(
			SecretVaultReader.class, "specialPrioritizedReader");

		Mockito.when(
			specialPrioritizedReader.getPriority()
		).thenReturn(
			100
		);
		Mockito.when(
			specialPrioritizedReader.isAllowedCompany(Mockito.anyLong())
		).thenReturn(
			true
		);

		Mockito.when(
			_readerServiceTrackerMap.keySet()
		).thenReturn(
			new LinkedHashSet<>(List.of("high", "low"))
		);

		Mockito.when(
			_readerServiceTrackerMap.getService("low")
		).thenReturn(
			lowPriorityReader
		);
		Mockito.when(
			_readerServiceTrackerMap.getService("high")
		).thenReturn(
			specialPrioritizedReader
		);

		KeyReference keyRef = new KeyReference(
			KeyReference.Type.SECRET, KeyReference.ANY_PROVIDER, "my-secret");

		SecureSecret mockSecret = Mockito.mock(SecureSecret.class);

		Mockito.when(
			specialPrioritizedReader.getSecret(0L, "my-secret")
		).thenReturn(
			mockSecret
		);

		SecureSecret result = _secretManagerImpl.getSecret(0L, keyRef);

		Assert.assertEquals(mockSecret, result);

		Mockito.verify(specialPrioritizedReader).getSecret(0L, "my-secret");

		// The low priority reader is never reached because high priority worked
		Mockito.verify(lowPriorityReader, Mockito.never()).getSecret(
			Mockito.anyLong(), Mockito.anyString());
	}

	@Test
	public void testDeleteSecret() throws Exception {
		KeyReference keyRef = KeyReference.fromString(
			"${secretRef:test-provider:my-secret}");

		_secretManagerImpl.deleteSecret(0L, keyRef);

		Mockito.verify(
			_secretVaultWriter
		).deleteSecret(
			0L, "my-secret"
		);
	}

	@Test
	public void testGetProviders() throws Exception {
		List<String> result = _secretManagerImpl.getProviders(0L);

		Assert.assertEquals(result.toString(), 1, result.size());
		Assert.assertEquals("test-provider", result.get(0));
	}

	@Test
	public void testGetSecret() throws Exception {
		KeyReference keyRef = KeyReference.fromString(
			"${secretRef:test-provider:my-secret}");

		SecureSecret mockSecret = new SecureSecret(keyRef, "data".getBytes());

		Mockito.when(
			_secretVaultReader.getSecret(0L, "my-secret")
		).thenReturn(
			mockSecret
		);

		SecureSecret result = _secretManagerImpl.getSecret(0L, keyRef);

		Assert.assertEquals(mockSecret, result);
	}

	@Test
	public void testGetSecretIdentifiers() throws Exception {
		List<String> identifiers = Collections.singletonList("secret-1");

		Mockito.when(
			_secretVaultReader.getSecretIdentifiers(0L)
		).thenReturn(
			identifiers
		);

		List<KeyReference> result = _secretManagerImpl.getSecretIdentifiers(
			0L, "test-provider");

		Assert.assertEquals(result.toString(), 1, result.size());
		Assert.assertEquals(
			"secret-1",
			result.get(
				0
			).getIdentifier());
		Assert.assertEquals(
			"test-provider",
			result.get(
				0
			).getProviderId());
	}

	@Test
	public void testGetSecretIdentifiersAnyProvider() throws Exception {
		List<String> identifiers = Collections.singletonList("secret-1");

		Mockito.when(
			_secretVaultReader.getSecretIdentifiers(0L)
		).thenReturn(
			identifiers
		);

		List<KeyReference> result = _secretManagerImpl.getSecretIdentifiers(
			0L, KeyReference.ANY_PROVIDER);

		Assert.assertEquals(result.toString(), 1, result.size());
		Assert.assertEquals("secret-1", result.get(0).getIdentifier());
		Assert.assertEquals("test-provider", result.get(0).getProviderId());
	}

	@Test(expected = SecretManagerException.class)
	public void testGetSecretWrongProvider() throws Exception {
		KeyReference keyRef = KeyReference.fromString(
			"${secretRef:wrong-provider:my-secret}");

		_secretManagerImpl.getSecret(0L, keyRef);
	}

	@Test
	public void testGetSecretNotFound() throws Exception {
		KeyReference keyRef = KeyReference.fromString(
			"${secretRef:test-provider:missing-secret}");

		Mockito.when(
			_secretVaultReader.getSecret(0L, "missing-secret")
		).thenReturn(
			null
		);

		SecureSecret result = _secretManagerImpl.getSecret(0L, keyRef);

		Assert.assertNull(result);
	}

	@Test
	public void testPutSecret() throws Exception {
		KeyReference keyRef = KeyReference.fromString(
			"${secretRef:test-provider:my-new-secret}");

		SecureSecret secureSecret = new SecureSecret(
			keyRef, "new-data".getBytes());

		KeyReference resultKeyRef = _secretManagerImpl.putSecret(0L, secureSecret);

		Assert.assertEquals("test-provider", resultKeyRef.getProviderId());
		Assert.assertEquals("my-new-secret", resultKeyRef.getIdentifier());

		Mockito.verify(
			_secretVaultWriter
		).putSecret(
			0L, secureSecret
		);
	}

	@Test
	public void testPutSecretAnyProvider() throws Exception {
		KeyReference keyRef = new KeyReference(
			KeyReference.Type.SECRET, KeyReference.ANY_PROVIDER, "any-secret");

		SecureSecret secureSecret = new SecureSecret(
			keyRef, "any-data".getBytes());

		KeyReference resultKeyRef = _secretManagerImpl.putSecret(0L, secureSecret);

		Assert.assertEquals("test-provider", resultKeyRef.getProviderId());
		Assert.assertEquals("any-secret", resultKeyRef.getIdentifier());

		Mockito.verify(
			_secretVaultWriter
		).putSecret(
			0L, secureSecret
		);
	}

	@Test
	public void testGetSecretAnyProvider() throws Exception {
		KeyReference keyRef = new KeyReference(
			KeyReference.Type.SECRET, KeyReference.ANY_PROVIDER, "my-secret");

		SecureSecret mockSecret = new SecureSecret(
			KeyReference.fromString("${secretRef:test-provider:my-secret}"),
			"any-data".getBytes());

		Mockito.when(
			_secretVaultReader.getSecret(0L, "my-secret")
		).thenReturn(
			mockSecret
		);

		SecureSecret result = _secretManagerImpl.getSecret(0L, keyRef);

		Assert.assertEquals(mockSecret, result);
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

	@Mock
	private ServiceTrackerMap<String, SecretVaultReader> _readerServiceTrackerMap;

	@Mock
	private ServiceTrackerMap<String, SecretVaultWriter> _writerServiceTrackerMap;

	private SecretManagerImpl _secretManagerImpl;

	@Mock
	private SecretVaultReader _secretVaultReader;

	@Mock
	private SecretVaultWriter _secretVaultWriter;

}