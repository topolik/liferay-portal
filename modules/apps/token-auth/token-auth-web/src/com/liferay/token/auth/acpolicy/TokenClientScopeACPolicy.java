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

package com.liferay.token.auth.acpolicy;

import com.liferay.portal.kernel.cache.CacheRegistryItem;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.util.CharPool;
import com.liferay.portal.kernel.util.PropsUtil;
import com.liferay.portal.kernel.util.ProxyUtil;
import com.liferay.portal.kernel.util.StringPool;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.security.ac.AccessControlPolicy;
import com.liferay.portal.security.ac.AccessControlThreadLocal;
import com.liferay.portal.security.ac.AccessControlUtil;
import com.liferay.portal.security.ac.AccessControlled;
import com.liferay.portal.security.ac.BaseAccessControlPolicy;
import com.liferay.portal.security.auth.AccessControlContext;
import com.liferay.token.auth.TokenAuthVerifier;
import com.liferay.token.auth.model.TokenClient;
import org.osgi.service.component.ComponentContext;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;

import java.lang.reflect.Method;
import java.util.Dictionary;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

/**
 * @author Tomas Polesovsky
 */
@Component(
	immediate = true,
	property = {
		"security-manager-services-profile[read]=*#get*,*#is*,*#has*,*#fetch*",
		"security-manager-services-profile[write]=*"
	},
	service = AccessControlPolicy.class
)
public class TokenClientScopeACPolicy extends BaseAccessControlPolicy {

	@Activate
	public void activate(ComponentContext componentContext) {
		Dictionary<String, Object> componentProperties =
			componentContext.getProperties();

		Enumeration<String> componentPropertiesKeys =
			componentProperties.keys();

		while(componentPropertiesKeys.hasMoreElements()) {
			String key = componentPropertiesKeys.nextElement();

			if (!key.startsWith(_SECURITY_MANAGER_SERVICES_PROFILE)) {
				continue;
			}

			String profileName = getProfileName(key);
			if(profileName != null) {
				_profiles.put(profileName, StringUtil.split(
					(String)componentProperties.get(key)));
			}
		}

		Properties customProfiles =
			PropsUtil.getProperties(_SECURITY_MANAGER_SERVICES_PROFILE, false);

		for (String key : customProfiles.stringPropertyNames()) {
			String profileName = getProfileName(key);
			if(profileName != null) {
				_profiles.put(profileName, StringUtil.split(
					customProfiles.getProperty(key)));
			}
		}
	}

	protected String getProfileName(String key) {
		int pos = key.indexOf(CharPool.OPEN_BRACKET);

		if (pos < 0) {
			if(_log.isWarnEnabled()) {
				_log.warn("Invalid profile name " + key + ". Ignoring.");
			}

			return null;
		}

		return key.substring(pos + 1, key.length() - 1);
	}

	@Override
	public void onServiceRemoteAccess(
			Method method, Object[] arguments,
			AccessControlled accessControlled)
		throws SecurityException {

		AccessControlContext accessControlContext =
			AccessControlUtil.getAccessControlContext();

		Map<String, Object> settings = accessControlContext.getSettings();

		TokenClient tokenClient = (TokenClient)settings.get(
			TokenAuthVerifier.TOKEN_CLIENT);

		if (tokenClient == null) {
			return;
		}

		Properties configuration = tokenClient.getConfiguration();
		String profile = configuration.getProperty(
			_SECURITY_MANAGER_SERVICES_PROFILE);

		String className = getServiceInterfaceName(getClass(
			method.getDeclaringClass()).getName());
		String methodFullName = className + CharPool.POUND + method.getName();


		if (Validator.isNotNull(profile)) {
			String[] profileAllowedServices = _profiles.get(profile);
			if (hasService(methodFullName, profileAllowedServices)) {
				return;
			}
		}

		String[] allowedServices = StringUtil.split(configuration.getProperty(
			_SECURITY_MANAGER_SERVICES, ""));

		if (hasService(methodFullName, allowedServices)) {
			return;
		}

		throw new SecurityException(
			"Method " + methodFullName + " is not allowed to be executed by " +
				tokenClient.getClientName() + " with id " +
				tokenClient.getClientId() + ", no matching " +
				_SECURITY_MANAGER_SERVICES + " found in the application " +
				"configuration");
	}

	protected boolean hasService(
		String methodFullName, String[] allowedServices) {

		if (_log.isDebugEnabled()) {
			_log.debug(
				"Checking method " + methodFullName + ", allowed services : " +
					StringUtil.merge(allowedServices));
		}

		for(String allowedService : allowedServices) {
			if(Validator.isNull(allowedService)) {
				continue;
			}

			if (allowedService.equals(StringPool.STAR)) {
				return true;
			}

			if (allowedService.indexOf(CharPool.POUND) < 0) {
				allowedService += CharPool.POUND + CharPool.STAR;
			}

			if (StringUtil.wildcardMatches(
				methodFullName, allowedService, CharPool.QUESTION,
				CharPool.STAR, CharPool.PERCENT, true)) {

				return true;
			}
		}

		return false;
	}


	public static Class<?> getClass(Object object) {
		Class<?> clazz = object.getClass();

		if (object instanceof Class) {
			clazz = (Class<?>)object;
		}

		if (ProxyUtil.isProxyClass(clazz) || !clazz.isInterface()) {
			Class<?>[] interfaces = clazz.getInterfaces();

			if (interfaces.length > 0) {
				clazz = interfaces[0];

				if (clazz.equals(CacheRegistryItem.class) &&
					(interfaces.length > 1)) {

					clazz = interfaces[1];
				}
			}
		}

		return clazz;
	}

	public static String getServiceInterfaceName(String serviceClassName) {
		int pos = serviceClassName.indexOf(".impl.");

		if (pos != -1) {
			serviceClassName =
				serviceClassName.substring(0, pos + 1) +
					serviceClassName.substring(pos + 6);
		}

		if (serviceClassName.endsWith("Impl")) {
			serviceClassName = serviceClassName.substring(
				0, serviceClassName.length() - 4);
		}

		return serviceClassName;
	}

	private static Log _log = LogFactoryUtil.getLog(
		TokenClientScopeACPolicy.class);

	private static final String _SECURITY_MANAGER_SERVICES =
		"security-manager-services";

	private static final String _SECURITY_MANAGER_SERVICES_PROFILE =
		"security-manager-services-profile";

	private Map<String, String[]> _profiles = new HashMap<String, String[]>();

}
