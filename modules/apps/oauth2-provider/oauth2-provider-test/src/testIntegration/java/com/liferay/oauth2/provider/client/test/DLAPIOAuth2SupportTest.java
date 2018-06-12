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

import com.liferay.document.library.kernel.service.DLAppLocalService;
import com.liferay.document.library.kernel.util.DLUtil;
import com.liferay.oauth2.provider.constants.GrantType;
import com.liferay.oauth2.provider.test.internal.TestFileEntryUrlApplication;
import com.liferay.oauth2.provider.test.internal.activator.BaseTestPreparatorBundleActivator;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.repository.model.FileEntry;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.test.util.UserTestUtil;
import com.liferay.portal.kernel.util.FileUtil;
import com.liferay.portal.kernel.util.PortalUtil;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;

import java.net.URISyntaxException;
import java.net.URL;

import java.util.Arrays;
import java.util.Collections;
import java.util.Hashtable;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.Invocation;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.Response;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.container.test.api.RunAsClient;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.arquillian.test.api.ArquillianResource;
import org.jboss.shrinkwrap.api.Archive;
import org.jboss.shrinkwrap.api.asset.Asset;
import org.jboss.shrinkwrap.api.asset.FileAsset;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;

import org.osgi.framework.ServiceReference;

/**
 * @author Víctor Galán Grande
 */
@RunAsClient
@RunWith(Arquillian.class)
public class DLAPIOAuth2SupportTest extends BaseClientTestCase {

	@Deployment
	public static Archive<?> getDeployment() throws Exception {
		Archive<?> deployment = BaseClientTestCase.getDeployment(
			DLAPIOAuth2SupportTestPreparator.class);

		Asset asset = new FileAsset(
			new File("src/testIntegration/resources/test-image.jpg"));

		deployment.add(asset, "META-INF/test-image.jpg");

		return deployment;
	}

	@Test
	public void testSomething() throws Exception {
		String tokenString = getToken("oauthTestApplication");

		WebTarget webTarget = getWebTarget("file-entry-url");

		Invocation.Builder builder = authorize(
			webTarget.request(), tokenString);

		String fileEntryRelativeUrl = builder.get(String.class);

		WebTarget fileEntryTarget = _getRootWebTarget(fileEntryRelativeUrl);

		Invocation.Builder fileEntryBuilder = fileEntryTarget.request();

		Response response = fileEntryBuilder.get();

		Assert.assertNotEquals(200, response.getStatus());

		fileEntryBuilder = authorize(fileEntryBuilder, tokenString);

		response = fileEntryBuilder.get();

		Assert.assertEquals(200, response.getStatus());
		Assert.assertEquals(
			"image/jpeg", response.getHeaderString("Content-Type"));
	}

	public static class DLAPIOAuth2SupportTestPreparator
		extends BaseTestPreparatorBundleActivator {

		@Override
		protected void prepareTest() throws Exception {
			_saveTestFile();
		}

		private byte[] _getFileBytes() throws IOException {
			URL url =
				bundleContext.getBundle().getEntry("META-INF/test-image.jpg");

			InputStream inputStream = url.openStream();

			return FileUtil.getBytes(inputStream);
		}

		private void _saveTestFile() throws Exception {
			ServiceReference<DLAppLocalService> serviceReference =
				bundleContext.getServiceReference(DLAppLocalService.class);

			DLAppLocalService dlAppLocalService = bundleContext.getService(
				serviceReference);

			long defaultCompanyId = PortalUtil.getDefaultCompanyId();

			User user = UserTestUtil.getAdminUser(defaultCompanyId);

			long groupId = user.getGroupIds()[0];

			byte[] fileBytes = _getFileBytes();

			FileEntry fileEntry = dlAppLocalService.addFileEntry(
				user.getUserId(), groupId, 0, "test-file.jpg", "image/jpg",
				fileBytes, new ServiceContext());

			String donwloadUrlRelative = DLUtil.getPreviewURL(
				fileEntry, fileEntry.getFileVersion(), null, "", false, false);

			Hashtable<String, Object> properties = new Hashtable<>();

			properties.put(
				"osgi.jaxrs.name", TestFileEntryUrlApplication.class.getName());

			registerJaxRsApplication(
				new TestFileEntryUrlApplication(donwloadUrlRelative),
				"file-entry-url", properties);

			createOAuth2Application(
				defaultCompanyId, user, "oauthTestApplication",
				Collections.singletonList(GrantType.CLIENT_CREDENTIALS),
				Arrays.asList(new String[] {"GET", "download"}));

			autoCloseables.add(
				() -> dlAppLocalService.deleteFileEntry(
					fileEntry.getFileEntryId()));
		}

	}

	private WebTarget _getRootWebTarget(String path) throws URISyntaxException {
		Client client = getClient();

		WebTarget webTarget = client.target(_url.toURI());

		return webTarget.path(path);
	}

	@ArquillianResource
	private URL _url;

}