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
import com.liferay.oauth2.provider.test.internal.TestAnnotatedApplication;
import com.liferay.oauth2.provider.test.internal.activator.configuration.BaseTestPreparatorBundleActivator;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.test.util.UserTestUtil;
import com.liferay.portal.kernel.util.PortalUtil;
import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.container.test.api.RunAsClient;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.shrinkwrap.api.Archive;
import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.osgi.framework.InvalidSyntaxException;
import org.osgi.framework.ServiceReference;
import org.osgi.util.tracker.ServiceTracker;
import org.osgi.util.tracker.ServiceTrackerCustomizer;

import java.util.Collections;
import java.util.Dictionary;
import java.util.Hashtable;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author Carlos Sierra Andrés
 */
@Ignore
@RunAsClient
@RunWith(Arquillian.class)
public class GrantAuthorizationCodeKillSwitchTest extends BaseClientTest {

	@Deployment()
	public static Archive<?> getDeployment() throws Exception {
		return BaseClientTest.getDeployment(
			GrantKillClientCredentialsSwitchTestPreparator.class);
	}

	@Test
	public void test() throws Exception {
		Assert.assertEquals(
			"unauthorized_client",
			getToken(
				"oauthTestApplicationCode", null,
				getAuthorizationCode("test@liferay.com", "test", null),
				this::parseError));

	}

	public static class GrantKillClientCredentialsSwitchTestPreparator
		extends BaseTestPreparatorBundleActivator {

		@Override
		protected void prepareTest() throws Exception {
			waitForFramework(
				() -> {
				Hashtable<String, Object> properties = new Hashtable<>();

				properties.put("oauth2.allow.authorization.code.grant", false);

				Runnable runnable = updateOrCreateConfiguration(
					"com.liferay.oauth2.provider.configuration." +
					"OAuth2ProviderConfiguration",
					properties);

					_autoCloseables.add(() -> waitForFramework(runnable));
				});

			long defaultCompanyId = PortalUtil.getDefaultCompanyId();

			User user = UserTestUtil.getAdminUser(defaultCompanyId);

			Dictionary<String, Object> properties = new Hashtable<>();

			properties.put("oauth2.scopechecker.type", "annotations");

			registerJaxRsApplication(
				new TestAnnotatedApplication(), "annotated", properties);

			createOauth2Application(
				defaultCompanyId, user, "oauthTestApplicationCode",
				Collections.singletonList(GrantType.AUTHORIZATION_CODE),
				Collections.singletonList("everything"));
		}

	}

}