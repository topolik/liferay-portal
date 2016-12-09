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

import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.util.StringBundler;
import com.liferay.portal.security.service.access.quota.SAQMetricProvider;
import com.liferay.portal.security.service.access.quota.ServiceAccessQuota;
import com.liferay.portal.security.service.access.quota.ServiceAccessQuota.SAQMetricConfig;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * @author Stian Sigvartsen
 */
public class SAQContextFactory<T extends SAQMetricProvider> {

	public SAQContextFactory() {
		_serviceAccessQuotas = new ArrayList<>();
		_saqMetricProviders = new HashMap<>();
	}

	public SAQContextFactory(
		List<ServiceAccessQuota> serviceAccessQuotas,
		Map<String, T> saqMetricProviders) {

		_serviceAccessQuotas = serviceAccessQuotas;
		_saqMetricProviders = saqMetricProviders;
	}

	public SAQContext buildContext(SAQMetricProviderAdapter<T> valueProvider) {
		List<ServiceAccessQuota> relevantServiceAccessQuotas =
			new LinkedList<>();

		Map<String, String> saqMetrics = new HashMap<>(
			_saqMetricProviders.size());

		Set<String> requiredSaqMetrics = new HashSet<>();

		Map<String, SAQMetricProvider> relevantSaqMetricProviders =
			new HashMap<>(_saqMetricProviders.size());

		Set<String> missingSaqMetricProviders = null;

		for (ServiceAccessQuota serviceAccessQuota : _serviceAccessQuotas) {
			boolean metricPatternsMatched = true;
			boolean missingSaqMetricProvider = false;

			for (SAQMetricConfig saqMetricConfig :
					serviceAccessQuota.getMetricConfigs()) {

				T saqMetricProvider = _saqMetricProviders.get(
					saqMetricConfig.getMetricName());

				if (saqMetricProvider == null) {
					if (missingSaqMetricProviders == null) {
						missingSaqMetricProviders = new HashSet<>();
					}

					missingSaqMetricProviders.add(
						saqMetricConfig.getMetricName());

					missingSaqMetricProvider = true;
					break;
				}

				String metricValue;

				if (saqMetrics.containsKey(saqMetricConfig.getMetricName())) {
					metricValue = saqMetrics.get(
						saqMetricConfig.getMetricName());
				}
				else {
					metricValue = valueProvider.getMetricValue(
						saqMetricProvider);

					saqMetrics.put(
						saqMetricConfig.getMetricName(), metricValue);
				}

				if (saqMetricConfig.getPattern() != null) {
					if (!saqMetricProvider.matches(
							metricValue, saqMetricConfig.getPattern())) {

						metricPatternsMatched = false;
						break;
					}
				}
			}

			if (missingSaqMetricProvider) {
				break;
			}

			if (metricPatternsMatched) {
				relevantServiceAccessQuotas.add(serviceAccessQuota);

				for (SAQMetricConfig saqMetricConfig :
						serviceAccessQuota.getMetricConfigs()) {

					requiredSaqMetrics.add(saqMetricConfig.getMetricName());
				}
			}
		}

		if ((missingSaqMetricProviders != null) && _log.isWarnEnabled() &&
			(_lastLog < System.currentTimeMillis() - 60000)) {

			StringBundler sb = new StringBundler(4);

			sb.append("No SAQMetricProvider available for the metric(s) ");
			sb.append(missingSaqMetricProviders.toString());
			sb.append(". Referencing Service Access Quotas are ");
			sb.append("disabled, please review system configuration");

			_log.warn(sb.toString());

			_lastLog = System.currentTimeMillis();
		}

		// Remove metrics that are not relevant
		// because of failed pattern matching

		Set<String> keySet = saqMetrics.keySet();

		keySet.retainAll(requiredSaqMetrics);

		for (String metricName : keySet) {
			relevantSaqMetricProviders.put(
				metricName, _saqMetricProviders.get(metricName));
		}

		long nowMillis = System.currentTimeMillis();

		return new SAQContext(
			saqMetrics, relevantServiceAccessQuotas, relevantSaqMetricProviders,
			nowMillis);
	}

	public Map<String, T> getMetricProviders() {
		return _saqMetricProviders;
	}

	public List<ServiceAccessQuota> getServiceAccessQuotas() {
		return _serviceAccessQuotas;
	}

	public void setMetricProviders(Map<String, T> saqMetricProviders) {
		_saqMetricProviders = saqMetricProviders;
	}

	public void setServiceAccessQuotas(
		List<ServiceAccessQuota> serviceAccessQuotas) {

		_serviceAccessQuotas = serviceAccessQuotas;
	}

	public interface SAQMetricProviderAdapter<T extends SAQMetricProvider> {

		public String getMetricValue(T saqMetricProvider);

	}

	private static final Log _log = LogFactoryUtil.getLog(
		SAQContextFactory.class);

	private static long _lastLog;

	private Map<String, T> _saqMetricProviders;
	private List<ServiceAccessQuota> _serviceAccessQuotas;

}