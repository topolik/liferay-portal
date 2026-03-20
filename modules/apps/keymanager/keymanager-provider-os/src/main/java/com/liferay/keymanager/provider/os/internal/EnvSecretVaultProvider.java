/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.keymanager.provider.os.internal;

import com.liferay.keymanager.KeyReference;
import com.liferay.keymanager.provider.os.internal.configuration.EnvSecretVaultProviderConfiguration;
import com.liferay.keymanager.secret.SecretManagerException;
import com.liferay.keymanager.secret.SecureSecret;
import com.liferay.keymanager.spi.secret.SecretVaultReader;
import com.liferay.portal.configuration.metatype.bnd.util.ConfigurableUtil;
import com.liferay.portal.kernel.util.Validator;

import java.util.ArrayList;
import java.util.List;
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
	service = SecretVaultReader.class
)
public class EnvSecretVaultProvider implements SecretVaultReader {

	@Override
	public SecureSecret getSecret(long companyId, String identifier)
		throws SecretManagerException {

		String envVariableName = identifier;

		if (Validator.isNotNull(_envVariablePrefix)) {
			envVariableName = _envVariablePrefix + identifier;
		}

		String value = getEnv(envVariableName);

		if (Validator.isNull(value)) {
			return null;
		}

		return new SecureSecret(
			new KeyReference(KeyReference.Type.SECRET, _providerId, identifier),
			value.getBytes());
	}

	@Override
	public List<String> getSecretIdentifiers(long companyId)
		throws SecretManagerException {

		List<String> identifiers = new ArrayList<>();

		Map<String, String> env = getEnv();

		for (String key : env.keySet()) {
			if (Validator.isNull(_envVariablePrefix) ||
				key.startsWith(_envVariablePrefix)) {

				if (Validator.isNotNull(_envVariablePrefix)) {
					identifiers.add(key.substring(_envVariablePrefix.length()));
				}
				else {
					identifiers.add(key);
				}
			}
		}

		return identifiers;
	}

	@Override
	public boolean isAllowedCompany(long companyId) {
		return true;
	}

	@Activate
	@Modified
	protected void activate(Map<String, Object> properties) {
		EnvSecretVaultProviderConfiguration envSecretVaultProviderConfiguration =
			ConfigurableUtil.createConfigurable(
				EnvSecretVaultProviderConfiguration.class, properties);

		_envVariablePrefix =
			envSecretVaultProviderConfiguration.envVariablePrefix();
		_providerId = envSecretVaultProviderConfiguration.providerId();
	}

	protected Map<String, String> getEnv() {
		return System.getenv();
	}

	protected String getEnv(String name) {
		return System.getenv(name);
	}

	private volatile String _envVariablePrefix;
	private volatile String _providerId;

}