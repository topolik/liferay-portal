/**
 * Copyright (c) 2000-2012 Liferay, Inc. All rights reserved.
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

package com.liferay.portal.servlet.filters.xss;

import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.servlet.BufferCacheServletResponse;
import com.liferay.portal.kernel.servlet.ServletResponseUtil;
import com.liferay.portal.kernel.util.ContentTypes;
import com.liferay.portal.kernel.util.HttpUtil;
import com.liferay.portal.kernel.util.StringPool;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.servlet.filters.BasePortalFilter;
import com.liferay.portal.util.WebKeys;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * @author Tomas Polesovsky
 */
public class XSSFilter extends BasePortalFilter {

	public static final String SKIP_FILTER =
		XSSFilter.class.getName() + "SKIP_FILTER";

	@Override
	public void init(FilterConfig filterConfig) {
		super.init(filterConfig);
	}

	@Override
	public boolean isFilterEnabled(
		HttpServletRequest request, HttpServletResponse response) {

		if (isAlreadyFiltered(request)) {
			return false;
		}
		else {
			return true;
		}
	}

	protected String getContent(HttpServletRequest request, String content) {

		String xssToken = (String) request.getAttribute(WebKeys.XSS_TOKEN);
		content = content.replaceAll("<script ", "<script-invalid ");
		content = content.replaceAll("<script:xssToken="+xssToken+" ", "<script ");

		return content;
	}

	protected boolean isAlreadyFiltered(HttpServletRequest request) {
		if (request.getAttribute(SKIP_FILTER) != null) {
			return true;
		}
		else {
			return false;
		}
	}

	@Override
	protected void processFilter(
		HttpServletRequest request, HttpServletResponse response,
		FilterChain filterChain)
		throws Exception {

		request.setAttribute(SKIP_FILTER, Boolean.TRUE);
		request.setAttribute(WebKeys.XSS_TOKEN, String.valueOf(new Object().hashCode()));
		System.out.println("XSS TOKEN: " + request.getAttribute(WebKeys.XSS_TOKEN));

		if (_log.isDebugEnabled()) {
			String completeURL = HttpUtil.getCompleteURL(request);

			_log.debug("Filtering XSS for " + completeURL);
		}

		BufferCacheServletResponse bufferCacheServletResponse =
			new BufferCacheServletResponse(response);

		processFilter(
			XSSFilter.class, request, bufferCacheServletResponse,
			filterChain);

		String content = bufferCacheServletResponse.getString();

		String contentType = response.getContentType();

		if ((contentType != null) &&
			contentType.startsWith(ContentTypes.TEXT_HTML)) {

			content = getContent(request, content);
		}

		ServletResponseUtil.write(response, content);
	}

	private static Log _log = LogFactoryUtil.getLog(XSSFilter.class);
}
