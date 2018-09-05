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

package com.liferay.portal.servlet.filters.secure;

import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.util.PortalUtil;
import com.liferay.portal.servlet.filters.BasePortalFilter;

import java.util.Date;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

/**
 * @author Marta Medio
 */
public class SecuritySessionFilter extends BasePortalFilter {

	@Override
	protected void processFilter(
			HttpServletRequest request, HttpServletResponse response,
			FilterChain filterChain)
		throws Exception {

		try {
			if (_isPasswordChanged(request)) {
				HttpSession session = request.getSession(false);

				session.invalidate();

				response.sendRedirect(request.getRequestURI());
			}
			else {
				filterChain.doFilter(request, response);
			}
		}
		catch (PortalException pe) {
			throw new ServletException(pe);
		}
	}

	private boolean _isPasswordChanged(HttpServletRequest request)
		throws PortalException {

		HttpSession session = null;
		User user = null;

		try {
			session = request.getSession(false);
			user = PortalUtil.getUser(request);
		}
		catch (PortalException pe) {
			_log.error(pe, pe);

			throw pe;
		}

		if ((session != null) && (user != null)) {
			Date sessionCreationDate = new Date(session.getCreationTime());
			Date sessionModifiedPasswordDate = (Date)session.getAttribute(
				"DATE_PASSWORD_CHANGED");
			Date userModifiedPasswordDate = user.getPasswordModifiedDate();

			if (sessionModifiedPasswordDate != null) {
				if (sessionCreationDate.before(sessionModifiedPasswordDate)) {
					return true;
				}
			}
			else if ((userModifiedPasswordDate != null) &&
					 sessionCreationDate.before(userModifiedPasswordDate)) {

				return true;
			}
		}

		return false;
	}

	private static final Log _log = LogFactoryUtil.getLog(
		SecuritySessionFilter.class);

}