package com.liferay.keymanager.provider.gcp;

import com.google.cloud.kms.v1.CryptoKeyName;
import com.google.cloud.kms.v1.DecryptResponse;
import com.google.cloud.kms.v1.EncryptResponse;
import com.google.cloud.kms.v1.KeyManagementServiceClient;
import com.google.protobuf.ByteString;

import com.liferay.keymanager.KeyMetadata;
import com.liferay.keymanager.KeyProvider;
import com.liferay.keymanager.constants.KeyManagerConstants;
import com.liferay.keymanager.exception.KeyProviderException;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;

import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.Base64;
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

		@AttributeDefinition(name = "Default Crypto Key")
		String cryptoKey() default "config-key";

		@AttributeDefinition(name = "Enabled")
		boolean enabled() default false;

	}

	@Activate
	@Modified
	protected void activate(Configuration configuration) {
		_projectId = configuration.projectId();
		_location = configuration.location();
		_keyRing = configuration.keyRing();
		_cryptoKey = configuration.cryptoKey();
		_enabled = configuration.enabled();

		if (_enabled) {
			try {
				_kmsClient = KeyManagementServiceClient.create();
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
		else {
			_available = false;
			_closeKmsClient();
		}
	}

	@Deactivate
	protected void deactivate() {
		_closeKmsClient();
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
		if (!_enabled || _kmsClient == null) {
			throw new KeyProviderException("GCP KMS provider is not enabled or initialized");
		}

		String encodedCiphertext = _encryptedStore.get(alias);

		if (encodedCiphertext == null) {
			return null;
		}

		try {
			byte[] ciphertext = Base64.getDecoder().decode(encodedCiphertext);

			CryptoKeyName keyName = CryptoKeyName.of(_projectId, _location, _keyRing, _cryptoKey);

			DecryptResponse response = _kmsClient.decrypt(keyName, ByteString.copyFrom(ciphertext));

			ByteString plaintext = response.getPlaintext();

			ByteBuffer byteBuffer = plaintext.asReadOnlyByteBuffer();
			CharBuffer charBuffer = StandardCharsets.UTF_8.decode(byteBuffer);

			char[] result = new char[charBuffer.remaining()];
			charBuffer.get(result);

			return result;
		}
		catch (Exception e) {
			throw new KeyProviderException("Failed to decrypt key: " + alias, e);
		}
	}

	@Override
	public byte[] resolveKeyBytes(String alias) throws KeyProviderException {
		char[] chars = resolveKey(alias);

		if (chars == null) {
			return null;
		}

		byte[] bytes = StandardCharsets.UTF_8.encode(CharBuffer.wrap(chars)).array();

		Arrays.fill(chars, '\0');

		return bytes;
	}

	@Override
	public void storeKey(String alias, char[] value) throws KeyProviderException {
		if (!_enabled || _kmsClient == null) {
			throw new KeyProviderException("GCP KMS provider is not enabled or initialized");
		}

		try {
			CryptoKeyName keyName = CryptoKeyName.of(_projectId, _location, _keyRing, _cryptoKey);

			ByteBuffer byteBuffer = StandardCharsets.UTF_8.encode(CharBuffer.wrap(value));
			ByteString plaintext = ByteString.copyFrom(byteBuffer);

			EncryptResponse response = _kmsClient.encrypt(keyName, plaintext);

			byte[] ciphertext = response.getCiphertext().toByteArray();

			_encryptedStore.put(alias, Base64.getEncoder().encodeToString(ciphertext));
		}
		catch (Exception e) {
			throw new KeyProviderException("Failed to encrypt and store key: " + alias, e);
		}
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

	private void _closeKmsClient() {
		if (_kmsClient != null) {
			_kmsClient.close();
			_kmsClient = null;
		}
	}

	private String _projectId;
	private String _location;
	private String _keyRing;
	private String _cryptoKey;
	private boolean _enabled;
	private volatile boolean _available = false;
	private KeyManagementServiceClient _kmsClient;
	private final Map<String, String> _encryptedStore = new ConcurrentHashMap<>();

	private static final Log _log = LogFactoryUtil.getLog(GoogleKmsProvider.class);

}
