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

import com.liferay.portal.PwdEncryptorException;
import com.liferay.portal.kernel.util.Base64;
import com.liferay.portal.kernel.util.CharPool;
import com.liferay.portal.kernel.util.Digester;
import com.liferay.portal.kernel.util.DigesterUtil;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.PropsKeys;
import com.liferay.portal.kernel.util.StringPool;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.util.PropsUtil;

import java.io.UnsupportedEncodingException;

import java.nio.ByteBuffer;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.spec.InvalidKeySpecException;

import java.util.Random;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;

import jodd.util.BCrypt;

import org.vps.crypt.Crypt;

/**
 * @author Brian Wing Shun Chan
 * @author Scott Lee
 */
public class PwdEncryptor {

	public static final String PASSWORDS_ENCRYPTION_ALGORITHM =
		GetterUtil.getString(
			PropsUtil.get(
				PropsKeys.PASSWORDS_ENCRYPTION_ALGORITHM)).toUpperCase();

	public static final char[] SALT_CHARS =
		"abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789./"
			.toCharArray();

	public static final String TYPE_BCRYPT = "BCRYPT";

	/**
	 * @deprecated As of 6.1.0, replaced by {@link #TYPE_UFC_CRYPT}
	 */
	public static final String TYPE_CRYPT = "CRYPT";

	public static final String TYPE_MD2 = "MD2";

	public static final String TYPE_MD5 = "MD5";

	public static final String TYPE_NONE = "NONE";

	public static final String TYPE_PBKDF2 = "PBKDF2";

	public static final String TYPE_SHA = "SHA";

	public static final String TYPE_SHA_256 = "SHA-256";

	public static final String TYPE_SHA_384 = "SHA-384";

	public static final String TYPE_SSHA = "SSHA";

	public static final String TYPE_UFC_CRYPT = "UFC-CRYPT";

	public static String encrypt(String clearTextPassword)
		throws PwdEncryptorException {

		return encrypt(PASSWORDS_ENCRYPTION_ALGORITHM, clearTextPassword, null);
	}

	public static String encrypt(
			String clearTextPassword, String currentEncryptedPassword)
		throws PwdEncryptorException {

		return encrypt(
			PASSWORDS_ENCRYPTION_ALGORITHM, clearTextPassword,
			currentEncryptedPassword);
	}

	public static String encrypt(
			String algorithm, String clearTextPassword,
			String currentEncryptedPassword)
		throws PwdEncryptorException {

		if (algorithm.startsWith(TYPE_BCRYPT)) {
			byte[] saltBytes = _getSaltFromBCrypt(
				algorithm, currentEncryptedPassword);

			return encodePassword(algorithm, clearTextPassword, saltBytes);
		}
		else if (algorithm.equals(TYPE_CRYPT) ||
				 algorithm.equals(TYPE_UFC_CRYPT)) {

			byte[] saltBytes = _getSaltFromCrypt(currentEncryptedPassword);

			return encodePassword(algorithm, clearTextPassword, saltBytes);
		}
		else if (algorithm.equals(TYPE_NONE)) {
			return clearTextPassword;
		}
		else if (algorithm.startsWith(TYPE_PBKDF2)) {
			byte[] saltBytes = _getSaltFromPBKDF2(
				algorithm, currentEncryptedPassword);

			return encodePassword(algorithm, clearTextPassword, saltBytes);
		}
		else if (algorithm.equals(TYPE_SSHA)) {
			byte[] saltBytes = _getSaltFromSSHA(currentEncryptedPassword);

			return encodePassword(algorithm, clearTextPassword, saltBytes);
		}
		else {
			return encodePassword(algorithm, clearTextPassword, null);
		}
	}

	protected static String encodePassword(
			String algorithm, String clearTextPassword, byte[] saltBytes)
		throws PwdEncryptorException {

		try {
			if (algorithm.startsWith(TYPE_BCRYPT)) {
				String salt = new String(saltBytes);

				return BCrypt.hashpw(clearTextPassword, salt);
			}
			else if (algorithm.equals(TYPE_CRYPT) ||
					 algorithm.equals(TYPE_UFC_CRYPT)) {

				return Crypt.crypt(
					saltBytes, clearTextPassword.getBytes(Digester.ENCODING));
			}
			else if (algorithm.startsWith(TYPE_PBKDF2)) {
				if (saltBytes.length < 12) {
					throw new PwdEncryptorException("Unsupported salt length");
				}

				int keySize = _DEFAULT_PBKDF2_KEY_SIZE;

				int slashIndex = algorithm.indexOf(CharPool.SLASH);
				if (slashIndex > -1) {
					int startIndex = slashIndex + 1;
					int endIndex = algorithm.indexOf(
						CharPool.SLASH, startIndex);

					if (endIndex > startIndex) {
						keySize = GetterUtil.getInteger(
							algorithm.substring(startIndex, endIndex), keySize);
					}

					algorithm = algorithm.substring(0, slashIndex);
				}

				byte[] onlySaltBytes = new byte[saltBytes.length - 4];

				ByteBuffer buff = ByteBuffer.wrap(saltBytes);
				int rounds = buff.getInt();
				buff.get(onlySaltBytes);

				PBEKeySpec keySpec = new PBEKeySpec(
					clearTextPassword.toCharArray(), onlySaltBytes, rounds,
					keySize);

				SecretKeyFactory keyFactory = SecretKeyFactory.getInstance(
					algorithm);

				byte[] key;
				try {
					key = keyFactory.generateSecret(keySpec).getEncoded();
				} catch (InvalidKeySpecException e) {
					throw new PwdEncryptorException(
						"Unable to generate hash: " + e.getMessage(), e);
				}

				ByteBuffer result = ByteBuffer.allocate(
					saltBytes.length + key.length);

				result.put(saltBytes);
				result.put(key);
				return Base64.encode(result.array());
			}
			else if (algorithm.equals(TYPE_SSHA)) {
				byte[] clearTextPasswordBytes = clearTextPassword.getBytes(
					Digester.ENCODING);

				// Create a byte array of salt bytes appended to password bytes

				byte[] pwdPlusSalt =
					new byte[clearTextPasswordBytes.length + saltBytes.length];

				System.arraycopy(
					clearTextPasswordBytes, 0, pwdPlusSalt, 0,
					clearTextPasswordBytes.length);

				System.arraycopy(
					saltBytes, 0, pwdPlusSalt, clearTextPasswordBytes.length,
					saltBytes.length);

				// Digest byte array

				MessageDigest sha1Digest = MessageDigest.getInstance("SHA-1");

				byte[] pwdPlusSaltHash = sha1Digest.digest(pwdPlusSalt);

				// Appends salt bytes to the SHA-1 digest.

				byte[] digestPlusSalt =
					new byte[pwdPlusSaltHash.length + saltBytes.length];

				System.arraycopy(
					pwdPlusSaltHash, 0, digestPlusSalt, 0,
					pwdPlusSaltHash.length);

				System.arraycopy(
					saltBytes, 0, digestPlusSalt, pwdPlusSaltHash.length,
					saltBytes.length);

				// Base64 encode and format string

				return Base64.encode(digestPlusSalt);
			}
			else {
				return DigesterUtil.digest(algorithm, clearTextPassword);
			}
		}
		catch (NoSuchAlgorithmException nsae) {
			throw new PwdEncryptorException(nsae.getMessage());
		}
		catch (UnsupportedEncodingException uee) {
			throw new PwdEncryptorException(uee.getMessage());
		}
	}

	private static byte[] _getSaltFromBCrypt(
			String algorithm, String bcryptString)
		throws PwdEncryptorException {

		byte[] saltBytes = null;

		try {
			int rounds = _DEFAULT_BCRYPT_ROUNDS;

			Matcher algorithmRoundsMatcher = _algorithmRounds.matcher(
				algorithm);

			if (algorithmRoundsMatcher.matches()) {
				rounds = GetterUtil.getInteger(
					algorithmRoundsMatcher.group(1), rounds);
			}

			if (Validator.isNull(bcryptString)) {
				String salt = BCrypt.gensalt(rounds);

				saltBytes = salt.getBytes(StringPool.UTF8);
			}
			else {
				String salt = bcryptString.substring(0, 29);

				saltBytes = salt.getBytes(StringPool.UTF8);
			}
		}
		catch (UnsupportedEncodingException uee) {
			throw new PwdEncryptorException(
				"Unable to extract salt from encrypted password: " +
					uee.getMessage());
		}

		return saltBytes;
	}

	private static byte[] _getSaltFromCrypt(String cryptString)
		throws PwdEncryptorException {

		byte[] saltBytes = null;

		try {
			if (Validator.isNull(cryptString)) {

				// Generate random salt

				Random random = new SecureRandom();

				int numSaltChars = SALT_CHARS.length;

				StringBuilder sb = new StringBuilder();

				int x = random.nextInt(Integer.MAX_VALUE) % numSaltChars;
				int y = random.nextInt(Integer.MAX_VALUE) % numSaltChars;

				sb.append(SALT_CHARS[x]);
				sb.append(SALT_CHARS[y]);

				String salt = sb.toString();

				saltBytes = salt.getBytes(Digester.ENCODING);
			}
			else {

				// Extract salt from encrypted password

				String salt = cryptString.substring(0, 2);

				saltBytes = salt.getBytes(Digester.ENCODING);
			}
		}
		catch (UnsupportedEncodingException uee) {
			throw new PwdEncryptorException(
				"Unable to extract salt from encrypted password: " +
					uee.getMessage());
		}

		return saltBytes;
	}

	private static byte[] _getSaltFromPBKDF2(
			String algorithm, String pbkdf2String)
		throws PwdEncryptorException {

		byte[] roundsWithSalt = new byte[12];

		if (Validator.isNull(pbkdf2String)) {
			byte[] saltBytes = new byte[8];

			SecureRandom random = new SecureRandom();

			random.nextBytes(saltBytes);

			int rounds = _DEFAULT_PBKDF2_ROUNDS;

			Matcher algorithmRoundsMatcher = _algorithmRounds.matcher(
				algorithm);

			if (algorithmRoundsMatcher.matches()) {
				rounds = GetterUtil.getInteger(
					algorithmRoundsMatcher.group(1), rounds);
			}

			ByteBuffer buff = ByteBuffer.allocate(12);
			buff.putInt(rounds);
			buff.put(saltBytes);

			roundsWithSalt = buff.array();

			return roundsWithSalt;
		}
		else {
			try {
				byte[] saltPlusDigest = Base64.decode(pbkdf2String);

				System.arraycopy(
					saltPlusDigest, 0, roundsWithSalt, 0,
					roundsWithSalt.length);

				return roundsWithSalt;
			}
			catch (Exception e) {
				throw new PwdEncryptorException(
					"Unable to extract salt from encrypted password: " +
						e.getMessage());
			}
		}
	}

	private static byte[] _getSaltFromSSHA(String sshaString)
		throws PwdEncryptorException {

		byte[] saltBytes = new byte[8];

		if (Validator.isNull(sshaString)) {

			// Generate random salt

			Random random = new SecureRandom();

			random.nextBytes(saltBytes);
		}
		else {

			// Extract salt from encrypted password

			try {
				byte[] digestPlusSalt = Base64.decode(sshaString);
				byte[] digestBytes = new byte[digestPlusSalt.length - 8];

				System.arraycopy(
					digestPlusSalt, 0, digestBytes, 0, digestBytes.length);

				System.arraycopy(
					digestPlusSalt, digestBytes.length, saltBytes, 0,
					saltBytes.length);
			}
			catch (Exception e) {
				throw new PwdEncryptorException(
					"Unable to extract salt from encrypted password: " +
						e.getMessage());
			}
		}

		return saltBytes;
	}

	private static final Pattern _algorithmRounds = Pattern.compile(
		"^.*/([0-9]+)$");

	private static final int _DEFAULT_BCRYPT_ROUNDS = 10;
	private static final int _DEFAULT_PBKDF2_KEY_SIZE = 160;
	private static final int _DEFAULT_PBKDF2_ROUNDS = 128000;

}