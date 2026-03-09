/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.keymanager.internal.secret;

import com.liferay.keymanager.KeyReference;
import com.liferay.keymanager.secret.SecretManager;
import com.liferay.keymanager.secret.SecretManagerException;
import com.liferay.keymanager.secret.SecureSecret;
import com.liferay.keymanager.spi.secret.SecretVaultProvider;
import com.liferay.osgi.service.tracker.collections.map.ServiceTrackerMap;
import com.liferay.portal.test.rule.LiferayUnitTestRule;

import java.lang.reflect.Field;

import java.util.Collections;
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

		_secretManager = new SecretManagerImpl();

		// Manually inject mock ServiceTrackerMap

		Field field = SecretManagerImpl.class.getDeclaredField(
			"_serviceTrackerMap");

		field.setAccessible(true);

		field.set(_secretManager, _serviceTrackerMap);

		Mockito.when(
			_serviceTrackerMap.getService("test-provider")
		).thenReturn(
			_secretVaultProvider
		);
	}

	@Test
	public void testGetSecret() throws Exception {
		KeyReference keyRef = KeyReference.fromString(
			"${secretRef:test-provider:my-secret}");

		SecureSecret mockSecret = new SecureSecret(keyRef, "data".getBytes());

		Mockito.when(
			_secretVaultProvider.getSecret("my-secret")
		).thenReturn(
			mockSecret
		);

		SecureSecret result = _secretManager.getSecret(keyRef);

		Assert.assertEquals(mockSecret, result);
	}

	@Test
	public void testGetSecretIdentifiers() throws Exception {
		List<String> identifiers = Collections.singletonList("secret-1");

		Mockito.when(
			_secretVaultProvider.getSecretIdentifiers()
		).thenReturn(
			identifiers
		);

		List<String> result = _secretManager.getSecretIdentifiers(
			"test-provider");

		Assert.assertEquals(identifiers, result);
	}

	@Test
	public void testPutSecret() throws Exception {
		KeyReference keyRef = KeyReference.fromString(
			"${secretRef:test-provider:my-new-secret}");

		SecureSecret secureSecret = new SecureSecret(keyRef, "new-data".getBytes());

		_secretManager.putSecret(secureSecret);

		Mockito.verify(_secretVaultProvider).putSecret(secureSecret);
	}

	@Test(expected = SecretManagerException.class)
	public void testGetSecretWrongProvider() throws Exception {
		KeyReference keyRef = KeyReference.fromString(
			"${secretRef:wrong-provider:my-secret}");

		_secretManager.getSecret(keyRef);
	}

	@Mock
	private SecretVaultProvider _secretVaultProvider;

	@Mock
	private ServiceTrackerMap<String, SecretVaultProvider> _serviceTrackerMap;

	private SecretManagerImpl _secretManager;

}
