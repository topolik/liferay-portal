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

import org.osgi.framework.Bundle;
import org.osgi.framework.FrameworkUtil;

import org.osgi.util.tracker.ServiceTracker;

/**
 * Provides the local service utility for OTP. This utility wraps
 * {@link com.liferay.multi.factor.authentication.provider.otp.service.impl.OTPLocalServiceImpl} and is the
 * primary access point for service operations in application layer code running
 * on the local server. Methods of this service will not have security checks
 * based on the propagated JAAS credentials because this service can only be
 * accessed from within the same VM.
 *
 * @author arthurchan35
 * @see OTPLocalService
 * @see com.liferay.multi.factor.authentication.provider.otp.service.base.OTPLocalServiceBaseImpl
 * @see com.liferay.multi.factor.authentication.provider.otp.service.impl.OTPLocalServiceImpl
 * @generated
 */
@ProviderType
public class OTPLocalServiceUtil {
	/*
	 * NOTE FOR DEVELOPERS:
	 *
	 * Never modify this class directly. Add custom service methods to {@link com.liferay.multi.factor.authentication.provider.otp.service.impl.OTPLocalServiceImpl} and rerun ServiceBuilder to regenerate this class.
	 */

	/**
	* NOTE FOR DEVELOPERS:
	*
	* Never reference this class directly. Always use {@link OTPLocalServiceUtil} to access the otp local service.
	*/
	public static com.liferay.multi.factor.authentication.provider.otp.model.OTP addOTP(
		long userId, String email)
		throws com.liferay.portal.kernel.exception.PortalException {
		return getService().addOTP(userId, email);
	}

	/**
	* Adds the otp to the database. Also notifies the appropriate model listeners.
	*
	* @param otp the otp
	* @return the otp that was added
	*/
	public static com.liferay.multi.factor.authentication.provider.otp.model.OTP addOTP(
		com.liferay.multi.factor.authentication.provider.otp.model.OTP otp) {
		return getService().addOTP(otp);
	}

	/**
	* Creates a new otp with the primary key. Does not add the otp to the database.
	*
	* @param otpId the primary key for the new otp
	* @return the new otp
	*/
	public static com.liferay.multi.factor.authentication.provider.otp.model.OTP createOTP(
		long otpId) {
		return getService().createOTP(otpId);
	}

	/**
	* Deletes the otp with the primary key from the database. Also notifies the appropriate model listeners.
	*
	* @param otpId the primary key of the otp
	* @return the otp that was removed
	* @throws PortalException if a otp with the primary key could not be found
	*/
	public static com.liferay.multi.factor.authentication.provider.otp.model.OTP deleteOTP(
		long otpId) throws com.liferay.portal.kernel.exception.PortalException {
		return getService().deleteOTP(otpId);
	}

	/**
	* Deletes the otp from the database. Also notifies the appropriate model listeners.
	*
	* @param otp the otp
	* @return the otp that was removed
	*/
	public static com.liferay.multi.factor.authentication.provider.otp.model.OTP deleteOTP(
		com.liferay.multi.factor.authentication.provider.otp.model.OTP otp) {
		return getService().deleteOTP(otp);
	}

	/**
	* @throws PortalException
	*/
	public static com.liferay.portal.kernel.model.PersistedModel deletePersistedModel(
		com.liferay.portal.kernel.model.PersistedModel persistedModel)
		throws com.liferay.portal.kernel.exception.PortalException {
		return getService().deletePersistedModel(persistedModel);
	}

	public static com.liferay.portal.kernel.dao.orm.DynamicQuery dynamicQuery() {
		return getService().dynamicQuery();
	}

	/**
	* Performs a dynamic query on the database and returns the matching rows.
	*
	* @param dynamicQuery the dynamic query
	* @return the matching rows
	*/
	public static <T> java.util.List<T> dynamicQuery(
		com.liferay.portal.kernel.dao.orm.DynamicQuery dynamicQuery) {
		return getService().dynamicQuery(dynamicQuery);
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
	public static <T> java.util.List<T> dynamicQuery(
		com.liferay.portal.kernel.dao.orm.DynamicQuery dynamicQuery, int start,
		int end) {
		return getService().dynamicQuery(dynamicQuery, start, end);
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
	public static <T> java.util.List<T> dynamicQuery(
		com.liferay.portal.kernel.dao.orm.DynamicQuery dynamicQuery, int start,
		int end,
		com.liferay.portal.kernel.util.OrderByComparator<T> orderByComparator) {
		return getService()
				   .dynamicQuery(dynamicQuery, start, end, orderByComparator);
	}

	/**
	* Returns the number of rows matching the dynamic query.
	*
	* @param dynamicQuery the dynamic query
	* @return the number of rows matching the dynamic query
	*/
	public static long dynamicQueryCount(
		com.liferay.portal.kernel.dao.orm.DynamicQuery dynamicQuery) {
		return getService().dynamicQueryCount(dynamicQuery);
	}

	/**
	* Returns the number of rows matching the dynamic query.
	*
	* @param dynamicQuery the dynamic query
	* @param projection the projection to apply to the query
	* @return the number of rows matching the dynamic query
	*/
	public static long dynamicQueryCount(
		com.liferay.portal.kernel.dao.orm.DynamicQuery dynamicQuery,
		com.liferay.portal.kernel.dao.orm.Projection projection) {
		return getService().dynamicQueryCount(dynamicQuery, projection);
	}

	public static com.liferay.multi.factor.authentication.provider.otp.model.OTP fetchOTP(
		long otpId) {
		return getService().fetchOTP(otpId);
	}

	public static com.liferay.multi.factor.authentication.provider.otp.model.OTP fetchOTPByUserId(
		long userId) {
		return getService().fetchOTPByUserId(userId);
	}

	public static com.liferay.portal.kernel.dao.orm.ActionableDynamicQuery getActionableDynamicQuery() {
		return getService().getActionableDynamicQuery();
	}

	public static com.liferay.portal.kernel.dao.orm.IndexableActionableDynamicQuery getIndexableActionableDynamicQuery() {
		return getService().getIndexableActionableDynamicQuery();
	}

	/**
	* Returns the OSGi service identifier.
	*
	* @return the OSGi service identifier
	*/
	public static String getOSGiServiceIdentifier() {
		return getService().getOSGiServiceIdentifier();
	}

	/**
	* Returns the otp with the primary key.
	*
	* @param otpId the primary key of the otp
	* @return the otp
	* @throws PortalException if a otp with the primary key could not be found
	*/
	public static com.liferay.multi.factor.authentication.provider.otp.model.OTP getOTP(
		long otpId) throws com.liferay.portal.kernel.exception.PortalException {
		return getService().getOTP(otpId);
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
	public static java.util.List<com.liferay.multi.factor.authentication.provider.otp.model.OTP> getOTPs(
		int start, int end) {
		return getService().getOTPs(start, end);
	}

	/**
	* Returns the number of otps.
	*
	* @return the number of otps
	*/
	public static int getOTPsCount() {
		return getService().getOTPsCount();
	}

	public static com.liferay.portal.kernel.model.PersistedModel getPersistedModel(
		java.io.Serializable primaryKeyObj)
		throws com.liferay.portal.kernel.exception.PortalException {
		return getService().getPersistedModel(primaryKeyObj);
	}

	/**
	* Updates the otp in the database or adds it if it does not yet exist. Also notifies the appropriate model listeners.
	*
	* @param otp the otp
	* @return the otp that was updated
	*/
	public static com.liferay.multi.factor.authentication.provider.otp.model.OTP updateOTP(
		com.liferay.multi.factor.authentication.provider.otp.model.OTP otp) {
		return getService().updateOTP(otp);
	}

	public static com.liferay.multi.factor.authentication.provider.otp.model.OTP updateVerified(
		long userId, boolean verified)
		throws com.liferay.portal.kernel.exception.PortalException {
		return getService().updateVerified(userId, verified);
	}

	public static OTPLocalService getService() {
		return _serviceTracker.getService();
	}

	private static ServiceTracker<OTPLocalService, OTPLocalService> _serviceTracker;

	static {
		Bundle bundle = FrameworkUtil.getBundle(OTPLocalService.class);

		ServiceTracker<OTPLocalService, OTPLocalService> serviceTracker = new ServiceTracker<OTPLocalService, OTPLocalService>(bundle.getBundleContext(),
				OTPLocalService.class, null);

		serviceTracker.open();

		_serviceTracker = serviceTracker;
	}
}