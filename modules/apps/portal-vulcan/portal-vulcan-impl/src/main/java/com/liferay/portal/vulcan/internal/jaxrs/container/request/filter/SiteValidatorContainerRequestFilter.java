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

package com.liferay.portal.vulcan.internal.jaxrs.container.request.filter;

import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.service.GroupLocalService;
import com.liferay.portal.kernel.util.GetterUtil;

import javax.ws.rs.NotFoundException;
import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerRequestFilter;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.UriInfo;
import javax.ws.rs.ext.Provider;

/**
 * @author Víctor Galán
 */
@Provider
public class SiteValidatorContainerRequestFilter
	implements ContainerRequestFilter {

	public SiteValidatorContainerRequestFilter(
		GroupLocalService groupLocalService) {

		_groupLocalService = groupLocalService;
	}

	@Override
	public void filter(ContainerRequestContext requestContext) {
		MultivaluedMap<String, String> multivaluedMap =
			_uriInfo.getPathParameters();

		String siteId = multivaluedMap.getFirst("siteId");

		if (siteId == null) {
			return;
		}

		if (!_isValidSite(siteId)) {
			throw new NotFoundException(
				"Unable to get a valid site with ID " + siteId);
		}
	}

	private boolean _isValidSite(String siteId) {
		Group group = _groupLocalService.fetchGroup(GetterUtil.getLong(siteId));

		if ((group != null) && group.isSite()) {
			return true;
		}

		return false;
	}

	private final GroupLocalService _groupLocalService;

	@Context
	private UriInfo _uriInfo;

}