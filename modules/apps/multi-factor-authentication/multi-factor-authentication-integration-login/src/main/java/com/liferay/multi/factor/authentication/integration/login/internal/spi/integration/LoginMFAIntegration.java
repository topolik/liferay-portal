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

package com.liferay.multi.factor.authentication.integration.login.internal.spi.integration;

import com.liferay.multi.factor.authentication.spi.integration.MFAIntegration;
import com.liferay.multi.factor.authentication.spi.verifier.BrowserMFAVerifier;
import com.liferay.multi.factor.authentication.spi.verifier.HeadlessMFAVerifier;
import org.osgi.service.component.annotations.Component;

/**
 * @author Tomas Polesovsky
 */
@Component(
	service = MFAIntegration.class)
public class LoginMFAIntegration implements MFAIntegration {

	public static final String NAME = "login-web";

	@Override
	public String getName() {
		return NAME;
	}

	@Override
	public boolean supportsHeadless() {
		return true;
	}

	@Override
	public boolean supportsBrowser() {
		return true;
	}

}
