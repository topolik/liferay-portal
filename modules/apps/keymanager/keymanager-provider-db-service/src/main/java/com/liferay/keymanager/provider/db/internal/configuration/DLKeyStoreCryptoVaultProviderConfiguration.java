/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.keymanager.provider.db.internal.configuration;

import aQute.bnd.annotation.metatype.Meta;

import com.liferay.portal.configuration.metatype.annotations.ExtendedObjectClassDefinition;

/**
 * @author Tomas Polesovsky
 */
@ExtendedObjectClassDefinition(
	category = "security", scope = ExtendedObjectClassDefinition.Scope.COMPANY
)
@Meta.OCD(
	id = "com.liferay.keymanager.provider.db.internal.configuration.DLKeyStoreCryptoVaultProviderConfiguration",
	localization = "content/Language",
	name = "dl-keystore-crypto-vault-provider-configuration"
)
public interface DLKeyStoreCryptoVaultProviderConfiguration {

	@Meta.AD(
		deflt = "dl-keystore", description = "provider-id-description",
		name = "provider-id", required = true
	)
	public String providerId();

	@Meta.AD(
		deflt = "password", description = "keystore-password-description",
		name = "keystore-password", required = true
	)
	public String keystorePassword();

	@Meta.AD(
		deflt = "PKCS12", description = "keystore-type-description",
		name = "keystore-type", required = true
	)
	public String keystoreType();

}
