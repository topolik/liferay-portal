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

package com.liferay.multi.factor.authentication.provider.email.otp.web.internal.configuration;

import aQute.bnd.annotation.metatype.Meta;
import com.liferay.portal.configuration.metatype.annotations.ExtendedObjectClassDefinition;
import com.liferay.portal.kernel.settings.LocalizedValuesMap;

/**
 * @author Tomas Polesovsky
 */
@ExtendedObjectClassDefinition(
	category = "multi-factor-authentication",
	factoryInstanceLabelAttribute = "name"
)
@Meta.OCD(
	id = "com.liferay.multi.factor.authentication.provider.email.otp.web.internal.configuration.EmailOTPConfiguration",
	localization = "content/Language",
	name = "email-otp-configuration-name"
)
public interface EmailOTPConfiguration {

	@Meta.AD(deflt = "true", name = "enabled", required = false)
	public boolean enabled();

	@Meta.AD(
		deflt = "email-one-time-password",
		description = "email-otp-name-description",
		name = "email-otp-name", required = false
	)
	public String name();

	@Meta.AD(
		deflt = "false",
		description = "force-user-setup-description",
		name = "force-user-setup", required = false
	)
	public boolean forceUserSetup();

	@Meta.AD(
		deflt = "300",
		description = "resend-email-timeout-description",
		name = "resend-email-timeout", required = false
	)
	public long resendEmailTimeout();

	@Meta.AD(
		deflt = "-1",
		description = "validation-expiration-time-description",
		name = "validation-expiration-time", required = false
	)
	public long validationExpirationTime();

	@Meta.AD(
		deflt = "false",
		description = "allow-custom-email-description",
		name = "allow-custom-email", required = false
	)
	public boolean allowCustomEmail();

	@Meta.AD(
		deflt = "noreply@liferay.com",
		description = "email-template-from-description",
		name = "email-template-from", required = false
	)
	public String emailTemplateFrom();

	@Meta.AD(
		deflt = "Test Test",
		description = "email-template-from-name-description",
		name = "email-template-from-name", required = false
	)
	public String emailTemplateFromName();

	@Meta.AD(
		deflt = "${resource:com/liferay/multi/factor/authentication/provider/email/otp/web/internal/configuration/email_subject.tmpl}",
		description = "email-template-subject-description",
		name = "email-template-subject", required = false
	)
	public LocalizedValuesMap emailTemplateSubject();

	@Meta.AD(
		deflt = "${resource:com/liferay/multi/factor/authentication/provider/email/otp/web/internal/configuration/email_body.tmpl}",
		description = "email-template-body-description",
		name = "email-template-body", required = false
	)
	public LocalizedValuesMap emailTemplateBody();

}
