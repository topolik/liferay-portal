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

package com.liferay.oauth2.provider.model;

import aQute.bnd.annotation.ProviderType;

import java.io.Serializable;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * This class is used by SOAP remote services.
 *
 * @author Brian Wing Shun Chan
 * @generated
 */
@ProviderType
public class OAuth2RefreshTokenSoap implements Serializable {
	public static OAuth2RefreshTokenSoap toSoapModel(OAuth2RefreshToken model) {
		OAuth2RefreshTokenSoap soapModel = new OAuth2RefreshTokenSoap();

		soapModel.setOAuth2RefreshTokenId(model.getOAuth2RefreshTokenId());
		soapModel.setCompanyId(model.getCompanyId());
		soapModel.setUserId(model.getUserId());
		soapModel.setUserName(model.getUserName());
		soapModel.setCreateDate(model.getCreateDate());
		soapModel.setLifeTime(model.getLifeTime());
		soapModel.setOAuth2ApplicationId(model.getOAuth2ApplicationId());

		return soapModel;
	}

	public static OAuth2RefreshTokenSoap[] toSoapModels(
		OAuth2RefreshToken[] models) {
		OAuth2RefreshTokenSoap[] soapModels = new OAuth2RefreshTokenSoap[models.length];

		for (int i = 0; i < models.length; i++) {
			soapModels[i] = toSoapModel(models[i]);
		}

		return soapModels;
	}

	public static OAuth2RefreshTokenSoap[][] toSoapModels(
		OAuth2RefreshToken[][] models) {
		OAuth2RefreshTokenSoap[][] soapModels = null;

		if (models.length > 0) {
			soapModels = new OAuth2RefreshTokenSoap[models.length][models[0].length];
		}
		else {
			soapModels = new OAuth2RefreshTokenSoap[0][0];
		}

		for (int i = 0; i < models.length; i++) {
			soapModels[i] = toSoapModels(models[i]);
		}

		return soapModels;
	}

	public static OAuth2RefreshTokenSoap[] toSoapModels(
		List<OAuth2RefreshToken> models) {
		List<OAuth2RefreshTokenSoap> soapModels = new ArrayList<OAuth2RefreshTokenSoap>(models.size());

		for (OAuth2RefreshToken model : models) {
			soapModels.add(toSoapModel(model));
		}

		return soapModels.toArray(new OAuth2RefreshTokenSoap[soapModels.size()]);
	}

	public OAuth2RefreshTokenSoap() {
	}

	public String getPrimaryKey() {
		return _oAuth2RefreshTokenId;
	}

	public void setPrimaryKey(String pk) {
		setOAuth2RefreshTokenId(pk);
	}

	public String getOAuth2RefreshTokenId() {
		return _oAuth2RefreshTokenId;
	}

	public void setOAuth2RefreshTokenId(String oAuth2RefreshTokenId) {
		_oAuth2RefreshTokenId = oAuth2RefreshTokenId;
	}

	public long getCompanyId() {
		return _companyId;
	}

	public void setCompanyId(long companyId) {
		_companyId = companyId;
	}

	public long getUserId() {
		return _userId;
	}

	public void setUserId(long userId) {
		_userId = userId;
	}

	public String getUserName() {
		return _userName;
	}

	public void setUserName(String userName) {
		_userName = userName;
	}

	public Date getCreateDate() {
		return _createDate;
	}

	public void setCreateDate(Date createDate) {
		_createDate = createDate;
	}

	public long getLifeTime() {
		return _lifeTime;
	}

	public void setLifeTime(long lifeTime) {
		_lifeTime = lifeTime;
	}

	public long getOAuth2ApplicationId() {
		return _oAuth2ApplicationId;
	}

	public void setOAuth2ApplicationId(long oAuth2ApplicationId) {
		_oAuth2ApplicationId = oAuth2ApplicationId;
	}

	private String _oAuth2RefreshTokenId;
	private long _companyId;
	private long _userId;
	private String _userName;
	private Date _createDate;
	private long _lifeTime;
	private long _oAuth2ApplicationId;
}