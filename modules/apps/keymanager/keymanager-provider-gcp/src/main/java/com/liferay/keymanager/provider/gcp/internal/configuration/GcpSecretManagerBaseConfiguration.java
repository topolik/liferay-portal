/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.keymanager.provider.gcp.internal.configuration;

import aQute.bnd.annotation.metatype.Meta;

/**
 * @author Tomas Polesovsky
 */
public interface GcpSecretManagerBaseConfiguration {

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

}