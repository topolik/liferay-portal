/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.keymanager.util;

/**
 * @author Tomas Polesovsky
 */
public class GcpAliasUtil {

	public static String normalize(String alias) {
		if (alias == null) {
			return null;
		}

		StringBuilder sb = new StringBuilder(alias.length());

		for (int i = 0; i < alias.length(); i++) {
			char c = alias.charAt(i);

			if (Character.isLetterOrDigit(c)) {
				sb.append(c);
			}
			else if ((c == '.') || (c == ' ')) {
				sb.append('-');
			}
			else if (c == '-') {
				sb.append('-');
			}
			else {
				sb.append('_');
			}
		}

		return sb.toString();
	}

}