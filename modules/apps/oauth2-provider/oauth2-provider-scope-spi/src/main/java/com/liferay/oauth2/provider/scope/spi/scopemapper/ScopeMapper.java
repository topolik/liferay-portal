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

package com.liferay.oauth2.provider.scope.spi.scopemapper;

import aQute.bnd.annotation.ProviderType;

import com.liferay.oauth2.provider.scope.spi.scopematcher.ScopeMatcher;

import java.util.Collections;
import java.util.Set;
import java.util.stream.Stream;

/**
 * Represents a transformation between internal scope names to external aliases.
 *
 * @author Carlos Sierra Andrés
 * @review
 */
@ProviderType
public interface ScopeMapper {

	public static final ScopeMapper PASSTHROUGH_SCOPEMAPPER =
		Collections::singleton;

	/**
	 * Returns a new {@link ScopeMatcher} that takes into account the effect
	 * of the given {@link ScopeMapper}. Some implementations might have
	 * optimization opportunities.
	 * @param scopeMatcher the scope matcher that should take into account this
	 * scope mapper.
	 * @return the new {@link ScopeMatcher} that takes into account the given
	 * {@link ScopeMapper}.
	 * @review
	 */
	public default ScopeMatcher applyTo(ScopeMatcher scopeMatcher) {
		return localName -> {
			Set<String> map = map(localName);

			Stream<String> stream = map.stream();

			return stream.anyMatch(scopeMatcher::match);
		};
	}

	/**
	 * Renames an application provided scope to new scope names
	 *
	 * @param scope application provided scope
	 * @return set of new names for the scope
	 * @review
	 */
	public Set<String> map(String scope);

}