/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.keymanager.internal;

import com.liferay.keymanager.KeyResolverService;
import com.liferay.portal.test.rule.LiferayUnitTestRule;

import java.util.Dictionary;
import java.util.Hashtable;

import org.junit.Assert;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;

import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import org.osgi.framework.ServiceReference;

/**
 * @author Tomas Polesovsky
 */
public class ConfigurationInterceptorTest {

	@ClassRule
	@Rule
	public static final LiferayUnitTestRule liferayUnitTestRule =
		LiferayUnitTestRule.INSTANCE;

	@Before
	public void setUp() {
		MockitoAnnotations.openMocks(this);
	}

	@Test
	public void testModifyConfigurationWithString() throws Exception {
		Dictionary<String, Object> properties = new Hashtable<>();

		properties.put("password", "${keyref:provider/alias}");

		Mockito.when(
			_keyResolverService.isKeyReference("${keyref:provider/alias}")
		).thenReturn(
			true
		);

		Mockito.when(
			_keyResolverService.resolve("${keyref:provider/alias}")
		).thenReturn(
			"secret-value"
		);

		_configurationInterceptor.modifyConfiguration(
			Mockito.mock(ServiceReference.class), properties);

		Assert.assertEquals("secret-value", properties.get("password"));
	}

	@Test
	public void testModifyConfigurationWithStringArray() throws Exception {
		Dictionary<String, Object> properties = new Hashtable<>();

		properties.put(
			"passwords",
			new String[] {"${keyref:provider/alias1}", "normal-value"});

		Mockito.when(
			_keyResolverService.isKeyReference("${keyref:provider/alias1}")
		).thenReturn(
			true
		);

		Mockito.when(
			_keyResolverService.isKeyReference("normal-value")
		).thenReturn(
			false
		);

		Mockito.when(
			_keyResolverService.resolve("${keyref:provider/alias1}")
		).thenReturn(
			"secret-value1"
		);

		_configurationInterceptor.modifyConfiguration(
			Mockito.mock(ServiceReference.class), properties);

		String[] passwords = (String[])properties.get("passwords");

		Assert.assertEquals("secret-value1", passwords[0]);
		Assert.assertEquals("normal-value", passwords[1]);
	}

	@InjectMocks
	private ConfigurationInterceptor _configurationInterceptor;

	@Mock
	private KeyResolverService _keyResolverService;

}