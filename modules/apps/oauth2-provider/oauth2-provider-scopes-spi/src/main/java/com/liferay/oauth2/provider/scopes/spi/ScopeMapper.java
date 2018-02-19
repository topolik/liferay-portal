package com.liferay.oauth2.provider.scopes.spi;

public interface ScopeMapper {

	public String map(String scope);

	public static ScopeMapper NULL = scope -> scope;

}
