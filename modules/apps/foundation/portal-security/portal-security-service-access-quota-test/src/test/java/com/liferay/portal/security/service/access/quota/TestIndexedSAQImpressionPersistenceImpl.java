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

package com.liferay.portal.security.service.access.quota;

import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.security.service.access.quota.persistence.BaseIndexedSAQImpressionPersistence;
import com.liferay.portal.security.service.access.quota.persistence.SAQImpression;

import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Properties;

/**
 * @author Stian Sigvartsen
 */
public class TestIndexedSAQImpressionPersistenceImpl
	extends BaseIndexedSAQImpressionPersistence
	implements TestIndexedSAQImpressionPersistence {

	@Override
	public void createImpression(
		long companyId, Properties callMetrics,
		long expiryMillis) {

		indexImpression(
			new TestSAQImpression(String.valueOf(_nextKey), callMetrics));

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
	public Iterator<SAQImpression> findImpressionsMatchingMetric(
		long companyId, String metric, String value) {

		if (_log.isDebugEnabled()) {
			_log.debug(
				"findImpressionsMatchingMetric(" + metric + "=" + value + ")");
		}

		List<SAQImpression> impressions = _getValueList(
			_getValueMap(_index, metric), value);

		if (_log.isDebugEnabled()) {
			_log.debug(" returning: " + impressions.toString());
		}

		return impressions.iterator();
	}

	@Override
	public Iterator<SAQImpression> findImpressionsWithoutMetric(
		long companyId, String metric) {

		if (_log.isDebugEnabled()) {
			_log.debug("findImpressionsWithoutMetric(" + metric + ")");
		}

		List<SAQImpression> impressions = _getValueList(
			_getValueMap(_index, metric), null);

		if (_log.isDebugEnabled()) {
			_log.debug(" returning: " + impressions.toString());
		}

		return impressions.iterator();
	}

	public void indexImpression(SAQImpression impression) {
		Properties metrics = new Properties();
		impression.loadMetrics(metrics);

		for (String metric : _metrics) {
			List<SAQImpression> impressions = _getValueList(
				_getValueMap(_index, metric),
				metrics.getProperty(metric));

			impressions.add(impression);
		}

		_indexed.add(impression);
	}

	public void indexImpressions(SAQImpression[] impressions) {
		for (SAQImpression impression : impressions) {
			indexImpression(impression);
		}
	}

	public void initIndex(String[] metrics) {
		_index = new HashMap<>();

		for (String metric : metrics) {
			_index.put(
				metric,
				new HashMap<String, List<SAQImpression>>());
		}

		_indexed = new LinkedList<>();
		_metrics = metrics;
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

	private Map<String, Map<String, List<SAQImpression>>> _index;
	private List<SAQImpression> _indexed;
	private String[] _metrics;
	private long _nextKey = 0;

}