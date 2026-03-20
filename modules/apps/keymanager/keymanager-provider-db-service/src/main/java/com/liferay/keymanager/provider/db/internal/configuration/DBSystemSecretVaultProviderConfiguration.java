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
	category = "keymanager",
	scope = ExtendedObjectClassDefinition.Scope.SYSTEM
)
@Meta.OCD(
	id = "com.liferay.keymanager.provider.db.internal.configuration.DBSystemSecretVaultProviderConfiguration",
	localization = "content/Language",
	name = "db-system-secret-vault-provider-configuration-name"
)
public interface DBSystemSecretVaultProviderConfiguration {

	@Meta.AD(deflt = "false", name = "enabled", required = false)
	public boolean enabled();

	@Meta.AD(
		deflt = "AES/GCM/NoPadding; keySize=256; ivSize=12; gcmTag=128",
		description = "dek-cipher-spec-description", name = "dek-cipher-spec",
		required = false
	)
	public String dekCipherSpec();

	@Meta.AD(
		deflt = "${keyRef:*:db-vault-provider-master-kek}",
		description = "master-key-reference-description",
		name = "master-key-reference", required = false
	)
	public String masterKeyReference();

	@Meta.AD(
		deflt = "db-system", description = "db-secret-provider-id-description",
		name = "provider-id", required = false
	)
	public String providerId();

	@Meta.AD(deflt = "10", name = "priority", required = false)
	public int priority();

}