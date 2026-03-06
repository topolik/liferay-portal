/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.keymanager.internal;

import com.liferay.keymanager.KeyResolutionException;
import com.liferay.keymanager.KeyResolverService;
import com.liferay.keymanager.SecureSecret;
import com.liferay.keymanager.internal.audit.KeyAuditService;
import com.liferay.keymanager.internal.cache.KeyCacheManager;
import com.liferay.keymanager.spi.KeyProvider;
import com.liferay.portal.kernel.test.ReflectionTestUtil;
import com.liferay.portal.test.rule.LiferayUnitTestRule;

import java.util.Collections;

import org.junit.Assert;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;

import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import org.osgi.util.tracker.ServiceTracker;

/**
 * @author Tomas Polesovsky
 */
public class KeyResolverServiceImplTest {

	@ClassRule
	@Rule
	public static final LiferayUnitTestRule liferayUnitTestRule =
		LiferayUnitTestRule.INSTANCE;

	@Before
	public void setUp() throws Exception {
		MockitoAnnotations.openMocks(this);

		ReflectionTestUtil.setFieldValue(
			_keyResolverServiceImpl, "_serviceTracker", _serviceTracker);
	}

	@Test
	public void testIsKeyReference() {
		Assert.assertTrue(
			_keyResolverServiceImpl.isKeyReference("${keyref:provider/alias}"));
		Assert.assertFalse(
			_keyResolverServiceImpl.isKeyReference("not-a-reference"));
		Assert.assertFalse(_keyResolverServiceImpl.isKeyReference(null));
	}

	@Test
	public void testResolveMultipleReferences() throws Exception {
		String referenceString =
			"Pass1: ${keyref:p1/a1}, Pass2: ${keyref:p2/a2}";

		Mockito.when(
			_keyCacheManager.get("${keyref:p1/a1}")
		).thenReturn(
			new SecureSecret("v1".toCharArray())
		);

		Mockito.when(
			_keyCacheManager.get("${keyref:p2/a2}")
		).thenReturn(
			new SecureSecret("v2".toCharArray())
		);

		String resolved = _keyResolverServiceImpl.resolve(referenceString);

		Assert.assertEquals("Pass1: v1, Pass2: v2", resolved);
	}

	@Test
	public void testResolveSecure() throws Exception {
		String referenceString = "${keyref:provider/alias}";

		Mockito.when(
			_keyCacheManager.get(referenceString)
		).thenReturn(
			new SecureSecret("v".toCharArray())
		);

		try (SecureSecret secret = _keyResolverServiceImpl.resolveSecure(
				referenceString)) {

			Assert.assertArrayEquals("v".toCharArray(), secret.getChars());
		}
	}

	@Test(expected = KeyResolutionException.class)
	public void testResolveSecureInvalidFormat() throws Exception {
		_keyResolverServiceImpl.resolveSecure("not-a-reference");
	}

	@Test
	public void testResolveWithCacheHit() throws Exception {
		String referenceString = "${keyref:provider/alias}";
		SecureSecret secret = new SecureSecret("resolved-value".toCharArray());

		Mockito.when(
			_keyCacheManager.get(referenceString)
		).thenReturn(
			secret
		);

		String resolved = _keyResolverServiceImpl.resolve(referenceString);

		Assert.assertEquals("resolved-value", resolved);

		Mockito.verify(_keyAuditService).auditAccess(
			"provider", "alias", true, "Cache hit");
	}

	@Test
	public void testResolveWithProvider() throws Exception {
		String referenceString = "${keyref:provider/alias}";

		KeyProvider keyProvider = Mockito.mock(KeyProvider.class);

		Mockito.when(
			keyProvider.getProviderId()
		).thenReturn(
			"provider"
		);

		Mockito.when(
			keyProvider.isAvailable()
		).thenReturn(
			true
		);

		Mockito.when(
			keyProvider.resolveKey(Mockito.eq("alias"), Mockito.anyMap())
		).thenReturn(
			new SecureSecret("secret-value".toCharArray())
		);

		Mockito.when(
			_serviceTracker.getServices()
		).thenReturn(
			new Object[] {keyProvider}
		);

		String resolved = _keyResolverServiceImpl.resolve(referenceString);

		Assert.assertEquals("secret-value", resolved);

		Mockito.verify(_keyAuditService).auditAccess(
			"provider", "alias", true, "Provider resolve");
	}

	@Test(expected = KeyResolutionException.class)
	public void testResolveWithMissingProvider() throws Exception {
		String referenceString = "${keyref:missing-provider/alias}";

		Mockito.when(
			_serviceTracker.getServices()
		).thenReturn(
			new Object[0]
		);

		_keyResolverServiceImpl.resolve(referenceString);
	}

	@Test(expected = KeyResolutionException.class)
	public void testResolveWithProviderFailure() throws Exception {
		String referenceString = "${keyref:provider/alias}";

		KeyProvider keyProvider = Mockito.mock(KeyProvider.class);

		Mockito.when(
			keyProvider.getProviderId()
		).thenReturn(
			"provider"
		);

		Mockito.when(
			keyProvider.isAvailable()
		).thenReturn(
			true
		);

		Mockito.when(
			keyProvider.resolveKey(Mockito.eq("alias"), Mockito.anyMap())
		).thenThrow(
			new Exception("Failed")
		);

		Mockito.when(
			_serviceTracker.getServices()
		).thenReturn(
			new Object[] {keyProvider}
		);

		try {
			_keyResolverServiceImpl.resolve(referenceString);
		}
		finally {
			Mockito.verify(_keyAuditService).auditAccess(
				Mockito.eq("provider"), Mockito.eq("alias"), Mockito.eq(false),
				Mockito.anyString());
		}
	}

	@Mock
	private KeyAuditService _keyAuditService;

	@Mock
	private KeyCacheManager _keyCacheManager;

	@InjectMocks
	private KeyResolverServiceImpl _keyResolverServiceImpl;

	@Mock
	private ServiceTracker<KeyProvider, KeyProvider> _serviceTracker;

}
