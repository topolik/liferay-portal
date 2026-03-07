/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.keymanager.secret;

import com.liferay.keymanager.KeyReference;
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
	public void testSecureSecretImmutable() {
		KeyReference keyReference = KeyReference.fromString(
			"${secretRef:db:alias}");

		byte[] data = {1, 2, 3, 4};

		SecureSecret secureSecret = new SecureSecret(keyReference, data);

		// Constructor should make a copy

		data[0] = 9;

		Assert.assertEquals(1, secureSecret.getBytes()[0]);
	}

	@Test
	public void testSecureSecretZeroing() {
		KeyReference keyReference = KeyReference.fromString(
			"${secretRef:db:alias}");

		byte[] data = {1, 2, 3, 4};

		SecureSecret secureSecret = new SecureSecret(keyReference, data);

		byte[] internalBytes = secureSecret.getBytes();

		Assert.assertArrayEquals(data, internalBytes);

		secureSecret.close();

		for (byte b : internalBytes) {
			Assert.assertEquals(0, b);
		}
	}

}