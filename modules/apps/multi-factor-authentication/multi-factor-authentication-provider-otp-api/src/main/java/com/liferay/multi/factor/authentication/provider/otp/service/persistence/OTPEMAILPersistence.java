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

import com.liferay.multi.factor.authentication.provider.otp.exception.NoSuchEMAILException;
import com.liferay.multi.factor.authentication.provider.otp.model.OTPEMAIL;

import com.liferay.portal.kernel.service.persistence.BasePersistence;

import java.io.Serializable;

import java.util.Map;
import java.util.Set;

/**
 * The persistence interface for the otpemail service.
 *
 * <p>
 * Caching information and settings can be found in <code>portal.properties</code>
 * </p>
 *
 * @author arthurchan35
 * @see com.liferay.multi.factor.authentication.provider.otp.service.persistence.impl.OTPEMAILPersistenceImpl
 * @see OTPEMAILUtil
 * @generated
 */
@ProviderType
public interface OTPEMAILPersistence extends BasePersistence<OTPEMAIL> {
	/*
	 * NOTE FOR DEVELOPERS:
	 *
	 * Never modify or reference this interface directly. Always use {@link OTPEMAILUtil} to access the otpemail persistence. Modify <code>service.xml</code> and rerun ServiceBuilder to regenerate this interface.
	 */
	@Override
	public Map<Serializable, OTPEMAIL> fetchByPrimaryKeys(
		Set<Serializable> primaryKeys);

	/**
	* Returns the otpemail where userId = &#63; or throws a {@link NoSuchEMAILException} if it could not be found.
	*
	* @param userId the user ID
	* @return the matching otpemail
	* @throws NoSuchEMAILException if a matching otpemail could not be found
	*/
	public OTPEMAIL findByUserId(long userId) throws NoSuchEMAILException;

	/**
	* Returns the otpemail where userId = &#63; or returns <code>null</code> if it could not be found. Uses the finder cache.
	*
	* @param userId the user ID
	* @return the matching otpemail, or <code>null</code> if a matching otpemail could not be found
	*/
	public OTPEMAIL fetchByUserId(long userId);

	/**
	* Returns the otpemail where userId = &#63; or returns <code>null</code> if it could not be found, optionally using the finder cache.
	*
	* @param userId the user ID
	* @param retrieveFromCache whether to retrieve from the finder cache
	* @return the matching otpemail, or <code>null</code> if a matching otpemail could not be found
	*/
	public OTPEMAIL fetchByUserId(long userId, boolean retrieveFromCache);

	/**
	* Removes the otpemail where userId = &#63; from the database.
	*
	* @param userId the user ID
	* @return the otpemail that was removed
	*/
	public OTPEMAIL removeByUserId(long userId) throws NoSuchEMAILException;

	/**
	* Returns the number of otpemails where userId = &#63;.
	*
	* @param userId the user ID
	* @return the number of matching otpemails
	*/
	public int countByUserId(long userId);

	/**
	* Caches the otpemail in the entity cache if it is enabled.
	*
	* @param otpemail the otpemail
	*/
	public void cacheResult(OTPEMAIL otpemail);

	/**
	* Caches the otpemails in the entity cache if it is enabled.
	*
	* @param otpemails the otpemails
	*/
	public void cacheResult(java.util.List<OTPEMAIL> otpemails);

	/**
	* Creates a new otpemail with the primary key. Does not add the otpemail to the database.
	*
	* @param otpEmailId the primary key for the new otpemail
	* @return the new otpemail
	*/
	public OTPEMAIL create(long otpEmailId);

	/**
	* Removes the otpemail with the primary key from the database. Also notifies the appropriate model listeners.
	*
	* @param otpEmailId the primary key of the otpemail
	* @return the otpemail that was removed
	* @throws NoSuchEMAILException if a otpemail with the primary key could not be found
	*/
	public OTPEMAIL remove(long otpEmailId) throws NoSuchEMAILException;

	public OTPEMAIL updateImpl(OTPEMAIL otpemail);

	/**
	* Returns the otpemail with the primary key or throws a {@link NoSuchEMAILException} if it could not be found.
	*
	* @param otpEmailId the primary key of the otpemail
	* @return the otpemail
	* @throws NoSuchEMAILException if a otpemail with the primary key could not be found
	*/
	public OTPEMAIL findByPrimaryKey(long otpEmailId)
		throws NoSuchEMAILException;

	/**
	* Returns the otpemail with the primary key or returns <code>null</code> if it could not be found.
	*
	* @param otpEmailId the primary key of the otpemail
	* @return the otpemail, or <code>null</code> if a otpemail with the primary key could not be found
	*/
	public OTPEMAIL fetchByPrimaryKey(long otpEmailId);

	/**
	* Returns all the otpemails.
	*
	* @return the otpemails
	*/
	public java.util.List<OTPEMAIL> findAll();

	/**
	* Returns a range of all the otpemails.
	*
	* <p>
	* Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to {@link QueryUtil#ALL_POS} will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent and pagination is required (<code>start</code> and <code>end</code> are not {@link QueryUtil#ALL_POS}), then the query will include the default ORDER BY logic from {@link OTPEMAILModelImpl}. If both <code>orderByComparator</code> and pagination are absent, for performance reasons, the query will not have an ORDER BY clause and the returned result set will be sorted on by the primary key in an ascending order.
	* </p>
	*
	* @param start the lower bound of the range of otpemails
	* @param end the upper bound of the range of otpemails (not inclusive)
	* @return the range of otpemails
	*/
	public java.util.List<OTPEMAIL> findAll(int start, int end);

	/**
	* Returns an ordered range of all the otpemails.
	*
	* <p>
	* Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to {@link QueryUtil#ALL_POS} will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent and pagination is required (<code>start</code> and <code>end</code> are not {@link QueryUtil#ALL_POS}), then the query will include the default ORDER BY logic from {@link OTPEMAILModelImpl}. If both <code>orderByComparator</code> and pagination are absent, for performance reasons, the query will not have an ORDER BY clause and the returned result set will be sorted on by the primary key in an ascending order.
	* </p>
	*
	* @param start the lower bound of the range of otpemails
	* @param end the upper bound of the range of otpemails (not inclusive)
	* @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	* @return the ordered range of otpemails
	*/
	public java.util.List<OTPEMAIL> findAll(int start, int end,
		com.liferay.portal.kernel.util.OrderByComparator<OTPEMAIL> orderByComparator);

	/**
	* Returns an ordered range of all the otpemails.
	*
	* <p>
	* Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to {@link QueryUtil#ALL_POS} will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent and pagination is required (<code>start</code> and <code>end</code> are not {@link QueryUtil#ALL_POS}), then the query will include the default ORDER BY logic from {@link OTPEMAILModelImpl}. If both <code>orderByComparator</code> and pagination are absent, for performance reasons, the query will not have an ORDER BY clause and the returned result set will be sorted on by the primary key in an ascending order.
	* </p>
	*
	* @param start the lower bound of the range of otpemails
	* @param end the upper bound of the range of otpemails (not inclusive)
	* @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	* @param retrieveFromCache whether to retrieve from the finder cache
	* @return the ordered range of otpemails
	*/
	public java.util.List<OTPEMAIL> findAll(int start, int end,
		com.liferay.portal.kernel.util.OrderByComparator<OTPEMAIL> orderByComparator,
		boolean retrieveFromCache);

	/**
	* Removes all the otpemails from the database.
	*/
	public void removeAll();

	/**
	* Returns the number of otpemails.
	*
	* @return the number of otpemails
	*/
	public int countAll();
}