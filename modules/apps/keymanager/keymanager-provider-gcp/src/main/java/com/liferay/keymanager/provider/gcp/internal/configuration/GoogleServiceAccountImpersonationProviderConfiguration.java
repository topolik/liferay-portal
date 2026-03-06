/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.keymanager.provider.gcp.internal.configuration;

import aQute.bnd.annotation.metatype.Meta;

import org.osgi.service.metatype.annotations.ObjectClassDefinition;

/**
 * @author Tomas Polesovsky
 */
@ObjectClassDefinition(
	id = "com.liferay.keymanager.provider.gcp.internal.configuration.GoogleServiceAccountImpersonationProviderConfiguration",
	localization = "content/Language",
	name = "google-service-account-impersonation-provider-configuration"
)
public @interface GoogleServiceAccountImpersonationProviderConfiguration {

	@Meta.AD(
		deflt = "https://www.googleapis.com/auth/cloud-platform",
		description = "delegated-scopes-description", name = "delegated-scopes",
		required = false
	)
	public String[] delegatedScopes();

	@Meta.AD(
		deflt = "Google Service Account Impersonation Provider",
		description = "display-name-description", name = "display-name",
		required = false
	)
	public String displayName();

	@Meta.AD(
		deflt = "true", description = "enabled-description", name = "enabled",
		required = false
	)
	public boolean enabled();

	@Meta.AD(
		deflt = "gcp-impersonation", description = "provider-id-description",
		name = "provider-id", required = false
	)
	public String providerId();

	@Meta.AD(
		description = "target-service-account-email-description",
		name = "target-service-account-email"
	)
	public String targetServiceAccountEmail();

}