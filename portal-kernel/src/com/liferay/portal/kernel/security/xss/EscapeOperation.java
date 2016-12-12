/**
 * Copyright (c) 2000-2013 Liferay, Inc. All rights reserved.
 * <p>
 * This library is free software; you can redistribute it and/or modify it under
 * the terms of the GNU Lesser General Public License as published by the Free
 * Software Foundation; either version 2.1 of the License, or (at your option)
 * any later version.
 * <p>
 * This library is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more
 * details.
 */

package com.liferay.portal.kernel.security.xss;

import java.util.Comparator;

/**
 * @author Carlos Sierra Andrés
 */
public interface EscapeOperation extends Comparator<EscapeOperation> {

	public int getPrecendence();

	public String escape(String input);

	@Override
	public default int compare(EscapeOperation o1, EscapeOperation o2) {
		if (o1.equals(o2)) {
			return 0;
		}
		// If they are not equal but have the same precedence do not return
		// them as equals or else one could be removed from sets
		return Math.min(
			Integer.compare(o1.getPrecendence(), o2.getPrecendence()), -1);
	}

}
