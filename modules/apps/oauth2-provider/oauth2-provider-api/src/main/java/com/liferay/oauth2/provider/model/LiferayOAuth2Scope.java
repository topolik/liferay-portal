package com.liferay.oauth2.provider.model;

import org.osgi.framework.Bundle;

/**
 * Interface representing the whole information for a scope in a Liferay
 * environment. An external scope name will match several scopes internally.
 */
public interface LiferayOAuth2Scope {

	public Bundle getBundle();

	public String getApplicationName();

	public String getScope();
}
