/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.keymanager;

import com.liferay.petra.string.StringBundler;

import java.io.Serializable;

import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author Tomas Polesovsky
 */
public class KeyReference implements Serializable {

	public static KeyReference fromString(String value) {
		if (value == null) {
			return null;
		}

		Matcher matcher = _pattern.matcher(value);

		if (!matcher.matches()) {
			return null;
		}

		String typeStr = matcher.group(1);

		Type type = null;

		if (Objects.equals(typeStr, "keyRef")) {
			type = Type.CRYPTO;
		}
		else if (Objects.equals(typeStr, "secretRef")) {
			type = Type.SECRET;
		}

		if (type == null) {
			return null;
		}

		String providerId = matcher.group(2);
		String identifier = matcher.group(3);

		return new KeyReference(type, providerId, identifier);
	}

	public KeyReference(Type type, String providerId, String identifier) {
		_type = Objects.requireNonNull(type, "Type must not be null");
		_providerId = Objects.requireNonNull(
			providerId, "Provider ID must not be null");
		_identifier = Objects.requireNonNull(
			identifier, "Identifier must not be null");
	}

	@Override
	public boolean equals(Object object) {
		if (this == object) {
			return true;
		}

		if (!(object instanceof KeyReference)) {
			return false;
		}

		KeyReference keyReference = (KeyReference)object;

		if ((_type == keyReference._type) &&
			Objects.equals(_providerId, keyReference._providerId) &&
			Objects.equals(_identifier, keyReference._identifier)) {

			return true;
		}

		return false;
	}

	public String getIdentifier() {
		return _identifier;
	}

	public String getProviderId() {
		return _providerId;
	}

	public Type getType() {
		return _type;
	}

	@Override
	public int hashCode() {
		return Objects.hash(_identifier, _providerId, _type);
	}

	@Override
	public String toString() {
		String typeStr = "keyRef";

		if (_type == Type.SECRET) {
			typeStr = "secretRef";
		}

		return StringBundler.concat(
			"${", typeStr, ":", _providerId, ":", _identifier, "}");
	}

	public enum Type {

		CRYPTO, SECRET

	}

	private static final Pattern _pattern = Pattern.compile(
		"\\$\\{(keyRef|secretRef):([^:]+):(.+)\\}");
	private static final long serialVersionUID = 1L;

	private final String _identifier;
	private final String _providerId;
	private final Type _type;

}
