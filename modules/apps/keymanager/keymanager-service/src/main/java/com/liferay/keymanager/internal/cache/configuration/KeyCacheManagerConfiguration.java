/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.keymanager.internal.cache.configuration;

import aQute.bnd.annotation.metatype.Meta;

import com.liferay.keymanager.constants.KeyManagerConstants;
import com.liferay.portal.configuration.metatype.annotations.ExtendedObjectClassDefinition;

/**
 * @author Tomas Polesovsky
 */
@ExtendedObjectClassDefinition(category = "key-manager")
@Meta.OCD(
	id = "com.liferay.keymanager.internal.cache.configuration.KeyCacheManagerConfiguration",
	name = "Key Manager Cache Configuration"
)
public interface KeyCacheManagerConfiguration {

	@Meta.AD(deflt = "true", name = "Cache Enabled", required = false)
	public boolean cacheEnabled();

	@Meta.AD(
		deflt = KeyManagerConstants.DEFAULT_CACHE_TTL_SECONDS + "",
		name = "Cache TTL (seconds)", required = false
	)
	public long cacheTtlSeconds();

	@Meta.AD(
		deflt = KeyManagerConstants.DEFAULT_CACHE_MAX_SIZE + "",
		name = "Max Cache Size", required = false
	)
	public int maxCacheSize();

}
