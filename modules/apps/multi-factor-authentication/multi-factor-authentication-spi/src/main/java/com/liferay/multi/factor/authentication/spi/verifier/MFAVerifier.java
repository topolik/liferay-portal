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

import javax.servlet.http.HttpServletRequest;

/**
 * @author Tomas Polesovsky
 */
public interface MFAVerifier {

	public String getName();

	public String getProviderName();

	public default boolean supportsHeadless() {
		return HeadlessMFAVerifier.class.isAssignableFrom(getClass());
	}

	public default boolean supportsBrowser() {
		return BrowserMFAVerifier.class.isAssignableFrom(getClass());
	}

	public default boolean supportsUserAccountSetup() {
		return UserAccountSetupMFAVerifier.class.isAssignableFrom(getClass());
	}

	public boolean isEnabled();

}