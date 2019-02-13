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

package com.liferay.multi.factor.authentication.provider.otp.model.impl;

import aQute.bnd.annotation.ProviderType;

import com.liferay.multi.factor.authentication.provider.otp.model.OTP;

import com.liferay.petra.lang.HashUtil;
import com.liferay.petra.string.StringBundler;

import com.liferay.portal.kernel.model.CacheModel;

import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;

import java.util.Date;

/**
 * The cache model class for representing OTP in entity cache.
 *
 * @author arthurchan35
 * @see OTP
 * @generated
 */
@ProviderType
public class OTPCacheModel implements CacheModel<OTP>, Externalizable {
	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}

		if (!(obj instanceof OTPCacheModel)) {
			return false;
		}

		OTPCacheModel otpCacheModel = (OTPCacheModel)obj;

		if (otpId == otpCacheModel.otpId) {
			return true;
		}

		return false;
	}

	@Override
	public int hashCode() {
		return HashUtil.hash(0, otpId);
	}

	@Override
	public String toString() {
		StringBundler sb = new StringBundler(25);

		sb.append("{otpId=");
		sb.append(otpId);
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
		sb.append(", emailAddress=");
		sb.append(emailAddress);
		sb.append(", lastLoginDate=");
		sb.append(lastLoginDate);
		sb.append(", lastLoginIP=");
		sb.append(lastLoginIP);
		sb.append(", lastFailedLoginDate=");
		sb.append(lastFailedLoginDate);
		sb.append(", failedLoginAttempts=");
		sb.append(failedLoginAttempts);
		sb.append(", verified=");
		sb.append(verified);
		sb.append("}");

		return sb.toString();
	}

	@Override
	public OTP toEntityModel() {
		OTPImpl otpImpl = new OTPImpl();

		otpImpl.setOtpId(otpId);
		otpImpl.setCompanyId(companyId);
		otpImpl.setUserId(userId);

		if (userName == null) {
			otpImpl.setUserName("");
		}
		else {
			otpImpl.setUserName(userName);
		}

		if (createDate == Long.MIN_VALUE) {
			otpImpl.setCreateDate(null);
		}
		else {
			otpImpl.setCreateDate(new Date(createDate));
		}

		if (modifiedDate == Long.MIN_VALUE) {
			otpImpl.setModifiedDate(null);
		}
		else {
			otpImpl.setModifiedDate(new Date(modifiedDate));
		}

		if (emailAddress == null) {
			otpImpl.setEmailAddress("");
		}
		else {
			otpImpl.setEmailAddress(emailAddress);
		}

		if (lastLoginDate == Long.MIN_VALUE) {
			otpImpl.setLastLoginDate(null);
		}
		else {
			otpImpl.setLastLoginDate(new Date(lastLoginDate));
		}

		if (lastLoginIP == null) {
			otpImpl.setLastLoginIP("");
		}
		else {
			otpImpl.setLastLoginIP(lastLoginIP);
		}

		if (lastFailedLoginDate == Long.MIN_VALUE) {
			otpImpl.setLastFailedLoginDate(null);
		}
		else {
			otpImpl.setLastFailedLoginDate(new Date(lastFailedLoginDate));
		}

		otpImpl.setFailedLoginAttempts(failedLoginAttempts);
		otpImpl.setVerified(verified);

		otpImpl.resetOriginalValues();

		return otpImpl;
	}

	@Override
	public void readExternal(ObjectInput objectInput) throws IOException {
		otpId = objectInput.readLong();

		companyId = objectInput.readLong();

		userId = objectInput.readLong();
		userName = objectInput.readUTF();
		createDate = objectInput.readLong();
		modifiedDate = objectInput.readLong();
		emailAddress = objectInput.readUTF();
		lastLoginDate = objectInput.readLong();
		lastLoginIP = objectInput.readUTF();
		lastFailedLoginDate = objectInput.readLong();

		failedLoginAttempts = objectInput.readInt();

		verified = objectInput.readBoolean();
	}

	@Override
	public void writeExternal(ObjectOutput objectOutput)
		throws IOException {
		objectOutput.writeLong(otpId);

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

		if (emailAddress == null) {
			objectOutput.writeUTF("");
		}
		else {
			objectOutput.writeUTF(emailAddress);
		}

		objectOutput.writeLong(lastLoginDate);

		if (lastLoginIP == null) {
			objectOutput.writeUTF("");
		}
		else {
			objectOutput.writeUTF(lastLoginIP);
		}

		objectOutput.writeLong(lastFailedLoginDate);

		objectOutput.writeInt(failedLoginAttempts);

		objectOutput.writeBoolean(verified);
	}

	public long otpId;
	public long companyId;
	public long userId;
	public String userName;
	public long createDate;
	public long modifiedDate;
	public String emailAddress;
	public long lastLoginDate;
	public String lastLoginIP;
	public long lastFailedLoginDate;
	public int failedLoginAttempts;
	public boolean verified;
}