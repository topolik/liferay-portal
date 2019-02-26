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

package com.liferay.multi.factor.authentication.portlet.web.internal.user.settings;

import com.liferay.frontend.taglib.servlet.taglib.ScreenNavigationEntry;
import com.liferay.multi.factor.authentication.spi.verifier.UserAccountSetupMFAVerifier;
import com.liferay.osgi.util.ServiceTrackerFactory;
import com.liferay.portal.kernel.util.HashMapDictionary;
import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.osgi.framework.FrameworkUtil;
import org.osgi.framework.ServiceReference;
import org.osgi.framework.ServiceRegistration;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Deactivate;
import org.osgi.service.component.annotations.Reference;
import org.osgi.util.tracker.ServiceTracker;
import org.osgi.util.tracker.ServiceTrackerCustomizer;

import javax.servlet.ServletContext;
import java.util.Collections;
import java.util.Dictionary;
import java.util.HashMap;
import java.util.Map;

/**
 * @author Tomas Polesovsky
 */
@Component(immediate = true)
public class UserAccountSetupMFAVerifierTracker {

	private ServiceTracker<UserAccountSetupMFAVerifier, ServiceRegistration<ScreenNavigationEntry>> _serviceTracker;
	private BundleContext _bundleContext;

	@Activate
	protected void activate(BundleContext bundleContext) {
		_bundleContext = bundleContext;

		_serviceTracker = ServiceTrackerFactory.open(
			bundleContext, UserAccountSetupMFAVerifier.class,
			new UserAccountSetupMFAVerifierServiceTrackerCustomizer());
	}

	@Deactivate
	protected void deactivate() {
		_serviceTracker.close();

		for (ServiceRegistration<ScreenNavigationEntry> serviceRegistration :
				_serviceRegistrationsMap.values()) {

			serviceRegistration.unregister();
		}
	}

	class UserAccountSetupMFAVerifierServiceTrackerCustomizer
		implements ServiceTrackerCustomizer<UserAccountSetupMFAVerifier, ServiceRegistration<ScreenNavigationEntry>> {

		@Override
		public ServiceRegistration<ScreenNavigationEntry> addingService(
			ServiceReference<UserAccountSetupMFAVerifier> reference) {

			UserAccountSetupMFAVerifier userAccountSetupMFAVerifier =
				_bundleContext.getService(reference);

			Dictionary<String, Object> dictionary = new HashMapDictionary<>();

			dictionary.put("screen.navigation.entry.order", userAccountSetupMFAVerifier.hashCode());

			UserAccountSetupMFAScreenNavigationEntry
				userAccountSetupMFAScreenNavigationEntry =
				new UserAccountSetupMFAScreenNavigationEntry(
					userAccountSetupMFAVerifier);

			userAccountSetupMFAScreenNavigationEntry.setServletContext(_servletContext);

			return _bundleContext.registerService(
				ScreenNavigationEntry.class,
				userAccountSetupMFAScreenNavigationEntry,
				dictionary);

		}

		@Override
		public void modifiedService(
			ServiceReference<UserAccountSetupMFAVerifier> reference,
			ServiceRegistration<ScreenNavigationEntry> service) {

		}

		@Override
		public void removedService(
			ServiceReference<UserAccountSetupMFAVerifier> reference,
			ServiceRegistration<ScreenNavigationEntry> service) {

			service.unregister();

			_bundleContext.ungetService(reference);
		}
	}

//	@Reference(
//		policy = ReferencePolicy.DYNAMIC,
//		policyOption = ReferencePolicyOption.GREEDY
//	)
	public void setUserAccountSetupMFAVerifier(
		UserAccountSetupMFAVerifier userAccountSetupMFAVerifier) {

		Bundle bundle = FrameworkUtil.getBundle(getClass());

		BundleContext bundleContext = bundle.getBundleContext();

		Dictionary<String, Object> dictionary = new HashMapDictionary<>();

		String providerName = userAccountSetupMFAVerifier.getProviderName();

		dictionary.put("screen.navigation.entry.order", providerName.hashCode());

		UserAccountSetupMFAScreenNavigationEntry
			userAccountSetupMFAScreenNavigationEntry =
			new UserAccountSetupMFAScreenNavigationEntry(
				userAccountSetupMFAVerifier);

		userAccountSetupMFAScreenNavigationEntry.setServletContext(_servletContext);

		_serviceRegistrationsMap.put(
			providerName,
			bundleContext.registerService(
				ScreenNavigationEntry.class,
				userAccountSetupMFAScreenNavigationEntry, dictionary));

	}

	public void unsetUserAccountSetupMFAVerifier(
		UserAccountSetupMFAVerifier userAccountSetupMFAVerifier) {

		ServiceRegistration<ScreenNavigationEntry> serviceRegistration =
			_serviceRegistrationsMap.remove(
				userAccountSetupMFAVerifier.getProviderName());

		serviceRegistration.unregister();
	}

	private Map<String, ServiceRegistration<ScreenNavigationEntry>>
		_serviceRegistrationsMap = Collections.synchronizedMap(new HashMap<>());


	@Reference(
		target = "(osgi.web.symbolicname=com.liferay.multi.factor.authentication.portlet.web)"
	)
	private ServletContext _servletContext;

}
