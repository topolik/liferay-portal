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

import com.liferay.multi.factor.authentication.provider.otp.model.OTPEmail;

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
 * The persistence utility for the otp email service. This utility wraps {@link com.liferay.multi.factor.authentication.provider.otp.service.persistence.impl.OTPEmailPersistenceImpl} and provides direct access to the database for CRUD operations. This utility should only be used by the service layer, as it must operate within a transaction. Never access this utility in a JSP, controller, model, or other front-end class.
 *
 * <p>
 * Caching information and settings can be found in <code>portal.properties</code>
 * </p>
 *
 * @author arthurchan35
 * @see OTPEmailPersistence
 * @see com.liferay.multi.factor.authentication.provider.otp.service.persistence.impl.OTPEmailPersistenceImpl
 * @generated
 */
@ProviderType
public class OTPEmailUtil {
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
	public static void clearCache(OTPEmail otpEmail) {
		getPersistence().clearCache(otpEmail);
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
	public static Map<Serializable, OTPEmail> fetchByPrimaryKeys(
		Set<Serializable> primaryKeys) {
		return getPersistence().fetchByPrimaryKeys(primaryKeys);
	}

	/**
	 * @see com.liferay.portal.kernel.service.persistence.BasePersistence#findWithDynamicQuery(DynamicQuery)
	 */
	public static List<OTPEmail> findWithDynamicQuery(DynamicQuery dynamicQuery) {
		return getPersistence().findWithDynamicQuery(dynamicQuery);
	}

	/**
	 * @see com.liferay.portal.kernel.service.persistence.BasePersistence#findWithDynamicQuery(DynamicQuery, int, int)
	 */
	public static List<OTPEmail> findWithDynamicQuery(
		DynamicQuery dynamicQuery, int start, int end) {
		return getPersistence().findWithDynamicQuery(dynamicQuery, start, end);
	}

	/**
	 * @see com.liferay.portal.kernel.service.persistence.BasePersistence#findWithDynamicQuery(DynamicQuery, int, int, OrderByComparator)
	 */
	public static List<OTPEmail> findWithDynamicQuery(
		DynamicQuery dynamicQuery, int start, int end,
		OrderByComparator<OTPEmail> orderByComparator) {
		return getPersistence()
				   .findWithDynamicQuery(dynamicQuery, start, end,
			orderByComparator);
	}

	/**
	 * @see com.liferay.portal.kernel.service.persistence.BasePersistence#update(com.liferay.portal.kernel.model.BaseModel)
	 */
	public static OTPEmail update(OTPEmail otpEmail) {
		return getPersistence().update(otpEmail);
	}

	/**
	 * @see com.liferay.portal.kernel.service.persistence.BasePersistence#update(com.liferay.portal.kernel.model.BaseModel, ServiceContext)
	 */
	public static OTPEmail update(OTPEmail otpEmail,
		ServiceContext serviceContext) {
		return getPersistence().update(otpEmail, serviceContext);
	}

	/**
	* Returns the otp email where userId = &#63; or throws a {@link NoSuchEmailException} if it could not be found.
	*
	* @param userId the user ID
	* @return the matching otp email
	* @throws NoSuchEmailException if a matching otp email could not be found
	*/
	public static OTPEmail findByUserId(long userId)
		throws com.liferay.multi.factor.authentication.provider.otp.exception.NoSuchEmailException {
		return getPersistence().findByUserId(userId);
	}

	/**
	* Returns the otp email where userId = &#63; or returns <code>null</code> if it could not be found. Uses the finder cache.
	*
	* @param userId the user ID
	* @return the matching otp email, or <code>null</code> if a matching otp email could not be found
	*/
	public static OTPEmail fetchByUserId(long userId) {
		return getPersistence().fetchByUserId(userId);
	}

	/**
	* Returns the otp email where userId = &#63; or returns <code>null</code> if it could not be found, optionally using the finder cache.
	*
	* @param userId the user ID
	* @param retrieveFromCache whether to retrieve from the finder cache
	* @return the matching otp email, or <code>null</code> if a matching otp email could not be found
	*/
	public static OTPEmail fetchByUserId(long userId, boolean retrieveFromCache) {
		return getPersistence().fetchByUserId(userId, retrieveFromCache);
	}

	/**
	* Removes the otp email where userId = &#63; from the database.
	*
	* @param userId the user ID
	* @return the otp email that was removed
	*/
	public static OTPEmail removeByUserId(long userId)
		throws com.liferay.multi.factor.authentication.provider.otp.exception.NoSuchEmailException {
		return getPersistence().removeByUserId(userId);
	}

	/**
	* Returns the number of otp emails where userId = &#63;.
	*
	* @param userId the user ID
	* @return the number of matching otp emails
	*/
	public static int countByUserId(long userId) {
		return getPersistence().countByUserId(userId);
	}

	/**
	* Caches the otp email in the entity cache if it is enabled.
	*
	* @param otpEmail the otp email
	*/
	public static void cacheResult(OTPEmail otpEmail) {
		getPersistence().cacheResult(otpEmail);
	}

	/**
	* Caches the otp emails in the entity cache if it is enabled.
	*
	* @param otpEmails the otp emails
	*/
	public static void cacheResult(List<OTPEmail> otpEmails) {
		getPersistence().cacheResult(otpEmails);
	}

	/**
	* Creates a new otp email with the primary key. Does not add the otp email to the database.
	*
	* @param otpEmailId the primary key for the new otp email
	* @return the new otp email
	*/
	public static OTPEmail create(long otpEmailId) {
		return getPersistence().create(otpEmailId);
	}

	/**
	* Removes the otp email with the primary key from the database. Also notifies the appropriate model listeners.
	*
	* @param otpEmailId the primary key of the otp email
	* @return the otp email that was removed
	* @throws NoSuchEmailException if a otp email with the primary key could not be found
	*/
	public static OTPEmail remove(long otpEmailId)
		throws com.liferay.multi.factor.authentication.provider.otp.exception.NoSuchEmailException {
		return getPersistence().remove(otpEmailId);
	}

	public static OTPEmail updateImpl(OTPEmail otpEmail) {
		return getPersistence().updateImpl(otpEmail);
	}

	/**
	* Returns the otp email with the primary key or throws a {@link NoSuchEmailException} if it could not be found.
	*
	* @param otpEmailId the primary key of the otp email
	* @return the otp email
	* @throws NoSuchEmailException if a otp email with the primary key could not be found
	*/
	public static OTPEmail findByPrimaryKey(long otpEmailId)
		throws com.liferay.multi.factor.authentication.provider.otp.exception.NoSuchEmailException {
		return getPersistence().findByPrimaryKey(otpEmailId);
	}

	/**
	* Returns the otp email with the primary key or returns <code>null</code> if it could not be found.
	*
	* @param otpEmailId the primary key of the otp email
	* @return the otp email, or <code>null</code> if a otp email with the primary key could not be found
	*/
	public static OTPEmail fetchByPrimaryKey(long otpEmailId) {
		return getPersistence().fetchByPrimaryKey(otpEmailId);
	}

	/**
	* Returns all the otp emails.
	*
	* @return the otp emails
	*/
	public static List<OTPEmail> findAll() {
		return getPersistence().findAll();
	}

	/**
	* Returns a range of all the otp emails.
	*
	* <p>
	* Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to {@link QueryUtil#ALL_POS} will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent and pagination is required (<code>start</code> and <code>end</code> are not {@link QueryUtil#ALL_POS}), then the query will include the default ORDER BY logic from {@link OTPEmailModelImpl}. If both <code>orderByComparator</code> and pagination are absent, for performance reasons, the query will not have an ORDER BY clause and the returned result set will be sorted on by the primary key in an ascending order.
	* </p>
	*
	* @param start the lower bound of the range of otp emails
	* @param end the upper bound of the range of otp emails (not inclusive)
	* @return the range of otp emails
	*/
	public static List<OTPEmail> findAll(int start, int end) {
		return getPersistence().findAll(start, end);
	}

	/**
	* Returns an ordered range of all the otp emails.
	*
	* <p>
	* Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to {@link QueryUtil#ALL_POS} will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent and pagination is required (<code>start</code> and <code>end</code> are not {@link QueryUtil#ALL_POS}), then the query will include the default ORDER BY logic from {@link OTPEmailModelImpl}. If both <code>orderByComparator</code> and pagination are absent, for performance reasons, the query will not have an ORDER BY clause and the returned result set will be sorted on by the primary key in an ascending order.
	* </p>
	*
	* @param start the lower bound of the range of otp emails
	* @param end the upper bound of the range of otp emails (not inclusive)
	* @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	* @return the ordered range of otp emails
	*/
	public static List<OTPEmail> findAll(int start, int end,
		OrderByComparator<OTPEmail> orderByComparator) {
		return getPersistence().findAll(start, end, orderByComparator);
	}

	/**
	* Returns an ordered range of all the otp emails.
	*
	* <p>
	* Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to {@link QueryUtil#ALL_POS} will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent and pagination is required (<code>start</code> and <code>end</code> are not {@link QueryUtil#ALL_POS}), then the query will include the default ORDER BY logic from {@link OTPEmailModelImpl}. If both <code>orderByComparator</code> and pagination are absent, for performance reasons, the query will not have an ORDER BY clause and the returned result set will be sorted on by the primary key in an ascending order.
	* </p>
	*
	* @param start the lower bound of the range of otp emails
	* @param end the upper bound of the range of otp emails (not inclusive)
	* @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	* @param retrieveFromCache whether to retrieve from the finder cache
	* @return the ordered range of otp emails
	*/
	public static List<OTPEmail> findAll(int start, int end,
		OrderByComparator<OTPEmail> orderByComparator, boolean retrieveFromCache) {
		return getPersistence()
				   .findAll(start, end, orderByComparator, retrieveFromCache);
	}

	/**
	* Removes all the otp emails from the database.
	*/
	public static void removeAll() {
		getPersistence().removeAll();
	}

	/**
	* Returns the number of otp emails.
	*
	* @return the number of otp emails
	*/
	public static int countAll() {
		return getPersistence().countAll();
	}

	public static OTPEmailPersistence getPersistence() {
		return _serviceTracker.getService();
	}

	private static ServiceTracker<OTPEmailPersistence, OTPEmailPersistence> _serviceTracker;

	static {
		Bundle bundle = FrameworkUtil.getBundle(OTPEmailPersistence.class);

		ServiceTracker<OTPEmailPersistence, OTPEmailPersistence> serviceTracker = new ServiceTracker<OTPEmailPersistence, OTPEmailPersistence>(bundle.getBundleContext(),
				OTPEmailPersistence.class, null);

		serviceTracker.open();

		_serviceTracker = serviceTracker;
	}
}