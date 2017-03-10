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
import com.liferay.portal.security.service.access.quota.ServiceAccessQuota.SAQMetricConfig;
import com.liferay.portal.security.service.access.quota.metric.SAQContextMatcher;
import com.liferay.portal.security.service.access.quota.persistence.SAQImpression;
import com.liferay.portal.security.service.access.quota.persistence.SAQImpressionConsumer;
import com.liferay.portal.security.service.access.quota.persistence.SAQImpressionProvider;

import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * @author Stian Sigvartsen
 */
public class SAQProcessor<T extends SAQMetricProvider>
	implements SAQContextMatcher {

	public SAQProcessor(SAQContext saqContext) {
		_saqContext = saqContext;

		List<ServiceAccessQuota> relevantServiceAccessQuotas =
			saqContext.getServiceAccessQuotas();

		_serviceAccessQuotasCount = new HashMap<>(
			relevantServiceAccessQuotas.size());

		for (Iterator<ServiceAccessQuota> it =
			relevantServiceAccessQuotas.iterator();
			it.hasNext();) {

			_serviceAccessQuotasCount.put(it.next(), 0);
		}

		_matchedServiceAccessQuotas = new HashSet<>(
			relevantServiceAccessQuotas.size());

		_saqMetricConfigs = new HashMap<>(saqContext.getMetricsMap().size());

		_relevantServiceAccessQuotasWithoutMetrics = new LinkedList<>();

		for (ServiceAccessQuota serviceAccessQuota :
				relevantServiceAccessQuotas) {

			List<SAQMetricConfig> quotaSaqMetricConfigs =
				serviceAccessQuota.getSaqMetricConfigs();

			if (quotaSaqMetricConfigs.isEmpty()) {
				_relevantServiceAccessQuotasWithoutMetrics.add(
					serviceAccessQuota);
			}
			else {
				for (SAQMetricConfig saqMetricConfig : quotaSaqMetricConfigs) {
					List<SAQMetricConfig> saqMetricConfigs =
						_saqMetricConfigs.get(saqMetricConfig.getMetricName());

					if (saqMetricConfigs == null) {
						saqMetricConfigs = new LinkedList<>();

						_saqMetricConfigs.put(
							saqMetricConfig.getMetricName(), saqMetricConfigs);
					}

					if (!saqMetricConfigs.contains(saqMetricConfig)) {
						saqMetricConfigs.add(saqMetricConfig);
					}
				}
			}
		}

		// Sort relevantQuotasWithoutMetrics by their max property
		// Will then fail fast because quotas with lower max are
		// checked and reported first

		Collections.sort(
			_relevantServiceAccessQuotasWithoutMetrics,
			new Comparator<ServiceAccessQuota>() {

				@Override
				public int compare(
					ServiceAccessQuota o1, ServiceAccessQuota o2) {

					return o1.getMax() - o2.getMax();
				};

			});
	}

	@Override
	public Set<String> getMetricNames() {
		return _saqContext.getMetricsMap().keySet();
	}

	public Set<ServiceAccessQuota> matches(SAQImpression saqImpression) {
		_matchedServiceAccessQuotas.clear();

		for (ServiceAccessQuota serviceAccessQuota :
				_saqContext.getServiceAccessQuotas()) {

			if ((
					saqImpression.getCreatedMillis() +
						serviceAccessQuota.getIntervalMillis()) <
							_saqContext.getNowMillis()) {

				continue;
			}

			Map<String, String> saqImpressionMetrics =
				saqImpression.getMetrics();

			boolean allMetricsMatch = true;

			for (SAQMetricConfig saqMetricConfig :
					serviceAccessQuota.getSaqMetricConfigs()) {

				String saqImpressionMetricValue = saqImpressionMetrics.get(
					saqMetricConfig.getMetricName());

				if (saqImpressionMetricValue == null) {
					allMetricsMatch = false;

					break;
				}

				else {
					if (saqMetricConfig.getPattern() != null) {
						SAQMetricProvider saqMetricProvider =
							_saqContext.getSaqMetricProviders().get(
								saqMetricConfig.getMetricName());

						if (!saqMetricProvider.matches(
								saqImpressionMetricValue,
								saqMetricConfig.getPattern())) {

							allMetricsMatch = false;

							break;
						}
					}
					else {
						String saqContextMetricValue =
							_saqContext.getMetricsMap().get(
								saqMetricConfig.getMetricName());

						if (!saqContextMetricValue.equals(
								saqImpressionMetricValue)) {

							allMetricsMatch = false;
							break;
						}
					}
				}
			}

			if (allMetricsMatch) {
				_matchedServiceAccessQuotas.add(serviceAccessQuota);
			}
		}

		return _matchedServiceAccessQuotas;
	}

	@Override
	public boolean matches(String metricName, String metricValue) {
		if (metricValue == null) {
			return false;
		}

		SAQMetricProvider saqMetricProvider =
			_saqContext.getSaqMetricProviders().get(metricName);
		List<SAQMetricConfig> saqMetricConfigs = _saqMetricConfigs.get(
			metricName);

		for (SAQMetricConfig saqMetricConfig : saqMetricConfigs) {
			if (saqMetricConfig.getPattern() != null) {
				if (saqMetricProvider.matches(
						metricValue, saqMetricConfig.getPattern())) {

					return true;
				}
			}
			else {
				String saqContextMetricValue =
					_saqContext.getMetricsMap().get(metricName);

				if (saqContextMetricValue.equals(metricValue)) {
					return true;
				}
			}
		}

		return false;
	}

	public ProcessingResult process(
		long companyId, SAQImpressionProvider saqImpressionProvider) {

		final ProcessingResult processingResult = new ProcessingResult();

		// Fail fast when quotas are configured with no metrics

		if (_relevantServiceAccessQuotasWithoutMetrics.size() > 0) {
			for (ServiceAccessQuota serviceAccessQuota :
					_relevantServiceAccessQuotasWithoutMetrics) {

				int saqImpressionsCount =
					saqImpressionProvider.getSAQImpressionsCount(
						companyId, serviceAccessQuota.getIntervalMillis());

				if (saqImpressionsCount >= serviceAccessQuota.getMax()) {
					processingResult.setStatus(
						ProcessingResult.Status.BREACHED_QUOTA);
					processingResult.setBreachedQuota(serviceAccessQuota);

					return processingResult;
				}
			}
		}

		final Set<SAQImpression> processedSaqImpressions = new HashSet<>();

		saqImpressionProvider.populateSAQImpressions(
			companyId, this,
			new SAQImpressionConsumer() {

				@Override
				public SAQImpressionConsumer.Status consume(
					SAQImpression saqImpression) {

					if (processedSaqImpressions.contains(saqImpression)) {
						return SAQImpressionConsumer.Status.HUNGRY;
					}

					processedSaqImpressions.add(saqImpression);

					Set<ServiceAccessQuota> matchedServiceAccessQuotas =
						matches(saqImpression);

					for (ServiceAccessQuota serviceAccessQuota :
							matchedServiceAccessQuotas) {

						int count = _serviceAccessQuotasCount.get(
							serviceAccessQuota);

						count = count + saqImpression.getWeight();

						if (count < serviceAccessQuota.getMax()) {
							_serviceAccessQuotasCount.put(
								serviceAccessQuota, count);

							continue;
						}

						// If through impression matching a quota max is hit,
						// then adding the impression for the current request
						// later will breach it, so fail fast now

						processingResult.setBreachedQuota(serviceAccessQuota);
						processingResult.setStatus(
							ProcessingResult.Status.BREACHED_QUOTA);

						return SAQImpressionConsumer.Status.SATISFIED;
					}

					return SAQImpressionConsumer.Status.HUNGRY;
				}

			});

		return processingResult;
	}

	public static class ProcessingResult {

		public ProcessingResult() {
			_status = ProcessingResult.Status.NO_BREACHED_QUOTA;
			_breachedQuota = null;
		}

		public ServiceAccessQuota getBreachedQuota() {
			return _breachedQuota;
		}

		public Status getStatus() {
			return _status;
		}

		public void setBreachedQuota(ServiceAccessQuota breachedQuota) {
			_breachedQuota = breachedQuota;
		}

		public void setStatus(ProcessingResult.Status status) {
			_status = status;
		}

		public enum Status {

			NO_BREACHED_QUOTA, BREACHED_QUOTA

		}

		private ServiceAccessQuota _breachedQuota;
		private ProcessingResult.Status _status;

	}

	// Recycled instance for optimization only

	private final Set<ServiceAccessQuota> _matchedServiceAccessQuotas;

	private final List<ServiceAccessQuota>
		_relevantServiceAccessQuotasWithoutMetrics;
	private final SAQContext _saqContext;
	private final Map<String, List<SAQMetricConfig>> _saqMetricConfigs;
	private final HashMap<ServiceAccessQuota, Integer>
		_serviceAccessQuotasCount;

}