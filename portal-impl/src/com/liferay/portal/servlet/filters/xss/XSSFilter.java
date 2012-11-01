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

package com.liferay.portal.servlet.filters.xss;

import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.servlet.BufferCacheServletResponse;
import com.liferay.portal.kernel.servlet.ServletResponseUtil;
import com.liferay.portal.kernel.util.ContentTypes;
import com.liferay.portal.kernel.util.HttpUtil;
import com.liferay.portal.kernel.util.Randomizer;
import com.liferay.portal.kernel.util.StringBundler;
import com.liferay.portal.kernel.util.StringPool;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.servlet.filters.BasePortalFilter;
import com.liferay.portal.util.WebKeys;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author Tomas Polesovsky
 */
public class XSSFilter extends BasePortalFilter {

	public static final String SKIP_FILTER =
		XSSFilter.class.getName() + "SKIP_FILTER";

	@Override
	public void init(FilterConfig filterConfig) {
		super.init(filterConfig);
	}

	@Override
	public boolean isFilterEnabled(
		HttpServletRequest request, HttpServletResponse response) {

		if((request.getParameter("csp") == null) &&
			(request.getParameter("inlineScript") == null)){

			return false;
		}

		if (isAlreadyFiltered(request)) {
			return false;
		}
		else {
			return true;
		}
	}

	protected String getContent(HttpServletRequest request, String content) {

		// TODO: implement
		StringBundler sb = (StringBundler) request.getAttribute(WebKeys.XSS_FILTER_BUFFER);

		if(sb.length() == 0){
			return content;
		}

		TimedBuffer timedBuffer = new TimedBuffer();
		timedBuffer.setBuffer(sb);
		timedBuffer.setTimeStamp(System.currentTimeMillis());

		int key = 0;

		synchronized (_randomizer){
			key = _randomizer.nextInt();
		}

		_buffers.put(String.valueOf(key), timedBuffer);

		content = content.replace("</html>", "<script type=\"text/javascript\" src=\"?inlineScript="+key+"\"></script></html>");
		return content;
	}

	protected boolean isAlreadyFiltered(HttpServletRequest request) {
		if (request.getAttribute(SKIP_FILTER) != null) {
			return true;
		}
		else {
			return false;
		}
	}

	@Override
	protected void processFilter(
		HttpServletRequest request, HttpServletResponse response,
		FilterChain filterChain)
		throws Exception {

		String inlineScript = request.getParameter("inlineScript");
		if(inlineScript != null){
			response.setContentType(ContentTypes.TEXT_JAVASCRIPT);

			if(_buffers.containsKey(inlineScript)){
				TimedBuffer timedBuffer = _buffers.remove(inlineScript);

				ServletResponseUtil.write(
					response, timedBuffer.getBuffer().toString());

			} else {
				_log.warn("Stale cache for " + inlineScript);
			}

			return;
		}

		request.setAttribute(SKIP_FILTER, Boolean.TRUE);

		request.setAttribute(WebKeys.XSS_FILTER_BUFFER, new StringBundler());

		if (_log.isDebugEnabled()) {
			String completeURL = HttpUtil.getCompleteURL(request);

			_log.debug("Filtering XSS for " + completeURL);
		}

		BufferCacheServletResponse bufferCacheServletResponse =
			new BufferCacheServletResponse(response);

		processFilter(
			XSSFilter.class, request, bufferCacheServletResponse,
			filterChain);

		String content = bufferCacheServletResponse.getString();

		String contentType = response.getContentType();

		if ((contentType != null) &&
			contentType.startsWith(ContentTypes.TEXT_HTML)) {

			content = getContent(request, content);
		}

		ServletResponseUtil.write(response, content);
	}

	private static Map<String, TimedBuffer> _buffers = new ConcurrentHashMap<String, TimedBuffer>();

	private static Log _log = LogFactoryUtil.getLog(XSSFilter.class);

	private static Randomizer _randomizer = Randomizer.getInstance();

	private class TimedBuffer {
		public long getTimeStamp() {
			return _timeStamp;
		}

		public void setTimeStamp(long _timeStamp) {
			this._timeStamp = _timeStamp;
		}

		public StringBundler getBuffer() {
			return _buffer;
		}

		public void setBuffer(StringBundler _buffer) {
			this._buffer = _buffer;
		}

		long _timeStamp;
		StringBundler _buffer;
	}
}
