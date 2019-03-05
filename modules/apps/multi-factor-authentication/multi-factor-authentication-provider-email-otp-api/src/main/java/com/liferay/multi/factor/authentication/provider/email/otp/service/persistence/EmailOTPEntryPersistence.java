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

import com.liferay.multi.factor.authentication.provider.email.otp.exception.NoSuchEntryException;
import com.liferay.multi.factor.authentication.provider.email.otp.model.EmailOTPEntry;
import com.liferay.portal.kernel.service.persistence.BasePersistence;

/**
 * The persistence interface for the email otp entry service.
 *
 * <p>
 * Caching information and settings can be found in <code>portal.properties</code>
 * </p>
 *
 * @author arthurchan35
 * @see EmailOTPEntryUtil
 * @generated
 */
@ProviderType
public interface EmailOTPEntryPersistence
	extends BasePersistence<EmailOTPEntry> {

	/*
	 * NOTE FOR DEVELOPERS:
	 *
	 * Never modify or reference this interface directly. Always use {@link EmailOTPEntryUtil} to access the email otp entry persistence. Modify <code>service.xml</code> and rerun ServiceBuilder to regenerate this interface.
	 */

	/**
	 * Returns the email otp entry where userId = &#63; or throws a <code>NoSuchEntryException</code> if it could not be found.
	 *
	 * @param userId the user ID
	 * @return the matching email otp entry
	 * @throws NoSuchEntryException if a matching email otp entry could not be found
	 */
	public EmailOTPEntry findByUserId(long userId) throws NoSuchEntryException;

	/**
	 * Returns the email otp entry where userId = &#63; or returns <code>null</code> if it could not be found. Uses the finder cache.
	 *
	 * @param userId the user ID
	 * @return the matching email otp entry, or <code>null</code> if a matching email otp entry could not be found
	 */
	public EmailOTPEntry fetchByUserId(long userId);

	/**
	 * Returns the email otp entry where userId = &#63; or returns <code>null</code> if it could not be found, optionally using the finder cache.
	 *
	 * @param userId the user ID
	 * @param retrieveFromCache whether to retrieve from the finder cache
	 * @return the matching email otp entry, or <code>null</code> if a matching email otp entry could not be found
	 */
	public EmailOTPEntry fetchByUserId(long userId, boolean retrieveFromCache);

	/**
	 * Removes the email otp entry where userId = &#63; from the database.
	 *
	 * @param userId the user ID
	 * @return the email otp entry that was removed
	 */
	public EmailOTPEntry removeByUserId(long userId)
		throws NoSuchEntryException;

	/**
	 * Returns the number of email otp entries where userId = &#63;.
	 *
	 * @param userId the user ID
	 * @return the number of matching email otp entries
	 */
	public int countByUserId(long userId);

	/**
	 * Caches the email otp entry in the entity cache if it is enabled.
	 *
	 * @param emailOTPEntry the email otp entry
	 */
	public void cacheResult(EmailOTPEntry emailOTPEntry);

	/**
	 * Caches the email otp entries in the entity cache if it is enabled.
	 *
	 * @param emailOTPEntries the email otp entries
	 */
	public void cacheResult(java.util.List<EmailOTPEntry> emailOTPEntries);

	/**
	 * Creates a new email otp entry with the primary key. Does not add the email otp entry to the database.
	 *
	 * @param entryId the primary key for the new email otp entry
	 * @return the new email otp entry
	 */
	public EmailOTPEntry create(long entryId);

	/**
	 * Removes the email otp entry with the primary key from the database. Also notifies the appropriate model listeners.
	 *
	 * @param entryId the primary key of the email otp entry
	 * @return the email otp entry that was removed
	 * @throws NoSuchEntryException if a email otp entry with the primary key could not be found
	 */
	public EmailOTPEntry remove(long entryId) throws NoSuchEntryException;

	public EmailOTPEntry updateImpl(EmailOTPEntry emailOTPEntry);

	/**
	 * Returns the email otp entry with the primary key or throws a <code>NoSuchEntryException</code> if it could not be found.
	 *
	 * @param entryId the primary key of the email otp entry
	 * @return the email otp entry
	 * @throws NoSuchEntryException if a email otp entry with the primary key could not be found
	 */
	public EmailOTPEntry findByPrimaryKey(long entryId)
		throws NoSuchEntryException;

	/**
	 * Returns the email otp entry with the primary key or returns <code>null</code> if it could not be found.
	 *
	 * @param entryId the primary key of the email otp entry
	 * @return the email otp entry, or <code>null</code> if a email otp entry with the primary key could not be found
	 */
	public EmailOTPEntry fetchByPrimaryKey(long entryId);

	/**
	 * Returns all the email otp entries.
	 *
	 * @return the email otp entries
	 */
	public java.util.List<EmailOTPEntry> findAll();

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
	public java.util.List<EmailOTPEntry> findAll(int start, int end);

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
	public java.util.List<EmailOTPEntry> findAll(
		int start, int end,
		com.liferay.portal.kernel.util.OrderByComparator<EmailOTPEntry>
			orderByComparator);

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
	public java.util.List<EmailOTPEntry> findAll(
		int start, int end,
		com.liferay.portal.kernel.util.OrderByComparator<EmailOTPEntry>
			orderByComparator,
		boolean retrieveFromCache);

	/**
	 * Removes all the email otp entries from the database.
	 */
	public void removeAll();

	/**
	 * Returns the number of email otp entries.
	 *
	 * @return the number of email otp entries
	 */
	public int countAll();

}