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
import org.osgi.service.metatype.annotations.AttributeDefinition;
import org.osgi.service.metatype.annotations.Designate;
import org.osgi.service.metatype.annotations.ObjectClassDefinition;

@Component(
	immediate = true,
	service = KeyProvider.class,
	property = "provider.id=" + KeyManagerConstants.PROVIDER_GCP_ADC
)
@Designate(ocd = GoogleAdcProvider.Configuration.class)
public class GoogleAdcProvider implements KeyProvider {

	@ObjectClassDefinition(
		name = "Google ADC Provider Configuration",
		description = "Configuration for Google Application Default Credentials provider"
	)
	public @interface Configuration {

		@AttributeDefinition(name = "Default Scopes")
		String[] defaultScopes() default {"https://www.googleapis.com/auth/cloud-platform"};

		@AttributeDefinition(name = "Enabled")
		boolean enabled() default false;

	}

	@Activate
	protected void activate(Configuration configuration) {
		_defaultScopes = List.of(configuration.defaultScopes());
		_enabled = configuration.enabled();

		if (_enabled) {
			try {
				// In production: GoogleCredentials.getApplicationDefault()
				_available = true;
			}
			catch (Exception e) {
				_available = false;

				_log.error("Failed to initialize Google ADC provider.", e);
			}
		}
	}

	@Override
	public String getProviderId() {
		return KeyManagerConstants.PROVIDER_GCP_ADC;
	}

	@Override
	public String getDisplayName() {
		return "Google Application Default Credentials";
	}

	@Override
	public char[] resolveKey(String alias) throws KeyProviderException {
		throw new KeyProviderException("Google ADC provider requires google-auth-library dependency.");
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
		return "access-token".equals(alias) || alias.startsWith("id-token:");
	}

	@Override
	public List<String> listAliases() throws KeyProviderException {
		return List.of("access-token");
	}

	@Override
	public KeyMetadata getKeyMetadata(String alias) throws KeyProviderException {
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

	private List<String> _defaultScopes;
	private boolean _enabled;
	private volatile boolean _available = false;

	private static final Log _log = LogFactoryUtil.getLog(GoogleAdcProvider.class);

}
