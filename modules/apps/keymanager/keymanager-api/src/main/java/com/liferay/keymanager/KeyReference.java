package com.liferay.keymanager;

import java.io.Serializable;
import java.util.Objects;

/**
 * Represents a parsed key reference.
 * A key reference string looks like: ${keyref:provider/alias}
 */
public class KeyReference implements Serializable {

	private static final long serialVersionUID = 1L;

	private final String _provider;
	private final String _alias;
	private final String _rawReference;

	public KeyReference(String provider, String alias, String rawReference) {
		_provider = Objects.requireNonNull(provider, "Provider must not be null");
		_alias = Objects.requireNonNull(alias, "Alias must not be null");
		_rawReference = Objects.requireNonNull(rawReference, "Raw reference must not be null");
	}

	public String getProvider() {
		return _provider;
	}

	public String getAlias() {
		return _alias;
	}

	public String getRawReference() {
		return _rawReference;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;

		KeyReference that = (KeyReference) o;
		return _provider.equals(that._provider) && _alias.equals(that._alias);
	}

	@Override
	public int hashCode() {
		return Objects.hash(_provider, _alias);
	}

	@Override
	public String toString() {
		return _rawReference;
	}

}