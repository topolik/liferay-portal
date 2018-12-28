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

package com.liferay.portal.template.freemarker.internal;

import com.liferay.petra.string.StringBundler;

import freemarker.ext.beans.BeanModel;
import freemarker.ext.beans.BeansWrapper;
import freemarker.ext.beans.InvalidPropertyException;

import freemarker.template.TemplateModel;
import freemarker.template.TemplateModelException;

import java.util.List;

/**
 * @author Marta Medio
 */
public class LiferayFreeMarkerBeanModel extends BeanModel {

	public LiferayFreeMarkerBeanModel(Object object, BeansWrapper wrapper) {
		super(object, wrapper);
	}

	@Override
	public TemplateModel get(String key) throws TemplateModelException {
		if (key.startsWith("get")) {
			String property = key.substring(3);

			property =
				Character.toLowerCase(property.charAt(0)) +
					property.substring(1);

			if (_restrictedProperties.contains(property)) {
				throw new InvalidPropertyException(
					StringBundler.concat(
						"Forbbiden access to property ", key, " of ",
						object.getClass()));
			}
		}

		return super.get(key);
	}

	public void setRestrictedProperties(List<String> restrictedProperties) {
		_restrictedProperties = restrictedProperties;
	}

	private List<String> _restrictedProperties;

}