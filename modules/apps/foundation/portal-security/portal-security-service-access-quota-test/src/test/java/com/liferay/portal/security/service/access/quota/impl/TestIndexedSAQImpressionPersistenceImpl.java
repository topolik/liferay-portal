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

package com.liferay.portal.security.service.access.quota.impl;

import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.security.service.access.quota.metric.SAQMetricMatcher;
import com.liferay.portal.security.service.access.quota.persistence.BaseIndexedSAQImpressionPersistence;
import com.liferay.portal.security.service.access.quota.persistence.SAQImpression;

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
public class TestIndexedSAQImpressionPersistenceImpl
	extends BaseIndexedSAQImpressionPersistence {

	@Override
	public void createImpression(
		long companyId, Map<String, String> metrics,
		long expiryIntervalMillis) {

		expiryIntervalMillis += System.currentTimeMillis();

		SAQImpression impression = new TestSAQImpression(
			String.valueOf(_nextKey), metrics, expiryIntervalMillis);

		for (String metricName : metrics.keySet()) {
			List<SAQImpression> impressions = _getValueList(
				_getValueMap(_index, metricName), metrics.get(metricName));

			impressions.add(impression);
		}

		_indexed.add(impression);

		_nextKey++;
	}

	@Override
	public Iterator<SAQImpression> findAllImpressions(long companyId) {
		if (_log.isDebugEnabled()) {
			_log.debug("findAllImpressions()");
			_log.debug(" returning: " + _indexed);
		}

		return _indexed.iterator();
	}

	@Override
	public Iterator<String> findAllMetricValues(
		long companyId, String metricName) {

		return _getValueMap(_index, metricName).keySet().iterator();
	}

	@Override
	public Iterator<SAQImpression> findImpressionsMatchingMetric(
		long companyId, String metricName, SAQMetricMatcher metricMatcher) {

		if (_log.isDebugEnabled()) {
			_log.debug("findImpressionsMatchingMetric(" + metricName + ")");
		}

		Iterator<String> metricValuesIterator = findAllMetricValues(
			companyId, metricName);

		Set<SAQImpression> union = new HashSet<>();

		while (metricValuesIterator.hasNext()) {
			String value = metricValuesIterator.next();

			if (metricMatcher.matches(value)) {
				union.addAll(_index.get(metricName).get(value));
			}
		}

		if (_log.isDebugEnabled()) {
			_log.debug(" returning: " + union.toString());
		}

		return union.iterator();
	}

	@Override
	public int getImpressionsCount(long companyId, long expiryIntervalMillis) {
		long nowMillis = System.currentTimeMillis();

		int totalWeight = 0;

		for (SAQImpression impression : _indexed) {
			if ((impression.getCreatedMillis() + expiryIntervalMillis) >
					nowMillis) {

				totalWeight += impression.getWeight();
			}
		}

		return totalWeight;
	}

	private <X, Y> List<Y> _getValueList(Map<X, List<Y>> map, X key) {
		List<Y> valueList = map.get(key);

		if (valueList == null) {
			valueList = new LinkedList<>();
			map.put(key, valueList);
		}

		return valueList;
	}

	private <X, Y, Z> Map<Y, Z> _getValueMap(Map<X, Map<Y, Z>> map, X key) {
		Map<Y, Z> valueMap = map.get(key);

		if (valueMap == null) {
			valueMap = new HashMap<>();
			map.put(key, valueMap);
		}

		return valueMap;
	}

	private static final Log _log = LogFactoryUtil.getLog(
		TestIndexedSAQImpressionPersistenceImpl.class);

	private final Map<String, Map<String, List<SAQImpression>>> _index =
		new HashMap<>();
	private final List<SAQImpression> _indexed = new LinkedList<>();
	private long _nextKey = 0;

}