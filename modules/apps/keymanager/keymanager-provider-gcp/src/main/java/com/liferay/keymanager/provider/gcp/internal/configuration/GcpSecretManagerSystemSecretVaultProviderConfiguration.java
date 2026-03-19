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
	scope = ExtendedObjectClassDefinition.Scope.SYSTEM
)
@Meta.OCD(
	id = "com.liferay.keymanager.provider.gcp.internal.configuration.GcpSecretManagerSystemSecretVaultProviderConfiguration",
	localization = "content/Language",
	name = "gcp-secret-manager-system-secret-vault-provider-configuration-name"
)
public interface GcpSecretManagerSystemSecretVaultProviderConfiguration {

	@Meta.AD(
		deflt = "adc", description = "gcp-auth-type-description",
		name = "gcp-auth-type",
		optionLabels = {"ADC", "Impersonation", "Service Account Key"},
		optionValues = {"adc", "impersonation", "sa-key"},
		required = false
	)
	public String gcpAuthType();

	@Meta.AD(
		description = "gcp-impersonated-service-account-system-description",
		name = "gcp-impersonated-service-account", required = false
	)
	public String gcpImpersonatedServiceAccount();

	@Meta.AD(
		description = "gcp-service-account-key-system-description",
		name = "gcp-service-account-key", required = false,
		type = Meta.Type.Password
	)
	public String gcpServiceAccountKey();

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
		deflt = "gcp-secrets-system",
		description = "gcp-secrets-provider-id-description", name = "provider-id"
	)
	public String providerId();

}