/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.keymanager.provider.gcp.internal;

import com.google.auth.oauth2.AccessToken;
import com.google.auth.oauth2.GoogleCredentials;
import com.google.auth.oauth2.ServiceAccountCredentials;

import com.liferay.keymanager.KeyResolverService;
import com.liferay.keymanager.SecureSecret;
import com.liferay.keymanager.provider.gcp.internal.configuration.GoogleServiceAccountTokenProviderConfiguration;
import com.liferay.keymanager.spi.KeyProvider;

import java.io.ByteArrayInputStream;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.ConfigurationPolicy;
import org.osgi.service.component.annotations.Modified;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.metatype.annotations.Designate;

/**
 * @author Tomas Polesovsky
 */
@Component(
	configurationPid = "com.liferay.keymanager.provider.gcp.internal.configuration.GoogleServiceAccountTokenProviderConfiguration",
	configurationPolicy = ConfigurationPolicy.REQUIRE,
	service = KeyProvider.class, property = "service.ranking:Integer=50"
)
@Designate(ocd = GoogleServiceAccountTokenProviderConfiguration.class)
public class GoogleServiceAccountTokenProvider
	extends BaseGCPTokenKeyProvider {

	@Override
	public int getInitializationPhase() {
		return 2;
	}

	@Override
	public SecureSecret resolveKey(String alias, Map<String, Object> context)
		throws Exception {

		if (!Objects.equals("access-token", alias)) {
			throw new Exception("Unsupported alias: " + alias);
		}

		try (SecureSecret secret = _resolveJsonKey()) {
			byte[] bytes = secret.getBytes();

			try (ByteArrayInputStream byteArrayInputStream =
					new ByteArrayInputStream(bytes)) {

				ServiceAccountCredentials credentials =
					ServiceAccountCredentials.fromStream(
						byteArrayInputStream);

				GoogleCredentials credentialsWithScopes =
					credentials.createScoped(_defaultScopes);

				credentialsWithScopes.refreshIfExpired();

				AccessToken accessToken =
					credentialsWithScopes.getAccessToken();

				return new SecureSecret(
					accessToken.getTokenValue().toCharArray());
			}
			finally {
				Arrays.fill(bytes, (byte)0);
			}
		}
	}

	@Activate
	@Modified
	protected void activate(
		GoogleServiceAccountTokenProviderConfiguration googleServiceAccountTokenProviderConfiguration) {

		_providerId =
			googleServiceAccountTokenProviderConfiguration.providerId();

		_jsonKeyRef =
			googleServiceAccountTokenProviderConfiguration.serviceAccountJsonKey();

		_defaultScopes = Arrays.asList(
			googleServiceAccountTokenProviderConfiguration.defaultScopes());

		_enabled = googleServiceAccountTokenProviderConfiguration.enabled();

		if (_enabled) {
			_available = true;
		}
		else {
			_available = false;
		}
	}

	private SecureSecret _resolveJsonKey() throws Exception {
		if (_keyResolverService.isKeyReference(_jsonKeyRef)) {
			return _keyResolverService.resolveSecure(_jsonKeyRef);
		}

		return new SecureSecret(_jsonKeyRef.toCharArray());
	}

	private List<String> _defaultScopes;
	private String _jsonKeyRef;

	@Reference
	private KeyResolverService _keyResolverService;

}
