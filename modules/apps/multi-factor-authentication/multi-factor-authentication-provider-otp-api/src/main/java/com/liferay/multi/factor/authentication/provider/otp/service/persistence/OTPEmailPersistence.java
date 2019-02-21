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

import com.liferay.multi.factor.authentication.provider.otp.exception.NoSuchEmailException;
import com.liferay.multi.factor.authentication.provider.otp.model.OTPEmail;

import com.liferay.portal.kernel.service.persistence.BasePersistence;

import java.io.Serializable;

import java.util.Map;
import java.util.Set;

/**
 * The persistence interface for the otp email service.
 *
 * <p>
 * Caching information and settings can be found in <code>portal.properties</code>
 * </p>
 *
 * @author arthurchan35
 * @see com.liferay.multi.factor.authentication.provider.otp.service.persistence.impl.OTPEmailPersistenceImpl
 * @see OTPEmailUtil
 * @generated
 */
@ProviderType
public interface OTPEmailPersistence extends BasePersistence<OTPEmail> {
	/*
	 * NOTE FOR DEVELOPERS:
	 *
	 * Never modify or reference this interface directly. Always use {@link OTPEmailUtil} to access the otp email persistence. Modify <code>service.xml</code> and rerun ServiceBuilder to regenerate this interface.
	 */
	@Override
	public Map<Serializable, OTPEmail> fetchByPrimaryKeys(
		Set<Serializable> primaryKeys);

	/**
	* Returns the otp email where userId = &#63; or throws a {@link NoSuchEmailException} if it could not be found.
	*
	* @param userId the user ID
	* @return the matching otp email
	* @throws NoSuchEmailException if a matching otp email could not be found
	*/
	public OTPEmail findByUserId(long userId) throws NoSuchEmailException;

	/**
	* Returns the otp email where userId = &#63; or returns <code>null</code> if it could not be found. Uses the finder cache.
	*
	* @param userId the user ID
	* @return the matching otp email, or <code>null</code> if a matching otp email could not be found
	*/
	public OTPEmail fetchByUserId(long userId);

	/**
	* Returns the otp email where userId = &#63; or returns <code>null</code> if it could not be found, optionally using the finder cache.
	*
	* @param userId the user ID
	* @param retrieveFromCache whether to retrieve from the finder cache
	* @return the matching otp email, or <code>null</code> if a matching otp email could not be found
	*/
	public OTPEmail fetchByUserId(long userId, boolean retrieveFromCache);

	/**
	* Removes the otp email where userId = &#63; from the database.
	*
	* @param userId the user ID
	* @return the otp email that was removed
	*/
	public OTPEmail removeByUserId(long userId) throws NoSuchEmailException;

	/**
	* Returns the number of otp emails where userId = &#63;.
	*
	* @param userId the user ID
	* @return the number of matching otp emails
	*/
	public int countByUserId(long userId);

	/**
	* Caches the otp email in the entity cache if it is enabled.
	*
	* @param otpEmail the otp email
	*/
	public void cacheResult(OTPEmail otpEmail);

	/**
	* Caches the otp emails in the entity cache if it is enabled.
	*
	* @param otpEmails the otp emails
	*/
	public void cacheResult(java.util.List<OTPEmail> otpEmails);

	/**
	* Creates a new otp email with the primary key. Does not add the otp email to the database.
	*
	* @param otpEmailId the primary key for the new otp email
	* @return the new otp email
	*/
	public OTPEmail create(long otpEmailId);

	/**
	* Removes the otp email with the primary key from the database. Also notifies the appropriate model listeners.
	*
	* @param otpEmailId the primary key of the otp email
	* @return the otp email that was removed
	* @throws NoSuchEmailException if a otp email with the primary key could not be found
	*/
	public OTPEmail remove(long otpEmailId) throws NoSuchEmailException;

	public OTPEmail updateImpl(OTPEmail otpEmail);

	/**
	* Returns the otp email with the primary key or throws a {@link NoSuchEmailException} if it could not be found.
	*
	* @param otpEmailId the primary key of the otp email
	* @return the otp email
	* @throws NoSuchEmailException if a otp email with the primary key could not be found
	*/
	public OTPEmail findByPrimaryKey(long otpEmailId)
		throws NoSuchEmailException;

	/**
	* Returns the otp email with the primary key or returns <code>null</code> if it could not be found.
	*
	* @param otpEmailId the primary key of the otp email
	* @return the otp email, or <code>null</code> if a otp email with the primary key could not be found
	*/
	public OTPEmail fetchByPrimaryKey(long otpEmailId);

	/**
	* Returns all the otp emails.
	*
	* @return the otp emails
	*/
	public java.util.List<OTPEmail> findAll();

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
	public java.util.List<OTPEmail> findAll(int start, int end);

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
	public java.util.List<OTPEmail> findAll(int start, int end,
		com.liferay.portal.kernel.util.OrderByComparator<OTPEmail> orderByComparator);

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
	public java.util.List<OTPEmail> findAll(int start, int end,
		com.liferay.portal.kernel.util.OrderByComparator<OTPEmail> orderByComparator,
		boolean retrieveFromCache);

	/**
	* Removes all the otp emails from the database.
	*/
	public void removeAll();

	/**
	* Returns the number of otp emails.
	*
	* @return the number of otp emails
	*/
	public int countAll();
}