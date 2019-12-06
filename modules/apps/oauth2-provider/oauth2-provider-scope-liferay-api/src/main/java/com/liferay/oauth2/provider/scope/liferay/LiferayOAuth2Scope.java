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

package com.liferay.oauth2.provider.scope.liferay;

import org.osgi.framework.Bundle;

/**
 * Represents an application exported scope for Liferay's OAuth2 Provider
 * framework.
 *
 * @author Carlos Sierra Andrés
 */
public interface LiferayOAuth2Scope {

	/**
	 * Returns the name of the application that provides the scope. This usually
	 * refers to the JAX-RS application name.
	 *
	 * @return the non-<code>null</code> application name
	 */
	public String getApplicationName();

	/**
	 * Returns the OSGi bundle context where the application and scope are
	 * published.
	 *
	 * @return the non-<code>null</code> OSGi bundle
	 */
	public Bundle getBundle();

	/**
	 * Returns the scope name as registered in the OAuth2 Provider framework.
	 *
	 * @return the non-<code>null</code> scope name
	 */
	public String getScope();

}