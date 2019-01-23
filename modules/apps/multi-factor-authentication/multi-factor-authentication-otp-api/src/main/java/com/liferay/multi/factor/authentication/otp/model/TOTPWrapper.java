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

import com.liferay.portal.kernel.model.ModelWrapper;
import com.liferay.portal.kernel.model.wrapper.BaseModelWrapper;

import java.util.HashMap;
import java.util.Map;

/**
 * <p>
 * This class is a wrapper for {@link TOTP}.
 * </p>
 *
 * @author arthurchan35
 * @see TOTP
 * @generated
 */
@ProviderType
public class TOTPWrapper extends BaseModelWrapper<TOTP> implements TOTP,
	ModelWrapper<TOTP> {
	public TOTPWrapper(TOTP totp) {
		super(totp);
	}

	@Override
	public Map<String, Object> getModelAttributes() {
		Map<String, Object> attributes = new HashMap<String, Object>();

		attributes.put("totpId", getTotpId());
		attributes.put("userId", getUserId());
		attributes.put("failedAttempts", getFailedAttempts());
		attributes.put("sharedSecret", getSharedSecret());
		attributes.put("verified", isVerified());

		return attributes;
	}

	@Override
	public void setModelAttributes(Map<String, Object> attributes) {
		Long totpId = (Long)attributes.get("totpId");

		if (totpId != null) {
			setTotpId(totpId);
		}

		Long userId = (Long)attributes.get("userId");

		if (userId != null) {
			setUserId(userId);
		}

		Integer failedAttempts = (Integer)attributes.get("failedAttempts");

		if (failedAttempts != null) {
			setFailedAttempts(failedAttempts);
		}

		String sharedSecret = (String)attributes.get("sharedSecret");

		if (sharedSecret != null) {
			setSharedSecret(sharedSecret);
		}

		Boolean verified = (Boolean)attributes.get("verified");

		if (verified != null) {
			setVerified(verified);
		}
	}

	/**
	* Returns the failed attempts of this totp.
	*
	* @return the failed attempts of this totp
	*/
	@Override
	public int getFailedAttempts() {
		return model.getFailedAttempts();
	}

	/**
	* Returns the primary key of this totp.
	*
	* @return the primary key of this totp
	*/
	@Override
	public long getPrimaryKey() {
		return model.getPrimaryKey();
	}

	/**
	* Returns the shared secret of this totp.
	*
	* @return the shared secret of this totp
	*/
	@Override
	public String getSharedSecret() {
		return model.getSharedSecret();
	}

	/**
	* Returns the totp ID of this totp.
	*
	* @return the totp ID of this totp
	*/
	@Override
	public long getTotpId() {
		return model.getTotpId();
	}

	/**
	* Returns the user ID of this totp.
	*
	* @return the user ID of this totp
	*/
	@Override
	public long getUserId() {
		return model.getUserId();
	}

	/**
	* Returns the user uuid of this totp.
	*
	* @return the user uuid of this totp
	*/
	@Override
	public String getUserUuid() {
		return model.getUserUuid();
	}

	/**
	* Returns the verified of this totp.
	*
	* @return the verified of this totp
	*/
	@Override
	public boolean getVerified() {
		return model.getVerified();
	}

	/**
	* Returns <code>true</code> if this totp is verified.
	*
	* @return <code>true</code> if this totp is verified; <code>false</code> otherwise
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
	* Sets the failed attempts of this totp.
	*
	* @param failedAttempts the failed attempts of this totp
	*/
	@Override
	public void setFailedAttempts(int failedAttempts) {
		model.setFailedAttempts(failedAttempts);
	}

	/**
	* Sets the primary key of this totp.
	*
	* @param primaryKey the primary key of this totp
	*/
	@Override
	public void setPrimaryKey(long primaryKey) {
		model.setPrimaryKey(primaryKey);
	}

	/**
	* Sets the shared secret of this totp.
	*
	* @param sharedSecret the shared secret of this totp
	*/
	@Override
	public void setSharedSecret(String sharedSecret) {
		model.setSharedSecret(sharedSecret);
	}

	/**
	* Sets the totp ID of this totp.
	*
	* @param totpId the totp ID of this totp
	*/
	@Override
	public void setTotpId(long totpId) {
		model.setTotpId(totpId);
	}

	/**
	* Sets the user ID of this totp.
	*
	* @param userId the user ID of this totp
	*/
	@Override
	public void setUserId(long userId) {
		model.setUserId(userId);
	}

	/**
	* Sets the user uuid of this totp.
	*
	* @param userUuid the user uuid of this totp
	*/
	@Override
	public void setUserUuid(String userUuid) {
		model.setUserUuid(userUuid);
	}

	/**
	* Sets whether this totp is verified.
	*
	* @param verified the verified of this totp
	*/
	@Override
	public void setVerified(boolean verified) {
		model.setVerified(verified);
	}

	@Override
	protected TOTPWrapper wrap(TOTP totp) {
		return new TOTPWrapper(totp);
	}
}