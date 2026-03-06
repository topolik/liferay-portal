/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.keymanager.internal;

import com.liferay.keymanager.KeyResolverService;
import com.liferay.keymanager.SecureConfigurationService;
import com.liferay.keymanager.SecureSecret;
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

/**
 * @author Tomas Polesovsky
 */
public class SecureConfigurationServiceImplTest {

	@ClassRule
	@Rule
	public static final LiferayUnitTestRule liferayUnitTestRule =
		LiferayUnitTestRule.INSTANCE;

	@Before
	public void setUp() {
		MockitoAnnotations.openMocks(this);
	}

	@Test
	public void testWrap() throws Exception {
		Dictionary<String, Object> properties = new Hashtable<>();

		properties.put("prop1", "value1");
		properties.put("prop2", "${keyref:provider/alias}");

		Mockito.when(
			_keyResolverService.isKeyReference("value1")
		).thenReturn(
			false
		);

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

		Mockito.when(
			_keyResolverService.resolveSecure("${keyref:provider/alias}")
		).thenReturn(
			new SecureSecret("secret-value".toCharArray())
		);

		SecureConfigurationService.SecureConfiguration secureConfig =
			_secureConfigurationServiceImpl.wrap(properties);

		Assert.assertEquals("value1", secureConfig.getString("prop1"));
		Assert.assertEquals("secret-value", secureConfig.getString("prop2"));

		try (SecureSecret secret = secureConfig.getSecret("prop2")) {
			Assert.assertArrayEquals(
				"secret-value".toCharArray(), secret.getChars());
		}
	}

	@Mock
	private KeyResolverService _keyResolverService;

	@InjectMocks
	private SecureConfigurationServiceImpl _secureConfigurationServiceImpl;

}