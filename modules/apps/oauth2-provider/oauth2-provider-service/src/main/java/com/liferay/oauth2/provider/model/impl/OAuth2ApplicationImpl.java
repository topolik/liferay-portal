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

package com.liferay.oauth2.provider.model.impl;

import aQute.bnd.annotation.ProviderType;
import com.liferay.oauth2.provider.constants.GrantType;
import com.liferay.portal.kernel.util.StringPool;
import com.liferay.portal.kernel.util.StringUtil;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * The extended model implementation for the OAuth2Application service. Represents a row in the &quot;OAuth2Application&quot; database table, with each column mapped to a property of this class.
 *
 * <p>
 * Helper methods and all application logic should be put in this class. Whenever methods are added, rerun ServiceBuilder to copy their definitions into the {@link com.liferay.oauth2.provider.model.OAuth2Application} interface.
 * </p>
 *
 * @author Brian Wing Shun Chan
 */
@ProviderType
public class OAuth2ApplicationImpl extends OAuth2ApplicationBaseImpl {
	/*
	 * NOTE FOR DEVELOPERS:
	 *
	 * Never reference this class directly. All methods that expect a o auth2 application model instance should use the {@link com.liferay.oauth2.provider.model.OAuth2Application} interface instead.
	 */
	public OAuth2ApplicationImpl() {
	}

	@Override
	public List<GrantType> getAllowedGrantTypesList(){
		Stream<String> stream =
			Arrays.stream(StringUtil.split(getAllowedGrantTypes()));

		List<GrantType> oAuth2ProviderGrantTypes = stream.map(
			GrantType::valueOf
		).collect(
			Collectors.toList()
		);

		return oAuth2ProviderGrantTypes;
	}

	@Override
	public List<String> getFeaturesList() {
		return Arrays.asList(StringUtil.split(getFeatures()));
	}

	@Override
	public List<String> getRedirectURIsList(){
		return Arrays.asList(
			StringUtil.split(getRedirectURIs(), StringPool.NEW_LINE));
	}

	@Override
	public List<String> getScopesList() {
		return Arrays.asList(StringUtil.split(getScopes(), StringPool.SPACE));
	}

	@Override
	public void setAllowedGrantTypesList(
		List<GrantType> allowedGrantTypesList) {

		Stream<GrantType> stream = allowedGrantTypesList.stream();

		String allowedGrantTypes = stream.map(
			GrantType::toString
		).collect(
			Collectors.joining(StringPool.COMMA)
		);

		setAllowedGrantTypes(allowedGrantTypes);
	}

	@Override
	public void setFeaturesList(List<String> featuresList) {
		String features = StringUtil.merge(featuresList);

		setFeatures(features);
	}

	@Override
	public void setRedirectURIsList(List<String> redirectURIsList) {
		String redirectURIs = StringUtil.merge(
			redirectURIsList, StringPool.NEW_LINE);

		setRedirectURIs(redirectURIs);
	}

	@Override
	public void setScopesList(List<String> scopesList) {
		String scopes = StringUtil.merge(scopesList, StringPool.SPACE);

		setScopes(scopes);
	}
}