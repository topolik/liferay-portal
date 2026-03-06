/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.keymanager.spi;

import com.liferay.keymanager.SecureSecret;

/**
 * @author Tomas Polesovsky
 */
public abstract class BaseKeyProvider implements KeyProvider {

	@Override
	public String getProviderId() {
		return _providerId;
	}

	@Override
	public boolean isAvailable() {
		return _available;
	}

	@Override
	public void storeKey(String alias, SecureSecret secret) throws Exception {
		throw new UnsupportedOperationException();
	}

	protected volatile boolean _available;
	protected boolean _enabled;
	protected String _providerId;

}
