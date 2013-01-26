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
import com.liferay.portal.kernel.security.pacl.permission.PortalRuntimePermission;
import com.liferay.portal.model.CompanyConstants;
import com.liferay.portal.model.Group;
import com.liferay.portal.service.GroupLocalServiceUtil;

import java.util.List;

/**
 * Provides the utility class for the {@link PortalSafe}. This is the is the
 * primary access point for operations on the portal safe.
 *
 * @author Tomas Polesovsky
 */
public class PortalSafeUtil {

	/**
	 * Returns the portal safe.
	 *
	 * @return the portal safe
	 */
	public static PortalSafe getPortalSafe() {
		PortalRuntimePermission.checkGetBeanProperty(PortalSafe.class);

		return _portalSafe;
	}

	/**
	 * Returns the names of all the stored items associated with the company.
	 *
	 * @param  companyId the primary key of the company
	 * @return the names of all the stored items associated with the company
	 * @throws PortalSafeException if a portal safe exception occurred
	 */
	public static List<String> listItemsNames(long companyId)
		throws PortalSafeException {

		long groupId = getCompanyGroupId(companyId);

		return listItemsNames(companyId, groupId);
	}

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
	public static List<String> listItemsNames(long companyId, long groupId)
		throws PortalSafeException {

		return getPortalSafe().listItemsNames(companyId, groupId);
	}

	/**
	 * Loads the named item associated with the company.
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
	 * @param  name the item's name
	 * @return the loaded item
	 * @throws PortalSafeException if no item was found with the name, if no
	 *         registered serializer could read the item, if a problem occurred
	 *         loading the item from underlying storage, or if a portal safe
	 *         exception occurred
	 */
	public static Item loadItem(long companyId, String name)
		throws PortalSafeException {

		long groupId = getCompanyGroupId(companyId);

		return getPortalSafe().loadItem(companyId, groupId, name);
	}

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
	 * @param  name the item's name
	 * @param  groupId the primary key of the group
	 * @return the loaded item
	 * @throws PortalSafeException if no item was found with the name, if no
	 *         registered serializer could read the item, if a problem occurred
	 *         loading the item from underlying storage, or if a portal safe
	 *         exception occurred
	 */
	public static Item loadItem(long companyId, String name, long groupId)
		throws PortalSafeException {

		return getPortalSafe().loadItem(companyId, groupId, name);
	}

	/**
	 * Removes from the safe the named item associated with the company.
	 *
	 * <p>
	 * A {@link com.liferay.portal.kernel.safe.model.NoSuchItemException} can
	 * occur if no item is found with the name. A {@link
	 * com.liferay.portal.kernel.safe.storage.StorageException} can occur if
	 * there is a problem accessing the item from underlying storage.
	 * </p>
	 *
	 * @param  companyId the primary key of the company
	 * @param  name the item's name
	 * @throws PortalSafeException if no item could be found with the name, if
	 *         there was a problem accessing the item from underlying storage,
	 *         or if a portal safe exception occurred
	 */
	public static void removeItem(long companyId, String name)
		throws PortalSafeException {

		long groupId = getCompanyGroupId(companyId);

		getPortalSafe().removeItem(companyId, groupId, name);
	}

	/**
	 * Removes from the safe the named item associated with the company and
	 * group.
	 *
	 * <p>
	 * A {@link com.liferay.portal.kernel.safe.model.NoSuchItemException} can
	 * occur if no item is found with the name. A {@link
	 * com.liferay.portal.kernel.safe.storage.StorageException} can occur if
	 * there is a problem accessing the item from underlying storage.
	 * </p>
	 *
	 * @param  companyId the primary key of the company
	 * @param  name the item's name
	 * @param  groupId the primary key of the group
	 * @throws PortalSafeException if no item could be found with the name, if
	 *         there was a problem accessing the item from underlying storage,
	 *         or if a portal safe exception occurred
	 */
	public static void removeItem(long companyId, String name, long groupId)
		throws PortalSafeException {

		getPortalSafe().removeItem(companyId, groupId, name);
	}

	/**
	 * Saves the item to the portal safe associating it with the company.
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
	 * @param  item the item to be saved
	 * @throws PortalSafeException if no registered serializer could serialize
	 *         the item, if there was a problem saving the item to underlying
	 *         storage, or if a portal safe exception occurred
	 */
	public static void saveItem(long companyId, Item item)
		throws PortalSafeException {

		long groupId = getCompanyGroupId(companyId);

		getPortalSafe().saveItem(companyId, groupId, item);
	}

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
	public static void saveItem(long companyId, long groupId, Item item)
		throws PortalSafeException {

		getPortalSafe().saveItem(companyId, groupId, item);
	}

	/**
	 * Sets the portal safe instance.
	 *
	 * @param portalSafe the portal safe instance to wrap
	 */
	public void setPortalSafe(PortalSafe portalSafe) {
		PortalRuntimePermission.checkSetBeanProperty(getClass());

		_portalSafe = portalSafe;
	}

	/**
	 * Returns the company's group ID.
	 *
	 * @param  companyId the primary key of the company
	 * @return the company's group ID or {@link CompanyConstants#SYSTEM} if the
	 *         company is the system company
	 * @throws PortalSafeException if a matching group could not be found
	 */
	private static long getCompanyGroupId(long companyId)
		throws PortalSafeException {

		if (companyId != CompanyConstants.SYSTEM) {
			try {
				Group companyGroup = GroupLocalServiceUtil.getCompanyGroup(
					companyId);

				return companyGroup.getGroupId();
			}
			catch (Exception e) {
				throw new PortalSafeException(
					"Unable to get the company's group ID", e);
			}
		}

		return CompanyConstants.SYSTEM;
	}

	/**
	 * Specifies the wrapped portal safe instance.
	 */
	private static PortalSafe _portalSafe;

}