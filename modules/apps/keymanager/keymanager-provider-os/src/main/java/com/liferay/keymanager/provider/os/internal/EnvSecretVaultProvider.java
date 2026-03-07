/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.keymanager.provider.os.internal;

import com.liferay.keymanager.KeyReference;
import com.liferay.keymanager.provider.os.internal.configuration.EnvSecretVaultProviderConfiguration;
import com.liferay.keymanager.secret.SecretManagerException;
import com.liferay.keymanager.secret.SecureSecret;
import com.liferay.keymanager.spi.secret.SecretVaultProvider;
import com.liferay.portal.configuration.metatype.bnd.util.ConfigurableUtil;
import com.liferay.portal.kernel.util.StringUtil;

import java.nio.charset.StandardCharsets;

import java.util.Map;

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
	service = SecretVaultProvider.class
)
public class EnvSecretVaultProvider implements SecretVaultProvider {

	@Override
	public void deleteSecret(String identifier) throws SecretManagerException {
		throw new SecretManagerException("Read-only provider");
	}

	@Override
	public SecureSecret getSecret(String identifier)
		throws SecretManagerException {

		if (!StringUtil.startsWith(identifier, _envVariablePrefix)) {
			throw new SecretManagerException(
				"Access denied to environment variable: " + identifier);
		}

		String value = getEnv(identifier);

		if (value == null) {
			throw new SecretManagerException(
				"Environment variable not found: " + identifier);
		}

		return new SecureSecret(
			new KeyReference(KeyReference.Type.SECRET, _providerId, identifier, ""),
			value.getBytes(StandardCharsets.UTF_8));
	}

	@Override
	public SecureSecret putSecret(SecureSecret secureSecret)
		throws SecretManagerException {

		throw new SecretManagerException("Read-only provider");
	}

	@Activate
	@Modified
	protected void activate(Map<String, Object> properties) {
		EnvSecretVaultProviderConfiguration
			envSecretVaultProviderConfiguration =
				ConfigurableUtil.createConfigurable(
					EnvSecretVaultProviderConfiguration.class, properties);

		_providerId = envSecretVaultProviderConfiguration.providerId();
		_envVariablePrefix =
			envSecretVaultProviderConfiguration.envVariablePrefix();
	}

	protected String getEnv(String name) {
		return System.getenv(name);
	}

	private String _envVariablePrefix;
	private String _providerId;

}
