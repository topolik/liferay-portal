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

package com.liferay.vulcan.message.json.plain.internal;

import static com.liferay.vulcan.message.json.plain.internal.JSONPlainConstants.JSON_PLAIN_MEDIA_TYPE;
import static com.liferay.vulcan.message.json.plain.internal.JSONPlainConstants.JSON_PLAIN_SELF;

import com.liferay.vulcan.list.FunctionalList;
import com.liferay.vulcan.message.SingleModelJSONMessageMapper;
import com.liferay.vulcan.message.json.JSONObjectBuilder;

import org.osgi.service.component.annotations.Component;

/**
 * @author Alejandro Hernández
 * @author Carlos Sierra Andrés
 * @author Jorge Ferrer
 */
@Component(
	immediate = true,
	service = {
		SingleModelPlainJSONMessageMapper.class,
		SingleModelJSONMessageMapper.class
	}
)
public class SingleModelPlainJSONMessageMapper<T>
	implements SingleModelJSONMessageMapper<T> {

	@Override
	public String getMediaType() {
		return JSON_PLAIN_MEDIA_TYPE;
	}

	@Override
	public void mapEmbeddedResourceField(
		JSONObjectBuilder jsonObjectBuilder,
		FunctionalList<String> embeddedPathElements, String fieldName,
		Object value) {

		jsonObjectBuilder.nestedField(
			embeddedPathElements.head(),
			embeddedPathElements.tail().toArray(String[]::new)
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
	public void mapEmbeddedResourceURL(
		JSONObjectBuilder jsonObjectBuilder,
		FunctionalList<String> embeddedPathElements, String url) {

		jsonObjectBuilder.nestedField(
			embeddedPathElements.head(),
			embeddedPathElements.tail().toArray(String[]::new)
		).field(
			JSON_PLAIN_SELF
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

		jsonObjectBuilder.nestedField(
			embeddedPathElements.head(),
			embeddedPathElements.tail().toArray(String[]::new)
		).value(
			url
		);
	}

	@Override
	public void mapSelfURL(JSONObjectBuilder jsonObjectBuilder, String url) {
		jsonObjectBuilder.field(JSON_PLAIN_SELF).value(url);
	}

}