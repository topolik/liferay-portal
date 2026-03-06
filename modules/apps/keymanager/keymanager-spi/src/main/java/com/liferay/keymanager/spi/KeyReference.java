/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.keymanager.spi;

import java.io.Serializable;
import java.util.Objects;

/**
 * @author Tomas Polesovsky
 */
public class KeyReference implements Serializable {

	public KeyReference(
		String provider, String alias, String version, String rawReference) {

		_provider = Objects.requireNonNull(provider, "Provider must not be null");
		_alias = Objects.requireNonNull(alias, "Alias must not be null");
		_version = version;
		_rawReference = Objects.requireNonNull(
			rawReference, "Raw reference must not be null");
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

	public String getVersion() {
		return _version;
	}

	private static final long serialVersionUID = 1L;

	private final String _alias;
	private final String _provider;
	private final String _rawReference;
	private final String _version;

}
