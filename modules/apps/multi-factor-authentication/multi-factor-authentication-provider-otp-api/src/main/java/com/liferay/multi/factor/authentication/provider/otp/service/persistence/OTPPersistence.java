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

import com.liferay.multi.factor.authentication.provider.otp.exception.NoSuchOTPException;
import com.liferay.multi.factor.authentication.provider.otp.model.OTP;

import com.liferay.portal.kernel.service.persistence.BasePersistence;

import java.io.Serializable;

import java.util.Map;
import java.util.Set;

/**
 * The persistence interface for the otp service.
 *
 * <p>
 * Caching information and settings can be found in <code>portal.properties</code>
 * </p>
 *
 * @author arthurchan35
 * @see com.liferay.multi.factor.authentication.provider.otp.service.persistence.impl.OTPPersistenceImpl
 * @see OTPUtil
 * @generated
 */
@ProviderType
public interface OTPPersistence extends BasePersistence<OTP> {
	/*
	 * NOTE FOR DEVELOPERS:
	 *
	 * Never modify or reference this interface directly. Always use {@link OTPUtil} to access the otp persistence. Modify <code>service.xml</code> and rerun ServiceBuilder to regenerate this interface.
	 */
	@Override
	public Map<Serializable, OTP> fetchByPrimaryKeys(
		Set<Serializable> primaryKeys);

	/**
	* Returns the otp where userId = &#63; or throws a {@link NoSuchOTPException} if it could not be found.
	*
	* @param userId the user ID
	* @return the matching otp
	* @throws NoSuchOTPException if a matching otp could not be found
	*/
	public OTP findByUserId(long userId) throws NoSuchOTPException;

	/**
	* Returns the otp where userId = &#63; or returns <code>null</code> if it could not be found. Uses the finder cache.
	*
	* @param userId the user ID
	* @return the matching otp, or <code>null</code> if a matching otp could not be found
	*/
	public OTP fetchByUserId(long userId);

	/**
	* Returns the otp where userId = &#63; or returns <code>null</code> if it could not be found, optionally using the finder cache.
	*
	* @param userId the user ID
	* @param retrieveFromCache whether to retrieve from the finder cache
	* @return the matching otp, or <code>null</code> if a matching otp could not be found
	*/
	public OTP fetchByUserId(long userId, boolean retrieveFromCache);

	/**
	* Removes the otp where userId = &#63; from the database.
	*
	* @param userId the user ID
	* @return the otp that was removed
	*/
	public OTP removeByUserId(long userId) throws NoSuchOTPException;

	/**
	* Returns the number of otps where userId = &#63;.
	*
	* @param userId the user ID
	* @return the number of matching otps
	*/
	public int countByUserId(long userId);

	/**
	* Caches the otp in the entity cache if it is enabled.
	*
	* @param otp the otp
	*/
	public void cacheResult(OTP otp);

	/**
	* Caches the otps in the entity cache if it is enabled.
	*
	* @param otps the otps
	*/
	public void cacheResult(java.util.List<OTP> otps);

	/**
	* Creates a new otp with the primary key. Does not add the otp to the database.
	*
	* @param otpId the primary key for the new otp
	* @return the new otp
	*/
	public OTP create(long otpId);

	/**
	* Removes the otp with the primary key from the database. Also notifies the appropriate model listeners.
	*
	* @param otpId the primary key of the otp
	* @return the otp that was removed
	* @throws NoSuchOTPException if a otp with the primary key could not be found
	*/
	public OTP remove(long otpId) throws NoSuchOTPException;

	public OTP updateImpl(OTP otp);

	/**
	* Returns the otp with the primary key or throws a {@link NoSuchOTPException} if it could not be found.
	*
	* @param otpId the primary key of the otp
	* @return the otp
	* @throws NoSuchOTPException if a otp with the primary key could not be found
	*/
	public OTP findByPrimaryKey(long otpId) throws NoSuchOTPException;

	/**
	* Returns the otp with the primary key or returns <code>null</code> if it could not be found.
	*
	* @param otpId the primary key of the otp
	* @return the otp, or <code>null</code> if a otp with the primary key could not be found
	*/
	public OTP fetchByPrimaryKey(long otpId);

	/**
	* Returns all the otps.
	*
	* @return the otps
	*/
	public java.util.List<OTP> findAll();

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
	public java.util.List<OTP> findAll(int start, int end);

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
	public java.util.List<OTP> findAll(int start, int end,
		com.liferay.portal.kernel.util.OrderByComparator<OTP> orderByComparator);

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
	public java.util.List<OTP> findAll(int start, int end,
		com.liferay.portal.kernel.util.OrderByComparator<OTP> orderByComparator,
		boolean retrieveFromCache);

	/**
	* Removes all the otps from the database.
	*/
	public void removeAll();

	/**
	* Returns the number of otps.
	*
	* @return the number of otps
	*/
	public int countAll();
}