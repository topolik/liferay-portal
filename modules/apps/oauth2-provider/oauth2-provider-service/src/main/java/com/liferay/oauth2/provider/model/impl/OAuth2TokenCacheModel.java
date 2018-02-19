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

import com.liferay.oauth2.provider.model.OAuth2Token;

import com.liferay.portal.kernel.model.CacheModel;
import com.liferay.portal.kernel.util.HashUtil;
import com.liferay.portal.kernel.util.StringBundler;

import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;

import java.util.Date;

/**
 * The cache model class for representing OAuth2Token in entity cache.
 *
 * @author Brian Wing Shun Chan
 * @see OAuth2Token
 * @generated
 */
@ProviderType
public class OAuth2TokenCacheModel implements CacheModel<OAuth2Token>,
	Externalizable {
	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}

		if (!(obj instanceof OAuth2TokenCacheModel)) {
			return false;
		}

		OAuth2TokenCacheModel oAuth2TokenCacheModel = (OAuth2TokenCacheModel)obj;

		if (oAuth2TokenId.equals(oAuth2TokenCacheModel.oAuth2TokenId)) {
			return true;
		}

		return false;
	}

	@Override
	public int hashCode() {
		return HashUtil.hash(0, oAuth2TokenId);
	}

	@Override
	public String toString() {
		StringBundler sb = new StringBundler(21);

		sb.append("{oAuth2TokenId=");
		sb.append(oAuth2TokenId);
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
		sb.append(", oAuth2TokenType=");
		sb.append(oAuth2TokenType);
		sb.append(", oAuth2RefreshTokenId=");
		sb.append(oAuth2RefreshTokenId);
		sb.append(", scopes=");
		sb.append(scopes);
		sb.append("}");

		return sb.toString();
	}

	@Override
	public OAuth2Token toEntityModel() {
		OAuth2TokenImpl oAuth2TokenImpl = new OAuth2TokenImpl();

		if (oAuth2TokenId == null) {
			oAuth2TokenImpl.setOAuth2TokenId("");
		}
		else {
			oAuth2TokenImpl.setOAuth2TokenId(oAuth2TokenId);
		}

		oAuth2TokenImpl.setCompanyId(companyId);
		oAuth2TokenImpl.setUserId(userId);

		if (userName == null) {
			oAuth2TokenImpl.setUserName("");
		}
		else {
			oAuth2TokenImpl.setUserName(userName);
		}

		if (createDate == Long.MIN_VALUE) {
			oAuth2TokenImpl.setCreateDate(null);
		}
		else {
			oAuth2TokenImpl.setCreateDate(new Date(createDate));
		}

		oAuth2TokenImpl.setLifeTime(lifeTime);
		oAuth2TokenImpl.setOAuth2ApplicationId(oAuth2ApplicationId);

		if (oAuth2TokenType == null) {
			oAuth2TokenImpl.setOAuth2TokenType("");
		}
		else {
			oAuth2TokenImpl.setOAuth2TokenType(oAuth2TokenType);
		}

		if (oAuth2RefreshTokenId == null) {
			oAuth2TokenImpl.setOAuth2RefreshTokenId("");
		}
		else {
			oAuth2TokenImpl.setOAuth2RefreshTokenId(oAuth2RefreshTokenId);
		}

		if (scopes == null) {
			oAuth2TokenImpl.setScopes("");
		}
		else {
			oAuth2TokenImpl.setScopes(scopes);
		}

		oAuth2TokenImpl.resetOriginalValues();

		return oAuth2TokenImpl;
	}

	@Override
	public void readExternal(ObjectInput objectInput) throws IOException {
		oAuth2TokenId = objectInput.readUTF();

		companyId = objectInput.readLong();

		userId = objectInput.readLong();
		userName = objectInput.readUTF();
		createDate = objectInput.readLong();

		lifeTime = objectInput.readLong();

		oAuth2ApplicationId = objectInput.readLong();
		oAuth2TokenType = objectInput.readUTF();
		oAuth2RefreshTokenId = objectInput.readUTF();
		scopes = objectInput.readUTF();
	}

	@Override
	public void writeExternal(ObjectOutput objectOutput)
		throws IOException {
		if (oAuth2TokenId == null) {
			objectOutput.writeUTF("");
		}
		else {
			objectOutput.writeUTF(oAuth2TokenId);
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

		if (oAuth2TokenType == null) {
			objectOutput.writeUTF("");
		}
		else {
			objectOutput.writeUTF(oAuth2TokenType);
		}

		if (oAuth2RefreshTokenId == null) {
			objectOutput.writeUTF("");
		}
		else {
			objectOutput.writeUTF(oAuth2RefreshTokenId);
		}

		if (scopes == null) {
			objectOutput.writeUTF("");
		}
		else {
			objectOutput.writeUTF(scopes);
		}
	}

	public String oAuth2TokenId;
	public long companyId;
	public long userId;
	public String userName;
	public long createDate;
	public long lifeTime;
	public long oAuth2ApplicationId;
	public String oAuth2TokenType;
	public String oAuth2RefreshTokenId;
	public String scopes;
}