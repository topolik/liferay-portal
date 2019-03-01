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

package com.liferay.multi.factor.authentication.provider.ip.address.web.internal.spi.verifier;

import com.liferay.multi.factor.authentication.provider.ip.address.web.internal.configuration.IPAddressConfiguration;
import com.liferay.multi.factor.authentication.spi.verifier.HeadlessMFAVerifier;
import com.liferay.multi.factor.authentication.spi.verifier.MFAVerifier;
import com.liferay.portal.configuration.metatype.bnd.util.ConfigurableUtil;
import com.liferay.portal.kernel.security.access.control.AccessControlUtil;
import com.liferay.portal.kernel.service.UserLocalService;
import com.liferay.portal.kernel.util.Portal;
import com.liferay.portal.util.PropsValues;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.ConfigurationPolicy;
import org.osgi.service.component.annotations.Deactivate;
import org.osgi.service.component.annotations.Reference;

/**
 * @author arthurchan35
 */
@Component(
	configurationPid = "com.liferay.multi.factor.authentication.provider.ip.address.web.internal.configuration.IPAddressConfiguration",
	configurationPolicy = ConfigurationPolicy.REQUIRE, immediate = true,
	service = MFAVerifier.class
)
public class IPAddressMFAVerifier implements HeadlessMFAVerifier, MFAVerifier {

	@Activate
	public void activate() {
		if (PropsValues.SESSION_ENABLE_PHISHING_PROTECTION) {
			List<String> sessionPhishingProtectedAttributesList = new ArrayList(
				Arrays.asList(
					PropsValues.SESSION_PHISHING_PROTECTED_ATTRIBUTES));

			sessionPhishingProtectedAttributesList.add(_VALIDATED_AT);

			PropsValues.SESSION_PHISHING_PROTECTED_ATTRIBUTES =
				sessionPhishingProtectedAttributesList.toArray(
					new String[sessionPhishingProtectedAttributesList.size()]);
		}
	}

	@Override
	public String getName() {
		return _name;
	}

	@Override
	public String getProviderName() {
		return "ip-address";
	}

	@Override
	public boolean isEnabled() {
		return true;
	}

	@Override
	public boolean requiresHeadlessVerification(
		HttpServletRequest request, long userId) {

		HttpServletRequest originalServletRequest =
			_portal.getOriginalServletRequest(request);

		HttpSession session = originalServletRequest.getSession(false);

		if (isValid(session)) {
			return false;
		}

		return true;
	}

	@Override
	public boolean verifyHeadlessRequest(
		HttpServletRequest request, long userId) {

		return AccessControlUtil.isAccessAllowed(request, _allowedIPsWithMasks);
	}

	@Activate
	protected void activate(Map<String, Object> properties) {
		IPAddressConfiguration config = ConfigurableUtil.createConfigurable(
			IPAddressConfiguration.class, properties);

		_enabled = config.enabled();
		_name = config.name();
		_allowedIPsWithMasks = new HashSet<>(
			Arrays.asList(config.allowedIPsWithMasks()));

	}

	@Deactivate
	protected void deactivate() {
		if (PropsValues.SESSION_ENABLE_PHISHING_PROTECTION) {
			List<String> sessionPhishingProtectedAttributesList = new ArrayList(
				Arrays.asList(
					PropsValues.SESSION_PHISHING_PROTECTED_ATTRIBUTES));

			sessionPhishingProtectedAttributesList.remove(_VALIDATED_AT);

			PropsValues.SESSION_PHISHING_PROTECTED_ATTRIBUTES =
				sessionPhishingProtectedAttributesList.toArray(
					new String[sessionPhishingProtectedAttributesList.size()]);
		}
	}

	protected boolean isValid(HttpSession httpSession) {
		if (httpSession == null) {
			return false;
		}

		Object validatedAtObject = httpSession.getAttribute(_VALIDATED_AT);

		if (validatedAtObject != null) {
			if (_validationExpirationTime < 0) {
				return true;
			}

			long validatedAt = (Long)validatedAtObject;

			if (validatedAt + _validationExpirationTime * 1000 >
					System.currentTimeMillis()) {

				return true;
			}
		}

		return false;
	}

	private Set<String> _allowedIPsWithMasks;
	private boolean _enabled;
	private String _name;

	@Reference
	private Portal _portal;

	@Reference(
		target = "(osgi.web.symbolicname=com.liferay.multi.factor.authentication.provider.ip.address.web)"
	)
	private ServletContext _servletContext;

	@Reference
	private UserLocalService _userLocalService;

}