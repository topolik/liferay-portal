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

package com.liferay.oauth2.provider.client.test;

import com.liferay.oauth2.provider.constants.GrantType;
import com.liferay.oauth2.provider.test.internal.TestApplication;
import com.liferay.oauth2.provider.test.internal.activator.BaseTestPreparatorBundleActivator;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.test.util.UserTestUtil;
import com.liferay.portal.kernel.util.HashMapDictionary;
import com.liferay.portal.kernel.util.PortalUtil;

import java.util.Arrays;
import java.util.Collections;

import javax.ws.rs.client.Invocation;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.Response;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.container.test.api.RunAsClient;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.shrinkwrap.api.Archive;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * @author Marta Medio
 */
@RunAsClient
@RunWith(Arquillian.class)
public class CORSApplicationClientTest extends BaseClientTestCase {

	@Deployment
	public static Archive<?> getDeployment() throws Exception {
		return BaseClientTestCase.getDeployment(
			CORSApplicationTestPreparatorBundleActivator.class);
	}

	@Test
	public void testApplication() throws Exception {
		WebTarget webTarget = getWebTarget("/no-cors");

		Invocation.Builder invocationBuilder = authorize(
			webTarget.request(), getToken("oauthTestApplication"));

		Response response = invocationBuilder.options();

		Assert.assertNull(
			response.getHeaderString("Access-Control-Allow-Origin"));

		response = invocationBuilder.get();

		Assert.assertNull(
			response.getHeaderString("Access-Control-Allow-Origin"));
		Assert.assertEquals(200, response.getStatus());
	}

	@Test
	public void testApplicationCORS() throws Exception {
		WebTarget webTarget = getWebTarget("/cors");

		Invocation.Builder invocationBuilder = authorize(
			webTarget.request(), getToken("oauthTestApplicationCORS"));

		invocationBuilder.header("Origin", _TEST_CORS_URI);

		Response response = invocationBuilder.options();

		Assert.assertEquals(
			_TEST_CORS_URI,
			response.getHeaderString("Access-Control-Allow-Origin"));

		response = invocationBuilder.get();

		Assert.assertEquals(
			_TEST_CORS_URI,
			response.getHeaderString("Access-Control-Allow-Origin"));
		Assert.assertEquals(200, response.getStatus());
		Assert.assertNotEquals(
			StringPool.BLANK, response.readEntity(String.class));
	}

	@Test
	public void testCORSPreflight() throws Exception {
		WebTarget webTarget = getWebTarget("/cors");

		Invocation.Builder invocationBuilder = authorize(
			webTarget.request(),
			getToken("oauthTestApplicationCORSWithoutCallback"));

		invocationBuilder.header(
			"Access-Control-Request-Method", "authentication");
		invocationBuilder.header("Origin", _TEST_CORS_URI);

		Response response = invocationBuilder.options();

		Assert.assertEquals(
			_TEST_CORS_URI,
			response.getHeaderString("Access-Control-Allow-Origin"));

		response = invocationBuilder.get();

		Assert.assertEquals(
			StringPool.BLANK, response.readEntity(String.class));
	}

	public static class CORSApplicationTestPreparatorBundleActivator
		extends BaseTestPreparatorBundleActivator {

		@Override
		protected void prepareTest() throws Exception {
			long defaultCompanyId = PortalUtil.getDefaultCompanyId();

			User user = UserTestUtil.getAdminUser(defaultCompanyId);

			registerJaxRsApplication(
				new TestApplication(), "cors", new HashMapDictionary<>());
			registerJaxRsApplication(
				new TestApplication(), "no-cors", new HashMapDictionary<>());

			createOAuth2Application(
				defaultCompanyId, user, "oauthTestApplication",
				Collections.singletonList("GET"));
			createOAuth2Application(
				defaultCompanyId, user, "oauthTestApplicationCORS",
				"oauthTestApplicationSecret",
				Arrays.asList(
					GrantType.CLIENT_CREDENTIALS,
					GrantType.RESOURCE_OWNER_PASSWORD),
				Collections.singletonList("GET"),
				Collections.singletonList(_TEST_CORS_URI));
			createOAuth2Application(
				defaultCompanyId, user,
				"oauthTestApplicationCORSWithoutCallback");
		}

	}

	private static final String _TEST_CORS_URI = "http://test-cors.com";

}