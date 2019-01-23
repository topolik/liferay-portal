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

package com.bemis.portal.twofa.device.manager.service;

import aQute.bnd.annotation.ProviderType;

import org.osgi.framework.Bundle;
import org.osgi.framework.FrameworkUtil;

import org.osgi.util.tracker.ServiceTracker;

/**
 * Provides the local service utility for DeviceCode. This utility wraps
 * {@link com.bemis.portal.twofa.device.manager.service.impl.DeviceCodeLocalServiceImpl} and is the
 * primary access point for service operations in application layer code running
 * on the local server. Methods of this service will not have security checks
 * based on the propagated JAAS credentials because this service can only be
 * accessed from within the same VM.
 *
 * @author Prathima Shreenath
 * @see DeviceCodeLocalService
 * @see com.bemis.portal.twofa.device.manager.service.base.DeviceCodeLocalServiceBaseImpl
 * @see com.bemis.portal.twofa.device.manager.service.impl.DeviceCodeLocalServiceImpl
 * @generated
 */
@ProviderType
public class DeviceCodeLocalServiceUtil {
	/*
	 * NOTE FOR DEVELOPERS:
	 *
	 * Never modify this class directly. Add custom service methods to {@link com.bemis.portal.twofa.device.manager.service.impl.DeviceCodeLocalServiceImpl} and rerun ServiceBuilder to regenerate this class.
	 */

	/**
	* Adds the device code to the database. Also notifies the appropriate model listeners.
	*
	* @param deviceCode the device code
	* @return the device code that was added
	*/
	public static com.bemis.portal.twofa.device.manager.model.DeviceCode addDeviceCode(
		com.bemis.portal.twofa.device.manager.model.DeviceCode deviceCode) {
		return getService().addDeviceCode(deviceCode);
	}

	/**
	* Creates a new device code with the primary key. Does not add the device code to the database.
	*
	* @param deviceCodeId the primary key for the new device code
	* @return the new device code
	*/
	public static com.bemis.portal.twofa.device.manager.model.DeviceCode createDeviceCode(
		long deviceCodeId) {
		return getService().createDeviceCode(deviceCodeId);
	}

	/**
	* Deletes the device code from the database. Also notifies the appropriate model listeners.
	*
	* @param deviceCode the device code
	* @return the device code that was removed
	*/
	public static com.bemis.portal.twofa.device.manager.model.DeviceCode deleteDeviceCode(
		com.bemis.portal.twofa.device.manager.model.DeviceCode deviceCode) {
		return getService().deleteDeviceCode(deviceCode);
	}

	/**
	* Deletes the device code with the primary key from the database. Also notifies the appropriate model listeners.
	*
	* @param deviceCodeId the primary key of the device code
	* @return the device code that was removed
	* @throws PortalException if a device code with the primary key could not be found
	*/
	public static com.bemis.portal.twofa.device.manager.model.DeviceCode deleteDeviceCode(
		long deviceCodeId)
		throws com.liferay.portal.kernel.exception.PortalException {
		return getService().deleteDeviceCode(deviceCodeId);
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
	* Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to {@link com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS} will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent and pagination is required (<code>start</code> and <code>end</code> are not {@link com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS}), then the query will include the default ORDER BY logic from {@link com.bemis.portal.twofa.device.manager.model.impl.DeviceCodeModelImpl}. If both <code>orderByComparator</code> and pagination are absent, for performance reasons, the query will not have an ORDER BY clause and the returned result set will be sorted on by the primary key in an ascending order.
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
	* Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to {@link com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS} will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent and pagination is required (<code>start</code> and <code>end</code> are not {@link com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS}), then the query will include the default ORDER BY logic from {@link com.bemis.portal.twofa.device.manager.model.impl.DeviceCodeModelImpl}. If both <code>orderByComparator</code> and pagination are absent, for performance reasons, the query will not have an ORDER BY clause and the returned result set will be sorted on by the primary key in an ascending order.
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

	public static com.bemis.portal.twofa.device.manager.model.DeviceCode fetchDeviceCode(
		long deviceCodeId) {
		return getService().fetchDeviceCode(deviceCodeId);
	}

	public static com.bemis.portal.twofa.device.manager.model.DeviceCode fetchDeviceCodeByPortalUserId(
		long userId) {
		return getService().fetchDeviceCodeByPortalUserId(userId);
	}

	public static com.liferay.portal.kernel.dao.orm.ActionableDynamicQuery getActionableDynamicQuery() {
		return getService().getActionableDynamicQuery();
	}

	/**
	* Returns the device code with the primary key.
	*
	* @param deviceCodeId the primary key of the device code
	* @return the device code
	* @throws PortalException if a device code with the primary key could not be found
	*/
	public static com.bemis.portal.twofa.device.manager.model.DeviceCode getDeviceCode(
		long deviceCodeId)
		throws com.liferay.portal.kernel.exception.PortalException {
		return getService().getDeviceCode(deviceCodeId);
	}

	/**
	* Returns a range of all the device codes.
	*
	* <p>
	* Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to {@link com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS} will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent and pagination is required (<code>start</code> and <code>end</code> are not {@link com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS}), then the query will include the default ORDER BY logic from {@link com.bemis.portal.twofa.device.manager.model.impl.DeviceCodeModelImpl}. If both <code>orderByComparator</code> and pagination are absent, for performance reasons, the query will not have an ORDER BY clause and the returned result set will be sorted on by the primary key in an ascending order.
	* </p>
	*
	* @param start the lower bound of the range of device codes
	* @param end the upper bound of the range of device codes (not inclusive)
	* @return the range of device codes
	*/
	public static java.util.List<com.bemis.portal.twofa.device.manager.model.DeviceCode> getDeviceCodes(
		int start, int end) {
		return getService().getDeviceCodes(start, end);
	}

	/**
	* Returns the number of device codes.
	*
	* @return the number of device codes
	*/
	public static int getDeviceCodesCount() {
		return getService().getDeviceCodesCount();
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

	public static String getVerificationURL(long portalUserId,
		String verificationBaseURL) {
		return getService().getVerificationURL(portalUserId, verificationBaseURL);
	}

	public static void removeDeviceCode(long userId) {
		getService().removeDeviceCode(userId);
	}

	public static void storeDeviceCodeAndSendNotification(long portalUserId,
		String deviceIP, String secretKey, int validationCode,
		String verificationBaseURL)
		throws com.liferay.portal.kernel.exception.PortalException {
		getService()
			.storeDeviceCodeAndSendNotification(portalUserId, deviceIP,
			secretKey, validationCode, verificationBaseURL);
	}

	/**
	* Updates the device code in the database or adds it if it does not yet exist. Also notifies the appropriate model listeners.
	*
	* @param deviceCode the device code
	* @return the device code that was updated
	*/
	public static com.bemis.portal.twofa.device.manager.model.DeviceCode updateDeviceCode(
		com.bemis.portal.twofa.device.manager.model.DeviceCode deviceCode) {
		return getService().updateDeviceCode(deviceCode);
	}

	public static DeviceCodeLocalService getService() {
		return _serviceTracker.getService();
	}

	private static ServiceTracker<DeviceCodeLocalService, DeviceCodeLocalService> _serviceTracker;

	static {
		Bundle bundle = FrameworkUtil.getBundle(DeviceCodeLocalService.class);

		ServiceTracker<DeviceCodeLocalService, DeviceCodeLocalService> serviceTracker =
			new ServiceTracker<DeviceCodeLocalService, DeviceCodeLocalService>(bundle.getBundleContext(),
				DeviceCodeLocalService.class, null);

		serviceTracker.open();

		_serviceTracker = serviceTracker;
	}
}