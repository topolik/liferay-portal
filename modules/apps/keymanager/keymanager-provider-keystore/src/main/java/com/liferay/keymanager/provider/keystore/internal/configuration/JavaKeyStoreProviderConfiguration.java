/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.keymanager.provider.keystore.internal.configuration;

import aQute.bnd.annotation.metatype.Meta;

import org.osgi.service.metatype.annotations.ObjectClassDefinition;

/**
 * @author Tomas Polesovsky
 */
@ObjectClassDefinition(
	id = "com.liferay.keymanager.provider.keystore.internal.configuration.JavaKeyStoreProviderConfiguration",
	localization = "content/Language",
	name = "java-keystore-provider-configuration"
)
public @interface JavaKeyStoreProviderConfiguration {

	@Meta.AD(
		deflt = "true", description = "auto-create-description",
		name = "auto-create", required = false
	)
	public boolean autoCreate();

	@Meta.AD(
		deflt = "Java KeyStore Provider",
		description = "display-name-description", name = "display-name",
		required = false
	)
	public String displayName();

	@Meta.AD(
		deflt = "${keyref:gcp-sm/keystore-master-password}",
		description = "keystore-password-description",
		name = "keystore-password", required = false
	)
	public String keystorePassword();

	@Meta.AD(
		deflt = "${liferay.home}/data/keystore.p12",
		description = "keystore-path-description", name = "keystore-path",
		required = false
	)
	public String keystorePath();

	@Meta.AD(
		deflt = "PKCS12", description = "keystore-type-description",
		name = "keystore-type", required = false
	)
	public String keystoreType();

	@Meta.AD(
		deflt = "keystore", description = "provider-id-description",
		name = "provider-id", required = false
	)
	public String providerId();

}