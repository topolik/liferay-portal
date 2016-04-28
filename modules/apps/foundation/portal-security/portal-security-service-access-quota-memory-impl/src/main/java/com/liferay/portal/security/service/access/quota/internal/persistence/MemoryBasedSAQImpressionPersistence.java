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

import com.liferay.portal.security.service.access.quota.persistence.BaseIndexedSAQImpressionPersistence;
import com.liferay.portal.security.service.access.quota.persistence.SAQImpression;
import com.liferay.portal.security.service.access.quota.persistence.SAQImpressionPersistence;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;

import org.osgi.service.component.annotations.Component;

/**
 * @author Stian Sigvartsen
 */
@Component (service = SAQImpressionPersistence.class)
public class MemoryBasedSAQImpressionPersistence
	extends BaseIndexedSAQImpressionPersistence {

	@Override
	public void createImpression(
		long companyId, Properties callMetrics, long expiryMillis) {

		long bucketStartMillis = _getBucketStartMillis();

		LinkedList<SAQImpressionsBucket> buckets = _getBuckets(companyId);
		SAQImpressionsBucket currentBucket = buckets.peekLast();

		if ((currentBucket == null) ||
			(bucketStartMillis != currentBucket.getStartMillis())) {

			currentBucket = new SAQImpressionsBucket(
				bucketStartMillis, bucketStartMillis + expiryMillis);
			buckets.add(currentBucket);
		}

		AggregateSAQImpression matchedImpression = _fetchCompleteMetricsMatch(
			callMetrics, currentBucket);

		if (matchedImpression != null) {
			matchedImpression.incrementWeight();
		}
		else {
			AggregateSAQImpression impression = new AggregateSAQImpression(
				String.valueOf(_nextKey), callMetrics, bucketStartMillis);

			currentBucket.indexImpression(impression);
			_nextKey++;
		}
	}

	@Override
	public Iterator<SAQImpression> findImpressionsWithoutMetric(
		long companyId, String metric) {

		return _findImpressions(companyId, metric, null).iterator();
	}

	private AggregateSAQImpression _fetchCompleteMetricsMatch(
		Properties callMetrics, SAQImpressionsBucket currentBucket) {

		Set<AggregateSAQImpression> intersectSet = null;

		for (Map.Entry<Object, Object> entry : callMetrics.entrySet()) {
			Set<AggregateSAQImpression> metricImpressions =
				currentBucket.getImpressions(
					(String)entry.getKey(), (String)entry.getValue());

			if (metricImpressions.size() == 0) {
				return null;
			}
			else if (intersectSet == null) {
				intersectSet = metricImpressions;
			}
			else {
				intersectSet.retainAll(metricImpressions);
			}
		}

		if ((intersectSet != null) && (intersectSet.size() > 0)) {
			return intersectSet.iterator().next();
		}
		else {
			return null;
		}
	}

	@Override
	public Iterator<SAQImpression> findImpressionsMatchingMetric(
		long companyId, String metric, String value) {

		return _findImpressions(companyId, metric, value).iterator();
	}

	@Override
	public Iterator<SAQImpression> findAllImpressions(long companyId) {
		return _findImpressions(companyId, null, null).iterator();
	}

	private Set<SAQImpression> _findImpressions(
		long companyId, String metric, String value) {

		long nowMillis = System.currentTimeMillis();

		List<SAQImpressionsBucket> buckets = _getBuckets(companyId);

		Set<SAQImpression> union = new HashSet<>();

		Iterator<SAQImpressionsBucket> i = buckets.iterator();

		while (i.hasNext()) {
			SAQImpressionsBucket bucket = i.next();

			if (bucket.getExpiryMillis() < nowMillis) {
				i.remove();
			}
			else {
				union.addAll(bucket.getImpressions(metric, value));
			}
		}

		return union;
	}

	private LinkedList<SAQImpressionsBucket> _getBuckets(long companyId) {
		Long companyIdLong = Long.valueOf(companyId);
		LinkedList<SAQImpressionsBucket> buckets = _buckets.get(companyIdLong);

		if (buckets == null) {
			buckets = new LinkedList<>();
			_buckets.put(companyIdLong, buckets);
		}

		return buckets;
	}

	private long _getBucketStartMillis() {

		// Round up current time millis to bucket boundary
		// So that the quota intervalMillis is honored (as minimum)

		long bucketStart = System.currentTimeMillis();
		return
			bucketStart - (bucketStart % _BUCKET_INTERVAL) + _BUCKET_INTERVAL;
	}

	private static final long _BUCKET_INTERVAL = 1000;

	private final Map<Long, LinkedList<SAQImpressionsBucket>> _buckets =
		new HashMap<>();
	private long _nextKey = 0;

}