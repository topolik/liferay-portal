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

package com.liferay.portal.service.remote;

import java.util.Date;
import java.util.List;

/**
 * @author Tomas Polesovsky
 */
public class JaxWsServiceObjectImpl implements JAXWSServiceObject{
	private String stringParam;
	private Date dateParam;
	private int intParam;
	private List<JAXWSServiceObject> children;

	public JaxWsServiceObjectImpl(List<JAXWSServiceObject> children) {
		this.children = children;
	}

	public String getStringParam() {
		return stringParam;
	}

	public void setStringParam(String stringParam) {
		this.stringParam = stringParam;
	}

	public Date getDateParam() {
		return dateParam;
	}

	public void setDateParam(Date dateParam) {
		this.dateParam = dateParam;
	}

	public int getIntParam() {
		return intParam;
	}

	public void setIntParam(int intParam) {
		this.intParam = intParam;
	}

	@Override
	public List<JAXWSServiceObject> getChildren() {
		return children;
	}

	@Override
	public String toString() {
		return "JAXWSServiceObject{" +
			"stringParam='" + stringParam + '\'' +
			", dateParam=" + dateParam +
			", intParam=" + intParam +
			'}';
	}
}
