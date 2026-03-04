/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.keymanager.internal.interceptor;

import com.liferay.keymanager.KeyResolverService;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.module.framework.ModuleServiceLifecycle;
import com.liferay.portal.kernel.util.PropsUtil;
import com.liferay.portal.kernel.util.Validator;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Tomas Polesovsky
 */
@Component(immediate = true, service = PortalPropertiesOverride.class)
public class PortalPropertiesOverride {

	@Activate
	protected void activate() {
		if (_log.isInfoEnabled()) {
			_log.info("Resolving key references in portal properties...");
		}

		int count = 0;

		for (String propertyName : _sensitiveProperties) {
			String value = PropsUtil.get(propertyName);

			if (Validator.isNotNull(value) &&
				_keyResolverService.isKeyReference(value)) {

				try {
					String resolved = _keyResolverService.resolve(value);

					PropsUtil.set(propertyName, resolved);

					count++;
				}
				catch (Exception e) {
					if (_log.isErrorEnabled()) {
						_log.error(
							"Failed to resolve key reference for " +
								"property: " + propertyName,
							e);
					}
				}
			}
		}

		if (_log.isInfoEnabled()) {
			_log.info(
				"Resolved " + count +
					" key references in portal properties");
		}
	}

	private static final Log _log = LogFactoryUtil.getLog(
		PortalPropertiesOverride.class);

	private static final Set<String> _sensitiveProperties;

	static {
		Set<String> sensitiveProperties = new HashSet<>();

		sensitiveProperties.add("amazon.access.key.id");
		sensitiveProperties.add("amazon.secret.access.key");
		sensitiveProperties.add("auth.token.shared.secret");
		sensitiveProperties.add("captcha.engine.recaptcha.key.private");
		sensitiveProperties.add("captcha.engine.recaptcha.key.site");
		sensitiveProperties.add("dl.store.s3.access.key");
		sensitiveProperties.add("dl.store.s3.secret.key");
		sensitiveProperties.add("jdbc.default.password");
		sensitiveProperties.add("ldap.security.credentials");
		sensitiveProperties.add("mail.session.mail.pop3.password");
		sensitiveProperties.add("mail.session.mail.smtp.password");
		sensitiveProperties.add("tunneling.servlet.shared.secret");

		_sensitiveProperties = Collections.unmodifiableSet(
			sensitiveProperties);
	}

	@Reference
	private KeyResolverService _keyResolverService;

	@Reference(target = ModuleServiceLifecycle.PORTAL_INITIALIZED)
	private ModuleServiceLifecycle _portalInitialized;

}
