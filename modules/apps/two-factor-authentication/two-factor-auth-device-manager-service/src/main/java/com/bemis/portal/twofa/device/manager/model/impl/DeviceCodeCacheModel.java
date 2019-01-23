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

package com.bemis.portal.twofa.device.manager.model.impl;

import aQute.bnd.annotation.ProviderType;

import com.bemis.portal.twofa.device.manager.model.DeviceCode;

import com.liferay.portal.kernel.model.CacheModel;
import com.liferay.portal.kernel.util.HashUtil;
import com.liferay.portal.kernel.util.StringBundler;

import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;

import java.util.Date;

/**
 * The cache model class for representing DeviceCode in entity cache.
 *
 * @author Prathima Shreenath
 * @see DeviceCode
 * @generated
 */
@ProviderType
public class DeviceCodeCacheModel implements CacheModel<DeviceCode>,
	Externalizable {
	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}

		if (!(obj instanceof DeviceCodeCacheModel)) {
			return false;
		}

		DeviceCodeCacheModel deviceCodeCacheModel = (DeviceCodeCacheModel)obj;

		if (deviceCodeId == deviceCodeCacheModel.deviceCodeId) {
			return true;
		}

		return false;
	}

	@Override
	public int hashCode() {
		return HashUtil.hash(0, deviceCodeId);
	}

	@Override
	public String toString() {
		StringBundler sb = new StringBundler(27);

		sb.append("{deviceCodeId=");
		sb.append(deviceCodeId);
		sb.append(", groupId=");
		sb.append(groupId);
		sb.append(", companyId=");
		sb.append(companyId);
		sb.append(", userId=");
		sb.append(userId);
		sb.append(", userName=");
		sb.append(userName);
		sb.append(", createDate=");
		sb.append(createDate);
		sb.append(", modifiedDate=");
		sb.append(modifiedDate);
		sb.append(", portalUserId=");
		sb.append(portalUserId);
		sb.append(", portalUserName=");
		sb.append(portalUserName);
		sb.append(", emailAddress=");
		sb.append(emailAddress);
		sb.append(", deviceCode=");
		sb.append(deviceCode);
		sb.append(", deviceIP=");
		sb.append(deviceIP);
		sb.append(", validationCode=");
		sb.append(validationCode);
		sb.append("}");

		return sb.toString();
	}

	@Override
	public DeviceCode toEntityModel() {
		DeviceCodeImpl deviceCodeImpl = new DeviceCodeImpl();

		deviceCodeImpl.setDeviceCodeId(deviceCodeId);
		deviceCodeImpl.setGroupId(groupId);
		deviceCodeImpl.setCompanyId(companyId);
		deviceCodeImpl.setUserId(userId);

		if (userName == null) {
			deviceCodeImpl.setUserName("");
		}
		else {
			deviceCodeImpl.setUserName(userName);
		}

		if (createDate == Long.MIN_VALUE) {
			deviceCodeImpl.setCreateDate(null);
		}
		else {
			deviceCodeImpl.setCreateDate(new Date(createDate));
		}

		if (modifiedDate == Long.MIN_VALUE) {
			deviceCodeImpl.setModifiedDate(null);
		}
		else {
			deviceCodeImpl.setModifiedDate(new Date(modifiedDate));
		}

		deviceCodeImpl.setPortalUserId(portalUserId);

		if (portalUserName == null) {
			deviceCodeImpl.setPortalUserName("");
		}
		else {
			deviceCodeImpl.setPortalUserName(portalUserName);
		}

		if (emailAddress == null) {
			deviceCodeImpl.setEmailAddress("");
		}
		else {
			deviceCodeImpl.setEmailAddress(emailAddress);
		}

		if (deviceCode == null) {
			deviceCodeImpl.setDeviceCode("");
		}
		else {
			deviceCodeImpl.setDeviceCode(deviceCode);
		}

		if (deviceIP == null) {
			deviceCodeImpl.setDeviceIP("");
		}
		else {
			deviceCodeImpl.setDeviceIP(deviceIP);
		}

		deviceCodeImpl.setValidationCode(validationCode);

		deviceCodeImpl.resetOriginalValues();

		return deviceCodeImpl;
	}

	@Override
	public void readExternal(ObjectInput objectInput) throws IOException {
		deviceCodeId = objectInput.readLong();

		groupId = objectInput.readLong();

		companyId = objectInput.readLong();

		userId = objectInput.readLong();
		userName = objectInput.readUTF();
		createDate = objectInput.readLong();
		modifiedDate = objectInput.readLong();

		portalUserId = objectInput.readLong();
		portalUserName = objectInput.readUTF();
		emailAddress = objectInput.readUTF();
		deviceCode = objectInput.readUTF();
		deviceIP = objectInput.readUTF();

		validationCode = objectInput.readInt();
	}

	@Override
	public void writeExternal(ObjectOutput objectOutput)
		throws IOException {
		objectOutput.writeLong(deviceCodeId);

		objectOutput.writeLong(groupId);

		objectOutput.writeLong(companyId);

		objectOutput.writeLong(userId);

		if (userName == null) {
			objectOutput.writeUTF("");
		}
		else {
			objectOutput.writeUTF(userName);
		}

		objectOutput.writeLong(createDate);
		objectOutput.writeLong(modifiedDate);

		objectOutput.writeLong(portalUserId);

		if (portalUserName == null) {
			objectOutput.writeUTF("");
		}
		else {
			objectOutput.writeUTF(portalUserName);
		}

		if (emailAddress == null) {
			objectOutput.writeUTF("");
		}
		else {
			objectOutput.writeUTF(emailAddress);
		}

		if (deviceCode == null) {
			objectOutput.writeUTF("");
		}
		else {
			objectOutput.writeUTF(deviceCode);
		}

		if (deviceIP == null) {
			objectOutput.writeUTF("");
		}
		else {
			objectOutput.writeUTF(deviceIP);
		}

		objectOutput.writeInt(validationCode);
	}

	public long deviceCodeId;
	public long groupId;
	public long companyId;
	public long userId;
	public String userName;
	public long createDate;
	public long modifiedDate;
	public long portalUserId;
	public String portalUserName;
	public String emailAddress;
	public String deviceCode;
	public String deviceIP;
	public int validationCode;
}