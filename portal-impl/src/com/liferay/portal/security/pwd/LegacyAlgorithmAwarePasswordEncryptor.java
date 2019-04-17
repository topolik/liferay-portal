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
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.security.pwd.PasswordEncryptor;
import com.liferay.portal.kernel.util.StringBundler;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.util.PropsValues;

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
	public String encrypt(
			String algorithm, String plainTextPassword,
			String encryptedPassword)
		throws PwdEncryptorException {

		if (_log.isDebugEnabled()) {
			String message =
				"Using legacy detection scheme for algorithm " + algorithm +
					" with current password ";

			if (Validator.isNull(encryptedPassword)) {
				message += "empty";
			}
			else {
				message += "provided";
			}

			_log.debug(message);
		}

		boolean prependAlgorithm = false;
		boolean doubleHash = false;

		// No encryptedPassword means setting a new password.
		// EncryptedPassword that does not start with a open curly brace can be
		// in either legacy hash algo when legacy algo is enabled or current
		// hash algo when legacy algo is not enabled.
		// EncryptedPassword that starts with a open curly brace means legacy
		// algo is enabled and it's either in current algo or in a combination
		// of legacy plus current algo

		if (Validator.isNull(encryptedPassword)) {

			// We need to prepend algo's name when legacy algo is enabled

			if (Validator.isNotNull(
					PropsValues.PASSWORDS_ENCRYPTION_ALGORITHM_LEGACY)) {

				prependAlgorithm = true;
			}
		}
		else if (encryptedPassword.charAt(0) != CharPool.OPEN_CURLY_BRACE) {

			// If is when legacy algo is enable, we need to support both actions
			// Else is excepted to happen most often, and we do nothing

			if (Validator.isNotNull(
					PropsValues.PASSWORDS_ENCRYPTION_ALGORITHM_LEGACY)) {

				// If only happens when admin tries to hash passwords in
				// legacy hashing, else is for user with a password in
				// legacy hashing tries to login before admin done hashing

				if (plainTextPassword.equals(encryptedPassword)) {
					doubleHash = true;
					prependAlgorithm = true;
					encryptedPassword = null;
				}
				else {
					algorithm =
						PropsValues.PASSWORDS_ENCRYPTION_ALGORITHM_LEGACY;

					if (_log.isDebugEnabled()) {
						_log.debug("Using legacy algorithm " + algorithm);
					}
				}
			}
		}
		else {
			int index = encryptedPassword.indexOf(CharPool.CLOSE_CURLY_BRACE);

			if (index <= 0) {
				throw new PwdEncryptorException(
					"Encrypted password missing close curly brace");
			}

			String algorithms = encryptedPassword.substring(1, index);

			String[] algosArr = algorithms.split(StringPool.COMMA);

			encryptedPassword = encryptedPassword.substring(index + 1);

			prependAlgorithm = true;

			// When length is 2, meaning user with a password in legacy
			// hashing tries to login after admin has done hashing.

			if (algosArr.length == 2) {
				plainTextPassword = _parentPasswordEncryptor.encrypt(
					algosArr[0], plainTextPassword, encryptedPassword);

				algorithm = algosArr[1];
				doubleHash = true;

				if (_log.isDebugEnabled()) {
					StringBundler sb = new StringBundler(5);

					sb.append("Upgraded password to use algorithms ");
					sb.append(algosArr[0]);
					sb.append(StringPool.COMMA);
					sb.append(StringPool.SPACE);
					sb.append(algosArr[1]);

					_log.debug(sb.toString());
				}
			}
			else if (algosArr.length == 1) {
				algorithm = algosArr[0];

				if (_log.isDebugEnabled()) {
					StringBundler sb = new StringBundler(2);

					sb.append("Upgraded password to use algorithm ");
					sb.append(algosArr[0]);

					_log.debug(sb.toString());
				}
			}
			else {
				throw new PwdEncryptorException(
					"Password contains illegal number of algorithms");
			}
		}

		String newEncryptedPassword = _parentPasswordEncryptor.encrypt(
			algorithm, plainTextPassword, encryptedPassword);

		if (!prependAlgorithm) {
			if (_log.isDebugEnabled()) {
				_log.debug(
					"Generated password without algorithm prefix using " +
						algorithm);
			}

			return newEncryptedPassword;
		}

		StringBundler algosSB = new StringBundler(3);

		if (doubleHash) {
			algosSB.append(PropsValues.PASSWORDS_ENCRYPTION_ALGORITHM_LEGACY);
			algosSB.append(StringPool.COMMA);
		}

		algosSB.append(getAlgorithmName(algorithm));

		if (_log.isDebugEnabled()) {
			_log.debug(
				"Generated password with algorithm(s) prefix using " +
					algosSB.toString());
		}

		StringBundler sb = new StringBundler(4);

		sb.append(StringPool.OPEN_CURLY_BRACE);
		sb.append(algosSB);
		sb.append(StringPool.CLOSE_CURLY_BRACE);
		sb.append(newEncryptedPassword);

		return sb.toString();
	}

	@Override
	public String[] getSupportedAlgorithmTypes() {
		return _parentPasswordEncryptor.getSupportedAlgorithmTypes();
	}

	protected String getAlgorithmName(String algorithm) {
		int index = algorithm.indexOf(CharPool.SLASH);

		if (index > 0) {
			return algorithm.substring(0, index);
		}

		return algorithm;
	}

	private LegacyAlgorithmAwarePasswordEncryptor(
		PasswordEncryptor parentPasswordEncryptor) {

		_parentPasswordEncryptor = parentPasswordEncryptor;
	}

	private static final Log _log = LogFactoryUtil.getLog(
		LegacyAlgorithmAwarePasswordEncryptor.class);

	private final PasswordEncryptor _parentPasswordEncryptor;

}