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

import com.liferay.petra.string.StringBundler;

import java.nio.ByteBuffer;

import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;

/**
 * @author arthurhan35
 */
public class TOTPUtil {

	public static boolean verifyTOTP(
		byte[] key, String totp, long clockSkewMs, long timeWindowMs,
		int digits, String algorithm) {

		long min = (System.currentTimeMillis() - clockSkewMs) / timeWindowMs;
		long max = (System.currentTimeMillis() + clockSkewMs) / timeWindowMs;

		for (long i = min; i <= max; i++) {
			String generatedTotp = generateHOTP(key, i, digits, algorithm);

			if (generatedTotp.equals(totp)) {
				return true;
			}
		}

		return false;
	}

	public static String generateHOTP(
		byte[] key, long count, int digits, String algorithm) {

		if ((digits < 1) || (digits > 9)) {
			throw new IllegalArgumentException(
				StringBundler.concat(
					"HOTP can only generate 1-9 digits but ", digits,
					" requested"));
		}

		Mac mac = null;

		try {
			mac = Mac.getInstance(algorithm);

			mac.init(new SecretKeySpec(key, algorithm));
		}
		catch (InvalidKeyException ike) {
			throw new IllegalArgumentException(
				"Invalid secret key for algorithm " + algorithm, ike);
		}
		catch (NoSuchAlgorithmException nsae) {
			throw new IllegalArgumentException(
				"Invalid HOTP algorithm " + algorithm, nsae);
		}

		ByteBuffer byteBuffer = ByteBuffer.allocate(8);

		byteBuffer.putLong(count);

		byte[] hmac = mac.doFinal(byteBuffer.array());

		int offset = hmac[hmac.length - 1] & 0xf;

		int binary =
			(hmac[offset + 0x3] & 0xff) |
			(hmac[offset + 0x2] & 0xff) << 8 |
			(hmac[offset + 0x1] & 0xff) << 16 |
			(hmac[offset + 0x0] & 0x7f) << 24;

		int otp = binary % (int)Math.pow(10, digits);

		return String.format(StringBundler.concat("%0", digits, "d"), otp);
	}

}