/**
 * Copyright (c) 2000-2013 Liferay, Inc. All rights reserved.
 *
 * This library is free software; you can redistribute it and/or modify it under
 * the terms of the GNU Lesser General Public License as published by the Free
 * Software Foundation; either version 2.1 of the License, or (at your option)
 * any later version.
 *
 * This library is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more
 * details.
 */

package com.liferay.portal.cache.key;

import com.liferay.portal.kernel.cache.key.CacheKeyGenerator;
import com.liferay.portal.kernel.util.StringBundler;

import java.security.SecureRandom;

/**
 * This cache key generator should be used in cases where an attacker is able to
 * modify input vector being encoded. This may result in cache poisoning using
 * collision attack.<br />
 *
 * Warning, this implementation must not be used for cluster wide caches,
 * every class instance generates different result for the same input.
 *
 * @author Tomas Polesovsky
 */
public class SecureHashCodeCacheKeyGenerator extends HashCodeCacheKeyGenerator {

	public SecureHashCodeCacheKeyGenerator() {
		_secret = new SecureRandom().nextLong();
	}

	@Override
	public CacheKeyGenerator clone() {
		SecureHashCodeCacheKeyGenerator result =
			new SecureHashCodeCacheKeyGenerator();

		result._secret = this._secret;

		return result;
	}

	@Override
	public Long getCacheKey(String key) {
		long hashCode = super.getCacheKey(key);

		return hashCode ^ _secret;
	}

	@Override
	public Long getCacheKey(String[] keys) {
		long hashCode = super.getCacheKey(keys);

		return hashCode ^ _secret;
	}

	@Override
	public Long getCacheKey(StringBundler sb) {
		long hashCode = super.getCacheKey(sb);

		return hashCode ^ _secret;
	}

	private long _secret;

}