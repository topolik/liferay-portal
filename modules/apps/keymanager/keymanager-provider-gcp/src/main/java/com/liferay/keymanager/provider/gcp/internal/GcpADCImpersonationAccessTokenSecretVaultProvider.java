/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.keymanager.provider.gcp.internal;

import com.google.auth.oauth2.AccessToken;
import com.google.auth.oauth2.GoogleCredentials;
import com.google.auth.oauth2.ImpersonatedCredentials;

import com.liferay.keymanager.KeyReference;
import com.liferay.keymanager.provider.gcp.internal.configuration.GcpADCImpersonationAccessTokenSecretVaultProviderConfiguration;
import com.liferay.keymanager.secret.SecretManagerException;
import com.liferay.keymanager.secret.SecureSecret;
import com.liferay.keymanager.spi.secret.SecretVaultProvider;
import com.liferay.petra.string.StringBundler;
import com.liferay.portal.configuration.metatype.bnd.util.ConfigurableUtil;

import java.io.IOException;

import java.util.Arrays;
import java.util.Collections;
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
	configurationPid = "com.liferay.keymanager.provider.gcp.internal.configuration.GcpADCImpersonationAccessTokenSecretVaultProviderConfiguration",
	configurationPolicy = ConfigurationPolicy.REQUIRE,
	service = SecretVaultProvider.class
)
public class GcpADCImpersonationAccessTokenSecretVaultProvider
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

			String json = StringBundler.concat(
				"{\"access_token\":\"", accessToken.getTokenValue(),
				"\", \"expires_at\":",
				accessToken.getExpirationTime(
				).getTime(),
				"}");

			return new SecureSecret(
				new KeyReference(
					KeyReference.Type.SECRET, _providerId, identifier),
				json.getBytes());
		}
		catch (Exception exception) {
			throw new SecretManagerException(
				"Unable to fetch impersonated token", exception);
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
		GcpADCImpersonationAccessTokenSecretVaultProviderConfiguration
			gcpADCImpersonationAccessTokenSecretVaultProviderConfiguration =
				ConfigurableUtil.createConfigurable(
					GcpADCImpersonationAccessTokenSecretVaultProviderConfiguration.class,
					properties);

		_providerId =
			gcpADCImpersonationAccessTokenSecretVaultProviderConfiguration.
				providerId();

		String[] scopes =
			gcpADCImpersonationAccessTokenSecretVaultProviderConfiguration.
				scopes();

		String targetServiceAccount =
			gcpADCImpersonationAccessTokenSecretVaultProviderConfiguration.
				targetServiceAccount();

		int tokenLifetimeSeconds =
			gcpADCImpersonationAccessTokenSecretVaultProviderConfiguration.
				tokenLifetimeSeconds();

		GoogleCredentials credentials =
			GoogleCredentials.getApplicationDefault();

		_credentials = ImpersonatedCredentials.create(
			credentials, targetServiceAccount, Collections.emptyList(),
			Arrays.asList(scopes), tokenLifetimeSeconds);
	}

	private volatile GoogleCredentials _credentials;
	private volatile String _providerId;

}