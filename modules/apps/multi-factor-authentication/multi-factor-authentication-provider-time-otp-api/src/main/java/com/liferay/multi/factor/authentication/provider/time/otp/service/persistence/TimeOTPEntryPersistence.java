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

import com.liferay.multi.factor.authentication.provider.time.otp.exception.NoSuchEntryException;
import com.liferay.multi.factor.authentication.provider.time.otp.model.TimeOTPEntry;
import com.liferay.portal.kernel.service.persistence.BasePersistence;

/**
 * The persistence interface for the time otp entry service.
 *
 * <p>
 * Caching information and settings can be found in <code>portal.properties</code>
 * </p>
 *
 * @author arthurchan35
 * @see TimeOTPEntryUtil
 * @generated
 */
@ProviderType
public interface TimeOTPEntryPersistence extends BasePersistence<TimeOTPEntry> {

	/*
	 * NOTE FOR DEVELOPERS:
	 *
	 * Never modify or reference this interface directly. Always use {@link TimeOTPEntryUtil} to access the time otp entry persistence. Modify <code>service.xml</code> and rerun ServiceBuilder to regenerate this interface.
	 */

	/**
	 * Returns the time otp entry where userId = &#63; or throws a <code>NoSuchEntryException</code> if it could not be found.
	 *
	 * @param userId the user ID
	 * @return the matching time otp entry
	 * @throws NoSuchEntryException if a matching time otp entry could not be found
	 */
	public TimeOTPEntry findByUserId(long userId) throws NoSuchEntryException;

	/**
	 * Returns the time otp entry where userId = &#63; or returns <code>null</code> if it could not be found. Uses the finder cache.
	 *
	 * @param userId the user ID
	 * @return the matching time otp entry, or <code>null</code> if a matching time otp entry could not be found
	 */
	public TimeOTPEntry fetchByUserId(long userId);

	/**
	 * Returns the time otp entry where userId = &#63; or returns <code>null</code> if it could not be found, optionally using the finder cache.
	 *
	 * @param userId the user ID
	 * @param retrieveFromCache whether to retrieve from the finder cache
	 * @return the matching time otp entry, or <code>null</code> if a matching time otp entry could not be found
	 */
	public TimeOTPEntry fetchByUserId(long userId, boolean retrieveFromCache);

	/**
	 * Removes the time otp entry where userId = &#63; from the database.
	 *
	 * @param userId the user ID
	 * @return the time otp entry that was removed
	 */
	public TimeOTPEntry removeByUserId(long userId) throws NoSuchEntryException;

	/**
	 * Returns the number of time otp entries where userId = &#63;.
	 *
	 * @param userId the user ID
	 * @return the number of matching time otp entries
	 */
	public int countByUserId(long userId);

	/**
	 * Caches the time otp entry in the entity cache if it is enabled.
	 *
	 * @param timeOTPEntry the time otp entry
	 */
	public void cacheResult(TimeOTPEntry timeOTPEntry);

	/**
	 * Caches the time otp entries in the entity cache if it is enabled.
	 *
	 * @param timeOTPEntries the time otp entries
	 */
	public void cacheResult(java.util.List<TimeOTPEntry> timeOTPEntries);

	/**
	 * Creates a new time otp entry with the primary key. Does not add the time otp entry to the database.
	 *
	 * @param entryId the primary key for the new time otp entry
	 * @return the new time otp entry
	 */
	public TimeOTPEntry create(long entryId);

	/**
	 * Removes the time otp entry with the primary key from the database. Also notifies the appropriate model listeners.
	 *
	 * @param entryId the primary key of the time otp entry
	 * @return the time otp entry that was removed
	 * @throws NoSuchEntryException if a time otp entry with the primary key could not be found
	 */
	public TimeOTPEntry remove(long entryId) throws NoSuchEntryException;

	public TimeOTPEntry updateImpl(TimeOTPEntry timeOTPEntry);

	/**
	 * Returns the time otp entry with the primary key or throws a <code>NoSuchEntryException</code> if it could not be found.
	 *
	 * @param entryId the primary key of the time otp entry
	 * @return the time otp entry
	 * @throws NoSuchEntryException if a time otp entry with the primary key could not be found
	 */
	public TimeOTPEntry findByPrimaryKey(long entryId)
		throws NoSuchEntryException;

	/**
	 * Returns the time otp entry with the primary key or returns <code>null</code> if it could not be found.
	 *
	 * @param entryId the primary key of the time otp entry
	 * @return the time otp entry, or <code>null</code> if a time otp entry with the primary key could not be found
	 */
	public TimeOTPEntry fetchByPrimaryKey(long entryId);

	/**
	 * Returns all the time otp entries.
	 *
	 * @return the time otp entries
	 */
	public java.util.List<TimeOTPEntry> findAll();

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
	public java.util.List<TimeOTPEntry> findAll(int start, int end);

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
	public java.util.List<TimeOTPEntry> findAll(
		int start, int end,
		com.liferay.portal.kernel.util.OrderByComparator<TimeOTPEntry>
			orderByComparator);

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
	public java.util.List<TimeOTPEntry> findAll(
		int start, int end,
		com.liferay.portal.kernel.util.OrderByComparator<TimeOTPEntry>
			orderByComparator,
		boolean retrieveFromCache);

	/**
	 * Removes all the time otp entries from the database.
	 */
	public void removeAll();

	/**
	 * Returns the number of time otp entries.
	 *
	 * @return the number of time otp entries
	 */
	public int countAll();

}