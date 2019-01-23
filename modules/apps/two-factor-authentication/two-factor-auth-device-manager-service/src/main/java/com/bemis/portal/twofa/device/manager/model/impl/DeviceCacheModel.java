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

import com.bemis.portal.twofa.device.manager.model.Device;

import com.liferay.portal.kernel.model.CacheModel;
import com.liferay.portal.kernel.util.HashUtil;
import com.liferay.portal.kernel.util.StringBundler;
import com.liferay.portal.kernel.util.StringPool;

import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;

import java.util.Date;

/**
 * The cache model class for representing Device in entity cache.
 *
 * @author Prathima Shreenath
 * @see Device
 * @generated
 */
@ProviderType
public class DeviceCacheModel implements CacheModel<Device>, Externalizable {
	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}

		if (!(obj instanceof DeviceCacheModel)) {
			return false;
		}

		DeviceCacheModel deviceCacheModel = (DeviceCacheModel)obj;

		if (deviceId == deviceCacheModel.deviceId) {
			return true;
		}

		return false;
	}

	@Override
	public int hashCode() {
		return HashUtil.hash(0, deviceId);
	}

	@Override
	public String toString() {
		StringBundler sb = new StringBundler(31);

		sb.append("{deviceId=");
		sb.append(deviceId);
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
		sb.append(", deviceIP=");
		sb.append(deviceIP);
		sb.append(", browserName=");
		sb.append(browserName);
		sb.append(", osName=");
		sb.append(osName);
		sb.append(", verified=");
		sb.append(verified);
		sb.append(", tempDevice=");
		sb.append(tempDevice);
		sb.append("}");

		return sb.toString();
	}

	@Override
	public Device toEntityModel() {
		DeviceImpl deviceImpl = new DeviceImpl();

		deviceImpl.setDeviceId(deviceId);
		deviceImpl.setGroupId(groupId);
		deviceImpl.setCompanyId(companyId);
		deviceImpl.setUserId(userId);

		if (userName == null) {
			deviceImpl.setUserName(StringPool.BLANK);
		}
		else {
			deviceImpl.setUserName(userName);
		}

		if (createDate == Long.MIN_VALUE) {
			deviceImpl.setCreateDate(null);
		}
		else {
			deviceImpl.setCreateDate(new Date(createDate));
		}

		if (modifiedDate == Long.MIN_VALUE) {
			deviceImpl.setModifiedDate(null);
		}
		else {
			deviceImpl.setModifiedDate(new Date(modifiedDate));
		}

		deviceImpl.setPortalUserId(portalUserId);

		if (portalUserName == null) {
			deviceImpl.setPortalUserName(StringPool.BLANK);
		}
		else {
			deviceImpl.setPortalUserName(portalUserName);
		}

		if (emailAddress == null) {
			deviceImpl.setEmailAddress(StringPool.BLANK);
		}
		else {
			deviceImpl.setEmailAddress(emailAddress);
		}

		if (deviceIP == null) {
			deviceImpl.setDeviceIP(StringPool.BLANK);
		}
		else {
			deviceImpl.setDeviceIP(deviceIP);
		}

		if (browserName == null) {
			deviceImpl.setBrowserName(StringPool.BLANK);
		}
		else {
			deviceImpl.setBrowserName(browserName);
		}

		if (osName == null) {
			deviceImpl.setOsName(StringPool.BLANK);
		}
		else {
			deviceImpl.setOsName(osName);
		}

		deviceImpl.setVerified(verified);
		deviceImpl.setTempDevice(tempDevice);

		deviceImpl.resetOriginalValues();

		return deviceImpl;
	}

	@Override
	public void readExternal(ObjectInput objectInput) throws IOException {
		deviceId = objectInput.readLong();

		groupId = objectInput.readLong();

		companyId = objectInput.readLong();

		userId = objectInput.readLong();
		userName = objectInput.readUTF();
		createDate = objectInput.readLong();
		modifiedDate = objectInput.readLong();

		portalUserId = objectInput.readLong();
		portalUserName = objectInput.readUTF();
		emailAddress = objectInput.readUTF();
		deviceIP = objectInput.readUTF();
		browserName = objectInput.readUTF();
		osName = objectInput.readUTF();

		verified = objectInput.readBoolean();

		tempDevice = objectInput.readBoolean();
	}

	@Override
	public void writeExternal(ObjectOutput objectOutput)
		throws IOException {
		objectOutput.writeLong(deviceId);

		objectOutput.writeLong(groupId);

		objectOutput.writeLong(companyId);

		objectOutput.writeLong(userId);

		if (userName == null) {
			objectOutput.writeUTF(StringPool.BLANK);
		}
		else {
			objectOutput.writeUTF(userName);
		}

		objectOutput.writeLong(createDate);
		objectOutput.writeLong(modifiedDate);

		objectOutput.writeLong(portalUserId);

		if (portalUserName == null) {
			objectOutput.writeUTF(StringPool.BLANK);
		}
		else {
			objectOutput.writeUTF(portalUserName);
		}

		if (emailAddress == null) {
			objectOutput.writeUTF(StringPool.BLANK);
		}
		else {
			objectOutput.writeUTF(emailAddress);
		}

		if (deviceIP == null) {
			objectOutput.writeUTF(StringPool.BLANK);
		}
		else {
			objectOutput.writeUTF(deviceIP);
		}

		if (browserName == null) {
			objectOutput.writeUTF(StringPool.BLANK);
		}
		else {
			objectOutput.writeUTF(browserName);
		}

		if (osName == null) {
			objectOutput.writeUTF(StringPool.BLANK);
		}
		else {
			objectOutput.writeUTF(osName);
		}

		objectOutput.writeBoolean(verified);

		objectOutput.writeBoolean(tempDevice);
	}

	public long deviceId;
	public long groupId;
	public long companyId;
	public long userId;
	public String userName;
	public long createDate;
	public long modifiedDate;
	public long portalUserId;
	public String portalUserName;
	public String emailAddress;
	public String deviceIP;
	public String browserName;
	public String osName;
	public boolean verified;
	public boolean tempDevice;
}