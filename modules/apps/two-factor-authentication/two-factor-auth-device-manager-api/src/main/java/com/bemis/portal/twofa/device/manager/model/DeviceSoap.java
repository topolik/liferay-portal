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

import java.io.Serializable;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * This class is used by SOAP remote services.
 *
 * @author Prathima Shreenath
 * @generated
 */
@ProviderType
public class DeviceSoap implements Serializable {
	public static DeviceSoap toSoapModel(Device model) {
		DeviceSoap soapModel = new DeviceSoap();

		soapModel.setDeviceId(model.getDeviceId());
		soapModel.setGroupId(model.getGroupId());
		soapModel.setCompanyId(model.getCompanyId());
		soapModel.setUserId(model.getUserId());
		soapModel.setUserName(model.getUserName());
		soapModel.setCreateDate(model.getCreateDate());
		soapModel.setModifiedDate(model.getModifiedDate());
		soapModel.setPortalUserId(model.getPortalUserId());
		soapModel.setPortalUserName(model.getPortalUserName());
		soapModel.setEmailAddress(model.getEmailAddress());
		soapModel.setDeviceIP(model.getDeviceIP());
		soapModel.setBrowserName(model.getBrowserName());
		soapModel.setOsName(model.getOsName());
		soapModel.setVerified(model.isVerified());
		soapModel.setTempDevice(model.isTempDevice());

		return soapModel;
	}

	public static DeviceSoap[] toSoapModels(Device[] models) {
		DeviceSoap[] soapModels = new DeviceSoap[models.length];

		for (int i = 0; i < models.length; i++) {
			soapModels[i] = toSoapModel(models[i]);
		}

		return soapModels;
	}

	public static DeviceSoap[][] toSoapModels(Device[][] models) {
		DeviceSoap[][] soapModels = null;

		if (models.length > 0) {
			soapModels = new DeviceSoap[models.length][models[0].length];
		}
		else {
			soapModels = new DeviceSoap[0][0];
		}

		for (int i = 0; i < models.length; i++) {
			soapModels[i] = toSoapModels(models[i]);
		}

		return soapModels;
	}

	public static DeviceSoap[] toSoapModels(List<Device> models) {
		List<DeviceSoap> soapModels = new ArrayList<DeviceSoap>(models.size());

		for (Device model : models) {
			soapModels.add(toSoapModel(model));
		}

		return soapModels.toArray(new DeviceSoap[soapModels.size()]);
	}

	public DeviceSoap() {
	}

	public long getPrimaryKey() {
		return _deviceId;
	}

	public void setPrimaryKey(long pk) {
		setDeviceId(pk);
	}

	public long getDeviceId() {
		return _deviceId;
	}

	public void setDeviceId(long deviceId) {
		_deviceId = deviceId;
	}

	public long getGroupId() {
		return _groupId;
	}

	public void setGroupId(long groupId) {
		_groupId = groupId;
	}

	public long getCompanyId() {
		return _companyId;
	}

	public void setCompanyId(long companyId) {
		_companyId = companyId;
	}

	public long getUserId() {
		return _userId;
	}

	public void setUserId(long userId) {
		_userId = userId;
	}

	public String getUserName() {
		return _userName;
	}

	public void setUserName(String userName) {
		_userName = userName;
	}

	public Date getCreateDate() {
		return _createDate;
	}

	public void setCreateDate(Date createDate) {
		_createDate = createDate;
	}

	public Date getModifiedDate() {
		return _modifiedDate;
	}

	public void setModifiedDate(Date modifiedDate) {
		_modifiedDate = modifiedDate;
	}

	public long getPortalUserId() {
		return _portalUserId;
	}

	public void setPortalUserId(long portalUserId) {
		_portalUserId = portalUserId;
	}

	public String getPortalUserName() {
		return _portalUserName;
	}

	public void setPortalUserName(String portalUserName) {
		_portalUserName = portalUserName;
	}

	public String getEmailAddress() {
		return _emailAddress;
	}

	public void setEmailAddress(String emailAddress) {
		_emailAddress = emailAddress;
	}

	public String getDeviceIP() {
		return _deviceIP;
	}

	public void setDeviceIP(String deviceIP) {
		_deviceIP = deviceIP;
	}

	public String getBrowserName() {
		return _browserName;
	}

	public void setBrowserName(String browserName) {
		_browserName = browserName;
	}

	public String getOsName() {
		return _osName;
	}

	public void setOsName(String osName) {
		_osName = osName;
	}

	public boolean getVerified() {
		return _verified;
	}

	public boolean isVerified() {
		return _verified;
	}

	public void setVerified(boolean verified) {
		_verified = verified;
	}

	public boolean getTempDevice() {
		return _tempDevice;
	}

	public boolean isTempDevice() {
		return _tempDevice;
	}

	public void setTempDevice(boolean tempDevice) {
		_tempDevice = tempDevice;
	}

	private long _deviceId;
	private long _groupId;
	private long _companyId;
	private long _userId;
	private String _userName;
	private Date _createDate;
	private Date _modifiedDate;
	private long _portalUserId;
	private String _portalUserName;
	private String _emailAddress;
	private String _deviceIP;
	private String _browserName;
	private String _osName;
	private boolean _verified;
	private boolean _tempDevice;
}