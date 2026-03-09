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
	id = "com.liferay.keymanager.provider.db.internal.configuration.DBCryptoVaultProviderConfiguration",
	localization = "content/Language",
	name = "db-crypto-vault-provider-configuration-name"
)
public interface DBCryptoVaultProviderConfiguration {

	@Meta.AD(
		deflt = "${keyRef:keystore:master}",
		description = "master-key-reference-description",
		name = "master-key-reference", required = false
	)
	public String masterKeyReference();

	@Meta.AD(deflt = "db", name = "provider-id", required = false)
	public String providerId();

}