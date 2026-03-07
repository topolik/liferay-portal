/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.keymanager.provider.os.internal.configuration;

import aQute.bnd.annotation.metatype.Meta;

import com.liferay.portal.configuration.metatype.annotations.ExtendedObjectClassDefinition;

/**
 * @author Tomas Polesovsky
 */
@ExtendedObjectClassDefinition(category = "security")
@Meta.OCD(
	id = "com.liferay.keymanager.provider.os.internal.configuration.EnvSecretVaultProviderConfiguration",
	localization = "content/Language",
	name = "env-secret-vault-provider-configuration"
)
public interface EnvSecretVaultProviderConfiguration {

	@Meta.AD(
		deflt = "LIFERAY_SECRET_", description = "env-variable-prefix-description",
		name = "env-variable-prefix", required = false
	)
	public String envVariablePrefix();

	@Meta.AD(
		deflt = "env", description = "provider-id-description",
		name = "provider-id", required = true
	)
	public String providerId();

}
