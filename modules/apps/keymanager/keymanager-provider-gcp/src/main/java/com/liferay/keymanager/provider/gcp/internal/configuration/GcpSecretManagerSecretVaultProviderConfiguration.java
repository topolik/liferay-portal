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
	category = "security-tools",
	scope = ExtendedObjectClassDefinition.Scope.COMPANY
)
@Meta.OCD(
	id = "com.liferay.keymanager.provider.gcp.internal.configuration.GcpSecretManagerSecretVaultProviderConfiguration",
	localization = "content/Language",
	name = "gcp-secret-manager-secret-vault-provider-configuration-name"
)
public interface GcpSecretManagerSecretVaultProviderConfiguration {

	@Meta.AD(
		description = "gcp-auth-key-reference-description",
		name = "gcp-auth-key-reference", required = false
	)
	public String gcpAuthKeyReference();

	@Meta.AD(
		description = "kms-key-name-description", name = "kms-key-name",
		required = false
	)
	public String kmsKeyName();

	@Meta.AD(
		description = "locations-description", name = "locations",
		required = false
	)
	public String[] locations();

	@Meta.AD(description = "project-id-description", name = "project-id")
	public String projectId();

	@Meta.AD(
		deflt = "gcp-secrets", description = "provider-id-description",
		name = "provider-id"
	)
	public String providerId();

}