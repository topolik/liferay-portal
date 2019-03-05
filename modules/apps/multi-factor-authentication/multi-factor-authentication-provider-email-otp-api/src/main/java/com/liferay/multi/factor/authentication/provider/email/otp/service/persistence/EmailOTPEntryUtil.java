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

import com.liferay.multi.factor.authentication.provider.email.otp.model.EmailOTPEntry;
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
 * The persistence utility for the email otp entry service. This utility wraps <code>com.liferay.multi.factor.authentication.provider.email.otp.service.persistence.impl.EmailOTPEntryPersistenceImpl</code> and provides direct access to the database for CRUD operations. This utility should only be used by the service layer, as it must operate within a transaction. Never access this utility in a JSP, controller, model, or other front-end class.
 *
 * <p>
 * Caching information and settings can be found in <code>portal.properties</code>
 * </p>
 *
 * @author arthurchan35
 * @see EmailOTPEntryPersistence
 * @generated
 */
@ProviderType
public class EmailOTPEntryUtil {

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
	public static void clearCache(EmailOTPEntry emailOTPEntry) {
		getPersistence().clearCache(emailOTPEntry);
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
	public static Map<Serializable, EmailOTPEntry> fetchByPrimaryKeys(
		Set<Serializable> primaryKeys) {

		return getPersistence().fetchByPrimaryKeys(primaryKeys);
	}

	/**
	 * @see com.liferay.portal.kernel.service.persistence.BasePersistence#findWithDynamicQuery(DynamicQuery)
	 */
	public static List<EmailOTPEntry> findWithDynamicQuery(
		DynamicQuery dynamicQuery) {

		return getPersistence().findWithDynamicQuery(dynamicQuery);
	}

	/**
	 * @see com.liferay.portal.kernel.service.persistence.BasePersistence#findWithDynamicQuery(DynamicQuery, int, int)
	 */
	public static List<EmailOTPEntry> findWithDynamicQuery(
		DynamicQuery dynamicQuery, int start, int end) {

		return getPersistence().findWithDynamicQuery(dynamicQuery, start, end);
	}

	/**
	 * @see com.liferay.portal.kernel.service.persistence.BasePersistence#findWithDynamicQuery(DynamicQuery, int, int, OrderByComparator)
	 */
	public static List<EmailOTPEntry> findWithDynamicQuery(
		DynamicQuery dynamicQuery, int start, int end,
		OrderByComparator<EmailOTPEntry> orderByComparator) {

		return getPersistence().findWithDynamicQuery(
			dynamicQuery, start, end, orderByComparator);
	}

	/**
	 * @see com.liferay.portal.kernel.service.persistence.BasePersistence#update(com.liferay.portal.kernel.model.BaseModel)
	 */
	public static EmailOTPEntry update(EmailOTPEntry emailOTPEntry) {
		return getPersistence().update(emailOTPEntry);
	}

	/**
	 * @see com.liferay.portal.kernel.service.persistence.BasePersistence#update(com.liferay.portal.kernel.model.BaseModel, ServiceContext)
	 */
	public static EmailOTPEntry update(
		EmailOTPEntry emailOTPEntry, ServiceContext serviceContext) {

		return getPersistence().update(emailOTPEntry, serviceContext);
	}

	/**
	 * Returns the email otp entry where userId = &#63; or throws a <code>NoSuchEntryException</code> if it could not be found.
	 *
	 * @param userId the user ID
	 * @return the matching email otp entry
	 * @throws NoSuchEntryException if a matching email otp entry could not be found
	 */
	public static EmailOTPEntry findByUserId(long userId)
		throws com.liferay.multi.factor.authentication.provider.email.otp.
			exception.NoSuchEntryException {

		return getPersistence().findByUserId(userId);
	}

	/**
	 * Returns the email otp entry where userId = &#63; or returns <code>null</code> if it could not be found. Uses the finder cache.
	 *
	 * @param userId the user ID
	 * @return the matching email otp entry, or <code>null</code> if a matching email otp entry could not be found
	 */
	public static EmailOTPEntry fetchByUserId(long userId) {
		return getPersistence().fetchByUserId(userId);
	}

	/**
	 * Returns the email otp entry where userId = &#63; or returns <code>null</code> if it could not be found, optionally using the finder cache.
	 *
	 * @param userId the user ID
	 * @param retrieveFromCache whether to retrieve from the finder cache
	 * @return the matching email otp entry, or <code>null</code> if a matching email otp entry could not be found
	 */
	public static EmailOTPEntry fetchByUserId(
		long userId, boolean retrieveFromCache) {

		return getPersistence().fetchByUserId(userId, retrieveFromCache);
	}

	/**
	 * Removes the email otp entry where userId = &#63; from the database.
	 *
	 * @param userId the user ID
	 * @return the email otp entry that was removed
	 */
	public static EmailOTPEntry removeByUserId(long userId)
		throws com.liferay.multi.factor.authentication.provider.email.otp.
			exception.NoSuchEntryException {

		return getPersistence().removeByUserId(userId);
	}

	/**
	 * Returns the number of email otp entries where userId = &#63;.
	 *
	 * @param userId the user ID
	 * @return the number of matching email otp entries
	 */
	public static int countByUserId(long userId) {
		return getPersistence().countByUserId(userId);
	}

	/**
	 * Caches the email otp entry in the entity cache if it is enabled.
	 *
	 * @param emailOTPEntry the email otp entry
	 */
	public static void cacheResult(EmailOTPEntry emailOTPEntry) {
		getPersistence().cacheResult(emailOTPEntry);
	}

	/**
	 * Caches the email otp entries in the entity cache if it is enabled.
	 *
	 * @param emailOTPEntries the email otp entries
	 */
	public static void cacheResult(List<EmailOTPEntry> emailOTPEntries) {
		getPersistence().cacheResult(emailOTPEntries);
	}

	/**
	 * Creates a new email otp entry with the primary key. Does not add the email otp entry to the database.
	 *
	 * @param entryId the primary key for the new email otp entry
	 * @return the new email otp entry
	 */
	public static EmailOTPEntry create(long entryId) {
		return getPersistence().create(entryId);
	}

	/**
	 * Removes the email otp entry with the primary key from the database. Also notifies the appropriate model listeners.
	 *
	 * @param entryId the primary key of the email otp entry
	 * @return the email otp entry that was removed
	 * @throws NoSuchEntryException if a email otp entry with the primary key could not be found
	 */
	public static EmailOTPEntry remove(long entryId)
		throws com.liferay.multi.factor.authentication.provider.email.otp.
			exception.NoSuchEntryException {

		return getPersistence().remove(entryId);
	}

	public static EmailOTPEntry updateImpl(EmailOTPEntry emailOTPEntry) {
		return getPersistence().updateImpl(emailOTPEntry);
	}

	/**
	 * Returns the email otp entry with the primary key or throws a <code>NoSuchEntryException</code> if it could not be found.
	 *
	 * @param entryId the primary key of the email otp entry
	 * @return the email otp entry
	 * @throws NoSuchEntryException if a email otp entry with the primary key could not be found
	 */
	public static EmailOTPEntry findByPrimaryKey(long entryId)
		throws com.liferay.multi.factor.authentication.provider.email.otp.
			exception.NoSuchEntryException {

		return getPersistence().findByPrimaryKey(entryId);
	}

	/**
	 * Returns the email otp entry with the primary key or returns <code>null</code> if it could not be found.
	 *
	 * @param entryId the primary key of the email otp entry
	 * @return the email otp entry, or <code>null</code> if a email otp entry with the primary key could not be found
	 */
	public static EmailOTPEntry fetchByPrimaryKey(long entryId) {
		return getPersistence().fetchByPrimaryKey(entryId);
	}

	/**
	 * Returns all the email otp entries.
	 *
	 * @return the email otp entries
	 */
	public static List<EmailOTPEntry> findAll() {
		return getPersistence().findAll();
	}

	/**
	 * Returns a range of all the email otp entries.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent and pagination is required (<code>start</code> and <code>end</code> are not <code>QueryUtil#ALL_POS</code>), then the query will include the default ORDER BY logic from <code>EmailOTPEntryModelImpl</code>. If both <code>orderByComparator</code> and pagination are absent, for performance reasons, the query will not have an ORDER BY clause and the returned result set will be sorted on by the primary key in an ascending order.
	 * </p>
	 *
	 * @param start the lower bound of the range of email otp entries
	 * @param end the upper bound of the range of email otp entries (not inclusive)
	 * @return the range of email otp entries
	 */
	public static List<EmailOTPEntry> findAll(int start, int end) {
		return getPersistence().findAll(start, end);
	}

	/**
	 * Returns an ordered range of all the email otp entries.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent and pagination is required (<code>start</code> and <code>end</code> are not <code>QueryUtil#ALL_POS</code>), then the query will include the default ORDER BY logic from <code>EmailOTPEntryModelImpl</code>. If both <code>orderByComparator</code> and pagination are absent, for performance reasons, the query will not have an ORDER BY clause and the returned result set will be sorted on by the primary key in an ascending order.
	 * </p>
	 *
	 * @param start the lower bound of the range of email otp entries
	 * @param end the upper bound of the range of email otp entries (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of email otp entries
	 */
	public static List<EmailOTPEntry> findAll(
		int start, int end,
		OrderByComparator<EmailOTPEntry> orderByComparator) {

		return getPersistence().findAll(start, end, orderByComparator);
	}

	/**
	 * Returns an ordered range of all the email otp entries.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent and pagination is required (<code>start</code> and <code>end</code> are not <code>QueryUtil#ALL_POS</code>), then the query will include the default ORDER BY logic from <code>EmailOTPEntryModelImpl</code>. If both <code>orderByComparator</code> and pagination are absent, for performance reasons, the query will not have an ORDER BY clause and the returned result set will be sorted on by the primary key in an ascending order.
	 * </p>
	 *
	 * @param start the lower bound of the range of email otp entries
	 * @param end the upper bound of the range of email otp entries (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param retrieveFromCache whether to retrieve from the finder cache
	 * @return the ordered range of email otp entries
	 */
	public static List<EmailOTPEntry> findAll(
		int start, int end, OrderByComparator<EmailOTPEntry> orderByComparator,
		boolean retrieveFromCache) {

		return getPersistence().findAll(
			start, end, orderByComparator, retrieveFromCache);
	}

	/**
	 * Removes all the email otp entries from the database.
	 */
	public static void removeAll() {
		getPersistence().removeAll();
	}

	/**
	 * Returns the number of email otp entries.
	 *
	 * @return the number of email otp entries
	 */
	public static int countAll() {
		return getPersistence().countAll();
	}

	public static EmailOTPEntryPersistence getPersistence() {
		return _serviceTracker.getService();
	}

	private static ServiceTracker
		<EmailOTPEntryPersistence, EmailOTPEntryPersistence> _serviceTracker;

	static {
		Bundle bundle = FrameworkUtil.getBundle(EmailOTPEntryPersistence.class);

		ServiceTracker<EmailOTPEntryPersistence, EmailOTPEntryPersistence>
			serviceTracker =
				new ServiceTracker
					<EmailOTPEntryPersistence, EmailOTPEntryPersistence>(
						bundle.getBundleContext(),
						EmailOTPEntryPersistence.class, null);

		serviceTracker.open();

		_serviceTracker = serviceTracker;
	}

}