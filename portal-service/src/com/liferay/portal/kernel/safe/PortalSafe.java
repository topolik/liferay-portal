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

import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.exception.SystemException;
import com.liferay.portal.kernel.safe.model.Item;

import java.util.List;

/**
 * The interface for Portal Safe implementations.
 *
 * <p>
 * Implementations should use
 * {@link com.liferay.portal.kernel.safe.serializer.ItemSerializerRegistryUtil}
 * to convert {@link Item} into
 * {@link com.liferay.portal.kernel.safe.serializer.SerializedItem}
 * and {@link com.liferay.portal.kernel.safe.storage.Storage} to load or store
 * the serialized item into underlying safe storage.
 *
 * </p>
 * @author Tomas Polesovsky
 */
public interface PortalSafe {

	/**
	 * Returns names of all {@link Item}s in the safe, stored under the
	 * companyId and the groupId.
	 *
	 * @param companyId id of company to load items from
	 * @param groupId id of group inside the company to load items from
	 * @return not null list of accessible items names. These can be later used
	 *         for loading or removing items from the safe.
	 * @throws com.liferay.portal.kernel.safe.storage.StorageException if there
	 *         is a problem with loading the item from the underlying storage
	 * @throws PortalSafeException if an other error occurs
	 */
	public List<String> listItemsNames(long companyId, long groupId)
		throws PortalSafeException;

	/**
	 * Loads and returns an item described by the companyId, the groupId and
	 * the item's name.
	 *
	 * @param companyId id of company to load items from
	 * @param groupId id of group inside the company to load items from
	 * @param name name of the item to be loaded
	 * @return descendant of {@link Item} interface
	 * @throws com.liferay.portal.kernel.safe.model.NoSuchItemException if
	 *         there isn't any item found with the name
	 * @throws com.liferay.portal.kernel.safe.serializer.NoSuchItemSerializerException
	 *         if there is no serializer registered that is able to read the
	 *         item
	 * @throws com.liferay.portal.kernel.safe.storage.StorageException if there
	 *         is a problem with loading the item from the underlying storage
	 * @throws PortalSafeException if an other error occurs
	 */
	public Item loadItem(long companyId, long groupId, String name)
		throws PortalSafeException;

	/**
	 * Removes the item from the safe. Item is described by the companyId,
	 * the groupId and the item's name.
	 *
	 * @param companyId id of company to remove the item from
	 * @param groupId id of group inside the company to remove the item from
	 * @param name name of the item to be removed
	 * @throws com.liferay.portal.kernel.safe.model.NoSuchItemException if
	 *         there isn't any item found with the name
	 * @throws com.liferay.portal.kernel.safe.storage.StorageException if there
	 *         is a problem with removing the item from the underlying storage
	 * @throws PortalSafeException if some error occurs during deleting
	 */
	public void removeItem(long companyId, long groupId, String name)
		throws PortalSafeException;

	/**
	 * Stores the item into safe.
	 *
	 * @param companyId id of company to save the item to
	 * @param groupId id of group inside the company to save the item to
	 * @param item the item to be saved
	 * @throws com.liferay.portal.kernel.safe.serializer.NoSuchItemSerializerException
	 *         if there is no serializer registered that is able to convert the
	 *         item into its binary form
	 * @throws com.liferay.portal.kernel.safe.storage.StorageException if there
	 *         is a problem with saving the item into the underlying storage
	 * @throws PortalSafeException if an other error occurs
	 */
	public void saveItem(long companyId, long groupId, Item item)
		throws PortalSafeException;

}