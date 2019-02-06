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

package com.liferay.multi.factor.authentication.integration.login.web.internal.servlet.taglib;

import com.liferay.multi.factor.authentication.integration.login.web.spi.LoginWebMFAVerifier;
import com.liferay.multi.factor.authentication.integration.spi.verifier.MFAVerifierRegistry;
import com.liferay.portal.kernel.servlet.taglib.DynamicInclude;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.WebKeys;

import java.io.IOException;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Tomas Polesovsky
 */
@Component(service = DynamicInclude.class)
public class SetupMFADynamicInclude implements DynamicInclude {

	@Override
	public void include(
			HttpServletRequest request, HttpServletResponse response,
			String key)
		throws IOException {

		LoginWebMFAVerifier loginWebMFAVerifier =
			_mfaVerifierRegistry.getMFAVerifier(LoginWebMFAVerifier.class);

		if (loginWebMFAVerifier == null) {
			return;
		}

		ThemeDisplay themeDisplay = (ThemeDisplay)request.getAttribute(
			WebKeys.THEME_DISPLAY);

		loginWebMFAVerifier.includeSetup(
			themeDisplay.getUserId(), request, response);
	}

	@Override
	public void register(DynamicIncludeRegistry dynamicIncludeRegistry) {
		dynamicIncludeRegistry.register(
			"com.liferay.multi.factor.authentication.integration.login.web#" +
				"/setup_mfa.jsp");
	}

	@Reference
	private MFAVerifierRegistry _mfaVerifierRegistry;

	@Reference(
		target = "(osgi.web.symbolicname=com.liferay.multi.factor.authentication.integration.login.web)"
	)
	private ServletContext _servletContext;

}