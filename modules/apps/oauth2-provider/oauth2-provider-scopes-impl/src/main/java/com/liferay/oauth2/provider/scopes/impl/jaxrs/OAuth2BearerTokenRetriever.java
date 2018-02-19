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

package com.liferay.oauth2.provider.scopes.impl.jaxrs;

import com.liferay.portal.kernel.util.StringPool;
import com.liferay.portal.kernel.util.Validator;

import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerRequestFilter;
import javax.ws.rs.core.HttpHeaders;
import java.io.IOException;
import java.util.function.Consumer;

public class OAuth2BearerTokenRetriever implements ContainerRequestFilter {

	private Consumer<String> _consumer;

	public OAuth2BearerTokenRetriever(Consumer<String> consumer) {
		_consumer = consumer;
	}

	@Override
	public void filter(ContainerRequestContext requestContext)
		throws IOException {

		String authorizationHeader =
			requestContext.getHeaderString(HttpHeaders.AUTHORIZATION);

		if (Validator.isNull(authorizationHeader)) {
			return;
		}

		String[] authorizationHeaderParts = authorizationHeader.split(
			StringPool.SPACE);

		if (authorizationHeaderParts.length != 2) {
			return;
		}

		String authorizationType = authorizationHeaderParts[0];
		if (Validator.isNull(authorizationType) ||
			!authorizationType.equals("Bearer")) {

			return;
		}

		_consumer.accept(authorizationHeaderParts[1]);
	}

}
