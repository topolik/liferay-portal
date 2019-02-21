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

package com.liferay.multi.factor.authentication.provider.totp.service.persistence.impl;

import aQute.bnd.annotation.ProviderType;

import com.liferay.multi.factor.authentication.provider.totp.exception.NoSuchTOTPException;
import com.liferay.multi.factor.authentication.provider.totp.model.TOTP;
import com.liferay.multi.factor.authentication.provider.totp.model.impl.TOTPImpl;
import com.liferay.multi.factor.authentication.provider.totp.model.impl.TOTPModelImpl;
import com.liferay.multi.factor.authentication.provider.totp.service.persistence.TOTPPersistence;

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
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.service.ServiceContextThreadLocal;
import com.liferay.portal.kernel.service.persistence.CompanyProvider;
import com.liferay.portal.kernel.service.persistence.CompanyProviderWrapper;
import com.liferay.portal.kernel.service.persistence.impl.BasePersistenceImpl;
import com.liferay.portal.kernel.util.OrderByComparator;
import com.liferay.portal.kernel.util.ProxyUtil;
import com.liferay.portal.spring.extender.service.ServiceReference;

import java.io.Serializable;

import java.lang.reflect.InvocationHandler;

import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * The persistence implementation for the totp service.
 *
 * <p>
 * Caching information and settings can be found in <code>portal.properties</code>
 * </p>
 *
 * @author arthurchan35
 * @see TOTPPersistence
 * @see com.liferay.multi.factor.authentication.provider.totp.service.persistence.TOTPUtil
 * @generated
 */
@ProviderType
public class TOTPPersistenceImpl extends BasePersistenceImpl<TOTP>
	implements TOTPPersistence {
	/*
	 * NOTE FOR DEVELOPERS:
	 *
	 * Never modify or reference this class directly. Always use {@link TOTPUtil} to access the totp persistence. Modify <code>service.xml</code> and rerun ServiceBuilder to regenerate this class.
	 */
	public static final String FINDER_CLASS_NAME_ENTITY = TOTPImpl.class.getName();
	public static final String FINDER_CLASS_NAME_LIST_WITH_PAGINATION = FINDER_CLASS_NAME_ENTITY +
		".List1";
	public static final String FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION = FINDER_CLASS_NAME_ENTITY +
		".List2";
	private FinderPath _finderPathWithPaginationFindAll;
	private FinderPath _finderPathWithoutPaginationFindAll;
	private FinderPath _finderPathCountAll;
	private FinderPath _finderPathFetchByUserId;
	private FinderPath _finderPathCountByUserId;

	/**
	 * Returns the totp where userId = &#63; or throws a {@link NoSuchTOTPException} if it could not be found.
	 *
	 * @param userId the user ID
	 * @return the matching totp
	 * @throws NoSuchTOTPException if a matching totp could not be found
	 */
	@Override
	public TOTP findByUserId(long userId) throws NoSuchTOTPException {
		TOTP totp = fetchByUserId(userId);

		if (totp == null) {
			StringBundler msg = new StringBundler(4);

			msg.append(_NO_SUCH_ENTITY_WITH_KEY);

			msg.append("userId=");
			msg.append(userId);

			msg.append("}");

			if (_log.isDebugEnabled()) {
				_log.debug(msg.toString());
			}

			throw new NoSuchTOTPException(msg.toString());
		}

		return totp;
	}

	/**
	 * Returns the totp where userId = &#63; or returns <code>null</code> if it could not be found. Uses the finder cache.
	 *
	 * @param userId the user ID
	 * @return the matching totp, or <code>null</code> if a matching totp could not be found
	 */
	@Override
	public TOTP fetchByUserId(long userId) {
		return fetchByUserId(userId, true);
	}

	/**
	 * Returns the totp where userId = &#63; or returns <code>null</code> if it could not be found, optionally using the finder cache.
	 *
	 * @param userId the user ID
	 * @param retrieveFromCache whether to retrieve from the finder cache
	 * @return the matching totp, or <code>null</code> if a matching totp could not be found
	 */
	@Override
	public TOTP fetchByUserId(long userId, boolean retrieveFromCache) {
		Object[] finderArgs = new Object[] { userId };

		Object result = null;

		if (retrieveFromCache) {
			result = finderCache.getResult(_finderPathFetchByUserId,
					finderArgs, this);
		}

		if (result instanceof TOTP) {
			TOTP totp = (TOTP)result;

			if ((userId != totp.getUserId())) {
				result = null;
			}
		}

		if (result == null) {
			StringBundler query = new StringBundler(3);

			query.append(_SQL_SELECT_TOTP_WHERE);

			query.append(_FINDER_COLUMN_USERID_USERID_2);

			String sql = query.toString();

			Session session = null;

			try {
				session = openSession();

				Query q = session.createQuery(sql);

				QueryPos qPos = QueryPos.getInstance(q);

				qPos.add(userId);

				List<TOTP> list = q.list();

				if (list.isEmpty()) {
					finderCache.putResult(_finderPathFetchByUserId, finderArgs,
						list);
				}
				else {
					TOTP totp = list.get(0);

					result = totp;

					cacheResult(totp);
				}
			}
			catch (Exception e) {
				finderCache.removeResult(_finderPathFetchByUserId, finderArgs);

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
			return (TOTP)result;
		}
	}

	/**
	 * Removes the totp where userId = &#63; from the database.
	 *
	 * @param userId the user ID
	 * @return the totp that was removed
	 */
	@Override
	public TOTP removeByUserId(long userId) throws NoSuchTOTPException {
		TOTP totp = findByUserId(userId);

		return remove(totp);
	}

	/**
	 * Returns the number of totps where userId = &#63;.
	 *
	 * @param userId the user ID
	 * @return the number of matching totps
	 */
	@Override
	public int countByUserId(long userId) {
		FinderPath finderPath = _finderPathCountByUserId;

		Object[] finderArgs = new Object[] { userId };

		Long count = (Long)finderCache.getResult(finderPath, finderArgs, this);

		if (count == null) {
			StringBundler query = new StringBundler(2);

			query.append(_SQL_COUNT_TOTP_WHERE);

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

	private static final String _FINDER_COLUMN_USERID_USERID_2 = "totp.userId = ?";

	public TOTPPersistenceImpl() {
		setModelClass(TOTP.class);

		setModelImplClass(TOTPImpl.class);
		setModelPKClass(long.class);
		setEntityCacheEnabled(TOTPModelImpl.ENTITY_CACHE_ENABLED);
	}

	/**
	 * Caches the totp in the entity cache if it is enabled.
	 *
	 * @param totp the totp
	 */
	@Override
	public void cacheResult(TOTP totp) {
		entityCache.putResult(TOTPModelImpl.ENTITY_CACHE_ENABLED,
			TOTPImpl.class, totp.getPrimaryKey(), totp);

		finderCache.putResult(_finderPathFetchByUserId,
			new Object[] { totp.getUserId() }, totp);

		totp.resetOriginalValues();
	}

	/**
	 * Caches the totps in the entity cache if it is enabled.
	 *
	 * @param totps the totps
	 */
	@Override
	public void cacheResult(List<TOTP> totps) {
		for (TOTP totp : totps) {
			if (entityCache.getResult(TOTPModelImpl.ENTITY_CACHE_ENABLED,
						TOTPImpl.class, totp.getPrimaryKey()) == null) {
				cacheResult(totp);
			}
			else {
				totp.resetOriginalValues();
			}
		}
	}

	/**
	 * Clears the cache for all totps.
	 *
	 * <p>
	 * The {@link EntityCache} and {@link FinderCache} are both cleared by this method.
	 * </p>
	 */
	@Override
	public void clearCache() {
		entityCache.clearCache(TOTPImpl.class);

		finderCache.clearCache(FINDER_CLASS_NAME_ENTITY);
		finderCache.clearCache(FINDER_CLASS_NAME_LIST_WITH_PAGINATION);
		finderCache.clearCache(FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION);
	}

	/**
	 * Clears the cache for the totp.
	 *
	 * <p>
	 * The {@link EntityCache} and {@link FinderCache} are both cleared by this method.
	 * </p>
	 */
	@Override
	public void clearCache(TOTP totp) {
		entityCache.removeResult(TOTPModelImpl.ENTITY_CACHE_ENABLED,
			TOTPImpl.class, totp.getPrimaryKey());

		finderCache.clearCache(FINDER_CLASS_NAME_LIST_WITH_PAGINATION);
		finderCache.clearCache(FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION);

		clearUniqueFindersCache((TOTPModelImpl)totp, true);
	}

	@Override
	public void clearCache(List<TOTP> totps) {
		finderCache.clearCache(FINDER_CLASS_NAME_LIST_WITH_PAGINATION);
		finderCache.clearCache(FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION);

		for (TOTP totp : totps) {
			entityCache.removeResult(TOTPModelImpl.ENTITY_CACHE_ENABLED,
				TOTPImpl.class, totp.getPrimaryKey());

			clearUniqueFindersCache((TOTPModelImpl)totp, true);
		}
	}

	protected void cacheUniqueFindersCache(TOTPModelImpl totpModelImpl) {
		Object[] args = new Object[] { totpModelImpl.getUserId() };

		finderCache.putResult(_finderPathCountByUserId, args, Long.valueOf(1),
			false);
		finderCache.putResult(_finderPathFetchByUserId, args, totpModelImpl,
			false);
	}

	protected void clearUniqueFindersCache(TOTPModelImpl totpModelImpl,
		boolean clearCurrent) {
		if (clearCurrent) {
			Object[] args = new Object[] { totpModelImpl.getUserId() };

			finderCache.removeResult(_finderPathCountByUserId, args);
			finderCache.removeResult(_finderPathFetchByUserId, args);
		}

		if ((totpModelImpl.getColumnBitmask() &
				_finderPathFetchByUserId.getColumnBitmask()) != 0) {
			Object[] args = new Object[] { totpModelImpl.getOriginalUserId() };

			finderCache.removeResult(_finderPathCountByUserId, args);
			finderCache.removeResult(_finderPathFetchByUserId, args);
		}
	}

	/**
	 * Creates a new totp with the primary key. Does not add the totp to the database.
	 *
	 * @param totpId the primary key for the new totp
	 * @return the new totp
	 */
	@Override
	public TOTP create(long totpId) {
		TOTP totp = new TOTPImpl();

		totp.setNew(true);
		totp.setPrimaryKey(totpId);

		totp.setCompanyId(companyProvider.getCompanyId());

		return totp;
	}

	/**
	 * Removes the totp with the primary key from the database. Also notifies the appropriate model listeners.
	 *
	 * @param totpId the primary key of the totp
	 * @return the totp that was removed
	 * @throws NoSuchTOTPException if a totp with the primary key could not be found
	 */
	@Override
	public TOTP remove(long totpId) throws NoSuchTOTPException {
		return remove((Serializable)totpId);
	}

	/**
	 * Removes the totp with the primary key from the database. Also notifies the appropriate model listeners.
	 *
	 * @param primaryKey the primary key of the totp
	 * @return the totp that was removed
	 * @throws NoSuchTOTPException if a totp with the primary key could not be found
	 */
	@Override
	public TOTP remove(Serializable primaryKey) throws NoSuchTOTPException {
		Session session = null;

		try {
			session = openSession();

			TOTP totp = (TOTP)session.get(TOTPImpl.class, primaryKey);

			if (totp == null) {
				if (_log.isDebugEnabled()) {
					_log.debug(_NO_SUCH_ENTITY_WITH_PRIMARY_KEY + primaryKey);
				}

				throw new NoSuchTOTPException(_NO_SUCH_ENTITY_WITH_PRIMARY_KEY +
					primaryKey);
			}

			return remove(totp);
		}
		catch (NoSuchTOTPException nsee) {
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
	protected TOTP removeImpl(TOTP totp) {
		Session session = null;

		try {
			session = openSession();

			if (!session.contains(totp)) {
				totp = (TOTP)session.get(TOTPImpl.class, totp.getPrimaryKeyObj());
			}

			if (totp != null) {
				session.delete(totp);
			}
		}
		catch (Exception e) {
			throw processException(e);
		}
		finally {
			closeSession(session);
		}

		if (totp != null) {
			clearCache(totp);
		}

		return totp;
	}

	@Override
	public TOTP updateImpl(TOTP totp) {
		boolean isNew = totp.isNew();

		if (!(totp instanceof TOTPModelImpl)) {
			InvocationHandler invocationHandler = null;

			if (ProxyUtil.isProxyClass(totp.getClass())) {
				invocationHandler = ProxyUtil.getInvocationHandler(totp);

				throw new IllegalArgumentException(
					"Implement ModelWrapper in totp proxy " +
					invocationHandler.getClass());
			}

			throw new IllegalArgumentException(
				"Implement ModelWrapper in custom TOTP implementation " +
				totp.getClass());
		}

		TOTPModelImpl totpModelImpl = (TOTPModelImpl)totp;

		ServiceContext serviceContext = ServiceContextThreadLocal.getServiceContext();

		Date now = new Date();

		if (isNew && (totp.getCreateDate() == null)) {
			if (serviceContext == null) {
				totp.setCreateDate(now);
			}
			else {
				totp.setCreateDate(serviceContext.getCreateDate(now));
			}
		}

		if (!totpModelImpl.hasSetModifiedDate()) {
			if (serviceContext == null) {
				totp.setModifiedDate(now);
			}
			else {
				totp.setModifiedDate(serviceContext.getModifiedDate(now));
			}
		}

		Session session = null;

		try {
			session = openSession();

			if (totp.isNew()) {
				session.save(totp);

				totp.setNew(false);
			}
			else {
				totp = (TOTP)session.merge(totp);
			}
		}
		catch (Exception e) {
			throw processException(e);
		}
		finally {
			closeSession(session);
		}

		finderCache.clearCache(FINDER_CLASS_NAME_LIST_WITH_PAGINATION);

		if (!TOTPModelImpl.COLUMN_BITMASK_ENABLED) {
			finderCache.clearCache(FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION);
		}
		else
		 if (isNew) {
			finderCache.removeResult(_finderPathCountAll, FINDER_ARGS_EMPTY);
			finderCache.removeResult(_finderPathWithoutPaginationFindAll,
				FINDER_ARGS_EMPTY);
		}

		entityCache.putResult(TOTPModelImpl.ENTITY_CACHE_ENABLED,
			TOTPImpl.class, totp.getPrimaryKey(), totp, false);

		clearUniqueFindersCache(totpModelImpl, false);
		cacheUniqueFindersCache(totpModelImpl);

		totp.resetOriginalValues();

		return totp;
	}

	/**
	 * Returns the totp with the primary key or throws a {@link com.liferay.portal.kernel.exception.NoSuchModelException} if it could not be found.
	 *
	 * @param primaryKey the primary key of the totp
	 * @return the totp
	 * @throws NoSuchTOTPException if a totp with the primary key could not be found
	 */
	@Override
	public TOTP findByPrimaryKey(Serializable primaryKey)
		throws NoSuchTOTPException {
		TOTP totp = fetchByPrimaryKey(primaryKey);

		if (totp == null) {
			if (_log.isDebugEnabled()) {
				_log.debug(_NO_SUCH_ENTITY_WITH_PRIMARY_KEY + primaryKey);
			}

			throw new NoSuchTOTPException(_NO_SUCH_ENTITY_WITH_PRIMARY_KEY +
				primaryKey);
		}

		return totp;
	}

	/**
	 * Returns the totp with the primary key or throws a {@link NoSuchTOTPException} if it could not be found.
	 *
	 * @param totpId the primary key of the totp
	 * @return the totp
	 * @throws NoSuchTOTPException if a totp with the primary key could not be found
	 */
	@Override
	public TOTP findByPrimaryKey(long totpId) throws NoSuchTOTPException {
		return findByPrimaryKey((Serializable)totpId);
	}

	/**
	 * Returns the totp with the primary key or returns <code>null</code> if it could not be found.
	 *
	 * @param totpId the primary key of the totp
	 * @return the totp, or <code>null</code> if a totp with the primary key could not be found
	 */
	@Override
	public TOTP fetchByPrimaryKey(long totpId) {
		return fetchByPrimaryKey((Serializable)totpId);
	}

	/**
	 * Returns all the totps.
	 *
	 * @return the totps
	 */
	@Override
	public List<TOTP> findAll() {
		return findAll(QueryUtil.ALL_POS, QueryUtil.ALL_POS, null);
	}

	/**
	 * Returns a range of all the totps.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to {@link QueryUtil#ALL_POS} will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent and pagination is required (<code>start</code> and <code>end</code> are not {@link QueryUtil#ALL_POS}), then the query will include the default ORDER BY logic from {@link TOTPModelImpl}. If both <code>orderByComparator</code> and pagination are absent, for performance reasons, the query will not have an ORDER BY clause and the returned result set will be sorted on by the primary key in an ascending order.
	 * </p>
	 *
	 * @param start the lower bound of the range of totps
	 * @param end the upper bound of the range of totps (not inclusive)
	 * @return the range of totps
	 */
	@Override
	public List<TOTP> findAll(int start, int end) {
		return findAll(start, end, null);
	}

	/**
	 * Returns an ordered range of all the totps.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to {@link QueryUtil#ALL_POS} will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent and pagination is required (<code>start</code> and <code>end</code> are not {@link QueryUtil#ALL_POS}), then the query will include the default ORDER BY logic from {@link TOTPModelImpl}. If both <code>orderByComparator</code> and pagination are absent, for performance reasons, the query will not have an ORDER BY clause and the returned result set will be sorted on by the primary key in an ascending order.
	 * </p>
	 *
	 * @param start the lower bound of the range of totps
	 * @param end the upper bound of the range of totps (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of totps
	 */
	@Override
	public List<TOTP> findAll(int start, int end,
		OrderByComparator<TOTP> orderByComparator) {
		return findAll(start, end, orderByComparator, true);
	}

	/**
	 * Returns an ordered range of all the totps.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to {@link QueryUtil#ALL_POS} will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent and pagination is required (<code>start</code> and <code>end</code> are not {@link QueryUtil#ALL_POS}), then the query will include the default ORDER BY logic from {@link TOTPModelImpl}. If both <code>orderByComparator</code> and pagination are absent, for performance reasons, the query will not have an ORDER BY clause and the returned result set will be sorted on by the primary key in an ascending order.
	 * </p>
	 *
	 * @param start the lower bound of the range of totps
	 * @param end the upper bound of the range of totps (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param retrieveFromCache whether to retrieve from the finder cache
	 * @return the ordered range of totps
	 */
	@Override
	public List<TOTP> findAll(int start, int end,
		OrderByComparator<TOTP> orderByComparator, boolean retrieveFromCache) {
		boolean pagination = true;
		FinderPath finderPath = null;
		Object[] finderArgs = null;

		if ((start == QueryUtil.ALL_POS) && (end == QueryUtil.ALL_POS) &&
				(orderByComparator == null)) {
			pagination = false;
			finderPath = _finderPathWithoutPaginationFindAll;
			finderArgs = FINDER_ARGS_EMPTY;
		}
		else {
			finderPath = _finderPathWithPaginationFindAll;
			finderArgs = new Object[] { start, end, orderByComparator };
		}

		List<TOTP> list = null;

		if (retrieveFromCache) {
			list = (List<TOTP>)finderCache.getResult(finderPath, finderArgs,
					this);
		}

		if (list == null) {
			StringBundler query = null;
			String sql = null;

			if (orderByComparator != null) {
				query = new StringBundler(2 +
						(orderByComparator.getOrderByFields().length * 2));

				query.append(_SQL_SELECT_TOTP);

				appendOrderByComparator(query, _ORDER_BY_ENTITY_ALIAS,
					orderByComparator);

				sql = query.toString();
			}
			else {
				sql = _SQL_SELECT_TOTP;

				if (pagination) {
					sql = sql.concat(TOTPModelImpl.ORDER_BY_JPQL);
				}
			}

			Session session = null;

			try {
				session = openSession();

				Query q = session.createQuery(sql);

				if (!pagination) {
					list = (List<TOTP>)QueryUtil.list(q, getDialect(), start,
							end, false);

					Collections.sort(list);

					list = Collections.unmodifiableList(list);
				}
				else {
					list = (List<TOTP>)QueryUtil.list(q, getDialect(), start,
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
	 * Removes all the totps from the database.
	 *
	 */
	@Override
	public void removeAll() {
		for (TOTP totp : findAll()) {
			remove(totp);
		}
	}

	/**
	 * Returns the number of totps.
	 *
	 * @return the number of totps
	 */
	@Override
	public int countAll() {
		Long count = (Long)finderCache.getResult(_finderPathCountAll,
				FINDER_ARGS_EMPTY, this);

		if (count == null) {
			Session session = null;

			try {
				session = openSession();

				Query q = session.createQuery(_SQL_COUNT_TOTP);

				count = (Long)q.uniqueResult();

				finderCache.putResult(_finderPathCountAll, FINDER_ARGS_EMPTY,
					count);
			}
			catch (Exception e) {
				finderCache.removeResult(_finderPathCountAll, FINDER_ARGS_EMPTY);

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
		return "totpId";
	}

	@Override
	protected String getSelectSQL() {
		return _SQL_SELECT_TOTP;
	}

	@Override
	protected Map<String, Integer> getTableColumnsMap() {
		return TOTPModelImpl.TABLE_COLUMNS_MAP;
	}

	/**
	 * Initializes the totp persistence.
	 */
	public void afterPropertiesSet() {
		_finderPathWithPaginationFindAll = new FinderPath(TOTPModelImpl.ENTITY_CACHE_ENABLED,
				TOTPModelImpl.FINDER_CACHE_ENABLED, TOTPImpl.class,
				FINDER_CLASS_NAME_LIST_WITH_PAGINATION, "findAll", new String[0]);

		_finderPathWithoutPaginationFindAll = new FinderPath(TOTPModelImpl.ENTITY_CACHE_ENABLED,
				TOTPModelImpl.FINDER_CACHE_ENABLED, TOTPImpl.class,
				FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "findAll",
				new String[0]);

		_finderPathCountAll = new FinderPath(TOTPModelImpl.ENTITY_CACHE_ENABLED,
				TOTPModelImpl.FINDER_CACHE_ENABLED, Long.class,
				FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "countAll",
				new String[0]);

		_finderPathFetchByUserId = new FinderPath(TOTPModelImpl.ENTITY_CACHE_ENABLED,
				TOTPModelImpl.FINDER_CACHE_ENABLED, TOTPImpl.class,
				FINDER_CLASS_NAME_ENTITY, "fetchByUserId",
				new String[] { Long.class.getName() },
				TOTPModelImpl.USERID_COLUMN_BITMASK);

		_finderPathCountByUserId = new FinderPath(TOTPModelImpl.ENTITY_CACHE_ENABLED,
				TOTPModelImpl.FINDER_CACHE_ENABLED, Long.class,
				FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "countByUserId",
				new String[] { Long.class.getName() });
	}

	public void destroy() {
		entityCache.removeCache(TOTPImpl.class.getName());
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
	private static final String _SQL_SELECT_TOTP = "SELECT totp FROM TOTP totp";
	private static final String _SQL_SELECT_TOTP_WHERE = "SELECT totp FROM TOTP totp WHERE ";
	private static final String _SQL_COUNT_TOTP = "SELECT COUNT(totp) FROM TOTP totp";
	private static final String _SQL_COUNT_TOTP_WHERE = "SELECT COUNT(totp) FROM TOTP totp WHERE ";
	private static final String _ORDER_BY_ENTITY_ALIAS = "totp.";
	private static final String _NO_SUCH_ENTITY_WITH_PRIMARY_KEY = "No TOTP exists with the primary key ";
	private static final String _NO_SUCH_ENTITY_WITH_KEY = "No TOTP exists with the key {";
	private static final Log _log = LogFactoryUtil.getLog(TOTPPersistenceImpl.class);
}