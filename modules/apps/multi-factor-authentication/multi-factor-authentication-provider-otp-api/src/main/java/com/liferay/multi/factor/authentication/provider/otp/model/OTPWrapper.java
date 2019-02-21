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
 * This class is a wrapper for {@link OTP}.
 * </p>
 *
 * @author arthurchan35
 * @see OTP
 * @generated
 */
@ProviderType
public class OTPWrapper extends BaseModelWrapper<OTP> implements OTP,
	ModelWrapper<OTP> {
	public OTPWrapper(OTP otp) {
		super(otp);
	}

	@Override
	public Map<String, Object> getModelAttributes() {
		Map<String, Object> attributes = new HashMap<String, Object>();

		attributes.put("otpId", getOtpId());
		attributes.put("companyId", getCompanyId());
		attributes.put("userId", getUserId());
		attributes.put("userName", getUserName());
		attributes.put("createDate", getCreateDate());
		attributes.put("modifiedDate", getModifiedDate());
		attributes.put("emailAddress", getEmailAddress());
		attributes.put("lastLoginDate", getLastLoginDate());
		attributes.put("lastLoginIP", getLastLoginIP());
		attributes.put("lastFailedLoginDate", getLastFailedLoginDate());
		attributes.put("failedLoginAttempts", getFailedLoginAttempts());
		attributes.put("verified", isVerified());

		return attributes;
	}

	@Override
	public void setModelAttributes(Map<String, Object> attributes) {
		Long otpId = (Long)attributes.get("otpId");

		if (otpId != null) {
			setOtpId(otpId);
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

		String emailAddress = (String)attributes.get("emailAddress");

		if (emailAddress != null) {
			setEmailAddress(emailAddress);
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
	* Returns the company ID of this otp.
	*
	* @return the company ID of this otp
	*/
	@Override
	public long getCompanyId() {
		return model.getCompanyId();
	}

	/**
	* Returns the create date of this otp.
	*
	* @return the create date of this otp
	*/
	@Override
	public Date getCreateDate() {
		return model.getCreateDate();
	}

	/**
	* Returns the email address of this otp.
	*
	* @return the email address of this otp
	*/
	@Override
	public String getEmailAddress() {
		return model.getEmailAddress();
	}

	/**
	* Returns the failed login attempts of this otp.
	*
	* @return the failed login attempts of this otp
	*/
	@Override
	public int getFailedLoginAttempts() {
		return model.getFailedLoginAttempts();
	}

	/**
	* Returns the last failed login date of this otp.
	*
	* @return the last failed login date of this otp
	*/
	@Override
	public Date getLastFailedLoginDate() {
		return model.getLastFailedLoginDate();
	}

	/**
	* Returns the last login date of this otp.
	*
	* @return the last login date of this otp
	*/
	@Override
	public Date getLastLoginDate() {
		return model.getLastLoginDate();
	}

	/**
	* Returns the last login ip of this otp.
	*
	* @return the last login ip of this otp
	*/
	@Override
	public String getLastLoginIP() {
		return model.getLastLoginIP();
	}

	/**
	* Returns the modified date of this otp.
	*
	* @return the modified date of this otp
	*/
	@Override
	public Date getModifiedDate() {
		return model.getModifiedDate();
	}

	/**
	* Returns the otp ID of this otp.
	*
	* @return the otp ID of this otp
	*/
	@Override
	public long getOtpId() {
		return model.getOtpId();
	}

	/**
	* Returns the primary key of this otp.
	*
	* @return the primary key of this otp
	*/
	@Override
	public long getPrimaryKey() {
		return model.getPrimaryKey();
	}

	/**
	* Returns the user ID of this otp.
	*
	* @return the user ID of this otp
	*/
	@Override
	public long getUserId() {
		return model.getUserId();
	}

	/**
	* Returns the user name of this otp.
	*
	* @return the user name of this otp
	*/
	@Override
	public String getUserName() {
		return model.getUserName();
	}

	/**
	* Returns the user uuid of this otp.
	*
	* @return the user uuid of this otp
	*/
	@Override
	public String getUserUuid() {
		return model.getUserUuid();
	}

	/**
	* Returns the verified of this otp.
	*
	* @return the verified of this otp
	*/
	@Override
	public boolean getVerified() {
		return model.getVerified();
	}

	/**
	* Returns <code>true</code> if this otp is verified.
	*
	* @return <code>true</code> if this otp is verified; <code>false</code> otherwise
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
	* Sets the company ID of this otp.
	*
	* @param companyId the company ID of this otp
	*/
	@Override
	public void setCompanyId(long companyId) {
		model.setCompanyId(companyId);
	}

	/**
	* Sets the create date of this otp.
	*
	* @param createDate the create date of this otp
	*/
	@Override
	public void setCreateDate(Date createDate) {
		model.setCreateDate(createDate);
	}

	/**
	* Sets the email address of this otp.
	*
	* @param emailAddress the email address of this otp
	*/
	@Override
	public void setEmailAddress(String emailAddress) {
		model.setEmailAddress(emailAddress);
	}

	/**
	* Sets the failed login attempts of this otp.
	*
	* @param failedLoginAttempts the failed login attempts of this otp
	*/
	@Override
	public void setFailedLoginAttempts(int failedLoginAttempts) {
		model.setFailedLoginAttempts(failedLoginAttempts);
	}

	/**
	* Sets the last failed login date of this otp.
	*
	* @param lastFailedLoginDate the last failed login date of this otp
	*/
	@Override
	public void setLastFailedLoginDate(Date lastFailedLoginDate) {
		model.setLastFailedLoginDate(lastFailedLoginDate);
	}

	/**
	* Sets the last login date of this otp.
	*
	* @param lastLoginDate the last login date of this otp
	*/
	@Override
	public void setLastLoginDate(Date lastLoginDate) {
		model.setLastLoginDate(lastLoginDate);
	}

	/**
	* Sets the last login ip of this otp.
	*
	* @param lastLoginIP the last login ip of this otp
	*/
	@Override
	public void setLastLoginIP(String lastLoginIP) {
		model.setLastLoginIP(lastLoginIP);
	}

	/**
	* Sets the modified date of this otp.
	*
	* @param modifiedDate the modified date of this otp
	*/
	@Override
	public void setModifiedDate(Date modifiedDate) {
		model.setModifiedDate(modifiedDate);
	}

	/**
	* Sets the otp ID of this otp.
	*
	* @param otpId the otp ID of this otp
	*/
	@Override
	public void setOtpId(long otpId) {
		model.setOtpId(otpId);
	}

	/**
	* Sets the primary key of this otp.
	*
	* @param primaryKey the primary key of this otp
	*/
	@Override
	public void setPrimaryKey(long primaryKey) {
		model.setPrimaryKey(primaryKey);
	}

	/**
	* Sets the user ID of this otp.
	*
	* @param userId the user ID of this otp
	*/
	@Override
	public void setUserId(long userId) {
		model.setUserId(userId);
	}

	/**
	* Sets the user name of this otp.
	*
	* @param userName the user name of this otp
	*/
	@Override
	public void setUserName(String userName) {
		model.setUserName(userName);
	}

	/**
	* Sets the user uuid of this otp.
	*
	* @param userUuid the user uuid of this otp
	*/
	@Override
	public void setUserUuid(String userUuid) {
		model.setUserUuid(userUuid);
	}

	/**
	* Sets whether this otp is verified.
	*
	* @param verified the verified of this otp
	*/
	@Override
	public void setVerified(boolean verified) {
		model.setVerified(verified);
	}

	@Override
	protected OTPWrapper wrap(OTP otp) {
		return new OTPWrapper(otp);
	}
}