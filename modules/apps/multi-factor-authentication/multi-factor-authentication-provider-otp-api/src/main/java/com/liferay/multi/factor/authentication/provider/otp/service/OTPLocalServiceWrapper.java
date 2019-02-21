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

package com.liferay.multi.factor.authentication.provider.otp.service;

import aQute.bnd.annotation.ProviderType;

import com.liferay.portal.kernel.service.ServiceWrapper;

/**
 * Provides a wrapper for {@link OTPLocalService}.
 *
 * @author arthurchan35
 * @see OTPLocalService
 * @generated
 */
@ProviderType
public class OTPLocalServiceWrapper implements OTPLocalService,
	ServiceWrapper<OTPLocalService> {
	public OTPLocalServiceWrapper(OTPLocalService otpLocalService) {
		_otpLocalService = otpLocalService;
	}

	/**
	* NOTE FOR DEVELOPERS:
	*
	* Never reference this class directly. Always use {@link OTPLocalServiceUtil} to access the otp local service.
	*/
	@Override
	public com.liferay.multi.factor.authentication.provider.otp.model.OTP addOTP(
		long userId, String email)
		throws com.liferay.portal.kernel.exception.PortalException {
		return _otpLocalService.addOTP(userId, email);
	}

	/**
	* Adds the otp to the database. Also notifies the appropriate model listeners.
	*
	* @param otp the otp
	* @return the otp that was added
	*/
	@Override
	public com.liferay.multi.factor.authentication.provider.otp.model.OTP addOTP(
		com.liferay.multi.factor.authentication.provider.otp.model.OTP otp) {
		return _otpLocalService.addOTP(otp);
	}

	/**
	* Creates a new otp with the primary key. Does not add the otp to the database.
	*
	* @param otpId the primary key for the new otp
	* @return the new otp
	*/
	@Override
	public com.liferay.multi.factor.authentication.provider.otp.model.OTP createOTP(
		long otpId) {
		return _otpLocalService.createOTP(otpId);
	}

	/**
	* Deletes the otp with the primary key from the database. Also notifies the appropriate model listeners.
	*
	* @param otpId the primary key of the otp
	* @return the otp that was removed
	* @throws PortalException if a otp with the primary key could not be found
	*/
	@Override
	public com.liferay.multi.factor.authentication.provider.otp.model.OTP deleteOTP(
		long otpId) throws com.liferay.portal.kernel.exception.PortalException {
		return _otpLocalService.deleteOTP(otpId);
	}

	/**
	* Deletes the otp from the database. Also notifies the appropriate model listeners.
	*
	* @param otp the otp
	* @return the otp that was removed
	*/
	@Override
	public com.liferay.multi.factor.authentication.provider.otp.model.OTP deleteOTP(
		com.liferay.multi.factor.authentication.provider.otp.model.OTP otp) {
		return _otpLocalService.deleteOTP(otp);
	}

	/**
	* @throws PortalException
	*/
	@Override
	public com.liferay.portal.kernel.model.PersistedModel deletePersistedModel(
		com.liferay.portal.kernel.model.PersistedModel persistedModel)
		throws com.liferay.portal.kernel.exception.PortalException {
		return _otpLocalService.deletePersistedModel(persistedModel);
	}

	@Override
	public com.liferay.portal.kernel.dao.orm.DynamicQuery dynamicQuery() {
		return _otpLocalService.dynamicQuery();
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
		return _otpLocalService.dynamicQuery(dynamicQuery);
	}

	/**
	* Performs a dynamic query on the database and returns a range of the matching rows.
	*
	* <p>
	* Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to {@link com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS} will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent and pagination is required (<code>start</code> and <code>end</code> are not {@link com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS}), then the query will include the default ORDER BY logic from {@link com.liferay.multi.factor.authentication.provider.otp.model.impl.OTPModelImpl}. If both <code>orderByComparator</code> and pagination are absent, for performance reasons, the query will not have an ORDER BY clause and the returned result set will be sorted on by the primary key in an ascending order.
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
		return _otpLocalService.dynamicQuery(dynamicQuery, start, end);
	}

	/**
	* Performs a dynamic query on the database and returns an ordered range of the matching rows.
	*
	* <p>
	* Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to {@link com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS} will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent and pagination is required (<code>start</code> and <code>end</code> are not {@link com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS}), then the query will include the default ORDER BY logic from {@link com.liferay.multi.factor.authentication.provider.otp.model.impl.OTPModelImpl}. If both <code>orderByComparator</code> and pagination are absent, for performance reasons, the query will not have an ORDER BY clause and the returned result set will be sorted on by the primary key in an ascending order.
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
		return _otpLocalService.dynamicQuery(dynamicQuery, start, end,
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
		return _otpLocalService.dynamicQueryCount(dynamicQuery);
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
		return _otpLocalService.dynamicQueryCount(dynamicQuery, projection);
	}

	@Override
	public com.liferay.multi.factor.authentication.provider.otp.model.OTP fetchOTP(
		long otpId) {
		return _otpLocalService.fetchOTP(otpId);
	}

	@Override
	public com.liferay.multi.factor.authentication.provider.otp.model.OTP fetchOTPByUserId(
		long userId) {
		return _otpLocalService.fetchOTPByUserId(userId);
	}

	@Override
	public com.liferay.portal.kernel.dao.orm.ActionableDynamicQuery getActionableDynamicQuery() {
		return _otpLocalService.getActionableDynamicQuery();
	}

	@Override
	public com.liferay.portal.kernel.dao.orm.IndexableActionableDynamicQuery getIndexableActionableDynamicQuery() {
		return _otpLocalService.getIndexableActionableDynamicQuery();
	}

	/**
	* Returns the OSGi service identifier.
	*
	* @return the OSGi service identifier
	*/
	@Override
	public String getOSGiServiceIdentifier() {
		return _otpLocalService.getOSGiServiceIdentifier();
	}

	/**
	* Returns the otp with the primary key.
	*
	* @param otpId the primary key of the otp
	* @return the otp
	* @throws PortalException if a otp with the primary key could not be found
	*/
	@Override
	public com.liferay.multi.factor.authentication.provider.otp.model.OTP getOTP(
		long otpId) throws com.liferay.portal.kernel.exception.PortalException {
		return _otpLocalService.getOTP(otpId);
	}

	/**
	* Returns a range of all the otps.
	*
	* <p>
	* Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to {@link com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS} will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent and pagination is required (<code>start</code> and <code>end</code> are not {@link com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS}), then the query will include the default ORDER BY logic from {@link com.liferay.multi.factor.authentication.provider.otp.model.impl.OTPModelImpl}. If both <code>orderByComparator</code> and pagination are absent, for performance reasons, the query will not have an ORDER BY clause and the returned result set will be sorted on by the primary key in an ascending order.
	* </p>
	*
	* @param start the lower bound of the range of otps
	* @param end the upper bound of the range of otps (not inclusive)
	* @return the range of otps
	*/
	@Override
	public java.util.List<com.liferay.multi.factor.authentication.provider.otp.model.OTP> getOTPs(
		int start, int end) {
		return _otpLocalService.getOTPs(start, end);
	}

	/**
	* Returns the number of otps.
	*
	* @return the number of otps
	*/
	@Override
	public int getOTPsCount() {
		return _otpLocalService.getOTPsCount();
	}

	@Override
	public com.liferay.portal.kernel.model.PersistedModel getPersistedModel(
		java.io.Serializable primaryKeyObj)
		throws com.liferay.portal.kernel.exception.PortalException {
		return _otpLocalService.getPersistedModel(primaryKeyObj);
	}

	/**
	* Updates the otp in the database or adds it if it does not yet exist. Also notifies the appropriate model listeners.
	*
	* @param otp the otp
	* @return the otp that was updated
	*/
	@Override
	public com.liferay.multi.factor.authentication.provider.otp.model.OTP updateOTP(
		com.liferay.multi.factor.authentication.provider.otp.model.OTP otp) {
		return _otpLocalService.updateOTP(otp);
	}

	@Override
	public com.liferay.multi.factor.authentication.provider.otp.model.OTP updateVerified(
		long userId, boolean verified)
		throws com.liferay.portal.kernel.exception.PortalException {
		return _otpLocalService.updateVerified(userId, verified);
	}

	@Override
	public OTPLocalService getWrappedService() {
		return _otpLocalService;
	}

	@Override
	public void setWrappedService(OTPLocalService otpLocalService) {
		_otpLocalService = otpLocalService;
	}

	private OTPLocalService _otpLocalService;
}