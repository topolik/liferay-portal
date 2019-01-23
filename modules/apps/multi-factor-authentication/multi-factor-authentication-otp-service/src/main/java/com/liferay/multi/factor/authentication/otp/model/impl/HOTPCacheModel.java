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

import com.liferay.multi.factor.authentication.otp.model.HOTP;

import com.liferay.petra.lang.HashUtil;
import com.liferay.petra.string.StringBundler;

import com.liferay.portal.kernel.model.CacheModel;

import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;

/**
 * The cache model class for representing HOTP in entity cache.
 *
 * @author arthurchan35
 * @see HOTP
 * @generated
 */
@ProviderType
public class HOTPCacheModel implements CacheModel<HOTP>, Externalizable {
	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}

		if (!(obj instanceof HOTPCacheModel)) {
			return false;
		}

		HOTPCacheModel hotpCacheModel = (HOTPCacheModel)obj;

		if (hotpId == hotpCacheModel.hotpId) {
			return true;
		}

		return false;
	}

	@Override
	public int hashCode() {
		return HashUtil.hash(0, hotpId);
	}

	@Override
	public String toString() {
		StringBundler sb = new StringBundler(13);

		sb.append("{hotpId=");
		sb.append(hotpId);
		sb.append(", userId=");
		sb.append(userId);
		sb.append(", count=");
		sb.append(count);
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
	public HOTP toEntityModel() {
		HOTPImpl hotpImpl = new HOTPImpl();

		hotpImpl.setHotpId(hotpId);
		hotpImpl.setUserId(userId);
		hotpImpl.setCount(count);
		hotpImpl.setFailedAttempts(failedAttempts);

		if (sharedSecret == null) {
			hotpImpl.setSharedSecret("");
		}
		else {
			hotpImpl.setSharedSecret(sharedSecret);
		}

		hotpImpl.setVerified(verified);

		hotpImpl.resetOriginalValues();

		return hotpImpl;
	}

	@Override
	public void readExternal(ObjectInput objectInput) throws IOException {
		hotpId = objectInput.readLong();

		userId = objectInput.readLong();

		count = objectInput.readLong();

		failedAttempts = objectInput.readInt();
		sharedSecret = objectInput.readUTF();

		verified = objectInput.readBoolean();
	}

	@Override
	public void writeExternal(ObjectOutput objectOutput)
		throws IOException {
		objectOutput.writeLong(hotpId);

		objectOutput.writeLong(userId);

		objectOutput.writeLong(count);

		objectOutput.writeInt(failedAttempts);

		if (sharedSecret == null) {
			objectOutput.writeUTF("");
		}
		else {
			objectOutput.writeUTF(sharedSecret);
		}

		objectOutput.writeBoolean(verified);
	}

	public long hotpId;
	public long userId;
	public long count;
	public int failedAttempts;
	public String sharedSecret;
	public boolean verified;
}