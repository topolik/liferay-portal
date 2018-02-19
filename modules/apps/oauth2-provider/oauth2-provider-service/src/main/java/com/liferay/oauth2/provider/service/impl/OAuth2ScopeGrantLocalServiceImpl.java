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

package com.liferay.oauth2.provider.service.impl;

import com.liferay.oauth2.provider.exception.NoSuchOAuth2TokenException;
import com.liferay.oauth2.provider.model.LiferayOAuth2Scope;
import com.liferay.oauth2.provider.model.OAuth2ScopeGrant;
import com.liferay.oauth2.provider.model.OAuth2Token;
import com.liferay.oauth2.provider.service.base.OAuth2ScopeGrantLocalServiceBaseImpl;
import com.liferay.oauth2.provider.service.persistence.OAuth2ScopeGrantPK;
import org.osgi.framework.Bundle;
import org.osgi.framework.Version;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Optional;

/**
 * The implementation of the o auth2 scope grant local service.
 *
 * <p>
 * All custom service methods should be put in this class. Whenever methods are added, rerun ServiceBuilder to copy their definitions into the {@link com.liferay.oauth2.provider.service.OAuth2ScopeGrantLocalService} interface.
 *
 * <p>
 * This is a local service. Methods of this service will not have security checks based on the propagated JAAS credentials because this service can only be accessed from within the same VM.
 * </p>
 *
 * @author Brian Wing Shun Chan
 * @see OAuth2ScopeGrantLocalServiceBaseImpl
 * @see com.liferay.oauth2.provider.service.OAuth2ScopeGrantLocalServiceUtil
 */
public class OAuth2ScopeGrantLocalServiceImpl
	extends OAuth2ScopeGrantLocalServiceBaseImpl {
	/*
	 * NOTE FOR DEVELOPERS:
	 *
	 * Never reference this class directly. Always use {@link com.liferay.oauth2.provider.service.OAuth2ScopeGrantLocalServiceUtil} to access the o auth2 scope grant local service.
	 */

	public Collection<OAuth2ScopeGrant> grantScopesToToken(
		String tokenString, Collection<LiferayOAuth2Scope> scopes)
		throws NoSuchOAuth2TokenException {

		if (scopes.isEmpty()) {
			return Collections.emptyList();
		}

		OAuth2Token oAuth2Token = oAuth2TokenLocalService.fetchOAuth2Token(
			tokenString);

		if (oAuth2Token == null) {
			throw new NoSuchOAuth2TokenException(tokenString);
		}

		Collection<OAuth2ScopeGrant> oAuth2ScopeGrants = new ArrayList<>(
			scopes.size());

		for (LiferayOAuth2Scope scope : scopes) {
			Bundle bundle = scope.getBundle();
			Version version = bundle.getVersion();

			OAuth2ScopeGrant oAuth2ScopeGrant = createOAuth2ScopeGrant(
				new OAuth2ScopeGrantPK(
					scope.getApplicationName(), bundle.getSymbolicName(),
					version.toString(), oAuth2Token.getCompanyId(),
					scope.getScope(), tokenString));

			oAuth2ScopeGrants.add(updateOAuth2ScopeGrant(oAuth2ScopeGrant));
		}

		return oAuth2ScopeGrants;
	}

	public Collection<OAuth2ScopeGrant> findByTokenId(String tokenId) {
		return oAuth2ScopeGrantPersistence.findByToken(tokenId);
	}

	public Collection<OAuth2ScopeGrant> findByA_BNS_BV_C_T(
		String applicationName, String bundleSymbolicName, String bundleVersion,
		long companyId, String tokenId) {

		return oAuth2ScopeGrantPersistence.findByA_BSN_BV_C_T(
			applicationName, bundleSymbolicName, bundleVersion, companyId,
			tokenId);
	}

	public Optional<OAuth2ScopeGrant> findByA_BNS_BV_C_O_T(
		String applicationName, String bundleSymbolicName, String bundleVersion,
		long companyId, String scope, String tokenId) {

		return Optional.ofNullable(
			oAuth2ScopeGrantPersistence.fetchByA_BSN_BV_C_O_T(
				applicationName, bundleSymbolicName, bundleVersion, companyId,
				scope, tokenId));
	}

}