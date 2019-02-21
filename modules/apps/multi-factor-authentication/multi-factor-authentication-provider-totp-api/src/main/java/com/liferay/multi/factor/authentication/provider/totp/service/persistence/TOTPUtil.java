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

package com.liferay.multi.factor.authentication.provider.totp.service.persistence;

import aQute.bnd.annotation.ProviderType;

import com.liferay.multi.factor.authentication.provider.totp.model.TOTP;

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
 * The persistence utility for the totp service. This utility wraps {@link com.liferay.multi.factor.authentication.provider.totp.service.persistence.impl.TOTPPersistenceImpl} and provides direct access to the database for CRUD operations. This utility should only be used by the service layer, as it must operate within a transaction. Never access this utility in a JSP, controller, model, or other front-end class.
 *
 * <p>
 * Caching information and settings can be found in <code>portal.properties</code>
 * </p>
 *
 * @author arthurchan35
 * @see TOTPPersistence
 * @see com.liferay.multi.factor.authentication.provider.totp.service.persistence.impl.TOTPPersistenceImpl
 * @generated
 */
@ProviderType
public class TOTPUtil {
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
	public static void clearCache(TOTP totp) {
		getPersistence().clearCache(totp);
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
	public static Map<Serializable, TOTP> fetchByPrimaryKeys(
		Set<Serializable> primaryKeys) {
		return getPersistence().fetchByPrimaryKeys(primaryKeys);
	}

	/**
	 * @see com.liferay.portal.kernel.service.persistence.BasePersistence#findWithDynamicQuery(DynamicQuery)
	 */
	public static List<TOTP> findWithDynamicQuery(DynamicQuery dynamicQuery) {
		return getPersistence().findWithDynamicQuery(dynamicQuery);
	}

	/**
	 * @see com.liferay.portal.kernel.service.persistence.BasePersistence#findWithDynamicQuery(DynamicQuery, int, int)
	 */
	public static List<TOTP> findWithDynamicQuery(DynamicQuery dynamicQuery,
		int start, int end) {
		return getPersistence().findWithDynamicQuery(dynamicQuery, start, end);
	}

	/**
	 * @see com.liferay.portal.kernel.service.persistence.BasePersistence#findWithDynamicQuery(DynamicQuery, int, int, OrderByComparator)
	 */
	public static List<TOTP> findWithDynamicQuery(DynamicQuery dynamicQuery,
		int start, int end, OrderByComparator<TOTP> orderByComparator) {
		return getPersistence()
				   .findWithDynamicQuery(dynamicQuery, start, end,
			orderByComparator);
	}

	/**
	 * @see com.liferay.portal.kernel.service.persistence.BasePersistence#update(com.liferay.portal.kernel.model.BaseModel)
	 */
	public static TOTP update(TOTP totp) {
		return getPersistence().update(totp);
	}

	/**
	 * @see com.liferay.portal.kernel.service.persistence.BasePersistence#update(com.liferay.portal.kernel.model.BaseModel, ServiceContext)
	 */
	public static TOTP update(TOTP totp, ServiceContext serviceContext) {
		return getPersistence().update(totp, serviceContext);
	}

	/**
	* Returns the totp where userId = &#63; or throws a {@link NoSuchTOTPException} if it could not be found.
	*
	* @param userId the user ID
	* @return the matching totp
	* @throws NoSuchTOTPException if a matching totp could not be found
	*/
	public static TOTP findByUserId(long userId)
		throws com.liferay.multi.factor.authentication.provider.totp.exception.NoSuchTOTPException {
		return getPersistence().findByUserId(userId);
	}

	/**
	* Returns the totp where userId = &#63; or returns <code>null</code> if it could not be found. Uses the finder cache.
	*
	* @param userId the user ID
	* @return the matching totp, or <code>null</code> if a matching totp could not be found
	*/
	public static TOTP fetchByUserId(long userId) {
		return getPersistence().fetchByUserId(userId);
	}

	/**
	* Returns the totp where userId = &#63; or returns <code>null</code> if it could not be found, optionally using the finder cache.
	*
	* @param userId the user ID
	* @param retrieveFromCache whether to retrieve from the finder cache
	* @return the matching totp, or <code>null</code> if a matching totp could not be found
	*/
	public static TOTP fetchByUserId(long userId, boolean retrieveFromCache) {
		return getPersistence().fetchByUserId(userId, retrieveFromCache);
	}

	/**
	* Removes the totp where userId = &#63; from the database.
	*
	* @param userId the user ID
	* @return the totp that was removed
	*/
	public static TOTP removeByUserId(long userId)
		throws com.liferay.multi.factor.authentication.provider.totp.exception.NoSuchTOTPException {
		return getPersistence().removeByUserId(userId);
	}

	/**
	* Returns the number of totps where userId = &#63;.
	*
	* @param userId the user ID
	* @return the number of matching totps
	*/
	public static int countByUserId(long userId) {
		return getPersistence().countByUserId(userId);
	}

	/**
	* Caches the totp in the entity cache if it is enabled.
	*
	* @param totp the totp
	*/
	public static void cacheResult(TOTP totp) {
		getPersistence().cacheResult(totp);
	}

	/**
	* Caches the totps in the entity cache if it is enabled.
	*
	* @param totps the totps
	*/
	public static void cacheResult(List<TOTP> totps) {
		getPersistence().cacheResult(totps);
	}

	/**
	* Creates a new totp with the primary key. Does not add the totp to the database.
	*
	* @param totpId the primary key for the new totp
	* @return the new totp
	*/
	public static TOTP create(long totpId) {
		return getPersistence().create(totpId);
	}

	/**
	* Removes the totp with the primary key from the database. Also notifies the appropriate model listeners.
	*
	* @param totpId the primary key of the totp
	* @return the totp that was removed
	* @throws NoSuchTOTPException if a totp with the primary key could not be found
	*/
	public static TOTP remove(long totpId)
		throws com.liferay.multi.factor.authentication.provider.totp.exception.NoSuchTOTPException {
		return getPersistence().remove(totpId);
	}

	public static TOTP updateImpl(TOTP totp) {
		return getPersistence().updateImpl(totp);
	}

	/**
	* Returns the totp with the primary key or throws a {@link NoSuchTOTPException} if it could not be found.
	*
	* @param totpId the primary key of the totp
	* @return the totp
	* @throws NoSuchTOTPException if a totp with the primary key could not be found
	*/
	public static TOTP findByPrimaryKey(long totpId)
		throws com.liferay.multi.factor.authentication.provider.totp.exception.NoSuchTOTPException {
		return getPersistence().findByPrimaryKey(totpId);
	}

	/**
	* Returns the totp with the primary key or returns <code>null</code> if it could not be found.
	*
	* @param totpId the primary key of the totp
	* @return the totp, or <code>null</code> if a totp with the primary key could not be found
	*/
	public static TOTP fetchByPrimaryKey(long totpId) {
		return getPersistence().fetchByPrimaryKey(totpId);
	}

	/**
	* Returns all the totps.
	*
	* @return the totps
	*/
	public static List<TOTP> findAll() {
		return getPersistence().findAll();
	}

	/**
	* Returns a range of all the totps.
	*
	* <p>
	* Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to {@link QueryUtil#ALL_POS} will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent and pagination is required (<code>start</code> and <code>end</code> are not {@link QueryUtil#ALL_POS}), then the query will include the default ORDER BY logic from {@link TOTPModelImpl}. If both <code>orderByComparator</code> and pagination are absent, for performance reasons, the query will not have an ORDER BY clause and the returned result set will be sorted on by the primary key in an ascending order.
	* </p>
	*
	* @param start the lower bound of the range of totps
	* @param end the upper bound of the range of totps (not inclusive)
	* @return the range of totps
	*/
	public static List<TOTP> findAll(int start, int end) {
		return getPersistence().findAll(start, end);
	}

	/**
	* Returns an ordered range of all the totps.
	*
	* <p>
	* Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to {@link QueryUtil#ALL_POS} will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent and pagination is required (<code>start</code> and <code>end</code> are not {@link QueryUtil#ALL_POS}), then the query will include the default ORDER BY logic from {@link TOTPModelImpl}. If both <code>orderByComparator</code> and pagination are absent, for performance reasons, the query will not have an ORDER BY clause and the returned result set will be sorted on by the primary key in an ascending order.
	* </p>
	*
	* @param start the lower bound of the range of totps
	* @param end the upper bound of the range of totps (not inclusive)
	* @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	* @return the ordered range of totps
	*/
	public static List<TOTP> findAll(int start, int end,
		OrderByComparator<TOTP> orderByComparator) {
		return getPersistence().findAll(start, end, orderByComparator);
	}

	/**
	* Returns an ordered range of all the totps.
	*
	* <p>
	* Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to {@link QueryUtil#ALL_POS} will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent and pagination is required (<code>start</code> and <code>end</code> are not {@link QueryUtil#ALL_POS}), then the query will include the default ORDER BY logic from {@link TOTPModelImpl}. If both <code>orderByComparator</code> and pagination are absent, for performance reasons, the query will not have an ORDER BY clause and the returned result set will be sorted on by the primary key in an ascending order.
	* </p>
	*
	* @param start the lower bound of the range of totps
	* @param end the upper bound of the range of totps (not inclusive)
	* @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	* @param retrieveFromCache whether to retrieve from the finder cache
	* @return the ordered range of totps
	*/
	public static List<TOTP> findAll(int start, int end,
		OrderByComparator<TOTP> orderByComparator, boolean retrieveFromCache) {
		return getPersistence()
				   .findAll(start, end, orderByComparator, retrieveFromCache);
	}

	/**
	* Removes all the totps from the database.
	*/
	public static void removeAll() {
		getPersistence().removeAll();
	}

	/**
	* Returns the number of totps.
	*
	* @return the number of totps
	*/
	public static int countAll() {
		return getPersistence().countAll();
	}

	public static TOTPPersistence getPersistence() {
		return _serviceTracker.getService();
	}

	private static ServiceTracker<TOTPPersistence, TOTPPersistence> _serviceTracker;

	static {
		Bundle bundle = FrameworkUtil.getBundle(TOTPPersistence.class);

		ServiceTracker<TOTPPersistence, TOTPPersistence> serviceTracker = new ServiceTracker<TOTPPersistence, TOTPPersistence>(bundle.getBundleContext(),
				TOTPPersistence.class, null);

		serviceTracker.open();

		_serviceTracker = serviceTracker;
	}
}