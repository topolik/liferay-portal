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

package com.liferay.portal.security.service.access.quota.internal.access.control;

import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.security.access.control.AccessControlPolicy;
import com.liferay.portal.kernel.security.access.control.AccessControlUtil;
import com.liferay.portal.kernel.security.access.control.AccessControlled;
import com.liferay.portal.kernel.security.access.control.BaseAccessControlPolicy;
import com.liferay.portal.kernel.security.auth.AccessControlContext;
import com.liferay.portal.kernel.security.auth.CompanyThreadLocal;
import com.liferay.portal.kernel.util.StringBundler;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.security.service.access.quota.AccessControlPolicySAQMetricProvider;
import com.liferay.portal.security.service.access.quota.ServiceAccessQuota;
import com.liferay.portal.security.service.access.quota.ServiceAccessQuota.SAQMetricConfig;
import com.liferay.portal.security.service.access.quota.internal.AccessControlPolicySAQContextFactory;
import com.liferay.portal.security.service.access.quota.internal.QuotaBreachException;
import com.liferay.portal.security.service.access.quota.internal.SAQContext;
import com.liferay.portal.security.service.access.quota.internal.SAQProcessor;
import com.liferay.portal.security.service.access.quota.persistence.SAQImpressionProvider;

import java.lang.reflect.Method;

import java.util.List;
import java.util.Map;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.component.annotations.ReferenceCardinality;
import org.osgi.service.component.annotations.ReferencePolicy;
import org.osgi.service.component.annotations.ReferencePolicyOption;

/**
 * @author Stian Sigvartsen
 * @author Carlos Sierra Andrés
 */
@Component(service = AccessControlPolicy.class)
public class SAQAccessControlPolicy extends BaseAccessControlPolicy {

	@Override
	public void onServiceRemoteAccess(
			final Method method, Object[] arguments,
			AccessControlled accessControlled)
		throws SecurityException {

		long companyId = CompanyThreadLocal.getCompanyId();

		if (isChecked()) {
			return;
		}

		final AccessControlContext accessControlContext =
			AccessControlUtil.getAccessControlContext();

		SAQContext saqContext =
			_accessControlPolicySaqContextFactory.buildContext(
				accessControlContext, method);

		if (saqContext.getQuotas().size() == 0) {
			return;
		}

		SAQProcessor<AccessControlPolicySAQMetricProvider> processor =
			new SAQProcessor<>(saqContext);

		SAQProcessor.ProcessingResult processingResult = processor.process(
			companyId, _saqImpressionProvider);

		if (processingResult.getStatus().equals(
				SAQProcessor.ProcessingResult.Status.BREACHED_QUOTA)) {

			String quotaBreachedMsg = _getQuotaBreachedMsg(
				processingResult.getBreachedQuota());

			if (_log.isDebugEnabled()) {
				_log.debug(quotaBreachedMsg);
			}

			throw new QuotaBreachException(quotaBreachedMsg);
		}

		long largestQuotaIntervalMillis = 0;

		for (ServiceAccessQuota serviceAccessQuota : saqContext.getQuotas()) {
			largestQuotaIntervalMillis = Math.max(
				serviceAccessQuota.getIntervalMillis(),
				largestQuotaIntervalMillis);
		}

		_saqImpressionProvider.createSAQImpression(
			companyId, saqContext.getMetricsMap(), largestQuotaIntervalMillis);
	}

	@Reference (unbind = "-")
	public void setImpressionProvider(
		SAQImpressionProvider saqImpressionProvider) {

		_saqImpressionProvider = saqImpressionProvider;
	}

	@Reference (
		cardinality = ReferenceCardinality.MULTIPLE,
		policy = ReferencePolicy.DYNAMIC,
		policyOption = ReferencePolicyOption.GREEDY,
		unbind = "unsetMetricProvider"
	)
	public void setMetricProvider(
		AccessControlPolicySAQMetricProvider saqMetricProvider) {

		Map<String, AccessControlPolicySAQMetricProvider> saqMetricProviders =
			_accessControlPolicySaqContextFactory.getMetricProviders();

		saqMetricProviders.put(
			StringUtil.toLowerCase(saqMetricProvider.getMetricName()),
			saqMetricProvider);
	}

	@Reference (
		cardinality = ReferenceCardinality.MULTIPLE,
		policy = ReferencePolicy.DYNAMIC,
		policyOption = ReferencePolicyOption.GREEDY,
		unbind = "unsetServiceAccessQuota"
	)
	public void setServiecAccessQuota(ServiceAccessQuota serviceAccessQuota) {
		List<ServiceAccessQuota> serviceAccessQuotas =
			_accessControlPolicySaqContextFactory.getServiceAccessQuotas();

		serviceAccessQuotas.add(serviceAccessQuota);
	}

	public void unsetMetricProvider(
		AccessControlPolicySAQMetricProvider saqMetricProvider) {

		Map<String, AccessControlPolicySAQMetricProvider> saqMetricProviders =
			_accessControlPolicySaqContextFactory.getMetricProviders();

		saqMetricProviders.remove(saqMetricProvider.getMetricName());
	}

	public void unsetServiceAccessQuota(ServiceAccessQuota serviceAccessQuota) {
		List<ServiceAccessQuota> serviceAccessQuotas =
			_accessControlPolicySaqContextFactory.getServiceAccessQuotas();

		serviceAccessQuotas.remove(serviceAccessQuota);
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

	private String _getQuotaBreachedMsg(ServiceAccessQuota serviceAccessQuota) {
		List<SAQMetricConfig> saqMetricConfigs =
			serviceAccessQuota.getMetricConfigs();

		StringBundler sb = new StringBundler(saqMetricConfigs.size() + 4);

		sb.append("Breached quota ");

		sb.append(serviceAccessQuota.getMax());

		sb.append('/');

		sb.append(serviceAccessQuota.getIntervalMillis());

		for (SAQMetricConfig saqMetricConfig : saqMetricConfigs) {
			if (Validator.isNotNull(saqMetricConfig)) {
				sb.append('/').append(saqMetricConfig);
			}
		}

		return sb.toString();
	}

	private static final Log _log = LogFactoryUtil.getLog(
		SAQAccessControlPolicy.class);

	private final AccessControlPolicySAQContextFactory
		_accessControlPolicySaqContextFactory =
			new AccessControlPolicySAQContextFactory();
	private SAQImpressionProvider _saqImpressionProvider;

}