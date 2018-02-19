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

package com.liferay.oauth2.provider.scopes.impl;

import com.liferay.oauth2.provider.model.OAuth2ScopeGrant;
import com.liferay.oauth2.provider.scopes.api.ScopeChecker;
import com.liferay.oauth2.provider.scopes.liferay.api.ScopeContext;
import com.liferay.oauth2.provider.service.OAuth2ScopeGrantLocalService;
import com.liferay.portal.kernel.model.Company;
import com.liferay.portal.kernel.util.StringPool;
import com.liferay.portal.kernel.util.Validator;
import org.osgi.framework.Bundle;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Optional;

@Component(service = {ScopeChecker.class, ScopeContext.class})
public class ThreadLocalServiceScopeChecker
	implements ScopeChecker, ScopeContext {

	ThreadLocal<Long> _companyIdThreadLocal = ThreadLocal.withInitial(() -> 0L);
	ThreadLocal<String> _bundleSymbolicName = ThreadLocal.withInitial(
		() -> StringPool.BLANK);
	ThreadLocal<String> _bundleVersion = ThreadLocal.withInitial(
		() -> StringPool.BLANK);
	ThreadLocal<String> _applicationName = ThreadLocal.withInitial(
		() -> StringPool.BLANK);
	ThreadLocal<String> _tokenString = ThreadLocal.withInitial(
		() -> StringPool.BLANK);

	@Override
	public boolean hasScope(String scope) {
		if (Validator.isNull(scope)) {
			throw new IllegalArgumentException("Scope can't be null");
		}

		Optional<OAuth2ScopeGrant> optionalScope =
			_oAuth2ScopeGrantLocalService.findByA_BNS_BV_C_O_T(
				_applicationName.get(), _bundleSymbolicName.get(),
				_bundleVersion.get(), _companyIdThreadLocal.get(),
				scope, _tokenString.get());

		return optionalScope.isPresent();
	}

	@Override
	public boolean hasAllScopes(String... scopes) {
		if (Validator.isNull(scopes)) {
			throw new IllegalArgumentException("Scopes can't be null");
		}
		Collection<OAuth2ScopeGrant> oAuth2ScopeGrants =
			new ArrayList<>(
				_oAuth2ScopeGrantLocalService.findByA_BNS_BV_C_T(
					_applicationName.get(), _bundleSymbolicName.get(),
					_bundleVersion.get(), _companyIdThreadLocal.get(),
					_tokenString.get()));

		if (scopes.length > oAuth2ScopeGrants.size()) {
			return false;
		}

		for (String scope : scopes) {
			if (Validator.isNull(scope)) {
				throw new IllegalArgumentException("Scope can't be null");
			}
			boolean found = oAuth2ScopeGrants.removeIf(
				o -> scope.equals(o.getOAuth2ScopeName()));

			if (!found) {
				return false;
			}
		}

		return true;
	}

	@Override
	public boolean hasAnyScope(String... scopes) {
		if (Validator.isNull(scopes)) {
			throw new IllegalArgumentException("Scopes can't be null");
		}
		Collection<OAuth2ScopeGrant> oAuth2ScopeGrants =
			_oAuth2ScopeGrantLocalService.findByA_BNS_BV_C_T(
				_applicationName.get(), _bundleSymbolicName.get(),
				_bundleVersion.get(), _companyIdThreadLocal.get(),
				_tokenString.get());

		for (String scope : scopes) {
			if (Validator.isNull(scope)) {
				throw new IllegalArgumentException("Scope can't be null");
			}
			for (OAuth2ScopeGrant oAuth2ScopeGrant : oAuth2ScopeGrants) {
				if (scope.equals(oAuth2ScopeGrant.getOAuth2ScopeName())) {
					return true;
				}
			}
		}

		return false;
	}

	@Override
	public void setCompany(Company company) {
		_companyIdThreadLocal.set(company.getCompanyId());
	}

	@Override
	public void setBundle(Bundle bundle) {
		_bundleSymbolicName.set(bundle.getSymbolicName());
		_bundleVersion.set(bundle.getVersion().toString());
	}

	@Override
	public void setApplicationName(String applicationName) {
		_applicationName.set(applicationName);
	}

	@Override
	public String getTokenString() {
		return _tokenString.get();
	}

	@Override
	public void setTokenString(String tokenString) {
		_tokenString.set(tokenString);
	}

	@Override
	public void clear() {
		_applicationName.remove();
		_bundleSymbolicName.remove();
		_bundleVersion.remove();
		_companyIdThreadLocal.remove();
	}

	@Reference
	OAuth2ScopeGrantLocalService _oAuth2ScopeGrantLocalService;

}