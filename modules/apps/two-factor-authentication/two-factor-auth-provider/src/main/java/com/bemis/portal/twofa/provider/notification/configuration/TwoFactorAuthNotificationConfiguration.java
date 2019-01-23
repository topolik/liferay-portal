package com.bemis.portal.twofa.provider.notification.configuration;

import aQute.bnd.annotation.metatype.Meta;

import com.liferay.portal.configuration.metatype.annotations.ExtendedObjectClassDefinition;

/**
 * @author Prathima Shreenath
 */
@ExtendedObjectClassDefinition(category = "Bemis")
@Meta.OCD(
	id = "com.bemis.portal.twofa.provider.notification.configuration.TwoFactorAuthNotificationConfiguration",
	localization = "content/Language",
	name = "twofa.provider.notification.configuration.name"
)
public interface TwoFactorAuthNotificationConfiguration {

	@Meta.AD(deflt = "false", description = "enabled-help", required = false)
	public boolean enabled();

	@Meta.AD(
		deflt = "email", description = "notification-type-help",
		optionValues = {"email", "sms"}, required = false
	)
	public String twoFactorAuthNotificationType();

}