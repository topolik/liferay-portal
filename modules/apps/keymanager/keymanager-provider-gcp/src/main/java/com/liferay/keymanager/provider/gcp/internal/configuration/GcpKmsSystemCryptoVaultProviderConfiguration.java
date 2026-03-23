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
public interface GcpKmsSystemCryptoVaultProviderConfiguration {

	@Meta.AD(deflt = "false", name = "enabled", required = false)
	public boolean enabled();

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

	@Meta.AD(description = "key-ring-path-description", name = "key-ring-path")
	public String keyRingPath();

	@Meta.AD(
		deflt = "HSM", description = "new-key-protection-level-description",
		name = "new-key-protection-level", required = false
	)
	public String newKeyProtectionLevel();

	@Meta.AD(
		deflt = "7776000",
		description = "new-key-rotation-period-seconds-description",
		name = "new-key-rotation-period-seconds", required = false
	)
	public long newKeyRotationPeriodSeconds();

}