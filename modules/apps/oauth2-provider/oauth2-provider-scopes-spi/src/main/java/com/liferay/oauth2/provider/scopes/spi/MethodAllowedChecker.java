package com.liferay.oauth2.provider.scopes.spi;

import java.lang.reflect.Method;

public interface MethodAllowedChecker {

	public boolean isAllowed(Method method);

	default MethodAllowedChecker and(MethodAllowedChecker other) {
		return method -> isAllowed(method) && other.isAllowed(method);
	}

	default MethodAllowedChecker or(MethodAllowedChecker other) {
		return method -> isAllowed(method) || other.isAllowed(method);
	}

}