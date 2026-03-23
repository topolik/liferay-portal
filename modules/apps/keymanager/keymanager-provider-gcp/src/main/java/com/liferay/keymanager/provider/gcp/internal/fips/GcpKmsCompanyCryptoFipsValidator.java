/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.keymanager.provider.gcp.internal.fips;

import com.liferay.keymanager.spi.fips.FipsReport;
import com.liferay.keymanager.spi.fips.FipsValidator;
import com.liferay.portal.kernel.util.GetterUtil;

import java.util.Map;

import org.osgi.service.component.annotations.Component;

/**
 * @author Tomas Polesovsky
 */
@Component(service = FipsValidator.class)
public class GcpKmsCompanyCryptoFipsValidator extends BaseGcpFipsValidator {

	@Override
	public String getConfigurationPid() {
		return "com.liferay.keymanager.provider.gcp.internal.configuration.GcpKmsCompanyCryptoVaultProviderConfiguration";
	}

	@Override
	public FipsReport validate(Map<String, ?> properties) {
		if (!isFipsEnforced()) {
			return FipsReport.compliant();
		}

		String protectionLevel = GetterUtil.getString(
			properties.get("new-key-protection-level"));

		if (!"HSM".equals(protectionLevel)) {
			return FipsReport.nonCompliant(
				"Only HSM protection level is allowed when FIPS is enforced.");
		}

		return FipsReport.compliant();
	}

}