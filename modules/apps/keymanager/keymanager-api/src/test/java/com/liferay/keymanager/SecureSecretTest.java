/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.keymanager;

import com.liferay.portal.test.rule.LiferayUnitTestRule;

import org.junit.Assert;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;

/**
 * @author Tomas Polesovsky
 */
public class SecureSecretTest {

	@ClassRule
	@Rule
	public static final LiferayUnitTestRule liferayUnitTestRule =
		LiferayUnitTestRule.INSTANCE;

	@Test
	public void testGetChars() {
		char[] chars = {'s', 'e', 'c', 'r', 'e', 't'};

		SecureSecret secureSecret = new SecureSecret(chars);

		Assert.assertArrayEquals(chars, secureSecret.getChars());
		Assert.assertNotSame(chars, secureSecret.getChars());
	}

	@Test
	public void testClose() {
		char[] chars = {'s', 'e', 'c', 'r', 'e', 't'};

		SecureSecret secureSecret = new SecureSecret(chars);

		char[] internalChars = secureSecret.getChars();

		secureSecret.close();

		for (char c : internalChars) {
			Assert.assertEquals('\0', c);
		}
	}

	@Test
	public void testNullChars() {
		SecureSecret secureSecret = new SecureSecret((char[])null);

		Assert.assertNotNull(secureSecret.getChars());
		Assert.assertEquals(0, secureSecret.getChars().length);
	}

}
