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

package com.liferay.multi.factor.authentication.provider.ip.address.web.internal.configuration;

import aQute.bnd.annotation.metatype.Meta;

import com.liferay.portal.configuration.metatype.annotations.ExtendedObjectClassDefinition;

/**
 * @author arthurchan35
 */
@ExtendedObjectClassDefinition(
	category = "multi-factor-authentication",
	factoryInstanceLabelAttribute = "name"
)
@Meta.OCD(
	factory = true,
	id = "com.liferay.multi.factor.authentication.provider.ip.address.web.internal.configuration.IPAddressConfiguration",
	localization = "content/Language", name = "ip-address-configuration-name"
)
public interface IPAddressConfiguration {

	@Meta.AD(deflt = "true", name = "enabled", required = false)
	public boolean enabled();

	@Meta.AD(
		deflt = "ip-address", description = "ip-address-name-description",
		name = "ip-address-name", required = false
	)
	public String name();

	/**
	 * Allowed IPs and its network masks, use add button to add new entry for
	 * different integration. Can be both IPv4 and IPv6.
	 *
	 * @return allowed IPs and their network masks.
	 */
	@Meta.AD(
		deflt = "127.0.0.1/255.0.0.0|127.0.0.1/255.0.0.0|127.0.0.1/255.0.0.0",
		description = "allowed-ips-with-masks-description",
		name = "allowed-ips-with-masks-name", required = false
	)
	public String[] allowedIPsWithMasks();

}