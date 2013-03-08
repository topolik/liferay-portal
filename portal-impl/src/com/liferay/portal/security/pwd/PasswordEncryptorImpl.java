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
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.StringPool;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.util.PropsUtil;

import java.io.UnsupportedEncodingException;

import java.nio.ByteBuffer;

import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.spec.InvalidKeySpecException;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;

import jodd.util.BCrypt;

/**
 * @author Tomas Polesovsky
 */
public class PasswordEncryptorImpl implements PasswordEncryptor {

	public PasswordEncryptorImpl() {
		_parent = new PwdOldEncryptor();
	}

	public String encrypt(
			String algorithm, String clearTextPassword,
			String currentEncryptedPassword)
		throws PwdEncryptorException {

		if (clearTextPassword == null) {
			throw new IllegalArgumentException(
				"Parameter clearTextPassword cannot be null!");
		}

		if (Validator.isNull(algorithm)) {
			algorithm = _PASSWORDS_ENCRYPTOR_LIFERAY_ALGORITHM;
		}

		if (algorithm.startsWith(PwdEncryptor.TYPE_BCRYPT)) {
			return encryptUsingBCrypt(
					algorithm, clearTextPassword, currentEncryptedPassword);
		}

		if (algorithm.startsWith(PwdEncryptor.TYPE_PBKDF2)) {
			return encryptUsingPBKDF2(
					algorithm, clearTextPassword, currentEncryptedPassword);
		}

		// backwards compatibility

		return _parent.encrypt(
			algorithm, clearTextPassword, currentEncryptedPassword);
	}

	protected String encryptUsingBCrypt(
			String algorithm, String clearTextPassword,
			String currentEncryptedPassword)
		throws PwdEncryptorException {

		byte[] saltBytes = _getSaltFromBCrypt(
			algorithm, currentEncryptedPassword);

		String salt = new String(saltBytes);

		return BCrypt.hashpw(clearTextPassword, salt);
	}

	protected String encryptUsingPBKDF2(
			String algorithm, String clearTextPassword,
			String currentEncryptedPassword)
		throws PwdEncryptorException {

		byte[] saltBytes = _getSaltFromPBKDF2(
			algorithm, currentEncryptedPassword);

		if (saltBytes.length < 12) {
			throw new PwdEncryptorException("Unsupported salt length");
		}

		int keySize = _DEFAULT_PBKDF2_KEY_SIZE;

		int slashIndex = algorithm.indexOf(CharPool.SLASH);
		if (slashIndex > -1) {
			int startIndex = slashIndex + 1;
			int endIndex = algorithm.indexOf(CharPool.SLASH, startIndex);

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
			clearTextPassword.toCharArray(), onlySaltBytes, rounds, keySize);

		byte[] key;
		try {
			SecretKeyFactory keyFactory = SecretKeyFactory.getInstance(
				algorithm);

			key = keyFactory.generateSecret(keySpec).getEncoded();
		} catch (InvalidKeySpecException e) {
			throw new PwdEncryptorException(
				"Unable to generate hash: " + e.getMessage(), e);
		} catch (NoSuchAlgorithmException e) {
			throw new PwdEncryptorException(
				"Unable to generate hash: " + e.getMessage(), e);
		}

		ByteBuffer result = ByteBuffer.allocate(saltBytes.length + key.length);
		result.put(saltBytes);
		result.put(key);
		return Base64.encode(result.array());
	}

	private byte[] _getSaltFromBCrypt(String algorithm, String bcryptString)
		throws PwdEncryptorException {

		byte[] saltBytes;

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

	private byte[] _getSaltFromPBKDF2(String algorithm, String pbkdf2String)
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

	private static final Pattern _algorithmRounds = Pattern.compile(
		"^.*/([0-9]+)$");

	private static final int _DEFAULT_BCRYPT_ROUNDS = 10;
	private static final int _DEFAULT_PBKDF2_KEY_SIZE = 160;
	private static final int _DEFAULT_PBKDF2_ROUNDS = 128000;

	private static final String _PASSWORDS_ENCRYPTOR_LIFERAY_ALGORITHM =
		PropsUtil.get("passwords.encryptor.liferay.algorithm");

	private PasswordEncryptor _parent;

}