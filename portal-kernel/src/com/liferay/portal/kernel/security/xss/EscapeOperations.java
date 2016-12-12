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

package com.liferay.portal.kernel.security.xss;

import com.liferay.portal.kernel.util.HtmlUtil;

import java.util.function.Function;

/**
 * @author Carlos Sierra Andrés
 */
public enum EscapeOperations implements EscapeOperation {

	HTML(0, HtmlUtil::escape),
	ATTRIBUTE(1, HtmlUtil::escapeAttribute),
	CSS(2, HtmlUtil::escapeCSS),
	HREF(3, HtmlUtil::escapeHREF),
	JS(4, HtmlUtil::escapeJS),
	JS_LINK(5, HtmlUtil::escapeJSLink),
	URL(6, HtmlUtil::escapeURL),
	XPATH(7, HtmlUtil::escapeXPath),
	XPATH_ATTRIBUTE(8, HtmlUtil::escapeXPathAttribute);

	private final int _precedence;
	private final Function<String, String> _f;

	EscapeOperations(int precedence, Function<String, String> f) {
		_precedence = precedence;
		_f = f;
	}

	@Override
	public int getPrecendence() {
		return _precedence;
	}

	@Override
	public String escape(String input) {
		return _f.apply(input);
	}

}
