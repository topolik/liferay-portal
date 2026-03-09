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
	category = "security", scope = ExtendedObjectClassDefinition.Scope.COMPANY
)
@Meta.OCD(
	id = "com.liferay.keymanager.provider.gcp.internal.configuration.GcpKmsCryptoVaultProviderConfiguration",
	localization = "content/Language",
	name = "gcp-kms-crypto-vault-provider-configuration"
)
public interface GcpKmsCryptoVaultProviderConfiguration {

	@Meta.AD(
		deflt = "gcp-kms", description = "provider-id-description",
		name = "provider-id", required = true
	)
	public String providerId();

	@Meta.AD(
		description = "project-id-description", name = "project-id",
		required = true
	)
	public String projectId();

	@Meta.AD(
		deflt = "global", description = "location-id-description",
		name = "location-id", required = true
	)
	public String locationId();

	@Meta.AD(
		description = "key-ring-id-description", name = "key-ring-id",
		required = true
	)
	public String keyRingId();

}
