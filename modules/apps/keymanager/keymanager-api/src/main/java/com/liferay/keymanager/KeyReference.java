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

		return new KeyReference(type, providerId, identifier, value);
	}

	public KeyReference(
		Type type, String providerId, String identifier, String rawReference) {

		_type = Objects.requireNonNull(type, "Type must not be null");
		_providerId = Objects.requireNonNull(
			providerId, "Provider ID must not be null");
		_identifier = Objects.requireNonNull(
			identifier, "Identifier must not be null");
		_rawReference = Objects.requireNonNull(
			rawReference, "Raw reference must not be null");
	}

	public String getIdentifier() {
		return _identifier;
	}

	public String getProviderId() {
		return _providerId;
	}

	public String getRawReference() {
		return _rawReference;
	}

	public Type getType() {
		return _type;
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
		"\\$\\{(keyRef|secretRef):([a-zA-Z0-9\\-_]+):([a-zA-Z0-9\\-_]+)\\}");
	private static final long serialVersionUID = 1L;

	private final String _identifier;
	private final String _providerId;
	private final String _rawReference;
	private final Type _type;

}