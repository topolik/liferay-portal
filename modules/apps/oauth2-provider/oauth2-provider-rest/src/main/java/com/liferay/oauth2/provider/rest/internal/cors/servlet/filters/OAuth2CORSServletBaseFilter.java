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

package com.liferay.oauth2.provider.rest.internal.cors.servlet.filters;

import com.liferay.oauth2.provider.rest.internal.cors.CORSSupport;

import java.io.IOException;

import java.util.Map;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * @author Carlos Sierra Andrés
 */
public abstract class OAuth2CORSServletBaseFilter implements Filter {

	@Override
	public void destroy() {
	}

	@Override
	public final void doFilter(
			ServletRequest servletRequest, ServletResponse servletResponse,
			FilterChain filterChain)
		throws IOException, ServletException {

		if (!isFilterEnabled(
				(HttpServletRequest)servletRequest,
				(HttpServletResponse)servletResponse)) {

			filterChain.doFilter(servletRequest, servletResponse);

			return;
		}

		try {
			processFilter(
				(HttpServletRequest)servletRequest,
				(HttpServletResponse)servletResponse, filterChain);
		}
		catch (Exception e) {
			throw new ServletException(e);
		}
	}

	@Override
	public void init(FilterConfig filterConfig) throws ServletException {
	}

	public abstract void processFilter(
			HttpServletRequest request, HttpServletResponse response,
			FilterChain filterChain)
		throws Exception;

	public void setHeaders(Map<String, String> headers) {
		corsSupport.setHeaders(headers);
	}

	protected boolean isFilterEnabled(
		HttpServletRequest request, HttpServletResponse response) {

		if (!corsSupport.isCORSRequest(request::getHeader)) {
			return false;
		}

		return true;
	}

	protected final CORSSupport corsSupport = new CORSSupport();

}