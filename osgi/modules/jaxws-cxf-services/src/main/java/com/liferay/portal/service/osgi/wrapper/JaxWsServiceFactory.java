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
import com.liferay.portal.kernel.search.SearchContext;
import com.liferay.portal.kernel.util.AggregateClassLoader;
import com.liferay.portal.kernel.util.ClassUtil;
import com.liferay.portal.kernel.util.MethodComparator;
import com.liferay.portal.service.ServiceContext;
import com.liferay.portal.theme.ThemeDisplay;
import javassist.CannotCompileException;
import javassist.ClassClassPath;
import javassist.ClassPool;
import javassist.CtClass;
import javassist.CtMethod;
import javassist.CtNewConstructor;
import javassist.CtNewMethod;
import javassist.Loader;
import javassist.NotFoundException;
import javassist.bytecode.AnnotationsAttribute;
import javassist.bytecode.ConstPool;
import javassist.bytecode.annotation.Annotation;
import javassist.bytecode.annotation.StringMemberValue;
import org.apache.commons.lang.ClassUtils;

import javax.jws.WebMethod;
import javax.jws.WebService;
import java.io.Serializable;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * @author Tomas Polesovsky
 */
public class JaxWsServiceFactory {

	public JaxWsServiceFactory() {
		init();
	}

	protected void init() {
		_pool = new ClassPool(true);
		_pool.appendClassPath(new ClassClassPath(getClass()));
	}

	public void detachServiceClass(Class serviceClass) {
		Class webServiceClass = findAnnotatedClass(
			serviceClass, WebService.class);

		if (webServiceClass != null) {
			try {
				CtClass webServiceCtClass = _pool.getCtClass(
					generateClassName(webServiceClass));

				webServiceCtClass.defrost();
				webServiceCtClass.detach();
			} catch (NotFoundException e) {
			}
		}
	}

	public Class loadWrapperClass(Class originalClass) {
		String wrapperName = generateClassName(originalClass);

		if (!hasCtClass(wrapperName)) {
			throw new NoClassDefFoundError(wrapperName);
		}

		try {
			return _pool.getClassLoader().loadClass(wrapperName);
		} catch (ClassNotFoundException e) {
			throw new RuntimeException("Internal exception", e);
		}
	}

	public JaxWsService createService(
			JaxWsServiceMetadata jaxWsServiceMetadata)
		throws NotFoundException, CannotCompileException,
		IllegalAccessException, InstantiationException {

		Class jaxWsServiceDefinitionClass =
			findJaxWsServiceDefinitionClass(jaxWsServiceMetadata);

		ClassLoader serviceClassloader =
			createServiceClassloader(jaxWsServiceDefinitionClass);

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
		throws NotFoundException, CannotCompileException {

		_pool.appendClassPath(new ClassClassPath(definitionClass));

		String serviceName = generateClassName(definitionClass);

		CtClass serviceCtClass = _pool.makeClass(serviceName);

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

		return (Class<JaxWsService>) serviceCtClass.toClass(
			classLoader, classLoader.getClass().getProtectionDomain());
	}

	protected CtMethod generateWebMethod(
			Method method, CtClass serviceCtClass, ClassLoader classLoader,
			Set<String> webMethodNames)
		throws NotFoundException, CannotCompileException,
		IncompatibleMethodException {

		CtMethod webMethod = wrapMethod(method, serviceCtClass, classLoader);

		addWebMethodAnnotation(method, webMethod, webMethodNames);

		return webMethod;
	}

	protected CtMethod wrapMethod(
			Method method, CtClass serviceCtClass, ClassLoader classLoader)
		throws NotFoundException, IncompatibleMethodException,
		CannotCompileException {

		CtMethod wrappingMethod = generateMethodDeclaration(
			method, serviceCtClass, classLoader);

		StringBuffer methodBody = new StringBuffer();
		methodBody.append("{");
		methodBody.append("String methodKey = \"%1$s\";");

		methodBody.append("Object[] unwrappedArguments = new Object[] {");
		for (int i = 0; i < method.getParameterTypes().length; i++) {
			methodBody.append("unwrap($" + i + ")");
			if ((i + 1) < method.getParameterTypes().length) {
				methodBody.append(",");
			}
		}
		methodBody.append("};");

		methodBody.append("return ($r) super.invoke(methodKey, wrappedArguments);");
		methodBody.append("}");

		wrappingMethod.setBody(String.format(
			methodBody.toString(), ClassWrapper.generateMethodKey(method)));

		return wrappingMethod;
	}

	private CtMethod generateMethodDeclaration(
			Method method, CtClass serviceCtClass, ClassLoader classLoader)
		throws NotFoundException, IncompatibleMethodException,
		CannotCompileException {

		Class[] parameterTypes = method.getParameterTypes();
		CtClass[] parameters = new CtClass[parameterTypes.length];
		for (int i = 0; i < parameters.length; i++) {
			Class parameterClass = parameterTypes[i];
			try {
				parameters[i] = wrapType(parameterClass, classLoader);
			} catch (IncompatibleTypeException e) {
				throw new IncompatibleMethodException(
					"Unable to register method " +
						method.getDeclaringClass().getName() + "." +
						ClassWrapper.generateMethodKey(method) +
						" invalid parameter " + parameterClass
					, e);
			}
		}

		CtClass[] exceptions = null;
		if (method.getExceptionTypes().length > 0) {
			exceptions = new CtClass[]{asCtClass(Exception.class)};
		}
		else {
			exceptions = new CtClass[0];
		}

		Class returnType = method.getReturnType();
		CtClass ctReturnType = null;
		try {
			ctReturnType = wrapType(returnType, classLoader);
		} catch (IncompatibleTypeException e) {
			throw new IncompatibleMethodException(
				"Unable to register method " +
					method.getDeclaringClass().getName() + "." +
					ClassWrapper.generateMethodKey(method) +
					" invalid return type " + returnType
				, e);
		}

		String methodName = method.getName();

		CtMethod result = CtNewMethod.abstractMethod(
			ctReturnType, methodName, parameters, exceptions,
			serviceCtClass);

		return result;
	}

	private CtClass wrapType(Class originalClass, ClassLoader classLoader)
		throws IncompatibleTypeException, NotFoundException,
		CannotCompileException {

		if(!isMarshalable(originalClass)) {
			throw new IncompatibleTypeException(
				"The type cannot be marshalled " + originalClass.getName());
		}

		if (canJAXBHandle(originalClass)) {
			return asCtClass(originalClass);
		}

		if (originalClass.isPrimitive()) {
			return asCtClass(ClassUtils.primitiveToWrapper(originalClass));
		}

		if (Map.class.isAssignableFrom(originalClass)) {
			return asCtClass(HashMap.class);
		}

		if (List.class.isAssignableFrom(originalClass) || originalClass.isArray()) {
			return asCtClass(ArrayList.class);
		}

		// TODO: support wider range of "model" classes
		if (originalClass.getName().startsWith("com.liferay")){
			try {
				return generateBeanWrapper(originalClass, classLoader);
			}
			catch (IncompatibleMethodException e) {
				throw new IncompatibleTypeException(
					"The type cannot be marshalled " + originalClass.getName(),
					e);
			}
		}

		if (originalClass.isInterface()) {
			throw new IncompatibleTypeException(
				"Unable to marshall interface " + originalClass.getName());
		}

		// TODO: let's be optimistic and try it :)
		return asCtClass(originalClass);
	}

	protected ClassLoader createServiceClassloader(Class serviceDefinition) {
		AggregateClassLoader aggregateClassLoader = new AggregateClassLoader(
			getClass().getClassLoader());

		aggregateClassLoader.addClassLoader(serviceDefinition.getClassLoader());
		Loader loader = new Loader(aggregateClassLoader, _pool);

		return loader;
	}

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


	protected CtClass generateBeanWrapper(
			Class interfaceClass, ClassLoader classLoader)
		throws NotFoundException, CannotCompileException,
			IncompatibleMethodException {

		String wrapperName = generateClassName(interfaceClass);

		// cross dependency, the interface wrapper has been already generated
		if (hasCtClass(wrapperName)) {
			return asCtClass(wrapperName);
		}

		CtClass wrapper = _pool.makeClass(wrapperName);

		wrapper.addInterface(asCtClass(Serializable.class));
		wrapper.setSuperclass(asCtClass(ClassWrapper.class));
		wrapper.addConstructor(CtNewConstructor.defaultConstructor(wrapper));

		Method[] methods = interfaceClass.getDeclaredMethods();
		Arrays.sort(methods, new MethodComparator());

		List<String> fields = new ArrayList<String>();
		// add get/set methods
		for (Method method : methods) {
			String fieldName = parseFieldName(method);

			if (fieldName == null) {
				continue;
			}

			wrapper.addMethod(wrapMethod(method, wrapper, classLoader));
			fields.add(fieldName);
		}

		return wrapper;
	}

	protected String generateClassName(Class serviceClass) {
		if (Map.class.isAssignableFrom(serviceClass)) {
			return HashMap.class.getName();
		}
		if (List.class.isAssignableFrom(serviceClass) || serviceClass.isArray()) {
			return ArrayList.class.getName();
		}

		return "soap." + serviceClass.getSimpleName() + _SOAP;
	}

	protected static boolean isMarshalable(Class aClass) {
		Class[] unmarshableClasses = {
			ThemeDisplay.class, ServiceContext.class, SearchContext.class};

		for (Class unmarshableClass: unmarshableClasses) {
			if (aClass.isAssignableFrom(unmarshableClass)) {
				return false;
			}
		}

		return true;
	}

	protected static boolean canJAXBHandle(Class returnType) {
		Class[] builtIn = {
			javax.activation.DataHandler.class, java.awt.Image.class,
			java.lang.String.class, java.math.BigInteger.class,
			java.math.BigDecimal.class, java.net.URI.class,
			java.util.Calendar.class, java.util.Date.class,
			java.util.UUID.class, javax.xml.datatype.XMLGregorianCalendar.class,
			javax.xml.datatype.Duration.class, javax.xml.namespace.QName.class,
			javax.xml.transform.Source.class,
			Boolean.class, Byte.class, Double.class, Float.class, Long.class,
			Integer.class, Short.class, Character.class};

		for (Class builtInClass : builtIn) {
			if (returnType.equals(builtInClass)) {
				return true;
			}
		}

		return false;
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

	private String parseFieldName(Method method) {
		String methodName = method.getName();

		int index = 0;

		if ((methodName.length() > 3) && methodName.startsWith("get")) {
			if (method.getReturnType() == Void.class) {
				return null;
			}

			if (method.getParameterTypes().length != 0) {
				return null;
			}

			if (Character.isLowerCase(methodName.charAt(3))) {
				return null;
			}

			index = 3;
		}
		else if ((methodName.length() > 3) && methodName.startsWith("set")) {
			if (method.getReturnType() != Void.class) {
				return null;
			}

			if (method.getParameterTypes().length != 1) {
				return null;
			}

			if (Character.isLowerCase(methodName.charAt(3))) {
				return null;
			}

			index = 3;
		}
		else if ((methodName.length() > 2) && methodName.startsWith("is")) {
			if (method.getReturnType() == Void.class) {
				return null;
			}

			if (method.getParameterTypes().length != 0) {
				return null;
			}

			if (Character.isLowerCase(methodName.charAt(2))) {
				return null;
			}

			index = 2;
		}
		else {
			return null;
		}

		String result = "" + Character.toLowerCase(methodName.charAt(index));

		if (methodName.length() > (index)) {
			result += methodName.substring(index + 1);
		}

		return result;
	}

	private CtClass asCtClass(Class aClass) throws NotFoundException {
		return asCtClass(aClass.getName());
	}

	private CtClass asCtClass(String className) throws NotFoundException {
		return _pool.getCtClass(className);
	}

	private boolean hasCtClass(String className) {
		try {
			asCtClass(className);
		} catch (NotFoundException e) {
			return false;
		}

		return true;
	}

	private static final String _SOAP = "SOAP";
	private ClassPool _pool;
	// TODO: until we have the right @REMOTE annotation, use JSONWebService annotation
	private static final Class PORTAL_REMOTE_SERVICE_ANNOTATION = JSONWebService.class;
}
