/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.keymanager.provider.gcp.internal.profile;

import com.liferay.keymanager.spi.profile.KeyManagerProfile;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.Validator;

import java.util.Dictionary;
import java.util.Hashtable;

import org.osgi.service.cm.Configuration;
import org.osgi.service.cm.ConfigurationAdmin;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Tomas Polesovsky
 */
@Component(
	property = "keymanager.profile.id=gcp", service = KeyManagerProfile.class
)
public class GcpKeyManagerProfile implements KeyManagerProfile {

	@Override
	public void bootstrap() throws Exception {
		String projectId = System.getenv("GOOGLE_CLOUD_PROJECT");

		if (Validator.isNull(projectId)) {
			projectId = System.getenv("GCP_PROJECT_ID");
		}

		if (Validator.isNull(projectId)) {
			_log.error("Unable to infer GCP Project ID from environment variables.");
			return;
		}

		boolean fipsEnforced = GetterUtil.getBoolean(
			System.getenv("LIFERAY_KEYMANAGER_FIPS_ENFORCED"));

		String protectionLevel = fipsEnforced ? "HSM" : "SOFTWARE";

		try (com.google.cloud.kms.v1.KeyManagementServiceClient client =
				com.google.cloud.kms.v1.KeyManagementServiceClient.create()) {

			String locationName =
				"projects/" + projectId + "/locations/global";
			String keyRingId = "liferay-keymanager";

			try {
				client.createKeyRing(
					locationName, keyRingId,
					com.google.cloud.kms.v1.KeyRing.getDefaultInstance());
			}
			catch (com.google.api.gax.rpc.AlreadyExistsException alreadyExistsException) {
				if (_log.isDebugEnabled()) {
					_log.debug(
						"KeyRing already exists", alreadyExistsException);
				}
			}

			String keyId = "system-master-kek";

			com.google.cloud.kms.v1.CryptoKey cryptoKey =
				com.google.cloud.kms.v1.CryptoKey.newBuilder(
				).setPurpose(
					com.google.cloud.kms.v1.CryptoKey.CryptoKeyPurpose.ENCRYPT_DECRYPT
				).setVersionTemplate(
					com.google.cloud.kms.v1.CryptoKeyVersionTemplate.newBuilder(
					).setProtectionLevel(
						fipsEnforced ? com.google.cloud.kms.v1.ProtectionLevel.HSM : com.google.cloud.kms.v1.ProtectionLevel.SOFTWARE
					).setAlgorithm(
						com.google.cloud.kms.v1.CryptoKeyVersion.CryptoKeyVersionAlgorithm.GOOGLE_SYMMETRIC_ENCRYPTION
					).build()
				).build();

			try {
				client.createCryptoKey(
					locationName + "/keyRings/" + keyRingId, keyId, cryptoKey);
			}
			catch (com.google.api.gax.rpc.AlreadyExistsException alreadyExistsException) {
				if (_log.isDebugEnabled()) {
					_log.debug(
						"CryptoKey already exists", alreadyExistsException);
				}
			}
		}

		// Configure GCP KMS System Provider
		_updateGcpKmsProvider(
			"com.liferay.keymanager.provider.gcp.internal.configuration.GcpKmsSystemCryptoVaultProviderConfiguration",
			projectId, protectionLevel);

		// Configure GCP KMS Company Provider
		_updateGcpKmsProvider(
			"com.liferay.keymanager.provider.gcp.internal.configuration.GcpKmsCompanyCryptoVaultProviderConfiguration",
			projectId, protectionLevel);

		// Configure DB providers to use gcp-kms-system-crypto as KEK
		String systemKekRef = "${keyRef:gcp-kms-system-crypto:system-master-kek}";

		_updateDbProvider(
			"com.liferay.keymanager.provider.db.internal.configuration.DBSystemCryptoVaultProviderConfiguration",
			systemKekRef);
		_updateDbProvider(
			"com.liferay.keymanager.provider.db.internal.configuration.DBCompanyCryptoVaultProviderConfiguration",
			systemKekRef);
		_updateDbProvider(
			"com.liferay.keymanager.provider.db.internal.configuration.DBSystemSecretVaultProviderConfiguration",
			systemKekRef);
		_updateDbProvider(
			"com.liferay.keymanager.provider.db.internal.configuration.DBCompanySecretVaultProviderConfiguration",
			systemKekRef);
	}

	@Override
	public String getCompanyDekProviderId() {
		return "db-company-crypto";
	}

	@Override
	public String getCompanyKekProviderId() {
		return "db-company-crypto";
	}

	@Override
	public String getCompanySecretProviderId() {
		return "db-company-secret";
	}

	@Override
	public String getProfileId() {
		return "gcp";
	}

	@Override
	public String getSystemDekProviderId() {
		return "db-system-crypto";
	}

	@Override
	public String getSystemKekProviderId() {
		return "gcp-kms-system-crypto";
	}

	@Override
	public String getSystemSecretProviderId() {
		return "db-system-secret";
	}

	@Override
	public boolean isStrictMode() {
		return true;
	}

	@Override
	public boolean requireFips() {
		return GetterUtil.getBoolean(
			System.getenv("LIFERAY_KEYMANAGER_FIPS_ENFORCED"));
	}

	private void _updateDbProvider(String pid, String masterKeyRef)
		throws Exception {

		Configuration config = _configurationAdmin.getConfiguration(pid, null);

		Dictionary<String, Object> props = config.getProperties();

		if (props == null) {
			props = new Hashtable<>();
		}

		props.put("enabled", true);
		props.put("masterKeyReference", masterKeyRef);

		config.update(props);
	}

	private void _updateGcpKmsProvider(
			String pid, String projectId, String protectionLevel)
		throws Exception {

		Configuration config = _configurationAdmin.getConfiguration(pid, null);

		Dictionary<String, Object> props = config.getProperties();

		if (props == null) {
			props = new Hashtable<>();
		}

		props.put("enabled", true);
		props.put("projectId", projectId);
		props.put("newKeyProtectionLevel", protectionLevel);
		props.put("keyRingPath", "projects/" + projectId + "/locations/global/keyRings/liferay-keymanager");

		config.update(props);
	}

	private static final Log _log = LogFactoryUtil.getLog(
		GcpKeyManagerProfile.class);

	@Reference
	private ConfigurationAdmin _configurationAdmin;

}