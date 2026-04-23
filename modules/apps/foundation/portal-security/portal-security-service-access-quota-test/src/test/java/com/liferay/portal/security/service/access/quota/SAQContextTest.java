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

import com.liferay.portal.kernel.security.auth.AccessControlContext;
import com.liferay.portal.kernel.security.auth.verifier.AuthVerifierResult;
import com.liferay.portal.kernel.util.ListUtil;
import com.liferay.portal.security.service.access.quota.ServiceAccessQuota.SAQMetricConfig;
import com.liferay.portal.security.service.access.quota.impl.TestIndexedSAQImpressionProviderImpl;
import com.liferay.portal.security.service.access.quota.impl.TestServiceAccessQuotaImpl;
import com.liferay.portal.security.service.access.quota.internal.QuotaBreachException;
import com.liferay.portal.security.service.access.quota.internal.SAQContext;
import com.liferay.portal.security.service.access.quota.internal.SAQProcessor;
import com.liferay.portal.security.service.access.quota.internal.SAQProcessor.ProcessingResult;
import com.liferay.portal.security.service.access.quota.internal.access.control.AccessControlPolicySAQContextFactory;
import com.liferay.portal.security.service.access.quota.internal.access.control.ServiceSAQMetricProvider;
import com.liferay.portal.security.service.access.quota.internal.access.control.UserIdSAQMetricProvider;
import com.liferay.portal.security.service.access.quota.persistence.SAQImpressionProvider;
import com.liferay.portal.security.service.access.quota.sim.TestService;

import java.lang.reflect.Method;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import org.mockito.InjectMocks;
import org.mockito.Mock;

import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

/**
 * @author Stian Sigvartsen
 */
@PrepareForTest(AccessControlContext.class)
@RunWith(PowerMockRunner.class)
public class SAQContextTest extends PowerMockito {

	@Before
	public void setUp() throws Exception {
		_companyId = 0;

		_registerMetricProvider(new ServiceSAQMetricProvider());
		_registerMetricProvider(new UserIdSAQMetricProvider());

		when(
			_accessControlContext.getAuthVerifierResult().getUserId()
		).thenReturn(
			123L
		);
	}

	@Test
	public void testContextHasMatchedQuotasOnly()
		throws NoSuchMethodException, QuotaBreachException {

		List<ServiceAccessQuota> quotas =
			ListUtil.fromArray(new ServiceAccessQuota[] {
				_QUOTA_3_60000_NO_METRICS, _QUOTA_2_60000_GET,
				_QUOTA_1_60000_GETMETHOD2_USER123
			});

		Method testMethod;
		testMethod = TestService.class.getMethod("getMethod1");

		SAQContext context = _getSAQContext(quotas, testMethod);

		// Test for quota with no metrics + the *#get* one

		Assert.assertTrue(context.getServiceAccessQuotas().size() == 2);
	}

	@Test
	public void testContextHasMetricsFromMatchedQuotasOnly()
		throws NoSuchMethodException, QuotaBreachException {

		List<ServiceAccessQuota> quotas =
			ListUtil.fromArray(new ServiceAccessQuota[] {
				_QUOTA_3_60000_NO_METRICS, _QUOTA_2_60000_GET,
				_QUOTA_1_60000_GETMETHOD2_USER123
			});

		Method testMethod;
		testMethod = TestService.class.getMethod("getMethod1");

		SAQContext context = _getSAQContext(quotas, testMethod);

		Assert.assertEquals(1, context.getMetricsMap().keySet().size());
		Assert.assertFalse(context.getMetricsMap().keySet().contains("user"));
	}

	@Test
	public void testImpressionsWithNoMetricsCounted()
		throws NoSuchMethodException, QuotaBreachException {

		List<ServiceAccessQuota> quotas =
			ListUtil.fromArray(new ServiceAccessQuota[] {
				_QUOTA_3_60000_NO_METRICS, _QUOTA_2_60000_GET
			});

		SAQImpressionProvider iR = new TestIndexedSAQImpressionProviderImpl();

		// A couple of impressions left after calls to methods not matching
		// any quota except _QUOTA_3_60000_NO_METRICS

		iR.createSAQImpression(_companyId, _metricsNone, _TEST_EXPIRY_MILLIS);
		iR.createSAQImpression(_companyId, _metricsNone, _TEST_EXPIRY_MILLIS);

		Method testMethod;

		// Matches quota _QUOTA_2_60000_GET

		testMethod = TestService.class.getMethod("getMethod1");
		iR.createSAQImpression(
			_companyId, _metricsGetMethod1User1, _TEST_EXPIRY_MILLIS);

		SAQContext context = _getSAQContext(quotas, testMethod);

		SAQProcessor processor = new SAQProcessor(context);

		Assert.assertEquals(2, context.getServiceAccessQuotas().size());

		Assert.assertEquals(
			ProcessingResult.Status.BREACHED_QUOTA,
			processor.process(_companyId, iR).getStatus());
	}

	@Test
	public void testQuotaWithNoMetricsMatchesAllImpressions()
		throws NoSuchMethodException, QuotaBreachException {

		List<ServiceAccessQuota> quotas =
			ListUtil.fromArray(new ServiceAccessQuota[] {
				_QUOTA_3_60000_NO_METRICS
			});

		SAQImpressionProvider iR = new TestIndexedSAQImpressionProviderImpl();

		Method testMethod;
		testMethod = TestService.class.getMethod("getMethod1");
		iR.createSAQImpression(
			_companyId, _metricsGetMethod1User1, _TEST_EXPIRY_MILLIS);

		testMethod = TestService.class.getMethod("getMethod2");
		iR.createSAQImpression(
			_companyId, _metricsGetMethod2User1, _TEST_EXPIRY_MILLIS);

		testMethod = TestService.class.getMethod("setMethod1");
		iR.createSAQImpression(
			_companyId, _metricsSetMethod1User1, _TEST_EXPIRY_MILLIS);

		SAQContext context = _getSAQContext(quotas, testMethod);

		SAQProcessor processor = new SAQProcessor(context);

		Assert.assertEquals(
			ProcessingResult.Status.BREACHED_QUOTA,
			processor.process(_companyId, iR).getStatus());
	}

	@Test
	public void testWildcardMethodMatching()
		throws NoSuchMethodException, QuotaBreachException {

		List<ServiceAccessQuota> quotas = ListUtil.fromArray(
			new ServiceAccessQuota[] {_QUOTA_2_60000_GET});

		SAQImpressionProvider iR = new TestIndexedSAQImpressionProviderImpl();

		Method testMethod;
		testMethod = TestService.class.getMethod("getMethod1");
		iR.createSAQImpression(
			_companyId, _metricsGetMethod1User1, _TEST_EXPIRY_MILLIS);

		testMethod = TestService.class.getMethod("getMethod2");
		iR.createSAQImpression(
			_companyId, _metricsGetMethod2User1, _TEST_EXPIRY_MILLIS);

		SAQContext context = _getSAQContext(quotas, testMethod);

		SAQProcessor processor = new SAQProcessor(context);

		Assert.assertEquals(
			ProcessingResult.Status.BREACHED_QUOTA,
			processor.process(_companyId, iR).getStatus());
	}

	private static Map<String, String> _stringArrayToMap(String[][] props) {
		Map<String, String> retMap = new HashMap<>();

		for (int i = 0; i < props.length; i++) {
			retMap.put(props[i][0], props[i][1]);
		}

		return retMap;
	}

	private SAQContext _getSAQContext(
		List<ServiceAccessQuota> quotas, Method testMethod) {

		AccessControlPolicySAQContextFactory factory =
			new AccessControlPolicySAQContextFactory();

		factory.setSaqMetricProviders(_metricProviders);
		factory.setServiceAccessQuotas(quotas);

		SAQContext context = factory.buildContext(
			_accessControlContext, testMethod);

		return context;
	}

	// ----

	private void _registerMetricProvider(
		AccessControlPolicySAQMetricProvider metricProvider) {

		_metricProviders.put(metricProvider.getMetricName(), metricProvider);
	}

	private static final ServiceAccessQuota _QUOTA_1_60000_GETMETHOD2_USER123 =
		new TestServiceAccessQuotaImpl(
			60000, 1,
			ListUtil.fromArray(
				new SAQMetricConfig[] {
					new SAQMetricConfig("service", "*#getMethod2"),
					new SAQMetricConfig("user", "123")
				}));

	private static final ServiceAccessQuota _QUOTA_2_60000_GET =
		new TestServiceAccessQuotaImpl(
			60000, 2, ListUtil.fromArray(
				new SAQMetricConfig[] {
					new SAQMetricConfig(
						"service", "*#get*")
				}));

	private static final ServiceAccessQuota _QUOTA_3_60000_NO_METRICS =
		new TestServiceAccessQuotaImpl(
			60000, 3, ListUtil.fromArray(new SAQMetricConfig[0]));

	private static final long _TEST_EXPIRY_MILLIS = 10000;

	private static final Map<String, String> _metricsGetMethod1User1 =
		_stringArrayToMap(
			new String[][] {
				{"service", TestService.class.getName() + "#getMethod1"},
				{"user", "123"}
			});
	private static final Map<String, String> _metricsGetMethod2User1 =
		_stringArrayToMap(
			new String[][] {
				{"service", TestService.class.getName() + "#getMethod2"},
				{"user", "123"}
			});
	private static final Map<String, String> _metricsNone = _stringArrayToMap(
		new String[0][0]);
	private static final Map<String, String> _metricsSetMethod1User1 =
		_stringArrayToMap(
			new String[][] {
				{"service", TestService.class.getName() + "#setMethod1"},
				{"user", "123"}
			});

	@InjectMocks
	private final AccessControlContext _accessControlContext =
		new AccessControlContext();

	@Mock
	private AuthVerifierResult _authVerifierResult;

	private long _companyId;
	private final Map<String, AccessControlPolicySAQMetricProvider>
		_metricProviders = new HashMap<>();

}