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

import com.liferay.vulcan.message.RequestInfo;

import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;

/**
 * @author Alejandro Hernández
 * @author Carlos Sierra Andrés
 * @author Jorge Ferrer
 */
public class RequestInfoImpl implements RequestInfo {

	public RequestInfoImpl(
		MediaType mediaType, MultivaluedMap<String, Object> httpHeaders) {

		_mediaType = mediaType;
		_httpHeaders = httpHeaders;
	}

	@Override
	public MultivaluedMap<String, Object> getHttpHeaders() {
		return _httpHeaders;
	}

	@Override
	public MediaType getMediaType() {
		return _mediaType;
	}

	private final MultivaluedMap<String, Object> _httpHeaders;
	private final MediaType _mediaType;

}