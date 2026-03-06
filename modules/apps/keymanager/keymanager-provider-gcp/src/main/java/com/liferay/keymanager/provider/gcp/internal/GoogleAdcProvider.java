/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.keymanager.provider.gcp.internal;

import com.google.auth.oauth2.AccessToken;
import com.google.auth.oauth2.GoogleCredentials;
import com.google.auth.oauth2.UserCredentials;

import com.liferay.keymanager.SecureSecret;
import com.liferay.keymanager.provider.gcp.internal.configuration.GoogleAdcProviderConfiguration;
import com.liferay.keymanager.spi.KeyProvider;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;

import java.util.List;
import java.util.Map;
import java.util.Objects;

import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.ConfigurationPolicy;
import org.osgi.service.metatype.annotations.Designate;

/**
 * @author Tomas Polesovsky
 */
@Component(
	configurationPid = "com.liferay.keymanager.provider.gcp.internal.configuration.GoogleAdcProviderConfiguration",
	configurationPolicy = ConfigurationPolicy.REQUIRE,
	service = KeyProvider.class, property = "service.ranking:Integer=1000"
)
@Designate(ocd = GoogleAdcProviderConfiguration.class)
public class GoogleAdcProvider extends BaseGCPTokenKeyProvider {

	@Override
	public int getInitializationPhase() {
		return 1;
	}

	@Override
	public SecureSecret resolveKey(String alias, Map<String, Object> context)
		throws Exception {

		if (!Objects.equals("access-token", alias)) {
			throw new Exception("Unsupported alias for ADC provider: " + alias);
		}

		_credentials.refreshIfExpired();

		AccessToken accessToken = _credentials.getAccessToken();

		return new SecureSecret(accessToken.getTokenValue().toCharArray());
	}

	@Activate
	protected void activate(
		GoogleAdcProviderConfiguration googleAdcProviderConfiguration) {

		_providerId = googleAdcProviderConfiguration.providerId();

		_enabled = googleAdcProviderConfiguration.enabled();

		if (_enabled) {
			try {
				_credentials = GoogleCredentials.getApplicationDefault(
				).createScoped(
					List.of(googleAdcProviderConfiguration.defaultScopes()));

				_credentials.refreshIfExpired();

				_available = true;

				if (_log.isInfoEnabled()) {
					if (_credentials instanceof UserCredentials) {
						_log.info(
							"Google ADC initialized using local gcloud user " +
								"credentials");
					}
					else {
						_log.info(
							"Google ADC initialized using infrastructure " +
								"ambient identity");
					}
				}
			}
			catch (Exception exception) {
				_available = false;

				_log.error(
					"Failed to initialize Google ADC provider", exception);
			}
		}
	}

	private static final Log _log = LogFactoryUtil.getLog(
		GoogleAdcProvider.class);

	private GoogleCredentials _credentials;

}
