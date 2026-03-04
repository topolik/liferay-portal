/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.keymanager.provider.keystore.internal.configuration;

import aQute.bnd.annotation.metatype.Meta;

import com.liferay.portal.configuration.metatype.annotations.ExtendedObjectClassDefinition;

/**
 * @author Tomas Polesovsky
 */
@ExtendedObjectClassDefinition(category = "key-manager")
@Meta.OCD(
	id = "com.liferay.keymanager.provider.keystore.internal.configuration.JavaKeyStoreProviderConfiguration",
	name = "Java KeyStore Provider Configuration"
)
public interface JavaKeyStoreProviderConfiguration {

	@Meta.AD(deflt = "keystore", name = "Provider ID", required = false)
	public String providerId();

	@Meta.AD(deflt = "Java KeyStore", name = "Display Name", required = false)
	public String displayName();

	@Meta.AD(
		deflt = "${liferay.home}/data/keymanager.jceks", name = "KeyStore Path",
		required = false
	)
	public String keystorePath();

	@Meta.AD(deflt = "JCEKS", name = "KeyStore Type", required = false)
	public String keystoreType();

	@Meta.AD(deflt = "", name = "KeyStore Password", required = false)
	public String keystorePassword();

	@Meta.AD(deflt = "true", name = "Auto-create KeyStore", required = false)
	public boolean autoCreate();

}
