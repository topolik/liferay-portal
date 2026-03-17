/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.keymanager.internal.secret;

import com.liferay.keymanager.KeyReference;
import com.liferay.keymanager.secret.SecretManagerException;
import com.liferay.keymanager.secret.SecureSecret;
import com.liferay.keymanager.spi.secret.SecretVaultProvider;
import com.liferay.keymanager.spi.secret.SecretVaultReader;
import com.liferay.keymanager.spi.secret.SecretVaultWriter;
import com.liferay.portal.test.rule.LiferayUnitTestRule;

import java.lang.reflect.Field;

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
public class SecretManagerImplTest {

	@ClassRule
	@Rule
	public static final LiferayUnitTestRule liferayUnitTestRule =
		LiferayUnitTestRule.INSTANCE;

	@Before
	public void setUp() throws Exception {
		MockitoAnnotations.openMocks(this);

		_secretManagerImpl = new SecretManagerImpl();

		// Manually inject mock ServiceTrackers

		Field readerField = SecretManagerImpl.class.getDeclaredField(
			"_readerServiceTracker");

		readerField.setAccessible(true);

		readerField.set(_secretManagerImpl, _readerServiceTracker);

		Field writerField = SecretManagerImpl.class.getDeclaredField(
			"_writerServiceTracker");

		writerField.setAccessible(true);

		writerField.set(_secretManagerImpl, _writerServiceTracker);

		ServiceReference<SecretVaultReader> readerServiceReference =
			Mockito.mock(ServiceReference.class);

		Mockito.when(
			readerServiceReference.getProperty("providerId")
		).thenReturn(
			"test-provider"
		);

		SortedMap<ServiceReference<SecretVaultReader>, SecretVaultReader>
			readers = new TreeMap<>();

		readers.put(readerServiceReference, _secretVaultReader);

		Mockito.when(
			_readerServiceTracker.getTracked()
		).thenReturn(
			readers
		);

		ServiceReference<SecretVaultWriter> writerServiceReference =
			Mockito.mock(ServiceReference.class);

		Mockito.when(
			writerServiceReference.getProperty("providerId")
		).thenReturn(
			"test-provider"
		);

		SortedMap<ServiceReference<SecretVaultWriter>, SecretVaultWriter>
			writers = new TreeMap<>();

		writers.put(writerServiceReference, _secretVaultWriter);

		Mockito.when(
			_writerServiceTracker.getTracked()
		).thenReturn(
			writers
		);

		Mockito.when(
			_secretVaultReader.isAllowedCompany(Mockito.anyLong())
		).thenReturn(
			true
		);
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

	@Test(expected = SecretManagerException.class)
	public void testGetSecretWrongProvider() throws Exception {
		KeyReference keyRef = KeyReference.fromString(
			"${secretRef:wrong-provider:my-secret}");

		_secretManagerImpl.getSecret(0L, keyRef);
	}

	@Test
	public void testPutSecret() throws Exception {
		KeyReference keyRef = KeyReference.fromString(
			"${secretRef:test-provider:my-new-secret}");

		SecureSecret secureSecret = new SecureSecret(
			keyRef, "new-data".getBytes());

		_secretManagerImpl.putSecret(0L, secureSecret);

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

	private SecretManagerImpl _secretManagerImpl;

	@Mock
	private SecretVaultReader _secretVaultReader;

	@Mock
	private SecretVaultWriter _secretVaultWriter;

	@Mock
	private ServiceTracker<SecretVaultReader, SecretVaultReader>
		_readerServiceTracker;

	@Mock
	private ServiceTracker<SecretVaultWriter, SecretVaultWriter>
		_writerServiceTracker;

}