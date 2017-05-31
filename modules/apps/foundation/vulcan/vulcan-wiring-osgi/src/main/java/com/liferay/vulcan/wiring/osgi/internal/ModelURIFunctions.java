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

package com.liferay.vulcan.wiring.osgi.internal;

import java.util.Optional;
import java.util.function.Function;
import java.util.function.Supplier;

/**
 * @author Alejandro Hernández
 * @author Carlos Sierra Andrés
 * @author Jorge Ferrer
 */
public class ModelURIFunctions<T> {

	public ModelURIFunctions(
		Supplier<Optional<String>> collectionResourceURISupplier,
		Function<T, Optional<String>> singleResourceURIFunction) {

		_collectionResourceURISupplier = collectionResourceURISupplier;
		_singleResourceURIFunction = singleResourceURIFunction;
	}

	public Supplier<Optional<String>> getCollectionResourceURISupplier() {
		return _collectionResourceURISupplier;
	}

	public Function<T, Optional<String>> getSingleResourceURIFunction() {
		return _singleResourceURIFunction;
	}

	private final Supplier<Optional<String>> _collectionResourceURISupplier;
	private final Function<T, Optional<String>> _singleResourceURIFunction;

}