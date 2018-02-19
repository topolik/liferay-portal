/**
 * Copyright (c) 2000-present Liferay, Inc. All rights reserved.
 * <p>
 * This library is free software; you can redistribute it and/or modify it under
 * the terms of the GNU Lesser General Public License as published by the Free
 * Software Foundation; either version 2.1 of the License, or (at your option)
 * any later version.
 * <p>
 * This library is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more
 * details.
 */

package com.liferay.oauth2.provider.scopes.spi;

import java.util.Arrays;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Interface to create {@link PrefixHandler} using a given prefix.
 * This allows components to switch prefixing strategies using configuration,
 * such as using different characters <i>'_'</i> or <i>'.'</i>, thus keeping
 * the prefixing strategy consistent across components.
 */
public interface PrefixHandlerFactory {

	/**
	 * Creates a {@link PrefixHandler} using the given prefix.
	 * @param prefix the prefix for the {@link PrefixHandler}
	 * @return the {@link PrefixHandler} for the given prefix.
	 */
	public PrefixHandler create(String prefix);

	/**
	 * Creates a {@link PrefixHandler} given several prefixes. Some
	 * implementations may provide an optimized version.
	 * @param prefixes the prefixes for the {@link PrefixHandler}
	 * @return a {@link PrefixHandler} for all the given prefixes.
	 */
	public default PrefixHandler create(String ... prefixes) {
		Stream<String> stream = Arrays.stream(prefixes);

		return PrefixHandler.merge(
			stream.map(
				this::create
			).collect(
				Collectors.toList()
			)
		);
	}

}