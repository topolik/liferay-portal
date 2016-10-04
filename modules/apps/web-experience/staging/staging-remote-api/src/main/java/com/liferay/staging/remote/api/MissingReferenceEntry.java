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

import java.util.Map;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * @author Carlos Sierra Andrés
 */
@XmlRootElement
public class MissingReferenceEntry {

	public MissingReferenceEntry() {
		_key = null;
		_missingReference = null;
	}

	public MissingReferenceEntry(
		Map.Entry<String, MissingReference> stringMissingReferenceEntry) {

		_key = stringMissingReferenceEntry.getKey();
		_missingReference = new MissingReferenceRepr(
			stringMissingReferenceEntry.getValue());
	}

	public String getKey() {
		return _key;
	}

	@XmlElement
	public MissingReferenceRepr getMissingReferenceRepr() {
		return _missingReference;
	}

	private final String _key;
	private final MissingReferenceRepr _missingReference;

}