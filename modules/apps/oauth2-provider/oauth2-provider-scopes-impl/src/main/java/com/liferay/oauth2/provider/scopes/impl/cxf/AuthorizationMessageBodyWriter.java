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

package com.liferay.oauth2.provider.scopes.impl.cxf;

import com.liferay.portal.kernel.util.PortalUtil;
import com.liferay.portal.kernel.util.StringBundler;
import com.liferay.portal.kernel.util.StringPool;
import org.apache.cxf.jaxrs.ext.MessageContext;
import org.apache.cxf.rs.security.oauth2.common.OAuthAuthorizationData;
import org.osgi.service.component.annotations.Component;

import javax.ws.rs.Produces;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.MessageBodyWriter;
import javax.ws.rs.ext.Provider;
import java.io.IOException;
import java.io.OutputStream;
import java.lang.annotation.Annotation;
import java.lang.reflect.Type;

/**
 * @author Carlos Sierra Andrés
 */
@Component(service = AuthorizationMessageBodyWriter.class)
@Produces("text/html")
@Provider
public class AuthorizationMessageBodyWriter
	implements MessageBodyWriter<OAuthAuthorizationData> {


	@Override
	public boolean isWriteable(
		Class<?> aClass, Type type, Annotation[] annotations,
		MediaType mediaType) {

		return aClass.isAssignableFrom(OAuthAuthorizationData.class) &&
			   "text".equalsIgnoreCase(mediaType.getType()) &&
			   "html".equalsIgnoreCase(mediaType.getSubtype());
	}

	@Override
	public long getSize(
		OAuthAuthorizationData oAuthAuthorizationData, Class<?> aClass,
		Type type, Annotation[] annotations, MediaType mediaType) {

		return -1L;
	}

	@Override
	public void writeTo(
		OAuthAuthorizationData oAuthAuthorizationData, Class<?> aClass,
		Type type, Annotation[] annotations, MediaType mediaType,
		MultivaluedMap<String, Object> httpHeaders,
		OutputStream outputStream)
		throws IOException, WebApplicationException {

		String portalURL =
			PortalUtil.getPortalURL(_messageContext.getHttpServletRequest());

		StringBundler sb = new StringBundler(portalURL);

		sb.append("/group/guest/oauth2-authorize");

		sb.append(StringPool.QUESTION);
		sb.append("reply_to");
		sb.append(StringPool.EQUAL);
		sb.append(oAuthAuthorizationData.getReplyTo());
		sb.append(StringPool.AMPERSAND);
		sb.append("client_id");
		sb.append(StringPool.EQUAL);
		sb.append(oAuthAuthorizationData.getClientId());
		sb.append(StringPool.AMPERSAND);
		sb.append("redirect_uri");
		sb.append(StringPool.EQUAL);
		sb.append(oAuthAuthorizationData.getRedirectUri());
		sb.append(StringPool.AMPERSAND);
		sb.append("session_authenticity_token");
		sb.append(StringPool.EQUAL);
		sb.append(oAuthAuthorizationData.getAuthenticityToken());
		sb.append(StringPool.AMPERSAND);
		sb.append("scope");
		sb.append(StringPool.EQUAL);
		sb.append(oAuthAuthorizationData.getProposedScope());

		throw new WebApplicationException(
			Response.status(303).header("Location", sb.toString()).build());

	}

	@Context
	private MessageContext _messageContext;

}
