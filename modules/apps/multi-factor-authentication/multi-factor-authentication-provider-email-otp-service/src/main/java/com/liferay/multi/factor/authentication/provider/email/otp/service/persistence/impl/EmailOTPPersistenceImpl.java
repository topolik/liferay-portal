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

package com.liferay.multi.factor.authentication.provider.email.otp.service.persistence.impl;

import aQute.bnd.annotation.ProviderType;

import com.liferay.multi.factor.authentication.provider.email.otp.exception.NoSuchEmailOTPException;
import com.liferay.multi.factor.authentication.provider.email.otp.model.EmailOTP;
import com.liferay.multi.factor.authentication.provider.email.otp.model.impl.EmailOTPImpl;
import com.liferay.multi.factor.authentication.provider.email.otp.model.impl.EmailOTPModelImpl;
import com.liferay.multi.factor.authentication.provider.email.otp.service.persistence.EmailOTPPersistence;

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
 * The persistence implementation for the email otp service.
 *
 * <p>
 * Caching information and settings can be found in <code>portal.properties</code>
 * </p>
 *
 * @author arthurchan35
 * @see EmailOTPPersistence
 * @see com.liferay.multi.factor.authentication.provider.email.otp.service.persistence.EmailOTPUtil
 * @generated
 */
@ProviderType
public class EmailOTPPersistenceImpl extends BasePersistenceImpl<EmailOTP>
	implements EmailOTPPersistence {
	/*
	 * NOTE FOR DEVELOPERS:
	 *
	 * Never modify or reference this class directly. Always use {@link EmailOTPUtil} to access the email otp persistence. Modify <code>service.xml</code> and rerun ServiceBuilder to regenerate this class.
	 */
	public static final String FINDER_CLASS_NAME_ENTITY = EmailOTPImpl.class.getName();
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
	 * Returns the email otp where userId = &#63; or throws a {@link NoSuchEmailOTPException} if it could not be found.
	 *
	 * @param userId the user ID
	 * @return the matching email otp
	 * @throws NoSuchEmailOTPException if a matching email otp could not be found
	 */
	@Override
	public EmailOTP findByUserId(long userId) throws NoSuchEmailOTPException {
		EmailOTP emailOTP = fetchByUserId(userId);

		if (emailOTP == null) {
			StringBundler msg = new StringBundler(4);

			msg.append(_NO_SUCH_ENTITY_WITH_KEY);

			msg.append("userId=");
			msg.append(userId);

			msg.append("}");

			if (_log.isDebugEnabled()) {
				_log.debug(msg.toString());
			}

			throw new NoSuchEmailOTPException(msg.toString());
		}

		return emailOTP;
	}

	/**
	 * Returns the email otp where userId = &#63; or returns <code>null</code> if it could not be found. Uses the finder cache.
	 *
	 * @param userId the user ID
	 * @return the matching email otp, or <code>null</code> if a matching email otp could not be found
	 */
	@Override
	public EmailOTP fetchByUserId(long userId) {
		return fetchByUserId(userId, true);
	}

	/**
	 * Returns the email otp where userId = &#63; or returns <code>null</code> if it could not be found, optionally using the finder cache.
	 *
	 * @param userId the user ID
	 * @param retrieveFromCache whether to retrieve from the finder cache
	 * @return the matching email otp, or <code>null</code> if a matching email otp could not be found
	 */
	@Override
	public EmailOTP fetchByUserId(long userId, boolean retrieveFromCache) {
		Object[] finderArgs = new Object[] { userId };

		Object result = null;

		if (retrieveFromCache) {
			result = finderCache.getResult(_finderPathFetchByUserId,
					finderArgs, this);
		}

		if (result instanceof EmailOTP) {
			EmailOTP emailOTP = (EmailOTP)result;

			if ((userId != emailOTP.getUserId())) {
				result = null;
			}
		}

		if (result == null) {
			StringBundler query = new StringBundler(3);

			query.append(_SQL_SELECT_EMAILOTP_WHERE);

			query.append(_FINDER_COLUMN_USERID_USERID_2);

			String sql = query.toString();

			Session session = null;

			try {
				session = openSession();

				Query q = session.createQuery(sql);

				QueryPos qPos = QueryPos.getInstance(q);

				qPos.add(userId);

				List<EmailOTP> list = q.list();

				if (list.isEmpty()) {
					finderCache.putResult(_finderPathFetchByUserId, finderArgs,
						list);
				}
				else {
					EmailOTP emailOTP = list.get(0);

					result = emailOTP;

					cacheResult(emailOTP);
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
			return (EmailOTP)result;
		}
	}

	/**
	 * Removes the email otp where userId = &#63; from the database.
	 *
	 * @param userId the user ID
	 * @return the email otp that was removed
	 */
	@Override
	public EmailOTP removeByUserId(long userId) throws NoSuchEmailOTPException {
		EmailOTP emailOTP = findByUserId(userId);

		return remove(emailOTP);
	}

	/**
	 * Returns the number of email otps where userId = &#63;.
	 *
	 * @param userId the user ID
	 * @return the number of matching email otps
	 */
	@Override
	public int countByUserId(long userId) {
		FinderPath finderPath = _finderPathCountByUserId;

		Object[] finderArgs = new Object[] { userId };

		Long count = (Long)finderCache.getResult(finderPath, finderArgs, this);

		if (count == null) {
			StringBundler query = new StringBundler(2);

			query.append(_SQL_COUNT_EMAILOTP_WHERE);

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

	private static final String _FINDER_COLUMN_USERID_USERID_2 = "emailOTP.userId = ?";

	public EmailOTPPersistenceImpl() {
		setModelClass(EmailOTP.class);

		setModelImplClass(EmailOTPImpl.class);
		setModelPKClass(long.class);
		setEntityCacheEnabled(EmailOTPModelImpl.ENTITY_CACHE_ENABLED);
	}

	/**
	 * Caches the email otp in the entity cache if it is enabled.
	 *
	 * @param emailOTP the email otp
	 */
	@Override
	public void cacheResult(EmailOTP emailOTP) {
		entityCache.putResult(EmailOTPModelImpl.ENTITY_CACHE_ENABLED,
			EmailOTPImpl.class, emailOTP.getPrimaryKey(), emailOTP);

		finderCache.putResult(_finderPathFetchByUserId,
			new Object[] { emailOTP.getUserId() }, emailOTP);

		emailOTP.resetOriginalValues();
	}

	/**
	 * Caches the email otps in the entity cache if it is enabled.
	 *
	 * @param emailOTPs the email otps
	 */
	@Override
	public void cacheResult(List<EmailOTP> emailOTPs) {
		for (EmailOTP emailOTP : emailOTPs) {
			if (entityCache.getResult(EmailOTPModelImpl.ENTITY_CACHE_ENABLED,
						EmailOTPImpl.class, emailOTP.getPrimaryKey()) == null) {
				cacheResult(emailOTP);
			}
			else {
				emailOTP.resetOriginalValues();
			}
		}
	}

	/**
	 * Clears the cache for all email otps.
	 *
	 * <p>
	 * The {@link EntityCache} and {@link FinderCache} are both cleared by this method.
	 * </p>
	 */
	@Override
	public void clearCache() {
		entityCache.clearCache(EmailOTPImpl.class);

		finderCache.clearCache(FINDER_CLASS_NAME_ENTITY);
		finderCache.clearCache(FINDER_CLASS_NAME_LIST_WITH_PAGINATION);
		finderCache.clearCache(FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION);
	}

	/**
	 * Clears the cache for the email otp.
	 *
	 * <p>
	 * The {@link EntityCache} and {@link FinderCache} are both cleared by this method.
	 * </p>
	 */
	@Override
	public void clearCache(EmailOTP emailOTP) {
		entityCache.removeResult(EmailOTPModelImpl.ENTITY_CACHE_ENABLED,
			EmailOTPImpl.class, emailOTP.getPrimaryKey());

		finderCache.clearCache(FINDER_CLASS_NAME_LIST_WITH_PAGINATION);
		finderCache.clearCache(FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION);

		clearUniqueFindersCache((EmailOTPModelImpl)emailOTP, true);
	}

	@Override
	public void clearCache(List<EmailOTP> emailOTPs) {
		finderCache.clearCache(FINDER_CLASS_NAME_LIST_WITH_PAGINATION);
		finderCache.clearCache(FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION);

		for (EmailOTP emailOTP : emailOTPs) {
			entityCache.removeResult(EmailOTPModelImpl.ENTITY_CACHE_ENABLED,
				EmailOTPImpl.class, emailOTP.getPrimaryKey());

			clearUniqueFindersCache((EmailOTPModelImpl)emailOTP, true);
		}
	}

	protected void cacheUniqueFindersCache(EmailOTPModelImpl emailOTPModelImpl) {
		Object[] args = new Object[] { emailOTPModelImpl.getUserId() };

		finderCache.putResult(_finderPathCountByUserId, args, Long.valueOf(1),
			false);
		finderCache.putResult(_finderPathFetchByUserId, args,
			emailOTPModelImpl, false);
	}

	protected void clearUniqueFindersCache(
		EmailOTPModelImpl emailOTPModelImpl, boolean clearCurrent) {
		if (clearCurrent) {
			Object[] args = new Object[] { emailOTPModelImpl.getUserId() };

			finderCache.removeResult(_finderPathCountByUserId, args);
			finderCache.removeResult(_finderPathFetchByUserId, args);
		}

		if ((emailOTPModelImpl.getColumnBitmask() &
				_finderPathFetchByUserId.getColumnBitmask()) != 0) {
			Object[] args = new Object[] { emailOTPModelImpl.getOriginalUserId() };

			finderCache.removeResult(_finderPathCountByUserId, args);
			finderCache.removeResult(_finderPathFetchByUserId, args);
		}
	}

	/**
	 * Creates a new email otp with the primary key. Does not add the email otp to the database.
	 *
	 * @param emailOTPId the primary key for the new email otp
	 * @return the new email otp
	 */
	@Override
	public EmailOTP create(long emailOTPId) {
		EmailOTP emailOTP = new EmailOTPImpl();

		emailOTP.setNew(true);
		emailOTP.setPrimaryKey(emailOTPId);

		emailOTP.setCompanyId(companyProvider.getCompanyId());

		return emailOTP;
	}

	/**
	 * Removes the email otp with the primary key from the database. Also notifies the appropriate model listeners.
	 *
	 * @param emailOTPId the primary key of the email otp
	 * @return the email otp that was removed
	 * @throws NoSuchEmailOTPException if a email otp with the primary key could not be found
	 */
	@Override
	public EmailOTP remove(long emailOTPId) throws NoSuchEmailOTPException {
		return remove((Serializable)emailOTPId);
	}

	/**
	 * Removes the email otp with the primary key from the database. Also notifies the appropriate model listeners.
	 *
	 * @param primaryKey the primary key of the email otp
	 * @return the email otp that was removed
	 * @throws NoSuchEmailOTPException if a email otp with the primary key could not be found
	 */
	@Override
	public EmailOTP remove(Serializable primaryKey)
		throws NoSuchEmailOTPException {
		Session session = null;

		try {
			session = openSession();

			EmailOTP emailOTP = (EmailOTP)session.get(EmailOTPImpl.class,
					primaryKey);

			if (emailOTP == null) {
				if (_log.isDebugEnabled()) {
					_log.debug(_NO_SUCH_ENTITY_WITH_PRIMARY_KEY + primaryKey);
				}

				throw new NoSuchEmailOTPException(_NO_SUCH_ENTITY_WITH_PRIMARY_KEY +
					primaryKey);
			}

			return remove(emailOTP);
		}
		catch (NoSuchEmailOTPException nsee) {
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
	protected EmailOTP removeImpl(EmailOTP emailOTP) {
		Session session = null;

		try {
			session = openSession();

			if (!session.contains(emailOTP)) {
				emailOTP = (EmailOTP)session.get(EmailOTPImpl.class,
						emailOTP.getPrimaryKeyObj());
			}

			if (emailOTP != null) {
				session.delete(emailOTP);
			}
		}
		catch (Exception e) {
			throw processException(e);
		}
		finally {
			closeSession(session);
		}

		if (emailOTP != null) {
			clearCache(emailOTP);
		}

		return emailOTP;
	}

	@Override
	public EmailOTP updateImpl(EmailOTP emailOTP) {
		boolean isNew = emailOTP.isNew();

		if (!(emailOTP instanceof EmailOTPModelImpl)) {
			InvocationHandler invocationHandler = null;

			if (ProxyUtil.isProxyClass(emailOTP.getClass())) {
				invocationHandler = ProxyUtil.getInvocationHandler(emailOTP);

				throw new IllegalArgumentException(
					"Implement ModelWrapper in emailOTP proxy " +
					invocationHandler.getClass());
			}

			throw new IllegalArgumentException(
				"Implement ModelWrapper in custom EmailOTP implementation " +
				emailOTP.getClass());
		}

		EmailOTPModelImpl emailOTPModelImpl = (EmailOTPModelImpl)emailOTP;

		ServiceContext serviceContext = ServiceContextThreadLocal.getServiceContext();

		Date now = new Date();

		if (isNew && (emailOTP.getCreateDate() == null)) {
			if (serviceContext == null) {
				emailOTP.setCreateDate(now);
			}
			else {
				emailOTP.setCreateDate(serviceContext.getCreateDate(now));
			}
		}

		if (!emailOTPModelImpl.hasSetModifiedDate()) {
			if (serviceContext == null) {
				emailOTP.setModifiedDate(now);
			}
			else {
				emailOTP.setModifiedDate(serviceContext.getModifiedDate(now));
			}
		}

		Session session = null;

		try {
			session = openSession();

			if (emailOTP.isNew()) {
				session.save(emailOTP);

				emailOTP.setNew(false);
			}
			else {
				emailOTP = (EmailOTP)session.merge(emailOTP);
			}
		}
		catch (Exception e) {
			throw processException(e);
		}
		finally {
			closeSession(session);
		}

		finderCache.clearCache(FINDER_CLASS_NAME_LIST_WITH_PAGINATION);

		if (!EmailOTPModelImpl.COLUMN_BITMASK_ENABLED) {
			finderCache.clearCache(FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION);
		}
		else
		 if (isNew) {
			finderCache.removeResult(_finderPathCountAll, FINDER_ARGS_EMPTY);
			finderCache.removeResult(_finderPathWithoutPaginationFindAll,
				FINDER_ARGS_EMPTY);
		}

		entityCache.putResult(EmailOTPModelImpl.ENTITY_CACHE_ENABLED,
			EmailOTPImpl.class, emailOTP.getPrimaryKey(), emailOTP, false);

		clearUniqueFindersCache(emailOTPModelImpl, false);
		cacheUniqueFindersCache(emailOTPModelImpl);

		emailOTP.resetOriginalValues();

		return emailOTP;
	}

	/**
	 * Returns the email otp with the primary key or throws a {@link com.liferay.portal.kernel.exception.NoSuchModelException} if it could not be found.
	 *
	 * @param primaryKey the primary key of the email otp
	 * @return the email otp
	 * @throws NoSuchEmailOTPException if a email otp with the primary key could not be found
	 */
	@Override
	public EmailOTP findByPrimaryKey(Serializable primaryKey)
		throws NoSuchEmailOTPException {
		EmailOTP emailOTP = fetchByPrimaryKey(primaryKey);

		if (emailOTP == null) {
			if (_log.isDebugEnabled()) {
				_log.debug(_NO_SUCH_ENTITY_WITH_PRIMARY_KEY + primaryKey);
			}

			throw new NoSuchEmailOTPException(_NO_SUCH_ENTITY_WITH_PRIMARY_KEY +
				primaryKey);
		}

		return emailOTP;
	}

	/**
	 * Returns the email otp with the primary key or throws a {@link NoSuchEmailOTPException} if it could not be found.
	 *
	 * @param emailOTPId the primary key of the email otp
	 * @return the email otp
	 * @throws NoSuchEmailOTPException if a email otp with the primary key could not be found
	 */
	@Override
	public EmailOTP findByPrimaryKey(long emailOTPId)
		throws NoSuchEmailOTPException {
		return findByPrimaryKey((Serializable)emailOTPId);
	}

	/**
	 * Returns the email otp with the primary key or returns <code>null</code> if it could not be found.
	 *
	 * @param emailOTPId the primary key of the email otp
	 * @return the email otp, or <code>null</code> if a email otp with the primary key could not be found
	 */
	@Override
	public EmailOTP fetchByPrimaryKey(long emailOTPId) {
		return fetchByPrimaryKey((Serializable)emailOTPId);
	}

	/**
	 * Returns all the email otps.
	 *
	 * @return the email otps
	 */
	@Override
	public List<EmailOTP> findAll() {
		return findAll(QueryUtil.ALL_POS, QueryUtil.ALL_POS, null);
	}

	/**
	 * Returns a range of all the email otps.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to {@link QueryUtil#ALL_POS} will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent and pagination is required (<code>start</code> and <code>end</code> are not {@link QueryUtil#ALL_POS}), then the query will include the default ORDER BY logic from {@link EmailOTPModelImpl}. If both <code>orderByComparator</code> and pagination are absent, for performance reasons, the query will not have an ORDER BY clause and the returned result set will be sorted on by the primary key in an ascending order.
	 * </p>
	 *
	 * @param start the lower bound of the range of email otps
	 * @param end the upper bound of the range of email otps (not inclusive)
	 * @return the range of email otps
	 */
	@Override
	public List<EmailOTP> findAll(int start, int end) {
		return findAll(start, end, null);
	}

	/**
	 * Returns an ordered range of all the email otps.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to {@link QueryUtil#ALL_POS} will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent and pagination is required (<code>start</code> and <code>end</code> are not {@link QueryUtil#ALL_POS}), then the query will include the default ORDER BY logic from {@link EmailOTPModelImpl}. If both <code>orderByComparator</code> and pagination are absent, for performance reasons, the query will not have an ORDER BY clause and the returned result set will be sorted on by the primary key in an ascending order.
	 * </p>
	 *
	 * @param start the lower bound of the range of email otps
	 * @param end the upper bound of the range of email otps (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of email otps
	 */
	@Override
	public List<EmailOTP> findAll(int start, int end,
		OrderByComparator<EmailOTP> orderByComparator) {
		return findAll(start, end, orderByComparator, true);
	}

	/**
	 * Returns an ordered range of all the email otps.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to {@link QueryUtil#ALL_POS} will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent and pagination is required (<code>start</code> and <code>end</code> are not {@link QueryUtil#ALL_POS}), then the query will include the default ORDER BY logic from {@link EmailOTPModelImpl}. If both <code>orderByComparator</code> and pagination are absent, for performance reasons, the query will not have an ORDER BY clause and the returned result set will be sorted on by the primary key in an ascending order.
	 * </p>
	 *
	 * @param start the lower bound of the range of email otps
	 * @param end the upper bound of the range of email otps (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param retrieveFromCache whether to retrieve from the finder cache
	 * @return the ordered range of email otps
	 */
	@Override
	public List<EmailOTP> findAll(int start, int end,
		OrderByComparator<EmailOTP> orderByComparator, boolean retrieveFromCache) {
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

		List<EmailOTP> list = null;

		if (retrieveFromCache) {
			list = (List<EmailOTP>)finderCache.getResult(finderPath,
					finderArgs, this);
		}

		if (list == null) {
			StringBundler query = null;
			String sql = null;

			if (orderByComparator != null) {
				query = new StringBundler(2 +
						(orderByComparator.getOrderByFields().length * 2));

				query.append(_SQL_SELECT_EMAILOTP);

				appendOrderByComparator(query, _ORDER_BY_ENTITY_ALIAS,
					orderByComparator);

				sql = query.toString();
			}
			else {
				sql = _SQL_SELECT_EMAILOTP;

				if (pagination) {
					sql = sql.concat(EmailOTPModelImpl.ORDER_BY_JPQL);
				}
			}

			Session session = null;

			try {
				session = openSession();

				Query q = session.createQuery(sql);

				if (!pagination) {
					list = (List<EmailOTP>)QueryUtil.list(q, getDialect(),
							start, end, false);

					Collections.sort(list);

					list = Collections.unmodifiableList(list);
				}
				else {
					list = (List<EmailOTP>)QueryUtil.list(q, getDialect(),
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
	 * Removes all the email otps from the database.
	 *
	 */
	@Override
	public void removeAll() {
		for (EmailOTP emailOTP : findAll()) {
			remove(emailOTP);
		}
	}

	/**
	 * Returns the number of email otps.
	 *
	 * @return the number of email otps
	 */
	@Override
	public int countAll() {
		Long count = (Long)finderCache.getResult(_finderPathCountAll,
				FINDER_ARGS_EMPTY, this);

		if (count == null) {
			Session session = null;

			try {
				session = openSession();

				Query q = session.createQuery(_SQL_COUNT_EMAILOTP);

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
		return "emailOTPId";
	}

	@Override
	protected String getSelectSQL() {
		return _SQL_SELECT_EMAILOTP;
	}

	@Override
	protected Map<String, Integer> getTableColumnsMap() {
		return EmailOTPModelImpl.TABLE_COLUMNS_MAP;
	}

	/**
	 * Initializes the email otp persistence.
	 */
	public void afterPropertiesSet() {
		_finderPathWithPaginationFindAll = new FinderPath(EmailOTPModelImpl.ENTITY_CACHE_ENABLED,
				EmailOTPModelImpl.FINDER_CACHE_ENABLED, EmailOTPImpl.class,
				FINDER_CLASS_NAME_LIST_WITH_PAGINATION, "findAll", new String[0]);

		_finderPathWithoutPaginationFindAll = new FinderPath(EmailOTPModelImpl.ENTITY_CACHE_ENABLED,
				EmailOTPModelImpl.FINDER_CACHE_ENABLED, EmailOTPImpl.class,
				FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "findAll",
				new String[0]);

		_finderPathCountAll = new FinderPath(EmailOTPModelImpl.ENTITY_CACHE_ENABLED,
				EmailOTPModelImpl.FINDER_CACHE_ENABLED, Long.class,
				FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "countAll",
				new String[0]);

		_finderPathFetchByUserId = new FinderPath(EmailOTPModelImpl.ENTITY_CACHE_ENABLED,
				EmailOTPModelImpl.FINDER_CACHE_ENABLED, EmailOTPImpl.class,
				FINDER_CLASS_NAME_ENTITY, "fetchByUserId",
				new String[] { Long.class.getName() },
				EmailOTPModelImpl.USERID_COLUMN_BITMASK);

		_finderPathCountByUserId = new FinderPath(EmailOTPModelImpl.ENTITY_CACHE_ENABLED,
				EmailOTPModelImpl.FINDER_CACHE_ENABLED, Long.class,
				FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "countByUserId",
				new String[] { Long.class.getName() });
	}

	public void destroy() {
		entityCache.removeCache(EmailOTPImpl.class.getName());
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
	private static final String _SQL_SELECT_EMAILOTP = "SELECT emailOTP FROM EmailOTP emailOTP";
	private static final String _SQL_SELECT_EMAILOTP_WHERE = "SELECT emailOTP FROM EmailOTP emailOTP WHERE ";
	private static final String _SQL_COUNT_EMAILOTP = "SELECT COUNT(emailOTP) FROM EmailOTP emailOTP";
	private static final String _SQL_COUNT_EMAILOTP_WHERE = "SELECT COUNT(emailOTP) FROM EmailOTP emailOTP WHERE ";
	private static final String _ORDER_BY_ENTITY_ALIAS = "emailOTP.";
	private static final String _NO_SUCH_ENTITY_WITH_PRIMARY_KEY = "No EmailOTP exists with the primary key ";
	private static final String _NO_SUCH_ENTITY_WITH_KEY = "No EmailOTP exists with the key {";
	private static final Log _log = LogFactoryUtil.getLog(EmailOTPPersistenceImpl.class);
}