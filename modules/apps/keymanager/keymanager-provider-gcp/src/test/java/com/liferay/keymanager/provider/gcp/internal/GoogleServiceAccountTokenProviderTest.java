/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.keymanager.provider.gcp.internal;

import com.google.auth.oauth2.AccessToken;
import com.google.auth.oauth2.ServiceAccountCredentials;

import com.liferay.keymanager.KeyResolverService;
import com.liferay.keymanager.SecureSecret;
import com.liferay.keymanager.provider.gcp.internal.configuration.GoogleServiceAccountTokenProviderConfiguration;
import com.liferay.keymanager.spi.KeyProvider;
import com.liferay.portal.test.rule.LiferayUnitTestRule;

import java.io.InputStream;
import java.util.Collections;
import java.util.Date;

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
public class GoogleServiceAccountTokenProviderTest {

	@ClassRule
	@Rule
	public static final LiferayUnitTestRule liferayUnitTestRule =
		LiferayUnitTestRule.INSTANCE;

	@Before
	public void setUp() {
		MockitoAnnotations.openMocks(this);

		_serviceAccountCredentialsMockedStatic = Mockito.mockStatic(
			ServiceAccountCredentials.class);
	}

	@After
	public void tearDown() {
		if (_serviceAccountCredentialsMockedStatic != null) {
			_serviceAccountCredentialsMockedStatic.close();
		}
	}

	@Test
	public void testResolveKey() throws Exception {
		Mockito.when(
			_googleServiceAccountTokenProviderConfiguration.enabled()
		).thenReturn(
			true
		);

		Mockito.when(
			_googleServiceAccountTokenProviderConfiguration.serviceAccountJsonKey()
		).thenReturn(
			"mock-json-key"
		);

		Mockito.when(
			_googleServiceAccountTokenProviderConfiguration.defaultScopes()
		).thenReturn(
			new String[] {"scope1"}
		);

		Mockito.when(
			_googleServiceAccountTokenProviderConfiguration.providerId()
		).thenReturn(
			"gcp-sa-token"
		);

		ServiceAccountCredentials credentials = Mockito.mock(
			ServiceAccountCredentials.class);

		_serviceAccountCredentialsMockedStatic.when(
			() -> ServiceAccountCredentials.fromStream(
				Mockito.any(InputStream.class))
		).thenReturn(
			credentials
		);

		Mockito.when(
			credentials.createScoped(Mockito.anyList())
		).thenReturn(
			credentials
		);

		AccessToken accessToken = new AccessToken(
			"sa-token", new Date(System.currentTimeMillis() + 3600000));

		Mockito.when(
			credentials.getAccessToken()
		).thenReturn(
			accessToken
		);

		_googleServiceAccountTokenProvider.activate(
			_googleServiceAccountTokenProviderConfiguration);

		try (SecureSecret secret =
				_googleServiceAccountTokenProvider.resolveKey(
					"access-token", Collections.emptyMap())) {

			Assert.assertEquals("sa-token", new String(secret.getChars()));
		}
	}

	@Mock
	private GoogleServiceAccountTokenProviderConfiguration
		_googleServiceAccountTokenProviderConfiguration;

	@InjectMocks
	private GoogleServiceAccountTokenProvider
		_googleServiceAccountTokenProvider;

	@Mock
	private KeyResolverService _keyResolverService;

	private MockedStatic<ServiceAccountCredentials>
		_serviceAccountCredentialsMockedStatic;

}
