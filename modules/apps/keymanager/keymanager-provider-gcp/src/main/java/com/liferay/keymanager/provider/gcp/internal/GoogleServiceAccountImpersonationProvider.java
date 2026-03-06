/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.keymanager.provider.gcp.internal;

import com.google.cloud.iam.credentials.v1.GenerateAccessTokenResponse;
import com.google.cloud.iam.credentials.v1.IamCredentialsClient;
import com.google.cloud.iam.credentials.v1.ServiceAccountName;

import com.liferay.keymanager.SecureSecret;
import com.liferay.keymanager.provider.gcp.internal.configuration.GoogleServiceAccountImpersonationProviderConfiguration;
import com.liferay.keymanager.spi.KeyProvider;
import com.liferay.petra.string.StringBundler;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.ConfigurationPolicy;
import org.osgi.service.component.annotations.Deactivate;
import org.osgi.service.component.annotations.Modified;
import org.osgi.service.metatype.annotations.Designate;

/**
 * @author Tomas Polesovsky
 */
@Component(
	configurationPid = "com.liferay.keymanager.provider.gcp.internal.configuration.GoogleServiceAccountImpersonationProviderConfiguration",
	configurationPolicy = ConfigurationPolicy.REQUIRE,
	service = KeyProvider.class, property = "service.ranking:Integer=100"
)
@Designate(ocd = GoogleServiceAccountImpersonationProviderConfiguration.class)
public class GoogleServiceAccountImpersonationProvider
	extends BaseGCPTokenKeyProvider {

	@Deactivate
	protected void deactivate() {
		_closeClient();
	}

	@Override
	public int getInitializationPhase() {
		return 2; // Remote Identity
	}

	@Override
	public SecureSecret resolveKey(String alias, Map<String, Object> context)
		throws Exception {

		if (!Objects.equals("access-token", alias)) {
			throw new Exception(
				"Unsupported alias for impersonation provider: " + alias);
		}

		if (!_available) {
			throw new Exception("Provider is not available");
		}

		GenerateAccessTokenResponse generateAccessTokenResponse =
			_iamCredentialsClient.generateAccessToken(
				ServiceAccountName.of("-", _targetServiceAccountEmail),
				_delegatedScopes, null, null);

		String accessToken = generateAccessTokenResponse.getAccessToken();

		return new SecureSecret(accessToken.toCharArray());
	}

	@Activate
	@Modified
	protected void activate(
		GoogleServiceAccountImpersonationProviderConfiguration googleServiceAccountImpersonationProviderConfiguration) {

		_providerId =
			googleServiceAccountImpersonationProviderConfiguration.providerId();

		_enabled =
			googleServiceAccountImpersonationProviderConfiguration.enabled();

		_targetServiceAccountEmail =
			googleServiceAccountImpersonationProviderConfiguration.targetServiceAccountEmail();

		_delegatedScopes = Arrays.asList(
			googleServiceAccountImpersonationProviderConfiguration.delegatedScopes());

		if (_enabled && (_targetServiceAccountEmail != null) &&
			!_targetServiceAccountEmail.isEmpty()) {

			try {
				_iamCredentialsClient = IamCredentialsClient.create();

				_available = true;

				if (_log.isInfoEnabled()) {
					_log.info(
						StringBundler.concat(
							"Google Impersonation initialized: id=", _providerId,
							", target=", _targetServiceAccountEmail));
				}
			}
			catch (Exception exception) {
				_available = false;

				if (_log.isErrorEnabled()) {
					_log.error(
						"Failed to initialize Google Impersonation client",
						exception);
				}
			}
		}
		else {
			_available = false;

			_closeClient();
		}
	}

	private void _closeClient() {
		if (_iamCredentialsClient != null) {
			_iamCredentialsClient.close();

			_iamCredentialsClient = null;
		}
	}

	private static final Log _log = LogFactoryUtil.getLog(
		GoogleServiceAccountImpersonationProvider.class);

	private List<String> _delegatedScopes;
	private IamCredentialsClient _iamCredentialsClient;
	private String _targetServiceAccountEmail;

}
