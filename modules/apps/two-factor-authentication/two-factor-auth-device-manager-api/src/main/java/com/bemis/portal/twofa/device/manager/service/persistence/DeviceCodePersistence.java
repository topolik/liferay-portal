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

package com.bemis.portal.twofa.device.manager.service.persistence;

import aQute.bnd.annotation.ProviderType;

import com.bemis.portal.twofa.device.manager.exception.NoSuchDeviceCodeException;
import com.bemis.portal.twofa.device.manager.model.DeviceCode;

import com.liferay.portal.kernel.service.persistence.BasePersistence;

/**
 * The persistence interface for the device code service.
 *
 * <p>
 * Caching information and settings can be found in <code>portal.properties</code>
 * </p>
 *
 * @author Prathima Shreenath
 * @see com.bemis.portal.twofa.device.manager.service.persistence.impl.DeviceCodePersistenceImpl
 * @see DeviceCodeUtil
 * @generated
 */
@ProviderType
public interface DeviceCodePersistence extends BasePersistence<DeviceCode> {
	/*
	 * NOTE FOR DEVELOPERS:
	 *
	 * Never modify or reference this interface directly. Always use {@link DeviceCodeUtil} to access the device code persistence. Modify <code>service.xml</code> and rerun ServiceBuilder to regenerate this interface.
	 */

	/**
	* Returns the device code where portalUserId = &#63; or throws a {@link NoSuchDeviceCodeException} if it could not be found.
	*
	* @param portalUserId the portal user ID
	* @return the matching device code
	* @throws NoSuchDeviceCodeException if a matching device code could not be found
	*/
	public DeviceCode findByPortalUserId(long portalUserId)
		throws NoSuchDeviceCodeException;

	/**
	* Returns the device code where portalUserId = &#63; or returns <code>null</code> if it could not be found. Uses the finder cache.
	*
	* @param portalUserId the portal user ID
	* @return the matching device code, or <code>null</code> if a matching device code could not be found
	*/
	public DeviceCode fetchByPortalUserId(long portalUserId);

	/**
	* Returns the device code where portalUserId = &#63; or returns <code>null</code> if it could not be found, optionally using the finder cache.
	*
	* @param portalUserId the portal user ID
	* @param retrieveFromCache whether to retrieve from the finder cache
	* @return the matching device code, or <code>null</code> if a matching device code could not be found
	*/
	public DeviceCode fetchByPortalUserId(long portalUserId,
		boolean retrieveFromCache);

	/**
	* Removes the device code where portalUserId = &#63; from the database.
	*
	* @param portalUserId the portal user ID
	* @return the device code that was removed
	*/
	public DeviceCode removeByPortalUserId(long portalUserId)
		throws NoSuchDeviceCodeException;

	/**
	* Returns the number of device codes where portalUserId = &#63;.
	*
	* @param portalUserId the portal user ID
	* @return the number of matching device codes
	*/
	public int countByPortalUserId(long portalUserId);

	/**
	* Returns the device code where emailAddress = &#63; or throws a {@link NoSuchDeviceCodeException} if it could not be found.
	*
	* @param emailAddress the email address
	* @return the matching device code
	* @throws NoSuchDeviceCodeException if a matching device code could not be found
	*/
	public DeviceCode findByEmailAddress(java.lang.String emailAddress)
		throws NoSuchDeviceCodeException;

	/**
	* Returns the device code where emailAddress = &#63; or returns <code>null</code> if it could not be found. Uses the finder cache.
	*
	* @param emailAddress the email address
	* @return the matching device code, or <code>null</code> if a matching device code could not be found
	*/
	public DeviceCode fetchByEmailAddress(java.lang.String emailAddress);

	/**
	* Returns the device code where emailAddress = &#63; or returns <code>null</code> if it could not be found, optionally using the finder cache.
	*
	* @param emailAddress the email address
	* @param retrieveFromCache whether to retrieve from the finder cache
	* @return the matching device code, or <code>null</code> if a matching device code could not be found
	*/
	public DeviceCode fetchByEmailAddress(java.lang.String emailAddress,
		boolean retrieveFromCache);

	/**
	* Removes the device code where emailAddress = &#63; from the database.
	*
	* @param emailAddress the email address
	* @return the device code that was removed
	*/
	public DeviceCode removeByEmailAddress(java.lang.String emailAddress)
		throws NoSuchDeviceCodeException;

	/**
	* Returns the number of device codes where emailAddress = &#63;.
	*
	* @param emailAddress the email address
	* @return the number of matching device codes
	*/
	public int countByEmailAddress(java.lang.String emailAddress);

	/**
	* Caches the device code in the entity cache if it is enabled.
	*
	* @param deviceCode the device code
	*/
	public void cacheResult(DeviceCode deviceCode);

	/**
	* Caches the device codes in the entity cache if it is enabled.
	*
	* @param deviceCodes the device codes
	*/
	public void cacheResult(java.util.List<DeviceCode> deviceCodes);

	/**
	* Creates a new device code with the primary key. Does not add the device code to the database.
	*
	* @param deviceCodeId the primary key for the new device code
	* @return the new device code
	*/
	public DeviceCode create(long deviceCodeId);

	/**
	* Removes the device code with the primary key from the database. Also notifies the appropriate model listeners.
	*
	* @param deviceCodeId the primary key of the device code
	* @return the device code that was removed
	* @throws NoSuchDeviceCodeException if a device code with the primary key could not be found
	*/
	public DeviceCode remove(long deviceCodeId)
		throws NoSuchDeviceCodeException;

	public DeviceCode updateImpl(DeviceCode deviceCode);

	/**
	* Returns the device code with the primary key or throws a {@link NoSuchDeviceCodeException} if it could not be found.
	*
	* @param deviceCodeId the primary key of the device code
	* @return the device code
	* @throws NoSuchDeviceCodeException if a device code with the primary key could not be found
	*/
	public DeviceCode findByPrimaryKey(long deviceCodeId)
		throws NoSuchDeviceCodeException;

	/**
	* Returns the device code with the primary key or returns <code>null</code> if it could not be found.
	*
	* @param deviceCodeId the primary key of the device code
	* @return the device code, or <code>null</code> if a device code with the primary key could not be found
	*/
	public DeviceCode fetchByPrimaryKey(long deviceCodeId);

	@Override
	public java.util.Map<java.io.Serializable, DeviceCode> fetchByPrimaryKeys(
		java.util.Set<java.io.Serializable> primaryKeys);

	/**
	* Returns all the device codes.
	*
	* @return the device codes
	*/
	public java.util.List<DeviceCode> findAll();

	/**
	* Returns a range of all the device codes.
	*
	* <p>
	* Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to {@link QueryUtil#ALL_POS} will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent and pagination is required (<code>start</code> and <code>end</code> are not {@link QueryUtil#ALL_POS}), then the query will include the default ORDER BY logic from {@link DeviceCodeModelImpl}. If both <code>orderByComparator</code> and pagination are absent, for performance reasons, the query will not have an ORDER BY clause and the returned result set will be sorted on by the primary key in an ascending order.
	* </p>
	*
	* @param start the lower bound of the range of device codes
	* @param end the upper bound of the range of device codes (not inclusive)
	* @return the range of device codes
	*/
	public java.util.List<DeviceCode> findAll(int start, int end);

	/**
	* Returns an ordered range of all the device codes.
	*
	* <p>
	* Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to {@link QueryUtil#ALL_POS} will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent and pagination is required (<code>start</code> and <code>end</code> are not {@link QueryUtil#ALL_POS}), then the query will include the default ORDER BY logic from {@link DeviceCodeModelImpl}. If both <code>orderByComparator</code> and pagination are absent, for performance reasons, the query will not have an ORDER BY clause and the returned result set will be sorted on by the primary key in an ascending order.
	* </p>
	*
	* @param start the lower bound of the range of device codes
	* @param end the upper bound of the range of device codes (not inclusive)
	* @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	* @return the ordered range of device codes
	*/
	public java.util.List<DeviceCode> findAll(int start, int end,
		com.liferay.portal.kernel.util.OrderByComparator<DeviceCode> orderByComparator);

	/**
	* Returns an ordered range of all the device codes.
	*
	* <p>
	* Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to {@link QueryUtil#ALL_POS} will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent and pagination is required (<code>start</code> and <code>end</code> are not {@link QueryUtil#ALL_POS}), then the query will include the default ORDER BY logic from {@link DeviceCodeModelImpl}. If both <code>orderByComparator</code> and pagination are absent, for performance reasons, the query will not have an ORDER BY clause and the returned result set will be sorted on by the primary key in an ascending order.
	* </p>
	*
	* @param start the lower bound of the range of device codes
	* @param end the upper bound of the range of device codes (not inclusive)
	* @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	* @param retrieveFromCache whether to retrieve from the finder cache
	* @return the ordered range of device codes
	*/
	public java.util.List<DeviceCode> findAll(int start, int end,
		com.liferay.portal.kernel.util.OrderByComparator<DeviceCode> orderByComparator,
		boolean retrieveFromCache);

	/**
	* Removes all the device codes from the database.
	*/
	public void removeAll();

	/**
	* Returns the number of device codes.
	*
	* @return the number of device codes
	*/
	public int countAll();
}