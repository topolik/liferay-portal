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

package com.liferay.dynamic.data.mapping.data.provider.internal.rest;

import com.jayway.jsonpath.DocumentContext;
import com.jayway.jsonpath.JsonPath;

import com.liferay.dynamic.data.mapping.data.provider.DDMDataProvider;
import com.liferay.dynamic.data.mapping.data.provider.DDMDataProviderContext;
import com.liferay.dynamic.data.mapping.data.provider.DDMDataProviderException;
import com.liferay.dynamic.data.mapping.data.provider.DDMDataProviderOutputParametersSettings;
import com.liferay.dynamic.data.mapping.data.provider.DDMDataProviderRequest;
import com.liferay.dynamic.data.mapping.data.provider.DDMDataProviderResponse;
import com.liferay.dynamic.data.mapping.data.provider.DDMDataProviderResponse.Status;
import com.liferay.dynamic.data.mapping.data.provider.DDMDataProviderResponseOutput;
import com.liferay.petra.string.CharPool;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.cache.MultiVMPool;
import com.liferay.portal.kernel.cache.PortalCache;
import com.liferay.portal.kernel.util.KeyValuePair;
import com.liferay.portal.kernel.util.ListUtil;
import com.liferay.portal.kernel.util.MapUtil;
import com.liferay.portal.kernel.util.PredicateFilter;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.kernel.util.Validator;

import java.io.IOException;
import java.io.Serializable;

import java.net.ConnectException;
import java.net.URI;
import java.net.URISyntaxException;

import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import org.apache.http.HttpEntity;
import org.apache.http.HttpRequest;
import org.apache.http.HttpResponse;
import org.apache.http.RequestLine;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.Credentials;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.CredentialsProvider;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.impl.client.BasicCredentialsProvider;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Marcellus Tavares
 */
@Component(immediate = true, property = "ddm.data.provider.type=rest")
public class DDMRESTDataProvider implements DDMDataProvider {

	@Override
	public List<KeyValuePair> getData(
			DDMDataProviderContext ddmDataProviderContext)
		throws DDMDataProviderException {

		try {
			DDMDataProviderRequest ddmDataProviderRequest =
				createDDMDataProviderRequest(ddmDataProviderContext);

			DDMDataProviderResponse ddmDataProviderResponse = doGetData(
				ddmDataProviderRequest);

			DDMDataProviderResponseOutput ddmDataProviderResponseOutput =
				ddmDataProviderResponse.get("Default-Output");

			List<KeyValuePair> results = ddmDataProviderResponseOutput.getValue(
				List.class);

			return results;
		}
		catch (Exception e) {
			throw new DDMDataProviderException(e);
		}
	}

	@Override
	public DDMDataProviderResponse getData(
			DDMDataProviderRequest ddmDataProviderRequest)
		throws DDMDataProviderException {

		try {
			return doGetData(ddmDataProviderRequest);
		}
		catch (IOException ioe) {
			Throwable cause = ioe.getCause();

			if (cause instanceof ConnectException) {
				return DDMDataProviderResponse.error(
					Status.SERVICE_UNAVAILABLE);
			}
			else {
				throw new DDMDataProviderException(ioe);
			}
		}
		catch (Exception e) {
			throw new DDMDataProviderException(e);
		}
	}

	@Override
	public Class<?> getSettings() {
		return DDMRESTDataProviderSettings.class;
	}

	protected URI buildURL(
			DDMDataProviderRequest ddmDataProviderRequest,
			DDMRESTDataProviderSettings ddmRESTDataProviderSettings)
		throws URISyntaxException {

		Map<String, String> pathParameters = getPathParameters(
			ddmDataProviderRequest, ddmRESTDataProviderSettings);

		String url = ddmRESTDataProviderSettings.url();

		for (Entry<String, String> pathParameter : pathParameters.entrySet()) {
			url = StringUtil.replaceFirst(
				url, String.format("{%s}", pathParameter.getKey()),
				pathParameter.getValue());
		}

		URIBuilder uriBuilder = new URIBuilder(url);

		if (ddmRESTDataProviderSettings.filterable()) {
			uriBuilder = uriBuilder.setParameter(
				ddmRESTDataProviderSettings.filterParameterName(),
				ddmDataProviderRequest.getParameter("filterParameterValue"));
		}

		if (ddmRESTDataProviderSettings.pagination()) {
			uriBuilder = uriBuilder.setParameter(
				ddmRESTDataProviderSettings.paginationEndParameterName(),
				ddmDataProviderRequest.getParameter("paginationStart"));
			uriBuilder = uriBuilder.setParameter(
				ddmRESTDataProviderSettings.paginationEndParameterName(),
				ddmDataProviderRequest.getParameter("paginationEnd"));
		}

		Map<String, String> queryParameters = getQueryParameters(
			ddmDataProviderRequest, ddmRESTDataProviderSettings);

		for (Entry<String, String> entry : queryParameters.entrySet()) {
			uriBuilder = uriBuilder.setParameter(
				entry.getKey(), entry.getValue());
		}

		return uriBuilder.build();
	}

	protected DDMDataProviderRequest createDDMDataProviderRequest(
		DDMDataProviderContext ddmDataProviderContext) {

		DDMDataProviderRequest ddmDataProviderRequest =
			new DDMDataProviderRequest(null, null);

		ddmDataProviderRequest.setDDMDataProviderContext(
			ddmDataProviderContext);

		// Backwards compatibility

		ddmDataProviderRequest.queryString(
			ddmDataProviderContext.getParameters());

		return ddmDataProviderRequest;
	}

	protected DDMDataProviderResponse createDDMDataProviderResponse(
		DocumentContext documentContext,
		DDMDataProviderRequest ddmDataProviderRequest,
		DDMRESTDataProviderSettings ddmRESTDataProviderSettings) {

		DDMDataProviderOutputParametersSettings[] outputParameterSettingsArray =
			ddmRESTDataProviderSettings.outputParameters();

		if ((outputParameterSettingsArray == null) ||
			(outputParameterSettingsArray.length == 0)) {

			return DDMDataProviderResponse.of();
		}

		List<DDMDataProviderResponseOutput> ddmDataProviderResponseOutputs =
			new ArrayList<>();

		for (DDMDataProviderOutputParametersSettings outputParameterSettings :
				outputParameterSettingsArray) {

			String name = outputParameterSettings.outputParameterName();
			String type = outputParameterSettings.outputParameterType();
			String path = outputParameterSettings.outputParameterPath();

			if (Objects.equals(type, "text")) {
				String value = documentContext.read(
					normalizePath(path), String.class);

				if (value != null) {
					ddmDataProviderResponseOutputs.add(
						DDMDataProviderResponseOutput.of(name, "text", value));
				}
			}
			else if (Objects.equals(type, "number")) {
				Number value = documentContext.read(
					normalizePath(path), Number.class);

				if (value != null) {
					ddmDataProviderResponseOutputs.add(
						DDMDataProviderResponseOutput.of(
							name, "number", value));
				}
			}
			else if (Objects.equals(type, "list")) {
				String[] paths = StringUtil.split(path, CharPool.SEMICOLON);

				String normalizedValuePath = normalizePath(paths[0]);

				String normalizedKeyPath = normalizedValuePath;

				List<String> values = documentContext.read(
					normalizedValuePath, List.class);

				if (values == null) {
					continue;
				}

				List<String> keys = new ArrayList<>(values);

				if (paths.length >= 2) {
					normalizedKeyPath = normalizePath(paths[1]);

					keys = documentContext.read(normalizedKeyPath);
				}

				List<KeyValuePair> keyValuePairs = new ArrayList<>();

				for (int i = 0; i < values.size(); i++) {
					keyValuePairs.add(
						new KeyValuePair(keys.get(i), values.get(i)));
				}

				if (ddmRESTDataProviderSettings.pagination()) {
					int start = Integer.valueOf(
						ddmDataProviderRequest.getParameter("paginationStart"));

					int end = Integer.valueOf(
						ddmDataProviderRequest.getParameter("paginationEnd"));

					if (keyValuePairs.size() > (end - start)) {
						keyValuePairs = ListUtil.subList(
							keyValuePairs, start, end);
					}
				}

				ddmDataProviderResponseOutputs.add(
					DDMDataProviderResponseOutput.of(
						name, "list", keyValuePairs));
			}
		}

		int size = ddmDataProviderResponseOutputs.size();

		return DDMDataProviderResponse.of(
			ddmDataProviderResponseOutputs.toArray(
				new DDMDataProviderResponseOutput[size]));
	}

	protected DDMDataProviderResponse doGetData(
			DDMDataProviderRequest ddmDataProviderRequest)
		throws IOException, KeyManagementException, NoSuchAlgorithmException,
			   URISyntaxException {

		DDMDataProviderContext ddmDataProviderContext =
			ddmDataProviderRequest.getDDMDataProviderContext();

		DDMRESTDataProviderSettings ddmRESTDataProviderSettings =
			ddmDataProviderContext.getSettingsInstance(
				DDMRESTDataProviderSettings.class);

		HttpUriRequest httpUriRequest = new HttpGet(
			buildURL(ddmDataProviderRequest, ddmRESTDataProviderSettings));

		String cacheKey = getCacheKey(httpUriRequest);

		DDMRESTDataProviderResult ddmRESTDataProviderResult = _portalCache.get(
			cacheKey);

		if ((ddmRESTDataProviderResult != null) &&
			ddmRESTDataProviderSettings.cacheable()) {

			return ddmRESTDataProviderResult.getDDMDataProviderResponse();
		}

		CloseableHttpClient closeableHttpClient = getCloseableHttpClient(
			ddmRESTDataProviderSettings);

		try {
			HttpResponse httpResponse = closeableHttpClient.execute(
				httpUriRequest);

			HttpEntity entity = httpResponse.getEntity();

			DocumentContext documentContext = JsonPath.parse(
				entity.getContent());

			DDMDataProviderResponse ddmDataProviderResponse =
				createDDMDataProviderResponse(
					documentContext, ddmDataProviderRequest,
					ddmRESTDataProviderSettings);

			if (ddmRESTDataProviderSettings.cacheable()) {
				_portalCache.put(
					cacheKey,
					new DDMRESTDataProviderResult(ddmDataProviderResponse));
			}

			return ddmDataProviderResponse;
		}
		finally {
			closeableHttpClient.close();
		}
	}

	protected String getCacheKey(HttpRequest httpRequest) {
		RequestLine requestLine = httpRequest.getRequestLine();

		return requestLine.getUri();
	}

	protected CloseableHttpClient getCloseableHttpClient(
			DDMRESTDataProviderSettings ddmRESTDataProviderSettings)
		throws KeyManagementException, NoSuchAlgorithmException {

		HttpClientBuilder httpClientBuilder = HttpClientBuilder.create();

		SSLContext sslContext = SSLContext.getInstance("TLS");

		sslContext.init(
			null, new TrustManager[] {new TrustAllX509TrustManager()}, null);

		httpClientBuilder = httpClientBuilder.setSSLSocketFactory(
			new SSLConnectionSocketFactory(
				sslContext, (s, sslSession) -> true));

		if (Validator.isNotNull(ddmRESTDataProviderSettings.username())) {
			CredentialsProvider credentialsProvider =
				new BasicCredentialsProvider();

			Credentials credentials = new UsernamePasswordCredentials(
				ddmRESTDataProviderSettings.username(),
				ddmRESTDataProviderSettings.password());

			credentialsProvider.setCredentials(AuthScope.ANY, credentials);

			httpClientBuilder = httpClientBuilder.setDefaultCredentialsProvider(
				credentialsProvider);
		}

		return httpClientBuilder.build();
	}

	protected Map<String, String> getPathParameters(
		DDMDataProviderRequest ddmDataProviderRequest,
		DDMRESTDataProviderSettings ddmRESTDataProviderSettings) {

		Map<String, String> parameters = ddmDataProviderRequest.getParameters();

		Map<String, String> pathParameters = new HashMap<>();

		Matcher matcher = _pathParameterPattern.matcher(
			ddmRESTDataProviderSettings.url());

		while (matcher.find()) {
			String parameterName = matcher.group(1);

			if (parameters.containsKey(parameterName)) {
				pathParameters.put(
					parameterName, parameters.get(parameterName));
			}
		}

		return pathParameters;
	}

	protected Map<String, String> getQueryParameters(
		DDMDataProviderRequest ddmDataProviderRequest,
		DDMRESTDataProviderSettings ddmRESTDataProviderSettings) {

		Map<String, String> pathParameters = getPathParameters(
			ddmDataProviderRequest, ddmRESTDataProviderSettings);

		return MapUtil.filter(
			ddmDataProviderRequest.getParameters(),
			new PredicateFilter<Entry<String, String>>() {

				@Override
				public boolean filter(Entry<String, String> parameter) {
					return !pathParameters.containsKey(parameter.getKey());
				}

			});
	}

	protected String normalizePath(String path) {
		if (StringUtil.startsWith(path, StringPool.PERIOD) ||
			StringUtil.startsWith(path, StringPool.DOLLAR)) {

			return path;
		}

		return StringPool.PERIOD.concat(path);
	}

	@Reference(unbind = "-")
	protected void setMultiVMPool(MultiVMPool multiVMPool) {
		_portalCache =
			(PortalCache<String, DDMRESTDataProviderResult>)
				multiVMPool.getPortalCache(DDMRESTDataProvider.class.getName());
	}

	private final Pattern _pathParameterPattern = Pattern.compile("\\{(.*)\\}");
	private PortalCache<String, DDMRESTDataProviderResult> _portalCache;

	private static class DDMRESTDataProviderResult implements Serializable {

		public DDMRESTDataProviderResult(
			DDMDataProviderResponse ddmDataProviderResponse) {

			_ddmDataProviderResponse = ddmDataProviderResponse;
		}

		public DDMDataProviderResponse getDDMDataProviderResponse() {
			return _ddmDataProviderResponse;
		}

		private final DDMDataProviderResponse _ddmDataProviderResponse;

	}

	private static class TrustAllX509TrustManager implements X509TrustManager {

		@Override
		public void checkClientTrusted(
				X509Certificate[] x509Certificates, String s)
			throws CertificateException {
		}

		@Override
		public void checkServerTrusted(
				X509Certificate[] x509Certificates, String s)
			throws CertificateException {
		}

		@Override
		public X509Certificate[] getAcceptedIssuers() {
			return null;
		}

	}

}