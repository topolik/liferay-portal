/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.keymanager.provider.os.internal;

import com.liferay.keymanager.KeyReference;
import com.liferay.keymanager.provider.os.internal.configuration.FileSecretVaultProviderConfiguration;
import com.liferay.keymanager.secret.SecretManagerException;
import com.liferay.keymanager.secret.SecureSecret;
import com.liferay.keymanager.spi.secret.SecretVaultProvider;
import com.liferay.portal.configuration.metatype.bnd.util.ConfigurableUtil;
import com.liferay.portal.kernel.util.FileUtil;
import com.liferay.portal.kernel.util.StringUtil;

import java.io.File;

import java.util.ArrayList;
import java.util.Arrays;
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
	configurationPid = "com.liferay.keymanager.provider.os.internal.configuration.FileSecretVaultProviderConfiguration",
	configurationPolicy = ConfigurationPolicy.REQUIRE,
	service = SecretVaultProvider.class
)
public class FileSecretVaultProvider implements SecretVaultProvider {

	@Override
	public void deleteSecret(String identifier) throws SecretManagerException {
		throw new SecretManagerException("Read-only provider");
	}

	@Override
	public SecureSecret getSecret(String identifier)
		throws SecretManagerException {

		try {
			File file = new File(_secretsDirectory, identifier);

			String canonicalPath = file.getCanonicalPath();

			if (!StringUtil.startsWith(canonicalPath, _secretsDirectoryPath)) {
				throw new SecretManagerException(
					"Invalid secret identifier (Path Traversal): " + identifier);
			}

			if (!file.exists()) {
				throw new SecretManagerException("Secret file not found: " + file);
			}

			byte[] bytes = FileUtil.getBytes(file);

			try {
				return new SecureSecret(
					new KeyReference(
						KeyReference.Type.SECRET, _providerId, identifier),
					bytes);
			}
			finally {
				Arrays.fill(bytes, (byte)0);
			}
		}
		catch (Exception exception) {
			throw new SecretManagerException(
				"Unable to get secret: " + identifier, exception);
		}
	}

	@Override
	public List<String> getSecretIdentifiers() throws SecretManagerException {
		List<String> identifiers = new ArrayList<>();

		File secretsDir = new File(_secretsDirectory);

		if (secretsDir.exists() && secretsDir.isDirectory()) {
			_addSecretIdentifiers(secretsDir, "", identifiers);
		}

		return identifiers;
	}

	@Override
	public SecureSecret putSecret(SecureSecret secureSecret)
		throws SecretManagerException {

		throw new SecretManagerException("Read-only provider");
	}

	@Activate
	@Modified
	protected void activate(Map<String, Object> properties) {
		FileSecretVaultProviderConfiguration
			fileSecretVaultProviderConfiguration =
				ConfigurableUtil.createConfigurable(
					FileSecretVaultProviderConfiguration.class, properties);

		_providerId = fileSecretVaultProviderConfiguration.providerId();
		_secretsDirectory =
			fileSecretVaultProviderConfiguration.secretsDirectory();

		try {
			_secretsDirectoryPath = new File(_secretsDirectory).getCanonicalPath();

			if (!_secretsDirectoryPath.endsWith(File.separator)) {
				_secretsDirectoryPath = _secretsDirectoryPath.concat(
					File.separator);
			}
		}
		catch (Exception exception) {
			throw new RuntimeException(exception);
		}
	}

	private void _addSecretIdentifiers(
		File directory, String relativePath, List<String> identifiers) {

		File[] files = directory.listFiles();

		if (files == null) {
			return;
		}

		for (File file : files) {
			String name = file.getName();

			if (!relativePath.isEmpty()) {
				name = relativePath + File.separator + name;
			}

			if (file.isDirectory()) {
				_addSecretIdentifiers(file, name, identifiers);
			}
			else if (file.isFile()) {
				identifiers.add(name);
			}
		}
	}

	private String _providerId;
	private String _secretsDirectory;
	private String _secretsDirectoryPath;

}
