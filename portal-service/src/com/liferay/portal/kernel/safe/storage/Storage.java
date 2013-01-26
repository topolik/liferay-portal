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

package com.liferay.portal.kernel.safe.storage;

import com.liferay.portal.kernel.safe.PortalSafeException;
import com.liferay.portal.kernel.safe.model.NoSuchItemException;
import com.liferay.portal.kernel.safe.serializer.SerializedItem;

import java.util.List;

/**
 * The interface for portal safe storage. Keep it secret! Keep it safe!
 *
 * <p>
 * It's assumed the implementations use underlying storage such as a file system
 * or a database. Because it's possible that data saved in the storage can leak,
 * the implementation assure the following:
 * </p>
 *
 * <ul>
 * <li>
 * any written data are encrypted using a strong key
 * </li>
 * <li>
 * the key is strong enough to endure offline attacks in case of the data leak
 * </li>
 * <li>
 * the key and the encrypted data are separated, so that an attacker would
 * need to gain access to multiple locations to decrypt the data
 * </li>
 * </ul>
 *
 * @author Tomas Polesovsky
 */
public interface Storage {

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
	 * @throws
	 */
	public List<String> listNames(long companyId, long groupId)
		throws StorageException;

	public SerializedItem load(long companyId, long groupId, String name)
		throws NoSuchItemException, StorageException;

	/**
	 * Removes the named item, associated with the company and group, from the
	 * safe.
	 *
	 * @param  companyId the primary key of the company
	 * @param  groupId the primary key of the group
	 * @param  name the item's name
	 * @throws NoSuchItemException if no matching item could be found
	 * @throws StorageException if a problem occurred accessing the item from
	 *         underlying storage
	 */
	public void remove(long companyId, long groupId, String name)
		throws NoSuchItemException, StorageException;

	/**
	 * Saves the item associating it with the company and group.
	 *
	 * @param  serializedItem the serialized item to be saved
	 * @param  name the serialized item's name
	 * @throws
	 */
	public void save(SerializedItem serializedItem, String name)
		throws StorageException;

}