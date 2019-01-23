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

import com.liferay.portal.kernel.service.ServiceWrapper;

/**
 * Provides a wrapper for {@link DeviceCodeLocalService}.
 *
 * @author Prathima Shreenath
 * @see DeviceCodeLocalService
 * @generated
 */
@ProviderType
public class DeviceCodeLocalServiceWrapper implements DeviceCodeLocalService,
	ServiceWrapper<DeviceCodeLocalService> {
	public DeviceCodeLocalServiceWrapper(
		DeviceCodeLocalService deviceCodeLocalService) {
		_deviceCodeLocalService = deviceCodeLocalService;
	}

	/**
	* Adds the device code to the database. Also notifies the appropriate model listeners.
	*
	* @param deviceCode the device code
	* @return the device code that was added
	*/
	@Override
	public com.bemis.portal.twofa.device.manager.model.DeviceCode addDeviceCode(
		com.bemis.portal.twofa.device.manager.model.DeviceCode deviceCode) {
		return _deviceCodeLocalService.addDeviceCode(deviceCode);
	}

	/**
	* Creates a new device code with the primary key. Does not add the device code to the database.
	*
	* @param deviceCodeId the primary key for the new device code
	* @return the new device code
	*/
	@Override
	public com.bemis.portal.twofa.device.manager.model.DeviceCode createDeviceCode(
		long deviceCodeId) {
		return _deviceCodeLocalService.createDeviceCode(deviceCodeId);
	}

	/**
	* Deletes the device code from the database. Also notifies the appropriate model listeners.
	*
	* @param deviceCode the device code
	* @return the device code that was removed
	*/
	@Override
	public com.bemis.portal.twofa.device.manager.model.DeviceCode deleteDeviceCode(
		com.bemis.portal.twofa.device.manager.model.DeviceCode deviceCode) {
		return _deviceCodeLocalService.deleteDeviceCode(deviceCode);
	}

	/**
	* Deletes the device code with the primary key from the database. Also notifies the appropriate model listeners.
	*
	* @param deviceCodeId the primary key of the device code
	* @return the device code that was removed
	* @throws PortalException if a device code with the primary key could not be found
	*/
	@Override
	public com.bemis.portal.twofa.device.manager.model.DeviceCode deleteDeviceCode(
		long deviceCodeId)
		throws com.liferay.portal.kernel.exception.PortalException {
		return _deviceCodeLocalService.deleteDeviceCode(deviceCodeId);
	}

	@Override
	public com.bemis.portal.twofa.device.manager.model.DeviceCode fetchDeviceCode(
		long deviceCodeId) {
		return _deviceCodeLocalService.fetchDeviceCode(deviceCodeId);
	}

	@Override
	public com.bemis.portal.twofa.device.manager.model.DeviceCode fetchDeviceCodeByPortalUserId(
		long userId) {
		return _deviceCodeLocalService.fetchDeviceCodeByPortalUserId(userId);
	}

	/**
	* Returns the device code with the primary key.
	*
	* @param deviceCodeId the primary key of the device code
	* @return the device code
	* @throws PortalException if a device code with the primary key could not be found
	*/
	@Override
	public com.bemis.portal.twofa.device.manager.model.DeviceCode getDeviceCode(
		long deviceCodeId)
		throws com.liferay.portal.kernel.exception.PortalException {
		return _deviceCodeLocalService.getDeviceCode(deviceCodeId);
	}

	/**
	* Updates the device code in the database or adds it if it does not yet exist. Also notifies the appropriate model listeners.
	*
	* @param deviceCode the device code
	* @return the device code that was updated
	*/
	@Override
	public com.bemis.portal.twofa.device.manager.model.DeviceCode updateDeviceCode(
		com.bemis.portal.twofa.device.manager.model.DeviceCode deviceCode) {
		return _deviceCodeLocalService.updateDeviceCode(deviceCode);
	}

	@Override
	public com.liferay.portal.kernel.dao.orm.ActionableDynamicQuery getActionableDynamicQuery() {
		return _deviceCodeLocalService.getActionableDynamicQuery();
	}

	@Override
	public com.liferay.portal.kernel.dao.orm.DynamicQuery dynamicQuery() {
		return _deviceCodeLocalService.dynamicQuery();
	}

	@Override
	public com.liferay.portal.kernel.dao.orm.IndexableActionableDynamicQuery getIndexableActionableDynamicQuery() {
		return _deviceCodeLocalService.getIndexableActionableDynamicQuery();
	}

	/**
	* @throws PortalException
	*/
	@Override
	public com.liferay.portal.kernel.model.PersistedModel deletePersistedModel(
		com.liferay.portal.kernel.model.PersistedModel persistedModel)
		throws com.liferay.portal.kernel.exception.PortalException {
		return _deviceCodeLocalService.deletePersistedModel(persistedModel);
	}

	@Override
	public com.liferay.portal.kernel.model.PersistedModel getPersistedModel(
		java.io.Serializable primaryKeyObj)
		throws com.liferay.portal.kernel.exception.PortalException {
		return _deviceCodeLocalService.getPersistedModel(primaryKeyObj);
	}

	/**
	* Returns the number of device codes.
	*
	* @return the number of device codes
	*/
	@Override
	public int getDeviceCodesCount() {
		return _deviceCodeLocalService.getDeviceCodesCount();
	}

	/**
	* Returns the OSGi service identifier.
	*
	* @return the OSGi service identifier
	*/
	@Override
	public java.lang.String getOSGiServiceIdentifier() {
		return _deviceCodeLocalService.getOSGiServiceIdentifier();
	}

	@Override
	public java.lang.String getVerificationURL(long portalUserId,
		java.lang.String verificationBaseURL) {
		return _deviceCodeLocalService.getVerificationURL(portalUserId,
			verificationBaseURL);
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
		return _deviceCodeLocalService.dynamicQuery(dynamicQuery);
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
	@Override
	public <T> java.util.List<T> dynamicQuery(
		com.liferay.portal.kernel.dao.orm.DynamicQuery dynamicQuery, int start,
		int end) {
		return _deviceCodeLocalService.dynamicQuery(dynamicQuery, start, end);
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
	@Override
	public <T> java.util.List<T> dynamicQuery(
		com.liferay.portal.kernel.dao.orm.DynamicQuery dynamicQuery, int start,
		int end,
		com.liferay.portal.kernel.util.OrderByComparator<T> orderByComparator) {
		return _deviceCodeLocalService.dynamicQuery(dynamicQuery, start, end,
			orderByComparator);
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
	@Override
	public java.util.List<com.bemis.portal.twofa.device.manager.model.DeviceCode> getDeviceCodes(
		int start, int end) {
		return _deviceCodeLocalService.getDeviceCodes(start, end);
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
		return _deviceCodeLocalService.dynamicQueryCount(dynamicQuery);
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
		return _deviceCodeLocalService.dynamicQueryCount(dynamicQuery,
			projection);
	}

	@Override
	public void removeDeviceCode(long userId) {
		_deviceCodeLocalService.removeDeviceCode(userId);
	}

	@Override
	public void storeDeviceCodeAndSendNotification(long portalUserId,
		java.lang.String deviceIP, java.lang.String secretKey,
		int validationCode, java.lang.String verificationBaseURL)
		throws com.liferay.portal.kernel.exception.PortalException {
		_deviceCodeLocalService.storeDeviceCodeAndSendNotification(portalUserId,
			deviceIP, secretKey, validationCode, verificationBaseURL);
	}

	@Override
	public DeviceCodeLocalService getWrappedService() {
		return _deviceCodeLocalService;
	}

	@Override
	public void setWrappedService(DeviceCodeLocalService deviceCodeLocalService) {
		_deviceCodeLocalService = deviceCodeLocalService;
	}

	private DeviceCodeLocalService _deviceCodeLocalService;
}