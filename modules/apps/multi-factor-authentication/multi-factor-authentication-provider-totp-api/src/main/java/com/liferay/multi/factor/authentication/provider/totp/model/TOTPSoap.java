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

package com.liferay.multi.factor.authentication.provider.totp.model;

import aQute.bnd.annotation.ProviderType;

import java.io.Serializable;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * This class is used by SOAP remote services.
 *
 * @author arthurchan35
 * @generated
 */
@ProviderType
public class TOTPSoap implements Serializable {
	public static TOTPSoap toSoapModel(TOTP model) {
		TOTPSoap soapModel = new TOTPSoap();

		soapModel.setTotpId(model.getTotpId());
		soapModel.setCompanyId(model.getCompanyId());
		soapModel.setUserId(model.getUserId());
		soapModel.setUserName(model.getUserName());
		soapModel.setCreateDate(model.getCreateDate());
		soapModel.setModifiedDate(model.getModifiedDate());
		soapModel.setBackupCodes(model.getBackupCodes());
		soapModel.setLastLoginDate(model.getLastLoginDate());
		soapModel.setLastLoginIP(model.getLastLoginIP());
		soapModel.setLastFailedLoginDate(model.getLastFailedLoginDate());
		soapModel.setFailedLoginAttempts(model.getFailedLoginAttempts());
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

	public Date getModifiedDate() {
		return _modifiedDate;
	}

	public void setModifiedDate(Date modifiedDate) {
		_modifiedDate = modifiedDate;
	}

	public String getBackupCodes() {
		return _backupCodes;
	}

	public void setBackupCodes(String backupCodes) {
		_backupCodes = backupCodes;
	}

	public Date getLastLoginDate() {
		return _lastLoginDate;
	}

	public void setLastLoginDate(Date lastLoginDate) {
		_lastLoginDate = lastLoginDate;
	}

	public String getLastLoginIP() {
		return _lastLoginIP;
	}

	public void setLastLoginIP(String lastLoginIP) {
		_lastLoginIP = lastLoginIP;
	}

	public Date getLastFailedLoginDate() {
		return _lastFailedLoginDate;
	}

	public void setLastFailedLoginDate(Date lastFailedLoginDate) {
		_lastFailedLoginDate = lastFailedLoginDate;
	}

	public int getFailedLoginAttempts() {
		return _failedLoginAttempts;
	}

	public void setFailedLoginAttempts(int failedLoginAttempts) {
		_failedLoginAttempts = failedLoginAttempts;
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
	private long _companyId;
	private long _userId;
	private String _userName;
	private Date _createDate;
	private Date _modifiedDate;
	private String _backupCodes;
	private Date _lastLoginDate;
	private String _lastLoginIP;
	private Date _lastFailedLoginDate;
	private int _failedLoginAttempts;
	private String _sharedSecret;
	private boolean _verified;
}