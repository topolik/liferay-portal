/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.keymanager.internal.cache;

import com.liferay.keymanager.SecureSecret;
import com.liferay.portal.test.rule.LiferayUnitTestRule;

import org.junit.Assert;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;

/**
 * @author Tomas Polesovsky
 */
public class KeyCacheManagerTest {

	@ClassRule
	@Rule
	public static final LiferayUnitTestRule liferayUnitTestRule =
		LiferayUnitTestRule.INSTANCE;

	@Test
	public void testCachePutAndGet() {
		KeyCacheManager cacheManager = new KeyCacheManager();

		SecureSecret secret = new SecureSecret("value".toCharArray());

		cacheManager.put("key", secret, 60);

		SecureSecret cached = cacheManager.get("key");

		Assert.assertNotNull(cached);
		Assert.assertArrayEquals("value".toCharArray(), cached.getChars());
		Assert.assertNotSame(secret, cached);
	}

	@Test
	public void testCacheExpiry() throws InterruptedException {
		KeyCacheManager cacheManager = new KeyCacheManager();

		SecureSecret secret = new SecureSecret("value".toCharArray());

		// Put with 1 second TTL
		cacheManager.put("key", secret, 1);

		Assert.assertNotNull(cacheManager.get("key"));

		// Wait for expiry
		Thread.sleep(1100);

		Assert.assertNull(cacheManager.get("key"));
	}

	@Test
	public void testInvalidateAll() {
		KeyCacheManager cacheManager = new KeyCacheManager();

		cacheManager.put(
			"key1", new SecureSecret("val1".toCharArray()), 60);
		cacheManager.put(
			"key2", new SecureSecret("val2".toCharArray()), 60);

		cacheManager.invalidateAll();

		Assert.assertNull(cacheManager.get("key1"));
		Assert.assertNull(cacheManager.get("key2"));
	}

}
