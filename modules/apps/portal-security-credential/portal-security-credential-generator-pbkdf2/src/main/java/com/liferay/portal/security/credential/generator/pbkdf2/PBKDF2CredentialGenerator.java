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

package com.liferay.portal.security.credential.generator.pbkdf2;

import com.liferay.portal.kernel.json.JSONFactoryUtil;
import com.liferay.portal.kernel.json.JSONObject;
import com.liferay.portal.security.credential.model.AlgorithmEntry;
import com.liferay.portal.security.credential.model.CredentialMeta;
import com.liferay.portal.security.crypto.generator.salt.DefaultSaltGenerator;
import com.liferay.portal.security.crypto.generator.secret.PBKDF2SecretGenerator;

/**
 * @author arthurchan35
 */
public class PBKDF2CredentialGenerator {

	public static String generateSalt(AlgorithmEntry algorithm)
		throws Exception {

		return DefaultSaltGenerator.generate();
	}

	public static String generateSecret(String password, CredentialMeta meta)
		throws Exception {

		String salt = meta.getSalt();

		AlgorithmEntry algorithm = meta.getAlgorithmEntry();

		JSONObject algorithmMeta = JSONFactoryUtil.createJSONObject(
			algorithm.getMeta());

		String prfName = algorithmMeta.getString("prfName");

		int rounds = algorithmMeta.getInt("rounds");

		int keySize = algorithmMeta.getInt("keySize");

		return PBKDF2SecretGenerator.generate(
			prfName, password, salt, rounds, keySize);
	}

}