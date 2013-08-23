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

import java.util.List;

/**
 * @author Sampsa Sohlman
 */
public interface Random {
	public boolean nextBoolean();

	public void nextBytes(byte[] bytes);

	public double nextDouble();

	public double nextGaussian();

	public int nextInt();

	public int nextInt(int n);

	public int[] nextInt(int n, int size);

	public long nextLong();

	public void randomize(char[] array);

	public void randomize(int[] array);

	public void randomize(List<Object> list);

	public void randomize(Object[] array);

	public String randomize(String s);

}