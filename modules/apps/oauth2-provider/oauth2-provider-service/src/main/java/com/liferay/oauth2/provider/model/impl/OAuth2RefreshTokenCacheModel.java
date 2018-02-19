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

package com.liferay.oauth2.provider.model.impl;

import aQute.bnd.annotation.ProviderType;

import com.liferay.oauth2.provider.model.OAuth2RefreshToken;

import com.liferay.portal.kernel.model.CacheModel;
import com.liferay.portal.kernel.util.HashUtil;
import com.liferay.portal.kernel.util.StringBundler;

import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;

import java.util.Date;

/**
 * The cache model class for representing OAuth2RefreshToken in entity cache.
 *
 * @author Brian Wing Shun Chan
 * @see OAuth2RefreshToken
 * @generated
 */
@ProviderType
public class OAuth2RefreshTokenCacheModel implements CacheModel<OAuth2RefreshToken>,
	Externalizable {
	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}

		if (!(obj instanceof OAuth2RefreshTokenCacheModel)) {
			return false;
		}

		OAuth2RefreshTokenCacheModel oAuth2RefreshTokenCacheModel = (OAuth2RefreshTokenCacheModel)obj;

		if (oAuth2RefreshTokenId.equals(
					oAuth2RefreshTokenCacheModel.oAuth2RefreshTokenId)) {
			return true;
		}

		return false;
	}

	@Override
	public int hashCode() {
		return HashUtil.hash(0, oAuth2RefreshTokenId);
	}

	@Override
	public String toString() {
		StringBundler sb = new StringBundler(15);

		sb.append("{oAuth2RefreshTokenId=");
		sb.append(oAuth2RefreshTokenId);
		sb.append(", companyId=");
		sb.append(companyId);
		sb.append(", userId=");
		sb.append(userId);
		sb.append(", userName=");
		sb.append(userName);
		sb.append(", createDate=");
		sb.append(createDate);
		sb.append(", lifeTime=");
		sb.append(lifeTime);
		sb.append(", oAuth2ApplicationId=");
		sb.append(oAuth2ApplicationId);
		sb.append("}");

		return sb.toString();
	}

	@Override
	public OAuth2RefreshToken toEntityModel() {
		OAuth2RefreshTokenImpl oAuth2RefreshTokenImpl = new OAuth2RefreshTokenImpl();

		if (oAuth2RefreshTokenId == null) {
			oAuth2RefreshTokenImpl.setOAuth2RefreshTokenId("");
		}
		else {
			oAuth2RefreshTokenImpl.setOAuth2RefreshTokenId(oAuth2RefreshTokenId);
		}

		oAuth2RefreshTokenImpl.setCompanyId(companyId);
		oAuth2RefreshTokenImpl.setUserId(userId);

		if (userName == null) {
			oAuth2RefreshTokenImpl.setUserName("");
		}
		else {
			oAuth2RefreshTokenImpl.setUserName(userName);
		}

		if (createDate == Long.MIN_VALUE) {
			oAuth2RefreshTokenImpl.setCreateDate(null);
		}
		else {
			oAuth2RefreshTokenImpl.setCreateDate(new Date(createDate));
		}

		oAuth2RefreshTokenImpl.setLifeTime(lifeTime);
		oAuth2RefreshTokenImpl.setOAuth2ApplicationId(oAuth2ApplicationId);

		oAuth2RefreshTokenImpl.resetOriginalValues();

		return oAuth2RefreshTokenImpl;
	}

	@Override
	public void readExternal(ObjectInput objectInput) throws IOException {
		oAuth2RefreshTokenId = objectInput.readUTF();

		companyId = objectInput.readLong();

		userId = objectInput.readLong();
		userName = objectInput.readUTF();
		createDate = objectInput.readLong();

		lifeTime = objectInput.readLong();

		oAuth2ApplicationId = objectInput.readLong();
	}

	@Override
	public void writeExternal(ObjectOutput objectOutput)
		throws IOException {
		if (oAuth2RefreshTokenId == null) {
			objectOutput.writeUTF("");
		}
		else {
			objectOutput.writeUTF(oAuth2RefreshTokenId);
		}

		objectOutput.writeLong(companyId);

		objectOutput.writeLong(userId);

		if (userName == null) {
			objectOutput.writeUTF("");
		}
		else {
			objectOutput.writeUTF(userName);
		}

		objectOutput.writeLong(createDate);

		objectOutput.writeLong(lifeTime);

		objectOutput.writeLong(oAuth2ApplicationId);
	}

	public String oAuth2RefreshTokenId;
	public long companyId;
	public long userId;
	public String userName;
	public long createDate;
	public long lifeTime;
	public long oAuth2ApplicationId;
}