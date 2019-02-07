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

package com.liferay.multi.factor.authentication.integration.auth.verifier.internal.spi.integration;

import com.liferay.multi.factor.authentication.spi.integration.MFAIntegration;
import com.liferay.multi.factor.authentication.spi.verifier.HeadlessMFAVerifier;
import com.liferay.multi.factor.authentication.spi.verifier.MFAVerifier;
import org.osgi.service.component.annotations.Component;

import java.util.Collections;
import java.util.Set;

/**
 * @author Tomas Polesovsky
 */
@Component(service = {MFAIntegration.class, AuthVerifierMFAIntegration.class})
public class AuthVerifierMFAIntegration
	implements MFAIntegration<HeadlessMFAVerifier> {

	public static String NAME = "auth-verifier";

	@Override
	public String getName() {
		return NAME;
	}

	@Override
	public Class<HeadlessMFAVerifier> getSupportedMFAVerifierClass() {
		return HeadlessMFAVerifier.class;
	}
}
