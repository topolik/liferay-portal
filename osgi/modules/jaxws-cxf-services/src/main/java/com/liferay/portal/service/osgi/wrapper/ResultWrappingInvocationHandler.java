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

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

/**
 * @author Tomas Polesovsky
 */
public class ResultWrappingInvocationHandler implements InvocationHandler {

	public ResultWrappingInvocationHandler(
		Object originalClass, JaxWsServiceGenerator jaxWsServiceGenerator) {

		this._originalClass = originalClass;
		this._generator = jaxWsServiceGenerator;

		Method[] methods = originalClass.getClass().getMethods();
		for (Method method : methods) {
			_methodsIndex.put(generateKey(method), method);
		}
	}

	@Override
	public Object invoke(Object proxy, Method method, Object[] args)
		throws Throwable {

		String key = generateKey(method);

		Method originalMethod = _methodsIndex.get(key);

		if (originalMethod == null) {
			throw new Exception("Unable to find original method!");
		}

		Object result = originalMethod.invoke(_originalClass, args);

		return wrapResult(method, originalMethod, result);
	}

	protected Object wrapResult(Method method, Method originalMethod, Object result)
		throws IllegalAccessException, InstantiationException {

		if (result == null) {
			return result;
		}

		if (!originalMethod.getReturnType().isInterface()) {
			return result;
		}

		Class returnType = method.getReturnType();

		Class wrapperClass = _generator.loadWrapperClass(returnType);

		ClassWrapper wrapper = (ClassWrapper)wrapperClass.newInstance();

		wrapper.wrap(result);

		return wrapper;
	}

	private String generateKey(Method method) {
		StringBuilder key = new StringBuilder();

		key.append(method.getName());

		key.append("(");

		for (Class parameterType : method.getParameterTypes()) {
			key.append(parameterType.getName());
			key.append(",");
		}

		key.append(") ");

		key.append(method.getReturnType().getName());

		return key.toString();
	}

	private Object _originalClass;
	private Map<String, Method> _methodsIndex = new HashMap();
	private JaxWsServiceGenerator _generator;
}
