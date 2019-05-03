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

package com.liferay.oauth2.provider.scope;

import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * Declares that a method on a JAX-RS resource may only be executed if the
 * incoming request is authorized for the scopes given in the value of the
 * annotation. When used on JAX-RS resource class, all methods without the
 * annotation inherit the resource class annotation.
 *
 * @author Carlos Sierra Andrés
 * @see    RequiresNoScope
 */
@Inherited
@Retention(RetentionPolicy.RUNTIME)
public @interface RequiresScope {

	/**
	 * Returns <code>true</code> if the returned scopes in {@link #value()} must
	 * all be authorized. This defaults to <code>true</code>.
	 *
	 * @return <code>true</code> if all specified scopes must be authorized;
	 *         <code>false</code> otherwise
	 */
	boolean allNeeded() default true;

	/**
	 * Returns the list of scopes requiring authorization to execute this
	 * method.
	 *
	 * @return the scopes
	 */
	String[] value();

}