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
 * The interface for the portal safe.
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
	 * Returns the names of all the stored items associated with the company and
	 * group.
	 *
	 * <p>
	 * A {@link com.liferay.portal.kernel.safe.storage.StorageException} can
	 * occur if there is a problem loading the item from underlying storage.
	 * </p>
	 *
	 * @param  companyId the primary key of the company
	 * @param  groupId the primary key of the group
	 * @return the names of all the stored items associated with the company and
	 *         group.
	 * @throws PortalSafeException if a problem occurred loading an item from
	 *         storage or if a portal safe exception occurred
	 */
	public List<String> listItemsNames(long companyId, long groupId)
		throws PortalSafeException;

	/**
	 * Loads the named item associated with the company and group.
	 *
	 * <p>
	 * A {@link com.liferay.portal.kernel.safe.model.NoSuchItemException} can
	 * occur if no item is found with the name. A {@link
	 * com.liferay.portal.kernel.safe.serializer.NoSuchItemSerializerException}
	 * can occur if no registered serializer can read the item. A {@link
	 * com.liferay.portal.kernel.safe.storage.StorageException} can occur if
	 * there is a problem loading the item from underlying storage.
	 * </p>
	 *
	 * @param  companyId the primary key of the company
	 * @param  groupId the primary key of the group
	 * @param  name the item's name
	 * @return the loaded item
	 * @throws PortalSafeException if no item was found with the name, if no
	 *         registered serializer could read the item, if a problem occurred
	 *         loading the item from underlying storage, or if a portal safe
	 *         exception occurred
	 */
	public Item loadItem(long companyId, long groupId, String name)
		throws PortalSafeException;

	/**
	 * Removes the named item, associated with the company and group, from the
	 * safe.
	 *
	 * <p>
	 * A {@link com.liferay.portal.kernel.safe.model.NoSuchItemException} can
	 * occur if no matching item is. A {@link
	 * com.liferay.portal.kernel.safe.storage.StorageException} can occur if
	 * there is a problem accessing the item from underlying storage.
	 * </p>
	 *
	 * @param  companyId the primary key of the company
	 * @param  groupId the primary key of the group
	 * @param  name the item's name
	 * @throws PortalSafeException if no item could be found with the name, if a
	 *         problem occurred accessing the item from underlying storage, or
	 *         if a portal safe exception occurred
	 */
	public void removeItem(long companyId, long groupId, String name)
		throws PortalSafeException;

	/**
	 * Saves the item to the portal safe associating it with the company and
	 * group.
	 *
	 * <p>
	 * A
	 * {@link
	 * com.liferay.portal.kernel.safe.serializer.NoSuchItemSerializerException}
	 * can occur if no registered serializer can serialize the item. A
	 * {@link com.liferay.portal.kernel.safe.storage.StorageException} can occur
	 * if there
	 * is a problem saving the item to underlying storage.
	 * </ul>
	 *
	 * @param  companyId the primary key of the company to associate with the
	 *         item
	 * @param  groupId the primary key of the group to associate with the item
	 * @param  item the item to be saved
	 * @throws PortalSafeException if no registered serializer could serialize
	 *         the item, if there was a problem saving the item to underlying
	 *         storage, or if a portal safe exception occurred
	 */
	public void saveItem(long companyId, long groupId, Item item)
		throws PortalSafeException;

}