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

import com.liferay.exportimport.kernel.lar.MissingReference;

import java.util.HashMap;
import java.util.Map;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * @author Carlos Sierra Andrés
 */
@XmlRootElement
public class MissingReferenceRepr {

	public MissingReferenceRepr() {
	}

	public MissingReferenceRepr(MissingReference value) {
		_className = value.getClassName();
		_classPK = value.getClassPK();
		_displayName = value.getDisplayName();
		_groupId = value.getGroupId();
		_referrerClassName = value.getReferrerClassName();
		_referrers = value.getReferrers();
		_type = value.getType();
	}

	public String getClassName() {
		return _className;
	}

	public String getClassPK() {
		return _classPK;
	}

	public String getDisplayName() {
		return _displayName;
	}

	public long getGroupId() {
		return _groupId;
	}

	public String getReferrerClassName() {
		return _referrerClassName;
	}

	@XmlElement
	public Map<String, String> getReferrers() {
		return _referrers;
	}

	public String getType() {
		return _type;
	}

	public void setClassName(String className) {
		_className = className;
	}

	public void setClassPK(String classPK) {
		_classPK = classPK;
	}

	public void setDisplayName(String displayName) {
		_displayName = displayName;
	}

	public void setGroupId(long groupId) {
		_groupId = groupId;
	}

	public void setReferrerClassName(String referrerClassName) {
		_referrerClassName = referrerClassName;
	}

	public void setReferrers(Map<String, String> referrers) {
		_referrers = referrers;
	}

	public void setType(String type) {
		_type = type;
	}

	private String _className;
	private String _classPK;
	private String _displayName;
	private long _groupId;
	private String _referrerClassName;
	private Map<String, String> _referrers = new HashMap<>();
	private String _type;

}