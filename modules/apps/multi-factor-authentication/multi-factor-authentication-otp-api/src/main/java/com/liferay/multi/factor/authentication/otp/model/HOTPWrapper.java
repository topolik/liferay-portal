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
 * This class is a wrapper for {@link HOTP}.
 * </p>
 *
 * @author arthurchan35
 * @see HOTP
 * @generated
 */
@ProviderType
public class HOTPWrapper extends BaseModelWrapper<HOTP> implements HOTP,
	ModelWrapper<HOTP> {
	public HOTPWrapper(HOTP hotp) {
		super(hotp);
	}

	@Override
	public Map<String, Object> getModelAttributes() {
		Map<String, Object> attributes = new HashMap<String, Object>();

		attributes.put("hotpId", getHotpId());
		attributes.put("userId", getUserId());
		attributes.put("count", getCount());
		attributes.put("failedAttempts", getFailedAttempts());
		attributes.put("sharedSecret", getSharedSecret());
		attributes.put("verified", isVerified());

		return attributes;
	}

	@Override
	public void setModelAttributes(Map<String, Object> attributes) {
		Long hotpId = (Long)attributes.get("hotpId");

		if (hotpId != null) {
			setHotpId(hotpId);
		}

		Long userId = (Long)attributes.get("userId");

		if (userId != null) {
			setUserId(userId);
		}

		Long count = (Long)attributes.get("count");

		if (count != null) {
			setCount(count);
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
	* Returns the count of this hotp.
	*
	* @return the count of this hotp
	*/
	@Override
	public long getCount() {
		return model.getCount();
	}

	/**
	* Returns the failed attempts of this hotp.
	*
	* @return the failed attempts of this hotp
	*/
	@Override
	public int getFailedAttempts() {
		return model.getFailedAttempts();
	}

	/**
	* Returns the hotp ID of this hotp.
	*
	* @return the hotp ID of this hotp
	*/
	@Override
	public long getHotpId() {
		return model.getHotpId();
	}

	/**
	* Returns the primary key of this hotp.
	*
	* @return the primary key of this hotp
	*/
	@Override
	public long getPrimaryKey() {
		return model.getPrimaryKey();
	}

	/**
	* Returns the shared secret of this hotp.
	*
	* @return the shared secret of this hotp
	*/
	@Override
	public String getSharedSecret() {
		return model.getSharedSecret();
	}

	/**
	* Returns the user ID of this hotp.
	*
	* @return the user ID of this hotp
	*/
	@Override
	public long getUserId() {
		return model.getUserId();
	}

	/**
	* Returns the user uuid of this hotp.
	*
	* @return the user uuid of this hotp
	*/
	@Override
	public String getUserUuid() {
		return model.getUserUuid();
	}

	/**
	* Returns the verified of this hotp.
	*
	* @return the verified of this hotp
	*/
	@Override
	public boolean getVerified() {
		return model.getVerified();
	}

	/**
	* Returns <code>true</code> if this hotp is verified.
	*
	* @return <code>true</code> if this hotp is verified; <code>false</code> otherwise
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
	* Sets the count of this hotp.
	*
	* @param count the count of this hotp
	*/
	@Override
	public void setCount(long count) {
		model.setCount(count);
	}

	/**
	* Sets the failed attempts of this hotp.
	*
	* @param failedAttempts the failed attempts of this hotp
	*/
	@Override
	public void setFailedAttempts(int failedAttempts) {
		model.setFailedAttempts(failedAttempts);
	}

	/**
	* Sets the hotp ID of this hotp.
	*
	* @param hotpId the hotp ID of this hotp
	*/
	@Override
	public void setHotpId(long hotpId) {
		model.setHotpId(hotpId);
	}

	/**
	* Sets the primary key of this hotp.
	*
	* @param primaryKey the primary key of this hotp
	*/
	@Override
	public void setPrimaryKey(long primaryKey) {
		model.setPrimaryKey(primaryKey);
	}

	/**
	* Sets the shared secret of this hotp.
	*
	* @param sharedSecret the shared secret of this hotp
	*/
	@Override
	public void setSharedSecret(String sharedSecret) {
		model.setSharedSecret(sharedSecret);
	}

	/**
	* Sets the user ID of this hotp.
	*
	* @param userId the user ID of this hotp
	*/
	@Override
	public void setUserId(long userId) {
		model.setUserId(userId);
	}

	/**
	* Sets the user uuid of this hotp.
	*
	* @param userUuid the user uuid of this hotp
	*/
	@Override
	public void setUserUuid(String userUuid) {
		model.setUserUuid(userUuid);
	}

	/**
	* Sets whether this hotp is verified.
	*
	* @param verified the verified of this hotp
	*/
	@Override
	public void setVerified(boolean verified) {
		model.setVerified(verified);
	}

	@Override
	protected HOTPWrapper wrap(HOTP hotp) {
		return new HOTPWrapper(hotp);
	}
}