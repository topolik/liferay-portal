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

package com.liferay.oauth2.provider.client.test;

import aQute.bnd.osgi.Analyzer;
import aQute.bnd.osgi.Jar;

import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.util.CharPool;
import com.liferay.portal.kernel.util.CookieKeys;
import com.liferay.portal.kernel.util.Digester;
import com.liferay.portal.kernel.util.DigesterUtil;
import com.liferay.portal.kernel.util.HttpUtil;
import com.liferay.portal.kernel.util.StringPool;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.util.DigesterImpl;
import com.liferay.portal.util.HttpImpl;
import com.liferay.shrinkwrap.osgi.api.BndProjectBuilder;

import java.io.ByteArrayOutputStream;
import java.io.File;

import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;

import java.util.Map;
import java.util.Properties;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.jar.Attributes;
import java.util.jar.Manifest;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.Invocation;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.Cookie;
import javax.ws.rs.core.MultivaluedHashMap;
import javax.ws.rs.core.NewCookie;
import javax.ws.rs.core.Response;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.apache.cxf.jaxrs.provider.json.JSONProvider;

import org.codehaus.jettison.json.JSONException;
import org.codehaus.jettison.json.JSONObject;
import org.jboss.arquillian.test.api.ArquillianResource;
import org.jboss.shrinkwrap.api.Archive;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.asset.ByteArrayAsset;
import org.jboss.shrinkwrap.api.exporter.ZipExporter;
import org.jboss.shrinkwrap.api.spec.JavaArchive;

import org.junit.BeforeClass;
import org.osgi.framework.BundleActivator;

import org.w3c.dom.Document;

public class BaseClientTest {

	@BeforeClass
	public static void setupClass() {
		System.setProperty("sun.net.http.allowRestrictedHeaders", "true");

		new HttpUtil().setHttp(new HttpImpl());
		new DigesterUtil().setDigester(new DigesterImpl());
	}

	public static Archive<?> getDeployment(
		Class<? extends BundleActivator> bundleActivator) throws Exception {

		BndProjectBuilder bndProjectBuilder = ShrinkWrap.create(
			BndProjectBuilder.class);

		String javaClassPathString = System.getProperty("java.class.path");

		String[] javaClassPaths = javaClassPathString.split(File.pathSeparator);

		for (String javaClassPath : javaClassPaths) {
			File file = new File(javaClassPath);

			if (file.isDirectory() ||
				javaClassPath.toLowerCase().endsWith(".zip") ||
				javaClassPath.toLowerCase().endsWith(".jar")) {

				bndProjectBuilder.addClassPath(file);
			}
		}

		Analyzer analyzer = new Analyzer();

		File bndFile = new File("bnd.bnd");

		JavaArchive javaArchive = bndProjectBuilder.setBndFile(
			bndFile
		).as(
			JavaArchive.class
		);

		javaArchive.addClass(bundleActivator);

		ZipExporter zipExporter = javaArchive.as(ZipExporter.class);

		Jar jar = new Jar(
			javaArchive.getName(), zipExporter.exportAsInputStream());

		Properties analyzerProperties = new Properties();

		analyzerProperties.putAll(analyzer.loadProperties(bndFile));

		analyzer.setJar(jar);

		Manifest firstPassManifest = new Manifest(
			javaArchive.get("META-INF/MANIFEST.MF").getAsset().openStream());

		firstPassManifest.getMainAttributes().remove(new Attributes.Name("Import-Package"));

		firstPassManifest.getMainAttributes().putValue("Bundle-Activator", bundleActivator.getName());
		analyzer.mergeManifest(firstPassManifest);
		Manifest manifest = analyzer.calcManifest();

		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		manifest.write(baos);
		ByteArrayAsset byteArrayAsset = new ByteArrayAsset(baos.toByteArray());

		javaArchive.delete("META-INF/MANIFEST.MF");

		javaArchive.add(byteArrayAsset, "META-INF/MANIFEST.MF");

		return javaArchive;
	}

	public URL getUrl() {
		return _url;
	}

	protected static Client getClient() {
		return ClientBuilder.newClient().register(JSONProvider.class);
	}

	protected Invocation.Builder authorize(
		Invocation.Builder builder, String tokenString) {

		return builder.header("Authorization", "Bearer " + tokenString);
	}

	protected String getToken(String clientId) throws URISyntaxException {
		return getToken(clientId, null);
	}

	protected String getToken(String clientId, String hostname)
		throws URISyntaxException {

		return parseTokenString(
			getClientCredentials(
				clientId, getTokenInvocationBuilder(hostname)));
	}

	protected <T> T getToken(
		String clientId, String hostname,
		BiFunction<String, Invocation.Builder, Response> credentials,
		Function<Response, T> tokenParser) throws URISyntaxException {

		return tokenParser.apply(
			credentials.apply(clientId, getTokenInvocationBuilder(hostname)));
	}

	protected String parseTokenString(Response tokenResponse) {
		String jsonString = tokenResponse.readEntity(String.class);

		try {
			JSONObject jsonObject = new JSONObject(jsonString);

			return jsonObject.getString("access_token");
		}
		catch (JSONException e) {
			throw new IllegalArgumentException(
				"The token service returned " + jsonString);
		}
	}

	protected JSONObject parseJsonObject(Response tokenResponse) {
		String jsonString = tokenResponse.readEntity(String.class);

		try {
			return  new JSONObject(jsonString);
		}
		catch (JSONException e) {
			throw new IllegalArgumentException(
				"The token service returned " + jsonString);
		}
	}

	protected String parseScopeString(Response tokenResponse) {
		String jsonString = tokenResponse.readEntity(String.class);

		try {
			JSONObject jsonObject = new JSONObject(jsonString);

			return jsonObject.getString("scope");
		}
		catch (JSONException e) {
			throw new IllegalArgumentException(
				"The token service returned " + jsonString);
		}
	}

	protected String parseError(Response tokenResponse) {
		String jsonString = tokenResponse.readEntity(String.class);

		try {
			JSONObject jsonObject = new JSONObject(jsonString);

			return jsonObject.getString("error");
		}
		catch (JSONException e) {
			throw new IllegalArgumentException(
				"The token service returned " + jsonString);
		}
	}

	protected BiFunction<String, Invocation.Builder, Response>
	getClientCredentials(String scope) {

		return (clientId, builder) -> {
			MultivaluedHashMap<String, String> formData =
				new MultivaluedHashMap<>();

			formData.add("client_id", clientId);
			formData.add("client_secret", "oauthTestApplicationSecret");
			formData.add("grant_type", "client_credentials");
			formData.add("scope", scope);

			return builder.post(Entity.form(formData));
		};
	}

	protected Response getClientCredentials(
		String clientId, Invocation.Builder builder) {
		MultivaluedHashMap<String, String> formData =
			new MultivaluedHashMap<>();

		formData.add("client_id", clientId);
		formData.add("client_secret", "oauthTestApplicationSecret");
		formData.add("grant_type", "client_credentials");

		return builder.post(Entity.form(formData));
	}

	protected BiFunction<String, Invocation.Builder, Response>
		getResourceOwnerPassword(String user, String password) {

		return (clientId, builder) -> {
			MultivaluedHashMap<String, String> formData =
				new MultivaluedHashMap<>();

			formData.add("client_id", clientId);
			formData.add("client_secret", "oauthTestApplicationSecret");
			formData.add("grant_type", "password");
			formData.add("username", user);
			formData.add("password", password);

			return builder.post(Entity.form(formData));
		};
	}

	protected BiFunction<String, Invocation.Builder, Response>
		getResourceOwnerPassword(String user, String password, String scope) {

		return (clientId, builder) -> {
			MultivaluedHashMap<String, String> formData =
				new MultivaluedHashMap<>();

			formData.add("client_id", clientId);
			formData.add("client_secret", "oauthTestApplicationSecret");
			formData.add("grant_type", "password");
			formData.add("username", user);
			formData.add("password", password);
			formData.add("scope", scope);

			return builder.post(Entity.form(formData));
		};
	}

	protected BiFunction<String, Invocation.Builder, Response>
		getAuthorizationCode(String user, String password, String hostname) {

		return (clientId, builder) -> {
			String authorizationCode = getAuthorizationCode(
				user, password, hostname,
				webTarget ->
					webTarget.queryParam("client_id", clientId).
						queryParam("response_type", "code"));

			MultivaluedHashMap<String, String> formData =
				new MultivaluedHashMap<>();

			formData.add("client_id", clientId);
			formData.add("client_secret", "oauthTestApplicationSecret");
			formData.add("grant_type", "authorization_code");
			formData.add("code", authorizationCode);

			return builder.post(Entity.form(formData));
		};
	}

	protected BiFunction<String, Invocation.Builder, Response>
		getAuthorizationCodePKCE(String user, String password, String hostname) {

		return (clientId, builder) -> {
			String codeVerifier = RandomTestUtil.randomString();

			String base64Digest = DigesterUtil.digestBase64(
				Digester.SHA_256, codeVerifier);

			String base64UrlDigest = StringUtil.replace(
				base64Digest, new char[]{CharPool.PLUS, CharPool.SLASH},
				new char[]{CharPool.MINUS, CharPool.UNDERLINE});

			base64UrlDigest = StringUtil.removeChar(
				base64UrlDigest, CharPool.EQUAL);

			final String codeChallenge = base64UrlDigest;

			String authorizationCode = getAuthorizationCode(
				user, password, hostname,
				webTarget ->
					webTarget.queryParam("client_id", clientId).
						queryParam("code_challenge", codeChallenge).
						queryParam("response_type", "code"));

			MultivaluedHashMap<String, String> formData =
				new MultivaluedHashMap<>();

			formData.add("client_id", clientId);
			formData.add("code", authorizationCode);
			formData.add("code_verifier", codeVerifier);
			formData.add("grant_type", "authorization_code");

			return builder.post(Entity.form(formData));
		};
	}

	protected Invocation.Builder getTokenInvocationBuilder(String hostname)
		throws URISyntaxException {

		return getInvocationBuilder(hostname, getTokenWebTarget());
	}

	protected Invocation.Builder getInvocationBuilder(
		String hostname, WebTarget webTarget) {

		Invocation.Builder builder = webTarget.request();

		if (hostname != null) {
			builder = builder.header("Host", hostname);
		}

		return builder;
	}

	protected WebTarget getOAuth2WebTarget() throws URISyntaxException {
		Client client = getClient();

		return client.target(
			_url.toURI()).path("o").path("oauth2");
	}

	protected WebTarget getTokenWebTarget() throws URISyntaxException {
		return getOAuth2WebTarget().path("token");
	}

	protected WebTarget getAuthorizeWebTarget() throws URISyntaxException {
		return getOAuth2WebTarget().path("authorize");
	}

	protected WebTarget getAuthorizeDecisionWebTarget() throws URISyntaxException {
		return getAuthorizeWebTarget().path("decision");
	}

	protected WebTarget getLoginWebTarget() throws URISyntaxException {
		Client client = getClient();

		return client.target(
			_url.toURI()).path("c").path("portal").path("login");
	}

	protected Cookie getAuthenticatedCookie(
			String login, String password, String hostname)
		throws URISyntaxException {

		Invocation.Builder loginInvocationBuilder = getInvocationBuilder(
			hostname, getLoginWebTarget());

		MultivaluedHashMap<String, String> formData =
			new MultivaluedHashMap<>();

		formData.add("login", login);
		formData.add("password", password);

		Response loginResponse = loginInvocationBuilder.post(
			Entity.form(formData));

		NewCookie cookie =
			loginResponse.getCookies().get(CookieKeys.JSESSIONID);

		if (cookie == null) {
			return null;
		}

		return cookie.toCookie();
	}

	protected String getAuthorizationCode(
			String login, String password, String hostname,
			Function<WebTarget, WebTarget> authorizeRequestFunction) {

		try {
			Invocation.Builder authorizeInvocationBuilder =
				getInvocationBuilder(
					hostname, authorizeRequestFunction.apply(
						getAuthorizeWebTarget()));

			Cookie authenticatedCookie = getAuthenticatedCookie(
				login, password, hostname);

			Response authorizeResponse =
				authorizeInvocationBuilder.accept("text/html").
					cookie(authenticatedCookie).get();

			URI location = authorizeResponse.getLocation();

			if (location == null) {
				throw new RuntimeException(
					"Invalid authorization response: " +
						authorizeResponse.getStatus());
			}

			Map<String, String[]> parameterMap =
				HttpUtil.getParameterMap(location.getQuery());

			if (parameterMap.containsKey("error")) {
				return parameterMap.get("error")[0];
			}

			MultivaluedHashMap<String, String> formData =
				new MultivaluedHashMap<>();

			formData.add("oauthDecision", "allow");
			for (String key : parameterMap.keySet()) {
				if (!StringUtil.startsWith(key, "oauth2_")) {
					continue;
				}

				formData.add(
					key.substring("oauth2_".length()),
					parameterMap.get(key)[0]);
			}

			Invocation.Builder authorizeDecisionInvocationBuilder =
				getInvocationBuilder(hostname, getAuthorizeDecisionWebTarget());

			Response authorizeDecisionResponse =
				authorizeDecisionInvocationBuilder.cookie(authenticatedCookie).
					post(Entity.form(formData));

			location = authorizeDecisionResponse.getLocation();

			if (location == null) {
				throw new RuntimeException(
					"Invalid authorization decision response: " +
						authorizeResponse.getStatus());
			}

			parameterMap = HttpUtil.getParameterMap(location.getQuery());

			if (parameterMap.containsKey("error")) {
				return parameterMap.get("error")[0];
			}

			return parameterMap.get("code")[0];
		}
		catch (URISyntaxException e) {
			throw new RuntimeException(e);
		}
	}

	public static void printDocument(Document doc, OutputStream out) throws
		IOException, TransformerException {
		TransformerFactory tf = TransformerFactory.newInstance();
		Transformer transformer = tf.newTransformer();
		transformer.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "no");
		transformer.setOutputProperty(OutputKeys.METHOD, "xml");
		transformer.setOutputProperty(OutputKeys.INDENT, "yes");
		transformer.setOutputProperty(OutputKeys.ENCODING, "UTF-8");
		transformer.setOutputProperty(
			"{http://xml.apache.org/xslt}indent-amount", "4");

		transformer.transform(new DOMSource(doc),
			new StreamResult(new OutputStreamWriter(out, "UTF-8")));
	}

	protected WebTarget getWebTarget(String... paths)
		throws URISyntaxException {

		Client client = getClient();

		WebTarget target = client.target(getUrl().toURI());

		target = target.path("o").path("oauth2-test");

		for (String path : paths) {
			target = target.path(path);
		}

		return target;
	}

	protected WebTarget getJsonWebTarget(String... paths)
		throws URISyntaxException {

		Client client = getClient();

		WebTarget target = client.target(getUrl().toURI());

		target = target.path("api").path("jsonws");

		for (String path : paths) {
			target = target.path(path);
		}

		return target;
	}

	@ArquillianResource
	protected URL _url;

}