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

package com.liferay.portal.tools.xss;

import com.liferay.portal.kernel.util.CharPool;
import com.liferay.portal.kernel.util.FileUtil;
import com.liferay.portal.kernel.util.Randomizer;
import com.liferay.portal.kernel.util.StringBundler;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.util.FileImpl;

import java.io.File;
import java.io.IOException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * @author Tomas Polesovsky
 */
public class InlineScriptNormalizer {

	public static void main(String[] args) throws IOException {
		String jspDir = System.getProperty("jspDir");
		if(Validator.isNull(jspDir)){
			System.out.println("System property jspDir is null");
			System.exit(1);
		}

		InlineScriptNormalizer inlineScriptNormalizer = new InlineScriptNormalizer();
		List<File> jsps = new ArrayList<File>();

		// get all JSPs
		inlineScriptNormalizer.listJSPFiles(new File(jspDir), jsps);

		// init environment
		new FileUtil().setFile(new FileImpl());

		// read files and normalize them
		for(File jspFile : jsps){
			try{
				String fileContent = FileUtil.read(jspFile);
				String newContent =
					inlineScriptNormalizer.normalize(fileContent);

				if(newContent.length() != fileContent.length()){
					System.out.println("Modifying: " + jspFile);
					FileUtil.write(jspFile, newContent);
				}
			} catch (ParseException e){
				System.out.println("Unable to parse " + jspFile);
				e.printStackTrace(System.out);
			}
		}
	}

	protected void listJSPFiles(File entry, List<File> result) {
		if (entry.isDirectory()) {
			String[] children = entry.list();
			for (int i=0; i<children.length; i++) {
				listJSPFiles(new File(entry, children[i]), result);
			}
		}
		else if(entry.getName().endsWith(".jsp")){
			result.add(entry);
		}
	}

	public String normalize(String jspContent) throws ParseException {
		// perform matches on a lowercase content but replace original one
		String lowerCaseContent = jspContent.toLowerCase();
		StringBundler output = new StringBundler();
		StringBundler inlineScriptOutput = new StringBundler();

		int lastContentPos = 0;
		int newLastContentPos = 0;
		boolean modified = false;

		// go through the file
		while(true){
			int foundAtPos = lowerCaseContent.indexOf(
				_EVENT_PREFIX, lastContentPos);

			if(foundAtPos < 0){
				break;
			}

			// parse possible event attribute
			int eventAttrEnd = lowerCaseContent.indexOf(
				CharPool.EQUAL, foundAtPos);

			if(eventAttrEnd < 0){
				newLastContentPos = foundAtPos + _EVENT_PREFIX.length();
				output.append(jspContent.substring(lastContentPos, newLastContentPos));
				lastContentPos = newLastContentPos;
				continue;
			}

			String eventAttr = jspContent.substring(
				foundAtPos + 1, eventAttrEnd);

			String eventAttrNormalized = lowerCaseContent.substring(
				foundAtPos + 1, eventAttrEnd);

			// check if we process this attribute
			if(!_EVENT_ATTRS.contains(eventAttrNormalized)){
				newLastContentPos = foundAtPos + _EVENT_PREFIX.length();
				output.append(jspContent.substring(lastContentPos, newLastContentPos));
				lastContentPos = newLastContentPos;
				continue;
			}

			/*
			 * isolate content of the event attribute
			 */
			int startPos = foundAtPos + eventAttr.length() + 3;
			char quote = lowerCaseContent.charAt(startPos - 1);
			int endPos = lowerCaseContent.indexOf(quote, startPos);

			if(endPos < 0){
				newLastContentPos = foundAtPos + _EVENT_PREFIX.length();
				output.append(jspContent.substring(lastContentPos, newLastContentPos));
				lastContentPos = newLastContentPos;
				continue;
			}

			String attributeContent = jspContent.substring(startPos, endPos);

			/*
			 * If the content contains scriptlet, we must find the first quote
			 * after the scriptlet ends
			 */
			if(attributeContent.contains(_SCRIPTLET_START)){
				int scriptletEndPos = lowerCaseContent.indexOf(
					_SCRIPTLET_END, startPos);

				endPos = lowerCaseContent.indexOf(quote, scriptletEndPos);

				if(endPos < 0){
					newLastContentPos = foundAtPos + _EVENT_PREFIX.length();
					output.append(jspContent.substring(
						lastContentPos, newLastContentPos));

					lastContentPos = newLastContentPos;
					continue;
				}

				attributeContent = jspContent.substring(startPos, endPos);
			}

			/*
			 * We need to verify that we are inside JSP, not inside scriptlet
			 */

			int nextScriptletStart = lowerCaseContent.indexOf(_SCRIPTLET_START, endPos);
			int nextScriptletEnd = lowerCaseContent.indexOf(_SCRIPTLET_END, endPos);
			if(nextScriptletEnd > 0 && nextScriptletEnd < nextScriptletStart){
				throw new ParseException("Cannot process " + eventAttr + "="+quote+attributeContent+quote, startPos);
			}

			// set end mark for modified chunk
			newLastContentPos = endPos + 1;

			/*
			 * Write to output buffer
			 * 1, unprocessed content
			 * 2, convert inline JavaScript for a placeholder inside <script> tag
			 */
			output.append(jspContent.substring(lastContentPos, foundAtPos));
			// generate name for the placeholder
			String eventId =
				eventAttr + Math.abs(Randomizer.getInstance().nextInt());

			output.append(" "+eventAttr+"='"+eventId+"()' ");
			inlineScriptOutput.append(
				"registerEvent('"+eventAttr+"', '"+eventId+"', "+
					quote+attributeContent+quote+");\n");

			lastContentPos = newLastContentPos;
			modified = true;
		}

		// nothing has been found
		if(!modified){
			return jspContent;
		}

		/*
		 * Write the rest of jspContent into buffer and append inline scripts
		 */
		output.append(jspContent.substring(lastContentPos));
		output.append("<script type=\"text/javascript\">\n");

		output.append("function registerEvent(event, funcName, funcContent){" +
			"\neval('window.'+funcName+'=function(){'+funcContent+'}');\n};\n");

		output.append(inlineScriptOutput);
		output.append("</script>\n");

		return output.toString();
	}

	private static final String _EVENT_PREFIX = " on";

	private static final String _SCRIPTLET_START = "<%";
	private static final String _SCRIPTLET_END = "%>";

	private static final List<String> _EVENT_ATTRS = Arrays.asList(new String[]{
		"onafterprint", "onbeforeprint", "onbeforeunload", "onerror",
		"onhaschange", "onload", "onmessage", "onoffline", "ononline",
		"onpagehide", "onpageshow", "onpopstate", "onredo", "onresize",
		"onstorage", "onundo", "onunload", "onblur", "onchange",
		"oncontextmenu", "onfocus", "onformchange", "onforminput", "oninput",
		"oninvalid", "onreset", "onselect", "onsubmit", "onkeydown",
		"onkeypress", "onkeyup", "onclick", "ondblclick", "ondrag", "ondragend",
		"ondragenter", "ondragleave", "ondragover", "ondragstart", "ondrop",
		"onmousedown", "onmousemove", "onmouseout", "onmouseover", "onmouseup",
		"onmousewheel", "onscroll", "onabort", "oncanplay", "oncanplaythrough",
		"ondurationchange", "onemptied", "onended", "onerror", "onloadeddata",
		"onloadedmetadata", "onloadstart", "onpause", "onplay", "onplaying",
		"onprogress", "onratechange", "onreadystatechange", "onseeked",
		"onseeking", "onstalled", "onsuspend", "ontimeupdate", "onvolumechange",
		"onwaiting"});

}
