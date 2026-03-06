/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.keymanager.provider.gcp.internal;

import com.google.auth.oauth2.AccessToken;
import com.google.auth.oauth2.GoogleCredentials;

import com.liferay.keymanager.SecureSecret;
import com.liferay.keymanager.provider.gcp.internal.configuration.GoogleAdcProviderConfiguration;
import com.liferay.keymanager.spi.KeyProvider;
import com.liferay.portal.test.rule.LiferayUnitTestRule;

import java.util.Collections;
import java.util.Date;
import java.util.List;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;

import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

/**
 * @author Tomas Polesovsky
 */
public class GoogleAdcProviderTest {

	@ClassRule
	@Rule
	public static final LiferayUnitTestRule liferayUnitTestRule =
		LiferayUnitTestRule.INSTANCE;

	@Before
	public void setUp() {
		MockitoAnnotations.openMocks(this);

		_googleCredentialsMockedStatic = Mockito.mockStatic(
			GoogleCredentials.class);
	}

	@After
	public void tearDown() {
		if (_googleCredentialsMockedStatic != null) {
			_googleCredentialsMockedStatic.close();
		}
	}

	@Test
	public void testContainsKey() throws Exception {
		Assert.assertTrue(_googleAdcProvider.containsKey("access-token"));
		Assert.assertFalse(_googleAdcProvider.containsKey("other"));
	}

	@Test
	public void testGetCapabilities() {
		KeyProvider.Capability[] capabilities =
			_googleAdcProvider.getCapabilities();

		List<KeyProvider.Capability> list = List.of(capabilities);

		Assert.assertTrue(list.contains(KeyProvider.Capability.READ));
		Assert.assertTrue(list.contains(KeyProvider.Capability.LIST));
	}

	@Test
	public void testListAliases() throws Exception {
		List<String> aliases = _googleAdcProvider.listAliases();

		Assert.assertEquals(1, aliases.size());
		Assert.assertTrue(aliases.contains("access-token"));
	}

	@Test
	public void testResolveKey() throws Exception {
		Mockito.when(
			_googleAdcProviderConfiguration.enabled()
		).thenReturn(
			true
		);

		Mockito.when(
			_googleAdcProviderConfiguration.defaultScopes()
		).thenReturn(
			new String[] {"scope1"}
		);

		Mockito.when(
			_googleAdcProviderConfiguration.providerId()
		).thenReturn(
			"gcp-adc"
		);

		GoogleCredentials credentials = Mockito.mock(GoogleCredentials.class);

		_googleCredentialsMockedStatic.when(
			GoogleCredentials::getApplicationDefault
		).thenReturn(
			credentials
		);

		Mockito.when(
			credentials.createScoped(Mockito.anyList())
		).thenReturn(
			credentials
		);

		AccessToken accessToken = new AccessToken(
			"mock-token", new Date(System.currentTimeMillis() + 3600000));

		Mockito.when(
			credentials.getAccessToken()
		).thenReturn(
			accessToken
		);

		_googleAdcProvider.activate(_googleAdcProviderConfiguration);

		try (SecureSecret secret = _googleAdcProvider.resolveKey(
				"access-token", Collections.emptyMap())) {

			Assert.assertEquals("mock-token", new String(secret.getChars()));
		}
	}

	@Test(expected = Exception.class)
	public void testResolveKeyInvalidAlias() throws Exception {
		_googleAdcProvider.resolveKey("invalid", Collections.emptyMap());
	}

	@Mock
	private GoogleAdcProviderConfiguration _googleAdcProviderConfiguration;

	@InjectMocks
	private GoogleAdcProvider _googleAdcProvider;

	private MockedStatic<GoogleCredentials> _googleCredentialsMockedStatic;

}
