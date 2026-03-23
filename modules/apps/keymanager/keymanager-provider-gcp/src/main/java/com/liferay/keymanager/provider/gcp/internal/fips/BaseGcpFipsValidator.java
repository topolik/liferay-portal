/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.keymanager.provider.gcp.internal.fips;

import com.liferay.keymanager.spi.fips.FipsReport;
import com.liferay.keymanager.spi.fips.FipsValidator;
import com.liferay.portal.kernel.util.GetterUtil;

import java.util.Map;

/**
 * @author Tomas Polesovsky
 */
public abstract class BaseGcpFipsValidator implements FipsValidator {

	@Override
	public abstract String getConfigurationPid();

	@Override
	public abstract FipsReport validate(Map<String, ?> properties);

	protected boolean isFipsEnforced() {
		return GetterUtil.getBoolean(
			System.getenv("LIFERAY_KEYMANAGER_FIPS_ENFORCED"));
	}

}