/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.keymanager.internal.cache;

import com.liferay.keymanager.constants.KeyManagerConstants;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;

import java.time.Instant;

import java.util.Arrays;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Deactivate;
import org.osgi.service.component.annotations.Modified;
import org.osgi.service.metatype.annotations.AttributeDefinition;
import org.osgi.service.metatype.annotations.Designate;
import org.osgi.service.metatype.annotations.ObjectClassDefinition;

/**
 * @author Tomas Polesovsky
 */
@Component(immediate = true, service = KeyCacheManager.class)
@Designate(ocd = KeyCacheManager.Configuration.class)
public class KeyCacheManager {

	@ObjectClassDefinition(
		description = "Configures caching behavior for resolved key references",
		name = "Key Manager Cache Configuration"
	)
	public @interface Configuration {

		@AttributeDefinition(name = "Cache Enabled")
		public boolean cacheEnabled() default true;

		@AttributeDefinition(name = "Cache TTL (seconds)")
		public long cacheTtlSeconds() default KeyManagerConstants.DEFAULT_CACHE_TTL_SECONDS;

		@AttributeDefinition(name = "Max Cache Size")
		public int maxCacheSize() default KeyManagerConstants.DEFAULT_CACHE_MAX_SIZE;

	}

	@Activate
	@Modified
	protected void activate(Configuration configuration) {
		_enabled = configuration.cacheEnabled();
		_ttlSeconds = configuration.cacheTtlSeconds();
		_maxSize = configuration.maxCacheSize();
	}

	@Deactivate
	protected void deactivate() {
		invalidateAll();
	}

	public char[] get(String referenceKey) {
		if (!_enabled) {
			return null;
		}

		CacheEntry cacheEntry = _cache.get(referenceKey);

		if (cacheEntry == null) {
			return null;
		}

		if (cacheEntry.isExpired(_ttlSeconds)) {
			_cache.remove(referenceKey);

			cacheEntry.clear();

			return null;
		}

		return cacheEntry.getValue();
	}

	public void invalidate(String referenceKey) {
		CacheEntry cacheEntry = _cache.remove(referenceKey);

		if (cacheEntry != null) {
			cacheEntry.clear();
		}
	}

	public void invalidateAll() {
		_cache.values(
		).forEach(
			CacheEntry::clear
		);

		_cache.clear();
	}

	public void put(String referenceKey, char[] value) {
		if (!_enabled) {
			return;
		}

		if (_cache.size() >= _maxSize) {
			_evictExpired();
		}

		char[] copy = Arrays.copyOf(value, value.length);

		CacheEntry previousCacheEntry = _cache.put(
			referenceKey, new CacheEntry(copy));

		if (previousCacheEntry != null) {
			previousCacheEntry.clear();
		}
	}

	private void _evictExpired() {
		_cache.entrySet(
		).removeIf(
			entry -> {
				CacheEntry cacheEntry = entry.getValue();

				if (cacheEntry.isExpired(_ttlSeconds)) {
					cacheEntry.clear();

					return true;
				}

				return false;
			}
		);
	}

	private static final Log _log = LogFactoryUtil.getLog(
		KeyCacheManager.class);

	private final Map<String, CacheEntry> _cache = new ConcurrentHashMap<>();
	private volatile boolean _enabled = true;
	private volatile int _maxSize = KeyManagerConstants.DEFAULT_CACHE_MAX_SIZE;
	private volatile long _ttlSeconds =
		KeyManagerConstants.DEFAULT_CACHE_TTL_SECONDS;

	private static class CacheEntry {

		public CacheEntry(char[] value) {
			_value = value;
			_createdAt = Instant.now();
		}

		public void clear() {
			Arrays.fill(_value, '\0');
		}

		public char[] getValue() {
			return Arrays.copyOf(_value, _value.length);
		}

		public boolean isExpired(long ttlSeconds) {
			return Instant.now().isAfter(_createdAt.plusSeconds(ttlSeconds));
		}

		private final Instant _createdAt;
		private final char[] _value;

	}

}
