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
	category = "security-tools",
	scope = ExtendedObjectClassDefinition.Scope.COMPANY
)
@Meta.OCD(
	id = "com.liferay.keymanager.provider.db.internal.configuration.DBSecretVaultProviderConfiguration",
	localization = "content/Language",
	name = "db-secret-vault-provider-configuration-name"
)
public interface DBSecretVaultProviderConfiguration {

	@Meta.AD(
		deflt = "AES/GCM/NoPadding;keySize=256;ivSize=12;gcmTag=128",
		description = "dek-cipher-spec-description", name = "dek-cipher-spec",
		required = false
	)
	public String dekCipherSpec();

	@Meta.AD(
		deflt = "${keyRef:keystore:master-key}",
		description = "master-key-reference-description",
		name = "master-key-reference"
	)
	public String masterKeyReference();

	@Meta.AD(
		deflt = "db", description = "provider-id-description",
		name = "provider-id"
	)
	public String providerId();

}