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
import com.liferay.oauth2.provider.scopes.spi.PrefixHandlerFactory;
import com.liferay.portal.kernel.util.StringBundler;
import com.liferay.portal.kernel.util.StringPool;
import com.liferay.portal.kernel.util.Validator;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;

import java.util.Map;

@Component(
	configurationPid = "com.liferay.oauth2.provider.impl.scopes.DefaultNamespaceAdderFactory",
	property = {
		"separator=" + StringPool.UNDERLINE
	}
)
public class DefaultPrefixHandlerFactory implements PrefixHandlerFactory {

	private String _separator = StringPool.UNDERLINE;

	@Activate
	protected void activate(Map<String, Object> properties) {
		Object separatorObject = properties.get("separator");

		if (Validator.isNotNull(separatorObject)) {
			_separator = separatorObject.toString();
		}
	}

	@Override
	public PrefixHandler create(String prefix) {
		return new DefaultPrefixHandler(prefix + _separator);
	}

	@Override
	public PrefixHandler create(String ... prefixes) {
		StringBundler sb = new StringBundler(prefixes.length * 2);

		for (String namespace : prefixes) {
			sb.append(namespace);
			sb.append(_separator);
		}

		return new DefaultPrefixHandler(sb.toString());
	}
}