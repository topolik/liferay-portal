/**
 * Copyright (c) 2000-2012 Liferay, Inc. All rights reserved.
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

package com.liferay.taglib.security;

import com.liferay.portal.kernel.util.StringBundler;
import com.liferay.portal.kernel.util.WebKeys;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.tagext.BodyContent;
import javax.servlet.jsp.tagext.BodyTagSupport;
import java.io.IOException;

/**
 * @author Tomas Polesovsky
 */
public class XSSTag extends BodyTagSupport {

	@Override
	public int doEndTag() throws JspException {
		BodyContent bodyContent = getBodyContent();

		StringBundler buffer = (StringBundler) pageContext.getRequest().getAttribute(WebKeys.XSS_FILTER_BUFFER);
		String body = bodyContent.getString();

		if(buffer == null || !body.startsWith("<script")){
			try {
				bodyContent.getEnclosingWriter().write(body);
			} catch (IOException e) {
				throw new JspException(e);
			}

		}

		else {
			processScriptContent(body, buffer);
		}

		return EVAL_PAGE;
	}

	private void processScriptContent(String body, StringBundler buffer) {
		int startPos = body.indexOf(">") + 1;
		int endPos = body.indexOf("</script>");

		String scriptBody = body.substring(startPos, endPos);

		buffer.append(scriptBody);
		buffer.append("\n");
	}

}