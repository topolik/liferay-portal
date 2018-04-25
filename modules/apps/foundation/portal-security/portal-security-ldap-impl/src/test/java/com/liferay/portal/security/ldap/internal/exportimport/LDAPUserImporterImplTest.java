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

package com.liferay.portal.security.ldap.internal.exportimport;

import javax.naming.InvalidNameException;

import org.junit.Assert;
import org.junit.Test;

/**
 * @author Jorge Díaz
 */
public class LDAPUserImporterImplTest {

	@Test
	public void testCleanupLdapName() throws InvalidNameException {
		Assert.assertEquals(
			"cn=test test,ou=test", cleanupLdapName("cn=test\\20test,ou=test"));
		Assert.assertEquals(
			"cn=test\\\"test,ou=test",
			cleanupLdapName("cn=test\\22test,ou=test"));
		Assert.assertEquals(
			"cn=test\\#test,ou=test",
			cleanupLdapName("cn=test\\23test,ou=test"));
		Assert.assertEquals(
			"cn=test\\+test,ou=test",
			cleanupLdapName("cn=test\\2Btest,ou=test"));
		Assert.assertEquals(
			"cn=test\\+test,ou=test",
			cleanupLdapName("cn=test\\2btest,ou=test"));
		Assert.assertEquals(
			"cn=test\\,test,ou=test",
			cleanupLdapName("cn=test\\2Ctest,ou=test"));
		Assert.assertEquals(
			"cn=test\\,test,ou=test",
			cleanupLdapName("cn=test\\2ctest,ou=test"));
		Assert.assertEquals(
			"cn=test\\;test,ou=test",
			cleanupLdapName("cn=test\\3Btest,ou=test"));
		Assert.assertEquals(
			"cn=test\\;test,ou=test",
			cleanupLdapName("cn=test\\3btest,ou=test"));
		Assert.assertEquals(
			"cn=test\\<test,ou=test",
			cleanupLdapName("cn=test\\3Ctest,ou=test"));
		Assert.assertEquals(
			"cn=test\\<test,ou=test",
			cleanupLdapName("cn=test\\3ctest,ou=test"));
		Assert.assertEquals(
			"cn=test\\=test,ou=test",
			cleanupLdapName("cn=test\\3Dtest,ou=test"));
		Assert.assertEquals(
			"cn=test\\=test,ou=test",
			cleanupLdapName("cn=test\\3dtest,ou=test"));
		Assert.assertEquals(
			"cn=test\\>test,ou=test",
			cleanupLdapName("cn=test\\3Etest,ou=test"));
		Assert.assertEquals(
			"cn=test\\>test,ou=test",
			cleanupLdapName("cn=test\\3etest,ou=test"));
		Assert.assertEquals(
			"cn=test\\\\test,ou=test",
			cleanupLdapName("cn=test\\5Ctest,ou=test"));
		Assert.assertEquals(
			"cn=test\\\\test,ou=test",
			cleanupLdapName("cn=test\\5ctest,ou=test"));
	}

	@Test
	public void testEscapeValue() {
		Assert.assertEquals("test\\\\ test", escapeValue("test\\ test"));
		Assert.assertEquals("test\\\\\"test", escapeValue("test\\\"test"));
		Assert.assertEquals("test\\\\#test", escapeValue("test\\#test"));
		Assert.assertEquals("test\\\\+test", escapeValue("test\\+test"));
		Assert.assertEquals("test\\\\,test", escapeValue("test\\,test"));
		Assert.assertEquals("test\\\\;test", escapeValue("test\\;test"));
		Assert.assertEquals("test\\\\<test", escapeValue("test\\<test"));
		Assert.assertEquals("test\\\\=test", escapeValue("test\\=test"));
		Assert.assertEquals("test\\\\>test", escapeValue("test\\>test"));
		Assert.assertEquals("test\\\\\\\\test", escapeValue("test\\\\test"));
	}

	protected String cleanupLdapName(String name) throws InvalidNameException {
		return _ldapUserImporterImpl.cleanupLdapName(name);
	}

	protected String escapeValue(String query) {
		return _ldapUserImporterImpl.escapeValue(query);
	}

	private static final LDAPUserImporterImpl _ldapUserImporterImpl =
		new LDAPUserImporterImpl();

}