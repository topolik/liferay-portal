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

import com.liferay.portal.kernel.util.MethodComparator;
import javassist.CannotCompileException;
import javassist.CtClass;
import javassist.CtField;
import javassist.CtMethod;
import javassist.CtNewConstructor;
import javassist.CtNewMethod;
import javassist.CtPrimitiveType;
import javassist.NotFoundException;
import org.apache.cxf.common.util.ReflectionUtil;
import org.springframework.util.ReflectionUtils;

import java.io.Serializable;
import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

/**
 * @author Tomas Polesovsky
 */
public class TypeWrapperFactory extends JavassistFactory {

	public TypeWrapperFactory(JaxWsClassPool _pool) {
		super(_pool);
	}


	public static Object wrapTypeInstance(
		Object inst, Class<?> originalType, ClassLoader classLoader) {

		if (inst == null) {
			return null;
		}

		if (isMarshalable(originalType) ||
			originalType.isPrimitive() ||
			originalType.isArray()) {

			return inst;
		}

		String wrapperClassName = generateClassName(originalType);
		try {

			Class<TypeWrapper> typeWrapperClass =
				(Class<TypeWrapper>)classLoader.loadClass(wrapperClassName);

			TypeWrapper typeWrapper = typeWrapperClass.newInstance();

			typeWrapper.wrap(inst);

			return typeWrapper;
		} catch (ClassNotFoundException e) {
			throw new IllegalStateException(
				"Unable to find wrapper class " + wrapperClassName, e);
		} catch (InstantiationException e) {
			throw new IllegalStateException(
				"Unable to instantiate wrapper class " + wrapperClassName, e);
		} catch (IllegalAccessException e) {
			throw new IllegalStateException(
				"Unable to access wrapper class " + wrapperClassName, e);
		}
	}

	public CtClass wrapType(Class originalClass, int depth)
		throws IncompatibleTypeException, NotFoundException,
		CannotCompileException, TooDeepException {

		if (isBlacklisted(originalClass)) {
			throw new IncompatibleTypeException(
				"The type cannot be marshalled because it's blacklisted "
					+ originalClass.getName());
		}

		if (originalClass.equals(Void.TYPE) ||
			originalClass.equals(Void.class)) {

			return CtPrimitiveType.voidType;
		}

		if (originalClass.isPrimitive()) {
			return asCtClass(PrimitiveWrapper.convertClass(originalClass));
		}

		if (isMarshalable(originalClass)) {
			return asCtClass(originalClass);
		}

		if (!originalClass.isPrimitive() &&
			!originalClass.isArray() &&
			!originalClass.isInterface() &&
			!_hasDefaultConstructor(originalClass)) {

			throw new IncompatibleTypeException(
				"The type cannot be marshalled, no default constructor found "
					+ originalClass.getName());
		}

		if (depth <= 0) {
			throw new TooDeepException();
		}

		depth--;

		try {
			return generateTypeWrapperClass(originalClass, depth);
		}
		catch (IncompatibleMethodException e) {
			throw new IncompatibleTypeException(
				"The type cannot be marshalled " + originalClass.getName(),
				e);
		}
	}


	protected void generateFieldMethods(
		CtClass wrapper, Set<FieldDescriptor> fields, int depth)
		throws NotFoundException, IncompatibleMethodException,
		CannotCompileException {

		StringBuffer getMethodBody = new StringBuffer();
		getMethodBody.append("{");
		getMethodBody.append("return ($r) _fields.get(\"%1$s\");");
		getMethodBody.append("}");

		StringBuffer setMethodBody = new StringBuffer();
		setMethodBody.append("{");
		setMethodBody.append(" _fields.put(\"%1$s\", new Object[]{$1}[0]);");
		setMethodBody.append("}");

		for (Iterator<FieldDescriptor> it = fields.iterator(); it.hasNext(); ) {
			FieldDescriptor field = it.next();

			String fieldName = field.getFieldName();
			Class fieldType = field.getFieldType();

			String fieldCamelCaseName = Character.toUpperCase(fieldName.charAt(0)) + fieldName.substring(1);

			CtClass ctFieldType = null;
			try {
				ctFieldType = wrapType(fieldType, depth);
			} catch (IncompatibleTypeException e) {
				it.remove();
				// TODO: log error
				System.out.println(
					"Unable to generate field " + wrapper.getName() + "#" +
						fieldName + ". Incompatible field type, see:");
				e.printStackTrace();
				continue;
			} catch (TooDeepException e) {
				it.remove();
				// TODO: log error
				System.out.println(
					"Unable to generate field " + wrapper.getName() + "#" +
						fieldName + ". Too deep wrapping, see:");
				e.printStackTrace();
				continue;
			}

			CtMethod getMethod = CtNewMethod.abstractMethod(
				ctFieldType, "get" + fieldCamelCaseName,
				null, null, wrapper);

			getMethod.setBody(String.format(
				getMethodBody.toString(), fieldName));

			wrapper.addMethod(getMethod);

			CtMethod setMethod = CtNewMethod.abstractMethod(
				CtClass.voidType, "set" + fieldCamelCaseName,
//				new CtClass[]{ctFieldType.isPrimitive() ? ctFieldType : asCtClass(Object.class)}, null, wrapper);
				new CtClass[]{ctFieldType}, null, wrapper);

			setMethod.setBody(String.format(
				setMethodBody.toString(), fieldName));

			wrapper.addMethod(setMethod);
		}
	}

	protected CtMethod implementMethod(
		Method method, CtClass serviceCtClass, int depth)
		throws NotFoundException, IncompatibleMethodException,
		CannotCompileException {

		CtMethod wrappingMethod = generateMethodDeclaration(
			method, serviceCtClass, depth);

		StringBuffer methodBody = new StringBuffer();
		methodBody.append("{");
		methodBody.append("throw new UnsupportedOperationException();");
		methodBody.append("}");

		wrappingMethod.setBody(methodBody.toString());

		return wrappingMethod;
	}

	protected CtClass generateTypeWrapperClass(
			Class originalClass, int depth)
		throws NotFoundException, CannotCompileException,
		IncompatibleMethodException, IncompatibleTypeException {

		if (!originalClass.isInterface() &&
			!_hasDefaultConstructor(originalClass)) {

			throw new IncompatibleTypeException(
				"Unable to generate type wrapper, missing default constructor "
					+ originalClass.getName());
		}

		String wrapperName = generateClassName(originalClass);

		// cross dependency, the interface wrapper has been already generated
		if (hasCtClass(wrapperName)) {
			return asCtClass(wrapperName);
		}

		CtClass wrapper = getPool().makeClass(wrapperName);

		wrapper.addInterface(asCtClass(Serializable.class));
		wrapper.addInterface(asCtClass(TypeWrapper.class));

		if (originalClass.isInterface()) {
			wrapper.addInterface(asCtClass(originalClass));
		}
		else {
			wrapper.setSuperclass(asCtClass(originalClass));
		}

		wrapper.addConstructor(CtNewConstructor.defaultConstructor(wrapper));
		wrapper.addField(
			new CtField(asCtClass(HashMap.class), "_fields", wrapper),
			CtField.Initializer.byExpr("new java.util.HashMap()"));


		// create emtpy method to reference from setters, we create the content
		// later in generateTypeWrapperWrapMethod()
		// ^^ resolves cyclic dependencies
		wrapper.addMethod(CtMethod.make(
			"public void wrap(Object originalObject) {}", wrapper));

		Method[] methods = originalClass.getDeclaredMethods();
		Arrays.sort(methods, new MethodComparator());

		Set<FieldDescriptor> fields = new HashSet<FieldDescriptor>();

		for (Method method : methods) {
			FieldDescriptor field = _parseField(method);

			if (field != null) {
				fields.add(field);
				continue;
			}

			int methodModifiers = method.getModifiers();
			boolean staticMethod = Modifier.isStatic(methodModifiers);
			boolean finalMethod = Modifier.isFinal(methodModifiers);
			boolean nativeMethod = Modifier.isNative(methodModifiers);
			boolean abstractMethod = Modifier.isAbstract(methodModifiers);

			if (!staticMethod && !finalMethod && !nativeMethod && abstractMethod) {
				if (field == null) {
					CtMethod wrapperMethod = implementMethod(
						method, wrapper, depth);

					wrapper.addMethod(wrapperMethod);
				}
			}
		}

		generateFieldMethods(wrapper, fields, depth);

		generateTypeWrapperWrapMethod(
			wrapper, originalClass, fields, depth);

		// TODO: Debug
		wrapper.debugWriteFile("/tmp/jax-ws-generated");

		return wrapper;
	}



	public CtMethod generateMethodDeclaration(
		Method method, CtClass serviceCtClass, int depth)
		throws NotFoundException, IncompatibleMethodException,
		CannotCompileException {

		Class[] parameterTypes = method.getParameterTypes();
		CtClass[] parameters = new CtClass[parameterTypes.length];
		for (int i = 0; i < parameters.length; i++) {
			Class parameterClass = parameterTypes[i];
			try {
				parameters[i] = wrapType(parameterClass, depth);
			} catch (IncompatibleTypeException e) {
				throw new IncompatibleMethodException(
					"Unable to register method " +
						method.toGenericString() +
						" invalid parameter " + parameterClass
					, e);
			} catch (TooDeepException e) {
				throw new IncompatibleMethodException(
					"Unable to register method " +
						method.toGenericString() +
						", wrapping went too deep ", e);
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
			ctReturnType = wrapType(returnType, depth);
		} catch (IncompatibleTypeException e) {
			throw new IncompatibleMethodException(
				"Unable to register method " +
					method.toGenericString() +
					", invalid return type " + returnType
				, e);
		} catch (TooDeepException e) {
			throw new IncompatibleMethodException(
				"Unable to register method " +
					method.toGenericString() +
					", wrapping went too deep", e);
		}

		String methodName = method.getName();

		CtMethod result = CtNewMethod.abstractMethod(
			ctReturnType, methodName, parameters, exceptions,
			serviceCtClass);

		return result;
	}

	protected void generateTypeWrapperWrapMethod(
			CtClass wrapper, Class originalClass, Set<FieldDescriptor> fields,
			int depth)
		throws CannotCompileException, NotFoundException {

		StringBuffer sb = new StringBuffer();
		sb.append("{");
		sb.append("if ($1 == null) {return;}");
		sb.append(
			String.format("%1$s orig = (%1$s) $1;", originalClass.getName()));

		for(FieldDescriptor field : fields) {
			String fieldName = field.getFieldName();
			Class fieldType = field.getFieldType();

			String fieldCamelCaseName =
				Character.toUpperCase(fieldName.charAt(0))+
					fieldName.substring(1);

			String getMethodName = _findGetMethod(
				originalClass, fieldCamelCaseName, fieldType);

			if (getMethodName == null) {
				// TODO: log error
				System.out.println(
					"Unable to generate field " + wrapper.getName() + "#" +
						fieldName + ", no get or is method found");

				continue;
			}


			if (fieldType.isPrimitive()) {
				String transferFieldBody =
					"this.set%1$s(com.liferay.portal.service.osgi.wrapper." +
						"PrimitiveWrapper.convert(orig.%2$s%1$s()));";

				sb.append(String.format(
					transferFieldBody, fieldCamelCaseName, getMethodName));

				continue;
			}

			if (isMarshalable(fieldType)) {
				String transferFieldBody = "this.set%1$s(orig.%2$s%1$s());";
				sb.append(String.format(
					transferFieldBody, fieldCamelCaseName, getMethodName));

				continue;
			}

			try {
				wrapType(fieldType, depth);
			}
			catch (IncompatibleTypeException e) {
				// TODO: log error
				System.out.println(
					"Unable to generate field " + wrapper.getName() + "#" +
						fieldName + ", field type is not supported, see:");
				e.printStackTrace();
				continue;
			}
			catch (TooDeepException e) {
				// TODO: log error
				System.out.println(
					"Unable to generate field " + wrapper.getName() + "#" +
						fieldName + ". Incompatible type, see:");
				e.printStackTrace();
				continue;
			}

			StringBuffer sb2 = new StringBuffer();
			sb2.append("{");
			sb2.append("Object result = orig.%2$s%1$s();");
			sb2.append("Object resultWrapper = com.liferay.portal." +
				"service.osgi.wrapper.TypeWrapperFactory.wrapTypeInstance(" +
				"result, %3$s.class, getClass().getClassLoader());");
			sb2.append("this.set%1$s( (%4$s) resultWrapper);");
			sb2.append("}");

			sb.append(String.format(sb2.toString(), fieldCamelCaseName, getMethodName, fieldType.getName(), generateClassName(fieldType)));
		}
		sb.append("}");

		CtMethod m = wrapper.getDeclaredMethod("wrap");
		m.insertBefore(sb.toString());
	}

	private String _findGetMethod(
		Class originalClass, String fieldCamelCaseName, Class fieldType) {

		String result = null;
		for (Method m : originalClass.getMethods()) {
			if ((result == null) &&
				m.getName().equals("get"+fieldCamelCaseName) &&
				(m.getParameterTypes().length == 0) &&
				m.getReturnType().equals(fieldType)) {

				result = "get";
			}

			if (fieldType.equals(Boolean.TYPE) || fieldType.equals(Boolean.class)) {
				if (m.getName().equals("is"+fieldCamelCaseName) &&
					(m.getParameterTypes().length == 0) &&
					m.getReturnType().equals(fieldType)) {

					result = "is";
				}
			}
		}

		return result;
	}


	protected static boolean isMarshalable(Class aClass) {
		if (canJAXBHandle(aClass)) {
			return true;
		}

		// TODO: let's say JAXB can handle all java.util classes
		if (aClass.getName().startsWith("java.util")) {
			return true;
		}

		if (aClass.isPrimitive() ||
			(aClass.isArray() && isMarshalable(aClass.getComponentType()))) {

			return true;
		}

		return false;
	}

	protected static boolean isBlacklisted(Class aClass) {
		Class[] unmarshableClasses = {};
//			ThemeDisplay.class, ServiceContext.class, SearchContext.class};

		if (aClass.getName().startsWith("javax.servlet")) {
			return true;
		}

		for (Class unmarshableClass: unmarshableClasses) {
			if (aClass.isAssignableFrom(unmarshableClass)) {
				return true;
			}
		}

		return false;
	}

	protected static boolean canJAXBHandle(Class returnType) {
		Class[] builtIn = {
			java.util.Map.class, java.util.List.class,
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

		Class[] xmlAdapters = {Locale.class};

		for (Class hasXmlAdapter : xmlAdapters) {
			if (returnType.equals(hasXmlAdapter)) {
				return true;
			}
		}

		return false;
	}

	private FieldDescriptor _parseField(Method method) {
		String methodName = method.getName();

		Class type = null;

		int index = 0;

		if ((methodName.length() > 3) && methodName.startsWith("get")) {
			if (method.getReturnType().equals(Void.class)) {
				return null;
			}

			if (method.getParameterTypes().length != 0) {
				return null;
			}

			if (Character.isLowerCase(methodName.charAt(3))) {
				return null;
			}

			type = method.getReturnType();
			index = 3;
		}
		else if ((methodName.length() > 3) && methodName.startsWith("set")) {
			if (method.getReturnType().equals(Void.class)) {
				return null;
			}

			if (method.getParameterTypes().length != 1) {
				return null;
			}

			if (Character.isLowerCase(methodName.charAt(3))) {
				return null;
			}

			type = method.getParameterTypes()[0];
			index = 3;
		}
		else if ((methodName.length() > 2) && methodName.startsWith("is")) {
			if (method.getReturnType().equals(Void.class)) {
				return null;
			}

			if (method.getParameterTypes().length != 0) {
				return null;
			}

			if (Character.isLowerCase(methodName.charAt(2))) {
				return null;
			}

			type = method.getReturnType();
			index = 2;
		}
		else {
			return null;
		}

		String result = "" + Character.toLowerCase(methodName.charAt(index));

		if (methodName.length() > (index)) {
			result += methodName.substring(index + 1);
		}

		return new FieldDescriptor(result, type);
	}

	private static boolean _hasDefaultConstructor(Class aClass) {
		for(Constructor constructor : aClass.getConstructors()) {
			if (constructor.getParameterTypes().length == 0) {
				return true;
			}
		}

		return false;
	}
}

class FieldDescriptor {
	private String fieldName;
	private Class fieldType;

	FieldDescriptor(String fieldName, Class fieldType) {
		this.fieldName = fieldName;
		this.fieldType = fieldType;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (!(o instanceof FieldDescriptor)) return false;

		FieldDescriptor that = (FieldDescriptor) o;

		if (!fieldName.equals(that.fieldName)) return false;
		if (!fieldType.equals(that.fieldType)) return false;

		return true;
	}

	@Override
	public int hashCode() {
		int result = fieldName.hashCode();
		result = 31 * result + fieldType.hashCode();
		return result;
	}

	public String getFieldName() {
		return fieldName;
	}

	public void setFieldName(String fieldName) {
		this.fieldName = fieldName;
	}

	public Class getFieldType() {
		return fieldType;
	}

	public void setFieldType(Class fieldType) {
		this.fieldType = fieldType;
	}

	@Override
	public String toString() {
		return "FieldDescriptor{" +
			"fieldName='" + fieldName + '\'' +
			", fieldType=" + fieldType +
			'}';
	}
}
