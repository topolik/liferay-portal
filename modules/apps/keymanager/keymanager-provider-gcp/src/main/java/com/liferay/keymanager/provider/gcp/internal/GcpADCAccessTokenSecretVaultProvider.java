/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.keymanager.provider.gcp.internal;

import com.google.auth.oauth2.AccessToken;
import com.google.auth.oauth2.GoogleCredentials;

import com.liferay.keymanager.KeyReference;
import com.liferay.keymanager.provider.gcp.internal.configuration.GcpADCAccessTokenSecretVaultProviderConfiguration;
import com.liferay.keymanager.secret.SecretManagerException;
import com.liferay.keymanager.secret.SecureSecret;
import com.liferay.keymanager.spi.secret.SecretVaultProvider;
import com.liferay.osgi.util.configuration.ConfigurationFactoryUtil;
import com.liferay.petra.string.StringBundler;
import com.liferay.portal.configuration.metatype.bnd.util.ConfigurableUtil;
import com.liferay.portal.kernel.service.CompanyLocalService;

import java.io.IOException;

import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Modified;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Tomas Polesovsky
 */
@Component(
	factory = "com.liferay.keymanager.provider.gcp.internal.GcpADCAccessTokenSecretVaultProvider",
	property = "providerId=gcp-adc", service = SecretVaultProvider.class
)
public class GcpADCAccessTokenSecretVaultProvider
	implements SecretVaultProvider {

	@Override
	public void deleteSecret(long companyId, String identifier)
		throws SecretManagerException {

		throw new SecretManagerException("Read-only provider");
	}

	@Override
	public SecureSecret getSecret(long companyId, String identifier)
		throws SecretManagerException {

		try {
			_credentials.refreshIfExpired();

			AccessToken accessToken = _credentials.getAccessToken();

			long expiresAt = Long.MAX_VALUE;

			Date expirationTime = accessToken.getExpirationTime();

			if (expirationTime != null) {
				expiresAt = expirationTime.getTime();
			}

			String json = StringBundler.concat(
				"{\"access_token\":\"", accessToken.getTokenValue(),
				"\", \"expires_at\":", expiresAt, "}");

			try {
				return new SecureSecret(
					new KeyReference(
						KeyReference.Type.SECRET, _providerId, identifier),
					json.getBytes());
			}
			finally {
			}
		}
		catch (Exception exception) {
			String msg =
				"Unable to fetch ADC token. Run 'gcloud auth login'.";

			throw new SecretManagerException(msg, exception);
		}
	}

	@Override
	public List<String> getSecretIdentifiers(long companyId)
		throws SecretManagerException {

		return Arrays.asList("default");
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

		throw new SecretManagerException("Read-only provider");
	}

	@Activate
	@Modified
	protected void activate(Map<String, Object> properties) throws IOException {
		GcpADCAccessTokenSecretVaultProviderConfiguration
			gcpADCAccessTokenSecretVaultProviderConfiguration =
				ConfigurableUtil.createConfigurable(
					GcpADCAccessTokenSecretVaultProviderConfiguration.class,
					properties);

		_companyId = ConfigurationFactoryUtil.getCompanyId(
			_companyLocalService, properties);
		_providerId =
			gcpADCAccessTokenSecretVaultProviderConfiguration.providerId();

		String[] scopes =
			gcpADCAccessTokenSecretVaultProviderConfiguration.scopes();

		GoogleCredentials credentials =
			GoogleCredentials.getApplicationDefault();

		if (scopes != null) {
			credentials = credentials.createScoped(scopes);
		}

		_credentials = credentials;
	}

	private volatile long _companyId;

	@Reference
	private CompanyLocalService _companyLocalService;

	private volatile GoogleCredentials _credentials;
	private volatile String _providerId;

}