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

import com.liferay.portal.kernel.util.AggregateClassLoader;
import javassist.ClassPool;
import javassist.Loader;

import java.net.URL;
import java.net.URLClassLoader;
import java.security.SecureClassLoader;

/**
 * @author Tomas Polesovsky
 */
public class JaxWsClassPool extends javassist.ClassPool {

	public JaxWsClassPool() {
		super(true);

		_aggregateClassLoader = new AggregateClassLoader(getClass().getClassLoader());
		_classLoader = new DelegatingLoader(_aggregateClassLoader, this);
	}

	@Override
	public ClassLoader getClassLoader() {
		return _classLoader;
	}

	public void addClassLoader(ClassLoader cl) {
		_aggregateClassLoader.addClassLoader(cl);
	}

	private AggregateClassLoader _aggregateClassLoader;
	private ClassLoader _classLoader;
}

class DelegatingLoader extends Loader {

	public DelegatingLoader(ClassLoader parent, ClassPool classPool) {
		super(parent, classPool);
	}

	/**
	 * Always try parent first, we don't redefine existing classes.
	 * We also avoid ClassCastException by loading the same class only once
	 */
	@Override
	protected Class loadClassByDelegation(String name)
		throws ClassNotFoundException {

		try {
			return delegateToParent(name);
		}
		catch (ClassNotFoundException e) {
			return null;
		}
	}
}
