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
public class OAuth2TokenSoap implements Serializable {
	public static OAuth2TokenSoap toSoapModel(OAuth2Token model) {
		OAuth2TokenSoap soapModel = new OAuth2TokenSoap();

		soapModel.setOAuth2TokenId(model.getOAuth2TokenId());
		soapModel.setCompanyId(model.getCompanyId());
		soapModel.setUserId(model.getUserId());
		soapModel.setUserName(model.getUserName());
		soapModel.setCreateDate(model.getCreateDate());
		soapModel.setLifeTime(model.getLifeTime());
		soapModel.setOAuth2ApplicationId(model.getOAuth2ApplicationId());
		soapModel.setOAuth2TokenType(model.getOAuth2TokenType());
		soapModel.setOAuth2RefreshTokenId(model.getOAuth2RefreshTokenId());
		soapModel.setScopes(model.getScopes());

		return soapModel;
	}

	public static OAuth2TokenSoap[] toSoapModels(OAuth2Token[] models) {
		OAuth2TokenSoap[] soapModels = new OAuth2TokenSoap[models.length];

		for (int i = 0; i < models.length; i++) {
			soapModels[i] = toSoapModel(models[i]);
		}

		return soapModels;
	}

	public static OAuth2TokenSoap[][] toSoapModels(OAuth2Token[][] models) {
		OAuth2TokenSoap[][] soapModels = null;

		if (models.length > 0) {
			soapModels = new OAuth2TokenSoap[models.length][models[0].length];
		}
		else {
			soapModels = new OAuth2TokenSoap[0][0];
		}

		for (int i = 0; i < models.length; i++) {
			soapModels[i] = toSoapModels(models[i]);
		}

		return soapModels;
	}

	public static OAuth2TokenSoap[] toSoapModels(List<OAuth2Token> models) {
		List<OAuth2TokenSoap> soapModels = new ArrayList<OAuth2TokenSoap>(models.size());

		for (OAuth2Token model : models) {
			soapModels.add(toSoapModel(model));
		}

		return soapModels.toArray(new OAuth2TokenSoap[soapModels.size()]);
	}

	public OAuth2TokenSoap() {
	}

	public String getPrimaryKey() {
		return _oAuth2TokenId;
	}

	public void setPrimaryKey(String pk) {
		setOAuth2TokenId(pk);
	}

	public String getOAuth2TokenId() {
		return _oAuth2TokenId;
	}

	public void setOAuth2TokenId(String oAuth2TokenId) {
		_oAuth2TokenId = oAuth2TokenId;
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

	public String getOAuth2TokenType() {
		return _oAuth2TokenType;
	}

	public void setOAuth2TokenType(String oAuth2TokenType) {
		_oAuth2TokenType = oAuth2TokenType;
	}

	public String getOAuth2RefreshTokenId() {
		return _oAuth2RefreshTokenId;
	}

	public void setOAuth2RefreshTokenId(String oAuth2RefreshTokenId) {
		_oAuth2RefreshTokenId = oAuth2RefreshTokenId;
	}

	public String getScopes() {
		return _scopes;
	}

	public void setScopes(String scopes) {
		_scopes = scopes;
	}

	private String _oAuth2TokenId;
	private long _companyId;
	private long _userId;
	private String _userName;
	private Date _createDate;
	private long _lifeTime;
	private long _oAuth2ApplicationId;
	private String _oAuth2TokenType;
	private String _oAuth2RefreshTokenId;
	private String _scopes;
}