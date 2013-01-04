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
 * Specifies the utility class for the {@link PortalSafe} service. This is the
 * main entry point into Portal Safe.
 *
 * @author Tomas Polesovsky
 */
public class PortalSafeUtil {

	/**
	 * Returns the wrapped {@link PortalSafe} instance.
	 *
	 * @return the wrapped Portal Safe instance
	 */
	public static PortalSafe getPortalSafe() {
		PortalRuntimePermission.checkGetBeanProperty(PortalSafe.class);

		return _portalSafe;
	}

	/**
	 * Returns names of items stored in the company's group.
	 *
	 * <p>
	 * The method tries to get the company's group using {@link
	 * #getCompanyGroupId(long)} and continues calling {@link
	 * #listItemsNames(long, long)}.
	 * </p>
	 *
	 * @param  companyId the primary key of the company
	 * @return the names of items stored in the company's group
	 * @throws PortalSafeException if a PortalSafeException occurred
	 */
	public static List<String> listItemsNames(long companyId)
		throws PortalSafeException {

		long groupId = getCompanyGroupId(companyId);

		return listItemsNames(companyId, groupId);
	}

	/**
	 * Returns names of all items in the safe associated with the company and
	 * group. The method delegates the call to the wrapped instance.
	 *
	 * @param  companyId the primary key of the company
	 * @param  groupId the primary key of the group
	 * @return the names of all items in the safe associated with the company
	 *         and group. These names can be used for loading and removing items
	 *         from the safe.
	 * @throws PortalSafeException if a PortalSafeException occurred
	 */
	public static List<String> listItemsNames(long companyId, long groupId)
		throws PortalSafeException {

		return getPortalSafe().listItemsNames(companyId, groupId);
	}

	/**
	 * Loads the named item associated with the company's group.
	 *
	 * <p>
	 * The method tries to get the company's group using {@link
	 * #getCompanyGroupId(long)} and continues calling {@link #loadItem(long,
	 * long, String)}.
	 * </p>
	 *
	 * <p>
	 * The following {@link PortalSafeException}s can occur:
	 * </p>
	 *
	 * <ul>
	 * <li>
	 * {@link
	 * com.liferay.portal.kernel.safe.model.NoSuchItemException} if no
	 * item is found with the name
	 * </li>
	 * <li>
	 * {@link
	 * com.liferay.portal.kernel.safe.serializer.NoSuchItemSerializerException}
	 * if no registered serializer can read the item
	 * </li>
	 * <li>
	 * {@link com.liferay.portal.kernel.safe.storage.StorageException} if
	 * there is a problem loading the item from the underlying storage
	 * </li>
	 * </ul>
	 *
	 * @param  companyId the primary key of the company
	 * @param  name the item's name
	 * @return the loaded item
	 * @throws PortalSafeException if a PortalSafeException occurred
	 */
	public static Item loadItem(long companyId, String name)
		throws PortalSafeException {

		long groupId = getCompanyGroupId(companyId);

		return getPortalSafe().loadItem(companyId, groupId, name);
	}

	/**
	 * Loads the named item associated with the company and group. The method
	 * delegates the call to the wrapped instance.
	 *
	 * <p>
	 * The following {@link PortalSafeException}s can occur:
	 * </p>
	 *
	 * <ul>
	 * <li>
	 * {@link
	 * com.liferay.portal.kernel.safe.model.NoSuchItemException} if no
	 * item is found with the name
	 * </li>
	 * <li>
	 * {@link
	 * com.liferay.portal.kernel.safe.serializer.NoSuchItemSerializerException}
	 * if no registered serializer registered can read the item
	 * </li>
	 * <li>
	 * {@link com.liferay.portal.kernel.safe.storage.StorageException} if
	 * there is a problem loading the item from the underlying storage
	 * </li>
	 * </ul>
	 *
	 * @param  companyId the primary key of the company
	 * @param  name the item's name
	 * @param  groupId the primary key of the group
	 * @return the loaded item
	 * @throws PortalSafeException if a PortalSafeException occurred
	 */
	public static Item loadItem(long companyId, String name, long groupId)
		throws PortalSafeException {

		return getPortalSafe().loadItem(companyId, groupId, name);
	}

	/**
	 * Removes from the safe the named item associated with the company's group.
	 *
	 * <p>
	 * The method tries to get the company's group using {@link
	 * #getCompanyGroupId(long)} and continues calling {@link #removeItem(long,
	 * long, String)}.
	 * </p>
	 *
	 * <p>
	 * The following {@link PortalSafeException}s can occur:
	 * </p>
	 *
	 * <ul>
	 * <li>
	 * {@link
	 * com.liferay.portal.kernel.safe.model.NoSuchItemException} if no
	 * item is found with the name
	 * </li>
	 * <li>
	 * {@link
	 * com.liferay.portal.kernel.safe.storage.StorageException} if there was a
	 * problem accessing the item from the underlying storage
	 * </li>
	 * </ul>
	 *
	 * @param  companyId the primary key of the company
	 * @param  name the item's name
	 * @throws PortalSafeException if a PortalSafeException occurred
	 */
	public static void removeItem(long companyId, String name)
		throws PortalSafeException {

		long groupId = getCompanyGroupId(companyId);

		getPortalSafe().removeItem(companyId, groupId, name);
	}

	/**
	 * Removes from the safe the named item associated with the company and
	 * group. The method delegates the call to the wrapped instance.
	 *
	 * <p>
	 * The following {@link PortalSafeException}s can occur:
	 * </p>
	 *
	 * <ul>
	 * <li>
	 * {@link
	 * com.liferay.portal.kernel.safe.model.NoSuchItemException} if no
	 * item is found with the name
	 * </li>
	 * <li>
	 * {@link
	 * com.liferay.portal.kernel.safe.storage.StorageException} if there was a
	 * problem accessing the item from the underlying storage
	 * </li>
	 * </ul>
	 *
	 * @param  companyId the primary key of the company
	 * @param  name the item's name
	 * @param  groupId the primary key of the group
	 * @throws PortalSafeException if a PortalSafeException occurred
	 */
	public static void removeItem(long companyId, String name, long groupId)
		throws PortalSafeException {

		getPortalSafe().removeItem(companyId, groupId, name);
	}

	/**
	 * Saves the item, associating it with the company's group.
	 *
	 * <p>
	 * The method tries to get the company's group using {@link
	 * #getCompanyGroupId(long)} and continues calling {@link #saveItem(long,
	 * long, com.liferay.portal.kernel.safe.model.Item)}.
	 * </p>
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
	 * @param  companyId the primary key of the company
	 * @param  item the item to be saved
	 * @throws PortalSafeException if a PortalSafeException occurred
	 */
	public static void saveItem(long companyId, Item item)
		throws PortalSafeException {

		long groupId = getCompanyGroupId(companyId);

		getPortalSafe().saveItem(companyId, groupId, item);
	}

	/**
	 * Saves the item, associating it with the company and group. The method
	 * delegates the call to the wrapped instance.
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
	 * @param  companyId the primary key of the company
	 * @param  groupId the primary key of the group
	 * @param  item the item to be saved
	 * @throws PortalSafeException if a PortalSafeException occurred
	 */
	public static void saveItem(long companyId, long groupId, Item item)
		throws PortalSafeException {

		getPortalSafe().saveItem(companyId, groupId, item);
	}

	/**
	 * Implements the wrapped {@link PortalSafe} instance.
	 *
	 * @param portalSafe the instance to be used as the wrapped service
	 */
	public void setPortalSafe(PortalSafe portalSafe) {
		PortalRuntimePermission.checkSetBeanProperty(getClass());

		_portalSafe = portalSafe;
	}

	/**
	 * Loads the company's group via {@link GroupLocalServiceUtil}.
	 *
	 * @param  companyId the primary key of the company
	 * @return the company's group or the {@link CompanyConstants#SYSTEM} value,
	 *         if applicable
	 * @throws PortalSafeException if the {@link GroupLocalServiceUtil} throws a
	 *         PortalSafeException
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
					"Unable to load company groupId", e);
			}
		}

		return CompanyConstants.SYSTEM;
	}

	/**
	 * Specifies the wrapped {@link PortalSafe} instance.
	 */
	private static PortalSafe _portalSafe;

}