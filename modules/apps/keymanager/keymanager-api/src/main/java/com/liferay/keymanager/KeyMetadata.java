/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.keymanager;

import java.io.Serializable;

import java.time.Instant;

/**
 * Metadata for a key managed by a key provider.
 *
 * @author Tomas Polesovsky
 */
public class KeyMetadata implements Serializable {

	public String getAlias() {
		return _alias;
	}

	public Instant getCreatedAt() {
		return _createdAt;
	}

	public Instant getExpiresAt() {
		return _expiresAt;
	}

	public String getKeyType() {
		return _keyType;
	}

	public Instant getLastAccessedAt() {
		return _lastAccessedAt;
	}

	public String getProvider() {
		return _provider;
	}

	public int getVersion() {
		return _version;
	}

	public boolean isExpired() {
		if ((_expiresAt != null) && Instant.now().isAfter(_expiresAt)) {
			return true;
		}

		return false;
	}

	public boolean isRotatable() {
		return _rotatable;
	}

	public static class Builder {

		public Builder alias(String alias) {
			_alias = alias;

			return this;
		}

		public KeyMetadata build() {
			return new KeyMetadata(this);
		}

		public Builder createdAt(Instant createdAt) {
			_createdAt = createdAt;

			return this;
		}

		public Builder expiresAt(Instant expiresAt) {
			_expiresAt = expiresAt;

			return this;
		}

		public Builder keyType(String keyType) {
			_keyType = keyType;

			return this;
		}

		public Builder lastAccessedAt(Instant at) {
			_lastAccessedAt = at;

			return this;
		}

		public Builder provider(String provider) {
			_provider = provider;

			return this;
		}

		public Builder rotatable(boolean rotatable) {
			_rotatable = rotatable;

			return this;
		}

		public Builder version(int version) {
			_version = version;

			return this;
		}

		private String _alias;
		private Instant _createdAt = Instant.now();
		private Instant _expiresAt;
		private String _keyType = "SECRET";
		private Instant _lastAccessedAt;
		private String _provider;
		private boolean _rotatable;
		private int _version = 1;

	}

	private KeyMetadata(Builder builder) {
		_alias = builder._alias;
		_provider = builder._provider;
		_createdAt = builder._createdAt;
		_expiresAt = builder._expiresAt;
		_lastAccessedAt = builder._lastAccessedAt;
		_keyType = builder._keyType;
		_version = builder._version;
		_rotatable = builder._rotatable;
	}

	private static final long serialVersionUID = 1L;

	private final String _alias;
	private final Instant _createdAt;
	private final Instant _expiresAt;
	private final String _keyType;
	private final Instant _lastAccessedAt;
	private final String _provider;
	private final boolean _rotatable;
	private final int _version;

}