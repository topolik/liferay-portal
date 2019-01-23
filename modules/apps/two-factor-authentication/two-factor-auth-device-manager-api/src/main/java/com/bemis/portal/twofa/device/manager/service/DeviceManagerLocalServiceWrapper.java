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

import com.liferay.portal.kernel.service.ServiceWrapper;

/**
 * Provides a wrapper for {@link DeviceManagerLocalService}.
 *
 * @author Prathima Shreenath
 * @see DeviceManagerLocalService
 * @generated
 */
@ProviderType
public class DeviceManagerLocalServiceWrapper
	implements DeviceManagerLocalService,
		ServiceWrapper<DeviceManagerLocalService> {
	public DeviceManagerLocalServiceWrapper(
		DeviceManagerLocalService deviceManagerLocalService) {
		_deviceManagerLocalService = deviceManagerLocalService;
	}

	@Override
	public boolean isDeviceVerifiedForUser(
		com.bemis.portal.twofa.device.manager.model.DeviceInfo deviceInfo)
		throws com.liferay.portal.kernel.exception.PortalException {
		return _deviceManagerLocalService.isDeviceVerifiedForUser(deviceInfo);
	}

	@Override
	public boolean twoFactorAuthenticationEnabled() {
		return _deviceManagerLocalService.twoFactorAuthenticationEnabled();
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
	@Override
	public boolean verifyDeviceCode(long userId,
		java.lang.String verificationCode)
		throws com.liferay.portal.kernel.exception.PortalException {
		return _deviceManagerLocalService.verifyDeviceCode(userId,
			verificationCode);
	}

	/**
	* Returns the OSGi service identifier.
	*
	* @return the OSGi service identifier
	*/
	@Override
	public java.lang.String getOSGiServiceIdentifier() {
		return _deviceManagerLocalService.getOSGiServiceIdentifier();
	}

	@Override
	public void createAndSendVerificationCode(
		com.liferay.portal.kernel.model.User portalUser,
		java.lang.String currentDeviceIP, java.lang.String verificationBaseURL)
		throws com.liferay.portal.kernel.exception.PortalException {
		_deviceManagerLocalService.createAndSendVerificationCode(portalUser,
			currentDeviceIP, verificationBaseURL);
	}

	/**
	* Sets Device as Verified or Temp based on Users decision
	* Removes the generated device code
	*
	* @param portalUserId
	* @param registerAsVerified
	*/
	@Override
	public void registerDeviceAsVerifiedOrTemp(long portalUserId,
		boolean registerAsVerified) {
		_deviceManagerLocalService.registerDeviceAsVerifiedOrTemp(portalUserId,
			registerAsVerified);
	}

	@Override
	public DeviceManagerLocalService getWrappedService() {
		return _deviceManagerLocalService;
	}

	@Override
	public void setWrappedService(
		DeviceManagerLocalService deviceManagerLocalService) {
		_deviceManagerLocalService = deviceManagerLocalService;
	}

	private DeviceManagerLocalService _deviceManagerLocalService;
}