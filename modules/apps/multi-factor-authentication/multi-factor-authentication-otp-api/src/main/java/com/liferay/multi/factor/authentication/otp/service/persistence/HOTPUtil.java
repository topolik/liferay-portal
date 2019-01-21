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

package com.liferay.multi.factor.authentication.otp.service.persistence;

import aQute.bnd.annotation.ProviderType;

import com.liferay.multi.factor.authentication.otp.model.HOTP;

import com.liferay.portal.kernel.dao.orm.DynamicQuery;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.util.OrderByComparator;

import org.osgi.framework.Bundle;
import org.osgi.framework.FrameworkUtil;

import org.osgi.util.tracker.ServiceTracker;

import java.io.Serializable;

import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * The persistence utility for the hotp service. This utility wraps {@link com.liferay.multi.factor.authentication.otp.service.persistence.impl.HOTPPersistenceImpl} and provides direct access to the database for CRUD operations. This utility should only be used by the service layer, as it must operate within a transaction. Never access this utility in a JSP, controller, model, or other front-end class.
 *
 * <p>
 * Caching information and settings can be found in <code>portal.properties</code>
 * </p>
 *
 * @author arthurchan35
 * @see HOTPPersistence
 * @see com.liferay.multi.factor.authentication.otp.service.persistence.impl.HOTPPersistenceImpl
 * @generated
 */
@ProviderType
public class HOTPUtil {
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
	public static void clearCache(HOTP hotp) {
		getPersistence().clearCache(hotp);
	}

	/**
	 * @see com.liferay.portal.kernel.service.persistence.BasePersistence#countWithDynamicQuery(DynamicQuery)
	 */
	public static long countWithDynamicQuery(DynamicQuery dynamicQuery) {
		return getPersistence().countWithDynamicQuery(dynamicQuery);
	}

	/**
	 * @see com.liferay.portal.kernel.service.persistence.BasePersistence#fetchByPrimaryKeys(Set)
	 */
	public static Map<Serializable, HOTP> fetchByPrimaryKeys(
		Set<Serializable> primaryKeys) {
		return getPersistence().fetchByPrimaryKeys(primaryKeys);
	}

	/**
	 * @see com.liferay.portal.kernel.service.persistence.BasePersistence#findWithDynamicQuery(DynamicQuery)
	 */
	public static List<HOTP> findWithDynamicQuery(DynamicQuery dynamicQuery) {
		return getPersistence().findWithDynamicQuery(dynamicQuery);
	}

	/**
	 * @see com.liferay.portal.kernel.service.persistence.BasePersistence#findWithDynamicQuery(DynamicQuery, int, int)
	 */
	public static List<HOTP> findWithDynamicQuery(DynamicQuery dynamicQuery,
		int start, int end) {
		return getPersistence().findWithDynamicQuery(dynamicQuery, start, end);
	}

	/**
	 * @see com.liferay.portal.kernel.service.persistence.BasePersistence#findWithDynamicQuery(DynamicQuery, int, int, OrderByComparator)
	 */
	public static List<HOTP> findWithDynamicQuery(DynamicQuery dynamicQuery,
		int start, int end, OrderByComparator<HOTP> orderByComparator) {
		return getPersistence()
				   .findWithDynamicQuery(dynamicQuery, start, end,
			orderByComparator);
	}

	/**
	 * @see com.liferay.portal.kernel.service.persistence.BasePersistence#update(com.liferay.portal.kernel.model.BaseModel)
	 */
	public static HOTP update(HOTP hotp) {
		return getPersistence().update(hotp);
	}

	/**
	 * @see com.liferay.portal.kernel.service.persistence.BasePersistence#update(com.liferay.portal.kernel.model.BaseModel, ServiceContext)
	 */
	public static HOTP update(HOTP hotp, ServiceContext serviceContext) {
		return getPersistence().update(hotp, serviceContext);
	}

	/**
	* Returns the hotp where userId = &#63; or throws a {@link NoSuchHOTPException} if it could not be found.
	*
	* @param userId the user ID
	* @return the matching hotp
	* @throws NoSuchHOTPException if a matching hotp could not be found
	*/
	public static HOTP findByUserId(long userId)
		throws com.liferay.multi.factor.authentication.otp.exception.NoSuchHOTPException {
		return getPersistence().findByUserId(userId);
	}

	/**
	* Returns the hotp where userId = &#63; or returns <code>null</code> if it could not be found. Uses the finder cache.
	*
	* @param userId the user ID
	* @return the matching hotp, or <code>null</code> if a matching hotp could not be found
	*/
	public static HOTP fetchByUserId(long userId) {
		return getPersistence().fetchByUserId(userId);
	}

	/**
	* Returns the hotp where userId = &#63; or returns <code>null</code> if it could not be found, optionally using the finder cache.
	*
	* @param userId the user ID
	* @param retrieveFromCache whether to retrieve from the finder cache
	* @return the matching hotp, or <code>null</code> if a matching hotp could not be found
	*/
	public static HOTP fetchByUserId(long userId, boolean retrieveFromCache) {
		return getPersistence().fetchByUserId(userId, retrieveFromCache);
	}

	/**
	* Removes the hotp where userId = &#63; from the database.
	*
	* @param userId the user ID
	* @return the hotp that was removed
	*/
	public static HOTP removeByUserId(long userId)
		throws com.liferay.multi.factor.authentication.otp.exception.NoSuchHOTPException {
		return getPersistence().removeByUserId(userId);
	}

	/**
	* Returns the number of hotps where userId = &#63;.
	*
	* @param userId the user ID
	* @return the number of matching hotps
	*/
	public static int countByUserId(long userId) {
		return getPersistence().countByUserId(userId);
	}

	/**
	* Caches the hotp in the entity cache if it is enabled.
	*
	* @param hotp the hotp
	*/
	public static void cacheResult(HOTP hotp) {
		getPersistence().cacheResult(hotp);
	}

	/**
	* Caches the hotps in the entity cache if it is enabled.
	*
	* @param hotps the hotps
	*/
	public static void cacheResult(List<HOTP> hotps) {
		getPersistence().cacheResult(hotps);
	}

	/**
	* Creates a new hotp with the primary key. Does not add the hotp to the database.
	*
	* @param hotpId the primary key for the new hotp
	* @return the new hotp
	*/
	public static HOTP create(long hotpId) {
		return getPersistence().create(hotpId);
	}

	/**
	* Removes the hotp with the primary key from the database. Also notifies the appropriate model listeners.
	*
	* @param hotpId the primary key of the hotp
	* @return the hotp that was removed
	* @throws NoSuchHOTPException if a hotp with the primary key could not be found
	*/
	public static HOTP remove(long hotpId)
		throws com.liferay.multi.factor.authentication.otp.exception.NoSuchHOTPException {
		return getPersistence().remove(hotpId);
	}

	public static HOTP updateImpl(HOTP hotp) {
		return getPersistence().updateImpl(hotp);
	}

	/**
	* Returns the hotp with the primary key or throws a {@link NoSuchHOTPException} if it could not be found.
	*
	* @param hotpId the primary key of the hotp
	* @return the hotp
	* @throws NoSuchHOTPException if a hotp with the primary key could not be found
	*/
	public static HOTP findByPrimaryKey(long hotpId)
		throws com.liferay.multi.factor.authentication.otp.exception.NoSuchHOTPException {
		return getPersistence().findByPrimaryKey(hotpId);
	}

	/**
	* Returns the hotp with the primary key or returns <code>null</code> if it could not be found.
	*
	* @param hotpId the primary key of the hotp
	* @return the hotp, or <code>null</code> if a hotp with the primary key could not be found
	*/
	public static HOTP fetchByPrimaryKey(long hotpId) {
		return getPersistence().fetchByPrimaryKey(hotpId);
	}

	/**
	* Returns all the hotps.
	*
	* @return the hotps
	*/
	public static List<HOTP> findAll() {
		return getPersistence().findAll();
	}

	/**
	* Returns a range of all the hotps.
	*
	* <p>
	* Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to {@link QueryUtil#ALL_POS} will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent and pagination is required (<code>start</code> and <code>end</code> are not {@link QueryUtil#ALL_POS}), then the query will include the default ORDER BY logic from {@link HOTPModelImpl}. If both <code>orderByComparator</code> and pagination are absent, for performance reasons, the query will not have an ORDER BY clause and the returned result set will be sorted on by the primary key in an ascending order.
	* </p>
	*
	* @param start the lower bound of the range of hotps
	* @param end the upper bound of the range of hotps (not inclusive)
	* @return the range of hotps
	*/
	public static List<HOTP> findAll(int start, int end) {
		return getPersistence().findAll(start, end);
	}

	/**
	* Returns an ordered range of all the hotps.
	*
	* <p>
	* Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to {@link QueryUtil#ALL_POS} will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent and pagination is required (<code>start</code> and <code>end</code> are not {@link QueryUtil#ALL_POS}), then the query will include the default ORDER BY logic from {@link HOTPModelImpl}. If both <code>orderByComparator</code> and pagination are absent, for performance reasons, the query will not have an ORDER BY clause and the returned result set will be sorted on by the primary key in an ascending order.
	* </p>
	*
	* @param start the lower bound of the range of hotps
	* @param end the upper bound of the range of hotps (not inclusive)
	* @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	* @return the ordered range of hotps
	*/
	public static List<HOTP> findAll(int start, int end,
		OrderByComparator<HOTP> orderByComparator) {
		return getPersistence().findAll(start, end, orderByComparator);
	}

	/**
	* Returns an ordered range of all the hotps.
	*
	* <p>
	* Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to {@link QueryUtil#ALL_POS} will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent and pagination is required (<code>start</code> and <code>end</code> are not {@link QueryUtil#ALL_POS}), then the query will include the default ORDER BY logic from {@link HOTPModelImpl}. If both <code>orderByComparator</code> and pagination are absent, for performance reasons, the query will not have an ORDER BY clause and the returned result set will be sorted on by the primary key in an ascending order.
	* </p>
	*
	* @param start the lower bound of the range of hotps
	* @param end the upper bound of the range of hotps (not inclusive)
	* @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	* @param retrieveFromCache whether to retrieve from the finder cache
	* @return the ordered range of hotps
	*/
	public static List<HOTP> findAll(int start, int end,
		OrderByComparator<HOTP> orderByComparator, boolean retrieveFromCache) {
		return getPersistence()
				   .findAll(start, end, orderByComparator, retrieveFromCache);
	}

	/**
	* Removes all the hotps from the database.
	*/
	public static void removeAll() {
		getPersistence().removeAll();
	}

	/**
	* Returns the number of hotps.
	*
	* @return the number of hotps
	*/
	public static int countAll() {
		return getPersistence().countAll();
	}

	public static HOTPPersistence getPersistence() {
		return _serviceTracker.getService();
	}

	private static ServiceTracker<HOTPPersistence, HOTPPersistence> _serviceTracker;

	static {
		Bundle bundle = FrameworkUtil.getBundle(HOTPPersistence.class);

		ServiceTracker<HOTPPersistence, HOTPPersistence> serviceTracker = new ServiceTracker<HOTPPersistence, HOTPPersistence>(bundle.getBundleContext(),
				HOTPPersistence.class, null);

		serviceTracker.open();

		_serviceTracker = serviceTracker;
	}
}