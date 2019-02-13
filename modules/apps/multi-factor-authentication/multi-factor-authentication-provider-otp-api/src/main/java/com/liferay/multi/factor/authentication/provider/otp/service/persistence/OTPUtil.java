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

package com.liferay.multi.factor.authentication.provider.otp.service.persistence;

import aQute.bnd.annotation.ProviderType;

import com.liferay.multi.factor.authentication.provider.otp.model.OTP;

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
 * The persistence utility for the otp service. This utility wraps {@link com.liferay.multi.factor.authentication.provider.otp.service.persistence.impl.OTPPersistenceImpl} and provides direct access to the database for CRUD operations. This utility should only be used by the service layer, as it must operate within a transaction. Never access this utility in a JSP, controller, model, or other front-end class.
 *
 * <p>
 * Caching information and settings can be found in <code>portal.properties</code>
 * </p>
 *
 * @author arthurchan35
 * @see OTPPersistence
 * @see com.liferay.multi.factor.authentication.provider.otp.service.persistence.impl.OTPPersistenceImpl
 * @generated
 */
@ProviderType
public class OTPUtil {
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
	public static void clearCache(OTP otp) {
		getPersistence().clearCache(otp);
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
	public static Map<Serializable, OTP> fetchByPrimaryKeys(
		Set<Serializable> primaryKeys) {
		return getPersistence().fetchByPrimaryKeys(primaryKeys);
	}

	/**
	 * @see com.liferay.portal.kernel.service.persistence.BasePersistence#findWithDynamicQuery(DynamicQuery)
	 */
	public static List<OTP> findWithDynamicQuery(DynamicQuery dynamicQuery) {
		return getPersistence().findWithDynamicQuery(dynamicQuery);
	}

	/**
	 * @see com.liferay.portal.kernel.service.persistence.BasePersistence#findWithDynamicQuery(DynamicQuery, int, int)
	 */
	public static List<OTP> findWithDynamicQuery(DynamicQuery dynamicQuery,
		int start, int end) {
		return getPersistence().findWithDynamicQuery(dynamicQuery, start, end);
	}

	/**
	 * @see com.liferay.portal.kernel.service.persistence.BasePersistence#findWithDynamicQuery(DynamicQuery, int, int, OrderByComparator)
	 */
	public static List<OTP> findWithDynamicQuery(DynamicQuery dynamicQuery,
		int start, int end, OrderByComparator<OTP> orderByComparator) {
		return getPersistence()
				   .findWithDynamicQuery(dynamicQuery, start, end,
			orderByComparator);
	}

	/**
	 * @see com.liferay.portal.kernel.service.persistence.BasePersistence#update(com.liferay.portal.kernel.model.BaseModel)
	 */
	public static OTP update(OTP otp) {
		return getPersistence().update(otp);
	}

	/**
	 * @see com.liferay.portal.kernel.service.persistence.BasePersistence#update(com.liferay.portal.kernel.model.BaseModel, ServiceContext)
	 */
	public static OTP update(OTP otp, ServiceContext serviceContext) {
		return getPersistence().update(otp, serviceContext);
	}

	/**
	* Returns the otp where userId = &#63; or throws a {@link NoSuchOTPException} if it could not be found.
	*
	* @param userId the user ID
	* @return the matching otp
	* @throws NoSuchOTPException if a matching otp could not be found
	*/
	public static OTP findByUserId(long userId)
		throws com.liferay.multi.factor.authentication.provider.otp.exception.NoSuchOTPException {
		return getPersistence().findByUserId(userId);
	}

	/**
	* Returns the otp where userId = &#63; or returns <code>null</code> if it could not be found. Uses the finder cache.
	*
	* @param userId the user ID
	* @return the matching otp, or <code>null</code> if a matching otp could not be found
	*/
	public static OTP fetchByUserId(long userId) {
		return getPersistence().fetchByUserId(userId);
	}

	/**
	* Returns the otp where userId = &#63; or returns <code>null</code> if it could not be found, optionally using the finder cache.
	*
	* @param userId the user ID
	* @param retrieveFromCache whether to retrieve from the finder cache
	* @return the matching otp, or <code>null</code> if a matching otp could not be found
	*/
	public static OTP fetchByUserId(long userId, boolean retrieveFromCache) {
		return getPersistence().fetchByUserId(userId, retrieveFromCache);
	}

	/**
	* Removes the otp where userId = &#63; from the database.
	*
	* @param userId the user ID
	* @return the otp that was removed
	*/
	public static OTP removeByUserId(long userId)
		throws com.liferay.multi.factor.authentication.provider.otp.exception.NoSuchOTPException {
		return getPersistence().removeByUserId(userId);
	}

	/**
	* Returns the number of otps where userId = &#63;.
	*
	* @param userId the user ID
	* @return the number of matching otps
	*/
	public static int countByUserId(long userId) {
		return getPersistence().countByUserId(userId);
	}

	/**
	* Caches the otp in the entity cache if it is enabled.
	*
	* @param otp the otp
	*/
	public static void cacheResult(OTP otp) {
		getPersistence().cacheResult(otp);
	}

	/**
	* Caches the otps in the entity cache if it is enabled.
	*
	* @param otps the otps
	*/
	public static void cacheResult(List<OTP> otps) {
		getPersistence().cacheResult(otps);
	}

	/**
	* Creates a new otp with the primary key. Does not add the otp to the database.
	*
	* @param otpId the primary key for the new otp
	* @return the new otp
	*/
	public static OTP create(long otpId) {
		return getPersistence().create(otpId);
	}

	/**
	* Removes the otp with the primary key from the database. Also notifies the appropriate model listeners.
	*
	* @param otpId the primary key of the otp
	* @return the otp that was removed
	* @throws NoSuchOTPException if a otp with the primary key could not be found
	*/
	public static OTP remove(long otpId)
		throws com.liferay.multi.factor.authentication.provider.otp.exception.NoSuchOTPException {
		return getPersistence().remove(otpId);
	}

	public static OTP updateImpl(OTP otp) {
		return getPersistence().updateImpl(otp);
	}

	/**
	* Returns the otp with the primary key or throws a {@link NoSuchOTPException} if it could not be found.
	*
	* @param otpId the primary key of the otp
	* @return the otp
	* @throws NoSuchOTPException if a otp with the primary key could not be found
	*/
	public static OTP findByPrimaryKey(long otpId)
		throws com.liferay.multi.factor.authentication.provider.otp.exception.NoSuchOTPException {
		return getPersistence().findByPrimaryKey(otpId);
	}

	/**
	* Returns the otp with the primary key or returns <code>null</code> if it could not be found.
	*
	* @param otpId the primary key of the otp
	* @return the otp, or <code>null</code> if a otp with the primary key could not be found
	*/
	public static OTP fetchByPrimaryKey(long otpId) {
		return getPersistence().fetchByPrimaryKey(otpId);
	}

	/**
	* Returns all the otps.
	*
	* @return the otps
	*/
	public static List<OTP> findAll() {
		return getPersistence().findAll();
	}

	/**
	* Returns a range of all the otps.
	*
	* <p>
	* Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to {@link QueryUtil#ALL_POS} will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent and pagination is required (<code>start</code> and <code>end</code> are not {@link QueryUtil#ALL_POS}), then the query will include the default ORDER BY logic from {@link OTPModelImpl}. If both <code>orderByComparator</code> and pagination are absent, for performance reasons, the query will not have an ORDER BY clause and the returned result set will be sorted on by the primary key in an ascending order.
	* </p>
	*
	* @param start the lower bound of the range of otps
	* @param end the upper bound of the range of otps (not inclusive)
	* @return the range of otps
	*/
	public static List<OTP> findAll(int start, int end) {
		return getPersistence().findAll(start, end);
	}

	/**
	* Returns an ordered range of all the otps.
	*
	* <p>
	* Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to {@link QueryUtil#ALL_POS} will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent and pagination is required (<code>start</code> and <code>end</code> are not {@link QueryUtil#ALL_POS}), then the query will include the default ORDER BY logic from {@link OTPModelImpl}. If both <code>orderByComparator</code> and pagination are absent, for performance reasons, the query will not have an ORDER BY clause and the returned result set will be sorted on by the primary key in an ascending order.
	* </p>
	*
	* @param start the lower bound of the range of otps
	* @param end the upper bound of the range of otps (not inclusive)
	* @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	* @return the ordered range of otps
	*/
	public static List<OTP> findAll(int start, int end,
		OrderByComparator<OTP> orderByComparator) {
		return getPersistence().findAll(start, end, orderByComparator);
	}

	/**
	* Returns an ordered range of all the otps.
	*
	* <p>
	* Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to {@link QueryUtil#ALL_POS} will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent and pagination is required (<code>start</code> and <code>end</code> are not {@link QueryUtil#ALL_POS}), then the query will include the default ORDER BY logic from {@link OTPModelImpl}. If both <code>orderByComparator</code> and pagination are absent, for performance reasons, the query will not have an ORDER BY clause and the returned result set will be sorted on by the primary key in an ascending order.
	* </p>
	*
	* @param start the lower bound of the range of otps
	* @param end the upper bound of the range of otps (not inclusive)
	* @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	* @param retrieveFromCache whether to retrieve from the finder cache
	* @return the ordered range of otps
	*/
	public static List<OTP> findAll(int start, int end,
		OrderByComparator<OTP> orderByComparator, boolean retrieveFromCache) {
		return getPersistence()
				   .findAll(start, end, orderByComparator, retrieveFromCache);
	}

	/**
	* Removes all the otps from the database.
	*/
	public static void removeAll() {
		getPersistence().removeAll();
	}

	/**
	* Returns the number of otps.
	*
	* @return the number of otps
	*/
	public static int countAll() {
		return getPersistence().countAll();
	}

	public static OTPPersistence getPersistence() {
		return _serviceTracker.getService();
	}

	private static ServiceTracker<OTPPersistence, OTPPersistence> _serviceTracker;

	static {
		Bundle bundle = FrameworkUtil.getBundle(OTPPersistence.class);

		ServiceTracker<OTPPersistence, OTPPersistence> serviceTracker = new ServiceTracker<OTPPersistence, OTPPersistence>(bundle.getBundleContext(),
				OTPPersistence.class, null);

		serviceTracker.open();

		_serviceTracker = serviceTracker;
	}
}