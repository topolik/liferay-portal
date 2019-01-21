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

package com.liferay.multi.factor.authentication.otp.service.persistence;

import aQute.bnd.annotation.ProviderType;

import com.liferay.multi.factor.authentication.otp.exception.NoSuchHOTPException;
import com.liferay.multi.factor.authentication.otp.model.HOTP;

import com.liferay.portal.kernel.service.persistence.BasePersistence;

import java.io.Serializable;

import java.util.Map;
import java.util.Set;

/**
 * The persistence interface for the hotp service.
 *
 * <p>
 * Caching information and settings can be found in <code>portal.properties</code>
 * </p>
 *
 * @author arthurchan35
 * @see com.liferay.multi.factor.authentication.otp.service.persistence.impl.HOTPPersistenceImpl
 * @see HOTPUtil
 * @generated
 */
@ProviderType
public interface HOTPPersistence extends BasePersistence<HOTP> {
	/*
	 * NOTE FOR DEVELOPERS:
	 *
	 * Never modify or reference this interface directly. Always use {@link HOTPUtil} to access the hotp persistence. Modify <code>service.xml</code> and rerun ServiceBuilder to regenerate this interface.
	 */
	@Override
	public Map<Serializable, HOTP> fetchByPrimaryKeys(
		Set<Serializable> primaryKeys);

	/**
	* Returns the hotp where userId = &#63; or throws a {@link NoSuchHOTPException} if it could not be found.
	*
	* @param userId the user ID
	* @return the matching hotp
	* @throws NoSuchHOTPException if a matching hotp could not be found
	*/
	public HOTP findByUserId(long userId) throws NoSuchHOTPException;

	/**
	* Returns the hotp where userId = &#63; or returns <code>null</code> if it could not be found. Uses the finder cache.
	*
	* @param userId the user ID
	* @return the matching hotp, or <code>null</code> if a matching hotp could not be found
	*/
	public HOTP fetchByUserId(long userId);

	/**
	* Returns the hotp where userId = &#63; or returns <code>null</code> if it could not be found, optionally using the finder cache.
	*
	* @param userId the user ID
	* @param retrieveFromCache whether to retrieve from the finder cache
	* @return the matching hotp, or <code>null</code> if a matching hotp could not be found
	*/
	public HOTP fetchByUserId(long userId, boolean retrieveFromCache);

	/**
	* Removes the hotp where userId = &#63; from the database.
	*
	* @param userId the user ID
	* @return the hotp that was removed
	*/
	public HOTP removeByUserId(long userId) throws NoSuchHOTPException;

	/**
	* Returns the number of hotps where userId = &#63;.
	*
	* @param userId the user ID
	* @return the number of matching hotps
	*/
	public int countByUserId(long userId);

	/**
	* Caches the hotp in the entity cache if it is enabled.
	*
	* @param hotp the hotp
	*/
	public void cacheResult(HOTP hotp);

	/**
	* Caches the hotps in the entity cache if it is enabled.
	*
	* @param hotps the hotps
	*/
	public void cacheResult(java.util.List<HOTP> hotps);

	/**
	* Creates a new hotp with the primary key. Does not add the hotp to the database.
	*
	* @param hotpId the primary key for the new hotp
	* @return the new hotp
	*/
	public HOTP create(long hotpId);

	/**
	* Removes the hotp with the primary key from the database. Also notifies the appropriate model listeners.
	*
	* @param hotpId the primary key of the hotp
	* @return the hotp that was removed
	* @throws NoSuchHOTPException if a hotp with the primary key could not be found
	*/
	public HOTP remove(long hotpId) throws NoSuchHOTPException;

	public HOTP updateImpl(HOTP hotp);

	/**
	* Returns the hotp with the primary key or throws a {@link NoSuchHOTPException} if it could not be found.
	*
	* @param hotpId the primary key of the hotp
	* @return the hotp
	* @throws NoSuchHOTPException if a hotp with the primary key could not be found
	*/
	public HOTP findByPrimaryKey(long hotpId) throws NoSuchHOTPException;

	/**
	* Returns the hotp with the primary key or returns <code>null</code> if it could not be found.
	*
	* @param hotpId the primary key of the hotp
	* @return the hotp, or <code>null</code> if a hotp with the primary key could not be found
	*/
	public HOTP fetchByPrimaryKey(long hotpId);

	/**
	* Returns all the hotps.
	*
	* @return the hotps
	*/
	public java.util.List<HOTP> findAll();

	/**
	* Returns a range of all the hotps.
	*
	* <p>
	* Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to {@link QueryUtil#ALL_POS} will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent and pagination is required (<code>start</code> and <code>end</code> are not {@link QueryUtil#ALL_POS}), then the query will include the default ORDER BY logic from {@link HOTPModelImpl}. If both <code>orderByComparator</code> and pagination are absent, for performance reasons, the query will not have an ORDER BY clause and the returned result set will be sorted on by the primary key in an ascending order.
	* </p>
	*
	* @param start the lower bound of the range of hotps
	* @param end the upper bound of the range of hotps (not inclusive)
	* @return the range of hotps
	*/
	public java.util.List<HOTP> findAll(int start, int end);

	/**
	* Returns an ordered range of all the hotps.
	*
	* <p>
	* Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to {@link QueryUtil#ALL_POS} will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent and pagination is required (<code>start</code> and <code>end</code> are not {@link QueryUtil#ALL_POS}), then the query will include the default ORDER BY logic from {@link HOTPModelImpl}. If both <code>orderByComparator</code> and pagination are absent, for performance reasons, the query will not have an ORDER BY clause and the returned result set will be sorted on by the primary key in an ascending order.
	* </p>
	*
	* @param start the lower bound of the range of hotps
	* @param end the upper bound of the range of hotps (not inclusive)
	* @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	* @return the ordered range of hotps
	*/
	public java.util.List<HOTP> findAll(int start, int end,
		com.liferay.portal.kernel.util.OrderByComparator<HOTP> orderByComparator);

	/**
	* Returns an ordered range of all the hotps.
	*
	* <p>
	* Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to {@link QueryUtil#ALL_POS} will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent and pagination is required (<code>start</code> and <code>end</code> are not {@link QueryUtil#ALL_POS}), then the query will include the default ORDER BY logic from {@link HOTPModelImpl}. If both <code>orderByComparator</code> and pagination are absent, for performance reasons, the query will not have an ORDER BY clause and the returned result set will be sorted on by the primary key in an ascending order.
	* </p>
	*
	* @param start the lower bound of the range of hotps
	* @param end the upper bound of the range of hotps (not inclusive)
	* @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	* @param retrieveFromCache whether to retrieve from the finder cache
	* @return the ordered range of hotps
	*/
	public java.util.List<HOTP> findAll(int start, int end,
		com.liferay.portal.kernel.util.OrderByComparator<HOTP> orderByComparator,
		boolean retrieveFromCache);

	/**
	* Removes all the hotps from the database.
	*/
	public void removeAll();

	/**
	* Returns the number of hotps.
	*
	* @return the number of hotps
	*/
	public int countAll();
}