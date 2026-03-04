/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.keymanager.provider.vault.internal.configuration;

import aQute.bnd.annotation.metatype.Meta;

import com.liferay.portal.configuration.metatype.annotations.ExtendedObjectClassDefinition;

/**
 * @author Tomas Polesovsky
 */
@ExtendedObjectClassDefinition(category = "key-manager")
@Meta.OCD(
	id = "com.liferay.keymanager.provider.vault.internal.configuration.VaultKeyProviderConfiguration",
	name = "HashiCorp Vault Provider Configuration"
)
public interface VaultKeyProviderConfiguration {

	@Meta.AD(deflt = "vault", name = "Provider ID", required = false)
	public String providerId();

	@Meta.AD(deflt = "HashiCorp Vault", name = "Display Name", required = false)
	public String displayName();

	@Meta.AD(
		deflt = "http://127.0.0.1:8200", name = "Vault URL", required = false
	)
	public String vaultAddress();

	@Meta.AD(deflt = "", name = "Vault Token", required = false)
	public String vaultToken();

	@Meta.AD(deflt = "secret", name = "KV Engine Path", required = false)
	public String enginePath();

	@Meta.AD(deflt = "2", name = "KV Engine Version", required = false)
	public int engineVersion();

	@Meta.AD(deflt = "false", name = "Enabled", required = false)
	public boolean enabled();

}
