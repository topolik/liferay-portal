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
 * Provides a wrapper for {@link OTPEMAILLocalService}.
 *
 * @author arthurchan35
 * @see OTPEMAILLocalService
 * @generated
 */
@ProviderType
public class OTPEMAILLocalServiceWrapper implements OTPEMAILLocalService,
	ServiceWrapper<OTPEMAILLocalService> {
	public OTPEMAILLocalServiceWrapper(
		OTPEMAILLocalService otpemailLocalService) {
		_otpemailLocalService = otpemailLocalService;
	}

	/**
	* Adds the otpemail to the database. Also notifies the appropriate model listeners.
	*
	* @param otpemail the otpemail
	* @return the otpemail that was added
	*/
	@Override
	public com.liferay.multi.factor.authentication.provider.otp.model.OTPEMAIL addOTPEMAIL(
		com.liferay.multi.factor.authentication.provider.otp.model.OTPEMAIL otpemail) {
		return _otpemailLocalService.addOTPEMAIL(otpemail);
	}

	/**
	* Creates a new otpemail with the primary key. Does not add the otpemail to the database.
	*
	* @param otpEmailId the primary key for the new otpemail
	* @return the new otpemail
	*/
	@Override
	public com.liferay.multi.factor.authentication.provider.otp.model.OTPEMAIL createOTPEMAIL(
		long otpEmailId) {
		return _otpemailLocalService.createOTPEMAIL(otpEmailId);
	}

	/**
	* Deletes the otpemail with the primary key from the database. Also notifies the appropriate model listeners.
	*
	* @param otpEmailId the primary key of the otpemail
	* @return the otpemail that was removed
	* @throws PortalException if a otpemail with the primary key could not be found
	*/
	@Override
	public com.liferay.multi.factor.authentication.provider.otp.model.OTPEMAIL deleteOTPEMAIL(
		long otpEmailId)
		throws com.liferay.portal.kernel.exception.PortalException {
		return _otpemailLocalService.deleteOTPEMAIL(otpEmailId);
	}

	/**
	* Deletes the otpemail from the database. Also notifies the appropriate model listeners.
	*
	* @param otpemail the otpemail
	* @return the otpemail that was removed
	*/
	@Override
	public com.liferay.multi.factor.authentication.provider.otp.model.OTPEMAIL deleteOTPEMAIL(
		com.liferay.multi.factor.authentication.provider.otp.model.OTPEMAIL otpemail) {
		return _otpemailLocalService.deleteOTPEMAIL(otpemail);
	}

	/**
	* @throws PortalException
	*/
	@Override
	public com.liferay.portal.kernel.model.PersistedModel deletePersistedModel(
		com.liferay.portal.kernel.model.PersistedModel persistedModel)
		throws com.liferay.portal.kernel.exception.PortalException {
		return _otpemailLocalService.deletePersistedModel(persistedModel);
	}

	@Override
	public com.liferay.portal.kernel.dao.orm.DynamicQuery dynamicQuery() {
		return _otpemailLocalService.dynamicQuery();
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
		return _otpemailLocalService.dynamicQuery(dynamicQuery);
	}

	/**
	* Performs a dynamic query on the database and returns a range of the matching rows.
	*
	* <p>
	* Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to {@link com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS} will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent and pagination is required (<code>start</code> and <code>end</code> are not {@link com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS}), then the query will include the default ORDER BY logic from {@link com.liferay.multi.factor.authentication.provider.otp.model.impl.OTPEMAILModelImpl}. If both <code>orderByComparator</code> and pagination are absent, for performance reasons, the query will not have an ORDER BY clause and the returned result set will be sorted on by the primary key in an ascending order.
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
		return _otpemailLocalService.dynamicQuery(dynamicQuery, start, end);
	}

	/**
	* Performs a dynamic query on the database and returns an ordered range of the matching rows.
	*
	* <p>
	* Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to {@link com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS} will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent and pagination is required (<code>start</code> and <code>end</code> are not {@link com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS}), then the query will include the default ORDER BY logic from {@link com.liferay.multi.factor.authentication.provider.otp.model.impl.OTPEMAILModelImpl}. If both <code>orderByComparator</code> and pagination are absent, for performance reasons, the query will not have an ORDER BY clause and the returned result set will be sorted on by the primary key in an ascending order.
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
		return _otpemailLocalService.dynamicQuery(dynamicQuery, start, end,
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
		return _otpemailLocalService.dynamicQueryCount(dynamicQuery);
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
		return _otpemailLocalService.dynamicQueryCount(dynamicQuery, projection);
	}

	@Override
	public com.liferay.multi.factor.authentication.provider.otp.model.OTPEMAIL fetchOTPEMAIL(
		long otpEmailId) {
		return _otpemailLocalService.fetchOTPEMAIL(otpEmailId);
	}

	@Override
	public com.liferay.portal.kernel.dao.orm.ActionableDynamicQuery getActionableDynamicQuery() {
		return _otpemailLocalService.getActionableDynamicQuery();
	}

	@Override
	public com.liferay.portal.kernel.dao.orm.IndexableActionableDynamicQuery getIndexableActionableDynamicQuery() {
		return _otpemailLocalService.getIndexableActionableDynamicQuery();
	}

	/**
	* Returns the OSGi service identifier.
	*
	* @return the OSGi service identifier
	*/
	@Override
	public String getOSGiServiceIdentifier() {
		return _otpemailLocalService.getOSGiServiceIdentifier();
	}

	/**
	* Returns the otpemail with the primary key.
	*
	* @param otpEmailId the primary key of the otpemail
	* @return the otpemail
	* @throws PortalException if a otpemail with the primary key could not be found
	*/
	@Override
	public com.liferay.multi.factor.authentication.provider.otp.model.OTPEMAIL getOTPEMAIL(
		long otpEmailId)
		throws com.liferay.portal.kernel.exception.PortalException {
		return _otpemailLocalService.getOTPEMAIL(otpEmailId);
	}

	/**
	* Returns a range of all the otpemails.
	*
	* <p>
	* Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to {@link com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS} will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent and pagination is required (<code>start</code> and <code>end</code> are not {@link com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS}), then the query will include the default ORDER BY logic from {@link com.liferay.multi.factor.authentication.provider.otp.model.impl.OTPEMAILModelImpl}. If both <code>orderByComparator</code> and pagination are absent, for performance reasons, the query will not have an ORDER BY clause and the returned result set will be sorted on by the primary key in an ascending order.
	* </p>
	*
	* @param start the lower bound of the range of otpemails
	* @param end the upper bound of the range of otpemails (not inclusive)
	* @return the range of otpemails
	*/
	@Override
	public java.util.List<com.liferay.multi.factor.authentication.provider.otp.model.OTPEMAIL> getOTPEMAILs(
		int start, int end) {
		return _otpemailLocalService.getOTPEMAILs(start, end);
	}

	/**
	* Returns the number of otpemails.
	*
	* @return the number of otpemails
	*/
	@Override
	public int getOTPEMAILsCount() {
		return _otpemailLocalService.getOTPEMAILsCount();
	}

	@Override
	public com.liferay.portal.kernel.model.PersistedModel getPersistedModel(
		java.io.Serializable primaryKeyObj)
		throws com.liferay.portal.kernel.exception.PortalException {
		return _otpemailLocalService.getPersistedModel(primaryKeyObj);
	}

	/**
	* Updates the otpemail in the database or adds it if it does not yet exist. Also notifies the appropriate model listeners.
	*
	* @param otpemail the otpemail
	* @return the otpemail that was updated
	*/
	@Override
	public com.liferay.multi.factor.authentication.provider.otp.model.OTPEMAIL updateOTPEMAIL(
		com.liferay.multi.factor.authentication.provider.otp.model.OTPEMAIL otpemail) {
		return _otpemailLocalService.updateOTPEMAIL(otpemail);
	}

	@Override
	public OTPEMAILLocalService getWrappedService() {
		return _otpemailLocalService;
	}

	@Override
	public void setWrappedService(OTPEMAILLocalService otpemailLocalService) {
		_otpemailLocalService = otpemailLocalService;
	}

	private OTPEMAILLocalService _otpemailLocalService;
}