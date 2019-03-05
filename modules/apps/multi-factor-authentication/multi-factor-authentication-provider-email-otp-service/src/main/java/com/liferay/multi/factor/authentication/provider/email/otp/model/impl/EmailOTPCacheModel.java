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

package com.liferay.multi.factor.authentication.provider.email.otp.model.impl;

import aQute.bnd.annotation.ProviderType;

import com.liferay.multi.factor.authentication.provider.email.otp.model.EmailOTP;
import com.liferay.petra.lang.HashUtil;
import com.liferay.petra.string.StringBundler;
import com.liferay.portal.kernel.model.CacheModel;

import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;

import java.util.Date;

/**
 * The cache model class for representing EmailOTP in entity cache.
 *
 * @author arthurchan35
 * @generated
 */
@ProviderType
public class EmailOTPCacheModel
	implements CacheModel<EmailOTP>, Externalizable {

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}

		if (!(obj instanceof EmailOTPCacheModel)) {
			return false;
		}

		EmailOTPCacheModel emailOTPCacheModel = (EmailOTPCacheModel)obj;

		if (emailOTPId == emailOTPCacheModel.emailOTPId) {
			return true;
		}

		return false;
	}

	@Override
	public int hashCode() {
		return HashUtil.hash(0, emailOTPId);
	}

	@Override
	public String toString() {
		StringBundler sb = new StringBundler(25);

		sb.append("{emailOTPId=");
		sb.append(emailOTPId);
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
		sb.append(", lastSuccessDate=");
		sb.append(lastSuccessDate);
		sb.append(", lastSuccessIP=");
		sb.append(lastSuccessIP);
		sb.append(", lastFailDate=");
		sb.append(lastFailDate);
		sb.append(", lastFailIP=");
		sb.append(lastFailIP);
		sb.append(", failedAttempts=");
		sb.append(failedAttempts);
		sb.append("}");

		return sb.toString();
	}

	@Override
	public EmailOTP toEntityModel() {
		EmailOTPImpl emailOTPImpl = new EmailOTPImpl();

		emailOTPImpl.setEmailOTPId(emailOTPId);
		emailOTPImpl.setCompanyId(companyId);
		emailOTPImpl.setUserId(userId);

		if (userName == null) {
			emailOTPImpl.setUserName("");
		}
		else {
			emailOTPImpl.setUserName(userName);
		}

		if (createDate == Long.MIN_VALUE) {
			emailOTPImpl.setCreateDate(null);
		}
		else {
			emailOTPImpl.setCreateDate(new Date(createDate));
		}

		if (modifiedDate == Long.MIN_VALUE) {
			emailOTPImpl.setModifiedDate(null);
		}
		else {
			emailOTPImpl.setModifiedDate(new Date(modifiedDate));
		}

		if (emailAddress == null) {
			emailOTPImpl.setEmailAddress("");
		}
		else {
			emailOTPImpl.setEmailAddress(emailAddress);
		}

		if (lastSuccessDate == Long.MIN_VALUE) {
			emailOTPImpl.setLastSuccessDate(null);
		}
		else {
			emailOTPImpl.setLastSuccessDate(new Date(lastSuccessDate));
		}

		if (lastSuccessIP == null) {
			emailOTPImpl.setLastSuccessIP("");
		}
		else {
			emailOTPImpl.setLastSuccessIP(lastSuccessIP);
		}

		if (lastFailDate == Long.MIN_VALUE) {
			emailOTPImpl.setLastFailDate(null);
		}
		else {
			emailOTPImpl.setLastFailDate(new Date(lastFailDate));
		}

		if (lastFailIP == null) {
			emailOTPImpl.setLastFailIP("");
		}
		else {
			emailOTPImpl.setLastFailIP(lastFailIP);
		}

		emailOTPImpl.setFailedAttempts(failedAttempts);

		emailOTPImpl.resetOriginalValues();

		return emailOTPImpl;
	}

	@Override
	public void readExternal(ObjectInput objectInput) throws IOException {
		emailOTPId = objectInput.readLong();

		companyId = objectInput.readLong();

		userId = objectInput.readLong();
		userName = objectInput.readUTF();
		createDate = objectInput.readLong();
		modifiedDate = objectInput.readLong();
		emailAddress = objectInput.readUTF();
		lastSuccessDate = objectInput.readLong();
		lastSuccessIP = objectInput.readUTF();
		lastFailDate = objectInput.readLong();
		lastFailIP = objectInput.readUTF();

		failedAttempts = objectInput.readInt();
	}

	@Override
	public void writeExternal(ObjectOutput objectOutput) throws IOException {
		objectOutput.writeLong(emailOTPId);

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

		objectOutput.writeLong(lastSuccessDate);

		if (lastSuccessIP == null) {
			objectOutput.writeUTF("");
		}
		else {
			objectOutput.writeUTF(lastSuccessIP);
		}

		objectOutput.writeLong(lastFailDate);

		if (lastFailIP == null) {
			objectOutput.writeUTF("");
		}
		else {
			objectOutput.writeUTF(lastFailIP);
		}

		objectOutput.writeInt(failedAttempts);
	}

	public long emailOTPId;
	public long companyId;
	public long userId;
	public String userName;
	public long createDate;
	public long modifiedDate;
	public String emailAddress;
	public long lastSuccessDate;
	public String lastSuccessIP;
	public long lastFailDate;
	public String lastFailIP;
	public int failedAttempts;

}