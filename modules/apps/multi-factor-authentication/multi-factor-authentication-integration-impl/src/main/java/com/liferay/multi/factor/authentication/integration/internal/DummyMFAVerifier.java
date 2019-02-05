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

package com.liferay.multi.factor.authentication.integration.internal;

import com.liferay.multi.factor.authentication.integration.spi.verifier.StringMFAVerifier;
import org.osgi.service.component.annotations.Component;

/**
 * @author Tomas Polesovsky
 */
@Component(
	immediate = true,
	service = StringMFAVerifier.class
)
public class DummyMFAVerifier implements StringMFAVerifier {

	public boolean isVerified(
		String scope, String verifier, long userId) {

		if (userId == 0) {
			return false;
		}

		System.out.println("Verifying " + userId + " with " + verifier + " for " + scope);

		return true;
	}
}
