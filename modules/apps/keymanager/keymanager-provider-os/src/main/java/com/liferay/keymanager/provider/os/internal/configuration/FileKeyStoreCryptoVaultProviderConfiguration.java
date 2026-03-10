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
@ExtendedObjectClassDefinition(category = "keymanager")
@Meta.OCD(
	id = "com.liferay.keymanager.provider.os.internal.configuration.FileKeyStoreCryptoVaultProviderConfiguration",
	localization = "content/Language",
	name = "file-keystore-crypto-vault-provider-configuration-name"
)
public interface FileKeyStoreCryptoVaultProviderConfiguration {

	@Meta.AD(
		deflt = "true", description = "auto-create-description",
		name = "auto-create", required = false
	)
	public boolean autoCreate();

	@Meta.AD(
		deflt = "password", description = "keystore-password-description",
		name = "keystore-password"
	)
	public String keystorePassword();

	@Meta.AD(
		deflt = "${liferay.home}/data/keystore.p12",
		description = "keystore-path-description", name = "keystore-path"
	)
	public String keystorePath();

	@Meta.AD(
		deflt = "PKCS12", description = "keystore-type-description",
		name = "keystore-type"
	)
	public String keystoreType();

	@Meta.AD(
		deflt = "file-keystore", description = "provider-id-description",
		name = "provider-id"
	)
	public String providerId();

}