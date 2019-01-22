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

import com.liferay.multi.factor.authentication.otp.exception.NoSuchHOTPException;
import com.liferay.multi.factor.authentication.otp.model.HOTP;
import com.liferay.multi.factor.authentication.otp.service.base.HOTPLocalServiceBaseImpl;
import com.liferay.portal.kernel.exception.PortalException;

/**
 * The implementation of the hotp local service.
 *
 * <p>
 * All custom service methods should be put in this class. Whenever methods are added, rerun ServiceBuilder to copy their definitions into the {@link com.liferay.multi.factor.authentication.otp.service.HOTPLocalService} interface.
 *
 * <p>
 * This is a local service. Methods of this service will not have security checks based on the propagated JAAS credentials because this service can only be accessed from within the same VM.
 * </p>
 *
 * @author arthurchan35
 * @see HOTPLocalServiceBaseImpl
 * @see com.liferay.multi.factor.authentication.otp.service.HOTPLocalServiceUtil
 */
public class HOTPLocalServiceImpl extends HOTPLocalServiceBaseImpl {

	/**
	 * NOTE FOR DEVELOPERS:
	 *
	 * Never reference this class directly. Always use {@link com.liferay.multi.factor.authentication.otp.service.HOTPLocalServiceUtil} to access the hotp local service.
	 */
	@Override
	public HOTP addFailedAttempts(long hotpId) throws PortalException {
		HOTP hotp = hotpPersistence.findByPrimaryKey(hotpId);

		hotp.setFailedAttempts(hotp.getFailedAttempts() + 1);

		hotpPersistence.update(hotp);

		return hotp;
	}

	public HOTP addHOTP(long userId, String sharedSecret)
		throws PortalException {

		HOTP hotp = fetchHOTPByUserId(userId);

		if (hotp != null) {
			throw new NoSuchHOTPException(
				"Each user can have only one HOTP configuration");
		}

		long hotpId = counterLocalService.increment();

		hotp = hotpPersistence.create(hotpId);

		hotp.setUserId(userId);

		hotp.setCount(0);

		hotp.setSharedSecret(sharedSecret);

		hotpPersistence.update(hotp);

		return hotp;
	}

	public HOTP deleteHOTP(long userId) throws PortalException {
		HOTP hotp = fetchHOTPByUserId(userId);

		hotpPersistence.remove(hotp);

		return hotp;
	}

	@Override
	public HOTP fetchHOTPByUserId(long userId) {
		return hotpPersistence.fetchByUserId(userId);
	}

	@Override
	public HOTP resetFailedAttempts(long hotpId) throws PortalException {
		HOTP hotp = hotpPersistence.findByPrimaryKey(hotpId);

		hotp.setFailedAttempts(0);

		hotpPersistence.update(hotp);

		return hotp;
	}

	@Override
	public HOTP resync(long hotpId, int increment) throws PortalException {
		HOTP hotp = hotpPersistence.findByPrimaryKey(hotpId);

		hotp.setCount(hotp.getCount() + increment);
		hotp.setFailedAttempts(0);
		hotp.setVerified(true);

		hotpPersistence.update(hotp);

		return hotp;
	}

	@Override
	public HOTP updateVerified(long hotpId, boolean verified)
		throws PortalException {

		HOTP hotp = hotpPersistence.findByPrimaryKey(hotpId);

		if (verified != hotp.isVerified()) {
			hotp.setCount(hotp.getCount() + 1);

			if (verified) {
				hotp.setFailedAttempts(0);
			}

			hotp.setVerified(verified);

			hotpPersistence.update(hotp);
		}

		return hotp;
	}

}