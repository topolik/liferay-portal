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

package com.liferay.oauth2.provider.rest.internal.endpoint.access.token.grant.handler;

import com.liferay.oauth2.provider.configuration.OAuth2ProviderConfiguration;
import com.liferay.oauth2.provider.model.OAuth2Application;
import com.liferay.oauth2.provider.rest.internal.endpoint.liferay.LiferayOAuthDataProvider;
import com.liferay.oauth2.provider.scope.liferay.LiferayOAuth2Scope;
import com.liferay.oauth2.provider.scope.liferay.ScopeLocator;
import com.liferay.portal.configuration.metatype.bnd.util.ConfigurableUtil;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import javax.ws.rs.core.MultivaluedMap;

import com.liferay.portal.kernel.security.auth.CompanyThreadLocal;
import org.apache.cxf.rs.security.oauth2.common.Client;
import org.apache.cxf.rs.security.oauth2.common.ServerAccessToken;
import org.apache.cxf.rs.security.oauth2.common.UserSubject;
import org.apache.cxf.rs.security.oauth2.grants.clientcred.ClientCredentialsGrantHandler;
import org.apache.cxf.rs.security.oauth2.provider.AccessTokenGrantHandler;

import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Tomas Polesovsky
 */
@Component(
	configurationPid = "com.liferay.oauth2.provider.configuration.OAuth2ProviderConfiguration",
	service = AccessTokenGrantHandler.class
)
public class LiferayClientCredentialsAccessTokenGrantHandler
	extends BaseAccessTokenGrantHandler {

	@Activate
	protected void activate(Map<String, Object> properties) {
		_clientCredentialsGrantHandler = new ClientCredentialsGrantHandler() {

			@Override
			protected ServerAccessToken getPreAuthorizedToken(
				Client client, UserSubject subject, String requestedGrant,
				List<String> requestedScopes, List<String> audiences) {

				Client expandedClient = _liferayOAuthDataProvider.getClient(
					client.getClientId());

				expandedClient.setRegisteredScopes(
					client.getRegisteredScopes().stream().flatMap(
						s -> _scopeLocator.getLiferayOAuth2Scopes(
					CompanyThreadLocal.getCompanyId(), s).stream().map(
					LiferayOAuth2Scope::getScope)).collect(Collectors.toList()));

				return super.getPreAuthorizedToken(
					expandedClient, subject, requestedGrant, requestedScopes,
					audiences);
			}

		};

		_clientCredentialsGrantHandler.setDataProvider(
			_liferayOAuthDataProvider);

		_oAuth2ProviderConfiguration = ConfigurableUtil.createConfigurable(
			OAuth2ProviderConfiguration.class, properties);
	}

	@Override
	protected AccessTokenGrantHandler getAccessTokenGrantHandler() {
		return _clientCredentialsGrantHandler;
	}

	@Override
	protected boolean hasPermission(
		Client client, MultivaluedMap<String, String> params) {

		OAuth2Application oAuth2Application =
			_liferayOAuthDataProvider.resolveOAuth2Application(client);

		return hasCreateTokenPermission(
			oAuth2Application.getUserId(), oAuth2Application);
	}

	@Override
	protected boolean isGrantHandlerEnabled() {
		return _oAuth2ProviderConfiguration.allowClientCredentialsGrant();
	}

	private ClientCredentialsGrantHandler _clientCredentialsGrantHandler;

	@Reference
	private LiferayOAuthDataProvider _liferayOAuthDataProvider;

	@Reference
	private ScopeLocator _scopeLocator;

	private OAuth2ProviderConfiguration _oAuth2ProviderConfiguration;

}