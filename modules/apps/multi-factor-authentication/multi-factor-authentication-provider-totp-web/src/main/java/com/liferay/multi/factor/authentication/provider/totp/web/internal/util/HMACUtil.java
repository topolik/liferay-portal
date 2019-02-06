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

package com.liferay.multi.factor.authentication.provider.totp.web.internal.util;

import java.nio.ByteBuffer;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;

/**
 * @author arthurhan35
 */
public class HMACUtil {

	public static byte[] generateHMAC(byte[] key, byte[] message)
		throws Exception {

		return generateHMAC(key, message, _DEFAULT_HMAC_CRYPTO);
	}

	public static byte[] generateHMAC(byte[] key, byte[] message, String crypto)
		throws Exception {

		SecretKeySpec keySpec = new SecretKeySpec(key, crypto);
		Mac mac = Mac.getInstance(crypto);

		mac.init(keySpec);

		return mac.doFinal(message);
	}

	public static byte[] generateHMAC(byte[] key, long message)
		throws Exception {

		return generateHMAC(key, message, _DEFAULT_HMAC_CRYPTO);
	}

	public static byte[] generateHMAC(byte[] key, long message, String crypto)
		throws Exception {

		ByteBuffer messageBuffer = ByteBuffer.allocate(8);

		messageBuffer = messageBuffer.putLong(message);

		return generateHMAC(key, messageBuffer.array(), crypto);
	}

	public static byte[] generateHMAC(byte[] key, String message)
		throws Exception {

		return generateHMAC(key, message, _DEFAULT_HMAC_CRYPTO);
	}

	public static byte[] generateHMAC(byte[] key, String message, String crypto)
		throws Exception {

		return generateHMAC(key, message.getBytes(), crypto);
	}

	private static final String _DEFAULT_HMAC_CRYPTO = "HmacSHA1";

}