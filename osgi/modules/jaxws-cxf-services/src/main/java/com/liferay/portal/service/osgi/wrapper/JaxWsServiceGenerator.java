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

import com.liferay.portal.kernel.jsonwebservice.JSONWebService;
import com.liferay.portal.kernel.search.SearchContext;
import com.liferay.portal.kernel.util.AggregateClassLoader;
import com.liferay.portal.kernel.util.MethodComparator;
import com.liferay.portal.service.ServiceContext;
import com.liferay.portal.theme.ThemeDisplay;
import javassist.CannotCompileException;
import javassist.ClassClassPath;
import javassist.ClassPool;
import javassist.CtClass;
import javassist.CtField;
import javassist.CtMethod;
import javassist.CtNewConstructor;
import javassist.CtNewMethod;
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
import java.lang.reflect.Proxy;
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
public class JaxWsServiceGenerator {

	public JaxWsServiceGenerator() {
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
					getWrapperClassName(webServiceClass));

				webServiceCtClass.defrost();
				webServiceCtClass.detach();
			}
			catch (NotFoundException e) {
			}
		}
	}

	public Class findAnnotatedClass(Class cls, Class annotation) {
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

	public Class loadWrapperClass(Class originalClass) {
		String wrapperName = getWrapperClassName(originalClass);

		if (!hasCtClass(wrapperName)) {
			throw new NoClassDefFoundError(wrapperName);
		}

		try {
			return _pool.getClassLoader().loadClass(wrapperName);
		}
		catch (ClassNotFoundException e) {
			throw new RuntimeException("Internal exception", e);
		}
	}

	public Object wrapPortalService(final Object originalService)
		throws NotFoundException, CannotCompileException {

		// Note: Portal service is already ServiceBeanAopProxy

		// Now we add @WebService annotation by generating a dynamic interface
		// and proxying the original service.
		// The interface has the @WebService annotation.

		// Q: is CXF able to get web service methods if the class is a proxy?
		// A: No, the @WebService interface must have all the methods from
		//    original service

		// Q: Is JAXB able to (un)marshall interfaces used by the service?
		// A: No, we need to create dynamic wrappers for the interfaces

		Class webServiceDefinitionClass = findAnnotatedClass(
			originalService.getClass(), PORTAL_REMOTE_SERVICE_ANNOTATION);

		// We need to reference in the one generated interface all:
		// 1, classes used in the original service, not visible to this bundle
		// 2, JAX-WS annotations references from this bundle, but not referenced
		// from the service bundle
		// >> For this we need AggregatedClassLoader
		AggregateClassLoader aggregateClassLoader = new AggregateClassLoader(
			getClass().getClassLoader());
		aggregateClassLoader.addClassLoader(webServiceDefinitionClass.getClassLoader());

		// CXF is separately loading the generated interface using
		// proxy.getClassLoader().loadClass(generatedInterfaceName).
		// For this to work we need to wrap the aggregated classloader with
		// javassist Loader. It looks first into the pool of generated classes
		// when loading the generated interface and returns correct class
		// instance
		Loader loader = new Loader(aggregateClassLoader, _pool);

		Class serviceInterface = generateServiceInterface(
			webServiceDefinitionClass, loader);

		aggregateClassLoader.addClassLoader(
			originalService.getClass().getClassLoader());

		// create a new proxy
		Object proxy = Proxy.newProxyInstance(
			loader, new Class[]{serviceInterface},
			new ResultWrappingInvocationHandler(originalService, this));

		return proxy;
	}

	protected Class generateServiceInterface(
			Class annotatedClass, ClassLoader cl)
		throws NotFoundException, CannotCompileException {

		_pool.appendClassPath(new ClassClassPath(annotatedClass));

		String serviceName = getWrapperClassName(annotatedClass);

		CtClass generatedInterface = _pool.makeInterface(serviceName);

		addWebServiceAnnotation(generatedInterface, serviceName);

		Method[] methods = annotatedClass.getDeclaredMethods();
		Arrays.sort(methods, new MethodComparator());

		// add declared methods
		Set<String> webMethodNames = new HashSet<String>();
		for (Method method : methods) {
			CtMethod serviceMethod = generateWebMethod(
				annotatedClass, method, generatedInterface, cl, webMethodNames);

			if (serviceMethod != null) {
				generatedInterface.addMethod(serviceMethod);
			}
		}

		try {
			return cl.loadClass(serviceName);
		} catch (ClassNotFoundException e) {
			throw new RuntimeException(
				"Unable to define service interface " + serviceName, e);
		}
	}

	protected String getWrapperClassName(Class serviceClass) {
		if (Map.class.isAssignableFrom(serviceClass)) {
			return HashMap.class.getName();
		}
		if (List.class.isAssignableFrom(serviceClass) || serviceClass.isArray()) {
			return ArrayList.class.getName();
		}

		return "soap." + serviceClass.getSimpleName() + _SOAP;
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

	protected CtMethod generateWebMethod(
			Class serviceClass, Method method, CtClass generatedInterface,
			ClassLoader cl, Set<String> webMethodNames)
		throws NotFoundException, CannotCompileException {

		Class[] parameterTypes = method.getParameterTypes();
		CtClass[] parameters = new CtClass[parameterTypes.length];
		for (int i = 0; i < parameters.length; i++) {
			Class parameterClass = parameterTypes[i];
			CtClass parameterCtClass = null;

			if(isNotMarshalable(parameterClass)) {
				System.out.println("Unable to register method " +
					serviceClass.getName() + ". "
					+ method.getName() +
					", cannot handle " + parameterClass.getName());

				return null;
			}
			else if (canJAXBHandle(parameterClass)) {
				parameterCtClass = asCtClass(parameterClass);
			}
			else if (Map.class.isAssignableFrom(parameterClass)) {
				parameterCtClass = asCtClass(HashMap.class);
			}
			else if (List.class.isAssignableFrom(parameterClass) || parameterClass.isArray()) {
				parameterCtClass = asCtClass(ArrayList.class);
			}
			else if (parameterClass.getName().startsWith("com.liferay")){
				parameterCtClass = generateInterfaceWrapper(parameterClass, cl);
			}
			else if (parameterClass.isInterface()) {
				System.out.println(
					"Unable to register method " +
						serviceClass.getName() + ". "
						+ method.getName() +
						", cannot map interface " +
						parameterClass.getName());

				return null;
			}
			else {
				// let's be optimistic and try it :)
				parameterCtClass = asCtClass(parameterClass);
			}

			parameters[i] = parameterCtClass;
		}

		Class[] exceptionTypes = method.getExceptionTypes();
		CtClass[] exceptions = new CtClass[exceptionTypes.length];
		for (int i = 0; i < exceptions.length; i++) {
			exceptions[i] = asCtClass(exceptionTypes[i]);
		}

		Class returnType = method.getReturnType();
		CtClass ctReturnType = null;

		if (isNotMarshalable(returnType)) {
			System.out.println(
				"Unable to register method " +
					serviceClass.getName() + ". "
					+ method.getName() +
					", cannot map interface " +
					returnType.getName());

			return null;
		}
		else if (canJAXBHandle(returnType)) {
			ctReturnType = asCtClass(returnType);
		}
		else if (Map.class.isAssignableFrom(returnType)) {
			ctReturnType = asCtClass(HashMap.class);
		}
		else if (List.class.isAssignableFrom(returnType) || returnType.isArray()) {
			ctReturnType = asCtClass(ArrayList.class);
		}
		else if (returnType.getName().startsWith("com.liferay")){
			ctReturnType = generateInterfaceWrapper(returnType, cl);
		}
		else if (returnType.isInterface()) {
			System.out.println(
				"Unable to register method " +
					serviceClass.getName() + ". "
					+ method.getName() +
					", cannot map interface " +
					returnType.getName());

			return null;
		}
		else {
			// let's be optimistic and try it :)
			ctReturnType = asCtClass(returnType);
		}

		String methodName = method.getName();

		CtMethod result = CtNewMethod.abstractMethod(
			ctReturnType, methodName, parameters, exceptions,
			generatedInterface);

		addWebMethodAnnotation(method, result, webMethodNames);

		return result;
	}

	protected boolean isNotMarshalable(Class aClass) {
		Class[] unmarshableClasses = {
			ThemeDisplay.class, ServiceContext.class, SearchContext.class};

		for (Class unmarshableClass: unmarshableClasses) {
			if (aClass.isAssignableFrom(unmarshableClass)) {
				return true;
			}
		}

		return false;
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

	protected CtClass generateInterfaceWrapper(
			Class interfaceClass, ClassLoader cl)
		throws NotFoundException, CannotCompileException {

		String wrapperName = getWrapperClassName(interfaceClass);

		// cross dependency among services
		if (hasCtClass(wrapperName)) {
			return asCtClass(wrapperName);
		}

		CtClass wrapper = _pool.makeClass(wrapperName);

		wrapper.addInterface(asCtClass(Serializable.class));
		wrapper.addInterface(asCtClass(MethodParameterWrapper.class));
		wrapper.addConstructor(CtNewConstructor.defaultConstructor(wrapper));

		// create emtpy method to reference from setters, we create the content
		// later in generateInterfaceWrapperWrapMethod()
		// ^^ resolves cyclic dependencies
		wrapper.addMethod(CtMethod.make(
			"public void wrap(Object originalInterfaceInstance) {}", wrapper));

		Method[] methods = interfaceClass.getDeclaredMethods();
		Arrays.sort(methods, new MethodComparator());

		List<String> fields = new ArrayList<String>();
		// add get/set methods
		for (Method method : methods) {
			if (!isGetMethod(method)) {
				continue;
			}

			String fieldName = parseFieldName(method.getName());
			Class returnType = method.getReturnType();

			CtClass fieldType = null;

			if (returnType.equals(interfaceClass)) {
				fieldType = wrapper;
			}
			else if (isNotMarshalable(returnType)) {
				System.out.println(
					"Unable to register field " +
						interfaceClass.getName() + "." + fieldName +
						", cannot handle " + returnType.getName());

				continue;
			}
			else if (canJAXBHandle(returnType)) {
				fieldType = asCtClass(returnType);
			}
			else if (Map.class.isAssignableFrom(returnType)) {
				fieldType = asCtClass(HashMap.class);
			}
			else if (List.class.isAssignableFrom(returnType) || returnType.isArray()) {
				fieldType = asCtClass(ArrayList.class);
			}
			else if (returnType.getName().startsWith("com.liferay")){
				fieldType = generateInterfaceWrapper(returnType, cl);
			}
			else if (returnType.isInterface()) {
				System.out.println(
					"Unable to register field " +
						interfaceClass.getName() + "." + fieldName +
						", cannot handle " + returnType.getName());

				continue;
			}
			else {
				// let's be optimistic and try it :)
				fieldType = asCtClass(returnType);
			}

			wrapper.addField(new CtField(fieldType, "_" + fieldName, wrapper));
			wrapper.addMethod(generateGetter(wrapper, fieldName, fieldType));
			wrapper.addMethod(generateSetter(wrapper, fieldName, fieldType, returnType));

			fields.add(fieldName);
		}

		generateInterfaceWrapperWrapMethod(wrapper, interfaceClass, fields);

		return wrapper;
	}

	private boolean canJAXBHandle(Class returnType) {
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

		if (returnType.isPrimitive()) {
			return true;
		}

		for (Class builtInClass : builtIn) {
			if (returnType.equals(builtInClass)) {
				return true;
			}
		}

		return false;
	}

	protected void generateInterfaceWrapperWrapMethod(CtClass wrapper, Class interfaceClass, List<String> fields) throws CannotCompileException, NotFoundException {
		String originalInterfaceName = interfaceClass.getName();

		StringBuffer sb = new StringBuffer();
		sb.append("{");
		sb.append(originalInterfaceName + " interfaceInstance = ("+originalInterfaceName+") $1;");
		for(String fieldName : fields) {
			sb.append(getSetterNameForFieldName(fieldName));
			sb.append("(interfaceInstance.");
			sb.append(getGetterNameForFieldName(fieldName));
			sb.append("());");
		}
		sb.append("}");

		CtMethod m = wrapper.getDeclaredMethod("wrap");
		m.insertBefore(sb.toString());
	}

	protected CtMethod generateGetter(
			CtClass wrapper, String fieldName, CtClass fieldClass)
		throws CannotCompileException {

		String getterName = getGetterNameForFieldName(fieldName);

		CtMethod method = new CtMethod(
			fieldClass, getterName, new CtClass[0], wrapper);
		method.setBody("{ return _"+fieldName+"; }");

		return method;
	}

	private String getGetterNameForFieldName(String fieldName) {
		return "get" + Character.toUpperCase(fieldName.charAt(0))
			+ fieldName.substring(1);
	}

	protected CtMethod generateSetter(
			CtClass wrapper, String fieldName, CtClass fieldClass,
			Class originalType)
		throws CannotCompileException, NotFoundException {

		String methodName = getSetterNameForFieldName(fieldName);

		CtMethod ctMethod = null;

		if (canJAXBHandle(originalType)) {
			ctMethod = new CtMethod(CtClass.voidType, methodName, new CtClass[]{fieldClass}, wrapper);
			String method = "{ this._%1$s = $1; }";
			ctMethod.setBody(String.format(method, fieldName));
		}
		else {
			if (Map.class.isAssignableFrom(originalType)) {
				ctMethod = new CtMethod(CtClass.voidType, methodName, new CtClass[]{asCtClass(Map.class)}, wrapper);
				String method = "{ this._%1$s = new java.util.HashMap($1); }";
				ctMethod.setBody(String.format(method, fieldName));
			}
			else if (List.class.isAssignableFrom(originalType)) {
				ctMethod = new CtMethod(CtClass.voidType, methodName, new CtClass[]{asCtClass(List.class)}, wrapper);
				String method = "{ this._%1$s = new java.util.ArrayList($1); }";
				ctMethod.setBody(String.format(method, fieldName));
			}
			else if (originalType.isArray()) {
				Class parentType = originalType.getComponentType();
				while (parentType.isArray()) {
					parentType = parentType.getComponentType();
				}
				if(parentType.isPrimitive()) {
					ctMethod = new CtMethod(CtClass.voidType, methodName, new CtClass[]{asCtClass(originalType)}, wrapper);
					String method = "{ this._%1$s = new java.util.ArrayList(java.util.Arrays.asList(com.liferay.portal.kernel.util.ArrayUtil.toArray($1))); }";
					ctMethod.setBody(String.format(method, fieldName));
				}
				else {
					ctMethod = new CtMethod(CtClass.voidType, methodName, new CtClass[]{asCtClass(Object[].class)}, wrapper);
					String method = "{ this._%1$s = new java.util.ArrayList(java.util.Arrays.asList($1)); }";
					ctMethod.setBody(String.format(method, fieldName));
				}
			}
			else if(originalType.getName().startsWith("com.liferay")) {
				ctMethod = new CtMethod(CtClass.voidType, methodName, new CtClass[]{asCtClass(Object.class)}, wrapper);

				String method = "{" +
					"%2$s soapWrapper = new %2$s();" +
					"soapWrapper.wrap($1);" +
					"this._%1$s = soapWrapper;" +
					"}";

				ctMethod.setBody(String.format(method, fieldName, fieldClass.getName()));
			}
			else {
				// whoops, what now? let's continue the same was as when
				// assigning primitives
				ctMethod = new CtMethod(CtClass.voidType, methodName, new CtClass[]{fieldClass}, wrapper);
				String method = "{ this._%1$s = $1; }";
				ctMethod.setBody(String.format(method, fieldName));
			}
		}

		return ctMethod;
	}

	private String getSetterNameForFieldName(String fieldName) {
		return "set" + Character.toUpperCase(fieldName.charAt(0))
			+ fieldName.substring(1);
	}

	private boolean isGetMethod(Method method) {
		if (method.getReturnType() == Void.class) {
			return false;
		}

		if (method.getParameterTypes().length > 0) {
			return false;
		}

		String methodName = method.getName();

		if (methodName.length() < 4) {
			return false;
		}

		if (Character.isLowerCase(methodName.charAt(3))) {
			return false;
		}

		return methodName.startsWith("get");
	}

	private String parseFieldName(String methodName) {
		return Character.toLowerCase(methodName.charAt(3)) +
			methodName.substring(4);
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
		}
		catch (NotFoundException e) {
			return false;
		}

		return true;
	}

	private static final String _SOAP = "SOAP";
	private ClassPool _pool;
	// TODO: until we have the right @REMOTE annotation, use JSONWebService annotation
	private static final Class PORTAL_REMOTE_SERVICE_ANNOTATION = JSONWebService.class;
}
