/**
 * Copyright (c) 2000-present Liferay, Inc. All rights reserved.
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

import com.liferay.petra.string.CharPool;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.exception.PwdEncryptorException;
import com.liferay.portal.kernel.security.pwd.PasswordEncryptor;
import com.liferay.portal.kernel.util.StringBundler;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.kernel.util.Validator;

/**
 * @author Tomas Polesovsky
 */
public class LegacyAlgorithmAwarePasswordEncryptor
	extends BasePasswordEncryptor {

	public static PasswordEncryptor create(
		PasswordEncryptor parentPasswordEncryptor) {

		return new LegacyAlgorithmAwarePasswordEncryptor(
			parentPasswordEncryptor);
	}

	@Override
	public String encrypt(String plainTextPassword, String encryptedPassword)
		throws PwdEncryptorException {

		// Set new passwords

		if (Validator.isNull(encryptedPassword)) {
			return _encrypt(plainTextPassword);
		}

		int index = encryptedPassword.indexOf(CharPool.CLOSE_CURLY_BRACE);

		if (index <= 0) {
			throw new PwdEncryptorException(
				"Encrypted password missing close curly brace");
		}

		String algorithms = encryptedPassword.substring(1, index);

		String[] algosArr = algorithms.split(StringPool.COMMA);

		String encryptedPasswordWithoutPrefix = encryptedPassword.substring(
			index + 1);

		if (algosArr.length == 0) {
			throw new PwdEncryptorException(
				"Encrypted password does not have applied algorithm");
		}

		// Admin tries to hash passwords in legacy algorithm(s)

		if (plainTextPassword.equals(encryptedPassword)) {
			return _manuallyHash(encryptedPasswordWithoutPrefix, algorithms);
		}

		for (String algorithm : algosArr) {
			plainTextPassword = _parentPasswordEncryptor.encrypt(
				algorithm, plainTextPassword, encryptedPasswordWithoutPrefix);
		}

		return _buildPasswordString(algorithms, plainTextPassword);
	}

	@Override
	public String encrypt(
			String algorithm, String plainTextPassword,
			String encryptedPassword)
		throws PwdEncryptorException {

		// Set new passwords

		if (Validator.isNull(encryptedPassword)) {
			return _encrypt(algorithm, plainTextPassword);
		}

		return encrypt(plainTextPassword, encryptedPassword);
	}

	@Override
	public String[] getSupportedAlgorithmTypes() {
		return _parentPasswordEncryptor.getSupportedAlgorithmTypes();
	}

	protected String getAlgorithmName(String algorithm) {
		int index = algorithm.indexOf(CharPool.SLASH);

		if (index > 0) {
			algorithm = algorithm.substring(0, index);
		}

		return StringUtil.toUpperCase(algorithm);
	}

	private LegacyAlgorithmAwarePasswordEncryptor(
		PasswordEncryptor parentPasswordEncryptor) {

		_parentPasswordEncryptor = parentPasswordEncryptor;
	}

	private String _buildPasswordString(
		String algorithms, String newEncryptedPassword) {

		StringBundler sb = new StringBundler(4);

		sb.append(CharPool.OPEN_CURLY_BRACE);
		sb.append(algorithms);
		sb.append(CharPool.CLOSE_CURLY_BRACE);
		sb.append(newEncryptedPassword);

		return sb.toString();
	}

	private String _encrypt(String plainTextPassword)
		throws PwdEncryptorException {

		String algorithm = getDefaultPasswordAlgorithmType();

		algorithm = getAlgorithmName(algorithm);

		return _encrypt(algorithm, plainTextPassword);
	}

	private String _encrypt(String algorithm, String plainTextPassword)
		throws PwdEncryptorException {

		String newEncryptedPassword = _parentPasswordEncryptor.encrypt(
			algorithm, plainTextPassword, null);

		return _buildPasswordString(algorithm, newEncryptedPassword);
	}

	private String _manuallyHash(String password, String algorithms)
		throws PwdEncryptorException {

		String currentAlgorithm = getDefaultPasswordAlgorithmType();

		currentAlgorithm = getAlgorithmName(currentAlgorithm);

		password = _parentPasswordEncryptor.encrypt(
			currentAlgorithm, password, null);

		algorithms = algorithms + CharPool.COMMA + currentAlgorithm;

		return _buildPasswordString(algorithms, password);
	}

	private final PasswordEncryptor _parentPasswordEncryptor;

}