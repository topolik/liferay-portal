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
public class KeyReferenceTest {

	@ClassRule
	@Rule
	public static final LiferayUnitTestRule liferayUnitTestRule =
		LiferayUnitTestRule.INSTANCE;

	@Test
	public void testEqualsAndHashCode() {
		KeyReference ref1 = KeyReference.fromString("${keyRef:p1:i1}");
		KeyReference ref2 = KeyReference.fromString("${keyRef:p1:i1}");
		KeyReference ref3 = KeyReference.fromString("${secretRef:p1:i1}");
		KeyReference ref4 = KeyReference.fromString("${keyRef:p2:i1}");

		Assert.assertEquals(ref1, ref2);
		Assert.assertEquals(ref1.hashCode(), ref2.hashCode());

		Assert.assertNotEquals(ref1, ref3);
		Assert.assertNotEquals(ref1, ref4);
		Assert.assertNotEquals(ref1, null);
		Assert.assertNotEquals(ref1, "string");
	}

	@Test
	public void testFromStringCrypto() {
		String raw = "${keyRef:keystore:master-key}";

		KeyReference keyReference = KeyReference.fromString(raw);

		Assert.assertNotNull(keyReference);
		Assert.assertEquals(KeyReference.Type.CRYPTO, keyReference.getType());
		Assert.assertEquals("keystore", keyReference.getProviderId());
		Assert.assertEquals("master-key", keyReference.getIdentifier());
		Assert.assertEquals(raw, keyReference.toString());

		// Test dots and colons in identifier
		raw = "${keyRef:gcp:my.key:v1}";
		keyReference = KeyReference.fromString(raw);
		Assert.assertEquals("gcp", keyReference.getProviderId());
		Assert.assertEquals("my.key:v1", keyReference.getIdentifier());
	}

	@Test
	public void testFromStringInvalid() {
		Assert.assertNull(KeyReference.fromString("invalid"));
		Assert.assertNull(KeyReference.fromString("${unknownRef:provider:id}"));
	}

	@Test
	public void testFromStringSecret() {
		String raw = "${secretRef:db:jdbc-password}";

		KeyReference keyReference = KeyReference.fromString(raw);

		Assert.assertNotNull(keyReference);
		Assert.assertEquals(KeyReference.Type.SECRET, keyReference.getType());
		Assert.assertEquals("db", keyReference.getProviderId());
		Assert.assertEquals("jdbc-password", keyReference.getIdentifier());
		Assert.assertEquals(raw, keyReference.toString());
	}

}
