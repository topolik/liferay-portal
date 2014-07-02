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

import com.liferay.portal.kernel.jsonwebservice.JSONWebService;
import com.liferay.portal.kernel.util.MethodComparator;
import javassist.CannotCompileException;
import javassist.CtClass;
import javassist.CtMethod;
import javassist.CtNewConstructor;
import javassist.Loader;
import javassist.NotFoundException;
import javassist.bytecode.AnnotationsAttribute;
import javassist.bytecode.ConstPool;
import javassist.bytecode.annotation.Annotation;
import javassist.bytecode.annotation.StringMemberValue;

import javax.jws.WebMethod;
import javax.jws.WebService;
import java.io.Serializable;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

/**
 * @author Tomas Polesovsky
 */
public class JaxWsServiceFactory extends JavassistFactory {

	public JaxWsServiceFactory() {
		super(new JaxWsClassPool());

		_typeWrapperFactory = new TypeWrapperFactory(getPool());

		initClassLoading();
	}

	protected void initClassLoading() {
		addClassLoader(getClass().getClassLoader());
//		addClassLoader(new Loader(getClass().getClassLoader(), getPool()));
//		appendClassPath(getClass());
	}

	public void detachServiceClass(Class serviceClass) {
		Class webServiceClass = findAnnotatedClass(
			serviceClass, WebService.class);

		if (webServiceClass != null) {
			try {
				CtClass webServiceCtClass = getPool().getCtClass(
					generateClassName(webServiceClass));

				webServiceCtClass.defrost();
				webServiceCtClass.detach();
			} catch (NotFoundException e) {
			}
		}
	}

	public JaxWsService createService(
			JaxWsServiceMetadata jaxWsServiceMetadata)
		throws NotFoundException, CannotCompileException,
		IllegalAccessException, InstantiationException, ClassNotFoundException {

		Class jaxWsServiceDefinitionClass =
			findJaxWsServiceDefinitionClass(jaxWsServiceMetadata);

		addClassLoader(jaxWsServiceDefinitionClass.getClassLoader());

		// TODO: remove all classloader references
		ClassLoader serviceClassloader =
			null;

		Class<JaxWsService> jaxWsServiceClass = generateJaxWsServiceClass(
			jaxWsServiceDefinitionClass, serviceClassloader,
			jaxWsServiceMetadata);

		JaxWsService jaxWsService = jaxWsServiceClass.newInstance();
		jaxWsService.setMetadata(jaxWsServiceMetadata);

		return jaxWsService;
	}

	protected Class findJaxWsServiceDefinitionClass(
		JaxWsServiceMetadata jaxWsServiceMetadata) {

		Object service = jaxWsServiceMetadata.getService();

		Class serviceClass = service.getClass();

		// TODO: for now use JSONWebService annotation
		return findAnnotatedClass(serviceClass, JSONWebService.class);
	}

	protected Class<JaxWsService> generateJaxWsServiceClass(
			Class definitionClass, ClassLoader classLoader,
			JaxWsServiceMetadata metadata)
		throws NotFoundException, CannotCompileException, ClassNotFoundException {

		appendClassPath(definitionClass);

		String serviceName = definitionClass.getName() + "SOAP";

		CtClass serviceCtClass = getPool().makeClass(serviceName);

		serviceCtClass.addInterface(asCtClass(Serializable.class));
		serviceCtClass.setSuperclass(asCtClass(JaxWsService.class));
		serviceCtClass.addConstructor(CtNewConstructor.defaultConstructor(
			serviceCtClass));

		addWebServiceAnnotation(serviceCtClass, serviceName);

		Set<String> webMethodNames = new HashSet<String>();
		Method[] methods = definitionClass.getDeclaredMethods();
		Arrays.sort(methods, new MethodComparator());
		// add declared methods
		for (Method method : methods) {
			try {
				CtMethod serviceMethod = generateWebMethod(
					method, serviceCtClass, classLoader, webMethodNames);

				serviceCtClass.addMethod(serviceMethod);
			} catch (IncompatibleMethodException e) {
				// TODO: logging service
				//e.printStackTrace();
				System.out.println(e.getMessage());
			}
		}

		return (Class<JaxWsService>) getPool().getClassLoader().loadClass(
			serviceName);
	}

	protected CtMethod generateWebMethod(
			Method method, CtClass serviceCtClass, ClassLoader classLoader,
			Set<String> webMethodNames)
		throws NotFoundException, CannotCompileException,
		IncompatibleMethodException {

		CtMethod webMethod = _typeWrapperFactory.generateMethodDeclaration(
			method, serviceCtClass, _MAX_DEPTH);

		StringBuffer methodBody = new StringBuffer();
		methodBody.append("{");
		methodBody.append("String methodKey = \"%1$s\";");
		methodBody.append("return ($r) super.invoke(methodKey, $args);");
		methodBody.append("}");

		webMethod.setBody(String.format(
			methodBody.toString(), JaxWsService.generateMethodKey(method)));

		addWebMethodAnnotation(method, webMethod, webMethodNames);

		return webMethod;
	}

//	protected ClassLoader createServiceClassloader(Class serviceDefinition) {
//		AggregateClassLoader aggregateClassLoader =
//			new AggregateClassLoader(null);
//
//		aggregateClassLoader.addClassLoader(getClass().getClassLoader());
//		aggregateClassLoader.addClassLoader(serviceDefinition.getClassLoader());
//		aggregateClassLoader.addClassLoader(new Loader(getPool()));
//
//		return aggregateClassLoader;
//	}

	protected void addWebServiceAnnotation(
		CtClass generatedInterface, String serviceName) {

		ConstPool constpool = generatedInterface.getClassFile().getConstPool();

		// add runtime visible @WebService(name=serviceName)
		AnnotationsAttribute attr = new AnnotationsAttribute(
			constpool, AnnotationsAttribute.visibleTag);

		Annotation annot = new Annotation(
			WebService.class.getName(), constpool);

		annot.addMemberValue(
			"name", new StringMemberValue(serviceName, constpool));

		attr.addAnnotation(annot);

		generatedInterface.getClassFile().addAttribute(attr);
	}

	protected void addWebMethodAnnotation(
		Method method, CtMethod generatedMethod, Set<String> webMethodNames) {

		// Avoid duplicating of web method names (cannot be registered)
		String uniqueWebMethodName = method.getName();
		int counter = 0;
		while (webMethodNames.contains(uniqueWebMethodName)) {
			uniqueWebMethodName = method.getName() + counter++;
		}
		webMethodNames.add(uniqueWebMethodName);

		ConstPool constPool = generatedMethod.getMethodInfo().getConstPool();

		AnnotationsAttribute attr = new AnnotationsAttribute(
			constPool, AnnotationsAttribute.visibleTag);

		Annotation annot = new Annotation(
			WebMethod.class.getName(), constPool);

		annot.addMemberValue(
			"operationName", new StringMemberValue(
			uniqueWebMethodName, constPool));

		attr.addAnnotation(annot);
		generatedMethod.getMethodInfo().addAttribute(attr);
	}

	private Class findAnnotatedClass(Class cls, Class annotation) {
		if (cls == null) {
			return null;
		}
		if (null != cls.getAnnotation(annotation)) {
			return cls;
		}
		for (Class inf : cls.getInterfaces()) {
			if (null != inf.getAnnotation(annotation)) {
				return inf;
			}
		}

		return findAnnotatedClass(cls.getSuperclass(), annotation);
	}

	private static final int _MAX_DEPTH = 5;
	private TypeWrapperFactory _typeWrapperFactory;
	// TODO: until we have the right @REMOTE annotation, use JSONWebService annotation
	private static final Class PORTAL_REMOTE_SERVICE_ANNOTATION = JSONWebService.class;
}
