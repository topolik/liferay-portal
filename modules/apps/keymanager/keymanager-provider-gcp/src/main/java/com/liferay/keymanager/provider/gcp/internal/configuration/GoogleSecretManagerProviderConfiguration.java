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
	id = "com.liferay.keymanager.provider.gcp.internal.configuration.GoogleSecretManagerProviderConfiguration",
	localization = "content/Language",
	name = "google-secret-manager-provider-configuration"
)
public @interface GoogleSecretManagerProviderConfiguration {

	@Meta.AD(
		deflt = "Google Secret Manager Provider",
		description = "display-name-description", name = "display-name",
		required = false
	)
	public String displayName();

	@Meta.AD(
		deflt = "true", description = "enabled-description", name = "enabled",
		required = false
	)
	public boolean enabled();

	@Meta.AD(description = "project-id-description", name = "project-id")
	public String projectId();

	@Meta.AD(
		deflt = "gcp-sm", description = "provider-id-description",
		name = "provider-id", required = false
	)
	public String providerId();

}