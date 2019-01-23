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

package com.liferay.multi.factor.authentication.otp.model.impl;

import aQute.bnd.annotation.ProviderType;

import com.liferay.multi.factor.authentication.otp.model.TOTP;

import com.liferay.petra.lang.HashUtil;
import com.liferay.petra.string.StringBundler;

import com.liferay.portal.kernel.model.CacheModel;

import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;

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
		StringBundler sb = new StringBundler(11);

		sb.append("{totpId=");
		sb.append(totpId);
		sb.append(", userId=");
		sb.append(userId);
		sb.append(", failedAttempts=");
		sb.append(failedAttempts);
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
		totpImpl.setUserId(userId);
		totpImpl.setFailedAttempts(failedAttempts);

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

		userId = objectInput.readLong();

		failedAttempts = objectInput.readInt();
		sharedSecret = objectInput.readUTF();

		verified = objectInput.readBoolean();
	}

	@Override
	public void writeExternal(ObjectOutput objectOutput)
		throws IOException {
		objectOutput.writeLong(totpId);

		objectOutput.writeLong(userId);

		objectOutput.writeInt(failedAttempts);

		if (sharedSecret == null) {
			objectOutput.writeUTF("");
		}
		else {
			objectOutput.writeUTF(sharedSecret);
		}

		objectOutput.writeBoolean(verified);
	}

	public long totpId;
	public long userId;
	public int failedAttempts;
	public String sharedSecret;
	public boolean verified;
}