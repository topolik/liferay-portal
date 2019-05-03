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
 * Declares that a method on a JAX-RS resource does not need a scope to be
 * authorized. When used on a JAX-RS resource class, all methods without the
 * annotation inherit the resource class annotation.
 *
 * <p>
 * If scope annotation checking is enabled and a method has no {@link
 * RequiresScope} or {@code RequiresNoScope} annotation, the request isn't
 * authorized to execute that method as security precaution.
 * </p>
 *
 * @author Tomas Polesovsky
 */
@Inherited
@Retention(RetentionPolicy.RUNTIME)
public @interface RequiresNoScope {
}