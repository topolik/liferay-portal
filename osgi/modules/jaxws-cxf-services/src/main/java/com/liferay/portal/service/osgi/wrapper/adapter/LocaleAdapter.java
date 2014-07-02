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

package com.liferay.portal.service.osgi.wrapper.adapter;

import javax.xml.bind.annotation.adapters.XmlAdapter;
import java.util.Locale;

/**
 * @author Tomas Polesovsky
 */
public class LocaleAdapter extends XmlAdapter<String, Locale> { {
}

	@Override
	public Locale unmarshal(String locale) throws Exception {
		if (locale == null || locale.length() == 0) {
			return Locale.getDefault();
		}

		String[] locales = locale.split("_");
		if (locales.length == 1) {
			return new Locale(locales[0]);
		}
		if (locales.length == 2) {
			return new Locale(locales[0], locales[1]);
		}
		if (locales.length == 3) {
			return new Locale(locales[0], locales[1], locales[2]);
		}

		throw new IllegalArgumentException(
			"Unable to unmarshall Locale " + locale);
	}

	@Override
	public String marshal(Locale locale) throws Exception {
		if (locale == null) {
			return "";
		}

		return locale.toString();
	}
}