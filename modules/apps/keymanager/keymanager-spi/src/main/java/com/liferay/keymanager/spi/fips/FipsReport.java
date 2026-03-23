/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.keymanager.spi.fips;

/**
 * @author Tomas Polesovsky
 */
public class FipsReport {

	public static FipsReport compliant() {
		return new FipsReport(true, null);
	}

	public static FipsReport nonCompliant(String violationMessage) {
		return new FipsReport(false, violationMessage);
	}

	public String getViolationMessage() {
		return _violationMessage;
	}

	public boolean isCompliant() {
		return _compliant;
	}

	private FipsReport(boolean compliant, String violationMessage) {
		_compliant = compliant;
		_violationMessage = violationMessage;
	}

	private final boolean _compliant;
	private final String _violationMessage;

}