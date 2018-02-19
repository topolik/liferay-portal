/**
 * Copyright (c) 2000-present Liferay, Inc. All rights reserved.
 * <p>
 * This library is free software; you can redistribute it and/or modify it under
 * the terms of the GNU Lesser General Public License as published by the Free
 * Software Foundation; either version 2.1 of the License, or (at your option)
 * any later version.
 * <p>
 * This library is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more
 * details.
 */

package com.liferay.oauth2.provider.scopes.impl;

import com.liferay.oauth2.provider.scopes.api.RequiresScope;
import com.liferay.oauth2.provider.scopes.impl.methodallowedchecker.AnnotationMethodAllowedChecker;
import com.liferay.oauth2.provider.scopes.spi.MethodAllowedChecker;
import org.junit.Test;

import static junit.framework.TestCase.assertFalse;
import static junit.framework.TestCase.assertTrue;

public class AnnotationMethodAllowedCheckerTest {

	@Test
	public void testisAllowed() throws NoSuchMethodException {
		TestScopeChecker testScopeChecker = new TestScopeChecker("READ");

		MethodAllowedChecker methodAllowedChecker =
			new AnnotationMethodAllowedChecker(testScopeChecker);

		assertTrue(
			methodAllowedChecker.isAllowed(
				TestEndpointSample.class.getMethod("hello")));
	}

	@Test
	public void testMethodAllowed() throws NoSuchMethodException {
		TestScopeChecker testScopeChecker = new TestScopeChecker("WRITE");

		MethodAllowedChecker methodAllowedChecker =
			new AnnotationMethodAllowedChecker(testScopeChecker);

		assertTrue(
			methodAllowedChecker.isAllowed(
				TestEndpointSample.class.getMethod("modify")));
	}

	@Test
	public void testMethodAllowedWithMultipleAllRequired()
		throws NoSuchMethodException {

		TestScopeChecker testScopeChecker = new TestScopeChecker(
			"READ", "WRITE");

		MethodAllowedChecker methodAllowedChecker =
			new AnnotationMethodAllowedChecker(testScopeChecker);

		assertTrue(
			methodAllowedChecker.isAllowed(
				TestEndpointSample.class.getMethod("requiresAll")));
	}

	@Test
	public void testMethodAllowedWithMultipleSomeRequired()
		throws NoSuchMethodException {

		TestScopeChecker testScopeChecker = new TestScopeChecker("READ");

		MethodAllowedChecker methodAllowedChecker =
			new AnnotationMethodAllowedChecker(testScopeChecker);

		assertTrue(
			methodAllowedChecker.isAllowed(
				TestEndpointSample.class.getMethod("requiresAny")));

		testScopeChecker = new TestScopeChecker("WRITE");

		methodAllowedChecker =
			new AnnotationMethodAllowedChecker(testScopeChecker);

		assertTrue(
			methodAllowedChecker.isAllowed(
				TestEndpointSample.class.getMethod("requiresAny")));

		testScopeChecker = new TestScopeChecker("RANDOM");

		methodAllowedChecker =
			new AnnotationMethodAllowedChecker(testScopeChecker);

		assertFalse(
			methodAllowedChecker.isAllowed(
				TestEndpointSample.class.getMethod("requiresAny")));
	}

	@Test
	public void testMethodAllowedWithMultipleAllRequiredAndNotGranted()
		throws NoSuchMethodException {

		TestScopeChecker testScopeChecker = new TestScopeChecker(
			"READ", "WRITE");

		MethodAllowedChecker methodAllowedChecker =
			new AnnotationMethodAllowedChecker(testScopeChecker);

		assertFalse(
			methodAllowedChecker.isAllowed(
				TestEndpointSample.class.getMethod("requiresTooMany")));
	}

	private static class TestEndpointSample {

		@RequiresScope("READ")
		public String hello() {
			return "hello";
		}

		@RequiresScope("WRITE")
		public void modify() {

		}

		@RequiresScope({"READ", "WRITE"})
		public void requiresAll() {

		}

		@RequiresScope({"READ", "WRITE", "NOTGRANTED"})
		public void requiresTooMany() {

		}

		@RequiresScope(
			value = {"READ", "WRITE"},
			allNeeded = false)
		public void requiresAny() {

		}
	}



}