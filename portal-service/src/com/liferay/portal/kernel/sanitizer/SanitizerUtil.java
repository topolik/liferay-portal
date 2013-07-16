/**
 * Copyright (c) 2000-2013 Liferay, Inc. All rights reserved.
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

package com.liferay.portal.kernel.sanitizer;

import com.liferay.portal.kernel.io.unsync.UnsyncStringWriter;
import com.liferay.portal.kernel.security.pacl.permission.PortalRuntimePermission;
import com.liferay.portal.kernel.util.CharPool;
import com.liferay.portal.kernel.util.StringPool;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.kernel.util.Validator;

import java.io.InputStream;
import java.io.OutputStream;

import java.nio.CharBuffer;

import java.util.Map;

/**
 * @author Zsolt Balogh
 * @author Brian Wing Shun Chan
 */
public class SanitizerUtil {

	public static Sanitizer getSanitizer() {
		PortalRuntimePermission.checkGetBeanProperty(SanitizerUtil.class);

		return _sanitizer;
	}

	public static byte[] sanitize(
			long companyId, long groupId, long userId, String className,
			long classPK, String contentType, byte[] bytes)
		throws SanitizerException {

		return sanitize(
			companyId, groupId, userId, className, classPK, contentType,
			Sanitizer.MODE_ALL, bytes, null);
	}

	public static void sanitize(
			long companyId, long groupId, long userId, String className,
			long classPK, String contentType, InputStream inputStream,
			OutputStream outputStream)
		throws SanitizerException {

		sanitize(
			companyId, groupId, userId, className, classPK, contentType,
			Sanitizer.MODE_ALL, inputStream, outputStream, null);
	}

	public static String sanitize(
			long companyId, long groupId, long userId, String className,
			long classPK, String contentType, String s)
		throws SanitizerException {

		return sanitize(
			companyId, groupId, userId, className, classPK, contentType,
			Sanitizer.MODE_ALL, s, null);
	}

	public static byte[] sanitize(
			long companyId, long groupId, long userId, String className,
			long classPK, String contentType, String mode, byte[] bytes,
			Map<String, Object> options)
		throws SanitizerException {

		return sanitize(
			companyId, groupId, userId, className, classPK, contentType,
			new String[] {mode}, bytes, options);
	}

	public static void sanitize(
			long companyId, long groupId, long userId, String className,
			long classPK, String contentType, String mode,
			InputStream inputStream, OutputStream outputStream,
			Map<String, Object> options)
		throws SanitizerException {

		sanitize(
			companyId, groupId, userId, className, classPK, contentType,
			new String[] {mode}, inputStream, outputStream, options);
	}

	public static String sanitize(
			long companyId, long groupId, long userId, String className,
			long classPK, String contentType, String mode, String s,
			Map<String, Object> options)
		throws SanitizerException {

		return sanitize(
			companyId, groupId, userId, className, classPK, contentType,
			new String[] {mode}, s, options);
	}

	public static byte[] sanitize(
			long companyId, long groupId, long userId, String className,
			long classPK, String contentType, String[] modes, byte[] bytes,
			Map<String, Object> options)
		throws SanitizerException {

		return getSanitizer().sanitize(
			companyId, groupId, userId, className, classPK, contentType, modes,
			bytes, options);
	}

	public static void sanitize(
			long companyId, long groupId, long userId, String className,
			long classPK, String contentType, String[] modes,
			InputStream inputStream, OutputStream outputStream,
			Map<String, Object> options)
		throws SanitizerException {

		getSanitizer().sanitize(
			companyId, groupId, userId, className, classPK, contentType, modes,
			inputStream, outputStream, options);
	}

	public static String sanitize(
			long companyId, long groupId, long userId, String className,
			long classPK, String contentType, String[] modes, String s,
			Map<String, Object> options)
		throws SanitizerException {

		return getSanitizer().sanitize(
			companyId, groupId, userId, className, classPK, contentType, modes,
			s, options);
	}

	public static void sanitizeCrlf(CharBuffer cb, UnsyncStringWriter writer) {
		sanitizeCrlf(cb, writer, -1);
	}

	public static void sanitizeCrlf(
		CharBuffer cb, UnsyncStringWriter writer, int limit) {

		sanitizeCrlf(cb, writer, limit, CharPool.UNDERLINE);
	}

	public static void sanitizeCrlf(
		CharBuffer cb, UnsyncStringWriter writer, int limit, char replacement) {

		if ((limit < 0) || (limit > cb.limit())) {
			limit = cb.limit();
		}

		for (int i = 0; i < limit; i++) {
			char c = cb.charAt(i);

			if ((c == CharPool.NEW_LINE) || (c == CharPool.RETURN)) {
				c = replacement;
			}

			writer.write(c);
		}
	}

	public static String sanitizeCrlf(String s) {
		return sanitizeCrlf(s, CharPool.UNDERLINE);
	}

	public static String sanitizeCrlf(String s, char replacement) {
		if (Validator.isBlank(s)) {
			return StringPool.BLANK;
		}

		String replacementString = String.valueOf(replacement);

		String[] replacements = new String[] {
			replacementString, replacementString
		};

		return StringUtil.replace(s, Sanitizer.CRLF, replacements);
	}

	public void setSanitizer(Sanitizer sanitizer) {
		PortalRuntimePermission.checkSetBeanProperty(getClass());

		_sanitizer = sanitizer;
	}

	private static Sanitizer _sanitizer;

}