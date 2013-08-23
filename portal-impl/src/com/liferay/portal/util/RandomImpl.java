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

package com.liferay.portal.util;

import com.liferay.portal.kernel.security.pacl.DoPrivileged;
import com.liferay.portal.kernel.util.Random;

import java.util.List;

/**
 *
 * @author Sampsa Sohlman
 *
 */
@DoPrivileged
public class RandomImpl extends java.util.Random implements Random {

	public int[] nextInt(int n, int size) {
		return RandomHelperUtil.nextInt(this, n, size);
	}

	public void randomize(char[] array) {
		RandomHelperUtil.randomize(this, array);
	}

	public void randomize(int[] array) {
		RandomHelperUtil.randomize(this, array);
	}

	public void randomize(List<Object> list) {
		RandomHelperUtil.randomize(this, list);
	}

	public void randomize(Object[] array) {
		RandomHelperUtil.randomize(this, array);
	}

	public String randomize(String s) {
		return RandomHelperUtil.randomize(this, s);
	}

}