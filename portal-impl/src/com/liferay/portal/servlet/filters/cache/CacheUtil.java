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

package com.liferay.portal.servlet.filters.cache;

import com.liferay.exportimport.kernel.lar.ExportImportThreadLocal;
import com.liferay.portal.kernel.cache.MultiVMPoolUtil;
import com.liferay.portal.kernel.cache.PortalCache;
import com.liferay.portal.kernel.cache.RequestParameterCacheValidator;
import com.liferay.portal.kernel.cache.key.CacheKeyGenerator;
import com.liferay.portal.kernel.cache.key.CacheKeyGeneratorUtil;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.servlet.ServletRequestParameterReader;
import com.liferay.portal.kernel.util.HttpUtil;
import com.liferay.portal.kernel.util.ListUtil;
import com.liferay.portal.kernel.util.PortalUtil;
import com.liferay.portal.kernel.util.StringBundler;
import com.liferay.portal.kernel.util.StringPool;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.registry.collections.ServiceTrackerCollections;
import com.liferay.registry.collections.ServiceTrackerMap;
import com.liferay.util.servlet.filters.CacheResponseData;

import java.util.Collections;
import java.util.Enumeration;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

/**
 * @author Alexander Chow
 * @author Michael Young
 */
public class CacheUtil {

	public static final String CACHE_NAME = CacheUtil.class.getName();

	public static void clearCache() {
		if (ExportImportThreadLocal.isImportInProcess()) {
			return;
		}

		_portalCache.removeAll();
	}

	public static void clearCache(long companyId) {
		clearCache();
	}

	public static String getCacheFileName(
		final HttpServletRequest request, String cacheName, Log log) {

		CacheKeyGenerator cacheKeyGenerator =
			CacheKeyGeneratorUtil.getCacheKeyGenerator(cacheName);

		cacheKeyGenerator.append(HttpUtil.getProtocol(request.isSecure()));
		cacheKeyGenerator.append(StringPool.UNDERLINE);
		cacheKeyGenerator.append(request.getRequestURI());

		Enumeration<String> parameterNamesEnumeration =
			request.getParameterNames();

		List<String> parameterNames = ListUtil.fromEnumeration(
			parameterNamesEnumeration);

		Collections.sort(parameterNames);

		for (String parameterName : _serviceTrackerMap.keySet()) {
			for (RequestParameterCacheValidator requestParameterCacheValidator :
					_serviceTrackerMap.getService(parameterName)) {

				if (requestParameterCacheValidator == null) {
					continue;
				}

				try {
					boolean valid = requestParameterCacheValidator.validate(
						parameterName,
						new ServletRequestParameterReader() {

							@Override
							public long getCompanyId() {
								return PortalUtil.getCompanyId(request);
							}

							@Override
							public String getParameter(String paramName) {
								return request.getParameter(paramName);
							}

							@Override
							public String[] getParameterValues(
								String paramName) {

								return request.getParameterValues(paramName);
							}

						});

					if (valid) {
						cacheKeyGenerator.append(StringPool.UNDERLINE);
						cacheKeyGenerator.append(parameterName);
						cacheKeyGenerator.append(StringPool.UNDERLINE);
						cacheKeyGenerator.append(
							request.getParameter(parameterName));
					}
					else {
						if (log.isDebugEnabled()) {
							StringBundler sb = new StringBundler(5);

							sb.append("Parameter value ");
							sb.append(request.getParameter(parameterName));
							sb.append(" for parameter ");
							sb.append(parameterName);
							sb.append(" has been discarded for cache key");

							log.debug(sb.toString());
						}
					}
				}
				catch (Exception e) {
					if (log.isInfoEnabled()) {
						log.info(
							"Error validating request parameter " +
								parameterName + " for cache",
							e);
					}
				}
			}
		}

		return String.valueOf(cacheKeyGenerator.finish());
	}

	public static CacheResponseData getCacheResponseData(
		long companyId, String key) {

		if (Validator.isNull(key)) {
			return null;
		}

		key = _encodeKey(companyId, key);

		return _portalCache.get(key);
	}

	public static void putCacheResponseData(
		long companyId, String key, CacheResponseData data) {

		if (data != null) {
			key = _encodeKey(companyId, key);

			_portalCache.put(key, data);
		}
	}

	private static String _encodeKey(long companyId, String key) {
		StringBundler sb = new StringBundler(5);

		sb.append(CACHE_NAME);
		sb.append(StringPool.POUND);
		sb.append(StringUtil.toHexString(companyId));
		sb.append(StringPool.POUND);
		sb.append(key);

		return sb.toString();
	}

	private static final PortalCache<String, CacheResponseData> _portalCache =
		MultiVMPoolUtil.getPortalCache(CACHE_NAME);
	private static final
		ServiceTrackerMap<String, List<RequestParameterCacheValidator>>
			_serviceTrackerMap = ServiceTrackerCollections.openMultiValueMap(
				RequestParameterCacheValidator.class,
				"filter.request.parameter");

}