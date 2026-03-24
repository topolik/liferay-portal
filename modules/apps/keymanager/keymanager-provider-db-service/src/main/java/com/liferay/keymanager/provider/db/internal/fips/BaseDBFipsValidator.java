/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.keymanager.provider.db.internal.fips;

import com.liferay.keymanager.spi.fips.FipsReport;
import com.liferay.keymanager.spi.fips.FipsValidator;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.Validator;

import java.util.Map;

/**
 * @author Tomas Polesovsky
 */
public abstract class BaseDBFipsValidator implements FipsValidator {

	@Override
	public FipsReport validate(Map<String, ?> properties) {
		if (!GetterUtil.getBoolean(
				System.getenv("LIFERAY_KEYMANAGER_FIPS_ENFORCED"))) {

			return FipsReport.compliant();
		}

		String dekCipherSpec = (String)properties.get("dek-cipher-spec");

		if (Validator.isNull(dekCipherSpec)) {
			return FipsReport.noncompliant(
				"dek-cipher-spec must be specified and use AES/GCM mode when " +
					"FIPS is enforced");
		}

		if (!dekCipherSpec.startsWith("AES/GCM/")) {
			return FipsReport.noncompliant(
				"Only AES/GCM mode is allowed for dek-cipher-spec when FIPS " +
					"is enforced");
		}

		return FipsReport.compliant();
	}

}