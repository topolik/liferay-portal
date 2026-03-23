/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.keymanager.internal.fips;

import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.util.GetterUtil;

import java.security.Provider;
import java.security.Security;

import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;

/**
 * @author Tomas Polesovsky
 */
@Component(immediate = true, service = FipsComplianceChecker.class)
public class FipsComplianceChecker {

	public void check() {
		if (!isFipsEnforced()) {
			return;
		}

		Provider[] providers = Security.getProviders();

		if ((providers.length == 0) ||
			!providers[0].getName().equals("BCFIPS")) {

			throw new RuntimeException(
				"FIPS compliance violation: BCFIPS must be the first security " +
					"provider when FIPS is enforced.");
		}
	}

	public boolean isFipsEnforced() {
		return GetterUtil.getBoolean(
			System.getenv("LIFERAY_KEYMANAGER_FIPS_ENFORCED"));
	}

	@Activate
	protected void activate() {
		if (isFipsEnforced()) {
			_log.info("Key Manager is running in STRICT FIPS Mode.");

			check();
		}
		else {
			_log.info("Key Manager is running in Standard Mode.");
		}
	}

	private static final Log _log = LogFactoryUtil.getLog(
		FipsComplianceChecker.class);

}