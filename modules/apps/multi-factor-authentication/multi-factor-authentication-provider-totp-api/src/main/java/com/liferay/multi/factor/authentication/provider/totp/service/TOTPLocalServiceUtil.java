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

import org.osgi.framework.Bundle;
import org.osgi.framework.FrameworkUtil;

import org.osgi.util.tracker.ServiceTracker;

/**
 * Provides the local service utility for TOTP. This utility wraps
 * {@link com.liferay.multi.factor.authentication.provider.totp.service.impl.TOTPLocalServiceImpl} and is the
 * primary access point for service operations in application layer code running
 * on the local server. Methods of this service will not have security checks
 * based on the propagated JAAS credentials because this service can only be
 * accessed from within the same VM.
 *
 * @author arthurchan35
 * @see TOTPLocalService
 * @see com.liferay.multi.factor.authentication.provider.totp.service.base.TOTPLocalServiceBaseImpl
 * @see com.liferay.multi.factor.authentication.provider.totp.service.impl.TOTPLocalServiceImpl
 * @generated
 */
@ProviderType
public class TOTPLocalServiceUtil {
	/*
	 * NOTE FOR DEVELOPERS:
	 *
	 * Never modify this class directly. Add custom service methods to {@link com.liferay.multi.factor.authentication.provider.totp.service.impl.TOTPLocalServiceImpl} and rerun ServiceBuilder to regenerate this class.
	 */
	public static com.liferay.multi.factor.authentication.provider.totp.model.TOTP addTOTP(
		long userId, String sharedSecret)
		throws com.liferay.portal.kernel.exception.PortalException {
		return getService().addTOTP(userId, sharedSecret);
	}

	/**
	* Adds the totp to the database. Also notifies the appropriate model listeners.
	*
	* @param totp the totp
	* @return the totp that was added
	*/
	public static com.liferay.multi.factor.authentication.provider.totp.model.TOTP addTOTP(
		com.liferay.multi.factor.authentication.provider.totp.model.TOTP totp) {
		return getService().addTOTP(totp);
	}

	/**
	* Creates a new totp with the primary key. Does not add the totp to the database.
	*
	* @param totpId the primary key for the new totp
	* @return the new totp
	*/
	public static com.liferay.multi.factor.authentication.provider.totp.model.TOTP createTOTP(
		long totpId) {
		return getService().createTOTP(totpId);
	}

	/**
	* @throws PortalException
	*/
	public static com.liferay.portal.kernel.model.PersistedModel deletePersistedModel(
		com.liferay.portal.kernel.model.PersistedModel persistedModel)
		throws com.liferay.portal.kernel.exception.PortalException {
		return getService().deletePersistedModel(persistedModel);
	}

	/**
	* Deletes the totp with the primary key from the database. Also notifies the appropriate model listeners.
	*
	* @param totpId the primary key of the totp
	* @return the totp that was removed
	* @throws PortalException if a totp with the primary key could not be found
	*/
	public static com.liferay.multi.factor.authentication.provider.totp.model.TOTP deleteTOTP(
		long totpId) throws com.liferay.portal.kernel.exception.PortalException {
		return getService().deleteTOTP(totpId);
	}

	/**
	* Deletes the totp from the database. Also notifies the appropriate model listeners.
	*
	* @param totp the totp
	* @return the totp that was removed
	*/
	public static com.liferay.multi.factor.authentication.provider.totp.model.TOTP deleteTOTP(
		com.liferay.multi.factor.authentication.provider.totp.model.TOTP totp) {
		return getService().deleteTOTP(totp);
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
	* Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to {@link com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS} will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent and pagination is required (<code>start</code> and <code>end</code> are not {@link com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS}), then the query will include the default ORDER BY logic from {@link com.liferay.multi.factor.authentication.provider.totp.model.impl.TOTPModelImpl}. If both <code>orderByComparator</code> and pagination are absent, for performance reasons, the query will not have an ORDER BY clause and the returned result set will be sorted on by the primary key in an ascending order.
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
	* Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to {@link com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS} will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent and pagination is required (<code>start</code> and <code>end</code> are not {@link com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS}), then the query will include the default ORDER BY logic from {@link com.liferay.multi.factor.authentication.provider.totp.model.impl.TOTPModelImpl}. If both <code>orderByComparator</code> and pagination are absent, for performance reasons, the query will not have an ORDER BY clause and the returned result set will be sorted on by the primary key in an ascending order.
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

	public static com.liferay.multi.factor.authentication.provider.totp.model.TOTP fetchTOTP(
		long totpId) {
		return getService().fetchTOTP(totpId);
	}

	public static com.liferay.multi.factor.authentication.provider.totp.model.TOTP fetchTOTPByUserId(
		long userId) {
		return getService().fetchTOTPByUserId(userId);
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

	public static com.liferay.portal.kernel.model.PersistedModel getPersistedModel(
		java.io.Serializable primaryKeyObj)
		throws com.liferay.portal.kernel.exception.PortalException {
		return getService().getPersistedModel(primaryKeyObj);
	}

	/**
	* Returns the totp with the primary key.
	*
	* @param totpId the primary key of the totp
	* @return the totp
	* @throws PortalException if a totp with the primary key could not be found
	*/
	public static com.liferay.multi.factor.authentication.provider.totp.model.TOTP getTOTP(
		long totpId) throws com.liferay.portal.kernel.exception.PortalException {
		return getService().getTOTP(totpId);
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
	public static java.util.List<com.liferay.multi.factor.authentication.provider.totp.model.TOTP> getTOTPs(
		int start, int end) {
		return getService().getTOTPs(start, end);
	}

	/**
	* Returns the number of totps.
	*
	* @return the number of totps
	*/
	public static int getTOTPsCount() {
		return getService().getTOTPsCount();
	}

	/**
	* Updates the totp in the database or adds it if it does not yet exist. Also notifies the appropriate model listeners.
	*
	* @param totp the totp
	* @return the totp that was updated
	*/
	public static com.liferay.multi.factor.authentication.provider.totp.model.TOTP updateTOTP(
		com.liferay.multi.factor.authentication.provider.totp.model.TOTP totp) {
		return getService().updateTOTP(totp);
	}

	public static com.liferay.multi.factor.authentication.provider.totp.model.TOTP updateVerified(
		long totpId, boolean verified)
		throws com.liferay.portal.kernel.exception.PortalException {
		return getService().updateVerified(totpId, verified);
	}

	public static TOTPLocalService getService() {
		return _serviceTracker.getService();
	}

	private static ServiceTracker<TOTPLocalService, TOTPLocalService> _serviceTracker;

	static {
		Bundle bundle = FrameworkUtil.getBundle(TOTPLocalService.class);

		ServiceTracker<TOTPLocalService, TOTPLocalService> serviceTracker = new ServiceTracker<TOTPLocalService, TOTPLocalService>(bundle.getBundleContext(),
				TOTPLocalService.class, null);

		serviceTracker.open();

		_serviceTracker = serviceTracker;
	}
}