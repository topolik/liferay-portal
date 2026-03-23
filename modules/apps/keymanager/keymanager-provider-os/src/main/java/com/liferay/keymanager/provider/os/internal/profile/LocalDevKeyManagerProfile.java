/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.keymanager.provider.os.internal.profile;

import com.liferay.keymanager.spi.profile.KeyManagerProfile;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.util.DigesterUtil;
import com.liferay.portal.kernel.util.FileUtil;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.StringUtil;

import java.io.File;
import java.io.FileOutputStream;

import java.net.InetAddress;

import java.security.KeyStore;
import java.security.SecureRandom;

import java.util.Dictionary;
import java.util.Hashtable;

import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;

import org.osgi.service.cm.Configuration;
import org.osgi.service.cm.ConfigurationAdmin;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Tomas Polesovsky
 */
@Component(
	property = "keymanager.profile.id=local-dev", service = KeyManagerProfile.class
)
public class LocalDevKeyManagerProfile implements KeyManagerProfile {

	@Override
	public void bootstrap() throws Exception {
		String userName = System.getProperty("user.name");
		String hostName = InetAddress.getLocalHost().getHostName();

		String passwordString = DigesterUtil.digestHex(userName + "@" + hostName);

		char[] password = passwordString.toCharArray();

		File keystoreFile = new File(
			System.getProperty("user.home"),
			".liferay/keymanager/local-master.p12");

		if (!keystoreFile.exists()) {
			_log.info("Generating local development master keystore: " + keystoreFile.getAbsolutePath());

			FileUtil.mkdirs(keystoreFile.getParentFile());

			KeyStore keyStore = KeyStore.getInstance("PKCS12");

			keyStore.load(null, password);

			KeyGenerator keyGenerator = KeyGenerator.getInstance("AES");

			keyGenerator.init(256, new SecureRandom());

			SecretKey secretKey = keyGenerator.generateKey();

			keyStore.setKeyEntry(
				"local-master-kek", secretKey, password, null);

			try (FileOutputStream fos = new FileOutputStream(keystoreFile)) {
				keyStore.store(fos, password);
			}
		}

		// Configure KeyStore provider
		Configuration keystoreConfig = _configurationAdmin.getConfiguration(
			"com.liferay.keymanager.provider.os.internal.configuration.KeyStoreCryptoVaultProviderConfiguration",
			null);

		Dictionary<String, Object> keystoreProps = new Hashtable<>();

		keystoreProps.put("enabled", true);
		keystoreProps.put("keystorePath", keystoreFile.getAbsolutePath());
		keystoreProps.put("keystoreType", "PKCS12");
		keystoreProps.put(
			"keystorePassword", "${secretRef:env:LIFERAY_KEYMANAGER_LOCAL_PWD}");

		// We need to ensure the environment variable is set for the provider to read it,
		// or just use the derived password directly if we can.
		// For local dev, let's just put the password in a system property that we can reference.
		System.setProperty("LIFERAY_KEYMANAGER_LOCAL_PWD", passwordString);

		keystoreConfig.update(keystoreProps);

		// Configure DB providers to use keystore-crypto as KEK
		_updateDbProvider(
			"com.liferay.keymanager.provider.db.internal.configuration.DBSystemCryptoVaultProviderConfiguration",
			"${keyRef:keystore-crypto:local-master-kek}");
		_updateDbProvider(
			"com.liferay.keymanager.provider.db.internal.configuration.DBCompanyCryptoVaultProviderConfiguration",
			"${keyRef:keystore-crypto:local-master-kek}");
		_updateDbProvider(
			"com.liferay.keymanager.provider.db.internal.configuration.DBSystemSecretVaultProviderConfiguration",
			"${keyRef:keystore-crypto:local-master-kek}");
		_updateDbProvider(
			"com.liferay.keymanager.provider.db.internal.configuration.DBCompanySecretVaultProviderConfiguration",
			"${keyRef:keystore-crypto:local-master-kek}");
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
		return "local-dev";
	}

	@Override
	public String getSystemDekProviderId() {
		return "db-system-crypto";
	}

	@Override
	public String getSystemKekProviderId() {
		return "db-system-crypto";
	}

	@Override
	public String getSystemSecretProviderId() {
		return "db-system-secret";
	}

	@Override
	public boolean isStrictMode() {
		return false;
	}

	@Override
	public boolean requireFips() {
		return false;
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

	private static final Log _log = LogFactoryUtil.getLog(
		LocalDevKeyManagerProfile.class);

	@Reference
	private ConfigurationAdmin _configurationAdmin;

}