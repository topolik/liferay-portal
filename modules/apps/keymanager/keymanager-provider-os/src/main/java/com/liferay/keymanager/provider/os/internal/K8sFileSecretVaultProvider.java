/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.keymanager.provider.os.internal;

import com.liferay.keymanager.KeyReference;
import com.liferay.keymanager.provider.os.internal.configuration.K8sFileSecretVaultProviderConfiguration;
import com.liferay.keymanager.secret.SecretManagerException;
import com.liferay.keymanager.secret.SecureSecret;
import com.liferay.keymanager.spi.secret.SecretVaultProvider;
import com.liferay.keymanager.spi.secret.SecretVaultReader;
import com.liferay.keymanager.spi.secret.SecretVaultWriter;
import com.liferay.portal.configuration.metatype.bnd.util.ConfigurableUtil;
import com.liferay.portal.kernel.util.FileUtil;
import com.liferay.portal.kernel.util.Validator;

import java.io.File;
import java.io.IOException;

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
	configurationPid = "com.liferay.keymanager.provider.os.internal.configuration.K8sFileSecretVaultProviderConfiguration",
	configurationPolicy = ConfigurationPolicy.REQUIRE,
	property = "keymanager.provider.id=k8s-secret",
	service = {
		SecretVaultProvider.class, SecretVaultReader.class,
		SecretVaultWriter.class
	}
)
public class K8sFileSecretVaultProvider
	implements SecretVaultProvider, SecretVaultReader, SecretVaultWriter {

	@Override
	public void deleteSecret(long companyId, String identifier)
		throws SecretManagerException {

		File file = _getFile(identifier);

		if (file.exists()) {
			file.delete();
		}
	}

	@Override
	public SecureSecret getSecret(long companyId, String identifier)
		throws SecretManagerException {

		File file = _getFile(identifier);

		if (!file.exists()) {
			return null;
		}

		byte[] bytes = null;

		try {
			bytes = FileUtil.getBytes(file);

			return new SecureSecret(
				new KeyReference(
					KeyReference.Type.SECRET, KeyReference.ANY_PROVIDER,
					identifier),
				bytes);
		}
		catch (IOException ioException) {
			throw new SecretManagerException(
				"Unable to read secret file: " + file, ioException);
		}
		finally {
			if (bytes != null) {
				Arrays.fill(bytes, (byte)0);
			}
		}
	}

	@Override
	public List<String> getSecretIdentifiers(long companyId)
		throws SecretManagerException {

		List<String> identifiers = new ArrayList<>();

		File directory = new File(_secretsDirectory);

		if (directory.exists() && directory.isDirectory()) {
			File[] files = directory.listFiles();

			if (files != null) {
				for (File file : files) {
					if (file.isFile()) {
						identifiers.add(file.getName());
					}
				}
			}
		}

		return identifiers;
	}

	@Override
	public boolean isAllowedCompany(long companyId) {
		return _enabled;
	}

	@Override
	public void putSecret(long companyId, SecureSecret secureSecret)
		throws SecretManagerException {

		KeyReference keyReference = secureSecret.getKeyReference();

		File file = _getFile(keyReference.getIdentifier());

		try {
			FileUtil.write(file, secureSecret.getBytes());
		}
		catch (IOException ioException) {
			throw new SecretManagerException(
				"Unable to write secret file: " + file, ioException);
		}
	}

	@Activate
	@Modified
	protected void activate(Map<String, Object> properties) {
		K8sFileSecretVaultProviderConfiguration
			k8sFileSecretVaultProviderConfiguration =
				ConfigurableUtil.createConfigurable(
					K8sFileSecretVaultProviderConfiguration.class, properties);

		_enabled = k8sFileSecretVaultProviderConfiguration.enabled();
		_secretsDirectory =
			k8sFileSecretVaultProviderConfiguration.secretsDirectory();
	}

	private File _getFile(String identifier) throws SecretManagerException {
		if (Validator.isNull(identifier)) {
			throw new SecretManagerException("Identifier is null");
		}

		try {
			File secretsDirectory = new File(_secretsDirectory);

			File file = new File(secretsDirectory, identifier);

			String canonicalPath = file.getCanonicalPath();

			if (!canonicalPath.equals(secretsDirectory.getCanonicalPath()) &&
				!canonicalPath.startsWith(
					secretsDirectory.getCanonicalPath() + File.separator)) {

				throw new SecretManagerException(
					"Invalid identifier: " + identifier);
			}

			return file;
		}
		catch (IOException ioException) {
			throw new SecretManagerException(
				"Unable to resolve file path for identifier: " + identifier,
				ioException);
		}
	}

	private volatile boolean _enabled;
	private volatile String _secretsDirectory;

}