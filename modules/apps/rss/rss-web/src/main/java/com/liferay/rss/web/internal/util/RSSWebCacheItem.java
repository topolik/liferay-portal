/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.rss.web.internal.util;

import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.util.Http;
import com.liferay.portal.kernel.util.HttpUtil;
import com.liferay.portal.kernel.util.InetAddressUtil;
import com.liferay.portal.kernel.util.PropsValues;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.kernel.util.Time;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.kernel.webcache.WebCacheException;
import com.liferay.portal.kernel.webcache.WebCacheItem;
import com.liferay.rss.web.internal.configuration.RSSWebCacheConfiguration;

import com.rometools.rome.io.SyndFeedInput;
import com.rometools.rome.io.XmlReader;

import java.io.IOException;
import java.io.InputStream;

import java.net.InetAddress;
import java.net.URI;
import java.net.UnknownHostException;

/**
 * @author Brian Wing Shun Chan
 */
public class RSSWebCacheItem implements WebCacheItem {

	public RSSWebCacheItem(
		RSSWebCacheConfiguration rssWebCacheConfiguration, String url) {

		_rssWebCacheConfiguration = rssWebCacheConfiguration;
		_url = url;
	}

	@Override
	public Object convert(String key) throws WebCacheException {

		// com.liferay.portal.kernel.util.HttpUtil will break the connection
		// if it spends more than 5 seconds looking up a location. However,
		// German umlauts do not get encoded correctly. This may be a bug
		// with commons-httpclient or with how FeedParser uses
		// java.io.Reader.

		// Use http://xml.newsisfree.com/feeds/29/629.xml and
		// http://test.domosoft.com/up/RSS to test if German umlauts show up
		// correctly.

		/*Reader reader = new StringReader(
			new String(HttpUtil.URLtoByteArray(_url)));

		channel = FeedParser.parse(builder, reader);*/

		SyndFeedInput input = new SyndFeedInput();

		try (InputStream inputStream = _readURL()) {
			return input.build(
				new XmlReader(inputStream, true, StringPool.UTF8));
		}
		catch (Exception exception) {
			throw new WebCacheException(
				_url + " " + exception.toString(), exception);
		}
	}

	@Override
	public long getRefreshTime() {
		return Time.MINUTE * _rssWebCacheConfiguration.feedTime();
	}

	private boolean _isValidURL(String url) {
		if (Validator.isBlank(url)) {
			if (_log.isWarnEnabled()) {
				_log.warn("Rejected RSS URL that is blank");
			}

			return false;
		}

		URI uri = null;

		try {
			uri = new URI(url);
		}
		catch (Exception exception) {
			if (_log.isWarnEnabled()) {
				_log.warn("Rejected malformed RSS URL: " + url);
			}

			return false;
		}

		String scheme = uri.getScheme();

		if ((scheme == null) ||
			(!StringUtil.equalsIgnoreCase(scheme, "http") &&
			 !StringUtil.equalsIgnoreCase(scheme, "https"))) {

			if (_log.isWarnEnabled()) {
				_log.warn("Rejected RSS URL with disallowed scheme: " + url);
			}

			return false;
		}

		String host = uri.getHost();

		if (Validator.isBlank(host)) {
			if (_log.isWarnEnabled()) {
				_log.warn("Rejected RSS URL without host: " + url);
			}

			return false;
		}

		try {
			for (InetAddress inetAddress : InetAddress.getAllByName(host)) {
				if (InetAddressUtil.isLocalInetAddress(inetAddress)) {
					if (_log.isWarnEnabled()) {
						_log.warn(
							"Rejected RSS URL resolving to a restricted " +
								"address: " + url);
					}

					return false;
				}
			}
		}
		catch (UnknownHostException unknownHostException) {
			if (_log.isWarnEnabled()) {
				_log.warn("Rejected RSS URL with unresolvable host: " + url);
			}

			return false;
		}

		return true;
	}

	private InputStream _readURL() throws IOException {
		if (!_isValidURL(_url)) {
			throw new IOException("Invalid RSS URL: " + _url);
		}

		Http.Options options = new Http.Options();

		options.setFollowRedirects(false);
		options.setLocation(_url);
		options.setTimeout(PropsValues.RSS_CONNECTION_TIMEOUT);

		return HttpUtil.URLtoInputStream(options);
	}

	private static final Log _log = LogFactoryUtil.getLog(
		RSSWebCacheItem.class);

	private final RSSWebCacheConfiguration _rssWebCacheConfiguration;
	private final String _url;

}
