/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.keymanager.provider.gcp.internal;

import com.liferay.keymanager.KeyReference;
import com.liferay.keymanager.secret.SecretManager;
import com.liferay.keymanager.secret.SecretManagerException;
import com.liferay.keymanager.secret.SecureSecret;
import com.liferay.portal.kernel.service.CompanyLocalService;
import com.liferay.portal.kernel.util.HashMapBuilder;
import com.liferay.portal.test.rule.LiferayUnitTestRule;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

import java.util.Collections;
import java.util.List;
import java.util.Map;

import org.junit.Assume;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;

import org.mockito.Mockito;

/**
 * A manual test class to verify GCP configurations.
 *
 * Usage:
 * 1. Set environment variables:
 *    export GCP_PROJECT_ID="your-project"
 *    export GCP_KMS_KEY_RING_PATH="projects/.../keyRings/..."
 *    export GCP_SA_JSON_KEY='{...}' (Optional)
 * 2. Run via Gradle:
 *    ./gradlew -p modules/apps/keymanager/keymanager-provider-gcp test --tests GcpManualTest
 *
 * @author Tomas Polesovsky
 */
public class GcpManualTest {

	@ClassRule
	@Rule
	public static final LiferayUnitTestRule liferayUnitTestRule =
		new LiferayUnitTestRule();

	@Before
	public void setUp() {
		_projectId = System.getenv("GCP_PROJECT_ID");
		_keyRingPath = System.getenv("GCP_KMS_KEY_RING_PATH");
		_saJsonKey = System.getenv("GCP_SA_JSON_KEY");

		_companyLocalService = Mockito.mock(CompanyLocalService.class);
	}

	@Test
	public void testAdcIdentityProvider() throws Exception {
		GcpADCAccessTokenSecretVaultProvider provider =
			new GcpADCAccessTokenSecretVaultProvider();

		_inject(provider, "_companyLocalService", _companyLocalService);

		_activate(
			provider,
			HashMapBuilder.<String, Object>put(
				"companyId", 12345L
			).put(
				"provider-id", "gcp-adc"
			).put(
				"providerId", "gcp-adc"
			).put(
				"scopes",
				new String[] {"https://www.googleapis.com/auth/cloud-platform"}
			).build());

		try (SecureSecret secret = provider.getSecret(12345L, "default")) {
			System.out.println("ADC Token: " + new String(secret.getBytes()));
		}
	}

	@Test
	public void testGcpKms() throws Exception {
		Assume.assumeNotNull(_keyRingPath);

		GcpKmsCryptoVaultProvider provider = new GcpKmsCryptoVaultProvider();

		_inject(provider, "_companyLocalService", _companyLocalService);
		_inject(provider, "_secretManager", new MockSecretManager(_saJsonKey));

		_activate(
			provider,
			HashMapBuilder.<String, Object>put(
				"companyId", 12345L
			).put(
				"gcp-auth-key-reference", "db:sa-key"
			).put(
				"gcpAuthKeyReference", "db:sa-key"
			).put(
				"key-ring-path", _keyRingPath
			).put(
				"keyRingPath", _keyRingPath
			).put(
				"new-key-protection-level", "HSM"
			).put(
				"newKeyProtectionLevel", "HSM"
			).put(
				"new-key-rotation-period-seconds", 7776000L
			).put(
				"newKeyRotationPeriodSeconds", 7776000L
			).put(
				"provider-id", "gcp-kms"
			).put(
				"providerId", "gcp-kms"
			).build());

		List<String> aliases = provider.getKeyIdentifiers(12345L);

		System.out.println("KMS Keys: " + aliases);

		if (!aliases.isEmpty()) {
			String alias = aliases.get(0);

			byte[] encrypted = provider.encrypt(12345L, alias, "test".getBytes());

			byte[] decrypted = provider.decrypt(12345L, alias, encrypted);

			System.out.println("KMS Roundtrip: " + new String(decrypted));
		}
	}

	@Test
	public void testGcpSecretManager() throws Exception {
		Assume.assumeNotNull(_projectId);

		GcpSecretManagerSecretVaultProvider provider =
			new GcpSecretManagerSecretVaultProvider();

		_inject(provider, "_companyLocalService", _companyLocalService);
		_inject(provider, "_secretManager", new MockSecretManager(_saJsonKey));

		_activate(
			provider,
			HashMapBuilder.<String, Object>put(
				"companyId", 12345L
			).put(
				"gcp-auth-key-reference", "db:sa-key"
			).put(
				"gcpAuthKeyReference", "db:sa-key"
			).put(
				"project-id", _projectId
			).put(
				"projectId", _projectId
			).put(
				"provider-id", "gcp-ssm"
			).put(
				"providerId", "gcp-ssm"
			).put(
				"kms-key-name", ""
			).put(
				"kmsKeyName", ""
			).put(
				"locations", new String[0]
			).build());

		List<String> ids = provider.getSecretIdentifiers(12345L);

		System.out.println("SSM Secrets: " + ids);
	}

	@Test
	public void testSaKeyIdentityProvider() throws Exception {
		Assume.assumeNotNull(_saJsonKey);

		GcpServiceAccountKeyAccessTokenSecretVaultProvider provider =
			new GcpServiceAccountKeyAccessTokenSecretVaultProvider();

		_inject(provider, "_companyLocalService", _companyLocalService);
		_inject(
			provider, "_secretManager", new MockSecretManager(_saJsonKey));

		_activate(
			provider,
			HashMapBuilder.<String, Object>put(
				"companyId", 12345L
			).put(
				"provider-id", "gcp-sa-key"
			).put(
				"providerId", "gcp-sa-key"
			).put(
				"gcp-auth-key-reference", "db:sa-key"
			).put(
				"gcpAuthKeyReference", "db:sa-key"
			).put(
				"scopes",
				new String[] {"https://www.googleapis.com/auth/cloud-platform"}
			).build());

		try (SecureSecret secret = provider.getSecret(12345L, "default")) {
			System.out.println("SA Token: " + new String(secret.getBytes()));
		}
	}

	private void _activate(Object target, Map<String, Object> properties)
		throws Exception {

		try {
			Method method = target.getClass().getDeclaredMethod(
				"activate", Map.class);

			method.setAccessible(true);

			method.invoke(target, properties);
		}
		catch (NoSuchMethodException noSuchMethodException) {
			Method method = target.getClass().getDeclaredMethod("activate");

			method.setAccessible(true);

			method.invoke(target);
		}
	}

	private void _inject(Object target, String fieldName, Object value)
		throws Exception {

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
			throw new NoSuchFieldException(fieldName);
		}

		field.setAccessible(true);

		field.set(target, value);
	}

	private CompanyLocalService _companyLocalService;
	private String _projectId;
	private String _keyRingPath;
	private String _saJsonKey;

	private static class MockSecretManager implements SecretManager {

		public MockSecretManager(String saJsonKey) {
			_saJsonKey = saJsonKey;
		}

		@Override
		public void deleteSecret(long companyId, KeyReference keyReference) {
		}

		@Override
		public List<String> getProviders(long companyId) {
			return Collections.emptyList();
		}

		@Override
		public SecureSecret getSecret(long companyId, KeyReference keyReference)
			throws SecretManagerException {

			if (_saJsonKey != null) {
				return new SecureSecret(keyReference, _saJsonKey.getBytes());
			}

			throw new SecretManagerException("No SA key provided in env");
		}

		@Override
		public List<KeyReference> getSecretIdentifiers(
			long companyId, String providerId) {

			return Collections.emptyList();
		}

		@Override
		public KeyReference putSecret(
			long companyId, SecureSecret secureSecret) {
			
			return null;
		}

		private final String _saJsonKey;

	}

}