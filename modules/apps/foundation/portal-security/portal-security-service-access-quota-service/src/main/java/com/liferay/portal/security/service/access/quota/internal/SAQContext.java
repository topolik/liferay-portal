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

import com.liferay.portal.security.service.access.quota.SAQMetricProvider;
import com.liferay.portal.security.service.access.quota.ServiceAccessQuota;

import java.util.List;
import java.util.Map;

/**
 * @author Stian Sigvartsen
 */
public class SAQContext {

	public SAQContext(
		Map<String, String> metricsMap,
		List<ServiceAccessQuota> relevantServiceAccessQuotas,
		Map<String, SAQMetricProvider> saqMetricProviders, long nowMillis) {

		_saqMetrics = metricsMap;
		_relevantServiceAccessQuotas = relevantServiceAccessQuotas;
		_saqMetricProviders = saqMetricProviders;
		_nowMillis = nowMillis;
	}

	public Map<String, String> getMetricsMap() {
		return _saqMetrics;
	}

	public long getNowMillis() {
		return _nowMillis;
	}

	public Map<String, SAQMetricProvider> getSaqMetricProviders() {
		return _saqMetricProviders;
	}

	public List<ServiceAccessQuota> getServiceAccessQuotas() {
		return _relevantServiceAccessQuotas;
	}

	private final long _nowMillis;
	private final List<ServiceAccessQuota> _relevantServiceAccessQuotas;
	private final Map<String, SAQMetricProvider> _saqMetricProviders;
	private final Map<String, String> _saqMetrics;

}