package com.liferay.keymanager.provider.vault;

import com.bettercloud.vault.Vault;
import com.bettercloud.vault.VaultConfig;
import com.bettercloud.vault.response.LogicalResponse;

import com.liferay.keymanager.KeyMetadata;
import com.liferay.keymanager.KeyProvider;
import com.liferay.keymanager.constants.KeyManagerConstants;
import com.liferay.keymanager.exception.KeyProviderException;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Modified;
import org.osgi.service.metatype.annotations.AttributeDefinition;
import org.osgi.service.metatype.annotations.Designate;
import org.osgi.service.metatype.annotations.ObjectClassDefinition;

/**
 * @author Tomas Polesovsky
 */
@Component(
	immediate = true,
	service = KeyProvider.class,
	property = "provider.id=" + KeyManagerConstants.PROVIDER_VAULT
)
@Designate(ocd = VaultKeyProvider.Configuration.class)
public class VaultKeyProvider implements KeyProvider {

	@ObjectClassDefinition(
		name = "HashiCorp Vault Provider Configuration",
		description = "Configuration for HashiCorp Vault key provider"
	)
	public @interface Configuration {

		@AttributeDefinition(name = "Vault URL")
		String vaultAddress() default "http://127.0.0.1:8200";

		@AttributeDefinition(name = "Vault Token")
		String vaultToken() default "";

		@AttributeDefinition(name = "KV Engine Path")
		String enginePath() default "secret";

		@AttributeDefinition(name = "KV Engine Version")
		int engineVersion() default 2;

		@AttributeDefinition(name = "Enabled")
		boolean enabled() default false;

	}

	@Activate
	@Modified
	protected void activate(Configuration configuration) {
		_address = configuration.vaultAddress();
		_token = configuration.vaultToken();
		_enginePath = configuration.enginePath();
		_engineVersion = configuration.engineVersion();
		_enabled = configuration.enabled();

		if (_enabled && !_address.isEmpty() && !_token.isEmpty()) {
			try {
				VaultConfig vaultConfig = new VaultConfig()
					.address(_address)
					.token(_token)
					.engineVersion(_engineVersion)
					.build();

				_vault = new Vault(vaultConfig);
				_available = true;

				if (_log.isInfoEnabled()) {
					_log.info("Vault provider initialized: " + _address);
				}
			}
			catch (Exception e) {
				_available = false;
				_log.error("Failed to initialize Vault provider", e);
			}
		}
		else {
			_available = false;
			_vault = null;
		}
	}

	@Override
	public String getProviderId() {
		return KeyManagerConstants.PROVIDER_VAULT;
	}

	@Override
	public String getDisplayName() {
		return "HashiCorp Vault";
	}

	@Override
	public char[] resolveKey(String alias) throws KeyProviderException {
		if (!_available) {
			throw new KeyProviderException("Vault provider is not available");
		}

		try {
			LogicalResponse response = _vault.logical().read(_enginePath + "/" + alias);

			if (response.getRestResponse().getStatus() == 404) {
				return null;
			}

			String value = response.getData().get("value");

			if (value == null) {
				// Try common keys if 'value' is not present
				value = response.getData().get("password");
			}

			return (value != null) ? value.toCharArray() : null;
		}
		catch (Exception e) {
			throw new KeyProviderException("Failed to read from Vault: " + alias, e);
		}
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

		return bytes;
	}

	@Override
	public void storeKey(String alias, char[] value) throws KeyProviderException {
		if (!_available) {
			throw new KeyProviderException("Vault provider is not available");
		}

		try {
			Map<String, Object> data = new HashMap<>();
			data.put("value", new String(value));

			_vault.logical().write(_enginePath + "/" + alias, data);
		}
		catch (Exception e) {
			throw new KeyProviderException("Failed to write to Vault: " + alias, e);
		}
	}

	@Override
	public void deleteKey(String alias) throws KeyProviderException {
		if (!_available) {
			throw new KeyProviderException("Vault provider is not available");
		}

		try {
			_vault.logical().delete(_enginePath + "/" + alias);
		}
		catch (Exception e) {
			throw new KeyProviderException("Failed to delete from Vault: " + alias, e);
		}
	}

	@Override
	public boolean containsKey(String alias) throws KeyProviderException {
		return resolveKey(alias) != null;
	}

	@Override
	public List<String> listAliases() throws KeyProviderException {
		if (!_available) {
			return List.of();
		}

		try {
			return _vault.logical().list(_enginePath).getListData();
		}
		catch (Exception e) {
			_log.error("Failed to list Vault aliases", e);
			return List.of();
		}
	}

	@Override
	public KeyMetadata getKeyMetadata(String alias) throws KeyProviderException {
		if (!containsKey(alias)) {
			return null;
		}

		return new KeyMetadata.Builder()
			.alias(alias)
			.provider(getProviderId())
			.keyType("VAULT_SECRET")
			.rotatable(true)
			.build();
	}

	@Override
	public int getPriority() {
		return 20;
	}

	@Override
	public boolean isAvailable() {
		return _available;
	}

	private String _address;
	private String _token;
	private String _enginePath;
	private int _engineVersion;
	private boolean _enabled;
	private volatile boolean _available = false;
	private Vault _vault;

	private static final Log _log = LogFactoryUtil.getLog(VaultKeyProvider.class);

}
