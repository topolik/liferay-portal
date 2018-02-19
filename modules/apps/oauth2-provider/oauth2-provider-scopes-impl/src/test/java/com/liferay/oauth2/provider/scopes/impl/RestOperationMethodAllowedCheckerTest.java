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

import static junit.framework.TestCase.assertTrue;

import javax.ws.rs.GET;
import javax.ws.rs.PUT;

import com.liferay.oauth2.provider.scopes.impl.methodallowedchecker.RestOperationMethodAllowedChecker;
import com.liferay.oauth2.provider.scopes.spi.MethodAllowedChecker;
import org.junit.Test;

public class RestOperationMethodAllowedCheckerTest {

	@Test
	public void testisAllowed() throws NoSuchMethodException {
		TestScopeChecker testScopeChecker = new TestScopeChecker(
			"everything.readonly");

		MethodAllowedChecker methodAllowedChecker =
			new RestOperationMethodAllowedChecker(testScopeChecker);

		assertTrue(
			methodAllowedChecker.isAllowed(
				EndpointSample.class.getMethod("hello")));
	}

	@Test
	public void testMethodAllowed() throws NoSuchMethodException {
		TestScopeChecker testScopeChecker = new TestScopeChecker(
			"everything");

		MethodAllowedChecker methodAllowedChecker =
			new RestOperationMethodAllowedChecker(testScopeChecker);

		assertTrue(
			methodAllowedChecker.isAllowed(
				EndpointSample.class.getMethod("modify")));
	}

	private static class EndpointSample {

		@GET
		public String hello() {
			return "hello";
		}

		@PUT
		public void modify() {

		}

	}



}