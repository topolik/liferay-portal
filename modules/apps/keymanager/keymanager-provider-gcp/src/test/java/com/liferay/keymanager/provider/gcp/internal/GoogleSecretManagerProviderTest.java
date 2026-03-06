/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.keymanager.provider.gcp.internal;

import com.google.cloud.secretmanager.v1.AccessSecretVersionResponse;
import com.google.cloud.secretmanager.v1.ProjectName;
import com.google.cloud.secretmanager.v1.Secret;
import com.google.cloud.secretmanager.v1.SecretManagerServiceClient;
import com.google.cloud.secretmanager.v1.SecretName;
import com.google.cloud.secretmanager.v1.SecretPayload;
import com.google.cloud.secretmanager.v1.SecretVersionName;
import com.google.protobuf.ByteString;

import com.liferay.keymanager.SecureSecret;
import com.liferay.keymanager.provider.gcp.internal.configuration.GoogleSecretManagerProviderConfiguration;
import com.liferay.keymanager.spi.KeyProvider;
import com.liferay.portal.test.rule.LiferayUnitTestRule;

import java.util.Collections;
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
public class GoogleSecretManagerProviderTest {

	@ClassRule
	@Rule
	public static final LiferayUnitTestRule liferayUnitTestRule =
		LiferayUnitTestRule.INSTANCE;

	@Before
	public void setUp() {
		MockitoAnnotations.openMocks(this);

		_secretManagerServiceClientMockedStatic = Mockito.mockStatic(
			SecretManagerServiceClient.class);
	}

	@After
	public void tearDown() {
		if (_secretManagerServiceClientMockedStatic != null) {
			_secretManagerServiceClientMockedStatic.close();
		}
	}

	@Test
	public void testContainsKey() throws Exception {
		_setUpAvailable();

		Mockito.when(
			_client.getSecret(Mockito.any(SecretName.class))
		).thenReturn(
			Secret.getDefaultInstance()
		);

		Assert.assertTrue(_googleSecretManagerProvider.containsKey("alias"));
	}

	@Test
	public void testDeleteKey() throws Exception {
		_setUpAvailable();

		_googleSecretManagerProvider.deleteKey("alias");

		Mockito.verify(_client).deleteSecret(Mockito.any(SecretName.class));
	}

	@Test
	public void testGetCapabilities() {
		KeyProvider.Capability[] capabilities =
			_googleSecretManagerProvider.getCapabilities();

		List<KeyProvider.Capability> list = List.of(capabilities);

		Assert.assertTrue(list.contains(KeyProvider.Capability.READ));
		Assert.assertTrue(list.contains(KeyProvider.Capability.WRITE));
		Assert.assertTrue(list.contains(KeyProvider.Capability.DELETE));
		Assert.assertTrue(list.contains(KeyProvider.Capability.LIST));
		Assert.assertTrue(list.contains(KeyProvider.Capability.VERSIONING));
	}

	@Test
	public void testListAliases() throws Exception {
		_setUpAvailable();

		SecretManagerServiceClient.ListSecretsPagedResponse response =
			Mockito.mock(
				SecretManagerServiceClient.ListSecretsPagedResponse.class);

		Mockito.when(
			_client.listSecrets(Mockito.any(ProjectName.class))
		).thenReturn(
			response
		);

		Secret secret = Mockito.mock(Secret.class);

		Mockito.when(
			secret.getName()
		).thenReturn(
			"projects/p/secrets/alias1"
		);

		Mockito.when(
			response.iterateAll()
		).thenReturn(
			Collections.singletonList(secret)
		);

		List<String> aliases = _googleSecretManagerProvider.listAliases();

		Assert.assertEquals(1, aliases.size());
		Assert.assertTrue(aliases.contains("alias1"));
	}

	@Test
	public void testResolveKey() throws Exception {
		_setUpAvailable();

		AccessSecretVersionResponse response = Mockito.mock(
			AccessSecretVersionResponse.class);

		Mockito.when(
			_client.accessSecretVersion(Mockito.any(SecretVersionName.class))
		).thenReturn(
			response
		);

		SecretPayload payload = Mockito.mock(SecretPayload.class);

		Mockito.when(
			response.getPayload()
		).thenReturn(
			payload
		);

		Mockito.when(
			payload.getData()
		).thenReturn(
			ByteString.copyFromUtf8("secret-value")
		);

		try (SecureSecret secret = _googleSecretManagerProvider.resolveKey(
				"my-secret", Collections.emptyMap())) {

			Assert.assertEquals("secret-value", new String(secret.getChars()));
		}
	}

	@Test
	public void testStoreKey() throws Exception {
		_setUpAvailable();

		Mockito.when(
			_client.getSecret(Mockito.any(SecretName.class))
		).thenReturn(
			Secret.getDefaultInstance()
		);

		try (SecureSecret secret = new SecureSecret("val".toCharArray())) {
			_googleSecretManagerProvider.storeKey("alias", secret);
		}

		Mockito.verify(_client).addSecretVersion(
			Mockito.any(SecretName.class), Mockito.any(SecretPayload.class));
	}

	private void _setUpAvailable() {
		Mockito.when(
			_googleSecretManagerProviderConfiguration.enabled()
		).thenReturn(
			true
		);

		Mockito.when(
			_googleSecretManagerProviderConfiguration.projectId()
		).thenReturn(
			"test-project"
		);

		Mockito.when(
			_googleSecretManagerProviderConfiguration.providerId()
		).thenReturn(
			"gcp-sm"
		);

		_secretManagerServiceClientMockedStatic.when(
			SecretManagerServiceClient::create
		).thenReturn(
			_client
		);

		_googleSecretManagerProvider.activate(
			_googleSecretManagerProviderConfiguration);
	}

	@Mock
	private SecretManagerServiceClient _client;

	@Mock
	private GoogleSecretManagerProviderConfiguration
		_googleSecretManagerProviderConfiguration;

	@InjectMocks
	private GoogleSecretManagerProvider _googleSecretManagerProvider;

	private MockedStatic<SecretManagerServiceClient>
		_secretManagerServiceClientMockedStatic;

}
