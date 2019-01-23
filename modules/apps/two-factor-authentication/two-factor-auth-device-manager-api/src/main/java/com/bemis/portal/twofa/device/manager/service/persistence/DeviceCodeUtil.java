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

import com.bemis.portal.twofa.device.manager.model.DeviceCode;

import com.liferay.osgi.util.ServiceTrackerFactory;

import com.liferay.portal.kernel.dao.orm.DynamicQuery;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.util.OrderByComparator;

import org.osgi.util.tracker.ServiceTracker;

import java.util.List;

/**
 * The persistence utility for the device code service. This utility wraps {@link com.bemis.portal.twofa.device.manager.service.persistence.impl.DeviceCodePersistenceImpl} and provides direct access to the database for CRUD operations. This utility should only be used by the service layer, as it must operate within a transaction. Never access this utility in a JSP, controller, model, or other front-end class.
 *
 * <p>
 * Caching information and settings can be found in <code>portal.properties</code>
 * </p>
 *
 * @author Prathima Shreenath
 * @see DeviceCodePersistence
 * @see com.bemis.portal.twofa.device.manager.service.persistence.impl.DeviceCodePersistenceImpl
 * @generated
 */
@ProviderType
public class DeviceCodeUtil {
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
	public static void clearCache(DeviceCode deviceCode) {
		getPersistence().clearCache(deviceCode);
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
	public static List<DeviceCode> findWithDynamicQuery(
		DynamicQuery dynamicQuery) {
		return getPersistence().findWithDynamicQuery(dynamicQuery);
	}

	/**
	 * @see com.liferay.portal.kernel.service.persistence.BasePersistence#findWithDynamicQuery(DynamicQuery, int, int)
	 */
	public static List<DeviceCode> findWithDynamicQuery(
		DynamicQuery dynamicQuery, int start, int end) {
		return getPersistence().findWithDynamicQuery(dynamicQuery, start, end);
	}

	/**
	 * @see com.liferay.portal.kernel.service.persistence.BasePersistence#findWithDynamicQuery(DynamicQuery, int, int, OrderByComparator)
	 */
	public static List<DeviceCode> findWithDynamicQuery(
		DynamicQuery dynamicQuery, int start, int end,
		OrderByComparator<DeviceCode> orderByComparator) {
		return getPersistence()
				   .findWithDynamicQuery(dynamicQuery, start, end,
			orderByComparator);
	}

	/**
	 * @see com.liferay.portal.kernel.service.persistence.BasePersistence#update(com.liferay.portal.kernel.model.BaseModel)
	 */
	public static DeviceCode update(DeviceCode deviceCode) {
		return getPersistence().update(deviceCode);
	}

	/**
	 * @see com.liferay.portal.kernel.service.persistence.BasePersistence#update(com.liferay.portal.kernel.model.BaseModel, ServiceContext)
	 */
	public static DeviceCode update(DeviceCode deviceCode,
		ServiceContext serviceContext) {
		return getPersistence().update(deviceCode, serviceContext);
	}

	/**
	* Returns the device code where portalUserId = &#63; or throws a {@link NoSuchDeviceCodeException} if it could not be found.
	*
	* @param portalUserId the portal user ID
	* @return the matching device code
	* @throws NoSuchDeviceCodeException if a matching device code could not be found
	*/
	public static DeviceCode findByPortalUserId(long portalUserId)
		throws com.bemis.portal.twofa.device.manager.exception.NoSuchDeviceCodeException {
		return getPersistence().findByPortalUserId(portalUserId);
	}

	/**
	* Returns the device code where portalUserId = &#63; or returns <code>null</code> if it could not be found. Uses the finder cache.
	*
	* @param portalUserId the portal user ID
	* @return the matching device code, or <code>null</code> if a matching device code could not be found
	*/
	public static DeviceCode fetchByPortalUserId(long portalUserId) {
		return getPersistence().fetchByPortalUserId(portalUserId);
	}

	/**
	* Returns the device code where portalUserId = &#63; or returns <code>null</code> if it could not be found, optionally using the finder cache.
	*
	* @param portalUserId the portal user ID
	* @param retrieveFromCache whether to retrieve from the finder cache
	* @return the matching device code, or <code>null</code> if a matching device code could not be found
	*/
	public static DeviceCode fetchByPortalUserId(long portalUserId,
		boolean retrieveFromCache) {
		return getPersistence()
				   .fetchByPortalUserId(portalUserId, retrieveFromCache);
	}

	/**
	* Removes the device code where portalUserId = &#63; from the database.
	*
	* @param portalUserId the portal user ID
	* @return the device code that was removed
	*/
	public static DeviceCode removeByPortalUserId(long portalUserId)
		throws com.bemis.portal.twofa.device.manager.exception.NoSuchDeviceCodeException {
		return getPersistence().removeByPortalUserId(portalUserId);
	}

	/**
	* Returns the number of device codes where portalUserId = &#63;.
	*
	* @param portalUserId the portal user ID
	* @return the number of matching device codes
	*/
	public static int countByPortalUserId(long portalUserId) {
		return getPersistence().countByPortalUserId(portalUserId);
	}

	/**
	* Returns the device code where emailAddress = &#63; or throws a {@link NoSuchDeviceCodeException} if it could not be found.
	*
	* @param emailAddress the email address
	* @return the matching device code
	* @throws NoSuchDeviceCodeException if a matching device code could not be found
	*/
	public static DeviceCode findByEmailAddress(java.lang.String emailAddress)
		throws com.bemis.portal.twofa.device.manager.exception.NoSuchDeviceCodeException {
		return getPersistence().findByEmailAddress(emailAddress);
	}

	/**
	* Returns the device code where emailAddress = &#63; or returns <code>null</code> if it could not be found. Uses the finder cache.
	*
	* @param emailAddress the email address
	* @return the matching device code, or <code>null</code> if a matching device code could not be found
	*/
	public static DeviceCode fetchByEmailAddress(java.lang.String emailAddress) {
		return getPersistence().fetchByEmailAddress(emailAddress);
	}

	/**
	* Returns the device code where emailAddress = &#63; or returns <code>null</code> if it could not be found, optionally using the finder cache.
	*
	* @param emailAddress the email address
	* @param retrieveFromCache whether to retrieve from the finder cache
	* @return the matching device code, or <code>null</code> if a matching device code could not be found
	*/
	public static DeviceCode fetchByEmailAddress(
		java.lang.String emailAddress, boolean retrieveFromCache) {
		return getPersistence()
				   .fetchByEmailAddress(emailAddress, retrieveFromCache);
	}

	/**
	* Removes the device code where emailAddress = &#63; from the database.
	*
	* @param emailAddress the email address
	* @return the device code that was removed
	*/
	public static DeviceCode removeByEmailAddress(java.lang.String emailAddress)
		throws com.bemis.portal.twofa.device.manager.exception.NoSuchDeviceCodeException {
		return getPersistence().removeByEmailAddress(emailAddress);
	}

	/**
	* Returns the number of device codes where emailAddress = &#63;.
	*
	* @param emailAddress the email address
	* @return the number of matching device codes
	*/
	public static int countByEmailAddress(java.lang.String emailAddress) {
		return getPersistence().countByEmailAddress(emailAddress);
	}

	/**
	* Caches the device code in the entity cache if it is enabled.
	*
	* @param deviceCode the device code
	*/
	public static void cacheResult(DeviceCode deviceCode) {
		getPersistence().cacheResult(deviceCode);
	}

	/**
	* Caches the device codes in the entity cache if it is enabled.
	*
	* @param deviceCodes the device codes
	*/
	public static void cacheResult(List<DeviceCode> deviceCodes) {
		getPersistence().cacheResult(deviceCodes);
	}

	/**
	* Creates a new device code with the primary key. Does not add the device code to the database.
	*
	* @param deviceCodeId the primary key for the new device code
	* @return the new device code
	*/
	public static DeviceCode create(long deviceCodeId) {
		return getPersistence().create(deviceCodeId);
	}

	/**
	* Removes the device code with the primary key from the database. Also notifies the appropriate model listeners.
	*
	* @param deviceCodeId the primary key of the device code
	* @return the device code that was removed
	* @throws NoSuchDeviceCodeException if a device code with the primary key could not be found
	*/
	public static DeviceCode remove(long deviceCodeId)
		throws com.bemis.portal.twofa.device.manager.exception.NoSuchDeviceCodeException {
		return getPersistence().remove(deviceCodeId);
	}

	public static DeviceCode updateImpl(DeviceCode deviceCode) {
		return getPersistence().updateImpl(deviceCode);
	}

	/**
	* Returns the device code with the primary key or throws a {@link NoSuchDeviceCodeException} if it could not be found.
	*
	* @param deviceCodeId the primary key of the device code
	* @return the device code
	* @throws NoSuchDeviceCodeException if a device code with the primary key could not be found
	*/
	public static DeviceCode findByPrimaryKey(long deviceCodeId)
		throws com.bemis.portal.twofa.device.manager.exception.NoSuchDeviceCodeException {
		return getPersistence().findByPrimaryKey(deviceCodeId);
	}

	/**
	* Returns the device code with the primary key or returns <code>null</code> if it could not be found.
	*
	* @param deviceCodeId the primary key of the device code
	* @return the device code, or <code>null</code> if a device code with the primary key could not be found
	*/
	public static DeviceCode fetchByPrimaryKey(long deviceCodeId) {
		return getPersistence().fetchByPrimaryKey(deviceCodeId);
	}

	public static java.util.Map<java.io.Serializable, DeviceCode> fetchByPrimaryKeys(
		java.util.Set<java.io.Serializable> primaryKeys) {
		return getPersistence().fetchByPrimaryKeys(primaryKeys);
	}

	/**
	* Returns all the device codes.
	*
	* @return the device codes
	*/
	public static List<DeviceCode> findAll() {
		return getPersistence().findAll();
	}

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
	public static List<DeviceCode> findAll(int start, int end) {
		return getPersistence().findAll(start, end);
	}

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
	public static List<DeviceCode> findAll(int start, int end,
		OrderByComparator<DeviceCode> orderByComparator) {
		return getPersistence().findAll(start, end, orderByComparator);
	}

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
	public static List<DeviceCode> findAll(int start, int end,
		OrderByComparator<DeviceCode> orderByComparator,
		boolean retrieveFromCache) {
		return getPersistence()
				   .findAll(start, end, orderByComparator, retrieveFromCache);
	}

	/**
	* Removes all the device codes from the database.
	*/
	public static void removeAll() {
		getPersistence().removeAll();
	}

	/**
	* Returns the number of device codes.
	*
	* @return the number of device codes
	*/
	public static int countAll() {
		return getPersistence().countAll();
	}

	public static DeviceCodePersistence getPersistence() {
		return _serviceTracker.getService();
	}

	private static ServiceTracker<DeviceCodePersistence, DeviceCodePersistence> _serviceTracker =
		ServiceTrackerFactory.open(DeviceCodePersistence.class);
}