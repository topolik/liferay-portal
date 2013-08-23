/**
 * Copyright (c) 2000-2013 Liferay, Inc. All rights reserved.
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

package com.liferay.portal.kernel.util;

import com.liferay.portal.kernel.security.pacl.permission.PortalRuntimePermission;

/**
 * @author Sampsa Sohlman
 */
public class RandomUtil {

	/**
	 * Returns instance of Random which implements generates pseudo random
	 * numbers
	 *
	 * @return Random
	 */
	public static Random getRandom() {
		return _random;
	}

	public void setRandom(Random random) {
		PortalRuntimePermission.checkSetBeanProperty(getClass());

		_random = random;
	}

	private static Random _random;

}