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

import jodd.util.Base32;

/**
 * @author arthurhan35
 */
public class OTPUtil {

	/**
	 * generate a HMAC based OTP for the given shared key
	 *
	 * @param  key is the shared key between token and server, Base32 encoded
	 * @param  count is used to generate message, stored in HOTP table
	 * @param  digits is OTP digits, cant be longer than 10, configured by admin
	 * @param  crypto is the hash used to generate message, configured by admin
	 * @return string representation of HOTP
	 */
	public static String generateHOTP(
			String key, long count, int digits, String crypto)
		throws Exception {

		byte[] hmac = HMACUtil.generateHMAC(Base32.decode(key), count, crypto);

		// Dynamic Truncate

		int offset = hmac[hmac.length - 1] & 0xf;

		int binary = hmac[offset + 0x3] & 0xff;

		binary |= (hmac[offset + 0x2] & 0xff) << 0x08;
		binary |= (hmac[offset + 0x1] & 0xff) << 0x10;
		binary |= (hmac[offset + 0x0] & 0x7f) << 0x18;

		int modulo = binary % (int)Math.pow(10, digits);

		// account leading 0s

		StringBuilder sb = new StringBuilder(digits);

		int moduloDigits = (int)(Math.log10(modulo) + 1);

		if (modulo == 0) {
			moduloDigits = 1;
		}

		for (int i = 0; i < digits - moduloDigits; ++i) {
			sb.append('0');
		}

		sb.append(String.valueOf(modulo));

		return sb.toString();
	}

	/**
	 * generate a time based OTP for the given shared key
	 *
	 * @param  key is the shared key between token and server, Base32 encoded
	 * @param  timeWindow is used to generate message, configured by admin
	 * @param  digits is OTP digits, cant be longer than 10, configured by admin
	 * @param  crypto is the hash used to generate message, configured by admin
	 * @return string representation of TOTP
	 */
	public static String generateTOTP(
			String key, int timeWindow, int digits, String crypto)
		throws Exception {

		long intervals = System.currentTimeMillis() / (1000 * (long)timeWindow);

		return generateHOTP(key, intervals, digits, crypto);
	}

	/**
	 * resync counter on server with counter in user token
	 *
	 * @param  key is the shared key between token and server, Base32 encoded
	 * @param  count is used to generate message, stored in HOTP table
	 * @param  digits is OTP digits, cant be longer than 10, configured by admin
	 * @param  crypto is the hash used to generate message, configured by admin
	 * @param  userInputs is an array of consective HOTPs from user input
	 * @param  maxSync is max number of counter increase in one user attempt
	 * @return the next counter if sucessfully resynced otherwise 0
	 */
	public static int resyncHOTPWithConsecHOTPs(
			String key, long count, int digits, String crypto,
			String[] userInputs, int maxSync)
		throws Exception {

		String[] generated = new String[maxSync + userInputs.length];

		for (int i = 0; i < generated.length; ++i) {
			generated[i] = generateHOTP(key, count + i, digits, crypto);
		}

		for (int i = 0; i < maxSync; ++i) {
			for (int j = 0; j < userInputs.length; ++j) {
				if ((j == userInputs.length - 1) &&
					userInputs[j].equals(generated[i + j])) {

					return i + userInputs.length;
				}
			}
		}

		return 0;
	}

}