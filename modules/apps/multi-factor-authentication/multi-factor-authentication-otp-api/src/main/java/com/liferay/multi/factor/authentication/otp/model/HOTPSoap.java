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
 * This class is used by SOAP remote services, specifically {@link com.liferay.multi.factor.authentication.otp.service.http.HOTPServiceSoap}.
 *
 * @author arthurchan35
 * @see com.liferay.multi.factor.authentication.otp.service.http.HOTPServiceSoap
 * @generated
 */
@ProviderType
public class HOTPSoap implements Serializable {
	public static HOTPSoap toSoapModel(HOTP model) {
		HOTPSoap soapModel = new HOTPSoap();

		soapModel.setHotpId(model.getHotpId());
		soapModel.setUserId(model.getUserId());
		soapModel.setCount(model.getCount());
		soapModel.setFailedAttempts(model.getFailedAttempts());
		soapModel.setSharedSecret(model.getSharedSecret());
		soapModel.setVerified(model.isVerified());

		return soapModel;
	}

	public static HOTPSoap[] toSoapModels(HOTP[] models) {
		HOTPSoap[] soapModels = new HOTPSoap[models.length];

		for (int i = 0; i < models.length; i++) {
			soapModels[i] = toSoapModel(models[i]);
		}

		return soapModels;
	}

	public static HOTPSoap[][] toSoapModels(HOTP[][] models) {
		HOTPSoap[][] soapModels = null;

		if (models.length > 0) {
			soapModels = new HOTPSoap[models.length][models[0].length];
		}
		else {
			soapModels = new HOTPSoap[0][0];
		}

		for (int i = 0; i < models.length; i++) {
			soapModels[i] = toSoapModels(models[i]);
		}

		return soapModels;
	}

	public static HOTPSoap[] toSoapModels(List<HOTP> models) {
		List<HOTPSoap> soapModels = new ArrayList<HOTPSoap>(models.size());

		for (HOTP model : models) {
			soapModels.add(toSoapModel(model));
		}

		return soapModels.toArray(new HOTPSoap[soapModels.size()]);
	}

	public HOTPSoap() {
	}

	public long getPrimaryKey() {
		return _hotpId;
	}

	public void setPrimaryKey(long pk) {
		setHotpId(pk);
	}

	public long getHotpId() {
		return _hotpId;
	}

	public void setHotpId(long hotpId) {
		_hotpId = hotpId;
	}

	public long getUserId() {
		return _userId;
	}

	public void setUserId(long userId) {
		_userId = userId;
	}

	public long getCount() {
		return _count;
	}

	public void setCount(long count) {
		_count = count;
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

	private long _hotpId;
	private long _userId;
	private long _count;
	private int _failedAttempts;
	private String _sharedSecret;
	private boolean _verified;
}