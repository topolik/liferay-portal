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
 * Specifies the item serializer registry interface for handling {@link
 * com.liferay.portal.kernel.safe.serializer.ItemSerializer} registration, and
 * reading/writing serialized items.
 *
 * @author Tomas Polesovsky
 */
public interface ItemSerializerRegistry {

	/**
	 * Removes the serializer from the registry.
	 *
	 * @param  serializer the serializer to be removed.
	 * @return <code>true</code> if the serializer was found in the registry and
	 *         successfully removed; <code>false</code> otherwise
	 */
	public boolean deregisterSerializer(ItemSerializer serializer);

	/**
	 * Reads the serialized item, parsing it, and creating a new {@link
	 * com.liferay.portal.kernel.safe.model.Item} descendant using a registered
	 * serializer.
	 *
	 * <p>
	 * Implementation MUST make sure the returned item's common attributes, like
	 * its company ID and group ID, are initialized from the the serialized
	 * item.
	 * </p>
	 *
	 * @param  serializedItem the serialized form of the item to be read
	 * @return deserialized the instance of the serialized item
	 * @throws ItemSerializerException if the deserialization process failed
	 * @throws NoSuchItemSerializerException if no suitable serializer was found
	 *         to parse the serialized item
	 */
	public Item read(SerializedItem serializedItem)
		throws ItemSerializerException, NoSuchItemSerializerException;

	/**
	 * Adds the serializer into the registry.
	 *
	 * @param  serializer the serializer to be registered
	 * @throws DuplicateItemSerializerException if the type of serializer is
	 *         already registered
	 */
	public void registerSerializer(ItemSerializer serializer)
		throws DuplicateItemSerializerException;

	/**
	 * Converts the item into a serialized item using a registered serializer.
	 *
	 * <p>
	 * Implementation MUST make sure that returned serialized item's common
	 * attributes, like its company ID and group ID, are initialized from the
	 * item.
	 * </p>
	 *
	 * @param  item the item to be serialized.
	 * @return serialized form of the item
	 * @throws ItemSerializerException if the serialization process fails
	 * @throws NoSuchItemSerializerException if no suitable serializer is found
	 *         to convert the item
	 */
	public SerializedItem write(Item item)
		throws ItemSerializerException, NoSuchItemSerializerException;

}