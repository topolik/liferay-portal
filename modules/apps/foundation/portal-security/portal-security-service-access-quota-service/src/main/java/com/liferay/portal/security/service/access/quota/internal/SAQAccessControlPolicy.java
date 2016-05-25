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
import com.liferay.portal.kernel.security.access.control.AccessControlPolicy;
import com.liferay.portal.kernel.security.access.control.AccessControlUtil;
import com.liferay.portal.kernel.security.access.control.AccessControlled;
import com.liferay.portal.kernel.security.access.control.BaseAccessControlPolicy;
import com.liferay.portal.kernel.security.auth.AccessControlContext;
import com.liferay.portal.kernel.security.auth.CompanyThreadLocal;
import com.liferay.portal.kernel.util.CharPool;
import com.liferay.portal.kernel.util.StringPool;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.security.service.access.quota.metric.SAQMetricProvider;
import com.liferay.portal.security.service.access.quota.persistence.SAQImpression;
import com.liferay.portal.security.service.access.quota.persistence.SAQImpressionPersistence;

import java.lang.reflect.Method;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Stian Sigvartsen
 */
@Component(service = AccessControlPolicy.class)
public class SAQAccessControlPolicy extends BaseAccessControlPolicy {

	public void checkServiceRateLimiting(
			long companyId, Class<?> serviceClazz, String serviceMethodName,
			Properties callMetrics, Set<ServiceAccessQuota> quotas)
		throws SecurityException {

		Iterator<SAQImpression> impressions =
			_impressionPersistence.findImpressions(companyId, callMetrics);

		if (!impressions.hasNext()) {
			return;
		}

		HashMap<ServiceAccessQuota, Integer> quotasCount = new HashMap<>(
			quotas.size());

		for (Iterator<ServiceAccessQuota> it = quotas.iterator();
			 it.hasNext();) {

			quotasCount.put(it.next(), 0);
		}

		Properties impressionMetrics = new Properties();
		List<String> quotaMetrics;

		while (impressions.hasNext()) {
			SAQImpression impression = impressions.next();

			impression.loadMetrics(impressionMetrics);

			for (Map.Entry<ServiceAccessQuota, Integer> entry :
					quotasCount.entrySet()) {

				ServiceAccessQuota quota = entry.getKey();

				quotaMetrics = quota.getMetric();
				int count = entry.getValue();

				if ((impression.getCreatedMillis() +
						quota.getIntervalMillis())
							< System.currentTimeMillis()) {

					continue;
				}

				if (!_isImpressionMatchedToCall(
						callMetrics, impressionMetrics, quotaMetrics)) {

					continue;
				}

				count = count + impression.getWeight();

				if (count < quota.getMax()) {
					entry.setValue(count);
					continue;
				}

				// If through impression matching a quota max is hit,
				// then adding the impression for the current request later
				// will breach it, so fail fast now

				StringBuffer sb = new StringBuffer();

				sb.append(
					"Breached limit ").append(quota.getMax()).append(
						'/').append(quota.getIntervalMillis());

				for (String quotaMetric : quota.getMetric()) {
					if (Validator.isNotNull(quotaMetric)) {
						sb.append('/').append(quotaMetric);
					}
				}

				sb.append(" for ").append(serviceClazz).append('#').append(
					serviceMethodName);

				throw new SecurityException(sb.toString());
			}
		}
	}

	@Override
	public void onServiceRemoteAccess(
		Method method, Object[] arguments,
		AccessControlled accessControlled) throws SecurityException {

		long companyId = CompanyThreadLocal.getCompanyId();

		if (isChecked()) {
			return;
		}

		Set<ServiceAccessQuota> matchedQuotas = matches(method);

		if (matchedQuotas.size() == 0) {
			return;
		}

		Class<?> clazz = method.getDeclaringClass();

		Properties callMetrics = getCallMetrics(method, matchedQuotas);

		try {
			checkServiceRateLimiting(
				companyId, clazz, method.getName(), callMetrics, matchedQuotas);
		}
		catch (SecurityException se) {
			if (_log.isDebugEnabled()) {
				_log.debug(se.getMessage());
			}

			throw se;
		}

		long largestQuotaIntervalMillis = 0;

		for (ServiceAccessQuota quota : matchedQuotas) {
			largestQuotaIntervalMillis = Math.max(
				quota.getIntervalMillis(), largestQuotaIntervalMillis);
		}

		_impressionPersistence.createImpression(
			companyId, callMetrics, largestQuotaIntervalMillis);
	}

	protected Properties getCallMetrics(
		Method method, Set<ServiceAccessQuota> quotas) {

		Properties callMetrics = new Properties();

		Set<String> requiredMetrics = new HashSet<>();

		for (ServiceAccessQuota quota : quotas) {
			requiredMetrics.addAll(quota.getMetric());
		}

		AccessControlContext accessControlContext =
			AccessControlUtil.getAccessControlContext();

		for (SAQMetricProvider metricProvider : _metricProviders) {
			String metricName = StringUtil.toLowerCase(
				metricProvider.getMetricName());

			if (Validator.isBlank(metricName) ||
				!requiredMetrics.contains(metricName)) {

				continue;
			}

			String metricValue = metricProvider.getMetricValue(
				accessControlContext, method);

			callMetrics.setProperty(metricName, metricValue);
		}

		return callMetrics;
	}

	protected boolean isChecked() {
		AccessControlContext accessControlContext =
			AccessControlUtil.getAccessControlContext();

		if (accessControlContext != null) {
			Map<String, Object> settings = accessControlContext.getSettings();

			int serviceDepth = (Integer)settings.get(
				AccessControlContext.Settings.SERVICE_DEPTH.toString());

			if (serviceDepth > 1) {
				return true;
			}
		}

		return false;
	}

	protected Set<ServiceAccessQuota> matches(Method method) {
		Set<ServiceAccessQuota> result = new HashSet<>(_quotas.size());

		Class<?> clazz = method.getDeclaringClass();

		String className = clazz.getName();
		String methodName = method.getName();

		for (ServiceAccessQuota quota : _quotas) {
			Set<String> serviceSignatures = quota.getServiceSignature();

			if (matches(className, methodName, serviceSignatures)) {
				result.add(quota);
			}
		}

		return result;
	}

	protected boolean matches(
		String className, String methodName, Set<String> serviceSignatures) {

		if (serviceSignatures.contains(StringPool.STAR)) {
			return true;
		}

		if (serviceSignatures.contains(className)) {
			return true;
		}

		String classNameAndMethodName = className.concat(
			StringPool.POUND).concat(methodName);

		if (serviceSignatures.contains(classNameAndMethodName)) {
			return true;
		}

		for (String serviceSignature : serviceSignatures) {
			if (matches(className, methodName, serviceSignature)) {
				return true;
			}
		}

		return false;
	}

	protected boolean matches(
		String className, String methodName, String serviceSignaturePattern) {

		String allowedClassName = null;
		String allowedMethodName = null;

		int index = serviceSignaturePattern.indexOf(CharPool.POUND);

		if (index > -1) {
			allowedClassName = serviceSignaturePattern.substring(0, index);
			allowedMethodName = serviceSignaturePattern.substring(index + 1);
		}
		else {
			allowedClassName = serviceSignaturePattern;
		}

		boolean wildcardMatchClass = false;

		if (Validator.isNotNull(allowedClassName) &&
			allowedClassName.endsWith(StringPool.STAR)) {

			allowedClassName = allowedClassName.substring(
				0, allowedClassName.length() - 1);
			wildcardMatchClass = true;
		}

		boolean wildcardMatchMethod = false;

		if (Validator.isNotNull(allowedMethodName) &&
			allowedMethodName.endsWith(StringPool.STAR)) {

			allowedMethodName = allowedMethodName.substring(
				0, allowedMethodName.length() - 1);
			wildcardMatchMethod = true;
		}

		if (Validator.isNotNull(allowedClassName) &&
			Validator.isNotNull(allowedMethodName)) {

			if (wildcardMatchClass && !className.startsWith(allowedClassName)) {
				return false;
			}
			else if (!wildcardMatchClass &&
					 !className.equals(allowedClassName)) {

				return false;
			}

			if (wildcardMatchMethod &&
				!methodName.startsWith(allowedMethodName)) {

				return false;
			}
			else if (!wildcardMatchMethod &&
					 !methodName.equals(allowedMethodName)) {

				return false;
			}

			return true;
		}
		else if (Validator.isNotNull(allowedClassName)) {
			if (wildcardMatchClass && !className.startsWith(allowedClassName)) {
				return false;
			}
			else if (!wildcardMatchClass &&
					 !className.equals(allowedClassName)) {

				return false;
			}

			return true;
		}
		else if (Validator.isNotNull(allowedMethodName)) {
			if (wildcardMatchMethod &&
				!methodName.startsWith(allowedMethodName)) {

				return false;
			}
			else if (!wildcardMatchMethod &&
					 !methodName.equals(allowedMethodName)) {

				return false;
			}

			return true;
		}
		else if (wildcardMatchClass && Validator.isNull(allowedClassName)) {
			return true;
		}

		return false;
	}

	private boolean _isImpressionMatchedToCall(
		Properties callMetrics, Properties impressionMetrics,
		List<String> quotaMetrics) {

		if ((quotaMetrics == null) || (quotaMetrics.size() == 0) ||
			Validator.isNull(quotaMetrics.get(0))) {

			return true;
		}

		boolean allMetricsMatch = true;

		for (String quotaMetric : quotaMetrics) {

			// Work around issue with System Settings changing
			// the character casing!

			quotaMetric = StringUtil.toLowerCase(quotaMetric);

			String impressionMetricValue = impressionMetrics.getProperty(
				quotaMetric);

			if ((impressionMetricValue == null) ||
				!impressionMetricValue.equals(
					callMetrics.get(quotaMetric))) {

				allMetricsMatch = false;
			}
		}

		return allMetricsMatch;
	}

	private static final Log _log = LogFactoryUtil.getLog(
		SAQAccessControlPolicy.class);

	@Reference
	private volatile SAQImpressionPersistence _impressionPersistence;

	@Reference
	private volatile List<SAQMetricProvider> _metricProviders;

	@Reference
	private volatile List<ServiceAccessQuota> _quotas;

}