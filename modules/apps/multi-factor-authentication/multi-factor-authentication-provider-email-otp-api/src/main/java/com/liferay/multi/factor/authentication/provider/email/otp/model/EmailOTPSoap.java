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

package com.liferay.multi.factor.authentication.provider.email.otp.model;

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
public class EmailOTPSoap implements Serializable {
	public static EmailOTPSoap toSoapModel(EmailOTP model) {
		EmailOTPSoap soapModel = new EmailOTPSoap();

		soapModel.setEmailOTPId(model.getEmailOTPId());
		soapModel.setCompanyId(model.getCompanyId());
		soapModel.setUserId(model.getUserId());
		soapModel.setUserName(model.getUserName());
		soapModel.setCreateDate(model.getCreateDate());
		soapModel.setModifiedDate(model.getModifiedDate());
		soapModel.setEmailAddress(model.getEmailAddress());
		soapModel.setLastSuccessDate(model.getLastSuccessDate());
		soapModel.setLastSuccessIP(model.getLastSuccessIP());
		soapModel.setLastFailDate(model.getLastFailDate());
		soapModel.setLastFailIP(model.getLastFailIP());
		soapModel.setFailedAttempts(model.getFailedAttempts());

		return soapModel;
	}

	public static EmailOTPSoap[] toSoapModels(EmailOTP[] models) {
		EmailOTPSoap[] soapModels = new EmailOTPSoap[models.length];

		for (int i = 0; i < models.length; i++) {
			soapModels[i] = toSoapModel(models[i]);
		}

		return soapModels;
	}

	public static EmailOTPSoap[][] toSoapModels(EmailOTP[][] models) {
		EmailOTPSoap[][] soapModels = null;

		if (models.length > 0) {
			soapModels = new EmailOTPSoap[models.length][models[0].length];
		}
		else {
			soapModels = new EmailOTPSoap[0][0];
		}

		for (int i = 0; i < models.length; i++) {
			soapModels[i] = toSoapModels(models[i]);
		}

		return soapModels;
	}

	public static EmailOTPSoap[] toSoapModels(List<EmailOTP> models) {
		List<EmailOTPSoap> soapModels = new ArrayList<EmailOTPSoap>(models.size());

		for (EmailOTP model : models) {
			soapModels.add(toSoapModel(model));
		}

		return soapModels.toArray(new EmailOTPSoap[soapModels.size()]);
	}

	public EmailOTPSoap() {
	}

	public long getPrimaryKey() {
		return _emailOTPId;
	}

	public void setPrimaryKey(long pk) {
		setEmailOTPId(pk);
	}

	public long getEmailOTPId() {
		return _emailOTPId;
	}

	public void setEmailOTPId(long emailOTPId) {
		_emailOTPId = emailOTPId;
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

	public String getEmailAddress() {
		return _emailAddress;
	}

	public void setEmailAddress(String emailAddress) {
		_emailAddress = emailAddress;
	}

	public Date getLastSuccessDate() {
		return _lastSuccessDate;
	}

	public void setLastSuccessDate(Date lastSuccessDate) {
		_lastSuccessDate = lastSuccessDate;
	}

	public String getLastSuccessIP() {
		return _lastSuccessIP;
	}

	public void setLastSuccessIP(String lastSuccessIP) {
		_lastSuccessIP = lastSuccessIP;
	}

	public Date getLastFailDate() {
		return _lastFailDate;
	}

	public void setLastFailDate(Date lastFailDate) {
		_lastFailDate = lastFailDate;
	}

	public String getLastFailIP() {
		return _lastFailIP;
	}

	public void setLastFailIP(String lastFailIP) {
		_lastFailIP = lastFailIP;
	}

	public int getFailedAttempts() {
		return _failedAttempts;
	}

	public void setFailedAttempts(int failedAttempts) {
		_failedAttempts = failedAttempts;
	}

	private long _emailOTPId;
	private long _companyId;
	private long _userId;
	private String _userName;
	private Date _createDate;
	private Date _modifiedDate;
	private String _emailAddress;
	private Date _lastSuccessDate;
	private String _lastSuccessIP;
	private Date _lastFailDate;
	private String _lastFailIP;
	private int _failedAttempts;
}