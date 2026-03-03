package com.liferay.keymanager.internal;

import com.liferay.keymanager.KeyProvider;
import com.liferay.keymanager.KeyProviderRegistry;
import com.liferay.keymanager.KeyReference;
import com.liferay.keymanager.KeyResolverService;
import com.liferay.keymanager.constants.KeyManagerConstants;
import com.liferay.keymanager.exception.KeyProviderException;
import com.liferay.keymanager.exception.KeyResolutionException;
import com.liferay.keymanager.internal.audit.KeyAccessAuditService;
import com.liferay.keymanager.internal.cache.KeyCacheManager;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

@Component(immediate = true, service = KeyResolverService.class)
public class KeyResolverServiceImpl implements KeyResolverService {

	@Override
	public boolean isKeyReference(String value) {
		return _parser.isKeyReference(value);
	}

	@Override
	public String resolve(String value) throws KeyResolutionException {
		if (value == null || !_parser.isKeyReference(value)) {
			return value;
		}

		return _parser.replaceAll(value, ref -> {
			char[] resolved = _resolveFromProvider(ref);

			try {
				return new String(resolved);
			}
			finally {
				Arrays.fill(resolved, '\0');
			}
		});
	}

	@Override
	public char[] resolveSecure(KeyReference reference) throws KeyResolutionException {
		return _resolveFromProvider(reference);
	}

	@Override
	public Map<String, String> resolveAll(Map<String, String> properties) throws KeyResolutionException {
		Map<String, String> resolved = new HashMap<>();

		for (Map.Entry<String, String> entry : properties.entrySet()) {
			resolved.put(entry.getKey(), resolve(entry.getValue()));
		}

		return resolved;
	}

	@Override
	public KeyReference parseReference(String referenceString) throws KeyResolutionException {
		return _parser.parse(referenceString);
	}

	@Override
	public String createReference(String providerId, String alias) {
		return KeyManagerConstants.KEY_REFERENCE_PREFIX +
			providerId + KeyManagerConstants.PROVIDER_ALIAS_SEPARATOR +
			alias + KeyManagerConstants.KEY_REFERENCE_SUFFIX;
	}

	@Override
	public String storeAndReference(String providerId, String alias, char[] value) throws KeyResolutionException {
		KeyProvider provider = _getProvider(providerId);

		try {
			provider.storeKey(alias, value);

			KeyReference ref = new KeyReference(providerId, alias, createReference(providerId, alias));

			_auditService.auditKeyAccess(KeyManagerConstants.AUDIT_EVENT_STORE, ref, true);

			return createReference(providerId, alias);
		}
		catch (KeyProviderException e) {
			throw new KeyResolutionException("Failed to store key with provider " + providerId, e);
		}
	}

	@Override
	public List<KeyProvider> getAvailableProviders() {
		return _registry.getAvailableProviders();
	}

	@Override
	public void invalidateCache(String referenceString) {
		_cacheManager.invalidate(referenceString);
	}

	@Override
	public void invalidateAllCaches() {
		_cacheManager.invalidateAll();
	}

	private char[] _resolveFromProvider(KeyReference reference) throws KeyResolutionException {
		String cacheKey = reference.getRawReference();

		char[] cached = _cacheManager.get(cacheKey);

		if (cached != null) {
			_auditService.auditKeyAccess(KeyManagerConstants.AUDIT_EVENT_RESOLVE, reference, true);

			return cached;
		}

		KeyProvider provider = _getProvider(reference.getProvider());

		try {
			char[] resolved = provider.resolveKey(reference.getAlias());

			if (resolved == null) {
				throw new KeyResolutionException(
					"Key not found: " + reference.getAlias() + " in provider " + reference.getProvider());
			}

			_cacheManager.put(cacheKey, resolved);

			_auditService.auditKeyAccess(KeyManagerConstants.AUDIT_EVENT_RESOLVE, reference, true);

			return resolved;
		}
		catch (KeyProviderException e) {
			_auditService.auditKeyAccess(KeyManagerConstants.AUDIT_EVENT_RESOLVE, reference, false);

			throw new KeyResolutionException("Failed to resolve key: " + reference.getRawReference(), e);
		}
	}

	private KeyProvider _getProvider(String providerId) throws KeyResolutionException {
		return _registry.getProvider(providerId).orElseThrow(
			() -> new KeyResolutionException("No key provider found with id: " + providerId));
	}

	@Reference
	private KeyProviderRegistry _registry;

	@Reference
	private KeyReferenceParser _parser;

	@Reference
	private KeyCacheManager _cacheManager;

	@Reference
	private KeyAccessAuditService _auditService;

	private static final Log _log = LogFactoryUtil.getLog(KeyResolverServiceImpl.class);

}
