/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.keymanager.provider.os.internal.fips;

import com.liferay.keymanager.spi.fips.FipsReport;
import com.liferay.keymanager.spi.fips.FipsValidator;
import com.liferay.portal.kernel.util.GetterUtil;

import java.util.Map;

/**
 * @author Tomas Polesovsky
 */
public abstract class BaseOsFipsValidator implements FipsValidator {

	@Override
	public abstract String getConfigurationPid();

	@Override
	public FipsReport validate(Map<String, ?> properties) {
		if (isFipsEnforced()) {
			return FipsReport.noncompliant(
				"OS-based providers are not FIPS certified and cannot be " +
					"used when FIPS is enforced.");
		}

		return FipsReport.compliant();
	}

	protected boolean isFipsEnforced() {
		return GetterUtil.getBoolean(
			System.getenv("LIFERAY_KEYMANAGER_FIPS_ENFORCED"));
	}

}