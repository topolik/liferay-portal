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

package com.liferay.portal.security.xss;

import com.liferay.portal.kernel.security.xss.EscapeOperation;
import com.liferay.portal.kernel.security.xss.XSS;

import java.util.Arrays;
import java.util.LinkedHashSet;
import java.util.Set;
import java.util.function.Function;

/**
 * @author Carlos Sierra Andrés
 * @author Tomas Polesovsky
 */
public class EscapedStringImpl implements XSS.EscapedString {

	public EscapedStringImpl(String original) {
		_original = original;
	}

	@Override
	public EscapedStringImpl chain(EscapeOperation... escapeOperation) {
		_escapeOperations.addAll(Arrays.asList(escapeOperation));

		recalculate();

		return this;
	}

	@Override
	public char charAt(int index) {
		return _escaped.charAt(index);
	}

	@Override
	public XSS.EscapedString concat(String input) {
		map(
			a -> {
				XSS.EscapedString newEscapedString = new EscapedStringImpl(
					input);

				newEscapedString.chain(getOperationsArray());

			return a + newEscapedString.toString();
		});

		return this;
	}

	@Override
	public XSS.EscapedString concat(XSS.EscapedString escapedString) {
		map(
			a -> {
				escapedString.reset();

				escapedString.chain(getOperationsArray());

				return a + escapedString.toString();
			});

		return this;
	}

	@Override
	public int length() {
		return _escaped.length();
	}

	@Override
	public String original() {
		return _original;
	}

	@Override
	public EscapedStringImpl reset() {
		_escapeOperations.clear();

		recalculate();

		return this;
	}

	@Override
	public CharSequence subSequence(int start, int end) {
		return new EscapedStringImpl(_original.substring(start, end));
	}

	@Override
	public String toString() {
		return _escaped.toString();
	}

	protected EscapeOperation[] getOperationsArray() {
		return _escapeOperations.toArray(
			new EscapeOperation[_escapeOperations.size()]);
	}

	protected void map(Function<String, String> function) {
		_function = _function.andThen(function);
	}

	protected void recalculate() {
		_escaped = _original;

		for (EscapeOperation escapeOperation : _escapeOperations) {
			_escaped = escapeOperation.escape(_escaped);
		}

		_escaped = _function.apply(_escaped);
	}

	private String _escaped;
	private final Set<EscapeOperation> _escapeOperations =
		new LinkedHashSet<>();
	private Function<String, String> _function;
	private final String _original;

}