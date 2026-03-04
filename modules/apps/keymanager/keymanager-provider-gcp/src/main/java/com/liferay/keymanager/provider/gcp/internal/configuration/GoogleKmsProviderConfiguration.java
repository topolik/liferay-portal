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
	id = "com.liferay.keymanager.provider.gcp.internal.configuration.GoogleKmsProviderConfiguration",
	name = "Google Cloud KMS Provider Configuration"
)
public interface GoogleKmsProviderConfiguration {

	@Meta.AD(deflt = "gcp-kms", name = "Provider ID", required = false)
	public String providerId();

	@Meta.AD(deflt = "Google Cloud KMS", name = "Display Name", required = false)
	public String displayName();

	@Meta.AD(deflt = "", name = "Default Project ID", required = false)
	public String projectId();

	@Meta.AD(deflt = "global", name = "Default Location", required = false)
	public String location();

	@Meta.AD(deflt = "liferay", name = "Default Key Ring", required = false)
	public String keyRing();

	@Meta.AD(deflt = "config-key", name = "Default Crypto Key", required = false)
	public String cryptoKey();

	@Meta.AD(deflt = "false", name = "Enabled", required = false)
	public boolean enabled();

}
