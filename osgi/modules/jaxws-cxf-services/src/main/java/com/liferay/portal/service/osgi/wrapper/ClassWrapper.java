/**
 * Copyright (c) 2000-2014 Liferay, Inc. All rights reserved.
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

import java.io.Serializable;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

/**
 * @author Tomas Polesovsky
 */
public abstract class ClassWrapper implements Serializable {

	public static String generateMethodKey(Method method) {
		StringBuilder key = new StringBuilder();

		key.append(method.getName());

		key.append("(");

		for (Class parameterType : method.getParameterTypes()) {
			key.append(parameterType.getName());
			key.append(",");
		}

		key.append("):");

		key.append(method.getReturnType().getName());

		return key.toString();
	}

	public void wrapClass(Object originalClass) {
		this._wrappedClass = originalClass;

		initMethods();
	}

	public Object invoke(String methodName, Object[] unwrappedArgs) throws Exception {
		Method originalMethod = _methodsIndex.get(methodName);

		if (originalMethod == null) {
			throw new Exception("Unable to find original method!");
		}

		Object result = originalMethod.invoke(_wrappedClass, unwrappedArgs);

		return wrap(result);

	}

	protected void initMethods() {
		Map methodsIndex = new HashMap();

		Method[] methods = _wrappedClass.getClass().getMethods();
		for (Method method : methods) {
			methodsIndex.put(generateMethodKey(method), method);
		}

		_methodsIndex = methodsIndex;
	}

	private Object unwrap(boolean arg) {
		return Boolean.valueOf(arg);
	}

	private Object unwrap(byte arg) {
		return Byte.valueOf(arg);
	}

	private Object unwrap(char arg) {
		return Character.valueOf(arg);
	}

	private Object unwrap(double arg) {
		return Double.valueOf(arg);
	}

	private Object unwrap(float arg) {
		return Float.valueOf(arg);
	}

	private Object unwrap(int arg) {
		return Integer.valueOf(arg);
	}

	private Object unwrap(long arg) {
		return Long.valueOf(arg);
	}

	private Object unwrap(short arg) {
		return Short.valueOf(arg);
	}

	private Object unwrap(Object arg) {
		return Byte.valueOf(arg);
	}

	private Object wrap(double result) {
		throw new UnsupportedOperationException("");
	}

	private Map<String, Method> _methodsIndex;
	private Object _wrappedClass;

}
