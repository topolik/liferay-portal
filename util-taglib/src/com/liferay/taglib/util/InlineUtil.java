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

	public static String buildDynamicAttributes(
		Map<String, Object> dynamicAttributes) {

		if ((dynamicAttributes == null) || dynamicAttributes.isEmpty()) {
			return StringPool.BLANK;
		}

		StringBundler sb = new StringBundler(dynamicAttributes.size() * 4);

		for (Map.Entry<String, Object> entry :
				dynamicAttributes.entrySet()) {

			String key = entry.getKey();
			Object value = entry.getValue();

			String normalizedKey = key.toLowerCase();

			if (normalizedKey.startsWith("on") || normalizedKey.equals("src") ||
				normalizedKey.equals("href")){

				if (!(value instanceof XSS.SafeHtml)) {
					value = "--sanitized--";

					if (_log.isWarnEnabled()) {
						_log.warn(
							"Unable to recognize safe content for " + key,
							new Exception());
					}
				}
			}


			if (!key.equals("class")) {
				sb.append(key.toString());
				sb.append("=\"");
				sb.append(XSS.attribute(value.toString()));
				sb.append("\" ");
			}
		}

		return sb.toString();
	}

	private static final String[] INLINE_JAVASCRIPT_SINKS = {"onblur","onfocus","onclick","ondblclick","onmousedown","onmouseup","onmouseover"};

	private static final Log _log = LogFactoryUtil.getLog(InlineUtil.class);
}