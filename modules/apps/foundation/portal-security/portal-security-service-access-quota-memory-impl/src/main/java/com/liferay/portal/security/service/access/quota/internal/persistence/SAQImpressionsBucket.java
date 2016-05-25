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

package com.liferay.portal.security.service.access.quota.internal.persistence;

import java.io.Serializable;

import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Properties;
import java.util.Set;

/**
 * @author Stian Sigvartsen
 */
public class SAQImpressionsBucket implements Serializable {

	public SAQImpressionsBucket(long startMillis, long expiryMillis) {
		_index = new HashMap<>();
		_indexed = new HashSet<>();
		_startMillis = startMillis;
		_expiryMillis = expiryMillis;
	}

	public long getExpiryMillis() {
		return _expiryMillis;
	}

	public Set<AggregateSAQImpression> getImpressions(
		String metric, String value) {

		// If the metric hasn't yet been indexed and
		// request is for impressions without the metric
		// then return all impressions in this bucket

		// This edge case can happen for the current basket
		// when a new SAQImpressionProvider is deployed and
		// impressions are requested before the basket expires

		// Likewise, if no metric is provided then return all indexed
		// impressions.

		if ((metric == null) ||
			((value == null) && !_index.containsKey(metric))) {

			return _indexed;
		}

		return _getValueSet(_getValueMap(_index, metric), value);
	}

	public long getStartMillis() {
		return _startMillis;
	}

	public void indexImpression(AggregateSAQImpression impression) {
		Properties metrics = new Properties();
		impression.loadMetrics(metrics);

		for (Map.Entry<Object, Object> entry : metrics.entrySet()) {
			String metric = (String)entry.getKey();

			Set<AggregateSAQImpression> impressions = _getValueSet(
				_getValueMap(_index, metric), metrics.getProperty(metric));

			impressions.add(impression);
		}

		_indexed.add(impression);
	}

	public String toString() {
		Date date = new Date();

		StringBuffer sb = new StringBuffer();

		sb.append('{');

		date.setTime(_startMillis);
		sb.append("_startMillis=");
		sb.append(date.toString());
		sb.append(',');

		date.setTime(_expiryMillis);
		sb.append("_expiryMillis=");
		sb.append(date.toString());
		sb.append(',');

		sb.append("impressions=");

		if (_indexed != null) {
			sb.append(_indexed.toString());
		}
		else {
			sb.append("{}");
		}

		sb.append('}');

		return sb.toString();
	}

	private <X, Y, Z> Map<Y, Z> _getValueMap(Map<X, Map<Y, Z>> map, X key) {
		Map<Y, Z> valueMap = map.get(key);

		if (valueMap == null) {
			valueMap = new HashMap<>();
			map.put(key, valueMap);
		}

		return valueMap;
	}

	private <X, Y> Set<Y> _getValueSet(Map<X, Set<Y>> map, X key) {
		Set<Y> valueList = map.get(key);

		if (valueList == null) {
			valueList = new HashSet<>();
			map.put(key, valueList);
		}

		return valueList;
	}

	private static final long serialVersionUID = 1L;

	private final long _expiryMillis;
	private final Map<String, Map<String, Set<AggregateSAQImpression>>> _index;

	// For metrics that have not been indexed through an impression

	private final Set<AggregateSAQImpression> _indexed;

	private final long _startMillis;

}