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

package com.liferay.multi.factor.authentication.integration.spi.verifier;

import java.util.Map;

/**
 * @author Tomas Polesovsky
 */
public interface MFAVerifierRegistry {

	default public MFAVerifier getMFAVerifier(String mfaVerifierClassName) {
		Map<String, MFAVerifier> registry = getRegistry();

		return registry.get(mfaVerifierClassName);
	}

	default public <T extends MFAVerifier> T getMFAVerifier(
		Class<T> mfaVerifierClass) {

		Map<String, MFAVerifier> registry = getRegistry();

		return (T)registry.get(mfaVerifierClass.getName());
	}

	public Map<String, MFAVerifier> getRegistry();
}
