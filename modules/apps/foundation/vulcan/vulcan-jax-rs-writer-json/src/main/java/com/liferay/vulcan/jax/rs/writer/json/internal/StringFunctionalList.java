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

package com.liferay.vulcan.jax.rs.writer.json.internal;

import com.liferay.vulcan.list.FunctionalList;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * @author Alejandro Hernández
 * @author Carlos Sierra Andrés
 * @author Jorge Ferrer
 */
public class StringFunctionalList implements FunctionalList<String> {

	public StringFunctionalList(
		FunctionalList<String> functionalList, String last) {

		if (functionalList == null) {
			_first = last;
			_tail = Collections.emptyList();
		}
		else {
			_first = functionalList.head();

			List<String> tail =
				functionalList.tail().collect(Collectors.toList());

			tail.add(last);

			_tail = tail;
		}
	}

	@Override
	public String head() {
		return _first;
	}

	@Override
	public Stream<String> init() {
		if (_init == null) {
			List<String> init = new ArrayList<>();

			init.add(_first);
			init.addAll(middle().collect(Collectors.toList()));

			_init = init;
		}

		return _init.stream();
	}

	@Override
	public String last() {
		if (_last == null) {
			if (_tail.size() == 0) {
				_last = _first;
			}
			else {
				_last = _tail.get(_tail.size() - 1);
			}
		}

		return _last;
	}

	@Override
	public Stream<String> middle() {
		if (_middle == null) {
			if (_tail.size() == 0) {
				_middle = Collections.emptyList();
			}
			else {
				_middle = _tail.subList(0, _tail.size() - 1);
			}
		}

		return _middle.stream();
	}

	@Override
	public Stream<String> tail() {
		return _tail.stream();
	}

	private final String _first;
	private List<String> _init;
	private String _last;
	private List<String> _middle;
	private final List<String> _tail;

}