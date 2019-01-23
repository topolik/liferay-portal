package com.bemis.portal.twofa.provider.notification.configuration;

import com.liferay.portal.configuration.metatype.bnd.util.ConfigurableUtil;

import java.util.Map;

import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Modified;

/**
 * @author Prathima Shreenath
 */
@Component(
	configurationPid = "com.bemis.portal.twofa.provider.notification.configuration.TwoFactorAuthNotificationConfiguration",
	immediate = true, service = TwoFactorAuthNotificationConfigurator.class
)
public class TwoFactorAuthNotificationConfigurator {

	public String get2FANotificationType() {
		return _twoFaNotificationConfiguration.twoFactorAuthNotificationType();
	}

	public boolean is2FactorAuthEnabled() {
		return _twoFaNotificationConfiguration.enabled();
	}

	@Activate
	protected void activate(Map<String, Object> properties) {
		_twoFaNotificationConfiguration = ConfigurableUtil.createConfigurable(
			TwoFactorAuthNotificationConfiguration.class, properties);
	}

	@Modified
	protected void modified(Map<String, Object> properties) {
		activate(properties);
	}

	private volatile TwoFactorAuthNotificationConfiguration
		_twoFaNotificationConfiguration;

}