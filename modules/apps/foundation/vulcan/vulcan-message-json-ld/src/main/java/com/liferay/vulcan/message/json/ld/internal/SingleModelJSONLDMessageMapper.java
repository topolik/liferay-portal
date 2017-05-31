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

package com.liferay.vulcan.message.json.ld.internal;

import static com.liferay.vulcan.message.json.ld.internal.JsonLDConstants.JSON_LD_CONTEXT;
import static com.liferay.vulcan.message.json.ld.internal.JsonLDConstants.JSON_LD_ID;
import static com.liferay.vulcan.message.json.ld.internal.JsonLDConstants.JSON_LD_MIME_TYPE;
import static com.liferay.vulcan.message.json.ld.internal.JsonLDConstants.JSON_LD_SCHEMA_ORG;
import static com.liferay.vulcan.message.json.ld.internal.JsonLDConstants.JSON_LD_TYPE;
import static com.liferay.vulcan.message.json.ld.internal.JsonLDConstants.JSON_LD_VOCAB;

import com.liferay.vulcan.list.FunctionalList;
import com.liferay.vulcan.message.RequestInfo;
import com.liferay.vulcan.message.SingleModelJSONMessageMapper;
import com.liferay.vulcan.message.json.JSONObjectBuilder;

import java.util.List;

import org.osgi.service.component.annotations.Component;

/**
 * @author Alejandro Hernández
 * @author Carlos Sierra Andrés
 * @author Jorge Ferrer
 */
@Component(
	immediate = true,
	service = {
		SingleModelJSONLDMessageMapper.class, SingleModelJSONMessageMapper.class
	}
)
public class SingleModelJSONLDMessageMapper<T>
	implements SingleModelJSONMessageMapper<T> {

	@Override
	public String getMediaType() {
		return JSON_LD_MIME_TYPE;
	}

	@Override
	public void mapEmbeddedResourceField(
		JSONObjectBuilder jsonObjectBuilder,
		FunctionalList<String> embeddedPathElements, String fieldName,
		Object value) {

		jsonObjectBuilder.nestedField(
			embeddedPathElements.head(),
			embeddedPathElements.tail().toArray(String[]::new)
		).field(
			fieldName
		).value(
			value
		);
	}

	@Override
	public void mapEmbeddedResourceLink(
		JSONObjectBuilder jsonObjectBuilder,
		FunctionalList<String> embeddedPathElements, String fieldName,
		String url) {

		jsonObjectBuilder.nestedField(
			embeddedPathElements.head(),
			embeddedPathElements.tail().toArray(String[]::new)
		).field(
			fieldName
		).value(
			url
		);
	}

	@Override
	public void mapEmbeddedResourceTypes(
		JSONObjectBuilder jsonObjectBuilder,
		FunctionalList<String> embeddedPathElements, List<String> types) {

		jsonObjectBuilder.nestedField(
			embeddedPathElements.head(),
			embeddedPathElements.tail().toArray(String[]::new)
		).field(
			JSON_LD_TYPE
		).arrayValue().addAll(
			types
		);
	}

	@Override
	public void mapEmbeddedResourceURL(
		JSONObjectBuilder jsonObjectBuilder,
		FunctionalList<String> embeddedPathElements, String url) {

		jsonObjectBuilder.nestedField(
			embeddedPathElements.head(),
			embeddedPathElements.tail().toArray(String[]::new)
		).field(
			JSON_LD_ID
		).value(
			url
		);
	}

	@Override
	public void mapField(
		JSONObjectBuilder jsonObjectBuilder, String fieldName, Object value) {

		jsonObjectBuilder.field(fieldName).value(value);
	}

	@Override
	public void mapLink(
		JSONObjectBuilder jsonObjectBuilder, String fieldName, String url) {

		jsonObjectBuilder.field(fieldName).value(url);
	}

	@Override
	public void mapLinkedResourceURL(
		JSONObjectBuilder jsonObjectBuilder,
		FunctionalList<String> embeddedPathElements, String url) {

		String[] tail = embeddedPathElements.tail().toArray(String[]::new);

		jsonObjectBuilder.nestedField(
			embeddedPathElements.head(), tail
		).value(
			url
		);

		if (tail.length == 0) {
			jsonObjectBuilder.nestedField(
				JSON_LD_CONTEXT, embeddedPathElements.last(), JSON_LD_TYPE
			).value(
				JSON_LD_ID
			);
		}
		else {
			jsonObjectBuilder.nestedField(
				embeddedPathElements.head(),
				embeddedPathElements.middle().toArray(String[]::new)
			).nestedField(
				JSON_LD_CONTEXT, embeddedPathElements.last(), JSON_LD_TYPE
			).value(
				JSON_LD_ID
			);
		}
	}

	@Override
	public void mapSelfURL(JSONObjectBuilder jsonObjectBuilder, String url) {
		jsonObjectBuilder.field(JSON_LD_ID).value(url);
	}

	@Override
	public void mapTypes(
		JSONObjectBuilder jsonObjectBuilder, List<String> types) {

		jsonObjectBuilder.field(
			JSON_LD_TYPE
		).arrayValue().addAll(
			types
		);
	}

	@Override
	public void onFinish(
		JSONObjectBuilder jsonObjectBuilder, T model, Class<T> modelClass,
		RequestInfo requestInfo) {

		jsonObjectBuilder.nestedField(
			JSON_LD_CONTEXT, JSON_LD_VOCAB
		).value(
			JSON_LD_SCHEMA_ORG
		);
	}

}