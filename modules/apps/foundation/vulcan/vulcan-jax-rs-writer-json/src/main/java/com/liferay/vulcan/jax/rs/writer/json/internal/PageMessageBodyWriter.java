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

import static org.osgi.service.component.annotations.ReferenceCardinality.AT_LEAST_ONE;
import static org.osgi.service.component.annotations.ReferencePolicyOption.GREEDY;

import com.liferay.vulcan.error.VulcanDeveloperError;
import com.liferay.vulcan.list.FunctionalList;
import com.liferay.vulcan.message.PageJSONMessageMapper;
import com.liferay.vulcan.message.RequestInfo;
import com.liferay.vulcan.message.json.JSONObjectBuilder;
import com.liferay.vulcan.pagination.Page;
import com.liferay.vulcan.representor.ModelRepresentorMapper;
import com.liferay.vulcan.wiring.osgi.RelatedModel;
import com.liferay.vulcan.wiring.osgi.RepresentorManager;
import com.liferay.vulcan.wiring.osgi.URIResolver;

import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintWriter;

import java.lang.annotation.Annotation;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

import javax.ws.rs.WebApplicationException;
import javax.ws.rs.container.ResourceInfo;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.UriInfo;
import javax.ws.rs.ext.MessageBodyWriter;
import javax.ws.rs.ext.Provider;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Alejandro Hernández
 * @author Carlos Sierra Andrés
 * @author Jorge Ferrer
 */
@Component(
	immediate = true, property = "liferay.vulcan.message.body.writer=true"
)
@Provider
public class PageMessageBodyWriter<T> implements MessageBodyWriter<Page<T>> {

	@Override
	public long getSize(
		Page<T> page, Class<?> type, Type genericType, Annotation[] annotations,
		MediaType mediaType) {

		return -1;
	}

	@Override
	public boolean isWriteable(
		Class<?> type, Type genericType, Annotation[] annotations,
		MediaType mediaType) {

		Class<?> returnType = resourceInfo.getResourceMethod().getReturnType();

		if (!returnType.isAssignableFrom(Page.class)) {
			return false;
		}

		Type genericReturnType =
			resourceInfo.getResourceMethod().getGenericReturnType();

		Type[] actualTypeArguments =
			((ParameterizedType)genericReturnType).getActualTypeArguments();

		try {
			Class<T> modelClass = (Class<T>)actualTypeArguments[0];

			Optional<ModelRepresentorMapper<T>> optional =
				_representorManager.getModelRepresentorMapperOptional(
					modelClass);

			if (!optional.isPresent()) {
				return false;
			}

			return true;
		}
		catch (ClassCastException cce) {
			return false;
		}
	}

	@Override
	public void writeTo(
			Page<T> page, Class<?> type, Type genericType,
			Annotation[] annotations, MediaType mediaType,
			MultivaluedMap<String, Object> httpHeaders,
			OutputStream entityStream)
		throws IOException, WebApplicationException {

		Type genericReturnType =
			resourceInfo.getResourceMethod().getGenericReturnType();

		Type[] actualTypeArguments =
			((ParameterizedType)genericReturnType).getActualTypeArguments();

		Class<T> modelClass = (Class<T>)actualTypeArguments[0];

		JSONObjectBuilder jsonObjectBuilder = new JSONObjectBuilderImpl();

		RequestInfo requestInfo = new RequestInfoImpl(mediaType, httpHeaders);

		Stream<PageJSONMessageMapper<T>> stream =
			_pageJSONMessageMappers.stream();

		PageJSONMessageMapper<T> pageJSONMessageMapper = stream.filter(
			bodyWriter ->
				mediaType.toString().equals(bodyWriter.getMediaType()) &&
					bodyWriter.supports(page, modelClass, requestInfo)
		).findFirst().orElseThrow(
			() -> new VulcanDeveloperError.CannotFindMessageMapper(
				mediaType.toString(), modelClass)
		);

		pageJSONMessageMapper.onStart(
			jsonObjectBuilder, page, modelClass, requestInfo);

		_writeItems(
			page, modelClass, requestInfo, pageJSONMessageMapper,
			jsonObjectBuilder);

		_writeItemTotalCount(pageJSONMessageMapper, jsonObjectBuilder, page);

		_writePageCount(pageJSONMessageMapper, jsonObjectBuilder, page);

		_writePageURLs(
			pageJSONMessageMapper, jsonObjectBuilder, page, modelClass);

		_writeCollectionURL(
			pageJSONMessageMapper, jsonObjectBuilder, modelClass);

		pageJSONMessageMapper.onFinish(
			jsonObjectBuilder, page, modelClass, requestInfo);

		PrintWriter printWriter = new PrintWriter(entityStream, true);

		printWriter.println(jsonObjectBuilder.build().toString());

		printWriter.close();
	}

	@Context
	protected ResourceInfo resourceInfo;

	@Context
	protected UriInfo uriInfo;

	private String _getCollectionURL(Class<T> modelClass) {
		Optional<String> optional =
			_uriResolver.getCollectionResourceURIOptional(modelClass);

		String uri = optional.orElseThrow(
			() -> new VulcanDeveloperError.CannotCalculateURI(modelClass));

		return _writerHelper.getAbsoluteURL(uriInfo, uri);
	}

	private String _getPageURL(
		Class<T> modelClass, int page, int itemsPerPage) {

		String url = _getCollectionURL(modelClass);

		return url + "?page=" + page + "&per_page=" + itemsPerPage;
	}

	private void _writeCollectionURL(
		PageJSONMessageMapper<T> pageJSONMessageMapper,
		JSONObjectBuilder jsonObjectBuilder, Class<T> modelClass) {

		String url = _getCollectionURL(modelClass);

		pageJSONMessageMapper.mapCollectionURL(jsonObjectBuilder, url);
	}

	private <U, V> void _writeEmbeddedRelatedModel(
		PageJSONMessageMapper<?> pageJSONMessageMapper,
		JSONObjectBuilder pageJSONObjectBuilder,
		JSONObjectBuilder itemJSONObjectBuilder,
		RelatedModel<U, V> relatedModel, U parentModel,
		FunctionalList<String> parentEmbeddedPathElements) {

		_writerHelper.writeRelatedModel(
			relatedModel, parentModel, parentEmbeddedPathElements, uriInfo,
			(model, modelClass, url, embeddedPathElements) -> {
				_writerHelper.writeFields(
					model, modelClass, (fieldName, value) ->
						pageJSONMessageMapper.mapItemEmbeddedResourceField(
							pageJSONObjectBuilder, itemJSONObjectBuilder,
							embeddedPathElements, fieldName, value));

				_writerHelper.writeLinks(
					modelClass, (fieldName, link) ->
						pageJSONMessageMapper.mapItemEmbeddedResourceLink(
							pageJSONObjectBuilder, itemJSONObjectBuilder,
							embeddedPathElements, fieldName, link));

				_writerHelper.writeTypes(
					modelClass, types ->
						pageJSONMessageMapper.mapItemEmbeddedResourceTypes(
							pageJSONObjectBuilder, itemJSONObjectBuilder,
							embeddedPathElements, types));

				pageJSONMessageMapper.mapItemEmbeddedResourceURL(
					pageJSONObjectBuilder, itemJSONObjectBuilder,
					embeddedPathElements, url);

				List<RelatedModel<V, ?>> embeddedRelatedModels =
					_representorManager.getEmbeddedRelatedModels(modelClass);

				embeddedRelatedModels.forEach(
					embeddedRelatedModel -> _writeEmbeddedRelatedModel(
						pageJSONMessageMapper, pageJSONObjectBuilder,
						itemJSONObjectBuilder, embeddedRelatedModel, model,
						embeddedPathElements));

				List<RelatedModel<V, ?>> linkedRelatedModels =
					_representorManager.getLinkedRelatedModels(modelClass);

				linkedRelatedModels.forEach(
					linkedRelatedModel -> _writeLinkedRelatedModel(
						pageJSONMessageMapper, pageJSONObjectBuilder,
						itemJSONObjectBuilder, linkedRelatedModel, model,
						embeddedPathElements));
			});
	}

	private void _writeItems(
		Page<T> page, Class<T> modelClass, RequestInfo requestInfo,
		PageJSONMessageMapper<T> pageJSONMessageMapper,
		JSONObjectBuilder jsonObjectBuilder) {

		page.getItems().forEach(
			item -> {
				JSONObjectBuilder itemJSONObjectBuilder =
					new JSONObjectBuilderImpl();

				pageJSONMessageMapper.onStartItem(
					jsonObjectBuilder, itemJSONObjectBuilder, item, modelClass,
					requestInfo);

				_writerHelper.writeFields(
					item, modelClass, (field, value) ->
						pageJSONMessageMapper.mapItemField(
							jsonObjectBuilder, itemJSONObjectBuilder, field,
							value));

				_writerHelper.writeLinks(
					modelClass, (fieldName, link) ->
						pageJSONMessageMapper.mapItemLink(
							jsonObjectBuilder, itemJSONObjectBuilder, fieldName,
							link));

				_writerHelper.writeTypes(
					modelClass, types -> pageJSONMessageMapper.mapItemTypes(
						jsonObjectBuilder, itemJSONObjectBuilder, types));

				_writerHelper.writeSingleResourceURL(
					item, modelClass, uriInfo, url ->
						pageJSONMessageMapper.mapItemSelfURL(
							jsonObjectBuilder, itemJSONObjectBuilder, url));

				List<RelatedModel<T, ?>> embeddedRelatedModels =
					_representorManager.getEmbeddedRelatedModels(modelClass);

				embeddedRelatedModels.forEach(
					embeddedRelatedModel -> _writeEmbeddedRelatedModel(
						pageJSONMessageMapper, jsonObjectBuilder,
						itemJSONObjectBuilder, embeddedRelatedModel, item,
						null));

				List<RelatedModel<T, ?>> linkedRelatedModels =
					_representorManager.getLinkedRelatedModels(modelClass);

				linkedRelatedModels.forEach(
					linkedRelatedModel -> _writeLinkedRelatedModel(
						pageJSONMessageMapper, jsonObjectBuilder,
						itemJSONObjectBuilder, linkedRelatedModel, item, null));

				pageJSONMessageMapper.onFinishItem(
					jsonObjectBuilder, itemJSONObjectBuilder, item, modelClass,
					requestInfo);
			});
	}

	private void _writeItemTotalCount(
		PageJSONMessageMapper<T> pageJSONMessageMapper,
		JSONObjectBuilder jsonObjectBuilder, Page<T> page) {

		int totalCount = page.getTotalCount();

		pageJSONMessageMapper.mapItemTotalCount(jsonObjectBuilder, totalCount);
	}

	private <U, V> void _writeLinkedRelatedModel(
		PageJSONMessageMapper<?> pageJSONMessageMapper,
		JSONObjectBuilder pageJSONObjectBuilder,
		JSONObjectBuilder itemJSONObjectBuilder,
		RelatedModel<U, V> relatedModel, U parentModel,
		FunctionalList<String> parentEmbeddedPathElements) {

		_writerHelper.writeRelatedModel(
			relatedModel, parentModel, parentEmbeddedPathElements, uriInfo,
			(model, modelClass, url, embeddedPathElements) ->
				pageJSONMessageMapper.mapItemLinkedResourceURL(
					pageJSONObjectBuilder, itemJSONObjectBuilder,
					embeddedPathElements, url));
	}

	private void _writePageCount(
		PageJSONMessageMapper<T> pageJSONMessageMapper,
		JSONObjectBuilder jsonObjectBuilder, Page<T> page) {

		int count = page.getItems().size();

		pageJSONMessageMapper.mapPageCount(jsonObjectBuilder, count);
	}

	private void _writePageURLs(
		PageJSONMessageMapper<T> pageJSONMessageMapper,
		JSONObjectBuilder jsonObjectBuilder, Page<T> page,
		Class<T> modelClass) {

		int itemsPerPage = page.getItemsPerPage();

		int currentPage = page.getPageNumber();

		pageJSONMessageMapper.mapCurrentPageURL(
			jsonObjectBuilder,
			_getPageURL(modelClass, currentPage, itemsPerPage));

		pageJSONMessageMapper.mapFirstPageURL(
			jsonObjectBuilder, _getPageURL(modelClass, 1, itemsPerPage));

		if (page.hasPrevious()) {
			pageJSONMessageMapper.mapPreviousPageURL(
				jsonObjectBuilder,
				_getPageURL(modelClass, currentPage - 1, itemsPerPage));
		}

		if (page.hasNext()) {
			pageJSONMessageMapper.mapNextPageURL(
				jsonObjectBuilder,
				_getPageURL(modelClass, currentPage + 1, itemsPerPage));
		}

		pageJSONMessageMapper.mapLastPageURL(
			jsonObjectBuilder,
			_getPageURL(modelClass, page.getLastPageNumber(), itemsPerPage));
	}

	@Reference(cardinality = AT_LEAST_ONE, policyOption = GREEDY)
	private List<PageJSONMessageMapper<T>> _pageJSONMessageMappers;

	@Reference
	private RepresentorManager _representorManager;

	@Reference
	private URIResolver _uriResolver;

	@Reference
	private WriterHelper _writerHelper;

}