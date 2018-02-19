package com.liferay.oauth2.provider.scopes.api;

import javax.ws.rs.core.Feature;

public interface LiferayOauth2OSGiFeatureFactory {
	Feature create(String applicationName);
}
