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

import com.bemis.portal.twofa.device.manager.model.Device;

import com.liferay.osgi.util.ServiceTrackerFactory;

import com.liferay.portal.kernel.dao.orm.DynamicQuery;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.util.OrderByComparator;

import org.osgi.util.tracker.ServiceTracker;

import java.util.List;

/**
 * The persistence utility for the device service. This utility wraps {@link com.bemis.portal.twofa.device.manager.service.persistence.impl.DevicePersistenceImpl} and provides direct access to the database for CRUD operations. This utility should only be used by the service layer, as it must operate within a transaction. Never access this utility in a JSP, controller, model, or other front-end class.
 *
 * <p>
 * Caching information and settings can be found in <code>portal.properties</code>
 * </p>
 *
 * @author Prathima Shreenath
 * @see DevicePersistence
 * @see com.bemis.portal.twofa.device.manager.service.persistence.impl.DevicePersistenceImpl
 * @generated
 */
@ProviderType
public class DeviceUtil {
	/*
	 * NOTE FOR DEVELOPERS:
	 *
	 * Never modify this class directly. Modify <code>service.xml</code> and rerun ServiceBuilder to regenerate this class.
	 */

	/**
	 * @see com.liferay.portal.kernel.service.persistence.BasePersistence#clearCache()
	 */
	public static void clearCache() {
		getPersistence().clearCache();
	}

	/**
	 * @see com.liferay.portal.kernel.service.persistence.BasePersistence#clearCache(com.liferay.portal.kernel.model.BaseModel)
	 */
	public static void clearCache(Device device) {
		getPersistence().clearCache(device);
	}

	/**
	 * @see com.liferay.portal.kernel.service.persistence.BasePersistence#countWithDynamicQuery(DynamicQuery)
	 */
	public static long countWithDynamicQuery(DynamicQuery dynamicQuery) {
		return getPersistence().countWithDynamicQuery(dynamicQuery);
	}

	/**
	 * @see com.liferay.portal.kernel.service.persistence.BasePersistence#findWithDynamicQuery(DynamicQuery)
	 */
	public static List<Device> findWithDynamicQuery(DynamicQuery dynamicQuery) {
		return getPersistence().findWithDynamicQuery(dynamicQuery);
	}

	/**
	 * @see com.liferay.portal.kernel.service.persistence.BasePersistence#findWithDynamicQuery(DynamicQuery, int, int)
	 */
	public static List<Device> findWithDynamicQuery(DynamicQuery dynamicQuery,
		int start, int end) {
		return getPersistence().findWithDynamicQuery(dynamicQuery, start, end);
	}

	/**
	 * @see com.liferay.portal.kernel.service.persistence.BasePersistence#findWithDynamicQuery(DynamicQuery, int, int, OrderByComparator)
	 */
	public static List<Device> findWithDynamicQuery(DynamicQuery dynamicQuery,
		int start, int end, OrderByComparator<Device> orderByComparator) {
		return getPersistence()
				   .findWithDynamicQuery(dynamicQuery, start, end,
			orderByComparator);
	}

	/**
	 * @see com.liferay.portal.kernel.service.persistence.BasePersistence#update(com.liferay.portal.kernel.model.BaseModel)
	 */
	public static Device update(Device device) {
		return getPersistence().update(device);
	}

	/**
	 * @see com.liferay.portal.kernel.service.persistence.BasePersistence#update(com.liferay.portal.kernel.model.BaseModel, ServiceContext)
	 */
	public static Device update(Device device, ServiceContext serviceContext) {
		return getPersistence().update(device, serviceContext);
	}

	/**
	* Returns all the devices where portalUserId = &#63;.
	*
	* @param portalUserId the portal user ID
	* @return the matching devices
	*/
	public static List<Device> findByPortalUserId(long portalUserId) {
		return getPersistence().findByPortalUserId(portalUserId);
	}

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
	public static List<Device> findByPortalUserId(long portalUserId, int start,
		int end) {
		return getPersistence().findByPortalUserId(portalUserId, start, end);
	}

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
	public static List<Device> findByPortalUserId(long portalUserId, int start,
		int end, OrderByComparator<Device> orderByComparator) {
		return getPersistence()
				   .findByPortalUserId(portalUserId, start, end,
			orderByComparator);
	}

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
	public static List<Device> findByPortalUserId(long portalUserId, int start,
		int end, OrderByComparator<Device> orderByComparator,
		boolean retrieveFromCache) {
		return getPersistence()
				   .findByPortalUserId(portalUserId, start, end,
			orderByComparator, retrieveFromCache);
	}

	/**
	* Returns the first device in the ordered set where portalUserId = &#63;.
	*
	* @param portalUserId the portal user ID
	* @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	* @return the first matching device
	* @throws NoSuchDeviceException if a matching device could not be found
	*/
	public static Device findByPortalUserId_First(long portalUserId,
		OrderByComparator<Device> orderByComparator)
		throws com.bemis.portal.twofa.device.manager.exception.NoSuchDeviceException {
		return getPersistence()
				   .findByPortalUserId_First(portalUserId, orderByComparator);
	}

	/**
	* Returns the first device in the ordered set where portalUserId = &#63;.
	*
	* @param portalUserId the portal user ID
	* @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	* @return the first matching device, or <code>null</code> if a matching device could not be found
	*/
	public static Device fetchByPortalUserId_First(long portalUserId,
		OrderByComparator<Device> orderByComparator) {
		return getPersistence()
				   .fetchByPortalUserId_First(portalUserId, orderByComparator);
	}

	/**
	* Returns the last device in the ordered set where portalUserId = &#63;.
	*
	* @param portalUserId the portal user ID
	* @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	* @return the last matching device
	* @throws NoSuchDeviceException if a matching device could not be found
	*/
	public static Device findByPortalUserId_Last(long portalUserId,
		OrderByComparator<Device> orderByComparator)
		throws com.bemis.portal.twofa.device.manager.exception.NoSuchDeviceException {
		return getPersistence()
				   .findByPortalUserId_Last(portalUserId, orderByComparator);
	}

	/**
	* Returns the last device in the ordered set where portalUserId = &#63;.
	*
	* @param portalUserId the portal user ID
	* @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	* @return the last matching device, or <code>null</code> if a matching device could not be found
	*/
	public static Device fetchByPortalUserId_Last(long portalUserId,
		OrderByComparator<Device> orderByComparator) {
		return getPersistence()
				   .fetchByPortalUserId_Last(portalUserId, orderByComparator);
	}

	/**
	* Returns the devices before and after the current device in the ordered set where portalUserId = &#63;.
	*
	* @param deviceId the primary key of the current device
	* @param portalUserId the portal user ID
	* @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	* @return the previous, current, and next device
	* @throws NoSuchDeviceException if a device with the primary key could not be found
	*/
	public static Device[] findByPortalUserId_PrevAndNext(long deviceId,
		long portalUserId, OrderByComparator<Device> orderByComparator)
		throws com.bemis.portal.twofa.device.manager.exception.NoSuchDeviceException {
		return getPersistence()
				   .findByPortalUserId_PrevAndNext(deviceId, portalUserId,
			orderByComparator);
	}

	/**
	* Removes all the devices where portalUserId = &#63; from the database.
	*
	* @param portalUserId the portal user ID
	*/
	public static void removeByPortalUserId(long portalUserId) {
		getPersistence().removeByPortalUserId(portalUserId);
	}

	/**
	* Returns the number of devices where portalUserId = &#63;.
	*
	* @param portalUserId the portal user ID
	* @return the number of matching devices
	*/
	public static int countByPortalUserId(long portalUserId) {
		return getPersistence().countByPortalUserId(portalUserId);
	}

	/**
	* Returns the device where portalUserId = &#63; and deviceIP = &#63; or throws a {@link NoSuchDeviceException} if it could not be found.
	*
	* @param portalUserId the portal user ID
	* @param deviceIP the device i p
	* @return the matching device
	* @throws NoSuchDeviceException if a matching device could not be found
	*/
	public static Device findByPortalUserId_DeviceIP(long portalUserId,
		java.lang.String deviceIP)
		throws com.bemis.portal.twofa.device.manager.exception.NoSuchDeviceException {
		return getPersistence()
				   .findByPortalUserId_DeviceIP(portalUserId, deviceIP);
	}

	/**
	* Returns the device where portalUserId = &#63; and deviceIP = &#63; or returns <code>null</code> if it could not be found. Uses the finder cache.
	*
	* @param portalUserId the portal user ID
	* @param deviceIP the device i p
	* @return the matching device, or <code>null</code> if a matching device could not be found
	*/
	public static Device fetchByPortalUserId_DeviceIP(long portalUserId,
		java.lang.String deviceIP) {
		return getPersistence()
				   .fetchByPortalUserId_DeviceIP(portalUserId, deviceIP);
	}

	/**
	* Returns the device where portalUserId = &#63; and deviceIP = &#63; or returns <code>null</code> if it could not be found, optionally using the finder cache.
	*
	* @param portalUserId the portal user ID
	* @param deviceIP the device i p
	* @param retrieveFromCache whether to retrieve from the finder cache
	* @return the matching device, or <code>null</code> if a matching device could not be found
	*/
	public static Device fetchByPortalUserId_DeviceIP(long portalUserId,
		java.lang.String deviceIP, boolean retrieveFromCache) {
		return getPersistence()
				   .fetchByPortalUserId_DeviceIP(portalUserId, deviceIP,
			retrieveFromCache);
	}

	/**
	* Removes the device where portalUserId = &#63; and deviceIP = &#63; from the database.
	*
	* @param portalUserId the portal user ID
	* @param deviceIP the device i p
	* @return the device that was removed
	*/
	public static Device removeByPortalUserId_DeviceIP(long portalUserId,
		java.lang.String deviceIP)
		throws com.bemis.portal.twofa.device.manager.exception.NoSuchDeviceException {
		return getPersistence()
				   .removeByPortalUserId_DeviceIP(portalUserId, deviceIP);
	}

	/**
	* Returns the number of devices where portalUserId = &#63; and deviceIP = &#63;.
	*
	* @param portalUserId the portal user ID
	* @param deviceIP the device i p
	* @return the number of matching devices
	*/
	public static int countByPortalUserId_DeviceIP(long portalUserId,
		java.lang.String deviceIP) {
		return getPersistence()
				   .countByPortalUserId_DeviceIP(portalUserId, deviceIP);
	}

	/**
	* Returns all the devices where verified = &#63; and portalUserId = &#63;.
	*
	* @param verified the verified
	* @param portalUserId the portal user ID
	* @return the matching devices
	*/
	public static List<Device> findByVerified_PortalUserId(boolean verified,
		long portalUserId) {
		return getPersistence()
				   .findByVerified_PortalUserId(verified, portalUserId);
	}

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
	public static List<Device> findByVerified_PortalUserId(boolean verified,
		long portalUserId, int start, int end) {
		return getPersistence()
				   .findByVerified_PortalUserId(verified, portalUserId, start,
			end);
	}

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
	public static List<Device> findByVerified_PortalUserId(boolean verified,
		long portalUserId, int start, int end,
		OrderByComparator<Device> orderByComparator) {
		return getPersistence()
				   .findByVerified_PortalUserId(verified, portalUserId, start,
			end, orderByComparator);
	}

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
	public static List<Device> findByVerified_PortalUserId(boolean verified,
		long portalUserId, int start, int end,
		OrderByComparator<Device> orderByComparator, boolean retrieveFromCache) {
		return getPersistence()
				   .findByVerified_PortalUserId(verified, portalUserId, start,
			end, orderByComparator, retrieveFromCache);
	}

	/**
	* Returns the first device in the ordered set where verified = &#63; and portalUserId = &#63;.
	*
	* @param verified the verified
	* @param portalUserId the portal user ID
	* @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	* @return the first matching device
	* @throws NoSuchDeviceException if a matching device could not be found
	*/
	public static Device findByVerified_PortalUserId_First(boolean verified,
		long portalUserId, OrderByComparator<Device> orderByComparator)
		throws com.bemis.portal.twofa.device.manager.exception.NoSuchDeviceException {
		return getPersistence()
				   .findByVerified_PortalUserId_First(verified, portalUserId,
			orderByComparator);
	}

	/**
	* Returns the first device in the ordered set where verified = &#63; and portalUserId = &#63;.
	*
	* @param verified the verified
	* @param portalUserId the portal user ID
	* @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	* @return the first matching device, or <code>null</code> if a matching device could not be found
	*/
	public static Device fetchByVerified_PortalUserId_First(boolean verified,
		long portalUserId, OrderByComparator<Device> orderByComparator) {
		return getPersistence()
				   .fetchByVerified_PortalUserId_First(verified, portalUserId,
			orderByComparator);
	}

	/**
	* Returns the last device in the ordered set where verified = &#63; and portalUserId = &#63;.
	*
	* @param verified the verified
	* @param portalUserId the portal user ID
	* @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	* @return the last matching device
	* @throws NoSuchDeviceException if a matching device could not be found
	*/
	public static Device findByVerified_PortalUserId_Last(boolean verified,
		long portalUserId, OrderByComparator<Device> orderByComparator)
		throws com.bemis.portal.twofa.device.manager.exception.NoSuchDeviceException {
		return getPersistence()
				   .findByVerified_PortalUserId_Last(verified, portalUserId,
			orderByComparator);
	}

	/**
	* Returns the last device in the ordered set where verified = &#63; and portalUserId = &#63;.
	*
	* @param verified the verified
	* @param portalUserId the portal user ID
	* @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	* @return the last matching device, or <code>null</code> if a matching device could not be found
	*/
	public static Device fetchByVerified_PortalUserId_Last(boolean verified,
		long portalUserId, OrderByComparator<Device> orderByComparator) {
		return getPersistence()
				   .fetchByVerified_PortalUserId_Last(verified, portalUserId,
			orderByComparator);
	}

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
	public static Device[] findByVerified_PortalUserId_PrevAndNext(
		long deviceId, boolean verified, long portalUserId,
		OrderByComparator<Device> orderByComparator)
		throws com.bemis.portal.twofa.device.manager.exception.NoSuchDeviceException {
		return getPersistence()
				   .findByVerified_PortalUserId_PrevAndNext(deviceId, verified,
			portalUserId, orderByComparator);
	}

	/**
	* Removes all the devices where verified = &#63; and portalUserId = &#63; from the database.
	*
	* @param verified the verified
	* @param portalUserId the portal user ID
	*/
	public static void removeByVerified_PortalUserId(boolean verified,
		long portalUserId) {
		getPersistence().removeByVerified_PortalUserId(verified, portalUserId);
	}

	/**
	* Returns the number of devices where verified = &#63; and portalUserId = &#63;.
	*
	* @param verified the verified
	* @param portalUserId the portal user ID
	* @return the number of matching devices
	*/
	public static int countByVerified_PortalUserId(boolean verified,
		long portalUserId) {
		return getPersistence()
				   .countByVerified_PortalUserId(verified, portalUserId);
	}

	/**
	* Returns the device where verified = &#63; and portalUserId = &#63; and deviceIP = &#63; or throws a {@link NoSuchDeviceException} if it could not be found.
	*
	* @param verified the verified
	* @param portalUserId the portal user ID
	* @param deviceIP the device i p
	* @return the matching device
	* @throws NoSuchDeviceException if a matching device could not be found
	*/
	public static Device findByVerified_PortalUserId_DeviceIP(
		boolean verified, long portalUserId, java.lang.String deviceIP)
		throws com.bemis.portal.twofa.device.manager.exception.NoSuchDeviceException {
		return getPersistence()
				   .findByVerified_PortalUserId_DeviceIP(verified,
			portalUserId, deviceIP);
	}

	/**
	* Returns the device where verified = &#63; and portalUserId = &#63; and deviceIP = &#63; or returns <code>null</code> if it could not be found. Uses the finder cache.
	*
	* @param verified the verified
	* @param portalUserId the portal user ID
	* @param deviceIP the device i p
	* @return the matching device, or <code>null</code> if a matching device could not be found
	*/
	public static Device fetchByVerified_PortalUserId_DeviceIP(
		boolean verified, long portalUserId, java.lang.String deviceIP) {
		return getPersistence()
				   .fetchByVerified_PortalUserId_DeviceIP(verified,
			portalUserId, deviceIP);
	}

	/**
	* Returns the device where verified = &#63; and portalUserId = &#63; and deviceIP = &#63; or returns <code>null</code> if it could not be found, optionally using the finder cache.
	*
	* @param verified the verified
	* @param portalUserId the portal user ID
	* @param deviceIP the device i p
	* @param retrieveFromCache whether to retrieve from the finder cache
	* @return the matching device, or <code>null</code> if a matching device could not be found
	*/
	public static Device fetchByVerified_PortalUserId_DeviceIP(
		boolean verified, long portalUserId, java.lang.String deviceIP,
		boolean retrieveFromCache) {
		return getPersistence()
				   .fetchByVerified_PortalUserId_DeviceIP(verified,
			portalUserId, deviceIP, retrieveFromCache);
	}

	/**
	* Removes the device where verified = &#63; and portalUserId = &#63; and deviceIP = &#63; from the database.
	*
	* @param verified the verified
	* @param portalUserId the portal user ID
	* @param deviceIP the device i p
	* @return the device that was removed
	*/
	public static Device removeByVerified_PortalUserId_DeviceIP(
		boolean verified, long portalUserId, java.lang.String deviceIP)
		throws com.bemis.portal.twofa.device.manager.exception.NoSuchDeviceException {
		return getPersistence()
				   .removeByVerified_PortalUserId_DeviceIP(verified,
			portalUserId, deviceIP);
	}

	/**
	* Returns the number of devices where verified = &#63; and portalUserId = &#63; and deviceIP = &#63;.
	*
	* @param verified the verified
	* @param portalUserId the portal user ID
	* @param deviceIP the device i p
	* @return the number of matching devices
	*/
	public static int countByVerified_PortalUserId_DeviceIP(boolean verified,
		long portalUserId, java.lang.String deviceIP) {
		return getPersistence()
				   .countByVerified_PortalUserId_DeviceIP(verified,
			portalUserId, deviceIP);
	}

	/**
	* Returns the device where tempDevice = &#63; and portalUserId = &#63; and deviceIP = &#63; or throws a {@link NoSuchDeviceException} if it could not be found.
	*
	* @param tempDevice the temp device
	* @param portalUserId the portal user ID
	* @param deviceIP the device i p
	* @return the matching device
	* @throws NoSuchDeviceException if a matching device could not be found
	*/
	public static Device findByTempDevice_PortalUserId_DeviceIP(
		boolean tempDevice, long portalUserId, java.lang.String deviceIP)
		throws com.bemis.portal.twofa.device.manager.exception.NoSuchDeviceException {
		return getPersistence()
				   .findByTempDevice_PortalUserId_DeviceIP(tempDevice,
			portalUserId, deviceIP);
	}

	/**
	* Returns the device where tempDevice = &#63; and portalUserId = &#63; and deviceIP = &#63; or returns <code>null</code> if it could not be found. Uses the finder cache.
	*
	* @param tempDevice the temp device
	* @param portalUserId the portal user ID
	* @param deviceIP the device i p
	* @return the matching device, or <code>null</code> if a matching device could not be found
	*/
	public static Device fetchByTempDevice_PortalUserId_DeviceIP(
		boolean tempDevice, long portalUserId, java.lang.String deviceIP) {
		return getPersistence()
				   .fetchByTempDevice_PortalUserId_DeviceIP(tempDevice,
			portalUserId, deviceIP);
	}

	/**
	* Returns the device where tempDevice = &#63; and portalUserId = &#63; and deviceIP = &#63; or returns <code>null</code> if it could not be found, optionally using the finder cache.
	*
	* @param tempDevice the temp device
	* @param portalUserId the portal user ID
	* @param deviceIP the device i p
	* @param retrieveFromCache whether to retrieve from the finder cache
	* @return the matching device, or <code>null</code> if a matching device could not be found
	*/
	public static Device fetchByTempDevice_PortalUserId_DeviceIP(
		boolean tempDevice, long portalUserId, java.lang.String deviceIP,
		boolean retrieveFromCache) {
		return getPersistence()
				   .fetchByTempDevice_PortalUserId_DeviceIP(tempDevice,
			portalUserId, deviceIP, retrieveFromCache);
	}

	/**
	* Removes the device where tempDevice = &#63; and portalUserId = &#63; and deviceIP = &#63; from the database.
	*
	* @param tempDevice the temp device
	* @param portalUserId the portal user ID
	* @param deviceIP the device i p
	* @return the device that was removed
	*/
	public static Device removeByTempDevice_PortalUserId_DeviceIP(
		boolean tempDevice, long portalUserId, java.lang.String deviceIP)
		throws com.bemis.portal.twofa.device.manager.exception.NoSuchDeviceException {
		return getPersistence()
				   .removeByTempDevice_PortalUserId_DeviceIP(tempDevice,
			portalUserId, deviceIP);
	}

	/**
	* Returns the number of devices where tempDevice = &#63; and portalUserId = &#63; and deviceIP = &#63;.
	*
	* @param tempDevice the temp device
	* @param portalUserId the portal user ID
	* @param deviceIP the device i p
	* @return the number of matching devices
	*/
	public static int countByTempDevice_PortalUserId_DeviceIP(
		boolean tempDevice, long portalUserId, java.lang.String deviceIP) {
		return getPersistence()
				   .countByTempDevice_PortalUserId_DeviceIP(tempDevice,
			portalUserId, deviceIP);
	}

	/**
	* Caches the device in the entity cache if it is enabled.
	*
	* @param device the device
	*/
	public static void cacheResult(Device device) {
		getPersistence().cacheResult(device);
	}

	/**
	* Caches the devices in the entity cache if it is enabled.
	*
	* @param devices the devices
	*/
	public static void cacheResult(List<Device> devices) {
		getPersistence().cacheResult(devices);
	}

	/**
	* Creates a new device with the primary key. Does not add the device to the database.
	*
	* @param deviceId the primary key for the new device
	* @return the new device
	*/
	public static Device create(long deviceId) {
		return getPersistence().create(deviceId);
	}

	/**
	* Removes the device with the primary key from the database. Also notifies the appropriate model listeners.
	*
	* @param deviceId the primary key of the device
	* @return the device that was removed
	* @throws NoSuchDeviceException if a device with the primary key could not be found
	*/
	public static Device remove(long deviceId)
		throws com.bemis.portal.twofa.device.manager.exception.NoSuchDeviceException {
		return getPersistence().remove(deviceId);
	}

	public static Device updateImpl(Device device) {
		return getPersistence().updateImpl(device);
	}

	/**
	* Returns the device with the primary key or throws a {@link NoSuchDeviceException} if it could not be found.
	*
	* @param deviceId the primary key of the device
	* @return the device
	* @throws NoSuchDeviceException if a device with the primary key could not be found
	*/
	public static Device findByPrimaryKey(long deviceId)
		throws com.bemis.portal.twofa.device.manager.exception.NoSuchDeviceException {
		return getPersistence().findByPrimaryKey(deviceId);
	}

	/**
	* Returns the device with the primary key or returns <code>null</code> if it could not be found.
	*
	* @param deviceId the primary key of the device
	* @return the device, or <code>null</code> if a device with the primary key could not be found
	*/
	public static Device fetchByPrimaryKey(long deviceId) {
		return getPersistence().fetchByPrimaryKey(deviceId);
	}

	public static java.util.Map<java.io.Serializable, Device> fetchByPrimaryKeys(
		java.util.Set<java.io.Serializable> primaryKeys) {
		return getPersistence().fetchByPrimaryKeys(primaryKeys);
	}

	/**
	* Returns all the devices.
	*
	* @return the devices
	*/
	public static List<Device> findAll() {
		return getPersistence().findAll();
	}

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
	public static List<Device> findAll(int start, int end) {
		return getPersistence().findAll(start, end);
	}

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
	public static List<Device> findAll(int start, int end,
		OrderByComparator<Device> orderByComparator) {
		return getPersistence().findAll(start, end, orderByComparator);
	}

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
	public static List<Device> findAll(int start, int end,
		OrderByComparator<Device> orderByComparator, boolean retrieveFromCache) {
		return getPersistence()
				   .findAll(start, end, orderByComparator, retrieveFromCache);
	}

	/**
	* Removes all the devices from the database.
	*/
	public static void removeAll() {
		getPersistence().removeAll();
	}

	/**
	* Returns the number of devices.
	*
	* @return the number of devices
	*/
	public static int countAll() {
		return getPersistence().countAll();
	}

	public static DevicePersistence getPersistence() {
		return _serviceTracker.getService();
	}

	private static ServiceTracker<DevicePersistence, DevicePersistence> _serviceTracker =
		ServiceTrackerFactory.open(DevicePersistence.class);
}