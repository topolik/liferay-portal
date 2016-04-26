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

package com.liferay.portal.security.service.access.quota.persistence;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Properties;
import java.util.Set;

/**
 * @author Stian Sigvartsen
 */
public abstract class BaseIndexedSAQImpressionPersistence
	implements SAQImpressionPersistence {

	public abstract Iterator<SAQImpression> findImpressionsWithoutMetric(
		long companyId, String metric);

	public abstract Iterator<SAQImpression> findImpressionsMatchingMetric(
		long companyId, String metric, String value);

	public abstract Iterator<SAQImpression> findAllImpressions(long companyId);

	public Iterator<SAQImpression> findImpressions(
		long companyId, Properties callMetrics) {

		if (callMetrics.size() == 0) {
			return findAllImpressions(companyId);
		}

		Map<String, String> callMetricsMap = new HashMap<>();

		for (Map.Entry<Object, Object> entry : callMetrics.entrySet()) {
			callMetricsMap.put(
				(String)entry.getKey(), (String)entry.getValue());
		}

		return new MultiIterator(companyId, callMetricsMap);
	}

	private class MultiIterator implements Iterator<SAQImpression> {

		public MultiIterator(
			long companyId,
			Map<String, String> callMetrics) {

			_companyId = companyId;
			_metricsIterator = callMetrics.entrySet().iterator();
			_impressionsWithoutMetricIterator = null;
			_impressionsMatchingMetricIterator = null;
			_impressionKeys = new HashSet<>();
			_next = null;
		}

		@Override
		public boolean hasNext() {
			if (_next != null) {
				return true;
			}

			while (true) {
				if ((_impressionsMatchingMetricIterator != null) &&
					_prepareUniqueNextMatchingMetricImpression(
						_impressionsMatchingMetricIterator)) {

					return true;
				}
				else if ((_impressionsWithoutMetricIterator != null) &&
						 _prepareUniqueNextWithoutMetricImpression(
							 _impressionsWithoutMetricIterator)) {

					return true;
				}
				else {
					if (!_metricsIterator.hasNext()) {
						return false;
					}

					Map.Entry<String, String> callMetric =
						_metricsIterator.next();

					_impressionsMatchingMetricIterator =
						findImpressionsMatchingMetric(
							_companyId, callMetric.getKey(),
							callMetric.getValue());

					_impressionsWithoutMetricIterator =
						findImpressionsWithoutMetric(
							_companyId, callMetric.getKey());
				}
			}
		}

		@Override
		public SAQImpression next() {
			if (hasNext()) {
				SAQImpression next = _next;
				_next = null;
				return next;
			}
			else {
				return null;
			}
		}

		@Override
		public void remove() {
			throw new RuntimeException("Not implemented");
		}

		private boolean _prepareUniqueNext(
			Iterator<SAQImpression> metricIterator) {

			if (_next != null) {
				return true;
			}

			while (metricIterator.hasNext()) {
				SAQImpression next = metricIterator.next();

				if (_impressionKeys.add(next.getKey())) {
					_next = next;
					return true;
				}
			}

			return false;
		}

		private boolean _prepareUniqueNextMatchingMetricImpression(
			Iterator<SAQImpression> metricIterator) {

			if (_prepareUniqueNext(metricIterator)) {
				return true;
			}
			else {
				_impressionsMatchingMetricIterator = null;
				return false;
			}
		}

		private boolean _prepareUniqueNextWithoutMetricImpression(
			Iterator<SAQImpression> metricIterator) {

			if (_prepareUniqueNext(metricIterator)) {
				return true;
			}
			else {
				_impressionsWithoutMetricIterator = null;
				return false;
			}
		}

		private final long _companyId;
		private final Set<String> _impressionKeys;
		private Iterator<SAQImpression> _impressionsMatchingMetricIterator;
		private Iterator<SAQImpression> _impressionsWithoutMetricIterator;
		private final Iterator<Entry<String, String>> _metricsIterator;
		private SAQImpression _next;

	}

}