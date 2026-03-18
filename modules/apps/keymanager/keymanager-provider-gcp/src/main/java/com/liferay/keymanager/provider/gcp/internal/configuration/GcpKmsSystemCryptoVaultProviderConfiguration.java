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
	id = "com.liferay.keymanager.provider.gcp.internal.configuration.GcpKmsSystemCryptoVaultProviderConfiguration",
	localization = "content/Language",
	name = "gcp-kms-system-crypto-vault-provider-configuration-name"
)
public interface GcpKmsSystemCryptoVaultProviderConfiguration
	extends GcpKmsBaseConfiguration {

	@Meta.AD(
		description = "gcp-auth-type-description",
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
		deflt = "gcp-kms-system",
		description = "gcp-kms-provider-id-description", name = "provider-id"
	)
	public String providerId();

}