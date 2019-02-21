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

package com.liferay.multi.factor.authentication.provider.otp.model;

import aQute.bnd.annotation.ProviderType;

import com.liferay.portal.kernel.model.ModelWrapper;
import com.liferay.portal.kernel.model.wrapper.BaseModelWrapper;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * <p>
 * This class is a wrapper for {@link OTPEmail}.
 * </p>
 *
 * @author arthurchan35
 * @see OTPEmail
 * @generated
 */
@ProviderType
public class OTPEmailWrapper extends BaseModelWrapper<OTPEmail>
	implements OTPEmail, ModelWrapper<OTPEmail> {
	public OTPEmailWrapper(OTPEmail otpEmail) {
		super(otpEmail);
	}

	@Override
	public Map<String, Object> getModelAttributes() {
		Map<String, Object> attributes = new HashMap<String, Object>();

		attributes.put("otpEmailId", getOtpEmailId());
		attributes.put("companyId", getCompanyId());
		attributes.put("userId", getUserId());
		attributes.put("userName", getUserName());
		attributes.put("createDate", getCreateDate());
		attributes.put("modifiedDate", getModifiedDate());
		attributes.put("backupCodes", getBackupCodes());
		attributes.put("lastLoginDate", getLastLoginDate());
		attributes.put("lastLoginIP", getLastLoginIP());
		attributes.put("lastFailedLoginDate", getLastFailedLoginDate());
		attributes.put("failedLoginAttempts", getFailedLoginAttempts());
		attributes.put("verified", isVerified());

		return attributes;
	}

	@Override
	public void setModelAttributes(Map<String, Object> attributes) {
		Long otpEmailId = (Long)attributes.get("otpEmailId");

		if (otpEmailId != null) {
			setOtpEmailId(otpEmailId);
		}

		Long companyId = (Long)attributes.get("companyId");

		if (companyId != null) {
			setCompanyId(companyId);
		}

		Long userId = (Long)attributes.get("userId");

		if (userId != null) {
			setUserId(userId);
		}

		String userName = (String)attributes.get("userName");

		if (userName != null) {
			setUserName(userName);
		}

		Date createDate = (Date)attributes.get("createDate");

		if (createDate != null) {
			setCreateDate(createDate);
		}

		Date modifiedDate = (Date)attributes.get("modifiedDate");

		if (modifiedDate != null) {
			setModifiedDate(modifiedDate);
		}

		String backupCodes = (String)attributes.get("backupCodes");

		if (backupCodes != null) {
			setBackupCodes(backupCodes);
		}

		Date lastLoginDate = (Date)attributes.get("lastLoginDate");

		if (lastLoginDate != null) {
			setLastLoginDate(lastLoginDate);
		}

		String lastLoginIP = (String)attributes.get("lastLoginIP");

		if (lastLoginIP != null) {
			setLastLoginIP(lastLoginIP);
		}

		Date lastFailedLoginDate = (Date)attributes.get("lastFailedLoginDate");

		if (lastFailedLoginDate != null) {
			setLastFailedLoginDate(lastFailedLoginDate);
		}

		Integer failedLoginAttempts = (Integer)attributes.get(
				"failedLoginAttempts");

		if (failedLoginAttempts != null) {
			setFailedLoginAttempts(failedLoginAttempts);
		}

		Boolean verified = (Boolean)attributes.get("verified");

		if (verified != null) {
			setVerified(verified);
		}
	}

	/**
	* Returns the backup codes of this otp email.
	*
	* @return the backup codes of this otp email
	*/
	@Override
	public String getBackupCodes() {
		return model.getBackupCodes();
	}

	/**
	* Returns the company ID of this otp email.
	*
	* @return the company ID of this otp email
	*/
	@Override
	public long getCompanyId() {
		return model.getCompanyId();
	}

	/**
	* Returns the create date of this otp email.
	*
	* @return the create date of this otp email
	*/
	@Override
	public Date getCreateDate() {
		return model.getCreateDate();
	}

	/**
	* Returns the failed login attempts of this otp email.
	*
	* @return the failed login attempts of this otp email
	*/
	@Override
	public int getFailedLoginAttempts() {
		return model.getFailedLoginAttempts();
	}

	/**
	* Returns the last failed login date of this otp email.
	*
	* @return the last failed login date of this otp email
	*/
	@Override
	public Date getLastFailedLoginDate() {
		return model.getLastFailedLoginDate();
	}

	/**
	* Returns the last login date of this otp email.
	*
	* @return the last login date of this otp email
	*/
	@Override
	public Date getLastLoginDate() {
		return model.getLastLoginDate();
	}

	/**
	* Returns the last login ip of this otp email.
	*
	* @return the last login ip of this otp email
	*/
	@Override
	public String getLastLoginIP() {
		return model.getLastLoginIP();
	}

	/**
	* Returns the modified date of this otp email.
	*
	* @return the modified date of this otp email
	*/
	@Override
	public Date getModifiedDate() {
		return model.getModifiedDate();
	}

	/**
	* Returns the otp email ID of this otp email.
	*
	* @return the otp email ID of this otp email
	*/
	@Override
	public long getOtpEmailId() {
		return model.getOtpEmailId();
	}

	/**
	* Returns the primary key of this otp email.
	*
	* @return the primary key of this otp email
	*/
	@Override
	public long getPrimaryKey() {
		return model.getPrimaryKey();
	}

	/**
	* Returns the user ID of this otp email.
	*
	* @return the user ID of this otp email
	*/
	@Override
	public long getUserId() {
		return model.getUserId();
	}

	/**
	* Returns the user name of this otp email.
	*
	* @return the user name of this otp email
	*/
	@Override
	public String getUserName() {
		return model.getUserName();
	}

	/**
	* Returns the user uuid of this otp email.
	*
	* @return the user uuid of this otp email
	*/
	@Override
	public String getUserUuid() {
		return model.getUserUuid();
	}

	/**
	* Returns the verified of this otp email.
	*
	* @return the verified of this otp email
	*/
	@Override
	public boolean getVerified() {
		return model.getVerified();
	}

	/**
	* Returns <code>true</code> if this otp email is verified.
	*
	* @return <code>true</code> if this otp email is verified; <code>false</code> otherwise
	*/
	@Override
	public boolean isVerified() {
		return model.isVerified();
	}

	@Override
	public void persist() {
		model.persist();
	}

	/**
	* Sets the backup codes of this otp email.
	*
	* @param backupCodes the backup codes of this otp email
	*/
	@Override
	public void setBackupCodes(String backupCodes) {
		model.setBackupCodes(backupCodes);
	}

	/**
	* Sets the company ID of this otp email.
	*
	* @param companyId the company ID of this otp email
	*/
	@Override
	public void setCompanyId(long companyId) {
		model.setCompanyId(companyId);
	}

	/**
	* Sets the create date of this otp email.
	*
	* @param createDate the create date of this otp email
	*/
	@Override
	public void setCreateDate(Date createDate) {
		model.setCreateDate(createDate);
	}

	/**
	* Sets the failed login attempts of this otp email.
	*
	* @param failedLoginAttempts the failed login attempts of this otp email
	*/
	@Override
	public void setFailedLoginAttempts(int failedLoginAttempts) {
		model.setFailedLoginAttempts(failedLoginAttempts);
	}

	/**
	* Sets the last failed login date of this otp email.
	*
	* @param lastFailedLoginDate the last failed login date of this otp email
	*/
	@Override
	public void setLastFailedLoginDate(Date lastFailedLoginDate) {
		model.setLastFailedLoginDate(lastFailedLoginDate);
	}

	/**
	* Sets the last login date of this otp email.
	*
	* @param lastLoginDate the last login date of this otp email
	*/
	@Override
	public void setLastLoginDate(Date lastLoginDate) {
		model.setLastLoginDate(lastLoginDate);
	}

	/**
	* Sets the last login ip of this otp email.
	*
	* @param lastLoginIP the last login ip of this otp email
	*/
	@Override
	public void setLastLoginIP(String lastLoginIP) {
		model.setLastLoginIP(lastLoginIP);
	}

	/**
	* Sets the modified date of this otp email.
	*
	* @param modifiedDate the modified date of this otp email
	*/
	@Override
	public void setModifiedDate(Date modifiedDate) {
		model.setModifiedDate(modifiedDate);
	}

	/**
	* Sets the otp email ID of this otp email.
	*
	* @param otpEmailId the otp email ID of this otp email
	*/
	@Override
	public void setOtpEmailId(long otpEmailId) {
		model.setOtpEmailId(otpEmailId);
	}

	/**
	* Sets the primary key of this otp email.
	*
	* @param primaryKey the primary key of this otp email
	*/
	@Override
	public void setPrimaryKey(long primaryKey) {
		model.setPrimaryKey(primaryKey);
	}

	/**
	* Sets the user ID of this otp email.
	*
	* @param userId the user ID of this otp email
	*/
	@Override
	public void setUserId(long userId) {
		model.setUserId(userId);
	}

	/**
	* Sets the user name of this otp email.
	*
	* @param userName the user name of this otp email
	*/
	@Override
	public void setUserName(String userName) {
		model.setUserName(userName);
	}

	/**
	* Sets the user uuid of this otp email.
	*
	* @param userUuid the user uuid of this otp email
	*/
	@Override
	public void setUserUuid(String userUuid) {
		model.setUserUuid(userUuid);
	}

	/**
	* Sets whether this otp email is verified.
	*
	* @param verified the verified of this otp email
	*/
	@Override
	public void setVerified(boolean verified) {
		model.setVerified(verified);
	}

	@Override
	protected OTPEmailWrapper wrap(OTPEmail otpEmail) {
		return new OTPEmailWrapper(otpEmail);
	}
}