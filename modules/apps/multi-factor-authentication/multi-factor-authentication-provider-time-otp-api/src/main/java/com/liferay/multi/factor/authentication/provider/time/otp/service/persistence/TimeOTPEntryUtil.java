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

package com.liferay.multi.factor.authentication.provider.time.otp.service.persistence;

import aQute.bnd.annotation.ProviderType;

import com.liferay.multi.factor.authentication.provider.time.otp.model.TimeOTPEntry;
import com.liferay.portal.kernel.dao.orm.DynamicQuery;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.util.OrderByComparator;

import java.io.Serializable;

import java.util.List;
import java.util.Map;
import java.util.Set;

import org.osgi.framework.Bundle;
import org.osgi.framework.FrameworkUtil;
import org.osgi.util.tracker.ServiceTracker;

/**
 * The persistence utility for the time otp entry service. This utility wraps <code>com.liferay.multi.factor.authentication.provider.time.otp.service.persistence.impl.TimeOTPEntryPersistenceImpl</code> and provides direct access to the database for CRUD operations. This utility should only be used by the service layer, as it must operate within a transaction. Never access this utility in a JSP, controller, model, or other front-end class.
 *
 * <p>
 * Caching information and settings can be found in <code>portal.properties</code>
 * </p>
 *
 * @author arthurchan35
 * @see TimeOTPEntryPersistence
 * @generated
 */
@ProviderType
public class TimeOTPEntryUtil {

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
	public static void clearCache(TimeOTPEntry timeOTPEntry) {
		getPersistence().clearCache(timeOTPEntry);
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
	public static Map<Serializable, TimeOTPEntry> fetchByPrimaryKeys(
		Set<Serializable> primaryKeys) {

		return getPersistence().fetchByPrimaryKeys(primaryKeys);
	}

	/**
	 * @see com.liferay.portal.kernel.service.persistence.BasePersistence#findWithDynamicQuery(DynamicQuery)
	 */
	public static List<TimeOTPEntry> findWithDynamicQuery(
		DynamicQuery dynamicQuery) {

		return getPersistence().findWithDynamicQuery(dynamicQuery);
	}

	/**
	 * @see com.liferay.portal.kernel.service.persistence.BasePersistence#findWithDynamicQuery(DynamicQuery, int, int)
	 */
	public static List<TimeOTPEntry> findWithDynamicQuery(
		DynamicQuery dynamicQuery, int start, int end) {

		return getPersistence().findWithDynamicQuery(dynamicQuery, start, end);
	}

	/**
	 * @see com.liferay.portal.kernel.service.persistence.BasePersistence#findWithDynamicQuery(DynamicQuery, int, int, OrderByComparator)
	 */
	public static List<TimeOTPEntry> findWithDynamicQuery(
		DynamicQuery dynamicQuery, int start, int end,
		OrderByComparator<TimeOTPEntry> orderByComparator) {

		return getPersistence().findWithDynamicQuery(
			dynamicQuery, start, end, orderByComparator);
	}

	/**
	 * @see com.liferay.portal.kernel.service.persistence.BasePersistence#update(com.liferay.portal.kernel.model.BaseModel)
	 */
	public static TimeOTPEntry update(TimeOTPEntry timeOTPEntry) {
		return getPersistence().update(timeOTPEntry);
	}

	/**
	 * @see com.liferay.portal.kernel.service.persistence.BasePersistence#update(com.liferay.portal.kernel.model.BaseModel, ServiceContext)
	 */
	public static TimeOTPEntry update(
		TimeOTPEntry timeOTPEntry, ServiceContext serviceContext) {

		return getPersistence().update(timeOTPEntry, serviceContext);
	}

	/**
	 * Returns the time otp entry where userId = &#63; or throws a <code>NoSuchEntryException</code> if it could not be found.
	 *
	 * @param userId the user ID
	 * @return the matching time otp entry
	 * @throws NoSuchEntryException if a matching time otp entry could not be found
	 */
	public static TimeOTPEntry findByUserId(long userId)
		throws com.liferay.multi.factor.authentication.provider.time.otp.
			exception.NoSuchEntryException {

		return getPersistence().findByUserId(userId);
	}

	/**
	 * Returns the time otp entry where userId = &#63; or returns <code>null</code> if it could not be found. Uses the finder cache.
	 *
	 * @param userId the user ID
	 * @return the matching time otp entry, or <code>null</code> if a matching time otp entry could not be found
	 */
	public static TimeOTPEntry fetchByUserId(long userId) {
		return getPersistence().fetchByUserId(userId);
	}

	/**
	 * Returns the time otp entry where userId = &#63; or returns <code>null</code> if it could not be found, optionally using the finder cache.
	 *
	 * @param userId the user ID
	 * @param retrieveFromCache whether to retrieve from the finder cache
	 * @return the matching time otp entry, or <code>null</code> if a matching time otp entry could not be found
	 */
	public static TimeOTPEntry fetchByUserId(
		long userId, boolean retrieveFromCache) {

		return getPersistence().fetchByUserId(userId, retrieveFromCache);
	}

	/**
	 * Removes the time otp entry where userId = &#63; from the database.
	 *
	 * @param userId the user ID
	 * @return the time otp entry that was removed
	 */
	public static TimeOTPEntry removeByUserId(long userId)
		throws com.liferay.multi.factor.authentication.provider.time.otp.
			exception.NoSuchEntryException {

		return getPersistence().removeByUserId(userId);
	}

	/**
	 * Returns the number of time otp entries where userId = &#63;.
	 *
	 * @param userId the user ID
	 * @return the number of matching time otp entries
	 */
	public static int countByUserId(long userId) {
		return getPersistence().countByUserId(userId);
	}

	/**
	 * Caches the time otp entry in the entity cache if it is enabled.
	 *
	 * @param timeOTPEntry the time otp entry
	 */
	public static void cacheResult(TimeOTPEntry timeOTPEntry) {
		getPersistence().cacheResult(timeOTPEntry);
	}

	/**
	 * Caches the time otp entries in the entity cache if it is enabled.
	 *
	 * @param timeOTPEntries the time otp entries
	 */
	public static void cacheResult(List<TimeOTPEntry> timeOTPEntries) {
		getPersistence().cacheResult(timeOTPEntries);
	}

	/**
	 * Creates a new time otp entry with the primary key. Does not add the time otp entry to the database.
	 *
	 * @param entryId the primary key for the new time otp entry
	 * @return the new time otp entry
	 */
	public static TimeOTPEntry create(long entryId) {
		return getPersistence().create(entryId);
	}

	/**
	 * Removes the time otp entry with the primary key from the database. Also notifies the appropriate model listeners.
	 *
	 * @param entryId the primary key of the time otp entry
	 * @return the time otp entry that was removed
	 * @throws NoSuchEntryException if a time otp entry with the primary key could not be found
	 */
	public static TimeOTPEntry remove(long entryId)
		throws com.liferay.multi.factor.authentication.provider.time.otp.
			exception.NoSuchEntryException {

		return getPersistence().remove(entryId);
	}

	public static TimeOTPEntry updateImpl(TimeOTPEntry timeOTPEntry) {
		return getPersistence().updateImpl(timeOTPEntry);
	}

	/**
	 * Returns the time otp entry with the primary key or throws a <code>NoSuchEntryException</code> if it could not be found.
	 *
	 * @param entryId the primary key of the time otp entry
	 * @return the time otp entry
	 * @throws NoSuchEntryException if a time otp entry with the primary key could not be found
	 */
	public static TimeOTPEntry findByPrimaryKey(long entryId)
		throws com.liferay.multi.factor.authentication.provider.time.otp.
			exception.NoSuchEntryException {

		return getPersistence().findByPrimaryKey(entryId);
	}

	/**
	 * Returns the time otp entry with the primary key or returns <code>null</code> if it could not be found.
	 *
	 * @param entryId the primary key of the time otp entry
	 * @return the time otp entry, or <code>null</code> if a time otp entry with the primary key could not be found
	 */
	public static TimeOTPEntry fetchByPrimaryKey(long entryId) {
		return getPersistence().fetchByPrimaryKey(entryId);
	}

	/**
	 * Returns all the time otp entries.
	 *
	 * @return the time otp entries
	 */
	public static List<TimeOTPEntry> findAll() {
		return getPersistence().findAll();
	}

	/**
	 * Returns a range of all the time otp entries.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent and pagination is required (<code>start</code> and <code>end</code> are not <code>QueryUtil#ALL_POS</code>), then the query will include the default ORDER BY logic from <code>TimeOTPEntryModelImpl</code>. If both <code>orderByComparator</code> and pagination are absent, for performance reasons, the query will not have an ORDER BY clause and the returned result set will be sorted on by the primary key in an ascending order.
	 * </p>
	 *
	 * @param start the lower bound of the range of time otp entries
	 * @param end the upper bound of the range of time otp entries (not inclusive)
	 * @return the range of time otp entries
	 */
	public static List<TimeOTPEntry> findAll(int start, int end) {
		return getPersistence().findAll(start, end);
	}

	/**
	 * Returns an ordered range of all the time otp entries.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent and pagination is required (<code>start</code> and <code>end</code> are not <code>QueryUtil#ALL_POS</code>), then the query will include the default ORDER BY logic from <code>TimeOTPEntryModelImpl</code>. If both <code>orderByComparator</code> and pagination are absent, for performance reasons, the query will not have an ORDER BY clause and the returned result set will be sorted on by the primary key in an ascending order.
	 * </p>
	 *
	 * @param start the lower bound of the range of time otp entries
	 * @param end the upper bound of the range of time otp entries (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of time otp entries
	 */
	public static List<TimeOTPEntry> findAll(
		int start, int end, OrderByComparator<TimeOTPEntry> orderByComparator) {

		return getPersistence().findAll(start, end, orderByComparator);
	}

	/**
	 * Returns an ordered range of all the time otp entries.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent and pagination is required (<code>start</code> and <code>end</code> are not <code>QueryUtil#ALL_POS</code>), then the query will include the default ORDER BY logic from <code>TimeOTPEntryModelImpl</code>. If both <code>orderByComparator</code> and pagination are absent, for performance reasons, the query will not have an ORDER BY clause and the returned result set will be sorted on by the primary key in an ascending order.
	 * </p>
	 *
	 * @param start the lower bound of the range of time otp entries
	 * @param end the upper bound of the range of time otp entries (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param retrieveFromCache whether to retrieve from the finder cache
	 * @return the ordered range of time otp entries
	 */
	public static List<TimeOTPEntry> findAll(
		int start, int end, OrderByComparator<TimeOTPEntry> orderByComparator,
		boolean retrieveFromCache) {

		return getPersistence().findAll(
			start, end, orderByComparator, retrieveFromCache);
	}

	/**
	 * Removes all the time otp entries from the database.
	 */
	public static void removeAll() {
		getPersistence().removeAll();
	}

	/**
	 * Returns the number of time otp entries.
	 *
	 * @return the number of time otp entries
	 */
	public static int countAll() {
		return getPersistence().countAll();
	}

	public static TimeOTPEntryPersistence getPersistence() {
		return _serviceTracker.getService();
	}

	private static ServiceTracker
		<TimeOTPEntryPersistence, TimeOTPEntryPersistence> _serviceTracker;

	static {
		Bundle bundle = FrameworkUtil.getBundle(TimeOTPEntryPersistence.class);

		ServiceTracker<TimeOTPEntryPersistence, TimeOTPEntryPersistence>
			serviceTracker =
				new ServiceTracker
					<TimeOTPEntryPersistence, TimeOTPEntryPersistence>(
						bundle.getBundleContext(),
						TimeOTPEntryPersistence.class, null);

		serviceTracker.open();

		_serviceTracker = serviceTracker;
	}

}