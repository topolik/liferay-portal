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

package com.liferay.multi.factor.authentication.provider.totp.service.persistence;

import aQute.bnd.annotation.ProviderType;

import com.liferay.multi.factor.authentication.provider.totp.exception.NoSuchTOTPException;
import com.liferay.multi.factor.authentication.provider.totp.model.TOTP;

import com.liferay.portal.kernel.service.persistence.BasePersistence;

import java.io.Serializable;

import java.util.Map;
import java.util.Set;

/**
 * The persistence interface for the totp service.
 *
 * <p>
 * Caching information and settings can be found in <code>portal.properties</code>
 * </p>
 *
 * @author arthurchan35
 * @see com.liferay.multi.factor.authentication.provider.totp.service.persistence.impl.TOTPPersistenceImpl
 * @see TOTPUtil
 * @generated
 */
@ProviderType
public interface TOTPPersistence extends BasePersistence<TOTP> {
	/*
	 * NOTE FOR DEVELOPERS:
	 *
	 * Never modify or reference this interface directly. Always use {@link TOTPUtil} to access the totp persistence. Modify <code>service.xml</code> and rerun ServiceBuilder to regenerate this interface.
	 */
	@Override
	public Map<Serializable, TOTP> fetchByPrimaryKeys(
		Set<Serializable> primaryKeys);

	/**
	* Returns the totp where userId = &#63; or throws a {@link NoSuchTOTPException} if it could not be found.
	*
	* @param userId the user ID
	* @return the matching totp
	* @throws NoSuchTOTPException if a matching totp could not be found
	*/
	public TOTP findByUserId(long userId) throws NoSuchTOTPException;

	/**
	* Returns the totp where userId = &#63; or returns <code>null</code> if it could not be found. Uses the finder cache.
	*
	* @param userId the user ID
	* @return the matching totp, or <code>null</code> if a matching totp could not be found
	*/
	public TOTP fetchByUserId(long userId);

	/**
	* Returns the totp where userId = &#63; or returns <code>null</code> if it could not be found, optionally using the finder cache.
	*
	* @param userId the user ID
	* @param retrieveFromCache whether to retrieve from the finder cache
	* @return the matching totp, or <code>null</code> if a matching totp could not be found
	*/
	public TOTP fetchByUserId(long userId, boolean retrieveFromCache);

	/**
	* Removes the totp where userId = &#63; from the database.
	*
	* @param userId the user ID
	* @return the totp that was removed
	*/
	public TOTP removeByUserId(long userId) throws NoSuchTOTPException;

	/**
	* Returns the number of totps where userId = &#63;.
	*
	* @param userId the user ID
	* @return the number of matching totps
	*/
	public int countByUserId(long userId);

	/**
	* Caches the totp in the entity cache if it is enabled.
	*
	* @param totp the totp
	*/
	public void cacheResult(TOTP totp);

	/**
	* Caches the totps in the entity cache if it is enabled.
	*
	* @param totps the totps
	*/
	public void cacheResult(java.util.List<TOTP> totps);

	/**
	* Creates a new totp with the primary key. Does not add the totp to the database.
	*
	* @param totpId the primary key for the new totp
	* @return the new totp
	*/
	public TOTP create(long totpId);

	/**
	* Removes the totp with the primary key from the database. Also notifies the appropriate model listeners.
	*
	* @param totpId the primary key of the totp
	* @return the totp that was removed
	* @throws NoSuchTOTPException if a totp with the primary key could not be found
	*/
	public TOTP remove(long totpId) throws NoSuchTOTPException;

	public TOTP updateImpl(TOTP totp);

	/**
	* Returns the totp with the primary key or throws a {@link NoSuchTOTPException} if it could not be found.
	*
	* @param totpId the primary key of the totp
	* @return the totp
	* @throws NoSuchTOTPException if a totp with the primary key could not be found
	*/
	public TOTP findByPrimaryKey(long totpId) throws NoSuchTOTPException;

	/**
	* Returns the totp with the primary key or returns <code>null</code> if it could not be found.
	*
	* @param totpId the primary key of the totp
	* @return the totp, or <code>null</code> if a totp with the primary key could not be found
	*/
	public TOTP fetchByPrimaryKey(long totpId);

	/**
	* Returns all the totps.
	*
	* @return the totps
	*/
	public java.util.List<TOTP> findAll();

	/**
	* Returns a range of all the totps.
	*
	* <p>
	* Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to {@link QueryUtil#ALL_POS} will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent and pagination is required (<code>start</code> and <code>end</code> are not {@link QueryUtil#ALL_POS}), then the query will include the default ORDER BY logic from {@link TOTPModelImpl}. If both <code>orderByComparator</code> and pagination are absent, for performance reasons, the query will not have an ORDER BY clause and the returned result set will be sorted on by the primary key in an ascending order.
	* </p>
	*
	* @param start the lower bound of the range of totps
	* @param end the upper bound of the range of totps (not inclusive)
	* @return the range of totps
	*/
	public java.util.List<TOTP> findAll(int start, int end);

	/**
	* Returns an ordered range of all the totps.
	*
	* <p>
	* Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to {@link QueryUtil#ALL_POS} will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent and pagination is required (<code>start</code> and <code>end</code> are not {@link QueryUtil#ALL_POS}), then the query will include the default ORDER BY logic from {@link TOTPModelImpl}. If both <code>orderByComparator</code> and pagination are absent, for performance reasons, the query will not have an ORDER BY clause and the returned result set will be sorted on by the primary key in an ascending order.
	* </p>
	*
	* @param start the lower bound of the range of totps
	* @param end the upper bound of the range of totps (not inclusive)
	* @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	* @return the ordered range of totps
	*/
	public java.util.List<TOTP> findAll(int start, int end,
		com.liferay.portal.kernel.util.OrderByComparator<TOTP> orderByComparator);

	/**
	* Returns an ordered range of all the totps.
	*
	* <p>
	* Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to {@link QueryUtil#ALL_POS} will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent and pagination is required (<code>start</code> and <code>end</code> are not {@link QueryUtil#ALL_POS}), then the query will include the default ORDER BY logic from {@link TOTPModelImpl}. If both <code>orderByComparator</code> and pagination are absent, for performance reasons, the query will not have an ORDER BY clause and the returned result set will be sorted on by the primary key in an ascending order.
	* </p>
	*
	* @param start the lower bound of the range of totps
	* @param end the upper bound of the range of totps (not inclusive)
	* @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	* @param retrieveFromCache whether to retrieve from the finder cache
	* @return the ordered range of totps
	*/
	public java.util.List<TOTP> findAll(int start, int end,
		com.liferay.portal.kernel.util.OrderByComparator<TOTP> orderByComparator,
		boolean retrieveFromCache);

	/**
	* Removes all the totps from the database.
	*/
	public void removeAll();

	/**
	* Returns the number of totps.
	*
	* @return the number of totps
	*/
	public int countAll();
}