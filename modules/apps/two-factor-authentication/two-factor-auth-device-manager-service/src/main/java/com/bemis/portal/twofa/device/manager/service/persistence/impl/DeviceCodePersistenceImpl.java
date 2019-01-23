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

import com.bemis.portal.twofa.device.manager.exception.NoSuchDeviceCodeException;
import com.bemis.portal.twofa.device.manager.model.DeviceCode;
import com.bemis.portal.twofa.device.manager.model.impl.DeviceCodeImpl;
import com.bemis.portal.twofa.device.manager.model.impl.DeviceCodeModelImpl;
import com.bemis.portal.twofa.device.manager.service.persistence.DeviceCodePersistence;

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
import com.liferay.portal.kernel.util.StringBundler;
import com.liferay.portal.kernel.util.StringPool;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.spring.extender.service.ServiceReference;

import java.io.Serializable;

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
 * The persistence implementation for the device code service.
 *
 * <p>
 * Caching information and settings can be found in <code>portal.properties</code>
 * </p>
 *
 * @author Prathima Shreenath
 * @see DeviceCodePersistence
 * @see com.bemis.portal.twofa.device.manager.service.persistence.DeviceCodeUtil
 * @generated
 */
@ProviderType
public class DeviceCodePersistenceImpl extends BasePersistenceImpl<DeviceCode>
	implements DeviceCodePersistence {
	/*
	 * NOTE FOR DEVELOPERS:
	 *
	 * Never modify or reference this class directly. Always use {@link DeviceCodeUtil} to access the device code persistence. Modify <code>service.xml</code> and rerun ServiceBuilder to regenerate this class.
	 */
	public static final String FINDER_CLASS_NAME_ENTITY = DeviceCodeImpl.class.getName();
	public static final String FINDER_CLASS_NAME_LIST_WITH_PAGINATION = FINDER_CLASS_NAME_ENTITY +
		".List1";
	public static final String FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION = FINDER_CLASS_NAME_ENTITY +
		".List2";
	public static final FinderPath FINDER_PATH_WITH_PAGINATION_FIND_ALL = new FinderPath(DeviceCodeModelImpl.ENTITY_CACHE_ENABLED,
			DeviceCodeModelImpl.FINDER_CACHE_ENABLED, DeviceCodeImpl.class,
			FINDER_CLASS_NAME_LIST_WITH_PAGINATION, "findAll", new String[0]);
	public static final FinderPath FINDER_PATH_WITHOUT_PAGINATION_FIND_ALL = new FinderPath(DeviceCodeModelImpl.ENTITY_CACHE_ENABLED,
			DeviceCodeModelImpl.FINDER_CACHE_ENABLED, DeviceCodeImpl.class,
			FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "findAll", new String[0]);
	public static final FinderPath FINDER_PATH_COUNT_ALL = new FinderPath(DeviceCodeModelImpl.ENTITY_CACHE_ENABLED,
			DeviceCodeModelImpl.FINDER_CACHE_ENABLED, Long.class,
			FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "countAll", new String[0]);
	public static final FinderPath FINDER_PATH_FETCH_BY_PORTALUSERID = new FinderPath(DeviceCodeModelImpl.ENTITY_CACHE_ENABLED,
			DeviceCodeModelImpl.FINDER_CACHE_ENABLED, DeviceCodeImpl.class,
			FINDER_CLASS_NAME_ENTITY, "fetchByPortalUserId",
			new String[] { Long.class.getName() },
			DeviceCodeModelImpl.PORTALUSERID_COLUMN_BITMASK);
	public static final FinderPath FINDER_PATH_COUNT_BY_PORTALUSERID = new FinderPath(DeviceCodeModelImpl.ENTITY_CACHE_ENABLED,
			DeviceCodeModelImpl.FINDER_CACHE_ENABLED, Long.class,
			FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "countByPortalUserId",
			new String[] { Long.class.getName() });

	/**
	 * Returns the device code where portalUserId = &#63; or throws a {@link NoSuchDeviceCodeException} if it could not be found.
	 *
	 * @param portalUserId the portal user ID
	 * @return the matching device code
	 * @throws NoSuchDeviceCodeException if a matching device code could not be found
	 */
	@Override
	public DeviceCode findByPortalUserId(long portalUserId)
		throws NoSuchDeviceCodeException {
		DeviceCode deviceCode = fetchByPortalUserId(portalUserId);

		if (deviceCode == null) {
			StringBundler msg = new StringBundler(4);

			msg.append(_NO_SUCH_ENTITY_WITH_KEY);

			msg.append("portalUserId=");
			msg.append(portalUserId);

			msg.append(StringPool.CLOSE_CURLY_BRACE);

			if (_log.isDebugEnabled()) {
				_log.debug(msg.toString());
			}

			throw new NoSuchDeviceCodeException(msg.toString());
		}

		return deviceCode;
	}

	/**
	 * Returns the device code where portalUserId = &#63; or returns <code>null</code> if it could not be found. Uses the finder cache.
	 *
	 * @param portalUserId the portal user ID
	 * @return the matching device code, or <code>null</code> if a matching device code could not be found
	 */
	@Override
	public DeviceCode fetchByPortalUserId(long portalUserId) {
		return fetchByPortalUserId(portalUserId, true);
	}

	/**
	 * Returns the device code where portalUserId = &#63; or returns <code>null</code> if it could not be found, optionally using the finder cache.
	 *
	 * @param portalUserId the portal user ID
	 * @param retrieveFromCache whether to retrieve from the finder cache
	 * @return the matching device code, or <code>null</code> if a matching device code could not be found
	 */
	@Override
	public DeviceCode fetchByPortalUserId(long portalUserId,
		boolean retrieveFromCache) {
		Object[] finderArgs = new Object[] { portalUserId };

		Object result = null;

		if (retrieveFromCache) {
			result = finderCache.getResult(FINDER_PATH_FETCH_BY_PORTALUSERID,
					finderArgs, this);
		}

		if (result instanceof DeviceCode) {
			DeviceCode deviceCode = (DeviceCode)result;

			if ((portalUserId != deviceCode.getPortalUserId())) {
				result = null;
			}
		}

		if (result == null) {
			StringBundler query = new StringBundler(3);

			query.append(_SQL_SELECT_DEVICECODE_WHERE);

			query.append(_FINDER_COLUMN_PORTALUSERID_PORTALUSERID_2);

			String sql = query.toString();

			Session session = null;

			try {
				session = openSession();

				Query q = session.createQuery(sql);

				QueryPos qPos = QueryPos.getInstance(q);

				qPos.add(portalUserId);

				List<DeviceCode> list = q.list();

				if (list.isEmpty()) {
					finderCache.putResult(FINDER_PATH_FETCH_BY_PORTALUSERID,
						finderArgs, list);
				}
				else {
					if (list.size() > 1) {
						Collections.sort(list, Collections.reverseOrder());

						if (_log.isWarnEnabled()) {
							_log.warn(
								"DeviceCodePersistenceImpl.fetchByPortalUserId(long, boolean) with parameters (" +
								StringUtil.merge(finderArgs) +
								") yields a result set with more than 1 result. This violates the logical unique restriction. There is no order guarantee on which result is returned by this finder.");
						}
					}

					DeviceCode deviceCode = list.get(0);

					result = deviceCode;

					cacheResult(deviceCode);

					if ((deviceCode.getPortalUserId() != portalUserId)) {
						finderCache.putResult(FINDER_PATH_FETCH_BY_PORTALUSERID,
							finderArgs, deviceCode);
					}
				}
			}
			catch (Exception e) {
				finderCache.removeResult(FINDER_PATH_FETCH_BY_PORTALUSERID,
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
			return (DeviceCode)result;
		}
	}

	/**
	 * Removes the device code where portalUserId = &#63; from the database.
	 *
	 * @param portalUserId the portal user ID
	 * @return the device code that was removed
	 */
	@Override
	public DeviceCode removeByPortalUserId(long portalUserId)
		throws NoSuchDeviceCodeException {
		DeviceCode deviceCode = findByPortalUserId(portalUserId);

		return remove(deviceCode);
	}

	/**
	 * Returns the number of device codes where portalUserId = &#63;.
	 *
	 * @param portalUserId the portal user ID
	 * @return the number of matching device codes
	 */
	@Override
	public int countByPortalUserId(long portalUserId) {
		FinderPath finderPath = FINDER_PATH_COUNT_BY_PORTALUSERID;

		Object[] finderArgs = new Object[] { portalUserId };

		Long count = (Long)finderCache.getResult(finderPath, finderArgs, this);

		if (count == null) {
			StringBundler query = new StringBundler(2);

			query.append(_SQL_COUNT_DEVICECODE_WHERE);

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

	private static final String _FINDER_COLUMN_PORTALUSERID_PORTALUSERID_2 = "deviceCode.portalUserId = ?";
	public static final FinderPath FINDER_PATH_FETCH_BY_EMAILADDRESS = new FinderPath(DeviceCodeModelImpl.ENTITY_CACHE_ENABLED,
			DeviceCodeModelImpl.FINDER_CACHE_ENABLED, DeviceCodeImpl.class,
			FINDER_CLASS_NAME_ENTITY, "fetchByEmailAddress",
			new String[] { String.class.getName() },
			DeviceCodeModelImpl.EMAILADDRESS_COLUMN_BITMASK);
	public static final FinderPath FINDER_PATH_COUNT_BY_EMAILADDRESS = new FinderPath(DeviceCodeModelImpl.ENTITY_CACHE_ENABLED,
			DeviceCodeModelImpl.FINDER_CACHE_ENABLED, Long.class,
			FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "countByEmailAddress",
			new String[] { String.class.getName() });

	/**
	 * Returns the device code where emailAddress = &#63; or throws a {@link NoSuchDeviceCodeException} if it could not be found.
	 *
	 * @param emailAddress the email address
	 * @return the matching device code
	 * @throws NoSuchDeviceCodeException if a matching device code could not be found
	 */
	@Override
	public DeviceCode findByEmailAddress(String emailAddress)
		throws NoSuchDeviceCodeException {
		DeviceCode deviceCode = fetchByEmailAddress(emailAddress);

		if (deviceCode == null) {
			StringBundler msg = new StringBundler(4);

			msg.append(_NO_SUCH_ENTITY_WITH_KEY);

			msg.append("emailAddress=");
			msg.append(emailAddress);

			msg.append(StringPool.CLOSE_CURLY_BRACE);

			if (_log.isDebugEnabled()) {
				_log.debug(msg.toString());
			}

			throw new NoSuchDeviceCodeException(msg.toString());
		}

		return deviceCode;
	}

	/**
	 * Returns the device code where emailAddress = &#63; or returns <code>null</code> if it could not be found. Uses the finder cache.
	 *
	 * @param emailAddress the email address
	 * @return the matching device code, or <code>null</code> if a matching device code could not be found
	 */
	@Override
	public DeviceCode fetchByEmailAddress(String emailAddress) {
		return fetchByEmailAddress(emailAddress, true);
	}

	/**
	 * Returns the device code where emailAddress = &#63; or returns <code>null</code> if it could not be found, optionally using the finder cache.
	 *
	 * @param emailAddress the email address
	 * @param retrieveFromCache whether to retrieve from the finder cache
	 * @return the matching device code, or <code>null</code> if a matching device code could not be found
	 */
	@Override
	public DeviceCode fetchByEmailAddress(String emailAddress,
		boolean retrieveFromCache) {
		Object[] finderArgs = new Object[] { emailAddress };

		Object result = null;

		if (retrieveFromCache) {
			result = finderCache.getResult(FINDER_PATH_FETCH_BY_EMAILADDRESS,
					finderArgs, this);
		}

		if (result instanceof DeviceCode) {
			DeviceCode deviceCode = (DeviceCode)result;

			if (!Objects.equals(emailAddress, deviceCode.getEmailAddress())) {
				result = null;
			}
		}

		if (result == null) {
			StringBundler query = new StringBundler(3);

			query.append(_SQL_SELECT_DEVICECODE_WHERE);

			boolean bindEmailAddress = false;

			if (emailAddress == null) {
				query.append(_FINDER_COLUMN_EMAILADDRESS_EMAILADDRESS_1);
			}
			else if (emailAddress.equals(StringPool.BLANK)) {
				query.append(_FINDER_COLUMN_EMAILADDRESS_EMAILADDRESS_3);
			}
			else {
				bindEmailAddress = true;

				query.append(_FINDER_COLUMN_EMAILADDRESS_EMAILADDRESS_2);
			}

			String sql = query.toString();

			Session session = null;

			try {
				session = openSession();

				Query q = session.createQuery(sql);

				QueryPos qPos = QueryPos.getInstance(q);

				if (bindEmailAddress) {
					qPos.add(emailAddress);
				}

				List<DeviceCode> list = q.list();

				if (list.isEmpty()) {
					finderCache.putResult(FINDER_PATH_FETCH_BY_EMAILADDRESS,
						finderArgs, list);
				}
				else {
					if (list.size() > 1) {
						Collections.sort(list, Collections.reverseOrder());

						if (_log.isWarnEnabled()) {
							_log.warn(
								"DeviceCodePersistenceImpl.fetchByEmailAddress(String, boolean) with parameters (" +
								StringUtil.merge(finderArgs) +
								") yields a result set with more than 1 result. This violates the logical unique restriction. There is no order guarantee on which result is returned by this finder.");
						}
					}

					DeviceCode deviceCode = list.get(0);

					result = deviceCode;

					cacheResult(deviceCode);

					if ((deviceCode.getEmailAddress() == null) ||
							!deviceCode.getEmailAddress().equals(emailAddress)) {
						finderCache.putResult(FINDER_PATH_FETCH_BY_EMAILADDRESS,
							finderArgs, deviceCode);
					}
				}
			}
			catch (Exception e) {
				finderCache.removeResult(FINDER_PATH_FETCH_BY_EMAILADDRESS,
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
			return (DeviceCode)result;
		}
	}

	/**
	 * Removes the device code where emailAddress = &#63; from the database.
	 *
	 * @param emailAddress the email address
	 * @return the device code that was removed
	 */
	@Override
	public DeviceCode removeByEmailAddress(String emailAddress)
		throws NoSuchDeviceCodeException {
		DeviceCode deviceCode = findByEmailAddress(emailAddress);

		return remove(deviceCode);
	}

	/**
	 * Returns the number of device codes where emailAddress = &#63;.
	 *
	 * @param emailAddress the email address
	 * @return the number of matching device codes
	 */
	@Override
	public int countByEmailAddress(String emailAddress) {
		FinderPath finderPath = FINDER_PATH_COUNT_BY_EMAILADDRESS;

		Object[] finderArgs = new Object[] { emailAddress };

		Long count = (Long)finderCache.getResult(finderPath, finderArgs, this);

		if (count == null) {
			StringBundler query = new StringBundler(2);

			query.append(_SQL_COUNT_DEVICECODE_WHERE);

			boolean bindEmailAddress = false;

			if (emailAddress == null) {
				query.append(_FINDER_COLUMN_EMAILADDRESS_EMAILADDRESS_1);
			}
			else if (emailAddress.equals(StringPool.BLANK)) {
				query.append(_FINDER_COLUMN_EMAILADDRESS_EMAILADDRESS_3);
			}
			else {
				bindEmailAddress = true;

				query.append(_FINDER_COLUMN_EMAILADDRESS_EMAILADDRESS_2);
			}

			String sql = query.toString();

			Session session = null;

			try {
				session = openSession();

				Query q = session.createQuery(sql);

				QueryPos qPos = QueryPos.getInstance(q);

				if (bindEmailAddress) {
					qPos.add(emailAddress);
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

	private static final String _FINDER_COLUMN_EMAILADDRESS_EMAILADDRESS_1 = "deviceCode.emailAddress IS NULL";
	private static final String _FINDER_COLUMN_EMAILADDRESS_EMAILADDRESS_2 = "deviceCode.emailAddress = ?";
	private static final String _FINDER_COLUMN_EMAILADDRESS_EMAILADDRESS_3 = "(deviceCode.emailAddress IS NULL OR deviceCode.emailAddress = '')";

	public DeviceCodePersistenceImpl() {
		setModelClass(DeviceCode.class);
	}

	/**
	 * Caches the device code in the entity cache if it is enabled.
	 *
	 * @param deviceCode the device code
	 */
	@Override
	public void cacheResult(DeviceCode deviceCode) {
		entityCache.putResult(DeviceCodeModelImpl.ENTITY_CACHE_ENABLED,
			DeviceCodeImpl.class, deviceCode.getPrimaryKey(), deviceCode);

		finderCache.putResult(FINDER_PATH_FETCH_BY_PORTALUSERID,
			new Object[] { deviceCode.getPortalUserId() }, deviceCode);

		finderCache.putResult(FINDER_PATH_FETCH_BY_EMAILADDRESS,
			new Object[] { deviceCode.getEmailAddress() }, deviceCode);

		deviceCode.resetOriginalValues();
	}

	/**
	 * Caches the device codes in the entity cache if it is enabled.
	 *
	 * @param deviceCodes the device codes
	 */
	@Override
	public void cacheResult(List<DeviceCode> deviceCodes) {
		for (DeviceCode deviceCode : deviceCodes) {
			if (entityCache.getResult(
						DeviceCodeModelImpl.ENTITY_CACHE_ENABLED,
						DeviceCodeImpl.class, deviceCode.getPrimaryKey()) == null) {
				cacheResult(deviceCode);
			}
			else {
				deviceCode.resetOriginalValues();
			}
		}
	}

	/**
	 * Clears the cache for all device codes.
	 *
	 * <p>
	 * The {@link EntityCache} and {@link FinderCache} are both cleared by this method.
	 * </p>
	 */
	@Override
	public void clearCache() {
		entityCache.clearCache(DeviceCodeImpl.class);

		finderCache.clearCache(FINDER_CLASS_NAME_ENTITY);
		finderCache.clearCache(FINDER_CLASS_NAME_LIST_WITH_PAGINATION);
		finderCache.clearCache(FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION);
	}

	/**
	 * Clears the cache for the device code.
	 *
	 * <p>
	 * The {@link EntityCache} and {@link FinderCache} are both cleared by this method.
	 * </p>
	 */
	@Override
	public void clearCache(DeviceCode deviceCode) {
		entityCache.removeResult(DeviceCodeModelImpl.ENTITY_CACHE_ENABLED,
			DeviceCodeImpl.class, deviceCode.getPrimaryKey());

		finderCache.clearCache(FINDER_CLASS_NAME_LIST_WITH_PAGINATION);
		finderCache.clearCache(FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION);

		clearUniqueFindersCache((DeviceCodeModelImpl)deviceCode);
	}

	@Override
	public void clearCache(List<DeviceCode> deviceCodes) {
		finderCache.clearCache(FINDER_CLASS_NAME_LIST_WITH_PAGINATION);
		finderCache.clearCache(FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION);

		for (DeviceCode deviceCode : deviceCodes) {
			entityCache.removeResult(DeviceCodeModelImpl.ENTITY_CACHE_ENABLED,
				DeviceCodeImpl.class, deviceCode.getPrimaryKey());

			clearUniqueFindersCache((DeviceCodeModelImpl)deviceCode);
		}
	}

	protected void cacheUniqueFindersCache(
		DeviceCodeModelImpl deviceCodeModelImpl, boolean isNew) {
		if (isNew) {
			Object[] args = new Object[] { deviceCodeModelImpl.getPortalUserId() };

			finderCache.putResult(FINDER_PATH_COUNT_BY_PORTALUSERID, args,
				Long.valueOf(1));
			finderCache.putResult(FINDER_PATH_FETCH_BY_PORTALUSERID, args,
				deviceCodeModelImpl);

			args = new Object[] { deviceCodeModelImpl.getEmailAddress() };

			finderCache.putResult(FINDER_PATH_COUNT_BY_EMAILADDRESS, args,
				Long.valueOf(1));
			finderCache.putResult(FINDER_PATH_FETCH_BY_EMAILADDRESS, args,
				deviceCodeModelImpl);
		}
		else {
			if ((deviceCodeModelImpl.getColumnBitmask() &
					FINDER_PATH_FETCH_BY_PORTALUSERID.getColumnBitmask()) != 0) {
				Object[] args = new Object[] {
						deviceCodeModelImpl.getPortalUserId()
					};

				finderCache.putResult(FINDER_PATH_COUNT_BY_PORTALUSERID, args,
					Long.valueOf(1));
				finderCache.putResult(FINDER_PATH_FETCH_BY_PORTALUSERID, args,
					deviceCodeModelImpl);
			}

			if ((deviceCodeModelImpl.getColumnBitmask() &
					FINDER_PATH_FETCH_BY_EMAILADDRESS.getColumnBitmask()) != 0) {
				Object[] args = new Object[] {
						deviceCodeModelImpl.getEmailAddress()
					};

				finderCache.putResult(FINDER_PATH_COUNT_BY_EMAILADDRESS, args,
					Long.valueOf(1));
				finderCache.putResult(FINDER_PATH_FETCH_BY_EMAILADDRESS, args,
					deviceCodeModelImpl);
			}
		}
	}

	protected void clearUniqueFindersCache(
		DeviceCodeModelImpl deviceCodeModelImpl) {
		Object[] args = new Object[] { deviceCodeModelImpl.getPortalUserId() };

		finderCache.removeResult(FINDER_PATH_COUNT_BY_PORTALUSERID, args);
		finderCache.removeResult(FINDER_PATH_FETCH_BY_PORTALUSERID, args);

		if ((deviceCodeModelImpl.getColumnBitmask() &
				FINDER_PATH_FETCH_BY_PORTALUSERID.getColumnBitmask()) != 0) {
			args = new Object[] { deviceCodeModelImpl.getOriginalPortalUserId() };

			finderCache.removeResult(FINDER_PATH_COUNT_BY_PORTALUSERID, args);
			finderCache.removeResult(FINDER_PATH_FETCH_BY_PORTALUSERID, args);
		}

		args = new Object[] { deviceCodeModelImpl.getEmailAddress() };

		finderCache.removeResult(FINDER_PATH_COUNT_BY_EMAILADDRESS, args);
		finderCache.removeResult(FINDER_PATH_FETCH_BY_EMAILADDRESS, args);

		if ((deviceCodeModelImpl.getColumnBitmask() &
				FINDER_PATH_FETCH_BY_EMAILADDRESS.getColumnBitmask()) != 0) {
			args = new Object[] { deviceCodeModelImpl.getOriginalEmailAddress() };

			finderCache.removeResult(FINDER_PATH_COUNT_BY_EMAILADDRESS, args);
			finderCache.removeResult(FINDER_PATH_FETCH_BY_EMAILADDRESS, args);
		}
	}

	/**
	 * Creates a new device code with the primary key. Does not add the device code to the database.
	 *
	 * @param deviceCodeId the primary key for the new device code
	 * @return the new device code
	 */
	@Override
	public DeviceCode create(long deviceCodeId) {
		DeviceCode deviceCode = new DeviceCodeImpl();

		deviceCode.setNew(true);
		deviceCode.setPrimaryKey(deviceCodeId);

		deviceCode.setCompanyId(companyProvider.getCompanyId());

		return deviceCode;
	}

	/**
	 * Removes the device code with the primary key from the database. Also notifies the appropriate model listeners.
	 *
	 * @param deviceCodeId the primary key of the device code
	 * @return the device code that was removed
	 * @throws NoSuchDeviceCodeException if a device code with the primary key could not be found
	 */
	@Override
	public DeviceCode remove(long deviceCodeId)
		throws NoSuchDeviceCodeException {
		return remove((Serializable)deviceCodeId);
	}

	/**
	 * Removes the device code with the primary key from the database. Also notifies the appropriate model listeners.
	 *
	 * @param primaryKey the primary key of the device code
	 * @return the device code that was removed
	 * @throws NoSuchDeviceCodeException if a device code with the primary key could not be found
	 */
	@Override
	public DeviceCode remove(Serializable primaryKey)
		throws NoSuchDeviceCodeException {
		Session session = null;

		try {
			session = openSession();

			DeviceCode deviceCode = (DeviceCode)session.get(DeviceCodeImpl.class,
					primaryKey);

			if (deviceCode == null) {
				if (_log.isDebugEnabled()) {
					_log.debug(_NO_SUCH_ENTITY_WITH_PRIMARY_KEY + primaryKey);
				}

				throw new NoSuchDeviceCodeException(_NO_SUCH_ENTITY_WITH_PRIMARY_KEY +
					primaryKey);
			}

			return remove(deviceCode);
		}
		catch (NoSuchDeviceCodeException nsee) {
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
	protected DeviceCode removeImpl(DeviceCode deviceCode) {
		deviceCode = toUnwrappedModel(deviceCode);

		Session session = null;

		try {
			session = openSession();

			if (!session.contains(deviceCode)) {
				deviceCode = (DeviceCode)session.get(DeviceCodeImpl.class,
						deviceCode.getPrimaryKeyObj());
			}

			if (deviceCode != null) {
				session.delete(deviceCode);
			}
		}
		catch (Exception e) {
			throw processException(e);
		}
		finally {
			closeSession(session);
		}

		if (deviceCode != null) {
			clearCache(deviceCode);
		}

		return deviceCode;
	}

	@Override
	public DeviceCode updateImpl(DeviceCode deviceCode) {
		deviceCode = toUnwrappedModel(deviceCode);

		boolean isNew = deviceCode.isNew();

		DeviceCodeModelImpl deviceCodeModelImpl = (DeviceCodeModelImpl)deviceCode;

		ServiceContext serviceContext = ServiceContextThreadLocal.getServiceContext();

		Date now = new Date();

		if (isNew && (deviceCode.getCreateDate() == null)) {
			if (serviceContext == null) {
				deviceCode.setCreateDate(now);
			}
			else {
				deviceCode.setCreateDate(serviceContext.getCreateDate(now));
			}
		}

		if (!deviceCodeModelImpl.hasSetModifiedDate()) {
			if (serviceContext == null) {
				deviceCode.setModifiedDate(now);
			}
			else {
				deviceCode.setModifiedDate(serviceContext.getModifiedDate(now));
			}
		}

		Session session = null;

		try {
			session = openSession();

			if (deviceCode.isNew()) {
				session.save(deviceCode);

				deviceCode.setNew(false);
			}
			else {
				deviceCode = (DeviceCode)session.merge(deviceCode);
			}
		}
		catch (Exception e) {
			throw processException(e);
		}
		finally {
			closeSession(session);
		}

		finderCache.clearCache(FINDER_CLASS_NAME_LIST_WITH_PAGINATION);

		if (isNew || !DeviceCodeModelImpl.COLUMN_BITMASK_ENABLED) {
			finderCache.clearCache(FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION);
		}

		entityCache.putResult(DeviceCodeModelImpl.ENTITY_CACHE_ENABLED,
			DeviceCodeImpl.class, deviceCode.getPrimaryKey(), deviceCode, false);

		clearUniqueFindersCache(deviceCodeModelImpl);
		cacheUniqueFindersCache(deviceCodeModelImpl, isNew);

		deviceCode.resetOriginalValues();

		return deviceCode;
	}

	protected DeviceCode toUnwrappedModel(DeviceCode deviceCode) {
		if (deviceCode instanceof DeviceCodeImpl) {
			return deviceCode;
		}

		DeviceCodeImpl deviceCodeImpl = new DeviceCodeImpl();

		deviceCodeImpl.setNew(deviceCode.isNew());
		deviceCodeImpl.setPrimaryKey(deviceCode.getPrimaryKey());

		deviceCodeImpl.setDeviceCodeId(deviceCode.getDeviceCodeId());
		deviceCodeImpl.setGroupId(deviceCode.getGroupId());
		deviceCodeImpl.setCompanyId(deviceCode.getCompanyId());
		deviceCodeImpl.setUserId(deviceCode.getUserId());
		deviceCodeImpl.setUserName(deviceCode.getUserName());
		deviceCodeImpl.setCreateDate(deviceCode.getCreateDate());
		deviceCodeImpl.setModifiedDate(deviceCode.getModifiedDate());
		deviceCodeImpl.setPortalUserId(deviceCode.getPortalUserId());
		deviceCodeImpl.setPortalUserName(deviceCode.getPortalUserName());
		deviceCodeImpl.setEmailAddress(deviceCode.getEmailAddress());
		deviceCodeImpl.setDeviceCode(deviceCode.getDeviceCode());
		deviceCodeImpl.setDeviceIP(deviceCode.getDeviceIP());
		deviceCodeImpl.setValidationCode(deviceCode.getValidationCode());

		return deviceCodeImpl;
	}

	/**
	 * Returns the device code with the primary key or throws a {@link com.liferay.portal.kernel.exception.NoSuchModelException} if it could not be found.
	 *
	 * @param primaryKey the primary key of the device code
	 * @return the device code
	 * @throws NoSuchDeviceCodeException if a device code with the primary key could not be found
	 */
	@Override
	public DeviceCode findByPrimaryKey(Serializable primaryKey)
		throws NoSuchDeviceCodeException {
		DeviceCode deviceCode = fetchByPrimaryKey(primaryKey);

		if (deviceCode == null) {
			if (_log.isDebugEnabled()) {
				_log.debug(_NO_SUCH_ENTITY_WITH_PRIMARY_KEY + primaryKey);
			}

			throw new NoSuchDeviceCodeException(_NO_SUCH_ENTITY_WITH_PRIMARY_KEY +
				primaryKey);
		}

		return deviceCode;
	}

	/**
	 * Returns the device code with the primary key or throws a {@link NoSuchDeviceCodeException} if it could not be found.
	 *
	 * @param deviceCodeId the primary key of the device code
	 * @return the device code
	 * @throws NoSuchDeviceCodeException if a device code with the primary key could not be found
	 */
	@Override
	public DeviceCode findByPrimaryKey(long deviceCodeId)
		throws NoSuchDeviceCodeException {
		return findByPrimaryKey((Serializable)deviceCodeId);
	}

	/**
	 * Returns the device code with the primary key or returns <code>null</code> if it could not be found.
	 *
	 * @param primaryKey the primary key of the device code
	 * @return the device code, or <code>null</code> if a device code with the primary key could not be found
	 */
	@Override
	public DeviceCode fetchByPrimaryKey(Serializable primaryKey) {
		Serializable serializable = entityCache.getResult(DeviceCodeModelImpl.ENTITY_CACHE_ENABLED,
				DeviceCodeImpl.class, primaryKey);

		if (serializable == nullModel) {
			return null;
		}

		DeviceCode deviceCode = (DeviceCode)serializable;

		if (deviceCode == null) {
			Session session = null;

			try {
				session = openSession();

				deviceCode = (DeviceCode)session.get(DeviceCodeImpl.class,
						primaryKey);

				if (deviceCode != null) {
					cacheResult(deviceCode);
				}
				else {
					entityCache.putResult(DeviceCodeModelImpl.ENTITY_CACHE_ENABLED,
						DeviceCodeImpl.class, primaryKey, nullModel);
				}
			}
			catch (Exception e) {
				entityCache.removeResult(DeviceCodeModelImpl.ENTITY_CACHE_ENABLED,
					DeviceCodeImpl.class, primaryKey);

				throw processException(e);
			}
			finally {
				closeSession(session);
			}
		}

		return deviceCode;
	}

	/**
	 * Returns the device code with the primary key or returns <code>null</code> if it could not be found.
	 *
	 * @param deviceCodeId the primary key of the device code
	 * @return the device code, or <code>null</code> if a device code with the primary key could not be found
	 */
	@Override
	public DeviceCode fetchByPrimaryKey(long deviceCodeId) {
		return fetchByPrimaryKey((Serializable)deviceCodeId);
	}

	@Override
	public Map<Serializable, DeviceCode> fetchByPrimaryKeys(
		Set<Serializable> primaryKeys) {
		if (primaryKeys.isEmpty()) {
			return Collections.emptyMap();
		}

		Map<Serializable, DeviceCode> map = new HashMap<Serializable, DeviceCode>();

		if (primaryKeys.size() == 1) {
			Iterator<Serializable> iterator = primaryKeys.iterator();

			Serializable primaryKey = iterator.next();

			DeviceCode deviceCode = fetchByPrimaryKey(primaryKey);

			if (deviceCode != null) {
				map.put(primaryKey, deviceCode);
			}

			return map;
		}

		Set<Serializable> uncachedPrimaryKeys = null;

		for (Serializable primaryKey : primaryKeys) {
			Serializable serializable = entityCache.getResult(DeviceCodeModelImpl.ENTITY_CACHE_ENABLED,
					DeviceCodeImpl.class, primaryKey);

			if (serializable != nullModel) {
				if (serializable == null) {
					if (uncachedPrimaryKeys == null) {
						uncachedPrimaryKeys = new HashSet<Serializable>();
					}

					uncachedPrimaryKeys.add(primaryKey);
				}
				else {
					map.put(primaryKey, (DeviceCode)serializable);
				}
			}
		}

		if (uncachedPrimaryKeys == null) {
			return map;
		}

		StringBundler query = new StringBundler((uncachedPrimaryKeys.size() * 2) +
				1);

		query.append(_SQL_SELECT_DEVICECODE_WHERE_PKS_IN);

		for (Serializable primaryKey : uncachedPrimaryKeys) {
			query.append(String.valueOf(primaryKey));

			query.append(StringPool.COMMA);
		}

		query.setIndex(query.index() - 1);

		query.append(StringPool.CLOSE_PARENTHESIS);

		String sql = query.toString();

		Session session = null;

		try {
			session = openSession();

			Query q = session.createQuery(sql);

			for (DeviceCode deviceCode : (List<DeviceCode>)q.list()) {
				map.put(deviceCode.getPrimaryKeyObj(), deviceCode);

				cacheResult(deviceCode);

				uncachedPrimaryKeys.remove(deviceCode.getPrimaryKeyObj());
			}

			for (Serializable primaryKey : uncachedPrimaryKeys) {
				entityCache.putResult(DeviceCodeModelImpl.ENTITY_CACHE_ENABLED,
					DeviceCodeImpl.class, primaryKey, nullModel);
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
	 * Returns all the device codes.
	 *
	 * @return the device codes
	 */
	@Override
	public List<DeviceCode> findAll() {
		return findAll(QueryUtil.ALL_POS, QueryUtil.ALL_POS, null);
	}

	/**
	 * Returns a range of all the device codes.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to {@link QueryUtil#ALL_POS} will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent and pagination is required (<code>start</code> and <code>end</code> are not {@link QueryUtil#ALL_POS}), then the query will include the default ORDER BY logic from {@link DeviceCodeModelImpl}. If both <code>orderByComparator</code> and pagination are absent, for performance reasons, the query will not have an ORDER BY clause and the returned result set will be sorted on by the primary key in an ascending order.
	 * </p>
	 *
	 * @param start the lower bound of the range of device codes
	 * @param end the upper bound of the range of device codes (not inclusive)
	 * @return the range of device codes
	 */
	@Override
	public List<DeviceCode> findAll(int start, int end) {
		return findAll(start, end, null);
	}

	/**
	 * Returns an ordered range of all the device codes.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to {@link QueryUtil#ALL_POS} will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent and pagination is required (<code>start</code> and <code>end</code> are not {@link QueryUtil#ALL_POS}), then the query will include the default ORDER BY logic from {@link DeviceCodeModelImpl}. If both <code>orderByComparator</code> and pagination are absent, for performance reasons, the query will not have an ORDER BY clause and the returned result set will be sorted on by the primary key in an ascending order.
	 * </p>
	 *
	 * @param start the lower bound of the range of device codes
	 * @param end the upper bound of the range of device codes (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of device codes
	 */
	@Override
	public List<DeviceCode> findAll(int start, int end,
		OrderByComparator<DeviceCode> orderByComparator) {
		return findAll(start, end, orderByComparator, true);
	}

	/**
	 * Returns an ordered range of all the device codes.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to {@link QueryUtil#ALL_POS} will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent and pagination is required (<code>start</code> and <code>end</code> are not {@link QueryUtil#ALL_POS}), then the query will include the default ORDER BY logic from {@link DeviceCodeModelImpl}. If both <code>orderByComparator</code> and pagination are absent, for performance reasons, the query will not have an ORDER BY clause and the returned result set will be sorted on by the primary key in an ascending order.
	 * </p>
	 *
	 * @param start the lower bound of the range of device codes
	 * @param end the upper bound of the range of device codes (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param retrieveFromCache whether to retrieve from the finder cache
	 * @return the ordered range of device codes
	 */
	@Override
	public List<DeviceCode> findAll(int start, int end,
		OrderByComparator<DeviceCode> orderByComparator,
		boolean retrieveFromCache) {
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

		List<DeviceCode> list = null;

		if (retrieveFromCache) {
			list = (List<DeviceCode>)finderCache.getResult(finderPath,
					finderArgs, this);
		}

		if (list == null) {
			StringBundler query = null;
			String sql = null;

			if (orderByComparator != null) {
				query = new StringBundler(2 +
						(orderByComparator.getOrderByFields().length * 2));

				query.append(_SQL_SELECT_DEVICECODE);

				appendOrderByComparator(query, _ORDER_BY_ENTITY_ALIAS,
					orderByComparator);

				sql = query.toString();
			}
			else {
				sql = _SQL_SELECT_DEVICECODE;

				if (pagination) {
					sql = sql.concat(DeviceCodeModelImpl.ORDER_BY_JPQL);
				}
			}

			Session session = null;

			try {
				session = openSession();

				Query q = session.createQuery(sql);

				if (!pagination) {
					list = (List<DeviceCode>)QueryUtil.list(q, getDialect(),
							start, end, false);

					Collections.sort(list);

					list = Collections.unmodifiableList(list);
				}
				else {
					list = (List<DeviceCode>)QueryUtil.list(q, getDialect(),
							start, end);
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
	 * Removes all the device codes from the database.
	 *
	 */
	@Override
	public void removeAll() {
		for (DeviceCode deviceCode : findAll()) {
			remove(deviceCode);
		}
	}

	/**
	 * Returns the number of device codes.
	 *
	 * @return the number of device codes
	 */
	@Override
	public int countAll() {
		Long count = (Long)finderCache.getResult(FINDER_PATH_COUNT_ALL,
				FINDER_ARGS_EMPTY, this);

		if (count == null) {
			Session session = null;

			try {
				session = openSession();

				Query q = session.createQuery(_SQL_COUNT_DEVICECODE);

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
		return DeviceCodeModelImpl.TABLE_COLUMNS_MAP;
	}

	/**
	 * Initializes the device code persistence.
	 */
	public void afterPropertiesSet() {
	}

	public void destroy() {
		entityCache.removeCache(DeviceCodeImpl.class.getName());
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
	private static final String _SQL_SELECT_DEVICECODE = "SELECT deviceCode FROM DeviceCode deviceCode";
	private static final String _SQL_SELECT_DEVICECODE_WHERE_PKS_IN = "SELECT deviceCode FROM DeviceCode deviceCode WHERE deviceCodeId IN (";
	private static final String _SQL_SELECT_DEVICECODE_WHERE = "SELECT deviceCode FROM DeviceCode deviceCode WHERE ";
	private static final String _SQL_COUNT_DEVICECODE = "SELECT COUNT(deviceCode) FROM DeviceCode deviceCode";
	private static final String _SQL_COUNT_DEVICECODE_WHERE = "SELECT COUNT(deviceCode) FROM DeviceCode deviceCode WHERE ";
	private static final String _ORDER_BY_ENTITY_ALIAS = "deviceCode.";
	private static final String _NO_SUCH_ENTITY_WITH_PRIMARY_KEY = "No DeviceCode exists with the primary key ";
	private static final String _NO_SUCH_ENTITY_WITH_KEY = "No DeviceCode exists with the key {";
	private static final Log _log = LogFactoryUtil.getLog(DeviceCodePersistenceImpl.class);
}