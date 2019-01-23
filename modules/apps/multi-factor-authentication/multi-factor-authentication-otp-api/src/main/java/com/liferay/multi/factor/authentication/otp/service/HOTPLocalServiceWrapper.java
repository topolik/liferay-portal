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

package com.liferay.multi.factor.authentication.otp.service;

import aQute.bnd.annotation.ProviderType;

import com.liferay.portal.kernel.service.ServiceWrapper;

/**
 * Provides a wrapper for {@link HOTPLocalService}.
 *
 * @author arthurchan35
 * @see HOTPLocalService
 * @generated
 */
@ProviderType
public class HOTPLocalServiceWrapper implements HOTPLocalService,
	ServiceWrapper<HOTPLocalService> {
	public HOTPLocalServiceWrapper(HOTPLocalService hotpLocalService) {
		_hotpLocalService = hotpLocalService;
	}

	/**
	* NOTE FOR DEVELOPERS:
	*
	* Never reference this class directly. Always use {@link HOTPLocalServiceUtil} to access the hotp local service.
	*/
	@Override
	public com.liferay.multi.factor.authentication.otp.model.HOTP addFailedAttempts(
		long hotpId) throws com.liferay.portal.kernel.exception.PortalException {
		return _hotpLocalService.addFailedAttempts(hotpId);
	}

	/**
	* Adds the hotp to the database. Also notifies the appropriate model listeners.
	*
	* @param hotp the hotp
	* @return the hotp that was added
	*/
	@Override
	public com.liferay.multi.factor.authentication.otp.model.HOTP addHOTP(
		com.liferay.multi.factor.authentication.otp.model.HOTP hotp) {
		return _hotpLocalService.addHOTP(hotp);
	}

	@Override
	public com.liferay.multi.factor.authentication.otp.model.HOTP addHOTP(
		long userId, String sharedSecret)
		throws com.liferay.portal.kernel.exception.PortalException {
		return _hotpLocalService.addHOTP(userId, sharedSecret);
	}

	/**
	* Creates a new hotp with the primary key. Does not add the hotp to the database.
	*
	* @param hotpId the primary key for the new hotp
	* @return the new hotp
	*/
	@Override
	public com.liferay.multi.factor.authentication.otp.model.HOTP createHOTP(
		long hotpId) {
		return _hotpLocalService.createHOTP(hotpId);
	}

	/**
	* Deletes the hotp from the database. Also notifies the appropriate model listeners.
	*
	* @param hotp the hotp
	* @return the hotp that was removed
	*/
	@Override
	public com.liferay.multi.factor.authentication.otp.model.HOTP deleteHOTP(
		com.liferay.multi.factor.authentication.otp.model.HOTP hotp) {
		return _hotpLocalService.deleteHOTP(hotp);
	}

	/**
	* Deletes the hotp with the primary key from the database. Also notifies the appropriate model listeners.
	*
	* @param hotpId the primary key of the hotp
	* @return the hotp that was removed
	* @throws PortalException if a hotp with the primary key could not be found
	*/
	@Override
	public com.liferay.multi.factor.authentication.otp.model.HOTP deleteHOTP(
		long hotpId) throws com.liferay.portal.kernel.exception.PortalException {
		return _hotpLocalService.deleteHOTP(hotpId);
	}

	/**
	* @throws PortalException
	*/
	@Override
	public com.liferay.portal.kernel.model.PersistedModel deletePersistedModel(
		com.liferay.portal.kernel.model.PersistedModel persistedModel)
		throws com.liferay.portal.kernel.exception.PortalException {
		return _hotpLocalService.deletePersistedModel(persistedModel);
	}

	@Override
	public com.liferay.portal.kernel.dao.orm.DynamicQuery dynamicQuery() {
		return _hotpLocalService.dynamicQuery();
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
		return _hotpLocalService.dynamicQuery(dynamicQuery);
	}

	/**
	* Performs a dynamic query on the database and returns a range of the matching rows.
	*
	* <p>
	* Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to {@link com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS} will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent and pagination is required (<code>start</code> and <code>end</code> are not {@link com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS}), then the query will include the default ORDER BY logic from {@link com.liferay.multi.factor.authentication.otp.model.impl.HOTPModelImpl}. If both <code>orderByComparator</code> and pagination are absent, for performance reasons, the query will not have an ORDER BY clause and the returned result set will be sorted on by the primary key in an ascending order.
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
		return _hotpLocalService.dynamicQuery(dynamicQuery, start, end);
	}

	/**
	* Performs a dynamic query on the database and returns an ordered range of the matching rows.
	*
	* <p>
	* Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to {@link com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS} will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent and pagination is required (<code>start</code> and <code>end</code> are not {@link com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS}), then the query will include the default ORDER BY logic from {@link com.liferay.multi.factor.authentication.otp.model.impl.HOTPModelImpl}. If both <code>orderByComparator</code> and pagination are absent, for performance reasons, the query will not have an ORDER BY clause and the returned result set will be sorted on by the primary key in an ascending order.
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
		return _hotpLocalService.dynamicQuery(dynamicQuery, start, end,
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
		return _hotpLocalService.dynamicQueryCount(dynamicQuery);
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
		return _hotpLocalService.dynamicQueryCount(dynamicQuery, projection);
	}

	@Override
	public com.liferay.multi.factor.authentication.otp.model.HOTP fetchHOTP(
		long hotpId) {
		return _hotpLocalService.fetchHOTP(hotpId);
	}

	@Override
	public com.liferay.multi.factor.authentication.otp.model.HOTP fetchHOTPByUserId(
		long userId) {
		return _hotpLocalService.fetchHOTPByUserId(userId);
	}

	@Override
	public com.liferay.portal.kernel.dao.orm.ActionableDynamicQuery getActionableDynamicQuery() {
		return _hotpLocalService.getActionableDynamicQuery();
	}

	/**
	* Returns the hotp with the primary key.
	*
	* @param hotpId the primary key of the hotp
	* @return the hotp
	* @throws PortalException if a hotp with the primary key could not be found
	*/
	@Override
	public com.liferay.multi.factor.authentication.otp.model.HOTP getHOTP(
		long hotpId) throws com.liferay.portal.kernel.exception.PortalException {
		return _hotpLocalService.getHOTP(hotpId);
	}

	/**
	* Returns a range of all the hotps.
	*
	* <p>
	* Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to {@link com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS} will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent and pagination is required (<code>start</code> and <code>end</code> are not {@link com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS}), then the query will include the default ORDER BY logic from {@link com.liferay.multi.factor.authentication.otp.model.impl.HOTPModelImpl}. If both <code>orderByComparator</code> and pagination are absent, for performance reasons, the query will not have an ORDER BY clause and the returned result set will be sorted on by the primary key in an ascending order.
	* </p>
	*
	* @param start the lower bound of the range of hotps
	* @param end the upper bound of the range of hotps (not inclusive)
	* @return the range of hotps
	*/
	@Override
	public java.util.List<com.liferay.multi.factor.authentication.otp.model.HOTP> getHOTPs(
		int start, int end) {
		return _hotpLocalService.getHOTPs(start, end);
	}

	/**
	* Returns the number of hotps.
	*
	* @return the number of hotps
	*/
	@Override
	public int getHOTPsCount() {
		return _hotpLocalService.getHOTPsCount();
	}

	@Override
	public com.liferay.portal.kernel.dao.orm.IndexableActionableDynamicQuery getIndexableActionableDynamicQuery() {
		return _hotpLocalService.getIndexableActionableDynamicQuery();
	}

	/**
	* Returns the OSGi service identifier.
	*
	* @return the OSGi service identifier
	*/
	@Override
	public String getOSGiServiceIdentifier() {
		return _hotpLocalService.getOSGiServiceIdentifier();
	}

	@Override
	public com.liferay.portal.kernel.model.PersistedModel getPersistedModel(
		java.io.Serializable primaryKeyObj)
		throws com.liferay.portal.kernel.exception.PortalException {
		return _hotpLocalService.getPersistedModel(primaryKeyObj);
	}

	@Override
	public com.liferay.multi.factor.authentication.otp.model.HOTP resetFailedAttempts(
		long hotpId) throws com.liferay.portal.kernel.exception.PortalException {
		return _hotpLocalService.resetFailedAttempts(hotpId);
	}

	@Override
	public com.liferay.multi.factor.authentication.otp.model.HOTP resync(
		long hotpId, int increment)
		throws com.liferay.portal.kernel.exception.PortalException {
		return _hotpLocalService.resync(hotpId, increment);
	}

	/**
	* Updates the hotp in the database or adds it if it does not yet exist. Also notifies the appropriate model listeners.
	*
	* @param hotp the hotp
	* @return the hotp that was updated
	*/
	@Override
	public com.liferay.multi.factor.authentication.otp.model.HOTP updateHOTP(
		com.liferay.multi.factor.authentication.otp.model.HOTP hotp) {
		return _hotpLocalService.updateHOTP(hotp);
	}

	@Override
	public com.liferay.multi.factor.authentication.otp.model.HOTP updateVerified(
		long hotpId, boolean verified)
		throws com.liferay.portal.kernel.exception.PortalException {
		return _hotpLocalService.updateVerified(hotpId, verified);
	}

	@Override
	public HOTPLocalService getWrappedService() {
		return _hotpLocalService;
	}

	@Override
	public void setWrappedService(HOTPLocalService hotpLocalService) {
		_hotpLocalService = hotpLocalService;
	}

	private HOTPLocalService _hotpLocalService;
}