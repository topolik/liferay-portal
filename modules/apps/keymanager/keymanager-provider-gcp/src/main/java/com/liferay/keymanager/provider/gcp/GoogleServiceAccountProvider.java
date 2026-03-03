package com.liferay.keymanager.provider.gcp;

import com.liferay.keymanager.KeyMetadata;
import com.liferay.keymanager.KeyProvider;
import com.liferay.keymanager.constants.KeyManagerConstants;
import com.liferay.keymanager.exception.KeyProviderException;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;

import java.util.Arrays;
import java.util.List;

import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Modified;
import org.osgi.service.metatype.annotations.AttributeDefinition;
import org.osgi.service.metatype.annotations.Designate;
import org.osgi.service.metatype.annotations.ObjectClassDefinition;

@Component(
	immediate = true,
	service = KeyProvider.class,
	property = "provider.id=" + KeyManagerConstants.PROVIDER_GCP_SERVICE_ACCOUNT
)
@Designate(ocd = GoogleServiceAccountProvider.Configuration.class)
public class GoogleServiceAccountProvider implements KeyProvider {

	@ObjectClassDefinition(
		name = "Google Service Account Provider Configuration",
		description = "Configuration for Google Service Account token provider"
	)
	public @interface Configuration {

		@AttributeDefinition(name = "Service Account Key File Path")
		String serviceAccountKeyPath() default "";

		@AttributeDefinition(name = "Default Scopes")
		String[] defaultScopes() default {"https://www.googleapis.com/auth/cloud-platform"};

		@AttributeDefinition(name = "Enabled")
		boolean enabled() default false;

	}

	@Activate
	@Modified
	protected void activate(Configuration configuration) {
		_serviceAccountKeyPath = configuration.serviceAccountKeyPath();
		_defaultScopes = List.of(configuration.defaultScopes());
		_enabled = configuration.enabled();

		if (_enabled) {
			try {
				// In production: initialize GoogleCredentials
				_available = true;
			}
			catch (Exception e) {
				_available = false;

				_log.error("Failed to initialize Google Service Account provider", e);
			}
		}
	}

	@Override
	public String getProviderId() {
		return KeyManagerConstants.PROVIDER_GCP_SERVICE_ACCOUNT;
	}

	@Override
	public String getDisplayName() {
		return "Google Service Account";
	}

	@Override
	public char[] resolveKey(String alias) throws KeyProviderException {
		// In production: credentials.refreshIfExpired(); return token
		throw new KeyProviderException("Google Service Account provider requires google-auth-library dependency.");
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
		throw new KeyProviderException("Google Service Account provider does not support storing keys.");
	}

	@Override
	public void deleteKey(String alias) throws KeyProviderException {
	}

	@Override
	public boolean containsKey(String alias) throws KeyProviderException {
		return "default".equals(alias);
	}

	@Override
	public List<String> listAliases() throws KeyProviderException {
		return List.of("default");
	}

	@Override
	public KeyMetadata getKeyMetadata(String alias) throws KeyProviderException {
		return new KeyMetadata.Builder()
			.alias(alias)
			.provider(getProviderId())
			.keyType("OAUTH2_ACCESS_TOKEN")
			.rotatable(false)
			.build();
	}

	@Override
	public int getPriority() {
		return 40;
	}

	@Override
	public boolean isAvailable() {
		return _available;
	}

	private String _serviceAccountKeyPath;
	private List<String> _defaultScopes;
	private boolean _enabled;
	private volatile boolean _available = false;

	private static final Log _log = LogFactoryUtil.getLog(GoogleServiceAccountProvider.class);

}
