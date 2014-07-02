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

package com.liferay.portal.service.osgi.wrapper;

/**
 * @author Tomas Polesovsky
 */
public class PrimitiveWrapper {

	public static Class convertClass(Class primitive) {
		if(primitive.equals(Boolean.TYPE)) return (Boolean.class);
		if(primitive.equals(Byte.TYPE)) return (Byte.class);
		if(primitive.equals(Character.TYPE)) return (Character.class);
		if(primitive.equals(Short.TYPE)) return (Short.class);
		if(primitive.equals(Integer.TYPE)) return (Integer.class);
		if(primitive.equals(Long.TYPE)) return (Long.class);
		if(primitive.equals(Float.TYPE)) return (Float.class);
		if(primitive.equals(Double.TYPE)) return (Double.class);

		throw new IllegalArgumentException("Not a primitive: " + primitive);
	}

	public static Boolean convert(boolean primitive) {
		return Boolean.valueOf(primitive);
	}

	public static Byte convert(byte primitive) {
		return Byte.valueOf(primitive);
	}

	public static Character convert(char primitive) {
		return Character.valueOf(primitive);
	}

	public static Short convert(short primitive) {
		return Short.valueOf(primitive);
	}

	public static Integer convert(int primitive) {
		return Integer.valueOf(primitive);
	}

	public static Long convert(long primitive) {
		return Long.valueOf(primitive);
	}

	public static Float convert(float primitive) {
		return Float.valueOf(primitive);
	}

	public static Double convert(double primitive) {
		return Double.valueOf(primitive);
	}


}
