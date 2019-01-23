/**
 * Copyright (c) 2000-present Liferay, Inc. All rights reserved.
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

package com.bemis.portal.twofa.device.manager.service.persistence.impl;

import aQute.bnd.annotation.ProviderType;

import com.bemis.portal.twofa.device.manager.exception.NoSuchDeviceException;
import com.bemis.portal.twofa.device.manager.model.Device;
import com.bemis.portal.twofa.device.manager.model.impl.DeviceImpl;
import com.bemis.portal.twofa.device.manager.model.impl.DeviceModelImpl;
import com.bemis.portal.twofa.device.manager.service.persistence.DevicePersistence;

import com.liferay.portal.kernel.dao.orm.EntityCache;
import com.liferay.portal.kernel.dao.orm.FinderCache;
import com.liferay.portal.kernel.dao.orm.FinderPath;
import com.liferay.portal.kernel.dao.orm.Query;
import com.liferay.portal.kernel.dao.orm.QueryPos;
import com.liferay.portal.kernel.dao.orm.QueryUtil;
import com.liferay.portal.kernel.dao.orm.Session;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.service.ServiceContextThreadLocal;
import com.liferay.portal.kernel.service.persistence.CompanyProvider;
import com.liferay.portal.kernel.service.persistence.CompanyProviderWrapper;
import com.liferay.portal.kernel.service.persistence.impl.BasePersistenceImpl;
import com.liferay.portal.kernel.util.OrderByComparator;
import com.liferay.portal.kernel.util.ProxyUtil;
import com.liferay.portal.kernel.util.StringBundler;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.spring.extender.service.ServiceReference;

import java.io.Serializable;

import java.lang.reflect.InvocationHandler;

import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

/**
 * The persistence implementation for the device service.
 *
 * <p>
 * Caching information and settings can be found in <code>portal.properties</code>
 * </p>
 *
 * @author Prathima Shreenath
 * @see DevicePersistence
 * @see com.bemis.portal.twofa.device.manager.service.persistence.DeviceUtil
 * @generated
 */
@ProviderType
public class DevicePersistenceImpl extends BasePersistenceImpl<Device>
	implements DevicePersistence {
	/*
	 * NOTE FOR DEVELOPERS:
	 *
	 * Never modify or reference this class directly. Always use {@link DeviceUtil} to access the device persistence. Modify <code>service.xml</code> and rerun ServiceBuilder to regenerate this class.
	 */
	public static final String FINDER_CLASS_NAME_ENTITY = DeviceImpl.class.getName();
	public static final String FINDER_CLASS_NAME_LIST_WITH_PAGINATION = FINDER_CLASS_NAME_ENTITY +
		".List1";
	public static final String FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION = FINDER_CLASS_NAME_ENTITY +
		".List2";
	public static final FinderPath FINDER_PATH_WITH_PAGINATION_FIND_ALL = new FinderPath(DeviceModelImpl.ENTITY_CACHE_ENABLED,
			DeviceModelImpl.FINDER_CACHE_ENABLED, DeviceImpl.class,
			FINDER_CLASS_NAME_LIST_WITH_PAGINATION, "findAll", new String[0]);
	public static final FinderPath FINDER_PATH_WITHOUT_PAGINATION_FIND_ALL = new FinderPath(DeviceModelImpl.ENTITY_CACHE_ENABLED,
			DeviceModelImpl.FINDER_CACHE_ENABLED, DeviceImpl.class,
			FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "findAll", new String[0]);
	public static final FinderPath FINDER_PATH_COUNT_ALL = new FinderPath(DeviceModelImpl.ENTITY_CACHE_ENABLED,
			DeviceModelImpl.FINDER_CACHE_ENABLED, Long.class,
			FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "countAll", new String[0]);
	public static final FinderPath FINDER_PATH_WITH_PAGINATION_FIND_BY_PORTALUSERID =
		new FinderPath(DeviceModelImpl.ENTITY_CACHE_ENABLED,
			DeviceModelImpl.FINDER_CACHE_ENABLED, DeviceImpl.class,
			FINDER_CLASS_NAME_LIST_WITH_PAGINATION, "findByPortalUserId",
			new String[] {
				Long.class.getName(),
				
			Integer.class.getName(), Integer.class.getName(),
				OrderByComparator.class.getName()
			});
	public static final FinderPath FINDER_PATH_WITHOUT_PAGINATION_FIND_BY_PORTALUSERID =
		new FinderPath(DeviceModelImpl.ENTITY_CACHE_ENABLED,
			DeviceModelImpl.FINDER_CACHE_ENABLED, DeviceImpl.class,
			FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "findByPortalUserId",
			new String[] { Long.class.getName() },
			DeviceModelImpl.PORTALUSERID_COLUMN_BITMASK);
	public static final FinderPath FINDER_PATH_COUNT_BY_PORTALUSERID = new FinderPath(DeviceModelImpl.ENTITY_CACHE_ENABLED,
			DeviceModelImpl.FINDER_CACHE_ENABLED, Long.class,
			FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "countByPortalUserId",
			new String[] { Long.class.getName() });

	/**
	 * Returns all the devices where portalUserId = &#63;.
	 *
	 * @param portalUserId the portal user ID
	 * @return the matching devices
	 */
	@Override
	public List<Device> findByPortalUserId(long portalUserId) {
		return findByPortalUserId(portalUserId, QueryUtil.ALL_POS,
			QueryUtil.ALL_POS, null);
	}

	/**
	 * Returns a range of all the devices where portalUserId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to {@link QueryUtil#ALL_POS} will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent and pagination is required (<code>start</code> and <code>end</code> are not {@link QueryUtil#ALL_POS}), then the query will include the default ORDER BY logic from {@link DeviceModelImpl}. If both <code>orderByComparator</code> and pagination are absent, for performance reasons, the query will not have an ORDER BY clause and the returned result set will be sorted on by the primary key in an ascending order.
	 * </p>
	 *
	 * @param portalUserId the portal user ID
	 * @param start the lower bound of the range of devices
	 * @param end the upper bound of the range of devices (not inclusive)
	 * @return the range of matching devices
	 */
	@Override
	public List<Device> findByPortalUserId(long portalUserId, int start, int end) {
		return findByPortalUserId(portalUserId, start, end, null);
	}

	/**
	 * Returns an ordered range of all the devices where portalUserId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to {@link QueryUtil#ALL_POS} will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent and pagination is required (<code>start</code> and <code>end</code> are not {@link QueryUtil#ALL_POS}), then the query will include the default ORDER BY logic from {@link DeviceModelImpl}. If both <code>orderByComparator</code> and pagination are absent, for performance reasons, the query will not have an ORDER BY clause and the returned result set will be sorted on by the primary key in an ascending order.
	 * </p>
	 *
	 * @param portalUserId the portal user ID
	 * @param start the lower bound of the range of devices
	 * @param end the upper bound of the range of devices (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching devices
	 */
	@Override
	public List<Device> findByPortalUserId(long portalUserId, int start,
		int end, OrderByComparator<Device> orderByComparator) {
		return findByPortalUserId(portalUserId, start, end, orderByComparator,
			true);
	}

	/**
	 * Returns an ordered range of all the devices where portalUserId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to {@link QueryUtil#ALL_POS} will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent and pagination is required (<code>start</code> and <code>end</code> are not {@link QueryUtil#ALL_POS}), then the query will include the default ORDER BY logic from {@link DeviceModelImpl}. If both <code>orderByComparator</code> and pagination are absent, for performance reasons, the query will not have an ORDER BY clause and the returned result set will be sorted on by the primary key in an ascending order.
	 * </p>
	 *
	 * @param portalUserId the portal user ID
	 * @param start the lower bound of the range of devices
	 * @param end the upper bound of the range of devices (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param retrieveFromCache whether to retrieve from the finder cache
	 * @return the ordered range of matching devices
	 */
	@Override
	public List<Device> findByPortalUserId(long portalUserId, int start,
		int end, OrderByComparator<Device> orderByComparator,
		boolean retrieveFromCache) {
		boolean pagination = true;
		FinderPath finderPath = null;
		Object[] finderArgs = null;

		if ((start == QueryUtil.ALL_POS) && (end == QueryUtil.ALL_POS) &&
				(orderByComparator == null)) {
			pagination = false;
			finderPath = FINDER_PATH_WITHOUT_PAGINATION_FIND_BY_PORTALUSERID;
			finderArgs = new Object[] { portalUserId };
		}
		else {
			finderPath = FINDER_PATH_WITH_PAGINATION_FIND_BY_PORTALUSERID;
			finderArgs = new Object[] {
					portalUserId,
					
					start, end, orderByComparator
				};
		}

		List<Device> list = null;

		if (retrieveFromCache) {
			list = (List<Device>)finderCache.getResult(finderPath, finderArgs,
					this);

			if ((list != null) && !list.isEmpty()) {
				for (Device device : list) {
					if ((portalUserId != device.getPortalUserId())) {
						list = null;

						break;
					}
				}
			}
		}

		if (list == null) {
			StringBundler query = null;

			if (orderByComparator != null) {
				query = new StringBundler(3 +
						(orderByComparator.getOrderByFields().length * 2));
			}
			else {
				query = new StringBundler(3);
			}

			query.append(_SQL_SELECT_DEVICE_WHERE);

			query.append(_FINDER_COLUMN_PORTALUSERID_PORTALUSERID_2);

			if (orderByComparator != null) {
				appendOrderByComparator(query, _ORDER_BY_ENTITY_ALIAS,
					orderByComparator);
			}
			else
			 if (pagination) {
				query.append(DeviceModelImpl.ORDER_BY_JPQL);
			}

			String sql = query.toString();

			Session session = null;

			try {
				session = openSession();

				Query q = session.createQuery(sql);

				QueryPos qPos = QueryPos.getInstance(q);

				qPos.add(portalUserId);

				if (!pagination) {
					list = (List<Device>)QueryUtil.list(q, getDialect(), start,
							end, false);

					Collections.sort(list);

					list = Collections.unmodifiableList(list);
				}
				else {
					list = (List<Device>)QueryUtil.list(q, getDialect(), start,
							end);
				}

				cacheResult(list);

				finderCache.putResult(finderPath, finderArgs, list);
			}
			catch (Exception e) {
				finderCache.removeResult(finderPath, finderArgs);

				throw processException(e);
			}
			finally {
				closeSession(session);
			}
		}

		return list;
	}

	/**
	 * Returns the first device in the ordered set where portalUserId = &#63;.
	 *
	 * @param portalUserId the portal user ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching device
	 * @throws NoSuchDeviceException if a matching device could not be found
	 */
	@Override
	public Device findByPortalUserId_First(long portalUserId,
		OrderByComparator<Device> orderByComparator)
		throws NoSuchDeviceException {
		Device device = fetchByPortalUserId_First(portalUserId,
				orderByComparator);

		if (device != null) {
			return device;
		}

		StringBundler msg = new StringBundler(4);

		msg.append(_NO_SUCH_ENTITY_WITH_KEY);

		msg.append("portalUserId=");
		msg.append(portalUserId);

		msg.append("}");

		throw new NoSuchDeviceException(msg.toString());
	}

	/**
	 * Returns the first device in the ordered set where portalUserId = &#63;.
	 *
	 * @param portalUserId the portal user ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching device, or <code>null</code> if a matching device could not be found
	 */
	@Override
	public Device fetchByPortalUserId_First(long portalUserId,
		OrderByComparator<Device> orderByComparator) {
		List<Device> list = findByPortalUserId(portalUserId, 0, 1,
				orderByComparator);

		if (!list.isEmpty()) {
			return list.get(0);
		}

		return null;
	}

	/**
	 * Returns the last device in the ordered set where portalUserId = &#63;.
	 *
	 * @param portalUserId the portal user ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching device
	 * @throws NoSuchDeviceException if a matching device could not be found
	 */
	@Override
	public Device findByPortalUserId_Last(long portalUserId,
		OrderByComparator<Device> orderByComparator)
		throws NoSuchDeviceException {
		Device device = fetchByPortalUserId_Last(portalUserId, orderByComparator);

		if (device != null) {
			return device;
		}

		StringBundler msg = new StringBundler(4);

		msg.append(_NO_SUCH_ENTITY_WITH_KEY);

		msg.append("portalUserId=");
		msg.append(portalUserId);

		msg.append("}");

		throw new NoSuchDeviceException(msg.toString());
	}

	/**
	 * Returns the last device in the ordered set where portalUserId = &#63;.
	 *
	 * @param portalUserId the portal user ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching device, or <code>null</code> if a matching device could not be found
	 */
	@Override
	public Device fetchByPortalUserId_Last(long portalUserId,
		OrderByComparator<Device> orderByComparator) {
		int count = countByPortalUserId(portalUserId);

		if (count == 0) {
			return null;
		}

		List<Device> list = findByPortalUserId(portalUserId, count - 1, count,
				orderByComparator);

		if (!list.isEmpty()) {
			return list.get(0);
		}

		return null;
	}

	/**
	 * Returns the devices before and after the current device in the ordered set where portalUserId = &#63;.
	 *
	 * @param deviceId the primary key of the current device
	 * @param portalUserId the portal user ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the previous, current, and next device
	 * @throws NoSuchDeviceException if a device with the primary key could not be found
	 */
	@Override
	public Device[] findByPortalUserId_PrevAndNext(long deviceId,
		long portalUserId, OrderByComparator<Device> orderByComparator)
		throws NoSuchDeviceException {
		Device device = findByPrimaryKey(deviceId);

		Session session = null;

		try {
			session = openSession();

			Device[] array = new DeviceImpl[3];

			array[0] = getByPortalUserId_PrevAndNext(session, device,
					portalUserId, orderByComparator, true);

			array[1] = device;

			array[2] = getByPortalUserId_PrevAndNext(session, device,
					portalUserId, orderByComparator, false);

			return array;
		}
		catch (Exception e) {
			throw processException(e);
		}
		finally {
			closeSession(session);
		}
	}

	protected Device getByPortalUserId_PrevAndNext(Session session,
		Device device, long portalUserId,
		OrderByComparator<Device> orderByComparator, boolean previous) {
		StringBundler query = null;

		if (orderByComparator != null) {
			query = new StringBundler(4 +
					(orderByComparator.getOrderByConditionFields().length * 3) +
					(orderByComparator.getOrderByFields().length * 3));
		}
		else {
			query = new StringBundler(3);
		}

		query.append(_SQL_SELECT_DEVICE_WHERE);

		query.append(_FINDER_COLUMN_PORTALUSERID_PORTALUSERID_2);

		if (orderByComparator != null) {
			String[] orderByConditionFields = orderByComparator.getOrderByConditionFields();

			if (orderByConditionFields.length > 0) {
				query.append(WHERE_AND);
			}

			for (int i = 0; i < orderByConditionFields.length; i++) {
				query.append(_ORDER_BY_ENTITY_ALIAS);
				query.append(orderByConditionFields[i]);

				if ((i + 1) < orderByConditionFields.length) {
					if (orderByComparator.isAscending() ^ previous) {
						query.append(WHERE_GREATER_THAN_HAS_NEXT);
					}
					else {
						query.append(WHERE_LESSER_THAN_HAS_NEXT);
					}
				}
				else {
					if (orderByComparator.isAscending() ^ previous) {
						query.append(WHERE_GREATER_THAN);
					}
					else {
						query.append(WHERE_LESSER_THAN);
					}
				}
			}

			query.append(ORDER_BY_CLAUSE);

			String[] orderByFields = orderByComparator.getOrderByFields();

			for (int i = 0; i < orderByFields.length; i++) {
				query.append(_ORDER_BY_ENTITY_ALIAS);
				query.append(orderByFields[i]);

				if ((i + 1) < orderByFields.length) {
					if (orderByComparator.isAscending() ^ previous) {
						query.append(ORDER_BY_ASC_HAS_NEXT);
					}
					else {
						query.append(ORDER_BY_DESC_HAS_NEXT);
					}
				}
				else {
					if (orderByComparator.isAscending() ^ previous) {
						query.append(ORDER_BY_ASC);
					}
					else {
						query.append(ORDER_BY_DESC);
					}
				}
			}
		}
		else {
			query.append(DeviceModelImpl.ORDER_BY_JPQL);
		}

		String sql = query.toString();

		Query q = session.createQuery(sql);

		q.setFirstResult(0);
		q.setMaxResults(2);

		QueryPos qPos = QueryPos.getInstance(q);

		qPos.add(portalUserId);

		if (orderByComparator != null) {
			for (Object orderByConditionValue : orderByComparator.getOrderByConditionValues(
					device)) {
				qPos.add(orderByConditionValue);
			}
		}

		List<Device> list = q.list();

		if (list.size() == 2) {
			return list.get(1);
		}
		else {
			return null;
		}
	}

	/**
	 * Removes all the devices where portalUserId = &#63; from the database.
	 *
	 * @param portalUserId the portal user ID
	 */
	@Override
	public void removeByPortalUserId(long portalUserId) {
		for (Device device : findByPortalUserId(portalUserId,
				QueryUtil.ALL_POS, QueryUtil.ALL_POS, null)) {
			remove(device);
		}
	}

	/**
	 * Returns the number of devices where portalUserId = &#63;.
	 *
	 * @param portalUserId the portal user ID
	 * @return the number of matching devices
	 */
	@Override
	public int countByPortalUserId(long portalUserId) {
		FinderPath finderPath = FINDER_PATH_COUNT_BY_PORTALUSERID;

		Object[] finderArgs = new Object[] { portalUserId };

		Long count = (Long)finderCache.getResult(finderPath, finderArgs, this);

		if (count == null) {
			StringBundler query = new StringBundler(2);

			query.append(_SQL_COUNT_DEVICE_WHERE);

			query.append(_FINDER_COLUMN_PORTALUSERID_PORTALUSERID_2);

			String sql = query.toString();

			Session session = null;

			try {
				session = openSession();

				Query q = session.createQuery(sql);

				QueryPos qPos = QueryPos.getInstance(q);

				qPos.add(portalUserId);

				count = (Long)q.uniqueResult();

				finderCache.putResult(finderPath, finderArgs, count);
			}
			catch (Exception e) {
				finderCache.removeResult(finderPath, finderArgs);

				throw processException(e);
			}
			finally {
				closeSession(session);
			}
		}

		return count.intValue();
	}

	private static final String _FINDER_COLUMN_PORTALUSERID_PORTALUSERID_2 = "device.portalUserId = ?";
	public static final FinderPath FINDER_PATH_FETCH_BY_PORTALUSERID_DEVICEIP = new FinderPath(DeviceModelImpl.ENTITY_CACHE_ENABLED,
			DeviceModelImpl.FINDER_CACHE_ENABLED, DeviceImpl.class,
			FINDER_CLASS_NAME_ENTITY, "fetchByPortalUserId_DeviceIP",
			new String[] { Long.class.getName(), String.class.getName() },
			DeviceModelImpl.PORTALUSERID_COLUMN_BITMASK |
			DeviceModelImpl.DEVICEIP_COLUMN_BITMASK);
	public static final FinderPath FINDER_PATH_COUNT_BY_PORTALUSERID_DEVICEIP = new FinderPath(DeviceModelImpl.ENTITY_CACHE_ENABLED,
			DeviceModelImpl.FINDER_CACHE_ENABLED, Long.class,
			FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION,
			"countByPortalUserId_DeviceIP",
			new String[] { Long.class.getName(), String.class.getName() });

	/**
	 * Returns the device where portalUserId = &#63; and deviceIP = &#63; or throws a {@link NoSuchDeviceException} if it could not be found.
	 *
	 * @param portalUserId the portal user ID
	 * @param deviceIP the device ip
	 * @return the matching device
	 * @throws NoSuchDeviceException if a matching device could not be found
	 */
	@Override
	public Device findByPortalUserId_DeviceIP(long portalUserId, String deviceIP)
		throws NoSuchDeviceException {
		Device device = fetchByPortalUserId_DeviceIP(portalUserId, deviceIP);

		if (device == null) {
			StringBundler msg = new StringBundler(6);

			msg.append(_NO_SUCH_ENTITY_WITH_KEY);

			msg.append("portalUserId=");
			msg.append(portalUserId);

			msg.append(", deviceIP=");
			msg.append(deviceIP);

			msg.append("}");

			if (_log.isDebugEnabled()) {
				_log.debug(msg.toString());
			}

			throw new NoSuchDeviceException(msg.toString());
		}

		return device;
	}

	/**
	 * Returns the device where portalUserId = &#63; and deviceIP = &#63; or returns <code>null</code> if it could not be found. Uses the finder cache.
	 *
	 * @param portalUserId the portal user ID
	 * @param deviceIP the device ip
	 * @return the matching device, or <code>null</code> if a matching device could not be found
	 */
	@Override
	public Device fetchByPortalUserId_DeviceIP(long portalUserId,
		String deviceIP) {
		return fetchByPortalUserId_DeviceIP(portalUserId, deviceIP, true);
	}

	/**
	 * Returns the device where portalUserId = &#63; and deviceIP = &#63; or returns <code>null</code> if it could not be found, optionally using the finder cache.
	 *
	 * @param portalUserId the portal user ID
	 * @param deviceIP the device ip
	 * @param retrieveFromCache whether to retrieve from the finder cache
	 * @return the matching device, or <code>null</code> if a matching device could not be found
	 */
	@Override
	public Device fetchByPortalUserId_DeviceIP(long portalUserId,
		String deviceIP, boolean retrieveFromCache) {
		deviceIP = Objects.toString(deviceIP, "");

		Object[] finderArgs = new Object[] { portalUserId, deviceIP };

		Object result = null;

		if (retrieveFromCache) {
			result = finderCache.getResult(FINDER_PATH_FETCH_BY_PORTALUSERID_DEVICEIP,
					finderArgs, this);
		}

		if (result instanceof Device) {
			Device device = (Device)result;

			if ((portalUserId != device.getPortalUserId()) ||
					!Objects.equals(deviceIP, device.getDeviceIP())) {
				result = null;
			}
		}

		if (result == null) {
			StringBundler query = new StringBundler(4);

			query.append(_SQL_SELECT_DEVICE_WHERE);

			query.append(_FINDER_COLUMN_PORTALUSERID_DEVICEIP_PORTALUSERID_2);

			boolean bindDeviceIP = false;

			if (deviceIP.isEmpty()) {
				query.append(_FINDER_COLUMN_PORTALUSERID_DEVICEIP_DEVICEIP_3);
			}
			else {
				bindDeviceIP = true;

				query.append(_FINDER_COLUMN_PORTALUSERID_DEVICEIP_DEVICEIP_2);
			}

			String sql = query.toString();

			Session session = null;

			try {
				session = openSession();

				Query q = session.createQuery(sql);

				QueryPos qPos = QueryPos.getInstance(q);

				qPos.add(portalUserId);

				if (bindDeviceIP) {
					qPos.add(deviceIP);
				}

				List<Device> list = q.list();

				if (list.isEmpty()) {
					finderCache.putResult(FINDER_PATH_FETCH_BY_PORTALUSERID_DEVICEIP,
						finderArgs, list);
				}
				else {
					if (list.size() > 1) {
						Collections.sort(list, Collections.reverseOrder());

						if (_log.isWarnEnabled()) {
							_log.warn(
								"DevicePersistenceImpl.fetchByPortalUserId_DeviceIP(long, String, boolean) with parameters (" +
								StringUtil.merge(finderArgs) +
								") yields a result set with more than 1 result. This violates the logical unique restriction. There is no order guarantee on which result is returned by this finder.");
						}
					}

					Device device = list.get(0);

					result = device;

					cacheResult(device);
				}
			}
			catch (Exception e) {
				finderCache.removeResult(FINDER_PATH_FETCH_BY_PORTALUSERID_DEVICEIP,
					finderArgs);

				throw processException(e);
			}
			finally {
				closeSession(session);
			}
		}

		if (result instanceof List<?>) {
			return null;
		}
		else {
			return (Device)result;
		}
	}

	/**
	 * Removes the device where portalUserId = &#63; and deviceIP = &#63; from the database.
	 *
	 * @param portalUserId the portal user ID
	 * @param deviceIP the device ip
	 * @return the device that was removed
	 */
	@Override
	public Device removeByPortalUserId_DeviceIP(long portalUserId,
		String deviceIP) throws NoSuchDeviceException {
		Device device = findByPortalUserId_DeviceIP(portalUserId, deviceIP);

		return remove(device);
	}

	/**
	 * Returns the number of devices where portalUserId = &#63; and deviceIP = &#63;.
	 *
	 * @param portalUserId the portal user ID
	 * @param deviceIP the device ip
	 * @return the number of matching devices
	 */
	@Override
	public int countByPortalUserId_DeviceIP(long portalUserId, String deviceIP) {
		deviceIP = Objects.toString(deviceIP, "");

		FinderPath finderPath = FINDER_PATH_COUNT_BY_PORTALUSERID_DEVICEIP;

		Object[] finderArgs = new Object[] { portalUserId, deviceIP };

		Long count = (Long)finderCache.getResult(finderPath, finderArgs, this);

		if (count == null) {
			StringBundler query = new StringBundler(3);

			query.append(_SQL_COUNT_DEVICE_WHERE);

			query.append(_FINDER_COLUMN_PORTALUSERID_DEVICEIP_PORTALUSERID_2);

			boolean bindDeviceIP = false;

			if (deviceIP.isEmpty()) {
				query.append(_FINDER_COLUMN_PORTALUSERID_DEVICEIP_DEVICEIP_3);
			}
			else {
				bindDeviceIP = true;

				query.append(_FINDER_COLUMN_PORTALUSERID_DEVICEIP_DEVICEIP_2);
			}

			String sql = query.toString();

			Session session = null;

			try {
				session = openSession();

				Query q = session.createQuery(sql);

				QueryPos qPos = QueryPos.getInstance(q);

				qPos.add(portalUserId);

				if (bindDeviceIP) {
					qPos.add(deviceIP);
				}

				count = (Long)q.uniqueResult();

				finderCache.putResult(finderPath, finderArgs, count);
			}
			catch (Exception e) {
				finderCache.removeResult(finderPath, finderArgs);

				throw processException(e);
			}
			finally {
				closeSession(session);
			}
		}

		return count.intValue();
	}

	private static final String _FINDER_COLUMN_PORTALUSERID_DEVICEIP_PORTALUSERID_2 =
		"device.portalUserId = ? AND ";
	private static final String _FINDER_COLUMN_PORTALUSERID_DEVICEIP_DEVICEIP_2 = "device.deviceIP = ?";
	private static final String _FINDER_COLUMN_PORTALUSERID_DEVICEIP_DEVICEIP_3 = "(device.deviceIP IS NULL OR device.deviceIP = '')";
	public static final FinderPath FINDER_PATH_WITH_PAGINATION_FIND_BY_VERIFIED_PORTALUSERID =
		new FinderPath(DeviceModelImpl.ENTITY_CACHE_ENABLED,
			DeviceModelImpl.FINDER_CACHE_ENABLED, DeviceImpl.class,
			FINDER_CLASS_NAME_LIST_WITH_PAGINATION,
			"findByVerified_PortalUserId",
			new String[] {
				Boolean.class.getName(), Long.class.getName(),
				
			Integer.class.getName(), Integer.class.getName(),
				OrderByComparator.class.getName()
			});
	public static final FinderPath FINDER_PATH_WITHOUT_PAGINATION_FIND_BY_VERIFIED_PORTALUSERID =
		new FinderPath(DeviceModelImpl.ENTITY_CACHE_ENABLED,
			DeviceModelImpl.FINDER_CACHE_ENABLED, DeviceImpl.class,
			FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION,
			"findByVerified_PortalUserId",
			new String[] { Boolean.class.getName(), Long.class.getName() },
			DeviceModelImpl.VERIFIED_COLUMN_BITMASK |
			DeviceModelImpl.PORTALUSERID_COLUMN_BITMASK);
	public static final FinderPath FINDER_PATH_COUNT_BY_VERIFIED_PORTALUSERID = new FinderPath(DeviceModelImpl.ENTITY_CACHE_ENABLED,
			DeviceModelImpl.FINDER_CACHE_ENABLED, Long.class,
			FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION,
			"countByVerified_PortalUserId",
			new String[] { Boolean.class.getName(), Long.class.getName() });

	/**
	 * Returns all the devices where verified = &#63; and portalUserId = &#63;.
	 *
	 * @param verified the verified
	 * @param portalUserId the portal user ID
	 * @return the matching devices
	 */
	@Override
	public List<Device> findByVerified_PortalUserId(boolean verified,
		long portalUserId) {
		return findByVerified_PortalUserId(verified, portalUserId,
			QueryUtil.ALL_POS, QueryUtil.ALL_POS, null);
	}

	/**
	 * Returns a range of all the devices where verified = &#63; and portalUserId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to {@link QueryUtil#ALL_POS} will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent and pagination is required (<code>start</code> and <code>end</code> are not {@link QueryUtil#ALL_POS}), then the query will include the default ORDER BY logic from {@link DeviceModelImpl}. If both <code>orderByComparator</code> and pagination are absent, for performance reasons, the query will not have an ORDER BY clause and the returned result set will be sorted on by the primary key in an ascending order.
	 * </p>
	 *
	 * @param verified the verified
	 * @param portalUserId the portal user ID
	 * @param start the lower bound of the range of devices
	 * @param end the upper bound of the range of devices (not inclusive)
	 * @return the range of matching devices
	 */
	@Override
	public List<Device> findByVerified_PortalUserId(boolean verified,
		long portalUserId, int start, int end) {
		return findByVerified_PortalUserId(verified, portalUserId, start, end,
			null);
	}

	/**
	 * Returns an ordered range of all the devices where verified = &#63; and portalUserId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to {@link QueryUtil#ALL_POS} will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent and pagination is required (<code>start</code> and <code>end</code> are not {@link QueryUtil#ALL_POS}), then the query will include the default ORDER BY logic from {@link DeviceModelImpl}. If both <code>orderByComparator</code> and pagination are absent, for performance reasons, the query will not have an ORDER BY clause and the returned result set will be sorted on by the primary key in an ascending order.
	 * </p>
	 *
	 * @param verified the verified
	 * @param portalUserId the portal user ID
	 * @param start the lower bound of the range of devices
	 * @param end the upper bound of the range of devices (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching devices
	 */
	@Override
	public List<Device> findByVerified_PortalUserId(boolean verified,
		long portalUserId, int start, int end,
		OrderByComparator<Device> orderByComparator) {
		return findByVerified_PortalUserId(verified, portalUserId, start, end,
			orderByComparator, true);
	}

	/**
	 * Returns an ordered range of all the devices where verified = &#63; and portalUserId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to {@link QueryUtil#ALL_POS} will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent and pagination is required (<code>start</code> and <code>end</code> are not {@link QueryUtil#ALL_POS}), then the query will include the default ORDER BY logic from {@link DeviceModelImpl}. If both <code>orderByComparator</code> and pagination are absent, for performance reasons, the query will not have an ORDER BY clause and the returned result set will be sorted on by the primary key in an ascending order.
	 * </p>
	 *
	 * @param verified the verified
	 * @param portalUserId the portal user ID
	 * @param start the lower bound of the range of devices
	 * @param end the upper bound of the range of devices (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param retrieveFromCache whether to retrieve from the finder cache
	 * @return the ordered range of matching devices
	 */
	@Override
	public List<Device> findByVerified_PortalUserId(boolean verified,
		long portalUserId, int start, int end,
		OrderByComparator<Device> orderByComparator, boolean retrieveFromCache) {
		boolean pagination = true;
		FinderPath finderPath = null;
		Object[] finderArgs = null;

		if ((start == QueryUtil.ALL_POS) && (end == QueryUtil.ALL_POS) &&
				(orderByComparator == null)) {
			pagination = false;
			finderPath = FINDER_PATH_WITHOUT_PAGINATION_FIND_BY_VERIFIED_PORTALUSERID;
			finderArgs = new Object[] { verified, portalUserId };
		}
		else {
			finderPath = FINDER_PATH_WITH_PAGINATION_FIND_BY_VERIFIED_PORTALUSERID;
			finderArgs = new Object[] {
					verified, portalUserId,
					
					start, end, orderByComparator
				};
		}

		List<Device> list = null;

		if (retrieveFromCache) {
			list = (List<Device>)finderCache.getResult(finderPath, finderArgs,
					this);

			if ((list != null) && !list.isEmpty()) {
				for (Device device : list) {
					if ((verified != device.isVerified()) ||
							(portalUserId != device.getPortalUserId())) {
						list = null;

						break;
					}
				}
			}
		}

		if (list == null) {
			StringBundler query = null;

			if (orderByComparator != null) {
				query = new StringBundler(4 +
						(orderByComparator.getOrderByFields().length * 2));
			}
			else {
				query = new StringBundler(4);
			}

			query.append(_SQL_SELECT_DEVICE_WHERE);

			query.append(_FINDER_COLUMN_VERIFIED_PORTALUSERID_VERIFIED_2);

			query.append(_FINDER_COLUMN_VERIFIED_PORTALUSERID_PORTALUSERID_2);

			if (orderByComparator != null) {
				appendOrderByComparator(query, _ORDER_BY_ENTITY_ALIAS,
					orderByComparator);
			}
			else
			 if (pagination) {
				query.append(DeviceModelImpl.ORDER_BY_JPQL);
			}

			String sql = query.toString();

			Session session = null;

			try {
				session = openSession();

				Query q = session.createQuery(sql);

				QueryPos qPos = QueryPos.getInstance(q);

				qPos.add(verified);

				qPos.add(portalUserId);

				if (!pagination) {
					list = (List<Device>)QueryUtil.list(q, getDialect(), start,
							end, false);

					Collections.sort(list);

					list = Collections.unmodifiableList(list);
				}
				else {
					list = (List<Device>)QueryUtil.list(q, getDialect(), start,
							end);
				}

				cacheResult(list);

				finderCache.putResult(finderPath, finderArgs, list);
			}
			catch (Exception e) {
				finderCache.removeResult(finderPath, finderArgs);

				throw processException(e);
			}
			finally {
				closeSession(session);
			}
		}

		return list;
	}

	/**
	 * Returns the first device in the ordered set where verified = &#63; and portalUserId = &#63;.
	 *
	 * @param verified the verified
	 * @param portalUserId the portal user ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching device
	 * @throws NoSuchDeviceException if a matching device could not be found
	 */
	@Override
	public Device findByVerified_PortalUserId_First(boolean verified,
		long portalUserId, OrderByComparator<Device> orderByComparator)
		throws NoSuchDeviceException {
		Device device = fetchByVerified_PortalUserId_First(verified,
				portalUserId, orderByComparator);

		if (device != null) {
			return device;
		}

		StringBundler msg = new StringBundler(6);

		msg.append(_NO_SUCH_ENTITY_WITH_KEY);

		msg.append("verified=");
		msg.append(verified);

		msg.append(", portalUserId=");
		msg.append(portalUserId);

		msg.append("}");

		throw new NoSuchDeviceException(msg.toString());
	}

	/**
	 * Returns the first device in the ordered set where verified = &#63; and portalUserId = &#63;.
	 *
	 * @param verified the verified
	 * @param portalUserId the portal user ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching device, or <code>null</code> if a matching device could not be found
	 */
	@Override
	public Device fetchByVerified_PortalUserId_First(boolean verified,
		long portalUserId, OrderByComparator<Device> orderByComparator) {
		List<Device> list = findByVerified_PortalUserId(verified, portalUserId,
				0, 1, orderByComparator);

		if (!list.isEmpty()) {
			return list.get(0);
		}

		return null;
	}

	/**
	 * Returns the last device in the ordered set where verified = &#63; and portalUserId = &#63;.
	 *
	 * @param verified the verified
	 * @param portalUserId the portal user ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching device
	 * @throws NoSuchDeviceException if a matching device could not be found
	 */
	@Override
	public Device findByVerified_PortalUserId_Last(boolean verified,
		long portalUserId, OrderByComparator<Device> orderByComparator)
		throws NoSuchDeviceException {
		Device device = fetchByVerified_PortalUserId_Last(verified,
				portalUserId, orderByComparator);

		if (device != null) {
			return device;
		}

		StringBundler msg = new StringBundler(6);

		msg.append(_NO_SUCH_ENTITY_WITH_KEY);

		msg.append("verified=");
		msg.append(verified);

		msg.append(", portalUserId=");
		msg.append(portalUserId);

		msg.append("}");

		throw new NoSuchDeviceException(msg.toString());
	}

	/**
	 * Returns the last device in the ordered set where verified = &#63; and portalUserId = &#63;.
	 *
	 * @param verified the verified
	 * @param portalUserId the portal user ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching device, or <code>null</code> if a matching device could not be found
	 */
	@Override
	public Device fetchByVerified_PortalUserId_Last(boolean verified,
		long portalUserId, OrderByComparator<Device> orderByComparator) {
		int count = countByVerified_PortalUserId(verified, portalUserId);

		if (count == 0) {
			return null;
		}

		List<Device> list = findByVerified_PortalUserId(verified, portalUserId,
				count - 1, count, orderByComparator);

		if (!list.isEmpty()) {
			return list.get(0);
		}

		return null;
	}

	/**
	 * Returns the devices before and after the current device in the ordered set where verified = &#63; and portalUserId = &#63;.
	 *
	 * @param deviceId the primary key of the current device
	 * @param verified the verified
	 * @param portalUserId the portal user ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the previous, current, and next device
	 * @throws NoSuchDeviceException if a device with the primary key could not be found
	 */
	@Override
	public Device[] findByVerified_PortalUserId_PrevAndNext(long deviceId,
		boolean verified, long portalUserId,
		OrderByComparator<Device> orderByComparator)
		throws NoSuchDeviceException {
		Device device = findByPrimaryKey(deviceId);

		Session session = null;

		try {
			session = openSession();

			Device[] array = new DeviceImpl[3];

			array[0] = getByVerified_PortalUserId_PrevAndNext(session, device,
					verified, portalUserId, orderByComparator, true);

			array[1] = device;

			array[2] = getByVerified_PortalUserId_PrevAndNext(session, device,
					verified, portalUserId, orderByComparator, false);

			return array;
		}
		catch (Exception e) {
			throw processException(e);
		}
		finally {
			closeSession(session);
		}
	}

	protected Device getByVerified_PortalUserId_PrevAndNext(Session session,
		Device device, boolean verified, long portalUserId,
		OrderByComparator<Device> orderByComparator, boolean previous) {
		StringBundler query = null;

		if (orderByComparator != null) {
			query = new StringBundler(5 +
					(orderByComparator.getOrderByConditionFields().length * 3) +
					(orderByComparator.getOrderByFields().length * 3));
		}
		else {
			query = new StringBundler(4);
		}

		query.append(_SQL_SELECT_DEVICE_WHERE);

		query.append(_FINDER_COLUMN_VERIFIED_PORTALUSERID_VERIFIED_2);

		query.append(_FINDER_COLUMN_VERIFIED_PORTALUSERID_PORTALUSERID_2);

		if (orderByComparator != null) {
			String[] orderByConditionFields = orderByComparator.getOrderByConditionFields();

			if (orderByConditionFields.length > 0) {
				query.append(WHERE_AND);
			}

			for (int i = 0; i < orderByConditionFields.length; i++) {
				query.append(_ORDER_BY_ENTITY_ALIAS);
				query.append(orderByConditionFields[i]);

				if ((i + 1) < orderByConditionFields.length) {
					if (orderByComparator.isAscending() ^ previous) {
						query.append(WHERE_GREATER_THAN_HAS_NEXT);
					}
					else {
						query.append(WHERE_LESSER_THAN_HAS_NEXT);
					}
				}
				else {
					if (orderByComparator.isAscending() ^ previous) {
						query.append(WHERE_GREATER_THAN);
					}
					else {
						query.append(WHERE_LESSER_THAN);
					}
				}
			}

			query.append(ORDER_BY_CLAUSE);

			String[] orderByFields = orderByComparator.getOrderByFields();

			for (int i = 0; i < orderByFields.length; i++) {
				query.append(_ORDER_BY_ENTITY_ALIAS);
				query.append(orderByFields[i]);

				if ((i + 1) < orderByFields.length) {
					if (orderByComparator.isAscending() ^ previous) {
						query.append(ORDER_BY_ASC_HAS_NEXT);
					}
					else {
						query.append(ORDER_BY_DESC_HAS_NEXT);
					}
				}
				else {
					if (orderByComparator.isAscending() ^ previous) {
						query.append(ORDER_BY_ASC);
					}
					else {
						query.append(ORDER_BY_DESC);
					}
				}
			}
		}
		else {
			query.append(DeviceModelImpl.ORDER_BY_JPQL);
		}

		String sql = query.toString();

		Query q = session.createQuery(sql);

		q.setFirstResult(0);
		q.setMaxResults(2);

		QueryPos qPos = QueryPos.getInstance(q);

		qPos.add(verified);

		qPos.add(portalUserId);

		if (orderByComparator != null) {
			for (Object orderByConditionValue : orderByComparator.getOrderByConditionValues(
					device)) {
				qPos.add(orderByConditionValue);
			}
		}

		List<Device> list = q.list();

		if (list.size() == 2) {
			return list.get(1);
		}
		else {
			return null;
		}
	}

	/**
	 * Removes all the devices where verified = &#63; and portalUserId = &#63; from the database.
	 *
	 * @param verified the verified
	 * @param portalUserId the portal user ID
	 */
	@Override
	public void removeByVerified_PortalUserId(boolean verified,
		long portalUserId) {
		for (Device device : findByVerified_PortalUserId(verified,
				portalUserId, QueryUtil.ALL_POS, QueryUtil.ALL_POS, null)) {
			remove(device);
		}
	}

	/**
	 * Returns the number of devices where verified = &#63; and portalUserId = &#63;.
	 *
	 * @param verified the verified
	 * @param portalUserId the portal user ID
	 * @return the number of matching devices
	 */
	@Override
	public int countByVerified_PortalUserId(boolean verified, long portalUserId) {
		FinderPath finderPath = FINDER_PATH_COUNT_BY_VERIFIED_PORTALUSERID;

		Object[] finderArgs = new Object[] { verified, portalUserId };

		Long count = (Long)finderCache.getResult(finderPath, finderArgs, this);

		if (count == null) {
			StringBundler query = new StringBundler(3);

			query.append(_SQL_COUNT_DEVICE_WHERE);

			query.append(_FINDER_COLUMN_VERIFIED_PORTALUSERID_VERIFIED_2);

			query.append(_FINDER_COLUMN_VERIFIED_PORTALUSERID_PORTALUSERID_2);

			String sql = query.toString();

			Session session = null;

			try {
				session = openSession();

				Query q = session.createQuery(sql);

				QueryPos qPos = QueryPos.getInstance(q);

				qPos.add(verified);

				qPos.add(portalUserId);

				count = (Long)q.uniqueResult();

				finderCache.putResult(finderPath, finderArgs, count);
			}
			catch (Exception e) {
				finderCache.removeResult(finderPath, finderArgs);

				throw processException(e);
			}
			finally {
				closeSession(session);
			}
		}

		return count.intValue();
	}

	private static final String _FINDER_COLUMN_VERIFIED_PORTALUSERID_VERIFIED_2 = "device.verified = ? AND ";
	private static final String _FINDER_COLUMN_VERIFIED_PORTALUSERID_PORTALUSERID_2 =
		"device.portalUserId = ?";
	public static final FinderPath FINDER_PATH_FETCH_BY_VERIFIED_PORTALUSERID_DEVICEIP =
		new FinderPath(DeviceModelImpl.ENTITY_CACHE_ENABLED,
			DeviceModelImpl.FINDER_CACHE_ENABLED, DeviceImpl.class,
			FINDER_CLASS_NAME_ENTITY, "fetchByVerified_PortalUserId_DeviceIP",
			new String[] {
				Boolean.class.getName(), Long.class.getName(),
				String.class.getName()
			},
			DeviceModelImpl.VERIFIED_COLUMN_BITMASK |
			DeviceModelImpl.PORTALUSERID_COLUMN_BITMASK |
			DeviceModelImpl.DEVICEIP_COLUMN_BITMASK);
	public static final FinderPath FINDER_PATH_COUNT_BY_VERIFIED_PORTALUSERID_DEVICEIP =
		new FinderPath(DeviceModelImpl.ENTITY_CACHE_ENABLED,
			DeviceModelImpl.FINDER_CACHE_ENABLED, Long.class,
			FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION,
			"countByVerified_PortalUserId_DeviceIP",
			new String[] {
				Boolean.class.getName(), Long.class.getName(),
				String.class.getName()
			});

	/**
	 * Returns the device where verified = &#63; and portalUserId = &#63; and deviceIP = &#63; or throws a {@link NoSuchDeviceException} if it could not be found.
	 *
	 * @param verified the verified
	 * @param portalUserId the portal user ID
	 * @param deviceIP the device ip
	 * @return the matching device
	 * @throws NoSuchDeviceException if a matching device could not be found
	 */
	@Override
	public Device findByVerified_PortalUserId_DeviceIP(boolean verified,
		long portalUserId, String deviceIP) throws NoSuchDeviceException {
		Device device = fetchByVerified_PortalUserId_DeviceIP(verified,
				portalUserId, deviceIP);

		if (device == null) {
			StringBundler msg = new StringBundler(8);

			msg.append(_NO_SUCH_ENTITY_WITH_KEY);

			msg.append("verified=");
			msg.append(verified);

			msg.append(", portalUserId=");
			msg.append(portalUserId);

			msg.append(", deviceIP=");
			msg.append(deviceIP);

			msg.append("}");

			if (_log.isDebugEnabled()) {
				_log.debug(msg.toString());
			}

			throw new NoSuchDeviceException(msg.toString());
		}

		return device;
	}

	/**
	 * Returns the device where verified = &#63; and portalUserId = &#63; and deviceIP = &#63; or returns <code>null</code> if it could not be found. Uses the finder cache.
	 *
	 * @param verified the verified
	 * @param portalUserId the portal user ID
	 * @param deviceIP the device ip
	 * @return the matching device, or <code>null</code> if a matching device could not be found
	 */
	@Override
	public Device fetchByVerified_PortalUserId_DeviceIP(boolean verified,
		long portalUserId, String deviceIP) {
		return fetchByVerified_PortalUserId_DeviceIP(verified, portalUserId,
			deviceIP, true);
	}

	/**
	 * Returns the device where verified = &#63; and portalUserId = &#63; and deviceIP = &#63; or returns <code>null</code> if it could not be found, optionally using the finder cache.
	 *
	 * @param verified the verified
	 * @param portalUserId the portal user ID
	 * @param deviceIP the device ip
	 * @param retrieveFromCache whether to retrieve from the finder cache
	 * @return the matching device, or <code>null</code> if a matching device could not be found
	 */
	@Override
	public Device fetchByVerified_PortalUserId_DeviceIP(boolean verified,
		long portalUserId, String deviceIP, boolean retrieveFromCache) {
		deviceIP = Objects.toString(deviceIP, "");

		Object[] finderArgs = new Object[] { verified, portalUserId, deviceIP };

		Object result = null;

		if (retrieveFromCache) {
			result = finderCache.getResult(FINDER_PATH_FETCH_BY_VERIFIED_PORTALUSERID_DEVICEIP,
					finderArgs, this);
		}

		if (result instanceof Device) {
			Device device = (Device)result;

			if ((verified != device.isVerified()) ||
					(portalUserId != device.getPortalUserId()) ||
					!Objects.equals(deviceIP, device.getDeviceIP())) {
				result = null;
			}
		}

		if (result == null) {
			StringBundler query = new StringBundler(5);

			query.append(_SQL_SELECT_DEVICE_WHERE);

			query.append(_FINDER_COLUMN_VERIFIED_PORTALUSERID_DEVICEIP_VERIFIED_2);

			query.append(_FINDER_COLUMN_VERIFIED_PORTALUSERID_DEVICEIP_PORTALUSERID_2);

			boolean bindDeviceIP = false;

			if (deviceIP.isEmpty()) {
				query.append(_FINDER_COLUMN_VERIFIED_PORTALUSERID_DEVICEIP_DEVICEIP_3);
			}
			else {
				bindDeviceIP = true;

				query.append(_FINDER_COLUMN_VERIFIED_PORTALUSERID_DEVICEIP_DEVICEIP_2);
			}

			String sql = query.toString();

			Session session = null;

			try {
				session = openSession();

				Query q = session.createQuery(sql);

				QueryPos qPos = QueryPos.getInstance(q);

				qPos.add(verified);

				qPos.add(portalUserId);

				if (bindDeviceIP) {
					qPos.add(deviceIP);
				}

				List<Device> list = q.list();

				if (list.isEmpty()) {
					finderCache.putResult(FINDER_PATH_FETCH_BY_VERIFIED_PORTALUSERID_DEVICEIP,
						finderArgs, list);
				}
				else {
					if (list.size() > 1) {
						Collections.sort(list, Collections.reverseOrder());

						if (_log.isWarnEnabled()) {
							_log.warn(
								"DevicePersistenceImpl.fetchByVerified_PortalUserId_DeviceIP(boolean, long, String, boolean) with parameters (" +
								StringUtil.merge(finderArgs) +
								") yields a result set with more than 1 result. This violates the logical unique restriction. There is no order guarantee on which result is returned by this finder.");
						}
					}

					Device device = list.get(0);

					result = device;

					cacheResult(device);
				}
			}
			catch (Exception e) {
				finderCache.removeResult(FINDER_PATH_FETCH_BY_VERIFIED_PORTALUSERID_DEVICEIP,
					finderArgs);

				throw processException(e);
			}
			finally {
				closeSession(session);
			}
		}

		if (result instanceof List<?>) {
			return null;
		}
		else {
			return (Device)result;
		}
	}

	/**
	 * Removes the device where verified = &#63; and portalUserId = &#63; and deviceIP = &#63; from the database.
	 *
	 * @param verified the verified
	 * @param portalUserId the portal user ID
	 * @param deviceIP the device ip
	 * @return the device that was removed
	 */
	@Override
	public Device removeByVerified_PortalUserId_DeviceIP(boolean verified,
		long portalUserId, String deviceIP) throws NoSuchDeviceException {
		Device device = findByVerified_PortalUserId_DeviceIP(verified,
				portalUserId, deviceIP);

		return remove(device);
	}

	/**
	 * Returns the number of devices where verified = &#63; and portalUserId = &#63; and deviceIP = &#63;.
	 *
	 * @param verified the verified
	 * @param portalUserId the portal user ID
	 * @param deviceIP the device ip
	 * @return the number of matching devices
	 */
	@Override
	public int countByVerified_PortalUserId_DeviceIP(boolean verified,
		long portalUserId, String deviceIP) {
		deviceIP = Objects.toString(deviceIP, "");

		FinderPath finderPath = FINDER_PATH_COUNT_BY_VERIFIED_PORTALUSERID_DEVICEIP;

		Object[] finderArgs = new Object[] { verified, portalUserId, deviceIP };

		Long count = (Long)finderCache.getResult(finderPath, finderArgs, this);

		if (count == null) {
			StringBundler query = new StringBundler(4);

			query.append(_SQL_COUNT_DEVICE_WHERE);

			query.append(_FINDER_COLUMN_VERIFIED_PORTALUSERID_DEVICEIP_VERIFIED_2);

			query.append(_FINDER_COLUMN_VERIFIED_PORTALUSERID_DEVICEIP_PORTALUSERID_2);

			boolean bindDeviceIP = false;

			if (deviceIP.isEmpty()) {
				query.append(_FINDER_COLUMN_VERIFIED_PORTALUSERID_DEVICEIP_DEVICEIP_3);
			}
			else {
				bindDeviceIP = true;

				query.append(_FINDER_COLUMN_VERIFIED_PORTALUSERID_DEVICEIP_DEVICEIP_2);
			}

			String sql = query.toString();

			Session session = null;

			try {
				session = openSession();

				Query q = session.createQuery(sql);

				QueryPos qPos = QueryPos.getInstance(q);

				qPos.add(verified);

				qPos.add(portalUserId);

				if (bindDeviceIP) {
					qPos.add(deviceIP);
				}

				count = (Long)q.uniqueResult();

				finderCache.putResult(finderPath, finderArgs, count);
			}
			catch (Exception e) {
				finderCache.removeResult(finderPath, finderArgs);

				throw processException(e);
			}
			finally {
				closeSession(session);
			}
		}

		return count.intValue();
	}

	private static final String _FINDER_COLUMN_VERIFIED_PORTALUSERID_DEVICEIP_VERIFIED_2 =
		"device.verified = ? AND ";
	private static final String _FINDER_COLUMN_VERIFIED_PORTALUSERID_DEVICEIP_PORTALUSERID_2 =
		"device.portalUserId = ? AND ";
	private static final String _FINDER_COLUMN_VERIFIED_PORTALUSERID_DEVICEIP_DEVICEIP_2 =
		"device.deviceIP = ?";
	private static final String _FINDER_COLUMN_VERIFIED_PORTALUSERID_DEVICEIP_DEVICEIP_3 =
		"(device.deviceIP IS NULL OR device.deviceIP = '')";
	public static final FinderPath FINDER_PATH_FETCH_BY_TEMPDEVICE_PORTALUSERID_DEVICEIP =
		new FinderPath(DeviceModelImpl.ENTITY_CACHE_ENABLED,
			DeviceModelImpl.FINDER_CACHE_ENABLED, DeviceImpl.class,
			FINDER_CLASS_NAME_ENTITY,
			"fetchByTempDevice_PortalUserId_DeviceIP",
			new String[] {
				Boolean.class.getName(), Long.class.getName(),
				String.class.getName()
			},
			DeviceModelImpl.TEMPDEVICE_COLUMN_BITMASK |
			DeviceModelImpl.PORTALUSERID_COLUMN_BITMASK |
			DeviceModelImpl.DEVICEIP_COLUMN_BITMASK);
	public static final FinderPath FINDER_PATH_COUNT_BY_TEMPDEVICE_PORTALUSERID_DEVICEIP =
		new FinderPath(DeviceModelImpl.ENTITY_CACHE_ENABLED,
			DeviceModelImpl.FINDER_CACHE_ENABLED, Long.class,
			FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION,
			"countByTempDevice_PortalUserId_DeviceIP",
			new String[] {
				Boolean.class.getName(), Long.class.getName(),
				String.class.getName()
			});

	/**
	 * Returns the device where tempDevice = &#63; and portalUserId = &#63; and deviceIP = &#63; or throws a {@link NoSuchDeviceException} if it could not be found.
	 *
	 * @param tempDevice the temp device
	 * @param portalUserId the portal user ID
	 * @param deviceIP the device ip
	 * @return the matching device
	 * @throws NoSuchDeviceException if a matching device could not be found
	 */
	@Override
	public Device findByTempDevice_PortalUserId_DeviceIP(boolean tempDevice,
		long portalUserId, String deviceIP) throws NoSuchDeviceException {
		Device device = fetchByTempDevice_PortalUserId_DeviceIP(tempDevice,
				portalUserId, deviceIP);

		if (device == null) {
			StringBundler msg = new StringBundler(8);

			msg.append(_NO_SUCH_ENTITY_WITH_KEY);

			msg.append("tempDevice=");
			msg.append(tempDevice);

			msg.append(", portalUserId=");
			msg.append(portalUserId);

			msg.append(", deviceIP=");
			msg.append(deviceIP);

			msg.append("}");

			if (_log.isDebugEnabled()) {
				_log.debug(msg.toString());
			}

			throw new NoSuchDeviceException(msg.toString());
		}

		return device;
	}

	/**
	 * Returns the device where tempDevice = &#63; and portalUserId = &#63; and deviceIP = &#63; or returns <code>null</code> if it could not be found. Uses the finder cache.
	 *
	 * @param tempDevice the temp device
	 * @param portalUserId the portal user ID
	 * @param deviceIP the device ip
	 * @return the matching device, or <code>null</code> if a matching device could not be found
	 */
	@Override
	public Device fetchByTempDevice_PortalUserId_DeviceIP(boolean tempDevice,
		long portalUserId, String deviceIP) {
		return fetchByTempDevice_PortalUserId_DeviceIP(tempDevice,
			portalUserId, deviceIP, true);
	}

	/**
	 * Returns the device where tempDevice = &#63; and portalUserId = &#63; and deviceIP = &#63; or returns <code>null</code> if it could not be found, optionally using the finder cache.
	 *
	 * @param tempDevice the temp device
	 * @param portalUserId the portal user ID
	 * @param deviceIP the device ip
	 * @param retrieveFromCache whether to retrieve from the finder cache
	 * @return the matching device, or <code>null</code> if a matching device could not be found
	 */
	@Override
	public Device fetchByTempDevice_PortalUserId_DeviceIP(boolean tempDevice,
		long portalUserId, String deviceIP, boolean retrieveFromCache) {
		deviceIP = Objects.toString(deviceIP, "");

		Object[] finderArgs = new Object[] { tempDevice, portalUserId, deviceIP };

		Object result = null;

		if (retrieveFromCache) {
			result = finderCache.getResult(FINDER_PATH_FETCH_BY_TEMPDEVICE_PORTALUSERID_DEVICEIP,
					finderArgs, this);
		}

		if (result instanceof Device) {
			Device device = (Device)result;

			if ((tempDevice != device.isTempDevice()) ||
					(portalUserId != device.getPortalUserId()) ||
					!Objects.equals(deviceIP, device.getDeviceIP())) {
				result = null;
			}
		}

		if (result == null) {
			StringBundler query = new StringBundler(5);

			query.append(_SQL_SELECT_DEVICE_WHERE);

			query.append(_FINDER_COLUMN_TEMPDEVICE_PORTALUSERID_DEVICEIP_TEMPDEVICE_2);

			query.append(_FINDER_COLUMN_TEMPDEVICE_PORTALUSERID_DEVICEIP_PORTALUSERID_2);

			boolean bindDeviceIP = false;

			if (deviceIP.isEmpty()) {
				query.append(_FINDER_COLUMN_TEMPDEVICE_PORTALUSERID_DEVICEIP_DEVICEIP_3);
			}
			else {
				bindDeviceIP = true;

				query.append(_FINDER_COLUMN_TEMPDEVICE_PORTALUSERID_DEVICEIP_DEVICEIP_2);
			}

			String sql = query.toString();

			Session session = null;

			try {
				session = openSession();

				Query q = session.createQuery(sql);

				QueryPos qPos = QueryPos.getInstance(q);

				qPos.add(tempDevice);

				qPos.add(portalUserId);

				if (bindDeviceIP) {
					qPos.add(deviceIP);
				}

				List<Device> list = q.list();

				if (list.isEmpty()) {
					finderCache.putResult(FINDER_PATH_FETCH_BY_TEMPDEVICE_PORTALUSERID_DEVICEIP,
						finderArgs, list);
				}
				else {
					if (list.size() > 1) {
						Collections.sort(list, Collections.reverseOrder());

						if (_log.isWarnEnabled()) {
							_log.warn(
								"DevicePersistenceImpl.fetchByTempDevice_PortalUserId_DeviceIP(boolean, long, String, boolean) with parameters (" +
								StringUtil.merge(finderArgs) +
								") yields a result set with more than 1 result. This violates the logical unique restriction. There is no order guarantee on which result is returned by this finder.");
						}
					}

					Device device = list.get(0);

					result = device;

					cacheResult(device);
				}
			}
			catch (Exception e) {
				finderCache.removeResult(FINDER_PATH_FETCH_BY_TEMPDEVICE_PORTALUSERID_DEVICEIP,
					finderArgs);

				throw processException(e);
			}
			finally {
				closeSession(session);
			}
		}

		if (result instanceof List<?>) {
			return null;
		}
		else {
			return (Device)result;
		}
	}

	/**
	 * Removes the device where tempDevice = &#63; and portalUserId = &#63; and deviceIP = &#63; from the database.
	 *
	 * @param tempDevice the temp device
	 * @param portalUserId the portal user ID
	 * @param deviceIP the device ip
	 * @return the device that was removed
	 */
	@Override
	public Device removeByTempDevice_PortalUserId_DeviceIP(boolean tempDevice,
		long portalUserId, String deviceIP) throws NoSuchDeviceException {
		Device device = findByTempDevice_PortalUserId_DeviceIP(tempDevice,
				portalUserId, deviceIP);

		return remove(device);
	}

	/**
	 * Returns the number of devices where tempDevice = &#63; and portalUserId = &#63; and deviceIP = &#63;.
	 *
	 * @param tempDevice the temp device
	 * @param portalUserId the portal user ID
	 * @param deviceIP the device ip
	 * @return the number of matching devices
	 */
	@Override
	public int countByTempDevice_PortalUserId_DeviceIP(boolean tempDevice,
		long portalUserId, String deviceIP) {
		deviceIP = Objects.toString(deviceIP, "");

		FinderPath finderPath = FINDER_PATH_COUNT_BY_TEMPDEVICE_PORTALUSERID_DEVICEIP;

		Object[] finderArgs = new Object[] { tempDevice, portalUserId, deviceIP };

		Long count = (Long)finderCache.getResult(finderPath, finderArgs, this);

		if (count == null) {
			StringBundler query = new StringBundler(4);

			query.append(_SQL_COUNT_DEVICE_WHERE);

			query.append(_FINDER_COLUMN_TEMPDEVICE_PORTALUSERID_DEVICEIP_TEMPDEVICE_2);

			query.append(_FINDER_COLUMN_TEMPDEVICE_PORTALUSERID_DEVICEIP_PORTALUSERID_2);

			boolean bindDeviceIP = false;

			if (deviceIP.isEmpty()) {
				query.append(_FINDER_COLUMN_TEMPDEVICE_PORTALUSERID_DEVICEIP_DEVICEIP_3);
			}
			else {
				bindDeviceIP = true;

				query.append(_FINDER_COLUMN_TEMPDEVICE_PORTALUSERID_DEVICEIP_DEVICEIP_2);
			}

			String sql = query.toString();

			Session session = null;

			try {
				session = openSession();

				Query q = session.createQuery(sql);

				QueryPos qPos = QueryPos.getInstance(q);

				qPos.add(tempDevice);

				qPos.add(portalUserId);

				if (bindDeviceIP) {
					qPos.add(deviceIP);
				}

				count = (Long)q.uniqueResult();

				finderCache.putResult(finderPath, finderArgs, count);
			}
			catch (Exception e) {
				finderCache.removeResult(finderPath, finderArgs);

				throw processException(e);
			}
			finally {
				closeSession(session);
			}
		}

		return count.intValue();
	}

	private static final String _FINDER_COLUMN_TEMPDEVICE_PORTALUSERID_DEVICEIP_TEMPDEVICE_2 =
		"device.tempDevice = ? AND ";
	private static final String _FINDER_COLUMN_TEMPDEVICE_PORTALUSERID_DEVICEIP_PORTALUSERID_2 =
		"device.portalUserId = ? AND ";
	private static final String _FINDER_COLUMN_TEMPDEVICE_PORTALUSERID_DEVICEIP_DEVICEIP_2 =
		"device.deviceIP = ?";
	private static final String _FINDER_COLUMN_TEMPDEVICE_PORTALUSERID_DEVICEIP_DEVICEIP_3 =
		"(device.deviceIP IS NULL OR device.deviceIP = '')";

	public DevicePersistenceImpl() {
		setModelClass(Device.class);
	}

	/**
	 * Caches the device in the entity cache if it is enabled.
	 *
	 * @param device the device
	 */
	@Override
	public void cacheResult(Device device) {
		entityCache.putResult(DeviceModelImpl.ENTITY_CACHE_ENABLED,
			DeviceImpl.class, device.getPrimaryKey(), device);

		finderCache.putResult(FINDER_PATH_FETCH_BY_PORTALUSERID_DEVICEIP,
			new Object[] { device.getPortalUserId(), device.getDeviceIP() },
			device);

		finderCache.putResult(FINDER_PATH_FETCH_BY_VERIFIED_PORTALUSERID_DEVICEIP,
			new Object[] {
				device.isVerified(), device.getPortalUserId(),
				device.getDeviceIP()
			}, device);

		finderCache.putResult(FINDER_PATH_FETCH_BY_TEMPDEVICE_PORTALUSERID_DEVICEIP,
			new Object[] {
				device.isTempDevice(), device.getPortalUserId(),
				device.getDeviceIP()
			}, device);

		device.resetOriginalValues();
	}

	/**
	 * Caches the devices in the entity cache if it is enabled.
	 *
	 * @param devices the devices
	 */
	@Override
	public void cacheResult(List<Device> devices) {
		for (Device device : devices) {
			if (entityCache.getResult(DeviceModelImpl.ENTITY_CACHE_ENABLED,
						DeviceImpl.class, device.getPrimaryKey()) == null) {
				cacheResult(device);
			}
			else {
				device.resetOriginalValues();
			}
		}
	}

	/**
	 * Clears the cache for all devices.
	 *
	 * <p>
	 * The {@link EntityCache} and {@link FinderCache} are both cleared by this method.
	 * </p>
	 */
	@Override
	public void clearCache() {
		entityCache.clearCache(DeviceImpl.class);

		finderCache.clearCache(FINDER_CLASS_NAME_ENTITY);
		finderCache.clearCache(FINDER_CLASS_NAME_LIST_WITH_PAGINATION);
		finderCache.clearCache(FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION);
	}

	/**
	 * Clears the cache for the device.
	 *
	 * <p>
	 * The {@link EntityCache} and {@link FinderCache} are both cleared by this method.
	 * </p>
	 */
	@Override
	public void clearCache(Device device) {
		entityCache.removeResult(DeviceModelImpl.ENTITY_CACHE_ENABLED,
			DeviceImpl.class, device.getPrimaryKey());

		finderCache.clearCache(FINDER_CLASS_NAME_LIST_WITH_PAGINATION);
		finderCache.clearCache(FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION);

		clearUniqueFindersCache((DeviceModelImpl)device, true);
	}

	@Override
	public void clearCache(List<Device> devices) {
		finderCache.clearCache(FINDER_CLASS_NAME_LIST_WITH_PAGINATION);
		finderCache.clearCache(FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION);

		for (Device device : devices) {
			entityCache.removeResult(DeviceModelImpl.ENTITY_CACHE_ENABLED,
				DeviceImpl.class, device.getPrimaryKey());

			clearUniqueFindersCache((DeviceModelImpl)device, true);
		}
	}

	protected void cacheUniqueFindersCache(DeviceModelImpl deviceModelImpl) {
		Object[] args = new Object[] {
				deviceModelImpl.getPortalUserId(), deviceModelImpl.getDeviceIP()
			};

		finderCache.putResult(FINDER_PATH_COUNT_BY_PORTALUSERID_DEVICEIP, args,
			Long.valueOf(1), false);
		finderCache.putResult(FINDER_PATH_FETCH_BY_PORTALUSERID_DEVICEIP, args,
			deviceModelImpl, false);

		args = new Object[] {
				deviceModelImpl.isVerified(), deviceModelImpl.getPortalUserId(),
				deviceModelImpl.getDeviceIP()
			};

		finderCache.putResult(FINDER_PATH_COUNT_BY_VERIFIED_PORTALUSERID_DEVICEIP,
			args, Long.valueOf(1), false);
		finderCache.putResult(FINDER_PATH_FETCH_BY_VERIFIED_PORTALUSERID_DEVICEIP,
			args, deviceModelImpl, false);

		args = new Object[] {
				deviceModelImpl.isTempDevice(),
				deviceModelImpl.getPortalUserId(), deviceModelImpl.getDeviceIP()
			};

		finderCache.putResult(FINDER_PATH_COUNT_BY_TEMPDEVICE_PORTALUSERID_DEVICEIP,
			args, Long.valueOf(1), false);
		finderCache.putResult(FINDER_PATH_FETCH_BY_TEMPDEVICE_PORTALUSERID_DEVICEIP,
			args, deviceModelImpl, false);
	}

	protected void clearUniqueFindersCache(DeviceModelImpl deviceModelImpl,
		boolean clearCurrent) {
		if (clearCurrent) {
			Object[] args = new Object[] {
					deviceModelImpl.getPortalUserId(),
					deviceModelImpl.getDeviceIP()
				};

			finderCache.removeResult(FINDER_PATH_COUNT_BY_PORTALUSERID_DEVICEIP,
				args);
			finderCache.removeResult(FINDER_PATH_FETCH_BY_PORTALUSERID_DEVICEIP,
				args);
		}

		if ((deviceModelImpl.getColumnBitmask() &
				FINDER_PATH_FETCH_BY_PORTALUSERID_DEVICEIP.getColumnBitmask()) != 0) {
			Object[] args = new Object[] {
					deviceModelImpl.getOriginalPortalUserId(),
					deviceModelImpl.getOriginalDeviceIP()
				};

			finderCache.removeResult(FINDER_PATH_COUNT_BY_PORTALUSERID_DEVICEIP,
				args);
			finderCache.removeResult(FINDER_PATH_FETCH_BY_PORTALUSERID_DEVICEIP,
				args);
		}

		if (clearCurrent) {
			Object[] args = new Object[] {
					deviceModelImpl.isVerified(),
					deviceModelImpl.getPortalUserId(),
					deviceModelImpl.getDeviceIP()
				};

			finderCache.removeResult(FINDER_PATH_COUNT_BY_VERIFIED_PORTALUSERID_DEVICEIP,
				args);
			finderCache.removeResult(FINDER_PATH_FETCH_BY_VERIFIED_PORTALUSERID_DEVICEIP,
				args);
		}

		if ((deviceModelImpl.getColumnBitmask() &
				FINDER_PATH_FETCH_BY_VERIFIED_PORTALUSERID_DEVICEIP.getColumnBitmask()) != 0) {
			Object[] args = new Object[] {
					deviceModelImpl.getOriginalVerified(),
					deviceModelImpl.getOriginalPortalUserId(),
					deviceModelImpl.getOriginalDeviceIP()
				};

			finderCache.removeResult(FINDER_PATH_COUNT_BY_VERIFIED_PORTALUSERID_DEVICEIP,
				args);
			finderCache.removeResult(FINDER_PATH_FETCH_BY_VERIFIED_PORTALUSERID_DEVICEIP,
				args);
		}

		if (clearCurrent) {
			Object[] args = new Object[] {
					deviceModelImpl.isTempDevice(),
					deviceModelImpl.getPortalUserId(),
					deviceModelImpl.getDeviceIP()
				};

			finderCache.removeResult(FINDER_PATH_COUNT_BY_TEMPDEVICE_PORTALUSERID_DEVICEIP,
				args);
			finderCache.removeResult(FINDER_PATH_FETCH_BY_TEMPDEVICE_PORTALUSERID_DEVICEIP,
				args);
		}

		if ((deviceModelImpl.getColumnBitmask() &
				FINDER_PATH_FETCH_BY_TEMPDEVICE_PORTALUSERID_DEVICEIP.getColumnBitmask()) != 0) {
			Object[] args = new Object[] {
					deviceModelImpl.getOriginalTempDevice(),
					deviceModelImpl.getOriginalPortalUserId(),
					deviceModelImpl.getOriginalDeviceIP()
				};

			finderCache.removeResult(FINDER_PATH_COUNT_BY_TEMPDEVICE_PORTALUSERID_DEVICEIP,
				args);
			finderCache.removeResult(FINDER_PATH_FETCH_BY_TEMPDEVICE_PORTALUSERID_DEVICEIP,
				args);
		}
	}

	/**
	 * Creates a new device with the primary key. Does not add the device to the database.
	 *
	 * @param deviceId the primary key for the new device
	 * @return the new device
	 */
	@Override
	public Device create(long deviceId) {
		Device device = new DeviceImpl();

		device.setNew(true);
		device.setPrimaryKey(deviceId);

		device.setCompanyId(companyProvider.getCompanyId());

		return device;
	}

	/**
	 * Removes the device with the primary key from the database. Also notifies the appropriate model listeners.
	 *
	 * @param deviceId the primary key of the device
	 * @return the device that was removed
	 * @throws NoSuchDeviceException if a device with the primary key could not be found
	 */
	@Override
	public Device remove(long deviceId) throws NoSuchDeviceException {
		return remove((Serializable)deviceId);
	}

	/**
	 * Removes the device with the primary key from the database. Also notifies the appropriate model listeners.
	 *
	 * @param primaryKey the primary key of the device
	 * @return the device that was removed
	 * @throws NoSuchDeviceException if a device with the primary key could not be found
	 */
	@Override
	public Device remove(Serializable primaryKey) throws NoSuchDeviceException {
		Session session = null;

		try {
			session = openSession();

			Device device = (Device)session.get(DeviceImpl.class, primaryKey);

			if (device == null) {
				if (_log.isDebugEnabled()) {
					_log.debug(_NO_SUCH_ENTITY_WITH_PRIMARY_KEY + primaryKey);
				}

				throw new NoSuchDeviceException(_NO_SUCH_ENTITY_WITH_PRIMARY_KEY +
					primaryKey);
			}

			return remove(device);
		}
		catch (NoSuchDeviceException nsee) {
			throw nsee;
		}
		catch (Exception e) {
			throw processException(e);
		}
		finally {
			closeSession(session);
		}
	}

	@Override
	protected Device removeImpl(Device device) {
		Session session = null;

		try {
			session = openSession();

			if (!session.contains(device)) {
				device = (Device)session.get(DeviceImpl.class,
						device.getPrimaryKeyObj());
			}

			if (device != null) {
				session.delete(device);
			}
		}
		catch (Exception e) {
			throw processException(e);
		}
		finally {
			closeSession(session);
		}

		if (device != null) {
			clearCache(device);
		}

		return device;
	}

	@Override
	public Device updateImpl(Device device) {
		boolean isNew = device.isNew();

		if (!(device instanceof DeviceModelImpl)) {
			InvocationHandler invocationHandler = null;

			if (ProxyUtil.isProxyClass(device.getClass())) {
				invocationHandler = ProxyUtil.getInvocationHandler(device);

				throw new IllegalArgumentException(
					"Implement ModelWrapper in device proxy " +
					invocationHandler.getClass());
			}

			throw new IllegalArgumentException(
				"Implement ModelWrapper in custom Device implementation " +
				device.getClass());
		}

		DeviceModelImpl deviceModelImpl = (DeviceModelImpl)device;

		ServiceContext serviceContext = ServiceContextThreadLocal.getServiceContext();

		Date now = new Date();

		if (isNew && (device.getCreateDate() == null)) {
			if (serviceContext == null) {
				device.setCreateDate(now);
			}
			else {
				device.setCreateDate(serviceContext.getCreateDate(now));
			}
		}

		if (!deviceModelImpl.hasSetModifiedDate()) {
			if (serviceContext == null) {
				device.setModifiedDate(now);
			}
			else {
				device.setModifiedDate(serviceContext.getModifiedDate(now));
			}
		}

		Session session = null;

		try {
			session = openSession();

			if (device.isNew()) {
				session.save(device);

				device.setNew(false);
			}
			else {
				device = (Device)session.merge(device);
			}
		}
		catch (Exception e) {
			throw processException(e);
		}
		finally {
			closeSession(session);
		}

		finderCache.clearCache(FINDER_CLASS_NAME_LIST_WITH_PAGINATION);

		if (!DeviceModelImpl.COLUMN_BITMASK_ENABLED) {
			finderCache.clearCache(FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION);
		}
		else
		 if (isNew) {
			Object[] args = new Object[] { deviceModelImpl.getPortalUserId() };

			finderCache.removeResult(FINDER_PATH_COUNT_BY_PORTALUSERID, args);
			finderCache.removeResult(FINDER_PATH_WITHOUT_PAGINATION_FIND_BY_PORTALUSERID,
				args);

			args = new Object[] {
					deviceModelImpl.isVerified(),
					deviceModelImpl.getPortalUserId()
				};

			finderCache.removeResult(FINDER_PATH_COUNT_BY_VERIFIED_PORTALUSERID,
				args);
			finderCache.removeResult(FINDER_PATH_WITHOUT_PAGINATION_FIND_BY_VERIFIED_PORTALUSERID,
				args);

			finderCache.removeResult(FINDER_PATH_COUNT_ALL, FINDER_ARGS_EMPTY);
			finderCache.removeResult(FINDER_PATH_WITHOUT_PAGINATION_FIND_ALL,
				FINDER_ARGS_EMPTY);
		}

		else {
			if ((deviceModelImpl.getColumnBitmask() &
					FINDER_PATH_WITHOUT_PAGINATION_FIND_BY_PORTALUSERID.getColumnBitmask()) != 0) {
				Object[] args = new Object[] {
						deviceModelImpl.getOriginalPortalUserId()
					};

				finderCache.removeResult(FINDER_PATH_COUNT_BY_PORTALUSERID, args);
				finderCache.removeResult(FINDER_PATH_WITHOUT_PAGINATION_FIND_BY_PORTALUSERID,
					args);

				args = new Object[] { deviceModelImpl.getPortalUserId() };

				finderCache.removeResult(FINDER_PATH_COUNT_BY_PORTALUSERID, args);
				finderCache.removeResult(FINDER_PATH_WITHOUT_PAGINATION_FIND_BY_PORTALUSERID,
					args);
			}

			if ((deviceModelImpl.getColumnBitmask() &
					FINDER_PATH_WITHOUT_PAGINATION_FIND_BY_VERIFIED_PORTALUSERID.getColumnBitmask()) != 0) {
				Object[] args = new Object[] {
						deviceModelImpl.getOriginalVerified(),
						deviceModelImpl.getOriginalPortalUserId()
					};

				finderCache.removeResult(FINDER_PATH_COUNT_BY_VERIFIED_PORTALUSERID,
					args);
				finderCache.removeResult(FINDER_PATH_WITHOUT_PAGINATION_FIND_BY_VERIFIED_PORTALUSERID,
					args);

				args = new Object[] {
						deviceModelImpl.isVerified(),
						deviceModelImpl.getPortalUserId()
					};

				finderCache.removeResult(FINDER_PATH_COUNT_BY_VERIFIED_PORTALUSERID,
					args);
				finderCache.removeResult(FINDER_PATH_WITHOUT_PAGINATION_FIND_BY_VERIFIED_PORTALUSERID,
					args);
			}
		}

		entityCache.putResult(DeviceModelImpl.ENTITY_CACHE_ENABLED,
			DeviceImpl.class, device.getPrimaryKey(), device, false);

		clearUniqueFindersCache(deviceModelImpl, false);
		cacheUniqueFindersCache(deviceModelImpl);

		device.resetOriginalValues();

		return device;
	}

	/**
	 * Returns the device with the primary key or throws a {@link com.liferay.portal.kernel.exception.NoSuchModelException} if it could not be found.
	 *
	 * @param primaryKey the primary key of the device
	 * @return the device
	 * @throws NoSuchDeviceException if a device with the primary key could not be found
	 */
	@Override
	public Device findByPrimaryKey(Serializable primaryKey)
		throws NoSuchDeviceException {
		Device device = fetchByPrimaryKey(primaryKey);

		if (device == null) {
			if (_log.isDebugEnabled()) {
				_log.debug(_NO_SUCH_ENTITY_WITH_PRIMARY_KEY + primaryKey);
			}

			throw new NoSuchDeviceException(_NO_SUCH_ENTITY_WITH_PRIMARY_KEY +
				primaryKey);
		}

		return device;
	}

	/**
	 * Returns the device with the primary key or throws a {@link NoSuchDeviceException} if it could not be found.
	 *
	 * @param deviceId the primary key of the device
	 * @return the device
	 * @throws NoSuchDeviceException if a device with the primary key could not be found
	 */
	@Override
	public Device findByPrimaryKey(long deviceId) throws NoSuchDeviceException {
		return findByPrimaryKey((Serializable)deviceId);
	}

	/**
	 * Returns the device with the primary key or returns <code>null</code> if it could not be found.
	 *
	 * @param primaryKey the primary key of the device
	 * @return the device, or <code>null</code> if a device with the primary key could not be found
	 */
	@Override
	public Device fetchByPrimaryKey(Serializable primaryKey) {
		Serializable serializable = entityCache.getResult(DeviceModelImpl.ENTITY_CACHE_ENABLED,
				DeviceImpl.class, primaryKey);

		if (serializable == nullModel) {
			return null;
		}

		Device device = (Device)serializable;

		if (device == null) {
			Session session = null;

			try {
				session = openSession();

				device = (Device)session.get(DeviceImpl.class, primaryKey);

				if (device != null) {
					cacheResult(device);
				}
				else {
					entityCache.putResult(DeviceModelImpl.ENTITY_CACHE_ENABLED,
						DeviceImpl.class, primaryKey, nullModel);
				}
			}
			catch (Exception e) {
				entityCache.removeResult(DeviceModelImpl.ENTITY_CACHE_ENABLED,
					DeviceImpl.class, primaryKey);

				throw processException(e);
			}
			finally {
				closeSession(session);
			}
		}

		return device;
	}

	/**
	 * Returns the device with the primary key or returns <code>null</code> if it could not be found.
	 *
	 * @param deviceId the primary key of the device
	 * @return the device, or <code>null</code> if a device with the primary key could not be found
	 */
	@Override
	public Device fetchByPrimaryKey(long deviceId) {
		return fetchByPrimaryKey((Serializable)deviceId);
	}

	@Override
	public Map<Serializable, Device> fetchByPrimaryKeys(
		Set<Serializable> primaryKeys) {
		if (primaryKeys.isEmpty()) {
			return Collections.emptyMap();
		}

		Map<Serializable, Device> map = new HashMap<Serializable, Device>();

		if (primaryKeys.size() == 1) {
			Iterator<Serializable> iterator = primaryKeys.iterator();

			Serializable primaryKey = iterator.next();

			Device device = fetchByPrimaryKey(primaryKey);

			if (device != null) {
				map.put(primaryKey, device);
			}

			return map;
		}

		Set<Serializable> uncachedPrimaryKeys = null;

		for (Serializable primaryKey : primaryKeys) {
			Serializable serializable = entityCache.getResult(DeviceModelImpl.ENTITY_CACHE_ENABLED,
					DeviceImpl.class, primaryKey);

			if (serializable != nullModel) {
				if (serializable == null) {
					if (uncachedPrimaryKeys == null) {
						uncachedPrimaryKeys = new HashSet<Serializable>();
					}

					uncachedPrimaryKeys.add(primaryKey);
				}
				else {
					map.put(primaryKey, (Device)serializable);
				}
			}
		}

		if (uncachedPrimaryKeys == null) {
			return map;
		}

		StringBundler query = new StringBundler((uncachedPrimaryKeys.size() * 2) +
				1);

		query.append(_SQL_SELECT_DEVICE_WHERE_PKS_IN);

		for (Serializable primaryKey : uncachedPrimaryKeys) {
			query.append((long)primaryKey);

			query.append(",");
		}

		query.setIndex(query.index() - 1);

		query.append(")");

		String sql = query.toString();

		Session session = null;

		try {
			session = openSession();

			Query q = session.createQuery(sql);

			for (Device device : (List<Device>)q.list()) {
				map.put(device.getPrimaryKeyObj(), device);

				cacheResult(device);

				uncachedPrimaryKeys.remove(device.getPrimaryKeyObj());
			}

			for (Serializable primaryKey : uncachedPrimaryKeys) {
				entityCache.putResult(DeviceModelImpl.ENTITY_CACHE_ENABLED,
					DeviceImpl.class, primaryKey, nullModel);
			}
		}
		catch (Exception e) {
			throw processException(e);
		}
		finally {
			closeSession(session);
		}

		return map;
	}

	/**
	 * Returns all the devices.
	 *
	 * @return the devices
	 */
	@Override
	public List<Device> findAll() {
		return findAll(QueryUtil.ALL_POS, QueryUtil.ALL_POS, null);
	}

	/**
	 * Returns a range of all the devices.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to {@link QueryUtil#ALL_POS} will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent and pagination is required (<code>start</code> and <code>end</code> are not {@link QueryUtil#ALL_POS}), then the query will include the default ORDER BY logic from {@link DeviceModelImpl}. If both <code>orderByComparator</code> and pagination are absent, for performance reasons, the query will not have an ORDER BY clause and the returned result set will be sorted on by the primary key in an ascending order.
	 * </p>
	 *
	 * @param start the lower bound of the range of devices
	 * @param end the upper bound of the range of devices (not inclusive)
	 * @return the range of devices
	 */
	@Override
	public List<Device> findAll(int start, int end) {
		return findAll(start, end, null);
	}

	/**
	 * Returns an ordered range of all the devices.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to {@link QueryUtil#ALL_POS} will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent and pagination is required (<code>start</code> and <code>end</code> are not {@link QueryUtil#ALL_POS}), then the query will include the default ORDER BY logic from {@link DeviceModelImpl}. If both <code>orderByComparator</code> and pagination are absent, for performance reasons, the query will not have an ORDER BY clause and the returned result set will be sorted on by the primary key in an ascending order.
	 * </p>
	 *
	 * @param start the lower bound of the range of devices
	 * @param end the upper bound of the range of devices (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of devices
	 */
	@Override
	public List<Device> findAll(int start, int end,
		OrderByComparator<Device> orderByComparator) {
		return findAll(start, end, orderByComparator, true);
	}

	/**
	 * Returns an ordered range of all the devices.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to {@link QueryUtil#ALL_POS} will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent and pagination is required (<code>start</code> and <code>end</code> are not {@link QueryUtil#ALL_POS}), then the query will include the default ORDER BY logic from {@link DeviceModelImpl}. If both <code>orderByComparator</code> and pagination are absent, for performance reasons, the query will not have an ORDER BY clause and the returned result set will be sorted on by the primary key in an ascending order.
	 * </p>
	 *
	 * @param start the lower bound of the range of devices
	 * @param end the upper bound of the range of devices (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param retrieveFromCache whether to retrieve from the finder cache
	 * @return the ordered range of devices
	 */
	@Override
	public List<Device> findAll(int start, int end,
		OrderByComparator<Device> orderByComparator, boolean retrieveFromCache) {
		boolean pagination = true;
		FinderPath finderPath = null;
		Object[] finderArgs = null;

		if ((start == QueryUtil.ALL_POS) && (end == QueryUtil.ALL_POS) &&
				(orderByComparator == null)) {
			pagination = false;
			finderPath = FINDER_PATH_WITHOUT_PAGINATION_FIND_ALL;
			finderArgs = FINDER_ARGS_EMPTY;
		}
		else {
			finderPath = FINDER_PATH_WITH_PAGINATION_FIND_ALL;
			finderArgs = new Object[] { start, end, orderByComparator };
		}

		List<Device> list = null;

		if (retrieveFromCache) {
			list = (List<Device>)finderCache.getResult(finderPath, finderArgs,
					this);
		}

		if (list == null) {
			StringBundler query = null;
			String sql = null;

			if (orderByComparator != null) {
				query = new StringBundler(2 +
						(orderByComparator.getOrderByFields().length * 2));

				query.append(_SQL_SELECT_DEVICE);

				appendOrderByComparator(query, _ORDER_BY_ENTITY_ALIAS,
					orderByComparator);

				sql = query.toString();
			}
			else {
				sql = _SQL_SELECT_DEVICE;

				if (pagination) {
					sql = sql.concat(DeviceModelImpl.ORDER_BY_JPQL);
				}
			}

			Session session = null;

			try {
				session = openSession();

				Query q = session.createQuery(sql);

				if (!pagination) {
					list = (List<Device>)QueryUtil.list(q, getDialect(), start,
							end, false);

					Collections.sort(list);

					list = Collections.unmodifiableList(list);
				}
				else {
					list = (List<Device>)QueryUtil.list(q, getDialect(), start,
							end);
				}

				cacheResult(list);

				finderCache.putResult(finderPath, finderArgs, list);
			}
			catch (Exception e) {
				finderCache.removeResult(finderPath, finderArgs);

				throw processException(e);
			}
			finally {
				closeSession(session);
			}
		}

		return list;
	}

	/**
	 * Removes all the devices from the database.
	 *
	 */
	@Override
	public void removeAll() {
		for (Device device : findAll()) {
			remove(device);
		}
	}

	/**
	 * Returns the number of devices.
	 *
	 * @return the number of devices
	 */
	@Override
	public int countAll() {
		Long count = (Long)finderCache.getResult(FINDER_PATH_COUNT_ALL,
				FINDER_ARGS_EMPTY, this);

		if (count == null) {
			Session session = null;

			try {
				session = openSession();

				Query q = session.createQuery(_SQL_COUNT_DEVICE);

				count = (Long)q.uniqueResult();

				finderCache.putResult(FINDER_PATH_COUNT_ALL, FINDER_ARGS_EMPTY,
					count);
			}
			catch (Exception e) {
				finderCache.removeResult(FINDER_PATH_COUNT_ALL,
					FINDER_ARGS_EMPTY);

				throw processException(e);
			}
			finally {
				closeSession(session);
			}
		}

		return count.intValue();
	}

	@Override
	protected Map<String, Integer> getTableColumnsMap() {
		return DeviceModelImpl.TABLE_COLUMNS_MAP;
	}

	/**
	 * Initializes the device persistence.
	 */
	public void afterPropertiesSet() {
	}

	public void destroy() {
		entityCache.removeCache(DeviceImpl.class.getName());
		finderCache.removeCache(FINDER_CLASS_NAME_ENTITY);
		finderCache.removeCache(FINDER_CLASS_NAME_LIST_WITH_PAGINATION);
		finderCache.removeCache(FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION);
	}

	@ServiceReference(type = CompanyProviderWrapper.class)
	protected CompanyProvider companyProvider;
	@ServiceReference(type = EntityCache.class)
	protected EntityCache entityCache;
	@ServiceReference(type = FinderCache.class)
	protected FinderCache finderCache;
	private static final String _SQL_SELECT_DEVICE = "SELECT device FROM Device device";
	private static final String _SQL_SELECT_DEVICE_WHERE_PKS_IN = "SELECT device FROM Device device WHERE deviceId IN (";
	private static final String _SQL_SELECT_DEVICE_WHERE = "SELECT device FROM Device device WHERE ";
	private static final String _SQL_COUNT_DEVICE = "SELECT COUNT(device) FROM Device device";
	private static final String _SQL_COUNT_DEVICE_WHERE = "SELECT COUNT(device) FROM Device device WHERE ";
	private static final String _ORDER_BY_ENTITY_ALIAS = "device.";
	private static final String _NO_SUCH_ENTITY_WITH_PRIMARY_KEY = "No Device exists with the primary key ";
	private static final String _NO_SUCH_ENTITY_WITH_KEY = "No Device exists with the key {";
	private static final Log _log = LogFactoryUtil.getLog(DevicePersistenceImpl.class);
}