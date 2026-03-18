/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.keymanager.provider.gcp.internal.configuration;

import aQute.bnd.annotation.metatype.Meta;

import com.liferay.portal.configuration.metatype.annotations.ExtendedObjectClassDefinition;

/**
 * @author Tomas Polesovsky
 */
@ExtendedObjectClassDefinition(
	category = "keymanager",
	scope = ExtendedObjectClassDefinition.Scope.COMPANY
)
@Meta.OCD(
	id = "com.liferay.keymanager.provider.gcp.internal.configuration.GcpSecretManagerSecretVaultProviderConfiguration",
	localization = "content/Language",
	name = "gcp-secret-manager-secret-vault-provider-configuration-name"
)
public interface GcpSecretManagerSecretVaultProviderConfiguration
	extends GcpSecretManagerBaseConfiguration {

	@Meta.AD(required = false)
	public long companyId();

	@Meta.AD(
		description = "gcp-service-account-key-description",
		name = "gcp-service-account-key", required = false,
		type = Meta.Type.Password
	)
	public String gcpServiceAccountKey();

	@Meta.AD(
		deflt = "gcp-secrets",
		description = "gcp-secrets-provider-id-description",
		name = "provider-id"
	)
	public String providerId();

}