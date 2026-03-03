package com.liferay.keymanager.provider.keystore;

import com.liferay.keymanager.KeyMetadata;
import com.liferay.keymanager.KeyProvider;
import com.liferay.keymanager.constants.KeyManagerConstants;
import com.liferay.keymanager.exception.KeyProviderException;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Enumeration;
import java.util.List;

import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Modified;
import org.osgi.service.metatype.annotations.AttributeDefinition;
import org.osgi.service.metatype.annotations.Designate;
import org.osgi.service.metatype.annotations.ObjectClassDefinition;

@Component(
	immediate = true,
	service = KeyProvider.class,
	property = "provider.id=" + KeyManagerConstants.PROVIDER_KEYSTORE
)
@Designate(ocd = JavaKeyStoreProvider.Configuration.class)
public class JavaKeyStoreProvider implements KeyProvider {

	@ObjectClassDefinition(
		name = "Java KeyStore Provider Configuration",
		description = "Configuration for the Java KeyStore key provider"
	)
	public @interface Configuration {

		@AttributeDefinition(name = "KeyStore Path")
		String keystorePath() default "${liferay.home}/data/keymanager.jceks";

		@AttributeDefinition(name = "KeyStore Type")
		String keystoreType() default "JCEKS";

		@AttributeDefinition(name = "KeyStore Password")
		String keystorePassword() default "";

		@AttributeDefinition(name = "Auto-create KeyStore")
		boolean autoCreate() default true;

	}

	@Activate
	@Modified
	protected void activate(Configuration configuration) {
		_keystorePath = _resolveEnvVars(configuration.keystorePath());
		_keystoreType = configuration.keystoreType();
		_keystorePassword = _resolveEnvVars(configuration.keystorePassword()).toCharArray();
		_autoCreate = configuration.autoCreate();

		try {
			_loadOrCreateKeyStore();

			_available = true;

			if (_log.isInfoEnabled()) {
				_log.info("Java KeyStore provider initialized: " + _keystorePath);
			}
		}
		catch (Exception e) {
			_available = false;

			_log.error("Failed to initialize Java KeyStore provider", e);
		}
	}

	@Override
	public String getProviderId() {
		return KeyManagerConstants.PROVIDER_KEYSTORE;
	}

	@Override
	public String getDisplayName() {
		return "Java KeyStore";
	}

	@Override
	public char[] resolveKey(String alias) throws KeyProviderException {
		try {
			KeyStore.SecretKeyEntry entry = _getSecretKeyEntry(alias);

			if (entry == null) {
				return null;
			}

			SecretKey secretKey = entry.getSecretKey();

			byte[] encoded = secretKey.getEncoded();

			char[] result = new char[encoded.length];

			for (int i = 0; i < encoded.length; i++) {
				result[i] = (char)(encoded[i] & 0xFF);
			}

			Arrays.fill(encoded, (byte)0);

			return result;
		}
		catch (Exception e) {
			throw new KeyProviderException("Failed to resolve key '" + alias + "' from KeyStore", e);
		}
	}

	@Override
	public byte[] resolveKeyBytes(String alias) throws KeyProviderException {
		try {
			KeyStore.SecretKeyEntry entry = _getSecretKeyEntry(alias);

			if (entry == null) {
				return null;
			}

			return entry.getSecretKey().getEncoded();
		}
		catch (Exception e) {
			throw new KeyProviderException("Failed to resolve key bytes for '" + alias + "'", e);
		}
	}

	@Override
	public synchronized void storeKey(String alias, char[] value) throws KeyProviderException {
		try {
			byte[] keyBytes = new byte[value.length];

			for (int i = 0; i < value.length; i++) {
				keyBytes[i] = (byte)value[i];
			}

			SecretKey secretKey = new SecretKeySpec(keyBytes, "AES");

			Arrays.fill(keyBytes, (byte)0);

			KeyStore.SecretKeyEntry entry = new KeyStore.SecretKeyEntry(secretKey);

			KeyStore.ProtectionParameter protection = new KeyStore.PasswordProtection(_keystorePassword);

			_keyStore.setEntry(alias, entry, protection);

			_saveKeyStore();
		}
		catch (Exception e) {
			throw new KeyProviderException("Failed to store key '" + alias + "' in KeyStore", e);
		}
	}

	@Override
	public synchronized void deleteKey(String alias) throws KeyProviderException {
		try {
			_keyStore.deleteEntry(alias);

			_saveKeyStore();
		}
		catch (KeyStoreException e) {
			throw new KeyProviderException("Failed to delete key '" + alias + "'", e);
		}
	}

	@Override
	public boolean containsKey(String alias) throws KeyProviderException {
		try {
			return _keyStore.containsAlias(alias);
		}
		catch (KeyStoreException e) {
			throw new KeyProviderException("Failed to check alias '" + alias + "'", e);
		}
	}

	@Override
	public List<String> listAliases() throws KeyProviderException {
		try {
			Enumeration<String> aliases = _keyStore.aliases();
			List<String> result = new ArrayList<>();

			while (aliases.hasMoreElements()) {
				result.add(aliases.nextElement());
			}

			return result;
		}
		catch (KeyStoreException e) {
			throw new KeyProviderException("Failed to list aliases", e);
		}
	}

	@Override
	public KeyMetadata getKeyMetadata(String alias) throws KeyProviderException {
		try {
			if (!_keyStore.containsAlias(alias)) {
				return null;
			}

			return new KeyMetadata.Builder()
				.alias(alias)
				.provider(getProviderId())
				.keyType("SECRET")
				.version(1)
				.rotatable(true)
				.build();
		}
		catch (KeyStoreException e) {
			throw new KeyProviderException("Failed to get metadata for '" + alias + "'", e);
		}
	}

	@Override
	public int getPriority() {
		return 50;
	}

	@Override
	public boolean isAvailable() {
		return _available;
	}

	private KeyStore.SecretKeyEntry _getSecretKeyEntry(String alias) throws Exception {
		if (!_keyStore.containsAlias(alias)) {
			return null;
		}

		KeyStore.ProtectionParameter protection = new KeyStore.PasswordProtection(_keystorePassword);

		KeyStore.Entry entry = _keyStore.getEntry(alias, protection);

		if (entry instanceof KeyStore.SecretKeyEntry) {
			return (KeyStore.SecretKeyEntry)entry;
		}

		throw new KeyProviderException("Entry '" + alias + "' is not a SecretKeyEntry");
	}

	private void _loadOrCreateKeyStore() throws Exception {
		_keyStore = KeyStore.getInstance(_keystoreType);

		Path path = Paths.get(_keystorePath);

		if (Files.exists(path)) {
			try (FileInputStream fis = new FileInputStream(path.toFile())) {
				_keyStore.load(fis, _keystorePassword);
			}
		}
		else if (_autoCreate) {
			_keyStore.load(null, _keystorePassword);

			Files.createDirectories(path.getParent());

			_saveKeyStore();
		}
		else {
			throw new IOException("KeyStore file not found: " + _keystorePath);
		}
	}

	private void _saveKeyStore() throws Exception {
		Path path = Paths.get(_keystorePath);

		try (FileOutputStream fos = new FileOutputStream(path.toFile())) {
			_keyStore.store(fos, _keystorePassword);
		}
	}

	private String _resolveEnvVars(String value) {
		if (value == null) {
			return "";
		}

		if (value.startsWith("${env:") && value.endsWith("}")) {
			String envVar = value.substring(6, value.length() - 1);
			String envValue = System.getenv(envVar);

			return envValue != null ? envValue : value;
		}

		if (value.contains("${liferay.home}")) {
			String liferayHome = System.getProperty("liferay.home",
				System.getenv().getOrDefault("LIFERAY_HOME", "/opt/liferay"));

			return value.replace("${liferay.home}", liferayHome);
		}

		return value;
	}

	private KeyStore _keyStore;
	private String _keystorePath;
	private String _keystoreType;
	private char[] _keystorePassword;
	private boolean _autoCreate;
	private volatile boolean _available = false;

	private static final Log _log = LogFactoryUtil.getLog(JavaKeyStoreProvider.class);

}
