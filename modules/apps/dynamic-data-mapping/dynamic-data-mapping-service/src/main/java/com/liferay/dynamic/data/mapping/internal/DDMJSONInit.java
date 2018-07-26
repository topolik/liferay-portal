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

package com.liferay.dynamic.data.mapping.internal;

import com.liferay.dynamic.data.mapping.model.DDMDataProviderInstance;
import com.liferay.portal.json.JoddJsonTransformer;
import com.liferay.portal.kernel.module.framework.ModuleServiceLifecycle;

import jodd.json.JoddJson;

import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Stian Sigvartsen
 */
@Component (immediate = true)
public class DDMJSONInit {

	@Activate
	public void activate() throws Exception {
		JoddJson.defaultSerializers.register(
			DDMDataProviderInstance.class,
			new JoddJsonTransformer(
				DDMDataProviderInstanceJSONTransformer.class.newInstance()));
	}

	@Reference (
		target = ModuleServiceLifecycle.PORTAL_INITIALIZED, unbind = "-"
	)
	public void setModuleServiceLifecycle(
		ModuleServiceLifecycle moduleServiceLifecycle) {
	}

}