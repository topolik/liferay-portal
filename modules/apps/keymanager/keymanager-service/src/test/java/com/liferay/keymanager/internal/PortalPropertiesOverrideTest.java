/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.keymanager.internal;

import com.liferay.keymanager.KeyResolverService;
import com.liferay.portal.kernel.util.PropsUtil;
import com.liferay.portal.test.rule.LiferayUnitTestRule;

import java.util.Properties;

import org.junit.After;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;

import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

/**
 * @author Tomas Polesovsky
 */
public class PortalPropertiesOverrideTest {

	@ClassRule
	@Rule
	public static final LiferayUnitTestRule liferayUnitTestRule =
		LiferayUnitTestRule.INSTANCE;

	@Before
	public void setUp() {
		MockitoAnnotations.openMocks(this);

		_propsUtilMockedStatic = Mockito.mockStatic(PropsUtil.class);
	}

	@After
	public void tearDown() {
		_propsUtilMockedStatic.close();
	}

	@Test
	public void testActivate() throws Exception {
		Properties properties = new Properties();

		properties.setProperty("jdbc.password", "${keyref:provider/alias}");
		properties.setProperty("normal.prop", "normal-value");

		_propsUtilMockedStatic.when(
			PropsUtil::getProperties
		).thenReturn(
			properties
		);

		Mockito.when(
			_keyResolverService.isKeyReference("${keyref:provider/alias}")
		).thenReturn(
			true
		);

		Mockito.when(
			_keyResolverService.isKeyReference("normal-value")
		).thenReturn(
			false
		);

		Mockito.when(
			_keyResolverService.resolve("${keyref:provider/alias}")
		).thenReturn(
			"secret-value"
		);

		_portalPropertiesOverride.activate();

		_propsUtilMockedStatic.verify(
			() -> PropsUtil.set("jdbc.password", "secret-value"));
	}

	@Mock
	private KeyResolverService _keyResolverService;

	@InjectMocks
	private PortalPropertiesOverride _portalPropertiesOverride;

	private MockedStatic<PropsUtil> _propsUtilMockedStatic;

}