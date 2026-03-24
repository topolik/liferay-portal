/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.keymanager.internal.osgi.commands;

import com.liferay.keymanager.diagnostic.KeyManagerDiagnosticResult;
import com.liferay.keymanager.diagnostic.KeyManagerDiagnosticTask;
import com.liferay.keymanager.spi.fips.FipsComplianceChecker;
import com.liferay.keymanager.spi.profile.KeyManagerProfile;
import com.liferay.keymanager.spi.profile.ProfileOrchestrator;
import com.liferay.keymanager.util.GcpAliasUtil;
import com.liferay.osgi.util.osgi.commands.OSGiCommands;
import com.liferay.petra.string.StringBundler;

import java.util.List;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.component.annotations.ReferenceCardinality;
import org.osgi.service.component.annotations.ReferencePolicy;

/**
 * @author Tomas Polesovsky
 */
@Component(
	property = {
		"osgi.command.function=normalize", "osgi.command.function=status",
		"osgi.command.scope=km"
	},
	service = OSGiCommands.class
)
public class KmOSGiCommands implements OSGiCommands {

	public void normalize(String alias) {
		System.out.println("Original: " + alias);

		System.out.println(
			"Normalized (GCP): " + GcpAliasUtil.normalize(alias));
	}

	public void status() {
		System.out.println("Key Manager Status Sweep:");
		System.out.println("=========================");

		boolean fipsEnforced = fipsComplianceChecker.isFipsEnforced();

		System.out.println(
			"FIPS Enforced (LIFERAY_KEYMANAGER_FIPS_ENFORCED): " +
				fipsEnforced);

		try {
			fipsComplianceChecker.check();

			System.out.println("JVM Security Provider: OK (BCFIPS is first)");
		}
		catch (Exception exception) {
			System.out.println(
				"JVM Security Provider: FAIL (" + exception.getMessage() + ")");
		}

		KeyManagerProfile activeProfile =
			profileOrchestrator.getActiveProfile();

		if (activeProfile != null) {
			System.out.println(
				"Active Profile: " + activeProfile.getProfileId());
			System.out.println(
				"  System DEK: " + activeProfile.getSystemDekProviderId());
			System.out.println(
				"  System KEK: " + activeProfile.getSystemKekProviderId());
			System.out.println(
				"  System Secret: " +
					activeProfile.getSystemSecretProviderId());
			System.out.println(
				"  Company DEK: " + activeProfile.getCompanyDekProviderId());
			System.out.println(
				"  Company KEK: " + activeProfile.getCompanyKekProviderId());
			System.out.println(
				"  Company Secret: " +
					activeProfile.getCompanySecretProviderId());
			System.out.println(
				"  Strict Mode: " + activeProfile.isStrictMode());
		}
		else {
			System.out.println("Active Profile: NONE (Using Fallback)");
		}

		System.out.println("\nDiagnostic Tasks:");

		for (KeyManagerDiagnosticTask keyManagerDiagnosticTask :
				_diagnosticTasks) {

			KeyManagerDiagnosticResult keyManagerDiagnosticResult =
				keyManagerDiagnosticTask.execute();

			System.out.println(
				StringBundler.concat(
					"[", keyManagerDiagnosticResult.getStatus(), "] ",
					keyManagerDiagnosticTask.getName(), ": ",
					keyManagerDiagnosticResult.getMessage()));
		}
	}

	@Reference
	protected FipsComplianceChecker fipsComplianceChecker;

	@Reference
	protected ProfileOrchestrator profileOrchestrator;

	@Reference(
		cardinality = ReferenceCardinality.MULTIPLE,
		policy = ReferencePolicy.DYNAMIC
	)
	private volatile List<KeyManagerDiagnosticTask> _diagnosticTasks;

}