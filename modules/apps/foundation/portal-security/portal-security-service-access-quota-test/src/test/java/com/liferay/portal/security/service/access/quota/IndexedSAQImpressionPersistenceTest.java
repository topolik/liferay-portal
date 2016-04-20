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

import com.liferay.portal.security.service.access.quota.persistence.SAQImpression;

import java.util.Iterator;
import java.util.Properties;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

/**
 * @author Stian Sigvartsen
 */
public class IndexedSAQImpressionPersistenceTest
	extends BaseIndexedSAQImpressionPersistenceTest {

	@Before
	public void setUp() throws Exception {
		_iR = getImpressionPersistence();
	}

	@Test
	public void testFindImpressionsMatchingMetric() {
		_iR.initIndex(_METRICS);

		_iR.createImpression(
			_TEST_SERVICE_CLASS_1,
			stringArrayToProperties(new String[][] {{_METRIC_USER, "user1"}}),
			_TEST_EXPIRY_MILLIS);

		Properties testCallMetrics = new Properties();

		testCallMetrics.setProperty(_METRIC_USER, "user1");

		Assert.assertEquals(
			1, _countImpressions(
				_iR.findImpressions(_TEST_SERVICE_CLASS_1, testCallMetrics)));
	}

	@Test
	public void testFindImpressionsWithoutMetric() {
		_iR.initIndex(_METRICS);

		_iR.createImpression(
			_TEST_SERVICE_CLASS_1,
			stringArrayToProperties(new String[][] {{_METRIC_USER, "user1"}}),
			_TEST_EXPIRY_MILLIS);

		Properties testCallMetrics = new Properties();

		testCallMetrics.setProperty(_METRIC_METHOD, "method1");

		Assert.assertEquals(
			1, _countImpressions(
				_iR.findImpressions(_TEST_SERVICE_CLASS_1, testCallMetrics)));
	}

	@Test
	public void testIgnoreImpressionsDifferentServiceClassName() {
		_iR.initIndex(_METRICS);

		_iR.createImpression(
			_TEST_SERVICE_CLASS_1,
			stringArrayToProperties(new String[][] {{_METRIC_USER, "user1"}}),
			_TEST_EXPIRY_MILLIS);

		Properties testCallMetrics = new Properties();

		testCallMetrics.setProperty(_METRIC_USER, "user1");

		Assert.assertEquals(
			0, _countImpressions(
				_iR.findImpressions(_TEST_SERVICE_CLASS_2, testCallMetrics)));
	}

	@Test
	public void testIgnoreImpressionsNotMatchingMetric() {
		_iR.initIndex(_METRICS);

		_iR.createImpression(
			_TEST_SERVICE_CLASS_1,
			stringArrayToProperties(new String[][] {{_METRIC_USER, "user1"}}),
			_TEST_EXPIRY_MILLIS);

		Properties testCallMetrics = new Properties();

		testCallMetrics.setProperty(_METRIC_USER, "user2");

		Assert.assertEquals(
			0, _countImpressions(
				_iR.findImpressions(_TEST_SERVICE_CLASS_1, testCallMetrics)));
	}

	@Test
	public void testUniqueImpressionsIteration() {

		// Test that impression 1 does not appear twice in the iterator

		_iR.initIndex(_METRICS);

		_iR.createImpression(
			_TEST_SERVICE_CLASS_1,
			stringArrayToProperties(
				new String[][] {{_METRIC_USER, "user1"
			},
					{_METRIC_METHOD, "method1"}
				}),
			_TEST_EXPIRY_MILLIS);

		_iR.createImpression(
			_TEST_SERVICE_CLASS_1,
			stringArrayToProperties(new String[][] {{_METRIC_USER, "user1"}}),
			_TEST_EXPIRY_MILLIS);

		_iR.createImpression(
			_TEST_SERVICE_CLASS_1,
			stringArrayToProperties(
				new String[][] {{_METRIC_METHOD, "method1"}}),
			_TEST_EXPIRY_MILLIS);

		Properties testCallMetrics = new Properties();

		testCallMetrics.setProperty(_METRIC_USER, "user1");
		testCallMetrics.setProperty(_METRIC_METHOD, "method1");

		Assert.assertEquals(
			3, _countImpressions(
				_iR.findImpressions(_TEST_SERVICE_CLASS_1, testCallMetrics)));
	}

	private int _countImpressions(Iterator<SAQImpression> iterator) {
		int impressionCount = 0;
		SAQImpression current;
		while (iterator.hasNext()) {
			current = iterator.next();
			impressionCount++;
		}

		return impressionCount;
	}

	private static final String _METRIC_CLIENTIP = "clientip";

	private static final String _METRIC_METHOD = "method";

	private static final String _METRIC_USER = "user";

	private static final String[] _METRICS =
		new String[] {_METRIC_USER, _METRIC_METHOD, _METRIC_CLIENTIP};

	private static final long _TEST_EXPIRY_MILLIS = 10000;

	private static final Class<?> _TEST_SERVICE_CLASS_1 =
		java.lang.Object.class;

	private static final Class<?> _TEST_SERVICE_CLASS_2 =
		java.lang.String.class;

	private TestIndexedSAQImpressionPersistence _iR;

}