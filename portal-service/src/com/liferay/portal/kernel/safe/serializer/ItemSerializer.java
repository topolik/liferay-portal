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

import com.liferay.portal.kernel.safe.model.Item;

/**
 * Serializes and deserializes {@link Item} objects to and from {@link
 * SerializedItem} objects.
 *
 * @author Tomas Polesovsky
 */
public interface ItemSerializer<T extends Item> {

	/**
	 * Returns the serializer's unique ID (UID). The UID identifies the
	 * serialized item's serializer for use in deserialization.
	 *
	 * @return the serializer's unique ID, usually a constant.
	 */
	public long getSerializerUID();

	/**
	 * Returns the class of the type <code>T</code> that is able to serialize
	 * {@link Item} descendants into {@link SerializedItem} objects.
	 *
	 * @return the class of the type <code>T</code> that is able to serialize
	 *         {@link Item} descendants into {@link SerializedItem} objects. The
	 *         class is usually a constant.
	 */
	public Class getSupportedClass();

	/**
	 * Deserializes a {@link SerializedItem} into a new instance of the {@link
	 * Item} descendant. Calls {@link SerializedItem#getItemStream()}.
	 *
	 * <p>
	 * Implementations MUST copy the serialized item's company ID and group ID
	 * into the the new {@link Item}. The serializer may use {@link
	 * SerializedItem#getHeader()} to access any metadata previously stored for
	 * the item.
	 * </p>
	 *
	 * @param  serializedItem the item to be deserialized
	 * @return the deserialized item as new instance of the {@link Item}
	 *         descendant
	 * @throws ItemSerializerException if deserialization failed
	 */
	public T read(SerializedItem serializedItem) throws ItemSerializerException;

	/**
	 * Serializes an internal representation of the item into an {@link
	 * java.io.InputStream} and stores it in a {@link SerializedItem} via {@link
	 * SerializedItem#setItemStream(java.io.InputStream)}.
	 *
	 * <p>
	 * Implementations MUST copy the serialized item's company ID and group ID
	 * into the the {@link SerializedItem}. The serializer may use {@link
	 * SerializedItem#setHeader(byte[])} to store the item's metadata, making it
	 * available for {@link #read(SerializedItem)}.
	 * </p>
	 *
	 * @param  item the item to be serialized
	 * @return the serialized item as new instance of {@link SerializedItem}
	 * @throws ItemSerializerException if serialization failed
	 */
	public SerializedItem write(T item) throws ItemSerializerException;

}