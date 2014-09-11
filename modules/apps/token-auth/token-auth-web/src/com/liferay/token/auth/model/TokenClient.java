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

import java.util.Properties;

/**
 * @author Tomas Polesovsky
 */
public class TokenClient implements Cloneable {

	@Override
	public TokenClient clone() {
		TokenClient result = new TokenClient();
		result._clientId = _clientId;
		result._clientName = _clientName;
		result._clientPolicy = new Properties(_clientPolicy);
		result._companyId = _companyId;
		result._state = _state;
		result._ownerId = _ownerId;
		return result;
	}

	public String getClientId() {
		return _clientId;
	}

	public void setClientId(String clientId) {
		this._clientId = clientId;
	}

	public String getClientName() {
		return _clientName;
	}

	public void setClientName(String clientName) {
		this._clientName = clientName;
	}

	public Properties getClientPolicy() {
		return _clientPolicy;
	}

	public void setClientPolicy(Properties clientPolicy) {
		this._clientPolicy = clientPolicy;
	}

	public long getCompanyId() {
		return _companyId;
	}

	public void setCompanyId(long companyId) {
		this._companyId = companyId;
	}

	public State getState() {
		return _state;
	}

	public void setState(State state) {
		this._state = state;
	}

	public long getOwnerId() {
		return _ownerId;
	}

	public void setOwnerId(long ownerId) {
		this._ownerId = ownerId;
	}

	public enum State {

		VALID, REVOKED

	}

	private String _clientId;
	private String _clientName;
	private Properties _clientPolicy;
	private long _companyId;
	private State _state = State.VALID;
	private long _ownerId;
}
