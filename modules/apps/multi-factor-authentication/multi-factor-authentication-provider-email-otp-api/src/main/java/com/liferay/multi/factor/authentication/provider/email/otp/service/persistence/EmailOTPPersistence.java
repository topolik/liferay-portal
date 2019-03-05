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

import com.liferay.multi.factor.authentication.provider.email.otp.exception.NoSuchEmailOTPException;
import com.liferay.multi.factor.authentication.provider.email.otp.model.EmailOTP;
import com.liferay.portal.kernel.service.persistence.BasePersistence;

/**
 * The persistence interface for the email otp service.
 *
 * <p>
 * Caching information and settings can be found in <code>portal.properties</code>
 * </p>
 *
 * @author arthurchan35
 * @see EmailOTPUtil
 * @generated
 */
@ProviderType
public interface EmailOTPPersistence extends BasePersistence<EmailOTP> {

	/*
	 * NOTE FOR DEVELOPERS:
	 *
	 * Never modify or reference this interface directly. Always use {@link EmailOTPUtil} to access the email otp persistence. Modify <code>service.xml</code> and rerun ServiceBuilder to regenerate this interface.
	 */

	/**
	 * Returns the email otp where userId = &#63; or throws a <code>NoSuchEmailOTPException</code> if it could not be found.
	 *
	 * @param userId the user ID
	 * @return the matching email otp
	 * @throws NoSuchEmailOTPException if a matching email otp could not be found
	 */
	public EmailOTP findByUserId(long userId) throws NoSuchEmailOTPException;

	/**
	 * Returns the email otp where userId = &#63; or returns <code>null</code> if it could not be found. Uses the finder cache.
	 *
	 * @param userId the user ID
	 * @return the matching email otp, or <code>null</code> if a matching email otp could not be found
	 */
	public EmailOTP fetchByUserId(long userId);

	/**
	 * Returns the email otp where userId = &#63; or returns <code>null</code> if it could not be found, optionally using the finder cache.
	 *
	 * @param userId the user ID
	 * @param retrieveFromCache whether to retrieve from the finder cache
	 * @return the matching email otp, or <code>null</code> if a matching email otp could not be found
	 */
	public EmailOTP fetchByUserId(long userId, boolean retrieveFromCache);

	/**
	 * Removes the email otp where userId = &#63; from the database.
	 *
	 * @param userId the user ID
	 * @return the email otp that was removed
	 */
	public EmailOTP removeByUserId(long userId) throws NoSuchEmailOTPException;

	/**
	 * Returns the number of email otps where userId = &#63;.
	 *
	 * @param userId the user ID
	 * @return the number of matching email otps
	 */
	public int countByUserId(long userId);

	/**
	 * Caches the email otp in the entity cache if it is enabled.
	 *
	 * @param emailOTP the email otp
	 */
	public void cacheResult(EmailOTP emailOTP);

	/**
	 * Caches the email otps in the entity cache if it is enabled.
	 *
	 * @param emailOTPs the email otps
	 */
	public void cacheResult(java.util.List<EmailOTP> emailOTPs);

	/**
	 * Creates a new email otp with the primary key. Does not add the email otp to the database.
	 *
	 * @param emailOTPId the primary key for the new email otp
	 * @return the new email otp
	 */
	public EmailOTP create(long emailOTPId);

	/**
	 * Removes the email otp with the primary key from the database. Also notifies the appropriate model listeners.
	 *
	 * @param emailOTPId the primary key of the email otp
	 * @return the email otp that was removed
	 * @throws NoSuchEmailOTPException if a email otp with the primary key could not be found
	 */
	public EmailOTP remove(long emailOTPId) throws NoSuchEmailOTPException;

	public EmailOTP updateImpl(EmailOTP emailOTP);

	/**
	 * Returns the email otp with the primary key or throws a <code>NoSuchEmailOTPException</code> if it could not be found.
	 *
	 * @param emailOTPId the primary key of the email otp
	 * @return the email otp
	 * @throws NoSuchEmailOTPException if a email otp with the primary key could not be found
	 */
	public EmailOTP findByPrimaryKey(long emailOTPId)
		throws NoSuchEmailOTPException;

	/**
	 * Returns the email otp with the primary key or returns <code>null</code> if it could not be found.
	 *
	 * @param emailOTPId the primary key of the email otp
	 * @return the email otp, or <code>null</code> if a email otp with the primary key could not be found
	 */
	public EmailOTP fetchByPrimaryKey(long emailOTPId);

	/**
	 * Returns all the email otps.
	 *
	 * @return the email otps
	 */
	public java.util.List<EmailOTP> findAll();

	/**
	 * Returns a range of all the email otps.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent and pagination is required (<code>start</code> and <code>end</code> are not <code>QueryUtil#ALL_POS</code>), then the query will include the default ORDER BY logic from <code>EmailOTPModelImpl</code>. If both <code>orderByComparator</code> and pagination are absent, for performance reasons, the query will not have an ORDER BY clause and the returned result set will be sorted on by the primary key in an ascending order.
	 * </p>
	 *
	 * @param start the lower bound of the range of email otps
	 * @param end the upper bound of the range of email otps (not inclusive)
	 * @return the range of email otps
	 */
	public java.util.List<EmailOTP> findAll(int start, int end);

	/**
	 * Returns an ordered range of all the email otps.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent and pagination is required (<code>start</code> and <code>end</code> are not <code>QueryUtil#ALL_POS</code>), then the query will include the default ORDER BY logic from <code>EmailOTPModelImpl</code>. If both <code>orderByComparator</code> and pagination are absent, for performance reasons, the query will not have an ORDER BY clause and the returned result set will be sorted on by the primary key in an ascending order.
	 * </p>
	 *
	 * @param start the lower bound of the range of email otps
	 * @param end the upper bound of the range of email otps (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of email otps
	 */
	public java.util.List<EmailOTP> findAll(
		int start, int end,
		com.liferay.portal.kernel.util.OrderByComparator<EmailOTP>
			orderByComparator);

	/**
	 * Returns an ordered range of all the email otps.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent and pagination is required (<code>start</code> and <code>end</code> are not <code>QueryUtil#ALL_POS</code>), then the query will include the default ORDER BY logic from <code>EmailOTPModelImpl</code>. If both <code>orderByComparator</code> and pagination are absent, for performance reasons, the query will not have an ORDER BY clause and the returned result set will be sorted on by the primary key in an ascending order.
	 * </p>
	 *
	 * @param start the lower bound of the range of email otps
	 * @param end the upper bound of the range of email otps (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param retrieveFromCache whether to retrieve from the finder cache
	 * @return the ordered range of email otps
	 */
	public java.util.List<EmailOTP> findAll(
		int start, int end,
		com.liferay.portal.kernel.util.OrderByComparator<EmailOTP>
			orderByComparator,
		boolean retrieveFromCache);

	/**
	 * Removes all the email otps from the database.
	 */
	public void removeAll();

	/**
	 * Returns the number of email otps.
	 *
	 * @return the number of email otps
	 */
	public int countAll();

}