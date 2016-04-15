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

import com.liferay.portal.kernel.exception.SystemException;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.model.Ticket;
import com.liferay.portal.kernel.security.access.control.AccessControlPolicy;
import com.liferay.portal.kernel.security.access.control.AccessControlUtil;
import com.liferay.portal.kernel.security.access.control.AccessControlled;
import com.liferay.portal.kernel.security.access.control.BaseAccessControlPolicy;
import com.liferay.portal.kernel.security.auth.AccessControlContext;
import com.liferay.portal.kernel.service.TicketLocalService;
import com.liferay.portal.kernel.util.CharPool;
import com.liferay.portal.kernel.util.StringPool;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.security.service.access.quota.metric.SAQMetricProvider;
import com.liferay.ticket.kernel.model.TicketConstants;

import java.io.IOException;
import java.io.StringReader;
import java.io.StringWriter;

import java.lang.reflect.Method;

import java.util.ArrayList;
import java.util.Date;
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
			String serviceClassName, String serviceMethodName,
			Map<String, String> requestMetrics, Set<ServiceAccessQuota> quotas)
		throws SecurityException {

		List<Ticket> tickets = _ticketService.findTickets(
			serviceClassName, 0, TicketConstants.TYPE_RATE_LIMITING);

		if (tickets == null) {
			return;
		}

		HashMap<ServiceAccessQuota, Integer> quotasCount = new HashMap<>(
			quotas.size());

		for (Iterator<ServiceAccessQuota> it = quotas.iterator();
			 it.hasNext();) {

			quotasCount.put(it.next(), 0);
		}

		Properties extraInfoFilter = new Properties();

		for (Ticket ticket : tickets) {
			if (ticket.isExpired()) {
				_ticketService.deleteTicket(ticket);
				continue;
			}

			for (Map.Entry<ServiceAccessQuota, Integer> entry :
					quotasCount.entrySet()) {

				ServiceAccessQuota quota = entry.getKey();
				int count = entry.getValue();

				if ((ticket.getCreateDate().getTime() +
					 quota.getIntervalMillis())
					< System.currentTimeMillis()) {

					continue;
				}

				List<String> quotaMetrics = quota.getMetric();

				if ((count - 1) >= quota.getMax()) {
					StringBuffer sb = new StringBuffer();

					sb.append(
						"Breached limit ").append(quota.getMax()).append(
						'/').append(quota.getIntervalMillis());

					for (String quotaMetric : quotaMetrics) {
						sb.append('/').append(quotaMetric);
					}

					sb.append(" for ").append(serviceClassName).append('#').
						append(serviceMethodName);

					throw new SecurityException(sb.toString());
				}

				if ((quotaMetrics == null) ||
					(quotaMetrics.size() == 0) ||
					Validator.isNull(quotaMetrics.get(0))) {

					entry.setValue(count + 1);
					continue;
				}

				String extraInfo = ticket.getExtraInfo();

				if (extraInfo != null) {
					try {
						extraInfoFilter.clear();

						extraInfoFilter.load(new StringReader(extraInfo));
					}
					catch (IOException ioe) {
						throw new SystemException(
							"Failed to parse extra info of ticket " +
							ticket.getKey());
					}
				}

				for (String quotaMetric : quotaMetrics) {

					// Work around issue with System Settings changing
					// the character casing!

					quotaMetric = StringUtil.toLowerCase(quotaMetric);

					String ticketMetricValue =
						extraInfoFilter.getProperty(quotaMetric);

					if ((ticketMetricValue == null) ||
						!ticketMetricValue.equals(
							requestMetrics.get(quotaMetric))) {

						continue;
					}
				}

				entry.setValue(count + 1);
			}
		}
	}

	@Override
	public void onServiceRemoteAccess(
		Method method, Object[] arguments,
		AccessControlled accessControlled) throws SecurityException {

		if (isChecked()) {
			return;
		}

		Set<ServiceAccessQuota> matchedQuotas = matches(method);

		if (matchedQuotas.size() == 0) {
			return;
		}

		Class<?> clazz = method.getDeclaringClass();

		Map<String, String> callMetrics = getCallMetrics(
			method, matchedQuotas);

		try {
			checkServiceRateLimiting(
				clazz.getName(), method.getName(), callMetrics,
				matchedQuotas);
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
				quota.getIntervalMillis(),
				largestQuotaIntervalMillis);

		}

		_createTicket(
			clazz, callMetrics, largestQuotaIntervalMillis);
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

	private void _createTicket(
		Class<?> clazz, Map<String, String> requestMetrics, long expiryMillis) {

		Date expirationDate = new Date(
			System.currentTimeMillis() + expiryMillis);

		StringWriter sw = new StringWriter();
		Properties extraInfoFilter = new Properties();

		extraInfoFilter.putAll(requestMetrics);

		try {
			extraInfoFilter.store(sw, null);
		}
		catch (IOException ioe) {
			throw new SystemException(ioe);
		}

		_ticketService.addTicket(
			0, clazz.getName(), 0,
			TicketConstants.TYPE_RATE_LIMITING, sw.toString(), expirationDate,
			null);
	}

	protected Map<String, String> getCallMetrics(
		Method method, Set<ServiceAccessQuota> quotas) {

		Map<String, String> callMetrics = new HashMap<>();

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

			callMetrics.put(metricName, metricValue);
		}

		return callMetrics;
	}

	private static final Log _log = LogFactoryUtil.getLog(
		SAQAccessControlPolicy.class);

	@Reference
	private volatile List<SAQMetricProvider> _metricProviders;

	@Reference
	private volatile List<ServiceAccessQuota> _quotas;

	@Reference
	private volatile TicketLocalService _ticketService;

}