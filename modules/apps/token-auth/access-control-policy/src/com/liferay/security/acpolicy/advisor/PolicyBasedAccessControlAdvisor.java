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

package com.liferay.security.acpolicy.advisor;

import com.liferay.portal.kernel.bean.PortalBeanLocatorUtil;
import com.liferay.portal.security.ac.AccessControlAdvice;
import com.liferay.portal.security.ac.AccessControlAdvisor;
import com.liferay.portal.security.ac.AccessControlUtil;
import com.liferay.portal.security.ac.AccessControlled;
import com.liferay.portal.security.auth.AccessControlContext;
import com.liferay.registry.Registry;
import com.liferay.registry.RegistryUtil;
import com.liferay.registry.ServiceTracker;
import com.liferay.security.acpolicy.AccessControlPolicy;
import org.osgi.service.component.ComponentContext;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Deactivate;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Map;

/**
 * @author Tomas Polesovsky
 */
@Component(
	immediate = true,
	service = AccessControlAdvisor.class
)
public class PolicyBasedAccessControlAdvisor implements AccessControlAdvisor {

	@Override
	public void accept(Method method, AccessControlled accessControlled)
		throws SecurityException {

		_originalAdvisor.accept(method, accessControlled);

		acceptPolicies(method, accessControlled);
	}

	private void acceptPolicies(
			Method method, AccessControlled accessControlled)
		throws SecurityException {

		AccessControlContext accessControlContext =
			AccessControlUtil.getAccessControlContext();

		Map<String, Object> settings = accessControlContext.getSettings();

		if (settings.containsKey(_POLICIES_CHECKED)) {
			return;
		}

		AccessControlPolicy[] policies =
			_accessControlPolicyServiceTracker.getServices(
				new AccessControlPolicy[0]);

		for (AccessControlPolicy policy : policies) {
			policy.check(method, accessControlled, accessControlContext);
		}

		settings.put(_POLICIES_CHECKED, Boolean.TRUE);
	}

	@Activate
	public void activate(ComponentContext componentContext) {
		Registry registry= RegistryUtil.getRegistry();

		_accessControlPolicyServiceTracker = registry.trackServices(
			AccessControlPolicy.class);

		_accessControlPolicyServiceTracker.open();

		//TODO: why is AccessControlAdvice among
		//TODO: module.framework.services.ignored.interfaces ?
		_accessControlAdvice =
			(AccessControlAdvice)PortalBeanLocatorUtil.getBeanLocator().locate(
				"accessControlAdvice");

		// TODO: and has no getter :(
		Field accessControlAdvisorField = null;
		try {
			accessControlAdvisorField =
				AccessControlAdvice.class.getDeclaredField(
					"_accessControlAdvisor");

		} catch (NoSuchFieldException e) {
			throw new RuntimeException(
				"Unable to get AccessControlAdvice._accessControlAdvisor " +
					"field using reflection!", e);
		}

		accessControlAdvisorField.setAccessible(true);
		try {
			_originalAdvisor =
				(AccessControlAdvisor)accessControlAdvisorField.get(
					_accessControlAdvice);

		} catch (IllegalAccessException e) {
			throw new RuntimeException(
				"Unable to get AccessControlAdvice._accessControlAdvisor " +
					"field value using reflection!", e);
		} finally {
			accessControlAdvisorField.setAccessible(false);
		}

		_accessControlAdvice.setAccessControlAdvisor(this);
	}

	@Deactivate
	public void deactivate(ComponentContext componentContext) {
		_accessControlAdvice.setAccessControlAdvisor(_originalAdvisor);
	}

	private static final String _POLICIES_CHECKED =
		PolicyBasedAccessControlAdvisor.class.getName() + "_POLICIES_CHECKED";

	private AccessControlAdvice _accessControlAdvice;
	private AccessControlAdvisor _originalAdvisor;
	private ServiceTracker<?, AccessControlPolicy>
		_accessControlPolicyServiceTracker;

}
