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

package com.liferay.multi.factor.authentication.integration.auto.login.internal.servlet.filter;

import com.liferay.multi.factor.authentication.integration.auto.login.internal.servlet.http.IgnoreAutoLoginFilterHttpServletRequestWrapper;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.servlet.BaseFilter;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;
import javax.servlet.http.HttpServletResponse;

import org.osgi.service.component.annotations.Component;

/**
 * @author Tomas Polesovsky
 */
@Component(
	immediate = true,
	property = {
		"after-filter=Auto Login Filter", "dispatcher=FORWARD",
		"dispatcher=REQUEST", "servlet-context-name=",
		"servlet-filter-name=MFA After Auto Login Filter",
		"url-pattern=/c/portal/login",
		"url-pattern=/c/portal/render_portlet",
		"url-pattern=/c/portal/saml/auth_redirect",
		"url-pattern=/c/portal/update_password",
		"url-pattern=/c/portal/update_reminder_query",
		"url-pattern=/documents/*",
		"url-pattern=/group/*",
		"url-pattern=/user/*",
		"url-pattern=/web/*",
		"url-pattern=/widget/*"
	},
	service = Filter.class
)
public class MFAAfterAutoLoginFilter extends BaseFilter {

	@Override
	protected Log getLog() {
		return _log;
	}

	@Override
	protected void processFilter(
		HttpServletRequest request, HttpServletResponse response,
		FilterChain filterChain) throws Exception {

		if (request instanceof IgnoreAutoLoginFilterHttpServletRequestWrapper) {
			HttpServletRequestWrapper httpServletRequestWrapper =
				(HttpServletRequestWrapper)request;

			request =
				(HttpServletRequest)httpServletRequestWrapper.getRequest();
		}

		super.processFilter(request, response, filterChain);
	}

	private static final Log _log = LogFactoryUtil.getLog(
		MFAAfterAutoLoginFilter.class);

}