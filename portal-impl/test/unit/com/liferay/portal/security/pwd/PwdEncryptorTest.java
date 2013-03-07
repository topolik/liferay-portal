/**
 * Copyright (c) 2000-2012 Liferay, Inc. All rights reserved.
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

import com.liferay.portal.kernel.test.TestCase;
import com.liferay.portal.kernel.util.DigesterUtil;
import com.liferay.portal.util.DigesterImpl;

import org.junit.Assert;
import org.junit.Before;

/**
 * @author Tomas Polesovsky
 */
public class PwdEncryptorTest extends TestCase {

	@Before
	public void setUp() {
		new DigesterUtil().setDigester(new DigesterImpl());
	}

	public void testEncryptBCrypt() throws Exception {
		String algorithm = PwdEncryptor.TYPE_BCRYPT;
		String password = "password";
		String expected = PwdEncryptor.encrypt(algorithm, password, null);

		long time = System.currentTimeMillis();

		String actual = PwdEncryptor.encrypt(algorithm, password, expected);

		System.out.println(
			"Hash [algorithm, time, result]: [" + algorithm +
				", " + (System.currentTimeMillis() - time) + ", " + actual +
					"]");

		Assert.assertEquals(expected, actual);
	}

	public void testEncryptBCrypt10() throws Exception {
		String algorithm = PwdEncryptor.TYPE_BCRYPT + "/10";
		String password = "password";
		String expected = PwdEncryptor.encrypt(algorithm, password, null);

		long time = System.currentTimeMillis();

		String actual = PwdEncryptor.encrypt(algorithm, password, expected);

		System.out.println(
			"Hash [algorithm, time, result]: [" + algorithm +
				", " + (System.currentTimeMillis() - time) + ", " + actual +
					"]");

		Assert.assertEquals(expected, actual);
	}

	public void testEncryptBCrypt12() throws Exception {
		String algorithm = PwdEncryptor.TYPE_BCRYPT + "/12";
		String password = "password";
		String expected = PwdEncryptor.encrypt(algorithm, password, null);

		long time = System.currentTimeMillis();

		String actual = PwdEncryptor.encrypt(algorithm, password, expected);

		System.out.println(
			"Hash [algorithm, time, result]: [" + algorithm +
				", " + (System.currentTimeMillis() - time) + ", " + actual +
					"]");

		Assert.assertEquals(expected, actual);
	}

	public void testEncryptPBKDF2() throws Exception {
		String algorithm = "PBKDF2WithHmacSHA1";
		String password = "password";
		String expected = PwdEncryptor.encrypt(algorithm, password, null);

		long time = System.currentTimeMillis();

		String actual = PwdEncryptor.encrypt(algorithm, password, expected);

		System.out.println(
			"Hash [algorithm, time, result]: [" + algorithm +
				", " + (System.currentTimeMillis() - time) + ", " + actual +
					"]");

		Assert.assertEquals(expected, actual);
	}

	public void testEncryptPBKDF2Rounds50000() throws Exception {
		String algorithm = "PBKDF2WithHmacSHA1/50000";
		String password = "password";
		String expected = PwdEncryptor.encrypt(algorithm, password, null);

		long time = System.currentTimeMillis();

		String actual = PwdEncryptor.encrypt(algorithm, password, expected);

		System.out.println(
			"Hash [algorithm, time, result]: [" + algorithm +
				", " + (System.currentTimeMillis() - time) + ", " + actual +
					"]");

		Assert.assertEquals(expected, actual);
	}

	public void testEncryptPBKDF2Rounds50000Key128() throws Exception {
		String algorithm = "PBKDF2WithHmacSHA1/128/50000";
		String password = "password";
		String expected = PwdEncryptor.encrypt(algorithm, password, null);

		long time = System.currentTimeMillis();

		String actual = PwdEncryptor.encrypt(algorithm, password, expected);

		System.out.println(
			"Hash [algorithm, time, result]: [" + algorithm +
				", " + (System.currentTimeMillis() - time) + ", " + actual +
					"]");

		Assert.assertEquals(expected, actual);
	}

}