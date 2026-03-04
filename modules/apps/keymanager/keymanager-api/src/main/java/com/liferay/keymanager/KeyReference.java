/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.keymanager;

import java.io.Serializable;

import java.util.Objects;

/**
 * Represents a parsed key reference.
 *
 * <p>
 * A key reference string looks like: ${keyref:provider/alias}
 * </p>
 *
 * @author Liferay
 */
public class KeyReference implements Serializable {

	public KeyReference(String provider, String alias, String rawReference) {
		_provider = Objects.requireNonNull(provider, "Provider must not be null");
		_alias = Objects.requireNonNull(alias, "Alias must not be null");
		_rawReference = Objects.requireNonNull(
			rawReference, "Raw reference must not be null");
	}

	@Override
	public boolean equals(Object object) {
		if (this == object) {
			return true;
		}

		if ((object == null) || (getClass() != object.getClass())) {
			return false;
		}

		KeyReference that = (KeyReference)object;

		if (_provider.equals(that._provider) && _alias.equals(that._alias)) {
			return true;
		}

		return false;
	}

	public String getAlias() {
		return _alias;
	}

	public String getProvider() {
		return _provider;
	}

	public String getRawReference() {
		return _rawReference;
	}

	@Override
	public int hashCode() {
		return Objects.hash(_provider, _alias);
	}

	@Override
	public String toString() {
		return _rawReference;
	}

	private static final long serialVersionUID = 1L;

	private final String _alias;
	private final String _provider;
	private final String _rawReference;

}