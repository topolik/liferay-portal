/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.keymanager.internal.fips;

import com.liferay.keymanager.spi.fips.FipsComplianceChecker;
import com.liferay.portal.test.rule.LiferayUnitTestRule;

import java.security.Provider;
import java.security.Security;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

/**
 * @author Tomas Polesovsky
 */
public class FipsComplianceCheckerTest {

	@Before
	public void setUp() {
		_fipsComplianceChecker = new FipsComplianceCheckerImpl() {

			@Override
			public boolean isFipsEnforced() {
				return true;
			}

		};
	}

	@Test
	public void testZombieMasterKeyBootTimeHeartbeatFailure() {
		Provider originalProvider = null;

		try {

			// Simulate BCFIPS being registered successfully initially

			Provider[] providers = Security.getProviders();

			if (providers.length > 0) {
				originalProvider = providers[0];

				Security.removeProvider(originalProvider.getName());
			}

			Provider dummyBcfips = new Provider("BCFIPS", 1.0, "Dummy BCFIPS") {
			};

			Security.insertProviderAt(dummyBcfips, 1);

			// Should pass initially

			_fipsComplianceChecker.check();

			// Tamper with the provider post-initialization

			Security.removeProvider("BCFIPS");

			try {
				_fipsComplianceChecker.check();

				Assert.fail(
					"Expected RuntimeException due to tampered FIPS provider.");
			}
			catch (RuntimeException runtimeException) {
				String message = runtimeException.getMessage();

				Assert.assertTrue(
					message.contains(
						"BCFIPS must be the first security provider"));
			}
		}
		finally {

			// Restore original state

			Security.removeProvider("BCFIPS");

			if (originalProvider != null) {
				Security.insertProviderAt(originalProvider, 1);
			}
		}
	}

	@Rule
	public final LiferayUnitTestRule liferayUnitTestRule =
		new LiferayUnitTestRule();

	private FipsComplianceChecker _fipsComplianceChecker;

}