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

import com.liferay.portal.service.osgi.wrapper.adapter.LocaleAdapter;

import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapters;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

/**
 * @author Tomas Polesovsky
 */
public abstract class JaxWsService {

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

	public void setMetadata(JaxWsServiceMetadata metadata) {
		this._metadata = metadata;

		wrapClass(metadata.getService());
	}

	public JaxWsServiceMetadata getMetadata() {
		return _metadata;
	}

	protected Object invoke(String methodName, Object... args) throws Exception {
		Method originalMethod = _methodsIndex.get(methodName);

		if (originalMethod == null) {
			throw new Exception("Unable to find original method!");
		}

		Object result = originalMethod.invoke(_wrappedClass, args);

		return TypeWrapperFactory.wrapTypeInstance(
			result, originalMethod.getReturnType(),
			getClass().getClassLoader());

	}

	protected void wrapClass(Object originalClass) {
		this._wrappedClass = originalClass;

		initMethods();
	}

	protected void initMethods() {
		Map methodsIndex = new HashMap();

		Method[] methods = _wrappedClass.getClass().getMethods();
		for (Method method : methods) {
			methodsIndex.put(generateMethodKey(method), method);
		}

		_methodsIndex = methodsIndex;
	}

	private Map<String, Method> _methodsIndex;
	private Object _wrappedClass;

	private JaxWsServiceMetadata _metadata;
}
