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

package com.liferay.multi.factor.authentication.otp.model;

import aQute.bnd.annotation.ProviderType;

import java.io.Serializable;

import java.util.ArrayList;
import java.util.List;

/**
 * This class is used by SOAP remote services, specifically {@link com.liferay.multi.factor.authentication.otp.service.http.TOTPServiceSoap}.
 *
 * @author arthurchan35
 * @see com.liferay.multi.factor.authentication.otp.service.http.TOTPServiceSoap
 * @generated
 */
@ProviderType
public class TOTPSoap implements Serializable {
	public static TOTPSoap toSoapModel(TOTP model) {
		TOTPSoap soapModel = new TOTPSoap();

		soapModel.setTotpId(model.getTotpId());
		soapModel.setUserId(model.getUserId());
		soapModel.setFailedAttempts(model.getFailedAttempts());
		soapModel.setSharedSecret(model.getSharedSecret());
		soapModel.setVerified(model.isVerified());

		return soapModel;
	}

	public static TOTPSoap[] toSoapModels(TOTP[] models) {
		TOTPSoap[] soapModels = new TOTPSoap[models.length];

		for (int i = 0; i < models.length; i++) {
			soapModels[i] = toSoapModel(models[i]);
		}

		return soapModels;
	}

	public static TOTPSoap[][] toSoapModels(TOTP[][] models) {
		TOTPSoap[][] soapModels = null;

		if (models.length > 0) {
			soapModels = new TOTPSoap[models.length][models[0].length];
		}
		else {
			soapModels = new TOTPSoap[0][0];
		}

		for (int i = 0; i < models.length; i++) {
			soapModels[i] = toSoapModels(models[i]);
		}

		return soapModels;
	}

	public static TOTPSoap[] toSoapModels(List<TOTP> models) {
		List<TOTPSoap> soapModels = new ArrayList<TOTPSoap>(models.size());

		for (TOTP model : models) {
			soapModels.add(toSoapModel(model));
		}

		return soapModels.toArray(new TOTPSoap[soapModels.size()]);
	}

	public TOTPSoap() {
	}

	public long getPrimaryKey() {
		return _totpId;
	}

	public void setPrimaryKey(long pk) {
		setTotpId(pk);
	}

	public long getTotpId() {
		return _totpId;
	}

	public void setTotpId(long totpId) {
		_totpId = totpId;
	}

	public long getUserId() {
		return _userId;
	}

	public void setUserId(long userId) {
		_userId = userId;
	}

	public int getFailedAttempts() {
		return _failedAttempts;
	}

	public void setFailedAttempts(int failedAttempts) {
		_failedAttempts = failedAttempts;
	}

	public String getSharedSecret() {
		return _sharedSecret;
	}

	public void setSharedSecret(String sharedSecret) {
		_sharedSecret = sharedSecret;
	}

	public boolean getVerified() {
		return _verified;
	}

	public boolean isVerified() {
		return _verified;
	}

	public void setVerified(boolean verified) {
		_verified = verified;
	}

	private long _totpId;
	private long _userId;
	private int _failedAttempts;
	private String _sharedSecret;
	private boolean _verified;
}