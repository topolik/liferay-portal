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
import com.liferay.portal.kernel.security.auth.CompanyThreadLocal;
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

import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
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
public class SAQAccessControlPolicyPipeline extends BaseAccessControlPolicy {

	public void checkServiceRateLimiting(
			long companyId, String serviceClassName, String serviceMethodName,
			Map<String, String> requestMetrics, SAQAccessControlPolicy policy)
		throws SecurityException {

		List<Ticket> tickets = _ticketService.findTickets(
			serviceClassName, 0l, TicketConstants.TYPE_RATE_LIMITING);

		int count = 0;

		String[] policyMetrics = policy.getPolicyMetric();

		Properties extraInfoFilter = new Properties();

		if (tickets != null) {
			for (Ticket ticket : tickets) {
				if (ticket.isExpired()) {
					_ticketService.deleteTicket(ticket);
				}
				else if ((ticket.getCreateDate().getTime() +
							policy.getIntervalMillis())
								> System.currentTimeMillis()) {

					String extraInfo = ticket.getExtraInfo();

					if (extraInfo != null) {
						try {
							extraInfoFilter.load(new StringReader(extraInfo));
						}
						catch (IOException ioe) {
							throw new SystemException(
								"Failed to parse extra info of ticket " +
									ticket.getKey());
						}
					}

					if ((policyMetrics == null) ||
						(policyMetrics.length == 0) ||
						Validator.isNull(policyMetrics[0])) {

						count++;
					}
					else {
						boolean allMetricsMatch = true;

						for (String policyMetric : policyMetrics) {

							// Work around issue with System Settings changing
							// the character casing!

							policyMetric = StringUtil.toLowerCase(policyMetric);

							String ticketMetricValue =
								extraInfoFilter.getProperty(policyMetric);

							if ((ticketMetricValue == null) ||
								!ticketMetricValue.equals(
									requestMetrics.get(policyMetric))) {

								allMetricsMatch = false;
							}
						}

						if (allMetricsMatch) {
							count++;
						}
					}

					extraInfoFilter.clear();
				}
			}

			if (count >= policy.getMax()) {
				StringBuffer sb = new StringBuffer();

				sb.append(
					"Breached limit ").append(policy.getMax()).append(
						'/').append(policy.getIntervalMillis());

				for (String policyMetric : policyMetrics) {
					sb.append('/') .append(policyMetric);
				}

				sb.append(" for ").append(serviceClassName).append('#').append(
					serviceMethodName);

				throw new SecurityException(sb.toString());
			}
		}
	}

	@Override
	public void onServiceRemoteAccess(
		Method method, Object[] arguments,
		AccessControlled accessControlled) throws SecurityException {

		Class<?> clazz = method.getDeclaringClass();

		Map<String, String> requestMetrics = _getRequestMetrics(method);

		boolean atLeastOnePolicyApplies = false;

		long largestPolicyIntervalMillis = 0;
		Set<SecurityException> breaches = new HashSet<>();

		for (SAQAccessControlPolicy policy : _policies) {
			for (String serviceSignature : policy.getServiceSignature()) {
				if (matches(
						clazz.getName(), method.getName(), serviceSignature)) {

					atLeastOnePolicyApplies = true;

					largestPolicyIntervalMillis = Math.max(
						policy.getIntervalMillis(),
						largestPolicyIntervalMillis);

					try {
						checkServiceRateLimiting(
							CompanyThreadLocal.getCompanyId().longValue(),
							clazz.getName(), method.getName(), requestMetrics,
							policy);
					}
					catch (SecurityException se) {
						breaches.add(se);
					}
				}
			}
		}

		if (atLeastOnePolicyApplies) {
			if (breaches.size() > 0) {
				if (_log.isDebugEnabled()) {
					for (SecurityException e : breaches) {
						_log.debug(e.getMessage());
					}
				}

				throw new SecurityException("Breached rate limit policy");
			}
			else {
				_createTicket(
					clazz, requestMetrics, largestPolicyIntervalMillis);
			}
		}
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
			CompanyThreadLocal.getCompanyId().longValue(), clazz.getName(), 0l,
			TicketConstants.TYPE_RATE_LIMITING, sw.toString(), expirationDate,
			null);
	}

	private Map<String, String> _getRequestMetrics(Method method) {
		Map<String, String> requestMetrics = new HashMap<>();

		AccessControlContext accessControlContext =
			AccessControlUtil.getAccessControlContext();

		for (SAQMetricProvider metricProvider : _metricProviders) {
			String metricName = metricProvider.getMetricName();

			if (Validator.isNotNull(metricName)) {
				requestMetrics.put(
					StringUtil.toLowerCase(metricName),
					metricProvider.getMetricValue(
						accessControlContext, method));
			}
		}

		return requestMetrics;
	}

	private static final Log _log = LogFactoryUtil.getLog(
		SAQAccessControlPolicyPipeline.class);

	@Reference
	private volatile List<SAQMetricProvider> _metricProviders;

	@Reference
	private volatile List<SAQAccessControlPolicy> _policies;

	@Reference
	private volatile TicketLocalService _ticketService;

}