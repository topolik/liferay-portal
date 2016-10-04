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

package com.liferay.staging.remote.api;

import javax.xml.bind.annotation.XmlRootElement;

/**
 * @author Carlos Sierra Andrés
 */
@XmlRootElement
public class MissingLayoutRepr {

	public MissingLayoutRepr() {
	}

	public MissingLayoutRepr(
		long layoutId, String uuid, boolean privateLayout) {

		_layoutId = layoutId;
		_uuid = uuid;
		_privateLayout = privateLayout;
	}

	public long getLayoutId() {
		return _layoutId;
	}

	public String getUuid() {
		return _uuid;
	}

	public boolean isPrivateLayout() {
		return _privateLayout;
	}

	public void setLayoutId(long layoutId) {
		_layoutId = layoutId;
	}

	public void setPrivateLayout(boolean privateLayout) {
		_privateLayout = privateLayout;
	}

	public void setUuid(String uuid) {
		_uuid = uuid;
	}

	private long _layoutId;
	private boolean _privateLayout;
	private String _uuid;

}