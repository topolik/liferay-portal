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

import com.liferay.oauth2.provider.scopes.impl.prefixhandler.DefaultPrefixHandlerFactory;
import com.liferay.oauth2.provider.scopes.impl.scopematcher.ChunkScopeMatcherFactory;
import com.liferay.oauth2.provider.scopes.spi.ScopeMatcher;
import com.liferay.oauth2.provider.scopes.spi.ScopeMatcherFactory;
import com.liferay.oauth2.provider.scopes.spi.PrefixHandler;
import com.liferay.portal.kernel.util.StringPool;
import org.junit.Test;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class ChunkScopeMatcherFactoryTest {

	@Test
	public void testMatch() throws Exception {
		ScopeMatcherFactory chunkScopeMatcherFactory =
			new ChunkScopeMatcherFactory();

		ScopeMatcher scopeMatcher =
			chunkScopeMatcherFactory.create("everything.readonly");

		assertTrue(scopeMatcher.match("everything.readonly"));
		assertFalse(scopeMatcher.match("everything"));
	}

	@Test
	public void testMatch2() throws Exception {
		ScopeMatcherFactory chunkScopeMatcherFactory =
			new ChunkScopeMatcherFactory();

		ScopeMatcher scopeMatcher =
			chunkScopeMatcherFactory.create("everything");

		assertTrue(scopeMatcher.match("everything.readonly"));
		assertTrue(scopeMatcher.match("everything"));
	}

	@Test
	public void testMatchMatchesWholeChunksOnly() throws Exception {
		ScopeMatcherFactory chunkScopeMatcherFactory =
			new ChunkScopeMatcherFactory();

		ScopeMatcher scopeMatcher =
			chunkScopeMatcherFactory.create("everything");

		assertFalse(scopeMatcher.match("everything2.readonly"));
		assertFalse(scopeMatcher.match("everything2"));
	}

	@Test
	public void testMatchWithNamespaces() throws Exception {
		ScopeMatcherFactory chunkScopeMatcherFactory =
			new ChunkScopeMatcherFactory();

		ScopeMatcher scopeMatcher =
			chunkScopeMatcherFactory.create("test_everything");

		PrefixHandler namespaceAdder =
			new DefaultPrefixHandlerFactory().create("test");

		scopeMatcher = scopeMatcher.prepend(namespaceAdder);

		assertTrue(scopeMatcher.match("everything.readonly"));
		assertTrue(scopeMatcher.match("everything"));
	}

	@Test
	public void testMatchWithNamespacesMatchingChunksAndEmpty()
		throws Exception {

		ScopeMatcherFactory chunkScopeMatcherFactory =
			new ChunkScopeMatcherFactory();

		ScopeMatcher scopeMatcher = chunkScopeMatcherFactory.create("test.");

		PrefixHandler namespaceAdder = localName -> "test." + localName;

		scopeMatcher = scopeMatcher.prepend(namespaceAdder);

		assertFalse(scopeMatcher.match("everything.readonly"));
		assertFalse(scopeMatcher.match("everything"));

		assertFalse(scopeMatcher.match("test.everything.readonly"));
		assertFalse(scopeMatcher.match("test.everything"));
	}

	@Test
	public void testMatchWithNamespacesMatchingChunks() throws Exception {
		ScopeMatcherFactory chunkScopeMatcherFactory =
			new ChunkScopeMatcherFactory();

		ScopeMatcher scopeMatcher =
			chunkScopeMatcherFactory.create("test.everything");

		PrefixHandler namespaceAdder = localName -> "test." + localName;

		scopeMatcher = scopeMatcher.prepend(namespaceAdder);

		assertTrue(scopeMatcher.match("everything.readonly"));
		assertTrue(scopeMatcher.match("everything"));

		assertFalse(scopeMatcher.match("test.everything.readonly"));
		assertFalse(scopeMatcher.match("test.everything"));
	}

	@Test
	public void testMatchWithNamespacesAndMapperMatchingChunks()
		throws Exception {

		ScopeMatcherFactory chunkScopeMatcherFactory =
			new ChunkScopeMatcherFactory();

		ScopeMatcher scopeMatcher = chunkScopeMatcherFactory.create(
			"test.everything");

		PrefixHandler namespaceAdder = localName -> "test." + localName;

		scopeMatcher = scopeMatcher.prepend(namespaceAdder);

		scopeMatcher = scopeMatcher.withMapper(s -> {
			switch (s) {
				case "RW": return "everything";
				case "RO": return "everything.readonly";
			}

			return s;
		});

		assertTrue(scopeMatcher.match("RO"));
		assertTrue(scopeMatcher.match("RW"));

		assertTrue(scopeMatcher.match("everything.readonly"));
		assertTrue(scopeMatcher.match("everything"));
	}

}
