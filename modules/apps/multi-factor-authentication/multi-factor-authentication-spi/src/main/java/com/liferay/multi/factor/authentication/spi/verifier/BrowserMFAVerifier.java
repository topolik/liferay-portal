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

package com.liferay.multi.factor.authentication.spi.verifier;

import javax.portlet.ActionRequest;
import javax.portlet.ActionResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;

/**
 * @author Tomas Polesovsky
 */
public interface BrowserMFAVerifier {

	public void includeSetup(
			long userId, HttpServletRequest request,
			HttpServletResponse response)
		throws IOException;

	public void includeVerification(
			long userId, HttpServletRequest request,
			HttpServletResponse response)
		throws IOException;

	public boolean setup(ActionRequest actionRequest, long userId);

	public boolean verify(
		ActionRequest actionRequest, ActionResponse actionResponse,
		long userId);

}
