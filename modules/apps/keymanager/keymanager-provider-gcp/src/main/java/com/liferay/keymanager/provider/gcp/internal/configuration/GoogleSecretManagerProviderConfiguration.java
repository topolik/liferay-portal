/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.keymanager.provider.gcp.internal.configuration;

import aQute.bnd.annotation.metatype.Meta;

import com.liferay.portal.configuration.metatype.annotations.ExtendedObjectClassDefinition;

/**
 * @author Tomas Polesovsky
 */
@ExtendedObjectClassDefinition(category = "key-manager")
@Meta.OCD(
	id = "com.liferay.keymanager.provider.gcp.internal.configuration.GoogleSecretManagerProviderConfiguration",
	name = "Google Secret Manager Provider Configuration"
)
public interface GoogleSecretManagerProviderConfiguration {

	@Meta.AD(deflt = "gcp-sm", name = "Provider ID", required = false)
	public String providerId();

	@Meta.AD(deflt = "Google Secret Manager", name = "Display Name", required = false)
	public String displayName();

	@Meta.AD(deflt = "", name = "Project ID", required = false)
	public String projectId();

	@Meta.AD(deflt = "false", name = "Enabled", required = false)
	public boolean enabled();

}
