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

import javassist.ClassClassPath;
import javassist.CtClass;
import javassist.NotFoundException;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author Tomas Polesovsky
 */
public class JavassistFactory {

	public JavassistFactory(JaxWsClassPool pool) {
		_pool = pool;
	}

	public CtClass asCtClass(Class aClass) throws NotFoundException {
		return asCtClass(aClass.getName());
	}

	public CtClass asCtClass(String className) throws NotFoundException {
		return _pool.getCtClass(className);
	}

	public boolean hasCtClass(String className) {
		try {
			asCtClass(className);
		} catch (NotFoundException e) {
			return false;
		}

		return true;
	}


	public static String generateClassName(Class serviceClass) {
		return _GENERATED + serviceClass.getName();
	}

	protected void addClassLoader(ClassLoader classLoader) {
		_pool.addClassLoader(classLoader);
	}

	protected void appendClassPath(Class fromClass) {
		_pool.appendClassPath(new ClassClassPath(fromClass));
	}

	protected JaxWsClassPool getPool() {
		return _pool;
	}

	private static final String _GENERATED = "generated.";
	private JaxWsClassPool _pool;
}
