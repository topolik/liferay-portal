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

package com.liferay.multi.factor.authentication.provider.time.otp.model;

import aQute.bnd.annotation.ProviderType;

import com.liferay.portal.kernel.model.ModelWrapper;
import com.liferay.portal.kernel.model.wrapper.BaseModelWrapper;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * <p>
 * This class is a wrapper for {@link TimeOTPEntry}.
 * </p>
 *
 * @author arthurchan35
 * @see TimeOTPEntry
 * @generated
 */
@ProviderType
public class TimeOTPEntryWrapper
	extends BaseModelWrapper<TimeOTPEntry>
	implements TimeOTPEntry, ModelWrapper<TimeOTPEntry> {

	public TimeOTPEntryWrapper(TimeOTPEntry timeOTPEntry) {
		super(timeOTPEntry);
	}

	@Override
	public Map<String, Object> getModelAttributes() {
		Map<String, Object> attributes = new HashMap<String, Object>();

		attributes.put("entryId", getEntryId());
		attributes.put("companyId", getCompanyId());
		attributes.put("userId", getUserId());
		attributes.put("userName", getUserName());
		attributes.put("createDate", getCreateDate());
		attributes.put("modifiedDate", getModifiedDate());
		attributes.put("sharedSecret", getSharedSecret());
		attributes.put("lastSuccessDate", getLastSuccessDate());
		attributes.put("lastSuccessIP", getLastSuccessIP());
		attributes.put("lastFailDate", getLastFailDate());
		attributes.put("lastFailIP", getLastFailIP());
		attributes.put("failedAttempts", getFailedAttempts());

		return attributes;
	}

	@Override
	public void setModelAttributes(Map<String, Object> attributes) {
		Long entryId = (Long)attributes.get("entryId");

		if (entryId != null) {
			setEntryId(entryId);
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

		String sharedSecret = (String)attributes.get("sharedSecret");

		if (sharedSecret != null) {
			setSharedSecret(sharedSecret);
		}

		Date lastSuccessDate = (Date)attributes.get("lastSuccessDate");

		if (lastSuccessDate != null) {
			setLastSuccessDate(lastSuccessDate);
		}

		String lastSuccessIP = (String)attributes.get("lastSuccessIP");

		if (lastSuccessIP != null) {
			setLastSuccessIP(lastSuccessIP);
		}

		Date lastFailDate = (Date)attributes.get("lastFailDate");

		if (lastFailDate != null) {
			setLastFailDate(lastFailDate);
		}

		String lastFailIP = (String)attributes.get("lastFailIP");

		if (lastFailIP != null) {
			setLastFailIP(lastFailIP);
		}

		Integer failedAttempts = (Integer)attributes.get("failedAttempts");

		if (failedAttempts != null) {
			setFailedAttempts(failedAttempts);
		}
	}

	/**
	 * Returns the company ID of this time otp entry.
	 *
	 * @return the company ID of this time otp entry
	 */
	@Override
	public long getCompanyId() {
		return model.getCompanyId();
	}

	/**
	 * Returns the create date of this time otp entry.
	 *
	 * @return the create date of this time otp entry
	 */
	@Override
	public Date getCreateDate() {
		return model.getCreateDate();
	}

	/**
	 * Returns the entry ID of this time otp entry.
	 *
	 * @return the entry ID of this time otp entry
	 */
	@Override
	public long getEntryId() {
		return model.getEntryId();
	}

	/**
	 * Returns the failed attempts of this time otp entry.
	 *
	 * @return the failed attempts of this time otp entry
	 */
	@Override
	public int getFailedAttempts() {
		return model.getFailedAttempts();
	}

	/**
	 * Returns the last fail date of this time otp entry.
	 *
	 * @return the last fail date of this time otp entry
	 */
	@Override
	public Date getLastFailDate() {
		return model.getLastFailDate();
	}

	/**
	 * Returns the last fail ip of this time otp entry.
	 *
	 * @return the last fail ip of this time otp entry
	 */
	@Override
	public String getLastFailIP() {
		return model.getLastFailIP();
	}

	/**
	 * Returns the last success date of this time otp entry.
	 *
	 * @return the last success date of this time otp entry
	 */
	@Override
	public Date getLastSuccessDate() {
		return model.getLastSuccessDate();
	}

	/**
	 * Returns the last success ip of this time otp entry.
	 *
	 * @return the last success ip of this time otp entry
	 */
	@Override
	public String getLastSuccessIP() {
		return model.getLastSuccessIP();
	}

	/**
	 * Returns the modified date of this time otp entry.
	 *
	 * @return the modified date of this time otp entry
	 */
	@Override
	public Date getModifiedDate() {
		return model.getModifiedDate();
	}

	/**
	 * Returns the primary key of this time otp entry.
	 *
	 * @return the primary key of this time otp entry
	 */
	@Override
	public long getPrimaryKey() {
		return model.getPrimaryKey();
	}

	/**
	 * Returns the shared secret of this time otp entry.
	 *
	 * @return the shared secret of this time otp entry
	 */
	@Override
	public String getSharedSecret() {
		return model.getSharedSecret();
	}

	/**
	 * Returns the user ID of this time otp entry.
	 *
	 * @return the user ID of this time otp entry
	 */
	@Override
	public long getUserId() {
		return model.getUserId();
	}

	/**
	 * Returns the user name of this time otp entry.
	 *
	 * @return the user name of this time otp entry
	 */
	@Override
	public String getUserName() {
		return model.getUserName();
	}

	/**
	 * Returns the user uuid of this time otp entry.
	 *
	 * @return the user uuid of this time otp entry
	 */
	@Override
	public String getUserUuid() {
		return model.getUserUuid();
	}

	@Override
	public void persist() {
		model.persist();
	}

	/**
	 * Sets the company ID of this time otp entry.
	 *
	 * @param companyId the company ID of this time otp entry
	 */
	@Override
	public void setCompanyId(long companyId) {
		model.setCompanyId(companyId);
	}

	/**
	 * Sets the create date of this time otp entry.
	 *
	 * @param createDate the create date of this time otp entry
	 */
	@Override
	public void setCreateDate(Date createDate) {
		model.setCreateDate(createDate);
	}

	/**
	 * Sets the entry ID of this time otp entry.
	 *
	 * @param entryId the entry ID of this time otp entry
	 */
	@Override
	public void setEntryId(long entryId) {
		model.setEntryId(entryId);
	}

	/**
	 * Sets the failed attempts of this time otp entry.
	 *
	 * @param failedAttempts the failed attempts of this time otp entry
	 */
	@Override
	public void setFailedAttempts(int failedAttempts) {
		model.setFailedAttempts(failedAttempts);
	}

	/**
	 * Sets the last fail date of this time otp entry.
	 *
	 * @param lastFailDate the last fail date of this time otp entry
	 */
	@Override
	public void setLastFailDate(Date lastFailDate) {
		model.setLastFailDate(lastFailDate);
	}

	/**
	 * Sets the last fail ip of this time otp entry.
	 *
	 * @param lastFailIP the last fail ip of this time otp entry
	 */
	@Override
	public void setLastFailIP(String lastFailIP) {
		model.setLastFailIP(lastFailIP);
	}

	/**
	 * Sets the last success date of this time otp entry.
	 *
	 * @param lastSuccessDate the last success date of this time otp entry
	 */
	@Override
	public void setLastSuccessDate(Date lastSuccessDate) {
		model.setLastSuccessDate(lastSuccessDate);
	}

	/**
	 * Sets the last success ip of this time otp entry.
	 *
	 * @param lastSuccessIP the last success ip of this time otp entry
	 */
	@Override
	public void setLastSuccessIP(String lastSuccessIP) {
		model.setLastSuccessIP(lastSuccessIP);
	}

	/**
	 * Sets the modified date of this time otp entry.
	 *
	 * @param modifiedDate the modified date of this time otp entry
	 */
	@Override
	public void setModifiedDate(Date modifiedDate) {
		model.setModifiedDate(modifiedDate);
	}

	/**
	 * Sets the primary key of this time otp entry.
	 *
	 * @param primaryKey the primary key of this time otp entry
	 */
	@Override
	public void setPrimaryKey(long primaryKey) {
		model.setPrimaryKey(primaryKey);
	}

	/**
	 * Sets the shared secret of this time otp entry.
	 *
	 * @param sharedSecret the shared secret of this time otp entry
	 */
	@Override
	public void setSharedSecret(String sharedSecret) {
		model.setSharedSecret(sharedSecret);
	}

	/**
	 * Sets the user ID of this time otp entry.
	 *
	 * @param userId the user ID of this time otp entry
	 */
	@Override
	public void setUserId(long userId) {
		model.setUserId(userId);
	}

	/**
	 * Sets the user name of this time otp entry.
	 *
	 * @param userName the user name of this time otp entry
	 */
	@Override
	public void setUserName(String userName) {
		model.setUserName(userName);
	}

	/**
	 * Sets the user uuid of this time otp entry.
	 *
	 * @param userUuid the user uuid of this time otp entry
	 */
	@Override
	public void setUserUuid(String userUuid) {
		model.setUserUuid(userUuid);
	}

	@Override
	protected TimeOTPEntryWrapper wrap(TimeOTPEntry timeOTPEntry) {
		return new TimeOTPEntryWrapper(timeOTPEntry);
	}

}