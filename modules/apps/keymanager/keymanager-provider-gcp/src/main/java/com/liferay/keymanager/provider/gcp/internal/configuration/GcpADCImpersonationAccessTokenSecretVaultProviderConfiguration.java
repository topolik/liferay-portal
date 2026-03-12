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
	id = "com.liferay.keymanager.provider.gcp.internal.configuration.GcpADCImpersonationAccessTokenSecretVaultProviderConfiguration",
	localization = "content/Language",
	name = "gcp-adc-impersonation-access-token-secret-vault-provider-configuration-name"
)
public interface GcpADCImpersonationAccessTokenSecretVaultProviderConfiguration {

	@Meta.AD(required = false)
	public long companyId();

	@Meta.AD(
		deflt = "gcp-adc-impersonation",
		description = "provider-id-description", name = "provider-id"
	)
	public String providerId();

	@Meta.AD(
		deflt = "https://www.googleapis.com/auth/cloud-platform",
		description = "scopes-description", name = "scopes", required = false
	)
	public String[] scopes();

	@Meta.AD(
		description = "target-service-account-description",
		name = "target-service-account"
	)
	public String targetServiceAccount();

	@Meta.AD(
		deflt = "3600", description = "token-lifetime-seconds-description",
		name = "token-lifetime-seconds", required = false
	)
	public int tokenLifetimeSeconds();

}