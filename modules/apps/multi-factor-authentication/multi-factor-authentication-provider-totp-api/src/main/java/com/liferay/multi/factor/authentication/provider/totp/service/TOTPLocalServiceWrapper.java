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

package com.liferay.multi.factor.authentication.provider.totp.service;

import aQute.bnd.annotation.ProviderType;

import com.liferay.portal.kernel.service.ServiceWrapper;

/**
 * Provides a wrapper for {@link TOTPLocalService}.
 *
 * @author arthurchan35
 * @see TOTPLocalService
 * @generated
 */
@ProviderType
public class TOTPLocalServiceWrapper implements TOTPLocalService,
	ServiceWrapper<TOTPLocalService> {
	public TOTPLocalServiceWrapper(TOTPLocalService totpLocalService) {
		_totpLocalService = totpLocalService;
	}

	@Override
	public com.liferay.multi.factor.authentication.provider.totp.model.TOTP addTOTP(
		long userId, String sharedSecret)
		throws com.liferay.portal.kernel.exception.PortalException {
		return _totpLocalService.addTOTP(userId, sharedSecret);
	}

	/**
	* Adds the totp to the database. Also notifies the appropriate model listeners.
	*
	* @param totp the totp
	* @return the totp that was added
	*/
	@Override
	public com.liferay.multi.factor.authentication.provider.totp.model.TOTP addTOTP(
		com.liferay.multi.factor.authentication.provider.totp.model.TOTP totp) {
		return _totpLocalService.addTOTP(totp);
	}

	/**
	* Creates a new totp with the primary key. Does not add the totp to the database.
	*
	* @param totpId the primary key for the new totp
	* @return the new totp
	*/
	@Override
	public com.liferay.multi.factor.authentication.provider.totp.model.TOTP createTOTP(
		long totpId) {
		return _totpLocalService.createTOTP(totpId);
	}

	/**
	* @throws PortalException
	*/
	@Override
	public com.liferay.portal.kernel.model.PersistedModel deletePersistedModel(
		com.liferay.portal.kernel.model.PersistedModel persistedModel)
		throws com.liferay.portal.kernel.exception.PortalException {
		return _totpLocalService.deletePersistedModel(persistedModel);
	}

	/**
	* Deletes the totp with the primary key from the database. Also notifies the appropriate model listeners.
	*
	* @param totpId the primary key of the totp
	* @return the totp that was removed
	* @throws PortalException if a totp with the primary key could not be found
	*/
	@Override
	public com.liferay.multi.factor.authentication.provider.totp.model.TOTP deleteTOTP(
		long totpId) throws com.liferay.portal.kernel.exception.PortalException {
		return _totpLocalService.deleteTOTP(totpId);
	}

	/**
	* Deletes the totp from the database. Also notifies the appropriate model listeners.
	*
	* @param totp the totp
	* @return the totp that was removed
	*/
	@Override
	public com.liferay.multi.factor.authentication.provider.totp.model.TOTP deleteTOTP(
		com.liferay.multi.factor.authentication.provider.totp.model.TOTP totp) {
		return _totpLocalService.deleteTOTP(totp);
	}

	@Override
	public com.liferay.portal.kernel.dao.orm.DynamicQuery dynamicQuery() {
		return _totpLocalService.dynamicQuery();
	}

	/**
	* Performs a dynamic query on the database and returns the matching rows.
	*
	* @param dynamicQuery the dynamic query
	* @return the matching rows
	*/
	@Override
	public <T> java.util.List<T> dynamicQuery(
		com.liferay.portal.kernel.dao.orm.DynamicQuery dynamicQuery) {
		return _totpLocalService.dynamicQuery(dynamicQuery);
	}

	/**
	* Performs a dynamic query on the database and returns a range of the matching rows.
	*
	* <p>
	* Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to {@link com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS} will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent and pagination is required (<code>start</code> and <code>end</code> are not {@link com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS}), then the query will include the default ORDER BY logic from {@link com.liferay.multi.factor.authentication.provider.totp.model.impl.TOTPModelImpl}. If both <code>orderByComparator</code> and pagination are absent, for performance reasons, the query will not have an ORDER BY clause and the returned result set will be sorted on by the primary key in an ascending order.
	* </p>
	*
	* @param dynamicQuery the dynamic query
	* @param start the lower bound of the range of model instances
	* @param end the upper bound of the range of model instances (not inclusive)
	* @return the range of matching rows
	*/
	@Override
	public <T> java.util.List<T> dynamicQuery(
		com.liferay.portal.kernel.dao.orm.DynamicQuery dynamicQuery, int start,
		int end) {
		return _totpLocalService.dynamicQuery(dynamicQuery, start, end);
	}

	/**
	* Performs a dynamic query on the database and returns an ordered range of the matching rows.
	*
	* <p>
	* Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to {@link com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS} will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent and pagination is required (<code>start</code> and <code>end</code> are not {@link com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS}), then the query will include the default ORDER BY logic from {@link com.liferay.multi.factor.authentication.provider.totp.model.impl.TOTPModelImpl}. If both <code>orderByComparator</code> and pagination are absent, for performance reasons, the query will not have an ORDER BY clause and the returned result set will be sorted on by the primary key in an ascending order.
	* </p>
	*
	* @param dynamicQuery the dynamic query
	* @param start the lower bound of the range of model instances
	* @param end the upper bound of the range of model instances (not inclusive)
	* @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	* @return the ordered range of matching rows
	*/
	@Override
	public <T> java.util.List<T> dynamicQuery(
		com.liferay.portal.kernel.dao.orm.DynamicQuery dynamicQuery, int start,
		int end,
		com.liferay.portal.kernel.util.OrderByComparator<T> orderByComparator) {
		return _totpLocalService.dynamicQuery(dynamicQuery, start, end,
			orderByComparator);
	}

	/**
	* Returns the number of rows matching the dynamic query.
	*
	* @param dynamicQuery the dynamic query
	* @return the number of rows matching the dynamic query
	*/
	@Override
	public long dynamicQueryCount(
		com.liferay.portal.kernel.dao.orm.DynamicQuery dynamicQuery) {
		return _totpLocalService.dynamicQueryCount(dynamicQuery);
	}

	/**
	* Returns the number of rows matching the dynamic query.
	*
	* @param dynamicQuery the dynamic query
	* @param projection the projection to apply to the query
	* @return the number of rows matching the dynamic query
	*/
	@Override
	public long dynamicQueryCount(
		com.liferay.portal.kernel.dao.orm.DynamicQuery dynamicQuery,
		com.liferay.portal.kernel.dao.orm.Projection projection) {
		return _totpLocalService.dynamicQueryCount(dynamicQuery, projection);
	}

	@Override
	public com.liferay.multi.factor.authentication.provider.totp.model.TOTP fetchTOTP(
		long totpId) {
		return _totpLocalService.fetchTOTP(totpId);
	}

	@Override
	public com.liferay.multi.factor.authentication.provider.totp.model.TOTP fetchTOTPByUserId(
		long userId) {
		return _totpLocalService.fetchTOTPByUserId(userId);
	}

	@Override
	public com.liferay.portal.kernel.dao.orm.ActionableDynamicQuery getActionableDynamicQuery() {
		return _totpLocalService.getActionableDynamicQuery();
	}

	@Override
	public com.liferay.portal.kernel.dao.orm.IndexableActionableDynamicQuery getIndexableActionableDynamicQuery() {
		return _totpLocalService.getIndexableActionableDynamicQuery();
	}

	/**
	* Returns the OSGi service identifier.
	*
	* @return the OSGi service identifier
	*/
	@Override
	public String getOSGiServiceIdentifier() {
		return _totpLocalService.getOSGiServiceIdentifier();
	}

	@Override
	public com.liferay.portal.kernel.model.PersistedModel getPersistedModel(
		java.io.Serializable primaryKeyObj)
		throws com.liferay.portal.kernel.exception.PortalException {
		return _totpLocalService.getPersistedModel(primaryKeyObj);
	}

	/**
	* Returns the totp with the primary key.
	*
	* @param totpId the primary key of the totp
	* @return the totp
	* @throws PortalException if a totp with the primary key could not be found
	*/
	@Override
	public com.liferay.multi.factor.authentication.provider.totp.model.TOTP getTOTP(
		long totpId) throws com.liferay.portal.kernel.exception.PortalException {
		return _totpLocalService.getTOTP(totpId);
	}

	/**
	* Returns a range of all the totps.
	*
	* <p>
	* Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to {@link com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS} will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent and pagination is required (<code>start</code> and <code>end</code> are not {@link com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS}), then the query will include the default ORDER BY logic from {@link com.liferay.multi.factor.authentication.provider.totp.model.impl.TOTPModelImpl}. If both <code>orderByComparator</code> and pagination are absent, for performance reasons, the query will not have an ORDER BY clause and the returned result set will be sorted on by the primary key in an ascending order.
	* </p>
	*
	* @param start the lower bound of the range of totps
	* @param end the upper bound of the range of totps (not inclusive)
	* @return the range of totps
	*/
	@Override
	public java.util.List<com.liferay.multi.factor.authentication.provider.totp.model.TOTP> getTOTPs(
		int start, int end) {
		return _totpLocalService.getTOTPs(start, end);
	}

	/**
	* Returns the number of totps.
	*
	* @return the number of totps
	*/
	@Override
	public int getTOTPsCount() {
		return _totpLocalService.getTOTPsCount();
	}

	/**
	* Updates the totp in the database or adds it if it does not yet exist. Also notifies the appropriate model listeners.
	*
	* @param totp the totp
	* @return the totp that was updated
	*/
	@Override
	public com.liferay.multi.factor.authentication.provider.totp.model.TOTP updateTOTP(
		com.liferay.multi.factor.authentication.provider.totp.model.TOTP totp) {
		return _totpLocalService.updateTOTP(totp);
	}

	@Override
	public com.liferay.multi.factor.authentication.provider.totp.model.TOTP updateVerified(
		long totpId, boolean verified)
		throws com.liferay.portal.kernel.exception.PortalException {
		return _totpLocalService.updateVerified(totpId, verified);
	}

	@Override
	public TOTPLocalService getWrappedService() {
		return _totpLocalService;
	}

	@Override
	public void setWrappedService(TOTPLocalService totpLocalService) {
		_totpLocalService = totpLocalService;
	}

	private TOTPLocalService _totpLocalService;
}