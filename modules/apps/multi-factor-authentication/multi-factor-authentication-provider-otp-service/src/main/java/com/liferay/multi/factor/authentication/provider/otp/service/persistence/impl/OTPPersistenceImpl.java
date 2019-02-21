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

package com.liferay.multi.factor.authentication.provider.otp.service.persistence.impl;

import aQute.bnd.annotation.ProviderType;

import com.liferay.multi.factor.authentication.provider.otp.exception.NoSuchOTPException;
import com.liferay.multi.factor.authentication.provider.otp.model.OTP;
import com.liferay.multi.factor.authentication.provider.otp.model.impl.OTPImpl;
import com.liferay.multi.factor.authentication.provider.otp.model.impl.OTPModelImpl;
import com.liferay.multi.factor.authentication.provider.otp.service.persistence.OTPPersistence;

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
 * The persistence implementation for the otp service.
 *
 * <p>
 * Caching information and settings can be found in <code>portal.properties</code>
 * </p>
 *
 * @author arthurchan35
 * @see OTPPersistence
 * @see com.liferay.multi.factor.authentication.provider.otp.service.persistence.OTPUtil
 * @generated
 */
@ProviderType
public class OTPPersistenceImpl extends BasePersistenceImpl<OTP>
	implements OTPPersistence {
	/*
	 * NOTE FOR DEVELOPERS:
	 *
	 * Never modify or reference this class directly. Always use {@link OTPUtil} to access the otp persistence. Modify <code>service.xml</code> and rerun ServiceBuilder to regenerate this class.
	 */
	public static final String FINDER_CLASS_NAME_ENTITY = OTPImpl.class.getName();
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
	 * Returns the otp where userId = &#63; or throws a {@link NoSuchOTPException} if it could not be found.
	 *
	 * @param userId the user ID
	 * @return the matching otp
	 * @throws NoSuchOTPException if a matching otp could not be found
	 */
	@Override
	public OTP findByUserId(long userId) throws NoSuchOTPException {
		OTP otp = fetchByUserId(userId);

		if (otp == null) {
			StringBundler msg = new StringBundler(4);

			msg.append(_NO_SUCH_ENTITY_WITH_KEY);

			msg.append("userId=");
			msg.append(userId);

			msg.append("}");

			if (_log.isDebugEnabled()) {
				_log.debug(msg.toString());
			}

			throw new NoSuchOTPException(msg.toString());
		}

		return otp;
	}

	/**
	 * Returns the otp where userId = &#63; or returns <code>null</code> if it could not be found. Uses the finder cache.
	 *
	 * @param userId the user ID
	 * @return the matching otp, or <code>null</code> if a matching otp could not be found
	 */
	@Override
	public OTP fetchByUserId(long userId) {
		return fetchByUserId(userId, true);
	}

	/**
	 * Returns the otp where userId = &#63; or returns <code>null</code> if it could not be found, optionally using the finder cache.
	 *
	 * @param userId the user ID
	 * @param retrieveFromCache whether to retrieve from the finder cache
	 * @return the matching otp, or <code>null</code> if a matching otp could not be found
	 */
	@Override
	public OTP fetchByUserId(long userId, boolean retrieveFromCache) {
		Object[] finderArgs = new Object[] { userId };

		Object result = null;

		if (retrieveFromCache) {
			result = finderCache.getResult(_finderPathFetchByUserId,
					finderArgs, this);
		}

		if (result instanceof OTP) {
			OTP otp = (OTP)result;

			if ((userId != otp.getUserId())) {
				result = null;
			}
		}

		if (result == null) {
			StringBundler query = new StringBundler(3);

			query.append(_SQL_SELECT_OTP_WHERE);

			query.append(_FINDER_COLUMN_USERID_USERID_2);

			String sql = query.toString();

			Session session = null;

			try {
				session = openSession();

				Query q = session.createQuery(sql);

				QueryPos qPos = QueryPos.getInstance(q);

				qPos.add(userId);

				List<OTP> list = q.list();

				if (list.isEmpty()) {
					finderCache.putResult(_finderPathFetchByUserId, finderArgs,
						list);
				}
				else {
					OTP otp = list.get(0);

					result = otp;

					cacheResult(otp);
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
			return (OTP)result;
		}
	}

	/**
	 * Removes the otp where userId = &#63; from the database.
	 *
	 * @param userId the user ID
	 * @return the otp that was removed
	 */
	@Override
	public OTP removeByUserId(long userId) throws NoSuchOTPException {
		OTP otp = findByUserId(userId);

		return remove(otp);
	}

	/**
	 * Returns the number of otps where userId = &#63;.
	 *
	 * @param userId the user ID
	 * @return the number of matching otps
	 */
	@Override
	public int countByUserId(long userId) {
		FinderPath finderPath = _finderPathCountByUserId;

		Object[] finderArgs = new Object[] { userId };

		Long count = (Long)finderCache.getResult(finderPath, finderArgs, this);

		if (count == null) {
			StringBundler query = new StringBundler(2);

			query.append(_SQL_COUNT_OTP_WHERE);

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

	private static final String _FINDER_COLUMN_USERID_USERID_2 = "otp.userId = ?";

	public OTPPersistenceImpl() {
		setModelClass(OTP.class);

		setModelImplClass(OTPImpl.class);
		setModelPKClass(long.class);
		setEntityCacheEnabled(OTPModelImpl.ENTITY_CACHE_ENABLED);
	}

	/**
	 * Caches the otp in the entity cache if it is enabled.
	 *
	 * @param otp the otp
	 */
	@Override
	public void cacheResult(OTP otp) {
		entityCache.putResult(OTPModelImpl.ENTITY_CACHE_ENABLED, OTPImpl.class,
			otp.getPrimaryKey(), otp);

		finderCache.putResult(_finderPathFetchByUserId,
			new Object[] { otp.getUserId() }, otp);

		otp.resetOriginalValues();
	}

	/**
	 * Caches the otps in the entity cache if it is enabled.
	 *
	 * @param otps the otps
	 */
	@Override
	public void cacheResult(List<OTP> otps) {
		for (OTP otp : otps) {
			if (entityCache.getResult(OTPModelImpl.ENTITY_CACHE_ENABLED,
						OTPImpl.class, otp.getPrimaryKey()) == null) {
				cacheResult(otp);
			}
			else {
				otp.resetOriginalValues();
			}
		}
	}

	/**
	 * Clears the cache for all otps.
	 *
	 * <p>
	 * The {@link EntityCache} and {@link FinderCache} are both cleared by this method.
	 * </p>
	 */
	@Override
	public void clearCache() {
		entityCache.clearCache(OTPImpl.class);

		finderCache.clearCache(FINDER_CLASS_NAME_ENTITY);
		finderCache.clearCache(FINDER_CLASS_NAME_LIST_WITH_PAGINATION);
		finderCache.clearCache(FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION);
	}

	/**
	 * Clears the cache for the otp.
	 *
	 * <p>
	 * The {@link EntityCache} and {@link FinderCache} are both cleared by this method.
	 * </p>
	 */
	@Override
	public void clearCache(OTP otp) {
		entityCache.removeResult(OTPModelImpl.ENTITY_CACHE_ENABLED,
			OTPImpl.class, otp.getPrimaryKey());

		finderCache.clearCache(FINDER_CLASS_NAME_LIST_WITH_PAGINATION);
		finderCache.clearCache(FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION);

		clearUniqueFindersCache((OTPModelImpl)otp, true);
	}

	@Override
	public void clearCache(List<OTP> otps) {
		finderCache.clearCache(FINDER_CLASS_NAME_LIST_WITH_PAGINATION);
		finderCache.clearCache(FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION);

		for (OTP otp : otps) {
			entityCache.removeResult(OTPModelImpl.ENTITY_CACHE_ENABLED,
				OTPImpl.class, otp.getPrimaryKey());

			clearUniqueFindersCache((OTPModelImpl)otp, true);
		}
	}

	protected void cacheUniqueFindersCache(OTPModelImpl otpModelImpl) {
		Object[] args = new Object[] { otpModelImpl.getUserId() };

		finderCache.putResult(_finderPathCountByUserId, args, Long.valueOf(1),
			false);
		finderCache.putResult(_finderPathFetchByUserId, args, otpModelImpl,
			false);
	}

	protected void clearUniqueFindersCache(OTPModelImpl otpModelImpl,
		boolean clearCurrent) {
		if (clearCurrent) {
			Object[] args = new Object[] { otpModelImpl.getUserId() };

			finderCache.removeResult(_finderPathCountByUserId, args);
			finderCache.removeResult(_finderPathFetchByUserId, args);
		}

		if ((otpModelImpl.getColumnBitmask() &
				_finderPathFetchByUserId.getColumnBitmask()) != 0) {
			Object[] args = new Object[] { otpModelImpl.getOriginalUserId() };

			finderCache.removeResult(_finderPathCountByUserId, args);
			finderCache.removeResult(_finderPathFetchByUserId, args);
		}
	}

	/**
	 * Creates a new otp with the primary key. Does not add the otp to the database.
	 *
	 * @param otpId the primary key for the new otp
	 * @return the new otp
	 */
	@Override
	public OTP create(long otpId) {
		OTP otp = new OTPImpl();

		otp.setNew(true);
		otp.setPrimaryKey(otpId);

		otp.setCompanyId(companyProvider.getCompanyId());

		return otp;
	}

	/**
	 * Removes the otp with the primary key from the database. Also notifies the appropriate model listeners.
	 *
	 * @param otpId the primary key of the otp
	 * @return the otp that was removed
	 * @throws NoSuchOTPException if a otp with the primary key could not be found
	 */
	@Override
	public OTP remove(long otpId) throws NoSuchOTPException {
		return remove((Serializable)otpId);
	}

	/**
	 * Removes the otp with the primary key from the database. Also notifies the appropriate model listeners.
	 *
	 * @param primaryKey the primary key of the otp
	 * @return the otp that was removed
	 * @throws NoSuchOTPException if a otp with the primary key could not be found
	 */
	@Override
	public OTP remove(Serializable primaryKey) throws NoSuchOTPException {
		Session session = null;

		try {
			session = openSession();

			OTP otp = (OTP)session.get(OTPImpl.class, primaryKey);

			if (otp == null) {
				if (_log.isDebugEnabled()) {
					_log.debug(_NO_SUCH_ENTITY_WITH_PRIMARY_KEY + primaryKey);
				}

				throw new NoSuchOTPException(_NO_SUCH_ENTITY_WITH_PRIMARY_KEY +
					primaryKey);
			}

			return remove(otp);
		}
		catch (NoSuchOTPException nsee) {
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
	protected OTP removeImpl(OTP otp) {
		Session session = null;

		try {
			session = openSession();

			if (!session.contains(otp)) {
				otp = (OTP)session.get(OTPImpl.class, otp.getPrimaryKeyObj());
			}

			if (otp != null) {
				session.delete(otp);
			}
		}
		catch (Exception e) {
			throw processException(e);
		}
		finally {
			closeSession(session);
		}

		if (otp != null) {
			clearCache(otp);
		}

		return otp;
	}

	@Override
	public OTP updateImpl(OTP otp) {
		boolean isNew = otp.isNew();

		if (!(otp instanceof OTPModelImpl)) {
			InvocationHandler invocationHandler = null;

			if (ProxyUtil.isProxyClass(otp.getClass())) {
				invocationHandler = ProxyUtil.getInvocationHandler(otp);

				throw new IllegalArgumentException(
					"Implement ModelWrapper in otp proxy " +
					invocationHandler.getClass());
			}

			throw new IllegalArgumentException(
				"Implement ModelWrapper in custom OTP implementation " +
				otp.getClass());
		}

		OTPModelImpl otpModelImpl = (OTPModelImpl)otp;

		ServiceContext serviceContext = ServiceContextThreadLocal.getServiceContext();

		Date now = new Date();

		if (isNew && (otp.getCreateDate() == null)) {
			if (serviceContext == null) {
				otp.setCreateDate(now);
			}
			else {
				otp.setCreateDate(serviceContext.getCreateDate(now));
			}
		}

		if (!otpModelImpl.hasSetModifiedDate()) {
			if (serviceContext == null) {
				otp.setModifiedDate(now);
			}
			else {
				otp.setModifiedDate(serviceContext.getModifiedDate(now));
			}
		}

		Session session = null;

		try {
			session = openSession();

			if (otp.isNew()) {
				session.save(otp);

				otp.setNew(false);
			}
			else {
				otp = (OTP)session.merge(otp);
			}
		}
		catch (Exception e) {
			throw processException(e);
		}
		finally {
			closeSession(session);
		}

		finderCache.clearCache(FINDER_CLASS_NAME_LIST_WITH_PAGINATION);

		if (!OTPModelImpl.COLUMN_BITMASK_ENABLED) {
			finderCache.clearCache(FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION);
		}
		else
		 if (isNew) {
			finderCache.removeResult(_finderPathCountAll, FINDER_ARGS_EMPTY);
			finderCache.removeResult(_finderPathWithoutPaginationFindAll,
				FINDER_ARGS_EMPTY);
		}

		entityCache.putResult(OTPModelImpl.ENTITY_CACHE_ENABLED, OTPImpl.class,
			otp.getPrimaryKey(), otp, false);

		clearUniqueFindersCache(otpModelImpl, false);
		cacheUniqueFindersCache(otpModelImpl);

		otp.resetOriginalValues();

		return otp;
	}

	/**
	 * Returns the otp with the primary key or throws a {@link com.liferay.portal.kernel.exception.NoSuchModelException} if it could not be found.
	 *
	 * @param primaryKey the primary key of the otp
	 * @return the otp
	 * @throws NoSuchOTPException if a otp with the primary key could not be found
	 */
	@Override
	public OTP findByPrimaryKey(Serializable primaryKey)
		throws NoSuchOTPException {
		OTP otp = fetchByPrimaryKey(primaryKey);

		if (otp == null) {
			if (_log.isDebugEnabled()) {
				_log.debug(_NO_SUCH_ENTITY_WITH_PRIMARY_KEY + primaryKey);
			}

			throw new NoSuchOTPException(_NO_SUCH_ENTITY_WITH_PRIMARY_KEY +
				primaryKey);
		}

		return otp;
	}

	/**
	 * Returns the otp with the primary key or throws a {@link NoSuchOTPException} if it could not be found.
	 *
	 * @param otpId the primary key of the otp
	 * @return the otp
	 * @throws NoSuchOTPException if a otp with the primary key could not be found
	 */
	@Override
	public OTP findByPrimaryKey(long otpId) throws NoSuchOTPException {
		return findByPrimaryKey((Serializable)otpId);
	}

	/**
	 * Returns the otp with the primary key or returns <code>null</code> if it could not be found.
	 *
	 * @param otpId the primary key of the otp
	 * @return the otp, or <code>null</code> if a otp with the primary key could not be found
	 */
	@Override
	public OTP fetchByPrimaryKey(long otpId) {
		return fetchByPrimaryKey((Serializable)otpId);
	}

	/**
	 * Returns all the otps.
	 *
	 * @return the otps
	 */
	@Override
	public List<OTP> findAll() {
		return findAll(QueryUtil.ALL_POS, QueryUtil.ALL_POS, null);
	}

	/**
	 * Returns a range of all the otps.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to {@link QueryUtil#ALL_POS} will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent and pagination is required (<code>start</code> and <code>end</code> are not {@link QueryUtil#ALL_POS}), then the query will include the default ORDER BY logic from {@link OTPModelImpl}. If both <code>orderByComparator</code> and pagination are absent, for performance reasons, the query will not have an ORDER BY clause and the returned result set will be sorted on by the primary key in an ascending order.
	 * </p>
	 *
	 * @param start the lower bound of the range of otps
	 * @param end the upper bound of the range of otps (not inclusive)
	 * @return the range of otps
	 */
	@Override
	public List<OTP> findAll(int start, int end) {
		return findAll(start, end, null);
	}

	/**
	 * Returns an ordered range of all the otps.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to {@link QueryUtil#ALL_POS} will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent and pagination is required (<code>start</code> and <code>end</code> are not {@link QueryUtil#ALL_POS}), then the query will include the default ORDER BY logic from {@link OTPModelImpl}. If both <code>orderByComparator</code> and pagination are absent, for performance reasons, the query will not have an ORDER BY clause and the returned result set will be sorted on by the primary key in an ascending order.
	 * </p>
	 *
	 * @param start the lower bound of the range of otps
	 * @param end the upper bound of the range of otps (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of otps
	 */
	@Override
	public List<OTP> findAll(int start, int end,
		OrderByComparator<OTP> orderByComparator) {
		return findAll(start, end, orderByComparator, true);
	}

	/**
	 * Returns an ordered range of all the otps.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to {@link QueryUtil#ALL_POS} will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent and pagination is required (<code>start</code> and <code>end</code> are not {@link QueryUtil#ALL_POS}), then the query will include the default ORDER BY logic from {@link OTPModelImpl}. If both <code>orderByComparator</code> and pagination are absent, for performance reasons, the query will not have an ORDER BY clause and the returned result set will be sorted on by the primary key in an ascending order.
	 * </p>
	 *
	 * @param start the lower bound of the range of otps
	 * @param end the upper bound of the range of otps (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param retrieveFromCache whether to retrieve from the finder cache
	 * @return the ordered range of otps
	 */
	@Override
	public List<OTP> findAll(int start, int end,
		OrderByComparator<OTP> orderByComparator, boolean retrieveFromCache) {
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

		List<OTP> list = null;

		if (retrieveFromCache) {
			list = (List<OTP>)finderCache.getResult(finderPath, finderArgs, this);
		}

		if (list == null) {
			StringBundler query = null;
			String sql = null;

			if (orderByComparator != null) {
				query = new StringBundler(2 +
						(orderByComparator.getOrderByFields().length * 2));

				query.append(_SQL_SELECT_OTP);

				appendOrderByComparator(query, _ORDER_BY_ENTITY_ALIAS,
					orderByComparator);

				sql = query.toString();
			}
			else {
				sql = _SQL_SELECT_OTP;

				if (pagination) {
					sql = sql.concat(OTPModelImpl.ORDER_BY_JPQL);
				}
			}

			Session session = null;

			try {
				session = openSession();

				Query q = session.createQuery(sql);

				if (!pagination) {
					list = (List<OTP>)QueryUtil.list(q, getDialect(), start,
							end, false);

					Collections.sort(list);

					list = Collections.unmodifiableList(list);
				}
				else {
					list = (List<OTP>)QueryUtil.list(q, getDialect(), start, end);
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
	 * Removes all the otps from the database.
	 *
	 */
	@Override
	public void removeAll() {
		for (OTP otp : findAll()) {
			remove(otp);
		}
	}

	/**
	 * Returns the number of otps.
	 *
	 * @return the number of otps
	 */
	@Override
	public int countAll() {
		Long count = (Long)finderCache.getResult(_finderPathCountAll,
				FINDER_ARGS_EMPTY, this);

		if (count == null) {
			Session session = null;

			try {
				session = openSession();

				Query q = session.createQuery(_SQL_COUNT_OTP);

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
		return "otpId";
	}

	@Override
	protected String getSelectSQL() {
		return _SQL_SELECT_OTP;
	}

	@Override
	protected Map<String, Integer> getTableColumnsMap() {
		return OTPModelImpl.TABLE_COLUMNS_MAP;
	}

	/**
	 * Initializes the otp persistence.
	 */
	public void afterPropertiesSet() {
		_finderPathWithPaginationFindAll = new FinderPath(OTPModelImpl.ENTITY_CACHE_ENABLED,
				OTPModelImpl.FINDER_CACHE_ENABLED, OTPImpl.class,
				FINDER_CLASS_NAME_LIST_WITH_PAGINATION, "findAll", new String[0]);

		_finderPathWithoutPaginationFindAll = new FinderPath(OTPModelImpl.ENTITY_CACHE_ENABLED,
				OTPModelImpl.FINDER_CACHE_ENABLED, OTPImpl.class,
				FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "findAll",
				new String[0]);

		_finderPathCountAll = new FinderPath(OTPModelImpl.ENTITY_CACHE_ENABLED,
				OTPModelImpl.FINDER_CACHE_ENABLED, Long.class,
				FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "countAll",
				new String[0]);

		_finderPathFetchByUserId = new FinderPath(OTPModelImpl.ENTITY_CACHE_ENABLED,
				OTPModelImpl.FINDER_CACHE_ENABLED, OTPImpl.class,
				FINDER_CLASS_NAME_ENTITY, "fetchByUserId",
				new String[] { Long.class.getName() },
				OTPModelImpl.USERID_COLUMN_BITMASK);

		_finderPathCountByUserId = new FinderPath(OTPModelImpl.ENTITY_CACHE_ENABLED,
				OTPModelImpl.FINDER_CACHE_ENABLED, Long.class,
				FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "countByUserId",
				new String[] { Long.class.getName() });
	}

	public void destroy() {
		entityCache.removeCache(OTPImpl.class.getName());
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
	private static final String _SQL_SELECT_OTP = "SELECT otp FROM OTP otp";
	private static final String _SQL_SELECT_OTP_WHERE = "SELECT otp FROM OTP otp WHERE ";
	private static final String _SQL_COUNT_OTP = "SELECT COUNT(otp) FROM OTP otp";
	private static final String _SQL_COUNT_OTP_WHERE = "SELECT COUNT(otp) FROM OTP otp WHERE ";
	private static final String _ORDER_BY_ENTITY_ALIAS = "otp.";
	private static final String _NO_SUCH_ENTITY_WITH_PRIMARY_KEY = "No OTP exists with the primary key ";
	private static final String _NO_SUCH_ENTITY_WITH_KEY = "No OTP exists with the key {";
	private static final Log _log = LogFactoryUtil.getLog(OTPPersistenceImpl.class);
}