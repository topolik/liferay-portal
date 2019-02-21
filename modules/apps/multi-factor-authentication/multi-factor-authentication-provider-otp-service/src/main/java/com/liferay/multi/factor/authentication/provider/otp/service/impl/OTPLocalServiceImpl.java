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

package com.liferay.multi.factor.authentication.provider.otp.service.impl;

import com.liferay.multi.factor.authentication.provider.otp.exception.NoSuchOTPException;
import com.liferay.multi.factor.authentication.provider.otp.model.OTP;
import com.liferay.multi.factor.authentication.provider.otp.service.base.OTPLocalServiceBaseImpl;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.model.User;

import java.util.Date;

/**
 * The implementation of the otp local service.
 *
 * <p>
 * All custom service methods should be put in this class. Whenever methods are added, rerun ServiceBuilder to copy their definitions into the {@link com.liferay.multi.factor.authentication.provider.otp.service.OTPLocalService} interface.
 *
 * <p>
 * This is a local service. Methods of this service will not have security checks based on the propagated JAAS credentials because this service can only be accessed from within the same VM.
 * </p>
 *
 * @author arthurchan35
 * @see OTPLocalServiceBaseImpl
 * @see com.liferay.multi.factor.authentication.provider.otp.service.OTPLocalServiceUtil
 */
public class OTPLocalServiceImpl extends OTPLocalServiceBaseImpl {

	/**
	 * NOTE FOR DEVELOPERS:
	 *
	 * Never reference this class directly. Always use {@link com.liferay.multi.factor.authentication.provider.otp.service.OTPLocalServiceUtil} to access the otp local service.
	 */
	public OTP addOTP(long userId, String email) throws PortalException {
		OTP otp = fetchOTPByUserId(userId);

		if (otp != null) {
			throw new NoSuchOTPException(
				"Each user can have only one OTP configuration");
		}

		User user = userLocalService.getUserById(userId);

		long otpId = counterLocalService.increment();

		otp = otpPersistence.create(otpId);

		otp.setCompanyId(user.getCompanyId());
		otp.setUserId(userId);
		otp.setEmailAddress(email);
		otp.setUserName(user.getFullName());
		otp.setCreateDate(new Date());
		otp.setVerified(false);

		return otpPersistence.update(otp);
	}

	@Override
	public OTP fetchOTPByUserId(long userId) {
		return otpPersistence.fetchByUserId(userId);
	}

	@Override
	public OTP updateVerified(long userId, boolean verified)
		throws PortalException {

		OTP otp = otpPersistence.fetchByUserId(userId);

		otp.setVerified(verified);

		if (verified) {
			otp.setFailedLoginAttempts(0);
		}
		else {
			int failedAttempts = otp.getFailedLoginAttempts();

			otp.setFailedLoginAttempts(failedAttempts + 1);
		}

		return otpPersistence.update(otp);
	}

}