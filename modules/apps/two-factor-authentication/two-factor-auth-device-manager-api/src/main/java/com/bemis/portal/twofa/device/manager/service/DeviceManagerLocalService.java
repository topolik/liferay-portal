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

import com.bemis.portal.twofa.device.manager.model.DeviceInfo;

import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.exception.SystemException;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.service.BaseLocalService;
import com.liferay.portal.kernel.transaction.Isolation;
import com.liferay.portal.kernel.transaction.Propagation;
import com.liferay.portal.kernel.transaction.Transactional;

/**
 * Provides the local service interface for DeviceManager. Methods of this
 * service will not have security checks based on the propagated JAAS
 * credentials because this service can only be accessed from within the same
 * VM.
 *
 * @author Prathima Shreenath
 * @see DeviceManagerLocalServiceUtil
 * @see com.bemis.portal.twofa.device.manager.service.base.DeviceManagerLocalServiceBaseImpl
 * @see com.bemis.portal.twofa.device.manager.service.impl.DeviceManagerLocalServiceImpl
 * @generated
 */
@ProviderType
@Transactional(isolation = Isolation.PORTAL, rollbackFor =  {
	PortalException.class, SystemException.class})
public interface DeviceManagerLocalService extends BaseLocalService {
	/*
	 * NOTE FOR DEVELOPERS:
	 *
	 * Never modify or reference this interface directly. Always use {@link DeviceManagerLocalServiceUtil} to access the device manager local service. Add custom service methods to {@link com.bemis.portal.twofa.device.manager.service.impl.DeviceManagerLocalServiceImpl} and rerun ServiceBuilder to automatically copy the method declarations to this interface.
	 */
	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public boolean isDeviceVerifiedForUser(DeviceInfo deviceInfo)
		throws PortalException;

	public boolean twoFactorAuthenticationEnabled();

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
	public boolean verifyDeviceCode(long userId,
		java.lang.String verificationCode) throws PortalException;

	/**
	* Returns the OSGi service identifier.
	*
	* @return the OSGi service identifier
	*/
	public java.lang.String getOSGiServiceIdentifier();

	public void createAndSendVerificationCode(User portalUser,
		java.lang.String currentDeviceIP, java.lang.String verificationBaseURL)
		throws PortalException;

	/**
	* Sets Device as Verified or Temp based on Users decision
	* Removes the generated device code
	*
	* @param portalUserId
	* @param registerAsVerified
	*/
	public void registerDeviceAsVerifiedOrTemp(long portalUserId,
		boolean registerAsVerified);
}