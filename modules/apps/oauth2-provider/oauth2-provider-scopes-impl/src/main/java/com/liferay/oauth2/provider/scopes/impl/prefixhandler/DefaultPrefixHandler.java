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

package com.liferay.oauth2.provider.scopes.impl.prefixhandler;


import com.liferay.oauth2.provider.scopes.spi.PrefixHandler;
import com.liferay.portal.kernel.util.StringPool;

public class DefaultPrefixHandler implements PrefixHandler {

	private String _prefix;

	public DefaultPrefixHandler(String prefix) {
		_prefix = prefix;
	}

	@Override
	public String addPrefix(String input) {
		return _prefix + input;
	}

	@Override
	public PrefixHandler append(PrefixHandler prefixHandler) {
		return new DefaultPrefixHandler(
			_prefix + prefixHandler.addPrefix(StringPool.BLANK));
	}

	@Override
	public PrefixHandler prepend(PrefixHandler prefixHandler) {
		return new DefaultPrefixHandler(prefixHandler.addPrefix(_prefix));
	}

	@Override
	public String removePrefix(String prefixed) {
		if (prefixed.startsWith(_prefix)) {
			return prefixed.substring(_prefix.length());
		}

		return prefixed;
	}
}