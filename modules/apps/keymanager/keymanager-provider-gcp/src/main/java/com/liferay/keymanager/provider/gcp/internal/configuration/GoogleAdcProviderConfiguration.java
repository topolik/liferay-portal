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
	id = "com.liferay.keymanager.provider.gcp.internal.configuration.GoogleAdcProviderConfiguration",
	localization = "content/Language",
	name = "google-adc-provider-configuration"
)
public @interface GoogleAdcProviderConfiguration {

	@Meta.AD(
		deflt = "https://www.googleapis.com/auth/cloud-platform",
		description = "default-scopes-description", name = "default-scopes",
		required = false
	)
	public String[] defaultScopes();

	@Meta.AD(
		deflt = "Google ADC Identity Provider",
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
		deflt = "gcp-adc", description = "provider-id-description",
		name = "provider-id", required = false
	)
	public String providerId();

}