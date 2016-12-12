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

package com.liferay.portal.kernel.security.test;

import com.liferay.portal.kernel.security.xss.XSS;
import com.liferay.portal.kernel.util.HtmlUtil;
import com.liferay.portal.util.HtmlImpl;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

/**
 * @author Carlos Sierra Andrés
 */
public class XSSTest {

	@Before
	public void setup() {
		new HtmlUtil().setHtml(new HtmlImpl());
	}

	@Test
	public void test1() {
		String original = "<a href=\"http://google.com\"> This is an XSS </a>";

		XSS.SafeHtml safeHtml = XSS.htmlBody(original);

		Assert.assertEquals(safeHtml.toString(), HtmlUtil.escape(original));
	}

	@Test
	public void avoidDoubleEscaping() {
		String original = "<a href=\"http://google.com\"> This is an XSS </a>";

		XSS.SafeHtml safeHtml = XSS.htmlBody(XSS.htmlBody(original));

		Assert.assertEquals(
			safeHtml.toString(), HtmlUtil.escape(original));
	}

	@Test
	public void testOrderIsStable() {
		String original = "<a href=\"http://google.com\"> This is an XSS </a>";

		XSS.EscapedString safeHtml = XSS.attribute(XSS.js(original));

		Assert.assertEquals(
			safeHtml.toString(),
			HtmlUtil.escapeJS(HtmlUtil.escapeAttribute(original)));

		safeHtml = XSS.js(XSS.attribute(original));

		Assert.assertEquals(
			safeHtml.toString(),
			HtmlUtil.escapeJS(HtmlUtil.escapeAttribute(original)));
	}

	@Test
	public void testAvoidDoubleEscapingAndOrderIsStable() {
		String original = "<a href=\"http://google.com\"> This is an XSS </a>";

		XSS.EscapedString safeHtml = XSS.attribute(
			XSS.js(XSS.attribute(XSS.js(original))));

		Assert.assertEquals(
			safeHtml.toString(),
			HtmlUtil.escapeJS(HtmlUtil.escapeAttribute(original)));

		safeHtml = XSS.js(XSS.attribute(XSS.js(XSS.attribute(original))));

		Assert.assertEquals(
			safeHtml.toString(),
			HtmlUtil.escapeJS(HtmlUtil.escapeAttribute(original)));
	}

	@Test
	public void testFunctionMapping() {
		String original = "<a href=\"http://google.com\"> This is an XSS </a>";

		XSS.EscapedString safeHtml = XSS.safeHtml(original);

		safeHtml.map(s -> s + "<anotherTag />");

		Assert.assertEquals(
			safeHtml.toString(), HtmlUtil.escape(original + "<anotherTag />"));
	}

	@Test
	public void testFunctionMapping2() {
		String original = "<a href=\"http://google.com\"> This is an XSS </a>";

		XSS.EscapedString safeHtml = XSS.safeHtml(original);

		safeHtml.map(s -> s + "<anotherTag />");

		Assert.assertEquals(
			safeHtml.toString(), HtmlUtil.escape(original + "<anotherTag />"));

		safeHtml.map(s -> s + "<yetAnotherTag />");

		Assert.assertEquals(
			safeHtml.toString(),
			HtmlUtil.escape(original + "<anotherTag /><yetAnotherTag />"));
	}
	
}
