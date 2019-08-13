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

package com.liferay.portal.security.credential.generator.message.digest;

import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.json.JSONFactoryUtil;
import com.liferay.portal.kernel.json.JSONObject;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.security.credential.model.AlgorithmEntry;
import com.liferay.portal.security.credential.model.CredentialMeta;
import com.liferay.portal.security.crypto.generator.salt.DefaultSaltGenerator;
import com.liferay.portal.security.crypto.generator.secret.MessageDigestSecretGenerator;

/**
 * @author arthurchan35
 */
public class MessageDigestCredentialGenerator {

	public static String generateSalt(AlgorithmEntry algorithm)
		throws Exception {

		JSONObject meta = JSONFactoryUtil.createJSONObject(algorithm.getMeta());

		if (meta.getBoolean("salted")) {
			return DefaultSaltGenerator.generate();
		}

		return StringPool.BLANK;
	}

	public static String generateSecret(String password, CredentialMeta meta)
		throws Exception {

		AlgorithmEntry algorithm = meta.getAlgorithmEntry();

		if (Validator.isNull(meta.getSalt())) {
			return MessageDigestSecretGenerator.generate(
				algorithm.getType(), password);
		}

		return MessageDigestSecretGenerator.generate(
			algorithm.getType(), password, meta.getSalt());
	}

}