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

import com.liferay.exportimport.kernel.model.ExportImportConfiguration;
import com.liferay.portlet.exportimport.model.impl.ExportImportConfigurationImpl;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import java.lang.annotation.Annotation;
import java.lang.reflect.Type;

import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.ext.MessageBodyReader;
import javax.ws.rs.ext.MessageBodyWriter;
import javax.ws.rs.ext.Provider;
import javax.ws.rs.ext.Providers;

/**
 * @author Carlos Sierra Andrés
 */
@Provider
public class ExportImporConfigurationMessageBodyWorker
	implements MessageBodyWriter<ExportImportConfiguration>,
			   MessageBodyReader<ExportImportConfiguration> {

	@Override
	public long getSize(
		ExportImportConfiguration exportImportConfiguration, Class<?> type,
		Type genericType, Annotation[] annotations, MediaType mediaType) {

		return -1;
	}

	@Override
	public boolean isReadable(
		Class<?> type, Type genericType, Annotation[] annotations,
		MediaType mediaType) {

		if (type.isAssignableFrom(ExportImportConfiguration.class) &&
			_providers.getMessageBodyReader(
				ExportImportConfigurationRepr.class, genericType, annotations,
				mediaType) != null) {

			return true;
		}

		return false;
	}

	@Override
	public boolean isWriteable(
		Class<?> type, Type genericType, Annotation[] annotations,
		MediaType mediaType) {

		if (type.isAssignableFrom(ExportImportConfiguration.class) &&
			_providers.getMessageBodyWriter(
				ExportImportConfigurationRepr.class, genericType, annotations,
				mediaType) != null) {

			return true;
		}

		return false;
	}

	@Override
	public ExportImportConfiguration readFrom(
			Class<ExportImportConfiguration> type, Type genericType,
			Annotation[] annotations, MediaType mediaType,
			MultivaluedMap<String, String> httpHeaders,
			InputStream entityStream)
		throws IOException, WebApplicationException {

		MessageBodyReader<ExportImportConfigurationRepr> messageBodyReader =
			_providers.getMessageBodyReader(
				ExportImportConfigurationRepr.class, genericType, annotations,
				mediaType);

		ExportImportConfigurationRepr exportImportConfigurationRepr =
			messageBodyReader.readFrom(
				ExportImportConfigurationRepr.class, genericType, annotations,
				mediaType, httpHeaders, entityStream);

		ExportImportConfigurationImpl exportImportConfigurationImpl =
			new ExportImportConfigurationImpl();

		exportImportConfigurationImpl.setMvccVersion(
			exportImportConfigurationRepr.getMvccVersion());
		exportImportConfigurationImpl.setExportImportConfigurationId(
			exportImportConfigurationRepr.getExportImportConfigurationId());
		exportImportConfigurationImpl.setGroupId(
			exportImportConfigurationRepr.getGroupId());
		exportImportConfigurationImpl.setCompanyId(
			exportImportConfigurationRepr.getCompanyId());
		exportImportConfigurationImpl.setUserId(
			exportImportConfigurationRepr.getUserId());
		exportImportConfigurationImpl.setUserName(
			exportImportConfigurationRepr.getUserName());
		exportImportConfigurationImpl.setCreateDate(
			exportImportConfigurationRepr.getCreateDate());
		exportImportConfigurationImpl.setModifiedDate(
			exportImportConfigurationRepr.getModifiedDate());
		exportImportConfigurationImpl.setName(
			exportImportConfigurationRepr.getName());
		exportImportConfigurationImpl.setDescription(
			exportImportConfigurationRepr.getDescription());
		exportImportConfigurationImpl.setType(
			exportImportConfigurationRepr.getType());
		exportImportConfigurationImpl.setSettings(
			exportImportConfigurationRepr.getSettings());
		exportImportConfigurationImpl.setStatus(
			exportImportConfigurationRepr.getStatus());
		exportImportConfigurationImpl.setStatusByUserId(
			exportImportConfigurationRepr.getStatusByUserId());
		exportImportConfigurationImpl.setStatusByUserName(
			exportImportConfigurationRepr.getStatusByUserName());
		exportImportConfigurationImpl.setStatusDate(
			exportImportConfigurationRepr.getStatusDate());

		return exportImportConfigurationImpl;
	}

	@Override
	public void writeTo(
			ExportImportConfiguration exportImportConfiguration, Class<?> type,
			Type genericType, Annotation[] annotations, MediaType mediaType,
			MultivaluedMap<String, Object> httpHeaders,
			OutputStream entityStream)
		throws IOException, WebApplicationException {

		ExportImportConfigurationRepr exportImportConfigurationRepr =
			ExportImportConfigurationRepr.toSoapModel(
				exportImportConfiguration);

		MessageBodyWriter<ExportImportConfigurationRepr> messageBodyWriter =
			_providers.getMessageBodyWriter(
				ExportImportConfigurationRepr.class, genericType, annotations,
				mediaType);

		messageBodyWriter.writeTo(
			exportImportConfigurationRepr, ExportImportConfigurationRepr.class,
			genericType, annotations, mediaType, httpHeaders, entityStream);
	}

	@Context
	private Providers _providers;

}