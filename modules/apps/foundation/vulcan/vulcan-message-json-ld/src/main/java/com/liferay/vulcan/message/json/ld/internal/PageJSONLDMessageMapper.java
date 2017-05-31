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

import static com.liferay.vulcan.message.json.ld.internal.HydraConstants.HYDRA_COLLECTION;
import static com.liferay.vulcan.message.json.ld.internal.HydraConstants.HYDRA_CONTEXT;
import static com.liferay.vulcan.message.json.ld.internal.HydraConstants.HYDRA_FIRST;
import static com.liferay.vulcan.message.json.ld.internal.HydraConstants.HYDRA_LAST;
import static com.liferay.vulcan.message.json.ld.internal.HydraConstants.HYDRA_MEMBERS;
import static com.liferay.vulcan.message.json.ld.internal.HydraConstants.HYDRA_NEXT;
import static com.liferay.vulcan.message.json.ld.internal.HydraConstants.HYDRA_PAGE_COUNT;
import static com.liferay.vulcan.message.json.ld.internal.HydraConstants.HYDRA_PREVIOUS;
import static com.liferay.vulcan.message.json.ld.internal.HydraConstants.HYDRA_TOTAL_ITEMS;
import static com.liferay.vulcan.message.json.ld.internal.HydraConstants.HYDRA_VIEW;
import static com.liferay.vulcan.message.json.ld.internal.JsonLDConstants.JSON_LD_CONTEXT;
import static com.liferay.vulcan.message.json.ld.internal.JsonLDConstants.JSON_LD_ID;
import static com.liferay.vulcan.message.json.ld.internal.JsonLDConstants.JSON_LD_MIME_TYPE;
import static com.liferay.vulcan.message.json.ld.internal.JsonLDConstants.JSON_LD_SCHEMA_ORG;
import static com.liferay.vulcan.message.json.ld.internal.JsonLDConstants.JSON_LD_TYPE;
import static com.liferay.vulcan.message.json.ld.internal.JsonLDConstants.JSON_LD_VOCAB;

import com.liferay.vulcan.list.FunctionalList;
import com.liferay.vulcan.message.PageJSONMessageMapper;
import com.liferay.vulcan.message.RequestInfo;
import com.liferay.vulcan.message.json.JSONObjectBuilder;
import com.liferay.vulcan.pagination.Page;

import java.util.List;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Alejandro Hernández
 * @author Carlos Sierra Andrés
 * @author Jorge Ferrer
 */
@Component(immediate = true)
public class PageJSONLDMessageMapper<T> implements PageJSONMessageMapper<T> {

	@Override
	public String getMediaType() {
		return JSON_LD_MIME_TYPE;
	}

	@Override
	public void mapCollectionURL(
		JSONObjectBuilder jsonObjectBuilder, String url) {

		_singleModelJSONLDMessageMapper.mapSelfURL(jsonObjectBuilder, url);
	}

	@Override
	public void mapCurrentPageURL(
		JSONObjectBuilder jsonObjectBuilder, String url) {

		jsonObjectBuilder.nestedField(
			HYDRA_VIEW, JSON_LD_ID
		).value(
			url
		);
	}

	@Override
	public void mapFirstPageURL(
		JSONObjectBuilder jsonObjectBuilder, String url) {

		jsonObjectBuilder.nestedField(
			HYDRA_VIEW, HYDRA_FIRST
		).value(
			url
		);
	}

	@Override
	public void mapItemEmbeddedResourceField(
		JSONObjectBuilder pageJSONObjectBuilder,
		JSONObjectBuilder itemJSONObjectBuilder,
		FunctionalList<String> embeddedPathElements, String field,
		Object fieldData) {

		itemJSONObjectBuilder.nestedField(
			embeddedPathElements.head(),
			embeddedPathElements.tail().toArray(String[]::new)
		).field(
			field
		).value(
			fieldData
		);
	}

	@Override
	public void mapItemEmbeddedResourceLink(
		JSONObjectBuilder pageJSONObjectBuilder,
		JSONObjectBuilder itemJSONObjectBuilder,
		FunctionalList<String> embeddedPathElements, String fieldName,
		String url) {

		_singleModelJSONLDMessageMapper.mapEmbeddedResourceLink(
			itemJSONObjectBuilder, embeddedPathElements, fieldName, url);
	}

	@Override
	public void mapItemEmbeddedResourceTypes(
		JSONObjectBuilder pageJSONObjectBuilder,
		JSONObjectBuilder itemJSONObjectBuilder,
		FunctionalList<String> embeddedPathElements, List<String> types) {

		_singleModelJSONLDMessageMapper.mapEmbeddedResourceTypes(
			itemJSONObjectBuilder, embeddedPathElements, types);
	}

	@Override
	public void mapItemEmbeddedResourceURL(
		JSONObjectBuilder pageJSONObjectBuilder,
		JSONObjectBuilder itemJSONObjectBuilder,
		FunctionalList<String> embeddedPathElements, String url) {

		_singleModelJSONLDMessageMapper.mapEmbeddedResourceURL(
			itemJSONObjectBuilder, embeddedPathElements, url);
	}

	@Override
	public void mapItemField(
		JSONObjectBuilder pageJSONObjectBuilder,
		JSONObjectBuilder itemJSONObjectBuilder, String fieldName,
		Object value) {

		itemJSONObjectBuilder.field(fieldName).value(value);
	}

	@Override
	public void mapItemLink(
		JSONObjectBuilder pageJSONObjectBuilder,
		JSONObjectBuilder itemJSONObjectBuilder, String fieldName, String url) {

		_singleModelJSONLDMessageMapper.mapLink(
			itemJSONObjectBuilder, fieldName, url);
	}

	@Override
	public void mapItemLinkedResourceURL(
		JSONObjectBuilder pageJSONObjectBuilder,
		JSONObjectBuilder itemJSONObjectBuilder,
		FunctionalList<String> embeddedPathElements, String url) {

		_singleModelJSONLDMessageMapper.mapLinkedResourceURL(
			itemJSONObjectBuilder, embeddedPathElements, url);
	}

	@Override
	public void mapItemSelfURL(
		JSONObjectBuilder pageJSONObjectBuilder,
		JSONObjectBuilder itemJSONObjectBuilder, String url) {

		_singleModelJSONLDMessageMapper.mapSelfURL(itemJSONObjectBuilder, url);
	}

	@Override
	public void mapItemTotalCount(
		JSONObjectBuilder jsonObjectBuilder, int totalCount) {

		jsonObjectBuilder.field(
			HYDRA_TOTAL_ITEMS
		).value(
			totalCount
		);
	}

	@Override
	public void mapItemTypes(
		JSONObjectBuilder pageJSONObjectBuilder,
		JSONObjectBuilder itemJSONObjectBuilder, List<String> types) {

		_singleModelJSONLDMessageMapper.mapTypes(itemJSONObjectBuilder, types);
	}

	@Override
	public void mapLastPageURL(
		JSONObjectBuilder jsonObjectBuilder, String url) {

		jsonObjectBuilder.nestedField(
			HYDRA_VIEW, HYDRA_LAST
		).value(
			url
		);
	}

	@Override
	public void mapNextPageURL(
		JSONObjectBuilder jsonObjectBuilder, String url) {

		jsonObjectBuilder.nestedField(
			HYDRA_VIEW, HYDRA_NEXT
		).value(
			url
		);
	}

	@Override
	public void mapPageCount(JSONObjectBuilder jsonObjectBuilder, int count) {
		jsonObjectBuilder.field(HYDRA_PAGE_COUNT).value(count);
	}

	@Override
	public void mapPreviousPageURL(
		JSONObjectBuilder jsonObjectBuilder, String url) {

		jsonObjectBuilder.nestedField(
			HYDRA_VIEW, HYDRA_PREVIOUS
		).value(
			url
		);
	}

	@Override
	public void onFinish(
		JSONObjectBuilder jsonObjectBuilder, Page<T> page, Class<T> modelClass,
		RequestInfo requestInfo) {

		jsonObjectBuilder.field(
			JSON_LD_TYPE
		).value(
			HYDRA_COLLECTION
		);

		jsonObjectBuilder.field(
			JSON_LD_CONTEXT
		).arrayValue().add(
			HYDRA_CONTEXT
		);

		jsonObjectBuilder.field(
			JSON_LD_CONTEXT
		).arrayValue().add(
			nestedJsonObjectBuilder -> nestedJsonObjectBuilder.field(
				JSON_LD_VOCAB
			).value(
				JSON_LD_SCHEMA_ORG
			)
		);
	}

	@Override
	public void onFinishItem(
		JSONObjectBuilder pageJSONObjectBuilder,
		JSONObjectBuilder itemJSONObjectBuilder, T model, Class<T> modelClass,
		RequestInfo requestInfo) {

		pageJSONObjectBuilder.field(
			HYDRA_MEMBERS
		).arrayValue().add(
			itemJSONObjectBuilder
		);
	}

	@Reference
	private SingleModelJSONLDMessageMapper<T> _singleModelJSONLDMessageMapper;

}