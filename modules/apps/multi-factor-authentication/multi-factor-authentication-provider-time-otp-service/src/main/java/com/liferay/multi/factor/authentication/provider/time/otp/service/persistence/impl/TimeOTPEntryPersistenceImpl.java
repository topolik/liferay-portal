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

package com.liferay.multi.factor.authentication.provider.time.otp.service.persistence.impl;

import aQute.bnd.annotation.ProviderType;

import com.liferay.multi.factor.authentication.provider.time.otp.exception.NoSuchEntryException;
import com.liferay.multi.factor.authentication.provider.time.otp.model.TimeOTPEntry;
import com.liferay.multi.factor.authentication.provider.time.otp.model.impl.TimeOTPEntryImpl;
import com.liferay.multi.factor.authentication.provider.time.otp.model.impl.TimeOTPEntryModelImpl;
import com.liferay.multi.factor.authentication.provider.time.otp.service.persistence.TimeOTPEntryPersistence;
import com.liferay.multi.factor.authentication.provider.time.otp.service.persistence.impl.constants.TimeOTPPersistenceConstants;
import com.liferay.petra.string.StringBundler;
import com.liferay.portal.kernel.configuration.Configuration;
import com.liferay.portal.kernel.dao.orm.EntityCache;
import com.liferay.portal.kernel.dao.orm.FinderCache;
import com.liferay.portal.kernel.dao.orm.FinderPath;
import com.liferay.portal.kernel.dao.orm.Query;
import com.liferay.portal.kernel.dao.orm.QueryPos;
import com.liferay.portal.kernel.dao.orm.QueryUtil;
import com.liferay.portal.kernel.dao.orm.Session;
import com.liferay.portal.kernel.dao.orm.SessionFactory;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.service.ServiceContextThreadLocal;
import com.liferay.portal.kernel.service.persistence.CompanyProvider;
import com.liferay.portal.kernel.service.persistence.CompanyProviderWrapper;
import com.liferay.portal.kernel.service.persistence.impl.BasePersistenceImpl;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.OrderByComparator;
import com.liferay.portal.kernel.util.ProxyUtil;

import java.io.Serializable;

import java.lang.reflect.InvocationHandler;

import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Map;

import javax.sql.DataSource;

import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Deactivate;
import org.osgi.service.component.annotations.Reference;

/**
 * The persistence implementation for the time otp entry service.
 *
 * <p>
 * Caching information and settings can be found in <code>portal.properties</code>
 * </p>
 *
 * @author arthurchan35
 * @generated
 */
@Component(service = TimeOTPEntryPersistence.class)
@ProviderType
public class TimeOTPEntryPersistenceImpl
	extends BasePersistenceImpl<TimeOTPEntry>
	implements TimeOTPEntryPersistence {

	/*
	 * NOTE FOR DEVELOPERS:
	 *
	 * Never modify or reference this class directly. Always use <code>TimeOTPEntryUtil</code> to access the time otp entry persistence. Modify <code>service.xml</code> and rerun ServiceBuilder to regenerate this class.
	 */
	public static final String FINDER_CLASS_NAME_ENTITY =
		TimeOTPEntryImpl.class.getName();

	public static final String FINDER_CLASS_NAME_LIST_WITH_PAGINATION =
		FINDER_CLASS_NAME_ENTITY + ".List1";

	public static final String FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION =
		FINDER_CLASS_NAME_ENTITY + ".List2";

	private FinderPath _finderPathWithPaginationFindAll;
	private FinderPath _finderPathWithoutPaginationFindAll;
	private FinderPath _finderPathCountAll;
	private FinderPath _finderPathFetchByUserId;
	private FinderPath _finderPathCountByUserId;

	/**
	 * Returns the time otp entry where userId = &#63; or throws a <code>NoSuchEntryException</code> if it could not be found.
	 *
	 * @param userId the user ID
	 * @return the matching time otp entry
	 * @throws NoSuchEntryException if a matching time otp entry could not be found
	 */
	@Override
	public TimeOTPEntry findByUserId(long userId) throws NoSuchEntryException {
		TimeOTPEntry timeOTPEntry = fetchByUserId(userId);

		if (timeOTPEntry == null) {
			StringBundler msg = new StringBundler(4);

			msg.append(_NO_SUCH_ENTITY_WITH_KEY);

			msg.append("userId=");
			msg.append(userId);

			msg.append("}");

			if (_log.isDebugEnabled()) {
				_log.debug(msg.toString());
			}

			throw new NoSuchEntryException(msg.toString());
		}

		return timeOTPEntry;
	}

	/**
	 * Returns the time otp entry where userId = &#63; or returns <code>null</code> if it could not be found. Uses the finder cache.
	 *
	 * @param userId the user ID
	 * @return the matching time otp entry, or <code>null</code> if a matching time otp entry could not be found
	 */
	@Override
	public TimeOTPEntry fetchByUserId(long userId) {
		return fetchByUserId(userId, true);
	}

	/**
	 * Returns the time otp entry where userId = &#63; or returns <code>null</code> if it could not be found, optionally using the finder cache.
	 *
	 * @param userId the user ID
	 * @param retrieveFromCache whether to retrieve from the finder cache
	 * @return the matching time otp entry, or <code>null</code> if a matching time otp entry could not be found
	 */
	@Override
	public TimeOTPEntry fetchByUserId(long userId, boolean retrieveFromCache) {
		Object[] finderArgs = new Object[] {userId};

		Object result = null;

		if (retrieveFromCache) {
			result = finderCache.getResult(
				_finderPathFetchByUserId, finderArgs, this);
		}

		if (result instanceof TimeOTPEntry) {
			TimeOTPEntry timeOTPEntry = (TimeOTPEntry)result;

			if ((userId != timeOTPEntry.getUserId())) {
				result = null;
			}
		}

		if (result == null) {
			StringBundler query = new StringBundler(3);

			query.append(_SQL_SELECT_TIMEOTPENTRY_WHERE);

			query.append(_FINDER_COLUMN_USERID_USERID_2);

			String sql = query.toString();

			Session session = null;

			try {
				session = openSession();

				Query q = session.createQuery(sql);

				QueryPos qPos = QueryPos.getInstance(q);

				qPos.add(userId);

				List<TimeOTPEntry> list = q.list();

				if (list.isEmpty()) {
					finderCache.putResult(
						_finderPathFetchByUserId, finderArgs, list);
				}
				else {
					TimeOTPEntry timeOTPEntry = list.get(0);

					result = timeOTPEntry;

					cacheResult(timeOTPEntry);
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
			return (TimeOTPEntry)result;
		}
	}

	/**
	 * Removes the time otp entry where userId = &#63; from the database.
	 *
	 * @param userId the user ID
	 * @return the time otp entry that was removed
	 */
	@Override
	public TimeOTPEntry removeByUserId(long userId)
		throws NoSuchEntryException {

		TimeOTPEntry timeOTPEntry = findByUserId(userId);

		return remove(timeOTPEntry);
	}

	/**
	 * Returns the number of time otp entries where userId = &#63;.
	 *
	 * @param userId the user ID
	 * @return the number of matching time otp entries
	 */
	@Override
	public int countByUserId(long userId) {
		FinderPath finderPath = _finderPathCountByUserId;

		Object[] finderArgs = new Object[] {userId};

		Long count = (Long)finderCache.getResult(finderPath, finderArgs, this);

		if (count == null) {
			StringBundler query = new StringBundler(2);

			query.append(_SQL_COUNT_TIMEOTPENTRY_WHERE);

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

	private static final String _FINDER_COLUMN_USERID_USERID_2 =
		"timeOTPEntry.userId = ?";

	public TimeOTPEntryPersistenceImpl() {
		setModelClass(TimeOTPEntry.class);

		setModelImplClass(TimeOTPEntryImpl.class);
		setModelPKClass(long.class);
	}

	/**
	 * Caches the time otp entry in the entity cache if it is enabled.
	 *
	 * @param timeOTPEntry the time otp entry
	 */
	@Override
	public void cacheResult(TimeOTPEntry timeOTPEntry) {
		entityCache.putResult(
			entityCacheEnabled, TimeOTPEntryImpl.class,
			timeOTPEntry.getPrimaryKey(), timeOTPEntry);

		finderCache.putResult(
			_finderPathFetchByUserId, new Object[] {timeOTPEntry.getUserId()},
			timeOTPEntry);

		timeOTPEntry.resetOriginalValues();
	}

	/**
	 * Caches the time otp entries in the entity cache if it is enabled.
	 *
	 * @param timeOTPEntries the time otp entries
	 */
	@Override
	public void cacheResult(List<TimeOTPEntry> timeOTPEntries) {
		for (TimeOTPEntry timeOTPEntry : timeOTPEntries) {
			if (entityCache.getResult(
					entityCacheEnabled, TimeOTPEntryImpl.class,
					timeOTPEntry.getPrimaryKey()) == null) {

				cacheResult(timeOTPEntry);
			}
			else {
				timeOTPEntry.resetOriginalValues();
			}
		}
	}

	/**
	 * Clears the cache for all time otp entries.
	 *
	 * <p>
	 * The <code>EntityCache</code> and <code>FinderCache</code> are both cleared by this method.
	 * </p>
	 */
	@Override
	public void clearCache() {
		entityCache.clearCache(TimeOTPEntryImpl.class);

		finderCache.clearCache(FINDER_CLASS_NAME_ENTITY);
		finderCache.clearCache(FINDER_CLASS_NAME_LIST_WITH_PAGINATION);
		finderCache.clearCache(FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION);
	}

	/**
	 * Clears the cache for the time otp entry.
	 *
	 * <p>
	 * The <code>EntityCache</code> and <code>FinderCache</code> are both cleared by this method.
	 * </p>
	 */
	@Override
	public void clearCache(TimeOTPEntry timeOTPEntry) {
		entityCache.removeResult(
			entityCacheEnabled, TimeOTPEntryImpl.class,
			timeOTPEntry.getPrimaryKey());

		finderCache.clearCache(FINDER_CLASS_NAME_LIST_WITH_PAGINATION);
		finderCache.clearCache(FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION);

		clearUniqueFindersCache((TimeOTPEntryModelImpl)timeOTPEntry, true);
	}

	@Override
	public void clearCache(List<TimeOTPEntry> timeOTPEntries) {
		finderCache.clearCache(FINDER_CLASS_NAME_LIST_WITH_PAGINATION);
		finderCache.clearCache(FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION);

		for (TimeOTPEntry timeOTPEntry : timeOTPEntries) {
			entityCache.removeResult(
				entityCacheEnabled, TimeOTPEntryImpl.class,
				timeOTPEntry.getPrimaryKey());

			clearUniqueFindersCache((TimeOTPEntryModelImpl)timeOTPEntry, true);
		}
	}

	protected void cacheUniqueFindersCache(
		TimeOTPEntryModelImpl timeOTPEntryModelImpl) {

		Object[] args = new Object[] {timeOTPEntryModelImpl.getUserId()};

		finderCache.putResult(
			_finderPathCountByUserId, args, Long.valueOf(1), false);
		finderCache.putResult(
			_finderPathFetchByUserId, args, timeOTPEntryModelImpl, false);
	}

	protected void clearUniqueFindersCache(
		TimeOTPEntryModelImpl timeOTPEntryModelImpl, boolean clearCurrent) {

		if (clearCurrent) {
			Object[] args = new Object[] {timeOTPEntryModelImpl.getUserId()};

			finderCache.removeResult(_finderPathCountByUserId, args);
			finderCache.removeResult(_finderPathFetchByUserId, args);
		}

		if ((timeOTPEntryModelImpl.getColumnBitmask() &
			 _finderPathFetchByUserId.getColumnBitmask()) != 0) {

			Object[] args = new Object[] {
				timeOTPEntryModelImpl.getOriginalUserId()
			};

			finderCache.removeResult(_finderPathCountByUserId, args);
			finderCache.removeResult(_finderPathFetchByUserId, args);
		}
	}

	/**
	 * Creates a new time otp entry with the primary key. Does not add the time otp entry to the database.
	 *
	 * @param entryId the primary key for the new time otp entry
	 * @return the new time otp entry
	 */
	@Override
	public TimeOTPEntry create(long entryId) {
		TimeOTPEntry timeOTPEntry = new TimeOTPEntryImpl();

		timeOTPEntry.setNew(true);
		timeOTPEntry.setPrimaryKey(entryId);

		timeOTPEntry.setCompanyId(companyProvider.getCompanyId());

		return timeOTPEntry;
	}

	/**
	 * Removes the time otp entry with the primary key from the database. Also notifies the appropriate model listeners.
	 *
	 * @param entryId the primary key of the time otp entry
	 * @return the time otp entry that was removed
	 * @throws NoSuchEntryException if a time otp entry with the primary key could not be found
	 */
	@Override
	public TimeOTPEntry remove(long entryId) throws NoSuchEntryException {
		return remove((Serializable)entryId);
	}

	/**
	 * Removes the time otp entry with the primary key from the database. Also notifies the appropriate model listeners.
	 *
	 * @param primaryKey the primary key of the time otp entry
	 * @return the time otp entry that was removed
	 * @throws NoSuchEntryException if a time otp entry with the primary key could not be found
	 */
	@Override
	public TimeOTPEntry remove(Serializable primaryKey)
		throws NoSuchEntryException {

		Session session = null;

		try {
			session = openSession();

			TimeOTPEntry timeOTPEntry = (TimeOTPEntry)session.get(
				TimeOTPEntryImpl.class, primaryKey);

			if (timeOTPEntry == null) {
				if (_log.isDebugEnabled()) {
					_log.debug(_NO_SUCH_ENTITY_WITH_PRIMARY_KEY + primaryKey);
				}

				throw new NoSuchEntryException(
					_NO_SUCH_ENTITY_WITH_PRIMARY_KEY + primaryKey);
			}

			return remove(timeOTPEntry);
		}
		catch (NoSuchEntryException nsee) {
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
	protected TimeOTPEntry removeImpl(TimeOTPEntry timeOTPEntry) {
		Session session = null;

		try {
			session = openSession();

			if (!session.contains(timeOTPEntry)) {
				timeOTPEntry = (TimeOTPEntry)session.get(
					TimeOTPEntryImpl.class, timeOTPEntry.getPrimaryKeyObj());
			}

			if (timeOTPEntry != null) {
				session.delete(timeOTPEntry);
			}
		}
		catch (Exception e) {
			throw processException(e);
		}
		finally {
			closeSession(session);
		}

		if (timeOTPEntry != null) {
			clearCache(timeOTPEntry);
		}

		return timeOTPEntry;
	}

	@Override
	public TimeOTPEntry updateImpl(TimeOTPEntry timeOTPEntry) {
		boolean isNew = timeOTPEntry.isNew();

		if (!(timeOTPEntry instanceof TimeOTPEntryModelImpl)) {
			InvocationHandler invocationHandler = null;

			if (ProxyUtil.isProxyClass(timeOTPEntry.getClass())) {
				invocationHandler = ProxyUtil.getInvocationHandler(
					timeOTPEntry);

				throw new IllegalArgumentException(
					"Implement ModelWrapper in timeOTPEntry proxy " +
						invocationHandler.getClass());
			}

			throw new IllegalArgumentException(
				"Implement ModelWrapper in custom TimeOTPEntry implementation " +
					timeOTPEntry.getClass());
		}

		TimeOTPEntryModelImpl timeOTPEntryModelImpl =
			(TimeOTPEntryModelImpl)timeOTPEntry;

		ServiceContext serviceContext =
			ServiceContextThreadLocal.getServiceContext();

		Date now = new Date();

		if (isNew && (timeOTPEntry.getCreateDate() == null)) {
			if (serviceContext == null) {
				timeOTPEntry.setCreateDate(now);
			}
			else {
				timeOTPEntry.setCreateDate(serviceContext.getCreateDate(now));
			}
		}

		if (!timeOTPEntryModelImpl.hasSetModifiedDate()) {
			if (serviceContext == null) {
				timeOTPEntry.setModifiedDate(now);
			}
			else {
				timeOTPEntry.setModifiedDate(
					serviceContext.getModifiedDate(now));
			}
		}

		Session session = null;

		try {
			session = openSession();

			if (timeOTPEntry.isNew()) {
				session.save(timeOTPEntry);

				timeOTPEntry.setNew(false);
			}
			else {
				timeOTPEntry = (TimeOTPEntry)session.merge(timeOTPEntry);
			}
		}
		catch (Exception e) {
			throw processException(e);
		}
		finally {
			closeSession(session);
		}

		finderCache.clearCache(FINDER_CLASS_NAME_LIST_WITH_PAGINATION);

		if (!_columnBitmaskEnabled) {
			finderCache.clearCache(FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION);
		}
		else if (isNew) {
			finderCache.removeResult(_finderPathCountAll, FINDER_ARGS_EMPTY);
			finderCache.removeResult(
				_finderPathWithoutPaginationFindAll, FINDER_ARGS_EMPTY);
		}

		entityCache.putResult(
			entityCacheEnabled, TimeOTPEntryImpl.class,
			timeOTPEntry.getPrimaryKey(), timeOTPEntry, false);

		clearUniqueFindersCache(timeOTPEntryModelImpl, false);
		cacheUniqueFindersCache(timeOTPEntryModelImpl);

		timeOTPEntry.resetOriginalValues();

		return timeOTPEntry;
	}

	/**
	 * Returns the time otp entry with the primary key or throws a <code>com.liferay.portal.kernel.exception.NoSuchModelException</code> if it could not be found.
	 *
	 * @param primaryKey the primary key of the time otp entry
	 * @return the time otp entry
	 * @throws NoSuchEntryException if a time otp entry with the primary key could not be found
	 */
	@Override
	public TimeOTPEntry findByPrimaryKey(Serializable primaryKey)
		throws NoSuchEntryException {

		TimeOTPEntry timeOTPEntry = fetchByPrimaryKey(primaryKey);

		if (timeOTPEntry == null) {
			if (_log.isDebugEnabled()) {
				_log.debug(_NO_SUCH_ENTITY_WITH_PRIMARY_KEY + primaryKey);
			}

			throw new NoSuchEntryException(
				_NO_SUCH_ENTITY_WITH_PRIMARY_KEY + primaryKey);
		}

		return timeOTPEntry;
	}

	/**
	 * Returns the time otp entry with the primary key or throws a <code>NoSuchEntryException</code> if it could not be found.
	 *
	 * @param entryId the primary key of the time otp entry
	 * @return the time otp entry
	 * @throws NoSuchEntryException if a time otp entry with the primary key could not be found
	 */
	@Override
	public TimeOTPEntry findByPrimaryKey(long entryId)
		throws NoSuchEntryException {

		return findByPrimaryKey((Serializable)entryId);
	}

	/**
	 * Returns the time otp entry with the primary key or returns <code>null</code> if it could not be found.
	 *
	 * @param entryId the primary key of the time otp entry
	 * @return the time otp entry, or <code>null</code> if a time otp entry with the primary key could not be found
	 */
	@Override
	public TimeOTPEntry fetchByPrimaryKey(long entryId) {
		return fetchByPrimaryKey((Serializable)entryId);
	}

	/**
	 * Returns all the time otp entries.
	 *
	 * @return the time otp entries
	 */
	@Override
	public List<TimeOTPEntry> findAll() {
		return findAll(QueryUtil.ALL_POS, QueryUtil.ALL_POS, null);
	}

	/**
	 * Returns a range of all the time otp entries.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent and pagination is required (<code>start</code> and <code>end</code> are not <code>QueryUtil#ALL_POS</code>), then the query will include the default ORDER BY logic from <code>TimeOTPEntryModelImpl</code>. If both <code>orderByComparator</code> and pagination are absent, for performance reasons, the query will not have an ORDER BY clause and the returned result set will be sorted on by the primary key in an ascending order.
	 * </p>
	 *
	 * @param start the lower bound of the range of time otp entries
	 * @param end the upper bound of the range of time otp entries (not inclusive)
	 * @return the range of time otp entries
	 */
	@Override
	public List<TimeOTPEntry> findAll(int start, int end) {
		return findAll(start, end, null);
	}

	/**
	 * Returns an ordered range of all the time otp entries.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent and pagination is required (<code>start</code> and <code>end</code> are not <code>QueryUtil#ALL_POS</code>), then the query will include the default ORDER BY logic from <code>TimeOTPEntryModelImpl</code>. If both <code>orderByComparator</code> and pagination are absent, for performance reasons, the query will not have an ORDER BY clause and the returned result set will be sorted on by the primary key in an ascending order.
	 * </p>
	 *
	 * @param start the lower bound of the range of time otp entries
	 * @param end the upper bound of the range of time otp entries (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of time otp entries
	 */
	@Override
	public List<TimeOTPEntry> findAll(
		int start, int end, OrderByComparator<TimeOTPEntry> orderByComparator) {

		return findAll(start, end, orderByComparator, true);
	}

	/**
	 * Returns an ordered range of all the time otp entries.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent and pagination is required (<code>start</code> and <code>end</code> are not <code>QueryUtil#ALL_POS</code>), then the query will include the default ORDER BY logic from <code>TimeOTPEntryModelImpl</code>. If both <code>orderByComparator</code> and pagination are absent, for performance reasons, the query will not have an ORDER BY clause and the returned result set will be sorted on by the primary key in an ascending order.
	 * </p>
	 *
	 * @param start the lower bound of the range of time otp entries
	 * @param end the upper bound of the range of time otp entries (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param retrieveFromCache whether to retrieve from the finder cache
	 * @return the ordered range of time otp entries
	 */
	@Override
	public List<TimeOTPEntry> findAll(
		int start, int end, OrderByComparator<TimeOTPEntry> orderByComparator,
		boolean retrieveFromCache) {

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
			finderArgs = new Object[] {start, end, orderByComparator};
		}

		List<TimeOTPEntry> list = null;

		if (retrieveFromCache) {
			list = (List<TimeOTPEntry>)finderCache.getResult(
				finderPath, finderArgs, this);
		}

		if (list == null) {
			StringBundler query = null;
			String sql = null;

			if (orderByComparator != null) {
				query = new StringBundler(
					2 + (orderByComparator.getOrderByFields().length * 2));

				query.append(_SQL_SELECT_TIMEOTPENTRY);

				appendOrderByComparator(
					query, _ORDER_BY_ENTITY_ALIAS, orderByComparator);

				sql = query.toString();
			}
			else {
				sql = _SQL_SELECT_TIMEOTPENTRY;

				if (pagination) {
					sql = sql.concat(TimeOTPEntryModelImpl.ORDER_BY_JPQL);
				}
			}

			Session session = null;

			try {
				session = openSession();

				Query q = session.createQuery(sql);

				if (!pagination) {
					list = (List<TimeOTPEntry>)QueryUtil.list(
						q, getDialect(), start, end, false);

					Collections.sort(list);

					list = Collections.unmodifiableList(list);
				}
				else {
					list = (List<TimeOTPEntry>)QueryUtil.list(
						q, getDialect(), start, end);
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
	 * Removes all the time otp entries from the database.
	 *
	 */
	@Override
	public void removeAll() {
		for (TimeOTPEntry timeOTPEntry : findAll()) {
			remove(timeOTPEntry);
		}
	}

	/**
	 * Returns the number of time otp entries.
	 *
	 * @return the number of time otp entries
	 */
	@Override
	public int countAll() {
		Long count = (Long)finderCache.getResult(
			_finderPathCountAll, FINDER_ARGS_EMPTY, this);

		if (count == null) {
			Session session = null;

			try {
				session = openSession();

				Query q = session.createQuery(_SQL_COUNT_TIMEOTPENTRY);

				count = (Long)q.uniqueResult();

				finderCache.putResult(
					_finderPathCountAll, FINDER_ARGS_EMPTY, count);
			}
			catch (Exception e) {
				finderCache.removeResult(
					_finderPathCountAll, FINDER_ARGS_EMPTY);

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
		return "entryId";
	}

	@Override
	protected String getSelectSQL() {
		return _SQL_SELECT_TIMEOTPENTRY;
	}

	@Override
	protected Map<String, Integer> getTableColumnsMap() {
		return TimeOTPEntryModelImpl.TABLE_COLUMNS_MAP;
	}

	/**
	 * Initializes the time otp entry persistence.
	 */
	@Activate
	public void activate() {
		TimeOTPEntryModelImpl.setEntityCacheEnabled(entityCacheEnabled);
		TimeOTPEntryModelImpl.setFinderCacheEnabled(finderCacheEnabled);

		_finderPathWithPaginationFindAll = new FinderPath(
			entityCacheEnabled, finderCacheEnabled, TimeOTPEntryImpl.class,
			FINDER_CLASS_NAME_LIST_WITH_PAGINATION, "findAll", new String[0]);

		_finderPathWithoutPaginationFindAll = new FinderPath(
			entityCacheEnabled, finderCacheEnabled, TimeOTPEntryImpl.class,
			FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "findAll",
			new String[0]);

		_finderPathCountAll = new FinderPath(
			entityCacheEnabled, finderCacheEnabled, Long.class,
			FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "countAll",
			new String[0]);

		_finderPathFetchByUserId = new FinderPath(
			entityCacheEnabled, finderCacheEnabled, TimeOTPEntryImpl.class,
			FINDER_CLASS_NAME_ENTITY, "fetchByUserId",
			new String[] {Long.class.getName()},
			TimeOTPEntryModelImpl.USERID_COLUMN_BITMASK);

		_finderPathCountByUserId = new FinderPath(
			entityCacheEnabled, finderCacheEnabled, Long.class,
			FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "countByUserId",
			new String[] {Long.class.getName()});
	}

	@Deactivate
	public void deactivate() {
		entityCache.removeCache(TimeOTPEntryImpl.class.getName());
		finderCache.removeCache(FINDER_CLASS_NAME_ENTITY);
		finderCache.removeCache(FINDER_CLASS_NAME_LIST_WITH_PAGINATION);
		finderCache.removeCache(FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION);
	}

	@Override
	@Reference(
		target = TimeOTPPersistenceConstants.ORIGIN_BUNDLE_SYMBOLIC_NAME_FILTER,
		unbind = "-"
	)
	public void setConfiguration(Configuration configuration) {
		super.setConfiguration(configuration);

		_columnBitmaskEnabled = GetterUtil.getBoolean(
			configuration.get(
				"value.object.column.bitmask.enabled.com.liferay.multi.factor.authentication.provider.time.otp.model.TimeOTPEntry"),
			true);
	}

	@Override
	@Reference(
		target = TimeOTPPersistenceConstants.ORIGIN_BUNDLE_SYMBOLIC_NAME_FILTER,
		unbind = "-"
	)
	public void setDataSource(DataSource dataSource) {
		super.setDataSource(dataSource);
	}

	@Override
	@Reference(
		target = TimeOTPPersistenceConstants.ORIGIN_BUNDLE_SYMBOLIC_NAME_FILTER,
		unbind = "-"
	)
	public void setSessionFactory(SessionFactory sessionFactory) {
		super.setSessionFactory(sessionFactory);
	}

	private boolean _columnBitmaskEnabled;

	@Reference(service = CompanyProviderWrapper.class)
	protected CompanyProvider companyProvider;

	@Reference
	protected EntityCache entityCache;

	@Reference
	protected FinderCache finderCache;

	private static final String _SQL_SELECT_TIMEOTPENTRY =
		"SELECT timeOTPEntry FROM TimeOTPEntry timeOTPEntry";

	private static final String _SQL_SELECT_TIMEOTPENTRY_WHERE =
		"SELECT timeOTPEntry FROM TimeOTPEntry timeOTPEntry WHERE ";

	private static final String _SQL_COUNT_TIMEOTPENTRY =
		"SELECT COUNT(timeOTPEntry) FROM TimeOTPEntry timeOTPEntry";

	private static final String _SQL_COUNT_TIMEOTPENTRY_WHERE =
		"SELECT COUNT(timeOTPEntry) FROM TimeOTPEntry timeOTPEntry WHERE ";

	private static final String _ORDER_BY_ENTITY_ALIAS = "timeOTPEntry.";

	private static final String _NO_SUCH_ENTITY_WITH_PRIMARY_KEY =
		"No TimeOTPEntry exists with the primary key ";

	private static final String _NO_SUCH_ENTITY_WITH_KEY =
		"No TimeOTPEntry exists with the key {";

	private static final Log _log = LogFactoryUtil.getLog(
		TimeOTPEntryPersistenceImpl.class);

}