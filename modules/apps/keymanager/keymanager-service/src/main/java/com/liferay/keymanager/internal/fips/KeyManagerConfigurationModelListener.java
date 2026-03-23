/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.keymanager.internal.fips;

import com.liferay.keymanager.spi.fips.FipsReport;
import com.liferay.keymanager.spi.fips.FipsValidator;
import com.liferay.osgi.service.tracker.collections.map.ServiceReferenceMapper;
import com.liferay.osgi.service.tracker.collections.map.ServiceTrackerMap;
import com.liferay.osgi.service.tracker.collections.map.ServiceTrackerMapFactory;
import com.liferay.portal.configuration.persistence.listener.ConfigurationModelListener;
import com.liferay.portal.configuration.persistence.listener.ConfigurationModelListenerException;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;

import java.util.Dictionary;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;

import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceReference;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Deactivate;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Tomas Polesovsky
 */
@Component(service = ConfigurationModelListener.class)
public class KeyManagerConfigurationModelListener
	implements ConfigurationModelListener {

	@Override
	public void onBeforeDelete(String pid)
		throws ConfigurationModelListenerException {
	}

	@Override
	public void onBeforeSave(String pid, Dictionary<String, Object> properties)
		throws ConfigurationModelListenerException {

		if (!pid.startsWith("com.liferay.keymanager.")) {
			return;
		}

		FipsValidator fipsValidator = _serviceTrackerMap.getService(pid);

		if (fipsValidator == null) {
			return;
		}

		Map<String, Object> propertiesMap = new HashMap<>();

		Enumeration<String> enumeration = properties.keys();

		while (enumeration.hasMoreElements()) {
			String key = enumeration.nextElement();

			propertiesMap.put(key, properties.get(key));
		}

		FipsReport fipsReport = fipsValidator.validate(propertiesMap);

		if (!fipsReport.isCompliant()) {
			if (_fipsComplianceChecker.isFipsEnforced()) {
				throw new ConfigurationModelListenerException(
					fipsReport.getViolationMessage(), getClass(), getClass(),
					properties);
			}

			if (_log.isWarnEnabled()) {
				_log.warn(
					"Configuration " + pid + " is not FIPS compliant: " +
						fipsReport.getViolationMessage());
			}
		}
	}

	@Activate
	protected void activate(BundleContext bundleContext) {
		_serviceTrackerMap = ServiceTrackerMapFactory.openSingleValueMap(
			bundleContext, FipsValidator.class, null,
			new ServiceReferenceMapper<String, FipsValidator>() {

				@Override
				public void map(
					ServiceReference<FipsValidator> serviceReference,
					Emitter<String> emitter) {

					FipsValidator fipsValidator = bundleContext.getService(
						serviceReference);

					if (fipsValidator != null) {
						try {
							emitter.emit(fipsValidator.getConfigurationPid());
						}
						finally {
							bundleContext.ungetService(serviceReference);
						}
					}
				}

			});
	}

	@Deactivate
	protected void deactivate() {
		_serviceTrackerMap.close();
	}

	private static final Log _log = LogFactoryUtil.getLog(
		KeyManagerConfigurationModelListener.class);

	@Reference
	private FipsComplianceChecker _fipsComplianceChecker;

	private ServiceTrackerMap<String, FipsValidator> _serviceTrackerMap;

}