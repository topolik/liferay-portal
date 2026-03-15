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
	id = "com.liferay.keymanager.provider.gcp.internal.configuration.GcpKmsCryptoVaultProviderConfiguration",
	localization = "content/Language",
	name = "gcp-kms-crypto-vault-provider-configuration-name"
)
public interface GcpKmsCryptoVaultProviderConfiguration {

	@Meta.AD(required = false)
	public long companyId();

	@Meta.AD(
		description = "gcp-auth-key-reference-description",
		name = "gcp-auth-key-reference", required = false
	)
	public String gcpAuthKeyReference();

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

	@Meta.AD(
		deflt = "gcp-kms", description = "gcp-kms-provider-id-description",
		name = "provider-id"
	)
	public String providerId();

}