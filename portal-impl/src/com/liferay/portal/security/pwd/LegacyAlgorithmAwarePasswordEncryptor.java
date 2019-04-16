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

		boolean prependAlgorithm = true;
		boolean doubleHash = false;

		if (Validator.isNotNull(encryptedPassword)) {
			if (encryptedPassword.charAt(0) != CharPool.OPEN_CURLY_BRACE) {
				if (Validator.isNotNull(
						PropsValues.PASSWORDS_ENCRYPTION_ALGORITHM_LEGACY)) {

					// If only happens when admin tries to hash passwords in
					// legacy hashing, else is for user with a password in
					// legacy hashing tries to login before admin done hashing

					if (plainTextPassword.equals(encryptedPassword)) {
						doubleHash = true;
						encryptedPassword = null;
					}
					else {
						algorithm =
							PropsValues.PASSWORDS_ENCRYPTION_ALGORITHM_LEGACY;

						prependAlgorithm = false;

						if (_log.isDebugEnabled()) {
							_log.debug("Using legacy algorithm " + algorithm);
						}
					}
				}
				else {
					prependAlgorithm = false;
				}
			}
			else {
				int index = encryptedPassword.indexOf(
					CharPool.CLOSE_CURLY_BRACE);

				if (index > 0) {
					String algorithms = encryptedPassword.substring(1, index);

					String[] algrsArr = algorithms.split(StringPool.COMMA);

					encryptedPassword = encryptedPassword.substring(index + 1);

					// When length is 2, meaning user with a password in legacy
					// hashing tries to login after admin has done hashing.

					if (algrsArr.length == 2) {
						plainTextPassword = _parentPasswordEncryptor.encrypt(
							algrsArr[0], plainTextPassword, encryptedPassword);

						algorithm = algrsArr[1];
						doubleHash = true;

						if (_log.isDebugEnabled()) {
							StringBundler sb = new StringBundler(5);

							sb.append("Upgraded password to use algorithms ");
							sb.append(algrsArr[0]);
							sb.append(StringPool.COMMA);
							sb.append(StringPool.SPACE);
							sb.append(algrsArr[1]);

							_log.debug(sb.toString());
						}
					}
					else if (algrsArr.length == 1) {
						algorithm = algrsArr[0];

						if (_log.isDebugEnabled()) {
							StringBundler sb = new StringBundler(2);

							sb.append("Upgraded password to use algorithm ");
							sb.append(algrsArr[0]);

							_log.debug(sb.toString());
						}
					}
					else {
						throw new PwdEncryptorException(
							"Password contains illegal number of algorithms");
					}
				}
			}
		}
		else {
			if (Validator.isNull(
				PropsValues.PASSWORDS_ENCRYPTION_ALGORITHM_LEGACY)) {
				prependAlgorithm = false;
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

		StringBundler algrsSB = new StringBundler(3);

		if (doubleHash) {
			algrsSB.append(PropsValues.PASSWORDS_ENCRYPTION_ALGORITHM_LEGACY);
			algrsSB.append(StringPool.COMMA);
		}

		algrsSB.append(getAlgorithmName(algorithm));

		if (_log.isDebugEnabled()) {
			_log.debug(
				"Generated password with algorithm(s) prefix using " +
					algrsSB.toString());
		}

		StringBundler sb = new StringBundler(4);

		sb.append(StringPool.OPEN_CURLY_BRACE);
		sb.append(algrsSB);
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