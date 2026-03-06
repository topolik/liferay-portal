/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.keymanager.internal.cache;

import com.liferay.keymanager.SecureSecret;

import java.time.Instant;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Deactivate;

/**
 * @author Tomas Polesovsky
 */
@Component(service = {})
public class KeyCacheManager {

	public SecureSecret get(String cacheKey) {
		CacheEntry entry = _cache.get(cacheKey);

		if (entry == null) {
			return null;
		}

		if (entry.isExpired()) {
			_cache.remove(cacheKey);

			entry.close();

			return null;
		}

		// Return a copy so the caller's close() doesn't clear the cache entry

		return new SecureSecret(
			entry.getSecret(
			).getChars());
	}

	public void invalidateAll() {
		for (CacheEntry entry : _cache.values()) {
			entry.close();
		}

		_cache.clear();
	}

	public void put(String cacheKey, SecureSecret secret, long ttlSeconds) {
		_evictExpired();

		_cache.put(
			cacheKey,
			new CacheEntry(
				new SecureSecret(secret.getChars()),
				Instant.now(
				).plusSeconds(
					ttlSeconds
				)));
	}

	@Deactivate
	protected void deactivate() {
		invalidateAll();
	}

	private void _evictExpired() {
		_cache.entrySet(
		).removeIf(
			entry -> {
				if (entry.getValue(
					).isExpired()) {

					entry.getValue(
					).close();

					return true;
				}

				return false;
			}
		);
	}

	private final Map<String, CacheEntry> _cache = new ConcurrentHashMap<>();

	private static class CacheEntry implements AutoCloseable {

		public CacheEntry(SecureSecret secret, Instant expiresAt) {
			_secret = secret;
			_expiresAt = expiresAt;
		}

		@Override
		public void close() {
			_secret.close();
		}

		public SecureSecret getSecret() {
			return _secret;
		}

		public boolean isExpired() {
			return Instant.now(
			).isAfter(
				_expiresAt
			);
		}

		private final Instant _expiresAt;
		private final SecureSecret _secret;

	}

}