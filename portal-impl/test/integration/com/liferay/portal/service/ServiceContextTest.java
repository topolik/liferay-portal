/**
 * Copyright (c) 2000-present Liferay, Inc. All rights reserved.
 *
 * This library is free software; you can redistribute it and/or modify it under
 * the terms of the GNU Lesser General Public License as published by the Free
 * Software Foundation; either version 2.1 of the License, or (at your option)
 * any later version.
 *
 * This library is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more
 * details.
 */

package com.liferay.portal.service;

import com.liferay.portal.kernel.json.JSONFactoryUtil;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.util.PropsKeys;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;
import com.liferay.registry.Registry;
import com.liferay.registry.RegistryUtil;
import com.liferay.registry.ServiceRegistration;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;

import org.powermock.api.mockito.PowerMockito;

/**
 * @author Michael C. Han
 */
public class ServiceContextTest extends PowerMockito {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new LiferayIntegrationTestRule();

	@Before
	public void setUp() {
		Registry registry = RegistryUtil.getRegistry();

		_serviceRegistration = registry.registerService(
			Object.class, new Object(),
			new HashMap<>(
				Collections.singletonMap(
					PropsKeys.JSON_DESERIALIZATION_WHITELIST_CLASS_NAMES,
					ServiceContext.class.getName())));
	}

	@After
	public void tearDown() {
		_serviceRegistration.unregister();
	}

	@Test
	public void testJSONSerialization() {
		ServiceContext serviceContext = new ServiceContext();

		serviceContext.setAttribute("TestName", "TestValue");

		Map<String, String> headers = new HashMap<>();

		headers.put("TestHeaderName", "TestHeaderValue");

		serviceContext.setHeaders(headers);

		serviceContext.setRequest(mock(HttpServletRequest.class));

		String json = JSONFactoryUtil.serialize(serviceContext);

		ServiceContext deserializedServiceContext =
			(ServiceContext)JSONFactoryUtil.deserialize(json);

		Assert.assertEquals(
			deserializedServiceContext.getAttributes(),
			serviceContext.getAttributes());
		Assert.assertNull(deserializedServiceContext.getHeaders());
		Assert.assertNull(deserializedServiceContext.getRequest());
	}

	private ServiceRegistration<Object> _serviceRegistration;

}