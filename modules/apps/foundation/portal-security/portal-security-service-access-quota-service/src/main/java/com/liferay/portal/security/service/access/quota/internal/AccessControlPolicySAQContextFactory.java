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

package com.liferay.portal.security.service.access.quota.internal;

import com.liferay.portal.kernel.security.auth.AccessControlContext;
import com.liferay.portal.security.service.access.quota.AccessControlPolicySAQMetricProvider;

import java.lang.reflect.Method;

/**
 * @author Stian Sigvartsen
 */
public class AccessControlPolicySAQContextFactory
	extends SAQContextFactory<AccessControlPolicySAQMetricProvider> {

	public SAQContext buildContext(
		final AccessControlContext accessControlContext, final Method method) {

		SAQMetricProviderAdapter<AccessControlPolicySAQMetricProvider>
			valueProvider =
				new SAQMetricProviderAdapter
					<AccessControlPolicySAQMetricProvider>() {

			@Override
			public String getMetricValue(
				AccessControlPolicySAQMetricProvider saqMetricProvider) {
					return saqMetricProvider.getMetricValue(
						accessControlContext, method);
			}

		};

		return super.buildContext(valueProvider);
	}

}