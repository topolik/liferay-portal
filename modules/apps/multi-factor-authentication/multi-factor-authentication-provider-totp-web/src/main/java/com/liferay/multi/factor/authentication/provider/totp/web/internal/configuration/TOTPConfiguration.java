/**
 * Copyright (c) 2000-present Liferay, Inc. All rights reserved.
 *
 * This library is free software; you can redistribute it and/or modify it under
 * the terms of the GNU Lesser General Public License as published by the Free
 * Software Foundation; either version 2.1 of the License, or (at your option)
 * any later version.
 *
 * This library is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more
 * details.
 */

package com.liferay.multi.factor.authentication.provider.totp.web.internal.configuration;

import aQute.bnd.annotation.metatype.Meta;
import com.liferay.portal.configuration.metatype.annotations.ExtendedObjectClassDefinition;

/**
 * @author Tomas Polesovsky
 */
@ExtendedObjectClassDefinition(
	category = "multi-factor-authentication",
	factoryInstanceLabelAttribute = "name"
)
@Meta.OCD(
	factory = true,
	id = "com.liferay.multi.factor.authentication.provider.totp.web.internal.configuration.TOTPConfiguration",
	localization = "content/Language",
	name = "totp-configuration-name"
)
public interface TOTPConfiguration {

	@Meta.AD(deflt = "true", name = "enabled", required = false)
	public boolean enabled();

	@Meta.AD(
		deflt = "time-based-one-time-password",
		description = "totp-name-description",
		name = "totp-name", required = false
	)
	public String name();

	@Meta.AD(
		deflt = "3000",
		description = "clock-skew-description",
		name = "clock-skew", required = false
	)
	public long clockSkew();

	@Meta.AD(
		deflt = "30000",
		description = "time-window-description",
		name = "time-window", required = false
	)
	public long timeWindow();

	@Meta.AD(
		deflt = "6",
		description = "digits-count-description",
		name = "digits-count", required = false
	)
	public int digitsCount();

	@Meta.AD(
		deflt = "HmacSHA1",
		description = "algorithm-description",
		name = "algorithm", required = false
	)
	public String algorithm();

	@Meta.AD(
		deflt = "20",
		description = "algorithm-key-size-description",
		name = "algorithm-key-size", required = false
	)
	public int algorithmKeySize();

	@Meta.AD(
		deflt = "X-2FA-Token",
		description = "headless-header-name-description",
		name = "headless-header-name", required = false
	)
	public String headlessHeaderName();

	@Meta.AD(
		deflt = "false",
		description = "force-user-setup-description",
		name = "force-user-setup", required = false
	)
	public boolean forceUserSetup();

	@Meta.AD(
		deflt = "-1",
		description = "validation-expiration-time-description",
		name = "validation-expiration-time", required = false
	)
	public long validationExpirationTime();
}
