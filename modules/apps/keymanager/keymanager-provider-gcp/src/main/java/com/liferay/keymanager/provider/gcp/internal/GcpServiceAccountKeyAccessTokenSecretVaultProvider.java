/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.keymanager.provider.gcp.internal;

import com.google.auth.oauth2.AccessToken;
import com.google.auth.oauth2.GoogleCredentials;

import com.liferay.keymanager.KeyReference;
import com.liferay.keymanager.provider.gcp.internal.configuration.GcpServiceAccountKeyAccessTokenSecretVaultProviderConfiguration;
import com.liferay.keymanager.secret.SecretManager;
import com.liferay.keymanager.secret.SecretManagerException;
import com.liferay.keymanager.secret.SecureSecret;
import com.liferay.keymanager.spi.secret.SecretVaultProvider;
import com.liferay.petra.string.StringBundler;
import com.liferay.portal.configuration.metatype.bnd.util.ConfigurableUtil;

import java.io.ByteArrayInputStream;
import java.io.IOException;

import java.util.Arrays;
import java.util.Date;
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
	configurationPid = "com.liferay.keymanager.provider.gcp.internal.configuration.GcpServiceAccountKeyAccessTokenSecretVaultProviderConfiguration",
	configurationPolicy = ConfigurationPolicy.REQUIRE,
	service = SecretVaultProvider.class
)
public class GcpServiceAccountKeyAccessTokenSecretVaultProvider
	implements SecretVaultProvider {

	@Override
	public void deleteSecret(String identifier) throws SecretManagerException {
		throw new SecretManagerException("Read-only provider");
	}

	@Override
	public SecureSecret getSecret(String identifier)
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

			return new SecureSecret(
				new KeyReference(
					KeyReference.Type.SECRET, _providerId, identifier),
				json.getBytes());
		}
		catch (Exception exception) {
			throw new SecretManagerException(
				"Unable to fetch SA token", exception);
		}
	}

	@Override
	public List<String> getSecretIdentifiers() throws SecretManagerException {
		return Arrays.asList("default");
	}

	@Override
	public void putSecret(SecureSecret secureSecret)
		throws SecretManagerException {

		throw new SecretManagerException("Read-only provider");
	}

	@Activate
	@Modified
	protected void activate(Map<String, Object> properties) throws IOException {
		GcpServiceAccountKeyAccessTokenSecretVaultProviderConfiguration
			gcpServiceAccountKeyAccessTokenSecretVaultProviderConfiguration =
				ConfigurableUtil.createConfigurable(
					GcpServiceAccountKeyAccessTokenSecretVaultProviderConfiguration.class,
					properties);

		_providerId =
			gcpServiceAccountKeyAccessTokenSecretVaultProviderConfiguration.
				providerId();

		String[] scopes =
			gcpServiceAccountKeyAccessTokenSecretVaultProviderConfiguration.
				scopes();

		String gcpAuthKeyReference =
			gcpServiceAccountKeyAccessTokenSecretVaultProviderConfiguration.
				gcpAuthKeyReference();

		try {
			try (SecureSecret secureSecret = _secretManager.getSecret(
					KeyReference.fromString(gcpAuthKeyReference))) {

				GoogleCredentials credentials = GoogleCredentials.fromStream(
					new ByteArrayInputStream(secureSecret.getBytes()));

				if (scopes != null) {
					credentials = credentials.createScoped(scopes);
				}

				_credentials = credentials;
			}
		}
		catch (Exception exception) {
			throw new IOException(
				"Unable to initialize Google credentials", exception);
		}
	}

	private volatile GoogleCredentials _credentials;
	private volatile String _providerId;

	@Reference
	private SecretManager _secretManager;

}