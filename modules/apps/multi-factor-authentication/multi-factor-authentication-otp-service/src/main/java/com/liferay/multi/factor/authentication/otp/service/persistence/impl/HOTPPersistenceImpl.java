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

package com.liferay.multi.factor.authentication.otp.service.persistence.impl;

import aQute.bnd.annotation.ProviderType;

import com.liferay.multi.factor.authentication.otp.exception.NoSuchHOTPException;
import com.liferay.multi.factor.authentication.otp.model.HOTP;
import com.liferay.multi.factor.authentication.otp.model.impl.HOTPImpl;
import com.liferay.multi.factor.authentication.otp.model.impl.HOTPModelImpl;
import com.liferay.multi.factor.authentication.otp.service.persistence.HOTPPersistence;

import com.liferay.petra.string.StringBundler;

import com.liferay.portal.kernel.dao.orm.EntityCache;
import com.liferay.portal.kernel.dao.orm.FinderCache;
import com.liferay.portal.kernel.dao.orm.FinderPath;
import com.liferay.portal.kernel.dao.orm.Query;
import com.liferay.portal.kernel.dao.orm.QueryPos;
import com.liferay.portal.kernel.dao.orm.QueryUtil;
import com.liferay.portal.kernel.dao.orm.Session;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.service.persistence.impl.BasePersistenceImpl;
import com.liferay.portal.kernel.util.OrderByComparator;
import com.liferay.portal.kernel.util.ProxyUtil;
import com.liferay.portal.spring.extender.service.ServiceReference;

import java.io.Serializable;

import java.lang.reflect.InvocationHandler;

import java.util.Collections;
import java.util.List;
import java.util.Map;

/**
 * The persistence implementation for the hotp service.
 *
 * <p>
 * Caching information and settings can be found in <code>portal.properties</code>
 * </p>
 *
 * @author arthurchan35
 * @see HOTPPersistence
 * @see com.liferay.multi.factor.authentication.otp.service.persistence.HOTPUtil
 * @generated
 */
@ProviderType
public class HOTPPersistenceImpl extends BasePersistenceImpl<HOTP>
	implements HOTPPersistence {
	/*
	 * NOTE FOR DEVELOPERS:
	 *
	 * Never modify or reference this class directly. Always use {@link HOTPUtil} to access the hotp persistence. Modify <code>service.xml</code> and rerun ServiceBuilder to regenerate this class.
	 */
	public static final String FINDER_CLASS_NAME_ENTITY = HOTPImpl.class.getName();
	public static final String FINDER_CLASS_NAME_LIST_WITH_PAGINATION = FINDER_CLASS_NAME_ENTITY +
		".List1";
	public static final String FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION = FINDER_CLASS_NAME_ENTITY +
		".List2";
	public static final FinderPath FINDER_PATH_WITH_PAGINATION_FIND_ALL = new FinderPath(HOTPModelImpl.ENTITY_CACHE_ENABLED,
			HOTPModelImpl.FINDER_CACHE_ENABLED, HOTPImpl.class,
			FINDER_CLASS_NAME_LIST_WITH_PAGINATION, "findAll", new String[0]);
	public static final FinderPath FINDER_PATH_WITHOUT_PAGINATION_FIND_ALL = new FinderPath(HOTPModelImpl.ENTITY_CACHE_ENABLED,
			HOTPModelImpl.FINDER_CACHE_ENABLED, HOTPImpl.class,
			FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "findAll", new String[0]);
	public static final FinderPath FINDER_PATH_COUNT_ALL = new FinderPath(HOTPModelImpl.ENTITY_CACHE_ENABLED,
			HOTPModelImpl.FINDER_CACHE_ENABLED, Long.class,
			FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "countAll", new String[0]);
	public static final FinderPath FINDER_PATH_FETCH_BY_USERID = new FinderPath(HOTPModelImpl.ENTITY_CACHE_ENABLED,
			HOTPModelImpl.FINDER_CACHE_ENABLED, HOTPImpl.class,
			FINDER_CLASS_NAME_ENTITY, "fetchByUserId",
			new String[] { Long.class.getName() },
			HOTPModelImpl.USERID_COLUMN_BITMASK);
	public static final FinderPath FINDER_PATH_COUNT_BY_USERID = new FinderPath(HOTPModelImpl.ENTITY_CACHE_ENABLED,
			HOTPModelImpl.FINDER_CACHE_ENABLED, Long.class,
			FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "countByUserId",
			new String[] { Long.class.getName() });

	/**
	 * Returns the hotp where userId = &#63; or throws a {@link NoSuchHOTPException} if it could not be found.
	 *
	 * @param userId the user ID
	 * @return the matching hotp
	 * @throws NoSuchHOTPException if a matching hotp could not be found
	 */
	@Override
	public HOTP findByUserId(long userId) throws NoSuchHOTPException {
		HOTP hotp = fetchByUserId(userId);

		if (hotp == null) {
			StringBundler msg = new StringBundler(4);

			msg.append(_NO_SUCH_ENTITY_WITH_KEY);

			msg.append("userId=");
			msg.append(userId);

			msg.append("}");

			if (_log.isDebugEnabled()) {
				_log.debug(msg.toString());
			}

			throw new NoSuchHOTPException(msg.toString());
		}

		return hotp;
	}

	/**
	 * Returns the hotp where userId = &#63; or returns <code>null</code> if it could not be found. Uses the finder cache.
	 *
	 * @param userId the user ID
	 * @return the matching hotp, or <code>null</code> if a matching hotp could not be found
	 */
	@Override
	public HOTP fetchByUserId(long userId) {
		return fetchByUserId(userId, true);
	}

	/**
	 * Returns the hotp where userId = &#63; or returns <code>null</code> if it could not be found, optionally using the finder cache.
	 *
	 * @param userId the user ID
	 * @param retrieveFromCache whether to retrieve from the finder cache
	 * @return the matching hotp, or <code>null</code> if a matching hotp could not be found
	 */
	@Override
	public HOTP fetchByUserId(long userId, boolean retrieveFromCache) {
		Object[] finderArgs = new Object[] { userId };

		Object result = null;

		if (retrieveFromCache) {
			result = finderCache.getResult(FINDER_PATH_FETCH_BY_USERID,
					finderArgs, this);
		}

		if (result instanceof HOTP) {
			HOTP hotp = (HOTP)result;

			if ((userId != hotp.getUserId())) {
				result = null;
			}
		}

		if (result == null) {
			StringBundler query = new StringBundler(3);

			query.append(_SQL_SELECT_HOTP_WHERE);

			query.append(_FINDER_COLUMN_USERID_USERID_2);

			String sql = query.toString();

			Session session = null;

			try {
				session = openSession();

				Query q = session.createQuery(sql);

				QueryPos qPos = QueryPos.getInstance(q);

				qPos.add(userId);

				List<HOTP> list = q.list();

				if (list.isEmpty()) {
					finderCache.putResult(FINDER_PATH_FETCH_BY_USERID,
						finderArgs, list);
				}
				else {
					HOTP hotp = list.get(0);

					result = hotp;

					cacheResult(hotp);
				}
			}
			catch (Exception e) {
				finderCache.removeResult(FINDER_PATH_FETCH_BY_USERID, finderArgs);

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
			return (HOTP)result;
		}
	}

	/**
	 * Removes the hotp where userId = &#63; from the database.
	 *
	 * @param userId the user ID
	 * @return the hotp that was removed
	 */
	@Override
	public HOTP removeByUserId(long userId) throws NoSuchHOTPException {
		HOTP hotp = findByUserId(userId);

		return remove(hotp);
	}

	/**
	 * Returns the number of hotps where userId = &#63;.
	 *
	 * @param userId the user ID
	 * @return the number of matching hotps
	 */
	@Override
	public int countByUserId(long userId) {
		FinderPath finderPath = FINDER_PATH_COUNT_BY_USERID;

		Object[] finderArgs = new Object[] { userId };

		Long count = (Long)finderCache.getResult(finderPath, finderArgs, this);

		if (count == null) {
			StringBundler query = new StringBundler(2);

			query.append(_SQL_COUNT_HOTP_WHERE);

			query.append(_FINDER_COLUMN_USERID_USERID_2);

			String sql = query.toString();

			Session session = null;

			try {
				session = openSession();

				Query q = session.createQuery(sql);

				QueryPos qPos = QueryPos.getInstance(q);

				qPos.add(userId);

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

	private static final String _FINDER_COLUMN_USERID_USERID_2 = "hotp.userId = ?";

	public HOTPPersistenceImpl() {
		setModelClass(HOTP.class);

		setModelImplClass(HOTPImpl.class);
		setModelPKClass(long.class);
		setEntityCacheEnabled(HOTPModelImpl.ENTITY_CACHE_ENABLED);
	}

	/**
	 * Caches the hotp in the entity cache if it is enabled.
	 *
	 * @param hotp the hotp
	 */
	@Override
	public void cacheResult(HOTP hotp) {
		entityCache.putResult(HOTPModelImpl.ENTITY_CACHE_ENABLED,
			HOTPImpl.class, hotp.getPrimaryKey(), hotp);

		finderCache.putResult(FINDER_PATH_FETCH_BY_USERID,
			new Object[] { hotp.getUserId() }, hotp);

		hotp.resetOriginalValues();
	}

	/**
	 * Caches the hotps in the entity cache if it is enabled.
	 *
	 * @param hotps the hotps
	 */
	@Override
	public void cacheResult(List<HOTP> hotps) {
		for (HOTP hotp : hotps) {
			if (entityCache.getResult(HOTPModelImpl.ENTITY_CACHE_ENABLED,
						HOTPImpl.class, hotp.getPrimaryKey()) == null) {
				cacheResult(hotp);
			}
			else {
				hotp.resetOriginalValues();
			}
		}
	}

	/**
	 * Clears the cache for all hotps.
	 *
	 * <p>
	 * The {@link EntityCache} and {@link FinderCache} are both cleared by this method.
	 * </p>
	 */
	@Override
	public void clearCache() {
		entityCache.clearCache(HOTPImpl.class);

		finderCache.clearCache(FINDER_CLASS_NAME_ENTITY);
		finderCache.clearCache(FINDER_CLASS_NAME_LIST_WITH_PAGINATION);
		finderCache.clearCache(FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION);
	}

	/**
	 * Clears the cache for the hotp.
	 *
	 * <p>
	 * The {@link EntityCache} and {@link FinderCache} are both cleared by this method.
	 * </p>
	 */
	@Override
	public void clearCache(HOTP hotp) {
		entityCache.removeResult(HOTPModelImpl.ENTITY_CACHE_ENABLED,
			HOTPImpl.class, hotp.getPrimaryKey());

		finderCache.clearCache(FINDER_CLASS_NAME_LIST_WITH_PAGINATION);
		finderCache.clearCache(FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION);

		clearUniqueFindersCache((HOTPModelImpl)hotp, true);
	}

	@Override
	public void clearCache(List<HOTP> hotps) {
		finderCache.clearCache(FINDER_CLASS_NAME_LIST_WITH_PAGINATION);
		finderCache.clearCache(FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION);

		for (HOTP hotp : hotps) {
			entityCache.removeResult(HOTPModelImpl.ENTITY_CACHE_ENABLED,
				HOTPImpl.class, hotp.getPrimaryKey());

			clearUniqueFindersCache((HOTPModelImpl)hotp, true);
		}
	}

	protected void cacheUniqueFindersCache(HOTPModelImpl hotpModelImpl) {
		Object[] args = new Object[] { hotpModelImpl.getUserId() };

		finderCache.putResult(FINDER_PATH_COUNT_BY_USERID, args,
			Long.valueOf(1), false);
		finderCache.putResult(FINDER_PATH_FETCH_BY_USERID, args, hotpModelImpl,
			false);
	}

	protected void clearUniqueFindersCache(HOTPModelImpl hotpModelImpl,
		boolean clearCurrent) {
		if (clearCurrent) {
			Object[] args = new Object[] { hotpModelImpl.getUserId() };

			finderCache.removeResult(FINDER_PATH_COUNT_BY_USERID, args);
			finderCache.removeResult(FINDER_PATH_FETCH_BY_USERID, args);
		}

		if ((hotpModelImpl.getColumnBitmask() &
				FINDER_PATH_FETCH_BY_USERID.getColumnBitmask()) != 0) {
			Object[] args = new Object[] { hotpModelImpl.getOriginalUserId() };

			finderCache.removeResult(FINDER_PATH_COUNT_BY_USERID, args);
			finderCache.removeResult(FINDER_PATH_FETCH_BY_USERID, args);
		}
	}

	/**
	 * Creates a new hotp with the primary key. Does not add the hotp to the database.
	 *
	 * @param hotpId the primary key for the new hotp
	 * @return the new hotp
	 */
	@Override
	public HOTP create(long hotpId) {
		HOTP hotp = new HOTPImpl();

		hotp.setNew(true);
		hotp.setPrimaryKey(hotpId);

		return hotp;
	}

	/**
	 * Removes the hotp with the primary key from the database. Also notifies the appropriate model listeners.
	 *
	 * @param hotpId the primary key of the hotp
	 * @return the hotp that was removed
	 * @throws NoSuchHOTPException if a hotp with the primary key could not be found
	 */
	@Override
	public HOTP remove(long hotpId) throws NoSuchHOTPException {
		return remove((Serializable)hotpId);
	}

	/**
	 * Removes the hotp with the primary key from the database. Also notifies the appropriate model listeners.
	 *
	 * @param primaryKey the primary key of the hotp
	 * @return the hotp that was removed
	 * @throws NoSuchHOTPException if a hotp with the primary key could not be found
	 */
	@Override
	public HOTP remove(Serializable primaryKey) throws NoSuchHOTPException {
		Session session = null;

		try {
			session = openSession();

			HOTP hotp = (HOTP)session.get(HOTPImpl.class, primaryKey);

			if (hotp == null) {
				if (_log.isDebugEnabled()) {
					_log.debug(_NO_SUCH_ENTITY_WITH_PRIMARY_KEY + primaryKey);
				}

				throw new NoSuchHOTPException(_NO_SUCH_ENTITY_WITH_PRIMARY_KEY +
					primaryKey);
			}

			return remove(hotp);
		}
		catch (NoSuchHOTPException nsee) {
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
	protected HOTP removeImpl(HOTP hotp) {
		Session session = null;

		try {
			session = openSession();

			if (!session.contains(hotp)) {
				hotp = (HOTP)session.get(HOTPImpl.class, hotp.getPrimaryKeyObj());
			}

			if (hotp != null) {
				session.delete(hotp);
			}
		}
		catch (Exception e) {
			throw processException(e);
		}
		finally {
			closeSession(session);
		}

		if (hotp != null) {
			clearCache(hotp);
		}

		return hotp;
	}

	@Override
	public HOTP updateImpl(HOTP hotp) {
		boolean isNew = hotp.isNew();

		if (!(hotp instanceof HOTPModelImpl)) {
			InvocationHandler invocationHandler = null;

			if (ProxyUtil.isProxyClass(hotp.getClass())) {
				invocationHandler = ProxyUtil.getInvocationHandler(hotp);

				throw new IllegalArgumentException(
					"Implement ModelWrapper in hotp proxy " +
					invocationHandler.getClass());
			}

			throw new IllegalArgumentException(
				"Implement ModelWrapper in custom HOTP implementation " +
				hotp.getClass());
		}

		HOTPModelImpl hotpModelImpl = (HOTPModelImpl)hotp;

		Session session = null;

		try {
			session = openSession();

			if (hotp.isNew()) {
				session.save(hotp);

				hotp.setNew(false);
			}
			else {
				hotp = (HOTP)session.merge(hotp);
			}
		}
		catch (Exception e) {
			throw processException(e);
		}
		finally {
			closeSession(session);
		}

		finderCache.clearCache(FINDER_CLASS_NAME_LIST_WITH_PAGINATION);

		if (!HOTPModelImpl.COLUMN_BITMASK_ENABLED) {
			finderCache.clearCache(FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION);
		}
		else
		 if (isNew) {
			finderCache.removeResult(FINDER_PATH_COUNT_ALL, FINDER_ARGS_EMPTY);
			finderCache.removeResult(FINDER_PATH_WITHOUT_PAGINATION_FIND_ALL,
				FINDER_ARGS_EMPTY);
		}

		entityCache.putResult(HOTPModelImpl.ENTITY_CACHE_ENABLED,
			HOTPImpl.class, hotp.getPrimaryKey(), hotp, false);

		clearUniqueFindersCache(hotpModelImpl, false);
		cacheUniqueFindersCache(hotpModelImpl);

		hotp.resetOriginalValues();

		return hotp;
	}

	/**
	 * Returns the hotp with the primary key or throws a {@link com.liferay.portal.kernel.exception.NoSuchModelException} if it could not be found.
	 *
	 * @param primaryKey the primary key of the hotp
	 * @return the hotp
	 * @throws NoSuchHOTPException if a hotp with the primary key could not be found
	 */
	@Override
	public HOTP findByPrimaryKey(Serializable primaryKey)
		throws NoSuchHOTPException {
		HOTP hotp = fetchByPrimaryKey(primaryKey);

		if (hotp == null) {
			if (_log.isDebugEnabled()) {
				_log.debug(_NO_SUCH_ENTITY_WITH_PRIMARY_KEY + primaryKey);
			}

			throw new NoSuchHOTPException(_NO_SUCH_ENTITY_WITH_PRIMARY_KEY +
				primaryKey);
		}

		return hotp;
	}

	/**
	 * Returns the hotp with the primary key or throws a {@link NoSuchHOTPException} if it could not be found.
	 *
	 * @param hotpId the primary key of the hotp
	 * @return the hotp
	 * @throws NoSuchHOTPException if a hotp with the primary key could not be found
	 */
	@Override
	public HOTP findByPrimaryKey(long hotpId) throws NoSuchHOTPException {
		return findByPrimaryKey((Serializable)hotpId);
	}

	/**
	 * Returns the hotp with the primary key or returns <code>null</code> if it could not be found.
	 *
	 * @param hotpId the primary key of the hotp
	 * @return the hotp, or <code>null</code> if a hotp with the primary key could not be found
	 */
	@Override
	public HOTP fetchByPrimaryKey(long hotpId) {
		return fetchByPrimaryKey((Serializable)hotpId);
	}

	/**
	 * Returns all the hotps.
	 *
	 * @return the hotps
	 */
	@Override
	public List<HOTP> findAll() {
		return findAll(QueryUtil.ALL_POS, QueryUtil.ALL_POS, null);
	}

	/**
	 * Returns a range of all the hotps.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to {@link QueryUtil#ALL_POS} will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent and pagination is required (<code>start</code> and <code>end</code> are not {@link QueryUtil#ALL_POS}), then the query will include the default ORDER BY logic from {@link HOTPModelImpl}. If both <code>orderByComparator</code> and pagination are absent, for performance reasons, the query will not have an ORDER BY clause and the returned result set will be sorted on by the primary key in an ascending order.
	 * </p>
	 *
	 * @param start the lower bound of the range of hotps
	 * @param end the upper bound of the range of hotps (not inclusive)
	 * @return the range of hotps
	 */
	@Override
	public List<HOTP> findAll(int start, int end) {
		return findAll(start, end, null);
	}

	/**
	 * Returns an ordered range of all the hotps.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to {@link QueryUtil#ALL_POS} will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent and pagination is required (<code>start</code> and <code>end</code> are not {@link QueryUtil#ALL_POS}), then the query will include the default ORDER BY logic from {@link HOTPModelImpl}. If both <code>orderByComparator</code> and pagination are absent, for performance reasons, the query will not have an ORDER BY clause and the returned result set will be sorted on by the primary key in an ascending order.
	 * </p>
	 *
	 * @param start the lower bound of the range of hotps
	 * @param end the upper bound of the range of hotps (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of hotps
	 */
	@Override
	public List<HOTP> findAll(int start, int end,
		OrderByComparator<HOTP> orderByComparator) {
		return findAll(start, end, orderByComparator, true);
	}

	/**
	 * Returns an ordered range of all the hotps.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to {@link QueryUtil#ALL_POS} will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent and pagination is required (<code>start</code> and <code>end</code> are not {@link QueryUtil#ALL_POS}), then the query will include the default ORDER BY logic from {@link HOTPModelImpl}. If both <code>orderByComparator</code> and pagination are absent, for performance reasons, the query will not have an ORDER BY clause and the returned result set will be sorted on by the primary key in an ascending order.
	 * </p>
	 *
	 * @param start the lower bound of the range of hotps
	 * @param end the upper bound of the range of hotps (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param retrieveFromCache whether to retrieve from the finder cache
	 * @return the ordered range of hotps
	 */
	@Override
	public List<HOTP> findAll(int start, int end,
		OrderByComparator<HOTP> orderByComparator, boolean retrieveFromCache) {
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

		List<HOTP> list = null;

		if (retrieveFromCache) {
			list = (List<HOTP>)finderCache.getResult(finderPath, finderArgs,
					this);
		}

		if (list == null) {
			StringBundler query = null;
			String sql = null;

			if (orderByComparator != null) {
				query = new StringBundler(2 +
						(orderByComparator.getOrderByFields().length * 2));

				query.append(_SQL_SELECT_HOTP);

				appendOrderByComparator(query, _ORDER_BY_ENTITY_ALIAS,
					orderByComparator);

				sql = query.toString();
			}
			else {
				sql = _SQL_SELECT_HOTP;

				if (pagination) {
					sql = sql.concat(HOTPModelImpl.ORDER_BY_JPQL);
				}
			}

			Session session = null;

			try {
				session = openSession();

				Query q = session.createQuery(sql);

				if (!pagination) {
					list = (List<HOTP>)QueryUtil.list(q, getDialect(), start,
							end, false);

					Collections.sort(list);

					list = Collections.unmodifiableList(list);
				}
				else {
					list = (List<HOTP>)QueryUtil.list(q, getDialect(), start,
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
	 * Removes all the hotps from the database.
	 *
	 */
	@Override
	public void removeAll() {
		for (HOTP hotp : findAll()) {
			remove(hotp);
		}
	}

	/**
	 * Returns the number of hotps.
	 *
	 * @return the number of hotps
	 */
	@Override
	public int countAll() {
		Long count = (Long)finderCache.getResult(FINDER_PATH_COUNT_ALL,
				FINDER_ARGS_EMPTY, this);

		if (count == null) {
			Session session = null;

			try {
				session = openSession();

				Query q = session.createQuery(_SQL_COUNT_HOTP);

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
	protected EntityCache getEntityCache() {
		return entityCache;
	}

	@Override
	protected String getPKDBName() {
		return "hotpId";
	}

	@Override
	protected String getSelectSQL() {
		return _SQL_SELECT_HOTP;
	}

	@Override
	protected Map<String, Integer> getTableColumnsMap() {
		return HOTPModelImpl.TABLE_COLUMNS_MAP;
	}

	/**
	 * Initializes the hotp persistence.
	 */
	public void afterPropertiesSet() {
	}

	public void destroy() {
		entityCache.removeCache(HOTPImpl.class.getName());
		finderCache.removeCache(FINDER_CLASS_NAME_ENTITY);
		finderCache.removeCache(FINDER_CLASS_NAME_LIST_WITH_PAGINATION);
		finderCache.removeCache(FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION);
	}

	@ServiceReference(type = EntityCache.class)
	protected EntityCache entityCache;
	@ServiceReference(type = FinderCache.class)
	protected FinderCache finderCache;
	private static final String _SQL_SELECT_HOTP = "SELECT hotp FROM HOTP hotp";
	private static final String _SQL_SELECT_HOTP_WHERE = "SELECT hotp FROM HOTP hotp WHERE ";
	private static final String _SQL_COUNT_HOTP = "SELECT COUNT(hotp) FROM HOTP hotp";
	private static final String _SQL_COUNT_HOTP_WHERE = "SELECT COUNT(hotp) FROM HOTP hotp WHERE ";
	private static final String _ORDER_BY_ENTITY_ALIAS = "hotp.";
	private static final String _NO_SUCH_ENTITY_WITH_PRIMARY_KEY = "No HOTP exists with the primary key ";
	private static final String _NO_SUCH_ENTITY_WITH_KEY = "No HOTP exists with the key {";
	private static final Log _log = LogFactoryUtil.getLog(HOTPPersistenceImpl.class);
}