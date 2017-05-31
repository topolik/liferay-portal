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

import com.liferay.vulcan.error.VulcanDeveloperError;
import com.liferay.vulcan.list.FunctionalList;
import com.liferay.vulcan.wiring.osgi.RelatedModel;
import com.liferay.vulcan.wiring.osgi.RepresentorManager;
import com.liferay.vulcan.wiring.osgi.URIResolver;

import java.net.URI;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Function;

import javax.ws.rs.core.UriBuilder;
import javax.ws.rs.core.UriInfo;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Alejandro Hernández
 * @author Carlos Sierra Andrés
 * @author Jorge Ferrer
 */
@Component(immediate = true, service = WriterHelper.class)
public class WriterHelper {

	public String getAbsoluteURL(UriInfo uriInfo, String relativeURI) {
		UriBuilder uriBuilder = uriInfo.getBaseUriBuilder().clone();

		URI uri = uriBuilder.path(relativeURI).build();

		return uri.toString();
	}

	public <T> void writeFields(
		T model, Class<T> modelClass, BiConsumer<String, Object> biConsumer) {

		Map<String, Function<T, Object>> fieldFunctions =
			_representorManager.getFieldFunctions(modelClass);

		for (String field : fieldFunctions.keySet()) {
			Object data = fieldFunctions.get(field).apply(model);

			if (data != null) {
				biConsumer.accept(field, data);
			}
		}
	}

	public <T> void writeLinks(
		Class<T> modelClass, BiConsumer<String, String> biConsumer) {

		Map<String, String> links = _representorManager.getLinks(modelClass);

		for (String key : links.keySet()) {
			biConsumer.accept(key, links.get(key));
		}
	}

	public <T, U> void writeRelatedModel(
		RelatedModel<T, U> relatedModel, T parentModel,
		FunctionalList<String> parentEmbeddedPathElements, UriInfo uriInfo,
		TetraConsumer<U, Class<U>, String, FunctionalList<String>>
			tetraConsumer) {

		Optional<U> modelOptional =
			relatedModel.getModelFunction().apply(parentModel);

		if (!modelOptional.isPresent()) {
			return;
		}

		U model = modelOptional.get();

		Class<U> modelClass = relatedModel.getModelClass();

		Optional<String> uriOptional =
			_uriResolver.getSingleResourceURIOptional(modelClass, model);

		uriOptional.ifPresent(
			uri -> {
				String key = relatedModel.getKey();

				FunctionalList<String> embeddedPathElements =
					new StringFunctionalList(parentEmbeddedPathElements, key);

				String url = getAbsoluteURL(uriInfo, uri);

				tetraConsumer.accept(
					model, modelClass, url, embeddedPathElements);
			});
	}

	public <T> void writeSingleResourceURL(
		T model, Class<T> modelClass, UriInfo uriInfo,
		Consumer<String> consumer) {

		Optional<String> optional = _uriResolver.getSingleResourceURIOptional(
			modelClass, model);

		String uri = optional.orElseThrow(
			() -> new VulcanDeveloperError.CannotCalculateURI(modelClass));

		String url = getAbsoluteURL(uriInfo, uri);

		consumer.accept(url);
	}

	public <U> void writeTypes(
		Class<U> modelClass, Consumer<List<String>> consumer) {

		List<String> types = _representorManager.getTypes(modelClass);

		consumer.accept(types);
	}

	@Reference
	private RepresentorManager _representorManager;

	@Reference
	private URIResolver _uriResolver;

}