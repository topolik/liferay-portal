/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.keymanager.internal;

import com.liferay.keymanager.KeyReference;
import com.liferay.keymanager.constants.KeyManagerConstants;
import com.liferay.keymanager.exception.KeyResolutionException;
import com.liferay.portal.kernel.util.Validator;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.osgi.service.component.annotations.Component;

/**
 * @author Tomas Polesovsky
 */
@Component(service = KeyReferenceParser.class)
public class KeyReferenceParser {

	public boolean isKeyReference(String value) {
		if (Validator.isNull(value)) {
			return false;
		}

		return value.contains(KeyManagerConstants.KEY_REFERENCE_PREFIX);
	}

	public KeyReference parse(String referenceString)
		throws KeyResolutionException {

		if (Validator.isNull(referenceString)) {
			throw new KeyResolutionException(
				"Reference string must not be null or empty");
		}

		Matcher matcher = _keyRefPattern.matcher(referenceString);

		if (!matcher.find()) {
			throw new KeyResolutionException(
				"Invalid key reference format: " + referenceString +
					". Expected format: ${keyref:provider/alias}");
		}

		return new KeyReference(
			matcher.group(1), matcher.group(2), matcher.group(0));
	}

	public List<KeyReference> parseAll(String value)
		throws KeyResolutionException {

		List<KeyReference> references = new ArrayList<>();

		if (Validator.isNull(value)) {
			return references;
		}

		Matcher matcher = _keyRefPattern.matcher(value);

		while (matcher.find()) {
			references.add(
				new KeyReference(
					matcher.group(1), matcher.group(2), matcher.group(0)));
		}

		return references;
	}

	public String replaceAll(
			String value, ThrowingFunction<KeyReference, String> resolver)
		throws KeyResolutionException {

		if (Validator.isNull(value) || !isKeyReference(value)) {
			return value;
		}

		Matcher matcher = _keyRefPattern.matcher(value);

		StringBuilder sb = new StringBuilder();

		while (matcher.find()) {
			KeyReference keyReference = new KeyReference(
				matcher.group(1), matcher.group(2), matcher.group(0));

			String resolved = resolver.apply(keyReference);

			matcher.appendReplacement(sb, Matcher.quoteReplacement(resolved));
		}

		matcher.appendTail(sb);

		return sb.toString();
	}

	@FunctionalInterface
	public interface ThrowingFunction<T, R> {

		public R apply(T t) throws KeyResolutionException;

	}

	private static final Pattern _keyRefPattern = Pattern.compile(
		"\\$\\{keyref:([a-zA-Z0-9\\-_]+)/(.+?)\\}");

}