/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.keymanager.internal.diagnostic;

import com.liferay.keymanager.diagnostic.KeyManagerDiagnosticResult;
import com.liferay.keymanager.diagnostic.KeyManagerDiagnosticTask;
import com.liferay.keymanager.spi.fips.FipsComplianceChecker;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Tomas Polesovsky
 */
@Component(service = KeyManagerDiagnosticTask.class)
public class FipsHealthCheck implements KeyManagerDiagnosticTask {

	@Override
	public KeyManagerDiagnosticResult execute() {
		try {
			_fipsComplianceChecker.check();

			return KeyManagerDiagnosticResult.ok(
				"FIPS compliance check passed.");
		}
		catch (Exception exception) {
			return KeyManagerDiagnosticResult.fail(
				"FIPS compliance violation: " + exception.getMessage());
		}
	}

	@Override
	public String getName() {
		return "FIPS Compliance Health Check";
	}

	@Reference
	private FipsComplianceChecker _fipsComplianceChecker;

}