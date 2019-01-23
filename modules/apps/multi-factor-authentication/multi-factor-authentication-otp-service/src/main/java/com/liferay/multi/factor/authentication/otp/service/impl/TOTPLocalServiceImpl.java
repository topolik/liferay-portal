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

package com.liferay.multi.factor.authentication.otp.service.impl;

import com.liferay.multi.factor.authentication.otp.exception.NoSuchTOTPException;
import com.liferay.multi.factor.authentication.otp.model.TOTP;
import com.liferay.multi.factor.authentication.otp.service.base.TOTPLocalServiceBaseImpl;
import com.liferay.portal.kernel.exception.PortalException;

/**
 * The implementation of the totp local service.
 *
 * <p>
 * All custom service methods should be put in this class. Whenever methods are added, rerun ServiceBuilder to copy their definitions into the {@link com.liferay.multi.factor.authentication.otp.service.TOTPLocalService} interface.
 *
 * <p>
 * This is a local service. Methods of this service will not have security checks based on the propagated JAAS credentials because this service can only be accessed from within the same VM.
 * </p>
 *
 * @author arthurchan35
 * @see TOTPLocalServiceBaseImpl
 * @see com.liferay.multi.factor.authentication.otp.service.TOTPLocalServiceUtil
 */
public class TOTPLocalServiceImpl extends TOTPLocalServiceBaseImpl {

	/**
	 * NOTE FOR DEVELOPERS:
	 *
	 * Never reference this class directly. Always use {@link com.liferay.multi.factor.authentication.otp.service.TOTPLocalServiceUtil} to access the totp local service.
	 */
	@Override
	public TOTP addFailedAttempts(long totpId) throws PortalException {
		TOTP totp = totpPersistence.findByPrimaryKey(totpId);

		totp.setFailedAttempts(totp.getFailedAttempts() + 1);

		totpPersistence.update(totp);

		return totp;
	}

	public TOTP addTOTP(long userId, String sharedSecret)
		throws PortalException {

		TOTP totp = fetchTOTPByUserId(userId);

		if (totp != null) {
			throw new NoSuchTOTPException(
				"Each user can have only one TOTP configuration");
		}

		long totpId = counterLocalService.increment();

		totp = totpPersistence.create(totpId);

		totp.setUserId(userId);

		totp.setSharedSecret(sharedSecret);

		totpPersistence.update(totp);

		return totp;
	}

	public TOTP deleteTOTP(long userId) throws PortalException {
		TOTP totp = fetchTOTPByUserId(userId);

		totpPersistence.remove(totp);

		return totp;
	}

	@Override
	public TOTP fetchTOTPByUserId(long userId) {
		return totpPersistence.fetchByUserId(userId);
	}

	@Override
	public TOTP resetFailedAttempts(long totpId) throws PortalException {
		TOTP totp = totpPersistence.findByPrimaryKey(totpId);

		totp.setFailedAttempts(0);

		totpPersistence.update(totp);

		return totp;
	}

	@Override
	public TOTP updateVerified(long totpId, boolean verified)
		throws PortalException {

		TOTP totp = totpPersistence.findByPrimaryKey(totpId);

		if (verified != totp.isVerified()) {
			if (verified) {
				totp.setFailedAttempts(0);
			}

			totp.setVerified(verified);

			totpPersistence.update(totp);
		}

		return totp;
	}

}