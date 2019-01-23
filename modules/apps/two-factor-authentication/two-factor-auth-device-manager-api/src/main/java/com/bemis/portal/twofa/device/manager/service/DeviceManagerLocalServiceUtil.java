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

package com.bemis.portal.twofa.device.manager.service;

import aQute.bnd.annotation.ProviderType;

import com.liferay.osgi.util.ServiceTrackerFactory;

import org.osgi.util.tracker.ServiceTracker;

/**
 * Provides the local service utility for DeviceManager. This utility wraps
 * {@link com.bemis.portal.twofa.device.manager.service.impl.DeviceManagerLocalServiceImpl} and is the
 * primary access point for service operations in application layer code running
 * on the local server. Methods of this service will not have security checks
 * based on the propagated JAAS credentials because this service can only be
 * accessed from within the same VM.
 *
 * @author Prathima Shreenath
 * @see DeviceManagerLocalService
 * @see com.bemis.portal.twofa.device.manager.service.base.DeviceManagerLocalServiceBaseImpl
 * @see com.bemis.portal.twofa.device.manager.service.impl.DeviceManagerLocalServiceImpl
 * @generated
 */
@ProviderType
public class DeviceManagerLocalServiceUtil {
	/*
	 * NOTE FOR DEVELOPERS:
	 *
	 * Never modify this class directly. Add custom service methods to {@link com.bemis.portal.twofa.device.manager.service.impl.DeviceManagerLocalServiceImpl} and rerun ServiceBuilder to regenerate this class.
	 */
	public static boolean isDeviceVerifiedForUser(
		com.bemis.portal.twofa.device.manager.model.DeviceInfo deviceInfo)
		throws com.liferay.portal.kernel.exception.PortalException {
		return getService().isDeviceVerifiedForUser(deviceInfo);
	}

	public static boolean twoFactorAuthenticationEnabled() {
		return getService().twoFactorAuthenticationEnabled();
	}

	/**
	* Verify Generated Device Code
	* If verified -
	* Register Device as verified
	* Delete the device code
	*
	* @param userId
	* @param verificationCode
	* @return
	* @throws PortalException
	*/
	public static boolean verifyDeviceCode(long userId,
		java.lang.String verificationCode)
		throws com.liferay.portal.kernel.exception.PortalException {
		return getService().verifyDeviceCode(userId, verificationCode);
	}

	/**
	* Returns the OSGi service identifier.
	*
	* @return the OSGi service identifier
	*/
	public static java.lang.String getOSGiServiceIdentifier() {
		return getService().getOSGiServiceIdentifier();
	}

	public static void createAndSendVerificationCode(
		com.liferay.portal.kernel.model.User portalUser,
		java.lang.String currentDeviceIP, java.lang.String verificationBaseURL)
		throws com.liferay.portal.kernel.exception.PortalException {
		getService()
			.createAndSendVerificationCode(portalUser, currentDeviceIP,
			verificationBaseURL);
	}

	/**
	* Sets Device as Verified or Temp based on Users decision
	* Removes the generated device code
	*
	* @param portalUserId
	* @param registerAsVerified
	*/
	public static void registerDeviceAsVerifiedOrTemp(long portalUserId,
		boolean registerAsVerified) {
		getService()
			.registerDeviceAsVerifiedOrTemp(portalUserId, registerAsVerified);
	}

	public static DeviceManagerLocalService getService() {
		return _serviceTracker.getService();
	}

	private static ServiceTracker<DeviceManagerLocalService, DeviceManagerLocalService> _serviceTracker =
		ServiceTrackerFactory.open(DeviceManagerLocalService.class);
}