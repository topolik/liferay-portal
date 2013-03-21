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

package com.liferay.portal.security.pwd;

import com.liferay.portal.kernel.util.DigesterUtil;
import com.liferay.portal.util.DigesterImpl;
import com.liferay.portal.util.PropsUtil;
import com.liferay.portal.util.PropsValues;

import java.util.ArrayList;
import java.util.List;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import org.powermock.core.classloader.annotations.PowerMockIgnore;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

/**
 * @author Michael C. Han
 */
@PowerMockIgnore({"javax.crypto.*" })
@PrepareForTest(PropsUtil.class)
@RunWith(PowerMockRunner.class)
public class LegacyAlgorithmAwarePasswordEncryptorTest {

	@Before
	public void setUp() {
		new DigesterUtil().setDigester(new DigesterImpl());

		CompositePasswordEncryptor compositePasswordEncryptor =
			new CompositePasswordEncryptor();

		compositePasswordEncryptor.setDefaultPasswordEncryptor(
			new DefaultPasswordEncryptor());

		List<PasswordEncryptor> passwordEncryptors =
			new ArrayList<PasswordEncryptor>();

		passwordEncryptors.add(new BCryptPasswordEncryptor());

		passwordEncryptors.add(new CryptPasswordEncryptor());

		passwordEncryptors.add(new NullPasswordEncryptor());

		passwordEncryptors.add(new PBKDF2PasswordEncryptor());

		passwordEncryptors.add(new SSHAPasswordEncryptor());

		compositePasswordEncryptor.setPasswordEncryptors(passwordEncryptors);

		LegacyAlgorithmAwarePasswordEncryptor
			legacyAlgorithmAwarePasswordEncryptor =
			new LegacyAlgorithmAwarePasswordEncryptor();

		legacyAlgorithmAwarePasswordEncryptor.setParentPasswordEncryptor(
			compositePasswordEncryptor);

		PasswordEncryptorUtil passwordEncryptorUtil =
			new PasswordEncryptorUtil();

		passwordEncryptorUtil.setPasswordEncryptor(
			legacyAlgorithmAwarePasswordEncryptor);
	}

	@Test
	public void testLegacyEncryption() throws Exception {

		String legacyEncryptionAlgorithm =
			PropsValues.PASSWORDS_ENCRYPTION_ALGORITHM_LEGACY;

		try {
			PropsValues.PASSWORDS_ENCRYPTION_ALGORITHM_LEGACY =
				PasswordEncryptorUtil.TYPE_SHA;

			String shaEncrypted = "W6ph5Mm5Pz8GgiULbPgzG37mj9g=";

			String newPassword = PasswordEncryptorUtil.encrypt(
				"password", shaEncrypted);

			Assert.assertEquals(shaEncrypted, newPassword);
		}
		finally {
			PropsValues.PASSWORDS_ENCRYPTION_ALGORITHM_LEGACY =
				legacyEncryptionAlgorithm;
		}
	}

	@Test
	public void testLegacyEncryptionDisabled() throws Exception {
		String legacyEncryptionAlgorithm =
			PropsValues.PASSWORDS_ENCRYPTION_ALGORITHM_LEGACY;

		try {
			PropsValues.PASSWORDS_ENCRYPTION_ALGORITHM_LEGACY = null;

			String algorithm =
				PasswordEncryptorUtil.TYPE_PBKDF2 + "WithHmacSHA1/128/50000";

			String expectedResults = PasswordEncryptorUtil.encrypt(
				algorithm, "password", null);

			String newPassword = PasswordEncryptorUtil.encrypt(
				"password", expectedResults);

			Assert.assertEquals(expectedResults, newPassword);
		}
		finally {
			PropsValues.PASSWORDS_ENCRYPTION_ALGORITHM_LEGACY =
				legacyEncryptionAlgorithm;
		}
	}

	@Test
	public void testPreviouslyMigratedEncryption() throws Exception {

		String legacyEncryptionAlgorithm =
			PropsValues.PASSWORDS_ENCRYPTION_ALGORITHM_LEGACY;

		try {
			PropsValues.PASSWORDS_ENCRYPTION_ALGORITHM_LEGACY =
				PasswordEncryptorUtil.TYPE_SHA;

			String algorithm =
				PasswordEncryptorUtil.TYPE_PBKDF2 + "WithHmacSHA1/128/50000";

			String expectedResults = PasswordEncryptorUtil.encrypt(
				algorithm, "password", null);

			String newPassword = PasswordEncryptorUtil.encrypt(
				"password", expectedResults);

			Assert.assertTrue(
				newPassword.indexOf("{PBKDF2WithHmacSHA1}") != -1);

			Assert.assertEquals(expectedResults, newPassword);
		}
		finally {
			PropsValues.PASSWORDS_ENCRYPTION_ALGORITHM_LEGACY =
				legacyEncryptionAlgorithm;
		}
	}

}