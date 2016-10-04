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

import com.liferay.exportimport.kernel.lar.MissingReferences;

import java.util.List;
import java.util.stream.Collectors;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * @author Carlos Sierra Andrés
 */
@XmlRootElement
public class MissingReferencesRepr {

	List<MissingReferenceEntry> _dependencyMissingReferences;
	List<MissingReferenceEntry> _weakMissingReferences;

	public MissingReferencesRepr(MissingReferences missingReferences) {

		_dependencyMissingReferences =
			missingReferences.getDependencyMissingReferences().entrySet().
				stream().map(MissingReferenceEntry::new).
				collect(Collectors.toList());
		_weakMissingReferences =
			missingReferences.getWeakMissingReferences().entrySet().stream().
				map(MissingReferenceEntry::new).collect(Collectors.toList());
	}

	public MissingReferencesRepr() {
	}

	@XmlElement
	public List<MissingReferenceEntry> getDependencyMissingReferences() {
		return _dependencyMissingReferences;
	}

	@XmlElement
	public List<MissingReferenceEntry> getWeakMissingReferences() {
		return _weakMissingReferences;
	}

}