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

package com.liferay.staging.remote.api;

import com.liferay.exportimport.kernel.model.ExportImportConfiguration;
import com.liferay.portal.kernel.security.auth.HttpPrincipal;
import com.liferay.portal.kernel.util.Base64;

import java.util.List;
import java.util.function.Function;

import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.Invocation;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.Form;
import javax.ws.rs.core.GenericType;
import javax.ws.rs.core.MediaType;

/**
 * @author Carlos Sierra Andrés
 */
public class StagingRemoteClient {

	public static List<MissingLayoutRepr> checkLayouts(
		HttpPrincipal httpPrincipal, List<MissingLayoutRepr> parentLayouts) {

		return createAuthorizedRequest(
			httpPrincipal,
			wt -> wt.path("check-layouts").
				request(MediaType.APPLICATION_JSON_TYPE)).post(
				Entity.entity(parentLayouts, MediaType.APPLICATION_JSON_TYPE),
				new GenericType<List<MissingLayoutRepr>>() {});
	}

	public static void cleanUpStagingRequest(
		HttpPrincipal httpPrincipal, long stagingRequestId) {

		createAuthorizedRequest(
			httpPrincipal,
			wt -> wt.path("staging-requests").
				path(Long.toString(stagingRequestId)).request()).delete();
	}

	public static long createStagingRequest(
		HttpPrincipal httpPrincipal, long targetGroupId, String checksum) {

		Form form = new Form();

		form.param("groupId", Long.toString(targetGroupId));
		form.param("checksum", checksum);

		return createAuthorizedRequest(
			httpPrincipal,
			wt -> wt.path("staging-requests").
				request(MediaType.APPLICATION_FORM_URLENCODED_TYPE)).post(
				Entity.entity(form, MediaType.APPLICATION_FORM_URLENCODED_TYPE),
				long.class);
	}

	public static MissingReferencesRepr publishStagingRequest(
		HttpPrincipal httpPrincipal, long stagingRequestId,
		ExportImportConfiguration exportImportConfiguration) {

		return createAuthorizedRequest(
			httpPrincipal,
			wt -> wt.path("staging-requests").
				path(Long.toString(stagingRequestId)).
				request(MediaType.APPLICATION_JSON_TYPE)).put(
				Entity.entity(
					exportImportConfiguration, MediaType.APPLICATION_JSON_TYPE),
				MissingReferencesRepr.class);
	}

	private static Invocation.Builder createAuthorizedRequest(
		HttpPrincipal httpPrincipal,
		Function<WebTarget, Invocation.Builder> f) {

		WebTarget target = _clientBuilder.newClient().
			target(httpPrincipal.getUrl());

		Invocation.Builder builder = f.apply(target);

		String authHeaderValue =
			httpPrincipal.getLogin() + ":" + httpPrincipal.getPassword();

		return builder.header(
			"Authorization", Base64.encode(authHeaderValue.getBytes()));
	}

	private static final ClientBuilder _clientBuilder;

	static {
		_clientBuilder = ClientBuilder.newBuilder();
		
		_clientBuilder.register(
			ExportImporConfigurationMessageBodyWorker.class);
		_clientBuilder.register(MissingReferencesMessageBodyWriter.class);
	}

}