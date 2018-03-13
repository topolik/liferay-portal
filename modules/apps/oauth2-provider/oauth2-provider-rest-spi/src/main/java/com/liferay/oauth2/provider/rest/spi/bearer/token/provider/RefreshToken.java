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

package com.liferay.oauth2.provider.rest.spi.bearer.token.provider;

import com.liferay.oauth2.provider.model.OAuth2Application;

import java.util.List;

/**
 * @author Tomas Polesovsky
 */
public class RefreshToken {

	public RefreshToken(
		OAuth2Application oAuth2Application, List<String> audiences,
		String clientCodeVerifier, long expiresIn, String grantType,
		long issuedAt, List<String> scopes, String tokenKey,
		String tokenType, long userId, String userName) {

		_oAuth2Application = oAuth2Application;
		_audiences = audiences;
		_clientCodeVerifier = clientCodeVerifier;
		_expiresIn = expiresIn;
		_grantType = grantType;
		_issuedAt = issuedAt;
		_scopes = scopes;
		_tokenKey = tokenKey;
		_tokenType = tokenType;
		_userId = userId;
		_userName = userName;
	}

	public List<String> getAudiences() {
		return _audiences;
	}

	public String getClientCodeVerifier() {
		return _clientCodeVerifier;
	}

	public long getExpiresIn() {
		return _expiresIn;
	}

	public String getGrantType() {
		return _grantType;
	}

	public long getIssuedAt() {
		return _issuedAt;
	}

	public OAuth2Application getOAuth2Application() {
		return _oAuth2Application;
	}

	public List<String> getScopes() {
		return _scopes;
	}

	public String getTokenKey() {
		return _tokenKey;
	}

	public String getTokenType() {
		return _tokenType;
	}

	public long getUserId() {
		return _userId;
	}

	public String getUserName() {
		return _userName;
	}

	public void setAudiences(List<String> audiences) {
		_audiences = audiences;
	}

	public void setClientCodeVerifier(String clientCodeVerifier) {
		_clientCodeVerifier = clientCodeVerifier;
	}

	public void setExpiresIn(long expiresIn) {
		_expiresIn = expiresIn;
	}

	public void setGrantType(String grantType) {
		_grantType = grantType;
	}

	public void setIssuedAt(long issuedAt) {
		_issuedAt = issuedAt;
	}

	public void setOAuth2Application(OAuth2Application oAuth2Application) {
		_oAuth2Application = oAuth2Application;
	}

	public void setScopes(List<String> scopes) {
		_scopes = scopes;
	}

	public void setTokenKey(String tokenKey) {
		_tokenKey = tokenKey;
	}

	public void setTokenType(String tokenType) {
		_tokenType = tokenType;
	}

	public void setUserId(long userId) {
		_userId = userId;
	}

	public void setUserName(String userName) {
		_userName = userName;
	}

	private List<String> _audiences;
	private String _clientCodeVerifier;
	private long _expiresIn;
	private String _grantType;
	private long _issuedAt;
	private OAuth2Application _oAuth2Application;
	private List<String> _scopes;
	private String _tokenKey;
	private String _tokenType;
	private long _userId;
	private String _userName;

}
