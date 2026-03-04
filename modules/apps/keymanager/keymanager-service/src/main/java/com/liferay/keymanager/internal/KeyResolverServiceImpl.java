/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

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
import com.liferay.portal.kernel.util.Validator;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Liferay
 */
@Component(immediate = true, service = KeyResolverService.class)
public class KeyResolverServiceImpl implements KeyResolverService {

	@Override
	public String createReference(String providerId, String alias) {
		return KeyManagerConstants.KEY_REFERENCE_PREFIX + providerId +
			KeyManagerConstants.PROVIDER_ALIAS_SEPARATOR + alias +
			KeyManagerConstants.KEY_REFERENCE_SUFFIX;
	}

	@Override
	public List<KeyProvider> getAvailableProviders() {
		return _keyProviderRegistry.getAvailableProviders();
	}

	@Override
	public void invalidateAllCaches() {
		_keyCacheManager.invalidateAll();
	}

	@Override
	public void invalidateCache(String referenceString) {
		_keyCacheManager.invalidate(referenceString);
	}

	@Override
	public boolean isKeyReference(String value) {
		return _keyReferenceParser.isKeyReference(value);
	}

	@Override
	public KeyReference parseReference(String referenceString)
		throws KeyResolutionException {

		return _keyReferenceParser.parse(referenceString);
	}

	@Override
	public String resolve(String value) throws KeyResolutionException {
		if (Validator.isNull(value) ||
			!_keyReferenceParser.isKeyReference(value)) {

			return value;
		}

		return _keyReferenceParser.replaceAll(
			value,
			ref -> {
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
	public Map<String, String> resolveAll(Map<String, String> properties)
		throws KeyResolutionException {

		Map<String, String> resolved = new HashMap<>();

		for (Map.Entry<String, String> entry : properties.entrySet()) {
			resolved.put(entry.getKey(), resolve(entry.getValue()));
		}

		return resolved;
	}

	@Override
	public char[] resolveSecure(KeyReference reference)
		throws KeyResolutionException {

		return _resolveFromProvider(reference);
	}

	@Override
	public String storeAndReference(
			String providerId, String alias, char[] value)
		throws KeyResolutionException {

		KeyProvider keyProvider = _getProvider(providerId);

		try {
			keyProvider.storeKey(alias, value);

			KeyReference keyReference = new KeyReference(
				providerId, alias, createReference(providerId, alias));

			_keyAccessAuditService.auditKeyAccess(
				KeyManagerConstants.AUDIT_EVENT_STORE, keyReference, true);

			return createReference(providerId, alias);
		}
		catch (KeyProviderException kpe) {
			throw new KeyResolutionException(
				"Failed to store key with provider " + providerId, kpe);
		}
	}

	private KeyProvider _getProvider(String providerId)
		throws KeyResolutionException {

		return _keyProviderRegistry.getProvider(providerId).orElseThrow(
			() -> new KeyResolutionException(
				"No key provider found with id: " + providerId));
	}

	private char[] _resolveFromProvider(KeyReference keyReference)
		throws KeyResolutionException {

		String cacheKey = keyReference.getRawReference();

		char[] cached = _keyCacheManager.get(cacheKey);

		if (cached != null) {
			_keyAccessAuditService.auditKeyAccess(
				KeyManagerConstants.AUDIT_EVENT_RESOLVE, keyReference, true);

			return cached;
		}

		KeyProvider keyProvider = _getProvider(keyReference.getProvider());

		try {
			char[] resolved = keyProvider.resolveKey(keyReference.getAlias());

			if (resolved == null) {
				throw new KeyResolutionException(
					"Key not found: " + keyReference.getAlias() +
						" in provider " + keyReference.getProvider());
			}

			_keyCacheManager.put(cacheKey, resolved);

			_keyAccessAuditService.auditKeyAccess(
				KeyManagerConstants.AUDIT_EVENT_RESOLVE, keyReference, true);

			return resolved;
		}
		catch (KeyProviderException kpe) {
			_keyAccessAuditService.auditKeyAccess(
				KeyManagerConstants.AUDIT_EVENT_RESOLVE, keyReference, false);

			throw new KeyResolutionException(
				"Failed to resolve key: " + keyReference.getRawReference(),
				kpe);
		}
	}

	private static final Log _log = LogFactoryUtil.getLog(
		KeyResolverServiceImpl.class);

	@Reference
	private KeyAccessAuditService _keyAccessAuditService;

	@Reference
	private KeyCacheManager _keyCacheManager;

	@Reference
	private KeyProviderRegistry _keyProviderRegistry;

	@Reference
	private KeyReferenceParser _keyReferenceParser;

}
