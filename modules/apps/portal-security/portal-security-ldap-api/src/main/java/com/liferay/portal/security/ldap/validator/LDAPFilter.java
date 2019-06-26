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

package com.liferay.portal.security.ldap.validator;

import com.liferay.petra.string.StringBundler;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.util.ArrayUtil;
import com.liferay.portal.kernel.util.StringUtil;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.TreeMap;

/**
 * @author Tomas Polesovsky
 */
public class LDAPFilter {

	public static LDAPFilter approx(String key, Object value) {
		return new LDAPFilter(
			_concat(rfc2254Escape(key), "~=", _PLACEHOLDER),
			Collections.singletonList(value));
	}

	public static LDAPFilter eq(String key, Object value) {
		return new LDAPFilter(
			_concat(rfc2254Escape(key), StringPool.EQUAL, _PLACEHOLDER),
			Collections.singletonList(value));
	}

	public static LDAPFilter ex(String key) {
		return new LDAPFilter(
			_concat(rfc2254Escape(key), StringPool.EQUAL, StringPool.STAR),
			Collections.emptyList());
	}

	public static LDAPFilter ge(String key, Object value) {
		return new LDAPFilter(
			_concat(
				rfc2254Escape(key), StringPool.GREATER_THAN_OR_EQUAL,
				_PLACEHOLDER),
			Collections.singletonList(value));
	}

	public static LDAPFilter le(String key, Object value) {
		return new LDAPFilter(
			_concat(
				rfc2254Escape(key), StringPool.LESS_THAN_OR_EQUAL,
				_PLACEHOLDER),
			Collections.singletonList(value));
	}

	public static String rfc2254Escape(String value) {
		return rfc2254Escape(value, false);
	}

	public static String rfc2254Escape(String value, boolean preserveStar) {
		if (!preserveStar) {
			return StringUtil.replace(
				value, _RFC2254_ESCAPE_KEYS, _RFC2254_ESCAPE_VALUES);
		}

		return StringUtil.replace(
			value, ArrayUtil.remove(_RFC2254_ESCAPE_KEYS, StringPool.STAR),
			ArrayUtil.remove(_RFC2254_ESCAPE_VALUES, "\\2a"));
	}

	public static LDAPFilter substring(String key, String value) {
		if (value.indexOf(StringPool.STAR) == -1) {
			throw new IllegalArgumentException(
				"Value must contain * character: " + value);
		}

		return new LDAPFilter(
			_concat(
				rfc2254Escape(key), StringPool.EQUAL,
				rfc2254Escape(value, true)),
			Collections.emptyList());
	}

	public LDAPFilter and(LDAPFilter... ldapFilters) {
		if ((ldapFilters == null) || (ldapFilters.length == 0)) {
			return this;
		}

		StringBundler filterSB = new StringBundler();

		List<Object> arguments = new ArrayList<>();

		filterSB.append(StringPool.OPEN_PARENTHESIS);
		filterSB.append(StringPool.AMPERSAND);

		filterSB.append(_filterSB);
		arguments.addAll(_arguments);

		for (LDAPFilter ldapFilter : ldapFilters) {
			filterSB.append(ldapFilter._filterSB);

			arguments.addAll(ldapFilter._arguments);
		}

		filterSB.append(StringPool.CLOSE_PARENTHESIS);

		return new LDAPFilter(filterSB, arguments);
	}

	public String generateFilter() {
		if (_generatedFilter != null) {
			return _generatedFilter;
		}

		StringBundler sb = new StringBundler(
			_filterSB.length() + _arguments.size() * 2);

		int placeholderPos = 0;

		for (int i = 0; i < _filterSB.index(); i++) {
			String string = _filterSB.stringAt(i);

			if (Objects.equals(string, _PLACEHOLDER)) {
				sb.append(StringPool.OPEN_CURLY_BRACE);
				sb.append(placeholderPos);
				sb.append(StringPool.CLOSE_CURLY_BRACE);
				placeholderPos++;
			}
			else {
				sb.append(string);
			}
		}

		_generatedFilter = sb.toString();

		return _generatedFilter;
	}

	public Object[] getArguments() {
		return _arguments.toArray();
	}

	public LDAPFilter not() {
		StringBundler filterSB = new StringBundler(_filterSB.index() + 3);

		filterSB.append(StringPool.OPEN_PARENTHESIS);
		filterSB.append(StringPool.EXCLAMATION);
		filterSB.append(_filterSB);
		filterSB.append(StringPool.CLOSE_PARENTHESIS);

		return new LDAPFilter(filterSB, new ArrayList(_arguments));
	}

	public LDAPFilter or(LDAPFilter... ldapFilters) {
		if ((ldapFilters == null) || (ldapFilters.length == 0)) {
			return this;
		}

		StringBundler filterSB = new StringBundler();

		List<Object> arguments = new ArrayList<>();

		filterSB.append(StringPool.OPEN_PARENTHESIS);
		filterSB.append(StringPool.PIPE);

		filterSB.append(_filterSB);
		arguments.addAll(_arguments);

		for (LDAPFilter ldapFilter : ldapFilters) {
			filterSB.append(ldapFilter._filterSB);

			arguments.addAll(ldapFilter._arguments);
		}

		filterSB.append(StringPool.CLOSE_PARENTHESIS);

		return new LDAPFilter(filterSB, arguments);
	}

	public LDAPFilter replace(String[] keys, String[] values) {
		if (keys == null) {
			throw new IllegalArgumentException("Parameter keys cannot be null");
		}

		if (values == null) {
			throw new IllegalArgumentException(
				"Parameter values cannot be null");
		}

		if (keys.length > values.length) {
			throw new IllegalArgumentException(
				"Parameters keys and values must have same length");
		}

		String[] placeholderValues = new String[values.length];

		Arrays.fill(placeholderValues, _PLACEHOLDER);

		List<Object> arguments = new ArrayList<>();
		StringBundler sb = new StringBundler();

		int argumentsPos = 0;

		for (int i = 0; i < _filterSB.index(); i++) {
			String string = _filterSB.stringAt(i);

			if (Objects.equals(string, _PLACEHOLDER)) {
				sb.append(_PLACEHOLDER);
				arguments.add(_arguments.get(argumentsPos));
				argumentsPos++;

				continue;
			}

			TreeMap<Integer, String> valuesSortedByKeyPos = new TreeMap<>();

			for (int j = 0; j < keys.length; j++) {
				String key = keys[j];

				int pos = string.indexOf(key);

				while (pos > -1) {
					valuesSortedByKeyPos.put(pos, values[j]);
					pos = string.indexOf(key, pos + key.length());
				}
			}

			if (valuesSortedByKeyPos.isEmpty()) {
				sb.append(string);
			}
			else {
				arguments.addAll(valuesSortedByKeyPos.values());

				String replacedKeys = StringUtil.replace(
					string, keys, placeholderValues);

				int lastPos = 0;
				int pos = replacedKeys.indexOf(_PLACEHOLDER);

				while (pos > -1) {
					sb.append(replacedKeys.substring(lastPos, pos));
					sb.append(_PLACEHOLDER);

					lastPos = pos + _PLACEHOLDER.length();
					pos = replacedKeys.indexOf(_PLACEHOLDER, lastPos);
				}

				if (lastPos < replacedKeys.length()) {
					sb.append(replacedKeys.substring(lastPos));
				}
			}
		}

		return new LDAPFilter(sb, arguments);
	}

	LDAPFilter(StringBundler filterSB, List<Object> arguments) {
		_arguments = arguments;
		_filterSB = filterSB;
	}

	@Override
	public String toString() {
		return StringBundler.concat(generateFilter(), " ", _arguments);
	}

	private static StringBundler _concat(String key, String op, String value) {
		StringBundler sb = new StringBundler(5);

		sb.append(StringPool.OPEN_PARENTHESIS);
		sb.append(key);
		sb.append(op);
		sb.append(value);
		sb.append(StringPool.CLOSE_PARENTHESIS);

		return sb;
	}

	private static final String _PLACEHOLDER = "(PLACEHOLDER)";

	private static final String[] _RFC2254_ESCAPE_KEYS = {
		StringPool.BACK_SLASH, StringPool.CLOSE_PARENTHESIS,
		StringPool.NULL_CHAR, StringPool.OPEN_PARENTHESIS, StringPool.STAR
	};

	private static final String[] _RFC2254_ESCAPE_VALUES = {
		"\\5c", "\\29", "\\00", "\\28", "\\2a"
	};

	private final List<Object> _arguments;
	private final StringBundler _filterSB;
	private String _generatedFilter;

}