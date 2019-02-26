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

package com.liferay.multi.factor.authentication.integration.internal.configuration;

import aQute.bnd.annotation.metatype.Meta;
import com.liferay.portal.configuration.metatype.annotations.ExtendedObjectClassDefinition;

/**
 * @author Tomas Polesovsky
 */
@ExtendedObjectClassDefinition(
	category = "multi-factor-authentication",
	factoryInstanceLabelAttribute = "integrationName"
)
@Meta.OCD(
	factory = true,
	id = "com.liferay.multi.factor.authentication.integration.internal.configuration.MFAIntegrationVerificationConfiguration",
	localization = "content/Language",
	name = "mfa-integration-verification-configuration-name"
)
public interface MFAIntegrationVerificationConfiguration {

	@Meta.AD(
		description = "integration-name-description",
		name = "integration-name", required = false
	)
	public String integrationName();

	@Meta.AD(
		description = "verifier-names-description",
		name = "verifier-names", required = false
	)
	public String[] verifierNames();
}
