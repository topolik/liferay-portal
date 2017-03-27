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

package com.liferay.portal.cacheutil.request.parameter.validators;

import com.liferay.portal.kernel.cache.RequestParameterCacheValidator;
import com.liferay.portal.kernel.model.Theme;
import com.liferay.portal.kernel.service.ThemeLocalService;
import com.liferay.portal.kernel.servlet.ServletRequestParameterReader;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Carlos Sierra Andrés
 */
@Component(immediate = true, property = "filter.request.parameter=themeId")
public class ThemeIdRequestParameterCacheValidator
	implements RequestParameterCacheValidator {

	@Override
	public boolean validate(
		String paramName,
		ServletRequestParameterReader servletRequestParameterReader) {

		long companyId = servletRequestParameterReader.getCompanyId();

		Theme theme = _themeLocalService.fetchTheme(
			companyId, servletRequestParameterReader.getParameter(paramName));

		if (theme != null) {
			return true;
		}

		return false;
	}

	@Reference
	private ThemeLocalService _themeLocalService;

}