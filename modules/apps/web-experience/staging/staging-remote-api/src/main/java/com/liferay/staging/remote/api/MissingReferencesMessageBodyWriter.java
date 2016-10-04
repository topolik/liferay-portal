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

package com.liferay.staging.remote.api;

import com.liferay.exportimport.kernel.lar.MissingReferences;

import java.io.IOException;
import java.io.OutputStream;

import java.lang.annotation.Annotation;
import java.lang.reflect.Type;

import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.ext.MessageBodyWriter;
import javax.ws.rs.ext.Provider;
import javax.ws.rs.ext.Providers;

/**
 * @author Carlos Sierra Andrés
 */
@Provider
public class MissingReferencesMessageBodyWriter
	implements MessageBodyWriter<MissingReferences> {

	@Override
	public long getSize(
		MissingReferences missingReferences, Class<?> type, Type genericType,
		Annotation[] annotations, MediaType mediaType) {

		return -1;
	}

	@Override
	public boolean isWriteable(
		Class<?> type, Type genericType, Annotation[] annotations,
		MediaType mediaType) {

		if (type.isAssignableFrom(MissingReferences.class) &&
			_providers.getMessageBodyWriter(
				MissingReferencesRepr.class, genericType, annotations,
				mediaType)
					!= null) {

			return true;
		}

		return false;
	}

	@Override
	public void writeTo(
			MissingReferences missingReferences, Class<?> type,
			Type genericType, Annotation[] annotations, MediaType mediaType,
			MultivaluedMap<String, Object> httpHeaders,
			OutputStream entityStream)
		throws IOException, WebApplicationException {

		MessageBodyWriter<MissingReferencesRepr> messageBodyWriter =
			_providers.getMessageBodyWriter(
				MissingReferencesRepr.class, genericType, annotations,
				mediaType);

		MissingReferencesRepr missingReferencesRepr = new MissingReferencesRepr(
			missingReferences);

		messageBodyWriter.writeTo(
			missingReferencesRepr, MissingReferencesRepr.class, genericType,
			annotations, mediaType, httpHeaders, entityStream);
	}

	@Context
	private final Providers _providers;

}