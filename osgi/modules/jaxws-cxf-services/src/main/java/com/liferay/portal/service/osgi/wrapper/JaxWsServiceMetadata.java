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

package com.liferay.portal.service.osgi.wrapper;

import java.util.HashMap;

/**
 * @author Tomas Polesovsky
 */
public class JaxWsServiceMetadata {
	public Object getService() {
		return _service;
	}

	public void setService(Object service) {
		this._service = service;
	}

	public void setConfiguration(HashMap<String, Object> configuration) {
		this._configuration = configuration;
	}

	public HashMap<String, Object> getConfiguration() {
		return _configuration;
	}

	private Object _service;
	private HashMap<String, Object> _configuration;
}
