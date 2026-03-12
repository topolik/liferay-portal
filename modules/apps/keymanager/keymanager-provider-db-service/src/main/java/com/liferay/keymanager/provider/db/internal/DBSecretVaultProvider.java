/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.keymanager.provider.db.internal;

import com.liferay.keymanager.KeyReference;
import com.liferay.keymanager.provider.db.internal.configuration.DBSecretVaultProviderConfiguration;
import com.liferay.keymanager.provider.db.model.SecretEntry;
import com.liferay.keymanager.provider.db.service.SecretEntryLocalService;
import com.liferay.keymanager.secret.SecretManagerException;
import com.liferay.keymanager.secret.SecureSecret;
import com.liferay.keymanager.spi.secret.SecretVaultProvider;
import com.liferay.osgi.util.configuration.ConfigurationFactoryUtil;
import com.liferay.portal.configuration.metatype.bnd.util.ConfigurableUtil;
import com.liferay.portal.kernel.dao.jdbc.OutputBlob;
import com.liferay.portal.kernel.service.CompanyLocalService;
import com.liferay.portal.kernel.util.StreamUtil;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;

import java.sql.Blob;

import java.util.List;
import java.util.Map;

import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.ConfigurationPolicy;
import org.osgi.service.component.annotations.Modified;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Tomas Polesovsky
 */
@Component(
	factory = "com.liferay.keymanager.provider.db.internal.DBSecretVaultProvider",
	property = "providerId=db", service = SecretVaultProvider.class
)
public class DBSecretVaultProvider implements SecretVaultProvider {

	@Override
	public void deleteSecret(long companyId, String identifier)
		throws SecretManagerException {

		try {
			SecretEntry secretEntry = _secretEntryLocalService.fetchSecretEntry(
				companyId, identifier);

			if (secretEntry != null) {
				_secretEntryLocalService.deleteSecretEntry(secretEntry);
			}
		}
		catch (Exception exception) {
			throw new SecretManagerException(
				"Unable to delete secret: " + identifier, exception);
		}
	}

	@Override
	public SecureSecret getSecret(long companyId, String identifier)
		throws SecretManagerException {

		try {
			SecretEntry secretEntry = _secretEntryLocalService.getSecretEntry(
				companyId, identifier);

			return new SecureSecret(
				new KeyReference(
					KeyReference.Type.SECRET, _providerId, identifier),
				_blobToBytes(secretEntry.getCiphertextBlob()));
		}
		catch (Exception exception) {
			throw new SecretManagerException(
				"Unable to fetch secret: " + identifier, exception);
		}
	}

	@Override
	public List<String> getSecretIdentifiers(long companyId)
		throws SecretManagerException {

		try {
			return _secretEntryLocalService.getSecretIdentifiers(companyId);
		}
		catch (Exception exception) {
			throw new SecretManagerException(
				"Unable to list secret identifiers", exception);
		}
	}

	@Override
	public boolean isAllowedCompany(long companyId) {
		if (_companyId == companyId) {
			return true;
		}

		return false;
	}

	@Override
	public void putSecret(long companyId, SecureSecret secureSecret)
		throws SecretManagerException {

		try {
			KeyReference keyReference = secureSecret.getKeyReference();

			String identifier = keyReference.getIdentifier();

			SecretEntry secretEntry = _secretEntryLocalService.fetchSecretEntry(
				companyId, identifier);

			if (secretEntry == null) {
				secretEntry = _secretEntryLocalService.createSecretEntry(0);

				secretEntry.setCompanyId(companyId);
				secretEntry.setAlias(identifier);
			}

			byte[] bytes = secureSecret.getBytes();

			secretEntry.setCiphertextBlob(
				new OutputBlob(
					new ByteArrayInputStream(bytes), bytes.length));

			_secretEntryLocalService.updateSecretEntry(secretEntry);
		}
		catch (Exception exception) {
			throw new SecretManagerException("Unable to put secret", exception);
		}
	}

	@Activate
	@Modified
	protected void activate(Map<String, Object> properties) {
		DBSecretVaultProviderConfiguration dbSecretVaultProviderConfiguration =
			ConfigurableUtil.createConfigurable(
				DBSecretVaultProviderConfiguration.class, properties);

		_companyId = ConfigurationFactoryUtil.getCompanyId(
			_companyLocalService, properties);
		_providerId = dbSecretVaultProviderConfiguration.providerId();
	}

	private byte[] _blobToBytes(Blob blob) throws Exception {
		try (InputStream inputStream = blob.getBinaryStream()) {
			ByteArrayOutputStream byteArrayOutputStream =
				new ByteArrayOutputStream();

			StreamUtil.transfer(inputStream, byteArrayOutputStream);

			return byteArrayOutputStream.toByteArray();
		}
	}

	private long _companyId;

	@Reference
	private CompanyLocalService _companyLocalService;

	private volatile String _providerId;

	@Reference
	private SecretEntryLocalService _secretEntryLocalService;

}