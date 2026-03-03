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

@Component(immediate = true, service = KeyCacheManager.class)
@Designate(ocd = KeyCacheManager.Configuration.class)
public class KeyCacheManager {

	@ObjectClassDefinition(
		name = "Key Manager Cache Configuration",
		description = "Configures caching behavior for resolved key references"
	)
	public @interface Configuration {

		@AttributeDefinition(name = "Cache Enabled")
		boolean cacheEnabled() default true;

		@AttributeDefinition(name = "Cache TTL (seconds)")
		long cacheTtlSeconds() default KeyManagerConstants.DEFAULT_CACHE_TTL_SECONDS;

		@AttributeDefinition(name = "Max Cache Size")
		int maxCacheSize() default KeyManagerConstants.DEFAULT_CACHE_MAX_SIZE;

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

		CacheEntry entry = _cache.get(referenceKey);

		if (entry == null) {
			return null;
		}

		if (entry.isExpired(_ttlSeconds)) {
			_cache.remove(referenceKey);
			entry.clear();

			return null;
		}

		return entry.getValue();
	}

	public void put(String referenceKey, char[] value) {
		if (!_enabled) {
			return;
		}

		if (_cache.size() >= _maxSize) {
			_evictExpired();
		}

		char[] copy = Arrays.copyOf(value, value.length);

		CacheEntry previous = _cache.put(referenceKey, new CacheEntry(copy));

		if (previous != null) {
			previous.clear();
		}
	}

	public void invalidate(String referenceKey) {
		CacheEntry entry = _cache.remove(referenceKey);

		if (entry != null) {
			entry.clear();
		}
	}

	public void invalidateAll() {
		_cache.values().forEach(CacheEntry::clear);
		_cache.clear();
	}

	private void _evictExpired() {
		_cache.entrySet().removeIf(entry -> {
			if (entry.getValue().isExpired(_ttlSeconds)) {
				entry.getValue().clear();

				return true;
			}

			return false;
		});
	}

	private volatile boolean _enabled = true;
	private volatile long _ttlSeconds = KeyManagerConstants.DEFAULT_CACHE_TTL_SECONDS;
	private volatile int _maxSize = KeyManagerConstants.DEFAULT_CACHE_MAX_SIZE;
	private final Map<String, CacheEntry> _cache = new ConcurrentHashMap<>();

	private static final Log _log = LogFactoryUtil.getLog(KeyCacheManager.class);

	private static class CacheEntry {

		private final char[] _value;
		private final Instant _createdAt;

		CacheEntry(char[] value) {
			_value = value;
			_createdAt = Instant.now();
		}

		char[] getValue() {
			return Arrays.copyOf(_value, _value.length);
		}

		boolean isExpired(long ttlSeconds) {
			return Instant.now().isAfter(_createdAt.plusSeconds(ttlSeconds));
		}

		void clear() {
			Arrays.fill(_value, '\0');
		}

	}

}
