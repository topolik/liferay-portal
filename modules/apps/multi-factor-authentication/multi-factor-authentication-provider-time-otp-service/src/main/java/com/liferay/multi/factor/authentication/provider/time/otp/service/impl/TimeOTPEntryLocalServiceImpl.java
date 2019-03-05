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

package com.liferay.multi.factor.authentication.provider.time.otp.service.impl;

import com.liferay.multi.factor.authentication.provider.time.otp.exception.NoSuchEntryException;
import com.liferay.multi.factor.authentication.provider.time.otp.model.TimeOTPEntry;
import com.liferay.multi.factor.authentication.provider.time.otp.service.base.TimeOTPEntryLocalServiceBaseImpl;
import com.liferay.portal.aop.AopService;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.model.User;

import java.util.Date;

import org.osgi.service.component.annotations.Component;

/**
 * The implementation of the time otp entry local service.
 *
 * <p>
 * All custom service methods should be put in this class. Whenever methods are added, rerun ServiceBuilder to copy their definitions into the <code>com.liferay.multi.factor.authentication.provider.time.otp.service.TimeOTPEntryLocalService</code> interface.
 *
 * <p>
 * This is a local service. Methods of this service will not have security checks based on the propagated JAAS credentials because this service can only be accessed from within the same VM.
 * </p>
 *
 * @author arthurchan35
 * @see TimeOTPEntryLocalServiceBaseImpl
 */
@Component(
	property = "model.class.name=com.liferay.multi.factor.authentication.provider.time.otp.model.TimeOTPEntry",
	service = AopService.class
)
public class TimeOTPEntryLocalServiceImpl
	extends TimeOTPEntryLocalServiceBaseImpl {

	/**
	 * NOTE FOR DEVELOPERS:
	 *
	 * Never reference this class directly. Use <code>com.liferay.multi.factor.authentication.provider.time.otp.service.TimeOTPEntryLocalService</code> via injection or a <code>org.osgi.util.tracker.ServiceTracker</code> or use <code>com.liferay.multi.factor.authentication.provider.time.otp.service.TimeOTPEntryLocalServiceUtil</code>.
	 */
	@Override
	public TimeOTPEntry addEntry(long userId, String sharedSecret, String ip)
		throws PortalException {

		TimeOTPEntry entry = timeOTPEntryPersistence.fetchByUserId(userId);

		if (entry != null) {
			throw new NoSuchEntryException(
				"Each user can have only one TimeOTPEntry configuration");
		}

		User user = userLocalService.getUserById(userId);

		long entryId = counterLocalService.increment();

		entry = timeOTPEntryPersistence.create(entryId);

		Date date = new Date();

		entry.setCompanyId(user.getCompanyId());
		entry.setUserId(userId);
		entry.setUserName(user.getFullName());
		entry.setCreateDate(date);
		entry.setModifiedDate(date);
		entry.setSharedSecret(sharedSecret);
		entry.setLastSuccessDate(date);
		entry.setLastSuccessIP(ip);
		entry.setFailedAttempts(0);

		return timeOTPEntryPersistence.update(entry);
	}

	@Override
	public TimeOTPEntry deleteEntryByUserId(long userId)
		throws PortalException {

		return timeOTPEntryPersistence.removeByUserId(userId);
	}

	@Override
	public TimeOTPEntry fetchEntryByUserId(long userId) {
		return timeOTPEntryPersistence.fetchByUserId(userId);
	}

	@Override
	public TimeOTPEntry updateAttempts(long userId, boolean success, String ip)
		throws PortalException {

		TimeOTPEntry entry = timeOTPEntryPersistence.fetchByUserId(userId);

		Date date = new Date();

		entry.setModifiedDate(date);

		if (success) {
			entry.setLastSuccessDate(date);
			entry.setLastSuccessIP(ip);
			entry.setFailedAttempts(0);
		}
		else {
			//there should be a throttling from admin configuration
			int failedAttempts = entry.getFailedAttempts();

			entry.setFailedAttempts(failedAttempts + 1);

			entry.setLastFailDate(date);
			entry.setLastFailIP(ip);
		}

		return timeOTPEntryPersistence.update(entry);
	}

}