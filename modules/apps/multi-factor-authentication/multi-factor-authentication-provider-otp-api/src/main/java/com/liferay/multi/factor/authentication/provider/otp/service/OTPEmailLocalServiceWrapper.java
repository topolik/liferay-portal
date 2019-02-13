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
 * Provides a wrapper for {@link OTPEmailLocalService}.
 *
 * @author arthurchan35
 * @see OTPEmailLocalService
 * @generated
 */
@ProviderType
public class OTPEmailLocalServiceWrapper implements OTPEmailLocalService,
	ServiceWrapper<OTPEmailLocalService> {
	public OTPEmailLocalServiceWrapper(
		OTPEmailLocalService otpEmailLocalService) {
		_otpEmailLocalService = otpEmailLocalService;
	}

	/**
	* Adds the otp email to the database. Also notifies the appropriate model listeners.
	*
	* @param otpEmail the otp email
	* @return the otp email that was added
	*/
	@Override
	public com.liferay.multi.factor.authentication.provider.otp.model.OTPEmail addOTPEmail(
		com.liferay.multi.factor.authentication.provider.otp.model.OTPEmail otpEmail) {
		return _otpEmailLocalService.addOTPEmail(otpEmail);
	}

	/**
	* Creates a new otp email with the primary key. Does not add the otp email to the database.
	*
	* @param otpEmailId the primary key for the new otp email
	* @return the new otp email
	*/
	@Override
	public com.liferay.multi.factor.authentication.provider.otp.model.OTPEmail createOTPEmail(
		long otpEmailId) {
		return _otpEmailLocalService.createOTPEmail(otpEmailId);
	}

	/**
	* Deletes the otp email with the primary key from the database. Also notifies the appropriate model listeners.
	*
	* @param otpEmailId the primary key of the otp email
	* @return the otp email that was removed
	* @throws PortalException if a otp email with the primary key could not be found
	*/
	@Override
	public com.liferay.multi.factor.authentication.provider.otp.model.OTPEmail deleteOTPEmail(
		long otpEmailId)
		throws com.liferay.portal.kernel.exception.PortalException {
		return _otpEmailLocalService.deleteOTPEmail(otpEmailId);
	}

	/**
	* Deletes the otp email from the database. Also notifies the appropriate model listeners.
	*
	* @param otpEmail the otp email
	* @return the otp email that was removed
	*/
	@Override
	public com.liferay.multi.factor.authentication.provider.otp.model.OTPEmail deleteOTPEmail(
		com.liferay.multi.factor.authentication.provider.otp.model.OTPEmail otpEmail) {
		return _otpEmailLocalService.deleteOTPEmail(otpEmail);
	}

	/**
	* @throws PortalException
	*/
	@Override
	public com.liferay.portal.kernel.model.PersistedModel deletePersistedModel(
		com.liferay.portal.kernel.model.PersistedModel persistedModel)
		throws com.liferay.portal.kernel.exception.PortalException {
		return _otpEmailLocalService.deletePersistedModel(persistedModel);
	}

	@Override
	public com.liferay.portal.kernel.dao.orm.DynamicQuery dynamicQuery() {
		return _otpEmailLocalService.dynamicQuery();
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
		return _otpEmailLocalService.dynamicQuery(dynamicQuery);
	}

	/**
	* Performs a dynamic query on the database and returns a range of the matching rows.
	*
	* <p>
	* Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to {@link com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS} will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent and pagination is required (<code>start</code> and <code>end</code> are not {@link com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS}), then the query will include the default ORDER BY logic from {@link com.liferay.multi.factor.authentication.provider.otp.model.impl.OTPEmailModelImpl}. If both <code>orderByComparator</code> and pagination are absent, for performance reasons, the query will not have an ORDER BY clause and the returned result set will be sorted on by the primary key in an ascending order.
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
		return _otpEmailLocalService.dynamicQuery(dynamicQuery, start, end);
	}

	/**
	* Performs a dynamic query on the database and returns an ordered range of the matching rows.
	*
	* <p>
	* Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to {@link com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS} will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent and pagination is required (<code>start</code> and <code>end</code> are not {@link com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS}), then the query will include the default ORDER BY logic from {@link com.liferay.multi.factor.authentication.provider.otp.model.impl.OTPEmailModelImpl}. If both <code>orderByComparator</code> and pagination are absent, for performance reasons, the query will not have an ORDER BY clause and the returned result set will be sorted on by the primary key in an ascending order.
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
		return _otpEmailLocalService.dynamicQuery(dynamicQuery, start, end,
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
		return _otpEmailLocalService.dynamicQueryCount(dynamicQuery);
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
		return _otpEmailLocalService.dynamicQueryCount(dynamicQuery, projection);
	}

	@Override
	public com.liferay.multi.factor.authentication.provider.otp.model.OTPEmail fetchOTPEmail(
		long otpEmailId) {
		return _otpEmailLocalService.fetchOTPEmail(otpEmailId);
	}

	@Override
	public com.liferay.portal.kernel.dao.orm.ActionableDynamicQuery getActionableDynamicQuery() {
		return _otpEmailLocalService.getActionableDynamicQuery();
	}

	@Override
	public com.liferay.portal.kernel.dao.orm.IndexableActionableDynamicQuery getIndexableActionableDynamicQuery() {
		return _otpEmailLocalService.getIndexableActionableDynamicQuery();
	}

	/**
	* Returns the OSGi service identifier.
	*
	* @return the OSGi service identifier
	*/
	@Override
	public String getOSGiServiceIdentifier() {
		return _otpEmailLocalService.getOSGiServiceIdentifier();
	}

	/**
	* Returns the otp email with the primary key.
	*
	* @param otpEmailId the primary key of the otp email
	* @return the otp email
	* @throws PortalException if a otp email with the primary key could not be found
	*/
	@Override
	public com.liferay.multi.factor.authentication.provider.otp.model.OTPEmail getOTPEmail(
		long otpEmailId)
		throws com.liferay.portal.kernel.exception.PortalException {
		return _otpEmailLocalService.getOTPEmail(otpEmailId);
	}

	/**
	* Returns a range of all the otp emails.
	*
	* <p>
	* Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to {@link com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS} will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent and pagination is required (<code>start</code> and <code>end</code> are not {@link com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS}), then the query will include the default ORDER BY logic from {@link com.liferay.multi.factor.authentication.provider.otp.model.impl.OTPEmailModelImpl}. If both <code>orderByComparator</code> and pagination are absent, for performance reasons, the query will not have an ORDER BY clause and the returned result set will be sorted on by the primary key in an ascending order.
	* </p>
	*
	* @param start the lower bound of the range of otp emails
	* @param end the upper bound of the range of otp emails (not inclusive)
	* @return the range of otp emails
	*/
	@Override
	public java.util.List<com.liferay.multi.factor.authentication.provider.otp.model.OTPEmail> getOTPEmails(
		int start, int end) {
		return _otpEmailLocalService.getOTPEmails(start, end);
	}

	/**
	* Returns the number of otp emails.
	*
	* @return the number of otp emails
	*/
	@Override
	public int getOTPEmailsCount() {
		return _otpEmailLocalService.getOTPEmailsCount();
	}

	@Override
	public com.liferay.portal.kernel.model.PersistedModel getPersistedModel(
		java.io.Serializable primaryKeyObj)
		throws com.liferay.portal.kernel.exception.PortalException {
		return _otpEmailLocalService.getPersistedModel(primaryKeyObj);
	}

	/**
	* Updates the otp email in the database or adds it if it does not yet exist. Also notifies the appropriate model listeners.
	*
	* @param otpEmail the otp email
	* @return the otp email that was updated
	*/
	@Override
	public com.liferay.multi.factor.authentication.provider.otp.model.OTPEmail updateOTPEmail(
		com.liferay.multi.factor.authentication.provider.otp.model.OTPEmail otpEmail) {
		return _otpEmailLocalService.updateOTPEmail(otpEmail);
	}

	@Override
	public OTPEmailLocalService getWrappedService() {
		return _otpEmailLocalService;
	}

	@Override
	public void setWrappedService(OTPEmailLocalService otpEmailLocalService) {
		_otpEmailLocalService = otpEmailLocalService;
	}

	private OTPEmailLocalService _otpEmailLocalService;
}