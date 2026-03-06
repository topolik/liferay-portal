/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.keymanager.provider.gcp.internal;

import com.liferay.keymanager.SecureSecret;
import com.liferay.keymanager.spi.BaseKeyProvider;

import java.util.List;
import java.util.Objects;

/**
 * @author Tomas Polesovsky
 */
public abstract class BaseGCPTokenKeyProvider extends BaseKeyProvider {

	@Override
	public boolean containsKey(String alias) throws Exception {
		return Objects.equals("access-token", alias);
	}

	@Override
	public void deleteKey(String alias) throws Exception {
		throw new UnsupportedOperationException("Token provider is read-only");
	}

	@Override
	public Capability[] getCapabilities() {
		return new Capability[] {Capability.READ, Capability.LIST};
	}

	@Override
	public List<String> listAliases() throws Exception {
		return List.of("access-token");
	}

	@Override
	public void storeKey(String alias, SecureSecret secret) throws Exception {
		throw new UnsupportedOperationException("Token provider is read-only");
	}

}
