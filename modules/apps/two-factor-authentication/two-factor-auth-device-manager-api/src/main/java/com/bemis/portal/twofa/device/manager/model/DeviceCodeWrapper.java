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
 * This class is a wrapper for {@link DeviceCode}.
 * </p>
 *
 * @author Prathima Shreenath
 * @see DeviceCode
 * @generated
 */
@ProviderType
public class DeviceCodeWrapper implements DeviceCode, ModelWrapper<DeviceCode> {
	public DeviceCodeWrapper(DeviceCode deviceCode) {
		_deviceCode = deviceCode;
	}

	@Override
	public Class<?> getModelClass() {
		return DeviceCode.class;
	}

	@Override
	public String getModelClassName() {
		return DeviceCode.class.getName();
	}

	@Override
	public Map<String, Object> getModelAttributes() {
		Map<String, Object> attributes = new HashMap<String, Object>();

		attributes.put("deviceCodeId", getDeviceCodeId());
		attributes.put("groupId", getGroupId());
		attributes.put("companyId", getCompanyId());
		attributes.put("userId", getUserId());
		attributes.put("userName", getUserName());
		attributes.put("createDate", getCreateDate());
		attributes.put("modifiedDate", getModifiedDate());
		attributes.put("portalUserId", getPortalUserId());
		attributes.put("portalUserName", getPortalUserName());
		attributes.put("emailAddress", getEmailAddress());
		attributes.put("deviceCode", getDeviceCode());
		attributes.put("deviceIP", getDeviceIP());
		attributes.put("validationCode", getValidationCode());

		return attributes;
	}

	@Override
	public void setModelAttributes(Map<String, Object> attributes) {
		Long deviceCodeId = (Long)attributes.get("deviceCodeId");

		if (deviceCodeId != null) {
			setDeviceCodeId(deviceCodeId);
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

		String deviceCode = (String)attributes.get("deviceCode");

		if (deviceCode != null) {
			setDeviceCode(deviceCode);
		}

		String deviceIP = (String)attributes.get("deviceIP");

		if (deviceIP != null) {
			setDeviceIP(deviceIP);
		}

		Integer validationCode = (Integer)attributes.get("validationCode");

		if (validationCode != null) {
			setValidationCode(validationCode);
		}
	}

	@Override
	public Object clone() {
		return new DeviceCodeWrapper((DeviceCode)_deviceCode.clone());
	}

	@Override
	public int compareTo(DeviceCode deviceCode) {
		return _deviceCode.compareTo(deviceCode);
	}

	/**
	* Returns the company ID of this device code.
	*
	* @return the company ID of this device code
	*/
	@Override
	public long getCompanyId() {
		return _deviceCode.getCompanyId();
	}

	/**
	* Returns the create date of this device code.
	*
	* @return the create date of this device code
	*/
	@Override
	public Date getCreateDate() {
		return _deviceCode.getCreateDate();
	}

	/**
	* Returns the device code of this device code.
	*
	* @return the device code of this device code
	*/
	@Override
	public String getDeviceCode() {
		return _deviceCode.getDeviceCode();
	}

	/**
	* Returns the device code ID of this device code.
	*
	* @return the device code ID of this device code
	*/
	@Override
	public long getDeviceCodeId() {
		return _deviceCode.getDeviceCodeId();
	}

	/**
	* Returns the device ip of this device code.
	*
	* @return the device ip of this device code
	*/
	@Override
	public String getDeviceIP() {
		return _deviceCode.getDeviceIP();
	}

	/**
	* Returns the email address of this device code.
	*
	* @return the email address of this device code
	*/
	@Override
	public String getEmailAddress() {
		return _deviceCode.getEmailAddress();
	}

	@Override
	public ExpandoBridge getExpandoBridge() {
		return _deviceCode.getExpandoBridge();
	}

	/**
	* Returns the group ID of this device code.
	*
	* @return the group ID of this device code
	*/
	@Override
	public long getGroupId() {
		return _deviceCode.getGroupId();
	}

	/**
	* Returns the modified date of this device code.
	*
	* @return the modified date of this device code
	*/
	@Override
	public Date getModifiedDate() {
		return _deviceCode.getModifiedDate();
	}

	/**
	* Returns the portal user ID of this device code.
	*
	* @return the portal user ID of this device code
	*/
	@Override
	public long getPortalUserId() {
		return _deviceCode.getPortalUserId();
	}

	/**
	* Returns the portal user name of this device code.
	*
	* @return the portal user name of this device code
	*/
	@Override
	public String getPortalUserName() {
		return _deviceCode.getPortalUserName();
	}

	/**
	* Returns the portal user uuid of this device code.
	*
	* @return the portal user uuid of this device code
	*/
	@Override
	public String getPortalUserUuid() {
		return _deviceCode.getPortalUserUuid();
	}

	/**
	* Returns the primary key of this device code.
	*
	* @return the primary key of this device code
	*/
	@Override
	public long getPrimaryKey() {
		return _deviceCode.getPrimaryKey();
	}

	@Override
	public Serializable getPrimaryKeyObj() {
		return _deviceCode.getPrimaryKeyObj();
	}

	/**
	* Returns the user ID of this device code.
	*
	* @return the user ID of this device code
	*/
	@Override
	public long getUserId() {
		return _deviceCode.getUserId();
	}

	/**
	* Returns the user name of this device code.
	*
	* @return the user name of this device code
	*/
	@Override
	public String getUserName() {
		return _deviceCode.getUserName();
	}

	/**
	* Returns the user uuid of this device code.
	*
	* @return the user uuid of this device code
	*/
	@Override
	public String getUserUuid() {
		return _deviceCode.getUserUuid();
	}

	/**
	* Returns the validation code of this device code.
	*
	* @return the validation code of this device code
	*/
	@Override
	public int getValidationCode() {
		return _deviceCode.getValidationCode();
	}

	@Override
	public int hashCode() {
		return _deviceCode.hashCode();
	}

	@Override
	public boolean isCachedModel() {
		return _deviceCode.isCachedModel();
	}

	@Override
	public boolean isEscapedModel() {
		return _deviceCode.isEscapedModel();
	}

	@Override
	public boolean isNew() {
		return _deviceCode.isNew();
	}

	@Override
	public void persist() {
		_deviceCode.persist();
	}

	@Override
	public void setCachedModel(boolean cachedModel) {
		_deviceCode.setCachedModel(cachedModel);
	}

	/**
	* Sets the company ID of this device code.
	*
	* @param companyId the company ID of this device code
	*/
	@Override
	public void setCompanyId(long companyId) {
		_deviceCode.setCompanyId(companyId);
	}

	/**
	* Sets the create date of this device code.
	*
	* @param createDate the create date of this device code
	*/
	@Override
	public void setCreateDate(Date createDate) {
		_deviceCode.setCreateDate(createDate);
	}

	/**
	* Sets the device code of this device code.
	*
	* @param deviceCode the device code of this device code
	*/
	@Override
	public void setDeviceCode(String deviceCode) {
		_deviceCode.setDeviceCode(deviceCode);
	}

	/**
	* Sets the device code ID of this device code.
	*
	* @param deviceCodeId the device code ID of this device code
	*/
	@Override
	public void setDeviceCodeId(long deviceCodeId) {
		_deviceCode.setDeviceCodeId(deviceCodeId);
	}

	/**
	* Sets the device ip of this device code.
	*
	* @param deviceIP the device ip of this device code
	*/
	@Override
	public void setDeviceIP(String deviceIP) {
		_deviceCode.setDeviceIP(deviceIP);
	}

	/**
	* Sets the email address of this device code.
	*
	* @param emailAddress the email address of this device code
	*/
	@Override
	public void setEmailAddress(String emailAddress) {
		_deviceCode.setEmailAddress(emailAddress);
	}

	@Override
	public void setExpandoBridgeAttributes(
		com.liferay.portal.kernel.model.BaseModel<?> baseModel) {
		_deviceCode.setExpandoBridgeAttributes(baseModel);
	}

	@Override
	public void setExpandoBridgeAttributes(ExpandoBridge expandoBridge) {
		_deviceCode.setExpandoBridgeAttributes(expandoBridge);
	}

	@Override
	public void setExpandoBridgeAttributes(ServiceContext serviceContext) {
		_deviceCode.setExpandoBridgeAttributes(serviceContext);
	}

	/**
	* Sets the group ID of this device code.
	*
	* @param groupId the group ID of this device code
	*/
	@Override
	public void setGroupId(long groupId) {
		_deviceCode.setGroupId(groupId);
	}

	/**
	* Sets the modified date of this device code.
	*
	* @param modifiedDate the modified date of this device code
	*/
	@Override
	public void setModifiedDate(Date modifiedDate) {
		_deviceCode.setModifiedDate(modifiedDate);
	}

	@Override
	public void setNew(boolean n) {
		_deviceCode.setNew(n);
	}

	/**
	* Sets the portal user ID of this device code.
	*
	* @param portalUserId the portal user ID of this device code
	*/
	@Override
	public void setPortalUserId(long portalUserId) {
		_deviceCode.setPortalUserId(portalUserId);
	}

	/**
	* Sets the portal user name of this device code.
	*
	* @param portalUserName the portal user name of this device code
	*/
	@Override
	public void setPortalUserName(String portalUserName) {
		_deviceCode.setPortalUserName(portalUserName);
	}

	/**
	* Sets the portal user uuid of this device code.
	*
	* @param portalUserUuid the portal user uuid of this device code
	*/
	@Override
	public void setPortalUserUuid(String portalUserUuid) {
		_deviceCode.setPortalUserUuid(portalUserUuid);
	}

	/**
	* Sets the primary key of this device code.
	*
	* @param primaryKey the primary key of this device code
	*/
	@Override
	public void setPrimaryKey(long primaryKey) {
		_deviceCode.setPrimaryKey(primaryKey);
	}

	@Override
	public void setPrimaryKeyObj(Serializable primaryKeyObj) {
		_deviceCode.setPrimaryKeyObj(primaryKeyObj);
	}

	/**
	* Sets the user ID of this device code.
	*
	* @param userId the user ID of this device code
	*/
	@Override
	public void setUserId(long userId) {
		_deviceCode.setUserId(userId);
	}

	/**
	* Sets the user name of this device code.
	*
	* @param userName the user name of this device code
	*/
	@Override
	public void setUserName(String userName) {
		_deviceCode.setUserName(userName);
	}

	/**
	* Sets the user uuid of this device code.
	*
	* @param userUuid the user uuid of this device code
	*/
	@Override
	public void setUserUuid(String userUuid) {
		_deviceCode.setUserUuid(userUuid);
	}

	/**
	* Sets the validation code of this device code.
	*
	* @param validationCode the validation code of this device code
	*/
	@Override
	public void setValidationCode(int validationCode) {
		_deviceCode.setValidationCode(validationCode);
	}

	@Override
	public com.liferay.portal.kernel.model.CacheModel<DeviceCode> toCacheModel() {
		return _deviceCode.toCacheModel();
	}

	@Override
	public DeviceCode toEscapedModel() {
		return new DeviceCodeWrapper(_deviceCode.toEscapedModel());
	}

	@Override
	public String toString() {
		return _deviceCode.toString();
	}

	@Override
	public DeviceCode toUnescapedModel() {
		return new DeviceCodeWrapper(_deviceCode.toUnescapedModel());
	}

	@Override
	public String toXmlString() {
		return _deviceCode.toXmlString();
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}

		if (!(obj instanceof DeviceCodeWrapper)) {
			return false;
		}

		DeviceCodeWrapper deviceCodeWrapper = (DeviceCodeWrapper)obj;

		if (Objects.equals(_deviceCode, deviceCodeWrapper._deviceCode)) {
			return true;
		}

		return false;
	}

	@Override
	public DeviceCode getWrappedModel() {
		return _deviceCode;
	}

	@Override
	public boolean isEntityCacheEnabled() {
		return _deviceCode.isEntityCacheEnabled();
	}

	@Override
	public boolean isFinderCacheEnabled() {
		return _deviceCode.isFinderCacheEnabled();
	}

	@Override
	public void resetOriginalValues() {
		_deviceCode.resetOriginalValues();
	}

	private final DeviceCode _deviceCode;
}