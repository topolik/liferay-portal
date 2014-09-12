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

package com.liferay.token.auth.model;


import java.util.Date;

/**
 * @author Tomas Polesovsky
 */
public class TokenSession {
	public Date getIssued() {
		return _issued;
	}

	public void setIssued(Date issued) {
		this._issued = issued;
	}

	public String getToken() {
		return _token;
	}

	public void setToken(String token) {
		this._token = token;
	}

	public String getTokenClientId() {
		return _tokenClientId;
	}

	public void setTokenClientId(String tokenClientId) {
		this._tokenClientId = tokenClientId;
	}

	public String getTokenType() {
		return _tokenType;
	}

	public void setTokenType(String tokenType) {
		this._tokenType = tokenType;
	}

	public long getUserId() {
		return _userId;
	}

	public void setUserId(long userId) {
		this._userId = userId;
	}

	private Date _issued;
	private String _token;
	private String _tokenType;
	private String _tokenClientId;
	private long _userId;

	@Override
	public String toString() {
		return "TokenSession{" +
			"_issued=" + _issued +
			", _token='" + _token + '\'' +
			", _tokenType='" + _tokenType + '\'' +
			", _tokenClientId='" + _tokenClientId + '\'' +
			", _userId=" + _userId +
			'}';
	}
}
