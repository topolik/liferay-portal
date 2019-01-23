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

import com.bemis.portal.twofa.device.manager.exception.NoSuchDeviceException;
import com.bemis.portal.twofa.device.manager.model.Device;

import com.liferay.portal.kernel.service.persistence.BasePersistence;

import java.io.Serializable;

import java.util.Map;
import java.util.Set;

/**
 * The persistence interface for the device service.
 *
 * <p>
 * Caching information and settings can be found in <code>portal.properties</code>
 * </p>
 *
 * @author Prathima Shreenath
 * @see com.bemis.portal.twofa.device.manager.service.persistence.impl.DevicePersistenceImpl
 * @see DeviceUtil
 * @generated
 */
@ProviderType
public interface DevicePersistence extends BasePersistence<Device> {
	/*
	 * NOTE FOR DEVELOPERS:
	 *
	 * Never modify or reference this interface directly. Always use {@link DeviceUtil} to access the device persistence. Modify <code>service.xml</code> and rerun ServiceBuilder to regenerate this interface.
	 */
	@Override
	public Map<Serializable, Device> fetchByPrimaryKeys(
		Set<Serializable> primaryKeys);

	/**
	* Returns all the devices where portalUserId = &#63;.
	*
	* @param portalUserId the portal user ID
	* @return the matching devices
	*/
	public java.util.List<Device> findByPortalUserId(long portalUserId);

	/**
	* Returns a range of all the devices where portalUserId = &#63;.
	*
	* <p>
	* Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to {@link QueryUtil#ALL_POS} will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent and pagination is required (<code>start</code> and <code>end</code> are not {@link QueryUtil#ALL_POS}), then the query will include the default ORDER BY logic from {@link DeviceModelImpl}. If both <code>orderByComparator</code> and pagination are absent, for performance reasons, the query will not have an ORDER BY clause and the returned result set will be sorted on by the primary key in an ascending order.
	* </p>
	*
	* @param portalUserId the portal user ID
	* @param start the lower bound of the range of devices
	* @param end the upper bound of the range of devices (not inclusive)
	* @return the range of matching devices
	*/
	public java.util.List<Device> findByPortalUserId(long portalUserId,
		int start, int end);

	/**
	* Returns an ordered range of all the devices where portalUserId = &#63;.
	*
	* <p>
	* Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to {@link QueryUtil#ALL_POS} will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent and pagination is required (<code>start</code> and <code>end</code> are not {@link QueryUtil#ALL_POS}), then the query will include the default ORDER BY logic from {@link DeviceModelImpl}. If both <code>orderByComparator</code> and pagination are absent, for performance reasons, the query will not have an ORDER BY clause and the returned result set will be sorted on by the primary key in an ascending order.
	* </p>
	*
	* @param portalUserId the portal user ID
	* @param start the lower bound of the range of devices
	* @param end the upper bound of the range of devices (not inclusive)
	* @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	* @return the ordered range of matching devices
	*/
	public java.util.List<Device> findByPortalUserId(long portalUserId,
		int start, int end,
		com.liferay.portal.kernel.util.OrderByComparator<Device> orderByComparator);

	/**
	* Returns an ordered range of all the devices where portalUserId = &#63;.
	*
	* <p>
	* Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to {@link QueryUtil#ALL_POS} will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent and pagination is required (<code>start</code> and <code>end</code> are not {@link QueryUtil#ALL_POS}), then the query will include the default ORDER BY logic from {@link DeviceModelImpl}. If both <code>orderByComparator</code> and pagination are absent, for performance reasons, the query will not have an ORDER BY clause and the returned result set will be sorted on by the primary key in an ascending order.
	* </p>
	*
	* @param portalUserId the portal user ID
	* @param start the lower bound of the range of devices
	* @param end the upper bound of the range of devices (not inclusive)
	* @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	* @param retrieveFromCache whether to retrieve from the finder cache
	* @return the ordered range of matching devices
	*/
	public java.util.List<Device> findByPortalUserId(long portalUserId,
		int start, int end,
		com.liferay.portal.kernel.util.OrderByComparator<Device> orderByComparator,
		boolean retrieveFromCache);

	/**
	* Returns the first device in the ordered set where portalUserId = &#63;.
	*
	* @param portalUserId the portal user ID
	* @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	* @return the first matching device
	* @throws NoSuchDeviceException if a matching device could not be found
	*/
	public Device findByPortalUserId_First(long portalUserId,
		com.liferay.portal.kernel.util.OrderByComparator<Device> orderByComparator)
		throws NoSuchDeviceException;

	/**
	* Returns the first device in the ordered set where portalUserId = &#63;.
	*
	* @param portalUserId the portal user ID
	* @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	* @return the first matching device, or <code>null</code> if a matching device could not be found
	*/
	public Device fetchByPortalUserId_First(long portalUserId,
		com.liferay.portal.kernel.util.OrderByComparator<Device> orderByComparator);

	/**
	* Returns the last device in the ordered set where portalUserId = &#63;.
	*
	* @param portalUserId the portal user ID
	* @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	* @return the last matching device
	* @throws NoSuchDeviceException if a matching device could not be found
	*/
	public Device findByPortalUserId_Last(long portalUserId,
		com.liferay.portal.kernel.util.OrderByComparator<Device> orderByComparator)
		throws NoSuchDeviceException;

	/**
	* Returns the last device in the ordered set where portalUserId = &#63;.
	*
	* @param portalUserId the portal user ID
	* @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	* @return the last matching device, or <code>null</code> if a matching device could not be found
	*/
	public Device fetchByPortalUserId_Last(long portalUserId,
		com.liferay.portal.kernel.util.OrderByComparator<Device> orderByComparator);

	/**
	* Returns the devices before and after the current device in the ordered set where portalUserId = &#63;.
	*
	* @param deviceId the primary key of the current device
	* @param portalUserId the portal user ID
	* @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	* @return the previous, current, and next device
	* @throws NoSuchDeviceException if a device with the primary key could not be found
	*/
	public Device[] findByPortalUserId_PrevAndNext(long deviceId,
		long portalUserId,
		com.liferay.portal.kernel.util.OrderByComparator<Device> orderByComparator)
		throws NoSuchDeviceException;

	/**
	* Removes all the devices where portalUserId = &#63; from the database.
	*
	* @param portalUserId the portal user ID
	*/
	public void removeByPortalUserId(long portalUserId);

	/**
	* Returns the number of devices where portalUserId = &#63;.
	*
	* @param portalUserId the portal user ID
	* @return the number of matching devices
	*/
	public int countByPortalUserId(long portalUserId);

	/**
	* Returns the device where portalUserId = &#63; and deviceIP = &#63; or throws a {@link NoSuchDeviceException} if it could not be found.
	*
	* @param portalUserId the portal user ID
	* @param deviceIP the device ip
	* @return the matching device
	* @throws NoSuchDeviceException if a matching device could not be found
	*/
	public Device findByPortalUserId_DeviceIP(long portalUserId, String deviceIP)
		throws NoSuchDeviceException;

	/**
	* Returns the device where portalUserId = &#63; and deviceIP = &#63; or returns <code>null</code> if it could not be found. Uses the finder cache.
	*
	* @param portalUserId the portal user ID
	* @param deviceIP the device ip
	* @return the matching device, or <code>null</code> if a matching device could not be found
	*/
	public Device fetchByPortalUserId_DeviceIP(long portalUserId,
		String deviceIP);

	/**
	* Returns the device where portalUserId = &#63; and deviceIP = &#63; or returns <code>null</code> if it could not be found, optionally using the finder cache.
	*
	* @param portalUserId the portal user ID
	* @param deviceIP the device ip
	* @param retrieveFromCache whether to retrieve from the finder cache
	* @return the matching device, or <code>null</code> if a matching device could not be found
	*/
	public Device fetchByPortalUserId_DeviceIP(long portalUserId,
		String deviceIP, boolean retrieveFromCache);

	/**
	* Removes the device where portalUserId = &#63; and deviceIP = &#63; from the database.
	*
	* @param portalUserId the portal user ID
	* @param deviceIP the device ip
	* @return the device that was removed
	*/
	public Device removeByPortalUserId_DeviceIP(long portalUserId,
		String deviceIP) throws NoSuchDeviceException;

	/**
	* Returns the number of devices where portalUserId = &#63; and deviceIP = &#63;.
	*
	* @param portalUserId the portal user ID
	* @param deviceIP the device ip
	* @return the number of matching devices
	*/
	public int countByPortalUserId_DeviceIP(long portalUserId, String deviceIP);

	/**
	* Returns all the devices where verified = &#63; and portalUserId = &#63;.
	*
	* @param verified the verified
	* @param portalUserId the portal user ID
	* @return the matching devices
	*/
	public java.util.List<Device> findByVerified_PortalUserId(
		boolean verified, long portalUserId);

	/**
	* Returns a range of all the devices where verified = &#63; and portalUserId = &#63;.
	*
	* <p>
	* Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to {@link QueryUtil#ALL_POS} will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent and pagination is required (<code>start</code> and <code>end</code> are not {@link QueryUtil#ALL_POS}), then the query will include the default ORDER BY logic from {@link DeviceModelImpl}. If both <code>orderByComparator</code> and pagination are absent, for performance reasons, the query will not have an ORDER BY clause and the returned result set will be sorted on by the primary key in an ascending order.
	* </p>
	*
	* @param verified the verified
	* @param portalUserId the portal user ID
	* @param start the lower bound of the range of devices
	* @param end the upper bound of the range of devices (not inclusive)
	* @return the range of matching devices
	*/
	public java.util.List<Device> findByVerified_PortalUserId(
		boolean verified, long portalUserId, int start, int end);

	/**
	* Returns an ordered range of all the devices where verified = &#63; and portalUserId = &#63;.
	*
	* <p>
	* Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to {@link QueryUtil#ALL_POS} will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent and pagination is required (<code>start</code> and <code>end</code> are not {@link QueryUtil#ALL_POS}), then the query will include the default ORDER BY logic from {@link DeviceModelImpl}. If both <code>orderByComparator</code> and pagination are absent, for performance reasons, the query will not have an ORDER BY clause and the returned result set will be sorted on by the primary key in an ascending order.
	* </p>
	*
	* @param verified the verified
	* @param portalUserId the portal user ID
	* @param start the lower bound of the range of devices
	* @param end the upper bound of the range of devices (not inclusive)
	* @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	* @return the ordered range of matching devices
	*/
	public java.util.List<Device> findByVerified_PortalUserId(
		boolean verified, long portalUserId, int start, int end,
		com.liferay.portal.kernel.util.OrderByComparator<Device> orderByComparator);

	/**
	* Returns an ordered range of all the devices where verified = &#63; and portalUserId = &#63;.
	*
	* <p>
	* Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to {@link QueryUtil#ALL_POS} will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent and pagination is required (<code>start</code> and <code>end</code> are not {@link QueryUtil#ALL_POS}), then the query will include the default ORDER BY logic from {@link DeviceModelImpl}. If both <code>orderByComparator</code> and pagination are absent, for performance reasons, the query will not have an ORDER BY clause and the returned result set will be sorted on by the primary key in an ascending order.
	* </p>
	*
	* @param verified the verified
	* @param portalUserId the portal user ID
	* @param start the lower bound of the range of devices
	* @param end the upper bound of the range of devices (not inclusive)
	* @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	* @param retrieveFromCache whether to retrieve from the finder cache
	* @return the ordered range of matching devices
	*/
	public java.util.List<Device> findByVerified_PortalUserId(
		boolean verified, long portalUserId, int start, int end,
		com.liferay.portal.kernel.util.OrderByComparator<Device> orderByComparator,
		boolean retrieveFromCache);

	/**
	* Returns the first device in the ordered set where verified = &#63; and portalUserId = &#63;.
	*
	* @param verified the verified
	* @param portalUserId the portal user ID
	* @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	* @return the first matching device
	* @throws NoSuchDeviceException if a matching device could not be found
	*/
	public Device findByVerified_PortalUserId_First(boolean verified,
		long portalUserId,
		com.liferay.portal.kernel.util.OrderByComparator<Device> orderByComparator)
		throws NoSuchDeviceException;

	/**
	* Returns the first device in the ordered set where verified = &#63; and portalUserId = &#63;.
	*
	* @param verified the verified
	* @param portalUserId the portal user ID
	* @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	* @return the first matching device, or <code>null</code> if a matching device could not be found
	*/
	public Device fetchByVerified_PortalUserId_First(boolean verified,
		long portalUserId,
		com.liferay.portal.kernel.util.OrderByComparator<Device> orderByComparator);

	/**
	* Returns the last device in the ordered set where verified = &#63; and portalUserId = &#63;.
	*
	* @param verified the verified
	* @param portalUserId the portal user ID
	* @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	* @return the last matching device
	* @throws NoSuchDeviceException if a matching device could not be found
	*/
	public Device findByVerified_PortalUserId_Last(boolean verified,
		long portalUserId,
		com.liferay.portal.kernel.util.OrderByComparator<Device> orderByComparator)
		throws NoSuchDeviceException;

	/**
	* Returns the last device in the ordered set where verified = &#63; and portalUserId = &#63;.
	*
	* @param verified the verified
	* @param portalUserId the portal user ID
	* @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	* @return the last matching device, or <code>null</code> if a matching device could not be found
	*/
	public Device fetchByVerified_PortalUserId_Last(boolean verified,
		long portalUserId,
		com.liferay.portal.kernel.util.OrderByComparator<Device> orderByComparator);

	/**
	* Returns the devices before and after the current device in the ordered set where verified = &#63; and portalUserId = &#63;.
	*
	* @param deviceId the primary key of the current device
	* @param verified the verified
	* @param portalUserId the portal user ID
	* @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	* @return the previous, current, and next device
	* @throws NoSuchDeviceException if a device with the primary key could not be found
	*/
	public Device[] findByVerified_PortalUserId_PrevAndNext(long deviceId,
		boolean verified, long portalUserId,
		com.liferay.portal.kernel.util.OrderByComparator<Device> orderByComparator)
		throws NoSuchDeviceException;

	/**
	* Removes all the devices where verified = &#63; and portalUserId = &#63; from the database.
	*
	* @param verified the verified
	* @param portalUserId the portal user ID
	*/
	public void removeByVerified_PortalUserId(boolean verified,
		long portalUserId);

	/**
	* Returns the number of devices where verified = &#63; and portalUserId = &#63;.
	*
	* @param verified the verified
	* @param portalUserId the portal user ID
	* @return the number of matching devices
	*/
	public int countByVerified_PortalUserId(boolean verified, long portalUserId);

	/**
	* Returns the device where verified = &#63; and portalUserId = &#63; and deviceIP = &#63; or throws a {@link NoSuchDeviceException} if it could not be found.
	*
	* @param verified the verified
	* @param portalUserId the portal user ID
	* @param deviceIP the device ip
	* @return the matching device
	* @throws NoSuchDeviceException if a matching device could not be found
	*/
	public Device findByVerified_PortalUserId_DeviceIP(boolean verified,
		long portalUserId, String deviceIP) throws NoSuchDeviceException;

	/**
	* Returns the device where verified = &#63; and portalUserId = &#63; and deviceIP = &#63; or returns <code>null</code> if it could not be found. Uses the finder cache.
	*
	* @param verified the verified
	* @param portalUserId the portal user ID
	* @param deviceIP the device ip
	* @return the matching device, or <code>null</code> if a matching device could not be found
	*/
	public Device fetchByVerified_PortalUserId_DeviceIP(boolean verified,
		long portalUserId, String deviceIP);

	/**
	* Returns the device where verified = &#63; and portalUserId = &#63; and deviceIP = &#63; or returns <code>null</code> if it could not be found, optionally using the finder cache.
	*
	* @param verified the verified
	* @param portalUserId the portal user ID
	* @param deviceIP the device ip
	* @param retrieveFromCache whether to retrieve from the finder cache
	* @return the matching device, or <code>null</code> if a matching device could not be found
	*/
	public Device fetchByVerified_PortalUserId_DeviceIP(boolean verified,
		long portalUserId, String deviceIP, boolean retrieveFromCache);

	/**
	* Removes the device where verified = &#63; and portalUserId = &#63; and deviceIP = &#63; from the database.
	*
	* @param verified the verified
	* @param portalUserId the portal user ID
	* @param deviceIP the device ip
	* @return the device that was removed
	*/
	public Device removeByVerified_PortalUserId_DeviceIP(boolean verified,
		long portalUserId, String deviceIP) throws NoSuchDeviceException;

	/**
	* Returns the number of devices where verified = &#63; and portalUserId = &#63; and deviceIP = &#63;.
	*
	* @param verified the verified
	* @param portalUserId the portal user ID
	* @param deviceIP the device ip
	* @return the number of matching devices
	*/
	public int countByVerified_PortalUserId_DeviceIP(boolean verified,
		long portalUserId, String deviceIP);

	/**
	* Returns the device where tempDevice = &#63; and portalUserId = &#63; and deviceIP = &#63; or throws a {@link NoSuchDeviceException} if it could not be found.
	*
	* @param tempDevice the temp device
	* @param portalUserId the portal user ID
	* @param deviceIP the device ip
	* @return the matching device
	* @throws NoSuchDeviceException if a matching device could not be found
	*/
	public Device findByTempDevice_PortalUserId_DeviceIP(boolean tempDevice,
		long portalUserId, String deviceIP) throws NoSuchDeviceException;

	/**
	* Returns the device where tempDevice = &#63; and portalUserId = &#63; and deviceIP = &#63; or returns <code>null</code> if it could not be found. Uses the finder cache.
	*
	* @param tempDevice the temp device
	* @param portalUserId the portal user ID
	* @param deviceIP the device ip
	* @return the matching device, or <code>null</code> if a matching device could not be found
	*/
	public Device fetchByTempDevice_PortalUserId_DeviceIP(boolean tempDevice,
		long portalUserId, String deviceIP);

	/**
	* Returns the device where tempDevice = &#63; and portalUserId = &#63; and deviceIP = &#63; or returns <code>null</code> if it could not be found, optionally using the finder cache.
	*
	* @param tempDevice the temp device
	* @param portalUserId the portal user ID
	* @param deviceIP the device ip
	* @param retrieveFromCache whether to retrieve from the finder cache
	* @return the matching device, or <code>null</code> if a matching device could not be found
	*/
	public Device fetchByTempDevice_PortalUserId_DeviceIP(boolean tempDevice,
		long portalUserId, String deviceIP, boolean retrieveFromCache);

	/**
	* Removes the device where tempDevice = &#63; and portalUserId = &#63; and deviceIP = &#63; from the database.
	*
	* @param tempDevice the temp device
	* @param portalUserId the portal user ID
	* @param deviceIP the device ip
	* @return the device that was removed
	*/
	public Device removeByTempDevice_PortalUserId_DeviceIP(boolean tempDevice,
		long portalUserId, String deviceIP) throws NoSuchDeviceException;

	/**
	* Returns the number of devices where tempDevice = &#63; and portalUserId = &#63; and deviceIP = &#63;.
	*
	* @param tempDevice the temp device
	* @param portalUserId the portal user ID
	* @param deviceIP the device ip
	* @return the number of matching devices
	*/
	public int countByTempDevice_PortalUserId_DeviceIP(boolean tempDevice,
		long portalUserId, String deviceIP);

	/**
	* Caches the device in the entity cache if it is enabled.
	*
	* @param device the device
	*/
	public void cacheResult(Device device);

	/**
	* Caches the devices in the entity cache if it is enabled.
	*
	* @param devices the devices
	*/
	public void cacheResult(java.util.List<Device> devices);

	/**
	* Creates a new device with the primary key. Does not add the device to the database.
	*
	* @param deviceId the primary key for the new device
	* @return the new device
	*/
	public Device create(long deviceId);

	/**
	* Removes the device with the primary key from the database. Also notifies the appropriate model listeners.
	*
	* @param deviceId the primary key of the device
	* @return the device that was removed
	* @throws NoSuchDeviceException if a device with the primary key could not be found
	*/
	public Device remove(long deviceId) throws NoSuchDeviceException;

	public Device updateImpl(Device device);

	/**
	* Returns the device with the primary key or throws a {@link NoSuchDeviceException} if it could not be found.
	*
	* @param deviceId the primary key of the device
	* @return the device
	* @throws NoSuchDeviceException if a device with the primary key could not be found
	*/
	public Device findByPrimaryKey(long deviceId) throws NoSuchDeviceException;

	/**
	* Returns the device with the primary key or returns <code>null</code> if it could not be found.
	*
	* @param deviceId the primary key of the device
	* @return the device, or <code>null</code> if a device with the primary key could not be found
	*/
	public Device fetchByPrimaryKey(long deviceId);

	/**
	* Returns all the devices.
	*
	* @return the devices
	*/
	public java.util.List<Device> findAll();

	/**
	* Returns a range of all the devices.
	*
	* <p>
	* Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to {@link QueryUtil#ALL_POS} will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent and pagination is required (<code>start</code> and <code>end</code> are not {@link QueryUtil#ALL_POS}), then the query will include the default ORDER BY logic from {@link DeviceModelImpl}. If both <code>orderByComparator</code> and pagination are absent, for performance reasons, the query will not have an ORDER BY clause and the returned result set will be sorted on by the primary key in an ascending order.
	* </p>
	*
	* @param start the lower bound of the range of devices
	* @param end the upper bound of the range of devices (not inclusive)
	* @return the range of devices
	*/
	public java.util.List<Device> findAll(int start, int end);

	/**
	* Returns an ordered range of all the devices.
	*
	* <p>
	* Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to {@link QueryUtil#ALL_POS} will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent and pagination is required (<code>start</code> and <code>end</code> are not {@link QueryUtil#ALL_POS}), then the query will include the default ORDER BY logic from {@link DeviceModelImpl}. If both <code>orderByComparator</code> and pagination are absent, for performance reasons, the query will not have an ORDER BY clause and the returned result set will be sorted on by the primary key in an ascending order.
	* </p>
	*
	* @param start the lower bound of the range of devices
	* @param end the upper bound of the range of devices (not inclusive)
	* @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	* @return the ordered range of devices
	*/
	public java.util.List<Device> findAll(int start, int end,
		com.liferay.portal.kernel.util.OrderByComparator<Device> orderByComparator);

	/**
	* Returns an ordered range of all the devices.
	*
	* <p>
	* Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to {@link QueryUtil#ALL_POS} will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent and pagination is required (<code>start</code> and <code>end</code> are not {@link QueryUtil#ALL_POS}), then the query will include the default ORDER BY logic from {@link DeviceModelImpl}. If both <code>orderByComparator</code> and pagination are absent, for performance reasons, the query will not have an ORDER BY clause and the returned result set will be sorted on by the primary key in an ascending order.
	* </p>
	*
	* @param start the lower bound of the range of devices
	* @param end the upper bound of the range of devices (not inclusive)
	* @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	* @param retrieveFromCache whether to retrieve from the finder cache
	* @return the ordered range of devices
	*/
	public java.util.List<Device> findAll(int start, int end,
		com.liferay.portal.kernel.util.OrderByComparator<Device> orderByComparator,
		boolean retrieveFromCache);

	/**
	* Removes all the devices from the database.
	*/
	public void removeAll();

	/**
	* Returns the number of devices.
	*
	* @return the number of devices
	*/
	public int countAll();
}