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

package com.bemis.portal.twofa.device.manager.model;

import aQute.bnd.annotation.ProviderType;

import com.liferay.expando.kernel.model.ExpandoBridge;

import com.liferay.portal.kernel.model.ModelWrapper;
import com.liferay.portal.kernel.service.ServiceContext;

import java.io.Serializable;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

/**
 * <p>
 * This class is a wrapper for {@link Device}.
 * </p>
 *
 * @author Prathima Shreenath
 * @see Device
 * @generated
 */
@ProviderType
public class DeviceWrapper implements Device, ModelWrapper<Device> {
	public DeviceWrapper(Device device) {
		_device = device;
	}

	@Override
	public Class<?> getModelClass() {
		return Device.class;
	}

	@Override
	public String getModelClassName() {
		return Device.class.getName();
	}

	@Override
	public Map<String, Object> getModelAttributes() {
		Map<String, Object> attributes = new HashMap<String, Object>();

		attributes.put("deviceId", getDeviceId());
		attributes.put("groupId", getGroupId());
		attributes.put("companyId", getCompanyId());
		attributes.put("userId", getUserId());
		attributes.put("userName", getUserName());
		attributes.put("createDate", getCreateDate());
		attributes.put("modifiedDate", getModifiedDate());
		attributes.put("portalUserId", getPortalUserId());
		attributes.put("portalUserName", getPortalUserName());
		attributes.put("emailAddress", getEmailAddress());
		attributes.put("deviceIP", getDeviceIP());
		attributes.put("browserName", getBrowserName());
		attributes.put("osName", getOsName());
		attributes.put("verified", isVerified());
		attributes.put("tempDevice", isTempDevice());

		return attributes;
	}

	@Override
	public void setModelAttributes(Map<String, Object> attributes) {
		Long deviceId = (Long)attributes.get("deviceId");

		if (deviceId != null) {
			setDeviceId(deviceId);
		}

		Long groupId = (Long)attributes.get("groupId");

		if (groupId != null) {
			setGroupId(groupId);
		}

		Long companyId = (Long)attributes.get("companyId");

		if (companyId != null) {
			setCompanyId(companyId);
		}

		Long userId = (Long)attributes.get("userId");

		if (userId != null) {
			setUserId(userId);
		}

		String userName = (String)attributes.get("userName");

		if (userName != null) {
			setUserName(userName);
		}

		Date createDate = (Date)attributes.get("createDate");

		if (createDate != null) {
			setCreateDate(createDate);
		}

		Date modifiedDate = (Date)attributes.get("modifiedDate");

		if (modifiedDate != null) {
			setModifiedDate(modifiedDate);
		}

		Long portalUserId = (Long)attributes.get("portalUserId");

		if (portalUserId != null) {
			setPortalUserId(portalUserId);
		}

		String portalUserName = (String)attributes.get("portalUserName");

		if (portalUserName != null) {
			setPortalUserName(portalUserName);
		}

		String emailAddress = (String)attributes.get("emailAddress");

		if (emailAddress != null) {
			setEmailAddress(emailAddress);
		}

		String deviceIP = (String)attributes.get("deviceIP");

		if (deviceIP != null) {
			setDeviceIP(deviceIP);
		}

		String browserName = (String)attributes.get("browserName");

		if (browserName != null) {
			setBrowserName(browserName);
		}

		String osName = (String)attributes.get("osName");

		if (osName != null) {
			setOsName(osName);
		}

		Boolean verified = (Boolean)attributes.get("verified");

		if (verified != null) {
			setVerified(verified);
		}

		Boolean tempDevice = (Boolean)attributes.get("tempDevice");

		if (tempDevice != null) {
			setTempDevice(tempDevice);
		}
	}

	@Override
	public Object clone() {
		return new DeviceWrapper((Device)_device.clone());
	}

	@Override
	public int compareTo(Device device) {
		return _device.compareTo(device);
	}

	/**
	* Returns the browser name of this device.
	*
	* @return the browser name of this device
	*/
	@Override
	public String getBrowserName() {
		return _device.getBrowserName();
	}

	/**
	* Returns the company ID of this device.
	*
	* @return the company ID of this device
	*/
	@Override
	public long getCompanyId() {
		return _device.getCompanyId();
	}

	/**
	* Returns the create date of this device.
	*
	* @return the create date of this device
	*/
	@Override
	public Date getCreateDate() {
		return _device.getCreateDate();
	}

	/**
	* Returns the device ID of this device.
	*
	* @return the device ID of this device
	*/
	@Override
	public long getDeviceId() {
		return _device.getDeviceId();
	}

	/**
	* Returns the device ip of this device.
	*
	* @return the device ip of this device
	*/
	@Override
	public String getDeviceIP() {
		return _device.getDeviceIP();
	}

	/**
	* Returns the email address of this device.
	*
	* @return the email address of this device
	*/
	@Override
	public String getEmailAddress() {
		return _device.getEmailAddress();
	}

	@Override
	public ExpandoBridge getExpandoBridge() {
		return _device.getExpandoBridge();
	}

	/**
	* Returns the group ID of this device.
	*
	* @return the group ID of this device
	*/
	@Override
	public long getGroupId() {
		return _device.getGroupId();
	}

	/**
	* Returns the modified date of this device.
	*
	* @return the modified date of this device
	*/
	@Override
	public Date getModifiedDate() {
		return _device.getModifiedDate();
	}

	/**
	* Returns the os name of this device.
	*
	* @return the os name of this device
	*/
	@Override
	public String getOsName() {
		return _device.getOsName();
	}

	/**
	* Returns the portal user ID of this device.
	*
	* @return the portal user ID of this device
	*/
	@Override
	public long getPortalUserId() {
		return _device.getPortalUserId();
	}

	/**
	* Returns the portal user name of this device.
	*
	* @return the portal user name of this device
	*/
	@Override
	public String getPortalUserName() {
		return _device.getPortalUserName();
	}

	/**
	* Returns the portal user uuid of this device.
	*
	* @return the portal user uuid of this device
	*/
	@Override
	public String getPortalUserUuid() {
		return _device.getPortalUserUuid();
	}

	/**
	* Returns the primary key of this device.
	*
	* @return the primary key of this device
	*/
	@Override
	public long getPrimaryKey() {
		return _device.getPrimaryKey();
	}

	@Override
	public Serializable getPrimaryKeyObj() {
		return _device.getPrimaryKeyObj();
	}

	/**
	* Returns the temp device of this device.
	*
	* @return the temp device of this device
	*/
	@Override
	public boolean getTempDevice() {
		return _device.getTempDevice();
	}

	/**
	* Returns the user ID of this device.
	*
	* @return the user ID of this device
	*/
	@Override
	public long getUserId() {
		return _device.getUserId();
	}

	/**
	* Returns the user name of this device.
	*
	* @return the user name of this device
	*/
	@Override
	public String getUserName() {
		return _device.getUserName();
	}

	/**
	* Returns the user uuid of this device.
	*
	* @return the user uuid of this device
	*/
	@Override
	public String getUserUuid() {
		return _device.getUserUuid();
	}

	/**
	* Returns the verified of this device.
	*
	* @return the verified of this device
	*/
	@Override
	public boolean getVerified() {
		return _device.getVerified();
	}

	@Override
	public int hashCode() {
		return _device.hashCode();
	}

	@Override
	public boolean isCachedModel() {
		return _device.isCachedModel();
	}

	@Override
	public boolean isEscapedModel() {
		return _device.isEscapedModel();
	}

	@Override
	public boolean isNew() {
		return _device.isNew();
	}

	/**
	* Returns <code>true</code> if this device is temp device.
	*
	* @return <code>true</code> if this device is temp device; <code>false</code> otherwise
	*/
	@Override
	public boolean isTempDevice() {
		return _device.isTempDevice();
	}

	/**
	* Returns <code>true</code> if this device is verified.
	*
	* @return <code>true</code> if this device is verified; <code>false</code> otherwise
	*/
	@Override
	public boolean isVerified() {
		return _device.isVerified();
	}

	@Override
	public void persist() {
		_device.persist();
	}

	/**
	* Sets the browser name of this device.
	*
	* @param browserName the browser name of this device
	*/
	@Override
	public void setBrowserName(String browserName) {
		_device.setBrowserName(browserName);
	}

	@Override
	public void setCachedModel(boolean cachedModel) {
		_device.setCachedModel(cachedModel);
	}

	/**
	* Sets the company ID of this device.
	*
	* @param companyId the company ID of this device
	*/
	@Override
	public void setCompanyId(long companyId) {
		_device.setCompanyId(companyId);
	}

	/**
	* Sets the create date of this device.
	*
	* @param createDate the create date of this device
	*/
	@Override
	public void setCreateDate(Date createDate) {
		_device.setCreateDate(createDate);
	}

	/**
	* Sets the device ID of this device.
	*
	* @param deviceId the device ID of this device
	*/
	@Override
	public void setDeviceId(long deviceId) {
		_device.setDeviceId(deviceId);
	}

	/**
	* Sets the device ip of this device.
	*
	* @param deviceIP the device ip of this device
	*/
	@Override
	public void setDeviceIP(String deviceIP) {
		_device.setDeviceIP(deviceIP);
	}

	/**
	* Sets the email address of this device.
	*
	* @param emailAddress the email address of this device
	*/
	@Override
	public void setEmailAddress(String emailAddress) {
		_device.setEmailAddress(emailAddress);
	}

	@Override
	public void setExpandoBridgeAttributes(
		com.liferay.portal.kernel.model.BaseModel<?> baseModel) {
		_device.setExpandoBridgeAttributes(baseModel);
	}

	@Override
	public void setExpandoBridgeAttributes(ExpandoBridge expandoBridge) {
		_device.setExpandoBridgeAttributes(expandoBridge);
	}

	@Override
	public void setExpandoBridgeAttributes(ServiceContext serviceContext) {
		_device.setExpandoBridgeAttributes(serviceContext);
	}

	/**
	* Sets the group ID of this device.
	*
	* @param groupId the group ID of this device
	*/
	@Override
	public void setGroupId(long groupId) {
		_device.setGroupId(groupId);
	}

	/**
	* Sets the modified date of this device.
	*
	* @param modifiedDate the modified date of this device
	*/
	@Override
	public void setModifiedDate(Date modifiedDate) {
		_device.setModifiedDate(modifiedDate);
	}

	@Override
	public void setNew(boolean n) {
		_device.setNew(n);
	}

	/**
	* Sets the os name of this device.
	*
	* @param osName the os name of this device
	*/
	@Override
	public void setOsName(String osName) {
		_device.setOsName(osName);
	}

	/**
	* Sets the portal user ID of this device.
	*
	* @param portalUserId the portal user ID of this device
	*/
	@Override
	public void setPortalUserId(long portalUserId) {
		_device.setPortalUserId(portalUserId);
	}

	/**
	* Sets the portal user name of this device.
	*
	* @param portalUserName the portal user name of this device
	*/
	@Override
	public void setPortalUserName(String portalUserName) {
		_device.setPortalUserName(portalUserName);
	}

	/**
	* Sets the portal user uuid of this device.
	*
	* @param portalUserUuid the portal user uuid of this device
	*/
	@Override
	public void setPortalUserUuid(String portalUserUuid) {
		_device.setPortalUserUuid(portalUserUuid);
	}

	/**
	* Sets the primary key of this device.
	*
	* @param primaryKey the primary key of this device
	*/
	@Override
	public void setPrimaryKey(long primaryKey) {
		_device.setPrimaryKey(primaryKey);
	}

	@Override
	public void setPrimaryKeyObj(Serializable primaryKeyObj) {
		_device.setPrimaryKeyObj(primaryKeyObj);
	}

	/**
	* Sets whether this device is temp device.
	*
	* @param tempDevice the temp device of this device
	*/
	@Override
	public void setTempDevice(boolean tempDevice) {
		_device.setTempDevice(tempDevice);
	}

	/**
	* Sets the user ID of this device.
	*
	* @param userId the user ID of this device
	*/
	@Override
	public void setUserId(long userId) {
		_device.setUserId(userId);
	}

	/**
	* Sets the user name of this device.
	*
	* @param userName the user name of this device
	*/
	@Override
	public void setUserName(String userName) {
		_device.setUserName(userName);
	}

	/**
	* Sets the user uuid of this device.
	*
	* @param userUuid the user uuid of this device
	*/
	@Override
	public void setUserUuid(String userUuid) {
		_device.setUserUuid(userUuid);
	}

	/**
	* Sets whether this device is verified.
	*
	* @param verified the verified of this device
	*/
	@Override
	public void setVerified(boolean verified) {
		_device.setVerified(verified);
	}

	@Override
	public com.liferay.portal.kernel.model.CacheModel<Device> toCacheModel() {
		return _device.toCacheModel();
	}

	@Override
	public Device toEscapedModel() {
		return new DeviceWrapper(_device.toEscapedModel());
	}

	@Override
	public String toString() {
		return _device.toString();
	}

	@Override
	public Device toUnescapedModel() {
		return new DeviceWrapper(_device.toUnescapedModel());
	}

	@Override
	public String toXmlString() {
		return _device.toXmlString();
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}

		if (!(obj instanceof DeviceWrapper)) {
			return false;
		}

		DeviceWrapper deviceWrapper = (DeviceWrapper)obj;

		if (Objects.equals(_device, deviceWrapper._device)) {
			return true;
		}

		return false;
	}

	@Override
	public Device getWrappedModel() {
		return _device;
	}

	@Override
	public boolean isEntityCacheEnabled() {
		return _device.isEntityCacheEnabled();
	}

	@Override
	public boolean isFinderCacheEnabled() {
		return _device.isFinderCacheEnabled();
	}

	@Override
	public void resetOriginalValues() {
		_device.resetOriginalValues();
	}

	private final Device _device;
}