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

package com.liferay.oauth2.provider.jsonws.internal.security.auth.verifier;

import com.liferay.oauth2.provider.jsonws.internal.constants.OAuth2JSONWSConstants;
import com.liferay.oauth2.provider.jsonws.internal.service.access.policy.scope.SAPEntryScope;
import com.liferay.oauth2.provider.jsonws.internal.service.access.policy.scope.SAPEntryScopeDescriptorFinderRegistrator;
import com.liferay.oauth2.provider.model.OAuth2Application;
import com.liferay.oauth2.provider.rest.spi.bearer.token.provider.BearerTokenProvider;
import com.liferay.oauth2.provider.scope.liferay.LiferayOAuth2Scope;
import com.liferay.oauth2.provider.scope.liferay.ScopeLocator;
import com.liferay.portal.kernel.security.auth.verifier.AuthVerifier;
import com.liferay.portal.kernel.security.auth.verifier.AuthVerifierResult;
import com.liferay.portal.kernel.security.service.access.policy.ServiceAccessPolicyThreadLocal;

import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Tomas Polesovsky
 */
@Component(
	property = "auth.verifier.JSONWSOAuth2AuthVerifier.urls.includes=/api/jsonws/*",
	service = AuthVerifier.class
)
public class JSONWSOAuth2AuthVerifier extends BaseOAuth2AuthVerifier {

	@Override
	protected AuthVerifierResult verify(
		BearerTokenProvider.AccessToken accessToken) {

		OAuth2Application oAuth2Application =
			accessToken.getOAuth2Application();

		long companyId = oAuth2Application.getCompanyId();

		Set<String> scopes = new HashSet<>();

		for (String scope : accessToken.getScopes()) {
			Collection<LiferayOAuth2Scope> liferayOAuth2Scopes =
				_scopeLocator.getLiferayOAuth2Scopes(
					companyId, scope, OAuth2JSONWSConstants.APPLICATION_NAME_JSONWS);

			for (LiferayOAuth2Scope liferayOAuth2Scope : liferayOAuth2Scopes) {
				scopes.add(liferayOAuth2Scope.getScope());
			}
		}

		List<SAPEntryScope> sapEntryScopes =
			_sapEntryScopeDescriptorFinderRegistrator.
				getRegisteredSAPEntryScopes(companyId);

		for (SAPEntryScope sapEntryScope : sapEntryScopes) {
			if (scopes.contains(sapEntryScope.getScope())) {
				ServiceAccessPolicyThreadLocal.addActiveServiceAccessPolicyName(
					sapEntryScope.getSapEntryName());
			}
		}

		AuthVerifierResult authVerifierResult = new AuthVerifierResult();

		Map<String, Object> settings = authVerifierResult.getSettings();

		settings.put(
			BearerTokenProvider.AccessToken.class.getName(), accessToken);

		authVerifierResult.setState(AuthVerifierResult.State.SUCCESS);
		authVerifierResult.setUserId(accessToken.getUserId());

		return authVerifierResult;
	}

	@Reference
	private SAPEntryScopeDescriptorFinderRegistrator
		_sapEntryScopeDescriptorFinderRegistrator;

	@Reference
	private ScopeLocator _scopeLocator;

}