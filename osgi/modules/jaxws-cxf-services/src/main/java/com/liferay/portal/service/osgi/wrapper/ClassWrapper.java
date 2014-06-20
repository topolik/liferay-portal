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

import org.apache.commons.lang.ClassUtils;

import java.io.Serializable;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
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

		return wrapReturnType(result);

	}

	protected void initMethods() {
		Map methodsIndex = new HashMap();

		Method[] methods = _wrappedClass.getClass().getMethods();
		for (Method method : methods) {
			methodsIndex.put(generateMethodKey(method), method);
		}

		_methodsIndex = methodsIndex;
	}

	private Object unwrap(Object obj, Class resultType) {
		if (obj == null) {
			return null;
		}

		Class actualType = obj.getClass();

		if (resultType.isPrimitive()) {
			return obj;
		}

		if (resultType.getName().startsWith("com.liferay")){
			throw new UnsupportedOperationException("Unable to create original class!");
		}

		throw new UnsupportedOperationException("Unsupported type: " + actualType.getName());
	}

	private Object wrapReturnType(Object result) {
		if (result == null) {
			return null;
		}
		Class resultClass = result.getClass();

		if (resultClass.isPrimitive()) {
			return result;

		}

		if(!JaxWsServiceFactory.isMarshalable(resultClass)) {
			throw new IncompatibleTypeException(
				"The type cannot be marshalled " + resultClass.getName());
		}

		if (JaxWsServiceFactory.canJAXBHandle(resultClass)) {
			return resultClass;
		}

		if (Map.class.isAssignableFrom(resultClass)) {
			return new HashMap((Map) result);
		}

		if (List.class.isAssignableFrom(resultClass) || resultClass.isArray()) {
			return new ArrayList((List) result);
		}

		// TODO: support wider range of "model" classes
		if (resultClass.getName().startsWith("com.liferay")){
			try {
				ClassWrapper beanWrapper = JaxWsF
				return generateBeanWrapper(resultClass, classLoader);
			}
			catch (IncompatibleMethodException e) {
				throw new IncompatibleTypeException(
					"The type cannot be marshalled " + resultClass.getName(),
					e);
			}
		}

		if (resultClass.isInterface()) {
			throw new IncompatibleTypeException(
				"Unable to marshall interface " + resultClass.getName());
		}

		// TODO: let's be optimistic and try it :)
		return asCtClass(resultClass);
		throw new UnsupportedOperationException("");
	}

	private Map<String, Method> _methodsIndex;
	private Object _wrappedClass;

}
