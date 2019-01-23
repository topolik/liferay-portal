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

import com.bemis.portal.commons.service.BemisPortalService;
import com.bemis.portal.twofa.device.manager.model.Device;
import com.bemis.portal.twofa.device.manager.model.DeviceCode;
import com.bemis.portal.twofa.device.manager.model.DeviceInfo;
import com.bemis.portal.twofa.device.manager.service.base.DeviceLocalServiceBaseImpl;

import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.spring.extender.service.ServiceReference;

import java.util.Date;

/**
 * The implementation of the device local service.
 *
 * <p>
 * All custom service methods should be put in this class. Whenever methods are added, rerun ServiceBuilder to copy their definitions into the {@link com.bemis.portal.twofa.device.manager.service.DeviceLocalService} interface.
 *
 * <p>
 * This is a local service. Methods of this service will not have security checks based on the propagated JAAS credentials because this service can only be accessed from within the same VM.
 * </p>
 *
 * @author Prathima Shreenath
 * @see DeviceLocalServiceBaseImpl
 * @see com.bemis.portal.twofa.device.manager.service.DeviceLocalServiceUtil
 */
@ProviderType
public class DeviceLocalServiceImpl extends DeviceLocalServiceBaseImpl {

	public boolean checkDeviceExistsForThisUser(long userId, String deviceIP) {
		if (fetchDeviceByUserAndDeviceIP(userId, deviceIP) == null) {
			return false;
		}

		return true;
	}

	public void deleteUnauthorizedDevice(long userId) {
		Device device = fetchDeviceByPortalUserId(userId);

		deviceLocalService.deleteDevice(device);
	}

	public Device fetchDeviceByUserAndDeviceIP(long userId, String deviceIP) {
		return devicePersistence.fetchByPortalUserId_DeviceIP(userId, deviceIP);
	}

	/**
	 * Sets registered device as verified
	 *
	 * @param userId
	 */
	public void registerAsTempDevice(long userId) {
		Device device = fetchDeviceByPortalUserId(userId);

		device.setTempDevice(true);
		devicePersistence.update(device);

		if (_log.isDebugEnabled()) {
			_log.debug(
				">>> Device registered as a temporary device for userId : " +
					userId);
		}
	}

	/**
	 * Sets registered device as verified
	 *
	 * @param userId
	 */
	public void registerAsVerifiedDevice(long userId) {
		Device device = fetchDeviceByPortalUserId(userId);

		device.setVerified(true);
		devicePersistence.update(device);

		if (_log.isDebugEnabled()) {
			_log.debug(
				">>> Device registered as verified for userId : " + userId);
		}
	}

	public Device registerDevice(DeviceInfo deviceInfo) throws PortalException {
		User portalUser = deviceInfo.getUser();

		if (portalUser == null) {
			throw new PortalException(
				">>> No Such User exists with email address : " +
					deviceInfo.getUserEmail());
		}

		String deviceIP = deviceInfo.getDeviceIP();

		long portalUserId = portalUser.getUserId();
		String portalUserFullName = portalUser.getFullName();

		Device device = fetchDeviceByUserAndDeviceIP(portalUserId, deviceIP);

		if (device == null) {
			if (_log.isDebugEnabled()) {
				_log.debug(
					String.format(
						">>> No Device registered for %s for IP : %s",
						portalUserFullName, deviceIP));
			}

			device = devicePersistence.create(
				counterLocalService.increment(Device.class.getName()));

			device.setCreateDate(new Date());
		}

		long companyId = portalUser.getCompanyId();

		long groupId = _bemisPortalService.getGlobalScopeGroupId();

		User defaultUser = _bemisPortalService.getDefaultUser();

		device.setCompanyId(companyId);
		device.setGroupId(groupId);
		device.setUserId(defaultUser.getUserId());
		device.setUserName(defaultUser.getScreenName());
		device.setModifiedDate(new Date());

		device.setPortalUserId(portalUserId);
		device.setPortalUserName(portalUserFullName);
		device.setEmailAddress(portalUser.getEmailAddress());
		device.setDeviceIP(deviceIP);
		device.setVerified(false);
		device.setTempDevice(false);

		devicePersistence.update(device);

		return device;
	}

	protected Device fetchDeviceByPortalUserId(long userId) {
		DeviceCode deviceCode =
			deviceCodeLocalService.fetchDeviceCodeByPortalUserId(userId);

		return fetchDeviceByUserAndDeviceIP(userId, deviceCode.getDeviceIP());
	}

	private static final Log _log = LogFactoryUtil.getLog(
		DeviceLocalServiceImpl.class);

	@ServiceReference(type = BemisPortalService.class)
	private BemisPortalService _bemisPortalService;

}