package com.liferay.oauth2.provider.scopes.api;

import aQute.bnd.annotation.ProviderType;

@ProviderType
public interface ScopeChecker {

	public boolean hasScope(String scope);

	public default boolean hasAllScopes(String ... scopes) {
		for (String scope : scopes) {
			if (!hasScope(scope)) {
				return false;
			}
		}

		return true;
	}

	public default boolean hasAnyScope(String ... scopes) {
		for (String scope : scopes) {
			if (hasScope(scope)) {
				return true;
			}
		}

		return false;
	}

}