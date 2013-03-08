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
import com.liferay.portal.kernel.util.StringPool;
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

	public void testEdgeCases() throws Exception {
		testFail(
			"Some nonexistent algorithm", StringPool.BLANK, StringPool.BLANK);
	}

	public void testEncryptBCrypt() throws Exception {
		String algorithm = PwdEncryptor.TYPE_BCRYPT;
		testAlgorithm(algorithm);
		testEdgeCasesAllowEmptyPass(algorithm);
	}

	public void testEncryptBCrypt10() throws Exception {
		String algorithm = PwdEncryptor.TYPE_BCRYPT + "/10";
		testAlgorithm(algorithm);
		testEdgeCasesAllowEmptyPass(algorithm);
	}

	public void testEncryptBCrypt12() throws Exception {
		String algorithm = PwdEncryptor.TYPE_BCRYPT + "/12";
		testAlgorithm(algorithm);
		testEdgeCasesAllowEmptyPass(algorithm);
	}

	public void testEncryptCRYPT() throws Exception {
		String algorithm = PwdEncryptor.TYPE_CRYPT;
		testAlgorithm(algorithm);
		testEdgeCasesAllowEmptyPass(algorithm);
		testAlgorithmWithEmptyVariations(
			algorithm, "password", "{CRYPT}SNbUMVY9kKQpY");
	}

	public void testEncryptMD2() throws Exception {
		String algorithm = PwdEncryptor.TYPE_MD2;
		testAlgorithm(algorithm);
		testEdgeCasesAllowEmptyPass(algorithm);
		testAlgorithmWithEmptyVariations(
			algorithm, "password", "{MD2}8DiBqIxuORNfDsxg79YJuQ==");
	}

	public void testEncryptMD5() throws Exception {
		String algorithm = PwdEncryptor.TYPE_MD5;
		testAlgorithm(algorithm);
		testEdgeCasesAllowEmptyPass(algorithm);
		testAlgorithmWithEmptyVariations(
			algorithm, "password", "{MD5}X03MO1qnZdYdgyfeuILPmQ==");
	}

	public void testEncryptNONE() throws Exception {
		String algorithm = PwdEncryptor.TYPE_NONE;
		testAlgorithm(algorithm);
		testEdgeCasesAllowEmptyPass(algorithm);
		testAlgorithmWithEmptyVariations(
			algorithm, "password", "{NONE}password");
	}

	public void testEncryptPBKDF2() throws Exception {
		String algorithm = "PBKDF2WithHmacSHA1";
		testAlgorithm(algorithm);
		testEdgeCases(algorithm);
	}

	public void testEncryptPBKDF2Rounds50000() throws Exception {
		String algorithm = "PBKDF2WithHmacSHA1/50000";
		testAlgorithm(algorithm);
		testEdgeCases(algorithm);
	}

	public void testEncryptPBKDF2Rounds50000Key128() throws Exception {
		String algorithm = "PBKDF2WithHmacSHA1/128/50000";
		testAlgorithm(algorithm);
		testEdgeCases(algorithm);
	}

	public void testEncryptSHA() throws Exception {
		String algorithm = PwdEncryptor.TYPE_SHA;
		testAlgorithm(algorithm);
		testEdgeCasesAllowEmptyPass(algorithm);
		testAlgorithmWithEmptyVariations(
			algorithm, "password", "{SHA}W6ph5Mm5Pz8GgiULbPgzG37mj9g=");
	}

	public void testEncryptSHA1() throws Exception {
		String algorithm = "SHA-1";
		testAlgorithm(algorithm);
		testEdgeCasesAllowEmptyPass(algorithm);
		testAlgorithmWithEmptyVariations(
			algorithm, "password", "{SHA-1}W6ph5Mm5Pz8GgiULbPgzG37mj9g=");
	}

	public void testEncryptSHA256() throws Exception {
		String algorithm = PwdEncryptor.TYPE_SHA_256;
		testAlgorithm(algorithm);
		testEdgeCasesAllowEmptyPass(algorithm);
		testAlgorithmWithEmptyVariations(
			algorithm, "password",
			"{SHA-256}XohImNooBHFR0OVvjcYpJ3NgPQ1qq73WKhHvch0VQtg=");
	}

	public void testEncryptSHA384() throws Exception {
		String algorithm = PwdEncryptor.TYPE_SHA_384;
		testAlgorithm(algorithm);
		testEdgeCasesAllowEmptyPass(algorithm);
		testAlgorithmWithEmptyVariations(
			algorithm, "password",
			"{SHA-384}qLZLq9CsqRpZvbt3YbQh1PK7OCgNOnW6DyHyvrxFWD1EbFmGYMl" +
				"M5oDEfRnDB4On");
	}

	public void testEncryptSSHA() throws Exception {
		String algorithm = PwdEncryptor.TYPE_SSHA;
		testAlgorithm(algorithm);
		testEdgeCasesAllowEmptyPass(algorithm);
	}

	public void testEncryptUFCCRYPT() throws Exception {
		String algorithm = PwdEncryptor.TYPE_UFC_CRYPT;
		testAlgorithm(algorithm);
		testEdgeCasesAllowEmptyPass(algorithm);
		testAlgorithmWithEmptyVariations(
			algorithm, "password", "{UFC-CRYPT}2lrTlR/pWPUOQ");
	}

	protected void testAlgorithm(String algorithm) throws Exception {
		String password = "password";
		String encrypted = PwdEncryptor.encrypt(algorithm, password, null);

		testAlgorithmWithEmptyVariations(algorithm, password, encrypted);
	}

	protected String testAlgorithm(
			String algorithm, String password, String encrypted,
			String hashedPrefix)
		throws Exception {

		long time = System.currentTimeMillis();

		String actual = PwdEncryptor.encrypt(algorithm, password, encrypted);

		System.out.println(
			"Hash [algorithm, time, result]: [" + algorithm +
				", " + (System.currentTimeMillis() - time) + ", " + actual +
				"]");

		Assert.assertEquals(encrypted, actual);

		Assert.assertTrue(actual.startsWith(hashedPrefix));

		return actual;
	}

	protected void testAlgorithmWithEmptyVariations(
			String algorithm, String password, String encrypted)
		throws Exception {

		String hashedPrefix =
			StringPool.OPEN_CURLY_BRACE + algorithm +
				StringPool.CLOSE_CURLY_BRACE;

		testAlgorithm(null, password, encrypted, hashedPrefix);
		testAlgorithm(StringPool.BLANK, password, encrypted, hashedPrefix);
		testAlgorithm(algorithm, password, encrypted, hashedPrefix);
	}

	protected void testEdgeCases(String algorithm) throws Exception {
		testFail(algorithm, null, null);
		testFail(algorithm, null, StringPool.BLANK);
		testFail(algorithm, StringPool.BLANK, null);
		testFail(algorithm, StringPool.BLANK, StringPool.BLANK);
	}

	protected void testEdgeCasesAllowEmptyPass(String algorithm)
		throws Exception {

		testFail(algorithm, null, null);
		testFail(algorithm, null, StringPool.BLANK);
		PwdEncryptor.encrypt(algorithm, StringPool.BLANK, null);
		PwdEncryptor.encrypt(algorithm, StringPool.BLANK, StringPool.BLANK);
	}

	protected void testFail(String password) {
		try {
			PwdEncryptor.encrypt(password);

			fail();
		} catch (Exception e){}
	}

	protected void testFail(String password, String encryptedPassword) {
		try {
			PwdEncryptor.encrypt(password, encryptedPassword);

			fail();
		} catch (Exception e){}
	}

	protected void testFail(
		String algorithm, String password, String encryptedPassword) {

		try {
			PwdEncryptor.encrypt(algorithm, password, encryptedPassword);

			fail();
		} catch (Exception e){}
	}

}