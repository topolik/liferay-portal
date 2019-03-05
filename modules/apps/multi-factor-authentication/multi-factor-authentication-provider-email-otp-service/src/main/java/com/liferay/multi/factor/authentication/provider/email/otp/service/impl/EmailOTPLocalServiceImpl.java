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

package com.liferay.multi.factor.authentication.provider.email.otp.service.impl;

import com.liferay.multi.factor.authentication.provider.email.otp.exception.NoSuchEmailOTPException;
import com.liferay.multi.factor.authentication.provider.email.otp.model.EmailOTP;
import com.liferay.multi.factor.authentication.provider.email.otp.service.base.EmailOTPLocalServiceBaseImpl;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.model.User;

import java.util.Date;

/**
 * The implementation of the email otp local service.
 *
 * <p>
 * All custom service methods should be put in this class. Whenever methods are added, rerun ServiceBuilder to copy their definitions into the {@link com.liferay.multi.factor.authentication.provider.otp.service.OTPLocalService} interface.
 *
 * <p>
 * This is a local service. Methods of this service will not have security checks based on the propagated JAAS credentials because this service can only be accessed from within the same VM.
 * </p>
 *
 * @author arthurchan35
 * @see EmailOTPLocalServiceBaseImpl
 * @see com.liferay.multi.factor.authentication.provider.email.otp.service.EmailOTPLocalServiceUtil
 */
public class EmailOTPLocalServiceImpl extends EmailOTPLocalServiceBaseImpl {

	/**
	 * NOTE FOR DEVELOPERS:
	 *
	 * Never reference this class directly. Always use {@link com.liferay.multi.factor.authentication.provider.otp.service.OTPLocalServiceUtil} to access the otp local service.
	 */
	public EmailOTP addEmailOTP(long userId, String email, String ip)
		throws PortalException {

		EmailOTP emailOTP = fetchEmailOTPByUserId(userId);

		if (emailOTP != null) {
			throw new NoSuchEmailOTPException(
				"Each user can have only one Email OTP configuration");
		}

		User user = userLocalService.getUserById(userId);

		long emailOTPId = counterLocalService.increment();

		emailOTP = emailOTPPersistence.create(emailOTPId);

		Date date = new Date();

		emailOTP.setCompanyId(user.getCompanyId());
		emailOTP.setUserId(userId);
		emailOTP.setUserName(user.getFullName());
		emailOTP.setCreateDate(date);
		emailOTP.setModifiedDate(date);
		emailOTP.setEmailAddress(email);
		emailOTP.setLastSuccessDate(date);
		emailOTP.setLastSuccessIP(ip);
		emailOTP.setFailedAttempts(0);

		return emailOTPPersistence.update(emailOTP);
	}

	@Override
	public EmailOTP deleteEmailOTPByUserId(long userId) throws PortalException {
		EmailOTP emailOTP = emailOTPPersistence.fetchByUserId(userId);

		return emailOTPPersistence.remove(emailOTP.getEmailOTPId());
	}

	@Override
	public EmailOTP fetchEmailOTPByUserId(long userId) {
		return emailOTPPersistence.fetchByUserId(userId);
	}

	@Override
	public EmailOTP updateAttempts(long userId, boolean success, String ip)
		throws PortalException {

		EmailOTP emailOTP = emailOTPPersistence.fetchByUserId(userId);

		Date date = new Date();

		emailOTP.setModifiedDate(date);

		if (success) {
			emailOTP.setLastSuccessDate(date);
			emailOTP.setLastSuccessIP(ip);
			emailOTP.setFailedAttempts(0);
		}
		else {
			//there should be a throttling from admin configuration
			int failedAttempts = emailOTP.getFailedAttempts();

			emailOTP.setFailedAttempts(failedAttempts + 1);

			emailOTP.setLastFailDate(date);
			emailOTP.setLastFailIP(ip);
		}

		return emailOTPPersistence.update(emailOTP);
	}

}