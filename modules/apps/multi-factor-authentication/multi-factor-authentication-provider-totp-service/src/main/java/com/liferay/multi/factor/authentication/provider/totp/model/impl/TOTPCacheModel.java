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

package com.liferay.multi.factor.authentication.provider.totp.model.impl;

import aQute.bnd.annotation.ProviderType;

import com.liferay.multi.factor.authentication.provider.totp.model.TOTP;

import com.liferay.petra.lang.HashUtil;
import com.liferay.petra.string.StringBundler;

import com.liferay.portal.kernel.model.CacheModel;

import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;

import java.util.Date;

/**
 * The cache model class for representing TOTP in entity cache.
 *
 * @author arthurchan35
 * @see TOTP
 * @generated
 */
@ProviderType
public class TOTPCacheModel implements CacheModel<TOTP>, Externalizable {
	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}

		if (!(obj instanceof TOTPCacheModel)) {
			return false;
		}

		TOTPCacheModel totpCacheModel = (TOTPCacheModel)obj;

		if (totpId == totpCacheModel.totpId) {
			return true;
		}

		return false;
	}

	@Override
	public int hashCode() {
		return HashUtil.hash(0, totpId);
	}

	@Override
	public String toString() {
		StringBundler sb = new StringBundler(27);

		sb.append("{totpId=");
		sb.append(totpId);
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
		sb.append(", backupCodes=");
		sb.append(backupCodes);
		sb.append(", lastLoginDate=");
		sb.append(lastLoginDate);
		sb.append(", lastLoginIP=");
		sb.append(lastLoginIP);
		sb.append(", lastFailedLoginDate=");
		sb.append(lastFailedLoginDate);
		sb.append(", failedLoginAttempts=");
		sb.append(failedLoginAttempts);
		sb.append(", sharedSecret=");
		sb.append(sharedSecret);
		sb.append(", verified=");
		sb.append(verified);
		sb.append("}");

		return sb.toString();
	}

	@Override
	public TOTP toEntityModel() {
		TOTPImpl totpImpl = new TOTPImpl();

		totpImpl.setTotpId(totpId);
		totpImpl.setCompanyId(companyId);
		totpImpl.setUserId(userId);

		if (userName == null) {
			totpImpl.setUserName("");
		}
		else {
			totpImpl.setUserName(userName);
		}

		if (createDate == Long.MIN_VALUE) {
			totpImpl.setCreateDate(null);
		}
		else {
			totpImpl.setCreateDate(new Date(createDate));
		}

		if (modifiedDate == Long.MIN_VALUE) {
			totpImpl.setModifiedDate(null);
		}
		else {
			totpImpl.setModifiedDate(new Date(modifiedDate));
		}

		if (backupCodes == null) {
			totpImpl.setBackupCodes("");
		}
		else {
			totpImpl.setBackupCodes(backupCodes);
		}

		if (lastLoginDate == Long.MIN_VALUE) {
			totpImpl.setLastLoginDate(null);
		}
		else {
			totpImpl.setLastLoginDate(new Date(lastLoginDate));
		}

		if (lastLoginIP == null) {
			totpImpl.setLastLoginIP("");
		}
		else {
			totpImpl.setLastLoginIP(lastLoginIP);
		}

		if (lastFailedLoginDate == Long.MIN_VALUE) {
			totpImpl.setLastFailedLoginDate(null);
		}
		else {
			totpImpl.setLastFailedLoginDate(new Date(lastFailedLoginDate));
		}

		totpImpl.setFailedLoginAttempts(failedLoginAttempts);

		if (sharedSecret == null) {
			totpImpl.setSharedSecret("");
		}
		else {
			totpImpl.setSharedSecret(sharedSecret);
		}

		totpImpl.setVerified(verified);

		totpImpl.resetOriginalValues();

		return totpImpl;
	}

	@Override
	public void readExternal(ObjectInput objectInput) throws IOException {
		totpId = objectInput.readLong();

		companyId = objectInput.readLong();

		userId = objectInput.readLong();
		userName = objectInput.readUTF();
		createDate = objectInput.readLong();
		modifiedDate = objectInput.readLong();
		backupCodes = objectInput.readUTF();
		lastLoginDate = objectInput.readLong();
		lastLoginIP = objectInput.readUTF();
		lastFailedLoginDate = objectInput.readLong();

		failedLoginAttempts = objectInput.readInt();
		sharedSecret = objectInput.readUTF();

		verified = objectInput.readBoolean();
	}

	@Override
	public void writeExternal(ObjectOutput objectOutput)
		throws IOException {
		objectOutput.writeLong(totpId);

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

		if (backupCodes == null) {
			objectOutput.writeUTF("");
		}
		else {
			objectOutput.writeUTF(backupCodes);
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

		if (sharedSecret == null) {
			objectOutput.writeUTF("");
		}
		else {
			objectOutput.writeUTF(sharedSecret);
		}

		objectOutput.writeBoolean(verified);
	}

	public long totpId;
	public long companyId;
	public long userId;
	public String userName;
	public long createDate;
	public long modifiedDate;
	public String backupCodes;
	public long lastLoginDate;
	public String lastLoginIP;
	public long lastFailedLoginDate;
	public int failedLoginAttempts;
	public String sharedSecret;
	public boolean verified;
}