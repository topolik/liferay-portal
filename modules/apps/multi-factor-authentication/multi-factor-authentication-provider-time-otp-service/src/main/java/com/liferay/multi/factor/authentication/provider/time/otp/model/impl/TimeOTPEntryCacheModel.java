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

package com.liferay.multi.factor.authentication.provider.time.otp.model.impl;

import aQute.bnd.annotation.ProviderType;

import com.liferay.multi.factor.authentication.provider.time.otp.model.TimeOTPEntry;
import com.liferay.petra.lang.HashUtil;
import com.liferay.petra.string.StringBundler;
import com.liferay.portal.kernel.model.CacheModel;

import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;

import java.util.Date;

/**
 * The cache model class for representing TimeOTPEntry in entity cache.
 *
 * @author arthurchan35
 * @generated
 */
@ProviderType
public class TimeOTPEntryCacheModel
	implements CacheModel<TimeOTPEntry>, Externalizable {

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}

		if (!(obj instanceof TimeOTPEntryCacheModel)) {
			return false;
		}

		TimeOTPEntryCacheModel timeOTPEntryCacheModel =
			(TimeOTPEntryCacheModel)obj;

		if (entryId == timeOTPEntryCacheModel.entryId) {
			return true;
		}

		return false;
	}

	@Override
	public int hashCode() {
		return HashUtil.hash(0, entryId);
	}

	@Override
	public String toString() {
		StringBundler sb = new StringBundler(25);

		sb.append("{entryId=");
		sb.append(entryId);
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
		sb.append(", sharedSecret=");
		sb.append(sharedSecret);
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
	public TimeOTPEntry toEntityModel() {
		TimeOTPEntryImpl timeOTPEntryImpl = new TimeOTPEntryImpl();

		timeOTPEntryImpl.setEntryId(entryId);
		timeOTPEntryImpl.setCompanyId(companyId);
		timeOTPEntryImpl.setUserId(userId);

		if (userName == null) {
			timeOTPEntryImpl.setUserName("");
		}
		else {
			timeOTPEntryImpl.setUserName(userName);
		}

		if (createDate == Long.MIN_VALUE) {
			timeOTPEntryImpl.setCreateDate(null);
		}
		else {
			timeOTPEntryImpl.setCreateDate(new Date(createDate));
		}

		if (modifiedDate == Long.MIN_VALUE) {
			timeOTPEntryImpl.setModifiedDate(null);
		}
		else {
			timeOTPEntryImpl.setModifiedDate(new Date(modifiedDate));
		}

		if (sharedSecret == null) {
			timeOTPEntryImpl.setSharedSecret("");
		}
		else {
			timeOTPEntryImpl.setSharedSecret(sharedSecret);
		}

		if (lastSuccessDate == Long.MIN_VALUE) {
			timeOTPEntryImpl.setLastSuccessDate(null);
		}
		else {
			timeOTPEntryImpl.setLastSuccessDate(new Date(lastSuccessDate));
		}

		if (lastSuccessIP == null) {
			timeOTPEntryImpl.setLastSuccessIP("");
		}
		else {
			timeOTPEntryImpl.setLastSuccessIP(lastSuccessIP);
		}

		if (lastFailDate == Long.MIN_VALUE) {
			timeOTPEntryImpl.setLastFailDate(null);
		}
		else {
			timeOTPEntryImpl.setLastFailDate(new Date(lastFailDate));
		}

		if (lastFailIP == null) {
			timeOTPEntryImpl.setLastFailIP("");
		}
		else {
			timeOTPEntryImpl.setLastFailIP(lastFailIP);
		}

		timeOTPEntryImpl.setFailedAttempts(failedAttempts);

		timeOTPEntryImpl.resetOriginalValues();

		return timeOTPEntryImpl;
	}

	@Override
	public void readExternal(ObjectInput objectInput) throws IOException {
		entryId = objectInput.readLong();

		companyId = objectInput.readLong();

		userId = objectInput.readLong();
		userName = objectInput.readUTF();
		createDate = objectInput.readLong();
		modifiedDate = objectInput.readLong();
		sharedSecret = objectInput.readUTF();
		lastSuccessDate = objectInput.readLong();
		lastSuccessIP = objectInput.readUTF();
		lastFailDate = objectInput.readLong();
		lastFailIP = objectInput.readUTF();

		failedAttempts = objectInput.readInt();
	}

	@Override
	public void writeExternal(ObjectOutput objectOutput) throws IOException {
		objectOutput.writeLong(entryId);

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

		if (sharedSecret == null) {
			objectOutput.writeUTF("");
		}
		else {
			objectOutput.writeUTF(sharedSecret);
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

	public long entryId;
	public long companyId;
	public long userId;
	public String userName;
	public long createDate;
	public long modifiedDate;
	public String sharedSecret;
	public long lastSuccessDate;
	public String lastSuccessIP;
	public long lastFailDate;
	public String lastFailIP;
	public int failedAttempts;

}