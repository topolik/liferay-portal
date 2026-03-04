/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.keymanager.upgrade.internal.configuration;

import aQute.bnd.annotation.metatype.Meta;

import com.liferay.portal.configuration.metatype.annotations.ExtendedObjectClassDefinition;

/**
 * @author Tomas Polesovsky
 */
@ExtendedObjectClassDefinition(category = "key-manager")
@Meta.OCD(
	id = "com.liferay.keymanager.upgrade.internal.configuration.KeyManagerUpgradeStepRegistratorConfiguration",
	name = "Key Manager Upgrade Configuration"
)
public interface KeyManagerUpgradeStepRegistratorConfiguration {

	@Meta.AD(deflt = "false", name = "Auto-migrate on Upgrade", required = false)
	public boolean autoMigrate();

	@Meta.AD(deflt = "DRY_RUN", name = "Migration Strategy", required = false)
	public String migrationStrategy();

	@Meta.AD(deflt = "keystore", name = "Target Provider ID", required = false)
	public String targetProviderId();

}
