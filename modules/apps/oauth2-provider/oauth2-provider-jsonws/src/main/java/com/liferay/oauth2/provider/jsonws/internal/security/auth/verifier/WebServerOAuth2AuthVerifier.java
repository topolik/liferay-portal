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

import com.liferay.oauth2.provider.jsonws.internal.configuration.OAuth2JSONWSConfiguration;
import com.liferay.oauth2.provider.jsonws.internal.constants.OAuth2JSONWSConstants;
import com.liferay.oauth2.provider.model.OAuth2Application;
import com.liferay.oauth2.provider.rest.spi.bearer.token.provider.BearerTokenProvider;
import com.liferay.oauth2.provider.scope.ScopeChecker;
import com.liferay.oauth2.provider.scope.liferay.ScopeContext;
import com.liferay.portal.configuration.metatype.bnd.util.ConfigurableUtil;
import com.liferay.portal.kernel.security.auth.verifier.AuthVerifier;
import com.liferay.portal.kernel.security.auth.verifier.AuthVerifierResult;
import com.liferay.portal.kernel.security.service.access.policy.ServiceAccessPolicyThreadLocal;

import java.util.Map;

import org.osgi.framework.BundleContext;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Tomas Polesovsky
 */
@Component(
	configurationPid = "com.liferay.oauth2.provider.jsonws.internal.configuration.OAuth2JSONWSConfiguration",
	property = "auth.verifier.WebServerOAuth2AuthVerifier.urls.includes=N/A",
	service = AuthVerifier.class
)
public class WebServerOAuth2AuthVerifier extends BaseOAuth2AuthVerifier {

	@Activate
	protected void activate(
		BundleContext bundleContext, Map<String, Object> properties) {

		_bundleContext = bundleContext;

		OAuth2JSONWSConfiguration oAuth2JSONWSConfiguration =
			ConfigurableUtil.createConfigurable(
				OAuth2JSONWSConfiguration.class, properties);

		_removeSAPEntryOAuth2Prefix =
			oAuth2JSONWSConfiguration.removeSAPEntryOAuth2Prefix();

		_sapEntryOAuth2Prefix =
			oAuth2JSONWSConfiguration.sapEntryOAuth2Prefix();
	}

	@Override
	protected AuthVerifierResult verify(
		BearerTokenProvider.AccessToken accessToken) {

		_scopeContext.setAccessToken(accessToken.getTokenKey());
		_scopeContext.setApplicationName(
			OAuth2JSONWSConstants.APPLICATION_NAME);
		_scopeContext.setBundle(_bundleContext.getBundle());

		OAuth2Application oAuth2Application =
			accessToken.getOAuth2Application();

		long companyId = oAuth2Application.getCompanyId();

		_scopeContext.setCompanyId(companyId);

		AuthVerifierResult authVerifierResult = new AuthVerifierResult();

		if (_scopeChecker.checkScope(OAuth2JSONWSConstants.SCOPE_DOCUMENTS)) {
			ServiceAccessPolicyThreadLocal.addActiveServiceAccessPolicyName(
				_getSAPEntryName());

			authVerifierResult.setState(AuthVerifierResult.State.SUCCESS);
			authVerifierResult.setUserId(accessToken.getUserId());
		}

		return authVerifierResult;
	}

	private String _getSAPEntryName() {
		String sapEntryName = OAuth2JSONWSConstants.SCOPE_DOCUMENTS;

		if (_removeSAPEntryOAuth2Prefix) {
			return sapEntryName;
		}

		return _sapEntryOAuth2Prefix.concat(sapEntryName);
	}

	private BundleContext _bundleContext;
	private boolean _removeSAPEntryOAuth2Prefix;
	private String _sapEntryOAuth2Prefix;

	@Reference
	private ScopeChecker _scopeChecker;

	@Reference
	private ScopeContext _scopeContext;

}