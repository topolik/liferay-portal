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

package com.liferay.multi.factor.authentication.api;

import com.liferay.multi.factor.authentication.spi.integration.MFAIntegration;
import com.liferay.multi.factor.authentication.spi.verifier.MFAVerifier;

import java.util.List;

/**
 * @author Tomas Polesovsky
 */
public interface MFARegistry {

	public MFAIntegration<?> getMFAIntegration(String name);

	public List<MFAIntegration<?>> getMFAIntegrations();

	public <T extends MFAVerifier> List<T> getMFAVerifier(Class<T> mfaVerifierClass);

	public List<MFAVerifier> getMFAVerifiers();

	public MFAVerifier getMFAVerifier(String mfaIntegrationName);

	public <T extends MFAVerifier> T getMFAVerifier(
		MFAIntegration<T> mfaIntegration);
}