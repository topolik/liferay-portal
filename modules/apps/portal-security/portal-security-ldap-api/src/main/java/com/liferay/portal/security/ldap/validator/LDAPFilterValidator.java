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

package com.liferay.portal.security.ldap.validator;

import com.liferay.petra.string.StringBundler;

import java.util.Collections;

import org.osgi.annotation.versioning.ProviderType;

/**
 * @author Vilmos Papp
 */
@ProviderType
public interface LDAPFilterValidator {

	public boolean isValid(String filter);

	public default LDAPFilter validate(String filter)
		throws LDAPFilterException {

		if (!isValid(filter)) {
			throw new LDAPFilterException("Invalid filter " + filter);
		}

		if (filter == null) {
			return null;
		}

		return new LDAPFilter(
			new StringBundler(filter), Collections.emptyList());
	}

	public default LDAPFilter validate(String filter, String filterPropertyName)
		throws LDAPFilterException {

		if (!isValid(filter)) {
			throw new LDAPFilterException(
				StringBundler.concat(
					"Invalid filter ", filter, " defined by ",
					filterPropertyName));
		}

		if (filter == null) {
			return null;
		}

		return new LDAPFilter(
			new StringBundler(filter), Collections.emptyList());
	}

}