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

import com.liferay.portal.kernel.util.Random;

import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

/**
 * @author Sampsa Sohlman
 */
class RandomHelperUtil {

	static int[] nextInt(Random random, int n, int size) {
		if (size > n) {
			size = n;
		}

		Set<Integer> set = new LinkedHashSet<Integer>();

		for (int i = 0; i < size; i++) {
			while (true) {
				Integer value = new Integer(random.nextInt(n));

				if (!set.contains(value)) {
					set.add(value);

					break;
				}
			}
		}

		int[] array = new int[set.size()];

		Iterator<Integer> itr = set.iterator();

		for (int i = 0; i < array.length; i++) {
			array[i] = itr.next().intValue();
		}

		return array;
	}

	static void randomize(Random random, char[] array) {
		int length = array.length;

		for (int i = 0; i < length - 1; i++) {
			int x = random.nextInt(length);
			char y = array[i];

			array[i] = array[i + x];
			array[i + x] = y;

			length--;
		}
	}

	static void randomize(Random random, int[] array) {
		int length = array.length;

		for (int i = 0; i < length - 1; i++) {
			int x = random.nextInt(length);
			int y = array[i];

			array[i] = array[i + x];
			array[i + x] = y;

			length--;
		}
	}

	static void randomize(Random random, List<Object> list) {
		int size = list.size();

		for (int i = 0; i <= size; i++) {
			Object obj = list.get(i);

			int j = random.nextInt(size);

			list.set(i, list.get(i + j));
			list.set(i + j, obj);

			size--;
		}
	}

	static void randomize(Random random, Object[] array) {
		int length = array.length;

		for (int i = 0; i < length - 1; i++) {
			int x = random.nextInt(length);
			Object y = array[i];

			array[i] = array[i + x];
			array[i + x] = y;

			length--;
		}
	}

	static String randomize(Random random, String s) {
		if (s == null) {
			return null;
		}

		char[] array = s.toCharArray();

		randomize(random, array);

		return new String(array);
	}

}