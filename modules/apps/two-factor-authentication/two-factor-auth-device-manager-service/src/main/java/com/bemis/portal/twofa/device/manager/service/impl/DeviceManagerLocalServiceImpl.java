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

package com.bemis.portal.twofa.device.manager.service.impl;

import aQute.bnd.annotation.ProviderType;

import com.bemis.portal.twofa.device.manager.model.Device;
import com.bemis.portal.twofa.device.manager.model.DeviceInfo;
import com.bemis.portal.twofa.device.manager.service.base.DeviceManagerLocalServiceBaseImpl;
import com.bemis.portal.twofa.provider.internal.TwoFactorAuthProvider;
import com.bemis.portal.twofa.provider.internal.TwoFactorAuthProviderFactory;
import com.bemis.portal.twofa.provider.notification.configuration.TwoFactorAuthNotificationConfigurator;

import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.spring.extender.service.ServiceReference;

/**
 * The implementation of the device manager local service.
 *
 * <p>
 * All custom service methods should be put in this class. Whenever methods are added, rerun ServiceBuilder to copy their definitions into the {@link com.bemis.portal.twofa.device.manager.service.DeviceManagerLocalService} interface.
 *
 * <p>
 * This is a local service. Methods of this service will not have security checks based on the propagated JAAS credentials because this service can only be accessed from within the same VM.
 * </p>
 *
 * @author Prathima Shreenath
 * @see DeviceManagerLocalServiceBaseImpl
 * @see com.bemis.portal.twofa.device.manager.service.DeviceManagerLocalServiceUtil
 */
@ProviderType
public class DeviceManagerLocalServiceImpl
	extends DeviceManagerLocalServiceBaseImpl {

	@Override
	public void createAndSendVerificationCode(
			User portalUser, String currentDeviceIP, String verificationBaseURL)
		throws PortalException {

		TwoFactorAuthProvider twoFactorAuthProvider =
			getTwoFactorAuthProvider();

		twoFactorAuthProvider.sendNotification(
			portalUser.getUserId(), currentDeviceIP, verificationBaseURL);
	}

	@Override
	public boolean isDeviceVerifiedForUser(DeviceInfo deviceInfo)
		throws PortalException {

		User portalUser = deviceInfo.getUser();
		String currentDeviceIP = deviceInfo.getDeviceIP();

		Device registeredDevice =
			deviceLocalService.fetchDeviceByUserAndDeviceIP(
				portalUser.getUserId(), currentDeviceIP);

		// If no device exists for User
		// Create device which is not verified yet

		if (registeredDevice == null) {
			deviceLocalService.registerDevice(deviceInfo);
			return false;
		}

		// Temp Device :
		// Device which the user does not want to add to his list of
		// personal devices, but has completed the two factor authentication
		// successfully.

		if (registeredDevice.isTempDevice()) {
			resetDevice(registeredDevice);
			return true;
		}

		return registeredDevice.isVerified();
	}

	/**
	 * Sets Device as Verified or Temp based on Users decision
	 * Removes the generated device code
	 *
	 * @param portalUserId
	 * @param registerAsVerified
	 */
	@Override
	public void registerDeviceAsVerifiedOrTemp(
		long portalUserId, boolean registerAsVerified) {

		if (registerAsVerified) {
			deviceLocalService.registerAsVerifiedDevice(portalUserId);
		}
		else {
			deviceLocalService.registerAsTempDevice(portalUserId);
		}

		deviceCodeLocalService.removeDeviceCode(portalUserId);
	}

	@Override
	public boolean twoFactorAuthenticationEnabled() {
		return _providerNotificationConfigurator.is2FactorAuthEnabled();
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
	public boolean verifyDeviceCode(long userId, String verificationCode)
		throws PortalException {

		TwoFactorAuthProvider twoFaNotificationConfigurator =
			getTwoFactorAuthProvider();

		boolean verified = twoFaNotificationConfigurator.verifyLoginCode(
			userId, verificationCode);

		return verified;
	}

	protected TwoFactorAuthProvider getTwoFactorAuthProvider() {
		String providerType =
			_providerNotificationConfigurator.get2FANotificationType();

		TwoFactorAuthProvider twoFactorAuthProvider =
			_twoFactorAuthProviderFactory.getTwoFactorAuthProvider(
				providerType);

		return twoFactorAuthProvider;
	}

	protected void resetDevice(Device registeredDevice) {
		registeredDevice.setTempDevice(false);

		deviceLocalService.updateDevice(registeredDevice);
	}

	@ServiceReference(type = TwoFactorAuthNotificationConfigurator.class)
	private TwoFactorAuthNotificationConfigurator
		_providerNotificationConfigurator;

	@ServiceReference(type = TwoFactorAuthProviderFactory.class)
	private TwoFactorAuthProviderFactory _twoFactorAuthProviderFactory;

}