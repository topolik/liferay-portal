package com.liferay.keymanager.provider.gcp;

import com.liferay.keymanager.KeyMetadata;
import com.liferay.keymanager.KeyProvider;
import com.liferay.keymanager.constants.KeyManagerConstants;
import com.liferay.keymanager.exception.KeyProviderException;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Deactivate;
import org.osgi.service.component.annotations.Modified;
import org.osgi.service.metatype.annotations.AttributeDefinition;
import org.osgi.service.metatype.annotations.Designate;
import org.osgi.service.metatype.annotations.ObjectClassDefinition;

@Component(
	immediate = true,
	service = KeyProvider.class,
	property = "provider.id=" + KeyManagerConstants.PROVIDER_GCP_KMS
)
@Designate(ocd = GoogleKmsProvider.Configuration.class)
public class GoogleKmsProvider implements KeyProvider {

	@ObjectClassDefinition(
		name = "Google Cloud KMS Provider Configuration",
		description = "Configuration for the Google Cloud KMS key provider"
	)
	public @interface Configuration {

		@AttributeDefinition(name = "Default Project ID")
		String projectId() default "";

		@AttributeDefinition(name = "Default Location")
		String location() default "global";

		@AttributeDefinition(name = "Default Key Ring")
		String keyRing() default "liferay";

		@AttributeDefinition(name = "Enabled")
		boolean enabled() default false;

	}

	@Activate
	@Modified
	protected void activate(Configuration configuration) {
		_projectId = configuration.projectId();
		_location = configuration.location();
		_keyRing = configuration.keyRing();
		_enabled = configuration.enabled();

		if (_enabled) {
			try {
				// In production: _kmsClient = KeyManagementServiceClient.create();
				_available = true;

				if (_log.isInfoEnabled()) {
					_log.info("Google Cloud KMS provider initialized: project=" + _projectId);
				}
			}
			catch (Exception e) {
				_available = false;

				_log.error("Failed to initialize Google Cloud KMS client", e);
			}
		}
	}

	@Deactivate
	protected void deactivate() {
		// Close KMS client if initialized
	}

	@Override
	public String getProviderId() {
		return KeyManagerConstants.PROVIDER_GCP_KMS;
	}

	@Override
	public String getDisplayName() {
		return "Google Cloud KMS";
	}

	@Override
	public char[] resolveKey(String alias) throws KeyProviderException {
		String ciphertext = _encryptedStore.get(alias);

		if (ciphertext == null) {
			throw new KeyProviderException("No encrypted value found for alias: " + alias);
		}

		// In production: decrypt using GCP KMS client
		// DecryptResponse response = _kmsClient.decrypt(resourceName, ByteString.copyFrom(ciphertextBytes));
		throw new KeyProviderException("GCP KMS integration requires google-cloud-kms dependency. Configure and enable in System Settings.");
	}

	@Override
	public byte[] resolveKeyBytes(String alias) throws KeyProviderException {
		char[] chars = resolveKey(alias);

		if (chars == null) {
			return null;
		}

		byte[] bytes = new byte[chars.length];

		for (int i = 0; i < chars.length; i++) {
			bytes[i] = (byte)chars[i];
		}

		Arrays.fill(chars, '\0');

		return bytes;
	}

	@Override
	public void storeKey(String alias, char[] value) throws KeyProviderException {
		// In production: encrypt using GCP KMS then store ciphertext
		throw new KeyProviderException("GCP KMS store requires google-cloud-kms dependency.");
	}

	@Override
	public void deleteKey(String alias) throws KeyProviderException {
		_encryptedStore.remove(alias);
	}

	@Override
	public boolean containsKey(String alias) throws KeyProviderException {
		return _encryptedStore.containsKey(alias);
	}

	@Override
	public List<String> listAliases() throws KeyProviderException {
		return List.copyOf(_encryptedStore.keySet());
	}

	@Override
	public KeyMetadata getKeyMetadata(String alias) throws KeyProviderException {
		if (!containsKey(alias)) {
			return null;
		}

		return new KeyMetadata.Builder()
			.alias(alias)
			.provider(getProviderId())
			.keyType("ENVELOPE_ENCRYPTED")
			.rotatable(true)
			.build();
	}

	@Override
	public int getPriority() {
		return 30;
	}

	@Override
	public boolean isAvailable() {
		return _available;
	}

	private String _projectId;
	private String _location;
	private String _keyRing;
	private boolean _enabled;
	private volatile boolean _available = false;
	private final Map<String, String> _encryptedStore = new ConcurrentHashMap<>();

	private static final Log _log = LogFactoryUtil.getLog(GoogleKmsProvider.class);

}
