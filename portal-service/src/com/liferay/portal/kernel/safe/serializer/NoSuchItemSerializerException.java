/**
 * Copyright (c) 2000-2012 Liferay, Inc. All rights reserved.
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

package com.liferay.portal.kernel.safe.serializer;

/**
 * Thrown by an {@link ItemSerializerRegistry} when no {@link ItemSerializer} is
 * found for serializing an {@link com.liferay.portal.kernel.safe.model.Item} into
 * a {@link SerializedItem} or vice-versa.
 *
 * @author Tomas Polesovsky
 */
public class NoSuchItemSerializerException extends ItemSerializerException {
	public NoSuchItemSerializerException() {
	}

	public NoSuchItemSerializerException(String msg) {
		super(msg);
	}

	public NoSuchItemSerializerException(String msg, Throwable cause) {
		super(msg, cause);
	}

	public NoSuchItemSerializerException(Throwable cause) {
		super(cause);
	}

}