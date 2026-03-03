package com.liferay.keymanager.internal.interceptor;

import com.liferay.keymanager.KeyResolverService;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.module.framework.ModuleServiceLifecycle;
import com.liferay.portal.kernel.util.PropsUtil;

import java.util.Set;

import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

@Component(immediate = true, service = PortalPropertiesOverride.class)
public class PortalPropertiesOverride {

	private static final Set<String> _SENSITIVE_PROPERTIES = Set.of(
		"mail.session.mail.smtp.password",
		"mail.session.mail.pop3.password",
		"jdbc.default.password",
		"ldap.security.credentials",
		"dl.store.s3.secret.key",
		"dl.store.s3.access.key",
		"captcha.engine.recaptcha.key.private",
		"captcha.engine.recaptcha.key.site",
		"amazon.access.key.id",
		"amazon.secret.access.key",
		"tunneling.servlet.shared.secret",
		"auth.token.shared.secret"
	);

	@Activate
	protected void activate() {
		if (_log.isInfoEnabled()) {
			_log.info("Resolving key references in portal properties...");
		}

		int count = 0;

		for (String propertyName : _SENSITIVE_PROPERTIES) {
			String value = PropsUtil.get(propertyName);

			if (value != null && _keyResolverService.isKeyReference(value)) {
				try {
					String resolved = _keyResolverService.resolve(value);

					PropsUtil.set(propertyName, resolved);

					count++;
				}
				catch (Exception e) {
					_log.error("Failed to resolve key reference for property: " + propertyName, e);
				}
			}
		}

		if (_log.isInfoEnabled()) {
			_log.info("Resolved " + count + " key references in portal properties");
		}
	}

	@Reference
	private KeyResolverService _keyResolverService;

	@Reference(target = ModuleServiceLifecycle.PORTAL_INITIALIZED)
	private ModuleServiceLifecycle _portalInitialized;

	private static final Log _log = LogFactoryUtil.getLog(PortalPropertiesOverride.class);

}
