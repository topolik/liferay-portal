/**
 * Copyright (c) 2000-present Liferay, Inc. All rights reserved.
 * <p>
 * This library is free software; you can redistribute it and/or modify it under
 * the terms of the GNU Lesser General Public License as published by the Free
 * Software Foundation; either version 2.1 of the License, or (at your option)
 * any later version.
 * <p>
 * This library is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more
 * details.
 */

package com.liferay.oauth2.provider.scopes.impl;

import com.liferay.osgi.service.tracker.collections.map.PropertyServiceReferenceMapper;
import com.liferay.osgi.service.tracker.collections.map.ServiceReferenceMapper;
import com.liferay.osgi.service.tracker.collections.map.ServiceTrackerMap;
import com.liferay.osgi.service.tracker.collections.map.ServiceTrackerMapFactory;
import com.liferay.portal.kernel.util.StringBundler;
import com.liferay.portal.kernel.util.Supplier;
import org.osgi.framework.BundleContext;

public class ScopedServiceTrackerMap<T> {

	private Supplier<T> _defaultServiceSupplier;

	public ScopedServiceTrackerMap(
		BundleContext bundleContext, Class<T> clazz, String property,
		Supplier<T> defaultServiceSupplier) {

		_defaultServiceSupplier = defaultServiceSupplier;

		_servicesByCompany = ServiceTrackerMapFactory.openSingleValueMap(
			bundleContext, clazz,
			StringBundler.concat("(&(companyId=*)(!(", property, "=*)))"),
			new PropertyServiceReferenceMapper<>("companyId"));

		_servicesByKey = ServiceTrackerMapFactory.openSingleValueMap(
			bundleContext, clazz,
			StringBundler.concat("(&(", property, "=*)(!(companyId=*)))"),
			new PropertyServiceReferenceMapper<>(property));

		_servicesByCompanyAndKey =
			ServiceTrackerMapFactory.openSingleValueMap(
				bundleContext, clazz,
				StringBundler.concat("(&(companyId=*)(", property, "=*))"),
				(serviceReference, emitter) -> {
					ServiceReferenceMapper<String, T>
						companyMapper = new PropertyServiceReferenceMapper<>(
							"companyId");
					ServiceReferenceMapper<String, T>
						nameMapper = new PropertyServiceReferenceMapper<>(
							property);

					companyMapper.map(
						serviceReference,
						key1 -> nameMapper.map(
							serviceReference,
							key2 -> emitter.emit(
								StringBundler.concat(key1, "-", key2))));
				});
	}

	public T getService(long companyId, String key) {
		String companyIdString = Long.toString(companyId);
		T service = _servicesByCompanyAndKey.getService(
			StringBundler.concat(companyIdString, "-", key));

		if (service != null) {
			return service;
		}

		service = _servicesByKey.getService(key);

		if (service != null) {
			return service;
		}

		service = _servicesByCompany.getService(companyIdString);

		if (service != null) {
			return service;
		}

		return _defaultServiceSupplier.get();
	}

	public void close() {
		_servicesByCompany.close();
		_servicesByCompanyAndKey.close();
		_servicesByKey.close();
	}

	private ServiceTrackerMap<String, T> _servicesByCompany;
	private ServiceTrackerMap<String, T> _servicesByCompanyAndKey;
	private ServiceTrackerMap<String, T> _servicesByKey;

}