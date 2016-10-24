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

package com.liferay.taglib.util;

import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.security.xss.XSS;
import com.liferay.portal.kernel.util.StringBundler;
import com.liferay.portal.kernel.util.StringPool;

import java.util.Map;

/**
 * @author Shuyang Zhou
 */
public class InlineUtil {

	/**
	 * @deprecated As of 7.1 please use {@link #buildSafeDynamicAttributes(Map)}
	 */
	@Deprecated
	public static String buildDynamicAttributes(
		Map<String, Object> dynamicAttributes) {

		return buildSafeDynamicAttributes(dynamicAttributes).toString();
	}

	public static XSS.SafeHTML buildSafeDynamicAttributes(
		Map<String, Object> dynamicAttributes) {

		if ((dynamicAttributes == null) || dynamicAttributes.isEmpty()) {
			return XSS.safeHTML(StringPool.BLANK);
		}

		StringBundler sb = new StringBundler(dynamicAttributes.size() * 4);

		for (Map.Entry<String, Object> entry :
				dynamicAttributes.entrySet()) {

			String key = entry.getKey();
			Object value = entry.getValue();

			String normalizedKey = key.toLowerCase();

			if (normalizedKey.startsWith("on") &&
				(!(value instanceof XSS.VerifiedJS))) {

				if (_log.isWarnEnabled()) {
					_log.warn(
						"Unable to recognize verified JS content for " + key,
						new Exception());
				}

				value = StringPool.BLANK;
			}

			if ((normalizedKey.equals("src") || normalizedKey.equals("href")) &&
					(!(value instanceof XSS.EscapedString))){

				if (_log.isWarnEnabled()) {
					_log.warn(
						"Unable to recognize safe content for " + key,
						new Exception());
				}

				value = StringPool.BLANK;
			}


			if (!key.equals("class")) {
				sb.append(key);
				sb.append("=\"");
				sb.append(XSS.attribute(value.toString()));
				sb.append("\" ");
			}
		}

		return XSS.safeHTML(sb.toString());
	}

	private static final Log _log = LogFactoryUtil.getLog(InlineUtil.class);
}