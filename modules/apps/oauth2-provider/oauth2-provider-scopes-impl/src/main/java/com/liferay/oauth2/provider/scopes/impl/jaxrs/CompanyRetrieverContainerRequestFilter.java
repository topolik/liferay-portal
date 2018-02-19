/**
 * Copyright (c) 2000-present Liferay, Inc. All rights reserved.
 * <p>
 * This library is free software; you can redistribute it and/or modify it under
 * the terms of the GNU Lesser General Public License as published by the Free
 * Software Foundation; either version 2.1 of the License, or (at your option)
 * any later version.
 * <p>
 * This library is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more
 * details.
 */

package com.liferay.oauth2.provider.scopes.impl.jaxrs;

import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.model.Company;
import com.liferay.portal.kernel.util.PortalUtil;

import java.io.IOException;
import java.util.function.Consumer;

import javax.servlet.http.HttpServletRequest;

import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerRequestFilter;
import javax.ws.rs.core.Context;

public class CompanyRetrieverContainerRequestFilter
	implements ContainerRequestFilter {

	public CompanyRetrieverContainerRequestFilter(Consumer<Company> consumer) {
		_consumer = consumer;
	}

	@Override
	public void filter(ContainerRequestContext requestContext)
		throws IOException {

		try {
			_consumer.accept(PortalUtil.getCompany(_httpServletRequest));
		}
		catch (PortalException pe) {
			pe.printStackTrace();
		}
	}

	private Consumer<Company> _consumer;

	@Context
	private HttpServletRequest _httpServletRequest;

}