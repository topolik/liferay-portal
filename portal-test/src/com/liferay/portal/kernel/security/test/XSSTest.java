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

import com.liferay.portal.kernel.security.xss.EscapeOperations;
import com.liferay.portal.kernel.security.xss.XSS;
import com.liferay.portal.kernel.util.HtmlUtil;
import com.liferay.portal.util.HtmlImpl;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.io.ByteArrayOutputStream;
import java.io.PrintWriter;

/**
 * @author Carlos Sierra Andrés
 */
public class XSSTest {

	@Before
	public void setup() {
		new HtmlUtil().setHtml(new HtmlImpl());
	}

	@Test
	public void superSimpleTestToIllustrateDoubleEscaping() {
		String original = "<a href=\"http://google.com\"> This is an XSS </a>";

		XSS.SafeHtml safeHtml = XSS.htmlBody(XSS.attribute(original));

		Assert.assertEquals(
			safeHtml.toString(), HtmlUtil.escape(original));
	}

	@Test
	public void testToIllustrateDoubleEscapingThatHappensInTaglibs() {
		String articleName = " ' onclick=alert(1)";

		XSS.SafeHtml escapedTooEarly_forExampleByAutoEscapeBeanHandler_beforePassingToTaglib
			= XSS.htmlBody(articleName);

		CharSequence labelForATag = escapedTooEarly_forExampleByAutoEscapeBeanHandler_beforePassingToTaglib;

		/*
		*
		* Before the planned changes in taglib
		*
		 */

		String outputSentToBrowser =
			taglibCallBeforeOurChanges(labelForATag.toString());

		// => make sure it's safe
		Assert.assertTrue(!outputSentToBrowser.contains("'"));

		// => but test double escaping -> escaped '&' char
		Assert.assertTrue(!outputSentToBrowser.contains(HtmlUtil.escape("&")));
		Assert.assertTrue(!outputSentToBrowser.contains(HtmlUtil.escapeAttribute("&")));

		/*
		*
		* After the planned changes in taglib
		*
		 */
		outputSentToBrowser =
			taglibCallAfterOurChanges(labelForATag);

		// => make sure it's safe
		Assert.assertTrue(!outputSentToBrowser.contains("'"));

		// => but test double escaping -> escaped '&' char
		Assert.assertTrue(!outputSentToBrowser.contains(HtmlUtil.escape("&")));
		Assert.assertTrue(!outputSentToBrowser.contains(HtmlUtil.escapeAttribute("&")));
	}

	protected String taglibCallBeforeOurChanges(String label) {
		PrintWriter jspWriter = new PrintWriter(new ByteArrayOutputStream());

		jspWriter.print("<a title='");
		jspWriter.print(label);
		jspWriter.print("'>");

		// =>

		String outputSentToBrowser = label;
		return outputSentToBrowser;
	}

	protected String taglibCallAfterOurChanges(CharSequence label) {
		PrintWriter jspWriter = new PrintWriter(new ByteArrayOutputStream());

		jspWriter.print("<a title='");
		// after the change we want to make sure the output is always correctly escaped
		jspWriter.print(XSS.attribute(label));
		jspWriter.print("'>");

		// =>

		CharSequence outputSendToBrowser = XSS.attribute(label);
		return outputSendToBrowser.toString();
	}

	@Test
	public void testx(){
		System.out.println(HtmlUtil.escapeAttribute("javascript:alert(1)"));
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
	public void testOrderIsConserved() {
		String original = "<a href=\"http://google.com\"> This is an XSS </a>";

		XSS.EscapedString safeHtml = XSS.js(original).apply(
			EscapeOperations.ATTRIBUTE);

		Assert.assertEquals(
			safeHtml.toString(),
			HtmlUtil.escapeAttribute(HtmlUtil.escapeJS(original)));

		safeHtml = XSS.attribute(original).apply(EscapeOperations.JS);

		Assert.assertEquals(
			safeHtml.toString(),
			HtmlUtil.escapeJS(HtmlUtil.escapeAttribute(original)));
	}

	@Test
	public void testAvoidDoubleEscapingAndOrderIsConserved() {
		String original = "<a href=\"http://google.com\"> This is an XSS </a>";

		XSS.EscapedString safeHtml =
			XSS.js(original).apply(
				EscapeOperations.ATTRIBUTE, EscapeOperations.JS,
				EscapeOperations.ATTRIBUTE);

		Assert.assertEquals(
			safeHtml.toString(),
			HtmlUtil.escapeAttribute(HtmlUtil.escapeJS(original)));

		safeHtml =
			XSS.attribute(original).apply(
				EscapeOperations.JS, EscapeOperations.ATTRIBUTE,
				EscapeOperations.JS);

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
