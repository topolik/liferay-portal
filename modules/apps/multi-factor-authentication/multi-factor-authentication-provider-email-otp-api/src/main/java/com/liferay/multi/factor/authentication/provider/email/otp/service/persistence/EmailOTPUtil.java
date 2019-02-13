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

package com.liferay.multi.factor.authentication.provider.email.otp.service.persistence;

import aQute.bnd.annotation.ProviderType;

import com.liferay.multi.factor.authentication.provider.email.otp.model.EmailOTP;

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
 * The persistence utility for the email otp service. This utility wraps {@link com.liferay.multi.factor.authentication.provider.email.otp.service.persistence.impl.EmailOTPPersistenceImpl} and provides direct access to the database for CRUD operations. This utility should only be used by the service layer, as it must operate within a transaction. Never access this utility in a JSP, controller, model, or other front-end class.
 *
 * <p>
 * Caching information and settings can be found in <code>portal.properties</code>
 * </p>
 *
 * @author arthurchan35
 * @see EmailOTPPersistence
 * @see com.liferay.multi.factor.authentication.provider.email.otp.service.persistence.impl.EmailOTPPersistenceImpl
 * @generated
 */
@ProviderType
public class EmailOTPUtil {
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
	public static void clearCache(EmailOTP emailOTP) {
		getPersistence().clearCache(emailOTP);
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
	public static Map<Serializable, EmailOTP> fetchByPrimaryKeys(
		Set<Serializable> primaryKeys) {
		return getPersistence().fetchByPrimaryKeys(primaryKeys);
	}

	/**
	 * @see com.liferay.portal.kernel.service.persistence.BasePersistence#findWithDynamicQuery(DynamicQuery)
	 */
	public static List<EmailOTP> findWithDynamicQuery(DynamicQuery dynamicQuery) {
		return getPersistence().findWithDynamicQuery(dynamicQuery);
	}

	/**
	 * @see com.liferay.portal.kernel.service.persistence.BasePersistence#findWithDynamicQuery(DynamicQuery, int, int)
	 */
	public static List<EmailOTP> findWithDynamicQuery(
		DynamicQuery dynamicQuery, int start, int end) {
		return getPersistence().findWithDynamicQuery(dynamicQuery, start, end);
	}

	/**
	 * @see com.liferay.portal.kernel.service.persistence.BasePersistence#findWithDynamicQuery(DynamicQuery, int, int, OrderByComparator)
	 */
	public static List<EmailOTP> findWithDynamicQuery(
		DynamicQuery dynamicQuery, int start, int end,
		OrderByComparator<EmailOTP> orderByComparator) {
		return getPersistence()
				   .findWithDynamicQuery(dynamicQuery, start, end,
			orderByComparator);
	}

	/**
	 * @see com.liferay.portal.kernel.service.persistence.BasePersistence#update(com.liferay.portal.kernel.model.BaseModel)
	 */
	public static EmailOTP update(EmailOTP emailOTP) {
		return getPersistence().update(emailOTP);
	}

	/**
	 * @see com.liferay.portal.kernel.service.persistence.BasePersistence#update(com.liferay.portal.kernel.model.BaseModel, ServiceContext)
	 */
	public static EmailOTP update(EmailOTP emailOTP,
		ServiceContext serviceContext) {
		return getPersistence().update(emailOTP, serviceContext);
	}

	/**
	* Returns the email otp where userId = &#63; or throws a {@link NoSuchEmailOTPException} if it could not be found.
	*
	* @param userId the user ID
	* @return the matching email otp
	* @throws NoSuchEmailOTPException if a matching email otp could not be found
	*/
	public static EmailOTP findByUserId(long userId)
		throws com.liferay.multi.factor.authentication.provider.email.otp.exception.NoSuchEmailOTPException {
		return getPersistence().findByUserId(userId);
	}

	/**
	* Returns the email otp where userId = &#63; or returns <code>null</code> if it could not be found. Uses the finder cache.
	*
	* @param userId the user ID
	* @return the matching email otp, or <code>null</code> if a matching email otp could not be found
	*/
	public static EmailOTP fetchByUserId(long userId) {
		return getPersistence().fetchByUserId(userId);
	}

	/**
	* Returns the email otp where userId = &#63; or returns <code>null</code> if it could not be found, optionally using the finder cache.
	*
	* @param userId the user ID
	* @param retrieveFromCache whether to retrieve from the finder cache
	* @return the matching email otp, or <code>null</code> if a matching email otp could not be found
	*/
	public static EmailOTP fetchByUserId(long userId, boolean retrieveFromCache) {
		return getPersistence().fetchByUserId(userId, retrieveFromCache);
	}

	/**
	* Removes the email otp where userId = &#63; from the database.
	*
	* @param userId the user ID
	* @return the email otp that was removed
	*/
	public static EmailOTP removeByUserId(long userId)
		throws com.liferay.multi.factor.authentication.provider.email.otp.exception.NoSuchEmailOTPException {
		return getPersistence().removeByUserId(userId);
	}

	/**
	* Returns the number of email otps where userId = &#63;.
	*
	* @param userId the user ID
	* @return the number of matching email otps
	*/
	public static int countByUserId(long userId) {
		return getPersistence().countByUserId(userId);
	}

	/**
	* Caches the email otp in the entity cache if it is enabled.
	*
	* @param emailOTP the email otp
	*/
	public static void cacheResult(EmailOTP emailOTP) {
		getPersistence().cacheResult(emailOTP);
	}

	/**
	* Caches the email otps in the entity cache if it is enabled.
	*
	* @param emailOTPs the email otps
	*/
	public static void cacheResult(List<EmailOTP> emailOTPs) {
		getPersistence().cacheResult(emailOTPs);
	}

	/**
	* Creates a new email otp with the primary key. Does not add the email otp to the database.
	*
	* @param emailOTPId the primary key for the new email otp
	* @return the new email otp
	*/
	public static EmailOTP create(long emailOTPId) {
		return getPersistence().create(emailOTPId);
	}

	/**
	* Removes the email otp with the primary key from the database. Also notifies the appropriate model listeners.
	*
	* @param emailOTPId the primary key of the email otp
	* @return the email otp that was removed
	* @throws NoSuchEmailOTPException if a email otp with the primary key could not be found
	*/
	public static EmailOTP remove(long emailOTPId)
		throws com.liferay.multi.factor.authentication.provider.email.otp.exception.NoSuchEmailOTPException {
		return getPersistence().remove(emailOTPId);
	}

	public static EmailOTP updateImpl(EmailOTP emailOTP) {
		return getPersistence().updateImpl(emailOTP);
	}

	/**
	* Returns the email otp with the primary key or throws a {@link NoSuchEmailOTPException} if it could not be found.
	*
	* @param emailOTPId the primary key of the email otp
	* @return the email otp
	* @throws NoSuchEmailOTPException if a email otp with the primary key could not be found
	*/
	public static EmailOTP findByPrimaryKey(long emailOTPId)
		throws com.liferay.multi.factor.authentication.provider.email.otp.exception.NoSuchEmailOTPException {
		return getPersistence().findByPrimaryKey(emailOTPId);
	}

	/**
	* Returns the email otp with the primary key or returns <code>null</code> if it could not be found.
	*
	* @param emailOTPId the primary key of the email otp
	* @return the email otp, or <code>null</code> if a email otp with the primary key could not be found
	*/
	public static EmailOTP fetchByPrimaryKey(long emailOTPId) {
		return getPersistence().fetchByPrimaryKey(emailOTPId);
	}

	/**
	* Returns all the email otps.
	*
	* @return the email otps
	*/
	public static List<EmailOTP> findAll() {
		return getPersistence().findAll();
	}

	/**
	* Returns a range of all the email otps.
	*
	* <p>
	* Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to {@link QueryUtil#ALL_POS} will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent and pagination is required (<code>start</code> and <code>end</code> are not {@link QueryUtil#ALL_POS}), then the query will include the default ORDER BY logic from {@link EmailOTPModelImpl}. If both <code>orderByComparator</code> and pagination are absent, for performance reasons, the query will not have an ORDER BY clause and the returned result set will be sorted on by the primary key in an ascending order.
	* </p>
	*
	* @param start the lower bound of the range of email otps
	* @param end the upper bound of the range of email otps (not inclusive)
	* @return the range of email otps
	*/
	public static List<EmailOTP> findAll(int start, int end) {
		return getPersistence().findAll(start, end);
	}

	/**
	* Returns an ordered range of all the email otps.
	*
	* <p>
	* Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to {@link QueryUtil#ALL_POS} will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent and pagination is required (<code>start</code> and <code>end</code> are not {@link QueryUtil#ALL_POS}), then the query will include the default ORDER BY logic from {@link EmailOTPModelImpl}. If both <code>orderByComparator</code> and pagination are absent, for performance reasons, the query will not have an ORDER BY clause and the returned result set will be sorted on by the primary key in an ascending order.
	* </p>
	*
	* @param start the lower bound of the range of email otps
	* @param end the upper bound of the range of email otps (not inclusive)
	* @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	* @return the ordered range of email otps
	*/
	public static List<EmailOTP> findAll(int start, int end,
		OrderByComparator<EmailOTP> orderByComparator) {
		return getPersistence().findAll(start, end, orderByComparator);
	}

	/**
	* Returns an ordered range of all the email otps.
	*
	* <p>
	* Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to {@link QueryUtil#ALL_POS} will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent and pagination is required (<code>start</code> and <code>end</code> are not {@link QueryUtil#ALL_POS}), then the query will include the default ORDER BY logic from {@link EmailOTPModelImpl}. If both <code>orderByComparator</code> and pagination are absent, for performance reasons, the query will not have an ORDER BY clause and the returned result set will be sorted on by the primary key in an ascending order.
	* </p>
	*
	* @param start the lower bound of the range of email otps
	* @param end the upper bound of the range of email otps (not inclusive)
	* @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	* @param retrieveFromCache whether to retrieve from the finder cache
	* @return the ordered range of email otps
	*/
	public static List<EmailOTP> findAll(int start, int end,
		OrderByComparator<EmailOTP> orderByComparator, boolean retrieveFromCache) {
		return getPersistence()
				   .findAll(start, end, orderByComparator, retrieveFromCache);
	}

	/**
	* Removes all the email otps from the database.
	*/
	public static void removeAll() {
		getPersistence().removeAll();
	}

	/**
	* Returns the number of email otps.
	*
	* @return the number of email otps
	*/
	public static int countAll() {
		return getPersistence().countAll();
	}

	public static EmailOTPPersistence getPersistence() {
		return _serviceTracker.getService();
	}

	private static ServiceTracker<EmailOTPPersistence, EmailOTPPersistence> _serviceTracker;

	static {
		Bundle bundle = FrameworkUtil.getBundle(EmailOTPPersistence.class);

		ServiceTracker<EmailOTPPersistence, EmailOTPPersistence> serviceTracker = new ServiceTracker<EmailOTPPersistence, EmailOTPPersistence>(bundle.getBundleContext(),
				EmailOTPPersistence.class, null);

		serviceTracker.open();

		_serviceTracker = serviceTracker;
	}
}