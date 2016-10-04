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

import com.liferay.exportimport.kernel.lar.MissingReferences;
import com.liferay.exportimport.kernel.model.ExportImportConfiguration;
import com.liferay.exportimport.kernel.service.StagingService;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.service.LayoutService;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import javax.ws.rs.ApplicationPath;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.FormParam;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Application;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Carlos Sierra Andrés
 */
@ApplicationPath("/api/liferay/do")
@Component(
	immediate = true, property = {"staging.remote=true"},
	service = Application.class
)
public class StagingRemoteEndpoint extends Application {

	@Path("/check-layouts")
	@POST
	public List<MissingLayoutRepr> checkLayouts(
		List<MissingLayoutRepr> layouts) {

		return layouts.stream().filter(l -> {
				try {
					_layoutService.getLayoutByUuidAndGroupId(
						l.getUuid(), l.getLayoutId(), l.isPrivateLayout());

					return false;
				}
				catch (PortalException pe) {
					return true;
				}
			}).collect(Collectors.toList());
	}

	@DELETE
	@Path("/staging-requests/{id}")
	public void cleanUpStagingRequest(@PathParam("id") long stagingRequestId)
		throws PortalException {

		_stagingService.cleanUpStagingRequest(stagingRequestId);
	}

	@Path("/staging-requests")
	@POST
	public long createStagingRequest(
			@FormParam("groupId") long groupId,
			@FormParam("checksum") String checksum)
		throws PortalException {

		return _stagingService.createStagingRequest(groupId, checksum);
	}

	@Override
	public Set<Class<?>> getClasses() {
		return new HashSet<>(
			Arrays.asList(
				ExportImporConfigurationMessageBodyWorker.class,
				MissingReferencesMessageBodyWriter.class));
	}

	@Override
	public Set<Object> getSingletons() {
		return Collections.singleton(this);
	}

	@Consumes({"application/json", "application/xml"})
	@Path("/staging-requests/{id}")
	@Produces({"application/json", "application/xml"})
	@PUT
	public MissingReferences publishStagingRequest(
			@PathParam("id") long stagingRequestId,
			ExportImportConfiguration exportImportConfiguration)
		throws PortalException {

		return _stagingService.publishStagingRequest(
			stagingRequestId, exportImportConfiguration);
	}

	@Path("/staging-requests/{id}/{fileName}")
	@PUT
	public void updateStagingRequest(
			@PathParam("id") long stagingRequestId,
			@PathParam("fileName") String fileName, byte[] bytes)
		throws PortalException {

		_stagingService.updateStagingRequest(stagingRequestId, fileName, bytes);
	}

	@Reference
	private LayoutService _layoutService;

	@Reference
	private StagingService _stagingService;

}