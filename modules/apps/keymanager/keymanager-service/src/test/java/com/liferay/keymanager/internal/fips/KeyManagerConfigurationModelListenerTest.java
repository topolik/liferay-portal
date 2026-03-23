/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.keymanager.internal.fips;

import com.liferay.keymanager.spi.fips.FipsReport;
import com.liferay.keymanager.spi.fips.FipsValidator;
import com.liferay.osgi.service.tracker.collections.map.ServiceTrackerMap;
import com.liferay.portal.configuration.persistence.listener.ConfigurationModelListenerException;

import java.lang.reflect.Field;

import java.util.Dictionary;
import java.util.Hashtable;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

/**
 * @author Tomas Polesovsky
 */
public class KeyManagerConfigurationModelListenerTest {

	@Before
	public void setUp() {
		MockitoAnnotations.openMocks(this);

		_listener = new KeyManagerConfigurationModelListener();

		_injectField(_listener, "_fipsComplianceChecker", _fipsComplianceChecker);
		_injectField(_listener, "_serviceTrackerMap", _serviceTrackerMap);
	}

	@Test
	public void testSchrodingersFipsConfigurationGuardBypass() throws Exception {
		String pid = "com.liferay.keymanager.provider.gcp.internal.configuration.GcpKmsCompanyCryptoVaultProviderConfiguration";
		Dictionary<String, Object> properties = new Hashtable<>();
		properties.put("newKeyProtectionLevel", "SOFTWARE");

		// Simulate FIPS Enforced = true
		Mockito.when(
			_fipsComplianceChecker.isFipsEnforced()
		).thenReturn(
			true
		);

		// The validator realizes this violates the mandate
		Mockito.when(
			_serviceTrackerMap.getService(pid)
		).thenReturn(
			_fipsValidator
		);

		Mockito.when(
			_fipsValidator.validate(Mockito.anyMap())
		).thenReturn(
			FipsReport.nonCompliant("SOFTWARE protection level is not allowed when FIPS is enforced.")
		);

		try {
			// Try to save the non-compliant configuration
			_listener.onBeforeSave(pid, properties);
			Assert.fail("Expected ConfigurationModelListenerException to block the save.");
		}
		catch (ConfigurationModelListenerException e) {
			Assert.assertTrue(e.getMessage().contains("SOFTWARE protection level is not allowed"));
		}
	}

	private void _injectField(Object target, String fieldName, Object value) {
		try {
			Field field = target.getClass().getDeclaredField(fieldName);

			field.setAccessible(true);
			field.set(target, value);
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	@Mock
	private FipsComplianceChecker _fipsComplianceChecker;

	@Mock
	private FipsValidator _fipsValidator;

	private KeyManagerConfigurationModelListener _listener;

	@Mock
	private ServiceTrackerMap<String, FipsValidator> _serviceTrackerMap;

}