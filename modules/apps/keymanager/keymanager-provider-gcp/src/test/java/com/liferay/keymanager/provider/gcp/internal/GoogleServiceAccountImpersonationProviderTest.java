/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.keymanager.provider.gcp.internal;

import com.google.cloud.iam.credentials.v1.GenerateAccessTokenResponse;
import com.google.cloud.iam.credentials.v1.IamCredentialsClient;
import com.google.cloud.iam.credentials.v1.ServiceAccountName;

import com.liferay.keymanager.SecureSecret;
import com.liferay.keymanager.provider.gcp.internal.configuration.GoogleServiceAccountImpersonationProviderConfiguration;
import com.liferay.keymanager.spi.KeyProvider;
import com.liferay.portal.test.rule.LiferayUnitTestRule;

import java.util.Collections;

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
public class GoogleServiceAccountImpersonationProviderTest {

	@ClassRule
	@Rule
	public static final LiferayUnitTestRule liferayUnitTestRule =
		LiferayUnitTestRule.INSTANCE;

	@Before
	public void setUp() {
		MockitoAnnotations.openMocks(this);

		_iamCredentialsClientMockedStatic = Mockito.mockStatic(
			IamCredentialsClient.class);
	}

	@After
	public void tearDown() {
		if (_iamCredentialsClientMockedStatic != null) {
			_iamCredentialsClientMockedStatic.close();
		}
	}

	@Test
	public void testResolveKey() throws Exception {
		Mockito.when(
			_googleServiceAccountImpersonationProviderConfiguration.enabled()
		).thenReturn(
			true
		);

		Mockito.when(
			_googleServiceAccountImpersonationProviderConfiguration.targetServiceAccountEmail()
		).thenReturn(
			"target@example.com"
		);

		Mockito.when(
			_googleServiceAccountImpersonationProviderConfiguration.delegatedScopes()
		).thenReturn(
			new String[] {"scope1"}
		);

		Mockito.when(
			_googleServiceAccountImpersonationProviderConfiguration.providerId()
		).thenReturn(
			"gcp-impersonation"
		);

		_iamCredentialsClientMockedStatic.when(
			IamCredentialsClient::create
		).thenReturn(
			_client
		);

		GenerateAccessTokenResponse response = Mockito.mock(
			GenerateAccessTokenResponse.class);

		Mockito.when(
			_client.generateAccessToken(
				Mockito.any(ServiceAccountName.class), Mockito.anyList(),
				Mockito.isNull(), Mockito.any())
		).thenReturn(
			response
		);

		Mockito.when(
			response.getAccessToken()
		).thenReturn(
			"impersonated-token"
		);

		_googleServiceAccountImpersonationProvider.activate(
			_googleServiceAccountImpersonationProviderConfiguration);

		try (SecureSecret secret =
				_googleServiceAccountImpersonationProvider.resolveKey(
					"access-token", Collections.emptyMap())) {

			Assert.assertEquals(
				"impersonated-token", new String(secret.getChars()));
		}
	}

	@Mock
	private IamCredentialsClient _client;

	@Mock
	private GoogleServiceAccountImpersonationProviderConfiguration
		_googleServiceAccountImpersonationProviderConfiguration;

	@InjectMocks
	private GoogleServiceAccountImpersonationProvider
		_googleServiceAccountImpersonationProvider;

	private MockedStatic<IamCredentialsClient> _iamCredentialsClientMockedStatic;

}
