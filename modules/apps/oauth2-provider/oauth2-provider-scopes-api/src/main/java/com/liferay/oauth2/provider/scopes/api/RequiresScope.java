package com.liferay.oauth2.provider.scopes.api;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Retention(RetentionPolicy.RUNTIME)
public @interface RequiresScope {

	String[] value();

	boolean allNeeded() default true;

}