package com.liferay.keymanager;

import java.io.Serializable;
import java.time.Instant;

public class KeyMetadata implements Serializable {

	private static final long serialVersionUID = 1L;

	private final String _alias;
	private final String _provider;
	private final Instant _createdAt;
	private final Instant _expiresAt;
	private final Instant _lastAccessedAt;
	private final String _keyType;
	private final int _version;
	private final boolean _rotatable;

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

	public String getAlias() { return _alias; }
	public String getProvider() { return _provider; }
	public Instant getCreatedAt() { return _createdAt; }
	public Instant getExpiresAt() { return _expiresAt; }
	public Instant getLastAccessedAt() { return _lastAccessedAt; }
	public String getKeyType() { return _keyType; }
	public int getVersion() { return _version; }
	public boolean isRotatable() { return _rotatable; }

	public boolean isExpired() {
		return _expiresAt != null && Instant.now().isAfter(_expiresAt);
	}

	public static class Builder {

		private String _alias;
		private String _provider;
		private Instant _createdAt = Instant.now();
		private Instant _expiresAt;
		private Instant _lastAccessedAt;
		private String _keyType = "SECRET";
		private int _version = 1;
		private boolean _rotatable = false;

		public Builder alias(String alias) { _alias = alias; return this; }
		public Builder provider(String provider) { _provider = provider; return this; }
		public Builder createdAt(Instant createdAt) { _createdAt = createdAt; return this; }
		public Builder expiresAt(Instant expiresAt) { _expiresAt = expiresAt; return this; }
		public Builder lastAccessedAt(Instant at) { _lastAccessedAt = at; return this; }
		public Builder keyType(String keyType) { _keyType = keyType; return this; }
		public Builder version(int version) { _version = version; return this; }
		public Builder rotatable(boolean rotatable) { _rotatable = rotatable; return this; }

		public KeyMetadata build() {
			return new KeyMetadata(this);
		}

	}

}
