/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.keymanager.provider.os.internal;

import com.liferay.keymanager.KeyReference;
import com.liferay.keymanager.provider.os.internal.configuration.EnvSecretVaultProviderConfiguration;
import com.liferay.keymanager.secret.SecureSecret;
import com.liferay.keymanager.spi.secret.SecretVaultReader;
import com.liferay.portal.configuration.metatype.bnd.util.ConfigurableUtil;
import com.liferay.portal.kernel.util.Validator;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.ConfigurationPolicy;
import org.osgi.service.component.annotations.Modified;

/**
 * @author Tomas Polesovsky
 */
@Component(
	configurationPid = "com.liferay.keymanager.provider.os.internal.configuration.EnvSecretVaultProviderConfiguration",
	configurationPolicy = ConfigurationPolicy.REQUIRE,
	property = "keymanager.provider.id=env-secret",
	service = SecretVaultReader.class
)
public class EnvSecretVaultProvider implements SecretVaultReader {

	@Override
	public SecureSecret getSecret(long companyId, String identifier) {

		String value = getEnv(identifier);

		if (Validator.isNull(value)) {
			return null;
		}

		byte[] bytes = value.getBytes();

		try {
			return new SecureSecret(
				new KeyReference(
					KeyReference.Type.SECRET, KeyReference.ANY_PROVIDER,
					identifier),
				bytes);
		}
		finally {
			if (bytes != null) {
				java.util.Arrays.fill(bytes, (byte)0);
			}
		}
	}

	@Override
	public List<String> getSecretIdentifiers(long companyId) {
		if (companyId != 0) {
			return Collections.emptyList();
		}

		return new ArrayList(_envKeysMap.keySet());
	}

	@Override
	public boolean isAllowedCompany(long companyId) {
		if (!_enabled) {
			return false;
		}

		return true;
	}

	@Activate
	@Modified
	protected void activate(Map<String, Object> properties) {
		EnvSecretVaultProviderConfiguration envSecretVaultProviderConfiguration =
			ConfigurableUtil.createConfigurable(
				EnvSecretVaultProviderConfiguration.class, properties);

		_enabled = envSecretVaultProviderConfiguration.enabled();

		String envVariablePrefix =
			envSecretVaultProviderConfiguration.envVariablePrefix();

		_envKeysMap = new ConcurrentHashMap<>();

		for (String key : getSystemEnv().keySet()) {
			if (Validator.isNull(envVariablePrefix)) {
				_envKeysMap.put(key.toLowerCase(), key);
			}
			else {
				String lowerCaseKey = key.toLowerCase();
				String lowerCasePrefix = envVariablePrefix.toLowerCase();

				if (lowerCaseKey.startsWith(lowerCasePrefix)) {
					_envKeysMap.put(
						lowerCaseKey.substring(lowerCasePrefix.length()),
						key);
				}
			}
		}
	}

	protected Map<String, String> getSystemEnv() {
		return System.getenv();
	}

	protected String getEnv(String name) {
		return getSystemEnv().get(_envKeysMap.get(name.toLowerCase()));
	}

	private volatile boolean _enabled;
	private volatile Map<String, String> _envKeysMap;

}