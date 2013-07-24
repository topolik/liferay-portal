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

import java.io.Serializable;

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
public class SecureCacheKeyGenerator implements CacheKeyGenerator {

	public SecureCacheKeyGenerator(CacheKeyGenerator cacheKeyGenerator) {
		_cacheKeyGenerator = cacheKeyGenerator;
		_secret = new SecureRandom().nextLong();
	}

	@Override
	public CacheKeyGenerator append(String key) {
		return _cacheKeyGenerator.append(key);
	}

	@Override
	public CacheKeyGenerator append(String[] keys) {
		return _cacheKeyGenerator.append(keys);
	}

	@Override
	public CacheKeyGenerator append(StringBundler sb) {
		return _cacheKeyGenerator.append(sb);
	}

	@Override
	public CacheKeyGenerator clone() {
		SecureCacheKeyGenerator result = new SecureCacheKeyGenerator(
			_cacheKeyGenerator);

		result._secret = this._secret;

		return result;
	}

	@Override
	public Serializable finish() {
		return _cacheKeyGenerator.finish();
	}

	@Override
	public Serializable getCacheKey(String key) {
		return _cacheKeyGenerator.getCacheKey(encrypt(key));
	}

	@Override
	public Serializable getCacheKey(String[] keys) {
		return _cacheKeyGenerator.getCacheKey(encrypt(keys));
	}

	@Override
	public Serializable getCacheKey(StringBundler sb) {
		return _cacheKeyGenerator.getCacheKey(encrypt(sb));
	}

	@Override
	public boolean isCallingGetCacheKeyThreadSafe() {
		return _cacheKeyGenerator.isCallingGetCacheKeyThreadSafe();
	}

	protected String encrypt(String message) {
		if (message == null) {
			return null;
		}

		int len = message.length();
		char[] cipher = new char[len];

		for (int i = 0; i < len; i++) {
			cipher[i] = (char) (message.charAt(i) ^ _secret);
		}

		return new String(cipher);
	}

	protected String[] encrypt(String[] message) {
		String[] result = new String[message.length];

		for (int i = 0; i < message.length; i++) {
			result[i] = encrypt(message[i]);
		}

		return result;
	}

	protected StringBundler encrypt(StringBundler sb) {
		int length = sb.index();
		StringBundler result = new StringBundler(length);

		for (int i = 0; i < sb.index(); i++) {
			String message = sb.stringAt(i);
			result.append(encrypt(message));
		}

		return result;
	}

	private CacheKeyGenerator _cacheKeyGenerator;
	private long _secret;

}