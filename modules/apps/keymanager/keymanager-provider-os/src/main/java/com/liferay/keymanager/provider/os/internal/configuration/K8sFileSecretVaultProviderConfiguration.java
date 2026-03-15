/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.keymanager.provider.os.internal.configuration;

import aQute.bnd.annotation.metatype.Meta;

import com.liferay.portal.configuration.metatype.annotations.ExtendedObjectClassDefinition;

/**
 * @author Tomas Polesovsky
 */
@ExtendedObjectClassDefinition(category = "keymanager")
@Meta.OCD(
	id = "com.liferay.keymanager.provider.os.internal.configuration.K8sFileSecretVaultProviderConfiguration",
	localization = "content/Language",
	name = "k8s-file-secret-vault-provider-configuration-name"
)
public interface K8sFileSecretVaultProviderConfiguration {

	@Meta.AD(
		deflt = "k8s-file", description = "k8s-file-provider-id-description",
		name = "provider-id", required = false
	)
	public String providerId();

	@Meta.AD(
		deflt = "/run/secrets", description = "secrets-directory-description",
		name = "secrets-directory", required = false
	)
	public String secretsDirectory();

}