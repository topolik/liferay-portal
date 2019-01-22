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

import org.osgi.framework.Bundle;
import org.osgi.framework.FrameworkUtil;

import org.osgi.util.tracker.ServiceTracker;

/**
 * Provides the local service utility for HOTP. This utility wraps
 * {@link com.liferay.multi.factor.authentication.otp.service.impl.HOTPLocalServiceImpl} and is the
 * primary access point for service operations in application layer code running
 * on the local server. Methods of this service will not have security checks
 * based on the propagated JAAS credentials because this service can only be
 * accessed from within the same VM.
 *
 * @author arthurchan35
 * @see HOTPLocalService
 * @see com.liferay.multi.factor.authentication.otp.service.base.HOTPLocalServiceBaseImpl
 * @see com.liferay.multi.factor.authentication.otp.service.impl.HOTPLocalServiceImpl
 * @generated
 */
@ProviderType
public class HOTPLocalServiceUtil {
	/*
	 * NOTE FOR DEVELOPERS:
	 *
	 * Never modify this class directly. Add custom service methods to {@link com.liferay.multi.factor.authentication.otp.service.impl.HOTPLocalServiceImpl} and rerun ServiceBuilder to regenerate this class.
	 */

	/**
	* NOTE FOR DEVELOPERS:
	*
	* Never reference this class directly. Always use {@link HOTPLocalServiceUtil} to access the hotp local service.
	*/
	public static com.liferay.multi.factor.authentication.otp.model.HOTP addFailedAttempts(
		long hotpId) throws com.liferay.portal.kernel.exception.PortalException {
		return getService().addFailedAttempts(hotpId);
	}

	/**
	* Adds the hotp to the database. Also notifies the appropriate model listeners.
	*
	* @param hotp the hotp
	* @return the hotp that was added
	*/
	public static com.liferay.multi.factor.authentication.otp.model.HOTP addHOTP(
		com.liferay.multi.factor.authentication.otp.model.HOTP hotp) {
		return getService().addHOTP(hotp);
	}

	public static com.liferay.multi.factor.authentication.otp.model.HOTP addHOTP(
		long userId, String sharedSecret)
		throws com.liferay.portal.kernel.exception.PortalException {
		return getService().addHOTP(userId, sharedSecret);
	}

	/**
	* Creates a new hotp with the primary key. Does not add the hotp to the database.
	*
	* @param hotpId the primary key for the new hotp
	* @return the new hotp
	*/
	public static com.liferay.multi.factor.authentication.otp.model.HOTP createHOTP(
		long hotpId) {
		return getService().createHOTP(hotpId);
	}

	/**
	* Deletes the hotp from the database. Also notifies the appropriate model listeners.
	*
	* @param hotp the hotp
	* @return the hotp that was removed
	*/
	public static com.liferay.multi.factor.authentication.otp.model.HOTP deleteHOTP(
		com.liferay.multi.factor.authentication.otp.model.HOTP hotp) {
		return getService().deleteHOTP(hotp);
	}

	/**
	* Deletes the hotp with the primary key from the database. Also notifies the appropriate model listeners.
	*
	* @param hotpId the primary key of the hotp
	* @return the hotp that was removed
	* @throws PortalException if a hotp with the primary key could not be found
	*/
	public static com.liferay.multi.factor.authentication.otp.model.HOTP deleteHOTP(
		long hotpId) throws com.liferay.portal.kernel.exception.PortalException {
		return getService().deleteHOTP(hotpId);
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
	* Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to {@link com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS} will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent and pagination is required (<code>start</code> and <code>end</code> are not {@link com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS}), then the query will include the default ORDER BY logic from {@link com.liferay.multi.factor.authentication.otp.model.impl.HOTPModelImpl}. If both <code>orderByComparator</code> and pagination are absent, for performance reasons, the query will not have an ORDER BY clause and the returned result set will be sorted on by the primary key in an ascending order.
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
	* Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to {@link com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS} will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent and pagination is required (<code>start</code> and <code>end</code> are not {@link com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS}), then the query will include the default ORDER BY logic from {@link com.liferay.multi.factor.authentication.otp.model.impl.HOTPModelImpl}. If both <code>orderByComparator</code> and pagination are absent, for performance reasons, the query will not have an ORDER BY clause and the returned result set will be sorted on by the primary key in an ascending order.
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

	public static com.liferay.multi.factor.authentication.otp.model.HOTP fetchHOTP(
		long hotpId) {
		return getService().fetchHOTP(hotpId);
	}

	public static com.liferay.multi.factor.authentication.otp.model.HOTP fetchHOTPByUserId(
		long userId) {
		return getService().fetchHOTPByUserId(userId);
	}

	public static com.liferay.portal.kernel.dao.orm.ActionableDynamicQuery getActionableDynamicQuery() {
		return getService().getActionableDynamicQuery();
	}

	/**
	* Returns the hotp with the primary key.
	*
	* @param hotpId the primary key of the hotp
	* @return the hotp
	* @throws PortalException if a hotp with the primary key could not be found
	*/
	public static com.liferay.multi.factor.authentication.otp.model.HOTP getHOTP(
		long hotpId) throws com.liferay.portal.kernel.exception.PortalException {
		return getService().getHOTP(hotpId);
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
	public static java.util.List<com.liferay.multi.factor.authentication.otp.model.HOTP> getHOTPs(
		int start, int end) {
		return getService().getHOTPs(start, end);
	}

	/**
	* Returns the number of hotps.
	*
	* @return the number of hotps
	*/
	public static int getHOTPsCount() {
		return getService().getHOTPsCount();
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

	public static com.liferay.portal.kernel.model.PersistedModel getPersistedModel(
		java.io.Serializable primaryKeyObj)
		throws com.liferay.portal.kernel.exception.PortalException {
		return getService().getPersistedModel(primaryKeyObj);
	}

	public static com.liferay.multi.factor.authentication.otp.model.HOTP resetFailedAttempts(
		long hotpId) throws com.liferay.portal.kernel.exception.PortalException {
		return getService().resetFailedAttempts(hotpId);
	}

	public static com.liferay.multi.factor.authentication.otp.model.HOTP resync(
		long hotpId, int increment)
		throws com.liferay.portal.kernel.exception.PortalException {
		return getService().resync(hotpId, increment);
	}

	/**
	* Updates the hotp in the database or adds it if it does not yet exist. Also notifies the appropriate model listeners.
	*
	* @param hotp the hotp
	* @return the hotp that was updated
	*/
	public static com.liferay.multi.factor.authentication.otp.model.HOTP updateHOTP(
		com.liferay.multi.factor.authentication.otp.model.HOTP hotp) {
		return getService().updateHOTP(hotp);
	}

	public static com.liferay.multi.factor.authentication.otp.model.HOTP updateVerified(
		long hotpId, boolean verified)
		throws com.liferay.portal.kernel.exception.PortalException {
		return getService().updateVerified(hotpId, verified);
	}

	public static HOTPLocalService getService() {
		return _serviceTracker.getService();
	}

	private static ServiceTracker<HOTPLocalService, HOTPLocalService> _serviceTracker;

	static {
		Bundle bundle = FrameworkUtil.getBundle(HOTPLocalService.class);

		ServiceTracker<HOTPLocalService, HOTPLocalService> serviceTracker = new ServiceTracker<HOTPLocalService, HOTPLocalService>(bundle.getBundleContext(),
				HOTPLocalService.class, null);

		serviceTracker.open();

		_serviceTracker = serviceTracker;
	}
}