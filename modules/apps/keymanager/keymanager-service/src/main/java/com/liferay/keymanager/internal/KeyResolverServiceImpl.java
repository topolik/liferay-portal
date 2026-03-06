/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.keymanager.internal;

import com.liferay.keymanager.KeyResolutionException;
import com.liferay.keymanager.KeyResolverService;
import com.liferay.keymanager.SecureSecret;
import com.liferay.keymanager.internal.audit.KeyAuditService;
import com.liferay.keymanager.internal.cache.KeyCacheManager;
import com.liferay.keymanager.spi.KeyProvider;

import java.util.Collections;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.osgi.framework.BundleContext;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Deactivate;
import org.osgi.service.component.annotations.Reference;
import org.osgi.util.tracker.ServiceTracker;

/**
 * @author Tomas Polesovsky
 */
@Component(service = KeyResolverService.class)
public class KeyResolverServiceImpl implements KeyResolverService {

	@Override
	public boolean isKeyReference(String value) {
		if (value == null) {
			return false;
		}

		return value.contains("${keyref:");
	}

	@Override
	public String resolve(String referenceString)
		throws KeyResolutionException {

		if (!isKeyReference(referenceString)) {
			return referenceString;
		}

		Matcher matcher = _keyRefPattern.matcher(referenceString);

		StringBuilder sb = new StringBuilder();

		while (matcher.find()) {
			try (SecureSecret secret = resolveSecure(matcher.group(0))) {
				char[] chars = secret.getChars();

				matcher.appendReplacement(
					sb, Matcher.quoteReplacement(new String(chars)));
			}
			catch (KeyResolutionException keyResolutionException) {
				throw keyResolutionException;
			}
			catch (Exception exception) {
				throw new KeyResolutionException(exception);
			}
		}

		matcher.appendTail(sb);

		return sb.toString();
	}

	@Override
	public SecureSecret resolveSecure(String referenceString)
		throws KeyResolutionException {

		Matcher matcher = _keyRefPattern.matcher(referenceString);

		if (!matcher.matches()) {
			throw new KeyResolutionException(
				"Invalid key reference: " + referenceString);
		}

		String providerId = matcher.group(1);

		String alias = matcher.group(2);

		String version = matcher.group(4);

		String aliasWithVersion =
			(version != null) ? (alias + ":" + version) : alias;

		// 1. Check Cache

		SecureSecret cached = _keyCacheManager.get(referenceString);

		if (cached != null) {
			_keyAuditService.auditAccess(
				providerId, aliasWithVersion, true, "Cache hit");

			return cached;
		}

		// 2. Resolve from Provider

		KeyProvider provider = _getProvider(providerId);

		if ((provider == null) || !provider.isAvailable()) {
			_keyAuditService.auditAccess(
				providerId, aliasWithVersion, false,
				"Provider not found or unavailable");

			throw new KeyResolutionException(
				"KeyProvider not found or unavailable: " + providerId);
		}

		try {
			SecureSecret secret = provider.resolveKey(
				aliasWithVersion, Collections.emptyMap());

			// 3. Update Cache (Default 300s TTL)

			_keyCacheManager.put(referenceString, secret, 300);

			_keyAuditService.auditAccess(
				providerId, aliasWithVersion, true, "Provider resolve");

			return secret;
		}
		catch (Exception exception) {
			_keyAuditService.auditAccess(
				providerId, aliasWithVersion, false, exception.getMessage());

			throw new KeyResolutionException(exception);
		}
	}

	@Activate
	protected void activate(BundleContext bundleContext) {
		_serviceTracker = new ServiceTracker<>(
			bundleContext, KeyProvider.class, null);

		_serviceTracker.open();
	}

	@Deactivate
	protected void deactivate() {
		_serviceTracker.close();
	}

	private KeyProvider _getProvider(String providerId) {
		Object[] services = _serviceTracker.getServices();

		if (services == null) {
			return null;
		}

		for (Object service : services) {
			KeyProvider keyProvider = (KeyProvider)service;

			if (providerId.equals(keyProvider.getProviderId())) {
				return keyProvider;
			}
		}

		return null;
	}

	private static final Pattern _keyRefPattern = Pattern.compile(
		"\\$\\{keyref:([a-zA-Z0-9\\-_]+)/([^:}]+)(:([^}]+))?\\}");

	@Reference
	private KeyAuditService _keyAuditService;

	@Reference
	private KeyCacheManager _keyCacheManager;

	private ServiceTracker<KeyProvider, KeyProvider> _serviceTracker;

}