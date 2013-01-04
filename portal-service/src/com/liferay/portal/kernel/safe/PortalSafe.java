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

package com.liferay.portal.kernel.safe;

import com.liferay.portal.kernel.safe.model.Item;

import java.util.List;

/**
 * Specifies the Portal Safe interface.
 *
 * <p>
 * Implementations should use the {@link
 * com.liferay.portal.kernel.safe.serializer.ItemSerializerRegistryUtil} to
 * convert an {@link com.liferay.portal.kernel.safe.model.Item} into a {@link
 * com.liferay.portal.kernel.safe.serializer.SerializedItem}, and use the {@link
 * com.liferay.portal.kernel.safe.storage.Storage} interface to load or store
 * the serialized item into underlying safe storage.
 * </p>
 *
 * @author Tomas Polesovsky
 */
public interface PortalSafe {

	/**
	 * Returns names of all items in the safe associated with the company and
	 * group.
	 *
	 * @param  companyId the primary key of the company
	 * @param  groupId the primary key of the group
	 * @return the names of all items in the safe associated with the company
	 *         and group. These names can be used for loading and removing items
	 *         from the safe.
	 * @throws PortalSafeException if a PortalSafeException occurred. The
	 *         exception may be a {@link
	 *         com.liferay.portal.kernel.safe.storage.StorageException} if there
	 *         was a problem loading the item from the underlying storage.
	 */
	public List<String> listItemsNames(long companyId, long groupId)
		throws PortalSafeException;

	/**
	 * Loads the named item associated with the company and group.
	 *
	 * <p>
	 * The following {@link PortalSafeException}s can occur:
	 * </p>
	 *
	 * <ul>
	 * <li>
	 * {@link
	 * com.liferay.portal.kernel.safe.model.NoSuchItemException} if no item is
	 * found with the name
	 * </li>
	 * <li>
	 * {@link
	 * com.liferay.portal.kernel.safe.serializer.NoSuchItemSerializerException}
	 * if no registered serializer can read the item
	 * </li>
	 * <li>
	 * {@link com.liferay.portal.kernel.safe.storage.StorageException} if there
	 * is a problem loading the item from the underlying storage
	 * </li>
	 * </ul>
	 *
	 * @param  companyId the primary key of the company
	 * @param  groupId the primary key of the group
	 * @param  name the item's name
	 * @return the loaded item
	 * @throws PortalSafeException if a PortalSafeException occurred
	 */
	public Item loadItem(long companyId, long groupId, String name)
		throws PortalSafeException;

	/**
	 * Removes from the safe the named item associated with the company and
	 * group.
	 *
	 * <p>
	 * The following {@link PortalSafeException}s can occur:
	 * </p>
	 *
	 * <ul>
	 * <li>
	 * {@link
	 * com.liferay.portal.kernel.safe.model.NoSuchItemException} if no item was
	 * found with the name
	 * </li>
	 * <li>
	 * {@link
	 * com.liferay.portal.kernel.safe.storage.StorageException} if there was a
	 * problem accessing the item from the underlying storage
	 * </li>
	 * </ul>
	 *
	 * @param  companyId the primary key of the company
	 * @param  groupId the primary key of the group
	 * @param  name the item's name
	 * @throws PortalSafeException if a PortalSafeException occurred
	 */
	public void removeItem(long companyId, long groupId, String name)
		throws PortalSafeException;

	/**
	 * Saves the item, associating it with the company and group.
	 *
	 * <p>
	 * The following {@link PortalSafeException}s can occur:
	 * </p>
	 *
	 * <ul>
	 * <li>
	 * {@link
	 * com.liferay.portal.kernel.safe.serializer.NoSuchItemSerializerException}
	 * if no registered serializer could serialize the item
	 * </li>
	 * <li>
	 * {@link
	 * com.liferay.portal.kernel.safe.storage.StorageException} if there was a
	 * problem saving the item to the underlying storage
	 * </li>
	 * </ul>
	 *
	 * @param  companyId the primary key of the company to associate with the
	 *         item
	 * @param  groupId the primary key of the group to associate with the item
	 * @param  item the item to be saved
	 * @throws PortalSafeException if a PortalSafeException occurred
	 */
	public void saveItem(long companyId, long groupId, Item item)
		throws PortalSafeException;

}