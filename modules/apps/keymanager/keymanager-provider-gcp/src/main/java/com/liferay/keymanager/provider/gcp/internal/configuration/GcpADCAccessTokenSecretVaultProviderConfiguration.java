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
	id = "com.liferay.keymanager.provider.gcp.internal.configuration.GcpADCAccessTokenSecretVaultProviderConfiguration",
	localization = "content/Language",
	name = "gcp-adc-access-token-secret-vault-provider-configuration-name"
)
public interface GcpADCAccessTokenSecretVaultProviderConfiguration {

	@Meta.AD(required = false)
	public long companyId();

	@Meta.AD(
		deflt = "gcp-adc", description = "provider-id-description",
		name = "provider-id"
	)
	public String providerId();

	@Meta.AD(
		deflt = "https://www.googleapis.com/auth/cloud-platform",
		description = "scopes-description", name = "scopes", required = false
	)
	public String[] scopes();

}