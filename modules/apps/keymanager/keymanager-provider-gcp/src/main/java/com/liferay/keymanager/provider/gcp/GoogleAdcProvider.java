package com.liferay.keymanager.provider.gcp;

import com.google.auth.oauth2.AccessToken;
import com.google.auth.oauth2.GoogleCredentials;

import com.liferay.keymanager.KeyMetadata;
import com.liferay.keymanager.KeyProvider;
import com.liferay.keymanager.constants.KeyManagerConstants;
import com.liferay.keymanager.exception.KeyProviderException;
import com.liferay.keymanager.provider.gcp.internal.configuration.GoogleAdcProviderConfiguration;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;

import java.util.Arrays;
import java.util.List;

import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.ConfigurationPolicy;
import org.osgi.service.metatype.annotations.AttributeDefinition;
import org.osgi.service.metatype.annotations.Designate;
import org.osgi.service.metatype.annotations.ObjectClassDefinition;

@Component(
	configurationPid = "com.liferay.keymanager.provider.gcp.internal.configuration.GoogleAdcProviderConfiguration",
	configurationPolicy = ConfigurationPolicy.REQUIRE, immediate = true,
	service = KeyProvider.class
)
@Designate(ocd = GoogleAdcProviderConfiguration.class)
public class GoogleAdcProvider implements KeyProvider {

	@Activate
	protected void activate(GoogleAdcProviderConfiguration configuration) {
		_providerId = configuration.providerId();
		_displayName = configuration.displayName();
		_defaultScopes = List.of(configuration.defaultScopes());
		_enabled = configuration.enabled();

		if (_enabled) {
			try {
				_credentials = GoogleCredentials.getApplicationDefault()
					.createScoped(_defaultScopes);

				_credentials.refreshIfExpired();

				_available = true;

				if (_log.isInfoEnabled()) {
					_log.info(
						"Google ADC provider initialized: id=" + _providerId);
				}
			}
			catch (Exception e) {
				_available = false;

				_log.error("Failed to initialize Google ADC provider.", e);
			}
		}
		else {
			_available = false;
			_credentials = null;
		}
	}

	@Override
	public String getProviderId() {
		return _providerId;
	}

	@Override
	public String getDisplayName() {
		return _displayName;
	}

	@Override
	public char[] resolveKey(String alias) throws KeyProviderException {
		if (!_enabled || _credentials == null) {
			throw new KeyProviderException("Google ADC provider is not enabled or initialized");
		}

		if (!"access-token".equals(alias)) {
			throw new KeyProviderException("Unsupported alias for ADC provider: " + alias);
		}

		try {
			_credentials.refreshIfExpired();

			AccessToken accessToken = _credentials.getAccessToken();

			return accessToken.getTokenValue().toCharArray();
		}
		catch (Exception e) {
			throw new KeyProviderException("Failed to resolve access token via ADC", e);
		}
	}

	@Override
	public byte[] resolveKeyBytes(String alias) throws KeyProviderException {
		char[] chars = resolveKey(alias);

		byte[] bytes = new byte[chars.length];

		for (int i = 0; i < chars.length; i++) {
			bytes[i] = (byte)chars[i];
		}

		Arrays.fill(chars, '\0');

		return bytes;
	}

	@Override
	public void storeKey(String alias, char[] value) throws KeyProviderException {
		throw new KeyProviderException("ADC provider does not support storing keys");
	}

	@Override
	public void deleteKey(String alias) throws KeyProviderException {
		throw new KeyProviderException("ADC provider does not support deleting keys");
	}

	@Override
	public boolean containsKey(String alias) throws KeyProviderException {
		return "access-token".equals(alias);
	}

	@Override
	public List<String> listAliases() throws KeyProviderException {
		return List.of("access-token");
	}

	@Override
	public KeyMetadata getKeyMetadata(String alias) throws KeyProviderException {
		if (!containsKey(alias)) {
			return null;
		}

		return new KeyMetadata.Builder()
			.alias(alias)
			.provider(getProviderId())
			.keyType("DYNAMIC_TOKEN")
			.rotatable(false)
			.build();
	}

	@Override
	public int getPriority() {
		return 45;
	}

	@Override
	public boolean isAvailable() {
		return _available;
	}

	private String _displayName;
	private String _providerId;
	private List<String> _defaultScopes;
	private boolean _enabled;
	private volatile boolean _available = false;
	private GoogleCredentials _credentials;

	private static final Log _log = LogFactoryUtil.getLog(GoogleAdcProvider.class);

}
